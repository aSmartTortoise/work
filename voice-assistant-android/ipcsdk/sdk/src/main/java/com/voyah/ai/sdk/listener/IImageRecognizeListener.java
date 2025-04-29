package com.voyah.ai.sdk.listener;

public interface IImageRecognizeListener {

    /**
     * 图像识别结果回调
     * @param sessionId 会话id
     * @param screenType 屏幕类型，参考@DhScreenType
     * @param state 回调状态
     * @param msg 回调说明
     */
    void onIRResult(String sessionId, int screenType, int state, String msg);
}
