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
public class NaviFavoritesSwitchAgent extends AbstractNaviAgent {

    @Override
    public String AgentName() {
        return "navi_favorites#switch";
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
        switch (operation) {
            case Constant.OPEN:
            case Constant.RETURN: {
                String poi = getParamKey(paramsMap, NaviConstants.POI, 0);
                String favoritesType = getParamKey(paramsMap, NaviConstants.FAVORITES_TYPE, 0);
                if (favoritesType.contains(NaviConstants.HOME)) {
                    favoritesType = NaviConstants.HOME;
                } else if (favoritesType.contains(NaviConstants.COMPANY)) {
                    favoritesType = NaviConstants.COMPANY;
                } else {
                    favoritesType = null;
                }
                if (favoritesType != null) {
                    return NaviPoiUtils.changeHomeOrCompany(flowContext, favoritesType, poi);
                } else {
                    NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
                    if (naviResponse != null && naviResponse.isSuccess()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011300));

                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                    }
                }

            }
            case Constant.CLOSE:
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(false);
                if (naviResponse != null && naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011400));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
            case Constant.CHANGE: {
                String poi = getParamKey(paramsMap, NaviConstants.POI, 0);
                String favoritesType = getParamKey(paramsMap, NaviConstants.FAVORITES_TYPE, 0);
                if (favoritesType.contains(NaviConstants.HOME)) {
                    favoritesType = NaviConstants.HOME;
                } else if (favoritesType.contains(NaviConstants.COMPANY)) {
                    favoritesType = NaviConstants.COMPANY;
                } else {
                    favoritesType = null;
                }
                return NaviPoiUtils.changeHomeOrCompany(flowContext, favoritesType, poi);

            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
