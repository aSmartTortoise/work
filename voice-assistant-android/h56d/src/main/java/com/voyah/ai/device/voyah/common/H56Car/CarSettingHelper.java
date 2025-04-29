package com.voyah.ai.device.voyah.common.H56Car;


import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ICommon.Switch;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;

import mega.car.VehicleArea;
import mega.car.config.Driving;
import mega.car.config.EntryLocks;
import mega.car.config.H56D;
import mega.car.config.Lighting;
import mega.car.config.ParamsCommon;
import mega.car.config.Signal;
import mega.car.config.VehicleMotion;
import mega.car.config.Windows;

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
            CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_SAFE_ESP_ENABLE, 2);
        } else {
            CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_SAFE_ESP_ENABLE, 1);
        }
    }

    public static void setEleParking(int onOff) {
        CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_PARKING_BRAKE_EPB_ON_AVA, onOff);
    }

    public static boolean getHillDescent() {
        int value = CarPropUtils.getInstance().getIntProp(VehicleMotion.ID_BRAKE_CTRL_HILL_DESCENT);
        if (1 == value) {
            return true;
        } else if (0 == value) {
            return false;
        }
        return false;
    }

    public static void setHillDescent(boolean on) {
        int valueToSet = on ? 1 : 2;
        CarPropUtils.getInstance().setIntProp(VehicleMotion.ID_BRAKE_CTRL_HILL_DESCENT, valueToSet);
    }

    public static int getAutoParking() {
        return CarPropUtils.getInstance().getIntProp(H56D.IPB7_IPB_AVHMODEWSTS);
    }

    public static void setAutoParking(int value) {
        int valueToSet = 0;
        if (value == ICarSetting.AvhMode.AUTO) {
            valueToSet = 1;
        } else if (value == ICarSetting.AvhMode.DEPP_STEP) {
            valueToSet = 2;
        }
        CarPropUtils.getInstance().setIntProp(H56D.IVI_chassisSet_IVI_AVHMODESET, valueToSet);
    }

    public static int getEnergyRecovery() {
        int value = CarPropUtils.getInstance().getIntProp(Driving.ID_DRV_BRK_REGEN_MODE);
        switch (value) {
            case 2:
                return ICommon.Level.LOW;
            case 3:
                return ICommon.Level.MID;
            case 4:
                return ICommon.Level.HIGH;
            case 5:
                return ICommon.Level.AUTO;
            case 1:
            default:
                return ICommon.Level.OFF;
        }
    }

    public static void setEnergyRecovery(int level) {
        int valueToSet = 1;
        switch (level) {
            case ICommon.Level.LOW:
                valueToSet = 2;
                break;
            case ICommon.Level.MID:
                valueToSet = 3;
                break;
            case ICommon.Level.HIGH:
                valueToSet = 4;
                break;
            case ICommon.Level.AUTO:
                valueToSet = 5;
        }
        Settings.System.putInt(Utils.getApp().getContentResolver(), "drv_brk_regen_mode", valueToSet);
        CarPropUtils.getInstance().setIntProp(Driving.ID_DRV_BRK_REGEN_MODE, valueToSet);
    }

    public static int getRemainMileage() {
        int value = CarPropUtils.getInstance().getIntProp(Driving.ID_REMAINING_MILEAGESWITCH);
        if (value == 0) {
            return ICarSetting.RemainMileage.STANDARD;
        } else if (value == 1) {
            return ICarSetting.RemainMileage.REAL;
        } else if (value == 2) {
            return ICarSetting.RemainMileage.PERCENT;
        }
        LogUtils.d(TAG, "get prop error. so return default value: [mileage, standard]");
        return ICarSetting.RemainMileage.STANDARD;
    }

    public static void setRemainMileage(int mode) {
        int valueToSet = 0; //CLTC  //20250115备注，设置给的2
        switch (mode) {
            case ICarSetting.RemainMileage.REAL:
                valueToSet = 1;
                break;
            case ICarSetting.RemainMileage.PERCENT:
                valueToSet = 2;
                break;
            case ICarSetting.RemainMileage.STANDARD:
            default:
                valueToSet = 2;
                break;
        }
        LogUtils.d(TAG, "setRemainingMileage:" + valueToSet);
        CarPropUtils.getInstance().setIntProp(Driving.ID_REMAINING_MILEAGESWITCH, valueToSet);
    }

    public static void setChildLockStatus(int area, boolean value) {
        // 0x1: UNLOCK 0x2: LOCK
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK, VehicleArea.ALL, value ? 1 : 2);
    }

    /**
     * 自动雨刮开关
     * @return 开：true 关：false
     */
    public static boolean getAutoWipeSwitch() {
        return CarPropUtils.getInstance().getIntProp(150994963, VehicleArea.FRONT_ROW) == ParamsCommon.OnOff.ON;
    }

    /**==============================电池维温 START==================================**/
    public static boolean getBatteryWarmSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(1677722048);
        LogUtils.d(TAG, "getBatterySwitchState: " + state);
        return state == 2;
    }

    public static void setBatteryWarmSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(1677722048,
                value ? 2 : 1);
    }

    /**==============================对车放电开关 START==================================**/
    public static boolean getV2VSwitch() {
        int state = CarPropUtils.getInstance().getIntProp(1677722066);
        LogUtils.d(TAG, "getVehicleDischargingSwitchState: " + state);
        return state == 2;
    }

    public static boolean isV2VInvalid() {
//        int state = CarPropUtils.getInstance().getIntProp(Signal.ID_V2V_DISCHRG);
//        LogUtils.d(TAG, "getV2VSupport: " + state);
//        return state == ONOFFSTS_INVALID;
        return true;
    }

    public static void setV2VSwitch(boolean value) {
        CarPropUtils.getInstance().setIntProp(1677722066,
                value ? 2 : 1);
    }

    /**==============================智能推荐 START==================================**/
    public static boolean getIntelligentRecommendSwitch() {
        int status = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_recommend_switch", 0);
        Log.i(TAG, "getIntelligentRecommend status :" + status);
        return status == 1;
    }

    public static void setIntelligentRecommendSwitch(boolean value) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_recommend_switch", value ? 1 : 0);
    }

    /**==============================驻车灯效 START==================================**/
    public static boolean getParkingLights() {
        int state = CarPropUtils.getInstance().getIntProp(1677722036);
        Log.i(TAG, "getSeatBeltReminderState state :" + state);
        return state > 1;
    }

    public static void setParkingLights(boolean value) {
        CarPropUtils.getInstance().setIntProp(1677722036,
                value ? 2 : 1);
    }

    /**==============================遥控钥匙近车自动解锁 START==================================**/
    public static boolean getRemoteKeyAutoUnlock() {
        //1=开 2=关
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_APPROACH_UNLOCK);
        Log.i(TAG, "getRemoteKeyAutoUnlock state :" + state);
        return state == 1;
    }

    public static void setRemoteKeyAutoUnlock(boolean value) {
        //1=开 2=关
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_APPROACH_UNLOCK, value ?  1 : 2);
    }

    /**==============================遥控钥匙离车自动上锁 START==================================**/
    public static boolean getRemoteKeyAutoLock() {
        //1=开 2=关
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK);
        return state == 1;
    }

    public static void setRemoteKeyAutoLock(boolean value) {
        //1=开 2=关
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK, value ?  1 : 2);
    }

    /**==============================蓝牙钥匙近车自动解锁 START==================================**/
    public static boolean getBTAutoUnlock() {
        //0:开 1:关
        int state = CarPropUtils.getInstance().getIntProp(H56D.BCM_state4_BCM_BLEAPPROACHUNLOCKSTA);
        return state == 0;
    }

    public static void setBTAutoUnlock(boolean value) {
        //1=开 2=关
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BD_CFSet_IVI_BLEAPPROACHUNLOCKSET, value ? 1 : 2);
    }

    /**==============================蓝牙钥匙离车自动上锁 START==================================**/
    public static boolean getBTAutoLock() {
        //0:开 1:关
        int state = CarPropUtils.getInstance().getIntProp(H56D.BCM_state4_BCM_BLEAWAYLOCKSTA);
        return state == 0;
    }

    public static void setBTAutoLock(boolean value) {
        //1=开 2=关
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BD_CFSet_IVI_BLEAWAYLOCKSET, value ? 1 : 2);
    }

    public static void setComfortParking(boolean value) {
        CarPropUtils.getInstance().setIntProp(Driving.ID_DRV_COMFORT_PARKING,value ? 1 : 2);
    }

    public static int getWelcomeLight() {
        int value = CarPropUtils.getInstance().getIntProp(Lighting.ID_EXT_LIGHT_WELCOME_MODE);
        return value - 1;
    }

    public static void setWelcomeLight(int value) {
        CarPropUtils.getInstance().setIntProp(Lighting.ID_EXT_LIGHT_WELCOME_MODE, value + 1);
    }

    public static int getWipeState() {
        int value = CarPropUtils.getInstance().getIntProp(Windows.ID_WASH_WIPER_SERVICE_MODE);
        return value;
    }

    public static void setWipeState(int value) {
        CarPropUtils.getInstance().setIntProp(Windows.ID_WASH_WIPER_SERVICE_MODE, value == 0 ? 2 : 1);
    }

    public static int getWipeStateSensitive() {
        int value = CarPropUtils.getInstance().getIntProp(H56D.BCM_state4_BCM_AUTOWIPERSTA);
        return value;
    }

    public static void setWipeStateSensitive(int value) {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet3_IVI_AUTOWIPER, value);
    }

    public static boolean getCentralLockStatus(){
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_DOOR_LOCK) == 1;
    }

    public static void setCentralLockStatus(boolean value){
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_DOOR_LOCK, value ? 1 : 2);
    }

    public static boolean getUnlockSettingStatus(int position) {
        int state = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_GEAR_PARK_UNLOCK_MODE);
        if (position == 1) {
            return state == 2;
        } else {
            return state == 1;
        }
    }

    public static void setUnlockSettingStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_GEAR_PARK_UNLOCK_MODE, value ? 2 : 1);
    }

    public static boolean getAutoLockStatus() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK) == 1;
    }

    public static void setAutoLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK, value ? 1 : 0);
    }

    public static boolean getParkingLockStatus() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_GEAR_UNLOCK_TYPE) == 1;
    }

    public static void setParkingLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_GEAR_UNLOCK_TYPE, value ? 1 : 2);
    }

    public static boolean getWhistleLockStatus() {
        return CarPropUtils.getInstance().getIntProp(H56D.BCM_state4_BCM_LOCKTIPSSTS) == ParamsCommon.OnOffInvalid.ON;
    }

    public static void setWhistleLockStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet2_IVI_LOCKTIPSSET, value ? ICommon.Switch.ON : ICommon.Switch.OFF);
    }

    public static boolean getAutoCloseWindowStatus() {
        return CarPropUtils.getInstance().getIntProp(Windows.ID_WINDOW_SET_LOKUP) == 1;
    }

    public static void setAutoCloseWindowStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Windows.ID_WINDOW_SET_LOKUP, value ? 1 : 2);
    }

    public static boolean getLockRmmAutoFoldStatus() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_MIRRORAUTOFOLDUNFOLDSET) == 0;
    }

    public static void setLockRmmAutoFoldStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_MIRRORAUTOFOLDUNFOLDSET, value ? 2 : 1);
    }

    public static boolean getBackupRmAutoDownStatus() {
        return CarPropUtils.getInstance().getIntProp(H56D.GW_DCU_state_DCU_FL_MIRRORAUTOTURNDOWNSTS_0X27E) == 1;
    }

    public static void setBackupRmAutoDownStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet2_IVI_RVMIRRORAUTOTURNDOWNSET, value ? 2 : 1);
    }

    public static boolean getPositionLightsFollowDrlStatus() {
        return CarPropUtils.getInstance().getIntProp(H56D.BCM_state4_BCM_REARPOSITIONLAMPFOLLOW) == 0;
    }

    public static void setPositionLightsFollowDrlStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet3_IVI_REARPOSITIONLAMPFOLLOWSET, value ? 1 : 2);
    }

    public static boolean getIntelligentHighBeamAssistStatus() {
        // 0=关 1=开
        return CarPropUtils.getInstance().getIntProp(H56D.MDC_bodyCtl_MDC_HMAENABLESTATUS) == 1;
    }

    public static void setIntelligentHighBeamAssistStatus(boolean value) {
        // 1=开 2=关
        CarPropUtils.getInstance().setIntProp(H56D.IVI_icmSet_IHBC_MAINSWITCH, value ? 1 : 2);
    }

    public static boolean getTopLightsAutoTurnOnStatus() {
        return CarPropUtils.getInstance().getIntProp(H56D.BCM_state2_BCM_DOORLIGHTCTRLFB) == 1;
    }

    public static void setTopLightsAutoTurnOnStatus(boolean value) {
        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet2_IVI_DOORLIGHTCTRL, value ? 1 : 2);
    }

    public static boolean getWelcomeLightStatus() {
        return CarPropUtils.getInstance().getIntProp(Lighting.ID_EXT_LIGHT_WELCOME_MODE) > 1;
    }
}
