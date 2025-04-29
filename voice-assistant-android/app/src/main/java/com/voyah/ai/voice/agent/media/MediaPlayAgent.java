package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.listener.UICardListener;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaPlayAgent extends BaseAgentX {

    private static final String TAG = MediaPlayAgent.class.getSimpleName();

    public static List<VideoInfo> videoInfoList;
    public static String mCurrentSoundLocation;

    private static final UICardListener uiCardListener = new UICardListener() {
        @Override
        public void onCardItemClick(int position, int itemType,int screenType) {
            LogUtils.d(TAG, "onCardItemClick position: " + position + " itemType: " + itemType + ", screenType = " + screenType);

            if (!DeviceHolder.INS().getDevices().getMediaPage().isViewType(itemType)) {
                return;
            }

            int currentPage = DeviceHolder.INS().getDevices().getMediaPage().getCurrentPage(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(mCurrentSoundLocation)).getPosition();
            int maxCount = DeviceHolder.INS().getDevices().getMediaPage().getCurrentPage(DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(mCurrentSoundLocation)).getMaxItemCount();
            int pos = currentPage * maxCount + position;
            LogUtils.d(TAG, "currentPage: " + currentPage + " maxCount: " + maxCount + " pos: " + pos);
            VideoInfo videoInfo = videoInfoList.get(pos);
            DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
            UIMgr.INSTANCE.forceExitAll("mediaPlay");
            VoiceImpl.getInstance().exDialog();
            videoInfoList = null;
        }

        @Override
        public void uiCardClose(String sessionId) {
            LogUtils.d(TAG, "uiCardClose");
            UIMgr.INSTANCE.removeCardStateListener(uiCardListener);
            videoInfoList = null;
        }

    };

    @Override
    public String AgentName() {
        return "media#play";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaPlayAgent------");
        String mediaType = getParamKey(paramsMap, "media_type", 0);
        String mediaTypeDetail = getParamKey(paramsMap, "media_type_detail", 0);
        String appName = getParamKey(paramsMap, "app_name", 0);
        String mediaSource = getParamKey(paramsMap, "media_source", 0);
        String mediaName = getParamKey(paramsMap, "media_name", 0);
        String mediaArtist = getParamKey(paramsMap, "media_artist", 0);
        String mediaAlbum = getParamKey(paramsMap, "media_album", 0);
        String mediaDate = getParamKey(paramsMap, "media_date", 0);
        String mediaMovie = getParamKey(paramsMap, "media_movie", 0);
        String mediaStyle = getParamKey(paramsMap, "media_style", 0);
        String mediaLan = getParamKey(paramsMap, "media_lan", 0);
        String mediaVersion = getParamKey(paramsMap, "media_version", 0);
        String mediaOffset = getParamKey(paramsMap, "media_offset", 0);
        String mediaUi = getParamKey(paramsMap, "ui_name", 0);
        String mediaRank = getParamKey(paramsMap, "media_rank", 0);
        String playMode = getParamKey(paramsMap, "play_mode", 0);
        String position = getParamKey(paramsMap,Constant.MEDIA_POSITION,0);
        String screenName = getParamKey(paramsMap, Constant.MEDIA_SCREEN, 0);
        String sessionid =  getFlowContextKey(Constant.PARAMS_SC_SESSION_ID, flowContext);
        String reaid = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        //声源位置
        String soundLocation = getUiSoundLocation(flowContext);

        TTSBean ttsBean = DeviceHolder.INS().getDevices().getMedia().initUserHandle(position,screenName,soundLocation);
        if (ttsBean != null) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
        //我想用“XX”看视频
        if ("video".equals(mediaType) && ("咪咕视频".equals(appName) || "哔哩哔哩".equals(appName) || "车鱼视听".equals(appName) || "抖音短视频".equals(appName)) && StringUtils.isBlank(mediaUi)) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4030700", "@{app_name}", appName));
        }
        mCurrentSoundLocation = getUiSoundLocation(flowContext);
        int screenType = DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(mCurrentSoundLocation);
        // 先判断是否是二次交互选择场景
        String currentCardType = DeviceHolder.INS().getDevices().getMediaPage().getCurrentCardType(screenType);
        if ("domain_type_multimedia_video".equals(currentCardType)) {
            if (videoInfoList != null && !videoInfoList.isEmpty() && !StringUtils.isBlank(mediaName)) {
                for (VideoInfo videoInfo : videoInfoList) {
                    if (videoInfo.videoName != null && videoInfo.videoName.contains(mediaName)) {
                        DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4007401"));
                    }
                }
            }
        }

        // 是否是视频卡片
        //我要看视频走到这里了，这里需要做处理 添加mediaName判断
        Object data = flowContext.get(FlowContextKey.FC_ONLINE_VIDEOS_SEARCH_SUCCEED);
        LogUtils.d(TAG, "online search data: " + data);
        if (data != null) {
            if (DeviceHolder.INS().getDevices().getMediaCenter().isSafeLimitation() && screenType == 0) {
                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT));
            }
            boolean videosSearchSucceed = (boolean) data;
            if (videosSearchSucceed) {
                Object list = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_ONLINE_VIDEOS_LIST);
                if (list != null) {
                    videoInfoList = (List<VideoInfo>) list;
                    LogUtils.d(TAG, "videoInfoList: " + videoInfoList);
                    if (videoInfoList != null && !videoInfoList.isEmpty()) {
                        for (VideoInfo videoInfo : videoInfoList) {
                            LogUtils.i(TAG, "videoName: " + videoInfo.videoName + " total: " + videoInfo.total);
                        }
                        // 只有一个结果直接点击
                        if (videoInfoList.size() == 1) {
                            VideoInfo videoInfo = videoInfoList.get(0);
                            if (StringUtils.isNotBlank(mediaOffset)) {
                                if (videoInfo.source == 1) {
                                    Object object = flowContext.containsKey(FlowContextKey.FC_ONLINE_MEDIA_SET_NEED_USE_ONE);
                                    DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
                                    if ((boolean) object) {
                                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4050066"));
                                    } else {
                                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4007401"));
                                    }
                                }
                            } else {
                                DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
                                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4007401"));
                            }
                        }
                        Map<String, Object> map = DeviceHolder.INS().getDevices().getMediaCenter().getVideoMap(videoInfoList, sessionid, getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext));
                        UIMgr.INSTANCE.showCard(UiConstant.CardType.MEDIA_CARD, map, sessionid, mAgentIdentifier,
                                BaseAgentX.getSoundSourceLocation(flowContext));
                        UIMgr.INSTANCE.addCardStateListener(uiCardListener);
//
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4023002"), "", TAG);
                    } else {
                        LogUtils.d(TAG, "video data is null 1");
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4020803"));
                    }
                } else {
                    LogUtils.d(TAG, "video data is null 2");
                    return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4020803"));
                }
            } else {
                Object searchError = flowContext.get(FlowContextKey.FC_ONLINE_VIDEOS_SEARCH_NETWORK_ERROR);
                if (searchError != null) {
                    boolean netError = (boolean) searchError;
                    if (netError) {//云端因为网络问题搜索失败
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4000000"));
                    } else {
                        return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4020803"));
                    }
                } else {
                    return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4020803"));
                }
            }
        }

        // 播放第几集
        if (!StringUtils.isBlank(mediaOffset)) {
            if (NumberUtils.areAllStringsEmpty(mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist,
                    mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaUi, mediaRank, playMode)) {
                ttsBean = DeviceHolder.INS().getDevices().getMediaCenter().playEpisode(Integer.parseInt(mediaOffset));
                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
            }
        }

        // 判断是否是视频
        if ("video".equals(mediaType)) {
            ttsBean = DeviceHolder.INS().getDevices().getMediaCenter().playVideo(mediaSource, appName, mediaUi);
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }

        // default
        DeviceHolder.INS().getDevices().getMedia().setIdentifier(mAgentIdentifier);
        DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().setCardInfo(sessionid,reaid,mAgentIdentifier,BaseAgentX.getSoundSourceLocation(flowContext));
        TTSBean tts = DeviceHolder.INS().getDevices().getMedia().play(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
        return new ClientAgentResponse(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().isShowMusicCard() ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.SUCCESS, flowContext, tts);
    }
}
