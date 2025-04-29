package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoadInfoQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_road_info#query";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return true;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return false;
    }

    @Override
    public boolean isNeedShowToast() {
        return false;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015600));
    }



}
