package com.howbuy.observable;

import android.support.annotation.NonNull;

public interface ClearListener<T> {
    void onClear(@NonNull T data);
}
