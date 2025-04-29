package com.voyah.ai.basecar.system;

import static com.voyah.ai.basecar.CommonSystemUtils.getAppVersion;

import android.provider.Settings;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.example.upload_log_manager.UploadLogManager;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.SystemSettingInterface;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;

import voyoh.lc.VlmWrapper;

public class SystemSettingImpl implements SystemSettingInterface {
    private static final String TAG = SystemSettingImpl.class.getSimpleName();

    @Override
    public int getInt(String type, String key, int defaultState) {
        LogUtils.d(TAG, "getInt type: " + type + " key: " + key + " defaultState: " + defaultState);
        if (!StringUtils.isEmpty(type)) {
            switch (type) {
                case ISysSetting.SystemSettingType.SETTING_SYSTEM:
                    return Settings.System.getInt(Utils.getApp().getContentResolver(), key, defaultState);
                case ISysSetting.SystemSettingType.SETTING_GLOBAL:
                    return Settings.Global.getInt(Utils.getApp().getContentResolver(), key, defaultState);
            }
        }
        return defaultState;
    }

    @Override
    public void putInt(String type, String key, int value) {
        LogUtils.d(TAG, "putInt type: " + type + " key: " + key + " value: " + value);
        if (!StringUtils.isEmpty(type)) {
            switch (type) {
                case ISysSetting.SystemSettingType.SETTING_SYSTEM:
                    Settings.System.putInt(Utils.getApp().getContentResolver(), key, value);
                    break;
                case ISysSetting.SystemSettingType.SETTING_GLOBAL:
                    Settings.Global.putInt(Utils.getApp().getContentResolver(), key, value);
                    break;
            }
        }
    }

    @Override
    public String getString(String type, String key) {
        LogUtils.d(TAG, "getString type: " + type + " key: " + key);
        if (!StringUtils.isEmpty(type)) {
            switch (type) {
                case ISysSetting.SystemSettingType.SETTING_SYSTEM:
                    return Settings.System.getString(Utils.getApp().getContentResolver(), key);
                case ISysSetting.SystemSettingType.SETTING_GLOBAL:
                    return Settings.Global.getString(Utils.getApp().getContentResolver(), key);
            }
        }
        return "";
    }

    @Override
    public void openUpLogPage() {
//        if (CarServicePropUtils.getInstance().isH37B() || CarServicePropUtils.getInstance().isH56D()) {
////            VlmWrapper wrapper = new VlmWrapper();
////            wrapper.setRecvCallback();//注册JNI回调
////            //初始化
////            if (wrapper.init("8295and", "com.voyah.ai.voice", getAppVersion()) != 0) {
////                LogUtils.e(TAG, "wrapper init error" + wrapper.getError());
////                return;
////            }
////            //发送采集命令
////            long now = System.currentTimeMillis() / 1000;
////            int result =  wrapper.sendCmd(now - (24 * 60 * 60), now, "语音上传日志采集");
////            LogUtils.e(TAG, "openUpLogPage result" + result);
//        } else {
//            UploadLogManager.getInstance().showUploadUi();
//        }
        UploadLogManager.getInstance().showUploadUi();
    }
}
