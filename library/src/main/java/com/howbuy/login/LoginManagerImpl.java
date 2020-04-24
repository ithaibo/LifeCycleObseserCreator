package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.SparseArray;

public class LoginManagerImpl implements LoginManager{
    private SparseArray<LogoutObserver> logoutObserverSparseArray = new SparseArray<>();
    private SparseArray<LoginObserver> loginObserverSparseArray = new SparseArray<>();
    private ClearProxyFactory proxyInstanceCreator;

    public static LoginManager getInstance() {
        return H.instance;
    }

    @Override
    public void publishLoginState(boolean login) {
        if (login) {
            dispatchLoginEvent();
        } else {
            dispatchLogoutEvent();
        }
    }

    @Override
    public void addLoginObserver(@NonNull final LoginObserver observer,
                                 @NonNull LifecycleOwner lifecycleOwner) {
        final int key = key(observer);
//        loginObserverSparseArray.put(key,
//                createProxyInstance(LoginObserver.class, observer, lifecycleOwner));
//        lifecycleOwner.getLifecycle().addObserver(new LifeEventObserver() {
//            @Override
//            public void clear() {
//                loginObserverSparseArray.remove(key);
//            }
//        });
        loginObserverSparseArray.put(key, observer);
        lifecycleOwner.getLifecycle()
                .addObserver(new LifecycleDecorator<>(observer,
                        new ClearListener<LoginObserver>() {
                            @Override
                            public void onClear(@NonNull LoginObserver target) {
                                loginObserverSparseArray.remove(key(target));
                            }
                        }));
    }

    @Override
    public void addLogoutObserver(@NonNull LogoutObserver observer,
                                  @NonNull LifecycleOwner lifecycleOwner) {
        final int key = key(observer);
//        final LogoutObserver observerProxy = createProxyInstance(LogoutObserver.class,
//                observer,
//                lifecycleOwner);
//        logoutObserverSparseArray.put(key, observerProxy);
//        lifecycleOwner.getLifecycle().addObserver(new LifeEventObserver() {
//            @Override
//            public void clear() {
//                logoutObserverSparseArray.remove(key);
//            }
//        });
        logoutObserverSparseArray.put(key, observer);
        lifecycleOwner.getLifecycle().addObserver(new LifecycleDecorator<>(observer,
                new ClearListener<LogoutObserver>() {
                    @Override
                    public void onClear(LogoutObserver target) {
                        logoutObserverSparseArray.remove(key(target));
                    }
                }));
    }

    @Override
    public void removeLoginObserver(@NonNull LoginObserver observer) {
        loginObserverSparseArray.remove(key(observer));
    }

    @Override
    public void removeLogoutObserver(@NonNull LogoutObserver observer) {
        logoutObserverSparseArray.remove(key(observer));
    }

    private void dispatchLogoutEvent() {
        for (int i =0; i < logoutObserverSparseArray.size(); i++) {
            logoutObserverSparseArray.valueAt(i).onLogout();
        }
    }

    private void dispatchLoginEvent() {
        for (int i =0; i < loginObserverSparseArray.size(); i++) {
            loginObserverSparseArray.valueAt(i).onLogin();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createProxyInstance(@NonNull Class<T> clazz,
                                      @NonNull T delegate,
                                      @NonNull LifecycleOwner lifecycleOwner) {
        if (null == proxyInstanceCreator) {
            proxyInstanceCreator = new DynamicProxyInstanceCreator();
        }
        return proxyInstanceCreator.create(clazz, delegate, lifecycleOwner);
    }


    private static int key(@NonNull Object object) {
        return object.hashCode();
    }

    private static class H {
        private static LoginManager instance = new LoginManagerImpl();
    }
}
