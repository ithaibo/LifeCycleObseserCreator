package com.howbuy.observable;

public interface Observer<T> {
    void onChange(T data);
}
