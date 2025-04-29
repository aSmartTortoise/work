package com.voice.sdk.device.media.bean;

public class MusicPlayStatusInfo {
    private int playStatus;
    private String sourceType;
    public MusicPlayStatusInfo() {

    }
    public MusicPlayStatusInfo(int playStatus, String sourceType) {
        this.playStatus = playStatus;
        this.sourceType = sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public int getPlayStatus() {
        return this.playStatus;
    }

    public String toString() {
        return "PlayStatusInfo{playStatus=" + this.playStatus + ", sourceType='" + this.sourceType + '\'' + '}';
    }
}
