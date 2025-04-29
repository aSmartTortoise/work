package com.voyah.ai.sdk.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 长识别返回错误码定义
 */
@IntDef({LongAsrEC.NORMAL, LongAsrEC.NO_NET, LongAsrEC.INPUT_TIMEOUT, LongAsrEC.INTERRUPT, LongAsrEC.CLOUD_ERR})
@Retention(RetentionPolicy.SOURCE)
public @interface LongAsrEC {
    int NORMAL = 0;   //正常结束
    int NO_NET = -1;  //网络异常
    int INPUT_TIMEOUT = -2;  //无音频输入超时
    int INTERRUPT = -3;  //被打断
    int CLOUD_ERR = -4;  //云端异常
}