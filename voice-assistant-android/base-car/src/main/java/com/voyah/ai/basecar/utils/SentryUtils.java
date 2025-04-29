package com.voyah.ai.basecar.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.arcsoft.sentryaidlinterface.ISentryServiceInterface;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.dc.SentryInterface;
import com.voyah.ai.common.utils.LogUtils;

public class SentryUtils implements SentryInterface {

    private static final String TAG = SentryUtils.class.getSimpleName();

    private static SentryUtils mInstance;
    private static Context mContext;
    private boolean isConnected = false;
    private ISentryServiceInterface mISentryServiceInterface;

    public static SentryUtils getInstance() {
        if (null == mInstance) {
            synchronized (SentryUtils.class) {
                if (null == mInstance) {
                    mInstance = new SentryUtils(Utils.getApp());
                }
            }
        }
        return mInstance;
    }

    SentryUtils(Context mContext) {
        this.mContext = mContext;
        initSetting();
    }

    private void initSetting() {
        LogUtils.d(TAG, "initSetting");
        if (!isConnected) {
            LogUtils.d(TAG, "connected aidl");
            Intent intent =new Intent();
            intent.setAction("com.arcsoft.sentryserviceimpl.SentryService");
            intent.setPackage("com.arcsoft.sentryService");
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "ServiceConnection onServiceConnected");
            isConnected = true;
            mISentryServiceInterface = ISentryServiceInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "ServiceConnection onServiceDisconnected");
            isConnected = false;
            mISentryServiceInterface = null;
        }
    };

    /**
     * 获取哨兵模式开关状态
     *
     * @return true打开，false关闭
     */
    @Override
    public boolean isSentryOpen() {
        int state = 0;
        try {
            state = mISentryServiceInterface.getMainSwitchOnOff();
        } catch (RemoteException e) {
            //
        }
        LogUtils.d(TAG, "getSwitchState: " + state);
        // 1是打开，2是关闭，3当前状态不可设置
        return state == 1;
    }

    /**
     * 设置哨兵模式开关状态
     *
     * @param state 1: 开关打开 2: 开关关闭
     */
    @Override
    public void setSentrySwitchState(int state) {
        int result = -2;
        try {
            result = mISentryServiceInterface.setMainSwitchOnOff(state);
        } catch (RemoteException e) {
            //
        }
        // -1 当前状态不可设 0 设置成功 1 低电量打开失败 2 设置值与当前值一致
        LogUtils.d(TAG, "result: " + result);
    }
}
