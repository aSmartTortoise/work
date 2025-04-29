package com.voyah.ai.basecar.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.utils.VideoControlCenter;
import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

public class BiliBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BiliBroadcastReceiver.class.getSimpleName();

    public static int currentScreenType;
    private static final String OPEN_HISTORY_UI = "open_history_ui";
    private static final String CLOSE_HISTORY_UI = "close_history_ui";
    private static final String OPEN_COLLECT_UI = "open_collect_ui";
    private static final String CLOSE_COLLECT_UI = "close_collect_ui";
    private static final String OPEN_DANMAKU = "open_danmaku";
    private static final String CLOSE_DANMAKU = "close_danmaku";
    private static final String PLAY_HISTORY = "play_history";
    private static final String PLAY_COLLECT = "play_collect";

    public static int playEpisodeValue;
    public static String actionType;
    public static boolean switchDanmakuOpen = false;
    public static boolean isOutRangeMaxSpeed = false;
    public static boolean isOutRangeMinSpeed = false;
    public static float currentSpeed;
    public static int currentQuality;
    public static final int PER_USER_RANGE = 100000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("playState")) {
            int playState = intent.getIntExtra("playState", -1);
            int uid = intent.getIntExtra("uid", -1);
//            LogUtils.d(TAG, "playState: " + playState + ", uid = " + uid);
            if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getCeilingScreenDisplayId()).hashCode() == uid / PER_USER_RANGE) {
                BiliImpl.INSTANCE.setPlayingC(playState == 4);
            } else if (MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()).hashCode() == uid / PER_USER_RANGE) {
                BiliImpl.INSTANCE.setPlayingP(playState == 4);
            } else {
                BiliImpl.INSTANCE.setPlaying(playState == 4);
            }
            LogUtils.d(TAG, "BiliCeilingImpl: " + BiliImpl.INSTANCE.isPlaying(MediaHelper.getCeilingScreenDisplayId())
                    + ", BiliCopilotImpl = " + BiliImpl.INSTANCE.isPlaying(MediaHelper.getPassengerScreenDisplayId())
                    + ", BiliImpl = " + BiliImpl.INSTANCE.isPlaying(MediaHelper.getMainScreenDisplayId()));
        } else {
            LogUtils.d(TAG, "userId = " + MegaUserHandle.myUserId() + ", intent = " + intent.getStringExtra("command")
                    + ", resultCode = " + intent.getStringExtra("resultCode"));
            String command = intent.getStringExtra("command");
            int resultCode = Integer.parseInt(intent.getStringExtra("resultCode"));

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
                        if (BiliImpl.INSTANCE.isFront()) {
                            ttsId = "4022803";
                        } else {
                            VideoControlCenter.getInstance().switchVideoApp(true,BiliImpl.APP_NAME);
                            ttsId = "4022802";
                        }
                    } else if (OPEN_COLLECT_UI.equals(actionType)) {
                        if (BiliImpl.INSTANCE.isFront()) {
                            ttsId = "4003403";
                        } else {
                            VideoControlCenter.getInstance().switchVideoApp(true,BiliImpl.APP_NAME);
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
    }
}
