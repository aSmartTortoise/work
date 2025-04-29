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
public class NaviScaleSwitchAgent extends AbstractNaviAgent {

    @Override
    public String AgentName() {
        return "navi_scale#switch";
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
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().switchAutoScale(true);
            if (naviResponse != null && naviResponse.isSuccess()) {
                if (naviResponse.isAlreadySuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010000));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010001));
                }
            }

        }
        if (Constant.CLOSE.equals(operation)) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().switchAutoScale(false);
            if (naviResponse != null && naviResponse.isSuccess()) {
                if (naviResponse.isAlreadySuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010100));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010101));
                }
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
