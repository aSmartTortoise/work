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
public class NaviSplitScreenSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_split_screen#switch";
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
    public boolean isNeedShowToast() {
        return false;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String operation = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        if (operation != null) {
            if (Constant.OPEN.equals(operation) || Constant.RETURN.equals(operation)) {
                if (!DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSupportDriveDesktop()) {
                    if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000900));
                    }
                    if (DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000800));
                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000801));
                    }
                } else {
                    if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDriveDesktop() && DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
                        if (ApplicationConstant.PACKAGE_NAME_MAP.equals(DeviceHolder.INS().getDevices().getSystem().getSplitScreen().getLeftSpiltScreenPackage())) {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "左侧分屏已经是导航了哦");
                        } else {
                            DeviceHolder.INS().getDevices().getSystem().getSplitScreen().switchSplitScreen();
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

                        }
                    } else {
                        DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "先为您打开地图吧");
                    }
                }
            }
            if (Constant.CLOSE.equals(operation)) {
                if (!DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSupportDriveDesktop()) {
                    if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000900));
                    }
                    if (!DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000902));

                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000901));
                    }
                } else {
                    if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "当前没有分屏导航哦");
                    } else {
                        if (ApplicationConstant.PACKAGE_NAME_MAP.equals(DeviceHolder.INS().getDevices().getSystem().getSplitScreen().getLeftSpiltScreenPackage())) {
                            DeviceHolder.INS().getDevices().getSystem().getSplitScreen().switchSplitScreen();
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "好的，左侧切换为智驾了");
                        } else {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "现在左侧不是导航哦");
                        }
                    }
                }
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
