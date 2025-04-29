package com.voyah.ai.logic.agent.generic;

import com.voice.sdk.tts.TtsReplyUtils;
import com.voice.sdk.util.LogUtils;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/4/25
 **/

public class InstanceTtsAgent extends BaseAgentX {
    private static final String TAG = InstanceTtsAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "instanceTts";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "InstanceTtsAgent");
//        boolean isStreamTts = flowContext.containsKey(FlowContextKey.FC_IS_STREAM_TTS_TEXT) ? (boolean) flowContext.get(FlowContextKey.FC_IS_STREAM_TTS_TEXT) : false;
        String tts = getFlowContextKey(FlowContextKey.FC_TTS_TEXT, flowContext);
        boolean isTtsFromLlm = getBooleanFlowContextKey(FlowContextKey.FC_IS_STREAM_MODE, flowContext);
        boolean isFromCPSPClassLabel = getBooleanFlowContextKey(FlowContextKey.FC_IS_CLASS_LABEL_FROM_CPSP, flowContext);
        String deviceState = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE, flowContext);
        LogUtils.d(TAG,"flowContext  deviceState: " + deviceState);
        //媒体二次交互无效，TTS修改
        if ("State.VIDEO_OPTIONS".equals(deviceState)) {
            tts = TtsReplyUtils.getTtsBean("4019705").getSelectTTs();
        }
        LogUtils.i(TAG, "tts is " + tts + " , isTtsFromLlm is " + isTtsFromLlm + " ,isFromCPSPClassLabel:" + isFromCPSPClassLabel);
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, tts);
        if (StringUtils.isBlank(tts))
            clientAgentResponse.setClearInput(true);
        return clientAgentResponse;
    }


}
