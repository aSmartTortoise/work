package com.voice.drawing.api.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 流的状态
 */
@IntDef({
        DrawingState.START,
        DrawingState.ON_GOING,
        DrawingState.COMPLETION,
        DrawingState.FAIL,
        DrawingState.TIMEOUT})
@Retention(RetentionPolicy.SOURCE)
public @interface DrawingState {

    int START = 0;
    int ON_GOING = 1;
    int COMPLETION = 2;
    int FAIL = 3;
    int TIMEOUT = 4;

}
