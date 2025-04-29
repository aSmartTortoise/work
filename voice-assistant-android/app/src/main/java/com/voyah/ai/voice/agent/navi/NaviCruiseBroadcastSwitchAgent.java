package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviCruiseBroadcastSwitchAgent extends AbstractNaviAgent {

    @Override
    public String AgentName() {
        return "navi_cruise_broadcast#switch";
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
    public boolean isNeedShowToast() {
        return false;
    }

    @Override
    public boolean isSupportRGear() {
        return true;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String operation = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        NaviResponse<String> naviResponse;
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(false);
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchCruiseBroadcast(true);
            if (naviResponse != null && naviResponse.isAlreadySuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011101));
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011100));

            }
        }
        if (Constant.CLOSE.equals(operation)) {
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchCruiseBroadcast(false);
            if (naviResponse != null && naviResponse.isAlreadySuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011200));
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011201));

            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
