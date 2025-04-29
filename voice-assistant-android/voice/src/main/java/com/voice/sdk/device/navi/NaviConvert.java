package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.Poi;

public class NaviConvert {
    private NaviConvert() {

    }

    public static com.voyah.ds.common.entity.domains.navi.Poi convertToDsPoi(Poi poi) {
        if (poi != null) {
            com.voyah.ds.common.entity.domains.navi.Poi poiDs = new com.voyah.ds.common.entity.domains.navi.Poi();
            poiDs.setId(poi.getId());
            poiDs.setAddress(poi.getAddress());
            poiDs.setCityCode(poi.getCityCode());
            poiDs.setDistance(poi.getDistance());
            poiDs.setLat(poi.getLat());
            poiDs.setLon(poi.getLon());
            poiDs.setCityName(poi.getCityName());
            poiDs.setName(poi.getName());
            poiDs.setDistrict(poi.getDistrict());
            return poiDs;
        }
        return null;
    }
}
