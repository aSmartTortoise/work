package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.LocationResult;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviLocationQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_location#query";
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
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String poi = getParamKey(paramsMap, NaviConstants.POI, 0);
        if (poi.isEmpty()) {
            NaviResponse<Poi> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().locateSelf();
            if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null) {
                Poi result = naviResponse.getData();
                String address = result.getProvince() + result.getCityName() + result.getDistrict() + result.getAddress();
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3005200, address));
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3017000));
    }


}
