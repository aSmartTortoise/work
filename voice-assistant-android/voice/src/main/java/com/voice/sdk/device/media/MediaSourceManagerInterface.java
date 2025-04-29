package com.voice.sdk.device.media;

public interface MediaSourceManagerInterface {
    String getMeidaPlayingSource(String soundLocation);

    boolean isPlayingAllScreen(String soundLocation);

    String getMeidaFrontSource();

    String getMeidaPlaypageSource();
}
