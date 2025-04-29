package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaShareAgent extends BaseAgentX {

    private static final String TAG = MediaShareAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_share#switch";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {

        String mediaType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String srcScreen = getParamKey(paramsMap, Constant.SRC_SCREEN, 0);
        String dstScreen = getParamKey(paramsMap, Constant.DST_SCREEN, 0);
        String dstPosition = getParamKey(paramsMap, Constant.DST_POSITION, 0);
        LogUtils.d(TAG, "srcScreen = " + srcScreen + ", dstScreen = " + dstScreen + ", dstPosition = " + dstPosition);

        String position = getParamKey(paramsMap, Constant.MEDIA_POSITION, 0);
        //有指定位置用指定位置，没有指定位置用声源位置
        if (StringUtils.isBlank(position)) {
            //获取声源位置
            position = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        }
        int screenType = DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(position);
        LogUtils.d(TAG, "screenType = " + screenType);

        //1.获取分享源屏幕id (1)如果有分享源，获取分享源 (2)如果没有分享源，获取正在播放的视频，（3）如果没有正在播放的视频，获取音区位置
        int sourceDisplayId;
        if (StringUtils.isBlank(srcScreen)) {
            sourceDisplayId = DeviceHolder.INS().getDevices().getMediaCenter().getPlayingDisplayId();
        } else {
            sourceDisplayId = DeviceHolder.INS().getDevices().getMediaCenter().getDisplayId(srcScreen);
        }
        //2.获取目的源屏幕id (1)目的源为单屏 (2)目的源为多屏
        boolean multiScreenForDst = DeviceHolder.INS().getDevices().getMediaCenter().isMultiScreenForDst(dstScreen,dstPosition);
        if (multiScreenForDst) {
            if (("open").equals(mediaType)) {
                DeviceHolder.INS().getDevices().getMediaCenter().shareScreenForAll(sourceDisplayId);
            } else {
                DeviceHolder.INS().getDevices().getMediaCenter().closeShareScreenForAll();
            }
        } else {
            //目的源为空，取声源位置
            int targetDisplayId = 0;
            boolean isSoundLocation = false;
            if(StringUtils.isBlank(dstScreen)) {
                if (StringUtils.isNotBlank(dstPosition)) {
                    targetDisplayId = DeviceHolder.INS().getDevices().getMediaCenter().getDisplayId(dstPosition);
                } else {
                    targetDisplayId = DeviceHolder.INS().getDevices().getMediaCenter().getDisplayId(screenType);
                    isSoundLocation = true;
                }
            }else{
                targetDisplayId = DeviceHolder.INS().getDevices().getMediaCenter().getDisplayId(dstScreen);
            }
            if (("open").equals(mediaType)) {
                DeviceHolder.INS().getDevices().getMediaCenter().shareScreenSingle(sourceDisplayId,targetDisplayId,isSoundLocation);
            } else {
                DeviceHolder.INS().getDevices().getMediaCenter().closeShareScreenSingle(targetDisplayId);
            }
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean(""));
    }
}
