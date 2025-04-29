package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/7/17 14:21
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class CarSettingPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> drvModeMap = new BiDirectionalMap<>();

    private final BiDirectionalMap<Integer, String> powerModeMap = new BiDirectionalMap<>();

    private final BiDirectionalMap<Integer, String> autoParkMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> remainMileage = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> unlockModeMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> followHomeMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> welcomeLightsMap = new BiDirectionalMap<>();


    public CarSettingPropertyOperator() {
        drvModeMap.put(ICarSetting.DrivingMode.COMFORTABLE, "comfortable");
        drvModeMap.put(ICarSetting.DrivingMode.ECONOMY, "economy");
        drvModeMap.put(ICarSetting.DrivingMode.SPORT, "high_energy");
        drvModeMap.put(ICarSetting.DrivingMode.PICNIC, "picnic");
        drvModeMap.put(ICarSetting.DrivingMode.SNOW, "snowfield");
        drvModeMap.put(ICarSetting.DrivingMode.SUPER_POWER, "super_power_saving");
        drvModeMap.put(ICarSetting.DrivingMode.CUSTOM, "custom");

        //动力模式
        powerModeMap.put(ICarSetting.PowerMode.ECO, "economy");
        powerModeMap.put(ICarSetting.PowerMode.STANDARD, "standard");
        powerModeMap.put(ICarSetting.PowerMode.SPORT, "sport");
        //自动驻车
        autoParkMap.put(ICarSetting.AvhMode.AUTO, "auto_activation");
        autoParkMap.put(ICarSetting.AvhMode.DEPP_STEP, "deep_step_activation");
        //仪表续航
        remainMileage.put(ICarSetting.RemainMileage.STANDARD, "cltc");
        remainMileage.put(ICarSetting.RemainMileage.REAL, "reality");
        remainMileage.put(ICarSetting.RemainMileage.PERCENT, "percentage");
        //解锁方式 主驾解锁-四门解锁
        unlockModeMap.put(ICarSetting.UnlockMode.ALL_CAR, "total_car");
        unlockModeMap.put(ICarSetting.UnlockMode.DRIVER_ONLY, "first_row_left");
        //伴我回家 时长
        followHomeMap.put(ICarSetting.FollowHomeMode.OFF, "off");
        followHomeMap.put(ICarSetting.FollowHomeMode.MODE_15S, "15");
        followHomeMap.put(ICarSetting.FollowHomeMode.MODE_30S, "30");
        followHomeMap.put(ICarSetting.FollowHomeMode.MODE_60S, "60");
        //迎宾灯效
        welcomeLightsMap.put(ICarSetting.WelcomeLights.OFF, "off");
        welcomeLightsMap.put(ICarSetting.WelcomeLights.MODE1, "joyful");
        welcomeLightsMap.put(ICarSetting.WelcomeLights.MODE2, "comfortable");
        welcomeLightsMap.put(ICarSetting.WelcomeLights.MODE3, "free");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                String mode = (String) getValue(key);
                return drvModeMap.getReverse(mode.toLowerCase());
            case CarSettingSignal.CARSET_POWER_MODE:
                String powerMode = (String) getValue(key);
                return powerModeMap.getReverse(powerMode.toLowerCase());
            case CarSettingSignal.CARSET_AUTO_PARKING:
                String autoPark = (String) getValue(key);
                return autoParkMap.getReverse(autoPark.toLowerCase());
            case CarSettingSignal.CARSET_REMAIN_MILEAGE:
                String remain = (String) getValue(key);
                return remainMileage.getReverse(remain.toLowerCase());
            case CarSettingSignal.CARSET_UNLOCK_MODE:
                String unlockMode = (String) getValue(key);
                return unlockModeMap.getReverse(unlockMode.toLowerCase());
            case CarSettingSignal.CARSET_FOLLOW_HOME:
                String followHome = (String) getValue(key);
                return followHomeMap.getReverse(followHome.toLowerCase());
            case CarSettingSignal.CARSET_WELCOME_LIGHTS:
                String lights = (String) getValue(key);
                return welcomeLightsMap.getReverse(lights.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                String mode = drvModeMap.getForward(value);
                setValue(key, mode);
                break;
            case CarSettingSignal.CARSET_POWER_MODE:
                String powerMode = powerModeMap.getForward(value);
                setValue(key, powerMode);
                break;
            case CarSettingSignal.CARSET_AUTO_PARKING:
                String autoPark = autoParkMap.getForward(value);
                setValue(key, autoPark);
                break;
            case CarSettingSignal.CARSET_REMAIN_MILEAGE:
                String remain = remainMileage.getForward(value);
                setValue(key, remain);
                break;
            case CarSettingSignal.CARSET_UNLOCK_MODE:
                String unlockMode = unlockModeMap.getForward(value);
                setValue(key, unlockMode);
                break;
            case CarSettingSignal.CARSET_FOLLOW_HOME:
                String followHome = followHomeMap.getForward(value);
                setValue(key, followHome);
                break;
            case CarSettingSignal.CARSET_WELCOME_LIGHTS:
                String lights = welcomeLightsMap.getForward(value);
                setValue(key, lights);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }

    }
}
