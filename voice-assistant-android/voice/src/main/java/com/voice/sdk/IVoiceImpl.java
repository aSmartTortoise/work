package com.voice.sdk;


import android.content.Context;

import com.voyah.ai.voice.sdk.api.task.AgentX;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/2/17
 **/
public interface IVoiceImpl {
    //初始化语音sdk
    void init(String vinCode, boolean isCar, String vehicleType, int configurationType, IVoAIVoiceSDKInitCallback initCallback);

    //注册agent
    void registerAgent(AgentX agent);

    //批量注册agent
    void addAgents(List<AgentX> agentXList);

    //反注册agent
    void unRegisterAgent(AgentX actionAgent);

    //退出语音对话
    void exDialog();

    //退出二次交互流程
    void exitSessionDialog(String sessionId);

    //退出(结束)指定流程
    void exitRequest(String requestId);

    //场景值上报
    void sceneReport(String id, String sceneState, String sceneTts, Map<String, Object> params);

    //三方主动上报场景值
    void thirdSceneReport(String sceneState, String sceneStateTts, Map<String, Object> params, int wakeUpLocation, boolean isWakeUp);

    //清除场景上报
    void cleanSceneReport();

    void sendAudio(byte[] datas);

    void sendAudio(String audioName);

    //唤醒(方控使用-不受语音唤醒开关影响)
    void wakeUp(int location, int wakeupType);

    void setLanguageModelDebug(boolean isLanguageModelDebug);

    //语音唤醒开关
    void enableWakeup(boolean enableWakeup);

    //语音连续对话开关
    void enableContinueSession(boolean enableContinueSession);

    //可收音区设置
    void setRegionConfig(int position);


    boolean getVoiceSdkInitStatus();

}
