package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointDeleteSelectAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point_delete#select";
    }

    @Override
    public boolean isNeedNaviInFront() {
        return false;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return true;
    }


    @Override
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        List<Poi> viaPoints = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
        if (viaPoints == null || viaPoints.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004202));
        }
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        int index = getIndex(indexType, indexStr, -1, viaPoints.size());
        if (index == Integer.MAX_VALUE) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004702, String.valueOf(viaPoints.size())));
        } else if (index < 0) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015000));
        }
        List<Poi> deletePoiList = new ArrayList<>();
        deletePoiList.add(viaPoints.get(index));
        return NaviViaPoiUtils.deleteViaPoint(flowContext, deletePoiList, null);

    }


}
