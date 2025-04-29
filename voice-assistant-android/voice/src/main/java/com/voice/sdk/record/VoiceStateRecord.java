package com.voice.sdk.record;

/**
 * @author:lcy 语音状态及任务记录
 * @data:2024/7/25
 **/
public class VoiceStateRecord {

    private static final String TAG = VoiceStateRecord.class.getSimpleName();

    //------------------当前任务
    private String mCurrentTtsRequestId;

    //当前执行任务ID，用于两次信息类卡片任务展示时出现第二个误关闭
    private String mCurrentTaskRequestId;

    //当前任务类型 0-信息类带有卡片 1-信息类不带有卡片 2-非信息类带有卡片 3-非信息类不带有卡片
    private int mCurrentTaskType = -1;


//------------------上一轮任务

    private String mPrevTtsRequestId;
    private String mPrevTaskRequestId;
    private int mPrevTaskType = -1;


//    private static class InnerHolder {
//        private static final VoiceStateRecord instance = new VoiceStateRecord();
//    }
//
//    public static VoiceStateRecord getInstance() {
//        return InnerHolder.instance;
//    }


    public void setCurrentTtsRequestId(String currentTtsRequestId) {
        mCurrentTtsRequestId = currentTtsRequestId;
    }

    public String getCurrentTtsRequestId() {
        return mCurrentTtsRequestId;
    }

    public String getCurrentTaskRequestId() {
        return mCurrentTaskRequestId;
    }

    public void setCurrentTaskRequestId(String currentTaskRequestId) {
        this.mCurrentTaskRequestId = currentTaskRequestId;
    }

    public int getCurrentTaskType() {
        return mCurrentTaskType;
    }

    public void setCurrentTaskType(int currentTaskType) {
        this.mCurrentTaskType = currentTaskType;
    }


    public String getPrevTtsRequestId() {
        return mPrevTtsRequestId;
    }

    public void setPrevTtsRequestId(String mPrevTtsRequestId) {
        this.mPrevTtsRequestId = mPrevTtsRequestId;
    }

    public String getPrevTaskRequestId() {
        return mPrevTaskRequestId;
    }

    public void setPrevTaskRequestId(String mPrevTaskRequestId) {
        this.mPrevTaskRequestId = mPrevTaskRequestId;
    }

    public int getPrevTaskType() {
        return mPrevTaskType;
    }

    public void setPrevTaskType(int mPrevTaskType) {
        this.mPrevTaskType = mPrevTaskType;
    }


    public void updatePrevTaskMessage() {
        setPrevTaskRequestId(mCurrentTaskRequestId);
        setPrevTaskType(mCurrentTaskType);
        setPrevTtsRequestId(mCurrentTtsRequestId);
    }


    public String VoiceStateString() {
        return "VoiceStateRecord{" +
                "mPrevTtsRequestId:" + mPrevTtsRequestId + '\n' +
                "mPrevTaskRequestId:" + mPrevTaskRequestId + '\n' +
                "mPrevTaskType:" + mPrevTaskType + '\n' +
                "mCurrentTtsRequestId:" + mCurrentTtsRequestId + '\n' +
                "mCurrentTaskRequestId:" + mCurrentTaskRequestId + '\n' +
                "mCurrentTaskType:" + mCurrentTaskType + '\n' +
                "}";
    }
}
