package com.voyah.ai.basecar.utils;

import android.os.RemoteException;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.dc.RemoteInterface;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.bt_remote_control_jar.BTRCHelper;

public class RemoteUtils implements RemoteInterface {

    private static final String TAG = "RemoteUtils";

    private static RemoteUtils mRemoteUtils;
    private BTRCHelper btrcHelper;

    public static RemoteUtils getInstance() {
        if (null == mRemoteUtils) {
            synchronized (RemoteUtils.class) {
                if (null == mRemoteUtils) {
                    mRemoteUtils = new RemoteUtils();
                }
            }
        }
        return mRemoteUtils;
    }

    public void init() {
        if (btrcHelper == null) {
            btrcHelper = BTRCHelper.getInstance();
        }
    }

    public void register() {
        btrcHelper.registerSDK(Utils.getApp());
    }

    public int getRemoteControlAppState() {
        int isRemoteControlAppOpend = 0;
        try {
            isRemoteControlAppOpend = btrcHelper.launchApp();
            LogUtils.i(TAG, "getRemoteControlAppState :" + isRemoteControlAppOpend);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return isRemoteControlAppOpend;
    }

    public void setRemoteControlAppClose() {
        try {
            int setRemoteControlAppClose = btrcHelper.closeApp();
            LogUtils.i(TAG, "setRemoteControlAppClose :" + setRemoteControlAppClose);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRemoteControlConnectState() {
        int isRemoteControlConnected = 0;
        try {
            isRemoteControlConnected = btrcHelper.connectDevice();
            LogUtils.i(TAG, "getRemoteControlConnectState :" + isRemoteControlConnected);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return isRemoteControlConnected;
    }

    public void setRemoteControlDisConnect() {
        try {
            int mSetRemoteControlDisConnect = btrcHelper.disConnectDevice();
            LogUtils.i(TAG, "setRemoteControlDisConnect :" + mSetRemoteControlDisConnect);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRemoteControlMenuPageState() {
        int isShowRemoteControlMenuUI = 0;
        try {
            isShowRemoteControlMenuUI = btrcHelper.navigateToMenu();
            LogUtils.i(TAG, "getRemoteControlMenuPageState :" + isShowRemoteControlMenuUI);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return isShowRemoteControlMenuUI;
    }
}
