package com.howbuy.observable;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.Objects;

class ObservableImpl<T> implements Observable<T> {
    private final SparseArray<LifeEventWrapper<Observer<T>>> cache = new SparseArray<>();

    @Override
    public void onChange(T newData) {
        if (cache.size() <= 0) return;
        for (int i = 0; i < cache.size(); i++) {
            LifeEventWrapper<Observer<T>> wrapper = cache.valueAt(i);
            if (null != wrapper.data) wrapper.data.onChange(newData);
        }
    }

    @Override
    public void addObserver(@NonNull LifecycleOwner lifecycleOwner, Observer<T> observer) {
        LifeEventWrapper<Observer<T>> wrapper = new LifeEventWrapper<>(
                observer,
                new ClearListener<Observer<T>>() {
                    @Override
                    public void onClear(@NonNull Observer<T> data) {
                        removeObserver(data);
                    }
                });

        synchronized (cache) {
            cache.put(key(wrapper.data), wrapper);
        }
        lifecycleOwner.getLifecycle().addObserver(wrapper);
    }

    @Override
    public void removeObserver(@NonNull Observer<T> observer) {
        synchronized (cache) {
            cache.remove(key(observer));
        }
    }

    private int key(@NonNull Observer observer) {
        return Objects.hashCode(observer);
    }
}
