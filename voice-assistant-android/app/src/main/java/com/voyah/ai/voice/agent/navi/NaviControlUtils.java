package com.voyah.ai.voice.agent.navi;

import android.text.TextUtils;

import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NaviControlUtils {
    private static final String TAG = "NaviControlUtils";

    private NaviControlUtils() {

    }

    public static String getScreenName(String screenName) {
        switch (screenName) {
            case FuncConstants.VALUE_SCREEN_CEIL:
                return "吸顶屏";
            case FuncConstants.VALUE_SCREEN_PASSENGER:
                return "副驾屏";
            default:
                return "中控屏";
        }
    }

    public static ClientAgentResponse openNavi(Map<String, Object> flowContext, String screenName, int location) {
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.fromValue(screenName))) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100028));
        }
        boolean isDriveRight = true;
        if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSupportDriveDesktop() &&
                DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDriveDesktop()) {
            isDriveRight = (ApplicationConstant.PACKAGE_NAME_MAP.equals(DeviceHolder.INS().getDevices().getSystem().getSplitScreen().getRightSpiltScreenPackage()));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront() && isDriveRight) {
            if (TextUtils.isEmpty(screenName)) {
                if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100001, "地图"));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100029, getScreenName(screenName), "地图"));
                }
            } else if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(screenName)) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100029, getScreenName(screenName), "地图"));
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100034, getScreenName(screenName), "地图"));
            }
        } else {
            if (TextUtils.isEmpty(screenName)) {
                DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100002, "地图"));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100030, getScreenName(screenName), "地图"));
                }
            } else {
                if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(screenName)) {
                    DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100030, getScreenName(screenName), "地图"));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100034, getScreenName(screenName), "地图"));

                }
            }
        }
    }


    public static ClientAgentResponse closeNavi(Map<String, Object> flowContext, String screenName, int location) {
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.fromValue(screenName))) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100028));
        }
        boolean isDriveDeskTop = DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDriveDesktop();
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            if (isDriveDeskTop) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "当前场景无法关闭该应用");
            }
            if (TextUtils.isEmpty(screenName)) {
                if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    DeviceHolder.INS().getDevices().getNavi().closeNaviApp();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100003, "地图"));
                } else {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    DeviceHolder.INS().getDevices().getNavi().closeNaviApp();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100031, getScreenName(screenName), "地图"));
                }
            } else {
                if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(screenName)) {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    DeviceHolder.INS().getDevices().getNavi().closeNaviApp();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100031, getScreenName(screenName), "地图"));

                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100035, getScreenName(screenName), "地图"));
                }
            }
        } else {
            if (TextUtils.isEmpty(screenName)) {
                if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100004, "地图"));
                } else {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100032, getScreenName(screenName), "地图"));

                }
            } else {
                if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(screenName)) {
                    DeviceHolder.INS().getDevices().getNavi().stopNavi();
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100032, getScreenName(screenName), "地图"));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100035, getScreenName(screenName), "地图"));
                }
            }
        }
    }

    public static ClientAgentResponse openNormalNavi(Map<String, Object> flowContext, boolean exitLane) {
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setLaneLevelNavigation(false);
        if (naviResponse.isSuccess()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_LANE_LEVEL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014400));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_NORMAL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(exitLane ? 3014501 : 3014400));

        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.NOT_SUPPORT_TO_LANE_LEVEL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014302));

        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_NAVI) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }

    public static ClientAgentResponse openLaneNavi(Map<String, Object> flowContext) {
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setLaneLevelNavigation(true);
        if (naviResponse.isSuccess()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_LANE_LEVEL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014400));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_NORMAL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014400));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.NOT_SUPPORT_TO_LANE_LEVEL_NAVIGATION) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014302));
        } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_NAVI) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }

    public static ClientAgentResponse closeLaneNavi(Map<String, Object> flowContext) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
        } else {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setLaneLevelNavigation(false);
            if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014501));

            }
        }
    }

    public static ClientAgentResponse returnNavigation(Map<String, Object> flowContext) {
        boolean alreadyOpen = true;
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            DeviceHolder.INS().getDevices().getNavi().openNaviApp();
            alreadyOpen = false;
        }
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().continueNavi();
        if (naviResponse != null && naviResponse.isSuccess()) {
            alreadyOpen = false;
        }
        int page = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage();
        if (!(page == NaviPage.PAGE_NAV.getValue() || page == NaviPage.PAGE_HOME.getValue())) {
            DeviceHolder.INS().getDevices().getNavi().backToNaviHome();
            alreadyOpen = false;
        }
        DeviceHolder.INS().getDevices().getNavi().backToNaviHome();
        TTSBean tts = alreadyOpen ? TtsBeanUtils.getTtsBean(3000400) : TtsBeanUtils.getTtsBean(1100005);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
    }

    public static ClientAgentResponse startNavigation(Map<String, Object> flowContext) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            DeviceHolder.INS().getDevices().getNavi().openNaviApp();
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInRoutePlan()) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviStatus().getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_STARTED) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000500));
            }
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().startNavi(-1);
            if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.START_NAVIGATION.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.END_POINT_NULL) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "抱歉，没有找到您想去的目的地");
            } else {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015100));

            }
        } else if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInPoiSearchResult()) {
            List<Poi> poiList = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiList();
            if (poiList != null && !poiList.isEmpty()) {
                Poi poi = poiList.get(0);
                return NaviPoiUtils.naviSelectPoi(flowContext, poi, DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), null);
            }
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000500));

        } else {
            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_DES_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001000));
        }
    }

    public static ClientAgentResponse cancelNavigation(Map<String, Object> flowContext) {
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            DeviceHolder.INS().getDevices().getNavi().stopNavi();
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        } else {
            Object state = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
            if (state instanceof String) {
                if (NaviResponseCode.isNaviState((String) state)) {
                    return NaviControlUtils.cancelSelect(flowContext);
                }
            }
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInPoiSearchResult() || DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInRoutePlan()) {
                DeviceHolder.INS().getDevices().getNavi().backToNaviHome();
            }
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()
                    && !DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDriveDesktop()) {
                DeviceHolder.INS().getDevices().getNavi().closeNaviApp();
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000705));

            }
        }
    }

    public static ClientAgentResponse resumeNavigation(Map<String, Object> flowContext) {
        boolean isInFront = true;
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            isInFront = false;
            DeviceHolder.INS().getDevices().getNavi().openNaviApp();
        }
        NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().continueNavi();
        if ((naviResponse != null && naviResponse.isSuccess())) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            if (!isInFront) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));

            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3000601));

            }
        } else {
            Poi lastPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getLastDestPoi();
            if (lastPoi == null) {
                return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_DES_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001000));
            } else {
                String tts = "您最近一次导航目的地是%s,需要继续导航吗？";
                tts = String.format(Locale.getDefault(), tts, lastPoi.getName());
                DeviceHolder.INS().getDevices().getNavi().getNaviStash().setLastPoi(lastPoi);
                return new ClientAgentResponse(NaviResponseCode.CONTINUE_NAVI_CONFIRM.getValue(), flowContext, tts);
            }
        }
    }

    public static ClientAgentResponse cancelSelect(Map<String, Object> flowContext) {
        Object state = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        LogUtils.i(TAG, "cancelSelect:" + state);
        if (state != null) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                DeviceHolder.INS().getDevices().getNavi().continueNavi();
            } else {
                DeviceHolder.INS().getDevices().getNavi().backToNaviHome();
            }
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
    }

}
