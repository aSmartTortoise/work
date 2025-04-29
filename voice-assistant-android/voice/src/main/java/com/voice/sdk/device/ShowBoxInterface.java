package com.voice.sdk.device;

/**
 * author : jie wang
 * date : 2025/3/3 14:21
 * description :
 */
public interface ShowBoxInterface extends DomainInterface {

    int openApp();

    int closeApp();

    boolean isShowBoxTabForeground(String tabType);

    boolean openShowBoxTab(String tabType);

    boolean isShoutOutOpen();

    boolean isRecorderEnable();

    boolean openShoutOut();

    boolean isParkingGearPosition();

    boolean isMusicLightShowOpen();

    int closeMusicLightShowSwitch();




}
