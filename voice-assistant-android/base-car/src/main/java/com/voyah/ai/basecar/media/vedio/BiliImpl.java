package com.voyah.ai.basecar.media.vedio;

import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.actionType;
import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.currentSpeed;
import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.isOutRangeMaxSpeed;
import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.isOutRangeMinSpeed;
import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.playEpisodeValue;
import static com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver.switchDanmakuOpen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.example.mservice.IMediaControllerService;
import com.mega.nexus.content.MegaContext;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.drawing.api.model.ScreenType;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voice.sdk.constant.MediaConstant;
import com.voyah.ai.basecar.media.ConnectStatus;
import com.voyah.ai.basecar.media.ServiceConnectListener;
import com.voyah.ai.basecar.media.receiver.BiliBroadcastReceiver;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.common.utils.HandlerUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public enum BiliImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = BiliImpl.class.getSimpleName();

    private Context context;

    public static final String APP_NAME = "com.bilibili.bilithings";
    public static final String SERVICE_NAME_OLD = "com.bilibili.sdk.manager.CustomMediaBrowserService";
    public static final String SERVICE_NAME = "com.bilibili.voyah.MediaControllerService";
    public static final String PLAY_ACTIVITY = "com.bilibili.player.play.ui.playui.VideoPlayActivity";

    private static final String OPEN_HISTORY_UI = "open_history_ui";
    private static final String CLOSE_HISTORY_UI = "close_history_ui";
    private static final String OPEN_COLLECT_UI = "open_collect_ui";
    private static final String CLOSE_COLLECT_UI = "close_collect_ui";
    private static final String OPEN_DANMAKU = "open_danmaku";
    private static final String CLOSE_DANMAKU = "close_danmaku";
    private static final String PLAY_HISTORY = "play_history";
    public static final String PLAY_COLLECT = "play_collect";

    private MediaBrowser mediaBrowser;
    private MediaController mediaController;
    private boolean isPlaying = false;
    private boolean isPlayingP = false;
    private boolean isPlayingC = false;
    private ServiceConnectListener mServiceConnectListener;

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
    public void setPlayingP(boolean isPlaying) {
        this.isPlayingP = isPlaying;
    }

    public void setPlayingC(boolean isPlaying) {
        this.isPlayingC = isPlaying;
    }

    public boolean isFront() {
        return MediaHelper.isAppForeGround(APP_NAME, VideoControlCenter.getInstance().getCurrentDisplayId());
    }

    public boolean isFrontByDisplayid(int displayid) {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME, displayid);
        return foregroundApp;
    }

    public boolean isPlayPage() {
        boolean playPage = PLAY_ACTIVITY.equals(MediaHelper.getTopActivityName(MegaDisplayHelper.getMainScreenDisplayId()));
        return playPage;
    }

    public boolean isPlayPage(int displayId) {
        return PLAY_ACTIVITY.equals(MediaHelper.getTopActivityName(displayId));
    }

    private String getParamValue(String key, Object value) {
        return key + ":" + value;
    }


    public boolean isPlaying() {
        if (MediaHelper.isSupportMultiScreen()) {
            return isPlaying;
        } else {
            List<String> list = VolumeUtils.getInstance().getAudioFocusList();
            if (list != null && !list.isEmpty()) {
                LogUtils.d(TAG, "isPlaying = " + list.contains(APP_NAME));
                return list.contains(APP_NAME);
            }
            return false;
        }
    }

    public boolean isPlaying(int displayId) {
        if (displayId == MediaHelper.getCeilingScreenDisplayId()) {
            return isPlayingC;
        } else if (displayId == MediaHelper.getPassengerScreenDisplayId()) {
            return isPlayingP;
        } else {
            return isPlaying;
        }
    }

    public boolean isPlayPageAndPlaying() {
        List<String> list = VolumeUtils.getInstance().getAudioFocusList();
        if (list != null && !list.isEmpty()) {
            LogUtils.d(TAG, "isPlaying = " + list.contains(APP_NAME));
            return isPlayPage() && list.contains(APP_NAME);
        }
        return false;
    }

    public TTSBean switchApp(boolean isOpen){
        if (isOpen) {
            if (isFront()) {
                return MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaTtsManager.APP_NAME_BILI,MediaHelper.SCREEN_NAME_CENTRAL);
            } else {
                open();
                return MediaTtsManager.getInstance().getOpenAppTts(MediaTtsManager.APP_NAME_BILI,MediaHelper.SCREEN_NAME_CENTRAL);
            }
        } else {
            if (isFront()) {
                close();
                return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_BILI,MediaHelper.SCREEN_NAME_CENTRAL);
            } else {
                if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                    MediaHelper.backToHome(DeviceScreenType.CENTRAL_SCREEN);
                    return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_BILI,MediaHelper.SCREEN_NAME_CENTRAL);
                } else {
                    return MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaTtsManager.APP_NAME_BILI,MediaHelper.SCREEN_NAME_CENTRAL);
                }
            }
        }
    }

    public void open() {
        //判断目标包名是否是支持同看的应用,如果是同看应用，需要做如下适配
        if (MirrorServiceManager.INSTANCE.isAllowMirroredApps(APP_NAME)) {
            //启动屏id
            boolean handled = MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
            //如果返回true，说明此事件被多屏同看接管
            if (!handled) {
                //handled=false 说明多屏同看不处理此事件， 正常启动
                openApp();
            }
        } else {
            // 非支持同看的应用，正常启动
            openApp();
        }
    }

    private void openApp() {
        MediaHelper.openApp(APP_NAME, DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL));
    }

    public void close() {
        MediaHelper.closeApp(APP_NAME,DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL));
    }

    private IMediaControllerService mIMediaControllerService;
    private IMediaControllerService mIMediaControllerServiceP;
    private IMediaControllerService mIMediaControllerServiceC;

    private void initService(ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(APP_NAME, SERVICE_NAME));
        MegaContext.bindServiceAsUser(context, intent, serviceConnection, Context.BIND_AUTO_CREATE, MegaUserHandle.of(0));
    }

    private void initService(DeviceScreenType deviceScreenType,boolean isWait){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(APP_NAME, SERVICE_NAME));
        MegaContext.bindServiceAsUser(context, intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "onServiceConnected bili");
                if(deviceScreenType == DeviceScreenType.CEIL_SCREEN){
                    mIMediaControllerServiceC = IMediaControllerService.Stub.asInterface(service);
                }else if(deviceScreenType == DeviceScreenType.PASSENGER_SCREEN){
                    mIMediaControllerServiceP = IMediaControllerService.Stub.asInterface(service);
                }else{
                    mIMediaControllerService = IMediaControllerService.Stub.asInterface(service);
                }
                if (isWait) {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (isWait) {
                    countDownLatch.countDown();
                }
            }
        }, Context.BIND_AUTO_CREATE, MediaHelper.getUserHandle(deviceScreenType));
        if (isWait) {
            try {
                countDownLatch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogUtils.d(TAG, "e = " + e);
            }
        }
    }

    final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            handleServiceConnected(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            handleServiceDisconnected();
        }
    };

    private void handleServiceConnected(IBinder service){
        mIMediaControllerService = IMediaControllerService.Stub.asInterface(service);
        LogUtils.d(TAG, "onServiceConnected bili");
    }

    private void handleServiceDisconnected(){
        LogUtils.d(TAG, "onServiceDisconnected tiktok main");
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init");
        this.context = context;
        if (MediaHelper.isSupportMultiScreen()) {
            initService(DeviceScreenType.CEIL_SCREEN, false);
            initService(DeviceScreenType.PASSENGER_SCREEN, false);
            initService(DeviceScreenType.CENTRAL_SCREEN, false);
        } else {
            LogUtils.d(TAG,"mediaSession init");
            initMediaBrowser();
        }
    }
    private void initMediaBrowser(){
        HandlerUtils.INSTANCE.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ComponentName componentName = new ComponentName(APP_NAME, SERVICE_NAME_OLD);
                mediaBrowser = new MediaBrowser(context, componentName, new MediaBrowser.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        super.onConnected();
                        LogUtils.d(TAG, "onConnected");
                        if (mediaBrowser != null && mediaBrowser.isConnected()) {
                            mediaController = new MediaController(context, mediaBrowser.getSessionToken());
                        }
                        if (mServiceConnectListener != null) {
                            mServiceConnectListener.onConnect(ConnectStatus.CONNECTED);
                        }
                    }

                    @Override
                    public void onConnectionSuspended() {
                        super.onConnectionSuspended();
                        if (mServiceConnectListener != null) {
                            mServiceConnectListener.onConnect(ConnectStatus.SUSPENDED);
                        }
                        LogUtils.d(TAG, "onConnectionSuspended");
                        mediaBrowser.disconnect();
                        mediaBrowser.connect();
                    }

                    @Override
                    public void onConnectionFailed() {
                        super.onConnectionFailed();
                        if (mServiceConnectListener != null) {
                            mServiceConnectListener.onConnect(ConnectStatus.SUSPENDED);
                        }
                        LogUtils.d(TAG, "onConnectionFailed");
                    }
                }, null);
                LogUtils.d(TAG, "mediaBrowser = " + mediaBrowser);
                if (!mediaBrowser.isConnected()) {
                    mediaBrowser.connect();
                }
            }
        });
    }

    @Override
    public void destroy(Context context) {
        LogUtils.d(TAG, "destroy");
    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre");
        mediaControl(MediaConstant.MediaControl.PRE, null);
        return null;
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next");
        mediaControl(MediaConstant.MediaControl.NEXT, null);
        return null;
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play");
        if (MediaHelper.isSupportMultiScreen()) {
            if (isPlaying()) {
                return TtsReplyUtils.getTtsBean("4003902");
            }
        }
        mediaControl(MediaConstant.MediaControl.PLAY, null);
        return null;
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay");
        Bundle bundle = new Bundle();
        bundle.putInt("value", 0);
        //哔哩哔哩replay会导致黑屏一下
        mediaControl(MediaConstant.MediaControl.SEEK, getParamValue("value",0));
        return null;
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop");
        if(!isPlaying()){
            return TtsReplyUtils.getTtsBean("4004001");
        }
        if (isExit) {
            close();
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            mediaControl(MediaConstant.MediaControl.PAUSE, null);
        }
        return null;
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        if (TextUtils.equals(seekType, "fast_forward")) {
            if (duration <= 0) {
                duration = 15;
            }
            mediaControl(MediaConstant.MediaControl.FORWARD, getParamValue("value",(int) duration));
            return null;
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            if (duration <= 0) {
                duration = 15;
            }
            mediaControl(MediaConstant.MediaControl.REVERSE, getParamValue("value",(int) duration));
            return null;
        } else if (TextUtils.equals(seekType, "set")) {
            if (duration < 0) {
                duration = 0;
            }
            mediaControl(MediaConstant.MediaControl.SEEK, getParamValue("value",(int) duration));
            return null;
        }
        return null;
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return null;
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        LogUtils.d(TAG, "switchDanmaku isOpen: " + isOpen);
        switchDanmakuOpen = isOpen;
        actionType = isOpen ? OPEN_DANMAKU : CLOSE_DANMAKU;
        mediaControl(MediaConstant.MediaControl.DAN_MA_KU_SET, getParamValue("status",isOpen?"true":"false"));
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " numberRate: " + numberRate + " level: " + level);
        if (TextUtils.equals(adjustType, "increase")) {
            mediaControl(MediaConstant.MediaControl.INCREASE_SPEED, getParamValue("status","false"));
            return null;
        } else if (TextUtils.equals(adjustType, "decrease")) {
            mediaControl(MediaConstant.MediaControl.DECREASE_SPEED, getParamValue("status","false"));
            return null;
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                mediaControl(MediaConstant.MediaControl.INCREASE_SPEED, getParamValue("status","true"));
                return null;
            } else if (TextUtils.equals(level, "min")) {
                mediaControl(MediaConstant.MediaControl.DECREASE_SPEED, getParamValue("status","true"));
                return null;
            } else {
                try {
                    float num = Float.parseFloat(numberRate);
                    LogUtils.d(TAG, "set num: " + num);
                    if (num > 0) {
                        float[] targets = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
                        float closest;
                        if (num > 2.0f) {
                            isOutRangeMaxSpeed = true;
                            closest = 2.0f;
                        } else if (num < 0.5f) {
                            isOutRangeMinSpeed = true;
                            closest = 0.5f;
                        } else {
                            closest = NumberUtils.findClosestValue(num, targets);
                        }
                        currentSpeed = closest;
                        LogUtils.d(TAG, "closest: " + closest);
                        mediaControl(MediaConstant.MediaControl.SET_SPEED, getParamValue("rate",closest));
                        return null;
                    }
                } catch (Exception e) {
                    LogUtils.d(TAG, "e: " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " mode: " + mode + " level: " + level);
        if (TextUtils.equals(adjustType, "increase")) {
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("status", false);
//            if (mediaBrowser != null && mediaBrowser.isConnected() && mediaController != null) {
//                mediaControl(MediaConstant.MediaControl.INCREASE_QUALITY, bundle);
//            } else {
//                init(context, () -> mediaControl(MediaConstant.MediaControl.INCREASE_QUALITY, bundle));
//            }
            return TtsReplyUtils.getTtsBean("4003100");
        } else if (TextUtils.equals(adjustType, "decrease")) {
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("status", false);
//            if (mediaBrowser != null && mediaBrowser.isConnected() && mediaController != null) {
//                mediaControl(MediaConstant.MediaControl.DECREASE_QUALITY, bundle);
//            } else {
//                init(context, () -> mediaControl(MediaConstant.MediaControl.DECREASE_QUALITY, bundle));
//            }
            return TtsReplyUtils.getTtsBean("4003100");
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",1080));
                return null;
            } else if (TextUtils.equals(level, "min")) {
                mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",360));
                return null;
            }

            if (!TextUtils.isEmpty(mode)) {
                String numString = NumberUtils.extractNumbers(mode);
                LogUtils.d(TAG, "numString: " + numString);
                if (TextUtils.isEmpty(numString)) {
                    // 汉字
                    switch (mode) {
                        case "超高清":
                        case "全高清":
                        case "蓝光":
                        case "超清":
                            mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",1080));
                            return null;
                        case "高清":
                        case "准高清":
                            mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",720));
                            return null;
                        case "标清":
                            mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",480));
                            return null;
                        case "流畅":
                            mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",360));
                            return null;
                        default:
                            mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",0));
                            return null;
                    }
                } else {
                    // 数字
                    int num = Integer.parseInt(numString);
                    if (mode.contains("k") || mode.contains("K")) {
                        num = num * 1000;
                    }
                    if (num > 0) {
                        float[] targets = {360, 480, 720, 1080};
//                        float closest = NumberUtils.findClosestValue(num, targets);
//                        int closestInt = (int) closest;
                        LogUtils.d(TAG, "closest: " + num);
                        mediaControl(MediaConstant.MediaControl.QUALITY_SWITCH, getParamValue("value",num));
                        return null;
                    }
                }
            }
        }
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
        LogUtils.d(TAG, "switchCollect isCollect: " + isCollect + " mediaType: " + mediaType);
        mediaControl(isCollect ? MediaConstant.MediaControl.ADD_FAVORITE : MediaConstant.MediaControl.CANCEL_FAVORITE, null);
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
        return null;
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        LogUtils.d(TAG, "switchPlayer isOpen: " + isOpen);
        mediaControl(MediaConstant.MediaControl.SET_PLAY_MODE, getParamValue("value",isOpen ? 0 : 1));
        return null;
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.d(TAG, "switchHistoryUI isOpen: " + isOpen);
//        if (DeviceHolder.INS().getDevices().getCarServiceProp().isH56D()) {
//            //56d不支持打开
//            MediaHelper.speakTts("4003100");
//            return null;
//        }
//        String param = "list:";
//        if (isOpen) {
//            if (StringUtils.isBlank(actionType)) {
//                actionType = OPEN_HISTORY_UI;
//            }
//            param+=MediaConstant.PageName.HISTORY;
//        } else {
//            if (!isFront()) {
//                MediaHelper.speakTts("4022901");
//                return null;
//            }
//            actionType = CLOSE_HISTORY_UI;
//            param+=MediaConstant.PageName.VIDEO;
//        }
//        try {
//            if (!isFront()) {
//                openApp();
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            LogUtils.d(TAG, e.toString());
//        }
//        mediaControl(MediaConstant.MediaControl.MEDIA_REQUEST, param);
//        return null;
        //新需求全车系不支持
        MediaHelper.speakTts("4003100");
        return null;
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.d(TAG, "switchCollectUI isOpen: " + isOpen);
        String param = "list:";
        if (isOpen) {
            if (MediaHelper.isSafeLimitation()) {
                MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                return null;
            }
            if (StringUtils.isBlank(actionType)) {
                actionType = OPEN_COLLECT_UI;
            }
            param += MediaConstant.PageName.FAVORITE;
        } else {
            if (!isFront()) {
                MediaHelper.speakTts("4003501");
                return null;
            }
            actionType = CLOSE_COLLECT_UI;
            param += MediaConstant.PageName.VIDEO;
        }
        try {
            if (!isFront()) {
                openApp();
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            LogUtils.d(TAG, e.toString());
        }
        mediaControl(MediaConstant.MediaControl.MEDIA_REQUEST, param);
        return null;
    }

    @Override
    public TTSBean playUI(int type) {
        if (type == 1) {
            MediaHelper.speakTts("4003100");
            return null;
//            actionType = PLAY_HISTORY;
//            switchHistoryUI(true);
        } else {
            if (MediaHelper.isSafeLimitation()) {
                MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                return null;
            }
            actionType = PLAY_COLLECT;
            switchCollectUI(true);
        }
        return null;
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }

    public void playEpisode(int value) {
        LogUtils.d(TAG, "playEpisode value: " + value);
        playEpisodeValue = value;
        mediaControl(MediaConstant.MediaControl.PLAY_EPISODE, getParamValue("value", value));
    }

    private void mediaControl(String cmd, String param) {
        if (MediaHelper.isSupportMultiScreen()) {
            BiliBroadcastReceiver.currentScreenType = ScreenType.MAIN;
            if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(cmd)) {
                BiliBroadcastReceiver.currentQuality = Integer.parseInt(param.replace("value:", ""));
            }
//        mediaController.sendCommand(cmd,bundle,resultReceiver);
            Bundle bundle = new Bundle();
            bundle.putString("command", cmd);
            if (StringUtils.isNotBlank(param)) {
                bundle.putString("bundle", param);
            }
            LogUtils.d(TAG, "sendBundle bundle = " + bundle);
            try {
                if (getmIMediaControllerService() != null) {
                    getmIMediaControllerService().sendBundle(bundle);
                } else {
                    initService(new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            handleServiceConnected(service);
                            if (mIMediaControllerService != null) {
                                try {
                                    mIMediaControllerService.sendBundle(bundle);
                                } catch (RemoteException e) {
                                    LogUtils.e(TAG, "mediaControl e = " + e);
                                }
                            }
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                        }
                    });
                }
            } catch (RemoteException e) {
                LogUtils.e(TAG, "sendBundle e = " + e);
                initService(new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        handleServiceConnected(service);
                        if (mIMediaControllerService != null) {
                            try {
                                mIMediaControllerService.sendBundle(bundle);
                            } catch (RemoteException e) {
                                LogUtils.e(TAG, "mediaControl e = " + e);
                            }
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                });
            }
        } else {
            if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(cmd)) {
                currentQuality = Integer.parseInt(param.replace("value:", ""));
            }
            Bundle bundle = new Bundle();
            if (StringUtils.isNotBlank(param)) {
                if (param.contains("value")) {
                    bundle.putInt("value", Integer.parseInt(param.replace("value:", "")));
                } else if (param.contains("status")) {
                    bundle.putBoolean("status", "true".equals(param.replace("status:", "")));
                } else if (param.contains("list")) {
                    bundle.putString("list", param.replace("list:", ""));
                } else if (param.contains("rate")) {
                    bundle.putFloat("rate", Float.parseFloat(param.replace("rate:", "")));
                }
            }
            LogUtils.d(TAG, "cmd = " + cmd);
            if (mediaController != null) {
                mediaController.sendCommand(cmd, bundle, resultReceiver);
            } else {
                mServiceConnectListener = status -> {
                    switch (status){
                        case ConnectStatus.CONNECTED:
                            mediaController.sendCommand(cmd, bundle, resultReceiver);
                            break;
                        case ConnectStatus.SUSPENDED:
                            mediaBrowser.disconnect();
                            mediaBrowser.connect();
                            break;
                        case ConnectStatus.FAILED:
                            mediaBrowser.connect();
                            break;
                    }
                };
                LogUtils.d(TAG, "mediaBrowser is " + mediaBrowser);
                initMediaBrowser();
            }
        }
    }

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    private IMediaControllerService getmIMediaControllerService(){
        if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getCeilingScreenDisplayId()) {
            if (mIMediaControllerServiceC == null) {
                initService(DeviceScreenType.CEIL_SCREEN, true);
            }
            return mIMediaControllerServiceC;
        } else if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getPassengerScreenDisplayId()) {
            if (mIMediaControllerServiceP == null) {
                initService(DeviceScreenType.PASSENGER_SCREEN, true);
            }
            return mIMediaControllerServiceP;
        } else {
            if (mIMediaControllerService == null) {
                initService(DeviceScreenType.CENTRAL_SCREEN, true);
            }
            return mIMediaControllerService;
        }
    }

    private int currentQuality;

    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())){
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String command = resultData.getString("command");
            LogUtils.d(TAG, "command = " + command + ", resultCode = " + resultCode);
            String ttsId = "";
            String ttsPart = "";
            if (resultCode == MediaConstant.MediaResult.OUT_TIME) {
                ttsId = "4000000";
            } else if (resultCode == MediaConstant.MediaResult.NOT_SUPPORT) {
                if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(command)) {
                    ttsId = "4024703";
                } else {
                    ttsId = "4003100";
                }
            } else if (resultCode == MediaConstant.MediaResult.NEED_LOGIN) {
                ttsId = "4000001";
            } else if (resultCode == MediaConstant.MediaResult.ALREADY_COLLECTION) {
                ttsId = "4004203";
            } else if (resultCode == MediaConstant.MediaResult.ALREADY_NO_COLLECTION) {
                ttsId = "4004304";
            } else if (resultCode == MediaConstant.MediaResult.ALREADY_LAST_OR_MIN) {
                if (MediaConstant.MediaControl.NEXT.equals(command)) {
                    ttsId = "4023502";
                } else if (MediaConstant.MediaControl.DECREASE_SPEED.equals(command)) {
                    ttsId = "4013605";
                }
            } else if (resultCode == MediaConstant.MediaResult.ALREADY_FIRST_OR_MAX) {
                if (MediaConstant.MediaControl.PRE.equals(command)) {
                    ttsId = "4023402";
                } else if (MediaConstant.MediaControl.INCREASE_SPEED.equals(command)) {
                    ttsId = "4013604";
                }
            } else if (resultCode == MediaConstant.MediaResult.OUT_RANGE) {
                if (MediaConstant.MediaControl.FORWARD.equals(command)) {//快进
                    ttsId = "4024003";
                } else if (MediaConstant.MediaControl.REVERSE.equals(command)) {//快退
                    ttsId = "4024103";
                } else if (MediaConstant.MediaControl.SEEK.equals(command)) {//设置进度
                    ttsId = "4024003";
                } else if (MediaConstant.MediaControl.PLAY_EPISODE.equals(command)) {//第几集
                    ttsPart = String.valueOf(playEpisodeValue);
                    ttsId = "4025003";
                } else if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(command)) {//设置清晰度
                    ttsId = "4024703";
                }
            } else if (resultCode == MediaConstant.MediaResult.REPEAT_OPERATION) {
                if (StringUtils.isBlank(actionType)) {
                    if (MediaConstant.MediaControl.SET_SPEED.equals(command)) {
                        if (isOutRangeMaxSpeed) {
                            isOutRangeMaxSpeed = false;
                            ttsPart = "2倍速";
                            ttsId = "4024503";
                        } else if (isOutRangeMinSpeed) {
                            isOutRangeMinSpeed = false;
                            ttsPart = "0.5倍速";
                            ttsId = "4024504";
                        } else {
                            ttsPart = currentSpeed + "倍速";
                            ttsId = "4024505";
                        }
                    } else if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(command)) {//设置清晰度
                        ttsPart = currentQuality + "P";
                        ttsId = "4013505";
                    } else if (MediaConstant.MediaControl.PLAY_EPISODE.equals(command)) {//播放第几集
                        ttsPart = String.valueOf(playEpisodeValue);
                        ttsId = "4025002";
                    }
                } else {
                    if (OPEN_HISTORY_UI.equals(actionType)) {
                        if (isFront()) {
                            ttsId = "4022803";
                        } else {
                            open();
                            ttsId = "4022802";
                        }
                    } else if (OPEN_COLLECT_UI.equals(actionType)) {
                        if (isFront()) {
                            ttsId = "4003403";
                        } else {
                            open();
                            ttsId = "4003402";
                        }
                    } else if (OPEN_DANMAKU.equals(actionType)) {
                        ttsId = "4024302";
                    } else if (CLOSE_DANMAKU.equals(actionType)) {
                        ttsId = "4024402";
                    } else if (CLOSE_HISTORY_UI.equals(actionType)) {
                        ttsId = "4022901";
                    } else if (CLOSE_COLLECT_UI.equals(actionType)) {
                        ttsId = "4003501";
                    } else if (PLAY_HISTORY.equals(actionType)) {
                        ttsId = "4019704";
                    } else if (PLAY_COLLECT.equals(actionType)) {
                        ttsId = "4019703";
                    }
                }
            } else if (resultCode == MediaConstant.MediaResult.SUCCESS) {
                if (MediaConstant.MediaControl.PLAY.equals(command)) {//播放
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.PAUSE.equals(command)) {//暂停
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.PRE.equals(command)) {//上一集
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.NEXT.equals(command)) {//下一集
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.SET_PLAY_MODE.equals(command)) {//设置播放模式
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.ADD_FAVORITE.equals(command)) {//收藏
                    ttsId = "4004204";
                } else if (MediaConstant.MediaControl.CANCEL_FAVORITE.equals(command)) {//取消收藏
                    ttsId = "4004303";
                } else if (MediaConstant.MediaControl.FORWARD.equals(command)) {//快进
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.REVERSE.equals(command)) {//快退
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.DAN_MA_KU_SET.equals(command)) {//弹幕
                    if (switchDanmakuOpen) {
                        ttsId = "4024303";
                    } else {
                        ttsId = "4024403";
                    }
                } else if (MediaConstant.MediaControl.SET_SPEED.equals(command)) {//设置播放速度
                    if (isOutRangeMaxSpeed) {
                        isOutRangeMaxSpeed = false;
                        ttsPart = "两倍播放速度";
                        ttsId = "4024503";
                    } else if (isOutRangeMinSpeed) {
                        isOutRangeMinSpeed = false;
                        ttsPart = "0.5倍播放速度";
                        ttsId = "4024504";
                    } else {
                        ttsId = "1100005";
                    }
                } else if (MediaConstant.MediaControl.INCREASE_SPEED.equals(command)) {//调高播放速度
                    ttsId = "4017302";
                } else if (MediaConstant.MediaControl.DECREASE_SPEED.equals(command)) {//调低播放速度
                    ttsId = "4017303";
                } else if (MediaConstant.MediaControl.QUALITY_SWITCH.equals(command)) {//设置清晰度
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.PLAY_EPISODE.equals(command)) {//设置播放集数
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.SEEK.equals(command)) {//从头播放
                    ttsId = "1100005";
                } else if (MediaConstant.MediaControl.MEDIA_REQUEST.equals(command)) {//页面跳转
                    if (CLOSE_HISTORY_UI.equals(actionType)) {
                        ttsId = "4022902";
                    } else if (OPEN_HISTORY_UI.equals(actionType)) {
                        ttsId = "4022802";
                    } else if (PLAY_HISTORY.equals(actionType)) {
                        ttsId = "4019704";
                    } else if (PLAY_COLLECT.equals(actionType)) {
                        ttsId = "4019703";
                    } else if (CLOSE_COLLECT_UI.equals(actionType)) {
                        ttsId = "4003502";
                    } else if (OPEN_COLLECT_UI.equals(actionType)) {
                        ttsId = "4003402";
                    }
                }
            }
            if (TextUtils.isEmpty(ttsId)) {
                LogUtils.d(TAG, "onReceiveResult ttsId is null");
            } else {
                String selectTTs = TtsReplyUtils.getTtsBean(ttsId).getSelectTTs();
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
                    } else if (selectTTs.contains("@{media_speed}")) {
                        MediaHelper.speak(selectTTs.replace("@{media_speed}", ttsPart));
                    } else if (selectTTs.contains("@{media_definition}")) {
                        MediaHelper.speak(selectTTs.replace("@{media_definition}", ttsPart));
                    }
                }
            }
            actionType = "";
        }
    };
}
