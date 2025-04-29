package com.voyah.ai.basecar.media.vedio;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.cockpit.appadapter.aidlimpl.VideoServiceImpl;
import com.voyah.mirror.MirrorServiceManager;


public enum UsbVideoImpl implements MediaInterface {
    INSTANCE;

    private static final String TAG = UsbVideoImpl.class.getSimpleName();

    public static final String APP_NAME = "com.voyah.cockpit.video";

    public static final String PLAY_ACTIVITY = "com.voyah.cockpit.video.ui.VideoActivity";

    private Context context;

    public boolean isFront() {
        boolean ret = MediaHelper.isAppForeGround(APP_NAME, VideoControlCenter.getInstance().getCurrentDisplayId());
        LogUtils.d(TAG, "isFront: " + ret);
        return ret;
    }


    public boolean isFrontByDisplayid(int displayid) {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME,displayid);
        LogUtils.d(TAG, "isFront = " + foregroundApp);
        return foregroundApp;
    }

    public boolean isPlayPage(int displayId) {
        String playPage = MediaHelper.getTopActivityName(displayId);
        LogUtils.d(TAG, "playPage: " + playPage);
        return PLAY_ACTIVITY.equals(playPage);
    }

    public boolean isPlaying() {
        boolean result = VideoServiceImpl.getInstance(context).playingState() == 1;
        LogUtils.d(TAG, "isPlaying = " + result);
        return result;
    }

    public TTSBean openByPlay() {
//        VideoServiceImpl.getInstance(context).openVideoApplication();
//        DeviceHolder.INS().getDevices().getSystem().getApp().openApp(APP_NAME);

        if (isUsbConnect()) {
            return TtsReplyUtils.getTtsBean("4039900");
        } else {
            return TtsReplyUtils.getTtsBean("4017400");
        }
    }

    public TTSBean open(boolean isOpen) {
        int currentDisplayId = VideoControlCenter.getInstance().getCurrentDisplayId();
        String screenName = MediaHelper.getScreenName(currentDisplayId);
        if (isOpen) {
            if (isFront()) {
                if (isUsbConnect()) {
                    return MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaTtsManager.APP_NAME_LOCAL_VIDEO, screenName);
                } else {
                    return TtsReplyUtils.getTtsBean("4017400");
                }
            } else {
                if (MediaHelper.isSafeLimitation() && currentDisplayId == MediaHelper.getMainScreenDisplayId()) {
                    return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
                }
                //判断目标包名是否是支持同看的应用,如果是同看应用，需要做如下适配
                if (MirrorServiceManager.INSTANCE.isAllowMirroredApps(APP_NAME)) {
                    //启动屏id
                    boolean handled = MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(APP_NAME, MegaDisplayHelper.getVoiceDisplayId());
                    //如果返回true，说明此事件被多屏同看接管
                    if (!handled) {
                        //handled=false 说明多屏同看不处理此事件， 正常启动
                        openApp();
                    }
                } else {
                    // 非支持同看的应用，正常启动
                    openApp();
                }
                if (isUsbConnect()) {
                    return MediaTtsManager.getInstance().getOpenAppTts(MediaTtsManager.APP_NAME_LOCAL_VIDEO, screenName);
                } else {
                    return TtsReplyUtils.getTtsBean("4017400");
                }
            }
        } else {
            if (isFront()) {
                MediaHelper.closeApp(APP_NAME, MediaHelper.getDeviceScreenTypeByDisplayId(currentDisplayId));
                return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_LOCAL_VIDEO, screenName);
            } else {
                return MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaTtsManager.APP_NAME_LOCAL_VIDEO, screenName);
            }
        }
    }

    private void openApp() {
        VideoControlCenter.getInstance().switchVideoApp(true, UsbVideoImpl.APP_NAME);
    }

    public TTSBean openAndPlay(){
        //打开
        openApp();
        //播放
        VideoServiceImpl.getInstance(context).play("");
        if (isUsbConnect()) {
            return TtsReplyUtils.getTtsBean("4019702");
        } else {
            return TtsReplyUtils.getTtsBean("4017400");
        }
    }

    public boolean isUsbConnect() {
        try {
            int status = VideoServiceImpl.getInstance(context).checkUsbStatus();
            //解决初次不能获取状态的问题
            if (status != 1) {
                Thread.sleep(200);
                status = VideoServiceImpl.getInstance(context).checkUsbStatus();
            }
            LogUtils.d(TAG, "isUsbConnect status: " + status);
            return status == 1;
        } catch (Exception e) {
            LogUtils.d(TAG, "Exception e: " + e);
        }
        return false;
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init");
        this.context = context;
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre");
        int ret = VideoServiceImpl.getInstance(context).previous();
        if (ret == -2) {
            return TtsReplyUtils.getTtsBean("4023402");
        }
        return TtsReplyUtils.getTtsBean("1100005");
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next");
        int ret = VideoServiceImpl.getInstance(context).next();
        if (ret == -2) {
            return TtsReplyUtils.getTtsBean("4023502");
        }
        return TtsReplyUtils.getTtsBean("1100005");
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play");
        //当前如果应用不在前台，执行播放不成功，这里添加判断
        if (isFront()) {
            if (isPlaying()) {
                return TtsReplyUtils.getTtsBean("4003902");
            } else {
                VideoServiceImpl.getInstance(context).play("");
                return TtsReplyUtils.getTtsBean("1100005");
            }
        } else {
            return TtsReplyUtils.getTtsBean("4003100");
        }
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay");
        VideoServiceImpl.getInstance(context).seekTo(1, 0);
        return TtsReplyUtils.getTtsBean("1100005");
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop");
        if (isExit) {
            CommonSystemUtils.forceStopPackage(context, APP_NAME);
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            if (isPlaying()) {
                VideoServiceImpl.getInstance(context).pause();
                return TtsReplyUtils.getTtsBean("1100005");
            } else {
                return TtsReplyUtils.getTtsBean("4004001");
            }
        }
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        if (TextUtils.equals(seekType, "fast_forward")) {
            if (duration > 0) {
                VideoServiceImpl.getInstance(context).seekTo(0, (int) (duration * 1000));
            } else {
                VideoServiceImpl.getInstance(context).seekTo(0, 15 * 1000);
            }
            return TtsReplyUtils.getTtsBean("1100005");
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            int ret;
            if (duration > 0) {
                ret = VideoServiceImpl.getInstance(context).seekTo(0, (int) (-duration * 1000));
            } else {
                ret = VideoServiceImpl.getInstance(context).seekTo(0, -15 * 1000);
            }
            if (ret == -2) {
                return TtsReplyUtils.getTtsBean("4024103");
            } else {
                return TtsReplyUtils.getTtsBean("1100005");
            }
        } else if (TextUtils.equals(seekType, "set")) {
            if (duration >= 0) {
                VideoServiceImpl.getInstance(context).seekTo(1, (int) (duration * 1000));
            } else {
                VideoServiceImpl.getInstance(context).seekTo(1, 0);
            }
            return TtsReplyUtils.getTtsBean("1100005");
        }
        return null;
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return null;
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        if (TextUtils.equals(adjustType, "increase")) {
            float speed = VideoServiceImpl.getInstance(context).getCurSpeed();
            if (TextUtils.equals(Float.toString(speed), Float.toString(0.75f))) {
                VideoServiceImpl.getInstance(context).speed(1.0f);
                return TtsReplyUtils.getTtsBean("4017302");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(1.0f))) {
                VideoServiceImpl.getInstance(context).speed(1.5f);
                return TtsReplyUtils.getTtsBean("4017302");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(1.5f))) {
                VideoServiceImpl.getInstance(context).speed(2.0f);
                return TtsReplyUtils.getTtsBean("4017302");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(2.0f))) {
                return TtsReplyUtils.getTtsBean("4013604");
            }
        } else if (TextUtils.equals(adjustType, "decrease")) {
            float speed = VideoServiceImpl.getInstance(context).getCurSpeed();
            if (TextUtils.equals(Float.toString(speed), Float.toString(0.75f))) {
                return TtsReplyUtils.getTtsBean("4013605");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(1.0f))) {
                VideoServiceImpl.getInstance(context).speed(0.75f);
                return TtsReplyUtils.getTtsBean("4017303");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(1.5f))) {
                VideoServiceImpl.getInstance(context).speed(1.0f);
                return TtsReplyUtils.getTtsBean("4017303");
            } else if (TextUtils.equals(Float.toString(speed), Float.toString(2.0f))) {
                VideoServiceImpl.getInstance(context).speed(1.5f);
                return TtsReplyUtils.getTtsBean("4017303");
            }
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                if (VideoServiceImpl.getInstance(context).getCurSpeed() == 2.0f) {
                    return TtsReplyUtils.getTtsBean("4013604");
                }
                VideoServiceImpl.getInstance(context).speed(2.0f);
                return TtsReplyUtils.getTtsBean("1100005");
            } else if (TextUtils.equals(level, "min")) {
                if (VideoServiceImpl.getInstance(context).getCurSpeed() == 0.75f) {
                    return TtsReplyUtils.getTtsBean("4013605");
                }
                VideoServiceImpl.getInstance(context).speed(0.75f);
                return TtsReplyUtils.getTtsBean("1100005");
            } else {
                try {
                    float num = Float.parseFloat(numberRate);
                    LogUtils.d(TAG, "set num: " + num);
                    if (num > 0) {
                        float[] targets = {0.75f, 1.0f, 1.5f, 2.0f};
                        if (((num == targets[0]) && (VideoServiceImpl.getInstance(context).getCurSpeed() == targets[0]))
                                || ((num == targets[1]) && (VideoServiceImpl.getInstance(context).getCurSpeed() == targets[1]))
                                || ((num == targets[2]) && (VideoServiceImpl.getInstance(context).getCurSpeed() == targets[2]))
                                || ((num == targets[3]) && (VideoServiceImpl.getInstance(context).getCurSpeed() == targets[3]))) {
                            return TtsReplyUtils.getTtsBean("4024505","@{media_speed}", String.valueOf(num));
                        }

                        float closest = NumberUtils.findClosestValue(num, targets);
                        LogUtils.d(TAG, "closest: " + closest);
                        if (VideoServiceImpl.getInstance(context).getCurSpeed() == closest) {
                            return TtsReplyUtils.getTtsBean("4024505","@{media_speed}", closest +"倍速");
                        }
                        VideoServiceImpl.getInstance(context).speed(closest);
                        if (num > targets[3]) {
                            return TtsReplyUtils.getTtsBean("4024503","@{media_speed_max}","最高倍速");
                        } else if (num < targets[0]) {
                            return TtsReplyUtils.getTtsBean("4024504","@{media_speed_min}","最低倍速");
                        } else {
                            return TtsReplyUtils.getTtsBean("1100005");
                        }
                    }
                } catch (NumberFormatException e) {
                    LogUtils.d(TAG, "speed set error: " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        return null;
    }

    @Override
    public TTSBean queryPlayInfo() {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean jump() {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchComment(boolean isComment, String mediaType) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchLike(boolean isLike, String mediaType) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean playUI(int type) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }
}
