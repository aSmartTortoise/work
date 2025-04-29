package com.voice.sdk.device.viewcmd;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 分屏
 */
@IntDef({SplitScreenId.FULL_SCREEN, SplitScreenId.LEFT_SCREEN, SplitScreenId.RIGHT_SCREEN})
@Retention(RetentionPolicy.SOURCE)
public @interface SplitScreenId {
    int FULL_SCREEN = 10;
    int LEFT_SCREEN = 11;
    int RIGHT_SCREEN = 12;
}
