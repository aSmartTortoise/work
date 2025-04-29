package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/29 10:51
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class HudSignal {
    public static final String HUD_SUPPORT = "hud_support"; //是否支持HUD
    public static final String HUD_SWITCH = "hud_MainSwitch"; //HUD主开关
    public static final String HUD_MODE = "hud_Mode"; //HUD 模式
    public static final String HUD_SNOW_MODE_SWITCH = "hud_SnowModeSwitch"; //雪地模式，高对比度模式
    public static final String HUD_AUTO_LIGHT = "hud_AutoLight"; //自动亮度
    public static final String HUD_MANUAL_LIGHT = "hud_ManualLight"; //手动亮度
    public static final String HUD_AUTO_HEIGHT = "hud_AutoHeight"; //自动高度
    public static final String HUD_MANUAL_HEIGHT = "hud_ManualHeight"; //手动高度
    public static final String HUD_PRIVACY_MODE = "hud_PrivacyMode"; //隐私模式，只读
    public static final String HUD_TEMPERATURE = "hud_Temperature"; //温度预警
    public static final String HUD_MODE_JAD = "hud_mode_jad"; //路口放大
    public static final String HUD_MODE_TLC = "hud_mode_tlc"; //红绿灯倒计时
    public static final String HUD_MODE_CTD = "hud_mode_ctd"; //当前时间
    public static final String HUD_MODE_MSD = "hud_mode_msd"; //多媒体信息
    public static final String HUD_HEIGHT_UPLOAD = "hud_height_upload"; //刷新一次高度，不用上虚拟车
}
