package com.voice.sdk.device.system;

public interface SystemInterface {

    VolumeInterface getVolume();

    UiInterface getUi();

    AppInterface getApp();

    ScreenInterface getScreen();

    SplitScreenInterface getSplitScreen();

    AttributeInterface getAttribute();

    KeyboardInterface getKeyboard();

    ShareInterface getShare();
}
