package com.voice.sdk.device.ui.listener;

/**
 * @author:lcy
 * @data:2024/3/13
 **/
public interface IUiStateListener {

    //电话、媒体卡片关闭监听
    void uiCardClose(String sessionId);

    //大模型卡片关闭监听
    default void uiModelCardClose(String requestId) {

    }

    //VPA关闭监听
    default void uiVpaClose() {

    }
}
