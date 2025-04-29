package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviFavoritesFrequentlyQueryAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_favorites_frequently#query";
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
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(NaviConstants.FavoritesType.FREQUENTLY, true);
        if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
            return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
        }
        if (naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
            String name;
            if (naviResponse.getData().size() > 1) {
                name = naviResponse.getData().get(0).getName() + "和" + naviResponse.getData().get(1).getName();
            } else {
                name = naviResponse.getData().get(0).getName();
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011600, name));

        } else {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011601));
        }
    }


}
