package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/8/6 14:10
 * @Author 8327821
 * @Email *
 * @Description 方向盘模块常量
 **/
public interface ISteeringWheel {

    /**
     * 设置自定义按键类型.
     *    <item>"音源切换"</item>               0
     *    <item>"全景泊车"</item>               1
     *    <item>"旅拍拍照"</item>               2
     *    <item>"自拍拍照"</item>               3
     *    <item>"屏幕移动"</item>               7
     *    <item>"AR HUD显示"</item>            8
     *    <item>"对外喊话"</item>               9
     *    <item>"低速行人警示音"</item>          10
     *    <item>"静音"</item>                  11
     */
    public interface CustomType {
        int SOURCE_SWITCH = 0;
        int PARK_360 = 1;
        int TRIP_SHOOT = 2;
        int SELFIE = 3;
        int SCREEN_SHIFT = 7;
        int AR_HUD = 8;
        int SHOUT_OUT = 9;
        int LOW_SPEED_ALERT = 10;
        int MUTE = 11;
    }


    //方向盘助力模式
    public interface EpsMode {
        int COMFORT = 1;
        int SPORT = 2;
        int INVALID = 3;
    }
}
