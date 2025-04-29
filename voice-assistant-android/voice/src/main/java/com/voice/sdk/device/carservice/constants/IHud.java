package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/7/29 16:40
 * @Author 8327821
 * @Email *
 * @Description HUD业务使用信号
 **/
public interface IHud {
    int HUD_SWITCH = 1; //0&1
    int HUD_SNOW_MODE = 7; //0&1
    int AUTO_BRIGHTNESS = 8; //0&1
    int AUTO_HEIGHT = 10; //0&1
    int MANUAL_HEIGHT = 12; //0-100
    int MANUAL_BRIGHTNESS = 17; //0-100
    int TEMPERATURE = 18; //0正常 1过热
    int HUD_MODE = 21;
    int PRIVACY_MODE = 22; //仅get, 1开启0关闭

    interface HudMode {
        int SIMPLEST = 1;
        int NAVI = 2;
        int INTELLIGENT_DRIVING = 3;
        int STANDARD = 4;
        int AR = 5;
    }

}
