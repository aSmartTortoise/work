package com.voyah.ai.voice.agent.media;

import android.text.TextUtils;

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
public class MediaSpeedAdjustAgent extends BaseAgentX {

    private static final String TAG = MediaSpeedAdjustAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_speed#adjust";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaSpeedAdjustAgent------");
        String adjustType = getParamKey(paramsMap, Constant.ADJUST_TYPE, 0);
        String numberRate = getParamKey(paramsMap, Constant.NUMBER_RATE, 0);
        String level = getParamKey(paramsMap, Constant.LEVEL, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }

        ttsBean = DeviceHolder.INS().getDevices().getMedia().speed(adjustType, numberRate, level);
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }

}
