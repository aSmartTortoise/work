package com.voyah.ai.sdk;

interface IVAResultCallback {
    /**
     * 语音实时识别结果
     *
     * @param text  交互文字内容
     * @param isEnd 是否结束
     */
    void onAsr(String text, boolean isEnd);

    /**
     * nlu结果
     */
    void onNluResult(String result);

    /**
     * 快捷命令结果
     *
     * @param command
     * @param result
     */
    void onShortCommand(String command, String result);

    /**
     * 可见即可说命令回调
     *
     * @param tag
     * @param result
     */
    void onViewCommand(String tag, String result);
}
