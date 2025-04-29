package com.voice.sdk.device.audio;

public interface IPcmAudioCallback {
    /**
     * 录音机音频
     *
     * @param audio
     */
    void onPcmInAudio(byte[] audio);
}
