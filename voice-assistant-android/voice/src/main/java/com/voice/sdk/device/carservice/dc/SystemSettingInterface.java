package com.voice.sdk.device.carservice.dc;

public interface SystemSettingInterface {
    int getInt(String type, String key, int defaultState);

    void putInt(String type, String key, int value);

    String getString(String type, String key);

    void openUpLogPage();
}
