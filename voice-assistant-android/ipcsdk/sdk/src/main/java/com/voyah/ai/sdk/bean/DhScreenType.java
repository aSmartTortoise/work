package com.voyah.ai.sdk.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * voyah屏幕类型
 */
@IntDef({DhScreenType.SCREEN_MAIN, DhScreenType.SCREEN_PASSENGER, DhScreenType.SCREEN_INSTRUMENT,
        DhScreenType.SCREEN_VEHICLE, DhScreenType.SCREEN_HUD, DhScreenType.SCREEN_CEILING,
        DhScreenType.SCREEN_LEFT_REAR, DhScreenType.SCREEN_RIGHT_REAR, DhScreenType.SCREEN_MID_REAR})
@Retention(RetentionPolicy.SOURCE)
public @interface DhScreenType {
    int SCREEN_MAIN = 0;   //中控屏
    int SCREEN_PASSENGER = 1;  //副驾屏
    int SCREEN_INSTRUMENT = 2;    //仪表屏
    int SCREEN_VEHICLE = 3;    //车控屏
    int SCREEN_HUD = 4;    //HUD屏
    int SCREEN_CEILING = 5; //吸顶屏
    int SCREEN_LEFT_REAR = 6; //后排左侧屏
    int SCREEN_RIGHT_REAR = 7; //后排右侧屏
    int SCREEN_MID_REAR = 8; //后排中间屏

}