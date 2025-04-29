package com.voyah.ai.voice.agent.media;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ClassAgent
public class MediaIndexSelectAgent extends BaseAgentX {

    private static final String TAG = MediaIndexSelectAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "media_index#select";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--MediaIndexSelectAgent------");
        String selectIndex = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        int index = Integer.parseInt(selectIndex);

        int screenType = DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(MediaPlayAgent.mCurrentSoundLocation);
        int num = DeviceHolder.INS().getDevices().getMediaPage().getCurrentPage(screenType).getItemCount();
        int maxNum = DeviceHolder.INS().getDevices().getMediaPage().getCurrentPage(screenType).getMaxItemCount();
        int currentPage = DeviceHolder.INS().getDevices().getMediaPage().getCurrentPage(screenType).getPosition();
        LogUtils.i(TAG, "index = " + index + ", pageNum = " + num + ", currentPage = " + currentPage);
        List<VoiceMusicSongInfo> musicInfoList = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().getMusicList();
        String currentCardType = DeviceHolder.INS().getDevices().getMediaPage().getCurrentCardType(screenType);
        boolean isCardMusic = MediaConstant.DOMAIN_TYPE_MULTIMEDIA_MUSIC.equals(currentCardType) && musicInfoList != null && !musicInfoList.isEmpty();

        if ("relative".equals(indexType)) {
            return new ClientAgentResponse(Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, TtsReplyUtils.getTtsBean("4019705").getSelectTTs());
        }

        if (index == 0 || Math.abs(index) > num) {
            String ttsStr;
            if (num == 1) {
                ttsStr = TtsReplyUtils.getTtsBean("4021602").getSelectTTs();
            } else {
                ttsStr = TtsReplyUtils.getTtsBean("4021601", "@{media_num_min}", "1", "@{media_num_max}", String.valueOf(num)).getSelectTTs();
            }
            return new ClientAgentResponse(isCardMusic ? Constant.MediaAgentResponseCode.MULTIPLE_MUSIC : Constant.MediaAgentResponseCode.MULTIPLE_VIDEO, flowContext, ttsStr);
        } else {
            List<VideoInfo> videoInfoList = (List<VideoInfo>) flowContext.get("ctx-onlineVideosList");
            if(isCardMusic){
                List<VoiceMusicSongInfo> list = new ArrayList<>();
                list.add(musicInfoList.get(currentPage * 8 + index-1));
                DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().playSongsByMediaId(list,list.size(),0,true,list.get(0).getMediaSource());
                UIMgr.INSTANCE.forceExitAct("mediaPlay", screenType);
                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4007401").getSelectTTs());
            } else if (videoInfoList != null && !videoInfoList.isEmpty()) {
                videoInfoList = getPageData(videoInfoList, maxNum, currentPage + 1);
                if (index < 0) {
                    Collections.reverse(videoInfoList);
                }
                for (VideoInfo videoInfo : videoInfoList) {
                    LogUtils.i(TAG, videoInfo.videoName);
                }
                VideoInfo videoInfo = videoInfoList.get(Math.abs(index) - 1);
                DeviceHolder.INS().getDevices().getMediaCenter().scheme(videoInfo);
                //TODO forceExitAct 当前使用声源位置，需要业务方判断要退哪块屏
                UIMgr.INSTANCE.forceExitAct("mediaPlay", screenType);
//                VoiceImpl.getInstance().exDialog();
                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getTtsBean("4007401").getSelectTTs());
            } else {
                UIMgr.INSTANCE.forceExitAct("mediaPlay", screenType);
//                VoiceImpl.getInstance().exDialog();
                return new ClientAgentResponse(Constant.MediaAgentResponseCode.SUCCESS, flowContext, TtsReplyUtils.getNotSupportReplay());
            }
        }
    }



    /**
     * 将给定的列表分页，并返回第k页的数据。
     *
     * @param data       包含m个数据的列表
     * @param pageSize   每页的大小i
     * @param pageNumber 要返回的页码k（从1开始计数）
     * @return 第k页的数据列表，如果k超出范围则返回null或空列表
     */
    public static <T> List<T> getPageData(List<T> data, int pageSize, int pageNumber) {
        if (data == null || pageSize <= 0 || pageNumber <= 0) {
            return null;
        }
        int totalSize = data.size();
        int totalPages = (totalSize + pageSize - 1) / pageSize; // 向上取整计算总页数

        if (pageNumber > totalPages) {
            return null;
        }
        int startIndex = (pageNumber - 1) * pageSize; // 计算当前页的第一个元素的索引
        int endIndex = Math.min(startIndex + pageSize, totalSize); // 计算当前页的最后一个元素的索引（不超过列表大小）
        return new ArrayList<>(data.subList(startIndex, endIndex));
    }
}
