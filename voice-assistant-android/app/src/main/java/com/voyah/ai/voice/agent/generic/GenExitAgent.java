package com.voyah.ai.voice.agent.generic;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.record.VoiceStatus;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.voice.agent.phone.PhoneUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/8
 **/
@ClassAgent
public class GenExitAgent extends BaseAgentX {
    private static final String TAG = GenExitAgent.class.getSimpleName();

    private boolean isIgnoreCountExceedLimitInDelayListening;
    private boolean isContinueListenTimeout = false;
    private boolean isMultiInteractionTimeout = false;
    private boolean isNotUnderstandExceedLimit = false;
    private boolean isIgnoreCountExceedLimit = false;

    @Override
    public String AgentName() {
        return "exitDialog";
    }


    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "onVoAIMessage -- EXIT");
        ParamsGather.isExit = true;
        //连续对话超时静默退出
        isContinueListenTimeout = flowContext.containsKey(FlowContextKey.FC_CONTINUE_LISTEN_TIMEOUT)
                ? (boolean) flowContext.get(FlowContextKey.FC_CONTINUE_LISTEN_TIMEOUT) : false;
        //二次交互超时退出
        isMultiInteractionTimeout = flowContext.containsKey(FlowContextKey.FC_MULTI_INTERACTION_TIMEOUT)
                ? (boolean) flowContext.get(FlowContextKey.FC_MULTI_INTERACTION_TIMEOUT) : false;
        //连续三次据识静默退出(包含唤醒后)
        isIgnoreCountExceedLimit = flowContext.containsKey(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT)
                ? (boolean) flowContext.get(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT) : false;
        //二次交互连续三次不理解静默退出
        isNotUnderstandExceedLimit = flowContext.containsKey(FlowContextKey.FC_NOT_UNDERSTAND_EXCEED_LIMIT)
                ? (boolean) flowContext.get(FlowContextKey.FC_NOT_UNDERSTAND_EXCEED_LIMIT) : false;

        //延时轮三次据识
        isIgnoreCountExceedLimitInDelayListening = flowContext.containsKey(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT_IN_DELAY_LISTENING)
                ? (boolean) flowContext.get(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT_IN_DELAY_LISTENING) : false;
        //延时轮超时退出不需要播报
        LogUtils.i(TAG, "isContinueListenTimeout:" + isContinueListenTimeout + " ,isMultiInteractionTimeout:" + isMultiInteractionTimeout
                + " ,isIgnoreCountExceedLimit:" + isIgnoreCountExceedLimit + " ,isNotUnderstandExceedLimit:" + isNotUnderstandExceedLimit + " ,isIgnoreCountExceedLimitInDelayListening:" + isIgnoreCountExceedLimitInDelayListening);

        if (!isPassiveExit())
            DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//        VoAIVoiceController.getInstance().exDialog();
        DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.ASLEEP);
        //延时轮退出无 tts
        TTSBean ttsBean = new TTSBean();
        if (!isContinueListenTimeout && !isMultiInteractionTimeout && !isIgnoreCountExceedLimit && !isNotUnderstandExceedLimit) {
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.EXIT[0], TTSAnsConstant.EXIT[1]);
        }
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean, TTSAnsConstant.EXIT[0]);
        clientAgentResponse.setExit(true);
        VoiceStateRecordManager.getInstance().updateWakeStatus(VoiceStatus.status.VOICE_STATE_EXIT, "");
        return clientAgentResponse;
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        LogUtils.d(TAG, "current status is exit:" + ParamsGather.isExit);
        if (StringUtils.equals(executeTag, TTSAnsConstant.EXIT[0]) && ParamsGather.isExit) {
//            VoiceImpl.getInstance().exDialog();
            if (isPassiveExit())
                UIMgr.INSTANCE.exitWeakBusiness();
            else
                UIMgr.INSTANCE.forceExitAll("GenExitAgent");
        }
    }

    private boolean isPassiveExit() {
        return isContinueListenTimeout || isIgnoreCountExceedLimitInDelayListening || isMultiInteractionTimeout;
    }

}
