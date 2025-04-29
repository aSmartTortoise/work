package com.voice.sdk.device.navi.bean;

import androidx.annotation.NonNull;

public enum NaviPage {
    PAGE_HOME("PAGE_MAP_HOME", 0, "地图首页"),
    PAGE_POI_DETAIL("PAGE_POI_DETAIL", 1, "Poi详情页"),
    PAGE_ROUTE("PAGE_ROUTE", 2, "算路页"),
    PAGE_NAV("PAGE_NAV", 3, "导航中"),
    PAGE_SEARCH_KEYWORD("PAGE_SEARCH_KEYWORD", 4, "检索页面"),
    PAGE_SEARCH_RESULT("PAGE_SEARCH_RESULT", 5, "检索结果页面"),
    PAGE_NAVI_SEARCH_RESULT("PAGE_NAVI_SEARCH_RESULT", 46, "导航中检索结果页面"),
    PAGE_PERSONAL_SETTING("PAGE_PERSONAL_SETTING", 6, "个人中心"),
    PAGE_FAV("PAGE_FAV", 8, "收藏夹"),
    PAGE_NAV_END("PAGE_NAV_END", 10, "行程报告"),
    PAGE_SEARCH_AROUND("PAGE_SEARCH_AROUND", 11, "周边搜"),
    PAGE_SELECT_POI("PAGE_SELECT_POI", 12, "地图选点"),
    PAGE_SEARCH_ALONG_WAY("PAGE_SEARCH_ALONG_WAY", 13, "沿途搜"),
    PAGE_ROUTE_SETTING("PAGE_ROUTE_SETTING", 14, "路线偏好设置"),
    PAGE_RESTRICTION("PAGE_RESTRICTION", 15, "限行政策"),
    PAGE_TRAFFIC("PAGE_TRAFFIC", 17, "道路故障详情"),
    PAGE_OFFLINE_MAP("PAGE_OFFLINE_MAP", 18, "离线地图"),
    PAGE_OFFLINE_DOWNLOAD("PAGE_OFFLINE_DOWNLOAD", 19, "离线地图下载管理"),
    PAGE_NAV_SETTING("PAGE_NAV_SETTING", 35, "导航设置"),
    PAGE_CRUISE("PAGE_CRUISE", 21, "首页巡航"),
    PAGE_PROVINCE_KEYBOARD("PAGE_PROVINCE_KEYBOARD", 22, "车牌设置"),
    PAGE_ABOUT("PAGE_ABOUT", 23, "服务条款、隐私政策"),
    PAGE_OTHER("OTHER", -1, "其他页面");

    private final String name;
    private final String chName;
    private final int value;

    NaviPage(String name, int value, String chName) {
        this.name = name;
        this.value = value;
        this.chName = chName;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return this.value + "_" + this.name + "_" + this.chName;
    }

    public static NaviPage fromValue(int value) {
        for (NaviPage e : NaviPage.values()) {
            if (value == e.value) {
                return e;
            }
        }
        return PAGE_OTHER;
    }
}
