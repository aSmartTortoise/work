package com.voyah.ai.voice.agent.navi;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi#switch";
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
        String naviMode = getParamKey(paramsMap, NaviConstants.NAVI_MODE, 0);
        String screenName = getParamKey(paramsMap, FuncConstants.KEY_SCREEN_NAME, 0);
        if (FuncConstants.VALUE_SCREEN.equals(screenName)) {
            screenName = "";
        }
        if (TextUtils.isEmpty(screenName)) {
            String position = getParamKey(paramsMap, DCContext.MAP_KEY_SLOT_POSITION, 0);
            if (!TextUtils.isEmpty(position)) {
                int location = BaseAgentX.translateLocation(position);
                screenName = getScreenFromSound(location);
            }
        }
        int location = BaseAgentX.getAwakenLocation(flowContext);
        if (operation != null) {
            if (Constant.OPEN.equals(operation)) {
                if (naviMode == null || naviMode.isEmpty()) {
                    return NaviControlUtils.openNavi(flowContext, screenName, location);
                }
                if (NaviConstants.NAVI_MODE_NORMAL.equals(naviMode)) {
                    return NaviControlUtils.openNormalNavi(flowContext, false);
                }
                if (NaviConstants.NAVI_MODE_LANE.equals(naviMode)) {
                    return NaviControlUtils.openLaneNavi(flowContext);
                }

            }
            if (Constant.CLOSE.equals(operation)) {
                if (naviMode == null || naviMode.isEmpty()) {
                    return NaviControlUtils.closeNavi(flowContext, screenName, location);
                }
                if (NaviConstants.NAVI_MODE_NORMAL.equals(naviMode)) {
                    return NaviControlUtils.openLaneNavi(flowContext);
                }
                if (NaviConstants.NAVI_MODE_LANE.equals(naviMode)) {
                    return NaviControlUtils.openNormalNavi(flowContext, true);
                }
            }

        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
