package com.voyah.ai.voice.agent.navi;

import android.util.Pair;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviDestPoiChangeAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_dest_poi#change";
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
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String abandonDestPoi = getParamKey(paramsMap, NaviConstants.ABANDON_DEST_POI, 0);
        String newDestPoi = getParamKey(paramsMap, NaviConstants.NEW_DEST_POI, 0);
        if (newDestPoi.isEmpty() && !abandonDestPoi.isEmpty()) {
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
            }
            Pair<Integer, Poi> pair = NaviViaPoiUtils.toPoi(abandonDestPoi);
            if (pair.first == NaviConstants.ErrCode.NO_PERMISSION) {
                return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
            }
            Poi poi = pair.second;
            if (poi == null) {
                String text;
                if (abandonDestPoi.contains(NaviConstants.HOME)) {
                    text = NaviConstants.HOME;
                } else {
                    text = NaviConstants.COMPANY;
                }
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003801, text));
            }
            Poi desPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
            if (NaviConstants.DESTINATION.equals(poi.getName()) || (desPoi != null && (desPoi.containKeyWord(poi.getName()) || (desPoi.getId() != null && desPoi.getId().equals(poi.getId()))))) {
                DeviceHolder.INS().getDevices().getNavi().stopNavi();
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004602, poi.getName()));
            }
            List<Poi> viaList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
            if (viaList != null && !viaList.isEmpty()) {
                for (Poi via : viaList) {
                    if (via.containKeyWord(poi.getName())) {
                        List<Poi> deletePoiList = new ArrayList<>();
                        deletePoiList.add(via);
                        return NaviViaPoiUtils.deleteViaPoint(flowContext, deletePoiList, poi.getName());
                    }
                }
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004601, poi.getName()));
        } else {
            return NaviPoiUtils.searchPoi(flowContext, paramsMap, false);
        }
    }


}
