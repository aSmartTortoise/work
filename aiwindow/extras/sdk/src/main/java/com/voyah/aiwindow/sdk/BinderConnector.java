package com.voyah.aiwindow.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.aiwindow.aidlbean.AIMessage;
import com.voyah.aiwindow.aidlbean.State;
import com.voyah.aiwindow.sdk.callback.OnInitCallback;
import com.voyah.aiwindow.sdk.callback.OnMsgResultCallback;


/**
 * binder远程服务辅助类
 */
public class BinderConnector {
    private static final String TAG = "Remote_" + BinderConnector.class.getSimpleName();
    /**
     * 启动语音服务的action和包名
     */
    private static final String HOST_SERVICE_ACTION = "com.voyah.aiwindow.AIRemoteService";
    private static final String HOST_SERVICE_PACKAGE = "com.voyah.aiwindow";

    private OnInitCallback mInitCallback;

    private final Context context;
    private IAIWindowService serviceInterface;


    public BinderConnector(Context context) {
        this.context = context;
    }

    /**
     * 绑定远程服务
     */
    public void bindService(@NonNull OnInitCallback callback) {
        Log.d(TAG, "bindService() called");
        this.mInitCallback = callback;
        Intent intent = new Intent(HOST_SERVICE_ACTION);
        intent.setPackage(HOST_SERVICE_PACKAGE);
        context.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        Log.d(TAG, "unBindService() called, isConnected:" + isConnected());
        if (isConnected()) {
            context.unbindService(mBinderPoolConnection);
            serviceInterface = null;
        }
    }

    public boolean isConnected() {
        return serviceInterface != null && serviceInterface.asBinder().isBinderAlive();
    }

    private final ServiceConnection mBinderPoolConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            serviceInterface = null;
            bindService(mInitCallback);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            try {
                mInitCallback.onConnected();
                serviceInterface = IAIWindowService.Stub.asInterface(service);
                serviceInterface.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 释放
     */
    public void release() {
        if (isConnected()) {
            unBindService();
        }
    }

    /**
     * 远程服务死亡监听
     */
    private final IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "binder died.");
            if (serviceInterface != null) {
                serviceInterface.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
                release();
                serviceInterface = null;
            }
            Log.e(TAG, "reBind service...");
            bindService(mInitCallback);
        }
    };

    /**
     * 发送推送消息
     *
     * @param msg
     * @param callback
     */
    public void sendAIMessage(AIMessage msg, OnMsgResultCallback callback) {
        try {
            if (isConnected()) {
                serviceInterface.sendAIMessage(msg, new RemotePushResultCallbackImpl(callback));
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class RemotePushResultCallbackImpl extends IMsgResultCallback.Stub {

        private final OnMsgResultCallback client;

        public RemotePushResultCallbackImpl(OnMsgResultCallback listener) {
            this.client = listener;
        }

        @Override
        public void onResult(int code, String msg) throws RemoteException {
            if (client != null) {
                client.onResult(State.fromCode(code), msg);
            }
        }
    }
}
