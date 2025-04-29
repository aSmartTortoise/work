package com.voyah.ai.voice.agent.phone;

import static com.voyah.ai.voice.agent.generic.Constant.SCENARIO_NUMBER;
import static com.voyah.ai.voice.agent.phone.PhoneFlowContextUtils.setFlowContextCardList;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;
import com.voyah.ds.domain.call.data.yellow.YellowPageResource;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 按名字拨打、查询
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneNameOpAgent extends BaseAgentX {
    private static final String TAG = PhoneNameOpAgent.class.getSimpleName();

    private String preCallNumber;
    private String preCallName;
    private PhoneInterface phoneInterface;

    private String sessionId;
    private String mRequestId;

    private boolean isDial = true;

    private List<ContactInfo> contactInfoList = new ArrayList<>();
    private List<ContactInfo> yellowPageList = new ArrayList<>();
    private List<ContactInfo> showContactList = new ArrayList<>();
    private List<ContactNumberInfo> showContactNumberList = new ArrayList<>();

    @Override
    public String AgentName() {
        return "phone_name#operation";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------PhoneNameOpAgent--------");
        preCallNumber = "";
        showContactList.clear();
        showContactNumberList.clear();
        contactInfoList.clear();
        yellowPageList.clear();
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();

        String scenario = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE, flowContext);
        String operation_type = getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
        isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
        String operation_source = getParamKey(paramsMap, Constant.OPERATION_SOURCE, 0);
        String name = getParamKey(paramsMap, Constant.NAME, 0);
        String index = getParamKey(paramsMap, Constant.INDEX, 0);
        sessionId = getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
        mRequestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        yellowPageList.addAll(flowContext.containsKey(FlowContextKey.FC_YELLOW_PAGE_DATA) ? (List<ContactInfo>) flowContext.get(FlowContextKey.FC_YELLOW_PAGE_DATA) : yellowPageList);
        if (yellowPageList.isEmpty())
            yellowPageList.addAll(YellowPageResource.getInstance().provideContactInfo());
        LogUtils.i(TAG, "PhoneNameOpAgent  scenario is " + scenario + "  operation_type is " + operation_type + " ,operation_source is " + operation_source +
                " ,name is " + name + " ,index is " + index + " , yellowPageList.size is " + yellowPageList.size() + " ,sessionId is " + sessionId);
        if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.YELLOW_PAGE)) {
            contactInfoList.addAll(yellowPageList);
        } else if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.CONTACT)) {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
        } else {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
            contactInfoList.addAll(yellowPageList);
        }
        LogUtils.i(TAG, "contactInfoList.size is" + contactInfoList.size());
        //列表选择场景
//        if (false) {
//            KeyWordSelectUtil.keyWordSelect(flowContext, paramsMap, uiInterface);
//        } else {
        //非列表选择场景
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        //1.蓝牙未连接
        if (!phoneInterface.isBtConnect()) {
            //todo:TTS播报 请先连接手机蓝牙
            LogUtils.i(TAG, "TTS播报 请先连接手机蓝牙");
            phoneInterface.openBluetoothSettings();
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        //2.正在同步通讯录
        if (phoneInterface.isSyncContacting() && !phoneInterface.isYellowPageContainsName(yellowPageList, name)) {
            //todo:TTS播报 正在同步通讯录,请稍等
            LogUtils.i(TAG, "TTS播报 正在同步通讯录,请稍等");
            phoneInterface.setBluetoothPhoneTab(0);
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        //3.未同步通讯录数据
        if (!phoneInterface.isSyncContacted() && !phoneInterface.isYellowPageContainsName(yellowPageList, name)) {
            //todo:TTS播报 请先同步通讯录
            LogUtils.i(TAG, "TTS播报 请先同步通讯录");
            phoneInterface.setBluetoothPhoneTab(0);
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        showContactList.addAll(phoneInterface.searchListByName(name, contactInfoList));
        //4.1 搜索为空
        if (showContactList.isEmpty()) {
            //todo:TTS播报 未找到该联系人,请问你要打给谁
            //todo:计数
            LogUtils.i(TAG, "TTS播报 未找到联系人");
            TTSBean ttsBean;
            if (isDial) {
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONTACT_INVALID[0], TTSAnsConstant.PHONE_CONTACT_INVALID[1]);
                PhoneUtils.getReplaceTtsBean(ttsBean, "@{tel_name}", name);
            } else {
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_NOT_FIND[0], TTSAnsConstant.PHONE_NOT_FIND[1]);
            }
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
        }

        showContactNumberList.addAll(phoneInterface.getContactToNumberList(showContactList));
        LogUtils.i(TAG, "showContactNumberList.size is " + showContactNumberList.size());

        int position = 0;
        boolean isMinus = false;
        if (!StringUtils.isBlank(index)) {
            position = getIntegerParams(index);
            isMinus = position < 0;
            position = isMinus ? Math.abs(position) : position;
        }
        //4.2 有多个联系人
        if (showContactList.size() > 1) {
            if (!StringUtils.isBlank(index)) {
                if (position == 0 || position > showContactNumberList.size()) {
                    //todo:TTS播报 超出选择范围 计数
                    LogUtils.i(TAG, "超出选择范围");
                    TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
                }
                preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, showContactNumberList);
                preCallName = name;
                return PhoneUtils.callOrAsk(showContactNumberList, preCallNumber, preCallName, operation_type, flowContext, false, "name", TAG);
            } else {
                //todo:TTS播报  找到多个相似联系人,你要打给第几个
                LogUtils.i(TAG, "TTS播报 找到多个联系人,你要打给第几个 showUi");
//                showContactNumberList.addAll(phoneInterface.getContactToNumberList(showContactList));
                setFlowContextCardList(showContactNumberList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, showContactNumberList, null, null, operation_type, "name");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[0], TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            }
        }

        if (showContactNumberList.size() > 1) {
            if (!StringUtils.isBlank(index)) {
                if (position == 0 || position > showContactNumberList.size()) {
                    //todo:TTS播报 超出选择范围 计数
                    LogUtils.i(TAG, "超出选择范围");
                    TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
                }
                preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, showContactNumberList);
                preCallName = name;
                return PhoneUtils.callOrAsk(showContactNumberList, preCallNumber, preCallName, operation_type, flowContext, false, "name", TAG);
            } else {
                //todo:TTS播报 找到多个电话,你想拨打第几个
                LogUtils.i(TAG, "TTS播报 找到多个电话");
                setFlowContextCardList(showContactNumberList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, showContactNumberList, null, null, operation_type, "name");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            }
        }

        //4.4单个联系人-单个号码
        preCallNumber = showContactNumberList.get(0).getNumber();
        preCallName = showContactNumberList.get(0).getName();
        return PhoneUtils.callOrAsk(showContactNumberList, preCallNumber, preCallName, operation_type, flowContext, false, "name", TAG);
//        }
//        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext);
    }


    @Override
    public void executeOrder(String executeTag, int location) {
        LogUtils.i(TAG, "executeTag is " + executeTag);
        if (StringUtils.equals(TTSAnsConstant.PHONE_REDIAL[0], executeTag))
            phoneInterface.placeCall(preCallNumber);
    }

    @Override
    public void showUi(String uiType, int location) {
        if (!StringUtils.equals(uiType, TAG))
            return;

        HashMap<String, Object> map = new HashMap<>();
        String scene = "";
        if (isDial) {
            if (showContactNumberList.isEmpty() || showContactNumberList.size() == 1)
                return;
            scene = SCENARIO_NUMBER;
        } else {
            if (showContactNumberList.isEmpty())
                return;
            if (showContactNumberList.size() == 1)
                scene = Constant.SCENARIO_CALL_CONFIRM;
            else
                scene = SCENARIO_NUMBER;
        }
        map.put("scene", scene);
        map.put("sessionId", sessionId);
        map.put("requestId", mRequestId);
        map.put("numberList", showContactNumberList);
        PhoneUtils.setShowPhoneCardRequestId(mRequestId);
        UIMgr.INSTANCE.showCard(UiConstant.CardType.PHONE_CARD, map, sessionId, mAgentIdentifier, location);
//        LogUtils.i(TAG, "showUi");
//        if (isDial) {
//            if (!showContactNumberList.isEmpty() && showContactNumberList.size() > 1)
//                uiInterface.showCard(UiConstant.CardType.PHONE_CARD, showContactNumberList);
//        } else {
//            if (!showContactNumberList.isEmpty())
//                uiInterface.showCard(UiConstant.CardType.PHONE_CARD, showContactNumberList);
//        }
    }
}
