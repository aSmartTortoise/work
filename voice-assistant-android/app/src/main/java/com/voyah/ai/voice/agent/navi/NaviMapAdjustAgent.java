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
public class NaviMapAdjustAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_map#adjust";
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
        String adjustType = getParamKey(paramsMap, Constant.ADJUST_TYPE, 0);
        NaviResponse<String> naviResponse = null;
        if (Constant.INCREASE.equals(adjustType)) {
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().zoomMap(true);
        }
        if (Constant.DECREASE.equals(adjustType)) {
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().zoomMap(false);
        }
        String level = getParamKey(paramsMap, Constant.LEVEL, 0);
        if (Constant.MAX.equals(level)) {
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().maxMinMap(true);
        } else if (Constant.MIN.equals(level)) {
            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().maxMinMap(false);
        }
        if (naviResponse == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));

        } else {
            if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_MAX) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008200));

            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_MIN) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008500));
            } else if (naviResponse.isSuccess()) {
                if (Constant.MAX.equals(level)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008401));

                }
                if (Constant.MIN.equals(level)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008701));
                }
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            } else
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        }
    }



}
