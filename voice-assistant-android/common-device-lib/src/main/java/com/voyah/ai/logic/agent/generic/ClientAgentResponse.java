package com.voyah.ai.logic.agent.generic;


import com.voyah.ai.logic.agent.flowchart.ttsEnd.TTsEndCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/18
 **/
public class ClientAgentResponse {

    public int mRetCode;
    public Map<String, Object> mExtra = new HashMap<>();

    public String mExecuteTag; //带有该参数表示先播报再执行-参数与TTS文案设置相同即可
    public String mUiType; //卡片类型

    private String dsScenario; //场景字段

    private boolean isExit;

    private boolean isStream; //是否为流式播报
    private int streamStatus = -1; //流式状态 0:开始 1:中间态 2:结束

    private int nearTtsLocation = -2; //就近播报特殊function指定播报位置

    private Object mTtsObject;
    private TTsEndCallBack mTTsEndCallBack;

    public TTsEndCallBack gettTsEndCallBack() {
        return mTTsEndCallBack;
    }

    public void settTsEndCallBack(TTsEndCallBack tTsEndCallBack) {
        this.mTTsEndCallBack = tTsEndCallBack;
    }

    public Object getmTtsObject() {
        return mTtsObject;
    }

    public void setmTtsObject(Object mTtsObject) {
        this.mTtsObject = mTtsObject;
    }

    public int getNearTtsLocation() {
        return nearTtsLocation;
    }

    public void setNearTtsLocation(int nearTtsLocation) {
        this.nearTtsLocation = nearTtsLocation;
    }

    public int getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(int streamStatus) {
        this.streamStatus = streamStatus;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    private boolean isAsrRec; //是否为asr识别

    private String asrText; //识别文本

    public String getAsrText() {
        return asrText;
    }

    public void setAsrText(String asrText) {
        this.asrText = asrText;
    }

    private boolean isWakeUp; //是否为唤醒

    private boolean isInformationCard = false;  //天气、股票、日程、大模型信息类卡片展示时需要设置为true

    private boolean isInValid = false; //拒识、兜底需要500ms切换vap状态
    private boolean IgQueryEmpty = false;

    private boolean isCrossDomain = false; //是否为跨域agent

    public boolean isClearInput() {
        return isClearInput;
    }

    public void setClearInput(boolean clearInput) {
        isClearInput = clearInput;
    }

    private boolean isClearInput = false; //是否需要清空打字机

    public boolean isCrossDomain() {
        return isCrossDomain;
    }

    public void setCrossDomain(boolean crossDomain) {
        isCrossDomain = crossDomain;
    }

    public boolean isIgQueryEmpty() {
        return IgQueryEmpty;
    }

    public void setIgQueryEmpty(boolean igQueryEmpty) {
        IgQueryEmpty = igQueryEmpty;
    }

    public String getDsScenario() {
        return dsScenario;
    }

    public void setDsScenario(String dsScenario) {
        this.dsScenario = dsScenario;
    }

    public boolean isWakeUp() {
        return isWakeUp;
    }

    public void setWakeUp(boolean wakeUp) {
        isWakeUp = wakeUp;
    }

    public ClientAgentResponse(int retCode) {
        this.mRetCode = retCode;
    }

    public ClientAgentResponse(int retCode, Map<String, Object> extra) {
        this.mRetCode = retCode;
//        this.mExtra = extra;
    }


    public ClientAgentResponse(int retCode, Map<String, Object> extra, Object ttsBean, String executeTag) {
        this.mRetCode = retCode;
//        this.mExtra = extra;
        this.mTtsObject = ttsBean;
        this.mExecuteTag = executeTag;
    }

    public ClientAgentResponse(int retCode, Map<String, Object> extra, Object ttsBean, String executeTag, String uiType) {
        this.mRetCode = retCode;
//        this.mExtra = extra;
        this.mTtsObject = ttsBean;
        this.mExecuteTag = executeTag;
        this.mUiType = uiType;
    }

    public ClientAgentResponse(int retCode, Map<String, Object> extra, Object ttsBean) {
        this.mRetCode = retCode;
//        this.mExtra = extra;
        this.mTtsObject = ttsBean;
    }


    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }

    /**
     * 天气、股票、日程、大模型信息类卡片展示时需要设置为true
     *
     * @return
     */
    public boolean isInformationCard() {
        return isInformationCard;
    }

    public void setInformationCard(boolean informationCard) {
        isInformationCard = informationCard;
    }

    public boolean isInValid() {
        return isInValid;
    }

    public void setInValid(boolean inValid) {
        isInValid = inValid;
    }

    public boolean isAsrRec() {
        return isAsrRec;
    }

    public void setAsrRec(boolean asrRec) {
        isAsrRec = asrRec;
    }

    public String getUiType() {
        return mUiType;
    }

    public void setUiType(String uiType) {
        this.mUiType = uiType;
    }

    @Override
    public String toString() {
        return "ClientAgentResponse{" +
                "mRetCode=" + mRetCode +
                ", mExtra=" + mExtra +
                ", mTts='" + mTtsObject.toString() + '\'' +
                ", mExecuteTag='" + mExecuteTag + '\'' +
                ", mUiType='" + mUiType + '\'' +
                ", dsScenario='" + dsScenario + '\'' +
                ", isExit=" + isExit +
                ", isAsrRec=" + isAsrRec +
                ", isWakeUp=" + isWakeUp +
                ", isInformationCard=" + isInformationCard +
                ", isInValid=" + isInValid +
                '}';
    }
}
