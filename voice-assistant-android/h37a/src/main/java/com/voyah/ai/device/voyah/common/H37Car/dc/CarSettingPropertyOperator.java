package com.voyah.ai.device.voyah.common.H37Car.dc;


import android.car.Car;
import android.os.RemoteException;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.AdasSignal;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.device.voyah.common.H37Car.CarSettingHelper;
import com.voyah.ai.device.voyah.common.H37Car.WipeHelper;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Adas;
import mega.car.config.Cabin;
import mega.car.config.Driving;
import mega.car.config.EntryLocks;
import mega.car.config.Lighting;
import mega.car.config.VehicleMotion;
import mega.car.config.Windows;

/**
 * @Date 2024/7/17 14:21
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class CarSettingPropertyOperator extends Base37Operator implements IDeviceRegister {

    @Override
    void init() {
        map.put(CarSettingSignal.CARSET_SAFE_SENSITIVE, Adas.ID_ADAS_MON_SENSITIVITY_SW);
        map.put(CarSettingSignal.CARSET_WIPE_REPAIR_STATE, Windows.ID_WASH_WIPER_SERVICE_MODE);
        map.put(CarSettingSignal.CARSET_AUTO_WIPE_SENSITIVE, Signal.ID_WIPER_SENSITIVITY);

        map.put(CarSettingSignal.CARSET_POWER_MODE, Driving.ID_DRV_PROP_MODESET);
        map.put(CarSettingSignal.CARSET_ELE_PARKING, VehicleMotion.ID_PARKING_BRAKE_EPB);
        map.put(CarSettingSignal.CARSET_SAFE_ESP, VehicleMotion.ID_SAFE_ESP_ENABLE);
        map.put(CarSettingSignal.CARSET_COMFORT_PARKING, Driving.ID_DRV_COMFORT_PARKING);
        map.put(CarSettingSignal.CARSET_DOOR_LOCK, EntryLocks.ID_DOOR_LOCK);
        map.put(CarSettingSignal.CARSET_UNLOCK_MODE, EntryLocks.ID_LOCKS_GEAR_PARK_UNLOCK_MODE);
        map.put(CarSettingSignal.CARSET_WALK_AWAY_LOCK, EntryLocks.ID_LOCKS_MODE_WALK_AWAY_LOCK);
        map.put(CarSettingSignal.CARSET_PARKING_UNLOCK, EntryLocks.ID_GEAR_UNLOCK_TYPE);
        map.put(CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE, EntryLocks.ID_ENTRY_SYSTEM_HORN_CFG);
        map.put(CarSettingSignal.CARSET_LOCK_CLOSE_WINDOW, Windows.ID_WINDOW_SET_LOKUP);
        map.put(CarSettingSignal.CARSET_RMM_AUTO_FOLD, Signal.ID_MIRRORAUTOFOLDUNFOLDSET); //读写相同信号，但返回值含义不一样
        map.put(CarSettingSignal.CARSET_RM_AUTO_DOWN, Cabin.ID_REAR_VIEW_MIRROR_AUTO_DIPPING);
        map.put(CarSettingSignal.CARSET_HIGH_BEAM_ASSIST, Signal.ID_SETADBSETTING);
        map.put(CarSettingSignal.CARSET_INTERIOR_LIGHT_AUTO_DOME, Lighting.ID_INTERIOR_LIGHT_AUTO_DOME);
        map.put(CarSettingSignal.CARSET_CHARGE_LIGHT_EFFECT, Signal.ID_CHARGE_LIGHT_EFFECT_SETTING);
        map.put(CarSettingSignal.CARSET_FOLLOW_HOME, Lighting.ID_EXT_LIGHT_FOLLOW_ME_HOME_CFG);
        map.put(CarSettingSignal.CARSET_WELCOME_LIGHTS, Lighting.ID_EXT_LIGHT_WELCOME_MODE);
        map.put(CarSettingSignal.CARSET_AIR_REAR_LOCK, Signal.ID_ACP_R_FORBID);
        map.put(CarSettingSignal.CARSET_DOOR_FRONTLEFT,EntryLocks.ID_DOOR);//左前车门
        map.put(CarSettingSignal.CARSET_DOOR_FRONTRIGHT,EntryLocks.ID_DOOR);//右前车门
        map.put(CarSettingSignal.CARSET_DOOR_REARLEFT,EntryLocks.ID_DOOR);//左后车门
        map.put(CarSettingSignal.CARSET_DOOR_REARRIGHT,EntryLocks.ID_DOOR);//右后车门
        map.put(CarSettingSignal.CARSET_POWER_MODE_DRIVER_MODE,Driving.ID_DRV_PROP_MODESET);//经济，运动，标准
        map.put(CarSettingSignal.CARSET_DISABLE_MODE,Signal.ID_CARMODE_VMM);//获取是否处于Disable模式

        map.put(CarSettingSignal.CARSET_APPROACHING_LIGHTS,Signal.ID_PROXIMITY_LIGHTING_STS);//接近灯光

    }

    @Override
    public int getBaseIntProp(String key, int area) {

        switch (key) {
            case CarSettingSignal.CARSET_WIPE_ACTION_STATE:
                return WipeHelper.getWipeActionState();
            case CarSettingSignal.CARSET_AUTO_PARKING:
                return CarSettingHelper.getAutoParking();
            case CarSettingSignal.CARSET_ENERGY_RECOVERY:
                return CarSettingHelper.getEnergyRecovery();
            case CarSettingSignal.CARSET_REMAIN_MILEAGE:
                return CarSettingHelper.getRemainMileage();
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {

        switch (key) {
            case CarSettingSignal.CARSET_AUTO_PARKING:
                CarSettingHelper.setAutoParking(value);
                break;
            case CarSettingSignal.CARSET_ELE_PARKING:
                CarSettingHelper.setEleParking(value);
                break;
            case CarSettingSignal.CARSET_ENERGY_RECOVERY:
                CarSettingHelper.setEnergyRecovery(value);
                break;
            case CarSettingSignal.CARSET_REMAIN_MILEAGE:
                CarSettingHelper.setRemainMileage(value);
                break;
            default:
                super.setBaseIntProp(key, area, value);
                break;
        }
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {

    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case CarSettingSignal.CARSET_SAFE_ESP:
                return CarSettingHelper.getSafeEsp();
            case CarSettingSignal.CARSET_HILL_DESCENT:
                return CarSettingHelper.getHillDescent();
            case CarSettingSignal.CARSET_WIPE_AUTO_SWITCH:
                return CarSettingHelper.getAutoWipeSwitch();
            case CarSettingSignal.CARSET_BATTERY_WARM_SWITCH:
                return CarSettingHelper.getBatteryWarmSwitch();
            case CarSettingSignal.CARSET_V2V_SWITCH:
                return CarSettingHelper.getV2VSwitch();
            case CarSettingSignal.CARSET_V2V_INVALID:
                return CarSettingHelper.isV2VInvalid();
            case CarSettingSignal.CARSET_INTELLIGENT_RECOMMEND:
                return CarSettingHelper.getIntelligentRecommendSwitch();
            case CarSettingSignal.CARSET_PARKING_LIGHTS:
                return CarSettingHelper.getParkingLights();
            case CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK:
                return CarSettingHelper.getRemoteKeyAutoUnlock();
            case CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK:
                return CarSettingHelper.getRemoteKeyAutoLock();
            case CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK:
                return CarSettingHelper.getBTAutoUnlock();
            case CarSettingSignal.CARSET_BTKEY_AUTO_LOCK:
                return CarSettingHelper.getBTAutoLock();
            case CarSettingSignal.CARSER_SUPER_POWER_SAVING_CONFIG: //超级省电配置
                return true;
            case CarSettingSignal.CARSET_DOOR_LOCK:
                return CarSettingHelper.getCentralLockStatus();
            case CarSettingSignal.CARSET_DOOR_FRONTLEFT:
                return CarSettingHelper.getCarDoorState(VehicleArea.FRONT_LEFT);
            case CarSettingSignal.CARSET_DOOR_FRONTRIGHT:
                return CarSettingHelper.getCarDoorState(VehicleArea.FRONT_RIGHT);
            case CarSettingSignal.CARSET_DOOR_REARLEFT:
                return CarSettingHelper.getCarDoorState(VehicleArea.REAR_LEFT);
            case CarSettingSignal.CARSET_DOOR_REARRIGHT:
                return CarSettingHelper.getCarDoorState(VehicleArea.REAR_RIGHT);
            case CarSettingSignal.CARSET_UNLOCK_MODE:
                return CarSettingHelper.getUnlockSettingStatus(area);
            case CarSettingSignal.CARSET_WALK_AWAY_LOCK:
                return CarSettingHelper.getAutoLockStatus();
            case CarSettingSignal.CARSET_PARKING_UNLOCK:
                return CarSettingHelper.getParkingLockStatus();
            case CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE:
                return CarSettingHelper.getWhistleLockStatus();
            case CarSettingSignal.CARSET_LOCK_CLOSE_WINDOW:
                return CarSettingHelper.getAutoCloseWindowStatus();
            case CarSettingSignal.CARSET_RMM_AUTO_FOLD:
                return CarSettingHelper.getLockRmmAutoFoldStatus();
            case CarSettingSignal.CARSET_RM_AUTO_DOWN:
                return CarSettingHelper.getBackupRmAutoDownStatus();
            case CarSettingSignal.CARSET_REAR_POS_LAMP:
                return CarSettingHelper.getPositionLightsFollowDrlStatus();
            case CarSettingSignal.CARSET_HIGH_BEAM_ASSIST:
                return CarSettingHelper.getIntelligentHighBeamAssistStatus();
            case CarSettingSignal.CARSET_INTERIOR_LIGHT_AUTO_DOME:
                return CarSettingHelper.getTopLightsAutoTurnOnStatus();
            case CarSettingSignal.CARSET_CHARGE_LIGHT_EFFECT:
                return CarSettingHelper.getChargingEffectstatus();
            default:
                return getCommonBoolean(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        switch (key) {
            case CarSettingSignal.CARSET_SAFE_ESP:
                CarSettingHelper.setSafeESP(value);
                break;
            case CarSettingSignal.CARSET_HILL_DESCENT:
                CarSettingHelper.setHillDescent(value);
                break;
            case CarSettingSignal.CARSET_BATTERY_WARM_SWITCH:
                CarSettingHelper.setBatteryWarmSwitch(value);
                break;
            case CarSettingSignal.CARSET_V2V_SWITCH:
                CarSettingHelper.setV2VSwitch(value);
                break;
            case CarSettingSignal.CARSET_INTELLIGENT_RECOMMEND:
                CarSettingHelper.setIntelligentRecommendSwitch(value);
                break;
            case CarSettingSignal.CARSET_PARKING_LIGHTS:
                CarSettingHelper.setParkingLights(value);
                break;
            case CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK:
                CarSettingHelper.setRemoteKeyAutoUnlock(value);
                break;
            case CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK:
                CarSettingHelper.setRemoteKeyAutoLock(value);
                break;
            case CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK:
                CarSettingHelper.setBTAutoUnlock(value);
                break;
            case CarSettingSignal.CARSET_BTKEY_AUTO_LOCK:
                CarSettingHelper.setBTAutoLock(value);
                break;
            case CarSettingSignal.CARSET_DOOR_LOCK:
                CarSettingHelper.setCentralLockStatus(value);
                break;
            case CarSettingSignal.CARSET_UNLOCK_MODE:
                CarSettingHelper.setUnlockSettingStatus(value);
                break;
            case CarSettingSignal.CARSET_WALK_AWAY_LOCK:
                CarSettingHelper.setAutoLockStatus(value);
                break;
            case CarSettingSignal.CARSET_PARKING_UNLOCK:
                CarSettingHelper.setParkingLockStatus(value);
                break;
            case CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE:
                CarSettingHelper.setWhistleLockStatus(value);
                break;
            case CarSettingSignal.CARSET_LOCK_CLOSE_WINDOW:
                CarSettingHelper.setAutoCloseWindowStatus(value);
                break;
            case CarSettingSignal.CARSET_RMM_AUTO_FOLD:
                CarSettingHelper.setLockRmmAutoFoldStatus(value);
                break;
            case CarSettingSignal.CARSET_RM_AUTO_DOWN:
                CarSettingHelper.setBackupRmAutoDownStatus(value);
                break;
            case CarSettingSignal.CARSET_REAR_POS_LAMP:
                CarSettingHelper.setPositionLightsFollowDrlStatus(value);
                break;
            case CarSettingSignal.CARSET_HIGH_BEAM_ASSIST:
                CarSettingHelper.setIntelligentHighBeamAssistStatus(value);
                break;
            case CarSettingSignal.CARSET_INTERIOR_LIGHT_AUTO_DOME:
                CarSettingHelper.setTopLightsAutoTurnOnStatus(value);
                break;
            case CarSettingSignal.CARSET_CHARGE_LIGHT_EFFECT:
                CarSettingHelper.setChargingEffectstatus(value);
                break;
            case CarSettingSignal.CARSET_CHILD_LOCK:
                CarSettingHelper.setChildLockStatus(area,value);
                break;
            default:
                setCommonBoolean(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        //  -1=回复“解闭锁鸣笛” 1=回复“闭锁鸣笛”   暂时只有37A文案是解锁锁鸣笛，其他车型都是闭锁鸣笛
        String carLockUnlockVoiceTtsText;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            carLockUnlockVoiceTtsText = "-1";
        } else {
            carLockUnlockVoiceTtsText = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE_TTS_TEXT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, carLockUnlockVoiceTtsText);
        // 是否有充电光效功能 37A有  56C没有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CarSettingSignal.CARSET_IS_HAS_CHARGING_EFFECTSTATUS_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 只有37有驻车灯效，56C和56D没有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CarSettingSignal.CARSET_IS_HAS_PARK_LIGHT_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 后排儿童安全锁 37A 37B有，56C没有 56D有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CarSettingSignal.CARSET_CHILD_LOCK_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 离车自动上锁&近车自动解锁是否拆分为 蓝牙钥匙和遥控钥匙 1=拆分 2=不拆分   37A 37B 56D 拆分  56C不拆分
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CarSettingSignal.CARSET_NEED_SPLIT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
    }
}
