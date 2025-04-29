package com.voyah.ai.voice.agent.phone;

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
 * @author:lcy 组合场景
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneComOpAgent extends BaseAgentX {
    private static String TAG = PhoneComOpAgent.class.getSimpleName();
    private PhoneInterface phoneInterface;
    private List<ContactInfo> showContactList = new ArrayList<>();
    private List<ContactNumberInfo> showContactNumberList = new ArrayList<>();

    private List<ContactInfo> contactInfoList = new ArrayList<>();
    private List<ContactInfo> yellowPageList = new ArrayList<>();
    private String preCallNumber;
    private String preCallName;
    private String sessionId;
    private String mRequestId;

    boolean isDial = true;

    @Override
    public String AgentName() {
        return "phone_combination#operation";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        showContactList.clear();
        showContactNumberList.clear();
        contactInfoList.clear();
        yellowPageList.clear();
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        String scenario = (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        String operation_type = getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
        String operation_source = getParamKey(paramsMap, Constant.OPERATION_SOURCE, 0);
        String index = getParamKey(paramsMap, Constant.INDEX, 0);
        //列表选择参数+号码拨打查询
        String number = getParamKey(paramsMap, Constant.NUMBER, 0);
        String name = getParamKey(paramsMap, Constant.NAME, 0);
        String number_front = getParamKey(paramsMap, Constant.NUMBER_FRONT, 0);
        String number_end = getParamKey(paramsMap, Constant.NUMBER_END, 0);
        sessionId = getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
        mRequestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        yellowPageList.addAll(flowContext.containsKey(FlowContextKey.FC_YELLOW_PAGE_DATA) ? (List<ContactInfo>) flowContext.get(FlowContextKey.FC_YELLOW_PAGE_DATA) : yellowPageList);
        if (yellowPageList.isEmpty())
            yellowPageList.addAll(YellowPageResource.getInstance().provideContactInfo());
        isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
        LogUtils.i(TAG, "PhoneComOpAgent  scenario is " + scenario + "  operation_type is " + operation_type + " ,operation_source is " + operation_source
                + " ,index is " + index + " ,number is " + number + " ,name is " + name + " ,number_front is " + number_front + " ,number_end is " + number_end + " ,isDial is " + isDial);
        if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.YELLOW_PAGE)) {
            contactInfoList.addAll(yellowPageList);
        } else if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.CONTACT)) {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
        } else {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
            contactInfoList.addAll(yellowPageList);
        }
        LogUtils.i(TAG, "contactInfoList.size is" + contactInfoList.size());
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

        //3.未同步通讯录数据(通讯录未同步支持黄页操作)
        if (!phoneInterface.isSyncContacted() && !phoneInterface.isYellowPageContainsName(yellowPageList, name)) {
            //todo:TTS播报 请先同步通讯录
            LogUtils.i(TAG, "TTS播报 请先同步通讯录");
            phoneInterface.setBluetoothPhoneTab(0);
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }


        if (!StringUtils.isBlank(number_front) && (number_front.length() < 3 || number_front.length() > 4)) {
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_NUMBER_FRONT[0], TTSAnsConstant.PHONE_CALL_NUMBER_FRONT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        } else if (!StringUtils.isBlank(number_end) && (number_end.length() < 3 || number_end.length() > 4)) {
            TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_NUMBER_END[0], TTSAnsConstant.PHONE_CALL_NUMBER_END[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        boolean isNotNumberSegment = StringUtils.isNotBlank(number);
        //4.名字+号码操作
        showContactList.addAll(phoneInterface.searchListByNameAndNumber(name, number_front, number_end, number, contactInfoList));

        //4.1没有搜索到数据
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
        //1.是否有索引
        if (StringUtils.isBlank(index)) {
            if (showContactList.size() > 1) {
                LogUtils.i(TAG, "TTS播报 找到多个联系人,你要打给第几个 showUi");
                showContactNumberList.addAll(phoneInterface.getContactToNumberList(showContactList));
                setFlowContextCardList(showContactNumberList, flowContext);
                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, showContactNumberList, null, null, operation_type, "all");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[0], TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[1]);
                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                return clientAgentResponse;
            } else {
                showContactNumberList.addAll(phoneInterface.getContactToNumberList(showContactList));
                if (showContactNumberList.size() > 1) {
                    //todo:TTS播报 找到多个电话,你想拨打第几个
                    LogUtils.i(TAG, "TTS播报 找到多个电话");
                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, showContactNumberList, null, null, operation_type, "all");
                    TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                    return clientAgentResponse;
                } else {
                    preCallName = showContactList.get(0).getName();
                    preCallNumber = showContactList.get(0).getNumberInfoList().get(0).getNumber();
                    return PhoneUtils.callOrAsk(showContactNumberList, preCallNumber, preCallName, operation_type, flowContext, isNotNumberSegment, "all", TAG);
                }
            }
        } else {
            showContactNumberList.addAll(showContactList.get(0).getNumberInfoList());
            int position = 10000;
            position = getIntegerParams(index);
            boolean isMinus = position < 0;
            position = isMinus ? Math.abs(position) : position;
            //无效索引
            if (position == 0 || position > showContactNumberList.size()) {
                //todo:TTS播报 超出选择范围 计数
                LogUtils.i(TAG, "超出选择范围");
                TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, ttsBean);
            }

            preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, showContactNumberList);
            String phone_name = showContactList.get(0).getName();
//            if (isDial) {
//                //todo:TTS播报 即将呼叫 联系人名
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else {
//                //todo:TTS播报 xxx的电话号码为xxxxx,需要帮您呼叫吗?
//                LogUtils.i(TAG, "TTS播报 " + phone_name + "的号码为" + preCallNumber + ",需要帮您呼叫么");
//                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, null, preCallNumber, phone_name, operation_type);
//                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, TTSAnsConstant.PHONE_CALL_ASK.replace("@name", name).replace("@phone_number", preCallNumber));
//                clientAgentResponse.setShowCard(true);
//                return clientAgentResponse;
//            }
            return PhoneUtils.callOrAsk(showContactNumberList, preCallNumber, phone_name, operation_type, flowContext, isNotNumberSegment, "all", TAG);
        }


//        //4.2多联系人 张三-章三
//        if (showContactList.size() > 1 && StringUtils.isBlank(index)) {
//            LogUtils.i(TAG, "TTS播报 找到多个联系人,你要打给第几个 showUi");
//            showContactNumberList.addAll(phoneInterface.getContactToNumberList(showContactList));
//            PhoneFlowContextUtils.setFlowContextParams(flowContext, null, showContactNumberList, null, null, operation_type);
//            ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME);
//            clientAgentResponse.setShowCard(true);
//            return clientAgentResponse;
//        }
//
//        showContactNumberList.addAll(showContactList.get(0).getNumberInfoList());
//        //索引
//        int position = 10000;
//        boolean isMinus = false;
//        //4.3单联系人多号码(判断索引)
//        //4.3.1 索引
//        if (!StringUtils.isBlank(index)) {
//            position = Integer.parseInt(index);
//            isMinus = position < 0;
//            position = isMinus ? Math.abs(position) : position;
//            //无效索引
//            if (position == 0 || position > showContactNumberList.size()) {
//                //todo:TTS播报 超出选择范围 计数
//                LogUtils.i(TAG, "超出选择范围");
////                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG);
////                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_OUT_OF_RANG.replace("@position", showContactNumberList.size() + ""));
//                return invalidCount(flowContext, TTSAnsConstant.PHONE_OUT_OF_RANG.replace("@position", showContactNumberList.size() + ""));
//            }
//        }
//        //4.3.2 索引合法  单联系人多号码
//        if (showContactNumberList.size() > 1) {
//            preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, showContactNumberList);
//            String phone_name = showContactList.get(0).getName();
//            if (isDial) {
//                //todo:TTS播报 即将呼叫 联系人名
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else {
//                //todo:TTS播报 xxx的电话号码为xxxxx,需要帮您呼叫吗?
//                LogUtils.i(TAG, "TTS播报 " + phone_name + "的号码为" + preCallNumber + ",需要帮您呼叫么");
//                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, null, preCallNumber, phone_name, operation_type);
//                ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, TTSAnsConstant.PHONE_CALL_ASK.replace("@name", name).replace("@phone_number", preCallNumber));
//                clientAgentResponse.setShowCard(true);
//                return clientAgentResponse;
//            }
//        } else {
//            //单联系人单号码
//            String phone_name = showContactList.get(0).getName();
//            preCallNumber = showContactNumberList.get(0).getNumber();
//            if (isDial) {
//                //todo:TTS播报 即将呼叫 联系人名
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else {
//                //todo:TTS播报 xxx的电话号码为xxxxx,需要帮您呼叫吗?
//                LogUtils.i(TAG, "TTS播报 " + phone_name + "的号码为" + preCallNumber + ",需要帮您呼叫么");
//                PhoneFlowContextUtils.setFlowContextParams(flowContext, null, null, preCallNumber, phone_name, operation_type);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, TTSAnsConstant.PHONE_CALL_ASK.replace("@name", phone_name).replace("@phone_number", phone_name));
//            }
//        }
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
        if (isDial) {
            if (showContactNumberList.isEmpty() || showContactNumberList.size() == 1)
                return;
            scene = Constant.SCENARIO_NUMBER;
        } else {
            if (showContactNumberList.isEmpty())
                return;
            if (showContactNumberList.size() == 1)
                scene = Constant.SCENARIO_CALL_CONFIRM;
            else
                scene = Constant.SCENARIO_NUMBER;
        }
        map.put("scene", scene);
        map.put("sessionId", sessionId);
        map.put("requestId", mRequestId);
        map.put("numberList", showContactNumberList);
        PhoneUtils.setShowPhoneCardRequestId(mRequestId);
        UIMgr.INSTANCE.showCard(UiConstant.CardType.PHONE_CARD, map, sessionId, mAgentIdentifier, location);
//        if (isDial) {
//            if (!showContactNumberList.isEmpty() && showContactNumberList.size() > 1)
//                uiInterface.showCard(UiConstant.CardType.PHONE_CARD, showContactNumberList);
//        } else {
//            if (!showContactNumberList.isEmpty())
//                uiInterface.showCard(UiConstant.CardType.PHONE_CARD, showContactNumberList);
//        }

    }
}
