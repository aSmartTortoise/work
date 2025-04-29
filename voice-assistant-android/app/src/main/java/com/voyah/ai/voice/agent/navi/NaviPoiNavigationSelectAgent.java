package com.voyah.ai.voice.agent.navi;


import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStatus;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviPoiNavigationSelectAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_poi_navigation#select";
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
        List<Poi> poiList;
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage() == NaviPage.PAGE_FAV.getValue()) {
            NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(NaviConstants.FavoritesType.FAVORITES, false);
            poiList = naviResponse.getData();
        } else {
            poiList = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiList();
        }
        if (poiList == null || poiList.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        }
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        int index = getIndex(indexType, indexStr, 0, poiList.size());
        if (index == Integer.MAX_VALUE) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014900));
        } else if (index < 0) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015000));
        }
        Poi poi = poiList.get(index);
        NaviStatus naviStatus = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviStatus();
        LogUtils.i("NaviStatus", "naviStatus.getSearchType():" + naviStatus.getSearchType());
        if (naviStatus.getSearchType() != NaviConstants.SearchUpdateType.SEARCH_TYPE_ON_WAY && naviStatus.getSearchType() != NaviConstants.SearchUpdateType.SEARCH_TYPE_VIA_POINT) {
            return NaviPoiUtils.naviSelectPoi(flowContext, poi, DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), null);
        } else {
            return NaviViaPoiUtils.addViaPoint(flowContext, poi, index);
        }
    }


}
