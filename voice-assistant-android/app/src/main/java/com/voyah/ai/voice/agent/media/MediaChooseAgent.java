package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaChooseAgent extends BaseAgentX {

    private static final String TAG = MediaChooseAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#choose";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaChooseAgent------");
        String chooseType = getParamKey(paramsMap, Constant.CHOOSE_TYPE, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        //有指定位置用指定位置，没有指定位置用声源位置
        if (StringUtils.isBlank(position)) {
            //获取声源位置
            position = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        }
        if ("cancel".equals(chooseType)) {
            //TODO tts版本2 无此tts
            UIMgr.INSTANCE.dismissCardOnScreen(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation));

            TTSBean ttsBean = TtsReplyUtils.getTtsBean("1100005");
            return new ClientAgentResponse(Constant.CommonResponseCode.ERROR, flowContext, ttsBean);
        } else if ("confirm".equals(chooseType)) {
            // TODO: 2024/12/11 unused
//            VoiceImpl.getInstance().exDialog();
//            //TODO tts版本2 无此tts
//            TTSBean ttsBean = TtsReplyUtils.getTtsBean("1100005");
//            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, null);
    }

    @Override
    public void destroyAgent() {

    }

}
