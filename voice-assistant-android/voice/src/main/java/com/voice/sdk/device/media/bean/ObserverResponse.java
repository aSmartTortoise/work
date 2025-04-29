package com.voice.sdk.device.media.bean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class ObserverResponse<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public final void onNext(T response) {
        onSuccess(response);
    }

    @Override
    public final void onComplete() {
    }

    @Override
    public void onError(Throwable e) {

    }

    abstract public void onSuccess(T response);

    public void onException(Throwable e) {
    }

    public void onFailed(int code, String msg) {
    }

}
