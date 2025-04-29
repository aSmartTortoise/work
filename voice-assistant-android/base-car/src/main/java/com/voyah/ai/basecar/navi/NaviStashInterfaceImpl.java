package com.voyah.ai.basecar.navi;


import com.voice.sdk.device.navi.bean.NaviStashInterface;
import com.voice.sdk.device.navi.bean.Poi;

import java.util.List;

public class NaviStashInterfaceImpl implements NaviStashInterface {


    public NaviStashInterfaceImpl() {

    }


    private List<Poi> viaPoiList;

    private String favoriteType;

    private String routeType;

    private String sessionId;

    private Poi lastPoi;

    @Override
    public String getFavoriteType() {
        return favoriteType;
    }

    @Override
    public void setFavoriteType(String favoriteType) {
        this.favoriteType = favoriteType;
    }

    @Override
    public List<Poi> getViaPoiList() {
        return viaPoiList;
    }

    @Override
    public void setViaPoiList(List<Poi> viaPoiList) {
        this.viaPoiList = viaPoiList;
    }

    @Override
    public String getRouteType() {
        return routeType;
    }

    @Override
    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    @Override
    public void clearStash() {
        viaPoiList = null;
        favoriteType = null;
        routeType = null;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void setLastPoi(Poi lastPoi) {
        this.lastPoi = lastPoi;
    }

    @Override
    public Poi getLastPoi() {
        return lastPoi;
    }


}
