package com.voyah.cockpit.window.model;

import java.util.List;

/**
 * author : jie wang
 * date : 2024/3/25 16:54
 * description :
 */
public class CardInfo {

    /**
     *  卡片数据的业务
     */
    private String domainType;

    /**
     *  用户选择的item，索引
     */
    private int position;

    private BTPhoneInfo btPhoneInfo;

    private List<Weather> weathers;

    private List<ScheduleInfo> schedules;

    private List<MultimediaInfo> multimediaInfos;

    private int itemType;


    /**
     *  描述卡片关联的意图行为
     */
    private String action;

    public String getDomainType() {
        return domainType;
    }



    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BTPhoneInfo getBtPhoneInfo() {
        return btPhoneInfo;
    }

    public void setBtPhoneInfo(BTPhoneInfo btPhoneInfo) {
        this.btPhoneInfo = btPhoneInfo;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }

    public void setWeathers(List<Weather> weathers) {
        this.weathers = weathers;
    }

    public List<ScheduleInfo> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleInfo> schedules) {
        this.schedules = schedules;
    }

    public List<MultimediaInfo> getMultimediaInfos() {
        return multimediaInfos;
    }

    public void setMultimediaInfos(List<MultimediaInfo> multimediaInfos) {
        this.multimediaInfos = multimediaInfos;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
