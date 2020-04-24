package com.howbuy.lifecycleobserverproxy;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

public interface ClearProxyFactory {
    <T> T create(@NonNull Class<T> tClass,
                 @NonNull T delegate,
                 @NonNull LifecycleOwner lifecycleOwner);
}
