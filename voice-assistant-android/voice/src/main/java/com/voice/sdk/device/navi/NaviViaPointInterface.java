package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;

import java.util.List;

@SuppressWarnings("unused")
public interface NaviViaPointInterface {

     List<Poi> getViaPoints();

     boolean deleteAllViaPoints();

     boolean deleteViaPoint(int index);

     boolean deleteViaPoints(List<Poi> list);

     boolean deleteViaPoint(Poi poi);

     NaviResponse<String> addViaPoint(Poi poi);

     NaviResponse<String> addViaPoint(int index);

     boolean isViaPointsFull();

     boolean isContainPoi(Poi poi);

}
