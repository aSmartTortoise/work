package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/17 14:18
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class CarSettingSignal {
    public static final String CARSET_SAFE_SENSITIVE = "car_SafeSensitiveLevel"; //安全监测灵敏度
    public static final String CARSET_WIPE_ACTION_STATE = "car_WipeActionState"; //雨刷动作状态
    public static final String CARSET_WIPE_SPEED_STATE = "car_WipeSpeedState"; //雨刷速度
    public static final String CARSET_WIPE_AUTO_SWITCH = "car_WipeAutoSwitch"; //自动雨刮开关
    public static final String CARSET_WIPE_REPAIR_STATE = "car_WipeRepairState"; //雨刮维修模式开关
    public static final String CARSET_AUTO_WIPE_SENSITIVE = "car_AutoWipeSensitive"; //自动雨刮灵敏度
    public static final String CARSET_DRIVING_MODE = "car_DriveMode"; //车辆驾驶模式；
    public static final String CARSER_SUPER_POWER_SAVING_CONFIG = "car_SuperPowerSaving"; //超级省电模式配置支持
    public static final String CARSET_POWER_MODE = "car_PowerMode"; //自定义驾驶模式-动力模式；
    public static final String CARSET_ELE_PARKING = "car_EleParking"; //电子驻车 读写分离
    public static final String CARSET_SAFE_ESP = "car_SafeESP"; //车身稳定系统
    public static final String CARSET_COMFORT_PARKING = "car_ComfortParking"; //舒适停车
    public static final String CARSET_HILL_DESCENT = "car_HillDescent"; //陡坡缓降
    public static final String CARSET_AUTO_PARKING = "car_AutoParking"; //自动驻车
    public static final String CARSET_ENERGY_RECOVERY = "car_EnergyRecovery"; //能量回收等级
    public static final String CARSET_REMAIN_MILEAGE = "car_RemainMileage"; //续航里程
    public static final String CARSET_DOOR_LOCK = "car_DoorLock"; //车门锁、中控锁
    public static final String CARSET_CHILD_LOCK = "car_ChildLock"; //儿童安全锁，业务上只有写的需求
    public static final String CARSET_CHILD_LOCK_CONFIG = "car_ChildLockConfig"; //儿童安全锁，业务上只有写的需求
    public static final String CARSET_UNLOCK_MODE = "car_UnlockMode"; //车辆解锁模式
    public static final String CARSET_WALK_AWAY_LOCK = "car_WalkAwayLock"; //送宾闭锁
    public static final String CARSET_PARKING_UNLOCK = "car_ParkingUnlock"; //P挡解锁，驻车解锁
    public static final String CARSET_LOCK_UNLOCK_VOICE = "car_LockUnlockVoice"; //解闭锁喇叭提示音
    public static final String CARSET_LOCK_CLOSE_WINDOW = "car_LockCloseWindow"; //锁车自动升窗
    public static final String CARSET_RMM_AUTO_FOLD = "car_RmmAutoFold"; //锁车后视镜自动折叠
    public static final String CARSET_RM_AUTO_DOWN = "car_RmAutoDown"; //倒车后视镜自动下翻
    public static final String CARSET_REAR_POS_LAMP = "car_RearPosLamp"; //后位置灯随日行灯开启
    public static final String CARSET_HIGH_BEAM_ASSIST = "car_HighBeamAssist"; //智能远光辅助
    public static final String CARSET_INTERIOR_LIGHT_AUTO_DOME = "car_InteriorLightAutoDome"; //顶灯自动点亮
    public static final String CARSET_CHARGE_LIGHT_EFFECT = "car_ChargeLightEffect"; //充电动态灯效
    public static final String CARSET_FOLLOW_HOME = "car_FollowHome"; //伴我回家
    public static final String CARSET_WELCOME_LIGHTS = "car_WelcomeLights"; //迎宾灯光

    public static final String CARSET_BATTERY_WARM_SWITCH = "car_BatteryWarmSwitch"; //电池维温
    public static final String CARSET_V2V_SWITCH = "car_V2VSwitch"; //对车放电开关
    public static final String CARSET_V2V_INVALID = "car_v2v_invalid"; //对车放电是否不可用
    public static final String CARSET_INTELLIGENT_RECOMMEND = "car_IntelligentRecommend"; //智能推荐
    public static final String CARSET_PARKING_LIGHTS = "car_ParkingLights"; //驻车灯效
    public static final String CARSET_REMOTEKEY_AUTO_UNLOCK = "car_RemoteKeyAutoUnlock"; //遥控钥匙近车自动解锁
    public static final String CARSET_REMOTEKEY_AUTO_LOCK = "car_RemoteKeyAutoLock"; //遥控钥匙离车自动上锁
    public static final String CARSET_BTKEY_AUTO_UNLOCK = "car_BTAutoUnlock"; //蓝牙钥匙近车自动解锁
    public static final String CARSET_BTKEY_AUTO_LOCK = "car_BTAutoLock"; //蓝牙钥匙离车自动上锁

    public static final String CARSET_POWER_MODE_DRIVER_MODE = "car_PowerModeDriverMode";//驾驶模式自定义中的动力模式，非动力模式，非驾驶模式
    public static final String CARSET_REAR_BADGE_LIGHTS = "car_RearBadgeLights";//执行打开/关闭后车标灯
    public static final String CARSET_REAR_BADGE_LIGHTS_STATE = "car_RearBadgeLightsState";//查询后车标灯的状态
    public static final String CARSET_UNABLE_WINDOW_SWITCH = "car_UnableWindowSwitch";//执行打开/关闭 后排车窗禁用开关
    public static final String CARSET_UNABLE_WINDOW_SWITCH_LEFT = "car_UnableWindowSwitchLeft";//左侧后排车窗禁用开关
    public static final String CARSET_UNABLE_WINDOW_SWITCH_RIGHT = "car_UnableWindowSwitchRight";//左侧后排车窗禁用开关


    public static final String CARSET_DOOR_STATE = "car_DoorState";//获取车门状态

    public static final String CARSET_DOOR_FRONTLEFT = "car_DoorFrontLeft";//左前车门
    public static final String CARSET_DOOR_FRONTRIGHT = "car_DoorFrontRight";//右前车门
    public static final String CARSET_DOOR_REARLEFT = "car_DoorRearLeft";//左后车门
    public static final String CARSET_DOOR_REARRIGHT = "car_DoorRearRight";//右后车门

    public static final String CARSET_DISABLE_MODE = "car_DisableMode";//获取是否处于Disable模式

    public static final String CARSET_ISHYBRID = "car_IsHybrid";//是否是混动车型

    public static final String CARSET_LOCK_UNLOCK_VOICE_TTS_TEXT = "car_LockUnlockVoiceTtsText"; //37A文案是解锁锁鸣笛，其他车型都是闭锁鸣笛
    public static final String CARSET_IS_HAS_CHARGING_EFFECTSTATUS_CONFIG = "car_IsHasChargingEffectstatusConfig"; //37A文案是解锁锁鸣笛，其他车型都是闭锁鸣笛
    public static final String CARSET_IS_HAS_PARK_LIGHT_CONFIG = "car_IsHasParkLightConfig"; //只有37有驻车灯效，56C和56D没有
    public static final String CARSET_AIR_REAR_LOCK = "carset_air_rear_lock"; //后排面板锁
    public static final String CARSET_NEED_SPLIT = "carset_need_split"; //功能是否拆分
    public static final String CARSET_APPROACHING_LIGHTS = "carset_ApproachingLights";//接近灯光
}

