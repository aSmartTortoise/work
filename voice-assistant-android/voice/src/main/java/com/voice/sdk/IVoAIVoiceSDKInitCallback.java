package com.voice.sdk;

import java.util.Map;

/**
 * @author:lcy
 * @data:2024/4/19
 **/
public interface IVoAIVoiceSDKInitCallback {
    void onSUCCESS();
    void onFAILED();
    void onExit();
    void onVoiceStatus(String detail, Map<String, Object> msgMap);

}
