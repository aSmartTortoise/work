package com.voice.sdk.device.navi.bean;

import java.io.Serializable;
@SuppressWarnings("unused")
public class HighWayPoi implements Serializable {
    private float lat;
    private float lon;
    private String name;
    private int remainDist;
    private int type;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemainDist() {
        return remainDist;
    }

    public void setRemainDist(int remainDist) {
        this.remainDist = remainDist;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
