package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaDanmakuSwitchAgent extends BaseAgentX {

    private static final String TAG = MediaDanmakuSwitchAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_danmaku#switch";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaDanmakuSwitchAgent------");
        String switchType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.SCREEN_NAME, 0);

        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
        TTSBean tts = null;
        if ("open".equals(switchType) || "change".equals(switchType)) {
            tts = DeviceHolder.INS().getDevices().getMedia().switchDanmaku(true);
        } else if ("close".equals(switchType)) {
            tts = DeviceHolder.INS().getDevices().getMedia().switchDanmaku(false);
        }
//        if (tts == null) {
//            tts = TtsReplyUtils.getNotSupportReplay();
//        }
        return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
    }


}
