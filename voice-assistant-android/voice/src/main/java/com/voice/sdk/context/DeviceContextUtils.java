package com.voice.sdk.context;

import android.content.Context;

import com.voice.sdk.VoiceImpl;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class DeviceContextUtils {
    public static final String TAG = "ReportedData";

    private DeviceContextUtils() {

    }


    private final static DeviceContextUtils instance = new DeviceContextUtils();

    public static DeviceContextUtils getInstance() {
        return instance;
    }


    public void startWork(Context context) {

    }

    public synchronized void updateDeviceInfo(DeviceInfo... deviceInfo) {
        LogUtils.i(TAG, "updateDeviceInfo:" + GsonUtils.toJson(deviceInfo));
        if (deviceInfo == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        for (DeviceInfo dInfo : deviceInfo) {
            map.putAll(dInfo.toMap());
        }
        VoiceImpl.getInstance().reportData(map);
    }
}
