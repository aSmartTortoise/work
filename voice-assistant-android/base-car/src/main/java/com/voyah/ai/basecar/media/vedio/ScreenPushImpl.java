package com.voyah.ai.basecar.media.vedio;


import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import multiscreenmirror.ShareScreenCallBack;

public enum ScreenPushImpl {
    INSTANCE;

    private int mDisplayId;

    private static final String TAG = ScreenPushImpl.class.getSimpleName();

    public void init(Context context){

    }

    /**
     * 发起视频推送
     * 这里视频推送涉及多种场景，以后排为例：
     * 1.接收屏为后排，中控和副驾屏（带耳机）都在播放中，此时发起屏为中控
     * 2.接收屏为后排，中控在播放，此时发起屏为中控
     * 3.接收屏为后排，副驾屏在播放，此时发起屏为副驾屏
     * 需要获取发起屏ID的接口，结论为中控在播放，发起屏为中控，副驾在播放，发起屏为副驾
     */
    public void pushScreenToDisplay(int sourceDisplayId, int targetDisplayId,boolean isSoundLocation) {
        mDisplayId = targetDisplayId;
        LogUtils.d(TAG, "pushScreenToDisplay sourceDisplayId = " + sourceDisplayId + ", targetDisplayId = " + targetDisplayId);
        int ceilingScreenDisplayId = MegaDisplayHelper.getCeilingScreenDisplayId();
        int passengerScreenDisplayId = MegaDisplayHelper.getPassengerScreenDisplayId();
        int mainScreenDisplayId = MegaDisplayHelper.getMainScreenDisplayId();

        //发起屏跟接收屏是同一个
        if (sourceDisplayId == targetDisplayId) {
            MediaHelper.speakTts("4050000");
            return;
        }

        //接收屏屏在ai识图中
        if (MediaHelper.isAIIdentifyPicStatus(targetDisplayId)) {
            MediaHelper.speak(MediaHelper.getScreenName(targetDisplayId) + "正在AI识图中，不支持视频推送");
            return;
        }

        //1.不支持推送
        if (!MediaHelper.isSupportScreenPush(sourceDisplayId)) {
            MediaHelper.speakTts("4003100");
            return;
        }
        if (sourceDisplayId == -1) {
            sourceDisplayId = 0;
        }
        //接收屏正在同看中，不支持推送
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        int[] targetScreen = MirrorServiceManager.INSTANCE.getTargetScreen();
        int sourceScreen = MirrorServiceManager.INSTANCE.getSourceScreen();
        int index = Arrays.binarySearch(targetScreen, targetDisplayId);
        if (StringUtils.isNotBlank(mirrorPackage) && (index >= 0 || sourceScreen == targetDisplayId)) {
            MediaHelper.speak(MediaHelper.getScreenName(targetDisplayId) + "正在多屏共享中，不支持视频推送");
            return;
        }

        //2.分享源是否在播放
        if (!MediaHelper.isPlaying(sourceDisplayId)) {
            MediaHelper.speakTts("4050065");
            return;
        }
        //是否有吸顶屏
        if (targetDisplayId == ceilingScreenDisplayId && !MediaHelper.judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL)) {
            MediaHelper.speakTts("1100028");
            return;
        }
        //3.接收屏吸顶屏是否打开
        if (targetDisplayId == ceilingScreenDisplayId && !MediaHelper.isCeilOpen()) {
            MediaHelper.speakTts("4050039");
            return;
        }
        //4.发起屏/接收屏为中控屏，且非P挡
        if (targetDisplayId == 0 && !MediaHelper.isParking()) {
            MediaHelper.speakTts("4050054");
            return;
        }
        //5.接收屏在清洁模式，清洁模式中控屏控制，
        boolean currentState = SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode");
        if(currentState){
            if (isSoundLocation && (targetDisplayId == passengerScreenDisplayId || targetDisplayId == mainScreenDisplayId)) {
                MediaHelper.speakTts("4050058");
                return;
            } else {
                if (targetDisplayId == passengerScreenDisplayId) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4050055", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER).getSelectTTs());
                    return;
                } else if (targetDisplayId == mainScreenDisplayId) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4050055", "@{screen_name}", MediaHelper.SCREEN_NAME_CENTRAL).getSelectTTs());
                    return;
                }
            }
        }

        //6.接收屏为中控屏且360
        int avmStatus = MediaHelper.getAvmStatus();
        if (targetDisplayId == 0 && avmStatus == 1) {
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4050056").getSelectTTs().replace("@{screen_name}", MediaHelper.SCREEN_NAME_CENTRAL));
            return;
        }

        MirrorServiceManager.INSTANCE.pushScreenToDisplay(sourceDisplayId, targetDisplayId, mShareScreenCallBack);
    }

    private final ShareScreenCallBack mShareScreenCallBack = new ShareScreenCallBack.Stub(){
        @Override
        public void onResultCallBack(int code) throws RemoteException {
            LogUtils.d(TAG, "返回结果：" + code);
            String ttsId = "1100005";
            String ttsPart = "";
            switch (code){
                case 0://0 成功
                    ttsId = "4050053";
                    ttsPart = MediaHelper.getScreenName(mDisplayId);
                    break;
                case 1://1 非P挡
                    ttsId = "4050040";
                    break;
                case 2://2 副驾蓝牙连接
                    ttsId = "4050041";
                    ttsPart = MediaHelper.SCREEN_NAME_PASSENGER;
                    break;
                case 21://吸顶屏蓝牙连接
                    ttsId = "4050041";
                    ttsPart = MediaHelper.SCREEN_NAME_CEIL;
                    break;
                case 22://吸顶屏和副驾蓝牙都连上了
                    ttsId = "4050041";
                    ttsPart = MediaHelper.SCREEN_NAME_PASSENGER + "和" + MediaHelper.SCREEN_NAME_CEIL;
                    break;
                case 3://3 清洁模式启动中
                    ttsId = "4050042";
                    ttsPart = MediaHelper.SCREEN_NAME_CENTRAL;
                    break;
                case 4://4 全景影像启动中
                    ttsId = "4050043";
                    ttsPart = MediaHelper.SCREEN_NAME_CENTRAL;
                    break;
                case 5://5 当前画面为不支持分享的应用
                    ttsId = "4003100";
                    break;
                case 6://6 屏幕未开启
                    ttsId = "4050039";
                    break;
                case 7://7 无吸顶屏
                    ttsId = "1100028";
                    break;
                case 8://8 当前画面为不支持分享的画面
                    ttsId = "4003100";
                    break;
                case 9://9 未知异常
                    ttsId = "4003100";
                    break;
            }
            if (TextUtils.isEmpty(ttsId)) {
                LogUtils.d(TAG, "onReceiveResult ttsId is null");
            } else {
                String selectTTs = TtsReplyUtils.getTtsBean(ttsId).getSelectTTs();
                LogUtils.d(TAG, "onReceiveResult tts is " + selectTTs);
                if (TextUtils.isEmpty(ttsPart)) {
                    MediaHelper.speak(selectTTs);
                } else {
                    if (selectTTs.contains("@{screen_name}")) {
                        MediaHelper.speak(selectTTs.replace("@{screen_name}", ttsPart));
                    }
                }
            }
        }
    };
}
