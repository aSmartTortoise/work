package com.voyah.vcos.ttsservices.info;

import android.text.TextUtils;

import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.utils.LogUtils;

/**
 * @author:lcy
 * @data:2024/3/22
 **/
public class PlayTTSBean {
    //id  = 时间戳+随机数
    private String ttsId;

    private String originTtsId;

    public String getOriginTtsId() {
        return originTtsId;
    }

    public void setOriginTtsId(String originTtsId) {
        this.originTtsId = originTtsId;
    }

    private String tts;

    //tts情感
    private String emotion;

    private String langType = Constant.DEFAULT_LAN_TYPE;

    private int usage;

    private int position = -1;//指定播放位置

    private String packageName = "";

    private TtsPriority ttsPriority;

    private VoiceCopyBean voiceCopyBean;

    private int streamStatus = -1;

    private long startTime;

    private int soundLocation = 0;

    private boolean isNearPlay = false;

    private ITtsCallback iTtsCallback;


    public VoiceCopyBean getVoiceCopyBean() {
        return voiceCopyBean;
    }

    public void setVoiceCopyBean(VoiceCopyBean voiceCopyBean) {
        this.voiceCopyBean = voiceCopyBean;
    }

    public ITtsCallback getiTtsCallback() {
        return iTtsCallback;
    }

    public void setiTtsCallback(ITtsCallback iTtsCallback) {
        this.iTtsCallback = iTtsCallback;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTtsId() {
        return ttsId;
    }

    public void setTtsId(String ttsId) {
        this.ttsId = ttsId;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public TtsPriority getTtsPriority() {
        return ttsPriority;
    }

    public void setTtsPriority(TtsPriority ttsPriority) {
        this.ttsPriority = ttsPriority;
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

    public int getSoundLocation() {
        return soundLocation;
    }

    public void setSoundLocation(int soundLocation) {
        this.soundLocation = soundLocation;
    }

    public boolean isNearPlay() {
        return isNearPlay;
    }

    public void setNearPlay(boolean nearPlay) {
        isNearPlay = nearPlay;
    }

    @Override
    public String toString() {
        return "VoiceTTSBean{" +
                "ttsId='" + ttsId + '\'' +
                ", tts='" + tts + '\'' +
                ", emotion='" + emotion + '\'' +
                ", langType='" + langType + '\'' +
                ", usage=" + usage + '\'' +
                ", position=" + position + '\'' +
                ", ttsPriority=" + ttsPriority + '\'' +
                ", startTime=" + startTime + '\'' +
                ", voiceCopyBean=" + voiceCopyBean +
                '}';
    }

    public static class VoiceCopyBean {
        private String voiceName;
        private String profileId;
        private int voiceSex;

        public String getVoiceName() {
            return voiceName;
        }

        public void setVoiceName(String voiceName) {
            this.voiceName = voiceName;
        }

        public String getProfileId() {
            return profileId;
        }

        public void setProfileId(String profileId) {
            this.profileId = profileId;
        }

        public int getVoiceSex() {
            return voiceSex;
        }

        public void setVoiceSex(int voiceSex) {
            this.voiceSex = voiceSex;
        }

        @Override
        public String toString() {
            return "VoiceCopyBean{" +
                    "voiceName='" + voiceName + '\'' +
                    ", profileId=" + profileId +
                    ", voiceSex=" + voiceSex +
                    '}';
        }
    }

    public static PlayTTSBean createBean(TtsBean bean) {
        PlayTTSBean voiceTTSBean = new PlayTTSBean();
        voiceTTSBean.setTtsId(bean.getTtsId());
        voiceTTSBean.setOriginTtsId(bean.getOriginTtsId());
        voiceTTSBean.setTts(bean.getTts());
        voiceTTSBean.setEmotion(bean.getEmotion());
        voiceTTSBean.setPackageName(bean.getPackageName());
        voiceTTSBean.setTtsPriority(bean.getTtsPriority());
        voiceTTSBean.setSoundLocation(bean.getSoundLocation());
        if (!TextUtils.isEmpty(bean.getLangType())) {
            voiceTTSBean.setLangType(bean.getLangType());
        }
        if (null != bean.getiTtsCallback())
            voiceTTSBean.setiTtsCallback(bean.getiTtsCallback());
        else
            LogUtils.d("PlayTTSBean", "createBean callback is null");
        voiceTTSBean.setStreamStatus(bean.getStreamStatus());
        voiceTTSBean.setStartTime(bean.getStartTime());
//        VoiceCopyBean copyBean = new VoiceCopyBean();
//        voiceTTSBean.setVoiceCopyBean(copyBean);
        return voiceTTSBean;
    }


}
