package com.howbuy.observable;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

public class LifeEventWrapper<T> implements LifecycleObserver {
    T data;
    private ClearListener<T> clearListener;

    public LifeEventWrapper(T data, ClearListener<T> clearListener) {
        this.data = data;
        this.clearListener = clearListener;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public final void clear() {
        if (null != clearListener) clearListener.onClear(data);
        data = null;
    }
}
