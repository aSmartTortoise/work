package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviTeamChooseAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_team#choose";
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
        String chooseType = getParamKey(paramsMap, NaviConstants.CHOOSE_TYPE, 0);
        if (NaviConstants.CONFIRM.equals(chooseType)) {
            NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviTeam().getTeamInfo();
            if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() == NaviConstants.TeamType.TEAM_MEMBER) {
                DeviceHolder.INS().getDevices().getNavi().getNaviTeam().exitTeam();
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            }
            if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() == NaviConstants.TeamType.TEAM_CAPTAIN) {
                DeviceHolder.INS().getDevices().getNavi().getNaviTeam().disbandTeam();
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            }
        }
        if (NaviConstants.CANCEL.equals(chooseType)) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

    }


}
