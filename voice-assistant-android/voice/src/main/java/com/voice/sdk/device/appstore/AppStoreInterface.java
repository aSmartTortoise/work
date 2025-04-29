package com.voice.sdk.device.appstore;

import com.voice.sdk.device.system.DeviceScreenType;

public interface AppStoreInterface {

    void init();

    void openAppStore(DeviceScreenType screenType);

    void closeAppStore(DeviceScreenType screenType);

    void openAppStorePage(DeviceScreenType screenType, int page);

    void openAppStorePage(DeviceScreenType screenType, int page, String tabName);

    boolean isInFront(DeviceScreenType screenType);

    int getCurrentPage(DeviceScreenType screenType);

    boolean isInAppStore(String appName);

    int searchApp(DeviceScreenType screenType, String appName);

    int updateApp(DeviceScreenType screenType, String appName);

    int uninstallApp(DeviceScreenType screenType, String appName);

    int downLoadApp(DeviceScreenType screenType, String appName);

    boolean isSuccessCode(int code);
}
