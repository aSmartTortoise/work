package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointChangeAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point#change";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return false;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return NaviViaPoiUtils.searchViaPoint(flowContext, paramsMap);
    }


    @Override
    public void destroyAgent() {

    }


}
