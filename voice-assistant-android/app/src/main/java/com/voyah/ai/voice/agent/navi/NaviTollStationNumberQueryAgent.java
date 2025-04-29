package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviTollStationNumberQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_toll_station_number#query";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return true;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return NaviQueryUtils.queryTollStation(flowContext);
    }



}
