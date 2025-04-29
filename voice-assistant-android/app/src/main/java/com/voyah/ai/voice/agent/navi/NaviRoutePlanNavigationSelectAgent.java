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
public class NaviRoutePlanNavigationSelectAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_route_plan_navigation#select";
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
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        int maxPlans = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePaths();
        int current = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePathSelected();
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        int index = getIndex(indexType, indexStr, current, maxPlans);
        if (index == Integer.MAX_VALUE) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014900));
        } else if (index < 0) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015000));
        }
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().startNavi(index);
        if (naviResponse.isSuccess()) {
            return new ClientAgentResponse(NaviResponseCode.START_NAVIGATION.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.END_POINT_NULL) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "抱歉，没有找到您想去的目的地");
        } else {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015100));

        }
    }


}
