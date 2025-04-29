package com.voice.sdk.device.system;

public interface VolumeInterface {

    void setMuted(VolumeStream stream, boolean muted, int minVolume);

    boolean isMuted(VolumeStream module);

    int getVolume(VolumeStream module);

    void setVolume(VolumeStream module, int volume);

    int getStreamValue(VolumeStream volumeStream);
}
