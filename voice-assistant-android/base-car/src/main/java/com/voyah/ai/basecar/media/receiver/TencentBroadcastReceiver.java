package com.voyah.ai.basecar.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.SPUtil;

public class TencentBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = TencentBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean playState = intent.getBooleanExtra("isPlaying", false);
        int pid = intent.getIntExtra("uid", -1);
        LogUtils.d(TAG, "playState: " + playState + ", uid = " + pid);
        if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getCeilingScreenDisplayId()).hashCode() == pid / MediaHelper.PER_USER_RANGE) {
            TencentVideoImpl.INSTANCE.setPlayingC(playState);
        } else if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()).hashCode() == pid / MediaHelper.PER_USER_RANGE) {
            TencentVideoImpl.INSTANCE.setPlayingP(playState);
        } else {
            TencentVideoImpl.INSTANCE.setPlaying(playState);
        }
        SPUtil.putBoolean(context, MediaHelper.IS_TENCENT_PLAY, (VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getCeilingScreenDisplayId())
                || VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getPassengerScreenDisplayId())
                || VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getMainScreenDisplayId())));
        LogUtils.d(TAG, "TencentVideoCeilingImpl: " + VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getCeilingScreenDisplayId())
                + ", TencentVideoCopilotImpl = " + VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getPassengerScreenDisplayId())
                + ", TencentVideoImpl = " + VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME, MediaHelper.getMainScreenDisplayId()));
    }
}
