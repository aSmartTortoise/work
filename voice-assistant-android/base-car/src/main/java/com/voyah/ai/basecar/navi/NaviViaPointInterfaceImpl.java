package com.voyah.ai.basecar.navi;

import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviViaPointInterface;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStatus;
import com.voice.sdk.device.navi.bean.Poi;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaviViaPointInterfaceImpl implements NaviViaPointInterface {
    private static final String TAG = "NaviViaPointInterfaceImpl";
    private final AbstractNaviInterfaceImpl abstractNaviInterface;

    public NaviViaPointInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }

    @Override
    public List<Poi> getViaPoints() {
        LogUtils.i(TAG, "getViaPoints");
        List<Poi> wayPoints = new ArrayList<>();
        NaviStatus naviStatus = NaviInterfaceImpl.getInstance().getNaviStatus().getNaviStatus();
        if (naviStatus != null && naviStatus.getWayPoi() != null) {
            wayPoints.addAll(naviStatus.getWayPoi());
        }
        return wayPoints;
    }

    @Override
    public boolean deleteAllViaPoints() {
        LogUtils.i(TAG, "deleteAllViaPoints");
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optWayPoint");
        map.put("actionType", params.getActionType());
        map.put("optType", 4);
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        return naviResponse.isSuccess();

    }

    @Override
    public boolean deleteViaPoint(int index) {
        LogUtils.i(TAG, "deleteViaPoint:" + index);
        List<Poi> viaPoints = getViaPoints();
        if (viaPoints == null || viaPoints.size() <= index) {
            return false;
        }
        return deleteViaPoint(viaPoints.get(index));
    }

    @Override
    public boolean deleteViaPoints(List<Poi> viaPoints) {
        LogUtils.i(TAG, "deleteViaPoints:" + viaPoints);
        if (viaPoints != null && !viaPoints.isEmpty()) {
            for (Poi p : viaPoints) {
                boolean result = deleteViaPoint(p);
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean deleteViaPoint(Poi poi) {
        LogUtils.i(TAG, "deleteViaPoint:" + GsonUtils.toJson(poi));
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optWayPoint");
        map.put("actionType", params.getActionType());
        map.put("optType", 1);
        map.put("protocol", 2);
        map.put("wayPoint", poi);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        return naviResponse.isSuccess();
    }

    @Override
    public NaviResponse<String> addViaPoint(Poi poi) {
        LogUtils.i(TAG, "addViaPoint:" + GsonUtils.toJson(poi));
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optWayPoint");
        map.put("actionType", params.getActionType());
        map.put("optType", 0);
        map.put("protocol", 2);
        map.put("wayPoint", poi);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> addViaPoint(int index) {
        LogUtils.i(TAG, "addViaPoint:" + index);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optSearchResult");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("action", 0);
        map.put("index", index);
        params.setParams(GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public boolean isViaPointsFull() {
        List<Poi> viaPoints = getViaPoints();
        boolean isFull = viaPoints != null && viaPoints.size() >= NaviConstants.MAX_VIA_POINTS_SIZE;
        LogUtils.i(TAG, "isViaPointsFull:" + isFull);
        return isFull;
    }

    @Override
    public boolean isContainPoi(Poi poi) {
        LogUtils.i(TAG, "isContainPoi:" + GsonUtils.toJson(poi));
        if (poi == null) {
            return false;
        }
        List<Poi> viaPoints = getViaPoints();
        Poi desPoi = NaviInterfaceImpl.getInstance().getNaviStatus().getDestPoi();
        if (desPoi != null) {
            viaPoints.add(desPoi);
        }
        if (viaPoints != null && !viaPoints.isEmpty()) {
            for (Poi p : viaPoints) {
                if (p.getId() != null && p.getId().equals(poi.getId())) {
                    return true;
                }
                if (p.getLat() == poi.getLat() && p.getLon() == poi.getLon() && p.getLat() != 0 && p.getLon() != 0) {
                    return true;
                }
            }
        }
        return false;
    }


}
