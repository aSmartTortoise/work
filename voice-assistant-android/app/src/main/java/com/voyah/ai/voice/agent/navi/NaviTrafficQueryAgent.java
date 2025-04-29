package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviTrafficQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_traffic#query";
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
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007200));
        }
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryTrafficInfo();
        if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true,true);
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, naviResponse.getData());
        } else {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));

        }
    }


}
