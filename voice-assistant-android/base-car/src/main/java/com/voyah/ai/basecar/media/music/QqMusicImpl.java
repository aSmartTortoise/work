package com.voyah.ai.basecar.media.music;

import android.content.Context;
import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaMusicInterface;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.device.media.bean.MediaMusicInfo;
import com.voice.sdk.device.media.bean.ObserverResponse;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.bean.Code;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public enum QqMusicImpl implements MediaMusicInterface {
    INSTANCE;
    private static final String TAG = QqMusicImpl.class.getSimpleName();

    private static final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    @Override
    public void init(Context context) {

    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean switchPage(boolean isOpen, int pageId) {
        LogUtils.i(TAG,"qq switchPage isOpen:"+isOpen+";pageid:"+pageId);
        int currentPageId = pageId;
        if(pageId == PageId.main.getId() && isOpen){
            currentPageId = PageId.QQ_main.getId();
        }
        int code = voyahMusicControlInterface.switchPage(isOpen,currentPageId);
        if (!isOpen) {
            String source = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getMediaPlayingResource();
            boolean needStop = pageId == PageId.main.getId() || (pageId == PageId.QQ_main.getId() && MediaSource.qq_music.getName().equals(source));
            if (needStop) {
                DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().stop(false);
            }
        }
        if (code == Code.SUCESS.code()) {
            LogUtils.d(TAG, isOpen ? "openPage success" : "closePage success");
            if (pageId == PageId.main.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "1100002" : "1100003", "@{app_name}", "音乐");
            } else if (pageId == PageId.QQ_main.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "1100002" : "1100003", "@{app_name}", "QQ音乐");
            } else if (pageId == PageId.QQ_play_page.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004700" : "4004800");
            } else if (pageId == PageId.QQ_login.getId()) {
                return null;
            } else if (pageId == PageId.QQ_list.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004502" : "4004601");
            } else if (pageId == PageId.QQ_lyric.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4005002" : "4005101");
            } else if (pageId == PageId.QQ_history.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4003102" : "4003202");
            } else if (pageId == PageId.QQ_collect.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4003402" : "4003502");
            }
        } else if (code == Code.NOT_LOGIN.code()) {
            LogUtils.d(TAG, "not login");
            if (pageId == PageId.QQ_collect.getId() || pageId == PageId.QQ_history.getId()) {
                switchPage(true, PageId.QQ_login.getId());
            }
            return TTSIDConvertHelper.getInstance().getTTSBean("4000001", 2);
        } else if (code == Code.VOICE_PAGEISEXIST.code()) {
            LogUtils.d(TAG, "voice_page_is_exist");
            if (pageId == PageId.main.getId()) {
                return TtsReplyUtils.getTtsBean("1100001", "@{app_name}", "音乐");
            } else if (pageId == PageId.QQ_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100001", "@{app_name}", "QQ音乐");
            } else if (pageId == PageId.QQ_play_page.getId() || pageId == PageId.wy_play_page.getId() || pageId == PageId.xm_play_page.getId() || pageId == PageId.yt_play_page.getId() || pageId == PageId.bt_play_page.getId() || pageId == PageId.usb_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004700");
            } else if (pageId == PageId.QQ_login.getId()) {
                return null;
            } else if (pageId == PageId.QQ_list.getId()) {
                return TtsReplyUtils.getTtsBean("4004501");
            } else if (pageId == PageId.QQ_lyric.getId() ) {
                return TtsReplyUtils.getTtsBean("4005001");
            } else if (pageId == PageId.QQ_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003103");
            } else if (pageId == PageId.QQ_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4003403");
            }
        } else if (code == Code.VOICE_PAGENOTEXIST.code()) {
            LogUtils.d(TAG, "voice_page_not_exist");
            if (pageId == PageId.main.getId()) {
                return TtsReplyUtils.getTtsBean("1100004", "@{app_name}", "岚图音乐");
            } else if (pageId == PageId.QQ_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100004", "@{app_name}", "QQ音乐");
            } else if (pageId == PageId.QQ_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004801");
            } else if (pageId == PageId.QQ_login.getId()) {
                return null;
            } else if (pageId == PageId.QQ_list.getId() ) {
                return TtsReplyUtils.getTtsBean("4004602");
            } else if (pageId == PageId.QQ_lyric.getId()) {
                return TtsReplyUtils.getTtsBean("4005102");
            } else if (pageId == PageId.QQ_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003201");
            } else if (pageId == PageId.QQ_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4003501");
            }
        } else if(code == Code.RGEAR.code()){
            return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean queryPlayInfo() {
        LogUtils.d(TAG, "qq queryPlayInfo");
        MediaMusicInfo mediaInfo = voyahMusicControlInterface.getMediaInfo(MediaSource.qq_music.getName());
        LogUtils.d(TAG, "mediaInfo command: " + mediaInfo);
        if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getMediaId())) {
            return TtsReplyUtils.getTtsBean("4004401", "@{artist_name}", mediaInfo.getSingerName(), "@{media_name}", mediaInfo.getMediaName(), "@{app_name}", "QQ音乐");
        }
        return TtsReplyUtils.getTtsBean("4004402");
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        LogUtils.i(TAG,"qq switchLyric:"+isOpen);
        return switchPage(isOpen, PageId.QQ_lyric.getId());
    }

    @Override
    public TTSBean collect(boolean isCollect) {
        LogUtils.i(TAG,"qq collect:"+isCollect);
        if(!isLogin()){
            return TtsReplyUtils.getTtsBean("4000001");
        }

        voyahMusicControlInterface.qqCollectState(new ObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                if((isCollect && response) || (!isCollect && !response)){
                    LogUtils.d(TAG, isCollect ? "qq collected" : "qq not collected");
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4004203" : "4004304").getSelectTTs());
                } else {
                   voyahMusicControlInterface.qqCollect(isCollect, new ObserverResponse<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if(response){
                                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4004204" : "4004303").getSelectTTs());
                            } else {
                                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
                            }
                        }
                    });
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        LogUtils.i(TAG,"qq switchPlayList:"+isOpen);
        return switchPage(isOpen,PageId.QQ_list.getId());
    }

    @Override
    public TTSBean switchPlayPage(boolean isOpen) {
        LogUtils.i(TAG,"qq switchPlayPage:"+isOpen);
        return switchPage(isOpen, PageId.QQ_play_page.getId());
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.i(TAG,"qq switchCollectUI:"+isOpen);
        return switchPage(isOpen, PageId.QQ_collect.getId());
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.i(TAG,"qq switchHistoryUI:"+isOpen);
        return switchPage(isOpen, PageId.QQ_history.getId());
    }

    @Override
    public TTSBean playMusic() {
        int ret = voyahMusicControlInterface.play(MediaSource.qq_music.getName());
        if (ret == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("4007401");
        } else if (ret == Code.PLAYING.code()) {
            return TtsReplyUtils.getTtsBean("4003902");
        } else if (ret == Code.NOT_LOGIN.code()) {
            switchPage(true, PageId.QQ_login.getId());
            return TtsReplyUtils.getTtsBean("4000001");
        } else if (ret == Code.NOT_PAY.code()) {
            return TtsReplyUtils.getTtsBean("4010104");
        } else if (ret == Code.NOT_VIP.code()) {
            return TtsReplyUtils.getTtsBean("4010103");
        } else if (ret == Code.NO_SONG_CAN_PALY.code()) {
            return TtsReplyUtils.getTtsBean("4004503");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean playCollect() {
        LogUtils.d(TAG, "qq playCollect");
        voyahMusicControlInterface.playCollectQq(new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if(response != null){
                    playSongsTts(response,false);
                } else {
                   DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.QQ_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003604").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean playHistory() {
        LogUtils.d(TAG, "qq playHistory");
        voyahMusicControlInterface.playHistoryQq(new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if(response != null){
                    playSongsTts(response,false);
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.QQ_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003304").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean playRecommend() {
        LogUtils.i(TAG,"qq playRecommend");
        voyahMusicControlInterface.playRecommendQq(new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if(response != null){
                    playSongsTts(response,true);
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.QQ_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003304").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean playSearch(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        LogUtils.d(TAG, "qq playSearch");
        if(!isLogin()){
            return TtsReplyUtils.getTtsBean("4000001");
        }
        voyahMusicControlInterface.playSearchQq(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode, new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if (response != null) {
                    playSongsTts(response,true);
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050061").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.QQ_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050008").getSelectTTs());
                } else if (code == Code.NETERROR.code() || code == Code.SERVICE_ERROR.code() || code == Code.INTERNAL_ERROR.code() || code == Code.ICP_TIMEOUT.code() || code == Code.PARAM_ERROR.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000000").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean mediaSwitch(boolean isOpen) {
        LogUtils.i(TAG,"qq mediaSwitch:"+isOpen);
        return switchPage(isOpen,PageId.QQ_main.getId());
    }

    @Override
    public boolean isLogin() {
        return voyahMusicControlInterface.isLoginQq();
    }

    private void playSongsTts(VoiceMusicSongInfo songInfo,boolean isJudgeFront){
        if (songInfo.isCanPlay()) {
            if (isJudgeFront && voyahMusicControlInterface.isVoyahMusicFront() && DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getPageState(PageId.QQ_main.getId()) != 1) {
                if (!TextUtils.isEmpty(songInfo.getMediaName())) {
                    TTSBean bean = TtsReplyUtils.getTtsBean("4000006", "@{artist_name}", songInfo.getArtist(), "@{media_name}", songInfo.getMediaName(), "@{app_name}", "qq音乐");
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
                }
            } else {
                if (!TextUtils.isEmpty(songInfo.getMediaName())) {
                    TTSBean bean = TtsReplyUtils.getTtsBean("4000002", "@{artist_name}", songInfo.getArtist(), "@{media_name}", songInfo.getMediaName());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
                }
            }
        } else {
            if (songInfo.isAudition()){
                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000004").getSelectTTs());
            } else {
                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
            }
        }
    }
}
