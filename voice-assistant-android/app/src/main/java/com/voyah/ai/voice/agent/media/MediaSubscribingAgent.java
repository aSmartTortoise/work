package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaSubscribingAgent extends BaseAgentX {

    private static final String TAG = MediaSubscribingAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#subscribing";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaSubscribingAgent------");
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.NOT_SUPPORT);
    }


}
