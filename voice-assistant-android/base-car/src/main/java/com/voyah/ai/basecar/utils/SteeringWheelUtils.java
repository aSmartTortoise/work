package com.voyah.ai.basecar.utils;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voyah.ai.common.utils.LogUtils;

import mega.config.MegaDataStorageConfig;

public class SteeringWheelUtils {

    private static final String TAG = SteeringWheelUtils.class.getSimpleName();

    private SteeringWheelUtils() {

    }

    /**
     * 获取自定义按键类型.
     */
    public static int getCustomType() {
        int mode = 0;
        try {
            mode = Settings.System
                .getInt(Utils.getApp().getContentResolver(),
                        "custom_type", 0);
            LogUtils.d(TAG, "getCustomType: " + mode);
        } catch (Exception exception) {
            LogUtils.d(TAG, "getCustomType: Exception -> "
                + exception.getMessage());
            exception.printStackTrace();
        }
        return mode;
    }

    /**
     * 设置自定义按键类型.
     *    <item>"音源切换"</item>               0
     *    <item>"全景泊车"</item>               1
     *    <item>"旅拍拍照"</item>               2
     *    <item>"自拍拍照"</item>               3
     *    <item>"屏幕移动"</item>               7
     *    <item>"AR HUD显示"</item>            8
     *    <item>"对外喊话"</item>               9
     *    <item>"低速行人警示音"</item>          10
     *    <item>"静音"</item>                  11
     */
    public static void setCustomType(int mode) {
        try {
            Settings.System
                .putInt(Utils.getApp().getContentResolver(),
                    "custom_type", mode);
            LogUtils.d(TAG, "setCustomType: " + mode);
            MegaDataStorageConfig.getContentResolver().notifyChange(
                Settings.System.getUriFor("custom_type"), null);
        } catch (Exception exception) {
            LogUtils.d(TAG, "setCustomType: Exception -> "
                + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
