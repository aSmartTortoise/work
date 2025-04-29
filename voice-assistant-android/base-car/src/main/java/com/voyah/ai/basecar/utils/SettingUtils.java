package com.voyah.ai.basecar.utils;

import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.dc.carsetting.SettingInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.vehiclesettings.ISettingInf;

public class SettingUtils implements SettingInterface {

    private static final String TAG = SettingUtils.class.getSimpleName();

    private static SettingUtils mInstance;
    private static Context mContext;
    private boolean isConnected = false;
    private ISettingInf mISettingInf;

    public static SettingUtils getInstance() {
        if (null == mInstance) {
            synchronized (SettingUtils.class) {
                if (null == mInstance) {
                    mInstance = new SettingUtils(Utils.getApp());
                }
            }
        }
        return mInstance;
    }

    private SettingUtils(Context mContext) {
        SettingUtils.mContext = mContext;
        initSetting();
    }

    private void initSetting() {
        LogUtils.d(TAG, "initSetting");
        if (!isConnected) {
            LogUtils.d(TAG, "connected aidl");
            Intent intent =new Intent();
            intent.setAction("com.voyah.cockpit.vehiclesettings.SettingService");
            intent.setPackage("com.voyah.cockpit.vehiclesettings");
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "ServiceConnection onServiceConnected");
            isConnected = true;
            mISettingInf = ISettingInf.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "ServiceConnection onServiceDisconnected");
            isConnected = false;
            mISettingInf = null;
        }
    };

    public void exec(@NonNull String action)  {
        LogUtils.d(TAG, "exec: " + action);
        if (mISettingInf != null) {
            LogUtils.d(TAG, "mISettingInf exec");
            try {
                // 只有界面跳转类，才需要关闭悬浮窗
                if (!action.contains("CleanMode") && !action.contains(SettingConstants.FUNC_PREFIX)) {
                    DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.CENTRAL_SCREEN, "");
                }
                mISettingInf.exec(action);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isCurrentState(@NonNull String action) {
        LogUtils.d(TAG, "isCurrentState: " + action);
        if (mISettingInf == null) {
            LogUtils.d(TAG, "mISettingInf is null");
            return false;
        }
        try {
            boolean value = mISettingInf.isCurrentState(action);
            if (value) {
                DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.CENTRAL_SCREEN, "");
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCurrentState(@NonNull String action) {
        LogUtils.d(TAG, "isCurrentState: " + action);
        if (mISettingInf == null) {
            LogUtils.d(TAG, "mISettingInf is null");
            return "";
        }
        try {
            return mISettingInf.getCurrentState(action);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
