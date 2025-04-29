package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class LlmControlImpl extends AbsDevices {

    private static final String TAG = LlmControlImpl.class.getSimpleName();

    public LlmControlImpl() {
        super();
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "switch_mode":
                String switch_mode = getLlmOrderMode(map) == 0 ? "逍遥座舱大模型" : "deepseek";
                str = str.replace("@{switch_mode}", switch_mode);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

    public void setLlmMode(HashMap<String, Object> map) {
        operator.setIntProp(CommonSignal.COMMON_LLM_MODE, getLlmOrderMode(map));
    }

    public int getLlmOrderMode(HashMap<String, Object> map) {
        int orderMode = 0;
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            orderMode = (switchMode.equals("XiaoYao") || switchMode.equals("lantu")) ? 0 : 1;
        }
        LogUtils.d(TAG, "getLlmOrderMode: " + orderMode);
        return orderMode;
    }

    @Override
    public String getDomain() {
        return "Llm";
    }
}
