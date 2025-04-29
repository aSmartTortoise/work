package com.voyah.vcos.ttsservices.manager;

/**
 * @author:lcy
 * @data:2024/9/3
 **/
public class CopyMsgInfo {
    public String voiceName; //复刻音色名
    public String profileId; //复刻id
    public int voiceSex = -1; //性别

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


    public void reset() {
        this.voiceName = "";
        this.profileId = "";
        this.voiceSex = -1;
    }

    @Override
    public String toString() {
        return "CopyMsgInfo{" +
                "voiceName='" + voiceName + '\'' +
                ", profileId=" + profileId +
                ", voiceSex=" + voiceSex +
                '}';
    }
}
