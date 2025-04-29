package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaSingAgent extends BaseAgentX {
    private static final String TAG = MediaSingAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#sing";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaSingAgent------");
        String singType = getParamKey(paramsMap, Constant.SING_TYPE, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        LogUtils.i(TAG, "--MediaSingAgent--" + "singType: " + singType);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
        ttsBean = DeviceHolder.INS().getDevices().getMedia().switchOriginalSinging(singType.equals("original"));
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
//        if (ret) {
//            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext);
//        } else {
//            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getNotSupportReplay());
//        }
    }

}
