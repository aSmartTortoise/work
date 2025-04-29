package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/26 10:30
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class ScreenSignal {
    public static final String SCREEN_POS = "screen_Pos"; //滑移屏位置（读写不同信号）
    public static final String SCREEN_MOVE_STATE = "screen_MoveState"; //滑移屏移动状态
    public static final String SCREEN_ABNORMAL_STATE = "screen_AbnormalState"; //滑移屏卡住状态
    public static final String SCREEN_BRIGHTNESS = "screen_Brightness"; // 中控屏亮度
    public static final String SCREEN_INSTRUMENT_BRIGHTNESS = "screen_InstrumentBrightness"; //仪表屏亮度
    public static final String SCREEN_PASSENGER_BRIGHTNESS = "screen_PassengerBrightness"; // 副驾屏亮度
    public static final String SCREEN_CEILING_BRIGHTNESS = "screen_CeilingBrightness"; //吸顶屏亮度
    public static final String SCREEN_POWER_OFF_RESET = "screen_PowerOffReset"; //下电复位
    public static final String SCREEN_ON_OFF = "screen_OnOff"; //亮屏熄屏

    public static final String SCREEN_CLEAN_MODE_SWITCH = "screen_CleanModeSwitch"; //清洁模式开关
    public static final String SCREEN_TYPE = "screen_type"; //屏幕类型，是否lcd屏
    public static final String SCREEN_LOCK_CONFIG = "screen_lock_config"; //是否支持扶手屏
    public static final String SCREEN_LOCK_RL = "screen_lock_rl"; //扶手屏左
    public static final String SCREEN_LOCK_RR = "screen_lock_rr"; //扶手屏右
    public static final String SCREEN_LOCK_STATE = "screen_lock_state"; //扶手屏开关状态
    public static final String SCREEN_CEILING_ANGLE = "screen_ceiling_angle"; //吸顶屏角度
    public static final String SCREEN_CENTER_AUTO = "screen_center_auto"; //中控自动亮度开关，打开/关闭
    public static final String SCREEN_INSTRUMENT_AUTO = "screen_instrument_auto"; //仪表自动亮度开关0/1，打开/关闭
    public static final String SCREEN_PASSENGER_AUTO = "screen_passenger_auto"; //副驾自动亮度开关0/1，打开/关闭
    public static final String SCREEN_CEIL_LOCK = "screen_ceil_lock"; //吸顶屏锁定
}
