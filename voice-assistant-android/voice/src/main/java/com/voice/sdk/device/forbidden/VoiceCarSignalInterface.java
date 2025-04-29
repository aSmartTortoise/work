package com.voice.sdk.device.forbidden;

/**
 * @author:lcy
 * @data:2025/3/5
 **/
public interface VoiceCarSignalInterface {
    void init();

    void showVolumeToast(String screenName);

    void nearbyTtsStatusChange(boolean enable);

    boolean getEnableAudioRecord();

    boolean isVoiceForbidden();

    void deviceForbiddenStatus(String source, int code);

    void onLeiKtvReceiveStatus(boolean isLsPlay);

    void showForbiddenToast(int type, String screenName);

    void showForbiddenToast(int type, int location);

    boolean isParkingLimitation();
}
