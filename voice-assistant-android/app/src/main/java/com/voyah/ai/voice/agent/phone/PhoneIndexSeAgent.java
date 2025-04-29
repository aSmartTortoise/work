package com.voyah.ai.voice.agent.phone;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 列表-索引选择
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneIndexSeAgent extends BaseAgentX {
    private static final String TAG = PhoneIndexSeAgent.class.getSimpleName();
    private List<ContactInfo> contactsInfoList = new ArrayList<>();
    private List<ContactNumberInfo> contactNumberInfoList = new ArrayList<>();
    private String preCallNumber;
    private PhoneInterface phoneInterface;
    private UiInterface uiInterface;

    @Override
    public String AgentName() {
        return "phone_index#select";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "------PhoneIndexSeAgent------");
        contactsInfoList.clear();
        contactNumberInfoList.clear();
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        uiInterface = DeviceHolder.INS().getDevices().getUiCardInterface();
        String operation_type = (String) flowContext.get(Constant.PARAMS_OPERATION_TYPE);
        //电话列表选择没有上一个、下一个，无需判断index_type
        String index_type = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        int select_index = getIntegerParams(getParamKey(paramsMap, Constant.SELECT_INDEX, 0));
        String deviceScenarioState = (String) flowContext.get(Constant.PARAMS_DEVICE_SCENARIO_STATE);
        String search_Type = getFlowContextKey(Constant.PARAMS_SEARCH_TYPE, flowContext);
        boolean isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
        int maxItems = uiInterface.getMaxItemCount(PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
//        String tts = "超出有效数字范围了";
        TTSBean ttsBean;
        //选中号码在全列表中索引位置
        int index;
        int totalPages = 0;
        int lasePageItems = 0;
        int currentPages = uiInterface.getCurrentPage(PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
        contactNumberInfoList.addAll((List<ContactNumberInfo>) flowContext.get(Constant.PARAMS_NUMBER_LIST));
        LogUtils.i(TAG, "contactNumberInfoList.size is " + contactNumberInfoList.size());
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        if (!phoneInterface.isBtConnect()) {
            String prevRequestId = PhoneUtils.getShowPhoneCardRequestId();
            LogUtils.d(TAG, "prevRequestId:" + prevRequestId);
            UIMgr.INSTANCE.dismissCard(prevRequestId);
            phoneInterface.openBluetoothSettings();
            //todo:TTS播报 请先连接手机蓝牙
            LogUtils.i(TAG, " bt not connect , open bt setting ");
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        if (contactNumberInfoList.size() > 0) {
            totalPages = PhoneUtils.getTotalPages(contactNumberInfoList.size(), maxItems);
            lasePageItems = PhoneUtils.getLasePageItems(contactNumberInfoList.size(), maxItems);
        }
        LogUtils.i(TAG, "operation_type is " + operation_type + "index_type is " + index_type + " ,select_index is" + select_index + ", deviceScenarioState is" + deviceScenarioState
                + " ,deviceScenarioState is " + deviceScenarioState + " ,isDial is " + isDial + " ,search_Type is " + search_Type + " ,totalPages is " + totalPages
                + " ,lasePageItems is " + lasePageItems + " ,currentPages is " + currentPages + " ,maxItems is " + maxItems);

        if (select_index == 0 || totalPages == currentPages && select_index > lasePageItems) {
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
        } else if (select_index < 0) {
            int currentSelectIndex = maxItems - Math.abs(select_index) + 1;
            LogUtils.i(TAG, "currentSelectIndex is " + currentSelectIndex);
            if (currentSelectIndex <= 0) {
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            } else {
                index = (currentPages - 1) * maxItems + currentSelectIndex;
            }
        } else {
            if (select_index > maxItems) {
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            } else {
                index = (currentPages - 1) * maxItems + select_index;
            }
        }
        LogUtils.i(TAG, "index is " + index);
        preCallNumber = phoneInterface.getNumberByIndex(false, index, contactNumberInfoList);
        String name = phoneInterface.getNameByIndex(false, index, contactNumberInfoList);
        return PhoneUtils.callOrAsk(contactNumberInfoList, preCallNumber, name, operation_type, flowContext, false, search_Type, TAG);
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        if (StringUtils.equals(executeTag, TTSAnsConstant.PHONE_REDIAL[0]))
            phoneInterface.placeCall(preCallNumber);
    }

}
