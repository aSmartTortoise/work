package com.voice.drawing.api;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/8/8 19:29
 * description :
 */

@IntDef({APIResult.INIT,
        APIResult.ERROR,
        APIResult.NO_RESOURCE,
        APIResult.SUCCESS,
        APIResult.UI_FOREGROUND,
        APIResult.UI_NOT_FOREGROUND})
@Retention(RetentionPolicy.SOURCE)
public @interface APIResult {

    int INIT = -1;
    int ERROR = 0;

    int NO_RESOURCE = 1;

    int SUCCESS = 200;

    int UI_FOREGROUND = 201;
    int UI_NOT_FOREGROUND = 202;
}
