package com.voyah.ai.basecar.media.utils;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.content.MegaContext;
import com.mega.nexus.os.MegaSystemProperties;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.service.MediaDeviceService;
import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.basecar.media.vedio.IqyImpl;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.basecar.media.vedio.UsbVideoImpl;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.SPUtil;
import com.voyah.ai.sdk.IImageRecognizeCallback;
import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;
import com.voyah.ai.sdk.manager.AIManager;
import com.voyah.ai.sdk.manager.DialogueManager;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;
import com.voyah.cockpit.window.model.ScreenType;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MediaHelper {

    private static final String TAG = MediaHelper.class.getSimpleName();
    private static final int VIDEO_TYPE_TENCENT = 1;
    private static final int VIDEO_TYPE_IQY = 2;
    public static String IS_TENCENT_PLAY = "is_tencent_play";
    public static final int PER_USER_RANGE = 100000;

    public static final String CENTRAL_SCREEN_1 = "central_media";
    public static final String CENTRAL_SCREEN = "central_screen";
    public static final String PASSENGER_SCREEN = "passenger_media";
    public static final String PASSENGER_SCREEN_1 = "passenger_screen";
    public static final String CEIL_SCREEN_1 = "ceil_media";
    public static final String CEIL_SCREEN = "ceil_screen";
    public static final String ALL_SCREEN = "all";

    public static final String SCREEN_NAME_CENTRAL  = "中控屏";
    public static final String SCREEN_NAME_PASSENGER  = "副驾屏";
    public static final String SCREEN_NAME_CEIL  = "吸顶屏";

    public static String[] MUSIC_MEDIA = { MediaSource.qq_music.getName(),MediaSource.wy_music.getName(),MediaSource.xmly_music.getName(),MediaSource.yt_music.getName(),MediaSource.usb_music.getName(),MediaSource.bt_music.getName()};

    public static List<String> CENTRAL_POSITION_SCREEN = Arrays.asList("central_screen", "first_row_left");

    public static List<String> PASSENGER_POSITION_SCREEN = Arrays.asList("passenger_screen", "first_row_right");

    public static List<String> CEIL_POSITION_SCREEN = Arrays.asList("ceil_screen", "second_row_left", "second_row_right", "second_row_mid", "second_side", "rear_side");

    public static List<String> MUSIC_APP_NAME = Arrays.asList("岚图音乐","网易","喜马","云听","qq音乐","QQ音乐","蓝牙音乐","USB音乐");
    /**
     * 智能选择视频
     * 1.控制语音设置中视频播放偏好APP;
     * 2.若偏好为智能选择,控制最近一次播放的在线视频
     * 3.若无，控制爱奇艺
     */
    public static void selectVideo(String mediaUi,boolean isOpen){
        int pref = SettingsManager.get().getVideoPreference();
        LogUtils.d(TAG, "selectVideo mediaUi = " + mediaUi + ", isOpen = " + isOpen + ", pref = " + pref);
        if (pref == VIDEO_TYPE_TENCENT) {
            if (ApplicationConstant.UI_NAME_HISTORY.equals(mediaUi)) {
                TencentVideoImpl.INSTANCE.switchHistoryUI(isOpen);
            } else if (ApplicationConstant.UI_NAME_COLLECTION.equals(mediaUi)) {
                TencentVideoImpl.INSTANCE.switchCollectUI(isOpen);
            } else {
                TencentVideoImpl.INSTANCE.launchTencentVideo(isOpen);
            }
        } else if (pref == VIDEO_TYPE_IQY) {
            if (ApplicationConstant.UI_NAME_HISTORY.equals(mediaUi)) {
                IqyImpl.INSTANCE.switchHistoryUI(isOpen);
            } else if (ApplicationConstant.UI_NAME_COLLECTION.equals(mediaUi)) {
                IqyImpl.INSTANCE.switchCollectUI(isOpen);
            } else {
                IqyImpl.INSTANCE.open(isOpen);
            }
        } else {
            Boolean isTencentPlay = SPUtil.getBoolean(Utils.getApp(), MediaDeviceService.IS_TENCENT_PLAY, false);
            LogUtils.d(TAG, "selectVideo isTencentPlay = " + isTencentPlay);
            if (isTencentPlay) {
                if (ApplicationConstant.UI_NAME_HISTORY.equals(mediaUi)) {
                    TencentVideoImpl.INSTANCE.switchHistoryUI(isOpen);
                } else if (ApplicationConstant.UI_NAME_COLLECTION.equals(mediaUi)) {
                    TencentVideoImpl.INSTANCE.switchCollectUI(isOpen);
                } else {
                    TencentVideoImpl.INSTANCE.launchTencentVideo(isOpen);
                }
            } else {
                if (ApplicationConstant.UI_NAME_HISTORY.equals(mediaUi)) {
                    IqyImpl.INSTANCE.switchHistoryUI(isOpen);
                } else if (ApplicationConstant.UI_NAME_COLLECTION.equals(mediaUi)) {
                    IqyImpl.INSTANCE.switchCollectUI(isOpen);
                } else {
                    IqyImpl.INSTANCE.open(isOpen);
                }
            }
        }
    }

    /**
     * 获取屏幕id
     */
    public static int getDisplayId(String screenStr) {
        int displayId = MegaDisplayHelper.getVoiceDisplayId();
        LogUtils.d(TAG, "getDisplayId screenStr =  " + screenStr);
        switch (screenStr) {
            case CENTRAL_SCREEN:
            case CENTRAL_SCREEN_1:
                displayId = MegaDisplayHelper.getMainScreenDisplayId();
                break;
            case PASSENGER_SCREEN:
            case PASSENGER_SCREEN_1:
                displayId = MegaDisplayHelper.getPassengerScreenDisplayId();
                break;
            case CEIL_SCREEN:
            case CEIL_SCREEN_1:
                displayId = MegaDisplayHelper.getCeilingScreenDisplayId();
                break;
        }
        LogUtils.d(TAG, "displayId = " + displayId);
        return displayId;
    }

    public static int getDisplayId(DeviceScreenType deviceScreenType){
        return MegaDisplayHelper.getDisplayId(deviceScreenType);
    }

    /**
     * 获取屏幕名称
     * @param displayId
     * @return
     */
    public static String getScreenName(int displayId) {
        String screenName = MediaHelper.SCREEN_NAME_CENTRAL;
        if(MegaDisplayHelper.getCeilingScreenDisplayId() == displayId){
            screenName = MediaHelper.SCREEN_NAME_CEIL;
        }else if(MegaDisplayHelper.getPassengerScreenDisplayId() == displayId){
            screenName = MediaHelper.SCREEN_NAME_PASSENGER;
        }else if(MegaDisplayHelper.getMainScreenDisplayId() == displayId){
            screenName = MediaHelper.SCREEN_NAME_CENTRAL;
        }
        return screenName;
    }

    /**
     * 获取正在播放的屏幕id
     * @return 屏幕id
     */
    public static int getPlayingDisplayId(){
        if (VideoControlCenter.getInstance().isVideoStatus("", VideoControlCenter.MediaStatus.PLAYING)) {
            return VideoControlCenter.getInstance().getCurrentDisplayId();
        } else {
            return MegaDisplayHelper.getVoiceDisplayId();
        }
    }

    /**
     * 打开多开应用
     */
    public static void openMultiApp(Context context,String appName,int displayId,int userId){
        Intent intentForPackage = context.getPackageManager().getLaunchIntentForPackage(appName);
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        activityOptions.setLaunchDisplayId(displayId);
        MegaContext.startActivityAsUser(context, intentForPackage, activityOptions.toBundle(), MegaUserHandle.of(userId));
    }

    /**
     * 获取指定用户的上下文
     */
    public static Context getUserContext(Context context,String appName,UserHandle userHandle){
        LogUtils.d(TAG, "getUserContext appName = " + appName + ", userHandle = " + userHandle.toString());
        try {
            Class<?> contextClass = ContextWrapper.class;
            Object contextIntance = contextClass.getDeclaredConstructor(Context.class).newInstance(context);
            Method method = contextClass.getDeclaredMethod("createPackageContextAsUser", String.class, int.class, UserHandle.class);
            method.setAccessible(true);
            Object object = method.invoke(contextIntance, appName, Context.CONTEXT_IGNORE_SECURITY, userHandle);
            return (Context) object;
        } catch (Exception e) {
            LogUtils.e(TAG, "e = " + e);
        }
        return context;
    }

    /**
     * 获取视频卡片的相应位置
     */
    public static int getScreenType(String position){
        int screenType = ScreenType.MAIN;
        switch (position){
            case "first_row_left"://主驾
                screenType = ScreenType.MAIN;
                break;
            case "first_row_right"://副驾
                screenType = ScreenType.PASSENGER;
                break;
            case "rear_side"://后排
            case "rear_side_left"://左后
            case "rear_side_mid"://后排中
            case "rear_side_right"://右后
            case "second_side"://二排
            case "second_row_right"://二排右
            case "second_row_left"://二排左
            case "second_row_mid"://二排中
            case "third_side"://三排
            case "third_row_left"://三排左
            case "third_row_right"://三排右
            case "third_row_mid"://三排中
                screenType = ScreenType.CEILING;
                break;
        }
        LogUtils.d(TAG, "screenType = " + screenType);
        return screenType;
    }

    public static boolean isCeilOpen() {
        return DeviceHolder.INS().getDevices().getSystem().getScreen().isCeilScreenOpen();
    }

    public static boolean isPlaying(int sourceDisplayId){
        return (IqyImpl.INSTANCE.isPlayPage(sourceDisplayId) && VideoControlCenter.getInstance().isPlaying(IqyImpl.APP_NAME,sourceDisplayId))
                || (MiguImpl.INSTANCE.isPlayPage(sourceDisplayId) && MiguImpl.INSTANCE.isPlaying(sourceDisplayId))
                || (TencentVideoImpl.INSTANCE.isPlayPage(sourceDisplayId) && VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME,sourceDisplayId))
                || (BiliImpl.INSTANCE.isPlayPage(sourceDisplayId) && BiliImpl.INSTANCE.isPlaying(sourceDisplayId))
                || (TiktokImpl.INSTANCE.isFrontByDisplayid(sourceDisplayId) && TiktokImpl.INSTANCE.isPlaying(sourceDisplayId))
                || (UsbVideoImpl.INSTANCE.isFrontByDisplayid(sourceDisplayId) && UsbVideoImpl.INSTANCE.isPlaying());
    }

    /**
     * 支持同看的应用有：爱奇艺，腾讯视频，咪咕视频，哔哩哔哩，车鱼视听，usb视频
     * @return
     */
    public static boolean isSupportScreenShare(int displayId){
        return IqyImpl.INSTANCE.isFrontByDisplayid(displayId)
                || MiguImpl.INSTANCE.isFrontByDisplayid(displayId)
                || TencentVideoImpl.INSTANCE.isFrontByDisplayid(displayId)
                || BiliImpl.INSTANCE.isFrontByDisplayid(displayId)
                || TiktokImpl.INSTANCE.isFrontByDisplayid(displayId)
                || UsbVideoImpl.INSTANCE.isFrontByDisplayid(displayId);
    }

    /**
     * 是否是P挡
     *
     * @return
     */
    public static boolean isParking() {
        int drvStatus = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                .getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getIntProp(CommonSignal.COMMON_GEAR_INFO);
        return drvStatus == GearInfo.CARSET_GEAR_PARKING;
    }

    /**
     * 支持同看的应用有：爱奇艺，腾讯视频，咪咕视频，哔哩哔哩
     * @return
     */
    public static boolean isSupportScreenPush(int displayId){
        return IqyImpl.INSTANCE.isFrontByDisplayid(displayId)
                || MiguImpl.INSTANCE.isFrontByDisplayid(displayId)
                || TencentVideoImpl.INSTANCE.isFrontByDisplayid(displayId)
                || BiliImpl.INSTANCE.isFrontByDisplayid(displayId);
    }

    /**
     *
     * @param key 功能位key
     * @param value 要判断是否支持的值
     * @return
     */
    public static boolean judgeSupport(String key, String value){
        FunctionalPositionBean bean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(key);
        if (bean == null || bean.getTypFunctionalMap() == null) {
            return false;
        }
        return bean.getTypFunctionalMap().containsKey(value);
    }

    /**
     * 是否支持头枕音响
     */
    public static boolean isSupportHeatRestSound() {
        return MegaSystemProperties.getInt(MegaProperties.CONFIG_HEADREST_SOUND, -1) != 0;
    }

    /**
     * 头枕音响是否开启
     * @return
     */
    public static boolean isHeatRestSoundOpened() {
        try {
            return IFunctionManagerImpl.getInstance(Utils.getApp()).getHeadrestAudioMode() > 1;
        } catch (Exception e) {
            LogUtils.d(TAG, "e");
        }
        return false;
    }

    public static void speakTts(String ttsId) {
        String text = TtsReplyUtils.getTtsBean(ttsId).getSelectTTs();
        speak(text);
    }

    public static void speak(String text) {
        DeviceHolder.INS().getDevices().getMediaCenter().speakTts(text);
    }

    /**
     * 同看应用是否在前台,如果在就退出同看
     * @return
     */
    public static boolean isMirrorAppFront(String packageName){
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        LogUtils.d(TAG,"packageName = "+packageName+", mirrorPackage = "+mirrorPackage+", ss = "+(StringUtils.isNotBlank(packageName) && packageName.equals(mirrorPackage)));
        return StringUtils.isNotBlank(packageName) && packageName.equals(mirrorPackage);
    }

    public static void openApp(String packageName, DeviceScreenType deviceScreenType) {
        VideoControlCenter.getInstance().switchVideoApp(true, packageName);
    }

    public static void closeApp(String appName,DeviceScreenType deviceScreenType){
        DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(appName, deviceScreenType);
    }

    public static void backToHome(DeviceScreenType deviceScreenType){
        DeviceHolder.INS().getDevices().getLauncher().backToHome(deviceScreenType);
    }

    public static boolean isAppForeGround(String pkgName, int displayId){
        return StringUtils.equalsIgnoreCase(pkgName, MediaHelper.getTopPackageName(displayId));
    }

    public static UserHandle getUserHandle(DeviceScreenType deviceScreenType){
        return MegaDisplayHelper.getUserHandle(deviceScreenType);
    }

    public static int getUserId(DeviceScreenType deviceScreenType){
        return MegaDisplayHelper.getUserHandle(deviceScreenType).hashCode();
    }

    public static String getTopActivityName(int displayId){
        return CommonSystemUtils.getTopActivityName(displayId);
    }

    public static String getTopPackageName(int displayId){
        return CommonSystemUtils.getTopPackageName(displayId);
    }

    public static int getAvmStatus(){
        return DeviceHolder.INS().getDevices().getMediaCenter().getAvmStatus();
    }

//    public static TTSBean setVoicePosition(String position, String soundLocation, String screenName) {
//        boolean isSoundLocation = false;
//        int voicePosition;
//        if (StringUtils.isBlank(screenName) || "screen".equals(screenName)) {
//            //有指定位置用指定位置，没有指定位置用声源位置
//            if (StringUtils.isBlank(position)) {
//                //获取声源位置
//                position = soundLocation;
//                isSoundLocation = true;
//            }
//            if (ScreenType.CEILING == MediaHelper.getScreenType(position)) {
//                if (judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL)) {
//                    if (!MediaHelper.isCeilOpen()) {
//                        if (isSoundLocation) {
//                            voicePosition = ScreenType.MAIN;
//                        } else {
//                            return TtsReplyUtils.getTtsBean("4050039");
//                        }
//                    } else {
//                        voicePosition = MediaHelper.getScreenType(position);
//                    }
//                } else {
//                    if (isSoundLocation) {
//                        voicePosition = ScreenType.MAIN;
//                    } else {
//                        return TtsReplyUtils.getTtsBean("1100028");
//                    }
//                }
//            } else {
//                voicePosition = MediaHelper.getScreenType(position);
//            }
//        } else {
//            if (FuncConstants.VALUE_SCREEN_CEIL.equals(screenName)) {
//                if (judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, FuncConstants.VALUE_SCREEN_CEIL)) {
//                    if (!MediaHelper.isCeilOpen()) {
//                        return TtsReplyUtils.getTtsBean("4050039");
//                    } else {
//                        voicePosition = ScreenType.CEILING;
//                    }
//                } else {
//                    return TtsReplyUtils.getTtsBean("1100028");
//                }
//            } else {
//                voicePosition = FuncConstants.VALUE_SCREEN_PASSENGER.equals(screenName) ? ScreenType.PASSENGER : ScreenType.MAIN;
//            }
//        }
//        DeviceHolder.INS().getDevices().getMediaCenter().setVoicePosition(voicePosition);
//        return null;
//    }

    public static String getScreenNameForPosition(int position) {
        String screenName;
        if (ScreenType.CEILING == position) {
            screenName = FuncConstants.VALUE_SCREEN_CEIL;
        } else if (ScreenType.PASSENGER == position) {
            screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
        } else {
            screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        }
        return screenName;
    }

    public static DeviceScreenType getScreenTypeForPosition(int position) {
        DeviceScreenType screenName;
        if (ScreenType.CEILING == position) {
            screenName = DeviceScreenType.CEIL_SCREEN;
        } else if (ScreenType.PASSENGER == position) {
            screenName = DeviceScreenType.PASSENGER_SCREEN;
        } else {
            screenName = DeviceScreenType.CENTRAL_SCREEN;
        }
        return screenName;
    }

    public static int getVideoDisplayId(int screenDisplayId,String appName){
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        int sourceScreen = MirrorServiceManager.INSTANCE.getSourceScreen();
        if (StringUtils.isNotBlank(mirrorPackage) && appName.equals(mirrorPackage) && sourceScreen == screenDisplayId) {
            MirrorServiceManager.INSTANCE.dispatchActivityLaunchEvent(appName,screenDisplayId);
            return MirrorServiceManager.INSTANCE.getVirtualDisplayId();
        } else {
            return screenDisplayId;
        }
    }

    public static boolean isSupportMultiScreen() {
        return DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen();
    }

    public static boolean isPassengerScreen(String position){
        if (position == null) {
            return false;
        }
        return PASSENGER_POSITION_SCREEN.contains(position);
    }

    public static boolean isCeilScreen(String position){
        if (position == null) {
            return false;
        }
        return CEIL_POSITION_SCREEN.contains(position);
    }

    /**
     * 根据屏幕id获取是否有视频正在播放
     * @param deviceScreenType
     * @return
     */
    public static boolean getVideoPlayingByDisplayId(DeviceScreenType deviceScreenType){
        int displayId;
        if (isSupportMultiScreen()) {
            if (deviceScreenType == DeviceScreenType.CEIL_SCREEN) {
                displayId = MediaHelper.getCeilingScreenDisplayId();
            } else if (deviceScreenType == DeviceScreenType.PASSENGER_SCREEN) {
                displayId = MediaHelper.getPassengerScreenDisplayId();
            } else {
                displayId = MediaHelper.getMainScreenDisplayId();
            }
        } else {
            displayId = MediaHelper.getMainScreenDisplayId();
        }
        return (IqyImpl.INSTANCE.isPlayPage(displayId) && VideoControlCenter.getInstance().isPlaying(IqyImpl.APP_NAME,displayId)) ||
                (MiguImpl.INSTANCE.isPlayPage(displayId) && MiguImpl.INSTANCE.isPlaying(displayId)) ||
                (TencentVideoImpl.INSTANCE.isPlayPage(displayId) && VideoControlCenter.getInstance().isPlaying(TencentVideoImpl.APP_NAME,displayId)) ||
                (BiliImpl.INSTANCE.isPlayPage(displayId) && BiliImpl.INSTANCE.isPlaying(displayId)) ||
                (TiktokImpl.INSTANCE.isFrontByDisplayid(displayId) && TiktokImpl.INSTANCE.isPlaying(displayId)) ||
                (UsbVideoImpl.INSTANCE.isPlayPage(displayId) && UsbVideoImpl.INSTANCE.isPlaying());
    }

    /**
     * 根据位置信息获取displayId
     * @param position
     * @return
     */
    public static int getDisplayIdByPosition(int position){
        int displayId;
        if (ScreenType.CEILING == position) {
            displayId = MegaDisplayHelper.getCeilingScreenDisplayId();
        } else if (ScreenType.PASSENGER == position) {
            displayId = MegaDisplayHelper.getPassengerScreenDisplayId();
        } else {
            displayId = MegaDisplayHelper.getMainScreenDisplayId();
        }
        return displayId;
    }


    public static int getCeilingScreenDisplayId(){
        return MegaDisplayHelper.getCeilingScreenDisplayId();
    }
    public static int getPassengerScreenDisplayId(){
        return MegaDisplayHelper.getPassengerScreenDisplayId();
    }
    public static int getMainScreenDisplayId(){
        return MegaDisplayHelper.getMainScreenDisplayId();
    }

    public static DeviceScreenType getDeviceScreenTypeByDisplayId(int displayId) {
        if (displayId == getCeilingScreenDisplayId()) {
            return DeviceScreenType.CEIL_SCREEN;
        } else if (displayId == getPassengerScreenDisplayId()) {
            return DeviceScreenType.PASSENGER_SCREEN;
        } else {
            return DeviceScreenType.CENTRAL_SCREEN;
        }
    }

    public static int getUserIdByDisplayId(int displayId) {
        if (displayId == getCeilingScreenDisplayId()) {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.CEIL_SCREEN).hashCode();
        } else if (displayId == getPassengerScreenDisplayId()) {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN).hashCode();
        } else {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.CENTRAL_SCREEN).hashCode();
        }
    }

    public static UserHandle getUserHandleByDisplayId(int displayId) {
        if (displayId == getCeilingScreenDisplayId()) {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.CEIL_SCREEN);
        } else if (displayId == getPassengerScreenDisplayId()) {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.PASSENGER_SCREEN);
        } else {
            return MegaDisplayHelper.getUserHandle(DeviceScreenType.CENTRAL_SCREEN);
        }
    }

    /**
     * R挡并且操作位置为主驾
     * @return
     */
    public static boolean isSafeLimitationAndMain(int displayId) {
        return isSafeLimitation() && displayId == getMainScreenDisplayId();
    }

    public static boolean isSafeLimitation() {
        return isH56D() && DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation();
    }

    public static boolean isGear(int gearValue) {
        return DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getIntProp(CommonSignal.COMMON_GEAR_INFO) == gearValue;
    }

    public static boolean isVideoApp(String appName){
        String pkgName = getPackageName(appName);
        return IqyImpl.APP_NAME.equals(pkgName) || TencentVideoImpl.APP_NAME.equals(pkgName)
                || MiguImpl.APP_NAME.equals(pkgName) || TiktokImpl.APP_NAME.equals(pkgName)
                || BiliImpl.APP_NAME.equals(pkgName) || ThunderKtvImpl.APP_NAME.equals(pkgName)
                || UsbVideoImpl.APP_NAME.equals(pkgName);
    }

    public static TTSBean getNotSupportTts(){
        return TtsReplyUtils.getTtsBean("4003100");
    }

    public static String getAppName(String pkgName) {
        String appName = "";
        if (IqyImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_IQY;
        } else if (MiguImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_MI_GU;
        } else if (TencentVideoImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_TENCENT;
        } else if (BiliImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_BILI;
        } else if (TiktokImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_TIKTOK;
        } else if (UsbVideoImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_LOCAL_VIDEO;
        } else if (ThunderKtvImpl.APP_NAME.equals(pkgName)) {
            appName = MediaTtsManager.APP_NAME_KTV;
        }
        return appName;
    }

    public static boolean isH56D(){
        return DeviceHolder.INS().getDevices().getCarServiceProp().isH56D();
    }

    /**
     * 根据应用名称获取包名
     * @param appName
     * @return
     */
    public static String getPackageName(String appName) {
        return DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName);
    }

    public static boolean isMusicApp(String appName){
        if (TextUtils.isEmpty(appName)) {
            return false;
        }
        for (String musicMedia : MUSIC_APP_NAME) {
            if (appName.contains(musicMedia)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMediaByAppName(String appName, String source) {
        if (TextUtils.isEmpty(appName)) {
            return false;
        }
        if (TextUtils.equals(MediaSource.voyah_music.getName(), source)) {
            return appName.contains("岚图音乐");
        } else if (TextUtils.equals(MediaSource.wy_music.getName(), source)) {
            return appName.contains("网易");
        } else if (TextUtils.equals(MediaSource.qq_music.getName(), source)) {
            return appName.contains("qq") || appName.contains("QQ");
        } else if (TextUtils.equals(MediaSource.xmly_music.getName(), source)) {
            return appName.contains("喜马");
        } else if (TextUtils.equals(MediaSource.yt_music.getName(), source)) {
            return appName.contains("云听");
        } else if (TextUtils.equals(MediaSource.migu_video.getName(), source)) {
            return appName.contains("咪咕");
        } else if (TextUtils.equals(MediaSource.tencent_video.getName(), source)) {
            return appName.contains("腾讯视频");
        } else if (TextUtils.equals(MediaSource.iqy_video.getName(), source)) {
            return appName.contains("爱奇艺");
        } else if (TextUtils.equals(MediaSource.tiktok_video.getName(), source)) {
            return appName.contains("抖音") || appName.contains("车鱼") || appName.contains("车娱") || appName.contains("火山") || appName.contains("短视频");
        } else if (TextUtils.equals(MediaSource.bili_video.getName(), source)) {
            return appName.contains("哔哩") || appName.contains("B站") || appName.contains("b站");
        } else if (TextUtils.equals(MediaSource.ls_ktv.getName(), source)) {
            return appName.contains("雷石");
        }
        return false;
    }

    public static String getArtist(String artist) {
        return TextUtils.isEmpty(artist) ? "未知歌手" : artist;
    }

    public static String getAlbum(String artist) {
        return TextUtils.isEmpty(artist) ? "未知专辑" : artist;
    }

    public static int translateLocation(String awakenLocation) {
        LogUtils.i(TAG, "translateLocation awakenLocation is " + awakenLocation);
        int location = 0; //默认主驾
        if (MediaConstant.Location.Location_LIST.contains(awakenLocation)) {
            location = MediaConstant.Location.Location_MAP.get(awakenLocation);
        }
        return location;
    }

    /**
     * 获取ai识图状态
     *      -1：网络错误、服务器异常等，可能会有-1,-2等
     *      0：开始截图
     *      1：图片上传识别中
     *      2：流式卡片展示中
     *      3：流式加载完成
     *      5：识图结束，卡片消失
     * @return
     */
    public static boolean isAIIdentifyPicStatus(int displayId){
        int screenType;
        if (displayId == MediaHelper.getCeilingScreenDisplayId()) {
            screenType = DhScreenType.SCREEN_CEILING;
        } else if (displayId == MediaHelper.getPassengerScreenDisplayId()) {
            screenType = DhScreenType.SCREEN_PASSENGER;
        } else {
            screenType = DhScreenType.SCREEN_MAIN;
        }
        AIManager.setImageRecognizeListener((sessionId, screenType1, state, msg) -> {
            LogUtils.d(TAG, "onIRResult() called with: sessionId = [" + sessionId + "], screenType = [" + screenType1 + "], state = [" + state + "], msg = [" + msg + "]");
        });
        AIManager.startImageRecognize(screenType);
        int state = AIManager.getImageRecognizeState();
        //0,1,2,3为识图中
        return state == 0 || state == 1 || state == 2 || state == 3;
    }
}
