package com.voyah.ai.basecar.media.vedio;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.thunder.voiceinterface.ICodeResultListener;
import com.thunder.voiceinterface.IPlayStateListener;
import com.thunder.voiceinterface.VoiceMethodName;
import com.thunder.voiceinterface.VoiceServiceManager;
import com.voice.drawing.api.model.ScreenType;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.helper.MultiAppHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

public enum ThunderKtvImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = ThunderKtvImpl.class.getSimpleName();

    public static final String APP_NAME = "com.thunder.carplay";
    public static final String ACTIVITY_NAME_PASSENGER = "com.thunder.ktvlite.display.activity.ActivityScreen2";
    public static final String ACTIVITY_NAME_CEIL = "com.thunder.ktvlite.display.activity.ActivityScreen3";

    private Context context;

    private boolean isConnect;

    public boolean isFront() {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId())||
                MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getCeilingScreenDisplayId())||
                MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
        LogUtils.d(TAG, "isFront = " + foregroundApp);
        return foregroundApp;
    }

    public boolean isFrontByDisplayid(int displayid) {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME,displayid);
        LogUtils.d(TAG, "isFront = " + foregroundApp);
        return foregroundApp;
    }

    public boolean isPlaying() {
        if (isFront()) {
            LogUtils.d(TAG, "isPlaying = " + VoiceServiceManager.getInstance().isPlaying());
            if (DeviceHolder.INS().getDevices().getSystem().getApp().isInstalledByAppName("雷石KTV")) {
                return VoiceServiceManager.getInstance().isPlaying();
            } else {
                return false;
            }
        } else {
            LogUtils.d(TAG, "isPlaying = false");
            return false;
        }
    }

    public boolean isFrontWithPosition(String position) {
        boolean foreGroundApp;
        if (FuncConstants.VALUE_SCREEN_PASSENGER.equals(position)) {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
        } else if (FuncConstants.VALUE_SCREEN_CEIL.equals(position)) {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getCeilingScreenDisplayId());
        } else {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
        }
        LogUtils.d(TAG, "isFront: " + foreGroundApp);
        return foreGroundApp;
    }

    public boolean isFrontWithPosition(int position) {
        boolean foreGroundApp;
        if (ScreenType.PASSENGER == position) {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getPassengerScreenDisplayId());
        } else if (ScreenType.CEILING == position) {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getCeilingScreenDisplayId());
        } else {
            foreGroundApp = MediaHelper.isAppForeGround(APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
        }
        LogUtils.d(TAG, "isFront: " + foreGroundApp);
        return foreGroundApp;
    }

    public TTSBean open(boolean isOpen,String position) {
        LogUtils.d(TAG, isOpen ? "open" : "close");
        String screenName;
        if (FuncConstants.VALUE_SCREEN_PASSENGER.equals(position)) {
            screenName = "副驾屏";
        } else if (FuncConstants.VALUE_SCREEN_CEIL.equals(position)) {
            screenName = "吸顶屏";
        } else {
            screenName = MediaHelper.SCREEN_NAME_CENTRAL;
        }
        TTSBean ttsBean;
        if (isOpen) {
            if (isFrontWithPosition(position)) {
                ttsBean = MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaTtsManager.APP_NAME_KTV,screenName);
            } else {
                if (MediaHelper.isSafeLimitation() && FuncConstants.VALUE_SCREEN_CENTRAL.equals(position)) {
                    MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                    return null;
                }
                //判断目标包名是否是支持同看的应用,如果是同看应用，需要做如下适配
                if (MirrorServiceManager.INSTANCE.isAllowMirroredApps(APP_NAME)) {
                    //启动屏id
                    boolean handled = MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(APP_NAME, MegaDisplayHelper.getVoiceDisplayId());
                    //如果返回true，说明此事件被多屏同看接管
                    if (!handled) {
                        //handled=false 说明多屏同看不处理此事件， 正常启动
                        openApp(position);
                    }
                } else {
                    // 非支持同看的应用，正常启动
                    openApp(position);
                }
                ttsBean = MediaTtsManager.getInstance().getOpenAppTts(MediaTtsManager.APP_NAME_KTV,screenName);
            }
        } else {
            if (isFrontWithPosition(position)) {
                MediaHelper.closeApp(APP_NAME, DeviceScreenType.fromValue(position));
                ttsBean = MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_KTV,screenName);
            } else {
                ttsBean = MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaTtsManager.APP_NAME_KTV,screenName);
            }
        }
        return ttsBean;
    }

    private void openApp(String position){
        MediaHelper.openApp(APP_NAME,DeviceScreenType.fromValue(position));
    }

    private ICodeResultListener iCodeResultListener = new ICodeResultListener.Stub() {
        @Override
        public void handlerResultCode(int methodName, int code) throws RemoteException {
            LogUtils.d(TAG, "methodName: " + methodName + " code: " + code);
            int ttsId = 4003100;
            if (methodName == VoiceMethodName.METHOD_SEARCH) {
                if (code == 4041001) {
                    // 点播成功
                    ttsId = code;
                } else if (code == 4041000) {
                    // 网络异常
                    // TODO: 2024/10/24 sdk未返回错误码
                    ttsId = 4000000;
                } else if (code == 4041003) {
                    // 未搜到
                    ttsId = code;
                }
            } else if (methodName == VoiceMethodName.METHOD_REPLAY) {
                if (code == 4042101) {
                    // 重播成功
                    ttsId = 1100005;
                }
            } else if (methodName == VoiceMethodName.METHOD_NEXT) {
                if (code == 4042101) {
                    // 下一首成功
                    ttsId = 1100005;
                } else if (code == 4042205) {
                    // 最后一首
                    ttsId = 4042203;
                } else if (code == 4042202) {
                    ttsId = 4000001;
                }
            } else if (methodName == VoiceMethodName.METHOD_PLAY) {
                if (code == 4042101) {
                    // 播放成功
                    ttsId = 1100005;
                } else if (code == 4003902) {
                    // 播放中
                    ttsId = code;
                }
            } else if (methodName == VoiceMethodName.METHOD_PAUSE) {
                if (code == 4042101) {
                    // 暂停成功
                    ttsId = 1100005;
                } else if (code == 4004001) {
                    // 已经暂停了
                    ttsId = code;
                }
            } else if (methodName == VoiceMethodName.METHOD_CHANGED_ORIGINAL) {
                if (code == 4042801) {
                    // 已开启原唱
                    ttsId = code;
                } else if (code == 4042802) {
                    // 开启原唱
                    ttsId = code;
                } else if (code == 4042803) {
                    // 不支持
                    ttsId = code;
                }
            } else if (methodName == VoiceMethodName.METHOD_CHANGED_ACCOMPANY) {
                if (code == 4042901) {
                    // 已开启伴唱
                    ttsId = code;
                } else if (code == 4042902) {
                    // 开启伴唱
                    ttsId = code;
                }
            }
            LogUtils.d(TAG, "ttsId: " + TtsReplyUtils.getTtsBean(String.valueOf(ttsId)).getSelectTTs());
            MediaHelper.speak(TtsReplyUtils.getTtsBean(String.valueOf(ttsId)).getSelectTTs());
        }
    };

    private PlayStateListener listener;

    private KtvConnectListener connectListener;

    public interface PlayStateListener {
        void onPlay();

        void onStop();
    }

    public interface KtvConnectListener {
        void onConnect();
    }

    public void registerPlayStateListener(PlayStateListener listener) {
        this.listener = listener;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtils.d(TAG, "onServiceConnected");
            isConnect = true;

            if (connectListener != null) {
                connectListener.onConnect();
            }

            if (listener != null) {
                if (isPlaying()) {
                    listener.onPlay();
                } else {
                    listener.onStop();
                }
            }

            VoiceServiceManager.getInstance().registerPlayStateListener(iPlayStateListener);
            VoiceServiceManager.getInstance().registerCodeResultListener(iCodeResultListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.d(TAG, "onServiceDisconnected");
            isConnect = false;
            if (listener != null) {
                listener.onStop();
            }
        }
    };

    public IPlayStateListener.Stub iPlayStateListener = new IPlayStateListener.Stub() {
        @Override
        public void onCompletion() throws RemoteException {
            LogUtils.d(TAG, "IPlayStateListener onCompletion");
            if (listener != null) {
                listener.onStop();
            }
        }

        @Override
        public void onPlay() throws RemoteException {
            LogUtils.d(TAG, "IPlayStateListener onPlay");
            if (listener != null) {
                listener.onPlay();
            }
        }

        @Override
        public void onPause() throws RemoteException {
            LogUtils.d(TAG, "IPlayStateListener onPause");
            if (listener != null) {
                listener.onStop();
            }
        }

        @Override
        public void onStop() throws RemoteException {
            LogUtils.d(TAG, "IPlayStateListener onStop");
            if (listener != null) {
                listener.onStop();
            }
        }

        @Override
        public void onError(String s) throws RemoteException {
            LogUtils.d(TAG, "IPlayStateListener onError: " + s);
            if (listener != null) {
                listener.onStop();
            }
        }
    };

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init");
        this.context = context;
        VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
    }

    @Override
    public void destroy(Context context) {
        LogUtils.d(TAG, "destroy");
        VoiceServiceManager.getInstance().unregisterCodeResultListener(iCodeResultListener);
        VoiceServiceManager.getInstance().unregisterPlayStateListener(iPlayStateListener);
        VoiceServiceManager.getInstance().unBindServer(context);
    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre isConnect: " + isConnect);
//        if (isConnect) {
//            VoiceServiceManager.getInstance().replay();// 同replay
//        } else {
//            connectListener = () -> VoiceServiceManager.getInstance().replay();
//            VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
//        }
        return TtsReplyUtils.getTtsBean("4003100");
//        return TtsReplyUtils.getTtsBean("4042101");
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next isConnect: " + isConnect);
        if (isConnect) {
            VoiceServiceManager.getInstance().nextSong();
        } else {
            connectListener = () -> VoiceServiceManager.getInstance().nextSong();
            VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
        }
//        return TTSIDConvertHelper.getInstance().getTTSBean("4042101");
        return null;
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play isConnect: " + isConnect);
        if (isConnect) {
            VoiceServiceManager.getInstance().playOrPause(true);
        } else {
            connectListener = () -> VoiceServiceManager.getInstance().playOrPause(true);
            VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
        }
//        return TTSIDConvertHelper.getInstance().getTTSBean("4042101");
        return null;
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay isConnect: " + isConnect);
//        if (isConnect) {
//            VoiceServiceManager.getInstance().replay();
//        } else {
//            connectListener = () -> VoiceServiceManager.getInstance().replay();
//            VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
//        }
//        return TTSIDConvertHelper.getInstance().getTTSBean("4042101");
        return pre();
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop isConnect: " + isConnect);
        if (isExit) {
//            VoiceServiceManager.getInstance().exitApplication();
            CommonSystemUtils.forceStopPackage(context, APP_NAME);
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            if (isConnect) {
                VoiceServiceManager.getInstance().playOrPause(false);
            } else {
                connectListener = () -> VoiceServiceManager.getInstance().playOrPause(false);
                VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
            }
        }
//        return TTSIDConvertHelper.getInstance().getTTSBean("4042101");
        return null;
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration + " isConnect: " + isConnect);
        if (TextUtils.equals(seekType, "set")) {
            if (duration <= 0) {
                return replay();
            }
        }
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        return TtsReplyUtils.getTtsBean("4003100");
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
        return TtsReplyUtils.getTtsBean("4003100");
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
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchPlayer(boolean isOpen) {
        return TtsReplyUtils.getTtsBean("4003100");
    }

    @Override
    public TTSBean switchOriginalSinging(boolean isOriginal) {
        LogUtils.d(TAG, "isOriginal: " + isOriginal + " isConnect: " + isConnect);
        if (isConnect) {
            VoiceServiceManager.getInstance().originalSinging(isOriginal);
        } else {
            connectListener = () -> VoiceServiceManager.getInstance().originalSinging(isOriginal);
            VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
        }
//        return TTSIDConvertHelper.getInstance().getTTSBean(isOriginal ? "4042802" : "4042902");
        return null;
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
        LogUtils.d(TAG, "play mediaName: " + mediaName + " ,mediaArtist: " + mediaArtist + " isConnect: " + isConnect);
        if (!TextUtils.isEmpty(mediaName) || !TextUtils.isEmpty(mediaArtist)) {
            if (MediaHelper.isSafeLimitationAndMain(VideoControlCenter.getInstance().getCurrentDisplayId())) {
                MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                return null;
            }
            if (TextUtils.isEmpty(mediaName)) {
                mediaName = "";
            }
            if (TextUtils.isEmpty(mediaArtist)) {
                mediaArtist = "";
            }
            if (isConnect) {
                VoiceServiceManager.getInstance().searchSong(context, VideoControlCenter.getInstance().getCurrentDisplayId(), mediaName, mediaArtist);
            } else {
                String finalMediaName = mediaName;
                String finalMediaArtist = mediaArtist;
                connectListener = () -> VoiceServiceManager.getInstance().searchSong(context, VideoControlCenter.getInstance().getCurrentDisplayId(), finalMediaName, finalMediaArtist);
                VoiceServiceManager.getInstance().bindServer(context, serviceConnection);
            }
//            return TTSIDConvertHelper.getInstance().getTTSBean("4041001");
            return null;
        } else {
            return VideoControlCenter.getInstance().switchVideoApp(true, APP_NAME);
        }
    }
}
