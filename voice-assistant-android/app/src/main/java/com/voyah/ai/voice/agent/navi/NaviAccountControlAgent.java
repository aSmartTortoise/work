package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviAccountControlAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_account#control";
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
        String controlType = getParamKey(paramsMap, Constant.CONTROL_TYPE, 0);
        if (NaviConstants.LOGIN_IN.equals(controlType)) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviLoginPage(true);
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "请尝试手动登录。");
        } else if (NaviConstants.LOGIN_OUT.equals(controlType)) {
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isLogin()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "地图当前还没有账号登录");
            } else {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviLoginPage(true);
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "请手动确认退出。");
            }
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
