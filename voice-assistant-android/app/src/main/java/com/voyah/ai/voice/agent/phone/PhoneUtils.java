package com.voyah.ai.voice.agent.phone;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/5/4
 **/
public class PhoneUtils {
    private static final String TAG = PhoneUtils.class.getSimpleName();
    public static final List<String> LOCATION_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
    public static String showPhoneCardRequestId; //展示电话卡片时的任务id-用于关闭卡片使用


    public static ClientAgentResponse callOrAsk(List<ContactNumberInfo> numberInfoList, String preCallNumber, String preCallName, String operation_type, Map<String, Object> flowContext, boolean isNotNumberSegment, String searchType, String uiType) {
        ClientAgentResponse clientAgentResponse;
        preCallNumber = StringUtils.isBlank(preCallNumber) ? "" : preCallNumber;
        preCallName = StringUtils.isBlank(preCallName) ? "" : preCallName;
        PhoneFlowContextUtils.setFlowContextParams(flowContext, null, null, preCallNumber, preCallName, operation_type, searchType);
        if (!StringUtils.equals(operation_type, Constant.OperationType.DIAL) && numberInfoList.size() == 1) {
            TTSBean ttsBean;
            if (StringUtils.equals(searchType, "number")) {
                ttsBean = getTtsBean(TTSAnsConstant.PHONE_SEARCH_ASK_NUMBER[0], TTSAnsConstant.PHONE_SEARCH_ASK_NUMBER[1]);
                getReplaceTtsBean(ttsBean, "@{tel_name}", preCallName);
            } else {
                ttsBean = getTtsBean(TTSAnsConstant.PHONE_SEARCH_ASK_NAME[0], TTSAnsConstant.PHONE_SEARCH_ASK_NAME[1]);
                getReplacesTtsBean(ttsBean, new String[]{"@{tel_name}", "@{tel_number}"}, new String[]{preCallName, preCallNumber});
            }

            //todo:TTS播报 xxx的电话号码为xxxxx,需要帮您呼叫吗?
            clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, ttsBean, "", uiType);
        } else {
            TTSBean ttsBean = getTtsBean(TTSAnsConstant.PHONE_CALL_REDIAL_NAME[0], TTSAnsConstant.PHONE_CALL_REDIAL_NAME[1]);
            getReplaceTtsBean(ttsBean, "@{tel_name}", preCallName);
//            if (isNotNumberSegment) {
//                clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.ASK_CALL, flowContext, ttsBean, "");
//            } else {
            clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean, TTSAnsConstant.PHONE_REDIAL[0]);
//            }
        }
        return clientAgentResponse;
    }

    //获取有效总页数
    public static int getTotalPages(int totalSize, int maxItems) {
        return totalSize / maxItems + (totalSize % maxItems == 0 ? 0 : 1);
    }

    //获取最后一页item个数
    public static int getLasePageItems(int totalSize, int maxItems) {
        if (totalSize % maxItems == 0)
            return totalSize - (((totalSize / maxItems) - 1) * maxItems);
        else
            return totalSize - ((totalSize / maxItems) * maxItems);
    }

    public static TTSBean getTtsBean(String ttsId, String defaultTts) {
        LogUtils.d(TAG, "getTtsBean ttsId:" + ttsId + " ,defaultTts:" + defaultTts);
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        if (null == ttsBean) {
            LogUtils.d(TAG, "getTtsBean ttsBean is null");
            ttsBean = new TTSBean();
            ttsBean.setSelectTTs(defaultTts);
        } else if (StringUtils.isBlank(ttsBean.getSelectTTs())) {
            LogUtils.d(TAG, "getSelectTTs is blank");
            ttsBean.setSelectTTs(defaultTts);
        }
        return ttsBean;
    }

    public static TTSBean getReplaceTtsBean(TTSBean ttsBean, String placeholder, String targetText) {
        String selectTts = ttsBean.getSelectTTs();
        if (selectTts.contains(placeholder))
            selectTts = selectTts.replace(placeholder, targetText);
        ttsBean.setSelectTTs(selectTts);
        return ttsBean;
    }

    public static TTSBean getReplacesTtsBean(TTSBean ttsBean, String[] placeholder, String[] targetText) {
        String selectTts = ttsBean.getSelectTTs();
//        LogUtils.d(TAG, "getReplacesTtsBean selectTts:" + selectTts);
        for (int i = 0; i < placeholder.length; i++) {
//            LogUtils.d(TAG, "placeholder[i]=" + placeholder[i] + " ,targetText[i]=" + targetText[i]);
            if (selectTts.contains(placeholder[i]))
                selectTts = selectTts.replace(placeholder[i], targetText[i]);
        }
        ttsBean.setSelectTTs(selectTts);
        return ttsBean;
    }

    public static int getScreenTypeBySoundLocation(String soundLocation) {
        LogUtils.d(TAG, "getScreenTypeBySoundLocation soundLocation:" + soundLocation);
        int screenType = 0;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (carType.contains("H37"))
            return screenType;
        if (LOCATION_LIST.contains(soundLocation)) {
            if (StringUtils.equals(soundLocation, LOCATION_LIST.get(0)))
                screenType = 0;
            else if (StringUtils.equals(soundLocation, LOCATION_LIST.get(1)))
                screenType = 1;
            else
                screenType = 2;
        }
        return screenType;
    }

    public static String getShowPhoneCardRequestId() {
        return showPhoneCardRequestId;
    }

    public static void setShowPhoneCardRequestId(String requestId) {
        showPhoneCardRequestId = requestId;
    }
}
