package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.howbuy.login.internal.LifecycleDecorator;

public class LoginManagerImpl implements LoginManager {
    private SparseArray<LogoutObserver> logoutObserverSparseArray = new SparseArray<>();
    private SparseArray<LoginObserver> loginObserverSparseArray = new SparseArray<>();

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
        for (int i = 0; i < logoutObserverSparseArray.size(); i++) {
            logoutObserverSparseArray.valueAt(i).onLogout();
        }
    }

    private void dispatchLoginEvent() {
        for (int i = 0; i < loginObserverSparseArray.size(); i++) {
            loginObserverSparseArray.valueAt(i).onLogin();
        }
    }


    private static int key(@NonNull Object object) {
        return object.hashCode();
    }

    private static class H {
        private static LoginManager instance = new LoginManagerImpl();
    }
}
