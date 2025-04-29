package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviControl extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi#control";
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
        String operationType = getParamKey(paramsMap, Constant.CONTROL_TYPE, 0);
        String naviMode = getParamKey(paramsMap, NaviConstants.NAVI_MODE, 0);
        if (Constant.START.equals(operationType)) {
            if (naviMode == null || naviMode.isEmpty()) {
                return NaviControlUtils.startNavigation(flowContext);
            }
        }
        if (Constant.CANCEL.equals(operationType)) {
            if (naviMode == null || naviMode.isEmpty()) {
                return NaviControlUtils.cancelNavigation(flowContext);
            }
            if (NaviConstants.NAVI_MODE_LANE.equals(naviMode)) {
                return NaviControlUtils.closeLaneNavi(flowContext);
            }
        }
        if (Constant.RESUME.equals(operationType)) {
            if (naviMode == null || naviMode.isEmpty()) {
                return NaviControlUtils.resumeNavigation(flowContext);
            }
        }
        if (Constant.RETURN.equals(operationType)) {
            if (naviMode == null || naviMode.isEmpty()) {
                return NaviControlUtils.returnNavigation(flowContext);
            }
            if (NaviConstants.NAVI_MODE_LANE.equals(naviMode)) {
                return NaviControlUtils.openLaneNavi(flowContext);
            }
            if (NaviConstants.NAVI_MODE_NORMAL.equals(naviMode)) {
                return NaviControlUtils.openNormalNavi(flowContext, false);
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
