package com.voyah.ai.device.voyah.h37.dc.bean;

public class RearviewMirrorBean {
    private int position;
    private String appid;
    private long timestamp;

    public RearviewMirrorBean(String appid) {
        this.appid = appid;
        this.timestamp = System.currentTimeMillis();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
