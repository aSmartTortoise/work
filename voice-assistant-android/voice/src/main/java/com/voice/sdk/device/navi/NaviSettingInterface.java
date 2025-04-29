package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.NaviResponse;

@SuppressWarnings("unused")
public interface NaviSettingInterface {
    NaviResponse<Integer> setSpeakMode(int speakMode);

    void setSpeakMute(boolean mute);

    boolean isSpeakMute();

    boolean isSupportNoa();

    NaviResponse<String> setPreView(boolean preView, boolean temp);

    NaviResponse<String> switchNaviSettingPage(boolean open);

    NaviResponse<String> switchNaviCommutePage(boolean open);

    NaviResponse<String> switchNaviRestrictInfo(boolean open);

    NaviResponse<String> switchHistoryPage(boolean open);

    NaviResponse<String> switchNaviLoginPage(boolean open);

    NaviResponse<String> switchNaviFavoritesPage(boolean open);

    NaviResponse<String> switchPoiDetailPage(int index);

    NaviResponse<String> switchLicensePlatePage(boolean open);

    NaviResponse<String> switchRoutePreference(boolean open);

    NaviResponse<String> switchCruiseBroadcast(boolean open);

    NaviResponse<String> switchRoutePlanInNavi(int plan);

    NaviResponse<String> selectRoutePlanInNavi(int plan);

    NaviResponse<String> switchRoutePlan(int plan);

    NaviResponse<String> switchQuickNavi(boolean open);

    NaviResponse<String> switchAvoidRestriction(boolean open);

    NaviResponse<String> setLaneLevelNavigation(boolean laneLevelNavigation);

    NaviResponse<String> shareTrip();

    NaviResponse<String> clearHistoryInfo();
}
