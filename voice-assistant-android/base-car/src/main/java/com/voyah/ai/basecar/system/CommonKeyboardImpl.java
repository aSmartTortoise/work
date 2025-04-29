package com.voyah.ai.basecar.system;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.KeyboardInterface;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;

public class CommonKeyboardImpl implements KeyboardInterface {

    /**
     * 判断输入法软键盘是否显示状态
     */
    @Override
    public boolean isKeyboardShowing(int displayId) {
        if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
            // 0-关闭 1-主屏 2-副屏 3-吸顶盘
            int showState = Settings.Global.getInt(Utils.getApp().getContentResolver(), "keyboard_show_state", 0);
            if (showState == 1 && displayId == MegaDisplayHelper.getMainScreenDisplayId()) {
                return true;
            } else if (showState == 2 && displayId == MegaDisplayHelper.getPassengerScreenDisplayId()) {
                return true;
            } else
                return showState == 3 && displayId == MegaDisplayHelper.getCeilingScreenDisplayId();
        } else {
            int showState = Settings.Global.getInt(Utils.getApp().getContentResolver(), "keyboard_show_state", 0);
            return showState == 1;
        }
    }
}
