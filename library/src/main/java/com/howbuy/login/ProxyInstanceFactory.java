package com.howbuy.login;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

public interface ProxyInstanceFactory {
    <T> T create(@NonNull Class<T> tClass, @NonNull T delegate, @NonNull LifecycleOwner lifecycleOwner);
}
