package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.LocationResult;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;

import java.util.List;

@SuppressWarnings("unused")
public interface NaviPoiInterface {
    NaviResponse<List<Poi>> searchPoi(String keyWord, int searchType, int radius);

    NaviResponse<List<Poi>> searchAroundPoi(String keyWord, Poi centorPoi, int radius);

    NaviResponse<List<Poi>> searchOnWay(String keyWord, boolean addViaPoint);

    NaviResponse<Poi> locateSelf();
}
