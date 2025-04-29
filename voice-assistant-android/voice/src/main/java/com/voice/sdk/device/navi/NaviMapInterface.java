package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.HighWayPoi;
import com.voice.sdk.device.navi.bean.NaviResponse;

import java.util.List;

@SuppressWarnings("unused")
public interface NaviMapInterface {
    void setThemeStyle(int theme);

    int getThemeStyle();

    NaviResponse<String> zoomMap(boolean big);

    NaviResponse<String> maxMinMap(boolean max);

    NaviResponse<String> switchTraffic(boolean open);

    NaviResponse<String> adjustViewMode(int viewMode);

    NaviResponse<Integer> getViewMode();

    NaviResponse<String> changeRoad(int roadType);

    NaviResponse<String> switchAutoScale(boolean open);

    NaviResponse<String> openMapDownloadPage();

    NaviResponse<String> queryTrafficInfo();

    NaviResponse<List<HighWayPoi>> queryServiceAreaInfo();

    NaviResponse<List<HighWayPoi>> queryTollStationInfo();

    NaviResponse<String> prevPage();

    NaviResponse<String> nextPage();

    NaviResponse<String> openHighWayInfoView();
}
