package com.voyah.ai.basecar.system;


import android.content.Context;
import android.os.UserHandle;

import com.blankj.utilcode.util.Utils;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voyah.cockpit.window.model.ScreenType;

public class CommonSingleScreenInterfaceImpl extends BaseScreenInterfaceImpl implements ScreenInterface {

    private static final String TAG = "SingleScreenInterface";

    public CommonSingleScreenInterfaceImpl() {
        super();
    }


    @Override
    public boolean isSupportMultiScreen() {
        return false;
    }

    @Override
    public boolean isSupportScreen(DeviceScreenType deviceScreenType) {
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        if (deviceScreenType == DeviceScreenType.CENTRAL_SCREEN) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCeilScreenOpen() {
        return false;
    }

    @Override
    public void openCeilScreen(int maxWaitTime) {

    }

    @Override
    public void onCeilOpen(Runnable runnable) {

    }

    @Override
    public int getDisplayId(DeviceScreenType deviceScreenType) {
        return 0;
    }

    @Override
    public UserHandle getUserHandle(DeviceScreenType deviceScreenType) {
        return MegaUserHandle.SYSTEM;
    }

    @Override
    public Context getScreenContext(DeviceScreenType deviceScreenType) {
        return Utils.getApp();
    }

    @Override
    public int getMainScreenDisplayId() {
        return 0;
    }

    @Override
    public int getPassengerScreenDisplayId() {
        return 0;
    }

    @Override
    public int getCeilingScreenDisplayId() {
        return 0;
    }

    @Override
    public int getCurVpaDisplayId() {
        return 0;
    }

    @Override
    public int getVoiceDisplayId(int direction) {
        return 0;
    }

    @Override
    public int getScreenType(String position) {
        return ScreenType.MAIN;
    }

    @Override
    public int getDisplayId(int screenType) {
        return 0;
    }
}
