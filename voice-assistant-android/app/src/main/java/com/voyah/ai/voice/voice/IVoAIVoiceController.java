package com.voyah.ai.voice.voice;

import com.voice.sdk.IVoAIVoiceSDKInitCallback;
import com.voyah.ai.voice.sdk.api.task.AgentX;

/**
 * @author:lcy sdk接口
 * @data:2024/1/29
 **/
public interface IVoAIVoiceController {
    void init(String vinCode, boolean isCar, IVoAIVoiceSDKInitCallback initCallback);

    void registerAgent(AgentX agent);

    void unRegisterAgent(AgentX agent);

    void registerAgent(AgentX[] agents);

    void unRegisterAgent(AgentX[] agents);

    void queryTest(String query, int location, boolean isOnline);

    void exDialog();

    void sendAudio(byte[] datas);

    void sendAudio(String audioName);

    void wakeUp(int location);
}
