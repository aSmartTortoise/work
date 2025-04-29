package com.voice.sdk.device.navi.bean;

import android.text.TextUtils;

import com.voice.sdk.device.navi.NaviConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Poi implements Serializable {
    private double lat;
    private double lon;
    private int coordinateType;
    private double altitude;
    private String id;

    private String itemId;
    private String typeCode;
    private String address;
    private String province;
    private String district;
    private String cityName;
    private int cityCode;
    private double distance;
    private ArrayList<String> tels;
    private String brandDesc;
    private String floorNo;
    private boolean isSourceSug;
    private boolean isSourcePark;
    private String name;
    private String customName;
    private String tradeTag;
    private String stdTag;
    private String poiTag;
    private String price;
    private String rating;
    private String shopHours;
    private String areaName;
    private String aoi;
    private String industry;
    private String floors;
    private int index;
    private double distanceFromCenter;
    private List<Poi> child;

    public String getId() {
        return (id != null && id.length() > 0) ? id : itemId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStdTag() {
        return stdTag;
    }

    public void setStdTag(String stdTag) {
        this.stdTag = stdTag;
    }

    public String getPoiTag() {
        return poiTag;
    }

    public void setPoiTag(String poiTag) {
        this.poiTag = poiTag;
    }

    public int getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(int coordinateType) {
        this.coordinateType = coordinateType;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public ArrayList<String> getTels() {
        return tels;
    }

    public void setTels(ArrayList<String> tels) {
        this.tels = tels;
    }

    public String getBrandDesc() {
        return brandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public boolean isSourceSug() {
        return isSourceSug;
    }

    public void setSourceSug(boolean sourceSug) {
        isSourceSug = sourceSug;
    }

    public boolean isSourcePark() {
        return isSourcePark;
    }

    public void setSourcePark(boolean sourcePark) {
        isSourcePark = sourcePark;
    }

    public String getTradeTag() {
        return tradeTag;
    }

    public void setTradeTag(String tradeTag) {
        this.tradeTag = tradeTag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getShopHours() {
        return shopHours;
    }

    public void setShopHours(String shopHours) {
        this.shopHours = shopHours;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAoi() {
        return aoi;
    }

    public void setAoi(String aoi) {
        this.aoi = aoi;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }

    public void setDistanceFromCenter(double distanceFromCenter) {
        this.distanceFromCenter = distanceFromCenter;
    }

    public List<Poi> getChild() {
        return child;
    }

    public void setChild(List<Poi> child) {
        this.child = child;
    }

    public boolean containKeyWord(String keyWord) {
        if (keyWord == null || keyWord.isEmpty()) {
            return false;
        }
        if (customName != null && customName.contains(keyWord)) {
            return true;
        }
        if (name != null && name.contains(keyWord)) {
            return true;
        }
        return address != null && address.contains(keyWord);
    }

    public boolean isUnNaviPoi() {
        return cityCode == 0 ||
                NaviConstants.POI_TAG_PROVINCE.equals(poiTag) ||
                NaviConstants.POI_TAG_CITY.equals(poiTag) ||
                NaviConstants.POI_TAG_AREA.equals(poiTag);
    }


    public String getShowName() {
        if (!TextUtils.isEmpty(customName)) {
            return customName;
        }
        return name;
    }
}
