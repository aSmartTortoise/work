package com.voyah.ai.logic.agent.generic;

import com.voice.sdk.util.LogUtils;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 据识
 * @data:2024/3/29
 **/
public class IgnoreAgent extends BaseAgentX {
    private static final String TAG = IgnoreAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "ignore";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {

        String ignoreQuery = getFlowContextKey(FlowContextKey.FC_WHOLE_QUERY, flowContext);
        boolean isIgQueryEmpty = StringUtils.isBlank(ignoreQuery);
        LogUtils.i(TAG, "onVoAIMessage -- IGNORE ignoreQuery:" + ignoreQuery);
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext);
        clientAgentResponse.setInValid(true);
        clientAgentResponse.setIgQueryEmpty(isIgQueryEmpty);
        return clientAgentResponse;
    }

    @Override
    public int getPriority() {
        return INVALID_PRIORITY;
    }

}
