package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/6/21 10:09
 * description :
 */
public class MultimediaInfo extends MultiItemEntity {
    private int position;

    private String name;

    /**
     *  媒体图片
     */
    private String imgUrl;

    /**
     *  媒体类型，枚举值 MediaType.TV_DRAMA, MediaType.MOVIE
     */
    private int type;


    /**
     *  媒体源，枚举值 SourceType.TENCENT, SourceType.IQIYI
     */
    private int sourceType;

    /**
     *  标签类型，枚举值 TagType.VIP, TagType.PAYMENT, TagType.SOLE_BROADCAST
     */
    private int tagType;

    /**
     *  电视剧集数
     */
    private int episodes;

    @Override
    public int getItemType() {
        return ViewType.MEDIA_TYPE;
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

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    @IntDef({MediaType.TV_DRAMA,
            MediaType.MOVIE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaType {

        int TV_DRAMA = 0;
        int MOVIE = 1;

    }

    @IntDef({SourceType.TENCENT,
            SourceType.IQIYI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceType {
        int TENCENT = 0;
        int IQIYI = 1;
    }


    @IntDef({TagType.VIP,
            TagType.PAYMENT,
            TagType.SOLE_BROADCAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TagType {

        int VIP = 0;
        int PAYMENT = 1;
        int SOLE_BROADCAST = 2;

    }

}
