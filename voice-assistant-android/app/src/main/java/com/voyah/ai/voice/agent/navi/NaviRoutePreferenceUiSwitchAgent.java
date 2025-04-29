package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoutePreferenceUiSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_route_preference_ui#switch";
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
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage() == NaviPage.PAGE_NAV_SETTING.getValue()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013801));
            } else {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(true);
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            }
        }
        if (Constant.CLOSE.equals(operation)) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(false);
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
