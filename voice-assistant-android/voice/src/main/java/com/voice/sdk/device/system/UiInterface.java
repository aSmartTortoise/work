package com.voice.sdk.device.system;

public interface UiInterface {
    void showSystemToast(DeviceScreenType screenType,String content);

    void showSystemToast(DeviceScreenType screenType,String content,int type);
}
