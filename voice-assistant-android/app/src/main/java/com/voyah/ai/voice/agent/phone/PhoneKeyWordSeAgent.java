package com.voyah.ai.voice.agent.phone;

import static com.voyah.ai.voice.agent.phone.PhoneFlowContextUtils.setFlowContextCardList;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.UiConstant;
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
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 列表-关键词选择
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneKeyWordSeAgent extends BaseAgentX {
    private static final String TAG = PhoneKeyWordSeAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;
    private UiInterface uiInterface;
    private List<ContactNumberInfo> selectContactNumberList = new ArrayList<>();
    private String preCallNumber;
    private String preCallName;

    private String sessionId;
    private String mRequestId;

    @Override
    public String AgentName() {
        return "phone_keyword#select";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "--------PhoneKeyWordSeAgent---------");
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        uiInterface = DeviceHolder.INS().getDevices().getUiCardInterface();
        selectContactNumberList.clear();
        String deviceScenarioState = (String) flowContext.get(Constant.PARAMS_DEVICE_SCENARIO_STATE);
        List<ContactNumberInfo> flowNumberInfoList = (List<ContactNumberInfo>) flowContext.get(Constant.PARAMS_NUMBER_LIST);
        String operation_type = (String) flowContext.get(Constant.PARAMS_OPERATION_TYPE);
        boolean isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
        String index = getParamKey(paramsMap, Constant.INDEX, 0);
        String name = getParamKey(paramsMap, Constant.NAME, 0);
        String number = getParamKey(paramsMap, Constant.NUMBER, 0);
        String number_front = getParamKey(paramsMap, Constant.NUMBER_FRONT, 0);
        String number_end = getParamKey(paramsMap, Constant.NUMBER_END, 0);
        String search_Type = getFlowContextKey(Constant.PARAMS_SEARCH_TYPE, flowContext);
        sessionId = getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
        mRequestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);

        LogUtils.i(TAG, "flowNumberInfoList.size is " + (flowNumberInfoList == null ? "0" : flowNumberInfoList.size()) + " , operation_type is " + operation_type
                + " ,index is " + index + " ,name is " + name + " ,number is " + " , number_front is " + number_front + " ,number_end is " + number_end + " ,search_Type is " + search_Type + ", sessionId is " + sessionId);
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
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        int position = 0;
        boolean isMinus = false;
        //todo:数据平铺的话不需要判断号码或联系人场景
        if (!StringUtils.isBlank(index)) {
            position = getIntegerParams(index);
            isMinus = position < 0;
            position = isMinus ? Math.abs(position) : position;
        }
        //1>.名字+号码+索引
        if (!StringUtils.isBlank(name) && (!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))
                && (!StringUtils.isBlank(index))) {
            //符合名字+号码组合的所有数据
            selectContactNumberList.addAll(phoneInterface.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList));

            if (StringUtils.equals(index, "0")) {
                //todo:TTS播报 超出选择范围 计数
                LogUtils.i(TAG, "TTS播报超出选择范围");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG);
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            if (selectContactNumberList.isEmpty()) {
                //todo:TTS播报未找到号码
                //todo:添加计数判断
                LogUtils.i(TAG, "TTS播报未找到号码");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_NOT_FIND[0], TTSAnsConstant.PHONE_NOT_FIND[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            //索引是否合法
            if (position > selectContactNumberList.size()) {
                //todo:超出选择范围 计数
                LogUtils.i(TAG, "TTS播报 超出选择范围");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, selectContactNumberList);
            preCallName = phoneInterface.getNameByIndex(isMinus, position, selectContactNumberList);
            return PhoneUtils.callOrAsk(selectContactNumberList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);

        } else if (!StringUtils.isBlank(name) && (!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))) {
            //2>.名字+号码
            List<ContactNumberInfo> selectContactNumberInfoList = phoneInterface.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList);
            if (selectContactNumberInfoList.isEmpty()) {
                //todo:TTS播报未找到号码
                //todo:添加计数判断
                LogUtils.i(TAG, "TTS播报 未找到号码");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[0], TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            if (selectContactNumberInfoList.size() > 1) {
                //todo:TTS播报：找到多个号码,你要打给第几个
                LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个 showUi");
                setFlowContextCardList(selectContactNumberInfoList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberInfoList, null, null, operation_type);
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            }

            preCallNumber = selectContactNumberInfoList.get(0).getNumber();
            preCallName = selectContactNumberInfoList.get(0).getName();
            return PhoneUtils.callOrAsk(selectContactNumberInfoList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);
        } else if (!StringUtils.isBlank(name) && !StringUtils.isBlank(index)) {
            //3>.名字+索引
            selectContactNumberList.addAll(phoneInterface.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList));

            if (StringUtils.equals(index, "0") || position > selectContactNumberList.size()) {
                //todo:TTS播报 超出选择范围 计数
                LogUtils.i(TAG, "TTS播报 超出选择范围");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            if (selectContactNumberList.isEmpty()) {
                //todo:添加计数判断
                LogUtils.i(TAG, "TTS播报 联系人不存在,请重新选择");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[0], TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, selectContactNumberList);
            preCallName = phoneInterface.getNameByIndex(isMinus, position, selectContactNumberList);
            return PhoneUtils.callOrAsk(selectContactNumberList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);

        } else if ((!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))
                && (!StringUtils.isBlank(index))) {
            //4>.号码+索引
            if (StringUtils.equals(index, "0")) {
                //todo:TTS播报 超出选择范围 计数
                LogUtils.i(TAG, "TTS播报 不存在该号码");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[0], TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }
            selectContactNumberList.addAll(phoneInterface.selectByNumber(number_front, number_end, number, flowNumberInfoList));
            if (selectContactNumberList.isEmpty()) {
                //todo:TTS播报未找到号码
                //todo:添加计数判断
                LogUtils.i(TAG, "TTS播报 未找到号码");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[0], TTSAnsConstant.PHONE_FIND_FAIL_NUMBER[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            if (position > selectContactNumberList.size()) {
                //todo:TTS播报 超出选择范围 计数
                LogUtils.i(TAG, "TTS播报 超出范围,请在1到" + flowNumberInfoList.size() + "间选择");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }
            preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, selectContactNumberList);
            preCallName = phoneInterface.getNameByIndex(isMinus, position, selectContactNumberList);
            return PhoneUtils.callOrAsk(selectContactNumberList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);
        } else if (!StringUtils.isBlank(name)) {
            //5>.名字
            selectContactNumberList.addAll(phoneInterface.selectByName(name, flowNumberInfoList));
            if (selectContactNumberList.isEmpty()) {
                //todo:TTS播报 未找到联系人
                LogUtils.i(TAG, "TTS播报 未找到联系人");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            if (selectContactNumberList.size() > 1) {
                //todo:TTS播报：找到多个号码,你要打给第几个
                LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个");
                setFlowContextCardList(selectContactNumberList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberList, null, null, operation_type);
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            }
            preCallName = selectContactNumberList.get(0).getName();
            preCallNumber = selectContactNumberList.get(0).getNumber();
            return PhoneUtils.callOrAsk(selectContactNumberList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);
        } else if ((!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))) {
            //6>.号码
            selectContactNumberList.addAll(phoneInterface.selectByNumber(number_front, number_end, number, flowNumberInfoList));
            if (selectContactNumberList.isEmpty()) {
                //todo:TTS播报 未找到指定号码
                LogUtils.i(TAG, "TTS播报 未找到号码");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_NOT_FIND[0], TTSAnsConstant.PHONE_NOT_FIND[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }
            if (selectContactNumberList.size() > 1) {
                //todo:TTS 找到多个联系人,你要打给第几个
                LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个");
                setFlowContextCardList(selectContactNumberList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberList, null, null, operation_type);
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            }

            //todo:TTS播报 即将呼叫+name or number
            preCallNumber = selectContactNumberList.get(0).getNumber();
            preCallName = selectContactNumberList.get(0).getName();
            LogUtils.d(TAG, "executeAgent, closeCard");
            UIMgr.INSTANCE.dismissCard(mAgentIdentifier);
            return PhoneUtils.callOrAsk(selectContactNumberList, preCallNumber, preCallName, Constant.OperationType.DIAL, flowContext, false, search_Type, TAG);
        }
        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext);
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        if (StringUtils.equals(executeTag, TTSAnsConstant.PHONE_REDIAL[0]))
            phoneInterface.placeCall(preCallNumber);
    }

    @Override
    public void showUi(String uiType, int location) {
        if (!StringUtils.equals(uiType, TAG))
            return;
        HashMap<String, Object> map = new HashMap<>();
        String scene = "";
        if (!selectContactNumberList.isEmpty() && selectContactNumberList.size() > 1)
            scene = Constant.SCENARIO_NUMBER;
        map.put("scene", scene);
        map.put("sessionId", sessionId);
        map.put("requestId", mRequestId);
        map.put("numberList", selectContactNumberList);
        PhoneUtils.setShowPhoneCardRequestId(mRequestId);
        UIMgr.INSTANCE.showCard(UiConstant.CardType.PHONE_CARD, map, sessionId, mAgentIdentifier, location);
//            uiInterface.showCard(UiConstant.CardType.PHONE_CARD, selectContactNumberList);
    }
}
