package com.voyah.ai.sdk;

interface IRmsDbCallback {
    /**
     * 音量大小回调
     *
     * @param rms 能量大小
     */
   void onRmsChanged(float rms);
}
