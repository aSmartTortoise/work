package com.voyah.cockpit.window.model;

import androidx.annotation.NonNull;

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

    private List<MultiMusicInfo> multiMusicInfos;

    private List<StockInfo> stockInfos;

    private List<ChatMessage> chatMessages;

    private int itemType;


    /**
     *  描述卡片关联的意图行为
     */
    private String action;

    /**
     *  卡片会话id
     *  大模型卡片对应于VoiceService的requestId
     */
    private String sessionId;


    private String requestId;

    /**
     *  标识是否是腾讯大模型输出（响应）的
     */
    private boolean fromGPTFlag;

    /**
     *  屏幕的位置
     */
    private int screenType;

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

    public List<StockInfo> getStockInfos() {
        return stockInfos;
    }

    public void setStockInfos(List<StockInfo> stockInfos) {
        this.stockInfos = stockInfos;
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

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isFromGPTFlag() {
        return fromGPTFlag;
    }

    public void setFromGPTFlag(boolean fromGPTFlag) {
        this.fromGPTFlag = fromGPTFlag;
    }

    public @ScreenType int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public List<MultiMusicInfo> getMultiMusicInfos() {
        return multiMusicInfos;
    }

    public void setMultiMusicInfos(List<MultiMusicInfo> multiMusicInfos) {
        this.multiMusicInfos = multiMusicInfos;
    }

    @NonNull
    @Override
    public String toString() {
        return "CardInfo{" +
                "domainType='" + domainType + '\'' +
                ", position=" + position +
                ", btPhoneInfo=" + btPhoneInfo +
                ", weathers=" + weathers +
                ", schedules=" + schedules +
                ", multimediaInfos=" + multimediaInfos +
                ", multimediaInfos=" + multiMusicInfos +
                ", stockInfos=" + stockInfos +
                ", chatMessages=" + chatMessages +
                ", itemType=" + itemType +
                ", action='" + action + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", fromGPTFlag=" + fromGPTFlag +
                ", screenType=" + screenType +
                '}';
    }
}
