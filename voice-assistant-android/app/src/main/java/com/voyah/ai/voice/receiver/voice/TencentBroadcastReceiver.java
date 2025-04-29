package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;

public class TencentBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("TencentBroadcastReceiver", "onReceive");
        DeviceHolder.INS().getDevices().getMediaCenter().gestureTencent();
    }
}
