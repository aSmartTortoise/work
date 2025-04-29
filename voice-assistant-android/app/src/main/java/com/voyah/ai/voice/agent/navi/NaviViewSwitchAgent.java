package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviViewSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_view#switch";
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
        String operation = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String viewType = getParamKey(paramsMap, NaviConstants.VIEW_TYPE, 0);
        String viewPoint = getParamKey(paramsMap, NaviConstants.VIEW_POINT, 0);
        int viewModeValue = -1;
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            if (NaviConstants.VIEW_TYPE_2D.equals(viewType)) {
                if (NaviConstants.VIEW_POINT_CAR_TO_UP.equals(viewPoint)) {
                    viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_2D_CAR_UP;
                }
                if (NaviConstants.VIEW_POINT_NORTH_TO_UP.equals(viewPoint)) {
                    viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_2D_NORTH_UP;
                }
                if (viewPoint.isEmpty()) {
                    NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().getViewMode();
                    if (naviResponse != null && naviResponse.isSuccess()) {
                        int viewMode = naviResponse.getData();
                        if (viewMode == NaviConstants.MapViewType.MAP_VIEW_MODE_3D_CAR_UP || viewMode == NaviConstants.MapViewType.MAP_VIEW_MODE_2D_CAR_UP) {
                            viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_2D_NORTH_UP;
                        }
                        if (viewMode == NaviConstants.MapViewType.MAP_VIEW_MODE_2D_NORTH_UP) {
                            viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_2D_CAR_UP;
                        }
                    }
                }
            } else if (NaviConstants.VIEW_TYPE_3D.equals(viewType)) {
                if (NaviConstants.VIEW_POINT_CAR_TO_UP.equals(viewPoint) || viewPoint == null || viewPoint.isEmpty()) {
                    viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_3D_CAR_UP;
                }
            } else {
                if (NaviConstants.VIEW_POINT_NORTH_TO_UP.equals(viewPoint)) {
                    viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_2D_NORTH_UP;
                }
                if (NaviConstants.VIEW_POINT_CAR_TO_UP.equals(viewPoint)) {
                    viewModeValue = NaviConstants.MapViewType.MAP_VIEW_MODE_3D_CAR_UP;
                }
            }
        }
        if (Constant.CHANGE.equals(operation)) {
            if (viewPoint == null || viewPoint.isEmpty()) {
                NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().getViewMode();
                if (naviResponse != null && naviResponse.isSuccess()) {
                    int viewMode = naviResponse.getData();
                    int[] viewArr = {
                            NaviConstants.MapViewType.MAP_VIEW_MODE_2D_NORTH_UP, NaviConstants.MapViewType.MAP_VIEW_MODE_3D_CAR_UP, NaviConstants.MapViewType.MAP_VIEW_MODE_2D_CAR_UP};
                    int curIndex = -1;
                    for (int i = 0; i < viewArr.length; i++) {
                        if (viewArr[i] == viewMode) {
                            curIndex = i;
                            break;
                        }
                    }
                    if (curIndex != -1) {
                        curIndex = (curIndex + 1) % viewArr.length;
                    }
                    viewModeValue = viewArr[curIndex];
                }
            }
        }
        if (viewModeValue != -1) {
            TTSBean tts;
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().adjustViewMode(viewModeValue);
            if (naviResponse.isSuccess() ||
                    naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_MAP_VIEW_2D_CAR_UP ||
                    naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_MAP_VIEW_2D_NORTH_UP ||
                    naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_MAP_VIEW_3D) {
                if (!naviResponse.isSuccess()) {
                    if (viewModeValue == NaviConstants.MapViewType.MAP_VIEW_MODE_2D_CAR_UP) {
                        tts = TtsBeanUtils.getTtsBean(3009200);
                    } else if (viewModeValue == NaviConstants.MapViewType.MAP_VIEW_MODE_3D_CAR_UP) {
                        tts = TtsBeanUtils.getTtsBean(3009100);
                    } else {
                        tts = TtsBeanUtils.getTtsBean(3009300);
                    }
                } else {
                    tts = TtsBeanUtils.getTtsBean(3008901, NaviConstants.MapViewType.getMapViewDesc(viewModeValue));
                }
            } else {
                tts = TtsBeanUtils.getTtsBean(3008803);
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3008803));
    }


}
