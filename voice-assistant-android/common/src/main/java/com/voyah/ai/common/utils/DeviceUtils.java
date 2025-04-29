package com.voyah.ai.common.utils;

import android.os.Build;

public class DeviceUtils {
    private static final String TAG = "DeviceUtils";

    private static final String VOYAH_DEVICE_NAME = "Qualcomm SA8295 Cockpit";

    public static boolean isVoyahDevice() {
        return VOYAH_DEVICE_NAME.equals(getDeviceName());
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName;
        if (model.startsWith(manufacturer)) {
            deviceName = capitalize(model);
        } else {
            deviceName = capitalize(manufacturer) + " " + model;
        }
        LogUtils.i(TAG, "deviceName:" + deviceName);
        return deviceName;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


}
