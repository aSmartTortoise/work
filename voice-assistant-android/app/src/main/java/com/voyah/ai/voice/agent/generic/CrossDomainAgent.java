package com.voyah.ai.voice.agent.generic;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/6/27
 **/
@ClassAgent
public class CrossDomainAgent extends BaseAgentX {
    private static final String TAG = CrossDomainAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "crossDomain";
    }

    @Override
    public boolean isAsync() {
        return false;
    }


    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.d(TAG, "CrossDomainAgent");
        int location = getSoundSourceLocation(flowContext);
        UIMgr.INSTANCE.dismissCardOnScreen(location);
        DeviceHolder.INS().getDevices().getTts().stopCurTts();
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(0);
        clientAgentResponse.setCrossDomain(true);
        return clientAgentResponse;
    }


}
