package com.voyah.ai.voice.agent.navi;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ClassAgent
public class NaviFavoritesAddAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_favorites#add";
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
        String poiStr = BaseAgentX.getParamKey(paramsMap, NaviConstants.POI, 0);
        NluPoi nluPoi = GsonUtils.fromJson(poiStr, NluPoi.class);
        if (nluPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "这个我们还在学习中，请先试试手动操作");
        }
        if (nluPoi.isCommonPoi()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "这个我们还在学习中，请先试试手动操作");
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "您已开启信息隐藏，无法收藏地址哦");

        }
        if (nluPoi.isCurrentLocation()) {
            NaviResponse<Poi> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().locateSelf();
            if (!naviResponse.isSuccess() || naviResponse.getData() == null) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "抱歉，定位失败了，请稍后重试。");
            }
            return NaviFavoriteUtils.favoritePoi(flowContext, naviResponse.getData());
        }
        if (nluPoi.isDestination()) {
            Poi poi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || poi == null) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003900));
            }
            if (nluPoi.isFitDestPoi(poi)) {
                return NaviFavoriteUtils.favoritePoi(flowContext, poi);
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "抱歉，没有找到对应的目的地。");
            }

        }
        if (nluPoi.isWayPoint()) {
            List<Poi> wayPoiList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
            if (wayPoiList == null || wayPoiList.isEmpty()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004202));
            }
            if (TextUtils.isEmpty(nluPoi.getDesc())) {
                return NaviFavoriteUtils.favoritePoi(flowContext, wayPoiList.get(0));
            } else {
                List<Poi> poiList = new ArrayList<>();
                for (Poi poiC : wayPoiList) {
                    if (poiC.containKeyWord(nluPoi.getDesc())) {
                        poiList.add(poiC);
                    }
                }
                if (poiList.isEmpty()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "抱歉，没有找到对应的途经点。");
                } else {
                    return NaviFavoriteUtils.favoritePoi(flowContext, poiList.get(0));
                }
            }

        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "这个我们还在学习中，请先试试手动操作");

    }
}
