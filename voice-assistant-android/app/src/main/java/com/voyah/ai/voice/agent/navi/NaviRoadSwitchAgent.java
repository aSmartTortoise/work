package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoadSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_road#switch";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return true;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return true;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String roadType = getParamKey(paramsMap, NaviConstants.ROAD_TYPE, 0);
        int roadValue = -1;
        TTSBean successTts = null;
        TTSBean failTts = null;
        if (NaviConstants.MAIN_ROAD.equals(roadType)) {
            roadValue = NaviConstants.RoadType.MAIN_ROAD_VALUE;
            successTts = TtsBeanUtils.getTtsBean(1100005);
            failTts = TtsBeanUtils.getTtsBean(3009701);
        }
        if (NaviConstants.SIDE_ROAD.equals(roadType)) {
            roadValue = NaviConstants.RoadType.SIDE_ROAD_VALUE;
            successTts = TtsBeanUtils.getTtsBean(1100005);
            failTts = TtsBeanUtils.getTtsBean(3009701);
        }
        if (NaviConstants.ON_VIADUCT.equals(roadType)) {
            roadValue = NaviConstants.RoadType.ON_ELEVATED_VALUE;
            successTts = TtsBeanUtils.getTtsBean(1100005);
            failTts = TtsBeanUtils.getTtsBean(3009701);
        }
        if (NaviConstants.DOWN_VIADUCT.equals(roadType)) {
            roadValue = NaviConstants.RoadType.UNDER_ELEVATED_VALUE;
            successTts = TtsBeanUtils.getTtsBean(1100005);
            failTts = TtsBeanUtils.getTtsBean(3009701);
        }
        if (roadValue != -1) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().changeRoad(roadValue);
            if (naviResponse != null && naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, successTts);
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, failTts);
            }

        } else {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));

        }
    }


}
