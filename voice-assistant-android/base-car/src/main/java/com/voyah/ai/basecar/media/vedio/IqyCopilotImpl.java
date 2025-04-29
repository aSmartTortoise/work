package com.voyah.ai.basecar.media.vedio;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.arcvideo.ivi.link.sdk.IqyVoiceCtrManager;
import com.arcvideo.ivi.link.sdk.QYSExecType;
import com.arcvideo.ivi.link.sdk.config.LinkCode;
import com.arcvideo.ivi.link.srv.sdk.IQysLinkResponseListener;
import com.arcvideo.ivi.link.srv.sdk.IServiceConnectListener;
import com.blankj.utilcode.util.JsonUtils;
import com.mega.nexus.content.MegaContext;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.common.utils.SPUtil;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public enum IqyCopilotImpl implements MediaInterface {
    INSTANCE;

    private static final String TAG = IqyCopilotImpl.class.getSimpleName();

    private Context context;

    private final int userId = MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()).hashCode();

    public static final String APP_NAME = "com.arcvideo.car.iqy.video";
    public static final String COLLECT_ACTIVITY = "com.arcvideo.car.user.activity.CollectActivity";
    public static final String HISTORY_ACTIVITY = "com.arcvideo.car.user.activity.HistoryActivity";
    public static final String DETAIL_ACTIVITY = "com.arcvideo.car.play.acivity.PlayerActivity";
    private static final int RESULT_CODE_PAGE_OPENED = 1016;

    private String adjustType;
    private String numberRate;
    private String level;
    private boolean isOutRangeMaxSpeed = false;
    private boolean isOutRangeMinSpeed = false;
    private String actionType;
    private static final String OPEN_COLLECT_UI = "open_collect_ui";
    private static final String CLOSE_COLLECT_UI = "close_collect_ui";
    private static final String PLAY_HISTORY = "play_history";
    private static final String PLAY_COLLECT = "play_collect";
    private static final String OPEN_COLLECT = "open_collect";
    private static final String CANCEL_COLLECT = "cancel_collect";
    private int setEpisode;
    Map<String, String> definitionMap = new HashMap<>();
    private String definitionMode;
    private String definitionClosest;
    private boolean isSecond;
    private boolean mServiceConnectionStatus = false;

    public boolean isFront() {
        boolean ret = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
        return ret;
    }

    public boolean isPlayPage() {
        boolean ret = DETAIL_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId()));
        return isFront() && ret;
    }

    public boolean isCollectFront() {
        return COLLECT_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId()));
    }

    public boolean isHistoryFront() {
        return HISTORY_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getPassengerScreenDisplayId()));
    }

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public synchronized boolean isPlaying() {
        final boolean[] ret = new boolean[1];
        if(mServiceConnectionStatus) {
            ret[0] = IqyVoiceCtrManager.getInstance().videoIsPlaying(userId);
        }else{
            IqyVoiceCtrManager.getInstance().setOnServiceConnectListener(userId, new IServiceConnectListener.Stub() {
                @Override
                public void onServiceConnection(boolean b) {
                    LogUtils.d(TAG, "onServiceConnection: " + b);
                    mServiceConnectionStatus = b;
                    if (b) {
                        registerIQysLinkResponse();
                        ret[0] = IqyVoiceCtrManager.getInstance().videoIsPlaying(userId);
                        LogUtils.d(TAG, "onServiceConnection--: " + ret[0]);
                    }
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogUtils.d(TAG, "e = " + e);
            }
        }
        return ret[0];
    }

    private void registerIQysLinkResponse() {
        IqyVoiceCtrManager.getInstance().setIQysLinkResponseListener(userId, new IQysLinkResponseListener.Stub() {
            @Override
            public void onQysCallback(String msgType, int code, String data) {
                if (MediaHelper.isSupportMultiScreen()) {
                    LogUtils.d(TAG, "msgType " + msgType + ", code " + code + ", data " + data);
                    handleResult(msgType, code, data);
                }
            }

            @Override
            public void onPlayState(int playState) {
                LogUtils.d(TAG, "onPlayState is " + playState);
                if (playState == 3) {
                    SPUtil.putBoolean(context, MediaHelper.IS_TENCENT_PLAY, false);
                }
            }
        });
    }

    public TTSBean open(boolean isOpen) {
        LogUtils.d(TAG, "isOpen: " + (isOpen ? "open" : "close"));
        if (isOpen) {
            if (isFront()) {
                return TtsReplyUtils.getTtsBean("1100029", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER,"@{app_name}", "爱奇艺");
            } else {
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
                return TtsReplyUtils.getTtsBean("1100030", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER,"@{app_name}", "爱奇艺");
            }
        } else {
            if (isFront()) {
                //使用爱奇艺api会导致userId is 0 binder service connect failed ,link interface is null
                MediaHelper.closeApp(APP_NAME, DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_PASSENGER));
                return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER,"@{app_name}", "爱奇艺");
            } else {
                if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                    MediaHelper.backToHome(DeviceScreenType.PASSENGER_SCREEN);
                    return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "爱奇艺");
                } else {
                    return TtsReplyUtils.getTtsBean("1100032", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "爱奇艺");
                }
            }
        }
    }

    public void openApp(){
        MediaHelper.openApp(APP_NAME, DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_PASSENGER));
    }

    private void serviceConnect(String cmd, Map<String, String> paramMap) {
        LogUtils.d(TAG,"serviceConnect = "+userId);
        IqyVoiceCtrManager.getInstance().setOnServiceConnectListener(userId, new IServiceConnectListener.Stub() {
            @Override
            public void onServiceConnection(boolean b) {
                LogUtils.d(TAG, "onServiceConnection: " + b);
                mServiceConnectionStatus = b;
                if (b) {
                    registerIQysLinkResponse();
                    IqyVoiceCtrManager.getInstance().obtainVoiceMessage(userId, cmd, paramMap);
                }
            }
        });
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init copilot");
        this.context = context;
        IqyVoiceCtrManager.getInstance().initialize(context);
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre");
        mediaControl(QYSExecType.PREVIOUS,null);
        return null;
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next");
        mediaControl(QYSExecType.NEXT, null);
        return null;
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play");
        if (isPlaying()) {
            return TtsReplyUtils.getTtsBean("4003902");
        }
        mediaControl(QYSExecType.PLAY, null);
        return null;
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay");
        Map<String, String> map = new HashMap<>();
        map.put("param", "0");
        mediaControl(QYSExecType.REPLAY, map);
        return null;
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop");
        if (isExit) {
            mediaControl(QYSExecType.CLOSE_APP, null);
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            if (!isPlaying()) {
                return TtsReplyUtils.getTtsBean("4004001");
            }
            mediaControl(QYSExecType.PAUSE, null);
        }
        return null;
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        if (TextUtils.equals(seekType, "fast_forward")) {
            Map<String, String> map = new HashMap<>();
            if (duration > 0) {
                map.put("param", String.valueOf(duration * 1000));
            } else {
                map.put("param", "15000");
            }
            mediaControl(QYSExecType.FAST_FORWARD, map);
            return null;
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            Map<String, String> map = new HashMap<>();
            if (duration > 0) {
                map.put("param", String.valueOf(duration * 1000));
            } else {
                map.put("param", "15000");
            }
            mediaControl(QYSExecType.FAST_BACKWARD, map);
            return null;
        } else if (TextUtils.equals(seekType, "set")) {
            Map<String, String> map = new HashMap<>();
            if (duration >= 0) {
                map.put("param", String.valueOf(duration * 1000));
            } else {
                map.put("param", "0");
            }
            mediaControl(QYSExecType.SEEK, map);
            return null;
        }
        return null;
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        LogUtils.d(TAG, "switchDanmaku isOpen: " + isOpen);
        mediaControl(isOpen ? QYSExecType.OPEN_DANMU : QYSExecType.CLOSE_DANMU, null);
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " numberRate: " + numberRate + " level: " + level);
        this.adjustType = adjustType;
        this.numberRate = numberRate;
        this.level = level;
        //1.获取当前速度
        mediaControl(QYSExecType.CURRENT_VIDEO_SPEED, null);
        return null;
    }

    private void speedAction(String currentSpeed) {
        LogUtils.d(TAG,"currentSpeed = "+currentSpeed);
        if (TextUtils.equals(adjustType, "increase")) {
            if (isMaxSpeed(currentSpeed)) {
                MediaHelper.speak(TtsReplyUtils.getTtsBean("4013604").getSelectTTs());
                return;
            }
            mediaControl(QYSExecType.SPEED_UP, null);
        } else if (TextUtils.equals(adjustType, "decrease")) {
            if (isMinSpeed(currentSpeed)) {
                MediaHelper.speak(TtsReplyUtils.getTtsBean("4013605").getSelectTTs());
                return;
            }
            mediaControl(QYSExecType.SPEED_DOWN, null);
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                if (isMaxSpeed(currentSpeed)) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4013604").getSelectTTs());
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("param", "200");
                mediaControl(QYSExecType.SPEED, map);
            } else if (TextUtils.equals(level, "min")) {
                if (isMinSpeed(currentSpeed)) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4013605").getSelectTTs());
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("param", "75");
                mediaControl(QYSExecType.SPEED, map);
            } else {
                float num = Float.parseFloat(numberRate);
                LogUtils.d(TAG, "set num: " + num);
                if (num > 0) {
                    float[] targets = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
                    float closest;
                    if (num > targets[targets.length - 1]) {
                        isOutRangeMaxSpeed = true;
                        closest = targets[targets.length - 1];
                    } else if (num < targets[0]) {
                        isOutRangeMinSpeed = true;
                        closest = targets[0];
                    } else {
                        closest = NumberUtils.findClosestValue(num, targets);
                    }
                    LogUtils.d(TAG, "closest: " + closest);
                    Map<String, String> map = new HashMap<>();
                    String tempSpeed = ((int) (closest * 100)) + "";
                    if (tempSpeed.equals(currentSpeed) && !isOutRangeMaxSpeed && !isOutRangeMinSpeed) {
                        MediaHelper.speak(TtsReplyUtils.getTtsBean("4024505", "@{media_speed}", closest + "倍播放速度").getSelectTTs());
                        return;
                    }
                    LogUtils.d(TAG, "tempSpeed: " + tempSpeed);
                    map.put("param", tempSpeed);
                    mediaControl(QYSExecType.SPEED, map);
                }
            }
        }
    }

    private boolean isMaxSpeed(String currentSpeed) {
        return "200".equals(currentSpeed);
    }

    private boolean isMinSpeed(String currentSpeed) {
        return "75".equals(currentSpeed);
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " mode: " + mode + " level: " + level);
        try {
            if (TextUtils.equals(adjustType, "increase")) {
                mediaControl(QYSExecType.BITRATE_UP, null);
            } else if (TextUtils.equals(adjustType, "decrease")) {
                mediaControl(QYSExecType.BITRATE_DOWN, null);
            } else if (TextUtils.equals(adjustType, "set")) {
                if (TextUtils.equals(level, "max")) {
                    mediaControl(QYSExecType.BITRATE_HIGHEST, null);
                } else if (TextUtils.equals(level, "min")) {
                    mediaControl(QYSExecType.BITRATE_LOWEST, null);
                } else {
                    if (!TextUtils.isEmpty(mode)) {
                        String numString = NumberUtils.extractNumbers(mode);
                        LogUtils.d(TAG, "numString: " + numString);
                        if (TextUtils.isEmpty(numString)) {
                            // 汉字
                            switch (mode) {
                                case "超高清":
                                    definitionMap.put("param", "4000");
                                    break;
                                case "全高清":
                                case "高清":
                                case "蓝光":
                                    definitionMap.put("param", "1080");
                                    break;
                                case "准高清":
                                    definitionMap.put("param", "720");
                                    break;
                                case "标清":
                                case "流畅":
                                case "自动":
                                default:
                                    definitionMap.put("param", "480");
                                    break;
                            }
                            definitionMode = mode;
                            LogUtils.d(TAG, "map: " + definitionMap.get("param"));
                            mediaControl(QYSExecType.CURRENT_VIDEO_RATE, null);
                        } else {
                            // 数字
                            int num = Integer.parseInt(numString);
                            if (mode.contains("k") || mode.contains("K")) {
                                num = num * 1000;
                            }
                            if (num > 0) {
                                float[] targets = {480, 720, 1080, 4000};
                                float closest = NumberUtils.findClosestValue(num, targets);
                                String closestInt = (int) closest + "";
                                LogUtils.d(TAG, "closest: " + closestInt);
                                definitionClosest = closestInt;
                                mediaControl(QYSExecType.CURRENT_VIDEO_RATE, null);
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
        LogUtils.d(TAG, "queryPlayInfo");
        mediaControl(QYSExecType.VIDEO_NAME, null);
        return null;
    }

    @Override
    public TTSBean jump() {
        LogUtils.d(TAG, "jump");
        mediaControl(QYSExecType.OPEN_SKIP, null);
        return null;
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        LogUtils.d(TAG, "switchCollect isCollect: " + isCollect + " mediaType: " + mediaType);
        actionType = isCollect ? OPEN_COLLECT : CANCEL_COLLECT;
        mediaControl(QYSExecType.IS_LOGIN, null);
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
        LogUtils.d(TAG, "switchHistoryUI isOpen: " + isOpen + ", userId = " + userId);
        if (isHistoryFront() && isOpen) {
            MediaHelper.speakTts("4022803");
        } else if (!isHistoryFront() && !isOpen) {
            MediaHelper.speakTts("4022901");
        } else {
            if (!isFront()) {
                open(true);
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    LogUtils.e(TAG, "e = " + e);
//                }
            }
            mediaControl(isOpen ? QYSExecType.OPEN_HISTORY : QYSExecType.CLOSE_HISTORY, null);
        }
        return null;
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.d(TAG, "switchCollectUI isOpen: " + isOpen);
        actionType = isOpen ? OPEN_COLLECT_UI : CLOSE_COLLECT_UI;
        if (isOpen) {
            if (!isFront()) {
                try {
                    open(true);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LogUtils.d(TAG, "e = " + e);
                }
            }
            mediaControl(QYSExecType.IS_LOGIN, null);
        } else {
            mediaControl(QYSExecType.CLOSE_MY_COLLECT, null);
        }
        return null;
    }

    @Override
    public TTSBean playUI(int type) {
        LogUtils.d(TAG, "playUI type: " + type);
        if (type == 1) {
            if (isHistoryFront()) {
                mediaControl(QYSExecType.PLAY_HISTORY_FIRST, null);
            } else {
                actionType = PLAY_HISTORY;
                mediaControl(QYSExecType.OPEN_HISTORY, null);
            }
        } else {
            actionType = PLAY_COLLECT;
            mediaControl(QYSExecType.IS_LOGIN, null);
        }
        return null;
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    public TTSBean playEpisode(int value) {
        LogUtils.d(TAG, "playEpisode value: " + value);
        setEpisode = value;
        mediaControl(QYSExecType.VIDEO_ORDER, null);
        return null;
    }

    public void scheme(String tvId, String channelId, String albumName) {
        int videoDisplayId = MediaHelper.getVideoDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId(), APP_NAME);
        LogUtils.d(TAG, "videoDisplayId = " + videoDisplayId + "---passenger = " + MegaDisplayHelper.getPassengerScreenDisplayId());
        final String SCHEME_HOST = "com.qiyi.video.iv";
        String methodType = "play_card_video";
        Map<String, String> map = new HashMap<>();
        map.put("qipuId", tvId);
        map.put("channelId", channelId);
        map.put("albumName", albumName);
        map.put("display_id", String.valueOf(videoDisplayId));
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("iqiyi").authority(SCHEME_HOST).appendPath("driver").appendQueryParameter("from", "other").appendQueryParameter("command", methodType);

        String displayIdStr = "";
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                uriBuilder.appendQueryParameter(key, map.get(key));
                if (key.equals("display_id")) {
                    displayIdStr = map.get(key);
                }
            }
        }
        Uri uri = uriBuilder.appendQueryParameter("time", String.valueOf(System.currentTimeMillis() / 1000)).build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        activityOptions.setLaunchDisplayId(videoDisplayId);
        if (!TextUtils.isEmpty(displayIdStr)) {
            MegaContext.startActivityAsUser(context,intent,activityOptions.toBundle(),MediaHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN));
        } else {
            MegaContext.startActivityAsUser(context,intent,activityOptions.toBundle(),MediaHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN));
        }
    }

    private void handleResult(String msgType, int code, String data) {
        LogUtils.d(TAG,userId+"--------"+ MegaUserHandle.myUserId());
        String ttsId = "";
        String ttsPart = "";
        String ttsStr = "";
        if (code == LinkCode.SUCCESS) {
            if (QYSExecType.CURRENT_VIDEO_SPEED.equals(msgType)) {//获取当前速度
                speedAction(data);
            } else if (QYSExecType.SPEED_UP.equals(msgType)) {//调高速度
                ttsId = "4017302";
            } else if (QYSExecType.SPEED_DOWN.equals(msgType)) {//调低速度
                ttsId = "4017303";
            } else if (QYSExecType.SPEED.equals(msgType)) {//速度设置
                if (isOutRangeMaxSpeed) {
                    isOutRangeMaxSpeed = false;
                    ttsPart = "最大播放速度";
                    ttsId = "4024503";
                } else if (isOutRangeMinSpeed) {
                    isOutRangeMinSpeed = false;
                    ttsPart = "最小播放速度";
                    ttsId = "4024504";
                } else {
                    ttsId = "1100005";
                }
            } else if (QYSExecType.PREVIOUS.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.NEXT.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.PLAY.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.REPLAY.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.PAUSE.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.FAST_FORWARD.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.FAST_BACKWARD.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.SEEK.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.OPEN_DANMU.equals(msgType)) {
                ttsId = "4024303";
            } else if (QYSExecType.CLOSE_DANMU.equals(msgType)) {
                ttsId = "4024403";
            } else if (QYSExecType.VIDEO_NAME.equals(msgType)) {
                if (!TextUtils.isEmpty(data)) {
                    ttsId = "4013001";
                    ttsStr = TtsReplyUtils.getTtsBean("4013001", "@{media_name}", data, "@{app_name}", "爱奇艺").getSelectTTs();
                } else {
                    ttsId = "4004402";
                }
            } else if (QYSExecType.OPEN_MY_COLLECT.equals(msgType)) {
                if(isSecond){
                    isSecond = false;
                    ttsId = "4019702";
                }else {
                    ttsId = "4003402";
                }
            } else if (QYSExecType.CLOSE_MY_COLLECT.equals(msgType)) {
                ttsId = "4003502";
            } else if (QYSExecType.PLAY_HISTORY_FIRST.equals(msgType)) {
                ttsId = "4007401";
            } else if (QYSExecType.PLAY_COLLECTED_FIRST.equals(msgType)) {
                ttsId = "4007401";
            } else if (QYSExecType.IS_LOGIN.equals(msgType)) {
                boolean isLogin = Boolean.parseBoolean(data);
                LogUtils.d(TAG, "isLogin: " + isLogin);
                if (isLogin) {
                    if (OPEN_COLLECT_UI.equals(actionType)) {
                        mediaControl(QYSExecType.OPEN_MY_COLLECT, null);
                    } else if (CLOSE_COLLECT_UI.equals(actionType)) {
                        mediaControl(QYSExecType.CLOSE_MY_COLLECT, null);
                    } else if (PLAY_COLLECT.equals(actionType)) {
                        if (isCollectFront()) {
                            mediaControl(QYSExecType.PLAY_COLLECTED_FIRST, null);
                        } else {
                            isSecond = true;
                            mediaControl(QYSExecType.OPEN_MY_COLLECT, null);
                        }
                    } else if (OPEN_COLLECT.equals(actionType)) {
                        mediaControl(QYSExecType.ADD_COLLECT, null);
                    } else if (CANCEL_COLLECT.equals(actionType)) {
                        mediaControl(QYSExecType.REMOVE_COLLECT, null);
                    }
                } else {
                    mediaControl(QYSExecType.TO_LOGIN_PAGE, null);
                    ttsId = "4000001";
                }
            } else if (QYSExecType.VIDEO_ORDER.equals(msgType)) {//第几集
                String anthologyCount = "";
                String order = JsonUtils.getString(data, "video_order");
                anthologyCount = JsonUtils.getString(data, "video_anthology_count");
                if (setEpisode > 0) {
                    if (TextUtils.equals(setEpisode + "", order)) {
                        ttsId = "4025002";
                        ttsPart = order;
                    }
                } else {
                    if (!StringUtils.isBlank(anthologyCount)) {
                        int anInt = org.apache.commons.lang3.math.NumberUtils.toInt(anthologyCount);
                        if (anInt > 0) {
                            if (TextUtils.equals(String.valueOf(anInt + setEpisode + 1), order)) {
                                ttsId = "4025002";
                                ttsPart = order;
                            }
                        }
                    }
                }
                if (StringUtils.isBlank(ttsId)) {
                    Map<String, String> map = new HashMap<>();
                    //负数处理（-1为最后一集）
                    if (setEpisode < 0 && !StringUtils.isBlank(anthologyCount)) {
                        int anInt = org.apache.commons.lang3.math.NumberUtils.toInt(anthologyCount);
                        if (anInt > 0) {
                            setEpisode = anInt + setEpisode + 1;
                        }
                    }
                    map.put("param", String.valueOf(setEpisode));
                    mediaControl(QYSExecType.EPISODE, map);
                }
            } else if (QYSExecType.EPISODE.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.OPEN_SKIP.equals(msgType)) {
                ttsId = "4025102";
            } else if (QYSExecType.ADD_COLLECT.equals(msgType)) {
                ttsId = "4004204";
            } else if (QYSExecType.REMOVE_COLLECT.equals(msgType)) {
                ttsId = "4004303";
            } else if (QYSExecType.BITRATE_UP.equals(msgType)) {
                ttsId = "4017302";
            } else if (QYSExecType.BITRATE_DOWN.equals(msgType)) {
                ttsId = "4017303";
            } else if (QYSExecType.BITRATE_HIGHEST.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.BITRATE_LOWEST.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.BITRATE.equals(msgType)) {
                ttsId = "1100005";
            } else if (QYSExecType.CURRENT_VIDEO_RATE.equals(msgType)) {
                if (!TextUtils.isEmpty(data)) {
                    if ("8".equals(data) && ("480".equals(definitionMap.get("param")) || "480".equals(definitionClosest))) {
                        ttsId = "4013505";
                        ttsPart = !TextUtils.isEmpty(definitionMode) ? definitionMode : "480P";
                    } else if ("16".equals(data) && ("720".equals(definitionMap.get("param")) || "720".equals(definitionClosest))) {
                        ttsId = "4013505";
                        ttsPart = !TextUtils.isEmpty(definitionMode) ? definitionMode : "720P";
                    } else if ("512".equals(data) && ("1080".equals(definitionMap.get("param")) || "1080".equals(definitionClosest))) {
                        ttsId = "4013505";
                        ttsPart = !TextUtils.isEmpty(definitionMode) ? definitionMode : "1080P";
                    } else if ("2048".equals(data) && ("4000".equals(definitionMap.get("param")) || "4000".equals(definitionClosest))) {
                        ttsId = "4013505";
                        ttsPart = !TextUtils.isEmpty(definitionMode) ? definitionMode : "4000P";
                    } else {
                        if (!TextUtils.isEmpty(definitionClosest)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("param", definitionClosest);
                            mediaControl(QYSExecType.BITRATE, map);
                        } else {
                            mediaControl(QYSExecType.BITRATE, definitionMap);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(definitionClosest)) {
                        Map<String, String> map = new HashMap<>();
                        map.put("param", definitionClosest);
                        mediaControl(QYSExecType.BITRATE, map);
                    } else {
                        mediaControl(QYSExecType.BITRATE, definitionMap);
                    }
                }
            } else if (QYSExecType.CLOSE_HISTORY.equals(msgType)) {
                ttsId = "4022902";
            } else if (QYSExecType.OPEN_HISTORY.equals(msgType)) {
                if (PLAY_HISTORY.equals(actionType)) {
                    ttsId = "4019702";
                } else {
                    ttsId = "4022802";
                }
            }
        } else if (code == 20007) {
            if (QYSExecType.PREVIOUS.equals(msgType)) {
                ttsId = "4023402";
            }
        } else if (code == 20006) {
            if (QYSExecType.NEXT.equals(msgType)) {
                ttsId = "4023502";
            }
        } else if (code == 21012) {
            if (QYSExecType.OPEN_DANMU.equals(msgType)) {
                ttsId = "4024302";
            }
        } else if (code == 21013) {
            if (QYSExecType.CLOSE_DANMU.equals(msgType)) {
                ttsId = "4024402";
            }
        } else if (code == 21014) {
            if (QYSExecType.FAST_FORWARD.equals(msgType)) {
                ttsId = "4024003";
            } else if (QYSExecType.SEEK.equals(msgType)) {
                ttsId = "4024003";
            }
        } else if (code == 21015) {
            if (QYSExecType.FAST_FORWARD.equals(msgType)) {
                ttsId = "4024004";
            } else if (QYSExecType.SEEK.equals(msgType)) {
                ttsId = "4024004";
            }
        } else if (code == 21016) {
            if (QYSExecType.FAST_BACKWARD.equals(msgType)) {
                ttsId = "4024103";
            }
        } else if (code == RESULT_CODE_PAGE_OPENED) {
            if (QYSExecType.OPEN_MY_COLLECT.equals(msgType)) {
                ttsId = "4003403";
            } else if (QYSExecType.CLOSE_MY_COLLECT.equals(msgType)) {
                ttsId = "4003501";
            } else if (QYSExecType.OPEN_HISTORY.equals(msgType)) {
                ttsId = "4022803";
            } else if (QYSExecType.CLOSE_HISTORY.equals(msgType)) {
                ttsId = "4022901";
            }
        } else if (code == 1010) {
            if (QYSExecType.PLAY_HISTORY_FIRST.equals(msgType)) {
                switchHistoryUI(true);
                ttsId = "4019702";
            } else if (QYSExecType.PLAY_COLLECTED_FIRST.equals(msgType)) {
                switchCollectUI(true);
                ttsId = "4019702";
            } else if (QYSExecType.CLOSE_MY_COLLECT.equals(msgType)) {
                ttsId = "4003501";
            }
        } else if (code == 90002) {
            if (QYSExecType.PLAY_HISTORY_FIRST.equals(msgType)) {
                ttsId = "4023003";
            } else if (QYSExecType.PLAY_COLLECTED_FIRST.equals(msgType)) {
                ttsId = "4003604";
            }
        } else if (code == 20003) {
            ttsId = "4025003";
            ttsPart = String.valueOf(setEpisode);
        } else if (code == 10017) {
            ttsId = "4033303";
        } else if (code == 1011 || code == 1012) {
            if (QYSExecType.EPISODE.equals(msgType)) {
                ttsId = "4050013";
            } else if (QYSExecType.NEXT.equals(msgType)) {
                ttsId = "4050013";
            } else if (QYSExecType.PREVIOUS.equals(msgType)) {
                ttsId = "4050013";
            } else {
                ttsId = "4024704";
            }
        } else if (code == 10012) {
            ttsId = "4024805";
        } else if (code == 10013) {
            ttsId = "4024806";
        } else if (code == 1015) {//未授权
            ttsId = "4050069";
        }
        //ttsId处理
        if (TextUtils.isEmpty(ttsId)) {
            LogUtils.d(TAG, "onReceiveResult ttsId is null");
        } else {
            String selectTTs;
            if (TextUtils.isEmpty(ttsStr)) {
                selectTTs = TtsReplyUtils.getTtsBean(ttsId).getSelectTTs();
            } else {
                selectTTs = ttsStr;
            }
            LogUtils.d(TAG, "onReceiveResult tts is " + selectTTs);
            if (TextUtils.isEmpty(ttsPart)) {
                MediaHelper.speak(selectTTs);
            } else {
                if (selectTTs.contains("@{media_speed_min}")) {
                    MediaHelper.speak(selectTTs.replace("@{media_speed_min}", ttsPart));
                } else if (selectTTs.contains("@{media_speed_max}")) {
                    MediaHelper.speak(selectTTs.replace("@{media_speed_max}", ttsPart));
                } else if (selectTTs.contains("@{media_num}")) {
                    MediaHelper.speak(selectTTs.replace("@{media_num}", ttsPart));
                } else if (selectTTs.contains("@{media_definition}")) {
                    MediaHelper.speak(selectTTs.replace("@{media_definition}", ttsPart));
                }
            }
        }
        actionType = "";
        definitionMap.clear();
        definitionMode = "";
        definitionClosest = "";
    }

    private void mediaControl(String cmd, Map<String, String> paramMap) {
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        int[] targetScreen = MirrorServiceManager.INSTANCE.getTargetScreen();
        int sourceScreen = MirrorServiceManager.INSTANCE.getSourceScreen();
        int passengerScreenDisplayId = MegaDisplayHelper.getPassengerScreenDisplayId();
        int index = Arrays.binarySearch(targetScreen, passengerScreenDisplayId);
        if (StringUtils.isNotBlank(mirrorPackage) && APP_NAME.equals(mirrorPackage) && (index >= 0 || sourceScreen == passengerScreenDisplayId)) {
            paramMap.put("display_id", String.valueOf(MirrorServiceManager.INSTANCE.getVirtualDisplayId()));
        } else {
            paramMap.put("display_id", String.valueOf(passengerScreenDisplayId));
        }
        if (mServiceConnectionStatus) {
            IqyVoiceCtrManager.getInstance().obtainVoiceMessage(userId, cmd, paramMap);
        } else {
            serviceConnect(cmd, paramMap);
        }
    }
}
