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

@ClassAgent
public class MediaUiSwitchAgent extends BaseAgentX {
    private static final String TAG = MediaUiSwitchAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_ui#switch";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaUiSwitchAgent------");
        String switchType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String mediaType = getParamKey(paramsMap, Constant.MEDIA_TYPE, 0);
        String appName = getParamKey(paramsMap, Constant.APP_NAME, 0);
        String uiName = getParamKey(paramsMap, Constant.UI_NAME, 0);
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

        boolean isOpen = Constant.OPEN.equals(switchType) || Constant.CHANGE.equals(switchType);
        if (isOpen) {
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(!TextUtils.isEmpty(queryScreen) ? queryScreen : getScreen(soundLocation)), "");
        }
        ttsBean = DeviceHolder.INS().getDevices().getMedia().switchUi(isOpen, uiName, appName, mediaType);
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
    }
}
