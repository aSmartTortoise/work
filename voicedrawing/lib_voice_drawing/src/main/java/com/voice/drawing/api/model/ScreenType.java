package com.voice.drawing.api.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 屏幕类型
 */
@IntDef({ScreenType.ALL,
        ScreenType.MAIN,
        ScreenType.PASSENGER,
        ScreenType.CEILING})
@Retention(RetentionPolicy.SOURCE)
public @interface ScreenType {

    int ALL = -1;
    int MAIN = 0;
    int PASSENGER = 1;
    int CEILING = 2;

}
