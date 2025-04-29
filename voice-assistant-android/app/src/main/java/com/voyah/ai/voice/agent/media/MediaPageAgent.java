package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaPageInterface;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;
import com.voice.sdk.model.UIPageInfo;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaPageAgent extends BaseAgentX {

    private static final String TAG = MediaPageAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media#page";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaPageAgent------");
        try {
            int screenType = DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation);
            String currentCardType = DeviceHolder.INS().getDevices().getMediaPage().getCurrentCardType(screenType);
            List<VideoInfo> videoInfoList = (List<VideoInfo>) flowContext.get("ctx-onlineVideosList");
            List<VoiceMusicSongInfo> musicInfoList = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().getMusicList();
            boolean isCardMusic = MediaConstant.DOMAIN_TYPE_MULTIMEDIA_MUSIC.equals(currentCardType) && musicInfoList != null && !musicInfoList.isEmpty();
            if (videoInfoList != null && !videoInfoList.isEmpty() || isCardMusic) {
                int index = Integer.parseInt(getParamKey(paramsMap, Constant.SELECT_INDEX, 0));
                String type = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
                String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
                //有指定位置用指定位置，没有指定位置用声源位置
                if (StringUtils.isBlank(position)) {
                    //获取声源位置
                    position = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
                }

                MediaPageInterface mediaPageInterface = DeviceHolder.INS().getDevices().getMediaPage();
                UIPageInfo uiPageInfo = mediaPageInterface.getCurrentPage(
                        DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation));
                int totalPage = 0;
                if(isCardMusic){
                    totalPage = musicInfoList.size() > 24
                            ? 3 : (musicInfoList.size() + uiPageInfo.getMaxItemCount() - 1)
                            / uiPageInfo.getMaxItemCount();
                } else {
                    totalPage = (videoInfoList.size() + uiPageInfo.getMaxItemCount() - 1)
                            / uiPageInfo.getMaxItemCount();
                }
                int currentPage = uiPageInfo.getPosition();
                LogUtils.i(TAG, "totalPage: " + totalPage + " currentPage: " + currentPage);
                LogUtils.d(TAG, "index: " + index + " type: " + type);
                if ("absolute".equals(type)) {
                    if (totalPage == 1) {
                        return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4021303").getSelectTTs());
                    }
                    if (index == -1 && currentPage == (totalPage + index)) {//最后一页
                        return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4021201").getSelectTTs());
                    }
                    if (index == 0 || Math.abs(index) > totalPage) {
                        String ttsStr = TtsReplyUtils.getTtsBean("4021301", "@{media_num_min}", "1", "@{media_num_max}", String.valueOf(totalPage)).getSelectTTs();
                        return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, ttsStr);
                    } else {
                        if (index > 0) {
                            if (currentPage == index - 1) {
                                String ttsStr = TtsReplyUtils.getTtsBean("4021302", "@{media_num}", String.valueOf(index)).getSelectTTs();
                                return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, ttsStr);
                            } else {
                                mediaPageInterface.setCurrentPage(index - 1, screenType);
                                return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("1100005").getSelectTTs());
                            }
                        } else {
                            if (currentPage == totalPage + index) {
                                String ttsStr = TtsReplyUtils.getTtsBean("4021402", "@{media_num}", String.valueOf(Math.abs(index))).getSelectTTs();
                                return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, ttsStr);
                            } else {
                                mediaPageInterface.setCurrentPage(totalPage + index, screenType);
                                return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("1100005").getSelectTTs());
                            }
                        }

                    }
                } else if ("relative".equals(type)) {
                    if (totalPage == 1) {
                        return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4021102").getSelectTTs());
                    }
                    //上一页 index为-1  下一页为1
                    int temp = index + currentPage + 1;
                    LogUtils.d(TAG, "temp: " + temp);

                    if (index > 0) {//下一页
                        if (temp > totalPage) {
                            return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4021201").getSelectTTs());
                        } else {
                            mediaPageInterface.setCurrentPage(temp - 1, screenType);
                            return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("1100005").getSelectTTs());
                        }
                    } else {//上一页
                        if (temp < 1) {
                            return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4021101").getSelectTTs());
                        } else {
                            mediaPageInterface.setCurrentPage(temp - 1, screenType);
                            return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("1100005").getSelectTTs());
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            LogUtils.i(TAG, "error: " + e.getMessage());
        }
        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getNotSupportReplay());
    }


}
