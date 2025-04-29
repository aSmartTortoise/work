package com.voyah.ai.voice.agent.navi;

import android.util.Pair;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViaPointDeleteAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_via_point#delete";
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
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004305));
        }
        List<Poi> viaPoints = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
        if (viaPoints == null || viaPoints.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004202));
        }
        String viaPoiStr = getParamKey(paramsMap, NaviConstants.VIA_POI, 0);
        if (viaPoiStr.isEmpty()) {
            if (viaPoints.size() == 1) {
                return NaviViaPoiUtils.deleteViaPoint(flowContext, viaPoints, null);
            } else {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
                return new ClientAgentResponse(NaviResponseCode.DELETE_VIA_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004300));
            }
        } else {
            Pair<Integer, Poi> pair = NaviViaPoiUtils.toPoi(viaPoiStr);
            if (pair.first == NaviConstants.ErrCode.TOO_MANY_VIA_POINTS) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015200));
            }
            if (pair.first == NaviConstants.ErrCode.NO_PERMISSION) {
                return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
            }
            Poi deletePoi = pair.second;
            List<Poi> mathPoiList = NaviViaPoiUtils.getMatchViaPoiList(deletePoi);
            return NaviViaPoiUtils.deleteViaPoint(flowContext, mathPoiList, deletePoi != null ? deletePoi.getName() : null);
        }

    }


}
