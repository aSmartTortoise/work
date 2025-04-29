package com.voice.sdk.device.carservice.vcar;

public interface CarPropertyEventCallback {
    default boolean isSticky() {
        return true;
    }

    void onChangeEvent(CarPropertyValue var1);

    void onErrorEvent(int var1, int var2);
}
