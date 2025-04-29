package com.voyah.ai.logic.agent.generic;

import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UIState;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.remote.RemoteAsrListenerManager;
import com.voyah.ai.sdk.bean.VoiceAsrStatus;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.protocol.entity.AsrRecognizeResult;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy asr上屏
 * @data:2024/4/1
 **/
public class AsrTextRecStreamAgent extends BaseAgentX {
    private static final String TAG = AsrTextRecStreamAgent.class.getSimpleName();

    private boolean isAsrStart = true; //asr开始识别

    @Override
    public String AgentName() {
        return "asrTextRecStream";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
//        LogUtils.i(TAG, "-----AsrTextRecStreamAgent-----");
        extractId(flowContext);
        boolean isOnline = flowContext.containsKey(FlowContextKey.FC_IS_ASR_REC_ONLINE) ? (boolean) flowContext.get(FlowContextKey.FC_IS_ASR_REC_ONLINE) : true;
        AsrRecognizeResult asrRecognizeResult = flowContext.containsKey(FlowContextKey.FC_ASR_REC_RESULT) ? ((AsrRecognizeResult) flowContext.get(FlowContextKey.FC_ASR_REC_RESULT)) : null;

        //埋点requestId的获取
        if (asrRecognizeResult != null) {
            DeviceHolder.INS().getDevices().getBuriedPointManager().saveVadTimeToRequestId((String) flowContext.get(FlowContextKey.FC_REQ_ID), asrRecognizeResult.soundLocation);
        } else {
            LogUtils.d(TAG, "埋点，进入asrAgent，但是没有生源位置信息");
        }


        //false 时，识别结束。
        boolean isAsrRecognizing = flowContext.containsKey(FlowContextKey.FC_IS_ASR_RECOGNIZING) ? ((boolean) flowContext.get(FlowContextKey.FC_IS_ASR_RECOGNIZING)) : false;
        //识别出错
        boolean isRecognizeError = flowContext.containsKey(FlowContextKey.FC_IS_ASR_RECOGNIZE_ERROR) ? ((boolean) flowContext.get(FlowContextKey.FC_IS_ASR_RECOGNIZE_ERROR)) : false;
        String asrText = null != asrRecognizeResult ? asrRecognizeResult.text : "";
        if (!isAsrRecognizing && !StringUtils.isBlank(asrText))
            LogUtils.i(TAG, "onVoAIMessage -- ASR : " + asrText + (isOnline ? " isOnline" : " isOffline"));

        //测试阶段暂时放开打印限制
//        LogUtils.i(TAG, "onVoAIMessage -- ASR : " + asrText + (isOnline ? " isOnline" : " isOffline"));
//        LogUtils.i(TAG, " isAsrRecognizing is " + isAsrRecognizing + " ,isAsrStart is " + isAsrStart + " ,isRecognizeError is " + isRecognizeError + " ,asrText is " + asrText + ", isOnline:" + isOnline);
//        if (flowContext.containsKey(FlowContextKey.FC_ASR_REC_ONLINE))
//            isOnline = (boolean) flowContext.get(FlowContextKey.FC_ASR_REC_ONLINE);
//        LogUtils.i(TAG, "isOnline is " + isOnline);

        //兜底sar有识别内容单据识却是无效据识场景及识别过程中异常
        if (!StringUtils.isBlank(asrText)) {
            UIMgr.INSTANCE.enterState(UIState.STATE_ASR, asrText, "", mAgentIdentifier, getAsrSoundLocation(flowContext));
        }

        if (!StringUtils.isBlank(asrText)) {
            if (VoiceImpl.getInstance().getModelDebug()) {
                if (isAsrStart) {
                    isAsrStart = false;
                    RemoteAsrListenerManager.getInstance().callbackAsrStatus(VoiceAsrStatus.startRecognition);
                    RemoteAsrListenerManager.getInstance().callbackAsrText(asrText);
                } else if (!isAsrRecognizing || isRecognizeError) {
                    //首字唤醒时间。
                    isAsrStart = true;
                    RemoteAsrListenerManager.getInstance().callbackAsrText(asrText);
                    int asrStatus = -1;
                    if (!isAsrRecognizing)
                        asrStatus = VoiceAsrStatus.endRecognition;
                    if (isRecognizeError) {
                        asrStatus = VoiceAsrStatus.failRecognition;
                        LogUtils.d(TAG, "asr识别结束埋点，当前是全部的nlu结果：" + asrText);
                    }
                    RemoteAsrListenerManager.getInstance().callbackAsrStatus(asrStatus);
                }
            }
            DeviceHolder.INS().getDevices().getDialogue().onAsrResultCallback(isOnline, asrText, !isAsrRecognizing);
        }
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext);
        clientAgentResponse.setAsrRec(true);
        clientAgentResponse.setAsrText(asrText);
        return clientAgentResponse;
    }

    @Override
    public int getPriority() {
        return INVALID_PRIORITY;
    }

    private int getAsrSoundLocation(Map<String, Object> flowContext) {
        if (flowContext.containsKey(FlowContextKey.FC_ASR_REC_RESULT)) {
            AsrRecognizeResult asrRecognizeResult = (AsrRecognizeResult) flowContext.get(FlowContextKey.FC_ASR_REC_RESULT);
            if (asrRecognizeResult!= null) {
                LogUtils.d(TAG, "asrRecognizeResult.soundLocation:" + asrRecognizeResult.soundLocation);
                return BaseAgentX.translateLocation(asrRecognizeResult.soundLocation);
            }
        } else {
            LogUtils.d(TAG, "not containsKey asrRecResult");
        }
        return 0;
    }


}
