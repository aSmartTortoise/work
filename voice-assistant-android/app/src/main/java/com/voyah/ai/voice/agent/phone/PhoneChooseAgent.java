package com.voyah.ai.voice.agent.phone;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.ui.UIMgr;
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
 * @author:lcy 确定、取消
 * @data:2024/3/4
 **/
@ClassAgent
public class PhoneChooseAgent extends BaseAgentX {
    private static final String TAG = PhoneChooseAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;
    private String preCallNumber;

    @Override
    public String AgentName() {
        return "phone#choose";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        //拿到号码 人名  operation_type
        preCallNumber = getFlowContextKey(Constant.PARAMS_NUMBER, flowContext);
        String operation_type = getFlowContextKey(Constant.PARAMS_OPERATION_TYPE, flowContext);
        String name = getParamKey(paramsMap, Constant.PARAMS_NAME, 0);
        String choose_type = getParamKey(paramsMap, Constant.CHOOSE_TYPE, 0);
        String sessionId = getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
//        String deviceScenarioState = getParamKey(paramsMap, Constant.PARAMS_DEVICE_SCENARIO_STATE, 0);
        boolean isConfirm = StringUtils.equals(choose_type, "confirm");

//        LogUtils.i(TAG, "preCallNumber is " + preCallNumber + " ,name is " + name + " ,operation_type is " + operation_type + " ,choose_type is " + choose_type + " ,deviceScenarioState is " + deviceScenarioState);
        LogUtils.i(TAG, "preCallNumber:" + preCallNumber + " ,name:" + name + " ,operation_type:" + operation_type + " ,choose_type:" + choose_type + " ,sessionId:" + sessionId);
        if (isConfirm) {
            if (!phoneInterface.isBtConnect()) {
                phoneInterface.openBluetoothSettings();
                //todo:TTS播报 请先连接手机蓝牙
                LogUtils.i(TAG, " bt not connect , open bt setting ");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
            } else {
                LogUtils.i(TAG, "即将呼叫" + (!StringUtils.isBlank(name) ? name : preCallNumber));
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_REDIAL[0], TTSAnsConstant.PHONE_REDIAL[1]);
                PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{tel_name}", "@{tel_number}"}, new String[]{name, preCallNumber});
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean, TTSAnsConstant.PHONE_REDIAL[0]);
            }
        } else {
            //非询问是否要拨打出去场景下 取消为退出电话场景
//            if (!StringUtils.equals(deviceScenarioState, "State.CALL_OUT_CONFIRM"))
//            VoiceImpl.getInstance().exitSessionDialog(sessionId);
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_CONFIRM[0], TTSAnsConstant.PHONE_OPEN_CONFIRM[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        if (StringUtils.equals(TTSAnsConstant.PHONE_REDIAL[0], executeTag))
            phoneInterface.placeCall(preCallNumber);
    }

    @Override
    public void showUi(String uiType, int location) {
//        VoAIVoiceController.getInstance().exDialog();
        LogUtils.d(TAG, "showUI uiType:" + uiType);
        UIMgr.INSTANCE.dismissCardOnScreen(location);
    }
}
