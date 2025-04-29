package com.voyah.ai.basecar.navi;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviScreenInterface;
import com.voyah.ai.common.utils.LogUtils;

public class NaviScreenInterfaceImpl implements NaviScreenInterface {
    private static final String TAG = "NaviScreenInterfaceImpl";

    public NaviScreenInterfaceImpl() {
    }

    @Override
    public boolean isSpiltScreen() {
        boolean isSpiltScreen = DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening();
        LogUtils.i(TAG, "isSpiltScreen:" + isSpiltScreen);
        return isSpiltScreen;
    }

    @Override
    public void enterSplitScreen() {
        LogUtils.i(TAG, "enterSplitScreen");
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().enterSpiltScreen();
    }

    @Override
    public void enterFullScreen() {
        LogUtils.i(TAG, "enterFullScreen");
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().enterFullScreen();

    }
}
