package com.voice.sdk.device.carservice.constants;

import androidx.annotation.IntDef;

/**
 * @Date 2024/7/22 10:53
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface ICommon {

    /**
     * 通用开关状态，1代表开，0代表关
     */
    @IntDef(value = {Switch.ON, Switch.OFF})
    @interface Switch {
        int ON = 1;
        int OFF = 0;
    }


    @IntDef(value = {
            Level.INVALID, Level.LOW, Level.MID, Level.HIGH, Level.OFF
    })
    @interface Level {
        int INVALID = -1;
        int OFF = 0;
        int LOW = 1;
        int MID = 2;
        int HIGH = 3;
        int AUTO = 99;
    }

}
