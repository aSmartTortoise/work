package com.voyah.ai.basecar.media.vedio;


import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.utils.ScreenUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import multiscreenmirror.CloseShareScreenCallBack;
import multiscreenmirror.ShareScreenCallBack;

public enum ScreenShareImpl  {
    INSTANCE;

    private static final String TAG = ScreenShareImpl.class.getSimpleName();

    public void init(Context context) {
        LogUtils.d(TAG, "init");
        //初始化MirrorServiceManager
        MirrorServiceManager.INSTANCE.init(context);
    }

    /**
     * 分享给单个屏
     */
    public void shareScreenSingle(int sourceDisplayId, int targetDisplayId, boolean isSoundLocation) {
        LogUtils.d(TAG, "shareScreenSingle sourceDisplayId = " + sourceDisplayId + ", targetDisplayId = " + targetDisplayId);
        int ceilingScreenDisplayId = MegaDisplayHelper.getCeilingScreenDisplayId();
        int passengerScreenDisplayId = MegaDisplayHelper.getPassengerScreenDisplayId();
        int mainScreenDisplayId = MegaDisplayHelper.getMainScreenDisplayId();

        if (sourceDisplayId == -1) {
            sourceDisplayId = 0;
        }
        if (targetDisplayId == -1) {
            targetDisplayId = 0;
        }
        //发起屏跟接收屏是同一个
        if (sourceDisplayId == targetDisplayId) {
            MediaHelper.speakTts("4050000");
            return;
        }

        //当前接收屏在共享中,tts提示
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        if (StringUtils.isNotBlank(mirrorPackage)) {
            int[] targetScreen = MirrorServiceManager.INSTANCE.getTargetScreen();
            int index = Arrays.binarySearch(targetScreen,targetDisplayId);
            if (index >= 0) {
                MediaHelper.speakTts("4050077");
                return;
            }
        }
        //接收屏在ai识图中
        if (MediaHelper.isAIIdentifyPicStatus(targetDisplayId)) {
            MediaHelper.speak(MediaHelper.getScreenName(targetDisplayId) + "正在AI识图中，不支持共享");
            return;
        }

        //1.不支持同看
        if (!MediaHelper.isSupportScreenShare(sourceDisplayId) && StringUtils.isBlank(mirrorPackage)) {
            MediaHelper.speakTts("4003100");
            return;
        }
        //2.分享源是否在播放,不在播放并且不在同看
        if (!MediaHelper.isPlaying(sourceDisplayId) && StringUtils.isBlank(mirrorPackage)) {
            MediaHelper.speakTts("4050064");
            return;
        }
        //是否有吸顶屏
        if ((targetDisplayId == ceilingScreenDisplayId
                || sourceDisplayId == ceilingScreenDisplayId) &&
                !MediaHelper.judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL)) {
            MediaHelper.speakTts("1100028");
            return;
        }
        //3.吸顶屏是否打开
        if ((targetDisplayId == ceilingScreenDisplayId
                || sourceDisplayId == ceilingScreenDisplayId)
                && !MediaHelper.isCeilOpen()) {
            MediaHelper.speakTts("4050039");
            return;
        }
        //4.发起屏/接收屏为中控屏，且非P挡
        if ((targetDisplayId == 0 || sourceDisplayId == 0) && !MediaHelper.isParking()) {
            MediaHelper.speakTts("4050040");
            return;
        }
        //5.接收屏息屏/屏保中,亮屏
        if ((sourceDisplayId == 0 || targetDisplayId == 0) && ScreenUtils.getInstance().getScreenOnOff(0) == 0) {
            ScreenUtils.getInstance().controlScreenBright(true, 0);
        }
        if ((sourceDisplayId == passengerScreenDisplayId || targetDisplayId == passengerScreenDisplayId) && ScreenUtils.getInstance().getScreenOnOff(1) == 0) {
            ScreenUtils.getInstance().controlScreenBright(true, 1);
        }
        //6.接收屏在清洁模式，清洁模式中控屏控制
        boolean currentState = SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode");
        if(currentState){
            if (isSoundLocation && (targetDisplayId == passengerScreenDisplayId || targetDisplayId == mainScreenDisplayId)) {
                MediaHelper.speakTts("4050049");
                return;
            } else {
                if (targetDisplayId == passengerScreenDisplayId) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4050042", "@{screen_name}", MediaHelper.SCREEN_NAME_PASSENGER).getSelectTTs());
                    return;
                } else if (targetDisplayId == mainScreenDisplayId) {
                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4050042", "@{screen_name}", MediaHelper.SCREEN_NAME_CENTRAL).getSelectTTs());
                    return;
                }
            }
        }

        //7.接收屏360
        int intProp = MediaHelper.getAvmStatus();
        if (targetDisplayId == 0 && intProp == 1) {
            MediaHelper.speak(TtsReplyUtils.getTtsBean("4050043").getSelectTTs().replace("@{screen_name}", MediaHelper.SCREEN_NAME_CENTRAL));
            return;
        }
        //
        MirrorServiceManager.INSTANCE.shareScreenToDisplay(sourceDisplayId, targetDisplayId, new ShareScreenCallBack.Stub() {
            @Override
            public void onResultCallBack(int code) throws RemoteException {
                LogUtils.d(TAG, "返回结果：" + code);
                String ttsId = "1100005";
                String ttsPart = "";
                switch (code) {
                    case 0://0 成功
                        ttsId = "4050038";
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
        });
    }

    /**
     * 分享给所有屏
     */
    public void shareScreenForAll(int sourceDisplayId) {
        int ceilingScreenDisplayId = MegaDisplayHelper.getCeilingScreenDisplayId();
        int passengerScreenDisplayId = MegaDisplayHelper.getPassengerScreenDisplayId();
        LogUtils.d(TAG, "shareScreenForAll sourceDisplayId = " + sourceDisplayId);

        if (sourceDisplayId == -1) {
            sourceDisplayId = 0;
        }

        //吸顶屏屏在ai识图中
        if (MediaHelper.isAIIdentifyPicStatus(ceilingScreenDisplayId)) {
            MediaHelper.speak(MediaHelper.getScreenName(ceilingScreenDisplayId) + "正在AI识图中，不支持共享");
            return;
        }

        //副驾屏在ai识图中
        if (MediaHelper.isAIIdentifyPicStatus(passengerScreenDisplayId)) {
            MediaHelper.speak(MediaHelper.getScreenName(passengerScreenDisplayId) + "正在AI识图中，不支持共享");
            return;
        }

        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        //1.不支持同看
        if (!MediaHelper.isSupportScreenShare(sourceDisplayId) && StringUtils.isBlank(mirrorPackage)) {
            MediaHelper.speakTts("4003100");
            return;
        }
        //2.分享源是否在播放
        if (!MediaHelper.isPlaying(sourceDisplayId) && StringUtils.isBlank(mirrorPackage)) {
            MediaHelper.speakTts("4050064");
            return;
        }
        //4.发起屏为中控屏，且非P挡
        if (sourceDisplayId == 0 && !MediaHelper.isParking()) {
            MediaHelper.speakTts("4050040");
            return;
        }

        //5.接收屏息屏/屏保中,亮屏
        if (ScreenUtils.getInstance().getScreenOnOff(0) == 0) {
            ScreenUtils.getInstance().controlScreenBright(true, 0);
        }
        if (ScreenUtils.getInstance().getScreenOnOff(1) == 0) {
            ScreenUtils.getInstance().controlScreenBright(true, 1);
        }
        //6.接收屏在清洁模式，清洁模式中控屏控制，
        //7.接收屏360
        int avmStatus = MediaHelper.getAvmStatus();

        int finalSourceDisplayId = sourceDisplayId;
        MirrorServiceManager.INSTANCE.shareScreenToAllDisplay(sourceDisplayId, new ShareScreenCallBack.Stub() {
            @Override
            public void onResultCallBack(int code) throws RemoteException {
                LogUtils.d(TAG, "code = " + code);
                String ttsPart = "";
                String ttsId = "1100005";
                switch (code) {
                    case 0://0 成功
                        if (!MediaHelper.isCeilOpen()) {
                            if (finalSourceDisplayId == passengerScreenDisplayId) {
                                ttsPart = MediaHelper.SCREEN_NAME_CENTRAL;
                            } else {
                                ttsPart = "副驾屏";
                            }
                            MediaHelper.speak(TtsReplyUtils.getTtsBean("4050045").getSelectTTs().replace("@{screen_name}", ttsPart));
                            return;
                        } else if (!MediaHelper.isParking()) {
                            if (finalSourceDisplayId == passengerScreenDisplayId) {
                                ttsPart = "吸顶屏";
                            } else {
                                ttsPart = "副驾屏";
                            }
                            MediaHelper.speak(TtsReplyUtils.getTtsBean("4050046").getSelectTTs().replace("@{screen_name}", ttsPart));
                            return;
                        } else if (avmStatus == 1) {
                            if (finalSourceDisplayId == passengerScreenDisplayId) {
                                ttsPart = "吸顶屏";
                            } else {
                                ttsPart = "副驾屏";
                            }
                            MediaHelper.speak(
                                    TtsReplyUtils.getTtsBean("4050048", "@{screen_name1}",
                                            MediaHelper.SCREEN_NAME_CENTRAL, "@{screen_name2}", ttsPart).getSelectTTs());
                            return;
                        } else {
                            MediaHelper.speakTts("4050038");
                            return;
                        }
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
        });
    }

    /**
     * 关闭单个屏幕分享
     */
    public void closeShareScreenSingle(int displayId) {
        LogUtils.d(TAG, "closeShareScreenSingle displayId = " + displayId);
        if (displayId == -1) {
            displayId = 0;
        }
        MirrorServiceManager.INSTANCE.closeShareScreenByDisplay(displayId, new CloseShareScreenCallBack.Stub() {
            @Override
            public void onResultCallBack(int code) throws RemoteException {
                LogUtils.d(TAG, "返回结果：" + code);
                //code 1.如果传的displayId是发起屏，会直接关闭整个分享链 2.如果传的是接受屏的displayId，结束目标屏的分享 3.关闭失败
                String ttsId = "1100005";
                //code: 1.成功  2.失败
                switch (code) {
                    case 1:
                    case 2:
                        ttsId = "4050051";
                        break;
                    case 3:
                        ttsId = "4050052";
                        break;
                }
                LogUtils.d(TAG, "ttsId = " + ttsId);
                MediaHelper.speakTts(ttsId);
            }
        });
    }

    /**
     * 结束所有分享
     */
    public void closeShareScreenForAll() {
        LogUtils.d(TAG, "closeShareScreenForAll");
        MirrorServiceManager.INSTANCE.closeAllShareScreen(new CloseShareScreenCallBack.Stub() {
            @Override
            public void onResultCallBack(int code) throws RemoteException {
                LogUtils.d(TAG, "返回结果：" + code);
                String ttsId = "1100005";
                //code: 1.成功  2.失败
                switch (code) {
                    case 1:
                        ttsId = "4050051";
                        break;
                    case 2:
                        ttsId = "4050052";
                        break;
                }
                LogUtils.d(TAG, "ttsId = " + ttsId);
                MediaHelper.speakTts(ttsId);
            }
        });
    }
}
