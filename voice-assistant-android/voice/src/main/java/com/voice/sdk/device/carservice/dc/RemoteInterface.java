package com.voice.sdk.device.carservice.dc;

public interface RemoteInterface {

    void init();

    void register();

    int getRemoteControlAppState();

    void setRemoteControlAppClose();

    int getRemoteControlConnectState();

    void setRemoteControlDisConnect();

    int getRemoteControlMenuPageState();
}
