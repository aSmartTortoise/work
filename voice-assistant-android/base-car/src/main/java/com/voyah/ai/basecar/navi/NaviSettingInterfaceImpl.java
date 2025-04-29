package com.voyah.ai.basecar.navi;


import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviSettingInterface;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.system.VolumeStream;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class NaviSettingInterfaceImpl implements NaviSettingInterface {
    private static final String TAG = "NaviSettingInterfaceImpl";
    private final AbstractNaviInterfaceImpl abstractNaviInterface;

    public NaviSettingInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }

    @Override
    public NaviResponse<Integer> setSpeakMode(int speakMode) {
        LogUtils.i(TAG, "setSpeakMode:" + speakMode);
        setSpeakMute(false);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("speakMode");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("speakModeValue", speakMode);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<Integer> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.getJsonObject() != null) {
            naviResponse.setData(naviResponse.getJsonObject().optInt("result"));
        }
        return naviResponse;
    }

    @Override
    public void setSpeakMute(boolean mute) {
        LogUtils.i(TAG, "setSpeakMute:" + mute);
        int volume = DeviceHolder.INS().getDevices().getSystem().getVolume().getVolume(VolumeStream.STREAM_NAVI);
        DeviceHolder.INS().getDevices().getSystem().getVolume().setMuted(VolumeStream.STREAM_NAVI, mute, volume == 0 ? 15 : volume);
    }

    @Override
    public boolean isSpeakMute() {
        boolean ans = DeviceHolder.INS().getDevices().getSystem().getVolume().isMuted(VolumeStream.STREAM_NAVI);
        int volume = DeviceHolder.INS().getDevices().getSystem().getVolume().getVolume(VolumeStream.STREAM_NAVI);
        LogUtils.i(TAG, "isSpeakMute:" + ans + ",volume:" + volume);
        return ans || volume == 0;
    }

    @Override
    public boolean isSupportNoa() {
        LogUtils.i(TAG, "isSupportNoa");
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if ("H56C".equals(carType)) {
            return false;
        }
        return true;
    }

    @Override
    public NaviResponse<String> setPreView(boolean preView, boolean temp) {
        LogUtils.i(TAG, "setPreView:" + preView + ",temp:" + temp);
        if (temp) {
            ActionParams params = new ActionParams();
            Map<String, Object> map = new HashMap<>();
            params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
            params.setActionType("preView");
            map.put("actionType", params.getActionType());
            map.put("optType", preView ? 0 : 1);
            map.put("protocol", 2);
            params.setParams(GsonUtils.toJson(map));
            ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
            return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        } else {
            ActionParams params = new ActionParams();
            Map<String, Object> map = new HashMap<>();
            params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
            params.setActionType("keepPreView");
            map.put("actionType", params.getActionType());
            map.put("optType", preView ? 0 : 1);
            map.put("protocol", 2);
            params.setParams(GsonUtils.toJson(map));
            ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
            return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        }
    }

    @Override
    public NaviResponse<String> switchNaviSettingPage(boolean open) {
        LogUtils.i(TAG, "switchNaviSettingPage:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optNaviSetting");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        map.put("protocol", 2);
        map.put("type", 1);
        map.put("pageType", 0);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchNaviCommutePage(boolean open) {
        LogUtils.i(TAG, "switchNaviCommutePage:" + open);
        if (DeviceHolder.INS().getDevices().getCarServiceProp().isH56D()) {
            ActionParams params = new ActionParams();
            Map<String, Object> map = new HashMap<>();
            params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
            params.setActionType("optNaviSetting");
            map.put("actionType", params.getActionType());
            map.put("optType", open ? 0 : 1);
            map.put("protocol", 2);
            map.put("type", 1);
            map.put("pageType", 1);
            params.setParams(GsonUtils.toJson(map));
            ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
            return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        } else {
            return switchNaviFavoritesPage(open);
        }
    }

    @Override
    public NaviResponse<String> switchNaviRestrictInfo(boolean open) {
        LogUtils.i(TAG, "switchNaviRestrictInfo:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("restrictInfo");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        map.put("protocol", 2);
        map.put("type", 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchHistoryPage(boolean open) {
        LogUtils.i(TAG, "switchHistoryPage:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("openHistoryPage");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        map.put("protocol", 2);
        map.put("type", 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchNaviLoginPage(boolean open) {
        LogUtils.i(TAG, "switchNaviLoginPage:" + open);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setActionType("loginPage");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("optType", open ? 0 : 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchNaviFavoritesPage(boolean open) {
        LogUtils.i(TAG, "switchNaviFaPage:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optFavorites");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("optType", open ? 0 : 1);
        params.setParams(GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchPoiDetailPage(int index) {
        LogUtils.i(TAG, "switchPoiDetailPage:" + index);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optSearchResult");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("action", 2);
        map.put("index", index);
        params.setParams(GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchLicensePlatePage(boolean open) {
        LogUtils.i(TAG, "switchNaviFaPage:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("licensePlate");
        map.put("protocol", 2);
        map.put("optType", open ? 0 : 1);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchRoutePreference(boolean open) {
        LogUtils.i(TAG, "switchRoutePreference:" + open);
        ActionParams params = new ActionParams();
        Map<String, Object> map = new HashMap<>();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optRememberRoutePrefer");
        map.put("protocol", 2);
        map.put("optType", open ? 0 : 1);
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchCruiseBroadcast(boolean open) {
        LogUtils.i(TAG, "switchCruiseBroadcast:" + open);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCruiseSpeak");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchRoutePlanInNavi(int plan) {
        LogUtils.i(TAG, "switchRoutePlanInNavi:" + plan);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("reRoute");
        map.put("actionType", params.getActionType());
        map.put("routeOption", plan);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> selectRoutePlanInNavi(int plan) {
        LogUtils.i(TAG, "selectRoutePlanInNavi:" + plan);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("selectRoute");
        map.put("actionType", params.getActionType());
        map.put("pathId", plan);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchRoutePlan(int plan) {
        LogUtils.i(TAG, "switchRoutePlanInNavi:" + plan);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("routePrefer");
        map.put("actionType", params.getActionType());
        map.put("routeOption", plan);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchQuickNavi(boolean open) {
        LogUtils.i(TAG, "switchQuickNavi:" + open);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optQuickDepart");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.DEFAULT_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> switchAvoidRestriction(boolean open) {
        LogUtils.i(TAG, "switchAvoidRestriction:" + open);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optAvoidRestriction");
        map.put("actionType", params.getActionType());
        map.put("optType", open ? 0 : 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true, AbstractNaviInterfaceImpl.LONG_WAIT_TIME);
        return new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
    }

    @Override
    public NaviResponse<String> setLaneLevelNavigation(boolean laneLevelNavigation) {
        LogUtils.i(TAG, "setLaneLevelNavigation:" + laneLevelNavigation);
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("guideStateMachineDest");
        map.put("actionType", params.getActionType());
        map.put("stateType", laneLevelNavigation ? 2 : 1);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> shareTrip() {
        LogUtils.i(TAG, "shareTrip");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optJourneyShare");
        map.put("actionType", params.getActionType());
        map.put("optType", 0);
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

    @Override
    public NaviResponse<String> clearHistoryInfo() {
        LogUtils.i(TAG, "clearAllHistoryInfo");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("clearAllHistoryInfo");
        map.put("actionType", params.getActionType());
        params.setParams(GsonUtils.toJson(map));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<String> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        return naviResponse;
    }

}
