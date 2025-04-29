package com.voyah.ai.sdk.listener;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tts播报监听器
 */
public interface ITtsPlayListener {

    @IntDef({REASON.FINISH, REASON.INTERRUPTED, REASON.OTHERS})
    @Retention(RetentionPolicy.SOURCE)
    @interface REASON {
        int FINISH = 0;
        int INTERRUPTED = 1;
        int OTHERS = 2;
    }

    /**
     * TTS播报开始
     *
     * @param text 播报内容
     */
    void onPlayBeginning(String text);

    /**
     * TTS播报结束
     *
     * @param text   播报内容
     * @param reason 结束原因
     */
    void onPlayEnd(String text, @REASON int reason);

    /**
     * TTS播报错误
     *
     * @param text  播报内容
     * @param errId 错误信息
     */
    void onPlayError(String text, int errId);
}
