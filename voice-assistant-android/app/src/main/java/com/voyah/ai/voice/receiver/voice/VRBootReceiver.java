package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.ai.voice.VRService;

import org.apache.commons.lang3.StringUtils;


/**
 * @author:lcy 系统拉起服务广播
 * @data:2024/1/20
 **/
public class VRBootReceiver extends BroadcastReceiver {
    private static final String action = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("onReceive() called");
        if (StringUtils.equals(action, intent.getAction()))
            VRService.startService(context);
    }
}
