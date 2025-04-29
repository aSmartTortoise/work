package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoutePlanViewSelectAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_route_plan_view#select";
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
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        int index = getIndex(indexType, indexStr, DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePathSelected(), DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePaths());
        if (index == Integer.MAX_VALUE) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014900));
        } else if (index < 0) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015000));
        }
        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().selectRoutePlanInNavi(index);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
    }


}
