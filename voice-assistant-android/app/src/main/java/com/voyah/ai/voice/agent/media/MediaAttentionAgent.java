
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
public class MediaAttentionAgent extends BaseAgentX {

    private static final String TAG = MediaAttentionAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#attention";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaAttentionAgent------");
        String operation = getParamKey(paramsMap, Constant.OPERATION, 0);
        String screenName = getParamKey(paramsMap, Constant.SCREEN_NAME, 0);
        boolean isAdd = "add".equals(operation);

        String position = getParamKey(paramsMap, Constant.MEDIA_POSITION, 0);
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);

        if (ttsBean == null) {
            ttsBean = DeviceHolder.INS().getDevices().getMedia().switchAttention(isAdd, null);
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }


}
