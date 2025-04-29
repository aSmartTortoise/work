package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviInfoSearchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_info#search";
    }


    @Override
    public boolean isAsync() {
        return true;
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
    public boolean isSupportRGear() {
        return true;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String searchInfo = getParamKey(paramsMap, NaviConstants.SEARCH_INFO, 0);
        String poi = getParamKey(paramsMap, NaviConstants.DEST_POI, 0);
        NluPoi nluPoi = GsonUtils.fromJson(poi, NluPoi.class);
        if (NaviConstants.Q_DISTANCE.equals(searchInfo) || NaviConstants.Q_DURATION.equals(searchInfo)) {
            return NaviQueryUtils.queryRemainTime(flowContext, nluPoi);
        }
        if (NaviConstants.Q_TOTAL_DISTANCE.equals(searchInfo) || NaviConstants.Q_TOTAL_DURATION.equals(searchInfo)) {
            return NaviQueryUtils.queryTotalTime(flowContext, nluPoi);
        }
        if (NaviConstants.Q_LOCATION.equals(searchInfo)) {
            return NaviQueryUtils.queryDestination(flowContext, nluPoi);
        }
        if (NaviConstants.Q_NUM.equals(searchInfo) || NaviConstants.Q_NAME.equals(searchInfo)) {
            if (nluPoi != null && NaviConstants.SERVICE_AREA.equals(nluPoi.getKeyword())) {
                return NaviQueryUtils.queryServiceArea(flowContext);
            }
            if (nluPoi != null && NaviConstants.TOLL_STATION.equals(nluPoi.getKeyword())) {
                return NaviQueryUtils.queryTollStation(flowContext);
            }
        }
        if (NaviConstants.Q_ENERGY.equals(searchInfo)) {
            return NaviQueryUtils.queryEnergy(flowContext, nluPoi);
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }
}
