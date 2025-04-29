package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviBroadcastSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_broadcast#switch";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return false;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return false;
    }

    @Override
    public boolean isSupportRGear() {
        return true;
    }

    @Override
    public boolean isNeedShowToast() {
        return false;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return NaviBroadcastUtils.changeBroadcast(flowContext, paramsMap);
    }

}
