package com.voyah.cockpit.window.model;

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
        APIResult.UI_NOT_FOREGROUND,
        APIResult.CARD_COLLAPSED,
        APIResult.INFORMATION_CARD_EXPAND,
        APIResult.OTHER_CARD_EXPAND,
        APIResult.VA_SHOWING,
        APIResult.VA_SHOWN,
        APIResult.VOICE_NOT_ALIVE})
@Retention(RetentionPolicy.SOURCE)
public @interface APIResult {

    int INIT = -1;
    int ERROR = 0;

    int NO_RESOURCE = 1;

    int SUCCESS = 200;

    int UI_FOREGROUND = 201;
    int UI_NOT_FOREGROUND = 202;
    int INFORMATION_CARD_EXPAND = 205;
    int OTHER_CARD_EXPAND = 206;

    int VA_SHOWING = 207;

    int VA_SHOWN = 208;

    int CARD_COLLAPSED = 401;

    int VOICE_NOT_ALIVE = 402;


}
