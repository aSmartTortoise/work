package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointAddSelectAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point_add#select";
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
        List<Poi> poiList = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiList();
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() && !DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isPlanRoute()) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3005503));
        }
        if (poiList == null || poiList.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        }
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        int index = getIndex(indexType, indexStr, 0, DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiListSize());
        if (index == Integer.MAX_VALUE) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014900));
        } else if (index < 0) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015000));
        }
        return NaviViaPoiUtils.addViaPoint(flowContext, poiList.get(index), index);
    }


}
