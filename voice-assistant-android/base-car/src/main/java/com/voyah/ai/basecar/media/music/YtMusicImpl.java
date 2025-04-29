package com.voyah.ai.basecar.media.music;

import android.content.Context;
import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaMusicInterface;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.device.media.bean.MediaMusicInfo;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.bean.Code;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voice.sdk.device.media.bean.ObserverResponse;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public enum YtMusicImpl implements MediaMusicInterface {
    INSTANCE;
    private static final String TAG = YtMusicImpl.class.getSimpleName();

    private static final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    @Override
    public void init(Context context) {
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean switchPage(boolean isOpen, int pageId) {
        LogUtils.i(TAG,"yt switchPage isOpen:"+isOpen+";pageid:"+pageId);
        int code = voyahMusicControlInterface.switchPage(isOpen,pageId);
        if (!isOpen) {
            String source = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getMediaPlayingResource();
            boolean needStop = pageId == PageId.main.getId() || (pageId == PageId.yt_main.getId() && MediaSource.yt_music.getName().equals(source));
            if (needStop) {
                DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().stop(false);
            }
        }
        if (code == Code.SUCESS.code()) {
            LogUtils.d(TAG, isOpen ? "openPage success" : "closePage success");
            if (pageId == PageId.yt_main.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "1100002" : "1100003", "@{app_name}", "云听");
            } else if (pageId == PageId.yt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004700" : "4004800");
            } else if (pageId == PageId.yt_login.getId()) {
                return null;
            } else if (pageId == PageId.yt_list.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004502" : "4004601");
            } else if (pageId == PageId.yt_history.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4003102" : "4003202");
            } else if (pageId == PageId.yt_collect.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4011302" : "4011402");
            }
        } else if (code == Code.NOT_LOGIN.code()) {
            LogUtils.d(TAG, "not login");
            if (pageId == PageId.yt_collect.getId() || pageId == PageId.yt_history.getId()) {
                switchPage(true, PageId.yt_login.getId());
            }
            return TTSIDConvertHelper.getInstance().getTTSBean("4000001", 2);
        } else if (code == Code.VOICE_PAGEISEXIST.code()) {
            LogUtils.d(TAG, "voice_page_is_exist");
            if (pageId == PageId.yt_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100001", "@{app_name}", "云听");
            } else if (pageId == PageId.yt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004700");
            } else if (pageId == PageId.yt_login.getId()) {
                return null;
            } else if (pageId == PageId.yt_list.getId()) {
                return TtsReplyUtils.getTtsBean("4004501");
            } else if (pageId == PageId.yt_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003103");
            } else if (pageId == PageId.yt_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4011303");
            }
        } else if (code == Code.VOICE_PAGENOTEXIST.code()) {
            LogUtils.d(TAG, "voice_page_not_exist");
            if (pageId == PageId.yt_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100004", "@{app_name}", "云听");
            } else if (pageId == PageId.yt_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004801");
            } else if (pageId == PageId.yt_login.getId()) {
                return null;
            } else if (pageId == PageId.yt_list.getId() ) {
                // TODO: 2024/12/12 此tts id未更新到版本2
                return TtsReplyUtils.getTtsBean("4004602");
            } else if ( pageId == PageId.yt_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003201");
            } else if (pageId == PageId.yt_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4011401");
            }
        } else if(code == Code.RGEAR.code()){
            return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean queryPlayInfo() {
        LogUtils.d(TAG, "yt queryPlayInfo");
        MediaMusicInfo mediaInfo = voyahMusicControlInterface.getMediaInfo(MediaSource.yt_music.getName());
        LogUtils.d(TAG, "mediaInfo command: " + mediaInfo);
        if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getMediaId())) {
            return TtsReplyUtils.getTtsBean("4013001", "@{media_name}", mediaInfo.getMediaName(), "@{app_name}", "云听");
        }
        return TtsReplyUtils.getTtsBean("4004402");
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        LogUtils.i(TAG,"yt switchLyric:"+isOpen);
        return null;
    }

    @Override
    public TTSBean collect(boolean isCollect) {
        LogUtils.i(TAG,"yt collect:"+isCollect);
        if(!isLogin()){
            return TtsReplyUtils.getTtsBean("4000001");
        }

        voyahMusicControlInterface.ytCollectState(new ObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "ytCollectState onSuccess: " + response);
                if((isCollect && response) || (!isCollect && !response)){
                    LogUtils.d(TAG, isCollect ? "yt collected" : "yt not collected");
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4012603" : "4012704").getSelectTTs());
                } else {
                    voyahMusicControlInterface.ytCollect(isCollect, new ObserverResponse<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if(response){
                                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4012604" : "4012703").getSelectTTs());
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
        LogUtils.i(TAG,"yt switchPlayList:"+isOpen);
        return switchPage(isOpen,PageId.yt_list.getId());
    }

    @Override
    public TTSBean switchPlayPage(boolean isOpen) {
        LogUtils.i(TAG,"yt switchPlayPage:"+isOpen);
        return switchPage(isOpen, PageId.yt_play_page.getId());
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.i(TAG,"yt switchCollectUI:"+isOpen);
        return switchPage(isOpen, PageId.yt_collect.getId());
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.i(TAG,"yt switchHistoryUI:"+isOpen);
        return switchPage(isOpen, PageId.yt_history.getId());
    }

    @Override
    public TTSBean playMusic() {
        int ret = voyahMusicControlInterface.play(MediaSource.yt_music.getName());
        if (ret == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("4007401");
        } else if (ret == Code.PLAYING.code()) {
            return TtsReplyUtils.getTtsBean("4003902");
        } else if (ret == Code.NOT_LOGIN.code()) {
            switchPage(true, PageId.yt_login.getId());
            return TtsReplyUtils.getTtsBean("4000001");
        } else if (ret == Code.NOT_PAY.code()) {
            return TtsReplyUtils.getTtsBean("4010104");
        } else if (ret == Code.NOT_VIP.code()) {
            return TtsReplyUtils.getTtsBean("4010103");
        } else if (ret == Code.NO_SONG_CAN_PALY.code() || ret == Code.NO_DATA.code()) {
            return TtsReplyUtils.getTtsBean("4004503");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean playCollect() {
        LogUtils.d(TAG, "yt playCollect");
        voyahMusicControlInterface.playCollectYt(new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if(response != null){
                    playSongsTts(response);
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.yt_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4011505").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean playHistory() {
        LogUtils.d(TAG, "yt playHistory");
        voyahMusicControlInterface.playHistoryYt(new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if(response != null){
                    playSongsTts(response);
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.yt_login.getId());
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
        LogUtils.i(TAG,"yt playRecommend");
        return null;
    }

    @Override
    public TTSBean playSearch(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        LogUtils.d(TAG, "yt playSearch");
        voyahMusicControlInterface.playSearchYt(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode, new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                LogUtils.i(TAG,"ytSearch onSuccess:"+response);
                if (response != null) {
                    if (voyahMusicControlInterface.isVoyahMusicFront() && DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getPageState(PageId.yt_main.getId()) != 1) {
                        if (!TextUtils.isEmpty(response.getMediaName())) {
                            TTSBean bean = TtsReplyUtils.getTtsBean("4000007", "@{media_name}", response.getMediaName(), "@{app_name}", "云听");
                            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
                        }
                    } else {
                        if (!TextUtils.isEmpty(response.getMediaName())) {
                            TTSBean bean = TtsReplyUtils.getTtsBean("4010101", "@{media_name}", response.getMediaName());
                            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
                        }
                    }
                } else {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050014").getSelectTTs());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if (code == Code.NOT_LOGIN.code()) {
                    switchPage(true, PageId.yt_login.getId());
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000001").getSelectTTs());
                } else if (code == Code.NO_DATA.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4050015").getSelectTTs());
                } else if (code == Code.NETERROR.code() || code == Code.SERVICE_ERROR.code() || code == Code.INTERNAL_ERROR.code() || code == Code.ICP_TIMEOUT.code() || code == Code.PARAM_ERROR.code()) {
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000000").getSelectTTs());
                }
            }
        });
        return null;
    }

    @Override
    public TTSBean mediaSwitch(boolean isOpen) {
        LogUtils.i(TAG,"yt mediaSwitch:"+isOpen);
        return switchPage(isOpen,PageId.yt_main.getId());
    }

    @Override
    public boolean isLogin() {
        return voyahMusicControlInterface.isLoginYt();
    }

    public TTSBean mediaSwitchBroadcastYt(boolean isOpen){
        LogUtils.i(TAG,"yt mediaSwitchBroadcastYt:"+isOpen);
        if (isOpen) {
            if (DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getPageState(PageId.yt_broadcast.getId()) == 1) {
                return TTSIDConvertHelper.getInstance().getTTSBean("4014803");
            } else {
                switchPage(true, PageId.yt_broadcast.getId());
                return TTSIDConvertHelper.getInstance().getTTSBean("4014802");
            }
        } else {
            if (DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getPageState(PageId.yt_main.getId()) == 1) {
                switchPage(false, PageId.yt_main.getId());
                TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("1100003");
                String tts = ttsBean.getSelectTTs();
                tts = tts.replace("@{app_name}", "云听");
                ttsBean.setSelectTTs(tts);
                return ttsBean;
            } else {
                TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("1100004");
                String tts = ttsBean.getSelectTTs();
                tts = tts.replace("@{app_name}", "云听");
                ttsBean.setSelectTTs(tts);
                return ttsBean;
            }
        }
    }

    private void playSongsTts(VoiceMusicSongInfo songInfo){
        if (!TextUtils.isEmpty(songInfo.getMediaName())) {
            TTSBean bean = TtsReplyUtils.getTtsBean("4010101", "@{media_name}", songInfo.getMediaName());
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
        }
    }
}
