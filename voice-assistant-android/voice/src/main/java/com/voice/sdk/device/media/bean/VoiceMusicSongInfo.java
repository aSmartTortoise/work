package com.voice.sdk.device.media.bean;

/**
 * @author jackl
 * @description 语音歌曲列表
 */
public class VoiceMusicSongInfo {
    private String mediaId;
    private String mediaName;
    private String artist;
    private String albumName;
    private String cover;
    private long duration;
    private boolean canPlay = true;
    private boolean isAudition = true;
    private String type;
    private int code = -1;


    private String mediaSource;

    public VoiceMusicSongInfo() {
    }

    public VoiceMusicSongInfo(String mediaId, String mediaName, String artist, String albumName, String cover, long duration, boolean canPlay, boolean isAudition, String mediaSource) {
        this.mediaId = mediaId;
        this.mediaName = mediaName;
        this.artist = artist;
        this.albumName = albumName;
        this.cover = cover;
        this.duration = duration;
        this.canPlay = canPlay;
        this.isAudition = isAudition;
        this.mediaSource = mediaSource;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaName() {
        return this.mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public boolean isCanPlay() {
        return this.canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public boolean isAudition() {
        return this.isAudition;
    }

    public void setAudition(boolean audition) {
        this.isAudition = audition;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMediaSource(String mediaSource) {
        this.mediaSource = mediaSource;
    }

    public String getMediaSource() {
        return mediaSource;
    }
    @Override
    public String toString() {
        return "VoiceMusicSongInfo{" +
                "mediaId='" + mediaId + '\'' +
                ", mediaName='" + mediaName + '\'' +
                ", artist='" + artist + '\'' +
                ", albumName='" + albumName + '\'' +
                ", cover='" + cover + '\'' +
                ", duration=" + duration +
                ", canPlay=" + canPlay +
                ", isAudition=" + isAudition +
                ", type='" + type + '\'' +
                ", code=" + code +
                ", mediaSource=" + mediaSource +
                '}';
    }
}
