package com.voice.sdk.device.llm;

import com.voice.sdk.device.DomainInterface;

import java.util.List;

/**
 * author : jie wang
 * date : 2025/3/6 16:36
 * description :
 */
public interface LLMDrawingInterface extends DomainInterface {

    boolean openApp(int displayId);

    boolean closeApp(int displayId);

    void postDrawingState(String prompt,
                          String keyWords,
                          String ttxText,
                          int streamMode,
                          int code,
                          int imageSize,
                          int drawingState,
                          List<String> imgUrls,
                          String requestId,
                          String screenType);
}
