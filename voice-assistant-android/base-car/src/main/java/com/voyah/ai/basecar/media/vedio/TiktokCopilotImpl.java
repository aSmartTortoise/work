package com.voyah.ai.basecar.media.vedio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.bytedance.variants.platform.IMediaSeesionCallback;
import com.bytedance.variants.platform.IMediaSeesionOnCommandCB;
import com.mega.nexus.content.MegaContext;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.helper.MultiAppHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

public enum TiktokCopilotImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = TiktokCopilotImpl.class.getSimpleName();
    private Context context;
    private boolean isPlaying = false;

    public static final String APP_NAME = "com.bytedance.byteautoservice3";
    private static final String SERVICE_NAME = "com.bytedance.variants.platform.MediaSessionAidlService";

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

    public boolean isFront() {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
        return foregroundApp;
    }

    public boolean isFrontAndPlaying(){
        return isFront()&&isPlaying();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
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
            if(isFront()) {
                return TtsReplyUtils.getTtsBean("1100029", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER,"@{app_name}", "车鱼视听");
            }else{
                open();
                return TtsReplyUtils.getTtsBean("1100030", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER,"@{app_name}", "车鱼视听");
            }
        } else {
            if (isFront()) {
                close();
                return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "车鱼视听");
            } else {
                if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                    MediaHelper.backToHome(DeviceScreenType.PASSENGER_SCREEN);
                    return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "车鱼视听");
                } else {
                    return TtsReplyUtils.getTtsBean("1100032", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER, "@{app_name}", "车鱼视听");
                }
            }
        }
    }

    public void open() {
        LogUtils.d(TAG, "open");
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
    }

    private void openApp(){
        MediaHelper.openApp(APP_NAME,DeviceScreenType.PASSENGER_SCREEN);
    }

    public void openAndPlay(){
        open();
        play();
    }

    public void close() {
        LogUtils.d(TAG, "close");
        MediaHelper.closeApp(APP_NAME,DeviceScreenType.PASSENGER_SCREEN);
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG,"init copilot");
        this.context = context;
        initService(mServiceConnection);
    }

    private void initService(ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(APP_NAME, SERVICE_NAME));
        MegaContext.bindServiceAsUser(context, intent, serviceConnection, Context.BIND_AUTO_CREATE, MediaHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN));
    }

    private IMediaSeesionOnCommandCB mIMediaSeesionOnCommandCB;

    final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMediaSeesionOnCommandCB = IMediaSeesionOnCommandCB.Stub.asInterface(service);
            try {
                mIMediaSeesionOnCommandCB.registerCallback(iMediaSeesionCallback);
            } catch (RemoteException e) {
                LogUtils.e(TAG, "onServiceDisconnected e = " + e);
            }
            LogUtils.d(TAG, "onServiceConnected tiktok copilot");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "onServiceDisconnected tiktok copilot");
            try {
                mIMediaSeesionOnCommandCB.unregisterCallback(iMediaSeesionCallback);
            } catch (RemoteException e) {
                LogUtils.e(TAG, "onServiceDisconnected e = " + e);
            }
        }
    };

    private void handleServiceConnected(IBinder service){
        mIMediaSeesionOnCommandCB = IMediaSeesionOnCommandCB.Stub.asInterface(service);
        try {
            mIMediaSeesionOnCommandCB.registerCallback(iMediaSeesionCallback);
        } catch (RemoteException e) {
            LogUtils.e(TAG, "onServiceDisconnected e = " + e);
        }
        LogUtils.d(TAG, "onServiceConnected tiktok ceil");
    }

    private void handleServiceDisconnected(){
        LogUtils.d(TAG, "onServiceDisconnected tiktok ceil");
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
        return null;
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        return null;
    }

    @Override
    public TTSBean switchHistoryUI(boolean isOpen) {
        LogUtils.d(TAG, "switchHistoryUI isOpen = " + isOpen);
//        speakNotSupport();
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
//        speakNotSupport();
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
        try {
            if (mIMediaSeesionOnCommandCB != null) {
                mIMediaSeesionOnCommandCB.onICommand(cmd, bundle);
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
        } catch (RemoteException e) {
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
    }

    private final IMediaSeesionCallback iMediaSeesionCallback = new IMediaSeesionCallback.Stub() {
        @Override
        public void onTTS(Bundle data) throws RemoteException {
            if (data != null && !data.keySet().isEmpty()) {
                String[] keys = data.keySet().toArray(new String[0]);
                String command = data.getString("tts_action_command");
                boolean isLogin = data.getBoolean("tts_action_islogin");
                int resultCode = Integer.parseInt(data.getString("tts_action_code"));
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
    };

    private void speakNotSupport() {
        MediaHelper.speak(TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
    }
}
