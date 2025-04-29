package com.voyah.ai.device.voyah.common.H37Car;

import static mega.car.Signal.ParamsCommonOnOffInvalid.ONOFFSTS_INVALID;
import static mega.car.Signal.ParamsCommonOnOffInvalid.ONOFFSTS_OFF;
import static mega.car.Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;

import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import java.util.Arrays;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Cabin;
import mega.car.config.Driving;
import mega.car.config.Driving.ParamsDrvBrkRegenMode;
import mega.car.config.EntryLocks;
import mega.car.config.Lighting;
import mega.car.config.ParamsCommon;
import mega.car.config.VehicleBody;
import mega.car.config.VehicleMotion;
import mega.car.config.Windows;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/7/18 14:43
 * @Author 8327821
 * @Email *
 * @Description 处理37车需要特殊处理的情况
 **/
public class CarSettingHelper {

    private static final String TAG = "CarSettingHelper";


    /**
     * 37车型 车身稳定系统的信号是反的
     *
     * @return
     */
    public static boolean getSafeEsp() {
        int value = CarPropUtils.getInstance().getIntProp(VehicleMotion.ID_SAFE_ESP_ENABLE);
        return ParamsCommon.OnOffInvalid.OFF == value;
    }

    public static void setSafeESP(boolean value) {
        if (value) {
            CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_SAFE_ESP_ENABLE, 0);
        } else {
            CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_SAFE_ESP_ENABLE, 1);
        }
    }

    public static void setEleParking(int onOff) {
        CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_PARKING_BRAKE_EPB_ON_AVA, onOff);
    }

    public static boolean getHillDescent() {
        int value = CarPropUtils.getInstance().getIntProp(VehicleMotion.ID_BRAKE_CTRL_HILL_DESCENT);
        if (VehicleMotion.HillDescentControlStatus.ON_NOT_ACTIVE_BRAKING == value) {
            return true;
        } else if (VehicleMotion.HillDescentControlStatus.OFF == value) {
            return false;
        }
        return false;
    }

    public static void setHillDescent(boolean on) {
        int valueToSet = on ? VehicleMotion.HillDescentControlStatus.ON_ACTIVE_BRAKING : VehicleMotion.HillDescentControlStatus.OFF;
        CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_BRAKE_CTRL_HILL_DESCENT, valueToSet);
    }

    public static int getAutoParking() {
        return CarPropUtils.getInstance().getIntProp(VehicleMotion.ID_PARKING_BRAKE_EPB);
    }

    public static void setAutoParking(int value) {
        int valueToSet = Signal.AvhModeReq.NOREQUEST;
        if (value == ICarSetting.AvhMode.AUTO) {
            valueToSet = Signal.AvhModeReq.AUTOHOLDMODE;
        } else if (value == ICarSetting.AvhMode.DEPP_STEP) {
            valueToSet = Signal.AvhModeReq.SECONDFOOTMODE;
        }
        CarPropUtils.getInstance().setIntProp(Signal.ID_AVHMODE_SET, valueToSet);
    }

    public static int getEnergyRecovery() {
        int value = CarPropUtils.getInstance().getIntProp(Driving.ID_DRV_BRK_REGEN_MODE);
        switch (value) {
            case ParamsDrvBrkRegenMode.LOW_LEVEL:
                return ICommon.Level.LOW;
            case ParamsDrvBrkRegenMode.NORMAL:
                return ICommon.Level.MID;
            case ParamsDrvBrkRegenMode.STRONG:
                return ICommon.Level.HIGH;
            case ParamsDrvBrkRegenMode.OFF:
            default:
                return ICommon.Level.INVALID;
        }
    }

    public static void setEnergyRecovery(int level) {
        int valueToSet = ParamsDrvBrkRegenMode.OFF;
        switch (level) {
            case ICommon.Level.LOW:
                valueToSet = ParamsDrvBrkRegenMode.LOW_LEVEL;
                break;
            case ICommon.Level.MID:
                valueToSet = ParamsDrvBrkRegenMode.NORMAL;
                break;
            case ICommon.Level.HIGH:
                valueToSet = ParamsDrvBrkRegenMode.STRONG;
        }
        CarPropUtils.getInstance().setIntProp(Driving.ID_DRV_BRK_REGEN_MODE, valueToSet);
    }

    public static int getRemainMileage() {
        if (CarServicePropUtils.getInstance().isH37A()) {
            Integer[] prop = (Integer[]) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_REMAINGMILEAGE_MODE_ARRAY).getValue();
            if (prop != null && prop.length == 2) {
                int percentOrMileage = prop[0] == null ? 0 : prop[0];
                int cltcOrStandard = prop[1] == null ? 0 : prop[1];
                if (1 == percentOrMileage) {
                    return ICarSetting.RemainMileage.PERCENT;
                } else {
                    if (0 == cltcOrStandard) {
                        return ICarSetting.RemainMileage.STANDARD;
                    } else {
                        return ICarSetting.RemainMileage.REAL;
                    }
                }
            }
        } else if (CarServicePropUtils.getInstance().isH37B()) {
            int value = CarPropUtils.getInstance().getIntProp(Driving.ID_REMAINGMILEAGE_MODE);
            if (value == 0) {
                return ICarSetting.RemainMileage.STANDARD;
            } else if (value == 1) {
                return ICarSetting.RemainMileage.REAL;
            } else if (value == 2) {
                return ICarSetting.RemainMileage.PERCENT;
            }
        }
        LogUtils.d(TAG, "get prop error. so return default value: [mileage, standard]");
        return ICarSetting.RemainMileage.STANDARD;
    }

    public static void setRemainMileage(int mode) {
        if (CarServicePropUtils.getInstance().isH37A()) {
            Integer[] param = new Integer[2];
            switch (mode) {
                case ICarSetting.RemainMileage.STANDARD:
                    param[0] = 0;
                    param[1] = 0;
                    break;
                case ICarSetting.RemainMileage.REAL:
                    param[0] = 0;
                    param[1] = 1;
                    break;
                case ICarSetting.RemainMileage.PERCENT:
                    param[0] = 1;
                    param[1] = 0;
                    break;
            }
            LogUtils.d(TAG, "setRemainingMileage:" + Arrays.toString(param));
            CarPropertyValue<Integer[]> value = new CarPropertyValue<>(Signal.ID_REMAINGMILEAGE_MODE_ARRAY, param);
            CarPropUtils.getInstance().setRawProp(value);
        } else if (CarServicePropUtils.getInstance().isH37B()) {
            CarPropUtils.getInstance().setIntProp(Driving.ID_REMAINGMILEAGE_MODE, mode);
        }
    }

    public static void setChildLockStatus(int area, boolean onOff) {
        if (area == 1) {
            CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK, VehicleArea.ALL, onOff ? Signal.ParamsChildLockUnlockCmd.REAR_LEFT_UNLOCK : Signal.ParamsChildLockUnlockCmd.REAR_LEFT_LOCK);
        } else if (area == 2) {
            CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK, VehicleArea.ALL, onOff ? Signal.ParamsChildLockUnlockCmd.REAR_RIGHT_UNLOCK : Signal.ParamsChildLockUnlockCmd.REAR_RIGHT_LOCK);
        } else {
            CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK, VehicleArea.ALL, onOff ? Signal.ParamsChildLockUnlockCmd.REAR_ALL_UNLOCK : Signal.ParamsChildLockUnlockCmd.REAR_ALL_LOCK);
        }
    }

    /**
     * 自动雨刮开关
     * @return 开：true 关：false
     */
    public static boolean getAutoWipeSwitch() {
        return CarPropUtils.getInstance().getIntProp(VehicleBody.ID_WIPER, VehicleArea.FRONT_ROW) == ParamsCommon.OnOff.ON;
    }

    /**==============================电池维温 START==================================**/
    public static boolean getBatteryWarmSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_BATT_KEEP_WAIM);
        LogUtils.d(TAG, "getBatterySwitchState: " + state);
        return state == ONOFFSTS_ON;
    }

    public static void setBatteryWarmSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_BATT_KEEP_WAIM,
                value ? Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    /**==============================对车放电开关 START==================================**/
    public static boolean getV2VSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_V2V_DISCHRG);
        LogUtils.d(TAG, "getVehicleDischargingSwitchState: " + state);
        return state == ONOFFSTS_ON;
    }

    public static boolean isV2VInvalid() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_V2V_DISCHRG);
        LogUtils.d(TAG, "getV2VSupport: " + state);
        return state == ONOFFSTS_INVALID;
    }

    public static void setV2VSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_V2V_DISCHRG,
                value ? Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    /**==============================智能推荐 START==================================**/
    public static boolean getIntelligentRecommendSwitch() {
        // 37A 和 37B默认值不同，37A=1  37B=0
        String carType = CarServicePropUtils.getInstance().getCarType();
        int status = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_recommend_switch", carType.equalsIgnoreCase("H37A") ? 1 : 0);
        Log.i(TAG, "getIntelligentRecommend status :" + status);
        return status == 1;
    }

    public static void setIntelligentRecommendSwitch(boolean value) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_recommend_switch", value ? 1 : 0);
    }

    /**==============================驻车灯效 START==================================**/
    public static boolean getParkingLights() {
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_PARK_LIGHT_SET);
        Log.i(TAG, "getSeatBeltReminderState state :" + state);
        return state > 1;
    }

    public static void setParkingLights(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_PARK_LIGHT_SET,
                value ? Signal.ParamsLightingModeReq.MODE1 : Signal.ParamsLightingModeReq.OFF);
    }

    /**==============================遥控钥匙近车自动解锁 START==================================**/
    public static boolean getRemoteKeyAutoUnlock() {
        //1:关 2:开
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_RKE_WLCM_AUTOLOCK);
        Log.i(TAG, "getRemoteKeyAutoUnlock state :" + state);
        return state == 2;
    }

    public static void setRemoteKeyAutoUnlock(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_RKE_WLCM_AUTOLOCK, value ?  Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    /**==============================遥控钥匙离车自动上锁 START==================================**/
    public static boolean getRemoteKeyAutoLock() {
        //1:关 2:开
        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_RKE_AWAY_AUTOLOCK);
        Log.i(TAG, "getRemoteKeyAutoLock state :" + state);
        return state == 2;
    }

    public static void setRemoteKeyAutoLock(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_RKE_AWAY_AUTOLOCK, value ?  Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    /**==============================蓝牙钥匙近车自动解锁 START==================================**/
    public static boolean getBTAutoUnlock() {
        //0:关 1:开
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_APPROACH_UNLOCK);
        Log.i(TAG, "getBluetoothKeyAutoUnlock state :" + state);
        return state == 1;
    }

    public static void setBTAutoUnlock(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_APPROACH_UNLOCK,
                value ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
    }

    /**==============================蓝牙钥匙离车自动上锁 START==================================**/
    public static boolean getBTAutoLock() {
        //1:关 2:开
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK);
        Log.i(TAG, "getBluetoothKeyAutoLock state :" + state);
        return state == 1;
    }

    public static void setBTAutoLock(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK,
                value ?  ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
    }

    public static boolean getCentralLockStatus(){
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_DOOR_LOCK) == 1;
    }

    public static void setCentralLockStatus(boolean value){
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_DOOR_LOCK, value ? ICommon.Switch.OFF : ICommon.Switch.ON);
    }

    public static boolean getCarDoorState(int position) {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_DOOR,position) == 1;
    }

    public static boolean getUnlockSettingStatus(int position) {
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_GEAR_PARK_UNLOCK_MODE);
        if (position == 1) {
            return state == ICarSetting.UnlockMode.ALL_CAR;
        } else {
            return state == ICarSetting.UnlockMode.DRIVER_ONLY;
        }
    }

    public static void setUnlockSettingStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_GEAR_PARK_UNLOCK_MODE, value ? ICarSetting.UnlockMode.ALL_CAR : ICarSetting.UnlockMode.DRIVER_ONLY);
    }

    public static boolean getAutoLockStatus() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setAutoLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getParkingLockStatus() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_GEAR_UNLOCK_TYPE) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setParkingLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_GEAR_UNLOCK_TYPE, value ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
    }

    public static boolean getWhistleLockStatus() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_ENTRY_SYSTEM_HORN_CFG) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setWhistleLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_ENTRY_SYSTEM_HORN_CFG, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getAutoCloseWindowStatus() {
        return CarPropUtils.getInstance().getIntProp(Windows.ID_WINDOW_SET_LOKUP) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setAutoCloseWindowStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Windows.ID_WINDOW_SET_LOKUP, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getLockRmmAutoFoldStatus() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_MIRRORAUTOFOLDUNFOLDSET) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setLockRmmAutoFoldStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_MIRRORAUTOFOLDUNFOLDSET, value ? Signal.ParamsMirrorAutoFoldUnfoldSet.MIRRAUTFODUNFDSET_ON : Signal.ParamsMirrorAutoFoldUnfoldSet.MIRRAUTFODUNFDSET_OFF);
    }

    public static boolean getBackupRmAutoDownStatus() {
        return CarPropUtils.getInstance().getIntProp(Cabin.ID_REAR_VIEW_MIRROR_AUTO_DIPPING) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setBackupRmAutoDownStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Cabin.ID_REAR_VIEW_MIRROR_AUTO_DIPPING, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getPositionLightsFollowDrlStatus() {
        //open = 2  close = 1
        return CarPropUtils.getInstance().getIntProp(Signal.ID_REAR_POS_LAMP_SETTING) == ONOFFSTS_ON;
    }

    public static void setPositionLightsFollowDrlStatus(boolean value) {
        //open = 2  close = 1
        CarPropUtils.getInstance().setIntProp(Signal.ID_REAR_POS_LAMP_SETTING, value ? ONOFFSTS_ON : ONOFFSTS_OFF);
    }

    public static boolean getIntelligentHighBeamAssistStatus() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_SETADBSETTING) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setIntelligentHighBeamAssistStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SETADBSETTING, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getTopLightsAutoTurnOnStatus() {
        return CarPropUtils.getInstance().getIntProp(Lighting.ID_INTERIOR_LIGHT_AUTO_DOME) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setTopLightsAutoTurnOnStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Lighting.ID_INTERIOR_LIGHT_AUTO_DOME, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getChargingEffectstatus() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_CHARGE_LIGHT_EFFECT_SETTING) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setChargingEffectstatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_CHARGE_LIGHT_EFFECT_SETTING, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }
}
