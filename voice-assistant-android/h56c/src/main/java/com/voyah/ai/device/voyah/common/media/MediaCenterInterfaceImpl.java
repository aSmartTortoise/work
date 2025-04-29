package com.voyah.ai.device.voyah.common.media;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaCenterInterface;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.device.media.VoyahMusicInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UIState;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voyah.ai.basecar.media.bean.UserHandleInfo;
import com.voyah.ai.basecar.media.music.QqMusicImpl;
import com.voyah.ai.basecar.media.music.VoyahMusicImpl;
import com.voyah.ai.basecar.media.music.WyMusicImpl;
import com.voyah.ai.basecar.media.service.MediaDeviceService;
import com.voyah.ai.basecar.media.utils.MediaAudioZoneUtils;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.basecar.media.vedio.BiliCeilingImpl;
import com.voyah.ai.basecar.media.vedio.BiliCopilotImpl;
import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.basecar.media.vedio.IqyCeilingImpl;
import com.voyah.ai.basecar.media.vedio.IqyCopilotImpl;
import com.voyah.ai.basecar.media.vedio.IqyImpl;
import com.voyah.ai.basecar.media.vedio.MiguCeilingImpl;
import com.voyah.ai.basecar.media.vedio.MiguCopilotImpl;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.basecar.media.vedio.ScreenPushImpl;
import com.voyah.ai.basecar.media.vedio.ScreenShareImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoCeilingImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoCopilotImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.media.vedio.TiktokCeilingImpl;
import com.voyah.ai.basecar.media.vedio.TiktokCopilotImpl;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.basecar.media.vedio.UsbVideoImpl;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.common.utils.SPUtil;
import com.voyah.ai.device.voyah.common.media.impl.VoyahMusicControlImpl;
import com.voyah.ai.sdk.listener.ITtsPlayListener;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.cockpit.window.model.MultimediaInfo;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaCenterInterfaceImpl implements MediaCenterInterface {

    private static final String TAG = MediaCenterInterfaceImpl.class.getSimpleName();

    private static  MediaCenterInterfaceImpl mediaInterface;

    private String soundLocation;
    private String queryPosition;
    public String mIdentifier = "";
    private Context context;

    private int displayId;
    private UserHandle userHandle;

    public static MediaCenterInterfaceImpl getInstance() {
        if (mediaInterface == null) {
            synchronized (MediaCenterInterfaceImpl.class) {
                if (mediaInterface == null) {
                    mediaInterface = new MediaCenterInterfaceImpl();
                }
            }
        }
        return mediaInterface;
    }

    @Override
    public VoyahMusicInterface getVoyahMusic() {
        return VoyahMusicImpl.INSTANCE;
    }

    @Override
    public VoyahMusicControlInterface getVoyahMusicControl() {
        return VoyahMusicControlImpl.INSTANCE;
    }

    private final VoyahMusicControlInterface voyahMusicControlInterface = getVoyahMusicControl();

    @Override
    public void init() {
        context = Utils.getApp();
        // 多屏互动初始化
        ScreenShareImpl.INSTANCE.init(context);

        IqyImpl.INSTANCE.init(context);
        IqyCopilotImpl.INSTANCE.init(context);
        IqyCeilingImpl.INSTANCE.init(context);

        TencentVideoImpl.INSTANCE.init(context);
        TencentVideoCopilotImpl.INSTANCE.init(context);
        TencentVideoCeilingImpl.INSTANCE.init(context);

        ThunderKtvImpl.INSTANCE.init(context);

        MiguImpl.INSTANCE.init(context);
        MiguCopilotImpl.INSTANCE.init(context);
        MiguCeilingImpl.INSTANCE.init(context);

        BiliImpl.INSTANCE.init(context);
        BiliCopilotImpl.INSTANCE.init(context);
        BiliCeilingImpl.INSTANCE.init(context);

        UsbVideoImpl.INSTANCE.init(context);
        VoyahMusicImpl.INSTANCE.init(context);

        TiktokImpl.INSTANCE.init(context);
        TiktokCopilotImpl.INSTANCE.init(context);
        TiktokCeilingImpl.INSTANCE.init(context);

        MediaAudioZoneUtils.getInstance().init(context);
    }

    @Override
    public TTSBean initVoicePosition(String queryPosition, String soundLocation) {
        this.soundLocation = soundLocation;
        this.queryPosition = queryPosition;

        VoyahMusicImpl.INSTANCE.initUserHandle(queryPosition,soundLocation);
        return initTargetUserHandle(queryPosition,soundLocation);
    }

    private void initUserHandle(UserHandle userHandle,int displayId){
        voyahMusicControlInterface.initUserHandle(userHandle,displayId);
        VideoControlCenter.getInstance().setCurrentDisplayId(displayId);
    }

    @Override
    public void destroy() {
        Context context = Utils.getApp();

        IqyImpl.INSTANCE.destroy(context);
        IqyCopilotImpl.INSTANCE.destroy(context);
        IqyCeilingImpl.INSTANCE.destroy(context);

        TencentVideoImpl.INSTANCE.destroy(context);
        TencentVideoCopilotImpl.INSTANCE.destroy(context);
        TencentVideoCeilingImpl.INSTANCE.destroy(context);

        ThunderKtvImpl.INSTANCE.destroy(context);

        MiguImpl.INSTANCE.destroy(context);
        MiguCopilotImpl.INSTANCE.destroy(context);
        MiguCeilingImpl.INSTANCE.destroy(context);

        BiliImpl.INSTANCE.destroy(context);
        BiliCopilotImpl.INSTANCE.destroy(context);
        BiliCeilingImpl.INSTANCE.destroy(context);

        UsbVideoImpl.INSTANCE.destroy(context);

        TiktokImpl.INSTANCE.destroy(context);
        TiktokCopilotImpl.INSTANCE.destroy(context);
        TiktokCeilingImpl.INSTANCE.destroy(context);
    }

    @Override
    public TTSBean switchUi(boolean isOpen, String uiName, String appName, String mediaType) {
        if ("player".equals(uiName)) {
            if (!TextUtils.isEmpty(appName)) {
                String pkgName = MediaHelper.getPackageName(appName);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaSourceByPkgName(pkgName))) {
                    return VideoControlCenter.getInstance().switchVideoApp(isOpen, pkgName);
                } else if (VoyahMusicImpl.INSTANCE.isOpenWy(appName)) {
                    return WyMusicImpl.INSTANCE.switchPage(isOpen, PageId.wy_main.getId());
                } else {
                    return QqMusicImpl.INSTANCE.switchPage(isOpen, PageId.QQ_main.getId());
                }
            } else {
                if ("video".equals(mediaType)) {
                    int pref = SettingsManager.get().getVideoPreference();
                    LogUtils.d(TAG, "video pref: " + pref);
                    //关闭视频播放器,如果腾讯，爱奇艺都不在前台，需要回复当前无播放内容
//                    if (!isOpen) {
//                        if (!TencentVideoImpl.INSTANCE.isFront() && !IqyImpl.INSTANCE.isFront()) {
//                            return TtsReplyUtils.getTtsBean("4004701");
//                        }
//                    }
                    LogUtils.d(TAG, "video pref: " + pref);
                    if (pref == 1) {
                        return VideoControlCenter.getInstance().switchVideoApp(isOpen,TencentVideoImpl.APP_NAME);
                    } else if (pref == 2) {
                        return VideoControlCenter.getInstance().switchVideoApp(isOpen,IqyImpl.APP_NAME);
                    } else {
                        if (SPUtil.getBoolean(Utils.getApp(), MediaDeviceService.IS_TENCENT_PLAY, false)) {
                            return VideoControlCenter.getInstance().switchVideoApp(isOpen,TencentVideoImpl.APP_NAME);
                        } else {
                            return VideoControlCenter.getInstance().switchVideoApp(isOpen,IqyImpl.APP_NAME);
                        }
                    }
                } else {
                    if ("music".equals(mediaType)) {
                        if (VoyahMusicImpl.INSTANCE.isOpenWy(null)) {
                            //当前默认音源为网易云音乐，但是当前QQ音乐在前台，tts需要做处理
                            return WyMusicImpl.INSTANCE.switchPage(isOpen, PageId.wy_main.getId());
                        } else {
                            return QqMusicImpl.INSTANCE.switchPage(isOpen, PageId.QQ_main.getId());
                        }
                    }  else {
                        //打开播放器，如果没有明确要打开的应用，TTS播报
                        if (isPlayingAllScreen()) {
                            return VoyahMusicImpl.INSTANCE.switchPlayer(isOpen);
                        }
                        return TtsReplyUtils.getTtsBean("4050062");
                    }
                }
            }
        } else if ("player_page".equals(uiName)) {
            if(isPlayingAllScreen()){
                return VoyahMusicImpl.INSTANCE.switchPlayPage(isOpen);
            }
            return TtsReplyUtils.getTtsBean("4004701");
        } else if ("play_list".equals(uiName)) {
            if(isPlayingAllScreen() || voyahMusicControlInterface.isVoyahMusicFront()){
                return VoyahMusicImpl.INSTANCE.switchPlayList(isOpen);
            }
            if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.IS_FRONT)) {
                return TtsReplyUtils.getTtsBean("4003100");
            }
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }


    @Override
    public TTSBean open(boolean isOpen, String mediaType, String mediaSource, String appName, String position, String queryPosition) {
        if (MediaConstant.MUSIC.equals(mediaType)) {
            if ("usb".equals(mediaSource)) {
                return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.usb_music.getName(), true, position, queryPosition);
            } else if ("local".equals(mediaSource) || "bluetooth".equals(mediaSource)) {
                return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.bt_music.getName(), true,position, queryPosition);
            } else {
                if(isOpen){
                    if (VoyahMusicImpl.INSTANCE.isOpenWy(appName)) {
                        return VoyahMusicImpl.INSTANCE.open(true, MediaSource.wy_music.getName(), true,position,queryPosition);
                    } else {
                        return VoyahMusicImpl.INSTANCE.open(true, MediaSource.qq_music.getName(), true,position,queryPosition);
                    }
                } else {
                    return WyMusicImpl.INSTANCE.switchPage(false, PageId.main.getId());
                }
            }
        } else if (MediaConstant.VIDEO.equals(mediaType)) {
            if ("local".equals(mediaSource) || "usb".equals(mediaSource)) {
                return UsbVideoImpl.INSTANCE.open(isOpen);
            } else {
                int pref = SettingsManager.get().getVideoPreference();
                LogUtils.d(TAG, "video pref: " + pref);
                if (pref == 1) {
                    return VideoControlCenter.getInstance().switchVideoApp(isOpen,TencentVideoImpl.APP_NAME);
                } else if (pref == 2) {
                    return VideoControlCenter.getInstance().switchVideoApp(isOpen,IqyImpl.APP_NAME);
                } else {
                    if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.IS_FRONT)) {
                        return VideoControlCenter.getInstance().switchVideoApp(isOpen, "");
                    }
                    if (SPUtil.getBoolean(context, MediaHelper.IS_TENCENT_PLAY, false)) {
                        return VideoControlCenter.getInstance().switchVideoApp(isOpen, TencentVideoImpl.APP_NAME);
                    } else {
                        return VideoControlCenter.getInstance().switchVideoApp(isOpen, IqyImpl.APP_NAME);
                    }
                }
            }
        } else if (MediaConstant.AUDIO_BOOK.equals(mediaType)) {
            return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.xmly_music.getName(), true,position, queryPosition);
        } else if (MediaConstant.RADIO.equals(mediaType)) {
            return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.yt_broadcast.getName(), true,position, queryPosition);
        } else if (MediaConstant.KTV.equals(mediaType)) {
            return VideoControlCenter.getInstance().switchVideoApp(isOpen, ThunderKtvImpl.APP_NAME);
        } else if (MediaConstant.AUDIO.equals(mediaType)) {
            if (VoyahMusicImpl.INSTANCE.isOpenWy(appName)) {
                return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.wy_music.getName(), true, position, queryPosition);
            } else {
                return VoyahMusicImpl.INSTANCE.open(isOpen, MediaSource.qq_music.getName(), true, position, queryPosition);
            }
        }
        return null;
    }

    @Override
    public TTSBean pre() {
        String source = getMeidaPlayingSource();
        if (!TextUtils.isEmpty(source)) {
            return VoyahMusicImpl.INSTANCE.pre(source);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().pre();
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.pre(null);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().pre();
        } else {
            return VoyahMusicImpl.INSTANCE.pre(null);
        }
    }

    @Override
    public TTSBean next() {
        String source = getMeidaPlayingSource();
        if (!TextUtils.isEmpty(source)) {
            return VoyahMusicImpl.INSTANCE.next(source);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().next();
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.next(null);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().next();
        } else {
            return VoyahMusicImpl.INSTANCE.next(null);
        }
    }

    @Override
    public TTSBean play() {
        if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.play(null);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().play();
        } else {
            return VoyahMusicImpl.INSTANCE.play(null);
        }
    }

    @Override
    public TTSBean replay() {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.replay();
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().replay();
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.replay();
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().replay();
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean stop(boolean isExit) {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.stop(isExit);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().stop(isExit);
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.stop(isExit);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().stop(isExit);
        } else if (isExit && VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.IS_FRONT)) {
            return VideoControlCenter.getInstance().stop(isExit);
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.seek(seekType, duration);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().seek(seekType, duration);
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.seek(seekType, duration);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().seek(seekType, duration);
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode){
        if (isPlayingAllScreen() || voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.switchPlayMode(switchType, playMode);
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        VoyahMusicImpl.INSTANCE.switchHistoryUI(isOpen, null);
        return TtsReplyUtils.getTtsBean("1100005");
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        VoyahMusicImpl.INSTANCE.switchCollectUI(isOpen, null);
        return TtsReplyUtils.getTtsBean("1100005");
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchDanmaku(isOpen);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().speed(adjustType, numberRate, level);
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().speed(adjustType, numberRate, level);
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().definition(adjustType, mode, level);
        }
        return null;
    }

    @Override
    public TTSBean queryPlayInfo() {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.queryPlayInfo();
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().queryPlayInfo();
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.queryPlayInfo();
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().queryPlayInfo();
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean jump() {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().jump();
        }
        return null;
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        if(isPlayingAllScreen() || voyahMusicControlInterface.isVoyahMusicFront()){
            return VoyahMusicImpl.INSTANCE.switchLyric(isOpen);
        }
        return TtsReplyUtils.getTtsBean("4004701");
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        if (isPlayingAllScreen()) {
            return VoyahMusicImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().switchCollect(isCollect, mediaType);
        } else if (voyahMusicControlInterface.isVoyahMusicFront()) {
            return VoyahMusicImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchCollect(isCollect, mediaType);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchComment(boolean isComment, String mediaType) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchComment(isComment, mediaType);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchLike(boolean isLike, String mediaType) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchLike(isLike, mediaType);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchAttention(isAttention, mediaType);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchPlayer(isOpen);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        return VoyahMusicImpl.INSTANCE.switchPlayList(isOpen);
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().switchOriginalSinging(isOriginal);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        // 播放usb bt
        if ("bluetooth".equals(mediaSource) || "phone".equals(mediaSource)) {
            if((MediaHelper.isCeilScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()) || (MediaHelper.isPassengerScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen()) || MediaHelper.isPassengerScreen(queryPosition) || MediaHelper.isCeilScreen(queryPosition)){
                return TtsReplyUtils.getTtsBeanText("当前屏幕还不支持这个应用");
            }
            return VoyahMusicImpl.INSTANCE.playMusic("蓝牙音乐");
        } else if ("usb".equals(mediaSource)) {
            if((MediaHelper.isCeilScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()) || (MediaHelper.isPassengerScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen())  || MediaHelper.isPassengerScreen(queryPosition) || MediaHelper.isCeilScreen(queryPosition)){
                return TtsReplyUtils.getTtsBeanText("当前屏幕还不支持这个应用");
            }
            return VoyahMusicImpl.INSTANCE.playMusic("USB音乐");
        }
        // 播放历史、收藏、推荐
        if ("history".equals(mediaUi) || "collection".equals(mediaUi)) {
            if (!TextUtils.isEmpty(appName)) {
                String pkgName = MediaHelper.getPackageName(appName);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaSourceByPkgName(pkgName))) {
                    VideoControlCenter.getInstance().playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                }
                if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.yt_music.getName())) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.yt_history.getId() : PageId.yt_collect.getId());
                }
                if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.xmly_music.getName())) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.xm_history.getId() : PageId.xm_collect.getId());
                }
                if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.qq_music.getName())) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.QQ_history.getId() : PageId.QQ_collect.getId());
                }
                if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.wy_music.getName())) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.wy_history.getId() : PageId.wy_collect.getId());
                }
                return TtsReplyUtils.getTtsBean("4003100");
            }

            if (TextUtils.isEmpty(mediaType)) {
                if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.IS_FRONT)) {
                    VideoControlCenter.getInstance().playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                } else if (VoyahMusicImpl.INSTANCE.getPageState(PageId.wy_main.getId()) == 1) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.wy_history.getId() : PageId.wy_collect.getId());
                } else if (VoyahMusicImpl.INSTANCE.getPageState(PageId.QQ_main.getId()) == 1) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.QQ_history.getId() : PageId.QQ_collect.getId());
                } else if (VoyahMusicImpl.INSTANCE.getPageState(PageId.xm_main.getId()) == 1) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.xm_history.getId() : PageId.xm_collect.getId());
                } else if (VoyahMusicImpl.INSTANCE.getPageState(PageId.yt_main.getId()) == 1) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.yt_history.getId() : PageId.yt_collect.getId());
                }

                if (isPlayingAllScreen()) {
                    String source = VoyahMusicImpl.INSTANCE.getSource();
                    if (MediaSource.wy_music.getName().equals(source)) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.wy_history.getId() : PageId.wy_collect.getId());
                    } else if (MediaSource.qq_music.getName().equals(source)) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.QQ_history.getId() : PageId.QQ_collect.getId());
                    } else if (MediaSource.xmly_music.getName().equals(source)) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.xm_history.getId() : PageId.xm_collect.getId());
                    } else if (MediaSource.yt_music.getName().equals(source)) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.yt_history.getId() : PageId.yt_collect.getId());
                    } else {
                        return TtsReplyUtils.getTtsBean("4003100");
                    }
                } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
                    VideoControlCenter.getInstance().playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                }
                return TtsReplyUtils.getTtsBean("4003100");
            } else {
                if ("video".equals(mediaType)) {
                    int pref = SettingsManager.get().getVideoPreference();
                    LogUtils.d(TAG, "video pref: " + pref);
                    if (pref == 1) {
                        TencentVideoImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                    } else if (pref == 2) {
                        IqyImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                    } else {
                        if (SPUtil.getBoolean(Utils.getApp(), MediaHelper.IS_TENCENT_PLAY, false)) {
                            TencentVideoImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                        } else {
                            IqyImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? 1 : 2);
                        }
                    }
                } else if ("music".equals(mediaType)) {
                    if (ThunderKtvImpl.INSTANCE.isFront()) {
                        return TtsReplyUtils.getTtsBean("4003100");
                    }
                    if (VoyahMusicImpl.INSTANCE.getPageState(PageId.wy_main.getId()) == 1) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.wy_history.getId() : PageId.wy_collect.getId());
                    } else if (VoyahMusicImpl.INSTANCE.getPageState(PageId.QQ_main.getId()) == 1) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.QQ_history.getId() : PageId.QQ_collect.getId());
                    }
                    boolean isWy = VoyahMusicImpl.INSTANCE.isOpenWy(null);
                    if (isWy) {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ?
                                PageId.wy_history.getId() : PageId.wy_collect.getId());
                    } else {
                        return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ?
                                PageId.QQ_history.getId() : PageId.QQ_collect.getId());
                    }
                } else if ("radio".equals(mediaType)) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.yt_history.getId() : PageId.yt_collect.getId());
                } else if ("audio_book".equals(mediaType)) {
                    return VoyahMusicImpl.INSTANCE.playUI(StringUtils.equals(mediaUi, "history") ? PageId.xm_history.getId() : PageId.xm_collect.getId());
                } else {
                    return TtsReplyUtils.getTtsBean("4003100");
                }
            }
        }

        // 播放爱奇艺 网易云等
        if (!TextUtils.isEmpty(appName)) {
            LogUtils.d(TAG, "appName = " + appName + ", displayId == " + displayId);
            if (MediaHelper.isSafeLimitationAndMain(displayId) && isVideoApp(DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName))) {
                return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
            }
            if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.tiktok_video.getName())) {
                if (AppUtils.isAppInstalled(TiktokImpl.APP_NAME)) {
                    //不支持用车鱼搜索内容 VCOSYY-2447
                    if (org.apache.commons.lang3.StringUtils.isBlank(mediaName)) {
                        TiktokImpl.INSTANCE.openAndPlay();
                        return TtsReplyUtils.getTtsBean("");
                    } else {
                        return TtsReplyUtils.getTtsBean("4030700", "@{app_name}", MediaTtsManager.APP_NAME_TIKTOK);
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().searchApp(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), MediaTtsManager.APP_NAME_TIKTOK);
                    return TtsReplyUtils.getTtsBean("4033502", "@{app_name}", "车鱼视听");
                }
            }
            if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.bili_video.getName())) {
                if (AppUtils.isAppInstalled(BiliImpl.APP_NAME)) {
                    //不支持用哔哩搜索内容
                    if (org.apache.commons.lang3.StringUtils.isBlank(mediaName)) {
                        VideoControlCenter.getInstance().switchVideoApp(true,BiliImpl.APP_NAME);
                        return TtsReplyUtils.getTtsBean("4019702");
                    } else {
                        return TtsReplyUtils.getTtsBean("4030700", "@{app_name}", "bilibili");
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().searchApp(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), "bilibili");
                    return TtsReplyUtils.getTtsBean("4033502", "@{app_name}", "bilibili");
                }
            }
            if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.migu_video.getName())) {
                if (AppUtils.isAppInstalled(MiguImpl.APP_NAME)) {
                    //不支持用哔哩搜索内容
                    if (org.apache.commons.lang3.StringUtils.isBlank(mediaName)) {
                        VideoControlCenter.getInstance().switchVideoApp(true, MiguImpl.APP_NAME);
                        return TtsReplyUtils.getTtsBean("4019702");
                    } else {
                        return TtsReplyUtils.getTtsBean("4030700", "@{app_name}", MediaTtsManager.APP_NAME_MI_GU);
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().searchApp(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), MediaTtsManager.APP_NAME_MI_GU);
                    return TtsReplyUtils.getTtsBean("4033502", "@{app_name}", MediaTtsManager.APP_NAME_MI_GU);
                }
            }

            if (NumberUtils.areAllStringsEmpty(mediaTypeDetail, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode)) {
                if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.iqy_video.getName())) {
                    //播放爱奇艺，直接打开
                    VideoControlCenter.getInstance().switchVideoApp(true, IqyImpl.APP_NAME);
                    return TtsReplyUtils.getTtsBean("4019702");
                } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.tencent_video.getName())) {
                    VideoControlCenter.getInstance().switchVideoApp(true,TencentVideoImpl.APP_NAME);
                    return TtsReplyUtils.getTtsBean("4019702");
                } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.bili_video.getName())) {
                    BiliImpl.INSTANCE.playUI(1);
                    return null;
                } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.voyah_music.getName())
                        || VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.wy_music.getName())
                        || VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.qq_music.getName())
                        || VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.xmly_music.getName())
                        || VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.yt_music.getName())) {
                    return VoyahMusicImpl.INSTANCE.playMusic(appName);
                } else {
                    return TtsReplyUtils.getTtsBean("4003100");
                }
            } else {
                if (TextUtils.isEmpty(mediaType)) {
                    if (appName.contains("爱奇艺") || appName.contains("腾讯")) {
                        mediaType = "video";
                    } else if (appName.contains("雷石")) {
                        mediaType = "ktv";
                    }
                }
            }
        }

        if (TextUtils.equals(mediaType, "ktv")) {
            if (AppUtils.isAppInstalled(ThunderKtvImpl.APP_NAME)) {
                return ThunderKtvImpl.INSTANCE.play(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
            } else {
                DeviceHolder.INS().getDevices().getAppStore().searchApp(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), "雷石KTV");
                return TtsReplyUtils.getTtsBean("4033502", "@{app_name}", "雷石KTV");
            }
        } else {
            return VoyahMusicImpl.INSTANCE.play(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
        }
    }

    @Override
    public TTSBean playUI(int type) {
        return VoyahMusicImpl.INSTANCE.playUI(type);
    }

    @Override
    public void scheme(VideoInfo videoInfo) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), "");
        VideoControlCenter.getInstance().scheme(videoInfo);
    }

    @Override
    public TTSBean playEpisode(int playEpisode) {
        if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)
                || VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAY_PAGE)
                || VideoControlCenter.getInstance().isVideoMirror()) {
            return VideoControlCenter.getInstance().playEpisode(playEpisode);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean playVideo(String mediaSource,String appName,String mediaUi) {
        //播放本地视频
        if ("local".equals(mediaSource) || "usb".equals(mediaSource)) {
            return UsbVideoImpl.INSTANCE.openAndPlay();
        }
        //应用不在前台，如果带有具体应用名称执行打开,用xx看剧
        if (!org.apache.commons.lang3.StringUtils.isBlank(appName)) {
            LogUtils.d(TAG, "appName = " + appName + ", mediaUi = " + mediaUi);
            //判断是否安装
            if (!DeviceHolder.INS().getDevices().getSystem().getApp().isInstalledByAppName(appName)) {
                int code = DeviceHolder.INS().getDevices().getAppStore().searchApp(MediaHelper.getDeviceScreenTypeByDisplayId(displayId), appName);
                return TtsReplyUtils.getTtsBean("4033502", "@{app_name}", appName);
            }
            //打开应用
            String pkgName = MediaHelper.getPackageName(appName);
            if (org.apache.commons.lang3.StringUtils.isBlank(mediaUi)) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(pkgName) && org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaSourceByPkgName(pkgName))) {
                    VideoControlCenter.getInstance().switchVideoApp(true, pkgName);
                    return TtsReplyUtils.getTtsBean("4019702");
                }
            } else {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(pkgName) && org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaSourceByPkgName(pkgName))) {
                    VideoControlCenter.getInstance().playUI(mediaUi.equals("history") ? 1 : 2);
                    return TtsReplyUtils.getTtsBean("");
                }
            }
        } else if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.IS_FRONT)) {
            if (VideoControlCenter.getInstance().isVideoStatus(soundLocation, VideoControlCenter.MediaStatus.PLAYING)) {
                return TtsReplyUtils.getTtsBean("4003902");
            } else {
                if (org.apache.commons.lang3.StringUtils.isBlank(mediaUi)) {
                    VideoControlCenter.getInstance().play();
                } else {
                    VideoControlCenter.getInstance().playUI(mediaUi.equals("history") ? 1 : 2);
                }
            }
        }
        int pref = SettingsManager.get().getVideoPreference();
        LogUtils.d(TAG, "video pref: " + pref);
        if (pref == 1) {
            VideoControlCenter.getInstance().openTencentOrIqy(mediaUi, true);
        } else if (pref == 2) {
            VideoControlCenter.getInstance().openTencentOrIqy(mediaUi, false);
        } else {
            VideoControlCenter.getInstance().openTencentOrIqy(mediaUi, SPUtil.getBoolean(Utils.getApp(), MediaDeviceService.IS_TENCENT_PLAY, false));
        }
        return TtsReplyUtils.getTtsBean("4019702");
    }

    @Override
    public void openTencentOrIqy(String mediaUi, boolean isTencent) {
    }

    @Override
    public Map<String, Object> getVideoMap(List<VideoInfo> videoInfoList,String sessionId,String requestId) {
        List<MultimediaInfo> multimediaInfos = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < videoInfoList.size(); i++) {
            if (count >= 64) {
                LogUtils.d(TAG, "executeAgent displayed number exceed...");
                break;
            }
            VideoInfo videoInfo = videoInfoList.get(i);
            MultimediaInfo multimediaInfo = new MultimediaInfo();
            multimediaInfo.setName(videoInfo.videoName);
            //_260_360 竖图后缀
            //_480_270 横图后缀
            //videoInfo.source =1 爱奇艺 source=0 腾讯
            if (!org.apache.commons.lang3.StringUtils.isBlank(videoInfo.videoPic) && videoInfo.videoPic.contains(".jpg") && videoInfo.source == 1) {
                String picStr = videoInfo.videoPic.substring(0, videoInfo.videoPic.indexOf(".jpg")) + "_260_360" + videoInfo.videoPic.substring(videoInfo.videoPic.indexOf(".jpg"));
                multimediaInfo.setImgUrl(picStr);
            } else {
                multimediaInfo.setImgUrl(videoInfo.videoPic);
            }
            if (videoInfo.channelId == 1) {
                multimediaInfo.setType(MultimediaInfo.MediaType.MOVIE);
            } else if (videoInfo.channelId == 2) {
                multimediaInfo.setType(MultimediaInfo.MediaType.TV_DRAMA);
                if (videoInfo.total < 1) {
                    videoInfo.total = 1;
                }
                multimediaInfo.setEpisodes(videoInfo.total);
            } else {
                multimediaInfo.setType(videoInfo.channelId);
                if (videoInfo.total < 1) {
                    videoInfo.total = 1;
                }
                multimediaInfo.setEpisodes(videoInfo.total);
            }
            if (videoInfo.source == 1) {
                multimediaInfo.setSourceType(MultimediaInfo.SourceType.IQIYI);
            } else {
                multimediaInfo.setSourceType(MultimediaInfo.SourceType.TENCENT);
            }
            if (videoInfo.isVip) {
                multimediaInfo.setTagType(MultimediaInfo.TagType.VIP);
            } else if (videoInfo.isExclusive) {
                multimediaInfo.setTagType(MultimediaInfo.TagType.SOLE_BROADCAST);
            }
            multimediaInfos.add(multimediaInfo);
            count++;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("scene", "State.VIDEO_OPTIONS");
        map.put("sessionId", sessionId);
        map.put("requestId", requestId);
        map.put("videoList", multimediaInfos);
        return map;
    }

    @Override
    public int getPlayingDisplayId() {
        return MediaHelper.getPlayingDisplayId();
    }

    @Override
    public int getDisplayId(String srcScreen) {
        return MediaHelper.getDisplayId(srcScreen);
    }

    @Override
    public boolean isMultiScreenForDst(String screenStr,String dstPosition) {
        //如果超过两个就是多屏
        if (org.apache.commons.lang3.StringUtils.isBlank(screenStr)) {
            return dstPosition.contains(",") || MediaHelper.ALL_SCREEN.equals(dstPosition);
        } else {
            return screenStr.contains(",") || MediaHelper.ALL_SCREEN.equals(screenStr);
        }
    }

    @Override
    public int getDisplayId(int screenType) {
        return MediaHelper.getDisplayIdByPosition(screenType);
    }

    @Override
    public void pushScreenToDisplay(int sourceDisplayId, int targetDisplayId, boolean isSoundLocation) {
        //头枕音响开启不支持推送
        if (MediaHelper.isSupportHeatRestSound() && MediaHelper.isHeatRestSoundOpened()) {
            MediaHelper.speakTts("4050076");
            return;
        }
        ScreenPushImpl.INSTANCE.pushScreenToDisplay(sourceDisplayId, targetDisplayId, isSoundLocation);
    }

    @Override
    public void shareScreenSingle(int sourceDisplayId, int targetDisplayId, boolean isSoundLocation) {
        //头枕音响开启不支持同看
        if (MediaHelper.isSupportHeatRestSound() && MediaHelper.isHeatRestSoundOpened()) {
            MediaHelper.speakTts("4050075");
            return;
        }
        ScreenShareImpl.INSTANCE.shareScreenSingle(sourceDisplayId, targetDisplayId,isSoundLocation);
    }

    @Override
    public void closeShareScreenSingle(int displayId) {
        ScreenShareImpl.INSTANCE.closeShareScreenSingle(displayId);
    }

    @Override
    public void shareScreenForAll(int sourceDisplayId) {
        //头枕音响开启不支持同看
        if (MediaHelper.isSupportHeatRestSound() && MediaHelper.isHeatRestSoundOpened()) {
            MediaHelper.speakTts("4050075");
            return;
        }
        ScreenShareImpl.INSTANCE.shareScreenForAll(sourceDisplayId);
    }

    @Override
    public void closeShareScreenForAll() {
        ScreenShareImpl.INSTANCE.closeShareScreenForAll();
    }

    @Override
    public boolean switchVideoPage(String uiName, String appName, boolean isOpen, String mediaType) {
        String pkgName = MediaHelper.getPackageName(appName);
        LogUtils.d(TAG, "switchVideoPage pkgName = " + pkgName + ", displayId = " + displayId + ", mediaType = " + mediaType);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(appName) && VoyahMusicImpl.INSTANCE.isMedia(appName)) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(uiName)) {
            if ((org.apache.commons.lang3.StringUtils.isNotBlank(pkgName) && org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaSourceByPkgName(pkgName)))
                    || org.apache.commons.lang3.StringUtils.isNotBlank(VideoControlCenter.getInstance().getMediaIsFrontByDisplayId(displayId))) {
                if (ApplicationConstant.UI_NAME_HISTORY.equals(uiName)) {
                    VideoControlCenter.getInstance().switchHistoryUI(isOpen);
                } else {
                    VideoControlCenter.getInstance().switchCollectUI(isOpen);
                }
                return true;
            }
            //模糊意图
            if ("video".equals(mediaType)) {
                int pref = SettingsManager.get().getVideoPreference();
                LogUtils.d(TAG, "video pref: " + pref);
                if (pref == 1) {
                    openTencentOrIqy(uiName, true);
                } else if (pref == 2) {
                    openTencentOrIqy(uiName, false);
                } else {
                    openTencentOrIqy(uiName, SPUtil.getBoolean(Utils.getApp(), MediaDeviceService.IS_TENCENT_PLAY, false));
                }
                return true;
            }
        } else if (isVideoApp(pkgName)) {
            TTSBean ttsBean = VideoControlCenter.getInstance().switchVideoApp(isOpen, pkgName);
            MediaHelper.speak(ttsBean.getSelectTTs());
            return true;
        }
        return false;
    }
    /**
     * 是否是视频应用
     */
    @Override
    public boolean isVideoApp(String pkgName) {
        return IqyImpl.APP_NAME.equals(pkgName) || TencentVideoImpl.APP_NAME.equals(pkgName)
                || MiguImpl.APP_NAME.equals(pkgName) || TiktokImpl.APP_NAME.equals(pkgName)
                || BiliImpl.APP_NAME.equals(pkgName) || ThunderKtvImpl.APP_NAME.equals(pkgName)
                || UsbVideoImpl.APP_NAME.equals(pkgName);
    }

    @Override
    public boolean judgeSupport(String key, String value) {
        return MediaHelper.judgeSupport(key,value);
    }

    @Override
    public void setIdentifier(String identifier) {
        this.mIdentifier = identifier;
    }

    @Override
    public int getAvmStatus() {
        return DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getIntProp(CommonSignal.COMMON_360_STATE);
    }

    @Override
    public boolean getCleanModeStatus() {
        return SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode");
    }

    public Map<String, String> uploadPlayStatus() {
        String mediaSource = "";
        String historyMediaSource = SPUtil.getString(context, MediaConstant.SP_HISTORY_MEDIASOURCE, "");
        boolean isPlaying;
        Map<String, String> foregroundMediaSourceEx = new HashMap<>();

        if (isPlayingAllScreen()) {
            mediaSource = MediaSource.translateMediasource(VoyahMusicImpl.INSTANCE.getSource()).getType();
            isPlaying = true;
        } else if (IqyImpl.INSTANCE.isPlaying() || IqyCopilotImpl.INSTANCE.isPlaying() || IqyCeilingImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.iqy_video.getType();
            isPlaying = true;
        } else if (TencentVideoImpl.INSTANCE.isPlaying() || TencentVideoCopilotImpl.INSTANCE.isPlaying() || TencentVideoCeilingImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.tencent_video.getType();
            isPlaying = true;
        } else if (MiguImpl.INSTANCE.isPlaying() || MiguCopilotImpl.INSTANCE.isPlaying() || MiguCeilingImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.migu_video.getType();
            isPlaying = true;
        } else if (ThunderKtvImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.ls_ktv.getType();
            isPlaying = true;
        } else if (TiktokImpl.INSTANCE.isPlaying() || TiktokCopilotImpl.INSTANCE.isPlaying() || TiktokCeilingImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.tiktok_video.getType();
            isPlaying = true;
        } else if (UsbVideoImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.usb_video.getType();
            isPlaying = true;
        } else if (BiliImpl.INSTANCE.isPlaying() || BiliCopilotImpl.INSTANCE.isPlaying() || BiliCeilingImpl.INSTANCE.isPlaying()) {
            mediaSource = MediaSource.bili_video.getType();
            isPlaying = true;
        } else {
            mediaSource = "";
            isPlaying = false;
        }

        //主屏
        String currentSource0 = MediaSource.translateMediasource(getMediaFrontSourceByDisplayid(UserHandleInfo.central_screen.getDisplayId())).getType();
        foregroundMediaSourceEx.put("central_screen",currentSource0);
        //副屏
        String currentSource1 = MediaSource.translateMediasource(getMediaFrontSourceByDisplayid(UserHandleInfo.passenger_screen.getDisplayId())).getType();
        foregroundMediaSourceEx.put("passenger_screen",currentSource1);
        //后排屏
        String currentSource2 = MediaSource.translateMediasource(getMediaFrontSourceByDisplayid(UserHandleInfo.ceil_screen.getDisplayId())).getType();
        foregroundMediaSourceEx.put("ceil_screen",currentSource2);

        Map<String, String> map = new HashMap<>();
        map.put("mediaSource", mediaSource);
        map.put("foregroundMediaSourceEx", JSON.toJSONString(foregroundMediaSourceEx));
        map.put("isPlaying", String.valueOf(isPlaying));
        map.put("historyMediaSource", historyMediaSource);
        return map;
    }

    @Override
    public boolean getVideoPlayingByDisplayId(DeviceScreenType deviceScreenType) {
        return MediaHelper.getVideoPlayingByDisplayId(deviceScreenType);
    }

    @Override
    public void setTiktokPlayStatus(boolean isPlaying) {

    }

    @Override
    public void gestureTencent() {

    }

    @Override
    public boolean isSafeLimitation() {
        return MediaHelper.isSafeLimitation();
    }

    @Override
    public void speakTts(String tts) {
        //添加TTS回调，用于执行UI销毁
        DeviceHolder.INS().getDevices().getTts().speak(tts,-1, MediaHelper.translateLocation(soundLocation) + 1, new ITtsPlayListener() {
            @Override
            public void onPlayBeginning(String s) {

            }

            @Override
            public void onPlayEnd(String s, int i) {
                UIMgr.INSTANCE.exitState(UIState.STATE_ACTION, mIdentifier);
            }

            @Override
            public void onPlayError(String s, int i) {

            }
        });
    }

    public TTSBean initTargetUserHandle(String queryPosition,String soundLocation) {
        LogUtils.i(TAG,"getTargetUserHandle queryPosition:"+queryPosition+";soundLocation:"+soundLocation);
        String tragetPosition;
        if(!TextUtils.isEmpty(queryPosition)){
            //指定屏幕
            tragetPosition = queryPosition;
            if(MediaHelper.isCeilScreen(tragetPosition)){
                if(!judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL)){
                    //后排屏不支持配置 则播报配置不支持
                    return TtsReplyUtils.getTtsBean("1100028");
                } else {
                    //后排屏支持配置 但未打开 播报后排屏未开
                    if(!MediaHelper.isCeilOpen()){
                        return TtsReplyUtils.getTtsBean("4050039");
                    }
                }
            }
        } else {
            tragetPosition = soundLocation;
            //若声源位置为后排屏，且后排屏未打开，则控制主屏
            if(MediaHelper.isCeilScreen(tragetPosition) && (!judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL) || !MediaHelper.isCeilOpen())){
                tragetPosition = MediaHelper.CENTRAL_SCREEN;
            }
        }

        if(MediaHelper.isPassengerScreen(tragetPosition)){
            userHandle = UserHandleInfo.passenger_screen.getUserHandle();
            displayId = UserHandleInfo.passenger_screen.getDisplayId();
        } else if(MediaHelper.isCeilScreen(tragetPosition)){
            userHandle = UserHandleInfo.ceil_screen.getUserHandle();
            displayId = UserHandleInfo.ceil_screen.getDisplayId();
        } else {
            userHandle = UserHandleInfo.central_screen.getUserHandle();
            displayId = UserHandleInfo.central_screen.getDisplayId();
        }
        initUserHandle(userHandle,displayId);
        return null;
    }

    /**
     * 获取声源位置前台
     * @return
     */
    public String getMediaFrontSource(){
        String topPackageName = MediaHelper.getTopPackageName(displayId);
        LogUtils.i(TAG,"getMediaFrontSource topPackageName:"+topPackageName);
        switch(topPackageName){
            case VoyahMusicImpl.APP_NAME:
                return voyahMusicControlInterface.getMediaUiResource(null);
            case IqyImpl.APP_NAME:
                return MediaSource.iqy_video.getName();
            case TencentVideoImpl.APP_NAME:
                return MediaSource.tencent_video.getName();
            case BiliCeilingImpl.APP_NAME:
                return MediaSource.bili_video.getName();
            case MiguImpl.APP_NAME:
                return MediaSource.migu_video.getName();
            case ThunderKtvImpl.APP_NAME:
                return MediaSource.ls_ktv.getName();
            case TiktokImpl.APP_NAME:
                return MediaSource.tiktok_video.getName();
            case UsbVideoImpl.APP_NAME:
                return MediaSource.usb_video.getName();
        }
        return "";
    }

    /**
     * 指定位置前台
     * @return
     */
    public String getMediaFrontSourceByDisplayid(int displayId){
        String topPackageName = MediaHelper.getTopPackageName(displayId);
        LogUtils.i(TAG,"getMediaFrontSourceByDisplayid topPackageName:"+topPackageName+",displayId:"+displayId);
        switch(topPackageName){
            case VoyahMusicImpl.APP_NAME:
                return voyahMusicControlInterface.getMediaUiResource(null);
            case IqyImpl.APP_NAME:
                return MediaSource.iqy_video.getName();
            case TencentVideoImpl.APP_NAME:
                return MediaSource.tencent_video.getName();
            case BiliCeilingImpl.APP_NAME:
                return MediaSource.bili_video.getName();
            case MiguImpl.APP_NAME:
                return MediaSource.migu_video.getName();
            case ThunderKtvImpl.APP_NAME:
                return MediaSource.ls_ktv.getName();
            case TiktokImpl.APP_NAME:
                return MediaSource.tiktok_video.getName();
            case UsbVideoImpl.APP_NAME:
                return MediaSource.usb_video.getName();
        }
        return "";
    }

    public String getMeidaPlayingSource(){
        if(isPlayingAllScreen()){
            return voyahMusicControlInterface.getSource();
        }
        return "";
    }

    /**
     * 仅播控接口时调用 其他时候不要调用改接口
     * @return
     */
    public boolean isPlayingAllScreen() {
        if(MediaHelper.isPassengerScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen()){
            if(voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.passenger_screen.getUserHandle())){
                userHandle = UserHandleInfo.passenger_screen.getUserHandle();
                displayId = UserHandleInfo.passenger_screen.getDisplayId();
                initUserHandle(userHandle,displayId);
                return true;
            }
            return false;
        }
        if(MediaHelper.isCeilScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()){
            if (voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.ceil_screen.getUserHandle())) {
                userHandle = UserHandleInfo.ceil_screen.getUserHandle();
                displayId = UserHandleInfo.ceil_screen.getDisplayId();
                initUserHandle(userHandle,displayId);
                return true;
            }
            return false;
        }

        if (voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.central_screen.getUserHandle())) {
            userHandle = UserHandleInfo.central_screen.getUserHandle();
            displayId = UserHandleInfo.central_screen.getDisplayId();
            initUserHandle(userHandle,displayId);
            return true;
        } else if (!MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen() && voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.passenger_screen.getUserHandle())) {
            userHandle = UserHandleInfo.passenger_screen.getUserHandle();
            displayId = UserHandleInfo.passenger_screen.getDisplayId();
            initUserHandle(userHandle,displayId);
            return true;
        } else if (!MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen() && voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.ceil_screen.getUserHandle())) {
            userHandle = UserHandleInfo.ceil_screen.getUserHandle();
            displayId = UserHandleInfo.ceil_screen.getDisplayId();
            initUserHandle(userHandle,displayId);
            return true;
        }
        return false;
    }
}
