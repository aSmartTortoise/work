package com.voyah.ai.voice.agent.system;


import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.navi.NaviBroadcastUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class SystemSettingBroadcastSwitchAgent extends BaseAgentX {
    @Override
    public String AgentName() {
        return "systemSetting_broadcast#switch";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return NaviBroadcastUtils.changeBroadcast(flowContext, paramsMap);
    }


}
