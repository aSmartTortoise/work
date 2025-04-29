package com.voyah.ai.basecar.media.vedio;

import android.content.Context;
import android.text.TextUtils;

import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.MediaTtsManager;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.mirror.MirrorServiceManager;

import java.util.Objects;

import cn.cmvideo.car.connect.ConnectListener;
import cn.cmvideo.car.connect.MiguHelper;
import cn.cmvideo.car.connect.MiguService;

public enum MiguImpl implements MediaInterface {
    INSTANCE;
    private static final String TAG = MiguImpl.class.getSimpleName();

    public static final String APP_NAME = "cn.cmvideo.car.play";
    //    public static final String COLLECT_ACTIVITY = "cn.cmvideo.car.setting.ui.UserCollectionActivity";
//    public static final String HISTORY_ACTIVITY = "cn.cmvideo.car.setting.ui.UserHistoryActivity";
    public static final String PLAY_ACTIVITY = "cn.miguvideo.migucar.libplaydetail.PlayerActivity";
    float[] speedArr = {0.5f, 1.0f, 1.25f, 1.5f, 2.0f};

    private MiguService miguService;
    private MiguService miguServiceP;
    private MiguService miguServiceC;

    private boolean isPlayCollect = false;
    private boolean isOutRangeMaxSpeed = false;
    private boolean isOutRangeMinSpeed = false;
    private boolean isPlaying = false;
    private boolean isPlayingP = false;
    private boolean isPlayingC = false;

    private Context context;

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setPlayingP(boolean isPlaying) {
        this.isPlayingP = isPlaying;
    }

    public void setPlayingC(boolean isPlaying) {
        this.isPlayingC = isPlaying;
    }

    public boolean isFront() {
        boolean foregroundApp = MediaHelper.isAppForeGround( APP_NAME, MegaDisplayHelper.getMainScreenDisplayId());
        return foregroundApp;
    }

    public boolean isFrontByDisplayid(int displayid) {
        boolean foregroundApp = MediaHelper.isAppForeGround(APP_NAME, displayid);
        return foregroundApp;
    }

    public boolean isPlayPage() {
        String playPage = MediaHelper.getTopActivityName(MegaDisplayHelper.getMainScreenDisplayId());
        return PLAY_ACTIVITY.equals(playPage);
    }

    public boolean isPlayPage(int displayId) {
        String playPage = MediaHelper.getTopActivityName(displayId);
        return PLAY_ACTIVITY.equals(playPage);
    }

    public boolean isPlaying() {
        if (MediaHelper.isSupportMultiScreen()) {
            return isPlaying;
        } else {
            if (miguService != null && miguService.isConnect()) {
                LogUtils.d(TAG, "isPlaying = " + miguService.isPlaying());
                return miguService.isPlaying();
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

    public boolean isCollectFront() {
        boolean ret = false;
        if (getMiguService() != null && getMiguService().isConnect()) {
            ret = getMiguService().isOpenCollectActivity();
        }
        LogUtils.d(TAG, "isCollectFront: " + ret);
        return ret;
    }

    public boolean isHistoryFront() {
        boolean ret = false;
        if (getMiguService() != null && getMiguService().isConnect()) {
            ret = getMiguService().isOpenHistoryActivity();
        }
        LogUtils.d(TAG, "isHistoryFront: " + ret);
        return ret;
    }

    public boolean isLogin() {
        boolean ret = false;
        if (getMiguService() != null && getMiguService().isConnect()) {
            ret = getMiguService().isLogin();
        }
        LogUtils.d(TAG, "isLogin: " + ret);
        return ret;
    }

    @Override
    public void init(Context context) {
        LogUtils.d(TAG, "init");
        miguServiceC = new MiguService(context, new ConnectListener() {
            @Override
            public void onConnectSuccess() {

            }

            @Override
            public void onDisConnect() {

            }
        });
        miguServiceC.connectService(MediaHelper.getUserId(DeviceScreenType.CEIL_SCREEN));
        miguServiceP = new MiguService(context, new ConnectListener() {
            @Override
            public void onConnectSuccess() {

            }

            @Override
            public void onDisConnect() {

            }
        });
        miguServiceP.connectService(MediaHelper.getUserId(DeviceScreenType.PASSENGER_SCREEN));
        miguService = new MiguService(context, new ConnectListener() {
            @Override
            public void onConnectSuccess() {

            }

            @Override
            public void onDisConnect() {

            }
        });
        miguService.connectService(MediaHelper.getUserId(DeviceScreenType.CENTRAL_SCREEN));
    }

    @Override
    public void destroy(Context context) {
        if (miguService != null) {
            miguService.release();
        }
        if (miguServiceC != null) {
            miguServiceC.release();
        }
        if (miguServiceP != null) {
            miguServiceP.release();
        }
    }

    public TTSBean switchApp(boolean isOpen){
        if (isOpen) {
            return open();
        } else {
            return close();
        }
    }

    public TTSBean open() {
        LogUtils.d(TAG, "open");
        if (isFront()) {
            return MediaTtsManager.getInstance().getAlreadyOpenAppTts(MediaTtsManager.APP_NAME_MI_GU,MediaHelper.SCREEN_NAME_CENTRAL);
        } else {
            if (MediaHelper.isSafeLimitation()) {
                return TtsReplyUtils.getTtsBeanText(TTSAnsConstant.PARK_NOT_SUPPORT);
            }
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
            return MediaTtsManager.getInstance().getOpenAppTts(MediaTtsManager.APP_NAME_MI_GU,MediaHelper.SCREEN_NAME_CENTRAL);
        }
    }

    private void openApp(){
        MediaHelper.openApp(APP_NAME, DeviceScreenType.CENTRAL_SCREEN);
    }

    public TTSBean close() {
        LogUtils.d(TAG, "close");
        if (isFront()) {
            MediaHelper.closeApp(APP_NAME,DeviceScreenType.CENTRAL_SCREEN);
            return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_MI_GU,MediaHelper.SCREEN_NAME_CENTRAL);
        } else {
            if (MediaHelper.isMirrorAppFront(APP_NAME)) {
                MediaHelper.backToHome(DeviceScreenType.CENTRAL_SCREEN);
                return MediaTtsManager.getInstance().getCloseAppTts(MediaTtsManager.APP_NAME_MI_GU,MediaHelper.SCREEN_NAME_CENTRAL);
            } else {
                return MediaTtsManager.getInstance().getAlreadyCloseAppTts(MediaTtsManager.APP_NAME_MI_GU,MediaHelper.SCREEN_NAME_CENTRAL);
            }
        }
    }

    @Override
    public TTSBean pre() {
        LogUtils.d(TAG, "pre");
        preAction();
        return null;
    }

    private void preAction() {
        if (getMiguService().isSkipPrevious()) {
            getMiguService().prev();
            commonReply();
        } else {
            MediaHelper.speakTts("4023402");
        }
    }

    @Override
    public TTSBean next() {
        LogUtils.d(TAG, "next");
        nextAction();
        return null;
    }

    private void nextAction() {
        if (getMiguService().isSkipNext()) {
            getMiguService().next();
            commonReply();
        } else {
            MediaHelper.speakTts("4023502");
        }
    }

    @Override
    public TTSBean play() {
        LogUtils.d(TAG, "play");
        playAction();
        return null;
    }

    private void playAction() {
        if (isPlaying()) {
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4003902").getSelectTTs());
        } else {
            getMiguService().play();
            commonReply();
        }
    }

    @Override
    public TTSBean replay() {
        LogUtils.d(TAG, "replay");
        replayAction();
        return null;
    }

    private void replayAction() {
        getMiguService().reStart();
        commonReply();
    }

    @Override
    public TTSBean stop(boolean isExit) {
        LogUtils.d(TAG, "stop");
        if (isExit) {
            close();
            return TtsReplyUtils.getTtsBean("1100005");
        } else {
            pauseAction();
        }
        return null;
    }

    private void pauseAction() {
        if (isPlaying()) {
            getMiguService().pause();
            commonReply();
        } else {
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4004001").getSelectTTs());
        }
    }

    @Override
    public TTSBean seek(String seekType, long duration) {
        LogUtils.d(TAG, "seekType: " + seekType + " duration: " + duration);
        if (TextUtils.equals(seekType, "fast_forward")) {
            if (duration <= 0) {
                duration = 15;
            }
            fastForwardAction(duration);
            return null;
        } else if (TextUtils.equals(seekType, "fast_rewind")) {
            if (duration <= 0) {
                duration = 15;
            }
            fastRewindAction(duration);
            return null;
        } else if (TextUtils.equals(seekType, "set")) {
            if (duration < 0) {
                duration = 0;
            }
            seekToAction(duration);
            return null;
        }
        return null;
    }

    private void fastForwardAction(long duration) {
        getMiguService().fastForward(duration * 1000, i -> {
            LogUtils.d(TAG, "fastForwardAction resultCode = " + i);
            if (i == 1) {//成功
                commonReply();
            } else if (i == 2) {//超过播放⻓度
                MediaHelper.speakTts("4024003");
            } else if (i == 3) {//超过试看
                MediaHelper.speakTts("4024004");
            } else if (i == 4) {//不支持快进快退
                MediaHelper.speakTts("4050000");
            }
        });
    }

    private void fastRewindAction(long duration) {
        getMiguService().fastRewind(duration * 1000, i -> {
            LogUtils.d(TAG, "fastRewindAction resultCode = " + i);
            if (i == 1) {//成功
                commonReply();
            } else if (i == 2) {//⼩于播放⻓度
                MediaHelper.speakTts("4024103");
            } else if (i == 4) {//不支持快进快退
                MediaHelper.speakTts("4050000");
            }
        });
    }

    private void seekToAction(long duration) {
        getMiguService().seekTo(duration * 1000, i -> {
            LogUtils.d(TAG, "seekToAction resultCode = " + i);
            if (i == 1) {//成功
                commonReply();
            } else if (i == 2) {//超过播放⻓度
                MediaHelper.speakTts("4024003");
            } else if (i == 3) {//超过试看
                MediaHelper.speakTts("4024004");
            } else if (i == 4) {//不支持快进快退
                MediaHelper.speakTts("4050000");
            }
        });
    }

    @Override
    public TTSBean switchPlayMode(String switchType, String playMode) {
        return null;
    }

    @Override
    public TTSBean switchDanmaku(boolean isOpen) {
        LogUtils.d(TAG, "switchDanmaku isOpen = " + isOpen);
        MediaHelper.speakTts("4003100");
        return null;
    }

    @Override
    public TTSBean speed(String adjustType, String numberRate, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " numberRate: " + numberRate + " level: " + level);
        if (TextUtils.equals(adjustType, "increase")) {
            stepPlaySpeedAction(true);
            return null;
        } else if (TextUtils.equals(adjustType, "decrease")) {
            stepPlaySpeedAction(false);
            return null;
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                specifyPlaySpeedAction(speedArr[speedArr.length - 1]);
                return null;
            } else if (TextUtils.equals(level, "min")) {
                specifyPlaySpeedAction(speedArr[0]);
                return null;
            } else {
                try {
                    float num = Float.parseFloat(numberRate);
                    LogUtils.d(TAG, "set num: " + num);
                    if (num > 0) {
                        float closest;
                        if (num > speedArr[speedArr.length - 1]) {
                            isOutRangeMaxSpeed = true;
                            closest = speedArr[speedArr.length - 1];
                        } else if (num < speedArr[0]) {
                            isOutRangeMinSpeed = true;
                            closest = speedArr[0];
                        } else {
                            closest = NumberUtils.findClosestValue(num, speedArr);
                        }
                        LogUtils.d(TAG, "closest: " + closest);
                        specifyPlaySpeedAction(closest);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    LogUtils.d(TAG, "speed set error: " + e.getMessage());
                }
            }
        }
        return null;
    }

    private void stepPlaySpeedAction(boolean isRise) {
        LogUtils.d(TAG, "current speed = " + getMiguService().currentRate());
        //已经是最低速
        if (!isRise && getMiguService().currentRate() == speedArr[0]) {
            MediaHelper.speakTts("4013605");
            return;
        }
        //已经是最高速
        if (isRise && getMiguService().currentRate() == speedArr[speedArr.length - 1]) {
            MediaHelper.speakTts("4013604");
            return;
        }
        getMiguService().stepPlaySpeed(isRise);
        if (isRise) {
            MediaHelper.speakTts("4017302");
        } else {
            MediaHelper.speakTts("4017303");
        }
    }

    private void specifyPlaySpeedAction(float speed) {
        LogUtils.d(TAG, "current speed = " + getMiguService().currentRate() + ", set speed = " + speed);
        //已经是当前倍速了
        if (getMiguService().currentRate() == speed && !isOutRangeMaxSpeed && !isOutRangeMinSpeed) {
            if (speed == speedArr[0]) {//已经是最低速
                    MediaHelper.speakTts("4013605");
            } else if (speed == speedArr[speedArr.length - 1]) { //已经是最高速
                    MediaHelper.speakTts("4013604");
            } else {
                MediaHelper.speak(TtsReplyUtils.getTtsBean("4024505", "@{media_speed}", speed + "倍速").getSelectTTs());
            }
            return;
        }
        getMiguService().specifyPlaySpeed(speed);
        if (isOutRangeMinSpeed) {
            isOutRangeMinSpeed = false;
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4024504", "@{media_speed_min}", speed + "倍速").getSelectTTs());
        } else if (isOutRangeMaxSpeed) {
            isOutRangeMaxSpeed = false;
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4024503", "@{media_speed_max}", speed + "倍速").getSelectTTs());
        } else {
            commonReply();
        }
    }

    @Override
    public TTSBean definition(String adjustType, String mode, String level) {
        LogUtils.d(TAG, "adjustType: " + adjustType + " mode: " + mode + " level: " + level);
        if (TextUtils.equals(adjustType, "increase")) {
            stepDefinitionAction(true);
            return null;
        } else if (TextUtils.equals(adjustType, "decrease")) {
            stepDefinitionAction(false);
            return null;
        } else if (TextUtils.equals(adjustType, "set")) {
            if (TextUtils.equals(level, "max")) {
                setDefinitionAction(MiguHelper.DEFINITION_1080);
                return null;
            } else if (TextUtils.equals(level, "min")) {
                setDefinitionAction(MiguHelper.DEFINITION_540);
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
                            setDefinitionAction(MiguHelper.DEFINITION_1080);
                            return null;
                        case "高清":
                        case "准高清":
                            setDefinitionAction(MiguHelper.DEFINITION_720);
                            return null;
                        case "标清":
                        case "流畅":
                        default:
                            setDefinitionAction(MiguHelper.DEFINITION_540);
                            return null;
                    }
                } else {
                    // 数字
                    int num = Integer.parseInt(numString);
                    if (mode.contains("k") || mode.contains("K")) {
                        num = num * 1000;
                    }
                    if (num > 0) {
                        float[] targets = {540, 720, 1080};
                        if(!Objects.equals(targets, num)){
                            MediaHelper.speakTts("4024703");
                            return null;
                        }
                        float closest = NumberUtils.findClosestValue(num, targets);
                        int closestInt = (int) closest;
                        LogUtils.d(TAG, "closest: " + closestInt);
                        if (closestInt == 540) {
                            setDefinitionAction(MiguHelper.DEFINITION_540);
                        } else if (closestInt == 720) {
                            setDefinitionAction(MiguHelper.DEFINITION_720);
                        } else {
                            setDefinitionAction(MiguHelper.DEFINITION_1080);
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private void stepDefinitionAction(boolean isRise) {
        String currentDefinition = getMiguService().currentDefinition();
        LogUtils.d(TAG, "current definition = " + currentDefinition);
        if (MiguHelper.DEFINITION_540.equals(currentDefinition) && !isRise) {
            MediaHelper.speakTts("4024806");
            return;
        }
        if (MiguHelper.DEFINITION_1080.equals(currentDefinition) && isRise) {
            MediaHelper.speakTts("4024805");
            return;
        }
        //是否可切
        int definition;
        if (isRise) {
            definition = Integer.parseInt(currentDefinition) + 1;
        } else {
            definition = Integer.parseInt(currentDefinition) - 1;
        }
        boolean canChange = getMiguService().isVipDefinition(String.valueOf(definition));
        LogUtils.d(TAG, "canChange = " + canChange);
        if (!canChange) {
            MediaHelper.speakTts("4024704");
        } else {
            getMiguService().stepDefinition(isRise);
            if (isRise) {
                MediaHelper.speakTts("4017302");
            } else {
                MediaHelper.speakTts("4017303");
            }
        }
    }

    private void setDefinitionAction(String definition) {
        String currentDefinition = getMiguService().currentDefinition();
        LogUtils.d(TAG, "current definition = " + currentDefinition + "---" + definition);
        //是否可切
        boolean canChange = getMiguService().isVipDefinition(definition);
        LogUtils.d(TAG, "isVip = " + canChange);
        if (!canChange) {
            MediaHelper.speakTts("4024704");
            return;
        }
        if(definition.equals(currentDefinition)){
            String ttsStr;
            if (currentDefinition.equals(MiguHelper.DEFINITION_540)) {
                ttsStr = "标清";
            } else if (currentDefinition.equals(MiguHelper.DEFINITION_720)) {
                ttsStr = "高清";
            } else {
                ttsStr = "蓝光";
            }
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4013505", "@{media_definition}", ttsStr).getSelectTTs());
            return;
        }
        getMiguService().setDefinition(definition);
        commonReply();
    }

    @Override
    public TTSBean queryPlayInfo() {
        LogUtils.d(TAG, "queryPlayInfo");
        getMediaInfoAction();
        return null;
    }

    private void getMediaInfoAction() {
        String info = getMiguService().getCurrentMediaInfo();
        if (TextUtils.isEmpty(info)) {
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4004402").getSelectTTs());
        } else {
            LogUtils.d(TAG, "getCurrentMediaInfo: " + info);
            String tts = TtsReplyUtils.getTtsBean("4013001").getSelectTTs();
            MediaHelper.speak(tts.replace("@{media_name}", info).replace("@{app_name}", "咪咕视频"));
        }
    }

    @Override
    public TTSBean jump() {
        LogUtils.d(TAG, "jump");
        skipStartAndEndAction();
        return null;
    }

    private void skipStartAndEndAction() {
        if (getMiguService().IsSkipStartAndEnd()) {
            MediaHelper.speakTts("4033304");
        } else {
            getMiguService().skipStartAndEnd();
            MediaHelper.speakTts("4025102");
        }
    }

    @Override
    public TTSBean switchLyric(boolean isOpen) {
        return null;
    }

    @Override
    public TTSBean switchCollect(boolean isCollect, String mediaType) {
        LogUtils.d(TAG, "switchCollect isCollect: " + isCollect + " mediaType: " + mediaType);
        // 收藏视频，需要在播放⻚才能有效
        switchCollectAction(isCollect);
        return null;
    }

    private void switchCollectAction(boolean isCollect) {
        if (isLogin()) {
            if (isCollect) {
                if (getMiguService().isCollect()) {
                    MediaHelper.speakTts("4004203");
                } else {
                    getMiguService().collect();
                    MediaHelper.speakTts("4004204");
                }
            } else {
                if (getMiguService().isCollect()) {
                    getMiguService().cancelCollect();
                    MediaHelper.speakTts("4004303");
                } else {
                    MediaHelper.speakTts("4004304");
                }
            }
        } else {
            getMiguService().login(i -> LogUtils.d(TAG, "resultCode = " + i));
            MediaHelper.speakTts("4000001");
        }
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
        LogUtils.d(TAG, "switchHistoryUI isOpen: " + isOpen);
        switchHistoryPageAction(isOpen);
        return null;
    }

    private void switchHistoryPageAction(boolean isOpen) {
        if (isOpen) {
            if (MediaHelper.isSafeLimitation()) {
                MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                return;
            }
            if(!isFront()){
                try {
                    open();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LogUtils.d(TAG, "e = " + e);
                }
            }
            if (isLogin()) {
                if (isHistoryFront()) {
                    MediaHelper.speakTts("4022803");
                } else {
                    getMiguService().jumpHistoryPage();
                    MediaHelper.speakTts("4022802");
                }
            } else {
                MediaHelper.speakTts("4000001");
                //咪咕不在前台时需要拉起应用
                getMiguService().login(i -> LogUtils.d(TAG, "resultCode = " + i));
            }
        } else {
            if (isHistoryFront()) {
                getMiguService().finishHistoryPage();
                MediaHelper.speakTts("4022902");
            } else {
                MediaHelper.speakTts("4022901");
            }
        }
    }

    @Override
    public TTSBean switchCollectUI(boolean isOpen) {
        LogUtils.d(TAG, "switchCollectUI isOpen: " + isOpen);
        switchCollectionPageAction(isOpen);
        return null;
    }

    private void switchCollectionPageAction(boolean isOpen) {
        if (isOpen) {
            if (MediaHelper.isSafeLimitation()) {
                MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
                return;
            }
            if(!isFront()){
                try {
                    open();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LogUtils.d(TAG, "e = " + e);
                }
            }
            if (isLogin()) {
                if (isCollectFront()) {
                    MediaHelper.speakTts("4003403");
                } else {
                    getMiguService().jumpCollectionPage();
                    MediaHelper.speakTts("4003402");
                }
            } else {
                MediaHelper.speakTts("4000001");
                //咪咕不在前台时需要拉起应用
                getMiguService().login(i -> LogUtils.d(TAG, "resultCode = " + i));
            }
        } else {
            if (isCollectFront()) {
                getMiguService().finishCollectionPage();
                MediaHelper.speakTts("4003502");
            } else {
                MediaHelper.speakTts("4003501");
            }
        }
    }

    @Override
    public TTSBean playUI(int type) {
        LogUtils.d(TAG, "playUI type: " + type);
        playFirstAction(type);
        return null;
    }

    private void playFirstAction(int type) {
        if (MediaHelper.isSafeLimitation()) {
            MediaHelper.speak(TTSAnsConstant.PARK_NOT_SUPPORT);
            return;
        }
        if (!isFront()) {
            try {
                open();
                Thread.sleep(1000);
            } catch (Exception e) {
                LogUtils.d(TAG, "e = " + e);
            }
        }
        if (isLogin()) {
            if (type == 2) {
                getMiguService().playFirstCollection(i -> {
                    LogUtils.d(TAG, "playFirstCollection resultCode = " + i);
                    if (i == 1) {//成功
                        String currentMediaInfo = getMiguService().getCurrentMediaInfo();
                        MediaHelper.speak(TtsReplyUtils.getTtsBean("4010101",
                                "@{media_name}", currentMediaInfo).getSelectTTs());
                    } else if (i == 2) {//⽆收藏记录
                        MediaHelper.speakTts("4003604");
                    }
                });
            } else {
                getMiguService().playFirstHistory(i -> {
                    LogUtils.d(TAG, "playFirstHistory resultCode = " + i);
                    if (i == 1) {//成功
                        String currentMediaInfo = getMiguService().getCurrentMediaInfo();
                        MediaHelper.speak(TtsReplyUtils.getTtsBean("4010101",
                                "@{media_name}", currentMediaInfo).getSelectTTs());
                    } else if (i == 2) {//⽆历史记录
                        MediaHelper.speakTts("4023003");
                    }
                });
            }
        } else {
            MediaHelper.speakTts("4000001");
            //咪咕不在前台时需要拉起应用
            getMiguService().login(i -> LogUtils.d(TAG, "resultCode = " + i));
        }
    }

    @Override
    public TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode) {
        return null;
    }

    /**
     * 咪咕功能控制
     */
    private void commonReply() {
        MediaHelper.speak(TtsReplyUtils.getTtsBean("1100005").getSelectTTs());
    }

    private MiguService getMiguService(){
        if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getCeilingScreenDisplayId()) {
            return miguServiceC;
        } else if (VideoControlCenter.getInstance().getCurrentDisplayId() == MediaHelper.getPassengerScreenDisplayId()) {
            return miguServiceP;
        } else {
            return miguService;
        }
    }
}
