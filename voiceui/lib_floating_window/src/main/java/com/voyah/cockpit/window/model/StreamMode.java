package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 流的状态
 */
@IntDef({StreamMode.NOT_STREAM,
        StreamMode.START,
        StreamMode.ON_GOING,
        StreamMode.COMPLETION})
@Retention(RetentionPolicy.SOURCE)
public @interface StreamMode {

    int NOT_STREAM = -1;
    int START = 0;
    int ON_GOING = 1;
    int COMPLETION = 2;

}
