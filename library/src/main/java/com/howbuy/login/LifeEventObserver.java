package com.howbuy.login;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.Keep;

@Keep
interface LifeEventObserver extends LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void clear();
}
