package com.voice.drawing.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
@TypeConverters(ListTypeConverter.class)
public class DrawingInfo implements Parcelable ,Cloneable{

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "drawingState")
    private int drawingState = DrawingState.START;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    private List<String> urlList;

    @ColumnInfo(name = "prompt")
    private String prompt;

    @ColumnInfo(name = "keyWords")
    // 绘图prompt中的关键词
    private String keyWords;

    @Ignore
    private int code;

    @Ignore
    private int picSize = 4;

    @Ignore
    private String ttsText;

    @Ignore
    private String requestId;

    @Ignore
    private int streamMode = -1;

    @Ignore
    private int completeCount;

    @Ignore
    private boolean loadFailFlag;

    @Ignore
    private boolean playTTSFlag = true;

    public DrawingInfo()
    {

    }

    protected DrawingInfo(Parcel in) {
        id = in.readInt();
        time = in.readLong();
        drawingState = in.readInt();
        urlList = in.createStringArrayList();
        prompt = in.readString();
        keyWords = in.readString();
        ttsText = in.readString();
        requestId = in.readString();
        code = in.readInt();
        picSize = in.readInt();
        streamMode = in.readInt();
        completeCount = in.readInt();
        loadFailFlag = in.readBoolean();
        playTTSFlag = in.readBoolean();
    }

    public static final Creator<DrawingInfo> CREATOR = new Creator<DrawingInfo>() {
        @Override
        public DrawingInfo createFromParcel(Parcel in) {
            return new DrawingInfo(in);
        }

        @Override
        public DrawingInfo[] newArray(int size) {
            return new DrawingInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrawingState() {
        return drawingState;
    }

    public void setDrawingState(int drawingState) {
        this.drawingState = drawingState;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPicSize() {
        return picSize;
    }

    public void setPicSize(int picSize) {
        this.picSize = picSize;
    }

    public String getTtsText() {
        return ttsText;
    }

    public void setTtsText(String ttsText) {
        this.ttsText = ttsText;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getStreamMode() {
        return streamMode;
    }

    public void setStreamMode(int streamMode) {
        this.streamMode = streamMode;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public boolean isLoadFailFlag() {
        return loadFailFlag;
    }

    public void setLoadFailFlag(boolean loadFailFlag) {
        this.loadFailFlag = loadFailFlag;
    }

    public boolean isPlayTTSFlag() {
        return playTTSFlag;
    }

    public void setPlayTTSFlag(boolean playTTSFlag) {
        this.playTTSFlag = playTTSFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time);
        dest.writeInt(drawingState);
        dest.writeStringList(urlList);
        dest.writeString(prompt);
        dest.writeString(keyWords);
        dest.writeString(ttsText);
        dest.writeString(requestId);
        dest.writeInt(code);
        dest.writeInt(picSize);
        dest.writeInt(streamMode);
        dest.writeInt(completeCount);
        dest.writeBoolean(loadFailFlag);
        dest.writeBoolean(playTTSFlag);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "DrawingInfo{" +
                "id=" + id +
                ", time=" + time +
                ", drawingState=" + drawingState +
                ", urlList=" + urlList +
                ", prompt='" + prompt + '\'' +
                ", keyWords='" + keyWords + '\'' +
                ", code=" + code +
                ", picSize=" + picSize +
                ", ttsText='" + ttsText + '\'' +
                ", requestId='" + requestId + '\'' +
                ", streamMode=" + streamMode +
                ", completeCount=" + completeCount +
                ", loadFailFlag=" + loadFailFlag +
                ", playTTSFlag=" + playTTSFlag +
                '}';
    }
}
