package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointDeleteAllAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point#delete_all";
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
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
        }
        List<Poi> viaPoints = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
        if (viaPoints == null || viaPoints.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004202));
        }
        boolean result = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().deleteAllViaPoints();
        Object tts;
        if (result) {
            tts = TtsBeanUtils.getTtsBean(3004301);
        } else {
            tts = TtsBeanUtils.getTtsBean(3015400);
        }
        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true,true);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);

    }


}
