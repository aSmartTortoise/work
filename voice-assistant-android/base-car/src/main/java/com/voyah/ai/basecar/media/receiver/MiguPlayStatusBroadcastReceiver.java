package com.voyah.ai.basecar.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.common.utils.LogUtils;

public class MiguPlayStatusBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MiguPlayStatusBroadcastReceiver.class.getSimpleName();

    public static final int PER_USER_RANGE = 100000;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean playState = intent.getBooleanExtra("isPlaying", false);
        int uid = intent.getIntExtra("uid", -1);
        LogUtils.d(TAG, "playState: " + playState + ", uid = " + uid);
        if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getCeilingScreenDisplayId()).hashCode() == uid / PER_USER_RANGE) {
            MiguImpl.INSTANCE.setPlayingC(playState);
        } else if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()).hashCode() == uid / PER_USER_RANGE) {
            MiguImpl.INSTANCE.setPlayingP(playState);
        } else {
            MiguImpl.INSTANCE.setPlaying(playState);
        }
        LogUtils.d(TAG, "MiguCeilingImpl: " + MiguImpl.INSTANCE.isPlaying(MediaHelper.getCeilingScreenDisplayId()) + ", MiguCopilotImpl = " + MiguImpl.INSTANCE.isPlaying(MediaHelper.getPassengerScreenDisplayId()) + ", MiguImpl = " + MiguImpl.INSTANCE.isPlaying(MediaHelper.getMainScreenDisplayId()));
    }
}
