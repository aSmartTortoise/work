package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/8/1 14:13
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class ReadingLightSignal {
    public static final String READING_LIGHT_SWITCH = "reading_Switch";

    public static final String DOME_LIGHT_SWITCH = "domeLight_Switch";

    public static final String INLIGHT_THIRDROWLHREADLIGHTREQ = "inLight_ThirdRowlhReadLightReq";//H56c H56D 专用  第三排左侧 阅读write信号

    public static final String INLIGHT_THIRDROWRHREADLIGHTREQ = "inLight_ThirdRowrhReadLightReq";//H56c H56D 专用  第三排右侧 阅读write信号

    public static final String INLIGHT_FLREADLIGHTSTS = "inLight_flReadLightSts";//主驾阅读灯read信号

    public static final String INLIGHT_FRREADLIGHTSTS = "inLight_frReadLightSts";//副驾阅读灯read信号

    public static final String INLIGHT_RLREADLIGHTSTS = "inLight_rlReadLightSts";//二排左阅读灯read信号

    public static final String INLIGHT_RRREADLIGHTSTS = "inLight_rrReadLightSts";//二排右阅读灯raad信号

    public static final String INLIGHT_THIRDROWLHREADLIGHTSTS = "inLight_thirdRowlhReadLightSts";//三排左阅读灯read信号

    public static final String INLIGHT_THIRDROWRHREADLIGHTSTS = "inLight_thirdRowrhReadLightSts";//三排右阅读灯raad信号

}
