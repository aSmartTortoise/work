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
 * @author:lcy 按号码拨打、查询
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneNumberOpAgent extends BaseAgentX {
    private static final String TAG = PhoneNumberOpAgent.class.getSimpleName();

    private PhoneInterface phoneInterface;
    private String sessionId;
    private String mRequestId;
    private List<ContactInfo> contactInfoList = new ArrayList<>();
    private List<ContactInfo> searchContactInfoList = new ArrayList<>();
    private List<ContactNumberInfo> contactNumberInfoList = new ArrayList<>();
    private List<ContactInfo> yellowPageList = new ArrayList<>();
    private String preCallNumber;

    private boolean isDial = true;

    @Override
    public String AgentName() {
        return "phone_number#operation";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-------------PhoneNumberOpAgent------------");
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        contactInfoList.clear();
        searchContactInfoList.clear();
        contactNumberInfoList.clear();
        yellowPageList.clear();
        String scenario = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE, flowContext);
        String operation_type = getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
        String operation_source = getParamKey(paramsMap, Constant.OPERATION_SOURCE, 0);
        String index = getParamKey(paramsMap, Constant.INDEX, 0);
        String number = getParamKey(paramsMap, Constant.NUMBER, 0);
        String number_front = getParamKey(paramsMap, Constant.NUMBER_FRONT, 0);
        String number_end = getParamKey(paramsMap, Constant.NUMBER_END, 0);
        isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
        sessionId = getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
        mRequestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        yellowPageList.addAll(flowContext.containsKey(FlowContextKey.FC_YELLOW_PAGE_DATA) ? (List<ContactInfo>) flowContext.get(FlowContextKey.FC_YELLOW_PAGE_DATA) : yellowPageList);
        if (yellowPageList.isEmpty())
            yellowPageList.addAll(YellowPageResource.getInstance().provideContactInfo());
        LogUtils.i(TAG, "PhoneNumberOpAgent  scenario is " + scenario + "  operation_type is " + operation_type + " ,operation_source is " + operation_source
                + "index is " + index + " ,number is " + number + " ,number_front is " + number_front + " ,number_end is " + number_end + " ,isDial is " + isDial);
        if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.YELLOW_PAGE)) {
            contactInfoList.addAll(yellowPageList);
        } else if (!StringUtils.isBlank(operation_source) && StringUtils.equals(operation_source, Constant.OperationSource.CONTACT)) {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
        } else {
            contactInfoList.addAll(phoneInterface.getContactInfoList());
            contactInfoList.addAll(yellowPageList);
        }
        TTSBean ttsBean;
        //列表选择场景
//        if (false) {
//            KeyWordSelectUtil.keyWordSelect(flowContext, paramsMap, uiInterface);
//        } else {
        //非列表选择场景
        //查询、打给 1.开头 结尾 都需要判断蓝牙连接 通讯录同步 2.打给号码不需要
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        //1.蓝牙状态判断
        if (!phoneInterface.isBtConnect()) {
            //todo: TTS 请先连接手机蓝牙
            LogUtils.i(TAG, "TTS播报 请先连接手机蓝牙");
            phoneInterface.openBluetoothSettings();
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }
        //2.根据是否有号段判断是否需要检查通讯录状态(后面添加黄页来源判断-黄页不需要判断通讯录状态)
        if (!StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end) || StringUtils.equals(operation_type, Constant.OperationType.SEARCH)) {
            //2.1通讯录同步中
            if (phoneInterface.isSyncContacting() && phoneInterface.searchListByNumber(number_front, number_end, number, yellowPageList).isEmpty()) {
                //todo:TTS 正在同步通讯录,请稍等
                LogUtils.i(TAG, "TTS播报 正在同步通讯录,请稍等");
                phoneInterface.setBluetoothPhoneTab(0);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
            }
            //2.2未同步通讯录数据
            if (!phoneInterface.isSyncContacted() && phoneInterface.searchListByNumber(number_front, number_end, number, yellowPageList).isEmpty()) {
                //todo:TTS 请先同步通讯录
                LogUtils.i(TAG, "TTS播报 请先同步通讯录");
                phoneInterface.setBluetoothPhoneTab(0);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
            }
        }

        if (!StringUtils.isBlank(number_front) && (number_front.length() < 3 || number_front.length() > 4)) {
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_NUMBER_FRONT[0], TTSAnsConstant.PHONE_CALL_NUMBER_FRONT[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        } else if (!StringUtils.isBlank(number_end) && (number_end.length() < 3 || number_end.length() > 4)) {
            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_NUMBER_END[0], TTSAnsConstant.PHONE_CALL_NUMBER_END[1]);
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
        }

        boolean isNotNumberSegment = StringUtils.isNotBlank(number);

        //2.3参数判断 存在号码不需要判断号段及索引
        if (!StringUtils.isBlank(number)) {
            //拨打
            if (isDial) {
                //todo:TTS 即将呼叫 number,确认还是取消?
                LogUtils.i(TAG, "即将呼叫" + number + ",确认还是取消?");
                flowContext.put(Constant.PARAMS_NUMBER, number);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_REDIAL[0], TTSAnsConstant.PHONE_CALL_REDIAL[1]);
                PhoneUtils.getReplaceTtsBean(ttsBean, "@{tel_number}", number);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, ttsBean);
            } else {
                //查询
                searchContactInfoList.addAll(phoneInterface.searchListByNumber(null, null, number, contactInfoList));
                if (searchContactInfoList.isEmpty()) {
                    //todo:TTS 抱歉，没有查到相关号码   计数：超过次数后返回没有找到状态值
                    LogUtils.i(TAG, "抱歉，没有查到相关号码");
//                        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, TTSAnsConstant.PHONE_NUMBER_INVALID);
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_NOT_FIND[0], TTSAnsConstant.PHONE_NOT_FIND[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
                }
                //查到多个联系人
                if (searchContactInfoList.size() > 1) {
                    LogUtils.i(TAG, "找到多个联系人,选择第几个  showUi");
                    contactNumberInfoList.addAll(phoneInterface.getContactToNumberList(searchContactInfoList));
                    setFlowContextCardList(contactNumberInfoList, flowContext);
                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, contactNumberInfoList, null, null, operation_type, "number");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[0], TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[1]);
                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                    return clientAgentResponse;
                } else {
                    //找到一个联系人
                    String preCallName = searchContactInfoList.get(0).getName();
                    preCallNumber = searchContactInfoList.get(0).getNumberInfoList().get(0).getNumber();
                    contactNumberInfoList.addAll(searchContactInfoList.get(0).getNumberInfoList());
                    //todo:TTS 是name的电话,需要帮您呼叫么?
//                    LogUtils.i(TAG, preCallNumber + "是" + phone_name + "的电话,需要帮您呼叫么?");
//                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, null, preCallNumber, phone_name, operation_type);
//                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, TTSAnsConstant.PHONE_SEARCH_ASK_NUMBER.replace("@phone_number", preCallNumber).replace("@phone_name", phone_name));
//                    return clientAgentResponse;
                    return PhoneUtils.callOrAsk(contactNumberInfoList, preCallNumber, preCallName, operation_type, flowContext, isNotNumberSegment, "number", TAG);
                }
            }

        }

        searchContactInfoList.addAll(phoneInterface.searchListByNumber(number_front, number_end, null, contactInfoList));

        //有号段
        if (!StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end)) {
            if (searchContactInfoList.isEmpty()) {
                //todo:TTS 抱歉,我没有找到相关号码
                LogUtils.i(TAG, "抱歉,没有找到该号码");
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_NOT_FIND[0], TTSAnsConstant.PHONE_NOT_FIND[1]);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
            }

            if (!StringUtils.isBlank(index)) {
                boolean isMinus = false;
                int position = getIntegerParams(index);
                isMinus = position < 0;
                position = isMinus ? Math.abs(position) : position;
                contactNumberInfoList.addAll(phoneInterface.getContactToNumberList(searchContactInfoList));

                //无效索引
                if (position == 0 || position > contactNumberInfoList.size()) {
                    //todo:TTS播报 超出选择范围 计数
                    LogUtils.i(TAG, "TTS播报 超出范围,请在1到" + contactNumberInfoList.size() + "间选择");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.NO_SEARCH_RESULT, flowContext, ttsBean);
                }
                LogUtils.i(TAG, "position is " + position + " isMinus is " + isMinus + " ，contactNumberInfoList.size is " + contactNumberInfoList.size());

                preCallNumber = phoneInterface.getNumberByIndex(isMinus, position, contactNumberInfoList);
                String preCallName = phoneInterface.getNameByIndex(isMinus, position, contactNumberInfoList);
                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CALL_REDIAL_NAME[0], TTSAnsConstant.PHONE_CALL_REDIAL_NAME[1]);
                PhoneUtils.getReplaceTtsBean(ttsBean, "@{tel_name}", preCallName);
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean, TTSAnsConstant.PHONE_REDIAL[0]);

            } else {
                if (searchContactInfoList.size() > 1) {
                    String tts;
                    if (isDial) {
                        //todo:TTS 找到多个相似联系人,你想拨打第几个?
                        LogUtils.i(TAG, "找到多个相似联系人,你想拨打第几个?");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[0], TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[1]);
                    } else {
                        //todo:TTS 找到多个电话,你想拨打第几个
                        LogUtils.i(TAG, "查到多个联系人,你想拨打第几个?");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[0], TTSAnsConstant.PHONE_DIAL_FIND_MORE_NAME[1]);
                    }
                    contactNumberInfoList.addAll(phoneInterface.getContactToNumberList(searchContactInfoList));
                    setFlowContextCardList(contactNumberInfoList, flowContext);
                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, contactNumberInfoList, null, null, operation_type, "number");
                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
                    return clientAgentResponse;
                } else {
                    contactNumberInfoList.addAll(searchContactInfoList.get(0).getNumberInfoList());
                    LogUtils.i(TAG, "contactNumberInfoList.size is " + contactNumberInfoList);
                    if (contactNumberInfoList.size() > 1) {
                        //todo:TTS 找到多个号码,你想拨打第几个?
                        LogUtils.i(TAG, "找到多个号码,你想拨打第几个?");
                        setFlowContextCardList(contactNumberInfoList, flowContext);
                        PhoneFlowContextUtils.setFlowContextParams(flowContext, null, contactNumberInfoList, null, null, operation_type, "number");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_FIND_MORE_NUMBER[0], TTSAnsConstant.PHONE_FIND_MORE_NUMBER[1]);
                        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, ttsBean, "", TAG);
//                        return clientAgentResponse;
                    } else {
                        //单个号码
                        String name = contactNumberInfoList.get(0).getName();
                        preCallNumber = contactNumberInfoList.get(0).getNumber();
                        return PhoneUtils.callOrAsk(contactNumberInfoList, preCallNumber, name, operation_type, flowContext, isNotNumberSegment, "number", TAG);
                    }
                }
            }

        }
//        }
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
        if (isDial) {
            if (contactNumberInfoList.isEmpty() || contactNumberInfoList.size() == 1)
                return;
            scene = SCENARIO_NUMBER;
        } else {
            if (contactNumberInfoList.isEmpty())
                return;
            if (contactNumberInfoList.size() == 1)
                scene = Constant.SCENARIO_CALL_CONFIRM;
            else
                scene = SCENARIO_NUMBER;
        }
        map.put("scene", scene);
        map.put("sessionId", sessionId);
        map.put("requestId", mRequestId);
        map.put("numberList", contactNumberInfoList);
        LogUtils.d(TAG, "showUi");
        PhoneUtils.setShowPhoneCardRequestId(mRequestId);
        UIMgr.INSTANCE.showCard(UiConstant.CardType.PHONE_CARD, map, sessionId, mAgentIdentifier, location);
    }
}
