package com.voyah.cockpit.window.model;

/**
 * author : jie wang
 * date : 2024/3/25 15:28
 * description : 语音服务发送过来的语音相关实体类
 */
public class VoiceInfo {

    private int voiceMode = VoiceMode.VOICE_MODE_ONLINE;

    private String voiceState;

    private int wakeVoiceLocation = VoiceLocation.FRONT_LEFT;

    /**
     *  用户输入的语音指令是否valid
     */
    private boolean asrValid = true;

    public int getVoiceMode() {
        return voiceMode;
    }

    public void setVoiceMode(int voiceMode) {
        this.voiceMode = voiceMode;
    }

    public String getVoiceState() {
        return voiceState;
    }

    public void setVoiceState(String voiceState) {
        this.voiceState = voiceState;
    }

    public int getWakeVoiceLocation() {
        return wakeVoiceLocation;
    }

    public void setWakeVoiceLocation(int wakeVoiceLocation) {
        this.wakeVoiceLocation = wakeVoiceLocation;
    }

    public boolean isAsrValid() {
        return asrValid;
    }

    public void setAsrValid(boolean asrValid) {
        this.asrValid = asrValid;
    }
}
