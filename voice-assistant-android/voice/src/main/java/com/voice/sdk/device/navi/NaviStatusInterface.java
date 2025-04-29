package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.NaviInfo;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStatus;
import com.voice.sdk.device.navi.bean.Poi;

import java.util.List;

@SuppressWarnings("unused")
public interface NaviStatusInterface {

    void parseNaviStatusReport(String naviStatus);

    boolean isInFront();

    boolean isFullScreen();

    boolean isLogin();

    boolean isHideInformation();

    boolean isQuickNavi();

    boolean isInNavigation();

    boolean isPlanRoute();

    boolean isDirectNavi();

    boolean isInPoiSearchResult();

    boolean isInRoutePlan();

    boolean isAgreeDisclaimer();

    boolean isActivation();

    int getLeftDistance();

    int getLeftTime();

    Poi getDestPoi();

    Poi getLastDestPoi();

    int getCurrentPage();

    List<Poi> getCurrentPoiList();

    int getCurrentPoiListSize();

    int getRoutePaths();

    int getSearchType();

    int getRoutePathSelected();

    void setSearchPoiResult(List<Poi> poiList);

    NaviResponse<List<Poi>> getFavoritesPoiList(int type, boolean checkPermission);

    NaviResponse<String> setFavoritesPoi(int type, Poi poi);

    NaviResponse<NaviInfo> getNaviInfo();

    int getCityCode();

    NaviStatus getNaviStatus();

    NaviResponse<List<Poi>> getHistoryPoiList(int type);
}
