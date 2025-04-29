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

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 拨打/查询电话 模糊意图
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneOpAgent extends BaseAgentX {
    private static final String TAG = PhoneOpAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;

    @Override
    public String AgentName() {
        return "phone#operation";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        String operationType = getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
        boolean isBtConnect = phoneInterface.isBtConnect();
        LogUtils.i(TAG, "---------PhoneOperationAgent-----------isBtConnect is " + isBtConnect + " ,operationType is " + operationType);
        TTSBean ttsBean;
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        //蓝牙是否连接
        if (!isBtConnect) {
            //todo:TTS播报 请先连接手机蓝牙
            LogUtils.i(TAG, "TTS播报 请先连请先连接手机蓝牙接手机蓝牙");
            phoneInterface.openBluetoothSettings();
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        //拨打电话
        if (StringUtils.equals(operationType, "dial")) {
            //todo:TTS询问你要打给谁
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_ASK_DIAL[0], TTSAnsConstant.PHONE_ASK_DIAL[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        } else if (StringUtils.equals(operationType, "search")) {
            //查询电话
            //1.正在同步通讯录
            if (phoneInterface.isSyncContacting()) {
                //todo: TTS播报 正在同步通讯录,请稍等
                LogUtils.i(TAG, "TTS播报 正在同步通讯录,请稍等");
                phoneInterface.setBluetoothPhoneTab(1);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
            }
            //2.通讯录是否同步
            if (!phoneInterface.isSyncContacted()) {
                //todo:TTS 播报 请先同步通讯录
                LogUtils.i(TAG, "TTS播报 请先同步通讯录");
                phoneInterface.setBluetoothPhoneTab(1);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
            }
            //3.已同步，询问
            //todo:TTS询问你要查谁
            LogUtils.i(TAG, "你要查谁的号码");
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_ASK_SEARCH[0], TTSAnsConstant.PHONE_ASK_SEARCH[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext);
    }


}
