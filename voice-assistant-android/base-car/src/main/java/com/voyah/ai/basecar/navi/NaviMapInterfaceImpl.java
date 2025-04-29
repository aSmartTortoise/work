package com.voyah.ai.basecar.navi;

import com.google.gson.reflect.TypeToken;
import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviMapInterface;
import com.voice.sdk.device.navi.bean.HighWayPoi;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ds.common.tool.GsonUtils;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaviMapInterfaceImpl implements NaviMapInterface {
    private static final String TAG = "NaviMapInterfaceImpl";
    private final AbstractNaviInterfaceImpl abstractNaviInterface;

    public NaviMapInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }

    @Override
    public void setThemeStyle(int theme) {
        LogUtils.i(TAG, "setThemeStyle:" + theme);
        DeviceHolder.INS().getDevices().getSystem().getAttribute().setThemeStyle(theme);
    }

    @Override
    public int getThemeStyle() {
        LogUtils.i(TAG, "getThemeStyle");
        return DeviceHolder.INS().getDevices().getSystem().getAttribute().getThemeStyle();
    }

    @Override
    public NaviResponse<String> zoomMap(boolean big) {
        LogUtils.i(TAG, "zoomMap:" + big);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("mapScaleAdjust");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        map.put("optType", big ? 1 : 0);
        map.put("adjustValue", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> maxMinMap(boolean max) {
        LogUtils.i(TAG, "mapMax");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("mapScale");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        map.put("scaleLevel", max ? NaviConstants.MAP_MAX_LEVEL : NaviConstants.MAP_MIN_LEVEL);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);

    }

    @Override
    public NaviResponse<String> switchTraffic(boolean open) {
        LogUtils.i(TAG, "switchTraffic:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optTraffic");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> adjustViewMode(int viewMode) {
        LogUtils.i(TAG, "adjustViewMode:" + viewMode);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("mapMode");
        map.put("actionType", params.getActionType());
        map.put("mapViewMode", viewMode);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<Integer> getViewMode() {
        LogUtils.i(TAG, "getViewMode");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("getMapViewMode");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<Integer> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            int teamInfo = naviResponse.getJsonObject().optInt("result");
            naviResponse.setData(teamInfo);
        }
        return naviResponse;
    }

    @Override
    public NaviResponse<String> changeRoad(int roadType) {
        LogUtils.i(TAG, "changeRoad:" + roadType);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("changeRoadType");
        map.put("actionType", params.getActionType());
        map.put("roadChangeType", roadType);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchAutoScale(boolean open) {
        LogUtils.i(TAG, "switchAutoScale:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optAutoRuler");
        map.put("optType", open ? 0 : 1);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> openMapDownloadPage() {
        LogUtils.i(TAG, "openMapDownloadPage");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("offlineMap");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> queryTrafficInfo() {
        LogUtils.i(TAG, "queryTrafficInfo");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("queryTraffic");
        map.put("actionType", params.getActionType());
        map.put("optType", 1);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String data = naviResponse.getJsonObject().optString("result");
            naviResponse.setData(data);
        }
        return naviResponse;
    }

    @Override
    public NaviResponse<List<HighWayPoi>> queryServiceAreaInfo() {
        LogUtils.i(TAG, "queryServiceAreaInfo");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("queryHighWayInfo");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 0);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<List<HighWayPoi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONArray jsonArray = naviResponse.getJsonObject().optJSONArray("result");
            if (jsonArray != null && jsonArray.length() > 0) {
                Type type = new TypeToken<List<HighWayPoi>>() {
                }.getType();
                List<HighWayPoi> highWayPoiList = GsonUtils.fromJson(jsonArray.toString(), type);
                naviResponse.setData(highWayPoiList);
            }
        }
        return naviResponse;
    }

    @Override
    public NaviResponse<List<HighWayPoi>> queryTollStationInfo() {
        LogUtils.i(TAG, "queryTollStationInfo");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("queryHighWayInfo");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<List<HighWayPoi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONArray jsonArray = naviResponse.getJsonObject().optJSONArray("result");
            if (jsonArray != null && jsonArray.length() > 0) {
                Type type = new TypeToken<List<HighWayPoi>>() {
                }.getType();
                List<HighWayPoi> highWayPoiList = GsonUtils.fromJson(jsonArray.toString(), type);
                naviResponse.setData(highWayPoiList);
            }
        }
        return naviResponse;
    }

    @Override
    public NaviResponse<String> prevPage() {
        LogUtils.i(TAG, "prevPage");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optSearchResult");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("action", 3);
        params.setParams(com.voyah.ai.common.utils.GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> nextPage() {
        LogUtils.i(TAG, "nextPage");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optSearchResult");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("action", 4);
        params.setParams(com.voyah.ai.common.utils.GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> openHighWayInfoView() {
        LogUtils.i(TAG, "openHighWayInfoView");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optHighWayList");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("optType", 0);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

}
