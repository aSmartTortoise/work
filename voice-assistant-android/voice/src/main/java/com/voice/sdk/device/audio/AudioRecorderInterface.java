package com.voice.sdk.device.audio;

public interface AudioRecorderInterface {
    void init(String vehicleType);

    boolean isAudioRecorderStart();

    void startAudioRecorder();

    void stopAudioRecorder();

    void enableSendAudio(boolean enableSend);

    void  releaseAudioRecorder();

    void addPcmAudioCallback(IPcmAudioCallback callBack);

    void removePcmAudioCallback(IPcmAudioCallback callBack);
}
