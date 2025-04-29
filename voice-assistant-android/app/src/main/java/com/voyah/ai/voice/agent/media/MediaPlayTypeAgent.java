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
public class MediaPlayTypeAgent extends BaseAgentX {

    private static final String TAG = MediaPlayTypeAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#play_type";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaPlayTypeAgent------");
        String playType = getParamKey(paramsMap, Constant.PLAY_TYPE, 0);
        boolean isVideo = playType.equals("video_play");
        TTSBean tts = DeviceHolder.INS().getDevices().getMedia().switchPlayer(isVideo);
//        if (tts ==null) {
//            tts = TtsReplyUtils.getNotSupportReplay();
//        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, tts);
    }


}
