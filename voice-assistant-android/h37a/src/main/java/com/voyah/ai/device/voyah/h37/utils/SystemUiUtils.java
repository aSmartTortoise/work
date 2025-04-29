package com.voyah.ai.device.voyah.h37.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceImpl;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;
import com.voyah.cockpit.seat.SeatServiceImpl;
import com.voyah.cockpit.systemui.voice.sdk.Common;
import com.voyah.cockpit.systemui.voice.sdk.IRemoteVoiceService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SystemUiUtils {

    private static final String TAG = SystemUiUtils.class.getSimpleName();

    private static SystemUiUtils mInstance;
    private static Context mContext;
    private boolean mIsConnected = false;

    //负一屏状态获取id
    private static final int QS_PANEL_STATE = 1003;
    private static final int QS_PANEL_STATE_OPENED = 1;
    private static final int QS_PANEL_STATE_CLOSED = 2;
    private IRemoteVoiceService mIRemoteVoiceService;

    public static SystemUiUtils getInstance() {
        if (null == mInstance) {
            synchronized (SystemUiUtils.class) {
                if (null == mInstance) {
                    mInstance = new SystemUiUtils();
                }
            }
        }
        return mInstance;
    }

    private SystemUiUtils() {

    }

    public void initVoiceSdk() {
        Log.d(TAG, "initVoiceSdk");
        if (!mIsConnected) {
            Log.d(TAG, "connected aidl");
            Intent intent = new Intent();
            intent.setPackage("com.voyah.cockpit.systemui");
            intent.setAction("com.voyah.cockpit.voice.sdk.action.RemoteService");
            Utils.getApp().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "ServiceConnection onServiceConnected");
            mIsConnected = true;
            mIRemoteVoiceService = IRemoteVoiceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceConnection onServiceDisconnected");
            mIsConnected = false;
            mIRemoteVoiceService = null;
            try {
                Thread.sleep(500);
                initVoiceSdk();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 熄屏/取消熄屏
     * 1101 屏幕亮屏
     * 1102 屏幕熄屏
     *
     * @param open Boolean 熄屏=false
     *             1 亮屏成功
     *             2 熄屏成功
     *             3 已亮屏
     *             4 已熄屏
     */
    public int getScreenState(boolean open) {
        if (null == mIRemoteVoiceService) {
            LogUtils.d(TAG, "mIRemoteVoiceService is null");
            return 0;
        }
        int status = 0;
        try {
            String statusStr = mIRemoteVoiceService.onCall("com.voyah.ai.voice", open ? Common.Screen.SCREEN_ON : Common.Screen.SCREEN_OFF, "");
            LogUtils.d(TAG, "getScreenState status: " + statusStr);
            JSONObject jsonObject = new JSONObject(statusStr);
            status = jsonObject.getInt("status");
            LogUtils.d(TAG, "ScreenState: " + status);
            //成远说的这句话不能放在这里了。需要移出去
//            if (status == 2) {
//                VoiceImpl.getInstance().exDialog();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 打开/关闭负一屏
     *
     * @param open
     * @return 1.打开成功
     * 2.关闭成功
     * 3.正在打开
     * 4.已打开
     * 5.正在关闭
     * 6.已关闭
     * {"action":1001,"status":1}
     */
    public int getNegativeScreenState(boolean open) {
        if (null == mIRemoteVoiceService) {
            LogUtils.d(TAG, "mIRemoteVoiceService is null");
            return 0;
        }
        int status = 0;
        try {
            String request = mIRemoteVoiceService.onCall("com.voyah.ai.voice", open ? Common.QsPanel.QS_PANEL_SHOW : Common.QsPanel.QS_PANEL_HIDE, "");
            try {
                JSONObject jsonObject = new JSONObject(request);
                status = jsonObject.getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtils.d(TAG, "NegativeScreenState: " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    //负一屏是否已打开(最新提供获取状态接口-0823)
    public boolean isNegativeScreenOpen() {
        if (null == mIRemoteVoiceService) {
            LogUtils.d(TAG, "mIRemoteVoiceService is null");
            return false;
        }
        //{"action":1003,"status":2}
        boolean isNegativeScreenOpen = false;
        try {
            String request = mIRemoteVoiceService.onCall("com.voyah.ai.voice", QS_PANEL_STATE, "");
            LogUtils.d(TAG, "isNegativeScreenOpen request:" + request);
            if (!StringUtils.isBlank(request)) {
                JSONObject jsonObject = new JSONObject(request);
                if ((int) jsonObject.get("action") == QS_PANEL_STATE) {
                    isNegativeScreenOpen = (int) jsonObject.get("status") == QS_PANEL_STATE_OPENED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNegativeScreenOpen;
    }

    //负一屏是否已关闭(最新提供获取状态接口-0823)
    public boolean isNegativeScreenClose() {
        if (null == mIRemoteVoiceService) {
            LogUtils.d(TAG, "mIRemoteVoiceService is null");
            return false;
        }
        //{"action":1003,"status":2}
        boolean isNegativeScreenClose = false;
        try {
            String request = mIRemoteVoiceService.onCall("com.voyah.ai.voice", QS_PANEL_STATE, "");
            LogUtils.d(TAG, "isNegativeScreenClose request:" + request);
            if (!StringUtils.isBlank(request)) {
                JSONObject jsonObject = new JSONObject(request);
                if ((int) jsonObject.get("action") == QS_PANEL_STATE) {
                    isNegativeScreenClose = (int) jsonObject.get("status") == QS_PANEL_STATE_CLOSED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNegativeScreenClose;
    }

    //空调界面是否已打开
    public boolean isAirPageOpen(String screenType) {
//        LogUtils.d(TAG, "isAirOpen:" + isAirOpen);
        boolean curAirPageState = HvacServiceImpl.getInstance(Utils.getApp()).isCurrentState("ACTION_IS_AC_FOREGROUND");
        return curAirPageState;
    }

    public void closeAirPage() {
        HvacServiceImpl.getInstance(mContext).exec("ACTION_CLOSE_AC_PAGE");
    }

    public void closeSeatPage() {
        SeatServiceImpl.getInstance(mContext).exec("ACTION_CLOSE_SEAT_PANEL");
    }

    public boolean isScreenCleanMode() {
        try {
            if (SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode")) {
                LogUtils.d(TAG, "close NegativeScreen， ExitCleanMode");
                return true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeScreenCleanMode() {
        try {
            if (SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode")) {
                SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
