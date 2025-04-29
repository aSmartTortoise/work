package com.voyah.ai.basecar.navi;

import android.provider.Settings;

import com.google.gson.reflect.TypeToken;
import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.context.DeviceContextUtils;
import com.voice.sdk.context.DeviceInfo;
import com.voice.sdk.context.ReportConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviConvert;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.NaviStatusInterface;
import com.voice.sdk.device.navi.bean.NaviInfo;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStatus;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.GpsUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NaviStatusInterfaceImpl implements NaviStatusInterface {

    private static final String TAG = "NaviStatusInterfaceImpl";
    private String lastNaviStatus = "";

    private boolean isExitDialog = true;

    private int lastPageValue = -1;

    private NaviStatus naviStatus;

    private final AbstractNaviInterfaceImpl abstractNaviInterface;

    private String lastSceneReport;

    public NaviStatusInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }


    private void exitDialog() {
        LogUtils.i(TAG, "exitDialog:" + isExitDialog + ",sessionId:" + DeviceHolder.INS().getDevices().getNavi().getNaviStash().getSessionId());
        if (!isExitDialog) {
            isExitDialog = true;
            lastSceneReport = null;
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStash().getSessionId() != null) {
                String sessionId = DeviceHolder.INS().getDevices().getNavi().getNaviStash().getSessionId();
                LogUtils.i(TAG, "exitDialog sessionId:" + sessionId);
                UIMgr.INSTANCE.forceExitSessionAct(sessionId);
                VoiceImpl.getInstance().exitSessionDialog(DeviceHolder.INS().getDevices().getNavi().getNaviStash().getSessionId());
                DeviceHolder.INS().getDevices().getNavi().getNaviStash().setSessionId(null);
            } else {
                VoiceImpl.getInstance().cleanSceneReport();
            }
        }
    }

    private void sceneReport(String state, String tts, Map<String, Object> params) {
        tts = tts != null ? tts : "";
        LogUtils.i(TAG, "sceneReport:" + state + ",tts:" + tts + ",params:" + GsonUtils.toJson(params));
        Map<String, Object> curReport = new HashMap<>();
        curReport.put("state", state);
        curReport.put("tts", tts);
        curReport.put("params", params);
        String curStr = GsonUtils.toJson(curReport);
        if (curStr != null && !curStr.equals(lastSceneReport)) {
            LogUtils.i(TAG, "real report:" + curStr);
            isExitDialog = false;
            VoiceImpl.getInstance().sceneReport(UUID.randomUUID().toString(), state, tts, params);
            lastSceneReport = curStr;
        } else {
            LogUtils.i(TAG, "ignore report");
        }

    }

    @Override
    public void parseNaviStatusReport(String naviStatusInfo) {
        if (naviStatusInfo != null && !naviStatusInfo.equals(lastNaviStatus)) {
            LogUtils.i(TAG, "navi status:" + naviStatusInfo);
            lastNaviStatus = naviStatusInfo;
            naviStatus = GsonUtils.fromJson(naviStatusInfo, NaviStatus.class);
            if (naviStatus != null) {
                if (naviStatus.getCurrentPoi() != null) {
                    String poiStr = GsonUtils.toJson(naviStatus.getCurrentPoi());
                    GpsUtils.setCurrentLocation(poiStr);
                    if (poiStr != null) {
                        DeviceContextUtils.getInstance().updateDeviceInfo(DeviceInfo.build(ReportConstant.KEY_GPS, poiStr));
                    }
                }
                if (!naviStatus.isResume()) {
                    exitDialog();
                    LogUtils.i(TAG, "exitDialog because is not resume");
                    return;
                }
                Poi destPoi = naviStatus.getDestPoi();
                String jsonDestPoi = GsonUtils.toJson(destPoi);
                DeviceContextUtils.getInstance().updateDeviceInfo(DeviceInfo.build(ReportConstant.KEY_DEST_POI, destPoi != null ? jsonDestPoi : ""));
                NaviPage naviPage = NaviPage.fromValue(naviStatus.getPageType());
                LogUtils.i(TAG, "naviPageValue:" + naviPage + ",fullScreen:" + naviStatus.isFullScreen() + ",searchType:" + naviStatus.getSearchType() + ",poiSize:" + (naviStatus.getSearchPoiList() != null ? naviStatus.getSearchPoiList().size() : 0));
                Map<String, Object> params = new HashMap<>();
                List<com.voyah.ds.common.entity.domains.navi.Poi> dsPoiList = new ArrayList<>();
                String state = null;
                String tts = null;
                if (isInPoiSearchResult() && getCurrentPoiList() != null && !getCurrentPoiList().isEmpty()) {
                    int size = getCurrentPoiList().size();
                    List<Poi> poiList = getCurrentPoiList();
                    if (size > 0) {
                        for (Poi p : poiList) {
                            dsPoiList.add(NaviConvert.convertToDsPoi(p));
                        }
                    }
                    if (size == 1) {
                        if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_VIA_POINT) {
                            state = NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_ONE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_ONE.getTts()).getSelectTTs();
                        } else if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_HOME) {
                            state = NaviResponseCode.FAVORITE_POI_RESULT_ONE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.FAVORITE_POI_RESULT_ONE.getTts()).getSelectTTs();
                            tts = String.format(Locale.getDefault(), tts, NaviConstants.HOME);

                        } else if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_COMPANY) {
                            state = NaviResponseCode.FAVORITE_POI_RESULT_ONE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.FAVORITE_POI_RESULT_ONE.getTts()).getSelectTTs();
                            tts = String.format(Locale.getDefault(), tts, NaviConstants.COMPANY);
                        } else {
                            state = NaviResponseCode.SEARCH_POI_RESULT_ONE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.SEARCH_POI_RESULT_ONE.getTts()).getSelectTTs();
                        }
                        params.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_NAVI_POIS_LIST, dsPoiList);
                    } else if (size > 1) {
                        if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_VIA_POINT) {
                            state = NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_MULTIPLE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_MULTIPLE.getTts()).getSelectTTs();
                        } else if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_HOME) {
                            state = NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getTts()).getSelectTTs();
                            tts = String.format(Locale.getDefault(), tts, NaviConstants.HOME);
                        } else if (naviStatus.getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_COMPANY) {
                            state = NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getTts()).getSelectTTs();
                            tts = String.format(Locale.getDefault(), tts, NaviConstants.COMPANY);
                        } else {
                            state = NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getState();
                            tts = TtsBeanUtils.getTtsBean(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getTts()).getSelectTTs();
                        }
                        params.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_NAVI_POIS_LIST, dsPoiList);
                    }

                }
                if (naviStatus.getPageType() == NaviPage.PAGE_FAV.getValue()) {
                    state = NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getState();
                    tts = TtsBeanUtils.getTtsBean(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getTts()).getSelectTTs();
                }
                if (isInRoutePlan()) {
                    state = NaviResponseCode.ROUTE_PLAN.getState();
                    tts = TtsBeanUtils.getTtsBean(NaviResponseCode.ROUTE_PLAN.getTts()).getSelectTTs();
                    List<String> routeNames = new ArrayList<>();
                    for (int i = 0; i < getRoutePaths(); i++) {
                        routeNames.add("路线" + (i + 1));
                    }
                    params.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_ROUTE_NAMES, routeNames);
                }
                if (state != null) {
                    sceneReport(state, tts, params);
                } else {
                    if (naviPage.getValue() != lastPageValue) {
                        LogUtils.i(TAG, "exitDialog");
                        exitDialog();
                    }
                }
                lastPageValue = naviPage.getValue();
            }
        }

    }

    @Override
    public boolean isInFront() {
        boolean inFront;
        if (naviStatus != null) {
            inFront = naviStatus.isResume();
        } else {
            inFront = false;
        }
        LogUtils.i(TAG, "isInFront:" + inFront);
        return inFront;

    }

    @Override
    public boolean isFullScreen() {
        boolean isFull = false;
        if (naviStatus != null) {
            isFull = naviStatus.isFullScreen();
        }
        LogUtils.i(TAG, "isFullScreen:" + isFull);
        return isFull;
    }

    @Override
    public boolean isLogin() {
        boolean isLogin = false;
        if (naviStatus != null) {
            isLogin = naviStatus.isLogin();
        }
        LogUtils.i(TAG, "isLogin:" + isLogin);
        return isLogin;
    }

    @Override
    public boolean isHideInformation() {
        int hasPermission = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(),
                "system_info_hiding", 0);
        LogUtils.i(TAG, "system_info_hiding:" + (hasPermission != 0));
        return hasPermission != 0;
    }

    @Override
    public boolean isQuickNavi() {
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getQuickDepart");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        params.setParams(com.voyah.ds.common.tool.GsonUtils.toJson((map)));
        boolean isQuickNavi = false;
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<Integer> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            int quickNavi = naviResponse.getJsonObject().optInt("result");
            naviResponse.setData(quickNavi);
            isQuickNavi = (quickNavi == 0);
        }
        LogUtils.i(TAG, "isQuickNavi:" + isQuickNavi);
        return isQuickNavi;
    }

    @Override
    public boolean isInNavigation() {
        boolean isInNavigation = false;
        if (naviStatus != null) {
            isInNavigation = (naviStatus.getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_STARTED ||
                    naviStatus.getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_FAMILIAR);
        }
        LogUtils.i(TAG, "isInNavigation:" + isInNavigation);
        return isInNavigation;
    }

    @Override
    public boolean isPlanRoute() {
        boolean isPlanRoute = false;
        if (naviStatus != null) {
            isPlanRoute = naviStatus.getNaviStatus() == NaviConstants.NaviStatueType.NAVIGATION_ROUTE_PLANNING;
        }
        LogUtils.i(TAG, "isPlanRoute:" + isPlanRoute);
        return isPlanRoute;
    }

    @Override
    public boolean isDirectNavi() {
        boolean result = isInNavigation() || isQuickNavi();
        LogUtils.i(TAG, "isDirectNavi:" + result);
        return result;
    }

    @Override
    public boolean isInPoiSearchResult() {
        boolean isInPoiSearchResult = false;
        if (naviStatus != null) {
            isInPoiSearchResult = (naviStatus.getPageType() == NaviPage.PAGE_SEARCH_RESULT.getValue()
                    || naviStatus.getPageType() == NaviPage.PAGE_NAVI_SEARCH_RESULT.getValue());
        }
        LogUtils.i(TAG, "isInPoiSearchResult:" + isInPoiSearchResult);
        return isInPoiSearchResult;
    }

    @Override
    public boolean isInRoutePlan() {
        boolean isInRoutePlan = false;
        if (naviStatus != null) {
            isInRoutePlan = (naviStatus.getPageType() == NaviPage.PAGE_ROUTE.getValue());
        }
        LogUtils.i(TAG, "isInRoutePlan:" + isInRoutePlan);
        return isInRoutePlan;
    }

    @Override
    public boolean isAgreeDisclaimer() {
        int value = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(),
                "persist.sys.map.disclaimer_status", -1);
        LogUtils.i(TAG, "isAgreeDisclaimer:" + value);
        return value == 1;
    }

    @Override
    public boolean isActivation() {
        int value = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(),
                "persist.sys.map.disclaimer_status", -1);
        LogUtils.i(TAG, "isActivation:" + value);
        return value != 2;
    }

    @Override
    public int getLeftDistance() {
        if (naviStatus != null) {
            return naviStatus.getLeftDistance();
        }
        return -1;
    }

    @Override
    public Poi getDestPoi() {
        if (naviStatus != null) {
            return naviStatus.getDestPoi();
        }
        return null;
    }

    @Override
    public Poi getLastDestPoi() {
        LogUtils.i(TAG, "getLastDestPoi");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getLastNaviDestPoi");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<Poi> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            Poi poi = GsonUtils.fromJson(naviResponse.getJsonObject().optString("lastNaviDestPoi"), Poi.class);
            naviResponse.setData(poi);
        }
        return naviResponse.getData();
    }

    @Override
    public int getCurrentPage() {
        if (naviStatus != null) {
            return naviStatus.getPageType();
        }
        return -1;
    }

    @Override
    public int getLeftTime() {
        if (naviStatus != null) {
            return naviStatus.getLeftTime();
        }
        return -1;
    }

    @Override
    public List<Poi> getCurrentPoiList() {
        if (naviStatus != null) {
            int size = naviStatus.getSearchPoiList() != null ? naviStatus.getSearchPoiList().size() : 0;
            LogUtils.i(TAG, "getCurrentPoiList:" + size);
            return naviStatus.getSearchPoiList();
        } else {
            LogUtils.i(TAG, "getCurrentPoiList null");
            return null;
        }
    }

    @Override
    public int getCurrentPoiListSize() {
        List<Poi> list = getCurrentPoiList();
        int size = (list != null ? list.size() : 0);
        LogUtils.i(TAG, "getCurrentPoiListSize:" + size);
        return size;
    }

    @Override
    public int getRoutePaths() {
        LogUtils.i(TAG, "getRoutePaths:" + naviStatus);
        if (naviStatus != null) {
            return naviStatus.getRoutePaths();
        }
        return 0;
    }

    @Override
    public int getSearchType() {
        int type = naviStatus != null ? naviStatus.getSearchType() : NaviConstants.SearchUpdateType.SEARCH_TYPE_NORMAL;
        LogUtils.i(TAG, "getSearchType:" + type);
        return type;
    }

    @Override
    public int getRoutePathSelected() {
        LogUtils.i(TAG, "getRoutePathSelected:" + naviStatus);
        if (naviStatus != null) {
            return naviStatus.getRoutePathSelected();
        }
        return 0;
    }

    @Override
    public void setSearchPoiResult(List<Poi> poiList) {
        LogUtils.i(TAG, "setSearchPoiResult:" + poiList);
    }

    @Override
    public NaviResponse<List<Poi>> getFavoritesPoiList(int type, boolean checkPermission) {
        if (checkPermission && isHideInformation()) {
            return new NaviResponse<>(NaviConstants.ErrCode.NO_PERMISSION);
        }
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getFavPoi");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("favPoiType", type);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<List<Poi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONArray jsonArray = naviResponse.getJsonObject().optJSONArray("favPois");
            if (jsonArray != null) {
                Type classType = new TypeToken<List<Poi>>() {
                }.getType();
                List<Poi> poiList = com.voyah.ds.common.tool.GsonUtils.fromJson(jsonArray.toString(), classType);
                naviResponse.setData(poiList);
            }
        }
        return naviResponse;
    }

    @Override
    public NaviResponse<String> setFavoritesPoi(int type, Poi poi) {
        LogUtils.i(TAG, "setFavoritesPoiList:" + type + ",poi:" + GsonUtils.toJson(poi));
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("addFavPoi");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("favPoiType", type);
        map.put("favPoi", poi);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<NaviInfo> getNaviInfo() {
        LogUtils.i(TAG, "getNaviInfo");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getNaviInfo");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<NaviInfo> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            NaviInfo naviInfo = GsonUtils.fromJson(naviResponse.getJsonObject().optString("result"), NaviInfo.class);
            naviResponse.setData(naviInfo);
        }
        return naviResponse;
    }

    @Override
    public int getCityCode() {
        LogUtils.i(TAG, "getCityCode:" + naviStatus);
        if (naviStatus != null && naviStatus.getCurrentPoi() != null) {
            return naviStatus.getCurrentPoi().getCityCode();
        }
        return 0;
    }


    @Override
    public NaviStatus getNaviStatus() {
        return naviStatus;
    }

    @Override
    public NaviResponse<List<Poi>> getHistoryPoiList(int type) {
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getHistoryInfo");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("historyType", type);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<List<Poi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONArray jsonArray = naviResponse.getJsonObject().optJSONArray("historyInfoList");
            if (jsonArray != null) {
                Type typeToken = new TypeToken<List<Poi>>() {
                }.getType();
                List<Poi> poiList = com.voyah.ds.common.tool.GsonUtils.fromJson(jsonArray.toString(), typeToken);
                naviResponse.setData(poiList);
                NaviInterfaceImpl.getInstance().getNaviStatus().setSearchPoiResult(poiList);
            }
        }
        return naviResponse;
    }

}
