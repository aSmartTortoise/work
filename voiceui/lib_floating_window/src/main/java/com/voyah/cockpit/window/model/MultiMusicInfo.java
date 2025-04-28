package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MultiMusicInfo  extends MultiItemEntity {
    private int position;
    private String name;
    private String artist;
    private String album;
    private String imgUrl;
    private int type;
    private int sourceType;
    private boolean isVip;

    @Override
    public int getItemType() {
        return ViewType.MUSIC_TYPE;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    @IntDef({SourceType.WY_MUSIC,
            SourceType.QQ_MUSIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceType {
        int WY_MUSIC = 0;
        int QQ_MUSIC = 1;
    }
}
