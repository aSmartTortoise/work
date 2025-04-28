package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 语音音区
 */
@IntDef({VoiceLocation.FRONT_LEFT,
        VoiceLocation.FRONT_RIGHT,
        VoiceLocation.REAR_LEFT,
        VoiceLocation.REAR_RIGHT,
        VoiceLocation.THIRD_ROW_LEFT,
        VoiceLocation.THIRD_ROW_RIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface VoiceLocation {

    int FRONT_LEFT = 0;
    int FRONT_RIGHT = 1;
    int REAR_LEFT = 2;
    int REAR_RIGHT = 3;
    int THIRD_ROW_LEFT = 4;
    int THIRD_ROW_RIGHT = 5;

}
