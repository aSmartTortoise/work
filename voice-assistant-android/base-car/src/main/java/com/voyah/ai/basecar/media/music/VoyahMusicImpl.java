package com.voyah.ai.basecar.media.music;

import android.content.Context;
import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.device.media.VoyahMusicInterface;
import com.voice.sdk.device.media.bean.MediaMusicInfo;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UIState;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.bean.Code;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voyah.ai.basecar.media.bean.PlayMode;
import com.voyah.ai.basecar.media.utils.MediaAudioZoneUtils;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.common.utils.SPUtil;
import com.voyah.ai.sdk.listener.ITtsPlayListener;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public enum VoyahMusicImpl implements VoyahMusicInterface {
    INSTANCE;

    private static final String TAG = VoyahMusicImpl.class.getSimpleName();

    public static final String APP_NAME = "com.voyah.cockpit.voyahmusic";

    private Context context;

    public String identifier = "";

    public static String IS_WY_PLAY = "is_wy_play";

    private final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init");
        this.context = context;
        voyahMusicControlInterface.init(context);
    }

    /**
     *
     * @param position 指定位置
     * @param uiSoundLocation 音源位置
     */
    @Override
    public void initUserHandle(String position,String uiSoundLocation) {
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean pre(String source) {
        String currentSource = getMediaPlayingResource();
        if (currentSource == null && voyahMusicControlInterface.isVoyahMusicFront()) {
            currentSource = getMediaUiResource();
        }
        int code = voyahMusicControlInterface.pre(currentSource);
        if (code == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("1100005");
        } else if (code == Code.NO_PREV_DATA.code() || code == Code.NO_SONG_CAN_PALY.code()) {
            return TtsReplyUtils.getTtsBean("4011802");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean next(String source) {
        String currentSource = getMediaPlayingResource();
        if (currentSource == null && voyahMusicControlInterface.isVoyahMusicFront()) {
            currentSource = getMediaUiResource();
        }
        int code = voyahMusicControlInterface.next(currentSource);
        if (code == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("1100005");
        } else if (code == Code.NO_NEXT_DATA.code() || code == Code.NO_SONG_CAN_PALY.code()) {
            return TtsReplyUtils.getTtsBean("4042203");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean play(String source) {
        LogUtils.d(TAG, "play");
        String currentSource = null;
        if(voyahMusicControlInterface.isVoyahMusicFront()){
            currentSource = getMediaUiResource();
        }
        int ret = voyahMusicControlInterface.play(currentSource);
        if(ret == Code.SUCESS.code()){
            return TtsReplyUtils.getTtsBean("1100005");
        } else if(ret == Code.PLAYING.code()){
            return TtsReplyUtils.getTtsBean("4003902");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay ");
        String currentSource = getMediaPlayingResource();
        if (currentSource == null) {
            currentSource = getMediaUiResource();
        }
        int code = voyahMusicControlInterface.replay(currentSource);
        if(code == Code.SUCESS.code()){
            return TtsReplyUtils.getTtsBean("1100005");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop: " + isExit);
        if (isExit) {
            WyMusicImpl.INSTANCE.switchPage(false, PageId.main.getId());
        }
        if (!isPlaying()) {
            return TtsReplyUtils.getTtsBean("4004001");
        }
        int code = voyahMusicControlInterface.stop(null);
        if (code == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("1100005");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        String currentSource = getMediaPlayingResource();
        if (currentSource == null) {
            currentSource = getMediaUiResource();
        }
        if (TextUtils.equals(seekType, "fast_forward")) {
           return setFastForwardOrRewind(true,duration,currentSource);
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            return setFastForwardOrRewind(false,duration,currentSource);
        } else if (TextUtils.equals(seekType, "set")) {
            return setSeekTo(duration,currentSource);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    private TTSBean setFastForwardOrRewind(boolean isForward,long duration,String source){
        long currentProgress = voyahMusicControlInterface.getProgress(source);
        MediaMusicInfo mediaInfo = voyahMusicControlInterface.getMediaInfo(source);
        if(currentProgress == Code.FAILED.code() || mediaInfo == null || mediaInfo.getPlayType() == 2){
            return TtsReplyUtils.getTtsBean("4003100");
        }

        if(duration <= 0 ){
            //默认快进15s
            duration = 15;
        }
        duration = duration * 1000;

        if(!isForward){
            long progress = currentProgress - duration;
            if (progress < 0) {
                return TtsReplyUtils.getTtsBean("4012203");
            }
            duration = -1 * duration;
        } else {
            long time = mediaInfo.getDuration();
            LogUtils.d(TAG, "total time: " + time + " currentProgress: " + currentProgress);
            if (duration + currentProgress > time) {
                return TtsReplyUtils.getTtsBean("4012203");
            }
        }
        int code = voyahMusicControlInterface.setSeekTo(duration+currentProgress,source);
        if (code == 0) {
            return TtsReplyUtils.getTtsBean("1100005");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    private TTSBean setSeekTo(long duration,String source){
        if (duration < 0) {
            duration = 0;
        }
        duration = duration * 1000;

        MediaMusicInfo mediaInfo = voyahMusicControlInterface.getMediaInfo(source);
        if(mediaInfo == null || mediaInfo.getPlayType() == 2){
            return TtsReplyUtils.getTtsBean("4003100");
        }
        long time = mediaInfo.getDuration();
        LogUtils.d(TAG, "mediaInfo time: " + time);
        if (duration > time) {
            return TtsReplyUtils.getTtsBean("4012203");
        }
        int code = voyahMusicControlInterface.setSeekTo(duration,source);
        if (code == Code.SUCESS.code()) {
            return TtsReplyUtils.getTtsBean("1100005");
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        LogUtils.d(TAG, "switchType: " + switchType + " playMode: " + playMode);
        String currentSource = getMediaPlayingResource();
        if (currentSource == null) {
            currentSource = getMediaUiResource();
        }
        if (MediaSource.yt_music.getName().equals(currentSource) || MediaSource.xmly_music.getName().equals(currentSource) || MediaSource.bt_music.getName().equals(currentSource)) {
            LogUtils.d(TAG, "yt xm or bt not support");
            return TtsReplyUtils.getTtsBean("4003100");
        }

        int currentPlayMode = voyahMusicControlInterface.getPlayMode(currentSource);
        if (currentPlayMode != Code.FAILED.code()) {
            int setPlayMode;
            if (PlayMode.single_cycle.name().equals(playMode) || PlayMode.cycle.name().equals(playMode)) {
                setPlayMode = 0;
            } else if (PlayMode.in_order.name().equals(playMode)) {
                setPlayMode = 2;
            } else if (PlayMode.list_cycle.name().equals(playMode)) {
                setPlayMode = 2;
            } else if (PlayMode.random_play.name().equals(playMode)) {
                setPlayMode = 3;
            } else {
                setPlayMode = -1;
            }
            LogUtils.d(TAG, "setPlayMode: " + setPlayMode);
            if (setPlayMode == -1) {
                return TtsReplyUtils.getTtsBean("4004103");
            }
            if ("open".equals(switchType) || "change".equals(switchType)) {
                if (currentPlayMode == setPlayMode) {
                    return TtsReplyUtils.getTtsBean("4004105");
                } else {
                    int code = voyahMusicControlInterface.setPlayMode(setPlayMode,currentSource);
                    if (code == Code.SUCESS.code()) {
                        return TtsReplyUtils.getTtsBean("1100005");
                    }
                }
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " numberRate: " + numberRate + " level: " + level);
        if (!MediaSource.xmly_music.getName().equals(getSource())) {
            LogUtils.d(TAG, "only xm support");
            return TtsReplyUtils.getTtsBean("4003100");
        }
        return XmMusicImpl.INSTANCE.mediaSpeedAdjustXm(adjustType,numberRate,level);
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        return null;
    }

    @Override
    public TTSBean queryPlayInfo() {
        String source = getSource();
        if (MediaSource.xmly_music.getName().equals(source)) {
            return XmMusicImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.yt_music.getName().equals(source)) {
            return YtMusicImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.qq_music.getName().equals(source)) {
            return QqMusicImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.wy_music.getName().equals(source)) {
            return WyMusicImpl.INSTANCE.queryPlayInfo();
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean jump() {
        return null;
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        LogUtils.d(TAG, "switchLyric: " + isOpen);
        String currentMediaSource = getMediaPlayingResource();
        if(currentMediaSource == null){
            currentMediaSource = getMediaUiResource();
        }
        if(currentMediaSource != null){
            switch (currentMediaSource) {
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.switchLyric(isOpen);
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.switchLyric(isOpen);
                case MediaConstant.TYPE_BT:
                    return BtMusicImpl.INSTANCE.switchLyric(isOpen);
                case MediaConstant.TYPE_USB:
                    return UsbMusicImpl.INSTANCE.switchLyric(isOpen);
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        LogUtils.d(TAG, "switchCollect isCollect: " + isCollect + " mediaType: " + mediaType);
        String currentMediaResource = getMediaPlayingResource();
        if(currentMediaResource == null){
            currentMediaResource = getMediaUiResource();
        }
        if(currentMediaResource != null){
            switch (currentMediaResource) {
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.collect(isCollect);
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.collect(isCollect);
                case MediaConstant.TYPE_YT:
                    return YtMusicImpl.INSTANCE.collect(isCollect);
                case MediaConstant.TYPE_XM:
                    return XmMusicImpl.INSTANCE.collect(isCollect);
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchComment(boolean isComment, String mediaType) {
        return null;
    }

    @Override
    public TTSBean switchLike(boolean isLike, String mediaType) {
        return null;
    }

    @Override
    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        return null;
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        LogUtils.d(TAG, "switchPlayList: " + isOpen);
        String currentMediaResource = getMediaPlayingResource();
        if(currentMediaResource == null){
            currentMediaResource = getMediaUiResource();
        }
        if(currentMediaResource != null){
            switch (currentMediaResource) {
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.switchPlayList(isOpen);
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.switchPlayList(isOpen);
                case MediaConstant.TYPE_XM:
                    return XmMusicImpl.INSTANCE.switchPlayList(isOpen);
                case MediaConstant.TYPE_YT:
                    return YtMusicImpl.INSTANCE.switchPlayList(isOpen);
                case MediaConstant.TYPE_USB:
                    return UsbMusicImpl.INSTANCE.switchPlayList(isOpen);
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    public TTSBean switchPlayPage(boolean isOpen) {
        LogUtils.d(TAG, "switchPlayPage: " + isOpen);
        // TODO: 2024/10/31 close未处理
        if (isOpen) {
            String currentMediaResource = getMediaPlayingResource();
            if(currentMediaResource  != null){
                switch (currentMediaResource) {
                    case MediaConstant.TYPE_QQ:
                        return QqMusicImpl.INSTANCE.switchPlayPage(isOpen);
                    case MediaConstant.TYPE_WY:
                        return WyMusicImpl.INSTANCE.switchPlayPage(isOpen);
                    case MediaConstant.TYPE_XM:
                        return XmMusicImpl.INSTANCE.switchPlayPage(isOpen);
                    case MediaConstant.TYPE_YT:
                        return YtMusicImpl.INSTANCE.switchPlayPage(isOpen);
                    case MediaConstant.TYPE_USB:
                        return UsbMusicImpl.INSTANCE.switchPlayPage(isOpen);
                    case MediaConstant.TYPE_BT:
                        return BtMusicImpl.INSTANCE.switchPlayPage(isOpen);
                }
            }
            return TtsReplyUtils.getTtsBean("4003100");
        } else {
            if (voyahMusicControlInterface.isVoyahMusicFront()) {
                WyMusicImpl.INSTANCE.switchPage(false, PageId.main_play_page.getId());
                return TtsReplyUtils.getTtsBean("4004800");
            } else {
                return TtsReplyUtils.getTtsBean("4004801");
            }
        }
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        String currentMediaResource = getMediaPlayingResource();
        if (currentMediaResource != null) {
            switch (currentMediaResource) {
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.switchPage(isOpen, PageId.QQ_main.getId());
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.switchPage(isOpen, PageId.wy_main.getId());
                case MediaConstant.TYPE_XM:
                    return XmMusicImpl.INSTANCE.switchPage(isOpen, PageId.xm_main.getId());
                case MediaConstant.TYPE_YT:
                    return YtMusicImpl.INSTANCE.switchPage(isOpen, PageId.yt_main.getId());
                case MediaConstant.TYPE_USB:
                    return UsbMusicImpl.INSTANCE.switchPage(isOpen, PageId.usb_main.getId());
                case MediaConstant.TYPE_BT:
                    return BtMusicImpl.INSTANCE.switchPage(isOpen, PageId.bt_main.getId());
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return null;
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen,String appName) {
        LogUtils.d(TAG, "switchHistoryUI: " + isOpen);
        String currentMediaResource;
        if(appName == null){
            currentMediaResource = getMediaUiResource() ;
            if(currentMediaResource == null){
                currentMediaResource = getMediaPlayingResource();
            }
        } else {
            currentMediaResource = getMediaResourceByName(appName);
        }

        if(currentMediaResource != null){
            switch (currentMediaResource){
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.switchHistoryUI(isOpen);
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.switchHistoryUI(isOpen);
                case  MediaConstant.TYPE_XM:
                    return XmMusicImpl.INSTANCE.switchHistoryUI(isOpen);
                case MediaConstant.TYPE_YT:
                    return YtMusicImpl.INSTANCE.switchHistoryUI(isOpen);

            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen,String appName) {
        LogUtils.d(TAG, "switchCollectUI: " + isOpen);
        String currentMediaResource;
        if(appName == null){
            currentMediaResource = getMediaUiResource() ;
            if(currentMediaResource == null ){
                currentMediaResource = getMediaPlayingResource();
            }
        } else {
            currentMediaResource = getMediaResourceByName(appName);
        }

        if(currentMediaResource != null){
            switch (currentMediaResource){
                case MediaConstant.TYPE_QQ:
                    return QqMusicImpl.INSTANCE.switchCollectUI(isOpen);
                case MediaConstant.TYPE_WY:
                    return WyMusicImpl.INSTANCE.switchCollectUI(isOpen);
                case  MediaConstant.TYPE_XM:
                    return XmMusicImpl.INSTANCE.switchCollectUI(isOpen);
                case MediaConstant.TYPE_YT:
                    return YtMusicImpl.INSTANCE.switchCollectUI(isOpen);
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean playUI(int type) {
        if (type == PageId.QQ_history.getId()) {
            QqMusicImpl.INSTANCE.playHistory();
        } else if (type == PageId.QQ_collect.getId()) {
           QqMusicImpl.INSTANCE.playCollect();
        } else if (type == PageId.wy_collect.getId()) {
            WyMusicImpl.INSTANCE.playCollect();
        } else if (type == PageId.wy_history.getId()) {
            WyMusicImpl.INSTANCE.playHistory();
        } else if (type == PageId.xm_history.getId()) {
            XmMusicImpl.INSTANCE.playHistory();
        } else if (type == PageId.xm_collect.getId()) {
           XmMusicImpl.INSTANCE.playCollect();
        } else if (type == PageId.yt_history.getId()) {
            YtMusicImpl.INSTANCE.playHistory();
        } else if (type == PageId.yt_collect.getId()) {
            YtMusicImpl.INSTANCE.playCollect();
        }
        return null;
    }

    @Override
    public TTSBean playMusic(String appName) {
        LogUtils.d(TAG, "playRecommend: " + appName);
        if (TextUtils.equals("网易云音乐", appName) || TextUtils.equals("岚图音乐", appName)) {
            return WyMusicImpl.INSTANCE.playMusic();
        } else if (TextUtils.equals("qq音乐", appName)) {
           return QqMusicImpl.INSTANCE.playMusic();
        } else if (TextUtils.equals("喜马拉雅", appName)) {
            return XmMusicImpl.INSTANCE.playMusic();
        } else if (TextUtils.equals("云听", appName)) {
            return YtMusicImpl.INSTANCE.playMusic();
        } else if (TextUtils.equals("USB音乐", appName)){
            return UsbMusicImpl.INSTANCE.playMusic();
        } else if(TextUtils.equals("蓝牙音乐", appName)){
            return BtMusicImpl.INSTANCE.playMusic();
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        if ((!TextUtils.isEmpty(appName) && appName.equals("喜马拉雅")) || (TextUtils.isEmpty(appName) && "audio_book".equals(mediaType)) || (TextUtils.isEmpty(appName) && "audio".equals(mediaType) && "节目".equals(mediaTypeDetail))) {
            return XmMusicImpl.INSTANCE.playSearch(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
        } else if ((!TextUtils.isEmpty(appName) && appName.equals("云听")) || (TextUtils.isEmpty(appName) && "radio".equals(mediaType))) {
            return YtMusicImpl.INSTANCE.playSearch(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
        } else {
            if (appName.contains("岚图音乐")|| NumberUtils.areAllStringsEmpty(mediaArtist, mediaStyle, mediaName, mediaLan, mediaAlbum, mediaMovie, mediaRank, mediaVersion, mediaOffset, mediaDate)) {
                if (TextUtils.isEmpty(playMode)) {
                    boolean isQQ = !isOpenWy(null);
                    return isQQ ? QqMusicImpl.INSTANCE.playMusic() : WyMusicImpl.INSTANCE.playMusic();
                } else {
                    switchPlayMode("open", playMode);
                }
                return TtsReplyUtils.getTtsBean("1100005");
            }
            if (isOpenWy(appName)) {
                if("每日推荐".equals(mediaName) || "每日推荐".equals(mediaStyle)){
                    return WyMusicImpl.INSTANCE.playRecommend();
                }
                return WyMusicImpl.INSTANCE.playSearch(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
            } else {
                if("每日推荐".equals(mediaName) || "每日推荐".equals(mediaStyle)){
                    return QqMusicImpl.INSTANCE.playRecommend();
                }
                return QqMusicImpl.INSTANCE.playSearch(mediaType, mediaTypeDetail, appName, mediaSource, mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaOffset, mediaUi, mediaRank, playMode);
            }
        }
    }

    public TTSBean open(boolean isOpen, String type, boolean isStop, String position, String queryPosition) {
        LogUtils.d(TAG, "open isOpen: " + isOpen + " type: " + type);
        if (!isOpen) {
            if (isPlaying() && TextUtils.equals(type, getSource())) {
                stop(false);
            }
        }
        if (MediaSource.bt_music.getName().equals(type)) {
            if(DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()){
                if (!TextUtils.isEmpty(queryPosition)) {
                    if (MediaHelper.isPassengerScreen(queryPosition)) {
                        return isOpen ? TtsReplyUtils.getTtsBean("1100034", "@{screen_name}", "副驾屏", "@{app_name}", "蓝牙音乐")
                                : TtsReplyUtils.getTtsBean("1100028");
                    } else if (MediaHelper.isCeilScreen(queryPosition)) {
                        return isOpen ? TtsReplyUtils.getTtsBean("1100034", "@{screen_name}", "后排屏", "@{app_name}", "蓝牙音乐")
                                : TtsReplyUtils.getTtsBean("1100028");
                    } else {
                        return BtMusicImpl.INSTANCE.mediaSwitch(isOpen);
                    }
                } else {
                    if (!MediaHelper.judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, "ceil_screen") && MediaHelper.isCeilScreen(position)) {
                        return BtMusicImpl.INSTANCE.mediaSwitch(isOpen);
                    } else {
                        if((MediaHelper.isCeilScreen(position) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()) || (MediaHelper.isPassengerScreen(position) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen())){
                            return TtsReplyUtils.getTtsBeanText("当前屏幕还不支持这个应用");
                        }
                        if(isOpen){
                            if (MediaHelper.isPassengerScreen(position)) {
                                BtMusicImpl.INSTANCE.mediaSwitch(true);
                                return TtsReplyUtils.getTtsBean("4050070", "@{screen_name}", "副驾屏", "@{app_name}", "蓝牙音乐");
                            } else if (MediaHelper.isCeilScreen(position)) {
                                BtMusicImpl.INSTANCE.mediaSwitch(true);
                                return TtsReplyUtils.getTtsBean("4050070", "@{screen_name}", "后排屏", "@{app_name}", "蓝牙音乐");
                            } else {
                                return BtMusicImpl.INSTANCE.mediaSwitch(true);
                            }
                        } else {
                            return BtMusicImpl.INSTANCE.mediaSwitch(false);
                        }
                    }
                }
            } else {
                return BtMusicImpl.INSTANCE.mediaSwitch(isOpen);
            }
        } else if (MediaSource.usb_music.getName().equals(type)) {
            if(DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()){
                if (!TextUtils.isEmpty(queryPosition)) {
                    if (MediaHelper.isPassengerScreen(queryPosition)) {
                        return isOpen ? TtsReplyUtils.getTtsBean("1100034", "@{screen_name}", "副驾屏", "@{app_name}", "USB音乐")
                                : TtsReplyUtils.getTtsBean("1100028");
                    } else if (MediaHelper.isCeilScreen(queryPosition)) {
                        return isOpen ? TtsReplyUtils.getTtsBean("1100034", "@{screen_name}", "后排屏", "@{app_name}", "USB音乐")
                                : TtsReplyUtils.getTtsBean("1100028");
                    } else {
                        return UsbMusicImpl.INSTANCE.mediaSwitch(isOpen);
                    }
                } else {
                    if (!MediaHelper.judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, "ceil_screen") && MediaHelper.isCeilScreen(position)) {
                        return UsbMusicImpl.INSTANCE.mediaSwitch(isOpen);
                    } else {
                        if((MediaHelper.isCeilScreen(position) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()) || (MediaHelper.isPassengerScreen(position) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen())){
                            return TtsReplyUtils.getTtsBeanText("当前屏幕还不支持这个应用");
                        }
                        if(isOpen){
                            if (MediaHelper.isPassengerScreen(position)) {
                                UsbMusicImpl.INSTANCE.mediaSwitch(true);
                                return TtsReplyUtils.getTtsBean("4050070", "@{screen_name}", "副驾屏", "@{app_name}", "USB音乐");
                            } else if (MediaHelper.isCeilScreen(position)) {
                                UsbMusicImpl.INSTANCE.mediaSwitch(true);
                                return TtsReplyUtils.getTtsBean("4050070", "@{screen_name}", "后排屏", "@{app_name}", "USB音乐");
                            } else {
                                return UsbMusicImpl.INSTANCE.mediaSwitch(true);
                            }
                        } else {
                            return UsbMusicImpl.INSTANCE.mediaSwitch(false);
                        }
                    }
                }
            } else {
                return UsbMusicImpl.INSTANCE.mediaSwitch(isOpen);
            }
        } else if (MediaSource.qq_music.getName().equals(type)) {
            return QqMusicImpl.INSTANCE.mediaSwitch(isOpen);
        } else if (MediaSource.wy_music.getName().equals(type)) {
            return WyMusicImpl.INSTANCE.mediaSwitch(isOpen);
        } else if (MediaSource.yt_music.getName().equals(type)) {
            return YtMusicImpl.INSTANCE.mediaSwitch(isOpen);
        } else if (MediaSource.xmly_music.getName().equals(type)) {
            return XmMusicImpl.INSTANCE.mediaSwitch(isOpen);
        } else if (MediaSource.yt_broadcast.getName().equals(type)) {
            return YtMusicImpl.INSTANCE.mediaSwitchBroadcastYt(isOpen);
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    public int getPageState(int pageId) {
        return voyahMusicControlInterface.getPageState(pageId);
    }

    public boolean isOpenWy(String appName) {
        LogUtils.d(TAG, "isOpenWy: " + appName);
        if (!TextUtils.isEmpty(appName)) {
            if (isMediaByAppName(appName, MediaSource.wy_music.getName())) {
                return true;
            } else if (isMediaByAppName(appName, MediaSource.qq_music.getName())) {
                return false;
            }
        }
        int pref = SettingsManager.get().getMusicPreference();
        LogUtils.d(TAG, "pref: " + pref);
        if (pref == 1) {
            return false;
        } else if (pref == 2) {
            return true;
        } else {
            if (getPageState(PageId.QQ_main.getId()) == 1) {
                return false;
            } else if (getPageState(PageId.wy_main.getId()) == 1) {
                return true;
            } else {
                boolean isPlay = isPlaying();
                String source = getSource();
                if (MediaSource.qq_music.getName().equals(source) && isPlay) {
                    return false;
                } else if (MediaSource.wy_music.getName().equals(source) && isPlay) {
                    return true;
                }
            }
            return SPUtil.getBoolean(context, IS_WY_PLAY, false);
        }
    }

    public boolean isLogin(String appName) {
        LogUtils.d(TAG, "appName: " + appName);
        if (MediaSource.qq_music.getName().equals(appName)) {
            return QqMusicImpl.INSTANCE.isLogin();
        } else if (MediaSource.wy_music.getName().equals(appName)) {
            return WyMusicImpl.INSTANCE.isLogin();
        } else if (MediaSource.xmly_music.getName().equals(appName)) {
            return XmMusicImpl.INSTANCE.isLogin();
        } else if (MediaSource.yt_music.getName().equals(appName)) {
            return YtMusicImpl.INSTANCE.isLogin();
        }
        return false;
    }

    public TTSBean openMediaAppByAppName(String appName,boolean isOpen) {
        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.wy_music.getName())) {
            return WyMusicImpl.INSTANCE.switchPage(isOpen, PageId.wy_main.getId());
        }

        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.yt_music.getName())) {
            return YtMusicImpl.INSTANCE.switchPage(isOpen, PageId.yt_main.getId());
        }

        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.xmly_music.getName())) {
            return XmMusicImpl.INSTANCE.switchPage(isOpen, PageId.xm_main.getId());
        }

        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.voyah_music.getName())) {
            if(isOpen){
                return isOpenWy(appName) ?
                        WyMusicImpl.INSTANCE.switchPage(true, PageId.main.getId()):
                        QqMusicImpl.INSTANCE.switchPage(true, PageId.main.getId());
            } else {
                return WyMusicImpl.INSTANCE.switchPage(false, PageId.main.getId());
            }

        }

        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.qq_music.getName())) {
            return QqMusicImpl.INSTANCE.switchPage(isOpen, PageId.QQ_main.getId());
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    public boolean isMedia(String appName) {
        return MediaHelper.isMusicApp(appName);
    }

    public boolean isMediaByAppName(String appName, String source) {
        return MediaHelper.isMediaByAppName(appName,source);
    }

    public boolean isPlaying() {
        return voyahMusicControlInterface.isPlaying();
    }

    public String getSource() {
        return voyahMusicControlInterface.getSource();
    }

    /**
     * @return显示在前台的音源
     */
    public String getMediaUiResource(){
        if(voyahMusicControlInterface.isVoyahMusicFront()){
            return voyahMusicControlInterface.getMediaUiResource(null);
        }
        return null;
    }

    /**
     * @return 正在播放的音源
     */
    public String getMediaPlayingResource(){
        if (isPlaying()) {
            String source = getSource();
            if (MediaSource.qq_music.getName().equals(source)) {
                return MediaConstant.TYPE_QQ;
            } else if (MediaSource.wy_music.getName().equals(source)) {
                return MediaConstant.TYPE_WY;
            } else if (MediaSource.yt_music.getName().equals(source)) {
                return MediaConstant.TYPE_YT;
            } else if (MediaSource.xmly_music.getName().equals(source)) {
                return MediaConstant.TYPE_XM;
            } else if (MediaSource.bt_music.getName().equals(source)) {
                return MediaConstant.TYPE_BT;
            } else if (MediaSource.usb_music.getName().equals(source)) {
                return MediaConstant.TYPE_USB;
            }
        }
        return null;
    }

    /**
     * @return appname对应的音源
     */
    public String getMediaResourceByName(String appName){
        LogUtils.i(TAG,"getMediaResourceByName :"+appName);
        if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.voyah_music.getName())) {
            return isOpenWy(appName) ? MediaConstant.TYPE_WY : MediaConstant.TYPE_QQ;
        } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.wy_music.getName())) {
            return MediaConstant.TYPE_WY;
        } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.qq_music.getName())) {
            return MediaConstant.TYPE_QQ;
        } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.yt_music.getName())) {
            return MediaConstant.TYPE_YT;
        } else if (VoyahMusicImpl.INSTANCE.isMediaByAppName(appName, MediaSource.xmly_music.getName())) {
            return MediaConstant.TYPE_XM;
        }
        return null;
    }
}
