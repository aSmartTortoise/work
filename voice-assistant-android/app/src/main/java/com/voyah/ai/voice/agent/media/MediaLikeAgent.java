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
public class MediaLikeAgent extends BaseAgentX {

    private static final String TAG = MediaLikeAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#like";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaLikeAgent------");
        String operation = getParamKey(paramsMap, Constant.OPERATION, 0);
        LogUtils.i(TAG, "operation: " + operation);
        boolean isLike = "add".equals(operation);

        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.SCREEN_NAME, 0);

        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean == null) {
            ttsBean = DeviceHolder.INS().getDevices().getMedia().switchLike(isLike, null);
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }


}
