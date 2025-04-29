package com.voice.sdk.device.navi.bean;

import java.io.Serializable;

public class UploadPoi implements Serializable {
    private String name;
    private double lat;
    private double lon;

    public UploadPoi(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
