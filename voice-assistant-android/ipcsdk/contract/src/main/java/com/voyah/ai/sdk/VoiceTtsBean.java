package com.voyah.ai.sdk;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author:lcy
 * @data:2024/8/28
 **/
public class VoiceTtsBean implements Parcelable {
    private String ttsId;

    private String tts;

    private String packageName;

    private String ttsPriority;

    //tts发音人
    private String ttsVoice;
    //tts情感
    private String emotion;

    private String langType = "zh-CN";

    private int streamStatus = -1;

    private int soundLocation = 0;


    public VoiceTtsBean() {
    }

    public VoiceTtsBean(String ttsId, String tts, String packageName, String ttsPriority) {
        this.ttsId = ttsId;
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = ttsPriority;
    }

    public VoiceTtsBean(String tts, String packageName, int streamStatus) {
        this.tts = tts;
        this.packageName = packageName;
        this.streamStatus = streamStatus;
    }

    public VoiceTtsBean(String tts, String packageName) {
        this.ttsId = "";
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = "P2";
    }

    public VoiceTtsBean(String tts, String packageName, String ttsVoice, String ttsPriority, String emotion) {
        this.ttsId = "";
        this.tts = tts;
        this.packageName = packageName;
        this.ttsVoice = ttsVoice;
        this.ttsPriority = ttsPriority;
        this.emotion = emotion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ttsId);
        dest.writeString(this.tts);
        dest.writeString(this.packageName);
        dest.writeString(this.ttsPriority);
        dest.writeString(this.ttsVoice);
        dest.writeString(this.emotion);
        dest.writeString(this.langType);
        dest.writeInt(this.streamStatus);
        dest.writeInt(this.soundLocation);
    }

    protected VoiceTtsBean(Parcel in) {
        this.ttsId = in.readString();
        this.tts = in.readString();
        this.packageName = in.readString();
        this.ttsPriority = in.readString();
        this.ttsVoice = in.readString();
        this.emotion = in.readString();
        this.langType = in.readString();
        this.streamStatus = in.readInt();
        this.soundLocation = in.readInt();
    }

    public void readFromParcel(Parcel in) {
        this.ttsId = in.readString();
        this.tts = in.readString();
        this.packageName = in.readString();
        this.ttsPriority = in.readString();
        this.ttsVoice = in.readString();
        this.emotion = in.readString();
        this.langType = in.readString();
        this.streamStatus = in.readInt();
        this.soundLocation = in.readInt();
    }

    public static final Creator<VoiceTtsBean> CREATOR = new Creator<VoiceTtsBean>() {
        @Override
        public VoiceTtsBean createFromParcel(Parcel source) {
            return new VoiceTtsBean(source);
        }

        @Override
        public VoiceTtsBean[] newArray(int size) {
            return new VoiceTtsBean[size];
        }
    };

    /**
     * ttsId ttsBean的身份 用于tts回调使用
     *
     * @return the tts id
     */
    public String getTtsId() {
        return ttsId;
    }

    /**
     * ttsId ttsBean的身份 用于tts回调使用
     *
     * @param ttsId the tts id
     */
    public void setTtsId(String ttsId) {
        this.ttsId = ttsId;
    }

    /**
     * tts播放的内容.
     *
     * @return the tts
     */
    public String getTts() {
        return tts;
    }

    /**
     * tts播放的内容.
     *
     * @param tts the tts
     */
    public void setTts(String tts) {
        this.tts = tts;
    }

    /**
     * 应用包名.
     *
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 应用包名.
     *
     * @param packageName the package name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * tts优先级
     *
     * @return the tts priority
     */
    public String getTtsPriority() {
        return ttsPriority;
    }

    public void setTtsPriority(String ttsPriority) {
        this.ttsPriority = ttsPriority;
    }

    public String getTtsVoice() {
        return ttsVoice;
    }

    public void setTtsVoice(String ttsVoice) {
        this.ttsVoice = ttsVoice;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getLangType() {
        return langType;
    }

    public void setLangType(String langType) {
        this.langType = langType;
    }

    public int getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(int streamStatus) {
        this.streamStatus = streamStatus;
    }

    public int getSoundLocation() {
        return soundLocation;
    }

    public void setSoundLocation(int soundLocation) {
        this.soundLocation = soundLocation;
    }

    @Override
    public String toString() {
        return "TtsBean{" +
                "ttsId='" + ttsId + '\'' +
                ", tts='" + tts + '\'' +
                ", packageName='" + packageName + '\'' +
                ", ttsPriority=" + ttsPriority +
                ", ttsVoice='" + ttsVoice + '\'' +
                ", emotion='" + emotion + '\'' +
                ", langType='" + langType + '\'' +
                ", soundLocation='" + soundLocation + '\'' +
                '}';
    }
}
