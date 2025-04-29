package com.voyah.ai.device.voyah.common.H37Car;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mega.car.Signal;
import mega.car.config.Comforts;
import mega.car.config.Infotainment;
import mega.car.config.ParamsCommon;
import mega.car.config.VehicleMotion;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/7/24 9:47
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SysSettingHelper {
    private static final String TAG = SysSettingHelper.class.getSimpleName();

    /**===========================蓝牙 START========================**/
    public static boolean getBlueToothSwitch() {
        //ConnectManagerServiceImpl.getInstance(Utils.getApp()).getBtState();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isBluetoothEnabled;
        if (bluetoothAdapter == null) {
            return false;
        } else {
            // 获取蓝牙的当前状态
            isBluetoothEnabled = bluetoothAdapter.isEnabled();
        }
        return isBluetoothEnabled;
    }

    public static void setBlueToothSwitch(boolean onOff) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setBlueToothSwitch(onOff);
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
     * @return
     */
    public static int getChargeState() {
        int workingStates = 0;
        CarPropertyValue propertyRaw = CarPropUtils.getInstance().getPropertyRaw(Comforts.ID_WIRELESS_CHARGING_STATUS);
        if (propertyRaw != null && propertyRaw.getValue() instanceof String) {
            String json = (String) propertyRaw.getValue();
            JSONObject obj = JSON.parseObject(json);
            if (obj != null) {
                workingStates = obj.getIntValue("WCM_WorkingStates");
            }
        }
        return workingStates;
    }

    /**===========================音量随速 START========================**/

    public static boolean getVolumeWithSpeedStatus() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return CarPropUtils.getInstance().getIntProp(Infotainment.ID_AMP_VEHICLESPEEDCOMP_MODEENABLE) == ParamsCommon.OnOff.ON;
        } else {
            // 37B 走设置action接口 "0"=关 "1"=开
            return SettingUtils.getInstance().getCurrentState(SettingConstants.NEW_ACTION_ISVOLUMESPEEDOPEN).equals("1");
        }
    }

    public static void setVolumeFollowSpeed(boolean onOff) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            try {
                IFunctionManagerImpl.getInstance(Utils.getApp()).setVolumeFlwSpeed(onOff);
            } catch (RemoteException e) {
                //
            }
        } else {
            // 37B 走设置action接口
            if (onOff) {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_OPENVOLUMESPEED);
            } else {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CLOSEVOLUMESPEED);
            }
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
        try {
            return IFunctionManagerImpl.getInstance(Utils.getApp()).getDriveAidBroadcastMode();
        } catch (RemoteException e) {
            return ICommon.Switch.OFF;
        }
    }

    public static void setDrvAssistBroadcastState(int onOff) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setDriveAidBroadcastMode(onOff);
        } catch (RemoteException e) {
            //
        }
    }

    /**===========================仪表静音 START========================**/
    public static boolean getInstrumentMuteState() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_CHANNELS_SILENT);
        Log.i(TAG, "getInstrumentMuteState state :" + state);
        return state == 1;
    }

    public static void setInstrumentMuteState(boolean mute) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_CHANNELS_SILENT, mute ? 1 : 0);
    }

    /**===========================5G优先开关 START========================**/
    public static boolean get5GSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_TBOX5GONOFFSET);
        Log.i(TAG, "get5GState state :" + state);
        return state == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
    }

    public static void set5GSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_TBOX5GONOFFSET, value ? Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    /**===========================模拟声浪 START========================**/
    public static int getSoundWaveInOut() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_AMP_ESE_OUTDOOR_SOUND_ON_OFF);
        Log.i(TAG, "getSoundWaveInOut state :" + state);
        return state;
    }

    public static void setSoundWaveInOut(int value) {
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setSoundWaveInOut(value == ISysSetting.VolumeImitatePos.IN_CAR);
        } catch (Exception ignore) {
            //
        }
    }

    public static boolean getNighttimeMuteSwitch() {
        Integer[] prop = (Integer[]) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_NIGHT_MUTE_LOCK_SETTING).getValue();
        return prop[0] == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
    }

    public static void setNighttimeMuteSwitch(boolean muteSwitch) {
        Integer[] param = (Integer[]) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_NIGHT_MUTE_LOCK_SETTING).getValue();
        param[0] = muteSwitch ? Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON : Signal.ParamsCommonOnOffInvalid.ONOFFSTS_OFF;
        CarPropertyValue<Integer[]> value = new CarPropertyValue<>(Signal.ID_NIGHT_MUTE_LOCK_SETTING, param);
        CarPropUtils.getInstance().setRawProp(value);
    }

    public static void setInstumentTone(int value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SOUND_FIELD_INSTUMENT_TONE, value);
    }

    public static boolean getRemoteKeyBroadcastState() {
        int keyNotDetected = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_not_detected", 1);
        return keyNotDetected == 1;
    }

    public static void setRemoteKeyBroadcastState(boolean value) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_not_detected", value ? 1 : 0);
    }

    public static int getHeadrestSoundState() {
        return Settings.System.getInt(Utils.getApp().getContentResolver(), "audio_headrest_mode", 1);
    }

    public static void setHeadrestSoundState(int value) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "audio_headrest_mode", value);
        CarPropUtils.getInstance().setIntProp(Signal.ID_AUDIO_HEADREST,value);
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

    public static int getSoundEffectsMode() {
        return Settings.Global.getInt(Utils.getApp().getContentResolver(), "sound_effect_mode", 1);
    }

    public static void setSoundEffectsMode(int value) {
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "sound_effect_mode", value);
        CarPropUtils.getInstance().setIntProp(Signal.ID_VIRTUAL_SOUND_FIELD, value);
    }

    public static int getVolumeFocusMode() {
        int keyFocusSound = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 1) + 1;
        return keyFocusSound;
    }

    public static void setVolumeFocusMode(int value) {
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", value - 1);
    }

    public static boolean getIntelligentVolume() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return Settings.System.getInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", 0) == 1;
        } else {
            return Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", 0) == 1;
        }
    }

    public static void setIntelligentVolume(boolean value) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            Settings.System.putInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", value ? 1 : 0);
        } else {
            Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_smart_sound_focus", value ? 1 : 0);
        }
    }

    public static boolean getLowSpeedPedesWarningSwitchState() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return CarPropUtils.getInstance().getIntProp(VehicleMotion.ID_ACOUSTIC_VEH_ALERT) == ParamsCommon.OnOff.ON;
        } else {
            int value = Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning_switch", 0);
            return value == 1;
        }
    }

    public static void setLowSpeedPedesWarningState(boolean state) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_ACOUSTIC_VEH_ALERT, state ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
        } else {
            int soundLowSpeedWarning = Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", 2);
            CarPropUtils.getInstance().setIntProp(Signal.ID_AMP_VSP_ONOFF2, state ? soundLowSpeedWarning : Signal.AmpVspOnOff.OFF);
            Settings.System.putInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning_switch", state ? 1 : 0);
        }
    }

    public static int getLowSpeedPedesWarningMode() {
        return Settings.System.getInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", 2);
    }

    public static void setLowSpeedPedesWarningMode(int mode) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_AMP_VSP_ONOFF2, mode);
        Settings.System.putInt(Utils.getApp().getContentResolver(), "sound_low_speed_warning", mode);
    }

    public static boolean getMediaOutsideSoundStatus() {
        // 0=关 1=开 2=开 3=自动关闭（功放有问题）
        int value = CarPropUtils.getInstance().getIntProp(Signal.ID_AMP_OUTDOOR_MODE);
        return value == 1 || value == 2;
    }
}
