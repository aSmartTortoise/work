package com.voyah.h37z;

/**
 * 车辆设置常量.
 */
public class Constants {

    public static final String TAG = "Vehicle--";

    //后视镜按键自定义KEY
    public static final String REAR_MIRROR_CUSTOM_KEY = "rear_mirror_custom_key";

    //后视镜调节超时时间
    public static final int REAR_MIRROR_TIME_OUT = 5000;

    //后视镜调节保存
    public static final String REAR_MIRROR_SAVE = "rear_mirror_save";

    //车内监控(后排监控)
    public static final String MONITOR_SHOW = "monitor_show";

    //监听方控调节
    public static final String REAR_MIRROR_ADJUSTING_KEY = "rear_mirror_adjusting_key";

    //车道偏离辅助模式记忆
    public static final String LDA_MODE_TYPE = "lda_mode_type";

    //电动尾门超时时间
    public static final int ELE_TAIL_GATE_TIME_OUT = 30600;

    //电动桌板超时时间
    public static final int ELE_TABLE_BOARD_TIME_OUT = 10600;

    //超时时间
    public static final int TIME_OUT = 3000;

    //时间间隔
    public static final int TIME_INTER = 1000;

    //Kanzi延时时间
    public static final int KANZI_DELAY_TIME = 500;

    //跳转Tab子页面延时时间
    public static final int CHILD_DELAY_TIME = 500;

    public static final int SLIDE_SCREEN_POS = 5;

    //配置字
    public static final int CONFIG_NONE = 0;

    public static final int CONFIG_HAVE = 1;

    public static final int CONFIG_HPP_RESERVE = 3;

    public static final int CONFIG_NO_AEB_NO_MEB = 0;

    public static final int CONFIG_HAVE_AEB_NO_MEB = 1;

    public static final int CONFIG_HAVE_AEB_HAVE_MEB = 2;

    public static final int CONFIG_NO_AEB_HAVE_MEB = 3;

    //遮阳帘超时时间
    public static final int SUNSHADE_TIME_OUT = 6000;

    //方控Position
    public static final String FANG_CONTROL_POS = "fang_control_pos";

    //点击选中的页面滑动到顶部
    public static final String SCROLL_TO_TOP = "scroll_top";
    //我的岚图超时
    public static final String VOYAH_TIME_OUT = "voyah_time_out";
    //车辆超时
    public static final String VEHICLE_TIME_OUT = "vehicle_time_out";
    //车辆 车窗天窗超时
    public static final String WINDOW_TIME_OUT = "window_time_out";
    //车辆 遮阳帘超时
    public static final String SUN_TIME_OUT = "sun_time_out";
    //电动尾门超时
    public static final String ELE_TIME_OUT = "ele_time_out";
    //灯光超时
    public static final String LIGHT_TIME_OUT = "light_time_out";
    //驾驶偏好超时
    public static final String DRV_PRE_TIME_OUT = "drv_pre_time_out";
    //驾驶辅助超时
    public static final String INTER_DRV_TIME_OUT = "inter_drv_time_out";
    //安全与维护超时
    public static final String SAFETY_TIME_OUT = "safety_time_out";
    //点击选中的页面滑动到顶部
    public static final String SOUND_ACTIVITY_DISMISS = "sound_activity_dismiss";

    public static final String WALLPAPER_ACTIVITY_FINISH = "Wallpaper_Activity_finish";

    //语音跳转
    public static final String ROUTE_FLAG = "route_flag";

    public static final String ROUTE_FLAG_NEW = "route_flag_new";

    //配合语音跳转到我的岚图Tab-语音配合显示
    public static final String VOYAH_VOICE_SHOW = "voyah_voice_show";

    // 语音选择模拟声浪位置
    public static final String SOUND_WAVE = "sound_wave";

    //流量查询
    public static final int VOYAH_TRAFFIC_QUERY = 1;

    //系统升级
    public static final int VOYAH_SYSTEM_UPGRADE = 2;

    //能量管理
    public static final int VOYAH_ENERGY_MANAGER = 3;

    //预约出行
    public static final int VOYAH_BOOK_TRIP = 4;

    //行程统计
    public static final int VOYAH_TRIP_CARD = 5;

    //能量管理，预约出行需要滑动的距离
    public static final int VOYAH_SCROLL_THREE_HUNDRED = 300;

    public static final int VOYAH_SCROLL_ADAS = 848;

    public static final int VEHICLE_SCROLL_THREE_HUNDRED = 920;

    //选中底部tab页时滑动距离
    public static final int RECYCLE_SCROLL_BOTTOM = 800;

    //配合专属用车跳转到车辆Tab-后视镜页面配合显示
    public static final String VEHICLE_USER_SHOW = "vehicle_user_show";

    //配合语音跳转
    public static final String VEHICLE_VOICE_SHOW = "vehicle_voice_show";

    //配合Launch跳转
    public static final String VEHICLE_LAUNCH_SHOW = "vehicle_launch_show";

    //语音-方向盘自定按键
    public static final int VEHICLE_VOICE_SHOW_CUSTOM_KEY_INPUT = 1;

    //配合语音跳转到调节后视镜(这个要单独弄)
    public static final String VEHICLE_REAR_MIRROR_ADJUST_VOICE =
            "vehicle_rear_mirror_adjust_voice";

    public static final String VEHICLE_SWITCH_REAR_MIRROR_VOICE =
            "vehicle_switch_rear_mirror_voice";

    //语音-解锁设置
    public static final int VEHICLE_SHOW_UNLOCK_SET = 2;

    //后视镜页面
    public static final int VEHICLE_SHOW_REAR_MIRROR = 3;

    //语音-后视镜调节弹窗
    public static final int VEHICLE_SHOW_REAR_MIRROR_ADJUST = 4;

    //配合用户中心跳转到灯光Tab-配合显示
    public static final String LIGHT_USER_SHOW = "light_user_show";

    //配合语音跳转到灯光Tab
    public static final String LIGHT_VOICE_SHOW = "light_voice_show";

    //氛围灯
    public static final int LIGHT_AMB_LIGHT = 1;

    //外部光效
    public static final int LIGHT_OUTSIDE_EFFECT = 2;

    //充电显示
    public static final int LIGHT_CHARGING_SHOW = 3;

    //氛围灯自定弹窗
    public static final int LIGHT_AMB_CUSTOM_COLOR = 4;

    //配合专属用车跳转到驾驶偏好Tab-显示
    public static final String DRV_PRE_USER_SHOW = "drv_pre_user_show";

    public static final int DRV_PRE_DRIVE_MODE = 1;

    //配合语音跳转到驾驶偏好Tab-语音配合显示
    public static final String DRV_PRE_VOICE_SHOW = "drv_pre_voice_show";

    //动力模式设置,底盘模式设置
    public static final int DRV_PRE_POWER_CHASSIS_MODE = 1;

    //语音-行驶辅助设置
    public static final int DRV_PRE_DRV_ASSIST_SET = 2;

    //语音-超级省电模式
    public static final int DRV_PRE_ECO_PLUS_MODE = 4;

    //行驶辅助需要滑动的距离
    public static final int DRV_PRE_DRV_ASSIST_SCROLL_VALUE = 1000;

    //语音-自动驻车
    public static final int DRV_PRE_AUTO_HOLD = 3;

    //行驶辅助需要滑动的距离
    public static final int DRV_PRE_AUTO_HOLD_SCROLL_VALUE = 1140;

    public static final int DRV_PRE_AUTO_HOLD_SCROLL_ADAS = 1516;

    //配合语音跳转到安全与维护Tab-语音配合显示
    public static final String SAFETY_VOICE_SHOW = "safety_voice_show";

    //语音-驾驶员行为监测
    public static final int SAFETY_DRIVER_ACTION_MONITOR = 1;

    //语音-后排乘客检测
    public static final int SAFETY_PASSENGER_MONITOR = 2;

    //语音-哨兵模式
    public static final int SAFETY_SENTINEL_MODE = 3;

    //语音-车辆维护
    public static final int SAFETY_VEHICLE_MAINTAIN = 4;

    //语音-车内监控画面(后排监控画面)
    public static final int SAFETY_PASSENGER_MONITOR_FRAME = 5;

    //后排乘客检测滑动距离
    public static final int SAFETY_PASSENGER_MONITOR_SCROLL_ADAS = 412;

    //车辆维护滑动距离
    public static final int SAFETY_VEHICLE_MAINTAIN_SCROLL_ADAS = 532;

    /**
     * 驾驶辅助-语音相关
     */
    //配合语音跳转到驾驶辅助
    public static final String INTER_DRV_VOICE_SHOW = "inter_drv_voice_show";

    //语音-主动安全卡片展开
    public static final int INTER_DRV_VOICE_ACTIVE_SAFETY = 1;

    //语音-智能行车卡片展开
    public static final int INTER_DRV_VOICE_SMART_DRIVING = 2;

    //语音-前向碰撞预警(FCW)
    public static final int INTER_DRV_VOICE_FCW = 3;

    //语音-前向紧急制动(自动紧急制动)AEB
    public static final int INTER_DRV_VOICE_AEB = 4;

    //语音-后向碰撞预警(RCW)
    public static final int INTER_DRV_VOICE_RCW = 5;

    //语音-低速紧急制动(MEB)
    public static final int INTER_DRV_VOICE_MEB = 6;

    //语音-车道偏离辅助(LDW-LKA)
    public static final int INTER_DRV_VOICE_LDA = 7;

    //语音-紧急车道保持(ELK)
    public static final int INTER_DRV_VOICE_ELK = 8;

    //语音-紧急转向辅助(ESA)
    public static final int INTER_DRV_VOICE_ESA = 9;

    //语音-盲区辅助卡片展开
    public static final int INTER_DRV_VOICE_BLIND_SPOT_ASSIST = 10;

    //语音-盲区检测预警(BSD)
    public static final int INTER_DRV_VOICE_BSD = 11;

    //语音-并线辅助预警(变道辅助预警LCA)
    public static final int INTER_DRV_VOICE_LCA = 12;

    //语音-前方交通穿行提示(FCTA)
    public static final int INTER_DRV_VOICE_FCTA = 13;

    //语音-后方交通穿行提示(RCTA)
    public static final int INTER_DRV_VOICE_RCTA = 14;

    //语音-开门预警(DOW)
    public static final int INTER_DRV_VOICE_DOW = 15;

    //语音-智能行车风格
    public static final int INTER_DRV_VOICE_SMART_DRV_STYLE = 16;

    //语音-触发式变道辅助(TLC)
    public static final int INTER_DRV_VOICE_TLC = 17;

    //语音-导航(领航)辅助驾驶(NOA)
    public static final int INTER_DRV_VOICE_NOA = 18;

    //语音-交通标志识别(TSR)
    public static final int INTER_DRV_VOICE_TSR = 19;

    //语音-前车起步提醒(FVSR)
    public static final int INTER_DRV_VOICE_FVSR = 20;
    //自动模式
    public static final int INTER_DRV_VOICE_AUTO_NIGHT = 21;

    //分屏开启
    public static final String SPLIT_SCREEN = "split_screen";

    public static final String KANZI_INIT_COMPLETE = "kanzi_init_complete";

    public static final String KANZI_ALL = "0";

    public static final String KANZI_SKYLIGHT = "1";

    public static final String KANZI_WINDOW = "2";

    public static final String KANZI_REAR_MIRROR = "3";

    public static final String KANZI_ELE_TAILGATE = "4";

    public static final String VEHICLE = "vehicle";

    public static final String VEHICLE_ALL = "vehicle_all";

    public static final String VEHICLE_WINDOW = "vehicle_window";

    public static final String VEHICLE_REAR_MIRROR = "vehicle_rear_mirror";

    public static final String VEHICLE_ELE_TAILGATE = "vehicle_ele_tailgate";

    public static final String VEHICLE_SKYLIGHT = "vehicle_skylight";

    //我的岚图
    public static final int FRAGMENT_VOYAH_ID = 0;
    //车辆
    public static final int FRAGMENT_VEHICLE_ID = 1;
    //灯光
    public static final int FRAGMENT_LIGHT_ID = 2;
    //驾驶偏好
    public static final int FRAGMENT_DRIVE_PREFERENCE = 3;
    //智能驾驶
    public static final int FRAGMENT_INTELLIGENT_DRIVE = 4;
    //显示
    public static final int FRAGMENT_DISPLAY = 5;
    //声音
    public static final int FRAGMENT_SOUND = 6;
    //语音
    public static final int FRAGMENT_VPA = 7;
    //连接
    public static final int FRAGMENT_CONNECT = 8;
    //安全与维护
    public static final int FRAGMENT_SAFETY_AND_MAINTENANCE = 9;
    //系统
    public static final int FRAGMENT_SYSTEM = 10;
    //测试氛围灯
    public static final int FRAGMENT_AMB = 11;

    //我的岚图Tag
    public static final String VOYAH_TAG = "VOYAH";

    //车辆Tag
    public static final String VEHICLE_TAG = "VEHICLE";

    //灯光Tag
    public static final String LIGHT_TAG = "LIGHT";

    //驾驶偏好Tag
    public static final String DVR_TAG = "DRV";

    //智能驾驶Tag
    public static final String INTER_TAG = "INTER";

    //安全与维护Tag
    public static final String SAFETY_TAG = "SAFETY";

    //显示Tag
    public static final String DISPLAY_TAG = "DISPLAY";

    //声音Tag
    public static final String SOUND_TAG = "SOUND";

    //系统Tag
    public static final String SYSTEM_TAG = "SYSTEM";

    //连接Tag
    public static final String CONNECT_TAG = "CONNECT";

    /**
     * 我的岚图.
     */
    public static final String DEVICE_NAME = "device_name";

    //智能领航辅助驾驶 是否是第一次开启
    public static final String NOA_DRV_ASSIST_OPEN_FIRST = "NOA_DRV_ASSIST_OPEN_FIRST";

    //电动桌板 是否是第一次开启
    public static final String ELE_TABLE_BOARD_OPEN_FIRST = "ELE_TABLE_BOARD_OPEN_FIRST";

    //中文字符范围
    public static final int MIN_CHINESE = 0x0391;

    public static final int MAX_CHINESE = 0xFFE5;
    //英文字符范围
    public static final int MAX_ENGLISH = 0x00FF;

    public static final int VALUE_THREE = 3;

    public static final int VALUE_FOUR = 4;

    public static final int VALUE_FIVE = 5;

    public static final int VALUE_SIX = 6;

    public static final int VALUE_SEVEN = 7;

    public static final int VALUE_TWENTY_THREE = 23;

    public static final int VALUE_THIRTY = 30;

    public static final int VALUE_SIXTY = 60;

    public static final int VALUE_FOURTEEN = 14;

    public static final int VALUE_FIFTEEN = 15;

    public static final int VALUE_TWENTY_EIGHT = 28;

    public static final int VALUE_THIRTY_ONE = 31;

    public static final int VALUE_FIFTY_SIX = 56;

    public static final int VALUE_SIXTY_TWO = 62;

    public static final int VALUE_SIXTY_THREE = 63;

    public static final int VALUE_ONE_HUNDRED_TWELVE = 112;

    public static final int VALUE_ONE_HUNDRED_TWENTY = 120;

    public static final int VALUE_ONE_HUNDRED_TWENTY_FOUR = 124;

    public static final int VALUE_ONE_HUNDRED_TWENTY_SIX = 126;

    public static final int VALUE_ONE_HUNDRED_TWENTY_SEVEN = 127;

    /**
     * 充电管理.
     */
    public static final int POWER_MIN_VALUE = 60;

    //最小目标充电电量
    public static final int POWER_MIN_PER = 5;

    public static final int POWER_MAX_VALUE = 100;

    //最小目标充电进度
    public static final int POWER_MIN_PROGRESS = 0;

    public static final int POWER_MAX_PROGRESS = 8;

    /**
     * 预约充电开关.
     * 0x0:初始值
     * 0x1:关闭
     * 0x2:开启
     * 0xFF:无效值
     */
    public static final int APPOINT_SWITCH_OFF = 1;

    public static final int APPOINT_SWITCH_ON = 2;

    /**
     * 放电管理.
     */
    public static final int POWER_DIS_MIN_PER = 5;

    public static final int POWER_DIS_MIN_VALUE = 5;

    public static final int POWER_DIS_MAX_VALUE = 50;

    public static final int POWER_DIS_MIN_PROGRESS = 0;

    public static final int POWER_DIS_MAX_PROGRESS = 9;

    public static final int POWER_DIS_DEFAULT_PROGRESS = 3;

    /**
     * ColorSeekBar.
     */
    public static final int WIDTH_PADDING = 5;

    public static final int BAR_HEIGHT = 64;

    public static final int NEGATIVE_ONE = -1;

    public static final int VALUE_ONE = 1;

    public static final float HALF_PERCENT = .5f;

    public static final int VALUE_TWO = 2;

    public static final float VALUE_TWO_FLOAT = 2f;

    public static final int RING_SIZE = 10;

    public static final int RING_BORDER_SIZE = 3;

    public static final int DEFAULT_THUMB_SIZE = 16;

    /**
     * 车辆.
     */
    public static final int VEHICLE_FRAGMENT_ALL = 0;

    public static final int VEHICLE_FRAGMENT_SKYLIGHT = 1;

    public static final int VEHICLE_FRAGMENT_WINDOW = 2;

    public static final int VEHICLE_FRAGMENT_REAR_MIRROR = 3;

    public static final int VEHICLE_FRAGMENT_ELECTRIC_TAILGATE = 4;

    public static final int LIMIT_HEIGHT = 780;

    //车窗
    public static final int WINDOW_CLOSE = 0;

    public static final int WINDOW_VEN = 50;

    public static final int WINDOW_OPEN = 100;

    //电动尾门
    public static final int ELE_GATE_PAUSE = 40;

    public static final int ELE_GATE_OPEN = 100;

    //遮阳帘
    public static final int SUNSHADE_CLOSE = 0;

    public static final int SUNSHADE_OPEN = 100;

    //天窗
    public static final int SKYLIGHT_CLOSE = 0;

    public static final int SKYLIGHT_OPEN = 100;

    public static final int SKYLIGHT_INVALID = 255;

    /**
     * kanzi联动的车窗.
     * 全开/全关/通风
     */
    public static final int KANZI_WINDOW_OPEN = 1;

    public static final int KANZI_WINDOW_ALL_OPEN = 1;

    public static final int KANZI_WINDOW_CLOSE = 0;

    public static final int KANZI_WINDOW_ALL_CLOSE = 0;

    public static final float KANZI_WINDOW_VEN = 0.5f;

    /**
     * 电动尾门高度.
     */
    public static final int ELE_TAILGATE_MIN_PROGRESS = 0;

    public static final int ELE_TAILGATE_ONE = 1;

    public static final int ELE_TAILGATE_MAX_PROGRESS = 6;

    public static final int ELE_TAILGATE_MAX_VALUE = 7;

    public static final int ELE_TAILGATE_MIN_HEIGHT_PERCENT = 40;

    public static final int ELE_TAILGATE_PER_PERCENT = 10;

    /**
     * 配置Kanzi.
     */
    public static final String FRAGMENT_ALL = "0";

    public static final String FRAGMENT_SKYLIGHT = "1";

    public static final String FRAGMENT_WINDOW = "2";

    public static final String FRAGMENT_REAR_MIRROR = "3";

    public static final String FRAGMENT_ELECTRIC_TAILGATE = "4";

    /**
     * kanzi联动的电动尾门.
     * 开启/关闭
     */
    public static final int KANZI_ELE_TAILGATE_OPEN = 1;

    public static final int KANZI_ELE_TAILGATE_CLOSE = 0;

    /**
     * kanzi联动的天窗.
     * 开启/关闭/通风
     */
    public static final int KANZI_SKYLIGHT_OPEN = 1;

    public static final int KANZI_SKYLIGHT_CLOSE = 0;

    public static final float KANZI_SKYLIGHT_VEN = 0.5f;

    /**
     * kanzi联动的遮阳帘.
     * 开启/关闭/半开通风
     */
    public static final int KANZI_SUNSHADE_OPEN = 1;

    public static final int KANZI_SUNSHADE_CLOSE = 0;

    public static final float KANZI_SUNSHADE_VEN = 0.5f;

    /**
     * kanzi联动的车门.
     * 开启/关闭
     */
    public static final int KANZI_DOOR_OPEN = 1;

    public static final int KANZI_DOOR_CLOSE = 0;

    /**
     * 左右后视镜
     */
    public static final int REAR_MIRROR_MAX = 100;

    public static final int REAR_MIRROR_ONE = 1;

    public static final int TYPE_NO_REQUEST = 0;

    public static final int TYPE_LEFT = 1;

    public static final int TYPE_RIGHT = 2;

    public static final int DELAY_TIME = 300;

    //方向
    public static final int DIRECTION_LEFT = 1;

    public static final int DIRECTION_RIGHT = 2;

    public static final int DIRECTION_UP = 3;

    public static final int DIRECTION_DOWN = 4;

    //方控服务重连次数
    public static final int RECONNECTION_COUNT = 5;

    /**
     * 滑移屏.
     */
    public static final int SCREEN_SLIDE_MAIN = 1;

    public static final int SCREEN_SLIDE_MIDDLE = 2;

    public static final int SCREEN_SLIDE_VICE = 3;

    public static final String AMB_LIGHT_STATIC_LAST_MODE = "amb_light_static_last_mode";

    public static final String AMB_LIGHT_DYNAMIC_LAST_MODE = "amb_light_dynamic_last_mode";

    /**
     * 氛围灯.
     */
    public static final int AMB_LIGHT_OFF = 0;

    public static final int AMB_LIGHT_STATIC_MODE = 1;

    public static final int AMB_LIGHT_MUSIC_MODE = 2;

    public static final int AMB_LIGHT_DRIVE_MODE = 3;

    public static final int BAR_BRIGHTNESS_POINT_RADIUS = 6;

    public static final int BAR_BRIGHTNESS_DEFAULT_POINT_COUNT = 10;

    public static final float BAR_BRIGHTNESS_THUMB_OFFSET = 2.0f;

    public static final int AMB_BRIGHTNESS_MIN_LEVEL = 1;

    public static final int AMB_BRIGHTNESS_MAX_LEVEL = 10;

    public static final int AMB_BRIGHTNESS_MIN_COLOR = 1;

    public static final int AMB_BRIGHTNESS_MAX_COLOR = 128;

    public static final String Amb_Light_Clothing_Link_Status = "amb_light_clothing_link_status";

    public static final String Amb_Light_Clothing_Main_Vice = "amb_light_clothing_main_vice";

    /**
     * 氛围灯色号.
     */
    public static final int AMB_VALUE_ONE = 1;

    public static final int AMB_COLOR_ECO = 87;
    public static final int AMB_COLOR_COMFORT = 128;
    public static final int AMB_COLOR_SPORT = 46;
    public static final int AMB_COLOR_ALL_ROAD = 62;
    public static final int AMB_COLOR_SNOW = 113;
    public static final int AMB_COLOR_CUSTOM = 3;

    public static final int AMB_COLOR_COOL = 1;
    public static final int AMB_COLOR_NEUTRAL = 74;
    public static final int AMB_COLOR_WARM = 2888;

    public static final int AMB_THEME_OCEAN = 68;
    public static final int AMB_THEME_XIA_LIGHT = 110;
    public static final int AMB_THEME_SUNLIGHT = 118;
    public static final int AMB_THEME_MYSTERY = 42;
    public static final int AMB_THEME_FOREST = 60;

    public static final int FOG_LAMPS_INVALID = 255;


    /**
     * 智能驾驶.
     */
    public static final int WARING_FCW_TYPE = 0;

    public static final int WARING_RCW_TYPE = 1;

    public static final int FCW_DIALOG_WIDTH = 1320;

    public static final int FCW_DIALOG_HEIGHT = 420;

    public static final int OFFSET_CONTROL_PERCENT = 0;

    public static final int OFFSET_CONTROL_NUM = 1;

    public static final int MIN_VALUE = -10;

    public static final int INIT_VALUE = 0;

    public static final int MAX_VALUE = 10;

    public static final int MIN_PROGRESS = 0;

    public static final int MAX_PROGRESS = 20;

    public static final int ECO_PLUS_MAX_SPEED_MAX = 130;

    public static final int ECO_PLUS_MAX_SPEED_MIN = 80;

    /**
     * 安全与维护.
     */
    public static final int LIMIT_SPEED = 10;

    public static final String FUNCTION_EFFECT = "function_effect";

    public static final String CAR_SOUND_FIELD = "car_sound_field";

    /**
     * adas status.
     */
    public static final String ADAS_CV_ADAS_STATUS = "cv_adas_status";
    public static final int ADAS_CV_ADAS_STATUS_CLOSE = 0;
    public static final int ADAS_CV_ADAS_STATUS_OPEN = 1;

    public static final String PAIR_CLICK_STATUS = "pari_click_status";
    public static final int PAIR_CLICK_STATUS_CLOSE = 0;
    public static final int PAIR_CLICK_STATUS_OPEN = 1;

    public static final String OPEN_BT_DIALOG = "OPEN_BT_DIALOG";
    public static final int OPEN_BT_DIALOG_HIDE = 0;
    public static final int OPEN_BT_DIALOG_SHOW = 1;
    public static final int OPEN_BT_DIALOG_SHOW_PAIR_SUCCESS = 2;
    public static final int OPEN_BT_DIALOG_SHOW_PAIR_FAIL = 3;

    public static final String BOND_STATE_DEVICE = "BOND_STATE_DEVICE";
    public static final int BOND_STATE_DEVICE_CONNECT = 0;
    public static final int BOND_STATE_DEVICE_CONNECTED_NO_DEVICE = 1;
    public static final int BOND_STATE_DEVICE_CONNECTED_TWO_DEVICE = 2;
    public static final int BOND_STATE_DEVICE_CONNECTED_TWO_DEVICE_DISCONNECT = 3;
}
