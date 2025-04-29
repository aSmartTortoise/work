package com.voyah.ai.basecar.system;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.system.AttributeInterface;
import com.voyah.ai.common.utils.LogUtils;

public class CommonAttributeInterfaceImpl implements AttributeInterface {

    private static final String TAG = "AttributeInterfaceImpl";


    @Override
    public void setThemeStyle(int theme) {
        LogUtils.i(TAG, "setThemeStyle:" + theme);
        DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain("SystemSetting").setIntProp(SysSettingSignal.SYS_THEME_MODE, theme);
    }

    @Override
    public int getThemeStyle() {
        int theme = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain("SystemSetting").getIntProp(SysSettingSignal.SYS_THEME_MODE);
        LogUtils.i(TAG, "getThemeStyle:" + theme);
        return theme;
    }
}
