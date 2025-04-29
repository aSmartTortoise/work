package com.voyah.ai.basecar.media.vedio;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.mega.nexus.content.MegaContext;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


public enum TencentVideoCopilotImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = TencentVideoCopilotImpl.class.getSimpleName();

    public static final String APP_NAME = "com.tencent.qqlive.audiobox";

    public static final String PLAY_ACTIVITY = "com.tencent.qqliveaudiobox.videodetail.view.VideoDetailActivity";
    public static final String HISTORY_ACTIVITY = "com.tencent.qqliveaudiobox.personalcenter.view.PersonalFeedActivity";
    public static final String COLLECT_ACTIVITY = "com.tencent.qqliveaudiobox.personalcenter.view.PersonalFollowActivity";

    public static int EPISODE = 0;
    private boolean isPlaying = false;

    private Context context;

    public void setPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
    }

    public boolean isFront() {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
        return foregroundApp;
    }

    public boolean isPlayPage() {
        String playPage = MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId());
        return PLAY_ACTIVITY.equals(playPage);
    }

    public boolean isCollectFront() {
        return COLLECT_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId()));
    }

    public boolean isHistoryFront() {
        return HISTORY_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId()));
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPlayPageAndPlaying() {
        return isPlayPage() && isPlaying;
    }

    public TTSBean launchTencentVideo(boolean isOpen) {
        LogUtils.d(TAG, "isOpen: " + (isOpen ? "open" : "close"));
        if (isOpen) {
            if (isFront()) {
                return TtsReplyUtils.getTtsBean("1100029", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "腾讯视频");
            } else {
                isOpen(true);
                return TtsReplyUtils.getTtsBean("1100030", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "腾讯视频");
            }
        } else {
            if (isFront()) {
                isOpen(false);
                return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "腾讯视频");
            } else {
                if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                    MediaHelper.backToHome(DeviceScreenType.PASSENGER_SCREEN);
                    return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "腾讯视频");
                } else {
                    return TtsReplyUtils.getTtsBean("1100032", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "腾讯视频");
                }
            }
        }
    }

    public void isOpen(boolean isOpen) {
        if (isOpen) {
            //判断目标包名是否是支持同看的应用,如果是同看应用，需要做如下适配
            if (MirrorServiceManager.INSTANCE.isAllowMirroredApps(APP_NAME)) {
                //启动屏id
                boolean handled = MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
                //如果返回true，说明此事件被多屏同看接管
                if (!handled) {
                    //handled=false 说明多屏同看不处理此事件， 正常启动
                    openApp();
                }
            } else {
                // 非支持同看的应用，正常启动
                openApp();
            }
        } else {
//            MediaHelper.closeApp(APP_NAME,DeviceScreenType.PASSENGER_SCREEN);
            isPlaying = false;
            start("关闭腾讯视频");
        }
    }

    private void openApp(){
        MediaHelper.openApp(APP_NAME,DeviceScreenType.PASSENGER_SCREEN);
    }

    private void start(String cmd) {
        LogUtils.d(TAG, "start cmd: " + cmd);
        Intent intent = new Intent();
        intent.setAction("com.voyah.ai.voice.ACTION_BIND_SERVICE");
        intent.setPackage(context.getPackageName());
        intent.putExtra("type", "command");
        intent.putExtra("text", cmd);
        intent.putExtra("userId",String.valueOf(MediaHelper.getUserId(DeviceScreenType.PASSENGER_SCREEN)));
        MegaContext.startServiceAsUser(context,intent,MediaHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN));
    }

    @Override
    public void init(Context context) {
        this.context = context;
        try {
            String appPackageName = context.getPackageName();
            MegaSystemProperties.set("ro.tencent.voice.package", appPackageName);
            MegaSystemProperties.set("ro.tencent.voice.action", "com.voyah.ai.voice.ACTION_BIND_SERVICE");
        } catch (Exception ignore) {
        }
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean pre() {
        start("上一集");
        return null;
    }

    @Override
    public TTSBean next() {
        start("下一集");
        return null;
    }

    @Override
    public TTSBean play() {
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        if (isPlayPage() || StringUtils.isNotBlank(mirrorPackage)) {
            if (isPlaying()) {
                MediaHelper.speakTts("4003902");
            } else {
                start("继续播放");
            }
        } else {
            start("打开播放历史");
        }
        return null;
    }

    @Override
    public TTSBean replay() {
        start("从头播放");
        return null;
    }

    @Override
    public TTSBean stop(boolean isExit) {

        if (isExit) {
            launchTencentVideo(false);
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            if (!isPlaying()) {
                MediaHelper.speakTts("4004001");
            } else {
                start("暂停播放");
            }
            return null;
        }
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        if (TextUtils.equals(seekType, "fast_forward")) {
            if (duration > 0) {
                start("快进" + duration + "秒");
            } else {
                start("快进");
            }
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            if (duration > 0) {
                start("快退" + duration + "秒");
            } else {
                start("快退");
            }
        } else if (TextUtils.equals(seekType, "set")) {
            if (duration > 0) {
                start("快进到" + duration + "秒");
            } else {
                replay();
            }
        }
        return null;
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        start(isOpen ? "打开弹幕" : "关闭弹幕");
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " numberRate: " + numberRate + " level: " + level);
        if (TextUtils.equals(adjustType, "increase")) {
            start("增加倍速播放");
        } else if (TextUtils.equals(adjustType, "decrease")) {
            start("降低倍速播放");
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                start("最快倍速播放");
            } else if (TextUtils.equals(level, "min")) {
                start("最慢倍速播放");
            } else {
                try {
                    float num = Float.parseFloat(numberRate);
                    LogUtils.d(TAG, "set num: " + num);
                    if (num > 0) {
                        float[] targets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f};
                        float closest;
                        if (num > targets[targets.length - 1]) {
                            TencentVideoImpl.isOutRangeMaxSpeed = true;
                            closest = targets[targets.length - 1];
                        } else if (num < targets[0]) {
                            TencentVideoImpl.isOutRangeMinSpeed = true;
                            closest = targets[0];
                        } else {
                            closest = NumberUtils.findClosestValue(num, targets);
                        }
                        LogUtils.d(TAG, "closest: " + closest);
                        start("播放速度调整为" + closest + "倍速");
                    } else {
                        start("最慢倍速播放");
                    }
                } catch (Exception e) {
                    LogUtils.d(TAG, "speed set error: " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " mode: " + mode + " level: " + level);
        try {
            if (TextUtils.equals(adjustType, "increase")) {
                start("提高清晰度");
            } else if (TextUtils.equals(adjustType, "decrease")) {
                start("降低清晰度");
            } else if (TextUtils.equals(adjustType, "set")) {
                if (TextUtils.equals(level, "max")) {
                    start("调到最高清晰度");
                } else if (TextUtils.equals(level, "min")) {
                    start("调到最低清晰度");
                } else {
                    if (!TextUtils.isEmpty(mode)) {
                        String numString = NumberUtils.extractNumbers(mode);
                        LogUtils.d(TAG, "numString: " + numString);
                        if (TextUtils.isEmpty(numString)) {
                            // 汉字
                            Map<String, String> map = new HashMap<>();
                            switch (mode) {
                                case "超高清":
                                case "全高清":
                                case "高清":
                                case "蓝光":
                                    start("设置为1080p清晰度");
                                    break;
                                case "准高清":
                                    start("设置为720p清晰度");
                                    break;
                                case "标清":
                                case "流畅":
                                case "自动":
                                default:
                                    start("设置为480p清晰度");
                                    break;
                            }
                        } else {
                            // 数字
                            int num = Integer.parseInt(numString);
                            if (mode.contains("k") || mode.contains("K")) {
                                num = num * 1000;
                            }
                            if (num > 0) {
                                float[] targets = {270, 480, 720, 1080};
                                float closest = NumberUtils.findClosestValue(num, targets);
                                start("清晰度[+]" + (int) closest + "p");
                            } else {
                                start("设置为480p清晰度");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public TTSBean queryPlayInfo() {
//        if (TextUtils.isEmpty(MediaDeviceService.VIDEO_NAME)) {
//            return TtsReplyUtils.getTtsBean("4004402");
//        }
//        return TtsReplyUtils.getTtsBean("4013001", "@{media_name}", MediaDeviceService.VIDEO_NAME
//                , "@{app_name}", "腾讯视频");

//        start("当前播放的是什么");
        MediaHelper.speakTts("4003100");
        return null;
    }

    @Override
    public TTSBean jump() {
//        start("跳过片头片尾");
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        start(isCollect ? "收藏视频" : "取消收藏视频");
        return null;
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
        return TtsReplyUtils.getTtsBean("4003100");
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
        LogUtils.d(TAG, "switchHistoryUI isOpen = " + isOpen);
        if (isOpen) {
            if (isHistoryFront()) {
                MediaHelper.speakTts("4022803");
            } else {
                try {
                    if (!isFront()) {
                        launchTencentVideo(true);
                        Thread.sleep(200);
                    }
                    start("打开历史记录");
                    MediaHelper.speakTts("4022802");
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }
//                AppUtils.openAppByComponent(context, APP_NAME, HISTORY_ACTIVITY);
            }
        } else {
            if (isHistoryFront()) {
                start("关闭历史记录");
//                AppUtils.forceStopPackage(context, APP_NAME);
                MediaHelper.speakTts("4022902");
            } else {
                MediaHelper.speakTts("4022901");
            }
        }
        return null;
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.d(TAG, "switchCollectUI isOpen = " + isOpen);
        if (isOpen) {
            if (isCollectFront()) {
                MediaHelper.speakTts("4003403");
            } else {
//                AppUtils.openAppByComponent(context, APP_NAME, COLLECT_ACTIVITY);
                try {
                    if (!isFront()) {
                        launchTencentVideo(true);
                        Thread.sleep(200);
                    }
                    start("打开我的在追");
                    MediaHelper.speakTts("4003402");
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }
            }
        } else {
            if (isCollectFront()) {
//                start("关闭我的收藏");
//                SystemUtils.forceStopPackage(context, APP_NAME);
                start("关闭我的在追");
                MediaHelper.speakTts("4003502");
            } else {
                MediaHelper.speakTts("4003501");
            }
        }
        return null;
    }

    @Override
    public TTSBean playUI(int type) {
        if (type == 1) {
            start("播放历史记录");
        } else {
            start("播放我的在追");
        }
//        return TTSIDConvertHelper.getInstance().getTTSBean("4028803");
        return null;
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }

    public void scheme(String cid) {
        LogUtils.d(TAG, "scheme cid: " + cid);
        if (!TextUtils.isEmpty(cid)) {
            Uri uri = Uri.parse("txvideo://v.qq.com/VideoDetailActivity?cid=" + cid);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityOptions activityOptions = ActivityOptions.makeBasic();
            activityOptions.setLaunchDisplayId(MediaHelper.getVideoDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId(), APP_NAME));
            MegaContext.startActivityAsUser(context, intent, activityOptions.toBundle(),MediaHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN));
        }
    }

    public TTSBean playEpisode(int value) {
        EPISODE = value;
        if (value > 0) {
            start("播放第" + value + "集");
        } else {
            start("换到负集");
        }
        return null;
    }
}
