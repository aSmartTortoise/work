package com.voyah.ai.sdk;

interface IVAStateCallback {
    /**
     * 语音生命周期回调
     *
     * @param state 生命周期状态
     */
    void onState(String state);
}
