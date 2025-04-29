package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviRoutePlan;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviRoutePlanSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_route_plan#switch";
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
        String routeType = getParamKey(paramsMap, NaviConstants.ROUTE_TYPE, 0);
        String switchType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        NaviRoutePlan value = NaviRoutePlan.fromValue(routeType);

        switch (switchType) {
            case Constant.OPEN:
                if (value.getValue() != -1) {
                    NaviResponse<String> naviResponse = null;
                    if (NaviRoutePlan.AVOID_RESTRICTION.equals(value)) {
                        naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchAvoidRestriction(true);
                        if (naviResponse != null && naviResponse.getResultCode() == NaviConstants.ErrCode.NEED_LICENSE_PLATE) {
                            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchLicensePlatePage(true);
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3006703));
                        } else {
                            TTSBean tts;
                            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                                tts = TtsBeanUtils.getTtsBean(3006702);
                            } else {
                                if (naviResponse != null && naviResponse.isAlreadySuccess()) {
                                    tts = TtsBeanUtils.getTtsBean(3005702, "规避限行");
                                } else {
                                    tts = TtsBeanUtils.getTtsBean(3006700);
                                }
                            }
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
                        }
                    } else {
                        TTSBean tts = TtsBeanUtils.getTtsBean(1100005);
                        if (!DeviceHolder.INS().getDevices().getNavi().getNaviSetting().isSupportNoa() && NaviRoutePlan.NAVI_PRIORITY.equals(value)) {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3018000));
                        }
                        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviStatus().getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_ROUTE_PLANNING) {
                            if (NaviRoutePlan.DISTANCE_SHORTEST.equals(value)) {
                                tts = TtsBeanUtils.getTtsBean(3005800);
                            }
                            naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePlanInNavi(value.getValue());
                        } else {
                            if (NaviRoutePlan.DISTANCE_SHORTEST.equals(value)) {
                                tts = TtsBeanUtils.getTtsBean(3007102);
                            } else {
                                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePlan(value.getValue());

                            }
                        }
                        if (!NaviRoutePlan.DISTANCE_SHORTEST.equals(value)) {
                            if (naviResponse != null && (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_OPEN || naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_CLOSE)) {
                                tts = TtsBeanUtils.getTtsBean(3005702, value.getChName());
                            } else if (naviResponse != null && naviResponse.isSuccess()) {
                                if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInRoutePlan()) {
                                    tts = TtsBeanUtils.getTtsBean(value.getTtsId());
                                } else {
                                    tts = TtsBeanUtils.getTtsBean(3005703, value.getChName());
                                }
                            } else {
                                tts = TtsBeanUtils.getTtsBean(3005701);
                            }
                        }
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
                    }
                } else if (NaviRoutePlan.TRAFFIC_LIGHT_LEAST.equals(value)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3006200));
                } else if (NaviRoutePlan.AVOID.equals(value)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3006800));

                }

                break;
            case Constant.CLOSE:
                if (NaviRoutePlan.AVOID_RESTRICTION.equals(value)) {
                    DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchAvoidRestriction(true);
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                }
                break;
            case Constant.CHANGE:
                if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviStatus().getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_ROUTE_PLANNING) {
                    int routeSize = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePaths();
                    if (routeSize <= 1) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3005500));
                    }
                    int selected = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePathSelected();
                    selected = (selected + 1) % routeSize;
                    NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().selectRoutePlanInNavi(selected);
                    if (naviResponse.isSuccess()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                    }
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000900));
                }
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }


}
