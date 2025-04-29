package com.voyah.ai.voice.agent.generic;//package com.voyah.ai.voice.agent.generic;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 二次交互超时
 * @data:2024/1/31
 **/
@ClassAgent
public class DialogTimeoutAgent extends BaseAgentX {
    private static final String TAG = DialogTimeoutAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "dialogTimeout";
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.d(TAG, "DialogTimeoutAgent");
        String tts = getFlowContextKey(FlowContextKey.FC_TTS_TEXT, flowContext);
        LogUtils.i(TAG, "tts is " + tts);
        return new ClientAgentResponse(0, flowContext, tts);
    }


}
