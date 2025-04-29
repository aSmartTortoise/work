package com.voyah.ai.virtual.dc;


import android.util.Log;

import com.voice.sdk.device.carservice.signal.FragranceSignal;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class FragrancePropertyOperator extends BaseVirtualPropertyOperator {

    private static final String TAG = "FragrancePropertyOperat";

    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP = new HashMap<>();

    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP = new HashMap<>();

    public FragrancePropertyOperator() {
        AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.put("min", "low");
        AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.put("low", "low");
        AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.put("mid", "mid");
        AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.put("high", "high");
        AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.put("max", "max");

        AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP.put("soft", "low");
        AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP.put("standard", "mid");
        AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP.put("strong", "high");
    }

    public int getBaseIntProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return (Integer) getValue(virtualKey);
    }


    public void setBaseIntProp(String key, int area, int value) {
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }


    public String getBaseStringProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return getValue(virtualKey) + "";
    }


    public void setBaseStringProp(String key, int area, String value) {
        Log.d(TAG, "setBaseStringProp : key" + key + " value:" + value);
        String virtualKey = getVirtualKey(key, area);
        String curValue;
        switch (key) {
            case FragranceSignal.FRAGRANCE_LEVEL:
                curValue = AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.get(value);
                if (curValue == null) {
                    curValue = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP.get(value);
                }
                break;

            default:
                curValue = value;
                LogUtils.e(TAG, "香氛当前方法setBaseStringProp存在没处理的情况：" + key);
                break;
        }
        setValue(virtualKey, curValue);
    }


    public boolean getBaseBooleanProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return (Boolean) getValue(virtualKey);
    }


    public void setBaseBooleanProp(String key, int area, boolean value) {
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }
}
