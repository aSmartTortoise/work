package com.voyah.ai.sdk.listener;

import com.voyah.ai.sdk.bean.LongAsrEC;

public interface ILongASRListener {
    /**
     * 识别结果回调
     *
     * @param text asr结果
     */
    void onRecognize(String text);

    /**
     * 识别结束
     *
     * @param errCode 错误码
     * @param reason  说明
     */
    void onEnd(@LongAsrEC int errCode, String reason);
}
