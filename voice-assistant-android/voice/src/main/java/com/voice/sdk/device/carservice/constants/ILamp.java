package com.voice.sdk.device.carservice.constants;

import androidx.annotation.IntDef;

/**
 * @Date 2024/8/1 10:32
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface ILamp {

    interface IMode {
        int DEFAULT = 0; //默认
        int OFF = 1; //关闭
        int POSITION = 2; //小灯, 位置灯
        int LOW = 3; //近光灯
        int AUTO = 4; //自动灯光
        int INVALID = 255; //无效
    }
    //近光灯的下发信号值  write
    interface ISetting{
//        int PASSED = 1;
//        int NO_PASSED = 2;
        int AUTO = 1;
        int LOW = 2;
        int POSITION = 3;
        int OFF =4;
    }
    /**
     * 0x0：AUTO灯软开关lighted
     * 0x1：近光灯软开关lighted
     * 0x2：位置灯软开关lighted
     * 0x3：OFF灯光灯软开关lighted
     */
    interface IModes {//用来read灯光信号状态的
        int AUTO = 0; //自动灯光
        int LOW = 1; //近光灯
        int POSITION = 2; //小灯, 位置灯
        int OFF = 3; //关闭
        int DEFAULT = 0; //默认
        int INVALID = 255; //无效
    }

    //阅读灯专用开关   1是关  2是开
    @IntDef(value = {LightReadSwitch.ON, LightReadSwitch.OFF})
    @interface LightReadSwitch {
        int ON = 2;
        int OFF = 1;
    }

}
