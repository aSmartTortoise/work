package com.voyah.ai.logic.remote;

import com.voice.sdk.util.LogUtils;
import com.voice.sdk.util.ThreadPoolUtils;
import com.voyah.ai.sdk.IAsrListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author:lcy 外部添加asr监听管理(暂时只有asr用到 、 后续持续添加)
 * @data:2024/6/3
 **/
public class RemoteAsrListenerManager {
    private static final String TAG = RemoteAsrListenerManager.class.getSimpleName();
    private List<IAsrListener> asrListeners = new ArrayList<>();
    private ExecutorService singlePool = ThreadPoolUtils.INSTANCE.getSinglePool(TAG);


    private static RemoteAsrListenerManager remoteAsrListenerManager;

    private RemoteAsrListenerManager() {
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

        singlePool.execute(new Runnable() {
            @Override
            public void run() {
                asrListeners.add(asrListener);
            }
        });
    }

    public synchronized void unregisterAsrListener(IAsrListener asrListener) {
        if (null == asrListeners)
            return;
        singlePool.execute(new Runnable() {
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
        singlePool.execute(new Runnable() {
            @Override
            public void run() {
                for (IAsrListener asrListener : asrListeners) {
                    try {
                        LogUtils.i(TAG, "status is " + status);
                        asrListener.asrStatus(status);
                    } catch (Exception e) {
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
        singlePool.execute(new Runnable() {
            @Override
            public void run() {
                for (IAsrListener asrListener : asrListeners) {
                    try {
                        asrListener.asrText(asrText);
                    } catch (Exception e) {
                        LogUtils.e(TAG, "callbackAsrText e is " + e.getMessage());
                        //失败移除对应监听
                        unregisterAsrListener(asrListener);
                    }
                }
            }
        });
    }

}
