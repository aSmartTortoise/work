package com.voyah.ai.basecar.media.vedio;

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

import com.bytedance.variants.platform.IMediaSeesionCallback;
import com.bytedance.variants.platform.IMediaSeesionOnCommandCB;
import com.mega.nexus.content.MegaContext;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.ConnectStatus;
import com.voyah.ai.basecar.media.ServiceConnectListener;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.common.utils.HandlerUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public enum TiktokImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = TiktokImpl.class.getSimpleName();
    private Context context;
    private boolean isPlaying = false;
    private boolean isPlayingPassenger = false;
    private boolean isPlayingCeiling = false;

    public static final String APP_NAME = "com.bytedance.byteautoservice3";
    private static final String SERVICE_NAME = "com.bytedance.variants.platform.MediaSessionAidlService";
    private static final String SERVICE_NAME_OLD = "com.bytedance.mediaservice.MediaSessionService";

    private static final String PREV = "com.bytedance.byteautoservice.action.prev";
    private static final String NEXT = "com.bytedance.byteautoservice.action.next";
    private static final String PLAY = "com.bytedance.byteautoservice.action.play";
    private static final String PAUSE = "com.bytedance.byteautoservice.action.pause";

    private static final String LIKE = "com.bytedance.byteautoservice.action.like";
    private static final String DISLIKE = "com.bytedance.byteautoservice.action.dislike";
    private static final String COMMENT_OPEN = "com.bytedance.byteautoservice.action.comment_open";
    private static final String COMMENT_CLOSE = "com.bytedance.byteautoservice.action.comment_close";
    private static final String FOLLOW = "com.bytedance.byteautoservice.action.follow";

    private static final int RESULT_CODE_SUCCESS = 0;
    private static final int RESULT_CODE_FAIL = 1;
    private static final int RESULT_CODE_REPEAT = 2;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private MediaBrowser mediaBrowser;
    private MediaController mediaController;

    final CountDownLatch countDownLatch = new CountDownLatch(1);
    private ServiceConnectListener mServiceConnectListener;

    public boolean isFront() {
        return MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
    }

    public boolean isFrontByDisplayid(int displayid) {
        return MediaHelper.isAppForeGround(APP_NAME, displayid);
    }
    public boolean isFrontAndPlaying(){
        return isFront()&&isPlaying();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPlaying(int displayId) {
        if (displayId == MediaHelper.getCeilingScreenDisplayId()) {
            return isPlayingCeiling;
        } else if (displayId == MediaHelper.getPassengerScreenDisplayId()) {
            return isPlayingPassenger;
        } else {
            return isPlaying;
        }
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setPlayingPassenger(boolean playing) {
        isPlayingPassenger = playing;
    }

    public void setPlayingCeiling(boolean playing) {
        isPlayingCeiling = playing;
    }

    /**
     * 打开/关闭历史页面不支持，默认返回false，让流程走到{@link #switchHistoryUI(boolean)}
     */
    public boolean isHistoryFront() {
        return false;
    }

    /**
     * 打开/关闭收藏列表不支持，默认返回false，让流程走到{@link #switchCollectUI(boolean)}
     */
    public boolean isCollectFront() {
        return false;
    }

    public TTSBean switchApp(boolean isOpen){
        if (isOpen) {
            if (isFront()) {
                return MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaTtsManager.APP_NAME_TIKTOK, MediaHelper.SCREEN_NAME_CENTRAL);
            } else {
                if (MediaHelper.isSafeLimitation()) {
                    return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
                }
                open();
                return MediaTtsManager.getInstance().getOpenAppTts(MediaTtsManager.APP_NAME_TIKTOK, MediaHelper.SCREEN_NAME_CENTRAL);
            }
        } else {
            if (isFront()) {
                close();
                return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_TIKTOK, MediaHelper.SCREEN_NAME_CENTRAL);
            } else {
                if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                    MediaHelper.backToHome(DeviceScreenType.CENTRAL_SCREEN);
                    return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_TIKTOK, MediaHelper.SCREEN_NAME_CENTRAL);
                } else {
                    return MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaTtsManager.APP_NAME_TIKTOK, MediaHelper.SCREEN_NAME_CENTRAL);
                }
            }
        }
    }

    public void open() {
        LogUtils.d(TAG, "open");
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

    private void openApp(){
        MediaHelper.openApp(APP_NAME,DeviceScreenType.CENTRAL_SCREEN);
    }

    public void openAndPlay(){
        open();
        play();
    }

    public void close() {
        LogUtils.d(TAG, "close");
        MediaHelper.closeApp(APP_NAME,DeviceScreenType.CENTRAL_SCREEN);
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG,"init main");
        this.context = context;
        if (MediaHelper.isSupportMultiScreen()) {
            initService(DeviceScreenType.CEIL_SCREEN, false);
            initService(DeviceScreenType.PASSENGER_SCREEN, false);
            initService(DeviceScreenType.CENTRAL_SCREEN, false);
        } else {
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
                        LogUtils.d(TAG, "onConnectionSuspended");
                        if (mServiceConnectListener != null) {
                            mServiceConnectListener.onConnect(ConnectStatus.SUSPENDED);
                        }
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

    private void initService(ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(APP_NAME, SERVICE_NAME));
        MegaContext.bindServiceAsUser(context, intent, serviceConnection, Context.BIND_AUTO_CREATE, MegaUserHandle.of(0));
    }

    private void initService(DeviceScreenType deviceScreenType,boolean isWait) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(APP_NAME, SERVICE_NAME));
        MegaContext.bindServiceAsUser(context, intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (deviceScreenType == DeviceScreenType.CEIL_SCREEN) {
                    mIMediaSeesionOnCommandCBC = IMediaSeesionOnCommandCB.Stub.asInterface(service);
                    try {
                        mIMediaSeesionOnCommandCBC.registerCallback(iMediaSeesionCallback);
                    } catch (RemoteException e) {
                        LogUtils.e(TAG, "onServiceDisconnected e = " + e);
                    }
                } else if (deviceScreenType == DeviceScreenType.PASSENGER_SCREEN) {
                    mIMediaSeesionOnCommandCBP = IMediaSeesionOnCommandCB.Stub.asInterface(service);
                    try {
                        mIMediaSeesionOnCommandCBP.registerCallback(iMediaSeesionCallback);
                    } catch (RemoteException e) {
                        LogUtils.e(TAG, "onServiceDisconnected e = " + e);
                    }
                } else {
                    mIMediaSeesionOnCommandCB = IMediaSeesionOnCommandCB.Stub.asInterface(service);
                    try {
                        mIMediaSeesionOnCommandCB.registerCallback(iMediaSeesionCallback);
                    } catch (RemoteException e) {
                        LogUtils.e(TAG, "onServiceDisconnected e = " + e);
                    }
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

    private IMediaSeesionOnCommandCB mIMediaSeesionOnCommandCB;
    private IMediaSeesionOnCommandCB mIMediaSeesionOnCommandCBC;
    private IMediaSeesionOnCommandCB mIMediaSeesionOnCommandCBP;

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
        mIMediaSeesionOnCommandCB = IMediaSeesionOnCommandCB.Stub.asInterface(service);
        try {
            mIMediaSeesionOnCommandCB.registerCallback(iMediaSeesionCallback);
        } catch (RemoteException e) {
            LogUtils.e(TAG, "onServiceDisconnected e = " + e);
        }
        LogUtils.d(TAG, "onServiceConnected tiktok main");
    }

    private void handleServiceDisconnected(){
        LogUtils.d(TAG, "onServiceDisconnected tiktok main");
        try {
            mIMediaSeesionOnCommandCB.unregisterCallback(iMediaSeesionCallback);
        } catch (RemoteException e) {
            LogUtils.e(TAG, "onServiceDisconnected e = " + e);
        }
    }

    @Override
    public void destroy(Context context) {
        try {
            mIMediaSeesionOnCommandCB.unregisterCallback(iMediaSeesionCallback);
        } catch (RemoteException e) {
            LogUtils.e(TAG, "destroy e = " + e);
        }
    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre");
        Bundle bundle = new Bundle();
        mediaControl(PREV, bundle);
        return null;
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next");
        Bundle bundle = new Bundle();
        mediaControl(NEXT, bundle);
        return null;
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play");
        Bundle bundle = new Bundle();
        mediaControl(PLAY, bundle);
        return null;
    }

    @Override
    public TTSBean replay() {
//        if (mediaController != null) {
//            mediaController.getTransportControls().seekTo(0);
//        }
//        return TTSAnsConstant.OK;
        return null;
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop");
        if (isExit) {
            close();
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            Bundle bundle = new Bundle();
            mediaControl(PAUSE, bundle);
        }
        return null;
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean queryPlayInfo() {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean jump() {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
//        LogUtils.d(TAG, "switchCollect isCollect: " + isCollect + " mediaType: " + mediaType);
//        if (mediaController != null) {
//            mediaController.getTransportControls().sendCustomAction(isCollect ? "addFavorite" : "cancelFavorite", null);
//        }
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchComment(boolean isComment, String mediaType) {
        LogUtils.d(TAG, "switchComment isComment: " + isComment);
        if (MediaHelper.isSafeLimitation()) {
            MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
            return null;
        }
        Bundle bundle = new Bundle();
        mediaControl(isComment ? COMMENT_OPEN : COMMENT_CLOSE, bundle);
        return null;
    }

    @Override
    public TTSBean switchLike(boolean isLike, String mediaType) {
        LogUtils.d(TAG, "switchLike isLike: " + isLike);
        Bundle bundle = new Bundle();
        mediaControl(isLike ? LIKE : DISLIKE, bundle);
        return null;
    }

    @Override
    public TTSBean switchAttention(boolean isAttention, String mediaType) {
        LogUtils.d(TAG, "switchAttention isAttention: " + isAttention);
        if (isAttention) {
            Bundle bundle = new Bundle();
            mediaControl(FOLLOW, bundle);
        } else {
            speakNotSupport();
        }
        return null;
    }

    @Override
    public TTSBean switchPlayList(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
//        if (mediaController != null) {
//            Bundle bundle = new Bundle();
//            bundle.putInt("value", isOpen ? 0 : 1);
//            mediaController.getTransportControls().sendCustomAction("setPlayMode", bundle);
//        }
        speakNotSupport();
        return null;
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.d(TAG, "switchHistoryUI isOpen = " + isOpen);
        speakNotSupport();
        return null;
//        if (isOpen) {
//            if (mediaController != null) {
//                Bundle bundle = new Bundle();
//                bundle.putString("list", "history");
//                mediaController.getTransportControls().sendCustomAction("mediaRequest", bundle);
//            }
//        } else {
//            MegaForegroundUtils.backToLauncher(context);
//        }
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.d(TAG, "switchCollectUI isOpen = " + isOpen);
        speakNotSupport();
        return null;
//        if (isOpen) {
//            if (mediaController != null) {
//                Bundle bundle = new Bundle();
//                bundle.putString("list", "history");
//                mediaController.getTransportControls().sendCustomAction("favorite", bundle);
//            }
//        } else {
//            MegaForegroundUtils.backToLauncher(context);
//        }
    }

    @Override
    public TTSBean playUI(int type) {
        return null;
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }

    private void mediaControl(String cmd, Bundle bundle) {
        LogUtils.d(TAG, "mediaControl cmd = " + cmd);
        if(MediaHelper.isSupportMultiScreen()) {
            try {
                if (getmIMediaSeesionOnCommandCB() != null) {
                    getmIMediaSeesionOnCommandCB().onICommand(cmd, bundle);
                } else {
                    initService(new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            handleServiceConnected(service);
                            if (mIMediaSeesionOnCommandCB != null) {
                                try {
                                    mIMediaSeesionOnCommandCB.onICommand(cmd, bundle);
                                } catch (RemoteException e) {
                                    LogUtils.e(TAG, "mediaControl e = " + e);
                                }
                            }
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            handleServiceDisconnected();
                        }
                    });
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "mediaControl e = " + e);
                initService(new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        handleServiceConnected(service);
                        if (mIMediaSeesionOnCommandCB != null) {
                            try {
                                mIMediaSeesionOnCommandCB.onICommand(cmd, bundle);
                            } catch (RemoteException e) {
                                LogUtils.e(TAG, "mediaControl e = " + e);
                            }
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        handleServiceDisconnected();
                    }
                });
            }
        } else {
            if (mediaController != null) {
                mediaController.sendCommand(cmd, bundle, resultReceiver);
            } else {
                mServiceConnectListener = status -> {
                    switch (status) {
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

    private IMediaSeesionOnCommandCB getmIMediaSeesionOnCommandCB(){
        if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getCeilingScreenDisplayId()) {
            if (mIMediaSeesionOnCommandCBC == null) {
                initService(DeviceScreenType.CEIL_SCREEN, true);
            }
            return mIMediaSeesionOnCommandCBC;
        } else if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getPassengerScreenDisplayId()) {
            if (mIMediaSeesionOnCommandCBP == null) {
                initService(DeviceScreenType.PASSENGER_SCREEN, true);
            }
            return mIMediaSeesionOnCommandCBP;
        } else {
            if (mIMediaSeesionOnCommandCB == null) {
                initService(DeviceScreenType.CENTRAL_SCREEN, true);
            }
            return mIMediaSeesionOnCommandCB;
        }
    }

    private final ResultReceiver resultReceiver = new ResultReceiver(mainHandler){
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            handleResult(resultData, resultCode);
        }
    };

    private final IMediaSeesionCallback iMediaSeesionCallback = new IMediaSeesionCallback.Stub() {
        @Override
        public void onTTS(Bundle data) throws RemoteException {
            handleResult(data,Integer.parseInt(data.getString("tts_action_code")));
        }
    };

    private void handleResult(Bundle data,int resultCode){
        if (data != null && !data.keySet().isEmpty()) {
            String[] keys = data.keySet().toArray(new String[0]);
            String command = data.getString("tts_action_command");
            boolean isLogin = data.getBoolean("tts_action_islogin");
            LogUtils.d(TAG, "onReceiveResult resultCode = " + resultCode + ", command = " + command+", isLogin = " + isLogin);
//                for (String key : keys) {
//                    LogUtils.d(TAG, "key = " + key + ", value = " + data.get(key));
//                }
            String ttsId = "";
            if (resultCode == RESULT_CODE_FAIL) {
                if (LIKE.equals(command) || DISLIKE.equals(command) || FOLLOW.equals(command) && !isLogin) {
                    ttsId = "4000001";
                }else{
                    ttsId = "1100005";
                }
            } else if (resultCode == RESULT_CODE_REPEAT) {
                if (PAUSE.equals(command)) {
                    ttsId = "4004001";
                } else if (PLAY.equals(command)) {
                    ttsId = "4003902";
                } else if (COMMENT_OPEN.equals(command)) {
                    ttsId = "4034602";
                } else if (COMMENT_CLOSE.equals(command)) {
                    ttsId = "4034701";
                } else if(FOLLOW.equals(command) && isLogin){
                    ttsId = "1100005";
                } else if (LIKE.equals(command)) {//点赞
                    ttsId = "4035404";
                } else if (DISLIKE.equals(command)) {//取消点赞
                    ttsId = "4035503";
                }
            }else if (resultCode == RESULT_CODE_SUCCESS) {
                if (PLAY.equals(command)) {//播放
                    ttsId = "1100005";
                } else if (PAUSE.equals(command)) {//暂停
                    ttsId = "1100005";
                } else if (PREV.equals(command)) {//上一集
                    ttsId = "1100005";
                } else if (NEXT.equals(command)) {//下一集
                    ttsId = "1100005";
                } else if (COMMENT_OPEN.equals(command)) {//打开评论
                    ttsId = "4034601";
                } else if (COMMENT_CLOSE.equals(command)) {//关闭评论
                    ttsId = "4034702";
                } else if (LIKE.equals(command)) {//点赞
                    ttsId = "4035404";
                } else if (DISLIKE.equals(command)) {//取消点赞
                    ttsId = "4035503";
                } else if (FOLLOW.equals(command)) {//关注
                    ttsId = "4035604";
                }
            }
            if (TextUtils.isEmpty(ttsId)) {
                LogUtils.d(TAG, "onReceiveResult ttsId is null");
            } else {
                String selectTTs = TtsReplyUtils.getTtsBean(ttsId).getSelectTTs();
                LogUtils.d(TAG, "onReceiveResult tts is " + selectTTs);
                MediaHelper.speak(selectTTs);
            }
        } else {
            LogUtils.d(TAG, "onReceiveResult resultData is null");
            speakNotSupport();
        }
    }


    private void speakNotSupport() {
        MediaHelper.speak(TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
    }
}
