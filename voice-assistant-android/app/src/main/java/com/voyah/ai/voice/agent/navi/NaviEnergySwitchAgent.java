package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviEnergySwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_energy#switch";
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
        String operation = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        if (Constant.OPEN.equals(operation)) {
            NluPoi nluPoi = new NluPoi();
            nluPoi.setKeyword("充电站");
            nluPoi.setSearchType("around");
            List<Object> list = new ArrayList<>();
            list.add(GsonUtils.toJson(nluPoi));
            paramsMap.put(NaviConstants.DEST_POI, list);
            return NaviPoiUtils.searchPoi(flowContext, paramsMap, false);
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }

}
