package com.voyah.vcos.ttsservices.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;

/**
 * @author:lcy
 * @data:2025/2/5
 **/
public class AudioDataHandlerThread extends Thread {
    private Handler mHandler;
    private Looper mLooper;

    public AudioDataHandlerThread(@NonNull String threadName) {
        super(threadName);
    }

    @Override
    public void run() {
        //设置线程优先级
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        Looper.prepare();
        mLooper = Looper.myLooper();
        mHandler = new Handler(mLooper);
        Looper.loop();
    }

    public void post(Runnable runnable) {
        if (mHandler != null)
            mHandler.post(runnable);
    }

    public void clear(Runnable runnable) {
        if (mHandler != null)
            mHandler.removeCallbacks(runnable);
    }

    public void clear() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public void quitSafely() {
        if (mLooper != null)
            mLooper.quitSafely();
    }
}
