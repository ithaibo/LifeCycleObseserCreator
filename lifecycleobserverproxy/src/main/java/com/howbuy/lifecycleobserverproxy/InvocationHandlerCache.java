package com.howbuy.lifecycleobserverproxy;

import android.support.annotation.NonNull;

import java.lang.reflect.InvocationHandler;

public interface InvocationHandlerCache {
    void cache(@NonNull Class key, @NonNull InvocationHandler invocationHandler);
    InvocationHandler shot(@NonNull Class key);
}
