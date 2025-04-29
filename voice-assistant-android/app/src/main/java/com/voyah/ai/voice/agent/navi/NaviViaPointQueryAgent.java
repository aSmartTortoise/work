package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point#query";
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
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004203));
        }
        List<Poi> viaPoints = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
        TTSBean tts;
        if (viaPoints == null || viaPoints.isEmpty()) {
            tts = TtsBeanUtils.getTtsBean(3004202);
        } else if (viaPoints.size() == 1) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
            tts = TtsBeanUtils.getTtsBean(3004200, viaPoints.get(0).getName());

        } else {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
            tts = TtsBeanUtils.getTtsBean(3004201);
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
    }


}
