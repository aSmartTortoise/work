package com.voyah.ai.basecar.navi;

import com.google.gson.reflect.TypeToken;
import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviPoiInterface;
import com.voice.sdk.device.navi.bean.LocationResult;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ds.common.tool.GsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaviPoiInterfaceImpl implements NaviPoiInterface {

    private static final String TAG = "NaviPoiInterfaceImpl";
    private final AbstractNaviInterfaceImpl abstractNaviInterface;

    public NaviPoiInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }

    @Override
    public NaviResponse<List<Poi>> searchPoi(String keyWord, int searchType, int radius) {
        LogUtils.i(TAG, "searchPoi:" + keyWord + ",searchType:" + searchType + ",radius:" + radius);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("searchPoi");
        map.put("actionType", params.getActionType());
        map.put("keyword", keyWord);
        map.put("protocol", 2);
        map.put("maxPoiNumber", 20);
        map.put("searchType", searchType);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<List<Poi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONObject jsonResult = naviResponse.getJsonObject().optJSONObject("result");
            if (jsonResult != null) {
                JSONArray jsonArray = jsonResult.optJSONArray("poiList");
                if (jsonArray != null) {
                    Type type = new TypeToken<List<Poi>>() {
                    }.getType();
                    List<Poi> poiList = GsonUtils.fromJson(jsonArray.toString(), type);
                    naviResponse.setData(poiList);
                    NaviInterfaceImpl.getInstance().getNaviStatus().setSearchPoiResult(poiList);
                }
            }
        }
        LogUtils.i(TAG, "size:" + (naviResponse.getData() != null ? naviResponse.getData().size() : 0));
        return naviResponse;
    }

    @Override
    public NaviResponse<List<Poi>> searchAroundPoi(String keyWord, Poi centorPoi, int radius) {
        LogUtils.i(TAG, "searchAroundPoi:" + keyWord);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("searchNearby");
        if (centorPoi != null) {
            if (centorPoi.getId() != null && !centorPoi.getId().isEmpty()) {
                map.put("centerPoi", centorPoi);
            } else {
                keyWord = centorPoi.getName() + "," + keyWord;
            }
        }
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("keyword", keyWord);
        map.put("maxPoiNumber", 20);
        if (radius != -1) {
            map.put("radius", radius);
        }
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<List<Poi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONObject jsonResult = naviResponse.getJsonObject().optJSONObject("result");
            if (jsonResult != null) {
                JSONArray jsonArray = jsonResult.optJSONArray("poiList");
                if (jsonArray != null) {
                    Type type = new TypeToken<List<Poi>>() {
                    }.getType();
                    List<Poi> poiList = GsonUtils.fromJson(jsonArray.toString(), type);
                    naviResponse.setData(poiList);
                    NaviInterfaceImpl.getInstance().getNaviStatus().setSearchPoiResult(poiList);
                }
            }
        }
        LogUtils.i(TAG, "size:" + (naviResponse.getData() != null ? naviResponse.getData().size() : 0));
        return naviResponse;

    }

    @Override
    public NaviResponse<List<Poi>> searchOnWay(String keyWord, boolean addViaPoint) {
        LogUtils.i(TAG, "searchOnWay:" + keyWord);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("searchAlongRoutePoi");
        map.put("actionType", params.getActionType());
        map.put("alongRouteKeyWord", keyWord);
        map.put("intent", addViaPoint ? 0 : 1);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<List<Poi>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONObject jsonResult = naviResponse.getJsonObject().optJSONObject("result");
            if (jsonResult != null) {
                JSONArray jsonArray = jsonResult.optJSONArray("poiList");
                if (jsonArray != null) {
                    Type type = new TypeToken<List<Poi>>() {
                    }.getType();
                    List<Poi> poiList = GsonUtils.fromJson(jsonArray.toString(), type);
                    naviResponse.setData(poiList);
                    NaviInterfaceImpl.getInstance().getNaviStatus().setSearchPoiResult(poiList);
                }
            }
        }
        LogUtils.i(TAG, "size:" + (naviResponse.getData() != null ? naviResponse.getData().size() : 0));
        return naviResponse;
    }

    @Override
    public NaviResponse<Poi> locateSelf() {
        LogUtils.i(TAG, "locateSelf");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("locate");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("mapLabel", false);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<Poi> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONObject jsonPoi = naviResponse.getJsonObject().optJSONObject("poi");
            if (jsonPoi != null) {
                Poi locationResult = GsonUtils.fromJson(jsonPoi.toString(), Poi.class);
                naviResponse.setData(locationResult);
            }
        }
        return naviResponse;
    }
}
