package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/8/12 14:37
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface ITailGate {

    int TAILGATE_FULLYCLOSED = 0; //全关
    int TAILGATE_FULLYOPENED = 1; //全开
    int TAILGATE_OPENING = 2; //打开中
    int TAILGATE_CLOSING = 3; //关闭中
    int TAILGATE_STOPPED = 4; //停止
    int TAILGATE_LATCHRELEASING = 5; //解锁中
    int TAILGATE_LATCHCINCHING = 6; //扣锁中
    int TAILGATE_RESERVED = 7; //
    int TAILGATE_INVALID = 255; //
}
