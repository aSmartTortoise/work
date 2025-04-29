package com.voyah.ai.basecar.media.music;

import android.content.Context;
import android.os.UserHandle;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaMusicInterface;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.bean.Code;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voyah.ai.basecar.media.bean.UserHandleInfo;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public enum BtMusicImpl implements MediaMusicInterface {
    INSTANCE;
    private static final String TAG = YtMusicImpl.class.getSimpleName();

    public static int displayId = UserHandleInfo.central_screen.getDisplayId();
    public static final UserHandle userHandle = UserHandleInfo.central_screen.getUserHandle();

    private static final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    @Override
    public void init(Context context) {
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean switchPage(boolean isOpen, int pageId) {
        LogUtils.i(TAG,"bt switchPage isOpen:"+isOpen+";pageid:"+pageId);
        voyahMusicControlInterface.initUserHandle(userHandle,displayId);
        int code = voyahMusicControlInterface.switchPage(isOpen,pageId);
        if (!isOpen) {
            String source = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getMediaPlayingResource();
            boolean needStop = pageId == PageId.main.getId() || (pageId == 5 && MediaSource.bt_music.getName().equals(source));
            if (needStop) {
                DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().stop(false);
            }
        }
        if (code == Code.SUCESS.code()) {
            LogUtils.d(TAG, isOpen ? "openPage success" : "closePage success");
            if (pageId == PageId.bt_main.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4018803" : "4018900");
            } else if (pageId == PageId.bt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004700" : "4004800");
            } else if (pageId == PageId.bt_lyric.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4005002" : "4005101");
            }
        } else if (code == Code.VOICE_PAGEISEXIST.code()) {
            LogUtils.d(TAG, "voice_page_is_exist");
            if (pageId == PageId.bt_main.getId()) {
                return TtsReplyUtils.getTtsBean("4018801");
            } else if (pageId == PageId.bt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004700");
            } else if (pageId == PageId.bt_lyric.getId()) {
                return TtsReplyUtils.getTtsBean("4005001");
            }
        } else if (code == Code.VOICE_PAGENOTEXIST.code()) {
            LogUtils.d(TAG, "voice_page_not_exist");
            if (pageId == PageId.bt_main.getId()) {
                return TtsReplyUtils.getTtsBean("4018901");
            } else if (pageId == PageId.bt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004801");
            } else if (pageId == PageId.bt_lyric.getId()) {
                return TtsReplyUtils.getTtsBean("4005102");
            }
        } else if(code == Code.RGEAR.code()){
            return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean queryPlayInfo() {
        return null;
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        LogUtils.i(TAG,"bt switchLyric:"+isOpen);
        return switchPage(isOpen, PageId.bt_lyric.getId());
    }

    @Override
    public TTSBean collect(boolean isCollect) {
        return null;
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchPlayPage(boolean isOpen) {
        LogUtils.i(TAG,"bt switchPlayPage:"+isOpen);
        return switchPage(isOpen, PageId.bt_play_page.getId());
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean playMusic() {
        voyahMusicControlInterface.initUserHandle(userHandle,displayId);
        if(voyahMusicControlInterface.isPlaying() && MediaSource.bt_music.getName().equals(voyahMusicControlInterface.getSource())){
            return TtsReplyUtils.getTtsBean("4003902");
        }
        boolean isConnect = voyahMusicControlInterface.getBtStatus();
        LogUtils.d(TAG, "bt playmusic isConnect: " + isConnect);
        switchPage(true, PageId.bt_main.getId());
        if (isConnect) {
            int code = voyahMusicControlInterface.playBtMusic();
            if (code == Code.SUCESS.code()) {
                return TtsReplyUtils.getTtsBean("4007401");
            } else {
                return TtsReplyUtils.getTtsBean("4050000");
            }
        } else {
            return TtsReplyUtils.getTtsBean("4018800");
        }
    }

    @Override
    public TTSBean playCollect() {
        return null;
    }

    @Override
    public TTSBean playHistory() {
        return null;
    }

    @Override
    public TTSBean playRecommend() {
        return null;
    }

    @Override
    public TTSBean playSearch(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }

    @Override
    public TTSBean mediaSwitch(boolean isOpen) {
        voyahMusicControlInterface.initUserHandle(userHandle,displayId);
        boolean isConnect = voyahMusicControlInterface.getBtStatus();
        LogUtils.d(TAG, "bt mediaSwitch isConnect: " + isConnect);
        if(isOpen){
            if(isConnect){
                return switchPage(true, PageId.bt_main.getId());
            } else {
                switchPage(true, PageId.bt_main.getId());
                return TtsReplyUtils.getTtsBean("4018800");
            }
        } else {
            return switchPage(false, PageId.bt_main.getId());
        }
    }

    @Override
    public boolean isLogin() {
        return false;
    }
}
