package com.voyah.ai.sdk;

interface ITtsCallback {

    /**
     * TTS播报开始
     *
     * @param text 播报内容
     */
    void onTtsBeginning(String text);

    /**
     * TTS播报停止
     *
     * @param text 播报内容
     */
    void onTtsEnd(String text, int reason);

    /**
     * TTS播报异常
     *
     * @param text 播报内容
     * @param errCode 错误码
     */
    void onTtsError(String text, int errCode);

}
