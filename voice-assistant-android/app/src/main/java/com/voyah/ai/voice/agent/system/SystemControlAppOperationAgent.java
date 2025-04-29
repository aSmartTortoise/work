package com.voyah.ai.voice.agent.system;


import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class SystemControlAppOperationAgent extends BaseAgentX {

    @Override
    public String AgentName() {
        return "systemControl_app#operation";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return SystemControlAppOperationUtils.executeAgent(flowContext,paramsMap);
    }

}
