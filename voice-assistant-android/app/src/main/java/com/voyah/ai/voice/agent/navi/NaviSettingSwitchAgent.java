package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviSettingSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_setting#switch";
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
        if (Constant.OPEN.equals(operation) || Constant.CHANGE.equals(operation) || Constant.RETURN.equals(operation)) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage() == NaviPage.PAGE_NAV_SETTING.getValue()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013301));

            } else {
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(true);
                if (naviResponse != null && naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
            }
        }
        if (Constant.CLOSE.equals(operation)) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage() == NaviPage.PAGE_NAV_SETTING.getValue()) {
                NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(false);
                if (naviResponse != null && naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013500));
            }

        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
