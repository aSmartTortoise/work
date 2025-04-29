package com.voyah.vcos.ttsservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voyah.vcos.ttsservices.utils.LogUtils;

/**
 * @author:lcy
 * @data:2024/2/8
 **/
public class TTSBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.i("TTSBootReceiver", "intent " + intent.getAction());
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            TTSService.startService(context);
        }
    }
}
