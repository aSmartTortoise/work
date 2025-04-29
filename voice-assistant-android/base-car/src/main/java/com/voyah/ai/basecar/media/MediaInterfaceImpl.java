package com.voyah.ai.basecar.media;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaControlInterface;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
//import com.voyah.ai.basecar.utils.ShareUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public class MediaInterfaceImpl implements MediaControlInterface {
    private static final String TAG = MediaInterfaceImpl.class.getSimpleName();
    private static final MediaInterfaceImpl mediaInterface = new MediaInterfaceImpl();
    private int mSoundLocation;
    private String mIdentifier;

    public int getSoundLocation(){
        return mSoundLocation;
    }

    @Override
    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return mIdentifier;
    }

    public static MediaInterfaceImpl getInstance() {
        return mediaInterface;
    }

    private Context context;

    public boolean isDlnaVideo() {
        boolean dlnaVideo = MegaForegroundUtils.isForegroundApp(context, "com.voyah.cockpit.dlnaserver");
        boolean dlnaMainActivity = MegaForegroundUtils.isDlnaMainActivity(context);
        LogUtils.d(TAG, "isDlnaVideo: " + dlnaVideo + "-- dlnaMainActivity: " + dlnaMainActivity);
        return dlnaVideo && dlnaMainActivity;
    }

    public boolean isDlnaVideoPlaying() {
        int dlnaVideoPlaying = DeviceHolder.INS().getDevices().getSystem().getShare().isDlnaVideoPlaying();
        LogUtils.d(TAG, "isDlnaVideoPlaying: " + dlnaVideoPlaying);
        return dlnaVideoPlaying == 1;
    }

    public TTSBean switchUi(boolean isOpen, String uiName, String appName, String mediaType) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchUi(isOpen, uiName, appName, mediaType);
    }

    public TTSBean open(boolean isOpen, String mediaType, String mediaSource, String appName, String position, String queryPosition) {
        return DeviceHolder.INS().getDevices().getMediaCenter().open(isOpen, mediaType, mediaSource, appName,position,queryPosition);
    }

    @Override
    public void init() {
        this.context = Utils.getApp();
        DeviceHolder.INS().getDevices().getMediaCenter().init();
    }

    @Override
    public TTSBean initUserHandle(String position,String screen, String soundLocation) {
        LogUtils.i(TAG, "initUserHandle position:" + ";screen:" + screen+ position + ";soundLocation:" + soundLocation);
        String queryScreen = !TextUtils.isEmpty(position) ? position : screen;

        mSoundLocation = translateLocation(soundLocation);

        return DeviceHolder.INS().getDevices().getMediaCenter().initVoicePosition(queryScreen,soundLocation);
    }

    private int translateLocation(String awakenLocation) {
        int location = 0; //默认主驾
        if (MediaConstant.Location.Location_LIST.contains(awakenLocation)) {
            location = MediaConstant.Location.Location_MAP.get(awakenLocation);
        }
        return location;
    }

    @Override
    public void destroy() {
        DeviceHolder.INS().getDevices().getMediaCenter().destroy();
    }

    @Override
    public TTSBean pre() {
        return DeviceHolder.INS().getDevices().getMediaCenter().pre();
    }

    @Override
    public TTSBean next() {
        return DeviceHolder.INS().getDevices().getMediaCenter().next();
    }

    @Override
    public TTSBean play() {
        if (isDlnaVideo()) {
            if (isDlnaVideoPlaying()) {
                return TtsReplyUtils.getTtsBean("6007020");
            } else {
                DeviceHolder.INS().getDevices().getSystem().getShare().startDlnaVideo();
                return TtsReplyUtils.getTtsBean("5050500");
            }
        }
        return DeviceHolder.INS().getDevices().getMediaCenter().play();
    }

    @Override
    public TTSBean replay() {
        return DeviceHolder.INS().getDevices().getMediaCenter().replay();
    }

    @Override
    public TTSBean stop(boolean isExit) {
        if (isDlnaVideo()) {
            if (isDlnaVideoPlaying()) {
                DeviceHolder.INS().getDevices().getSystem().getShare().pauseDlnaVideo();
                return TtsReplyUtils.getTtsBean("5050500");
            } else {
                return TtsReplyUtils.getTtsBean("6007023");
            }
        }
        return DeviceHolder.INS().getDevices().getMediaCenter().stop(isExit);
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.i(TAG, "seekType: " + seekType + " duration: " + duration);
        return DeviceHolder.INS().getDevices().getMediaCenter().seek(seekType,duration);
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchPlayMode(switchType,playMode);
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchDanmaku(isOpen);
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        return DeviceHolder.INS().getDevices().getMediaCenter().speed(adjustType,numberRate,level);
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        return DeviceHolder.INS().getDevices().getMediaCenter().definition(adjustType,mode,level);
    }

    @Override
    public TTSBean queryPlayInfo() {
        return DeviceHolder.INS().getDevices().getMediaCenter().queryPlayInfo();
    }

    @Override
    public TTSBean jump() {
        return DeviceHolder.INS().getDevices().getMediaCenter().jump();
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchLyric(isOpen);
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchCollect(isCollect,mediaType);
    }

    @Override
    public TTSBean switchComment(boolean isComment, String mediaType) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchComment(isComment,mediaType);
    }

    @Override
    public TTSBean switchLike(boolean isLike, String mediaType) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchLike(isLike,mediaType);
    }

    @Override
    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchAttention(isAttention,mediaType);
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchPlayList(isOpen);
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchPlayer(isOpen);
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchOriginalSinging(isOriginal);
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchHistoryUI(isOpen);
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        return DeviceHolder.INS().getDevices().getMediaCenter().switchCollectUI(isOpen);
    }

    @Override
    public TTSBean playUI(int type) {
        return DeviceHolder.INS().getDevices().getMediaCenter().playUI(type);
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return DeviceHolder.INS().getDevices().getMediaCenter().play(mediaType,mediaTypeDetail,appName,mediaSource,mediaName,mediaArtist,mediaAlbum,mediaDate,mediaMovie,mediaStyle,mediaLan,mediaVersion,mediaOffset,mediaUi,mediaRank,playMode);
    }
}
