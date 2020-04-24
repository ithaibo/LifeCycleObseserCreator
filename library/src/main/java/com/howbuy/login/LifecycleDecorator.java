package com.howbuy.login;

import android.support.annotation.Nullable;

public class LifecycleDecorator<T> implements LifeEventObserver{
    public T target;
    ClearListener<T> listener;

    public LifecycleDecorator(T target) {
        this.target = target;
    }

    public LifecycleDecorator(T target, ClearListener<T> listener) {
        this.target = target;
        this.listener = listener;
    }

    @Nullable
    public T getTarget() {
        return target;
    }

    @Override
    public void clear() {
        if (null != listener) listener.onClear(target);
        target = null;
    }
}
