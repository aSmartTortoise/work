package com.voyah.ai.sdk.listener;

import com.voyah.ai.sdk.bean.NluResult;

public interface IVAResultListener {

    /**
     * asr流式结果
     */
    void onAsr(String text, boolean isEnd);

    /**
     * nlu结果
     */
    void onNluResult(NluResult result);

    /**
     * 快捷命令结果
     */
    void onShortCommand(String command, NluResult result);

    /**
     * 可见即可说命令回调
     */
    void onViewCommand(String tag, NluResult result);
}
