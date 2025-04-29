package com.voice.sdk.device.appstore;

import androidx.annotation.NonNull;


public enum AppStorePage {
    PageNone(-1, "岚图商城", ""),
    PageRecommend(1, "商城推荐页面", "recommend"),
    PageAppStore(21, "商城应用页面", "app"),
    PageService(3, "商城服务页面", "service"),
    PageMine(41, "商城我的页面", "mine"),
    PageWallpaper(5, "商城壁纸页面", "wallpaper");

    private final String chName;

    private final String name;
    private final int value;

    AppStorePage(int value, String chName, String name) {
        this.value = value;
        this.chName = chName;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getChName() {
        return chName;
    }

    public static AppStorePage fromName(String uiName) {
        for (AppStorePage e : AppStorePage.values()) {
            if (e.name.equals(uiName)) {
                return e;
            }
        }
        return PageNone;
    }

    @NonNull
    @Override
    public String toString() {
        return this.value + "_" + this.chName;
    }

}
