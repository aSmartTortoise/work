package com.voyah.ai.basecar.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.common.utils.LogUtils;

public class TiktokBroadcastReceiver extends BroadcastReceiver {
    public static final int PER_USER_RANGE = 100000;

    @Override
    public void onReceive(Context context, Intent intent) {
//        LogUtils.d("TiktokBroadcastReceiver", "onReceive");
        int playStatus = intent.getIntExtra("playstatus", -1);
        int voyahMyID = intent.getIntExtra("voyahMyID", -1);
        LogUtils.d("TiktokBroadcastReceiver", "playStatus: " + playStatus + ", currentUid = " + Process.myUid() + ", voyahMyID = " + voyahMyID/PER_USER_RANGE);
        if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getCeilingScreenDisplayId()).hashCode() == voyahMyID / PER_USER_RANGE) {
            TiktokImpl.INSTANCE.setPlayingCeiling(playStatus == 3);
        } else if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()).hashCode() == voyahMyID / PER_USER_RANGE) {
            TiktokImpl.INSTANCE.setPlayingPassenger(playStatus == 3);
        } else {
            TiktokImpl.INSTANCE.setPlaying(playStatus == 3);
        }
        LogUtils.d("TiktokBroadcastReceiver", "TiktokCeilingImpl: " + TiktokImpl.INSTANCE.isPlaying(MediaHelper.getCeilingScreenDisplayId()) + ", TiktokCopilotImpl = " + TiktokImpl.INSTANCE.isPlaying(MediaHelper.getPassengerScreenDisplayId()) + ", TiktokImpl = " + TiktokImpl.INSTANCE.isPlaying(MediaHelper.getMainScreenDisplayId()));
    }
}
