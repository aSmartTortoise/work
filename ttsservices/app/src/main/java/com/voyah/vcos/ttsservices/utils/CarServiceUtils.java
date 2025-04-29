package com.voyah.vcos.ttsservices.utils;

import android.text.TextUtils;

import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;

/**
 * @author:lcy
 * @data:2025/2/25
 **/
public class CarServiceUtils {

    public static String getCarType() {
        String str = System.getProperty("vehicle_model");

        int type = MegaSystemProperties.getInt(MegaProperties.CONFIG_VEHICLE_MODEL, -1);
        switch (type) {
            case 0:
                str = "H97";
                break;
            case 1:
                str = "H56";
                break;
            case 2:
                str = "H53";
                break;
            case 3:
                str = "H37A";
                break;
            case 4:
                str = "H77";
                break;
            case 5:
                str = "H97c";
                break;
            case 6:
                str = "H53B";
                break;
            case 7:
                str = "H56C";
                break;
            case 8:
                str = "H97E";
                break;
            case 9:
                str = "H37B";
                break;
        }

        return TextUtils.isEmpty(str) ? "H56C" : str;
    }
}
