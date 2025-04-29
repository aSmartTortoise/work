package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/8/1 10:28
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class LightSignal {
    public static final String LAMP_MODE = "lamp_Mode"; //大灯
    public static final String LAMP_FOG = "lamp_Fog"; //雾灯
    public static final String LAMP_SWITCH = "lamp_Switch"; //前照灯、大灯、近光灯 开关
    public static final String LAMP_HEIGHT = "lamp_Height"; //大灯高度
    public static final String LAMP_POSITION = "lamp_Position"; //位置灯

    public static final String LOW_BEAM="low_beam";//近光灯
    public static final String LIGHT_AUTOSWITCH="light_autoswitch";//自动灯光软开关信号
    public static final String LIGHT_POSITIONSWITCH= "light_positionswitch";//位置灯软开关信号
    public static final String LIGHT_OFFLAMP = "light_offlamp";//外灯关闭软开关信号
    public static final String LIGHR_HEIGHT ="light_height";//大灯高度

    public static final String LAMP_SETFOGREAR = "lamp_SetFogRear";//后雾灯开启信号

    public static final String LIGHT_CEILINGLEFT = "light_CeilingLeft";//左侧航空灯

    public static final String LIGHT_CEILINGRIGHT = "light_CeilingRight";//右侧航空灯
}
