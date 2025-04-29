package com.voyah.ai.logic.dc;

import android.text.TextUtils;
import android.util.Log2;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.CarSettingControlInterface;
import com.voice.sdk.device.carservice.dc.SentryInterface;
import com.voice.sdk.device.carservice.dc.SystemSettingInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.DoorSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CarSettingControlImpl  extends AbsDevices  implements CarSettingControlInterface {

    private static final String TAG = CarSettingControlImpl.class.getSimpleName();

    private final SystemSettingInterface mSystemSettingImpl;
    private final SentryInterface mSentryHelper = DeviceHolder.INS().getDevices().getSentry();

    public CarSettingControlImpl() {
        this.mSystemSettingImpl = DeviceHolder.INS().getDevices().getCarService().getSystemSetting();
    }

    @Override
    public String getDomain() {
        return "carSetting";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        if (map.containsKey(FINAL_TTS)) {
            String repTts = (String) getValueInContext(map, FINAL_TTS);
            if (!StringUtils.isEmpty(repTts)) {
                return repTts;
            }
        }

        int index = 0;
        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "switch_mode":
                str = str.replace("@{switch_mode}", getSwitchModeStr(map));
                break;
            case "welcome_light":
                str = str.replace("@{welcome_light}", getSwitchModeStr(map));
                break;
            case "level":
            case "wiper_sensitivity_level":
            case "sentry_mode":
                if (key.equals("level")) {
                    str = str.replace("@{level}", getLevelStr(map, 0));
                } else if (key.equals("wiper_sensitivity_level")) {
                    str = str.replace("@{wiper_sensitivity_level}", getLevelStr(map, 1));
                } else {
                    str = str.replace("@{sentry_mode}", getLevelStr(map, 0));
                }
                break;
            case "driving_mode":
                String drive_mode = getDriveModeStringFromContext(map);
                str = str.replace("@{driving_mode}", drive_mode);
                break;
            case "auto_parking_mode":
                String auto_parking_mode = getAutoParkingStringFromContext(map);
                str = str.replace("@{auto_parking_mode}", auto_parking_mode);
                break;
            case "auto_hold":
                String auto_hold = getAutoParkingStringFromContext(map);
                str = str.replace("@{auto_hold}", auto_hold);
                break;
            case "energy_recovery_level":
                String energy_recovery_level = getEnergyRecoveryLevelStringFromContext(map);
                str = str.replace("@{energy_recovery_level}", energy_recovery_level);
                break;
            case "energy_recovery":
                String energy_recovery = getEnergyRecoveryLevelStringFromContext(map);
                str = str.replace("@{energy_recovery}", energy_recovery);
                break;
            case "remaining_mileage":
                String remainMileage = getRemainingMileageStringFromContext(map);
                str = str.replace("@{remaining_mileage}", remainMileage);
                break;
            case "ip_endurance_display":
                String ip_endurance_display = getRemainingMileageStringFromContext(map);
                str = str.replace("@{ip_endurance_display}", ip_endurance_display);
                break;
            case "power_mode":
//                String powerMode = getStringForPowerMode(map);
//                str = str.replace("@{power_mode}", powerMode);

                String powerMode = map.get("power_mode").toString();
                str = str.replace("@{power_mode}", powerMode);
                break;
            case "position":
                String position = (String) getValueInContext(map, "positions");
                str = str.replace("@{position}", getPositionSideName().get(position));
                break;
            case "follow_home_mode":
                String duration = (String) getValueInContext(map, "duration");
                if (duration.contains("秒")) {
                    str = str.replace("@{follow_home_mode}", duration);
                } else {
                    str = str.replace("@{follow_home_mode}", duration + "秒");
                }
                break;
            case "tab_name":
                str = str.replace("@{tab_name}", getTabName(map));
                break;
            case "app_name":
                str = str.replace("@{app_name}", "设置应用");
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

    private String getSwitchModeStr(HashMap<String, Object> map) {
        String str = "";
        if (map.containsKey("switch_mode")) {
            str = (String) getValueInContext(map, "switch_mode");
            switch (str) {
                case "low":
                    str = "低";
                    break;
                case "lower":
                    str = "较低";
                    break;
                case "standard":
                case "mid":
                    str = "标准";
                    break;
                case "higher":
                case "high":
                    str = "较高";
                    break;
                case "comfortable":
                    str = "舒享";
                    break;
                case "free":
                    str = "自由";
                    break;
                case "joyful":
                    str = "愉悦";
                    break;
            }
        }
        return str;
    }

    private String getLevelStr(HashMap<String, Object> map, int type) {
        String str = "";
        if (map.containsKey("level")) {
            str = (String) getValueInContext(map, "level");
            if (type == 1) {
                switch (str) {
                    case "low":
                        str = "低";
                        break;
                    case "lower":
                        str = "较低";
                        break;
                    case "standard":
                    case "mid":
                        str = "标准";
                        break;
                    case "higher":
                    case "high":
                        str = "较高";
                        break;
                }
                return str;
            }
            switch (str) {
                case "low":
                    str = "低";
                    break;
                case "mid":
                    str = "中";
                    break;
                case "high":
                    str = "高";
                    break;
                case "lower":
                    str = "较低";
                    break;
                case "standard":
                    str = "标准";
                    break;
                case "higher":
                    str = "较高";
                    break;
            }
        }
        return str;
    }

    private String getTabName(HashMap<String, Object> map) {
        String str = "";
        String pageName = (String) getValueInContext(map, "tab_name");
        if (!TextUtils.isEmpty(pageName)) {
            switch (pageName) {
                case "intelligent_monitor":
                    str = "智能监测";
                    break;
            }
        }
        return str;
    }

    /**
     * 获取信息隐藏开关状态
     *
     * @return Int
     * 隐私模式已开启 PRIVACY_MODE_ON = 1
     * 隐私模式已关闭 PRIVACY_MODE_OFF = 0
     */
    public int getPrivacyClauseSwitchState(HashMap<String, Object> map) {
        int status = operator.getIntProp(CommonSignal.COMMON_INFO_HIDING);
        LogUtils.d(TAG, "getPrivacyClauseSwitchState: " + status);
        return status;
    }

    public void setPrivacyClauseSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setPrivacyClauseSwitchState");
        String switchType = "";
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        operator.setIntProp(CommonSignal.COMMON_INFO_HIDING, switchType.equals("open") ? 1 : 0);
    }

    /**
     * 获取隐私保护开关状态
     *
     * @return Int
     * 已开启 1
     * 已关闭 0
     */
    public int getPrivacySecuritySwitchState(HashMap<String, Object> map) {
        int status = operator.getIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION);
        LogUtils.d(TAG, "getPrivacySecuritySwitchState: " + status);
        return status;
    }

    public void setPrivacySecuritySwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setPrivacySecuritySwitchState");
        String switchType = "";
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        operator.setIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION,
                "open".equals(switchType) ? ICommon.Switch.ON: ICommon.Switch.OFF);
    }

    /**
     * 获取安全检测灵敏度挡位，指的是哨兵模式
     * 高中低三档
     */
    public int getSafeSensitivityLevel(HashMap<String, Object> map) {
        int status = operator.getIntProp(CarSettingSignal.CARSET_SAFE_SENSITIVE);
        map.put("safe_sensitivity", "SAFE_SENSITIVITY");
        LogUtils.d(TAG, "getSafeSensitivityLevel: " + status);
        return status;
    }

    public int getOrderLevel(HashMap<String, Object> map) {
        int level = 0;
        String orderLevelStr = (String) getValueInContext(map, "level");
        if (!TextUtils.isEmpty(orderLevelStr)) {
            switch (orderLevelStr) {
                case "low":
                case "lower":
                    level = 1;
                    break;
                case "mid":
                case "standard":
                    level = 2;
                    break;
                case "high":
                case "higher":
                    level = 3;
                    break;
            }
        }
        return level;
    }

    public void setSafeSensitivityLevel(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setSafeSensitivityLevel");
        operator.setIntProp(CarSettingSignal.CARSET_SAFE_SENSITIVE, getOrderLevel(map));
    }

    public boolean isWillClosePDrv(HashMap<String, Object> map) {
        return isH37ACar(map) || isH37BCar(map);
    }

    /**
     * 获取是否处于Disable模式
     *
     * @param map 指令的数据
     * @return true处于
     */
    public boolean isDisableMode(HashMap<String, Object> map) {
//        int state = carPropHelper.getIntProp(H56C.PDCM_IVI_PDCM_EXHIBITIONSTS);
        int state = operator.getIntProp(CarSettingSignal.CARSET_DISABLE_MODE);
        LogUtils.d(TAG, "isDisableMode: " + state);
        if (isH56CCar(map) || isH56DCar(map)) {
            return state == 3 || state == 2;
        }
        return state == 2 || state == 4;
    }

    /**
     * 获取当前电量
     *
     * @param map 指令的数据
     * @return 当前电量
     */
    public float getElcPower(HashMap<String, Object> map) {
//        float curPower = carPropHelper.getFloatProp(ElecPower.ID_HV_PERCENT);
        float curPower = operator.getFloatProp(CommonSignal.COMMON_REMAIN_POWER);
        LogUtils.d(TAG, "getElcPower: " + curPower);
        return curPower;
    }

    /**
     * 获取当前挡位
     *
     * @param map 指令的数据
     * @return 车辆挡位数据
     */
    public int getDrvState(HashMap<String, Object> map) {
        int curDrvState = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
        LogUtils.d(TAG, "getDrvState: " + curDrvState);
        return curDrvState;
    }

    /**
     * 获取哨兵的开关状态
     *
     * @param map 指令的数据
     * @return true打开
     */
    public boolean getSafeSensitivitySwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getSafeSensitivitySwitchState");
        return mSentryHelper.isSentryOpen();
    }

    /**
     * 打开二次确认弹窗
     *
     * @param map 指令的数据
     */
    public void setSafeSensitivitySwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setSafeSensitivitySwitchState");
        String switch_type = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switch_type)) {
            if (switch_type.equals("open")) {
                mSentryHelper.setSentrySwitchState(1);
            } else {
                mSentryHelper.setSentrySwitchState(2);
            }
        }
    }

    public boolean getWipeWorkState(HashMap<String, Object> map) {
        boolean s = operator.getIntProp(CarSettingSignal.CARSET_WIPE_ACTION_STATE) == ICommon.Switch.ON;
        boolean auto = operator.getBooleanProp(CarSettingSignal.CARSET_WIPE_AUTO_SWITCH);
        return (!s && !auto);
    }

    /**
     * 前雨刮维修模式开关
     * 1是开启
     */
    public int getWipeRepairSwitchState(HashMap<String, Object> map) {
        int status = operator.getIntProp(CarSettingSignal.CARSET_WIPE_REPAIR_STATE);
        LogUtils.d(TAG, "getWipeRepairSwitchState: " + status);
        return status;
    }

    public void setWipeRepairSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setWipeRepairSwitchState");
        String switchType = "";
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        operator.setIntProp(CarSettingSignal.CARSET_WIPE_REPAIR_STATE,
            switchType.equals("open") ? ((isH56CCar(map) || isH56DCar(map)) ? 2 : 1) : ((isH56CCar(map) || isH56DCar(map)) ? 1 : 0));
    }

    public int getWipeAutoState(HashMap<String, Object> map) {
        int status = operator.getIntProp(CarSettingSignal.CARSET_AUTO_WIPE_SENSITIVE);
        LogUtils.d(TAG, "getWipeAutoState: " + status);
        return status;
    }

    public int getOrderWipeSpeedNum(HashMap<String, Object> map) {
        String level = "";
        if (map.containsKey("level")) {
            level = getValueInContext(map, "level") + "";
        }
        int orderSpeedNum = 0;
        switch (level) {
            case "low":
                orderSpeedNum = 0;
                break;
            case "lower":
                orderSpeedNum = 1;
                break;
            case "standard":
            case "mid":
                orderSpeedNum = 2;
                break;
            case "high":
            case "higher":
                orderSpeedNum = 3;
                break;
            default:
        }
        if (isH37ACar(map) || isH37BCar(map)) {
            orderSpeedNum = orderSpeedNum + 1;
        }
        return orderSpeedNum;
    }

    public void setWipeAutoState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setWipeAutoState");
        // 56C设置挡位需要+1
        int ordNumber = (isH56CCar(map) || isH56DCar(map)) ? getOrderWipeSpeedNum(map) + 1 : getOrderWipeSpeedNum(map);
        operator.setIntProp(CarSettingSignal.CARSET_AUTO_WIPE_SENSITIVE, ordNumber);
    }

    public boolean isSupportRefrigerator(HashMap<String, Object> map) {
        int state = operator.getIntProp(CommonSignal.COMMON_SUPPORT_REFRIGERATOR, 0);
        return state == 1;
    }

    public boolean isSupportAirRearLock(HashMap<String, Object> map) {
        return isH37BCar(map);
    }

    public int getAirRearLockState(HashMap<String, Object> map) {
        return operator.getIntProp(CarSettingSignal.CARSET_AIR_REAR_LOCK);
    }

    public void setAirRearLockState(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            operator.setIntProp(CarSettingSignal.CARSET_AIR_REAR_LOCK, switchType.equals("open") ? 1 :2);
        }
    }

    /**
     * ----------------------- 能量中心 start--------------------------------------------------
     */

    /**
     * 获取快充功能配置
     *
     * @param map
     * @return 2，400V 3,800V
     */
    public int getFastChargeConfig(HashMap<String, Object> map) {
//        int config =  MegaSystemProperties.getInt(MegaProperties.CONFIG_FAST_CHARGE, 2);
        int config = operator.getIntProp(CommonSignal.COMMON_SUPPORT_FAST, 0);
        LogUtils.d(TAG, "getFastChargeConfig: " + config);
        return config;
    }

    /**
     * 电池维温
     *
     * @param map
     * @return true/false
     */
    public boolean getBatterySwitchState(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_BATTERY_WARM_SWITCH);
    }

    public void setBatterySwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setBatterySwitchState");
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            operator.setBooleanProp(CarSettingSignal.CARSET_BATTERY_WARM_SWITCH, "open".equals(switchType));
        }
    }

    public boolean getVehicleDischargingSwitchState(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_V2V_SWITCH);
    }

    public void setVehicleDischargingSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setVehicleDischargingSwitchState");
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            operator.setBooleanProp(CarSettingSignal.CARSET_V2V_SWITCH, "open".equals(switchType));
        }
    }

    /** ----------------------- 能量中心 end--------------------------------------------------*/


    /** ======================驾驶模式 START===================== */

    /**
     * 判断当前是否在指定驾驶模式
     *
     * @param map
     * @return
     */
    public int getCurDrivingMode(HashMap<String, Object> map) {
        return operator.getIntProp(CarSettingSignal.CARSET_DRIVING_MODE);
    }

    //判断用户是否是需要打开郊游模式
    public boolean isOutingMode(HashMap<String, Object> map){
        String expectedMode = "";
        if (map.containsKey("switch_mode")) {
            expectedMode = getValueInContext(map, "switch_mode") + "";
        }
        if(expectedMode.equals("picnic")){
            return true;
        }else {
            return false;
        }
    }

    //判断当前车速是否大于60
    public boolean isGreater60(HashMap<String, Object> map){
        //先判断挡位
        int curDriveGear = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
        if (curDriveGear == 1 || curDriveGear == 3) {//0P  1R 2N 3D
            //获取当前车速
            float curDriveSpeed = operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
            if (curDriveSpeed > 60f) {
                return true;
            }
            return false;
        }
        return false;
    }
    /**
     * 判断当前是否自定义驾驶模式
     *
     * @param map
     * @return
     */
    public boolean isCustomDrivingMode(HashMap<String, Object> map) {
        return ICarSetting.DrivingMode.CUSTOM == getCurDrivingMode(map);
    }

    /**
     * 判断当前是否在驾驶偏好设置页
     *
     * @return
     */
    public boolean getIsInPreferencePage(HashMap<String, Object> map) {
        // todo
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            if (switchType.equals("close")) {
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround("com.voyah.cockpit.vehiclesettings", DeviceScreenType.CENTRAL_SCREEN);
            }
        }
        return mSettingHelper.isCurrentState(SettingConstants.DRIVE_PREFERENCE_PAGE);
    }

    /**
     * 从参数中提取预期的驾驶模式code
     *
     * @param map
     * @return
     */
    public int getExpectedDrivingMode(HashMap<String, Object> map) {
        String expectedMode = "";
        if (map.containsKey("switch_mode")) {
            expectedMode = getValueInContext(map, "switch_mode") + "";
        }

        switch (expectedMode) {
            case "comfortable":
                return ICarSetting.DrivingMode.COMFORTABLE;
            case "custom":
                return ICarSetting.DrivingMode.CUSTOM;
            case "economy":
                return ICarSetting.DrivingMode.ECONOMY;
            case "high_energy":
            case "sport":
                return ICarSetting.DrivingMode.SPORT;
            case "picnic":
                return ICarSetting.DrivingMode.PICNIC;
            case "snowfield":
                return ICarSetting.DrivingMode.SNOW;
            case "super_power_saving":
                return ICarSetting.DrivingMode.SUPER_POWER;
            default:
                return ICarSetting.DrivingMode.INVALID;
        }
    }

    /**
     * 设置驾驶模式
     *
     * @param map
     */
    public void setCurDrivingMode(HashMap<String, Object> map) {
        operator.setIntProp(CarSettingSignal.CARSET_DRIVING_MODE, getExpectedDrivingMode(map));
    }

    public boolean isSupportSuperPowerSaving(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSER_SUPER_POWER_SAVING_CONFIG);
    }

    public void showEcoPlusMode(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.SUPER_POWER);
    }

    private String getDriveModeStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String chassisStyle = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(chassisStyle)) {
            switch (chassisStyle) {
                case "comfortable":
                    res = "舒适";
                    break;
                case "custom":
                    res = "自定义";
                    break;
                case "economy":
                    res = "经济";
                    break;
                case "high_energy":
                case "sport": //兼容算法运动模式的说法
                    res = "高能";
                    break;
                case "picnic":
                    res = "郊游";
                    break;
                case "snowfield":
                    res = "雪地";
                    break;
                case "super_power_saving":
                    res = "超级省电";
                    break;
                default:
            }
        }
        return res;
    }
    /** ======================驾驶模式 END===================== */

    /**
     * ======================驾驶模式-动力模式 START=====================
     */

    public int getCurPowerMode(HashMap<String, Object> map) {
        int value =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE);
        String switchMode = "";
        if (map.containsKey("switch_mode")) {
            switchMode = getValueInContext(map, "switch_mode") + "";
        }
        if ("hybrid".equals(switchMode)) {//混动
            map.put("power_mode","油电混合");
        } else if ("ev".equals(switchMode)) {//纯电
            map.put("power_mode","纯电优先");
        } else if ("fuel".equals(switchMode) || "power_protection".equals(switchMode)) {//燃油 或者保电模式
            map.put("power_mode","保电");
        }else if ("economy".equals(switchMode)){//经济模式
            value =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE_DRIVER_MODE);
            map.put("power_mode","经济");
        }else if ("sport".equals(switchMode)){//运动模式
            value =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE_DRIVER_MODE);
            map.put("power_mode","运动");
        }else if ("standard".equals(switchMode)){//标准模式
            value =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE_DRIVER_MODE);
            map.put("power_mode","标准");
        }
        return value;
    }

    /**
     * "economy：经济
     * sport：运动
     * standard：标准
     *
     * ev：电动
     * fuel：燃油
     * hybrid：混动
     * 保电：power_protection"
     *
     */

    public int getExpectedPowerMode(HashMap<String, Object> map) {
        String switchMode = "";
        int valueExpected = ICarSetting.PowerMode.STANDARD;
        if (map.containsKey("switch_mode")) {
            switchMode = getValueInContext(map, "switch_mode") + "";
        }
        if ("hybrid".equals(switchMode)) {//混动
            valueExpected = 1;
            map.put("power_mode","油电混合");
        } else if ("ev".equals(switchMode)) {//纯电
            valueExpected = 2;
            map.put("power_mode","纯电优先");
        } else if ("fuel".equals(switchMode) || "power_protection".equals(switchMode)) {//燃油
            valueExpected = 3;
            map.put("power_mode","保电");
        }else if ("economy".equals(switchMode)){//经济模式
            valueExpected = ICarSetting.PowerMode.ECO;
        }else if ("sport".equals(switchMode)){//运动模式
            valueExpected = ICarSetting.PowerMode.SPORT;
        }else if ("standard".equals(switchMode)){//标准模式
            valueExpected = ICarSetting.PowerMode.STANDARD;
        }
        return valueExpected;
    }

    public void setCurPowerMode(HashMap<String, Object> map) {
//        operator.setIntProp(CarSettingSignal.CARSET_POWER_MODE, getExpectedPowerMode(map));
        String switchMode = "";
        if (map.containsKey("switch_mode")) {
            switchMode = getValueInContext(map, "switch_mode") + "";
        }
        switch (switchMode){
            case "hybrid"://混动
            case "ev"://纯电
            case "fuel"://燃油
            case "power_protection"://保电
                operator.setIntProp(CarSettingSignal.CARSET_POWER_MODE, getExpectedPowerMode(map));
                break;
            case "economy"://经济
            case "sport"://运动
            case "standard"://标准
                operator.setIntProp(CarSettingSignal.CARSET_POWER_MODE_DRIVER_MODE, getExpectedPowerMode(map));
                break;
            default:
                LogUtils.d(TAG, "动力模式：switchMode = " + switchMode + "、设置：" + getExpectedPowerMode(map));
                break;
        }

    }

    private String getStringForPowerMode(HashMap<String, Object> map) {
        String res = "";
        String autoParking = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(autoParking)) {
            switch (autoParking) {
                case "economy":
                    res = "经济";
                    break;
                case "sport":
                    res = "运动";
                    break;
                case "standard":
                    res = "标准";
                    break;
                default:
            }
        }
        return res;
    }

    /** ======================驾驶模式-动力模式 START===================== */

    public void handleEleParking(HashMap<String, Object> map) {
        if (isRestrictGearsR(map)) {
            map.put(FINAL_TTS, TTSAnsConstant.PARK_NOT_SUPPORT);
            return;
        }
        openELeParkingTab(map);
        map.put(FINAL_TTS, TtsReplyUtils.getTtsBean(5023700).getTts());
    }

    public void openELeParkingTab(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.NEW_ACTION_ELE_PARKING);
    }

    /** ====================== 电子驻车 END  ===================== */

    /**
     * ====================== 车身稳定系统 START  =====================
     */

    public boolean getSafeESP(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_SAFE_ESP);
    }

    /**
     * ID_SAFE_ESP_ENABLE 的信号值是反的
     *
     * @param map
     */
    public void setSafeESP(HashMap<String, Object> map) {
        String switchType = "";
        boolean valueToSet = false;
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        if ("open".equals(switchType)) {
            valueToSet = true;
        }
        LogUtils.d(TAG, "车身稳定：switchType = " + switchType + "、设置：" + valueToSet);
        operator.setBooleanProp(CarSettingSignal.CARSET_SAFE_ESP, valueToSet);
    }

    /** ====================== 车身稳定系统 END  ===================== */

    /**
     * ====================== 舒适停车 START  =====================
     */
    public boolean getComfortParking(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_COMFORT_PARKING);
    }

    public void setComfortParking(HashMap<String, Object> map) {
        String switchType = "";
        boolean valueToSet = false;
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        if ("open".equals(switchType)) {
            valueToSet = true;
        }
        operator.setBooleanProp(CarSettingSignal.CARSET_COMFORT_PARKING, valueToSet);
    }
    /** ====================== 舒适停车 END  ===================== */

    /**
     * ====================== 陡坡缓降 START  =====================
     */
    public boolean getHillDescent(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_HILL_DESCENT);
    }

    public void setHillDescent(HashMap<String, Object> map) {
        String switchType = "";
        boolean valueToSet = false;
        if (map.containsKey("switch_type")) {
            switchType = getValueInContext(map, "switch_type") + "";
        }
        if ("open".equals(switchType)) {
            valueToSet = true;
        }
        LogUtils.d(TAG, "陡坡缓降：switchType = " + switchType + "、设置：" + valueToSet);
        operator.setBooleanProp(CarSettingSignal.CARSET_HILL_DESCENT, valueToSet);
    }

    //陡坡缓降速度阈值
    private static final float THRESHOLD_SPEED = 35.0f;

    public boolean isOverSpeedForHillDesc(HashMap<String, Object> map) {
        float curDriveSpeed = operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
        return curDriveSpeed >= THRESHOLD_SPEED;
    }

    /** ====================== 陡坡缓降 END  ===================== */

    /** ====================== 自动驻车 START  ===================== */


    /**
     * 自动驻车
     * on 对应 自动激活：auto_activation
     * off 对应 深踩激活：deep_step_activation
     *
     * @return
     */
    public int getCurAutoParking(HashMap<String, Object> map) {
        return operator.getIntProp(CarSettingSignal.CARSET_AUTO_PARKING);
    }

    public int getWrite(HashMap<String, Object> map) {
        String switchMode = "";
        int valueExpected = ICarSetting.AvhMode.AUTO;
        if (map.containsKey("switch_mode")) {
            switchMode = getValueInContext(map, "switch_mode") + "";
        }
        if ("deep_step_activation".equals(switchMode)) {
            valueExpected = ICarSetting.AvhMode.DEPP_STEP;
        }
        return valueExpected;
    }

    public int getExpectedAutoParking(HashMap<String, Object> map) {
        String switchMode = "";
        int valueExpected = ICarSetting.AvhMode.AUTO;
        if (map.containsKey("switch_mode")) {
            switchMode = getValueInContext(map, "switch_mode") + "";
        }
        if ("deep_step_activation".equals(switchMode)) {
            valueExpected = ICarSetting.AvhMode.DEPP_STEP;
        }
        LogUtils.d(TAG, "自动驻车：switchType = " + switchMode + "、设置：" + valueExpected);
        return valueExpected;
    }

    public void setCurAutoParking(HashMap<String, Object> map) {
        operator.setIntProp(CarSettingSignal.CARSET_AUTO_PARKING, getWrite(map));
    }

    private String getAutoParkingStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String autoParking = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(autoParking)) {
            switch (autoParking) {
                case "auto_activation":
                    res = "自动激活";
                    break;
                case "deep_step_activation":
                    res = "深踩激活";
                    break;
                default:
            }
        }
        return res;
    }

    /** ====================== 自动驻车 END  ===================== */

    /** ====================== 能量回收 START  ===================== */

    /**
     * 能量回收等级 EnergyRecoveryLevel
     */

    public void handleEnergyRecovery(HashMap<String, Object> map) {
        String level = getValueInContext(map, "level") + "";
        //验参
        if (!isH56DCar(map) && "auto".equalsIgnoreCase(level)) {
            map.put(FINAL_TTS, TtsReplyUtils.getTtsBean(1100028).getSelectTTs());
            return;
        }
        //主驾
        if (!isFromFirstRowLeft(map)) {
            map.put(FINAL_TTS, TtsReplyUtils.getTtsBean(5014600).getSelectTTs());
            return;
        }
        //雪地
        if (getCurDrivingMode(map) == ICarSetting.DrivingMode.SNOW) {
            map.put(FINAL_TTS, TtsReplyUtils.getTtsBean(5023501).getSelectTTs());
            return;
        }
        if (getCurEnergyRecoveryLevel(map) == getExpectedEnergyRecoveryLevel(map)) {
            map.put(FINAL_TTS, TtsBeanUtils.getTtsBean(5023502, getEnergyRecoveryLevelStringFromContext(map)).getSelectTTs());
        } else {
            setCurEnergyRecoveryLevel(map);
            map.put(FINAL_TTS, TtsBeanUtils.getTtsBean(5023503, getEnergyRecoveryLevelStringFromContext(map)).getSelectTTs());
        }
    }
    public int getCurEnergyRecoveryLevel(HashMap<String, Object> map) {
        return operator.getIntProp(CarSettingSignal.CARSET_ENERGY_RECOVERY);
    }

    public int getExpectedEnergyRecoveryLevel(HashMap<String, Object> map) {
        String level = "";
        int valueExpected = ICommon.Level.LOW;
        if (map.containsKey("level")) {
            level = getValueInContext(map, "level") + "";
        }
        if ("low".equalsIgnoreCase(level)) {
            valueExpected = ICommon.Level.LOW;
        } else if ("mid".equalsIgnoreCase(level)) {
            valueExpected = ICommon.Level.MID;
        } else if ("high".equalsIgnoreCase(level)) {
            valueExpected = ICommon.Level.HIGH;
        } else if ("auto".equalsIgnoreCase(level)) {
            valueExpected = ICommon.Level.AUTO;
        }
        LogUtils.d(TAG, "expected energyRecovery" + valueExpected);
        return valueExpected;
    }

    public void setCurEnergyRecoveryLevel(HashMap<String, Object> map) {
        LogUtils.d(TAG, "设置能量回收");
        operator.setIntProp(CarSettingSignal.CARSET_ENERGY_RECOVERY, getExpectedEnergyRecoveryLevel(map));
    }

    private String getEnergyRecoveryLevelStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String energyRecoveryLevel = (String) getValueInContext(map, "level");
        if (!TextUtils.isEmpty(energyRecoveryLevel)) {
            switch (energyRecoveryLevel) {
                case "low":
                    res = "低";
                    break;
                case "mid":
                    res = "中";
                    break;
                case "high":
                    res = "高";
                    break;
                case "auto":
                    res = "自适应";
                    break;
                default:
            }
        }
        return res;
    }

    /** ====================== 能量回收 END  ===================== */

    /**
     * ====================== 续航里程切换 && 仪表电量显示 START  =====================
     */

    public void handleRemainingMileage(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        String switchMode = (String) getValueInContext(map, "switch_mode");
        if ("h37b".equalsIgnoreCase(carType) && "percentage".equalsIgnoreCase(switchMode)) {
            map.put(FINAL_TTS, TtsReplyUtils.getTtsBean(1100006).getSelectTTs());
            return;
        }
        if (!isFromFirstRowLeft(map)) {
            map.put(FINAL_TTS, TtsBeanUtils.getTtsBean(5014600).getSelectTTs());
            return;
        }
        if (getCurRemainingMileage(map) == getExpectedRemainingMileage(map)) {
            map.put(FINAL_TTS, TtsBeanUtils.getTtsBean(5028201, getRemainingMileageStringFromContext(map)).getSelectTTs());
        } else {
            setRemainingMileage(map);
            map.put(FINAL_TTS, TtsBeanUtils.getTtsBean(5028202, getRemainingMileageStringFromContext(map)).getSelectTTs());
        }
    }

    public int getCurRemainingMileage(HashMap<String, Object> map) {
        return operator.getIntProp(CarSettingSignal.CARSET_REMAIN_MILEAGE);
    }

    public int getExpectedRemainingMileage(HashMap<String, Object> map) {
        int valueExpected = ICarSetting.RemainMileage.STANDARD;
        String mode = getValueInContext(map, "switch_mode") + "";
        if ("reality".equalsIgnoreCase(mode)) {
            valueExpected = ICarSetting.RemainMileage.REAL;
        } else if ("cltc".equalsIgnoreCase(mode)) {
            valueExpected = ICarSetting.RemainMileage.STANDARD;
        } else if ("percentage".equalsIgnoreCase(mode)) {
            valueExpected = ICarSetting.RemainMileage.PERCENT;
        }
        return valueExpected;
    }

    public void setRemainingMileage(HashMap<String, Object> map) {
        operator.setIntProp(CarSettingSignal.CARSET_REMAIN_MILEAGE, getExpectedRemainingMileage(map));
    }

    private String getRemainingMileageStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String energyRecoveryLevel = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(energyRecoveryLevel)) {
            switch (energyRecoveryLevel) {
                case "mileage":
                    res = "里程";
                    break;
                case "percentage":
                    res = "百分比";
                    break;
                case "reality":
                    res = "综合工况";
                    break;
                case "cltc":
                    res = "CLTC工况";
                    break;
                default:
            }
        }
        return res;
    }
    /** ====================== 续航里程切换 && 仪表电量显示 END  ===================== */

    /**
     * ----------------------- 车锁 start--------------------------------------------------
     */

    /**
     * 获取中控锁状态
     */
    public boolean getCentralLockStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_DOOR_LOCK);
    }

    /**
     * 设置中控锁状态
     */
    public void setCentralLockStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_DOOR_LOCK, "open".equals(switch_type));
    }

    public boolean getDoorState(HashMap<String, Object> map) {
        if (operator.getBooleanProp(CarSettingSignal.CARSET_DOOR_FRONTLEFT)) {
            return true;
        }
        if (operator.getBooleanProp(CarSettingSignal.CARSET_DOOR_FRONTRIGHT)) {
            return true;
        }
        if (operator.getBooleanProp(CarSettingSignal.CARSET_DOOR_REARLEFT)) {
            return true;
        }
        if (operator.getBooleanProp(CarSettingSignal.CARSET_DOOR_REARRIGHT)) {
            return true;
        }
        return false;
    }

    public boolean isChildrenLockValidPosition(HashMap<String, Object> map) {
        if (DeviceHolder.INS().getDevices().getCarServiceProp().isH37B()) {
            String position = getOneMapValue("positions", map);
            if (position.equals("left_side") || position.equals("right_side") || position.equals("rear_side")) {
                return true;
            }
        }
        return false;
    }

    public boolean isHasPosition(HashMap<String, Object> map) {
        return map.containsKey("positions");
    }

    public boolean isChildrenLockLeftSide(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        return position.equals("left_side");
    }

    public boolean isChildrenLockRightSide(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        return position.equals("right_side");
    }

    public void setChildrenLockLeftSide(HashMap<String, Object> map) {
        // 设置左侧儿童锁
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_CHILD_LOCK,1, switch_type.equals("open"));
    }

    public void setChildrenLockRightSide(HashMap<String, Object> map) {
        // 设置右侧儿童锁
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_CHILD_LOCK,2, switch_type.equals("open"));
    }

    public void setChildrenLockStatus(HashMap<String, Object> map) {
        // 设置后排儿童锁
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_CHILD_LOCK,3, switch_type.equals("open"));
    }

    /**
     * 获取解锁设置状态
     */
    public boolean getUnlockSettingStatus(HashMap<String, Object> map) {
        String position = (String) getValueInContext(map, "positions");
        //1=全车解锁 2=主驾解锁
        return operator.getBooleanProp(CarSettingSignal.CARSET_UNLOCK_MODE, position.equals("total_car") ? 1 : 2);
    }

    /**
     * 获取解锁设置状态
     */
    public void setUnlockSettingStatus(HashMap<String, Object> map) {
        String position = (String) getValueInContext(map, "positions");
        //true=全车解锁 false=主驾解锁
        operator.setBooleanProp(CarSettingSignal.CARSET_UNLOCK_MODE, position.equals("total_car"));
    }

    /**
     * 获取自动解闭锁的状态
     */
    public boolean getAutoLockStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_WALK_AWAY_LOCK);
    }

    /**
     * 设置自动解闭锁的状态
     */
    public void setAutoLockStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_WALK_AWAY_LOCK, switch_type.equals("open"));
    }

    /**
     * 获取P挡解锁的状态
     */
    public boolean getParkingLockStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_PARKING_UNLOCK);
    }

    /**
     * 设置P挡解锁的状态
     */
    public void setParkingLockStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_PARKING_UNLOCK, switch_type.equals("open"));
    }

    /**
     * 获取解闭锁喇叭提示音的开关状态
     */
    public boolean getWhistleLockStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE);
    }

    /**
     * 设置解闭锁喇叭提示音的开关状态
     */
    public void setWhistleLockStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_LOCK_UNLOCK_VOICE, switch_type.equals("open"));
    }

    /**
     * 获取锁车自动升窗开关状态
     */
    public boolean getAutoCloseWindowStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_LOCK_CLOSE_WINDOW);
    }

    /**
     * 设置锁车自动升窗开关状态
     */
    public void setAutoCloseWindowStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_LOCK_CLOSE_WINDOW, switch_type.equals("open"));
    }

    /**
     * 获取锁车自动折叠后视镜状态（0 = 关闭  1 = 打开）
     */
    public boolean getLockRmmAutoFoldStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_RMM_AUTO_FOLD);
    }

    /**
     * 设置锁车自动折叠后视镜状态 （1 = 关闭 2 = 打开）
     */
    public void setLockRmmAutoFoldStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_RMM_AUTO_FOLD, switch_type.equals("open"));
    }

    /**
     * 获取后视镜倒车自动下翻
     */
    public boolean getBackupRmAutoDownStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_RM_AUTO_DOWN);
    }

    /**
     * 设置后视镜倒车自动下翻
     */
    public void setBackupRmAutoDownStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_RM_AUTO_DOWN, switch_type.equals("open"));
    }

    /**
     * position to name
     */
    public Map<String, String> getPositionSideName() {
        return new HashMap<String, String>(){
            {
                put("rear_side", "");
                put("rear_side_left", "左侧");
                put("total_car", "四门");
                put("first_row_left", "主驾");
            }
        };
    }

    /** ----------------------- 车锁 end---------------------------------------------------*/

    /** ----------------------- 灯光开关 start----------------------------------------------*/

    /**
     * 获取后位置灯随日行灯开启的开关状态
     */
    public boolean getPositionLightsFollowDrlStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_REAR_POS_LAMP);
    }

    /**
     * 设置后位置灯随日行灯开启的开关状态
     */
    public void setPositionLightsFollowDrlStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_REAR_POS_LAMP, switch_type.equals("open"));
    }

    /**
     * 获取智能远光辅助的开关状态
     */
    public boolean getIntelligentHighBeamAssistStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_HIGH_BEAM_ASSIST);
    }

    /**
     * 设置智能远光辅助的开关状态
     */
    public void setIntelligentHighBeamAssistStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_HIGH_BEAM_ASSIST, switch_type.equals("open"));
    }

    /**
     * 获取顶灯自动点亮的开关状态
     */
    public boolean getTopLightsAutoTurnOnStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_INTERIOR_LIGHT_AUTO_DOME);
    }

    /**
     * 设置顶灯自动点亮的开关状态
     */
    public void setTopLightsAutoTurnOnStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_INTERIOR_LIGHT_AUTO_DOME, switch_type.equals("open"));
    }

    /**
     * 是否是打开页面
     */
    public boolean isHasPageName(HashMap<String, Object> map) {
        return map.containsKey("page_name") ? true : false;
    }

    /**
     * 充电光效UI page 页
     */
//    public int getChargingEffectsUIPageStatus(HashMap<String, Object> map) throws RemoteException {
//        int code = mIPageShowManagerImpl.showChargingLight();
//        LogUtils.i(TAG, "getChargingEffectsUIPageStatus : code-" + code);
//        return code;
//    }

    /**
     * 获取充电光效的开关状态
     */
    public boolean getChargingEffectstatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_CHARGE_LIGHT_EFFECT);
    }

    /**
     * 获取充电光效的开关状态
     */
    public void setChargingEffectstatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_CHARGE_LIGHT_EFFECT, switch_type.equals("open"));
    }

    /** ----------------------- 灯光开关 end-----------------------------------------------*/

    /**
     * ----------------------- 伴我回家 start----------------------------------------------
     */

    public boolean isHasDuration(HashMap<String, Object> map) {
        return map.containsKey("duration");
    }

    /**
     * 获取伴我回家的开关状态
     */
    public int getFollowMeHomeStatus(HashMap<String, Object> map) {
        int state = operator.getIntProp(CarSettingSignal.CARSET_FOLLOW_HOME);
        LogUtils.i(TAG, "getFollowMeHomeStatus : state-" + state);
        return state;
    }

    /**
     * 设置伴我回家的开关状态
     */
    public void setFollowMeHomeStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setFollowMeHomeStatus : switch_type-" + switch_type);
        operator.setIntProp(CarSettingSignal.CARSET_FOLLOW_HOME, switch_type.equals("open")
                ? ICarSetting.FollowHomeMode.MODE_15S : ICarSetting.FollowHomeMode.OFF);
    }

    /**
     * 获取支持模式的状态
     */
    public boolean getSupportStatus(HashMap<String, Object> map) {
        String duration = (String) getValueInContext(map, "duration");
        if (duration.equals("15") || duration.equals("30") || duration.equals("60")
                || duration.equals("15秒") || duration.equals("30秒") || duration.equals("60秒")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前要设置的模式对应的信号值
     */
    public int getCurSetModeCode(HashMap<String, Object> map) {
        String duration = (String) getValueInContext(map, "duration");
        int code = getFollowMeHomeCode().get(duration);
        LogUtils.i(TAG, "getCurSetModeCode : duration-" + duration + "-code :" + code);
        return code;
    }

    /**
     * 设置伴我回家的模式（15s，30s，60s）
     */
    public void setFollowMeHomeMode(HashMap<String, Object> map) {
        operator.setIntProp(CarSettingSignal.CARSET_FOLLOW_HOME, getCurSetModeCode(map));
    }

    /**
     * 伴我回家Mode -> code
     */
    public Map<String, Integer> getFollowMeHomeCode() {
        return new HashMap<String, Integer>() {
            {
                put("15秒", ICarSetting.FollowHomeMode.MODE_15S);
                put("30秒", ICarSetting.FollowHomeMode.MODE_30S);
                put("60秒", ICarSetting.FollowHomeMode.MODE_60S);
                put("15", ICarSetting.FollowHomeMode.MODE_15S);
                put("30", ICarSetting.FollowHomeMode.MODE_30S);
                put("60", ICarSetting.FollowHomeMode.MODE_60S);
            }
        };
    }

    /** ----------------------- 伴我回家 end-----------------------------------------------*/

    /**
     * ----------------------- 迎宾灯光 start---------------------------------------------
     */

    /**
     * 获取迎宾灯支持模式的状态
     */
    public boolean getSupportMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        boolean isSupportMode = getWelcomeLightName().keySet().contains(switch_mode);
        return isSupportMode;
    }

    /**
     * 获取当前想设置的模式对应的code
     */
    public int getCurSetCode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int code = getWelcomeLightCode().get(switch_mode);
        return code;
    }

    /**
     * 获取迎宾灯的状态
     */
    public int getWelcomeLightStatus(HashMap<String, Object> map) {
        int state = operator.getIntProp(CarSettingSignal.CARSET_WELCOME_LIGHTS);
        LogUtils.i(TAG, "getWelcomeLightStatus : state-" + state);
        return state;
    }

    /**
     * 设置迎宾灯的状态
     */
    public void setWelcomeLightStatus(HashMap<String, Object> map) {
        if (map.containsKey("switch_mode")) {
            operator.setIntProp(CarSettingSignal.CARSET_WELCOME_LIGHTS, getCurSetCode(map));
        } else {
            String switch_type = (String) getValueInContext(map, "switch_type");
            operator.setIntProp(CarSettingSignal.CARSET_WELCOME_LIGHTS,
                    switch_type.equals("open") ? ICarSetting.WelcomeLights.MODE1 : ICarSetting.WelcomeLights.OFF);
            LogUtils.i(TAG, "setWelcomeLightStatus : switch_type-" + switch_type);
        }
    }

    /**
     * 迎宾灯Mode -> code （joyful：愉悦    comfortable：舒适    free：自由）
     */
    public Map<String, Integer> getWelcomeLightCode() {
        return new HashMap<String, Integer>() {
            {
                put("joyful", ICarSetting.WelcomeLights.MODE1);
                put("comfortable", ICarSetting.WelcomeLights.MODE2);
                put("free", ICarSetting.WelcomeLights.MODE3);
            }
        };
    }

    /**
     * 迎宾灯Mode -> name （joyful：愉悦    comfortable：舒适    free：自由）
     */
    public Map<String, String> getWelcomeLightName() {

        return new HashMap<String, String>() {
            {
                put("joyful", "愉悦");
                put("comfortable", "舒享");
                put("free", "自由");
            }
        };
    }

    /** ----------------------- 迎宾灯光 end-----------------------------------------------*/


    /**
     * 获取驻车灯效的状态
     */
    public boolean getParkLight(HashMap<String, Object> map){
        return operator.getBooleanProp(CarSettingSignal.CARSET_PARKING_LIGHTS);
    }

    /**
     * 设置驻车灯效的状态
     */
    public void setParkLight(HashMap<String, Object> map){
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_PARKING_LIGHTS, "open".equals(switch_type));
    }

    /** ----------------------- 驻车灯效 end------------------------------------------------------*/

    /** ----------------------- 遥控钥匙靠近解锁 start---------------------------------------------*/

    public boolean getRemoteKeyAutoUnlock(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK);
    }

    public void setRemoteKeyAutoUnlock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK, "open".equals(switchType));
    }

    /** ----------------------- 遥控钥匙靠近解锁 end-----------------------------------------------*/

    /** ----------------------- 遥控钥匙离车上锁 start---------------------------------------------*/

    public boolean getRemoteKeyAutoLock(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK);
    }

    public void setRemoteKeyAutoLock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK, "open".equals(switchType));
    }

    /** ----------------------- 遥控钥匙靠近解锁 end------------------------------------------------*/

    /** ----------------------- 蓝牙钥匙靠近解锁 start----------------------------------------------*/

    public boolean getBluetoothKeyAutoUnlock(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK);
    }

    public void setBluetoothKeyAutoUnlock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK, "open".equals(switchType));
    }

    /** ----------------------- 蓝牙钥匙靠近解锁 end------------------------------------------------*/

    /** ----------------------- 蓝牙钥匙离车上锁 start----------------------------------------------*/

    public boolean getBluetoothKeyAutoLock(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK);
    }

    public void setBluetoothKeyAutoLock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK, "open".equals(switchType));
    }

    /** ----------------------- 蓝牙钥匙离车上锁 end------------------------------------------------*/

    /** ----------------------- 近车自动解锁 start----------------------------------------------*/

    public boolean isRemoteOrBluetooth(HashMap<String, Object> map) {
        return map.containsKey("device");
    }

    public boolean isRemoteKey(HashMap<String, Object> map) {
        String device = (String) getValueInContext(map,"device");
        return device.equals("remote_key");
    }

    public boolean getAutoUnlock(HashMap<String, Object> map) {
        //遥控钥匙 1:关 2:开
        boolean remoteState = operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK);
        //蓝牙钥匙 0:关 1:开
        boolean bluetoothState = operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK);
        LogUtils.i(TAG, "getAutoUnlock remoteState :" + remoteState + "---bluetoothState :" + bluetoothState);
        String switchType = (String) getValueInContext(map,"switch_type");
        if (switchType.equals("open")) {
            return remoteState && bluetoothState;
        } else {
            return !remoteState && !bluetoothState;
        }
    }

    public void setAutoUnlock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        //遥控钥匙 1:关 2:开
        boolean remoteState = operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK);
        //蓝牙钥匙 0:关 1:开
        boolean bluetoothState = operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK);
        if (switchType.equals("open")) {
            if (!remoteState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK, true);
            }
            if (!bluetoothState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK, true);
            }
        } else {
            if (remoteState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_UNLOCK, false);
            }
            if (bluetoothState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_UNLOCK, false);
            }
        }
        LogUtils.i(TAG, "setAutoUnlock remoteState :" + remoteState + "---bluetoothState :" + bluetoothState);
    }

    /** ----------------------- 近车自动解锁 end------------------------------------------------*/

    /** ----------------------- 离车自动上锁 start----------------------------------------------*/

    public boolean getAutoLock(HashMap<String, Object> map) {
        //遥控钥匙 1:关 2:开
        boolean remoteState = operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK);
        //蓝牙钥匙 0:关 1:开
        boolean bluetoothState = operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK);
        LogUtils.i(TAG, "getAutoLock remoteState :" + remoteState + "---bluetoothState :" + bluetoothState);
        String switchType = (String) getValueInContext(map,"switch_type");
        if (switchType.equals("open")) {
            return remoteState && bluetoothState ;
        } else {
            return !remoteState && !bluetoothState ;
        }
    }

    public void setAutoLock(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map,"switch_type");
        //遥控钥匙 1:关 2:开
        boolean remoteState = operator.getBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK);
        //蓝牙钥匙 0:关 1:开
        boolean bluetoothState = operator.getBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK);
        if (switchType.equals("open")) {
            if (!remoteState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK, true);
            }
            if (!bluetoothState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK, true);
            }
        } else {
            if (remoteState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_REMOTEKEY_AUTO_LOCK, false);
            }
            if (bluetoothState) {
                operator.setBooleanProp(CarSettingSignal.CARSET_BTKEY_AUTO_LOCK, false);
            }
        }
        LogUtils.i(TAG, "setAutoLock remoteState :" + remoteState + "---bluetoothState :" + bluetoothState);
    }

    /** ----------------------- 离车自动上锁 end------------------------------------------------*/

    /**
     * ----------------------- 智能推荐 start----------------------------------------------
     */

    public boolean isOpenIntelligentRecommendUI(HashMap<String, Object> map){
        return map.containsKey("page_name");
    }

    public boolean getIntelligentRecommend(HashMap<String, Object> map) {
        return operator.getBooleanProp(CarSettingSignal.CARSET_INTELLIGENT_RECOMMEND);
    }

    public void setIntelligentRecommend(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CarSettingSignal.CARSET_INTELLIGENT_RECOMMEND, "open".equals(switchType));
    }

    /** ----------------------- 智能推荐 end------------------------------------------------*/


    /** ----------------------- 行车推荐 start------------------------------------------------*/

    public boolean getDrivingRecommend(HashMap<String, Object> map) {
        // 20250321 默认值为0
        int status = this.mSystemSettingImpl.getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM,"key_drive_mode_recommend", 0);
        LogUtils.i(TAG, "getDrivingRecommend status :" + status);
        return status == 1;
    }

    public void setDrivingRecommend(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        this.mSystemSettingImpl.putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM,"key_drive_mode_recommend", switchType.equals("open") ? 1 : 0);
    }

    /** ----------------------- 行车推荐 end------------------------------------------------*/


    /** ---------------------------双怠速--start----------------------------------------*/
    public boolean isHybridModel(HashMap<String, Object> map){//判断是否是混动车型，  true 是混动车型 false为纯电车型
        boolean isHybrid = true;
//        int powerMode = MegaSystemProperties.getInt(MegaProperties.CONFIG_POWER_MODE, -1);
        int powerMode = operator.getIntProp(CarSettingSignal.CARSET_ISHYBRID);
        LogUtils.i(TAG, "isHybridModel -------------:" + powerMode);
        if (powerMode == 0) {
            isHybrid = false;
        }
        return isHybrid;
    }

    /** ---------------------------双怠速--end-------------------------------------------*/


    /**-----------------------------后排车窗禁用------start-----------------------------------*/

    //判断后排车窗禁用开关是否打开
    public boolean isOpenBackWindowLock(HashMap<String, Object> map){
        LogUtils.i(TAG, "isOpenBackWindowLock -------------start----:");
        boolean isOpen = false;//默认没有打开开关
        int curValueL = operator.getIntProp(CarSettingSignal.CARSET_UNABLE_WINDOW_SWITCH_LEFT);
        int curValueR = operator.getIntProp(CarSettingSignal.CARSET_UNABLE_WINDOW_SWITCH_RIGHT);

        if(curValueL == 0 && curValueR == 0){
            isOpen = true;
        }
        LogUtils.i(TAG, "isOpenBackWindowLock -------------end----:");
        return  isOpen;
    }
    //执行打开后排车窗禁用开关
    public void openTheBackWindowSwitch(HashMap<String, Object> map){
        operator.setIntProp(CarSettingSignal.CARSET_UNABLE_WINDOW_SWITCH,2);//1是关闭 2是打开
        LogUtils.i(TAG, "openTheBackWindowSwitch -------------end----:");
    }


    //执行关闭后排车窗禁用开关
    public void closeTheBackWindowSwitch(HashMap<String, Object> map){
//        carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_REARWINDOWLOCKCTRL, 1);//1是关闭 2是打开
        operator.setIntProp(CarSettingSignal.CARSET_UNABLE_WINDOW_SWITCH,1);//1是关闭 2是打开
        LogUtils.i(TAG, "closeTheBackWindowSwitch -------------end----:");
    }

    /**-----------------------------后排车窗禁用------end-------------------------------------*/


    /**-----------------------------后车标灯--------start----------------------------------*/
    //判断后车标灯是否打开，  false为没有打开，true为打开
    public boolean isOpenRearBadgeLights(HashMap<String, Object> map){
        LogUtils.i(TAG, "isOpenRearBadgeLights -------------start----:");
        boolean isOpen = false;//默认没有打开开关
//        int curValue = carPropHelper.getIntProp(H56C.BCM_state4_BCM_REARLOGOLAMPSTA);
        int curValue = operator.getIntProp(CarSettingSignal.CARSET_REAR_BADGE_LIGHTS_STATE);
        if(curValue == 1){
            isOpen = true;
        }
        LogUtils.i(TAG, "isOpenRearBadgeLights -------------end----:");
        return  isOpen;
    }

    //执行打开后车标灯
    public void openTheRearBadgeLightsSwitch(HashMap<String, Object> map){
//        carPropHelper.setIntProp(H56C.IVI_BodySet3_IVI_REARLOGOLAMPSET, 1);//1是打开，2是关闭
        operator.setIntProp(CarSettingSignal.CARSET_REAR_BADGE_LIGHTS,1);//1是打开，2是关闭
        LogUtils.i(TAG, "openTheRearBadgeLightsSwitch -------------end----:");
    }

    //执行关闭后车标灯
    public void closeTheRearBadgeLightsSwitch(HashMap<String, Object> map){
//        carPropHelper.setIntProp(H56C.IVI_BodySet3_IVI_REARLOGOLAMPSET, 2);//1是打开，2是关闭
        operator.setIntProp(CarSettingSignal.CARSET_REAR_BADGE_LIGHTS,2);//1是打开，2是关闭
        LogUtils.i(TAG, "closeTheRearBadgeLightsSwitch -------------end----:");
    }


    /**-----------------------------后车标灯--------end-------------------------------------*/

    /**-----------------------------强制纯电--------start-------------------------------------*/
    //判断当前是否是纯电优先模式  true 是纯电优先  false 不是纯电优先
    public boolean isModePower(HashMap<String, Object> map){
        LogUtils.i(TAG, "isModePower -------------start----:");
        boolean isPowerMode = true;
        int state = ICarSetting.PowerMode.STANDARD;
//        state = carPropHelper.getIntProp(Driving.ID_DRV_SOCSET);
        state =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE);
//        int value =operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE);
        if(state  != 2){//代表不是纯电优先    等于2时 纯电优先
            isPowerMode = false;
        }
        LogUtils.i(TAG, "isModePower -------------end----:");
        return isPowerMode;
    }

    public boolean isOpenModePower(HashMap<String, Object> map){
        LogUtils.i(TAG, "isOpenModePower -------------start----:");
        boolean isOpen =false;
        int state = operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE);
        if(state ==4){
            isOpen = true;
        }
        LogUtils.i(TAG, "isOpenModePower ------open==="+isOpen+"-------end----:");
        return  isOpen;
    }


    //跳转到打开强制纯电的页面
    public void jumpToModePower(HashMap<String, Object> map){
        LogUtils.i(TAG, "jumpToModePower -------------start----:");
//        int start = -1;
//        try {
//            start = iPageShowManager.showForcePureElectricConfirm();
//        } catch (RemoteException e) {
//            LogUtils.i(TAG, "jumpToModePower -------------e----:"+e.getMessage());
//        }
        mSettingHelper.exec(SettingConstants.CARSETTING_MODEPOWER);
        LogUtils.i(TAG, "jumpToModePower -------------end----:");
    }

    //判断当前强制纯电是否关闭  false 没有关闭 true代表关闭
    public boolean isCloseModePower(HashMap<String, Object> map){
        LogUtils.i(TAG, "isCloseModePower -------------start----:");
        boolean isCloseMode = false ;
        int state = 0;
//        state = carPropHelper.getIntProp(Driving.ID_DRV_SOCSET);
        state = operator.getIntProp(CarSettingSignal.CARSET_POWER_MODE);
        if(state   != 4){//代表强制纯电已经关闭
            isCloseMode = true;
        }
        LogUtils.i(TAG, "isCloseModePower -------------end----:");
        return isCloseMode;
    }

    //执行关闭 强制纯电
    public void closeTheModePower(HashMap<String, Object> map){
//        carPropHelper.setIntProp(Driving.ID_DRV_SOCSET, 2);
        operator.setIntProp(CarSettingSignal.CARSET_POWER_MODE,2);
        LogUtils.i(TAG, "closeTheModePower -------------end----:");
    }

    /**-----------------------------强制纯电---------end-------------------------------------*/

    /**-----------------------------接近灯光---------start-------------------------------------*/

    public boolean isApproachingLightsOpen(HashMap<String, Object> map){
        int isOpen =operator.getIntProp(CarSettingSignal.CARSET_APPROACHING_LIGHTS);////2是关 1是开
        Log2.i(TAG, "---isApproachingLights-------- isApproachingLights :" + isOpen+"------");
        if(isOpen==1){//代表已经是打开的
            return true;
        }
        return  false;
    }

    public boolean isApproachingLightsClose(HashMap<String, Object> map){
        int isOpen =operator.getIntProp(CarSettingSignal.CARSET_APPROACHING_LIGHTS);////2是关 1是开
        Log2.i(TAG, "---isApproachingLightsClose-------- isApproachingLights :" + isOpen+"------");
        if(isOpen==2){//代表已经是关闭的
            return true;
        }
        return  false;
    }

    public void  setApproachingLights(HashMap<String, Object> map){
        LogUtils.i(TAG, "setApproachingLights -------------start----:");
        String switch_type = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switch_type)) {
            if (switch_type.equals("open")) {
                operator.setIntProp(CarSettingSignal.CARSET_APPROACHING_LIGHTS,1);
            } else {
                operator.setIntProp(CarSettingSignal.CARSET_APPROACHING_LIGHTS,2);
            }
        }
        LogUtils.i(TAG, "setApproachingLights -------------end----:");
    }


    /**-----------------------------接近灯光---------end-------------------------------------*/


    /**---------------电吸门功能--------start---------------------------*/
    //电吸门功能  H37B特有
    //获取电吸门开关状态
    public boolean isOpenElectricSuction(HashMap<String, Object> map){
        boolean isOpen = false;//默认开关是关闭的
        int stataValue = 0 ;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            stataValue = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION);
        }else{//H56D
            stataValue = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_GET);

        }
        if(stataValue == 2){//2代表开关是打开的
            isOpen = true;
        }
        LogUtils.d(TAG,"-----isOpenElectricSuction---------stataValue======"+stataValue+"-----");

        return isOpen;
    }
    //打开电吸门开关
    public void setElectricOpen(HashMap<String, Object> map){
        LogUtils.d(TAG,"---setElectricOpen--------======open------------------");

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION, 2);
        }else{//H56D
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_SET, 2);
        }
    }
    //关闭电吸门开关
    public void setElectricClose(HashMap<String, Object> map){
        LogUtils.d(TAG,"---setElectricOpen--------======open------------------");
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION, 1);
        }else{//H56D
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_SET, 1);
        }
    }

    //只有H37B和H56D有电吸功能
    public boolean isHaveElectricDoor(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H56D".equals(carType) || "H37B".equals(carType)) {
            return true;
        }else{//H56C H37A
            return false;
        }
    }

    /**---------------电吸门功能--------end------------------------------*/

}
