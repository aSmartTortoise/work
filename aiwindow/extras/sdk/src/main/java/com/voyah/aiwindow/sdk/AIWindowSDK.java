package com.voyah.aiwindow.sdk;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.aiwindow.aidlbean.AIMessage;
import com.voyah.aiwindow.sdk.callback.OnInitCallback;
import com.voyah.aiwindow.sdk.callback.OnMsgResultCallback;

/**
 * sdk 入口
 */
public class AIWindowSDK {

    private static final String TAG = AIWindowSDK.class.getSimpleName();

    public static BinderConnector binderConnector;

    /**
     * 初始化SDK
     *
     * @param context  上下文
     * @param callback 回调
     */
    public static void initialize(@NonNull Context context, @NonNull OnInitCallback callback) {
        if (binderConnector == null) {
            binderConnector = new BinderConnector(context);
        }
        binderConnector.bindService(callback);
    }


    /**
     * 释放 sdk 资源
     */
    public static void release() {
        if (binderConnector != null) {
            binderConnector.release();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 发送AI消息
     *
     * @param msg 消息
     */
    public static void sendAIMessage(AIMessage msg) {
        Log.d(TAG, "sendAIMessage() called with: msg = [" + msg + "]");
        if (binderConnector != null) {
            binderConnector.sendAIMessage(msg, null);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 发送AI消息
     *
     * @param msg      消息
     * @param callback 推送消息处理回调
     */
    public static void sendAIMessage(AIMessage msg, OnMsgResultCallback callback) {
        Log.d(TAG, "sendAIMessage() called with: msg = [" + msg + "], callback = [" + callback + "]");
        if (binderConnector != null) {
            binderConnector.sendAIMessage(msg, callback);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }
}
