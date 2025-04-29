package com.lib.common.voyah.service;


import android.content.Context;
import android.content.Intent;

/**
 * author : jie wang
 * date : 2025/3/18 11:32
 * description :
 */
public interface IVoiceService {

    void postBurialPointEvent(String eventType, String triggerMode, long time, String extraStr);

    int getDisplayId();

    void sendBroadcastToGallery(Context context, Intent intent);

    String getWakeUpWord();

    Context getDisplayContext(Context context, int displayId);
}
