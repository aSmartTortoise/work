package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStashInterface;
import com.voice.sdk.device.navi.bean.Poi;

import java.util.List;

@SuppressWarnings("unused")
public interface NaviInterface {

    NaviStashInterface getNaviStash();

    NaviStatusInterface getNaviStatus();

    NaviMapInterface getNaviMap();

    NaviTeamInterface getNaviTeam();

    NaviSettingInterface getNaviSetting();

    NaviPoiInterface getNaviPoi();

    NaviViaPointInterface getNaviViaPoint();

    NaviScreenInterface getNaviScreen();

    NaviResponse<List<String>> routePlan(Poi des, List<Poi> viaList, int routePrefer);

    NaviResponse<String> refreshRoute();

    NaviResponse<String> startNavi(int index);

    NaviResponse<String> startNavi(String keyWord);

    NaviResponse<String> startNavi(Poi poi);

    NaviResponse<String> continueNavi();

    NaviResponse<String> backToNaviHome();

    void init();

    boolean isServiceReady();

    void openNaviApp();

    void closeNaviApp();

    void stopNavi();
}
