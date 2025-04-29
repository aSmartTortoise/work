package com.voyah.ai.basecar.media.utils;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;
import com.mega.nexus.content.MegaContext;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.basecar.media.vedio.IqyImpl;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.basecar.media.vedio.UsbVideoImpl;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.domains.media.VideoInfo;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

public class VideoControlCenter {

    private int currentDisplayId;
    private String currentSource;

    public static final int FREE_FORM_ACTION_NONE = 0;//不做处理
    public static final int FREE_FORM_ACTION_ENTER = 1; // 进入自由窗口
    public static final int FREE_FORM_ACTION_QUIT = 2;//退出自由窗口,可重复调用,系统判断了如果task不在freeform状态,不做处理
    public static final String FREE_FORM_INTENT_KEY = "free_form_action"; //intent参数传递的key

    private static final String TAG = VideoControlCenter.class.getSimpleName();

    public static class MediaStatus {
        public static final int PLAYING = 344;
        public static final int PLAY_PAGE = 998;
        public static final int IS_FRONT = 2;
    }

    private VideoControlCenter(){
    }

    public int getCurrentDisplayId(){
        if (MediaHelper.isSupportMultiScreen()) {
            return currentDisplayId;
        } else {
            return MediaHelper.getMainScreenDisplayId();
        }
    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentDisplayId(int displayId){
        currentDisplayId = displayId;
    }

    private static VideoControlCenter mVideoControlCenter;

    public static VideoControlCenter getInstance(){
        if (mVideoControlCenter == null) {
            synchronized (VideoControlCenter.class) {
                if (mVideoControlCenter == null) {
                    mVideoControlCenter = new VideoControlCenter();
                }
            }
        }
        return mVideoControlCenter;
    }

    /**
     * 获取需要执行的应用及屏幕
     */
    public boolean isVideoStatus(String soundLocation,int mediaStatus){
        //1.如果连接声源位置连接蓝牙，此位置相应,仅吸顶屏和副驾屏可连接蓝牙,目前1.5需求 56D首发
        if (MediaHelper.isH56D()) {
            if (MediaHelper.isPassengerScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen()) {
                currentDisplayId = MediaHelper.getPassengerScreenDisplayId();
                return getCurrentDisplayIdAndSource(currentDisplayId,mediaStatus);
            }
            if (MediaHelper.isCeilScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()) {
                currentDisplayId = MediaHelper.getCeilingScreenDisplayId();
                return getCurrentDisplayIdAndSource(currentDisplayId,mediaStatus);
            }
        }
        //2.获取播放中/播放页的视频资源
        return getCurrentDisplayIdAndSource(MediaHelper.getCeilingScreenDisplayId(),mediaStatus)
                || getCurrentDisplayIdAndSource(MediaHelper.getPassengerScreenDisplayId(),mediaStatus)
                || getCurrentDisplayIdAndSource(MediaHelper.getMainScreenDisplayId(),mediaStatus);
    }

    private boolean getCurrentDisplayIdAndSource(int displayId,int type){
        String mediaSource = "";
        switch (type){
            case MediaStatus.PLAYING:
                mediaSource = getMediaPlayingByDisplayId(displayId);
                break;
            case MediaStatus.PLAY_PAGE:
                mediaSource = getMediaPlayPageByDisplayId(displayId);
                break;
            case MediaStatus.IS_FRONT:
                mediaSource = getMediaIsFrontByDisplayId(displayId);
                break;
        }
        if (StringUtils.isNotBlank(mediaSource)) {
            currentDisplayId = displayId;
            currentSource = mediaSource;
            return true;
        }
        return false;
    }

    /**
     * 是否是视频同看中
     * @return
     */
    public boolean isVideoMirror() {
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        if (StringUtils.isNotBlank(mirrorPackage)) {
            currentDisplayId = MirrorServiceManager.INSTANCE.getSourceScreen();
            if (TencentVideoImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.tencent_video.getName();
            } else if (IqyImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.iqy_video.getName();
            } else if (MiguImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.migu_video.getName();
            } else if (BiliImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.bili_video.getName();
            } else if (TiktokImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.tiktok_video.getName();
            } else if (UsbVideoImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.usb_video.getName();
            }
            return true;
        }
        return false;
    }

    /**
     * 根据displayId获取正在播放的音源
     *
     * @param displayId
     * @return
     */
    public String getMediaPlayingByDisplayId(int displayId) {
        if (isPlaying(IqyImpl.APP_NAME,displayId)) {
            return MediaSource.iqy_video.getName();
        } else if (MiguImpl.INSTANCE.isPlaying(displayId)) {
            return MediaSource.migu_video.getName();
        } else if (isPlaying(TencentVideoImpl.APP_NAME,displayId)) {
            return MediaSource.tencent_video.getName();
        } else if (BiliImpl.INSTANCE.isPlaying(displayId)) {
            return MediaSource.bili_video.getName();
        } else if (TiktokImpl.INSTANCE.isPlaying(displayId)) {
            return MediaSource.tiktok_video.getName();
        } else if (UsbVideoImpl.INSTANCE.isPlayPage(displayId) && UsbVideoImpl.INSTANCE.isPlaying()) {
            return MediaSource.usb_video.getName();
        } else if (MediaHelper.isAppForeGround(ThunderKtvImpl.APP_NAME, displayId) && ThunderKtvImpl.INSTANCE.isPlaying()) {
            return MediaSource.ls_ktv.getName();
        } else {
            return "";
        }
    }

    /**
     * 根据displayId获取播放页应用
     * @param displayId
     * @return
     */
    public String getMediaPlayPageByDisplayId(int displayId) {
        if (IqyImpl.INSTANCE.isPlayPage(displayId)) {
            return MediaSource.iqy_video.getName();
        } else if (MiguImpl.INSTANCE.isPlayPage(displayId)) {
            return MediaSource.migu_video.getName();
        } else if (TencentVideoImpl.INSTANCE.isPlayPage(displayId)) {
            return MediaSource.tencent_video.getName();
        } else if (BiliImpl.INSTANCE.isPlayPage(displayId)) {
            return MediaSource.bili_video.getName();
        } else if (TiktokImpl.INSTANCE.isFrontByDisplayid(displayId)) {
            return MediaSource.tiktok_video.getName();
        } else if (UsbVideoImpl.INSTANCE.isPlayPage(displayId)) {
            return MediaSource.usb_video.getName();
        } else if (MediaHelper.isAppForeGround(ThunderKtvImpl.APP_NAME, displayId)) {
            return MediaSource.ls_ktv.getName();
        } else {
            return "";
        }
    }

    /**
     * 根据displayId获取前台应用
     * @param displayId
     * @return
     */
    public String getMediaIsFrontByDisplayId(int displayId) {
        if (MediaHelper.isAppForeGround(IqyImpl.APP_NAME, displayId)) {
            return MediaSource.iqy_video.getName();
        } else if (MediaHelper.isAppForeGround(MiguImpl.APP_NAME, displayId)) {
            return MediaSource.migu_video.getName();
        } else if (MediaHelper.isAppForeGround(TencentVideoImpl.APP_NAME, displayId)) {
            return MediaSource.tencent_video.getName();
        } else if (MediaHelper.isAppForeGround(BiliImpl.APP_NAME, displayId)) {
            return MediaSource.bili_video.getName();
        } else if (MediaHelper.isAppForeGround(TiktokImpl.APP_NAME, displayId)) {
            return MediaSource.tiktok_video.getName();
        } else if (MediaHelper.isAppForeGround(UsbVideoImpl.APP_NAME, displayId)) {
            return MediaSource.usb_video.getName();
        } else if (MediaHelper.isAppForeGround(ThunderKtvImpl.APP_NAME, displayId)) {
            return MediaSource.ls_ktv.getName();
        } else {
            return "";
        }
    }

    /**
     * 通过媒体中心获取播放状态
     * @param pkgName
     * @param displayId
     * @return
     */
    public boolean isPlaying(String pkgName, int displayId) {
        if (MediaHelper.isH56D()) {
            return DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl().isplayingBySource(pkgName, MediaHelper.getUserHandleByDisplayId(displayId));
        } else {
            boolean isPlaying = false;
            switch (pkgName) {
                case IqyImpl.APP_NAME:
                    isPlaying = IqyImpl.INSTANCE.isPlaying(displayId);
                    break;
                case MiguImpl.APP_NAME:
                    isPlaying = MiguImpl.INSTANCE.isPlaying(displayId);
                    break;
                case TencentVideoImpl.APP_NAME:
                    isPlaying = TencentVideoImpl.INSTANCE.isPlaying(displayId);
                    break;
                case BiliImpl.APP_NAME:
                    isPlaying = BiliImpl.INSTANCE.isPlaying(displayId);
                    break;
                case TiktokImpl.APP_NAME:
                    isPlaying = TiktokImpl.INSTANCE.isPlaying(displayId);
                    break;
                case UsbVideoImpl.APP_NAME:
                    isPlaying = UsbVideoImpl.INSTANCE.isPlaying();
                    break;
                case ThunderKtvImpl.APP_NAME:
                    isPlaying = ThunderKtvImpl.INSTANCE.isPlaying();
                    break;
            }
            return isPlaying;
        }
    }

    /**
     * 通过媒体中心获取的音源，判断是否是视频音源
     * @param source
     * @return
     */
    public boolean isVideoSource(String source){
        currentSource = source;
        return MediaSource.iqy_video.getName().equals(source)
                || MediaSource.migu_video.getName().equals(source)
                || MediaSource.tencent_video.getName().equals(source)
                || MediaSource.bili_video.getName().equals(source)
                || MediaSource.tiktok_video.getName().equals(source)
                || MediaSource.usb_video.getName().equals(source)
                || MediaSource.ls_ktv.getName().equals(source);
    }

    public TTSBean switchDanmaku(boolean isOpen) {
        LogUtils.d(TAG, "switchDanmaku currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isOpen = " + isOpen);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchDanmaku(isOpen);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchDanmaku(isOpen);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchDanmaku(isOpen);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchDanmaku(isOpen);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchDanmaku(isOpen);
        }
        return null;
    }

    public TTSBean seek(String seekType, long duration){
        LogUtils.d(TAG, "seek currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", seekType = " + seekType + ", duration = " + duration);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.seek(seekType, duration);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.seek(seekType, duration);
        }
        return null;
    }

    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "speed currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", adjustType = " + adjustType + ", numberRate = " + numberRate + ", level = " + level);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.speed(adjustType, numberRate, level);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.speed(adjustType, numberRate, level);
        }
        return null;
    }

    public TTSBean definition(String adjustType, String mode, String level) {
        LogUtils.d(TAG, "definition currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", adjustType = " + adjustType + ", mode = " + mode + ", level = " + level);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.definition(adjustType, mode, level);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.definition(adjustType, mode, level);
        }
        return null;
    }

    public TTSBean queryPlayInfo() {
        LogUtils.d(TAG, "queryPlayInfo currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.queryPlayInfo();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.queryPlayInfo();
        }
        return null;
    }

    public TTSBean jump() {
        LogUtils.d(TAG, "jump currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.jump();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.jump();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.jump();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.jump();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.jump();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.jump();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.jump();
        }
        return null;
    }

    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        LogUtils.d(TAG, "switchCollect currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isCollect = " + isCollect + ", mediaType = " + mediaType);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchCollect(isCollect, mediaType);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchCollect(isCollect, mediaType);
        }
        return null;
    }

    public TTSBean switchComment(boolean isComment, String mediaType) {
        LogUtils.d(TAG, "switchComment currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isComment = " + isComment + ", mediaType = " + mediaType);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchComment(isComment, mediaType);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchComment(isComment, mediaType);
        }
        return null;
    }

    public TTSBean switchLike(boolean isLike, String mediaType) {
        LogUtils.d(TAG, "switchLike currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isLike = " + isLike + ", mediaType = " + mediaType);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchLike(isLike, mediaType);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchLike(isLike, mediaType);
        }
        return null;
    }

    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        LogUtils.d(TAG, "switchAttention currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isAttention = " + isAttention + ", mediaType = " + mediaType);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchAttention(isAttention, mediaType);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchAttention(isAttention, mediaType);
        }
        return null;
    }

    public TTSBean switchPlayer(boolean isOpen) {
        LogUtils.d(TAG, "switchPlayer currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isOpen = " + isOpen);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchPlayer(isOpen);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchPlayer(isOpen);
        }
        return null;
    }

    public TTSBean switchOriginalSinging(boolean isOriginal) {
        LogUtils.d(TAG, "switchOriginalSinging currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isOriginal = " + isOriginal);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.switchOriginalSinging(isOriginal);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.switchOriginalSinging(isOriginal);
        }
        return null;
    }

    public TTSBean playUI(int type) {
        LogUtils.d(TAG, "playUI currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", type = " + type);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.playUI(type);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            MiguImpl.INSTANCE.playUI(type);
            return null;
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.playUI(type);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            BiliImpl.INSTANCE.playUI(type);
            return null;
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.playUI(type);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.playUI(type);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.playUI(type);
        }
        return null;
    }

    public TTSBean playEpisode(int playEpisode) {
        LogUtils.d(TAG, "playEpisode currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", playEpisode = " + playEpisode);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.playEpisode(playEpisode);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MediaHelper.getNotSupportTts();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.playEpisode(playEpisode);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            BiliImpl.INSTANCE.playEpisode(playEpisode);
            return null;
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return MediaHelper.getNotSupportTts();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return MediaHelper.getNotSupportTts();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return MediaHelper.getNotSupportTts();
        }
        return null;
    }

    public TTSBean switchVideoApp(boolean isOpen, String pkgName) {
        if (StringUtils.isBlank(pkgName)) {
            pkgName = getPkgNameBySource();
        }
        LogUtils.d(TAG, "switchVideoApp currentDisplayId = " + currentDisplayId + ", isOpen = " + isOpen + ", pkgName = " + pkgName);
        if (StringUtils.isBlank(pkgName)) {
            LogUtils.d(TAG, "pkgName is null");
            return null;
        }
        TTSBean ttsBean;
        DeviceScreenType deviceScreenTypeByDisplayId = MediaHelper.getDeviceScreenTypeByDisplayId(currentDisplayId);
        if (isOpen) {
            if (MediaHelper.isAppForeGround(pkgName, currentDisplayId)) {
                ttsBean = MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaHelper.getAppName(pkgName), MediaHelper.getScreenName(currentDisplayId));
            } else {
                if (MediaHelper.isSafeLimitation()) {
                    ttsBean = TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
                } else {
                    //判断目标包名是否是支持同看的应用,如果是同看应用，需要做如下适配
                    if (MirrorServiceManager.INSTANCE.isAllowMirroredApps(pkgName)) {
                        //启动屏id
                        boolean handled = MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(pkgName, currentDisplayId);
                        //如果返回true，说明此事件被多屏同看接管
                        if (!handled) {
                            //handled=false 说明多屏同看不处理此事件， 正常启动
                            openApp(pkgName);
                        }
                    } else {
                        // 非支持同看的应用，正常启动
                        openApp(pkgName);
                    }
                    ttsBean = MediaTtsManager.getInstance().getOpenAppTts(MediaHelper.getAppName(pkgName), MediaHelper.getScreenName(currentDisplayId));
                }
            }
        } else {
            if (MediaHelper.isAppForeGround(pkgName, currentDisplayId)) {
                if (TencentVideoImpl.APP_NAME.equals(pkgName)) {
                    TencentVideoImpl.INSTANCE.closeApp();
                } else {
                    MediaHelper.closeApp(pkgName, deviceScreenTypeByDisplayId);
                }
                ttsBean = MediaTtsManager.getInstance().getCloseAppTts(MediaHelper.getAppName(pkgName), MediaHelper.getScreenName(currentDisplayId));
            } else {
                if (MediaHelper.isMirrorAppFront(pkgName)) {
                    MediaHelper.backToHome(deviceScreenTypeByDisplayId);
                    ttsBean = MediaTtsManager.getInstance().getCloseAppTts(MediaHelper.getAppName(pkgName), MediaHelper.getScreenName(currentDisplayId));
                } else {
                    ttsBean = MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaHelper.getAppName(pkgName), MediaHelper.getScreenName(currentDisplayId));
                }
            }
        }
        return ttsBean;
    }

    private void openApp(String packageName){
        DeviceScreenType deviceScreenType = MediaHelper.getDeviceScreenTypeByDisplayId(currentDisplayId);

        PackageManager packageManager = ContextUtils.getAppContext().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            LogUtils.i(TAG, "not find launch intent");
            return;
        }
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(deviceScreenType, "");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(currentDisplayId).toBundle();
        if (MediaHelper.isH56D() && currentDisplayId == MediaHelper.getMainScreenDisplayId()
                && (MediaHelper.isGear(GearInfo.CARSET_GEAR_DRIVING) || MediaHelper.isGear(GearInfo.CARSET_GEAR_PARKING))) {
            LogUtils.d(TAG, "openApp D挡 = " + MediaHelper.isGear(GearInfo.CARSET_GEAR_DRIVING) + ", R挡 = " + MediaHelper.isGear(GearInfo.CARSET_GEAR_PARKING));
            intent.setComponent(new ComponentName(packageName, intent.getComponent() != null ? intent.getComponent().getClassName() : ""));
            intent.putExtra(FREE_FORM_INTENT_KEY, MediaHelper.isGear(GearInfo.CARSET_GEAR_DRIVING) ? FREE_FORM_ACTION_ENTER : FREE_FORM_ACTION_QUIT);
            Utils.getApp().startActivity(intent, bundle);
        } else {
            if (UsbVideoImpl.APP_NAME.equals(packageName)) {
                Utils.getApp().startActivity(intent, bundle);
            } else {
                MegaContext.startActivityAsUser(Utils.getApp(), intent, bundle, MediaHelper.getUserHandle(deviceScreenType));
            }
        }
    }

    private String getPkgNameBySource(){
        String pkgName = "";
        if (IqyImpl.APP_NAME.equals(currentSource)) {
            pkgName = IqyImpl.APP_NAME;
        } else if (TencentVideoImpl.APP_NAME.equals(currentSource)) {
            pkgName = TencentVideoImpl.APP_NAME;
        } else if (MiguImpl.APP_NAME.equals(currentSource)) {
            pkgName = MiguImpl.APP_NAME;
        } else if (BiliImpl.APP_NAME.equals(currentSource)) {
            pkgName = BiliImpl.APP_NAME;
        } else if (TiktokImpl.APP_NAME.equals(currentSource)) {
            pkgName = TiktokImpl.APP_NAME;
        } else if (UsbVideoImpl.APP_NAME.equals(currentSource)) {
            pkgName = UsbVideoImpl.APP_NAME;
        } else if (ThunderKtvImpl.APP_NAME.equals(currentSource)) {
            pkgName = ThunderKtvImpl.APP_NAME;
        }
        return pkgName;
    }

    public void scheme(VideoInfo videoInfo){
        LogUtils.d(TAG, "scheme currentDisplayId = " + currentDisplayId + ", source = " + videoInfo.source + ", videoName = " + videoInfo.videoName);
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(MediaHelper.getDeviceScreenTypeByDisplayId(currentDisplayId), "");
        if (videoInfo.source == 1) {
            IqyImpl.INSTANCE.scheme(videoInfo.videoId, videoInfo.channelId + "", videoInfo.videoName);
        } else {
            TencentVideoImpl.INSTANCE.scheme(videoInfo.albumId);
        }
    }

    public TTSBean pre() {
        LogUtils.d(TAG, "pre currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.pre();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.pre();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.pre();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.pre();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.pre();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.pre();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.pre();
        }
        return null;
    }

    public TTSBean next() {
        LogUtils.d(TAG, "next currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.next();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.next();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.next();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.next();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.next();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.next();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.next();
        }
        return null;
    }

    public TTSBean play() {
        LogUtils.d(TAG, "play currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.play();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.play();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.play();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.play();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.play();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.play();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.play();
        }
        return null;
    }

    public TTSBean replay() {
        LogUtils.d(TAG, "replay currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.replay();
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.replay();
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.replay();
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.replay();
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.replay();
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.replay();
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.replay();
        }
        return null;
    }

    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isExit = " + isExit);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            return IqyImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            return MiguImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            return TencentVideoImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            return BiliImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            return TiktokImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            return UsbVideoImpl.INSTANCE.stop(isExit);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            return ThunderKtvImpl.INSTANCE.stop(isExit);
        }
        return null;
    }

    public String getMediaSourceByPkgName(String pkgName){
        String mediaSource = "";
        if (StringUtils.isNotBlank(pkgName)) {
            switch (pkgName) {
                case IqyImpl.APP_NAME:
                    mediaSource = MediaSource.iqy_video.getName();
                    break;
                case MiguImpl.APP_NAME:
                    mediaSource = MediaSource.migu_video.getName();
                    break;
                case TencentVideoImpl.APP_NAME:
                    mediaSource = MediaSource.tencent_video.getName();
                    break;
                case BiliImpl.APP_NAME:
                    mediaSource = MediaSource.bili_video.getName();
                    break;
                case TiktokImpl.APP_NAME:
                    mediaSource = MediaSource.tiktok_video.getName();
                    break;
                case UsbVideoImpl.APP_NAME:
                    mediaSource = MediaSource.usb_video.getName();
                    break;
                case ThunderKtvImpl.APP_NAME:
                    mediaSource = MediaSource.ls_ktv.getName();
                    break;
            }
        }
        currentSource = mediaSource;
        return mediaSource;
    }

    public void switchHistoryUI(boolean isOpen){
        LogUtils.d(TAG, "switchHistoryUI currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isOpen = " + isOpen);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            IqyImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
             MiguImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
             TencentVideoImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
             BiliImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
             TiktokImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
             UsbVideoImpl.INSTANCE.switchHistoryUI(isOpen);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
             ThunderKtvImpl.INSTANCE.switchHistoryUI(isOpen);
        }
    }

    public void switchCollectUI(boolean isOpen){
        LogUtils.d(TAG, "switchCollectUI currentDisplayId = " + currentDisplayId + ", currentSource = " + currentSource + ", isOpen = " + isOpen);
        if (MediaSource.iqy_video.getName().equals(currentSource)) {
            IqyImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.migu_video.getName().equals(currentSource)) {
            MiguImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.tencent_video.getName().equals(currentSource)) {
            TencentVideoImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.bili_video.getName().equals(currentSource)) {
            BiliImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.tiktok_video.getName().equals(currentSource)) {
            TiktokImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.usb_video.getName().equals(currentSource)) {
            UsbVideoImpl.INSTANCE.switchCollectUI(isOpen);
        } else if (MediaSource.ls_ktv.getName().equals(currentSource)) {
            ThunderKtvImpl.INSTANCE.switchCollectUI(isOpen);
        }
    }

    public void openTencentOrIqy(String mediaUi, boolean isTencent) {
        if (StringUtils.isBlank(mediaUi) || "history".equals(mediaUi)) {
            if (isTencent) {
                TencentVideoImpl.INSTANCE.switchHistoryUI(true);
            } else {
                IqyImpl.INSTANCE.switchHistoryUI(true);
            }
        } else {
            if (isTencent) {
                TencentVideoImpl.INSTANCE.switchCollectUI(true);
            } else {
                IqyImpl.INSTANCE.switchCollectUI(true);
            }
        }
    }
}
