package com.voyah.ai.device.voyah.h37.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.func.FuncConstants;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.SystemUIInterfaceImp;
import com.voyah.cockpit.appadapter.constant.SystemUiCommand;
import com.voyah.cockpit.seat.SeatServiceImpl;
import com.voyah.cockpit.systemui.voice.sdk.Common;
import com.voyah.cockpit.systemui.voice.sdk.IRemoteVoiceService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class SystemUiUtils {

    private static final String TAG = SystemUiUtils.class.getSimpleName();

    private static SystemUiUtils mInstance;

    private static SystemUIInterfaceImp systemUIInterfaceImp;

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
                    systemUIInterfaceImp = SystemUIInterfaceImp.getInstance();
                }
            }
        }
        return mInstance;
    }

    private SystemUiUtils() {
        initVoiceSdk();
    }

    private void initVoiceSdk() {
        Log.d(TAG, "initVoiceSdk");
        if (!mIsConnected) {
            Log.d(TAG, "connected aidl");
            Intent intent = new Intent();
            intent.setPackage("com.voyah.cockpit.systemui");
            intent.setAction("com.voyah.cockpit.voice.sdk.action.RemoteService");
            Utils.getApp().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void binderSystemUi(Context context) {
        systemUIInterfaceImp.bind(context);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
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
        int status = 0;
        try {
            if (mIRemoteVoiceService != null) {
                String statusStr = mIRemoteVoiceService.onCall("com.voyah.ai.voice", open ? Common.Screen.SCREEN_ON : Common.Screen.SCREEN_OFF, "");
                LogUtils.d(TAG, "getScreenState status: " + statusStr);
                JSONObject jsonObject = new JSONObject(statusStr);
                status = jsonObject.getInt("status");
                LogUtils.d(TAG, "ScreenState: " + status);
            } else {
                LogUtils.e(TAG, "mIRemoteVoiceService = null");
            }
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
        int resultCode = SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice", open ? SystemUiCommand.QSPanel.SHOW : SystemUiCommand.QSPanel.HIDE);
        LogUtils.d(TAG, "getNegativeScreenState resultCode:" + resultCode);
        return resultCode;
    }

    //负一屏是否已打开
    public boolean isNegativeScreenOpen() {
        int status = SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice", SystemUiCommand.QSPanel.STATE);
        LogUtils.d(TAG, "isNegativeScreenOpen status:" + status);
        return status == SystemUiCommand.QSPanelResult.STATE_OPENED;
    }

    //负一屏是否已关闭(最新提供获取状态接口-0823)
    public boolean isNegativeScreenClose() {
        int status = SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice", SystemUiCommand.QSPanel.STATE);
        LogUtils.d(TAG, "isNegativeScreenClose status:" + status);
        return status == SystemUiCommand.QSPanelResult.STATE_CLOSED;
    }

    /**
     * 指定屏幕 打开/关闭负一屏
     *
     * @param open         打开、关闭
     * @param assignScreen 指定屏幕
     */
    public void getNegativeScreenState(boolean open, String assignScreen) {
        int qsPanelSwitchInteger = getQsPanelSwitchInteger(open, assignScreen);
        int resultCode = SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice", qsPanelSwitchInteger);
        LogUtils.d(TAG, "getNegativeScreenState qsPanelSwitchInteger:" + qsPanelSwitchInteger + " ,resultCode:" + resultCode);
    }

    //指定屏幕负一屏是否已打开
    public boolean isNegativeScreenOpen(String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        int qsPanelStatusInteger = getQsPanelStatusInteger(screen);
        int status = SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice", qsPanelStatusInteger);
        LogUtils.d(TAG, "isNegativeScreenOpen qsPanelStatusInteger:" + qsPanelStatusInteger + " ,status:" + status + " ,assignScreen:" + assignScreen);
        return status == SystemUiCommand.QSPanelResult.STATE_OPENED;
    }

    //指定屏幕的应用列表是否打开
    public boolean isAssignScreenAllAppsOpen(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            return LauncherViewUtils.isAllAppsOpen(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER))
            return LauncherViewUtils.isSecondAllAppPanelShowing(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CEIL))
            return LauncherViewUtils.isCeilAllAppPanelShowing(context);
        else
            return LauncherViewUtils.isAllAppsOpen(context);
    }

    public void openAssignScreenAllApps(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            LauncherViewUtils.openAllApps(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER))
            LauncherViewUtils.openSecondAllApps(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CEIL))
            LauncherViewUtils.openCeilAllApps(context);
        else
            LauncherViewUtils.openAllApps(context);
    }

    public void closeAssignScreenAllApps(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            LauncherViewUtils.closeAllApps(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER))
            LauncherViewUtils.closeSecondAllApps(context);
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CEIL))
            LauncherViewUtils.closeCeilAllApps(context);
        else
            LauncherViewUtils.closeAllApps(context);
    }

    /**
     * @param open         是否打开
     * @param assignScreen 指定的屏幕
     * @return 返回负一屏的指定屏幕参数
     */
    private int getQsPanelSwitchInteger(boolean open, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            return open ? SystemUiCommand.QSPanel.SHOW : SystemUiCommand.QSPanel.HIDE;
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER))
            return open ? SystemUiCommand.QSPanelSecondary.SHOW : SystemUiCommand.QSPanelSecondary.HIDE;
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CEIL))
            return open ? SystemUiCommand.QSPanelCeiling.SHOW : SystemUiCommand.QSPanelCeiling.HIDE;
        else
            return open ? SystemUiCommand.QSPanel.SHOW : SystemUiCommand.QSPanel.HIDE;
    }

    private int getQsPanelStatusInteger(String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            return SystemUiCommand.QSPanel.STATE;
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER))
            return SystemUiCommand.QSPanelSecondary.STATE;
        else if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CEIL))
            return SystemUiCommand.QSPanelCeiling.STATE;
        else
            return SystemUiCommand.QSPanel.STATE;
    }

    public boolean isParkingOpen(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            return LauncherViewUtils.isParkingOpen();
        return false;
    }

    public void openParking(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            LauncherViewUtils.openParking();
    }

    public void closeParking(Context context, String assignScreen) {
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (LauncherViewUtils.isParkingOpen()) {
            if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL)) {
                LauncherViewUtils.closeParking();
            }
        }
    }


    //空调界面是否已打开
    public boolean isAirPageOpen(String screenType) {
        screenType = screenType.toLowerCase();
        LogUtils.d(TAG, "当前屏幕是：" + screenType);
        String displayID = "ACTION_IS_AC_FOREGROUND_displayId";
        if (!TextUtils.isEmpty(screenType)) {
            if (screenType.equals(FuncConstants.VALUE_SCREEN_CENTRAL)) {
                displayID = "ACTION_IS_AC_FOREGROUND_displayId";

            } else if (screenType.equals(FuncConstants.VALUE_SCREEN_PASSENGER))
                displayID = "ACTION_IS_AC_FOREGROUND_displayId2";
        }
        boolean res = HvacServiceImpl.getInstance(Utils.getApp()).isCurrentState(displayID);
        return res;
    }

    //关闭空调弹窗界面
    public void closeAirPage(String screenType) {
        LogUtils.d(TAG, "closeAirPage" + screenType);
        screenType = screenType.toLowerCase();
        String displayID = "ACTION_OPEN_AC_PAGE_displayId";
        if (!TextUtils.isEmpty(screenType)) {
            if (screenType.equals(FuncConstants.VALUE_SCREEN_CENTRAL)) {
                displayID = displayID + "_displayId";

            } else if (screenType.equals(FuncConstants.VALUE_SCREEN_PASSENGER))
                displayID = displayID + "_displayId2";
        }
        HvacServiceImpl.getInstance(Utils.getApp()).exec(displayID);
    }

    //座椅界面是否打开
    public boolean isSeatPageOpen(String screenType) {
        String displayID = "ACTION_GET_SEAT_PANEL_STATE";
        if (!TextUtils.isEmpty(screenType)) {
            if (screenType.equals(FuncConstants.VALUE_SCREEN_PASSENGER))
                displayID = displayID + "_displayId2";
        }
        LogUtils.d(TAG, "isSeatPageOpen displayID:" + displayID);
        return SeatServiceImpl.getInstance(mContext).isCurrentState(displayID);
    }


    //关闭座椅弹窗界面
    public void closeSeatPage(String screenType) {
        LogUtils.d(TAG, "closeSeatPage");
        String displayID = "ACTION_CLOSE_SEAT_PANEL";
        if (!TextUtils.isEmpty(screenType)) {
            if (screenType.equals(FuncConstants.VALUE_SCREEN_CENTRAL)) {
                displayID = displayID + "_displayId";

            } else if (screenType.equals(FuncConstants.VALUE_SCREEN_PASSENGER))
                displayID = displayID + "_displayId2";
        }
        SeatServiceImpl.getInstance(mContext).exec(displayID);
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
