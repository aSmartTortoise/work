package com.voyah.ai.voice.remote;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;

import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.IAsrListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:lcy 外部添加asr监听管理(暂时只有asr用到 、 后续持续添加)
 * @data:2024/6/3
 **/
public class RemoteAsrListenerManager {
    private static final String TAG = RemoteAsrListenerManager.class.getSimpleName();
    private List<IAsrListener> asrListeners = new ArrayList<>();

    private Handler handler;
    private HandlerThread handlerThread;

    private static RemoteAsrListenerManager remoteAsrListenerManager;

    private RemoteAsrListenerManager() {
        initHandler();
    }

    private void initHandler() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static RemoteAsrListenerManager getInstance() {
        if (null == remoteAsrListenerManager) {
            synchronized (RemoteAsrListenerManager.class) {
                if (null == remoteAsrListenerManager) {
                    remoteAsrListenerManager = new RemoteAsrListenerManager();
                }
            }
        }
        return remoteAsrListenerManager;
    }

    public synchronized void registerAsrListener(IAsrListener asrListener) {
        if (null == asrListeners)
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                asrListeners.add(asrListener);
            }
        });
    }

    public synchronized void unregisterAsrListener(IAsrListener asrListener) {
        if (null == asrListeners)
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                asrListeners.remove(asrListener);
            }
        });
    }

    public synchronized void callbackAsrStatus(int status) {
        if (null == asrListeners || asrListeners.isEmpty()) {
            LogUtils.i(TAG, "asrListeners is empty");
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (IAsrListener asrListener : asrListeners) {
                    try {
                        LogUtils.i(TAG, "status is " + status);
                        asrListener.asrStatus(status);
                    } catch (RemoteException e) {
                        LogUtils.e(TAG, "RemoteAsrListenerManager callbackAsrStatus e is " + e.getMessage());
                        //失败移除对应监听
                        unregisterAsrListener(asrListener);
                    }
                }
            }
        });
    }

    public synchronized void callbackAsrText(String asrText) {
        if (null == asrListeners || asrListeners.isEmpty()) {
            LogUtils.i(TAG, "asrListeners is empty");
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (IAsrListener asrListener : asrListeners) {
                    try {
                        asrListener.asrText(asrText);
                    } catch (RemoteException e) {
                        LogUtils.e(TAG, "callbackAsrText e is " + e.getMessage());
                        //失败移除对应监听
                        unregisterAsrListener(asrListener);
                    }
                }
            }
        });
    }

}
