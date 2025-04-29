package com.voice.sdk.device.system;

import android.content.Context;

public interface AppInterface {
    String getPackageName(String appName);

    boolean isInstalledByAppName(String appName);

    boolean isInstalledByPackageName(String packageName);

    String getAppVersionName(String packageName);

    long getAppVersionCode(String packageName);

    String getAppVersionName();

    long getAppVersionCode();

    boolean isSystemApp(String packageName);

    void refreshAppInfo();

    boolean openApp(String packageName, DeviceScreenType deviceScreenType);

    void closeApp(String packageName, DeviceScreenType deviceScreenType);

    boolean isAppForeGround(String pkgName, DeviceScreenType deviceScreenType);

    boolean isAppForeGround(String pkgName, int displayId);

    boolean isSupportMulti(String pkgName);

    void switchPage(String screenName, boolean switchTag, String tag, String secondTag, Object saveTag, Object map);

    void startTtsService();

    String fetchScreen(String pkgName);

    boolean isPreemptiveApp(String pkgName);

    boolean isAppSupportScreen(String pkgName, DeviceScreenType deviceScreenType);

    boolean isDrivingRestrict(String pkgName);
}
