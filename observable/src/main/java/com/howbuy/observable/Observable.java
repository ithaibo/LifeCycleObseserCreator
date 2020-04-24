package com.howbuy.observable;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

public interface Observable<T> {
    class Factory {
        public static<T> Observable<T> create() {
            return new ObservableImpl<T>();
        }
    }

    void onChange(T data);
    void addObserver(@NonNull LifecycleOwner lifecycleOwner, Observer<T> observer);
    void removeObserver(@NonNull Observer<T> observer);
}
