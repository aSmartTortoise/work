package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoutePlanRefreshAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_route_plan#refresh";
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
        DeviceHolder.INS().getDevices().getNavi().refreshRoute();
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3005600));
    }



}
