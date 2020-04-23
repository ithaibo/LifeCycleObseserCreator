package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface LoginManager {
    void publishLoginState(boolean login);

    void addLoginObserver(@NonNull LoginObserver observer, @Nullable LifecycleOwner lifecycleOwner);
    void addLogoutObserver(@NonNull LogoutObserver observer, @Nullable LifecycleOwner lifecycleOwner);

    void removeLoginObserver(@NonNull LoginObserver observer);
    void removeLogoutObserver(@NonNull LogoutObserver observer);
}
