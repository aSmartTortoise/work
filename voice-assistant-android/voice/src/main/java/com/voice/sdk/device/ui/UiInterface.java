package com.voice.sdk.device.ui;


import com.voice.sdk.device.ui.listener.IUiStateListener;

/**
 * @author:lcy
 * @data:2024/3/8
 **/
public interface UiInterface {

    void init();

    /**
     * closeCard
     */
    void closeCard();

    void setTypeWriterText(String text, int typeTextStyle);

    void setUiStateListener(IUiStateListener listener);


    void startInformationCardCountDown(long delayTime);

    void removeCountDown();

    void removeInformationCountDown();

    void removeInformationCardCountDown();

    void scrollAssignPage(int indexPage, int screenType);

    int getCurrentPage(int screenType);

    int scrollPage(int direction, int screenType);

    int getMaxItemCount(int screenType);

    void setVoiceMode(int voiceMode);

    void setLanguageType(int languageType);

    void setLanguageType(String languageType);

    /**
     * 信息卡片可见滑动时 卡片延时关闭处理
     *
     * @param type 触发类型 0:指令 1:手势
     */
    void delayInformationCardCountDown(int type);

    boolean isCardShowAndRegisterViewCmd(int displayId);

    boolean isInFormationCardShow();

}
