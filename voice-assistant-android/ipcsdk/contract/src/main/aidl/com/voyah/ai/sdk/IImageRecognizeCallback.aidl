package com.voyah.ai.sdk;

interface IImageRecognizeCallback {
    /**
     * 图像识别结果回调
     */
    void onIRResult(String sessionId, int screenType, int state, String msg);
}

