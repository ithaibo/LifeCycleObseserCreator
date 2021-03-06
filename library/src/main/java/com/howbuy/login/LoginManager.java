package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

public interface LoginManager {
    void publishLoginState(boolean login);

    void addLoginObserver(@NonNull LoginObserver observer, @NonNull LifecycleOwner lifecycleOwner);
    void addLogoutObserver(@NonNull LogoutObserver observer, @NonNull LifecycleOwner lifecycleOwner);

    void removeLoginObserver(@NonNull LoginObserver observer);
    void removeLogoutObserver(@NonNull LogoutObserver observer);
}
