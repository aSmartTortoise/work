package com.voyah.ai.voice.agent.phone;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
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
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/5/2
 **/
//todo:和永杰沟通下上一页下一页、第几页接口带有返回值，表示是否成功、是否已经是当前页(保证每页展示数量改变后只需要UI适配就可以是否可行),客户端自己记录(和永杰沟通没页展示数量)
@ClassAgent
public class PhonePageAgent extends BaseAgentX {
    private static final String TAG = PhonePageAgent.class.getSimpleName();

    private List<ContactNumberInfo> contactNumberInfoList = new ArrayList<>();

    private UiInterface uiInterface;

    @Override
    public String AgentName() {
        return "phone#page";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "PhonePageAgent");
        uiInterface = DeviceHolder.INS().getDevices().getUiCardInterface();
        String index_type = getParamKey(paramsMap, "index_type", 0);
        int select_index = Integer.parseInt(getParamKey(paramsMap, Constant.SELECT_INDEX, 0));
        int maxItems = 4;
        String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        int currentPage = uiInterface.getCurrentPage(PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
        contactNumberInfoList.clear();
        contactNumberInfoList.addAll((List<ContactNumberInfo>) flowContext.get(Constant.PARAMS_NUMBER_LIST));
//        LogUtils.i(TAG, "contactNumberInfoList " + new Gson().toJson(contactNumberInfoList));
        LogUtils.i(TAG, "currentPage is " + currentPage);
//        String tts = "";
        int retCode = 0;
        int totalPages = 0;
//        int lasePages = 0;
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }

        TTSBean ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_CONFIRM[0], TTSAnsConstant.PHONE_OPEN_CONFIRM[1]);
        if (contactNumberInfoList.size() > 0) {
            totalPages = PhoneUtils.getTotalPages(contactNumberInfoList.size(), maxItems);
//            lasePages = PhoneUtils.getLasePageItems(contactNumberInfoList.size(), maxItems);
            LogUtils.d(TAG, "totalPages:" + totalPages);
            //卡片最多展示8页，超出部分不展示，计算获取到的最大页码不能超过8
            totalPages = Math.min(totalPages, 8);

        }

        LogUtils.i(TAG, "select_index:" + select_index + " ,index_type:" + index_type + " ,contactNumberInfoList.size:" + contactNumberInfoList.size()
                + " ,totalPages:" + totalPages + " ,maxItems:" + maxItems);
        //第x页
        if (StringUtils.equals(index_type, "absolute")) {
            //倒数第x页
            if (select_index < 0) {
                int index = totalPages - Math.abs(select_index) + 1;
                LogUtils.i(TAG, "index is " + index);
                if (index > totalPages || index <= 0) {
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                } else {
//                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                    if (currentPage == index) {
                        if (totalPages == currentPage) {
                            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.LAST_PAGE[0], TTSAnsConstant.LAST_PAGE[1]);
                        } else {
                            ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.SCROLL_PAGE[0], TTSAnsConstant.SCROLL_PAGE[1]);
                            PhoneUtils.getReplaceTtsBean(ttsBean, "@{media_num}", String.valueOf(index));
                        }
//                        ttsBean.setSelectTTs("已经是第" + index + "页了。");
                    } else
                       DeviceHolder.INS().getDevices().getUiCardInterface().scrollAssignPage(index - 1, PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
                }

            } else if (select_index > 0) {
                //正数第x页
                if (select_index > totalPages) {
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[0], TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG[1]);
                } else {
                    if (currentPage == select_index) {
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.SCROLL_PAGE[0], TTSAnsConstant.SCROLL_PAGE[1]);
                        PhoneUtils.getReplaceTtsBean(ttsBean, "@{media_num}", String.valueOf(select_index));
                    } else
                        DeviceHolder.INS().getDevices().getUiCardInterface().scrollAssignPage(select_index - 1, PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
                }
            }

        } else if (StringUtils.equals(index_type, "relative")) {
            //下滑、上一页、上滑、下一页
            int scrollRe = DeviceHolder.INS().getDevices().getUiCardInterface().scrollPage(select_index, PhoneUtils.getScreenTypeBySoundLocation(soundLocation));
            LogUtils.i(TAG, "scrollRe is " + scrollRe);
            if (scrollRe == 2)
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.LAST_PAGE[0], TTSAnsConstant.LAST_PAGE[1]);
            else if (scrollRe == 1)
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.FIRST_PAGE[0], TTSAnsConstant.FIRST_PAGE[1]);
        }
        return new ClientAgentResponse(retCode, flowContext, ttsBean);
    }


}
