package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.flowchart.helper.InterventionTreatmentHelper;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaKeywordSelectAgent extends BaseAgentX {

    private static final String TAG = MediaKeywordSelectAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_keyword#select";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaKeywordSelectAgent------");
        String name = getParamKey(paramsMap, Constant.NAME, 0);
        LogUtils.i(TAG, "name: " + name);
        String currentCardType = DeviceHolder.INS().getDevices().getMediaPage().getCurrentCardType(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation));
        if (StringUtils.equals(currentCardType, "domain_type_multimedia_video")) {
            if (MediaPlayAgent.videoInfoList != null && !MediaPlayAgent.videoInfoList.isEmpty() && !StringUtils.isBlank(name)) {
                for (VideoInfo videoInfo : MediaPlayAgent.videoInfoList) {
                    if (videoInfo.videoName != null && videoInfo.videoName.contains(name)) {
                        //在allApp界面时，应用会被遮挡
                        InterventionTreatmentHelper.getInstance().closeAllScreen();
                        DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
                        //不会走跨域 ，也不会走ExitMultiInteractionAgent，需要自己处理卡片
                        UIMgr.INSTANCE.dismissCardOnScreen(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation));
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("1100005"));
                    }
                }
            }
        }
//        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("4025302");
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, null);
    }

}
