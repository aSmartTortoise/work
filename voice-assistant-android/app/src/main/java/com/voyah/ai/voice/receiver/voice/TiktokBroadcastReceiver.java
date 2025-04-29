package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;

public class TiktokBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        LogUtils.d("TiktokBroadcastReceiver", "onReceive");
        int playStatus = intent.getIntExtra("playstatus", -1);
        LogUtils.d("TiktokBroadcastReceiver", "playStatus: " + playStatus);
//        TiktokImpl.INSTANCE.setPlaying(playStatus == 3);
        DeviceHolder.INS().getDevices().getMediaCenter().setTiktokPlayStatus(playStatus == 3);
    }
}
