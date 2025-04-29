package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviHistorySwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_history#switch";
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
        String switchType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        if (Constant.OPEN.equals(switchType)) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "信息隐藏已开启，无法查看导航历史记录。");
            }
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchHistoryPage(true);
            if (naviResponse.isAlreadySuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "导航记录已打开");

            } else if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
            }
        } else if (Constant.CLOSE.equals(switchType)) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchHistoryPage(false);
            if (naviResponse.isAlreadySuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "导航记录已关闭");

            } else if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
            }
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
