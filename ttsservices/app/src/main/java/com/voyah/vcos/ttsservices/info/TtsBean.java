package com.voyah.vcos.ttsservices.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.ai.sdk.VoiceTtsBean;

/**
 * The type Tts bean.
 */
public class TtsBean implements Parcelable {
    private String ttsId;

    private String originTtsId;


    private String tts;

    private String packageName;

    private TtsPriority ttsPriority;

    //tts发音人
    private String ttsVoice;
    //tts情感
    private String emotion;

    private String langType = "zh-CN";

    private int streamStatus = -1;

    private long startTime;

    private int soundLocation = 0;

    private ITtsCallback iTtsCallback;

    public TtsBean() {
    }

    public TtsBean(String ttsId, String tts, String packageName, TtsPriority ttsPriority) {
        this.ttsId = ttsId;
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = ttsPriority;
    }

    public TtsBean(String originTtsId, String ttsId, String tts, String packageName, TtsPriority ttsPriority) {
        this.ttsId = ttsId;
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = ttsPriority;
    }

    public TtsBean(String tts, String packageName, int streamStatus) {
        this.tts = tts;
        this.packageName = packageName;
        this.streamStatus = streamStatus;
    }

    public TtsBean(String tts, String packageName) {
        this.ttsId = "";
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = TtsPriority.P2;
    }

    public TtsBean(String tts, String packageName, String ttsVoice, String emotion) {
        this.ttsId = "";
        this.tts = tts;
        this.packageName = packageName;
        this.ttsPriority = TtsPriority.P2;
        this.ttsVoice = ttsVoice;
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
        dest.writeInt(this.ttsPriority == null ? -1 : this.ttsPriority.ordinal());
        dest.writeString(this.ttsVoice);
        dest.writeString(this.emotion);
        dest.writeString(this.langType);
        dest.writeInt(this.streamStatus);
        dest.writeInt(this.soundLocation);
        dest.writeLong(this.startTime);
    }

    protected TtsBean(Parcel in) {
        this.ttsId = in.readString();
        this.tts = in.readString();
        this.packageName = in.readString();
        int tmpTtsPriority = in.readInt();
        this.ttsPriority = tmpTtsPriority == -1 ? null : TtsPriority.values()[tmpTtsPriority];
        this.ttsVoice = in.readString();
        this.emotion = in.readString();
        this.langType = in.readString();
        this.streamStatus = in.readInt();
        this.soundLocation = in.readInt();
        this.startTime = in.readLong();
    }

    public void readFromParcel(Parcel in) {
        this.ttsId = in.readString();
        this.tts = in.readString();
        this.packageName = in.readString();
        int tmpTtsPriority = in.readInt();
        this.ttsPriority = tmpTtsPriority == -1 ? null : TtsPriority.values()[tmpTtsPriority];
        this.ttsVoice = in.readString();
        this.emotion = in.readString();
        this.langType = in.readString();
        this.streamStatus = in.readInt();
        this.soundLocation = in.readInt();
        this.startTime = in.readLong();
    }

    public static final Creator<TtsBean> CREATOR = new Creator<TtsBean>() {
        @Override
        public TtsBean createFromParcel(Parcel source) {
            return new TtsBean(source);
        }

        @Override
        public TtsBean[] newArray(int size) {
            return new TtsBean[size];
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
        if (TextUtils.isEmpty(originTtsId))
            this.originTtsId = ttsId;
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
    public TtsPriority getTtsPriority() {
        return ttsPriority;
    }

    public void setTtsPriority(TtsPriority ttsPriority) {
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

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getOriginTtsId() {
        return originTtsId;
    }

    public void setOriginTtsId(String originTtsId) {
        this.originTtsId = originTtsId;
    }

    public int getSoundLocation() {
        return soundLocation;
    }

    public void setSoundLocation(int soundLocation) {
        this.soundLocation = soundLocation;
    }

    public ITtsCallback getiTtsCallback() {
        return iTtsCallback;
    }

    public void setiTtsCallback(ITtsCallback iTtsCallback) {
        this.iTtsCallback = iTtsCallback;
    }

    public static TtsBean createBean(VoiceTtsBean bean) {
        TtsBean ttsBean = new TtsBean();
        ttsBean.setOriginTtsId(bean.getTtsId());
        ttsBean.setTts(bean.getTts());
        ttsBean.setEmotion(bean.getEmotion());
        ttsBean.setSoundLocation(bean.getSoundLocation());
        ttsBean.setPackageName(bean.getPackageName());
        if (TextUtils.equals("P0", bean.getTtsPriority()))
            ttsBean.setTtsPriority(TtsPriority.P0);
        else if (TextUtils.equals("P1", bean.getTtsPriority()))
            ttsBean.setTtsPriority(TtsPriority.P1);
        else if (TextUtils.equals("P2", bean.getTtsPriority()))
            ttsBean.setTtsPriority(TtsPriority.P2);
        else if (TextUtils.equals("P3", bean.getTtsPriority()))
            ttsBean.setTtsPriority(TtsPriority.P3);
        else if (TextUtils.isEmpty(bean.getTtsPriority()))
            ttsBean.setTtsPriority(TtsPriority.P2);
        if (!TextUtils.isEmpty(bean.getLangType()))
            ttsBean.setLangType(bean.getLangType());
        else
            ttsBean.setLangType("zh-CN");
        ttsBean.setStreamStatus(bean.getStreamStatus());

        return ttsBean;
    }

    @Override
    public String toString() {
        return "TtsBean{" +
                "ttsId='" + ttsId + '\'' +
                ", tts='" + tts + '\'' +
                ", packageName='" + packageName + '\'' +
                ", ttsPriority=" + ttsPriority.getValue() +
                ", ttsVoice='" + ttsVoice + '\'' +
                ", emotion='" + emotion + '\'' +
                ", langType='" + langType + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}