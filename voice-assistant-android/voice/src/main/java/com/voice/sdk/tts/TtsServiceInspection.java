package com.voice.sdk.tts;

import com.voice.sdk.device.DeviceHolder;

/**
 * @author:lcy 启动TTS服务
 * @data:2024/4/17
 **/
public class TtsServiceInspection {
    private static final String TAG = TtsServiceInspection.class.getSimpleName();

    private static final String TTS_SERVICE_CLS = "com.voyah.vcos.ttsservices.TTSService";


    private static TtsServiceInspection mTtsServiceInspection;

    private TtsServiceInspection() {
    }

    public static TtsServiceInspection getInstance() {
        if (null == mTtsServiceInspection) {
            synchronized (TtsServiceInspection.class) {
                if (null == mTtsServiceInspection) {
                    mTtsServiceInspection = new TtsServiceInspection();
                }
            }
        }
        return mTtsServiceInspection;
    }

    public void startTtsService() {
        DeviceHolder.INS().getDevices().getSystem().getApp().startTtsService();
    }

}
