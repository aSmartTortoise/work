package com.voyah.ai.voice.agent.phone;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 更新通讯录
 * @data:2024/3/4
 **/
@ClassAgent
public class PhoneSyncAgent extends BaseAgentX {
    private static final String TAG = PhoneSyncAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "phone#sync";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "----------PhoneSyncAgent----------");
        PhoneInterface phoneInterface = DeviceHolder.INS().getDevices().getPhone();

        TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
        //1.蓝牙是否连接
        if (!phoneInterface.isBtConnect()) {
            //todo:TTS播报 请先连接手机蓝牙
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        if (phoneInterface.isSyncContacting()) {
            //todo:TTS播报 正在同步通讯录,请稍等
            LogUtils.i(TAG, "TTS播报 正在同步通讯录,请稍等--同步中");
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        //todo:TTS播报 正在同步通讯录,请稍等
        phoneInterface.syncContact();
        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }


}
