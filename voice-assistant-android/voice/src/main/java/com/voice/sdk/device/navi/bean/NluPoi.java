package com.voice.sdk.device.navi.bean;


import android.text.TextUtils;

import com.voice.sdk.device.navi.NaviConstants;

import java.io.Serializable;

@SuppressWarnings("unused")
public class NluPoi implements Serializable {
    //POI的类型 分为tag和poi
    //tag代表酒店 餐馆 停车场 等分类
    //poi代表具体名称的 如人民广场 天安门
    private String type;
    //关键字 需要对某些地点做归一化
    private String keyword;
    //为空代表不是收藏的地址或者常用的地址
    //favorite代表用户收藏的地址 如我想去收藏的野生动物园
    //frequently代表常用的地址 如我想回家 我想去公司 我想去常去的篮球场
    private String favoriteType;
    //范围 距离描述 待定
    private int maxDistance;
    //中心poi 某些说法需要做归一化
    private String centerPoi;
    //搜索类型
    //around 周边搜
    //way 沿途搜
    //为空就是普通搜索
    private String searchType;

    private String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(String favoriteType) {
        this.favoriteType = favoriteType;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public String getCenterPoi() {
        return centerPoi;
    }

    public void setCenterPoi(String centerPoi) {
        this.centerPoi = centerPoi;
    }

    public boolean isTag() {
        return "tag".equals(type);
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }


    public boolean isFavoritePoi() {
        return (favoriteType != null && !favoriteType.isEmpty()) ||
                NaviConstants.HOME.equals(keyword) || NaviConstants.COMPANY.equals(keyword) || NaviConstants.HOME.equals(centerPoi) || NaviConstants.COMPANY.equals(centerPoi);
    }


    public boolean isAroundSearch() {
        return NaviConstants.SEARCH_TYPE_AROUND.equals(searchType);
    }

    public boolean isWaySearch() {
        return NaviConstants.SEARCH_TYPE_WAY.equals(searchType);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isDestination() {
        return NaviConstants.DESTINATION.equals(keyword) || NaviConstants.DESTINATION.equals(desc);
    }

    public boolean isHome() {
        return NaviConstants.HOME.equals(keyword) || NaviConstants.HOME.equals(desc);
    }

    public boolean isCompany() {
        return NaviConstants.COMPANY.equals(keyword) || NaviConstants.COMPANY.equals(desc);
    }

    public boolean isWayPoint() {
        return NaviConstants.WAY_POINT.equals(keyword) || NaviConstants.WAY_POINT.equals(desc);
    }

    public boolean isCurrentLocation() {
        return NaviConstants.CURRENT_LOCATION.equals(keyword) || NaviConstants.CURRENT_LOCATION.equals(desc);
    }

    public boolean isCommonPoi() {
        if (isHome() || isCompany() || isDestination() || isWayPoint() || isCurrentLocation()) {
            return false;
        }
        return true;
    }

    public boolean isFitDestPoi(Poi poi) {
        if (poi == null) {
            return false;
        }
        if (isDestination()) {
            if (TextUtils.isEmpty(desc)) {
                return true;
            }
            return poi.containKeyWord(desc);

        } else {
            return poi.containKeyWord(keyword);
        }
    }

    public boolean isFitWayPoi(Poi poi) {
        if (poi == null) {
            return false;
        }
        if (isWayPoint()) {
            if (TextUtils.isEmpty(desc)) {
                return true;
            }
            return poi.containKeyWord(desc);
        } else {
            return poi.containKeyWord(keyword);
        }
    }
}
