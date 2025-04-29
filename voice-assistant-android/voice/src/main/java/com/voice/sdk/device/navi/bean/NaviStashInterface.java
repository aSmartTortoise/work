package com.voice.sdk.device.navi.bean;

import java.util.List;

public interface NaviStashInterface {
    String getFavoriteType();

    void setFavoriteType(String favoriteType);

    List<Poi> getViaPoiList();

    void setViaPoiList(List<Poi> viaPoiList);

    String getRouteType();

    void setRouteType(String routeType);

    void clearStash();

    String getSessionId();

    void setSessionId(String sessionId);

    void setLastPoi(Poi poi);

    Poi getLastPoi();
}
