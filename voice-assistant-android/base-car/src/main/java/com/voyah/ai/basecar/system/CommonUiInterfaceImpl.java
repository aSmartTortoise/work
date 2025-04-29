package com.voyah.ai.basecar.system;

import com.blankj.utilcode.util.ThreadUtils;
import com.vcos.common.widgets.vcostoast.VcosToastManager;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.UiInterface;
import com.voyah.ai.common.utils.LogUtils;

import java.util.Objects;

public class CommonUiInterfaceImpl implements UiInterface {

    private static final String TAG = "UiInterfaceImpl";

    @Override
    public void showSystemToast(DeviceScreenType screenType, String content) {
        LogUtils.i(TAG, "showSystemToast:" + screenType + ",content:" + content);
        if (screenType == null) {
            screenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        DeviceScreenType finalScreenType = screenType;
        ThreadUtils.runOnUiThread(() -> Objects.requireNonNull(VcosToastManager.Companion.getInstance()).showSystemToast(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenContext(finalScreenType), content));
    }

    @Override
    public void showSystemToast(DeviceScreenType screenType, String content, int type) {
        LogUtils.i(TAG, "showSystemToast:" + screenType + ",content:" + content + ",type:" + type);
        if (screenType == null) {
            screenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        DeviceScreenType finalScreenType = screenType;
        ThreadUtils.runOnUiThread(() -> Objects.requireNonNull(VcosToastManager.Companion.getInstance()).showSystemToast(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenContext(finalScreenType), content, type));
    }
}
