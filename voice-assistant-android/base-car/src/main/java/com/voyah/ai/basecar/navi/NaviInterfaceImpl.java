package com.voyah.ai.basecar.navi;


import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.device.navi.bean.SimplePoi;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ds.common.tool.GsonUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaviInterfaceImpl extends AbstractNaviInterfaceImpl {
    private static final String TAG = "NaviInterfaceImpl";

    private NaviInterfaceImpl() {

    }

    private static final NaviInterfaceImpl naviInterface = new NaviInterfaceImpl();

    public static NaviInterfaceImpl getInstance() {
        return naviInterface;
    }


    @Override
    public void openNaviApp() {
        LogUtils.i(TAG, "openNaviApp");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        map.put("protocol", 2);
        params.setActionType("optNavi");
        map.put("actionType", params.getActionType());
        map.put("optType", 0);
        params.setParams(GsonUtils.toJson(map));
        sendRequest(params);
    }

    @Override
    public void closeNaviApp() {
        LogUtils.i(TAG, "closeNaviApp11");
        boolean isSpilt = NaviInterfaceImpl.getInstance().getNaviScreen().isSpiltScreen();
        stopNavi();
        if (!isSpilt) {
            DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
        }
    }

    @Override
    public void stopNavi() {
        LogUtils.i(TAG, "stopNavi");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("stopNavi");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        sendRequest(params);
    }

    @Override
    public NaviResponse<List<String>> routePlan(Poi des, List<Poi> viaPoiList, int routePrefer) {
        LogUtils.i(TAG, "routePlan:" + GsonUtils.toJson(des) + ",routePrefer:" + routePrefer);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("planRoute");
        map.put("actionType", params.getActionType());
        if (routePrefer != -1) {
            map.put("routeOption", routePrefer);
        }
        if (des == null || (des.getId() != null && !des.getId().isEmpty())) {
            map.put("endPoi", des);
        } else {
            SimplePoi simplePoi = new SimplePoi();
            simplePoi.setName(des.getName());
            map.put("endPoi", simplePoi);
        }
        map.put("protocol", 2);
        if (viaPoiList != null && !viaPoiList.isEmpty()) {
            List<Object> list = new ArrayList<>();
            for (Poi poi : viaPoiList) {
                if (poi.getId() != null && !poi.getId().isEmpty()) {
                    list.add(poi);
                } else {
                    SimplePoi simplePoi = new SimplePoi();
                    simplePoi.setName(poi.getName());
                    list.add(simplePoi);
                }
            }
            map.put("viaPoiList", list);
        }
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<List<String>> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            JSONArray jsonArray = naviResponse.getJsonObject().optJSONArray("routePlans");
            List<String> pathList = new ArrayList<>();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    pathList.add(jsonArray.opt(i).toString());
                }
            }
            naviResponse.setData(pathList);
            naviResponse.setSuccess(!pathList.isEmpty());
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> refreshRoute() {
        LogUtils.i(TAG, "refreshRoute");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("updateRoute");
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> startNavi(int index) {
        LogUtils.i(TAG, "startNavi:" + index);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("startNavi");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        map.put("isSimulate", false);
        if (index != -1) {
            map.put("pathId", index);
        }
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String msg = naviResponse.getJsonObject().optString("resultMsg");
            naviResponse.setData(msg);
            naviResponse.setSuccess("OK".equals(msg));
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> startNavi(String keyWord) {
        LogUtils.i(TAG, "startNavi:" + keyWord);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("oneStepNav");
        map.put("protocol", 2);
        map.put("keyword", keyWord);
        map.put("actionType", params.getActionType());
        map.put("cityCode", NaviInterfaceImpl.getInstance().getNaviStatus().getCityCode());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String msg = naviResponse.getJsonObject().optString("resultMsg");
            naviResponse.setData(msg);
            naviResponse.setSuccess("OK".equals(msg));
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> startNavi(Poi poi) {
        LogUtils.i(TAG, "startNavi:" + GsonUtils.toJson(poi));
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("oneStepNav");
        map.put("protocol", 2);
        map.put("cityCode", NaviInterfaceImpl.getInstance().getNaviStatus().getCityCode());
        map.put("endPoi", poi);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String msg = naviResponse.getJsonObject().optString("resultMsg");
            naviResponse.setData(msg);
            naviResponse.setSuccess("OK".equals(msg));
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> continueNavi() {
        LogUtils.i(TAG, "continueNavi");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("continueNavi");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String msg = naviResponse.getJsonObject().optString("resultMsg");
            naviResponse.setData(msg);
            naviResponse.setSuccess("OK".equals(msg));
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> backToNaviHome() {
        LogUtils.i(TAG, "backToNaviHome");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("enterHomePage");
        map.put("protocol", 2);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.sendRequest(params, true, LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            String msg = naviResponse.getJsonObject().optString("resultMsg");
            naviResponse.setData(msg);
            naviResponse.setSuccess("OK".equals(msg));
        }
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

}
