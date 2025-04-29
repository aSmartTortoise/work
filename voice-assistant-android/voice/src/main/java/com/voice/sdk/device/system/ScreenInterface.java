package com.voice.sdk.device.system;

import android.content.Context;
import android.os.UserHandle;

import com.voyah.ai.sdk.bean.DhScreenType;

public interface ScreenInterface {

    boolean isSupportMultiScreen();

    boolean isSupportScreen(DeviceScreenType deviceScreenType);

    boolean isCeilScreenOpen();

    void openCeilScreen(int maxWaitTime);

    void onCeilOpen(Runnable runnable);

    int getDisplayId(DeviceScreenType deviceScreenType);

    UserHandle getUserHandle(DeviceScreenType deviceScreenType);

    Context getScreenContext(DeviceScreenType deviceScreenType);

    int getMainScreenDisplayId();

    int getPassengerScreenDisplayId();

    int getCeilingScreenDisplayId();

    int getCurVpaDisplayId();

    int getVoiceDisplayId(int direction);

    int getScreenType(String position);

    void requestScreenOnOff(int displayId, boolean isOn);

    boolean isScreenOn(int displayId);

    int getScreenOnOffState(int displayId);

    int getDisplayId(@DhScreenType int screenType);
}
