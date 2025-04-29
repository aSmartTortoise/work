package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviChooseAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi#choose";
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
        String chooseType = getParamKey(paramsMap, NaviConstants.CHOOSE_TYPE, 0);
        Object state = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        if (NaviConstants.CONFIRM.equals(chooseType)) {
            List<Poi> poiList = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiList();
            if (NaviResponseCode.CONTINUE_NAVI_CONFIRM.getState().equals(state)) {
                Poi lastPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStash().getLastPoi();
                if (lastPoi != null) {
                    return NaviPoiUtils.naviSelectPoi(flowContext, lastPoi, DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), null);
                }
            }
            if (NaviResponseCode.SEARCH_POI_RESULT_ONE.getState().equals(state) || NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getState().equals(state)) {
                if (poiList == null || poiList.isEmpty()) {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
                return NaviPoiUtils.naviSelectPoi(flowContext, poiList.get(0), DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), null);
            }
            if (NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_ONE.getState().equals(state) || NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_MULTIPLE.equals(state)) {
                if (poiList == null || poiList.isEmpty()) {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
                return NaviViaPoiUtils.addViaPoint(flowContext, poiList.get(0), 0);
            }
            if (NaviResponseCode.FAVORITE_POI_RESULT_ONE.getState().equals(state) || NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getState().equals(state)) {
                if (poiList == null || poiList.isEmpty()) {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
                return NaviPoiUtils.favoriteAddress(flowContext, poiList.get(0));
            }
            if (NaviResponseCode.ROUTE_PLAN.getState().equals(state)) {
                if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviStatus().getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_STARTED) {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000500));
                }
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().startNavi(-1);
                if (naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.START_NAVIGATION.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.END_POINT_NULL) {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "抱歉，没有找到您想去的目的地");
                } else {
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015100));
                }
            }
            if (NaviResponseCode.TEAM_DISBAND_CAPTAIN.getState().equals(state) || NaviResponseCode.TEAM_EXIT_MEMBER.getState().equals(state)) {
                NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviTeam().getTeamInfo();
                if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() == NaviConstants.TeamType.TEAM_MEMBER) {
                    DeviceHolder.INS().getDevices().getNavi().getNaviTeam().exitTeam();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                }
                if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() == NaviConstants.TeamType.TEAM_CAPTAIN) {
                    DeviceHolder.INS().getDevices().getNavi().getNaviTeam().disbandTeam();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                }
            }
        }
        if (NaviConstants.CANCEL.equals(chooseType)) {
            return NaviControlUtils.cancelSelect(flowContext);
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
