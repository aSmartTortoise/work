package com.voyah.ai.sdk.listener;

public interface IRmsDbListener {
    /**
     * 音量大小回调
     *
     * @param rms rms
     */
    void onRmsChanged(float rms);
}
