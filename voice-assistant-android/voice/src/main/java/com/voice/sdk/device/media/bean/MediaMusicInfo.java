package com.voice.sdk.device.media.bean;

public class MediaMusicInfo {
    private String mediaId = "";
    private String sourceType = "";
    private String mediaName = "";
    private String url = null;
    private String alumName = "";
    private String cover = "";
    private long duration = 0L;
    private String singerName = "";
    private int quality = 0;
    private int playType = 0;
    private boolean isPlaying = false;
    private boolean isPlay = false;
    private boolean isVip = false;
    private boolean isLike = false;
    private int category;
    private String audioId = null;
    private boolean isAudition = true;
    private boolean isPay = false;
    private long startAudition = 0L;
    private long endAudition = 30000L;
    private boolean isBuyStatus = false;
    private boolean isLiving = true;
    private boolean visible = true;
    private boolean isAscOrDesc = true;
    private int orderNumber;

    public MediaMusicInfo() {
    }

    public int getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isAscOrDesc() {
        return this.isAscOrDesc;
    }

    public void setAscOrDesc(boolean ascOrDesc) {
        this.isAscOrDesc = ascOrDesc;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isBuyStatus() {
        return this.isBuyStatus;
    }

    public void setBuyStatus(boolean buyStatus) {
        this.isBuyStatus = buyStatus;
    }

    public boolean isAudition() {
        return this.isAudition;
    }

    public void setAudition(boolean audition) {
        this.isAudition = audition;
    }

    public long getStartAudition() {
        return this.startAudition;
    }

    public void setStartAudition(long startAudition) {
        this.startAudition = startAudition;
    }

    public long getEndAudition() {
        return this.endAudition;
    }

    public void setEndAudition(long endAudition) {
        this.endAudition = endAudition;
    }

    public void setLike(boolean like) {
        this.isLike = like;
    }

    public boolean isLike() {
        return this.isLike;
    }

    public boolean isVip() {
        return this.isVip;
    }

    public void setVip(boolean vip) {
        this.isVip = vip;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public boolean isPlay() {
        return this.isPlay;
    }

    public void setPlay(boolean play) {
        this.isPlay = play;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getMediaName() {
        return this.mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlumName() {
        return this.alumName;
    }

    public void setAlumName(String alumName) {
        this.alumName = alumName;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSingerName() {
        return this.singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getQuality() {
        return this.quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getPlayType() {
        return this.playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public int getCategory() {
        return this.category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getAudioId() {
        return this.audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public void setPay(boolean pay) {
        this.isPay = pay;
    }

    public boolean isPay() {
        return this.isPay;
    }

    public boolean isLiving() {
        return this.isLiving;
    }

    public void setLiving(boolean living) {
        this.isLiving = living;
    }

    public String toString() {
        return "MediaInfo{mediaId='" + this.mediaId + '\'' + ", sourceType='" + this.sourceType + '\'' + ", mediaName='" + this.mediaName + '\'' + ", url='" + this.url + '\'' + ", alumName='" + this.alumName + '\'' + ", cover='" + this.cover + '\'' + ", duration=" + this.duration + ", singerName='" + this.singerName + '\'' + ", quality=" + this.quality + ",isPlaying = " + this.isPlaying + ", playType=" + this.playType + ",isVip=" + this.isVip + ",category=" + this.category + ",audioId=" + this.audioId + ",isLike=" + this.isLike + '}';
    }
}
