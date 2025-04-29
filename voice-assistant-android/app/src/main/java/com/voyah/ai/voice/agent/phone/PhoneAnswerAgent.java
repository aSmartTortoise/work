package com.voyah.ai.voice.agent.phone;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 接听-可见可说结果中实现
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneAnswerAgent extends BaseAgentX {
    private static final String TAG = PhoneAnswerAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;

    @Override
    public String AgentName() {
        return "phone#answer";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "------PhoneAnswerAgent-------");
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        boolean isWakeUpTime = getBooleanFlowContextKey(Constant.FC_IS_NO_WAKEUP_TIME, flowContext);
        LogUtils.i(TAG, "isWakeUpTime:" + isWakeUpTime);
        String tts = "";
        if (phoneInterface.isIncoming())
            phoneInterface.answerCall();
        else if (!isWakeUpTime)
            tts = TTSAnsConstant.PHONE_NO_TASK;
        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, tts);
    }


}
