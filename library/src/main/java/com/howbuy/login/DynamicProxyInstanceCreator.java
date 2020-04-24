package com.howbuy.login;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class DynamicProxyInstanceCreator implements ProxyInstanceFactory, InvocationHandlerCache{
    private final Map<Class, Queue<InvocationHandler>> invokePool = new HashMap<>(4);
    private final Map<Object, LifeEventObserver> lifeEventObserverMap = new HashMap<>();

    @Override
    public <T> T create(@NonNull Class<T> clazz,
                        @NonNull T delegate,
                        @NonNull LifecycleOwner lifecycleOwner) {
        InvocationHandler invocationHandler = obtainInvocationHandler(clazz,
                delegate,
                lifecycleOwner);
        T instance = (T) Proxy.newProxyInstance(LoginManagerImpl.class.getClassLoader(),
                new Class[]{clazz, LifeEventObserver.class},
                invocationHandler);
        //register lifecycle observer
        lifecycleOwner.getLifecycle().addObserver((LifecycleObserver) instance);
        lifeEventObserverMap.put(delegate, (LifeEventObserver) instance);
        return instance;
    }

    /**
     * @param clazz interface
     * @param delegate instance of implementation
     * @return InvocationHandler(to create a proxy instance of interface.)
     */
    @SuppressWarnings("unchecked")
    private <T> InvocationHandler obtainInvocationHandler(@NonNull Class<T> clazz,
                                                          @NonNull T delegate,
                                                          @NonNull LifecycleOwner lifecycleOwner) {
        InvocationHandlerImpl<T> shotHandler;
        InvocationHandlerCache cache = DynamicProxyInstanceCreator.this;
        shotHandler = (InvocationHandlerImpl<T>) cache.shot(clazz);
        if (null == shotHandler) {
            //create it directly
            Log.w("ProxyInstance", "invocationHandler cache not shot, class: " + clazz.getName());
            return new InvocationHandlerImpl<>(delegate,
                    clazz,
                    DynamicProxyInstanceCreator.this,
                    lifecycleOwner);
        } else {
            Log.w("ProxyInstance", "invocationHandler cache shot, class: " + clazz.getName());
            shotHandler.lifecycleOwner = lifecycleOwner;
            shotHandler.creatorRf = DynamicProxyInstanceCreator.this;
            shotHandler.delegate = delegate;
            return shotHandler;
        }
    }

    @Override
    public void cache(@NonNull Class key, @NonNull InvocationHandler invocationHandler) {
        Queue<InvocationHandler>  cacheQueue = invokePool.get(key);
        if (null ==  cacheQueue) {
             cacheQueue = new LinkedList<>();
            invokePool.put(key,  cacheQueue);
        }
         cacheQueue.add(invocationHandler);
//        Log.w("InvocationHandlerCache", "cache one invocationHandler, class: " + key.getName());
    }

    @Override
    public InvocationHandler shot(@NonNull Class key) {
        Queue<InvocationHandler> cacheQueue = invokePool.remove(key);
        if (null == cacheQueue) {
            return null;
        }
        return cacheQueue.poll();
    }


    private void removeObserverFromLifecycleOwner(LifecycleOwner lifecycleOwner,
                                                  @NonNull Object object) {
        if (null == lifecycleOwner) return;
        LifeEventObserver eventObserver = lifeEventObserverMap.remove(object);
        if (null != eventObserver) {
            lifecycleOwner.getLifecycle().removeObserver(eventObserver);
            //remove observer from loginmanager
        }
    }


    private static class InvocationHandlerImpl<T> implements InvocationHandler {
        private static Method clearMethod;
        private Class<T> tClass;
        private DynamicProxyInstanceCreator creatorRf;
        private LifecycleOwner lifecycleOwner;

        private T delegate;
        private List<Method> tMethodList;

        InvocationHandlerImpl(T delegate,
                              Class<T> tClass,
                              DynamicProxyInstanceCreator creator,
                              LifecycleOwner lifecycleOwner) {
            this.delegate = delegate;
            this.tClass = tClass;
            this.creatorRf = creator;
            this.lifecycleOwner = lifecycleOwner;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(Object.class == method.getDeclaringClass()) {
                return method.invoke(this, args);
            }

            //method clear
            if (isClearMethod(method)) {
                return handleInvocationClearMethodOfLifeEventObserver();
            }

            Log.w("ProxyInvoker", "method now: " + method.getName() +
                    ", class: " + method.getDeclaringClass());
            //no action after destroy
            if (null == delegate) return handleDefaultReturn(method);

            return method.invoke(delegate, args);
        }

        private Object handleInvocationClearMethodOfLifeEventObserver() {
            DynamicProxyInstanceCreator creator = creatorRf;
            if (null != creator) {
                creator.cache(InvocationHandlerImpl.this.tClass, InvocationHandlerImpl.this);
                creator.removeObserverFromLifecycleOwner(lifecycleOwner, delegate);
            }
            lifecycleOwner = null;
            creatorRf = null;
            //remove delegate
            delegate = null;
            Log.w("ProxyInvoker", "method, clear invocation is handled");
            return null;
        }

        private static boolean isClearMethod(@NonNull Method method) {
            if (LifeEventObserver.class != method.getDeclaringClass()) {
                return false;
            }
            if (null == clearMethod) {
                try {
                    clearMethod = LifeEventObserver.class.getDeclaredMethod("clear");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if (!areParameterTypesSameWithClearMethod(method)) {
                return false;
            }
            if (!TextUtils.equals(clearMethod.getName(), method.getName())) {
                return false;
            }

            Log.w("ProxyInvoker", "method is clear");
            return true;
        }

        private static boolean areParameterTypesSameWithClearMethod(@NonNull Method method) {
            Class[] typesTarget = clearMethod.getParameterTypes();
            Class[] typesNow = method.getParameterTypes();
            if (typesNow.length != typesTarget.length) {
                return false;
            }
            for (int i =0; i< typesTarget.length; i++) {
                Class itemType = typesTarget[i];
                Class itemNow = typesNow[i];
                if (itemType != itemNow) {
                    return false;
                }
            }
            return true;
        }

        private static Object handleDefaultReturn(@NonNull Method method) {
            Type returnType = method.getGenericReturnType();
            if (Boolean.TYPE.equals(returnType)) {
                return false;
            } else if (Integer.TYPE.equals(returnType)) {
                return 0;
            } else if (Short.TYPE.equals(returnType)) {
                return (short) 0;
            } else if (Long.TYPE.equals(returnType)) {
                return (long)0;
            } else if (Float.TYPE.equals(returnType)) {
                return (float)0;
            } else if (Double.TYPE.equals(returnType)) {
                return (double)0;
            } else if (Character.TYPE.equals(returnType)) {
                return '\0';
            }
            return null;
        }
    }
}
