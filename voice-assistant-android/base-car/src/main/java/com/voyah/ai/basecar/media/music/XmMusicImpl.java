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
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public enum XmMusicImpl implements MediaMusicInterface {
    INSTANCE;
    private static final String TAG = XmMusicImpl.class.getSimpleName();

    private static final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    @Override
    public void init(Context context) {

    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean switchPage(boolean isOpen, int pageId) {
        LogUtils.i(TAG,"xm switchPage isOpen:"+isOpen+";pageid:"+pageId);

        int code = voyahMusicControlInterface.switchPage(isOpen,pageId);
        if (!isOpen) {
            String source = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getMediaPlayingResource();
            boolean needStop = pageId == PageId.main.getId() || (pageId == PageId.xm_main.getId() && MediaSource.xmly_music.getName().equals(source));
            if (needStop) {
                DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().stop(false);
            }
        }
        if (code == Code.SUCESS.code()) {
            LogUtils.d(TAG, isOpen ? "openPage success" : "closePage success");
            if (pageId == PageId.xm_main.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "1100002" : "1100003", "@{app_name}", "喜马拉雅");
            } else if (pageId == PageId.xm_play_page.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004700" : "4004800");
            } else if (pageId == PageId.xm_login.getId()) {
                return null;
            } else if (pageId == PageId.xm_list.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4004502" : "4004601");
            } else if (pageId == PageId.xm_history.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4003102" : "4003202");
            } else if (pageId == PageId.xm_collect.getId()) {
                return TtsReplyUtils.getTtsBean(isOpen ? "4011302" : "4011402");
            }
        } else if (code == Code.NOT_LOGIN.code()) {
            LogUtils.d(TAG, "not login");
            if (pageId == PageId.xm_collect.getId() || pageId == PageId.xm_history.getId()) {
                switchPage(true, PageId.xm_login.getId());
            }
            return TTSIDConvertHelper.getInstance().getTTSBean("4000001", 2);
        } else if (code == Code.VOICE_PAGEISEXIST.code()) {
            LogUtils.d(TAG, "voice_page_is_exist");
            if (pageId == PageId.xm_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100001", "@{app_name}", "喜马拉雅");
            } else if (pageId == PageId.xm_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004700");
            } else if (pageId == PageId.xm_login.getId()) {
                return null;
            } else if (pageId == PageId.xm_list.getId()) {
                return TtsReplyUtils.getTtsBean("4004501");
            } else if (pageId == PageId.xm_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003103");
            } else if (pageId == PageId.xm_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4011303");
            }
        } else if (code == Code.VOICE_PAGENOTEXIST.code()) {
            LogUtils.d(TAG, "voice_page_not_exist");
            if (pageId == PageId.xm_main.getId()) {
                return TtsReplyUtils.getTtsBean("1100004", "@{app_name}", "喜马拉雅");
            } else if (pageId == PageId.xm_play_page.getId()) {
                return TtsReplyUtils.getTtsBean("4004801");
            } else if (pageId == PageId.xm_login.getId()) {
                return null;
            } else if (pageId == PageId.xm_list.getId()) {
                // TODO: 2024/12/12 此tts id未更新到版本2
                return TtsReplyUtils.getTtsBean("4004602");
            } else if (pageId == PageId.xm_history.getId()) {
                return TtsReplyUtils.getTtsBean("4003201");
            } else if (pageId == PageId.xm_collect.getId()) {
                return TtsReplyUtils.getTtsBean("4011401");
            }
        } else if(code == Code.RGEAR.code()){
            return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean queryPlayInfo() {
        LogUtils.d(TAG, "xm queryPlayInfo");
        MediaMusicInfo mediaInfo = voyahMusicControlInterface.getMediaInfo(MediaSource.xmly_music.getName());
        LogUtils.d(TAG, "mediaInfo command: " + mediaInfo);
        if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getMediaId())) {
            return TtsReplyUtils.getTtsBean("4013001", "@{media_name}", mediaInfo.getMediaName(), "@{app_name}", "喜马拉雅");
        }
        return TtsReplyUtils.getTtsBean("4004402");
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        LogUtils.i(TAG,"xm switchLyric:"+isOpen);
        return null;
    }

    @Override
    public TTSBean collect(boolean isCollect) {
        LogUtils.i(TAG,"xm collect:"+isCollect);
        if(!isLogin()){
            return TtsReplyUtils.getTtsBean("4000001");
        }

        voyahMusicControlInterface.xmCollectState(new ObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "xmCollectState onSuccess: " + response);
                if((isCollect && response) || (!isCollect && !response)){
                    LogUtils.d(TAG, isCollect ? "xm collected" : "xm not collected");
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4012603" : "4012704").getSelectTTs());
                } else {
                    voyahMusicControlInterface.xmCollect(isCollect, new ObserverResponse<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            LogUtils.d(TAG, "onSuccess response: " + response);
                            if(response){
                                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean(isCollect ? "4012604" : "4012703").getSelectTTs());
                            } else {
                                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                if(code == Code.NO_ATTRIBUTE.code()){
                    DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
                    return;
                }
                DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4000000").getSelectTTs());
            }
        });
        return null;
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        LogUtils.i(TAG,"xm switchPlayList:"+isOpen);
        return switchPage(isOpen,PageId.xm_list.getId());
    }

    @Override
    public TTSBean switchPlayPage(boolean isOpen) {
        LogUtils.i(TAG,"xm switchPlayPage:"+isOpen);
        return switchPage(isOpen, PageId.xm_play_page.getId());
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.i(TAG,"xm switchCollectUI:"+isOpen);
        return switchPage(isOpen, PageId.xm_collect.getId());
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.i(TAG,"xm switchHistoryUI:"+isOpen);
        return switchPage(isOpen, PageId.xm_history.getId());
    }

    @Override
    public TTSBean playMusic() {
        int ret = voyahMusicControlInterface.play(MediaSource.xmly_music.getName());
        if (ret == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("4007401");
        } else if (ret == Code.PLAYING.code()) {
            return TtsReplyUtils.getTtsBean("4003902");
        } else if (ret == Code.NOT_LOGIN.code()) {
            switchPage(true, PageId.xm_login.getId());
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
        LogUtils.d(TAG, "xm playCollect");
        voyahMusicControlInterface.playCollectXm(new ObserverResponse<VoiceMusicSongInfo>() {
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
                    switchPage(true, PageId.xm_login.getId());
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
        LogUtils.d(TAG, "xm playHistory");
        voyahMusicControlInterface.playHistoryXm(new ObserverResponse<VoiceMusicSongInfo>() {
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
                    switchPage(true, PageId.xm_login.getId());
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
        LogUtils.i(TAG,"xm playRecommend");
        return null;
    }

    @Override
    public TTSBean playSearch(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        LogUtils.d(TAG, "xm playSearch");
        StringBuilder sb = new StringBuilder();
        String[] strings = new String[]{mediaName, mediaTypeDetail, mediaArtist, mediaAlbum, /*mediaDate,*/ mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank};
        for (String str : strings) {
            if (!TextUtils.isEmpty(str)) {
                sb.append(str).append(" ");
            }
        }
        if (TextUtils.isEmpty(sb.toString())) {
            int ret = voyahMusicControlInterface.play(MediaSource.xmly_music.getName());
            LogUtils.d(TAG, "ret: " + ret);
            if (ret == Code.SUCESS.code()) {
                return TtsReplyUtils.getTtsBean("4007401");
            } else if (ret == Code.PLAYING.code()) {
                return TtsReplyUtils.getTtsBean("4003902");
            } else if (ret == Code.NOT_LOGIN.code()) {
                switchPage(true, PageId.xm_login.getId());
                return TtsReplyUtils.getTtsBean("4000001");
            } else if (ret == Code.NOT_PAY.code()) {
                return TtsReplyUtils.getTtsBean("4010104");
            } else if (ret == Code.NOT_VIP.code()) {
                return TtsReplyUtils.getTtsBean("4010103");
            } else if (ret == Code.NO_SONG_CAN_PALY.code()) {
                return TtsReplyUtils.getTtsBean("4004503");
            }
            return null;
        }

        voyahMusicControlInterface.playSearchXm(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode, new ObserverResponse<VoiceMusicSongInfo>() {
            @Override
            public void onSuccess(VoiceMusicSongInfo response) {
                if (response != null) {
                    if (voyahMusicControlInterface.isVoyahMusicFront() && DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().getPageState(PageId.xm_main.getId()) != 1) {
                        if (!TextUtils.isEmpty(response.getMediaName())) {
                            TTSBean bean = TtsReplyUtils.getTtsBean("4000007", "@{media_name}",response.getMediaName(), "@{app_name}", "喜马拉雅");
                            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
                        }
                    } else {
                        if (!TextUtils.isEmpty(response.getMediaName())) {
                            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(TtsReplyUtils.getTtsBean("4010101", "@{media_name}", response.getMediaName()).getSelectTTs());
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
                    switchPage(true, PageId.xm_login.getId());
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
        LogUtils.i(TAG,"xm mediaSwitch:"+isOpen);
        return switchPage(isOpen,PageId.xm_main.getId());
    }

    @Override
    public boolean isLogin() {
        return voyahMusicControlInterface.isLoginXm();
    }

    public TTSBean mediaSpeedAdjustXm(String adjustType, String numberRate, String level){
        Float currentSpeed = voyahMusicControlInterface.getSpeed();
        if (currentSpeed == -1f) {
            return TtsReplyUtils.getTtsBean("4003100");
        }
        LogUtils.d(TAG, "getSpeed data: " + currentSpeed);
        if (TextUtils.equals(adjustType, "increase")) {
            if (currentSpeed == 2.0f) {
                return TtsReplyUtils.getTtsBean("4013604");
            }
            Float speed = currentSpeed + 0.25f;
            if (speed > 2.0f) {
                speed = 2.0f;
            }
            voyahMusicControlInterface.setSpeed(speed);
            return TtsReplyUtils.getTtsBean("4017302");
        } else if (TextUtils.equals(adjustType, "decrease")) {
            if (currentSpeed == 0.5f) {
                return TtsReplyUtils.getTtsBean("4013605");
            }
            Float speed = currentSpeed - 0.25f;
            if (speed < 0.5f) {
                speed = 0.5f;
            }
            voyahMusicControlInterface.setSpeed(speed);
            return TtsReplyUtils.getTtsBean("4017303");
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                if (currentSpeed == 2.0f) {
                    return TtsReplyUtils.getTtsBean("4013604");
                }
                voyahMusicControlInterface.setSpeed(2.0f);
                return TtsReplyUtils.getTtsBean("1100005");
            } else if (TextUtils.equals(level, "min")) {
                if (currentSpeed == 0.5f) {
                    return TtsReplyUtils.getTtsBean("4013605");
                }
                voyahMusicControlInterface.setSpeed(0.5f);
                return TtsReplyUtils.getTtsBean("1100005");
            } else {
                float num = Float.parseFloat(numberRate);
                LogUtils.d(TAG, "set num: " + num);
                if (num > 0) {
                    if (num == currentSpeed) {
                        return TtsReplyUtils.getTtsBean("4024505", "@{media_speed}", String.valueOf(num));
                    } else {
                        if (num == 0.5f || num == 0.75f || num == 1.0f || num == 1.25f || num == 1.5f || num == 1.75f || num == 2.0f) {
                            voyahMusicControlInterface.setSpeed(num);
                            return TtsReplyUtils.getTtsBean("1100005");
                        } else if (num > 2.0f) {
                            voyahMusicControlInterface.setSpeed(2.0f);
                            return TtsReplyUtils.getTtsBean("4024503", "@{media_speed_max}", "2.0");
                        } else if (num < 0.5f) {
                            voyahMusicControlInterface.setSpeed(0.5f);
                            return TtsReplyUtils.getTtsBean("4024504", "@{media_speed_min}", "0.5");
                        } else {
                            float[] targets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};
                            float closest = NumberUtils.findClosestValue(num, targets);
                            LogUtils.d(TAG, "closest: " + closest);
                            voyahMusicControlInterface.setSpeed(closest);
                            return TtsReplyUtils.getTtsBean("1100005");
                        }
                    }
                }
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    private void playSongsTts(VoiceMusicSongInfo songInfo){
        if (!TextUtils.isEmpty(songInfo.getMediaName())) {
            TTSBean bean = TtsReplyUtils.getTtsBean("4010101", "@{media_name}", songInfo.getMediaName());
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(bean.getSelectTTs());
        }
    }
}
