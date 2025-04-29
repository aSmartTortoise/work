package com.voice.sdk.device.llm;

import com.voice.sdk.device.ui.UICardInterface;

import java.util.Map;

/**
 * author : jie wang
 * date : 2025/3/6 17:33
 * description :
 */
public interface LLMTextInterface extends UICardInterface {

    void constructCardInfo(String content, String topicType, String requestId);

    void constructStreamCardInfo(Map<String, Object> map);
}
