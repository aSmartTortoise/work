package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/18 15:58
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class CommonSignal {
    public static final String COMMON_CAR_TYPE = "common_CarType"; //车辆状态

    public static final String COMMON_SPEED_INFO = "common_SpeedInfo"; //车速信息
    public static final String COMMON_GEAR_INFO = "common_GearInfo"; //车辆档位信息
    public static final String COMMON_PRIVACY_PROTECTION = "common_PrivacyProtection"; //隐私保护开关
    public static final String COMMON_INFO_HIDING = "common_InfoHiding"; //信息隐藏开关
    public static final String COMMON_SPLIT_SWITCH = "common_SplitSwitch"; //分屏开关
    public static final String COMMON_CURRENT_PAGE = "common_CurrentPage"; //是否在当前界面，所有功能共用
    public static final String COMMON_IS_NAVI = "common_IsNavi"; //是否导航中
    public static final String COMMON_HAS_BT_CONNECT = "common_BTConnected"; //是否有蓝牙设备连接
    public static final String COMMON_IS_4WD = "common_Is4WD"; //是否四驱
    public static final String COMMON_SEAT_PEOPLE_STATE = "common_seat_people_state";//座椅上是否做人的判断。
    public static final String COMMON_SCREEN_ENUMERATION = "common_screen_enumeration";//屏幕的枚举
    public static final String COMMON_SEAT_STATE = "common_seat_state";//座椅状态
    public static final String COMMON_CEIL_CONFIG = "common_CeilingScreenConfig"; //吸顶屏配置字
    public static final String COMMON_CEIL_OPEN = "common_CeilingSwitch"; //吸顶屏开关
    public static final String COMMON_CEIL_ANGLE = "common_CeilingAngle"; //吸顶屏角度
    public static final String COMMON_LAST_ANGLE = "common_LastAngle"; //上一次角度 由设置缓存
    public static final String COMMON_POWER_MODER = "common_PowerMode"; //动力类型[0纯电1混动]
    public static final String COMMON_360_STATE = "common_360_state";//360是否打开
    public static final String COMMON_SUPPORT_REFRIGERATOR = "common_support_refrigerator";//是否有冰箱配置
    public static final String COMMON_SUPPORT_FAST = "COMMON_SUPPORT_FAST";//是否有快充配置
    /**
     * 车型配置
     * 0001	装备等级-N1
     * 0010	装备等级-N2
     * 0011	装备等级-N3
     * 0100	装备等级-N4
     * 0101	N1大客户版
     * 0110	N2大客户版
     * 0111	N3大客户版
     * 1000	N1对公版
     * 1001	N2对公版对公版
     * 1010	N3对公版对公版
     * 1011	装备等级-N5
     * 1100	装备等级-G1
     * 1101	装备等级N3+
     * 1110	装备等级N4+
     * 1111	行政版
     */
    public static final String COMMON_EQUIPMENT_LEVEL = "COMMON_EQUIPMENT_LEVEL";//装配等级

    public static final String COMMON_SUPPORT_HEADREST = "common_support_headrest";//是否有头枕音响配置
    public static final String COMMON_HEADREST_STATE = "common_headrest_state";//头枕音响状态

    public static final String COMMON_REMAIN_POWER = "car_ElecPower";//获取当前电量

    public static final String COMMON_DEAL_BT = "common_deal_bt";//操作吸顶屏蓝牙

    public static final String COMMON_DISMISS_DIALOG = "common_dismiss_dialog";//隐藏副驾、吸顶屏设置弹窗
    public static final String COMMON_OPEN_BT = "common_open_bt";
    public static final String COMMON_OPEN_AP = "common_open_ap";
    public static final String COMMON_OPEN_WIFI = "common_open_wifi";

    public static final String COMMON_SETTING_STATE = "common_setting_state";
    public static final String COMMON_CAR_HEALTH = "common_car_health";//车辆健康

    public static final String COMMON_LLM_MODE = "common_llm_mode"; //大模型类型
    public static final String COMMON_PARK_APP_STATE = "common_ParkAppState"; //泊车辅助app
    public static final String COMMON_HAS_REMOTE_APP = "common_HasRemoteApp"; // 是否支持遥控器app
    public static final String COMMON_IS_SUPPORT_R = "common_is_support_r";//R挡功能

}
