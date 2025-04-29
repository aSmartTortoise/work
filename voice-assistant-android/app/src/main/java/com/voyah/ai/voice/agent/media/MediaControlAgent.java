package com.voyah.ai.voice.agent.media;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaControlAgent extends BaseAgentX {

    private static final String TAG = MediaControlAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#control";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaControlAgent------");
        String type = getParamKey(paramsMap, Constant.MEDIA_CONTROL_TYPE, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);

        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }

        TTSBean tts;
        if (type.contains("pre")) {
            tts = DeviceHolder.INS().getDevices().getMedia().pre();
        } else if (type.equals("next")) {
            tts = DeviceHolder.INS().getDevices().getMedia().next();
        } else if (type.contains("stop") || type.contains("pause")) {
            tts = DeviceHolder.INS().getDevices().getMedia().stop(false);
        } else if (type.equals("play") || type.contains("resume")) {
            tts = DeviceHolder.INS().getDevices().getMedia().play();
        } else if (type.equals("replay")) {
            tts = DeviceHolder.INS().getDevices().getMedia().replay();
        } else if (type.contains("exit")) {
            tts = DeviceHolder.INS().getDevices().getMedia().stop(true);
        } else {
            tts = TtsReplyUtils.getTtsBean("4003100");
        }
        return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
    }
}