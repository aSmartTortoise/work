package com.voyah.ai.device.voyah.common.H56Car;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;
import com.voyah.cockpit.appadapter.aidlimpl.SystemUIInterfaceImp;
import com.voyah.cockpit.appadapter.constant.SystemUiCommand;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import mega.car.config.H56D;
import mega.car.config.Infotainment;
import mega.car.config.Qnx;
import mega.car.config.Signal;

/**
 * @Date 2024/7/24 9:47
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SysSettingHelper {
    private static final String TAG = SysSettingHelper.class.getSimpleName();

    /**===========================蓝牙 START========================**/
    public static boolean getBlueToothSwitch(int displayId) {
        switch (displayId) {
            case 0:
            case 1:
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                boolean isBluetoothEnabled;
                if (bluetoothAdapter == null) {
                    return false;
                } else {
                    // 获取蓝牙的当前状态
                    isBluetoothEnabled = bluetoothAdapter.isEnabled();
                }
                return isBluetoothEnabled;
            case 1003:
                return false;
            case 2003:
                return true;
        }
        return false;
    }

        public static void setBlueToothSwitch(int displayId, boolean onOff) {
            try {
                IFunctionManagerImpl.getInstance(Utils.getApp()).setBlueToothSwitch(onOff);
                switch (displayId) {
                    case 0:
                    case 1:
                        IFunctionManagerImpl.getInstance(Utils.getApp()).setBlueToothSwitch(onOff);
                        break;
                    case 2:
                        SystemUIInterfaceImp.getInstance().command("com.voyah.ai.voice",
                                onOff ? SystemUiCommand.CEILING.QS_PANEL_BT_SWITCH.OPEN : SystemUiCommand.CEILING.QS_PANEL_BT_SWITCH.CLOSE);
                        break;
                }
            } catch (RemoteException e) {
                //
            }
        }

            /**===========================热点 START========================**/

    public static boolean getHotSpotSwitch() {
        try {
            WifiManager manager = (WifiManager) Utils.getApp().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            int state = (Integer) method.invoke(manager);
            LogUtils.d(TAG, "getWifiApState: " + state);
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            int value = (Integer) field.get(manager);
            LogUtils.d(TAG, "WIFI_AP_STATE_ENABLED: " + value);
            return state == value;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 NoSuchFieldException | IllegalArgumentException e) {
            LogUtils.e(TAG, "getHotspotSwitchState error");
        }
        return false;
    }

    public static void setHotSpotSwitch(boolean onOff) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setHotSpotSwitch(onOff);
        } catch (RemoteException e) {
            //
        }
    }
    /**===========================WIFI START========================**/
    public static boolean getWifiSwitch() {
        try {
            //0 代表打开
            return IFunctionManagerImpl.getInstance(Utils.getApp()).getWifiState() == 0;
        } catch (RemoteException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public static void setWifiSwitch(boolean onOff) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setWiFiSwitch(onOff);
        } catch (RemoteException e) {
            //
        }
    }

    /**===========================无线充电 START========================**/

    /**
     * 0 未激活
     * 1 关闭
     * 2 打开
     * 3 无效
     * 56车型需要区分N2，N2为只有前排支持
     * @return true未激活/false已激活
     */
    public static boolean isChargeEnable(int area) {
        int workingStatesFL = CarPropUtils.getInstance().getIntProp(H56D.WCM_state_WCM_WORKINGSTS);
        int workingStatesFR = CarPropUtils.getInstance().getIntProp(H56D.WCM_FR_state_WCM_FR_WORKINGSTS);
        int workingStatesRL = CarPropUtils.getInstance().getIntProp(H56D.WCM_RL_state_WCM_RL_WORKINGSTS);
        int workingStatesRR = CarPropUtils.getInstance().getIntProp(H56D.WCM_RR_state_WCM_RR_WORKINGSTS);
        boolean isN2Car = CarServicePropUtils.getInstance().getCarModeConfig() == 2;
        LogUtils.d(TAG, "workingStatesFL:" + workingStatesFL + "workingStatesFR:" + workingStatesFR + "workingStatesRL: " + workingStatesRL + "workingStatesRR: " + workingStatesRR);
        switch (area) {
            case PositionSignal.FIRST_ROW_LEFT :
                return workingStatesFL == 0;
            case PositionSignal.FIRST_ROW_RIGHT :
                return workingStatesFR == 0;
            case PositionSignal.FIRST_ROW :
                return workingStatesFL == 0 || workingStatesFR == 0;
            case PositionSignal.REAR_ROW :
                return isN2Car || workingStatesRL == 0 || workingStatesRR == 0;
            case PositionSignal.SECOND_ROW_RIGHT :
                return isN2Car || workingStatesRR == 0;
            case PositionSignal.SECOND_ROW_LEFT :
                return isN2Car || workingStatesRL == 0;
            case PositionSignal.LEFT_COL :
                return isN2Car ? workingStatesFL == 0
                    : workingStatesFL == 0 || workingStatesRL == 0;
            case PositionSignal.RIGHT_COL :
                return isN2Car ? workingStatesFR == 0
                    : workingStatesFR == 0 || workingStatesRR == 0;
            case PositionSignal.ALL :
                return isN2Car ? workingStatesFL == 0 || workingStatesFR == 0
                    : workingStatesFL == 0 || workingStatesFR == 0 || workingStatesRL == 0 || workingStatesRR == 0;
        }
        return false;
    }

    public static void setChargeSwitchState(int area, int value) {
        switch (area) {
            case 0:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_WCMFLONOFFSET, value);
                break;
            case 1:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_WCMFRONOFFSET, value);
                break;
            case 2:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_WCMRLONOFFSET, value);
                break;
            case 3:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_WCMRRONOFFSET, value);
                break;
        }

    }

    public static int getChargeSwitchState(int area) {
        switch (area) {
            case 0:
                CarPropUtils.getInstance().getIntProp(H56D.WCM_state_WCM_WORKINGSTS);
                break;
            case 1:
                CarPropUtils.getInstance().getIntProp(H56D.WCM_FR_state_WCM_FR_WORKINGSTS);
                break;
            case 2:
                CarPropUtils.getInstance().getIntProp(H56D.WCM_RL_state_WCM_RL_WORKINGSTS);
                break;
            case 3:
                CarPropUtils.getInstance().getIntProp(H56D.WCM_RR_state_WCM_RR_WORKINGSTS);
                break;
        }
        return 0;
    }

    /**===========================音量随速 START========================**/

    public static boolean getVolumeWithSpeedStatus() {
        String currentState = SettingUtils.getInstance().getCurrentState(SettingConstants.NEW_ACTION_ISVOLUMESPEEDOPEN);
        // 56D 走设置action接口 "1" = 关  "2" = 开
        return currentState.equals("2");
    }

    public static void setVolumeFollowSpeed(boolean onOff) {
        if (onOff) {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_OPENVOLUMESPEED);
        } else {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CLOSEVOLUMESPEED);
        }
    }

    /**===========================模拟声浪 START========================**/

    public static int getVolumeImitateState() {
        try {
            int startMode = IFunctionManagerImpl.getInstance(Utils.getApp()).getSoundWave();
            LogUtils.i(TAG, "getVolumeImitateStatus : startMode-" + startMode);
            return startMode;
        } catch (RemoteException e) {
            return ISysSetting.VolumeImitate.OFF;
        }
    }

    public static void setVolumeImitate(int value) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setSoundWave(value);
        } catch (RemoteException e) {
            //
        }
    }

    /**===========================驾驶辅助播报 START========================**/

    public static int getDrvAssistBroadcastState() {
        String currentState = SettingUtils.getInstance().getCurrentState(SettingConstants.NEW_ACTION_DRIVE_AID_BROADCAST_MODE);
        return Integer.parseInt(currentState);
    }

    public static void setDrvAssistBroadcastState(int onOff) {
        if (onOff == 0) {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DRIVE_AID_BROADCAST_CLOSE);
        } else if (onOff == 1) {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DRIVE_AID_BROADCAST_CONCISE);
        } else {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DRIVE_AID_BROADCAST_DETAIL);
        }
    }

    /**===========================仪表静音 START========================**/
    public static boolean getInstrumentMuteState() {
        int state = CarPropUtils.getInstance().getIntProp(1677722108);
        Log.i(TAG, "getInstrumentMuteState state :" + state);
        return state == 1;
    }

    public static void setInstrumentMuteState(boolean mute) {
        CarPropUtils.getInstance().setIntProp(1677722108, mute ? 1 : 0);
    }

    /**===========================5G优先开关 START========================**/
    public static boolean get5GSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(1677722064);
        Log.i(TAG, "get5GState state :" + state);
        return state == 2;
    }

    public static void set5GSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(1677722064, value ? 2 : 1);
    }

    /**===========================模拟声浪 START========================**/
    public static int getSoundWaveInOut() {
        int state = CarPropUtils.getInstance().getIntProp(1677721905);
        Log.i(TAG, "getSoundWaveInOut state :" + state);
        return state;
    }

    public static void setSoundWaveInOut(int value) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setSoundWaveInOut(value == ISysSetting.VolumeImitatePos.IN_CAR);
        } catch (Exception ignore){
            //
        }
    }

    public static boolean getLowSpeedPedesWarningSwitchState() throws Settings.SettingNotFoundException {
        int carModeConfig = CarServicePropUtils.getInstance().getCarModeConfig();
        if (carModeConfig == 2) {
            //N2车型
            int value = CarPropUtils.getInstance().getIntProp(H56D.AMP_state_AMP_VSP_SOUNDENABLE);
            return value > 0;
        } else {
            //N3N4车型
//            int value = CarPropUtils.getInstance().getIntProp(H56C.AMP_Response1_AMP_VSP_SOUNDENABLE2);
            //字段默认值是2 = 科技未来
            int value = Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning_switch", 1);
            return value == 1;
        }
    }

    public static void setLowSpeedPedesWarningState(boolean state) {
        int carModeConfig = CarServicePropUtils.getInstance().getCarModeConfig();
        if (carModeConfig == 2) {
            CarPropUtils.getInstance().setIntProp(H56D.IVI_infoSet_IVI_VSPONOFFSET, state ? 2 : 1);
        } else {
            int soundLowSpeedWarning = Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", 2);
            CarPropUtils.getInstance().setIntProp(H56D.IVI_infoSet1_IVI_VSPONOFFSET2, state ? soundLowSpeedWarning : 0);
            Settings.System.putInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning_switch", state ? 1 : 0);
        }
    }

    public static int getLowSpeedPedesWarningMode() throws Settings.SettingNotFoundException {
        return Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", 2);
    }

    public static void setLowSpeedPedesWarningMode(int mode) throws Settings.SettingNotFoundException {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_infoSet1_IVI_VSPONOFFSET2, mode);
        Settings.System.putInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", mode);
    }

    public static void startSceneModeService(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClassName("com.voyah.cockpit.scenemode", "com.voyah.cockpit.scenemode.service.ScenceModeService");
        Utils.getApp().startService(intent);
    }

    public static void showMediaOutsideDialog() {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).turnOnMediaOutside();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

//    public static int getHeadrestSoundState() {
//        String currentState = SettingUtils.getInstance().getCurrentState(SettingConstants.NEW_ACTION_HEADREST_MODE);
//        return Integer.parseInt(currentState);
//    }

    public static void setHeadrestSoundState(int value) {
        if (value == 1) {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_HEADREST_CLOSE);
        } else if (value == 2) {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_HEADREST_FOCUS_ON);
        } else {
            SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_HEADREST_PRIVATE);
        }
    }

    public static void setVolumeFeatureModeN2(int value) {
        CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_DYNAMICAUDIO_MODE, value);
        Utils.getApp().getContentResolver().notifyChange(Settings.System.getUriFor("key_characteristic_sound"), null);
    }

    public static int setVolumeType() {
        if (VolumeUtils.getInstance().getNaviSpeakState() == 1) {
            //有导航播报优先走导航
            LogUtils.i(TAG, "setVolumeType :" + "navi");
            return 1;
        } else {
            LogUtils.i(TAG, "setVolumeType :" + "media");
            return 2;
        }

    }

    public static void setInstumentTone(int value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SOUND_FIELD_INSTUMENT_TONE, value);
        Settings.System.putInt(Utils.getApp().getContentResolver(), "vol_instrument_tone", value);
    }

    public static boolean getRemoteKeyBroadcastState() {
        int keyNotDetected = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_not_detected", 1);
        return keyNotDetected == 1;
    }

    public static void setRemoteKeyBroadcastState(boolean value) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_not_detected", value ? 1 : 0);
    }

    public static int getSoundEffectsMode() {
        return Settings.Global.getInt(Utils.getApp().getContentResolver(), "sound_effect_mode", 1);
    }

    public static void setSoundEffectsMode(int value) {
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "sound_effect_mode", value);
        CarPropUtils.getInstance().setIntProp(H56D.IVI_infoSet1_IVI_VIRTUALSOUNDFIELDSET, value);
    }

    public static int getVolumeFocusMode() {
        int keyFocusSound = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 1);
        return keyFocusSound;
    }

    public static void setVolumeFocusMode(int value) {
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", value);
    }

    public static boolean getIntelligentVolume() {
        return Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", 0) == 1;
    }

    public static void setIntelligentVolume(boolean value) {
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", value ? 1 : 0);
    }

    public static boolean getAutoParkingHintState() {
        //0x0: OFF  0x1: ON
        return CarPropUtils.getInstance().getIntProp(Qnx.ID_DRIVING_AUTOHOLDSOUND) == 1;
    }

    public static void setAutoParkingHintState(boolean value) {
        CarPropUtils.getInstance().setIntProp(Qnx.ID_DRIVING_AUTOHOLDSOUND, value ? 1 : 0);
    }
}
