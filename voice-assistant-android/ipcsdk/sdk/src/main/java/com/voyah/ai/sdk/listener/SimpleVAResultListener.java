package com.voyah.ai.sdk.listener;

import com.voyah.ai.sdk.bean.NluResult;

/**
 * 语音结果监听器
 */
public class SimpleVAResultListener implements IVAResultListener {

    /**
     * asr流式结果
     */
    public void onAsr(String text, boolean isEnd) {
    }

    /**
     * nlu结果
     */
    public void onNluResult(NluResult result) {
    }

    /**
     * 快捷命令结果
     */
    public void onShortCommand(String command, NluResult result) {
    }

    /**
     * 可见即可说命令回调
     */
    public void onViewCommand(String tag, NluResult result) {
    }
}
