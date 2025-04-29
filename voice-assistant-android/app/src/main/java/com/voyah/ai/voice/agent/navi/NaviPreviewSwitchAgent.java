package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviPreviewSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_preview#switch";
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
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String operation = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String type = getParamKey(paramsMap, NaviConstants.PREVIEW_TYPE, 0);
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000705));

        }
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            if (NaviConstants.REGULAR_OVERVIEW.equals(type)) {
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, false);
                if (naviResponse.isAlreadySuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008002));

                } else if (naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
            } else {
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
                if (naviResponse.isAlreadySuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007802));

                } else if (naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }

            }
        }
        if (Constant.CLOSE.equals(operation)) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(false, true);
            if (naviResponse.isAlreadySuccess()) {
                if (NaviConstants.REGULAR_OVERVIEW.equals(type)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008102));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007902));
                }
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }

}
