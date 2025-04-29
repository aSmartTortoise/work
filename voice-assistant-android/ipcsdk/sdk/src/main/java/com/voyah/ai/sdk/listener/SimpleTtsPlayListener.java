package com.voyah.ai.sdk.listener;

/**
 * Tts播报监听器
 */
public class SimpleTtsPlayListener implements ITtsPlayListener {

    /**
     * TTS播报开始
     *
     * @param text 播报内容
     */
    @Override
    public void onPlayBeginning(String text) {

    }

    /**
     * TTS播报结束
     *
     * @param text   播报内容
     * @param reason 结束原因
     */
    @Override
    public void onPlayEnd(String text, @REASON int reason) {

    }

    /**
     * TTS播报错误
     *
     * @param text  播报内容
     * @param errId 错误信息
     */
    @Override
    public void onPlayError(String text, int errId) {
        onPlayEnd(text, errId);
    }
}
