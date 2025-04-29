package com.voyah.ai.voice.agent.media;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 媒体-开关
 * @data:2024/2/19
 **/
@ClassAgent
public class MediaSwitchAgent extends BaseAgentX {
    private static final String TAG = MediaSwitchAgent.class.getSimpleName();


    @Override
    public String AgentName() {
        return "media#switch";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaSwitchAgent------");
        String type = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String mediaType = getParamKey(paramsMap, Constant.MEDIA_TYPE, 0);
        String source = getParamKey(paramsMap, Constant.MEDIA_SOURCE, 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);
        //指定位置屏幕
        String queryScreen = !TextUtils.isEmpty(position) ? getScreen(position) : screenName;

        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }

        boolean isOpen = Constant.OPEN.equals(type) || Constant.CHANGE.equals(type);

        if (isOpen) {
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(!TextUtils.isEmpty(queryScreen) ? queryScreen : getScreen(soundLocation)), "");
        }
        TTSBean tts = DeviceHolder.INS().getDevices().getMedia().open(isOpen, mediaType, source, null,soundLocation,queryScreen);

        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, tts);
    }

}
