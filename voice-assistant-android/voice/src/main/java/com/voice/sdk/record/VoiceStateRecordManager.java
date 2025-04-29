package com.voice.sdk.record;

import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2024/12/9
 **/
public class VoiceStateRecordManager {
    private static final String TAG = VoiceStateRecordManager.class.getSimpleName();

    private HashMap<String, VoiceStateRecord> voiceStateRecordHashMap = new HashMap<>();

//    private final Object lock = new Object();

    private int voiceState = -1; //语音状态  0:唤醒态 1:聆听态 2:播报态 3:休眠
    private String voiceLocation; //语音唤醒声源位置
    private int voiceMode = -1; //语音当前模式 在线 离线
    private boolean isAsr;
    private boolean mIsPlayTts;

    private static class InnerHolder {
        private static final VoiceStateRecordManager instance = new VoiceStateRecordManager();
    }

    public static VoiceStateRecordManager getInstance() {
        return InnerHolder.instance;
    }

    public VoiceStateRecord getVoiceStateRecord(String soundLocation) {
        if (!voiceStateRecordHashMap.containsKey(soundLocation))
            voiceStateRecordHashMap.put(soundLocation, new VoiceStateRecord());
        return voiceStateRecordHashMap.get(soundLocation);
    }

    public void setIsAsr(boolean status) {
        isAsr = status;
    }

    public boolean isAsr() {
        return isAsr;
    }

    public int getVoiceState() {
        return voiceState;
    }

    public void setVoiceLocation(String wakeUpLocation) {
        voiceLocation = wakeUpLocation;
    }

    public  String getVoiceLocation() {
        return voiceLocation;
    }

    public void setIsPlayTts(boolean isPlayTts) {
        mIsPlayTts = isPlayTts;
    }

    public boolean isTtsPlay() {
        return mIsPlayTts;
    }

    public void updateWakeStatus(int status, String wakeUpLocation) {
        LogUtils.d(TAG, "updateWakeStatus status:" + status + " ,wakeUpLocation:" + wakeUpLocation);
        voiceState = status;
        voiceLocation = wakeUpLocation;
    }


}
