package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/9/6 11:03
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class AtmosphereSignal {
    public static final String ATMO_CONFIG = "atmo_Config"; //氛围灯配置[0无配置，1多色流水氛围灯，2氛围灯-64色，3多色氛围灯-128色（非流水）]
    public static final String ATMO_SWITCH_STATE = "atmo_SwitchState"; //氛围灯开关
    //语音工程用int，数据库用string，在虚拟车operator里做映射
    public static final String ATMO_ACTION_MODE = "atmo_ActionMode"; //氛围灯模式(静态或者动态, 单色或者多色)组合[StaticSingleColor,StaticMultiColor,DynamicSingleColor,DynamicMultiColor]
    //用于记忆上次选中
    public static final String ATMO_LAST_STATIC_MODE = "atmo_LastStaticMode"; //氛围灯上次切到静态时是单色还是多色
    public static final String ATMO_LAST_DYNAMIC_MODE = "atmo_LastDynamicMode"; //氛围灯上次切到动态是单色还是多色
    public static final String ATMO_IS_STATIC = "atmo_IsStatic"; //仅语音工程使用，提取ATMO_ACTION_MODE里的静态动态属性。

    //语音工程用int，数据库用string，在虚拟车operator里做映射
    public static final String ATMO_STATIC_SINGLE_COLOR = "atmo_StaticSingleColor"; //静态单色[红色,橙色,黄色,绿色,紫色,粉色,蓝色,青色]
    public static final String ATMO_STATIC_MULTI_COLOR = "atmo_StaticMultiColor"; //静态多色[中性色,暖色,组合色,冷色]
    public static final String ATMO_DYNAMIC_SINGLE_COLOR = "atmo_DynamicSingleColor"; //动态单色[红色,橙色,黄色,绿色,紫色,粉色,蓝色,青色]
    public static final String ATMO_DYNAMIC_MULTI_COLOR = "atmo_DynamicMultiColor"; //动态多色[中性色,暖色,组合色,冷色]

    public static final String ATMO_STATIC_BRIGHTNESS = "atmo_StaticBrightness"; //静态亮度[1,10]
    public static final String ATMO_DYNAMIC_BRIGHTNESS = "atmo_DynamicBrightness"; //动态亮度[1,10]
    public static final String ATMO_EFFECT_MODE = "atmo_EffectMode"; //灯效模式
    public static final String ATMO_IS_FOLLOW_MUSIC_SWITCH = "atmo_IsFollowMusicSwitch"; //音乐律动是否是开关的执行
    public static final String ATMO_FOLLOW_MUSIC_SWITCH_STATE = "atmo_FollowMusicSwitchState"; //音乐律动开关状态
    public static final String ATMO_CENTER_SPEAKER_SWITCH_STATE = "atmo_CenterSpeakerSwitchState"; //中置扬声器开关状态
    public static final String ATMO_ATMOSPHERE_PAGE_STATE = "atmo_AtmospherePageState"; //氛围灯页面状态
    public static final String ATMO_ATMOSPHERE_THEME_COLOR = "atmo_AtmosphereThemeColor"; //氛围灯主题色
}
