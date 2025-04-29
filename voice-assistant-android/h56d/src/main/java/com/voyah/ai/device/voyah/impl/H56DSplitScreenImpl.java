package com.voyah.ai.device.voyah.impl;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voyah.ai.basecar.system.CommonSplitScreenImpl;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.device.voyah.h37.launcher.LauncherManager;

import org.apache.commons.lang3.StringUtils;


public class H56DSplitScreenImpl extends CommonSplitScreenImpl {

    public H56DSplitScreenImpl() {
        super();
    }

    @Override
    public boolean isNeedSplitScreen() {
        return DeviceHolder.INS().getDevices().getCarServiceProp().getDrvInfoGearPosition() == GearInfo.CARSET_GEAR_DRIVING;
    }

    @Override
    public boolean isSplitScreening() {
        int drvInfoGearPosition = DeviceHolder.INS().getDevices().getCarServiceProp().getDrvInfoGearPosition();
        return drvInfoGearPosition == 3 || LauncherManager.getInstance().isDriveDesktop();
    }

    @Override
    public boolean isSupportDriveDesktop() {
        return true;
    }

    @Override
    public void onSplitStatusChanged(boolean isSplitScreen) {
        super.onSplitStatusChanged(isSplitScreen);
        // 56D特殊处理（可见SDK通过system.settings监听失效, 逐渐过度到在语音侧触发应用重新扫描）
        int mainScreenId = DeviceHolder.INS().getDevices().getSystem().getScreen().getMainScreenDisplayId();
        DeviceHolder.INS().getDevices().getViewCmd().triggerViewCmdScan(Utils.getApp().getPackageName(), mainScreenId, false);
    }

    @Override
    protected boolean usingFreeForm() {
        return true;
    }
}
