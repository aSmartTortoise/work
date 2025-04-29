package com.voice.sdk.device.navi.bean;


import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class NaviStatus implements Serializable {
    private int naviStatus;
    private boolean isLogin;
    private int leftDistance;
    private int leftTime;
    private boolean isResume;
    private boolean fullScreen;
    private int pageType;
    private int routePaths;
    private int routePathSelected;
    private Poi destPoi;
    private Poi currentPoi;

    private List<Poi> wayPoi;

    private List<Poi> searchPoiList;

    private int searchType;

    public List<Poi> getSearchPoiList() {
        return searchPoiList;
    }

    public void setSearchPoiList(List<Poi> searchPoiList) {
        this.searchPoiList = searchPoiList;
    }

    public int getNaviStatus() {
        return naviStatus;
    }

    public void setNaviStatus(int naviStatus) {
        this.naviStatus = naviStatus;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public int getLeftDistance() {
        return leftDistance;
    }

    public void setLeftDistance(int leftDistance) {
        this.leftDistance = leftDistance;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }

    public boolean isResume() {
        return isResume;
    }

    public void setResume(boolean resume) {
        isResume = resume;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public int getRoutePaths() {
        return routePaths;
    }

    public void setRoutePaths(int routePaths) {
        this.routePaths = routePaths;
    }

    public int getRoutePathSelected() {
        return routePathSelected;
    }

    public void setRoutePathSelected(int routePathSelected) {
        this.routePathSelected = routePathSelected;
    }

    public Poi getDestPoi() {
        return destPoi;
    }

    public void setDestPoi(Poi destPoi) {
        this.destPoi = destPoi;
    }

    public Poi getCurrentPoi() {
        return currentPoi;
    }

    public void setCurrentPoi(Poi currentPoi) {
        this.currentPoi = currentPoi;
    }

    public List<Poi> getWayPoi() {
        return wayPoi;
    }

    public void setWayPoi(List<Poi> wayPoi) {
        this.wayPoi = wayPoi;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
}
