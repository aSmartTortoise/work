package com.voyah.ai.voice.remote;

import android.os.IBinder;
import android.os.IInterface;

import androidx.annotation.NonNull;

class SpeechCallbackWrapper<T extends IInterface> implements IInterface {
    private final String packageName;
    private final T callback;

    public SpeechCallbackWrapper(@NonNull String packageName, @NonNull T callback) {
        this.packageName = packageName;
        this.callback = callback;
    }

    public String getPackageName() {
        return packageName;
    }

    public T getCallback() {
        return callback;
    }

    @Override
    public IBinder asBinder() {
        return callback.asBinder();
    }
}