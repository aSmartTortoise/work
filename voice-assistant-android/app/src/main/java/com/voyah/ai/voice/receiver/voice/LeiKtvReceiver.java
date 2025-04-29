package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author:lcy
 * @data:2024/10/14
 **/
public class LeiKtvReceiver extends BroadcastReceiver {
    private static final String TAG = LeiKtvReceiver.class.getSimpleName();

    private static final String ACTION = "com.thunder.carplay.broadcast.NOTIFY_FEATURE";
    private static final String EXTRA_KEY_TYPE = "type";
    private static final int FLAG_STOP_PLAY = 1; //停止播放
    private static final int FLAG_START_PLAY = 2; //开始播放
    private static final int FLAG_ENTER_KARAOKE = 3; //进入卡拉OK
    private static final int FLAG_EXIT_KARAOKE = 4; //退出卡拉OK

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (StringUtils.equals(intentAction, ACTION)) {
            int type = intent.getIntExtra(EXTRA_KEY_TYPE, 0);
            LogUtils.d(TAG, "onReceive type:" + type);
            if (type == FLAG_STOP_PLAY) {
                DeviceHolder.INS().getDevices().getVoiceCarSignal().onLeiKtvReceiveStatus(false);
            } else if (type == FLAG_ENTER_KARAOKE) {
                DeviceHolder.INS().getDevices().getVoiceCarSignal().onLeiKtvReceiveStatus(false);
            } else if (type == FLAG_EXIT_KARAOKE) {
                DeviceHolder.INS().getDevices().getVoiceCarSignal().onLeiKtvReceiveStatus(false);
            } else if (type == FLAG_START_PLAY) {
                DeviceHolder.INS().getDevices().getVoiceCarSignal().onLeiKtvReceiveStatus(true);
            }
        }
    }
}
