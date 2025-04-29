package com.voyah.ai.basecar.voicecopy;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * @Date 2024/8/28 17:02
 * @Author 8327821
 * @Email *
 * @Description 声音复刻实体类
 **/
public class VoiceReproductionBean {
    private String id; //ID
    private String vin; //车辆vin码
    private String oneid; //音色包创建用户id
    private int voiceSex; //声音性别，0-女，1-男
    private String voiceName; //声音名称
    private String voiceId; //声音id
    private String profileId; //微软声音tts合成profile id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getOneid() {
        return oneid;
    }

    public void setOneid(String oneid) {
        this.oneid = oneid;
    }

    public int getVoiceSex() {
        return voiceSex;
    }

    public void setVoiceSex(int voiceSex) {
        this.voiceSex = voiceSex;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @NonNull
    @Override
    public String toString() {
        return "VoiceReproductionBean{" +
                "id='" + id + '\'' +
                ", vin='" + vin + '\'' +
                ", oneid='" + oneid + '\'' +
                ", voiceSex=" + voiceSex +
                ", voiceName='" + voiceName + '\'' +
                ", voiceId='" + voiceId + '\'' +
                ", profileId='" + profileId + '\'' +
                '}';
    }

    //用于VoiceReproductionManager 缓存去重
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceReproductionBean bean = (VoiceReproductionBean) o;
        return Objects.equals(voiceId, bean.voiceId) && Objects.equals(profileId, bean.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voiceId, profileId);
    }
}
