package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

public class LoginManagerImpl implements LoginManager{
    private SparseArray<LogoutObserver> logoutObserverSparseArray = new SparseArray<>();
    private SparseArray<LoginObserver> loginObserverSparseArray = new SparseArray<>();
    private volatile boolean isLogin;
    private DynamicProxyInstanceCreator proxyInstanceCreator;

    public static LoginManager getInstance() {
        return H.instance;
    }

    @Override
    public void publishLoginState(boolean login) {
        isLogin = login;
        if (isLogin) {
            dispatchLoginEvent();
        } else {
            dispatchLogoutEvent();
        }
    }

    @Override
    public void addLoginObserver(@NonNull LoginObserver observer, @Nullable LifecycleOwner lifecycleOwner) {
        loginObserverSparseArray.put(key(observer),
                createProxyInstance(LoginObserver.class, observer, lifecycleOwner));
    }

    @Override
    public void addLogoutObserver(@NonNull LogoutObserver observer, @Nullable LifecycleOwner lifecycleOwner) {
        logoutObserverSparseArray.put(key(observer),
                createProxyInstance(LogoutObserver.class, observer, lifecycleOwner));
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
    private <T> T createProxyInstance(@NonNull Class<T> clazz, @NonNull T delegate, @Nullable LifecycleOwner lifecycleOwner) {
        if (null == proxyInstanceCreator) {
            proxyInstanceCreator = new DynamicProxyInstanceCreator();
        }
        return proxyInstanceCreator.createProxyInstance(clazz, delegate, lifecycleOwner);
    }


    private static int key(@NonNull Object object) {
        return object.hashCode();
    }

    private static class H {
        private static LoginManager instance = new LoginManagerImpl();
    }
}
