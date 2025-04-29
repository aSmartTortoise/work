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
public class MediaSeekAgent extends BaseAgentX {

    private static final String TAG = MediaSeekAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#seek";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaSeekAgent------");
        String seekType = getParamKey(paramsMap, Constant.SEEK_TYPE, 0);
        String duration = getParamKey(paramsMap, Constant.DURATION, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        LogUtils.i(TAG, "seekType: " + seekType + " duration: " + duration);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }

        if (TextUtils.isEmpty(duration)) {
            ttsBean = DeviceHolder.INS().getDevices().getMedia().seek(seekType, 0);
        } else {
            try {
                ttsBean = DeviceHolder.INS().getDevices().getMedia().seek(seekType, Long.parseLong(duration));
            } catch (NumberFormatException e) {
                LogUtils.i(TAG, "NumberFormatException: " + e.getMessage());
            }
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }


}
