package com.voyah.ai.voice.remote;

import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.device.DeviceHolder;

/**
 * 远程client死亡监听
 */
class SpeechCallbackRecipient implements IBinder.DeathRecipient {

    private final String packageName;

    SpeechCallbackRecipient(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void binderDied() {
        LogUtils.w(packageName + " binderDied");
        DeviceHolder.INS().getDevices().getViewCmd().setTopCoverViewShowing(packageName, -1, false);
    }
}