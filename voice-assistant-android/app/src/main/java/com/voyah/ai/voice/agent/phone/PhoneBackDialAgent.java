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
 * @author:lcy 回拨
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneBackDialAgent extends BaseAgentX {
    private static final String TAG = PhoneBackDialAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;
    private String lastIncoming;

    @Override
    public String AgentName() {
        return "phone#backdial";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------PhoneBackDialAgent----------");
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        boolean isBtConnect = phoneInterface.isBtConnect();
        LogUtils.i(TAG, "isBtConnect is " + isBtConnect);
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        //1.蓝牙未连接
        if (!isBtConnect) {
            phoneInterface.openBluetoothSettings();
            //todo:TTS播报 请先连接手机蓝牙
            LogUtils.i(TAG, " bt not connect , open bt setting ");
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        boolean isSyncContacting = phoneInterface.isSyncContacting();
        LogUtils.i(TAG, "isSyncContacting is " + isSyncContacting);
        //2.1 通讯录同步中
        if (isSyncContacting) {
            phoneInterface.setBluetoothPhoneTab(1);
            //todo:TTS播报 正在同步通讯录
            LogUtils.i(TAG, "正在同步通讯录,请稍等");
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        //2.2 未同步通讯录
        boolean isSyncContacted = phoneInterface.isSyncContacted();
        LogUtils.i(TAG, "isSyncContacted is " + isSyncContacted);
        if (!isSyncContacted) {
            phoneInterface.setBluetoothPhoneTab(1);
            //todo:TTS播报 请先同步通讯录
            LogUtils.i(TAG, "请先同步通讯录");
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        //3.无法定位到最近一个来电
        lastIncoming = phoneInterface.getLastIncomingNumber();
        LogUtils.i(TAG, " lastIncoming is " + lastIncoming);
        if (StringUtils.isBlank(lastIncoming)) {
            //todo:TTS播报 没有找到符合条件的播报
            LogUtils.i(TAG, "没有找到符合条件的电话");
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[0], TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
        } else {
            LogUtils.i(TAG, "即将呼叫" + lastIncoming);
        }
        TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_REDIAL[0], TTSAnsConstant.PHONE_REDIAL[1]);
        PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{tel_name}", "@{tel_number}"}, new String[]{"", lastIncoming});
        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean, TTSAnsConstant.PHONE_REDIAL[0]);
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        if (StringUtils.equals(executeTag, TTSAnsConstant.PHONE_REDIAL[0]))
            phoneInterface.placeCall(lastIncoming);
    }


}
