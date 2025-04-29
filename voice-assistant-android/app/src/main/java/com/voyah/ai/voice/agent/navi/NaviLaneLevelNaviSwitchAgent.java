package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviLaneLevelNaviSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_lane_level_navi#switch";
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
        if (operation != null) {
            if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
                return NaviControlUtils.openLaneNavi(flowContext);
            }
            if (Constant.CLOSE.equals(operation)) {
                return NaviControlUtils.openNormalNavi(flowContext, true);
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103)
        );
    }


}
