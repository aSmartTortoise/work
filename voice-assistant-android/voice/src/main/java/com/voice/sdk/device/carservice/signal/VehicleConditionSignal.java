package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/9/13 17:26
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class VehicleConditionSignal {
    public static final String CONDITION_REMAIN_BATTERY = "cond_RemainBattery"; //剩余电量
    public static final String CONDITION_REMAIN_MILEAGE = "cond_RemainMileage"; //CLTC续航里程
    public static final String CONDITION_MAX_MILEAGE = "cond_MaxMileage"; //满电里程
    public static final String CONDITION_IS_CHARGING = "cond_IsCharging"; //是否充电中
    public static final String CONDITION_TRAVELED_MILEAGE = "cond_TraveledMileage"; //行驶过的里程
    public static final String CONDITION_REMAIN_MILEAGE_MODE = "cond_RemainMileageMode"; //续航里程模式
    public static final String CONDITION_REAL_REMAIN_MILEAGE = "cond_RealRemainMileage"; //综合续航里程
    public static final String CONDITION_OPEN_VEHICLE_HEALTH = "cond_VehicleHealth"; //打开车辆健康应用,无需存数据库
    public static final String CONDITION_CONFIG_EQUIPMENT_LEVEL = "cond_ConfigEquipmentLevel"; //车辆装备等级配置字
}
