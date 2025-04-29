package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviFullScreenSwitchAgent extends AbstractNaviAgent {

    @Override
    public String AgentName() {
        return "navi_full_screen#switch";
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
        boolean isDriveDeskTop = DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDriveDesktop();
        boolean isDriveRight = true;
        if (isDriveDeskTop) {
            isDriveRight = (ApplicationConstant.PACKAGE_NAME_MAP.equals(DeviceHolder.INS().getDevices().getSystem().getSplitScreen().getRightSpiltScreenPackage()));
        }
        if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront() && ((isDriveDeskTop && isDriveRight) || (!isDriveDeskTop && !DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen()))) {
                if (isDriveDeskTop) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "地图最大还只有三分之二窗口");
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000100));

                }
            } else {
                if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSupportDriveDesktop()) {
                    DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                } else {
                    DeviceHolder.INS().getDevices().getNavi().getNaviScreen().enterFullScreen();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000101));
                }

            }
        }
        if (Constant.CLOSE.equals(operation)) {
            if (!DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSupportDriveDesktop()) {
                DeviceHolder.INS().getDevices().getNavi().getNaviScreen().enterSplitScreen();
            } else {
                if (isDriveDeskTop && isDriveRight) {
                    DeviceHolder.INS().getDevices().getSystem().getSplitScreen().switchSplitScreen();
                }
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
