package com.voyah.ai.logic.dc.dvr;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.dvr.DvrInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.dc.AbsDevices;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/4/7
 **/
public class DvrControlImpl extends AbsDevices implements DvrInterface {
    private static final String TAG = DvrControlImpl.class.getSimpleName();

    //todo:后续优化统一放到CameraManager层定义常量
    private static final String REAL_TIME = "real_time_image";//实时画面
    private static final String VIDEO = "video";//行车记录仪录像页面(实时画面)
    private static final String DRIVING_VIDEO = "driving_video";//行车录像
    private static final String EMERGENCY_VIDEO = "emergency_video";//紧急录像
    private static final String SENTINEL_VIDEO = "sentinel_video";//哨兵录像
    private static final String COLLECT_LIST = "collection";//收藏列表
    private static final String SETTING = "setting";//设置
    private static final String VIDEO_BROWSING = "video_browsing";//录像浏览

    public static final Map<String, String> UI_MAP = new HashMap() {
        {
            put(REAL_TIME, "实时画面");
            put(VIDEO, "实时画面");
            put(DRIVING_VIDEO, "行车录像页面");
            put(EMERGENCY_VIDEO, "紧急录像页面");
            put(SENTINEL_VIDEO, "哨兵录像页面");
            put(COLLECT_LIST, "收藏列表页面");
            put(SETTING, "设置页面");
            put(VIDEO_BROWSING, "录像浏览页面");

        }
    };

    private static final String PKG_AVM = "com.voyah.cockpit.parking"; //360环视

    private static final String CAR_IN = "inside_car"; //车内
    private static final String CAR_OUT = "outside_car"; //车外
    private static final String SELF = "self"; //自拍


    public void init() {
        LogUtils.i(TAG, "initDvr");

    }


    public DvrControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "dvr";
    }

    //获取指定操作摄像头位置
    public String getAssignCameraPosition(HashMap<String, Object> map) {
        String position = (String) getValueInContext(map, "position");
        LogUtils.d(TAG, "getCurrentPosition position is " + position);
        return position;
    }


    /**
     * 隐私保护是否开启
     *
     * @return
     */
    @Override
    public boolean isPrivacyProtectionOpen(HashMap<String, Object> map) {
        int privacySecurityStatus = DeviceHolder.INS().getDevices().getLauncher().getPrivacySecurity();
        return privacySecurityStatus == 1;
    }

    @Override
    public boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map) {
        boolean isTargetFirstRowLeftScreen;
        String screen = getAssignScreen(map);
        String nluInfo = (String) getValueInContext(map, FlowChatContext.DSKey.NLU_INFO);
        isTargetFirstRowLeftScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL);
        LogUtils.d(TAG, "isTargetFirstRowLeftScreen screen:" + screen + " ,isTargetFirstRowLeftScreen:" + isTargetFirstRowLeftScreen);
        if (!isTargetFirstRowLeftScreen) {
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, screen);
            //todo:相机、行车记录仪
            if (nluInfo.contains("dvr_camera_app"))
                map.put("app_name", "相机");
            else
                map.put("app_name", "行车记录仪");
        }
        return isTargetFirstRowLeftScreen;
    }

    @Override
    public boolean isisTargetFirstRowLeftOrSoundLocation(HashMap<String, Object> map) {
        boolean isTargetFirstRowLeftScreen;
        String screen = getAssignScreen(map);
        String nluInfo = (String) getValueInContext(map, FlowChatContext.DSKey.NLU_INFO);
        isTargetFirstRowLeftScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL);
        if (!isTargetFirstRowLeftScreen) {
            String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
            String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
            String soundLocation = getSoundLocation(map);
            isTargetFirstRowLeftScreen = StringUtils.isBlank(screenName) && StringUtils.isBlank(position) && !StringUtils.isBlank(soundLocation);
        }
        LogUtils.d(TAG, "isTargetFirstRowLeftScreen screen:" + screen + " ,isTargetFirstRowLeftScreen:" + isTargetFirstRowLeftScreen);
        if (!isTargetFirstRowLeftScreen) {
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, screen);
            //todo:相机、行车记录仪
            if (nluInfo.contains("dvr_camera_app"))
                map.put("app_name", "相机");
            else
                map.put("app_name", "行车记录仪");
        }
        return isTargetFirstRowLeftScreen;
    }

    /**
     * @param map
     * @return
     */
    @Override
    public boolean isH56C(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        LogUtils.d(TAG, "isH56C carType:" + carType);
        return StringUtils.equals(carType, "H56C");
    }

    @Override
    public boolean isAdjustPosition(HashMap<String, Object> map) {
        String position = "";
        if (map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION))
            position = (String) getValueInContext(map, "positions");
        LogUtils.d(TAG, "isAdjustPosition positions:" + position);
        return !StringUtils.isBlank(position);
    }

    /**
     * @param map
     * @return
     */
    @Override
    public boolean isAdjustToTakeCarIn(HashMap<String, Object> map) {
        String position = "";
        if (map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION))
            position = (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION);
        LogUtils.d(TAG, "isAdjustToTake positions:" + position);
        return StringUtils.equals(position, CAR_IN);
    }

    @Override
    public boolean isNotAdjustToCarOut(HashMap<String, Object> map) {
        String position = "";
        if (map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION))
            position = (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION);
        LogUtils.d(TAG, "isNotAdjustToCarOut positions:" + position);
        return StringUtils.equals(position, CAR_OUT);
    }

    @Override
    public boolean isNotAdjustToCarInOrCarOut(HashMap<String, Object> map) {
        String position = "";
        if (map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION))
            position = (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION);
        LogUtils.d(TAG, "isNotAdjustToCarInOrCarOut positions:" + position);
        return !StringUtils.equals(position, CAR_IN) && !StringUtils.equals(position, CAR_OUT) && !StringUtils.equals(position, SELF) && !StringUtils.isBlank(position);
    }

    @Override
    public boolean isH56CAndAdjustCarIn(HashMap<String, Object> map) {
        return isH56C(map) && isAdjustToTakeCarIn(map);
    }

    @Override
    public boolean isOnOffTakeVideo(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCamera().isOnOffTakeVideo();
    }

    @Override
    public boolean isSupportPageSwitch(HashMap<String, Object> map) {
        String uiNameKey = (String) getValueInContext(map, "ui_name");
        return DeviceHolder.INS().getDevices().getCamera().isSupportPageSwitch(uiNameKey);
    }

    //------------------------行车记录仪--------------------------
    //行车记录仪是否在前台
    @Override
    public boolean isDashCamOpen(HashMap<String, Object> map) {
        //todo: 同步56最新代码后需要替换改判断为 MultiAppHelper.INSTANCE.isForeGroundApp(PKG_DVR_LOC, FuncConstants.VALUE_SCREEN_CENTRAL);
        boolean isDashCamOpen = DeviceHolder.INS().getDevices().getCamera().isDashCamOpen();
        LogUtils.i(TAG, "isDashCamOpen " + isDashCamOpen);
//        if (isDashCamOpen)
        map.put("app_name", "行车记录仪");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        return isDashCamOpen;
    }

    //打开行车记录仪
    @Override
    public void openDashCam(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openDashCam");
        map.put("app_name", "行车记录仪");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "dvr_app", "dvr_app_dashCam", "", map);
    }

    //关闭行车记录仪(行车记录仪后台执行)
    @Override
    public void closeDashCam(HashMap<String, Object> map) {
        LogUtils.i(TAG, "closeDashCam");
        map.put("app_name", "行车记录仪");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "dvr_app", "dvr_app_dashCam", "", map);
    }

    @Override
    public boolean isDashCamVideoUi(HashMap<String, Object> map) {
        String uiNameKey = (String) getValueInContext(map, "ui_name");
        LogUtils.d(TAG, "isDashCamVideoUi uiNameKey:" + uiNameKey);
        return !StringUtils.isBlank(uiNameKey) && StringUtils.equals(VIDEO, uiNameKey);
    }

    //行车记录仪指定页面是否已打开
    @Override
    public boolean isAssignedPageOpen(HashMap<String, Object> map) {
        String uiNameKey = (String) getValueInContext(map, "ui_name");
        boolean isAssignedPageOpen = DeviceHolder.INS().getDevices().getCamera().isAssignedPageOpen(uiNameKey);
        if (isAssignedPageOpen)
            map.put("dvr_ui_name", UI_MAP.get(uiNameKey));
        return isAssignedPageOpen;
    }

    @Override
    public boolean isRealTimePageOpen(HashMap<String, Object> map) {
        // 1:录像浏览界面 2:录像预览界面(实时) 3:设置界面
        int dvrView = DeviceHolder.INS().getDevices().getCamera().getDvrView(0);
        map.put("dvr_ui_name", "实时画面");
        LogUtils.d(TAG, "isRealTimePageOpen dvrView:" + dvrView);
        return dvrView == DvrConstants.VIDEO_PREVIEW;
    }

    @Override
    public void openRealTimePage(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openRealTimePage");
        DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_PREVIEW);
    }

    //打开行车记录仪指定页面
    @Override
    public void openAssignedPage(HashMap<String, Object> map) {
//        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        String uiNameKey = (String) getValueInContext(map, "ui_name");
//        String ui_name = "";
//        LogUtils.i(TAG, "openAssignedPage uiNameKey is " + uiNameKey);
//        switch (uiNameKey) {
//            case REAL_TIME: //实时
//                ui_name = "实时画面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_PREVIEW);
//                break;
//            case DRIVING_VIDEO: //行车录像
//                ui_name = "行车录像页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openNormalDvr(0);
//                break;
//            case EMERGENCY_VIDEO: //紧急录像
//                ui_name = "紧急录像页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openCrashDvr(0);
//                break;
//            case SENTINEL_VIDEO: //哨兵录像
//                ui_name = "哨兵录像页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openGuardDvr(0);
//                break;
//            case COLLECT_LIST: //收藏列表
//                ui_name = "收藏列表页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openCollectDvr(0);
//                break;
//            case SETTING: //设置
//                ui_name = "设置页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_SETTING);
//                break;
//            case VIDEO_BROWSING: //录像浏览
//                ui_name = "录像浏览页面";
//                map.put("dvr_ui_name", ui_name);
//                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIEW_BROWSE);
//                break;
//        }
        map.put("dvr_ui_name", UI_MAP.get(uiNameKey));
        DeviceHolder.INS().getDevices().getCamera().openAssignedPage(uiNameKey);
    }

    //是否为暂停行车记录仪二次确认
    @Override
    public boolean isDashCamPauseConfirm(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isDashCamPauseConfirm");
        return keyContextInMap(map, "choose_type");
    }

    //是否为暂停行车记录仪场景(非指定继续相机录像)  State.DC_OP_DVR_CONFIRM
    @Override
    public boolean isDashCamScenarioState(HashMap<String, Object> map) {
        String dsScenario = (String) getValueInContext(map, FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        String controlTag = (String) getValueInContext(map, "control_tag");
        LogUtils.i(TAG, "dsScenario is " + dsScenario + " ,controlTag is " + controlTag);
        return StringUtils.equals(dsScenario, "State.DC_OP_DVR_CONFIRM") && !StringUtils.equals(controlTag, "camera");
    }

    //是否确认暂停
    @Override
    public boolean isPauseConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        LogUtils.i(TAG, "isPauseConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    //行车记录仪是否为暂停录像状态
    @Override
    public boolean isDashCamPausing(HashMap<String, Object> map) {
        // 3:录像中 4:录像暂停 5:当前摄像头异常
        int cameraStatus = DeviceHolder.INS().getDevices().getCamera().getCameraStatus();
        boolean isCameraPause = cameraStatus == DvrConstants.CAMERA_STATUS_STOP;
        LogUtils.i(TAG, "isDashCamPausing isCameraPause is " + isCameraPause);
        return isCameraPause;
    }

    //行车记录仪是否为录像中状态
    @Override
    public boolean isDashCamInRecording(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isDashCamInRecording");
        int cameraStatus = DeviceHolder.INS().getDevices().getCamera().getCameraStatus();
        LogUtils.i(TAG, "isDashCamPausing cameraStatus is " + cameraStatus);
        return cameraStatus == DvrConstants.CAMERA_STATUS_RECORDING;
    }

    //行车记录仪开始录制
    @Override
    public void startDashCam(HashMap<String, Object> map) {
        LogUtils.i(TAG, "startDashCam");
        DeviceHolder.INS().getDevices().getCamera().startDashCam();
    }

    //行车记录仪暂停录制
    @Override
    public void stopDashCam(HashMap<String, Object> map) {
        LogUtils.i(TAG, "stopDashCam");
        DeviceHolder.INS().getDevices().getCamera().stopDashCam();
    }

    @Override
    public boolean isDashCamTask(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isDashCamTask");
        boolean isDashCam = false;
        if (keyContextInMap(map, "control_tag") && StringUtils.equals((String) getValueInContext(map, "control_tag"), "dashcam"))
            isDashCam = true;
        LogUtils.i(TAG, "isDashCamTask isDashCam is " + isDashCam);
        return isDashCam;
    }

    @Override
    public boolean isCameraTask(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isCameraTask");
        boolean isCamera = false;
        if (keyContextInMap(map, "control_tag") && StringUtils.equals((String) getValueInContext(map, "control_tag"), "camera"))
            isCamera = true;
        LogUtils.i(TAG, "isCameraTask isCamera is " + isCamera);
        return isCamera;
    }

    //------------------------相机--------------------------

    //指定屏幕是否为后屏/后排
    @Override
    public boolean isRearScreen(HashMap<String, Object> map) {
        //position != rear_side
        //h37没有多屏幕，默认false
        LogUtils.i(TAG, "isRearScreen");
        return false;
    }

    //摄像头是否被占用
    @Override
    public boolean isCameraException(HashMap<String, Object> map) {
//        //1.360在前台
//        return DeviceHolder.INS().getDevices().getLauncher().isParkOpen();
        //对手件支持小窗口打开,没有该异常状态
        return false;
    }

    //相机应用是否已打开
    @Override
    public boolean isCameraOpened(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isCameraOpened");
        boolean isCameraOpened = DeviceHolder.INS().getDevices().getCamera().isAppForeground();
        LogUtils.i(TAG, "isCameraOpened is " + isCameraOpened);
        map.put("app_name", "相机");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        return isCameraOpened;
    }

    //关闭相机应用
    @Override
    public void closeCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "closeCamera");
        map.put("app_name", "相机");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "dvr_app", "dvr_app_camera", "", map);
    }

    //打开相机应用
    @Override
    public void openCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openCamera");
        map.put("app_name", "相机");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, "中控屏");
        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "dvr_app", "dvr_app_camera", "", map);
    }

    //相机是否为录像中：开始录像后包括暂停状态都会返回true，停止和开始录制前返回false
    @Override
    public boolean isCameraInRecording(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isCameraInRecording");
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "isCameraInRecording cameraManager is null");
//            return false;
//        }
        boolean isInRecording = DeviceHolder.INS().getDevices().getCamera().isInRecording();
        LogUtils.i(TAG, "isCameraInRecording isInRecording:" + isInRecording);
        return isInRecording;
    }


    //切换到录像模式 一语触达
    @Override
    public void adjustToVideo(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "adjustToVideo cameraManager is null");
//            return;
//        }
        if (isCameraOpened(map)) {
            boolean isTakeVideoMode = isTakeVideoMode(map);
            LogUtils.i(TAG, "adjustToVideo isTakeVideoMode:" + isTakeVideoMode);
            if (!isTakeVideoMode)
                DeviceHolder.INS().getDevices().getCamera().setCaptureType(Constant.CaptureType.RECORD);
        } else {
            int cameraType = DeviceHolder.INS().getDevices().getCamera().getCameraType();
            LogUtils.d(TAG, "adjustToVideo cameraType:" + cameraType);
            DeviceHolder.INS().getDevices().getCamera().openCamera(cameraType, Constant.CaptureType.RECORD);
        }
    }

    //切换到拍照模式 一语触达
    @Override
    public void adjustToTakePhoto(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "adjustToTakePhoto cameraManager is null");
//            return;
//        }
        if (isCameraOpened(map)) {
            boolean isTakePhotoMode = isTakePhotoMode(map);
            LogUtils.i(TAG, "adjustToVideo isTakePhotoMode:" + isTakePhotoMode);
            if (!isTakePhotoMode)
                DeviceHolder.INS().getDevices().getCamera().setCaptureType(Constant.CaptureType.TAKE_PHOTO);
        } else {
            int cameraType = DeviceHolder.INS().getDevices().getCamera().getCameraType();
            LogUtils.d(TAG, "adjustToTakePhoto cameraType:" + cameraType);
            DeviceHolder.INS().getDevices().getCamera().openCamera(cameraType, Constant.CaptureType.TAKE_PHOTO);
        }
    }

    //切换为车内录像  一语触达
    @Override
    public void adjustToCarInAndVideo(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustToCarInAndVideo");
        if (isCameraOpened(map)) {
            adjustCarInSecurityCamera(map);
            adjustToVideo(map);
        } else {
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.OMS, Constant.CaptureType.RECORD);
        }
    }

    //切换为车外录像 一语触达
    @Override
    public void adjustToCarOutAndVideo(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustToCarOutAndVideo");
        if (isCameraOpened(map)) {
            adjustCarOutSecurityCamera(map);
            adjustToVideo(map);
        } else {
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.DVR, Constant.CaptureType.RECORD);
        }
    }

    //切换为车内拍照模式 一语触达
    @Override
    public void adjustToCarInAndTakePhoto(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustToCarInAndTakePhoto");
        if (isCameraOpened(map)) {
            adjustCarInSecurityCamera(map);
            adjustToTakePhoto(map);
        } else {
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.OMS, Constant.CaptureType.TAKE_PHOTO);
        }
    }

    //切换为车外拍照模式 一语触达
    @Override
    public void adjustToCarOutAndTakePhoto(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustToCarOutAndTakePhoto");
        if (isCameraOpened(map)) {
            adjustCarOutSecurityCamera(map);
            adjustToTakePhoto(map);
        } else {
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.DVR, Constant.CaptureType.TAKE_PHOTO);
        }
    }

    //切换为自拍 一语触达
    @Override
    public void adjustToTakeMySelf(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustToTakeMySelf");
        if (isCameraOpened(map)) {
            adjustCarInSecurityCamera(map);
            adjustToTakePhoto(map);
        } else {
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.OMS, Constant.CaptureType.TAKE_PHOTO);
        }
    }

    //切换摄像头 一语触达
    @Override
    public void adjustSecurityCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustSecurityCamera");
        int cameraType = DeviceHolder.INS().getDevices().getCamera().getCameraType();
        int captureType = DeviceHolder.INS().getDevices().getCamera().getCaptureType();
        LogUtils.i(TAG, "isCarInVisualAngle captureType:" + captureType);
        if (cameraType == 1)
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.DVR, captureType);
        else if (cameraType == 3)
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.OMS, captureType);
    }

    //切换到车内摄像头  一语触达
    @Override
    public void adjustCarInSecurityCamera(HashMap<String, Object> map) {
        if (isCameraOpened(map)) {
            boolean isCarInVisualAngle = isCarInVisualAngle(map);
            LogUtils.i(TAG, "adjustCarInSecurityCamera isCarInVisualAngle:" + isCarInVisualAngle);
            //1:OMS摄像头 3:旅拍摄像头
            if (!isCarInVisualAngle)
                DeviceHolder.INS().getDevices().getCamera().switchCamera(CameraConstant.CameraType.OMS);
        } else {
            int captureType = DeviceHolder.INS().getDevices().getCamera().getCaptureType();
            LogUtils.i(TAG, "isCarInVisualAngle captureType:" + captureType);
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.OMS, captureType);
        }
    }

    //切换到车外摄像头 一语触达
    @Override
    public void adjustCarOutSecurityCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "adjustCarOutSecurityCamera");
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "adjustCarOutSecurityCamera cameraManager is null");
//            return;
//        }
        if (isCameraOpened(map)) {
            boolean isCarOutVisualAngle = isCarOutVisualAngle(map);
            LogUtils.i(TAG, "adjustCarInSecurityCamera isCarOutVisualAngle:" + isCarOutVisualAngle);
            //1:OMS摄像头 3:旅拍摄像头
            if (!isCarOutVisualAngle)
                DeviceHolder.INS().getDevices().getCamera().switchCamera(CameraConstant.CameraType.DVR);
        } else {
            int captureType = DeviceHolder.INS().getDevices().getCamera().getCaptureType();
            LogUtils.i(TAG, "isCarInVisualAngle captureType:" + captureType);
            DeviceHolder.INS().getDevices().getCamera().openCamera(Constant.CameraType.DVR, captureType);
        }
    }

    //当前是否为拍照模式
    @Override
    public boolean isTakePhotoMode(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "isTakePhotoMode cameraManager is null");
//            return false;
//        }
        // 0:拍照 1:录像
        int captureType = DeviceHolder.INS().getDevices().getCamera().getCaptureType();
        LogUtils.i(TAG, "isTakePhotoMode captureType is " + captureType);
        return captureType == Constant.CaptureType.TAKE_PHOTO;
    }

    //当前是否为录像模式
    @Override
    public boolean isTakeVideoMode(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "isTakeVideoMode cameraManager is null");
//            return false;
//        }
        // 0:拍照 1:录像
        int captureType = DeviceHolder.INS().getDevices().getCamera().getCaptureType();
        LogUtils.i(TAG, "isTakeVideoMode captureType is " + captureType);
        return captureType == Constant.CaptureType.RECORD;
    }

    //当前是否为车内视角
    @Override
    public boolean isCarInVisualAngle(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "isCarInVisualAngle cameraManager is null");
//            return false;
//        }
        // 1:OMS摄像头 3:旅拍摄像头
        int cameraType = DeviceHolder.INS().getDevices().getCamera().getCameraType();
        LogUtils.i(TAG, "isCarInVisualAngle cameraType:" + cameraType);
        return Constant.CameraType.OMS == cameraType;
    }

    //是否为车内录像模式
    @Override
    public boolean isTakeVideoInCarMode(HashMap<String, Object> map) {
        return isTakeVideoMode(null) && isCarInVisualAngle(null);
    }

    //当前是否为车外视角
    @Override
    public boolean isCarOutVisualAngle(HashMap<String, Object> map) {
        // 1:OMS摄像头 3:旅拍摄像头
        int cameraType = DeviceHolder.INS().getDevices().getCamera().getCameraType();
        LogUtils.i(TAG, "isCarOutVisualAngle cameraType:" + cameraType);
        return Constant.CameraType.DVR == cameraType;
    }

    //是否为车外录像模式
    @Override
    public boolean isTakeVideoOutCarMode(HashMap<String, Object> map) {
        return isCarOutVisualAngle(null) && isTakeVideoMode(null);
    }

    @Override
    public boolean isTakePhotoInCarMode(HashMap<String, Object> map) {
        return isCarInVisualAngle(null) && isTakePhotoMode(null);
    }

    @Override
    public boolean isTakePhotoOutCarMode(HashMap<String, Object> map) {
        return isCarOutVisualAngle(null) && isTakePhotoMode(null);
    }

    //执行拍照动作
    @Override
    public void takePhoto(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "takePhoto cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "takePhoto");
        DeviceHolder.INS().getDevices().getCamera().takePicture();
    }

    @Override
    public boolean isInTakePhoneCountDown(HashMap<String, Object> map) {
        boolean isInTakePhoneCountDown = DeviceHolder.INS().getDevices().getCamera().isInTakePhoneCountDown();
        LogUtils.d(TAG, "isInTakePhoneCountDown:" + isInTakePhoneCountDown);
        return isInTakePhoneCountDown;
    }

    @Override
    public boolean isGearsRForbidden(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation() && isH56DCar(map);
    }

    @Override
    public boolean isStorageFull(HashMap<String, Object> map) {
        boolean isStorageFull = DeviceHolder.INS().getDevices().getCamera().isStorageFull();
        LogUtils.d(TAG, "isStorageFull:" + isStorageFull);
        return isStorageFull;
    }

    //开始相机录像
    @Override
    public void cameraStartTakeVideo(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "cameraStartTakeVideo cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "cameraStartTakeVideo");
        DeviceHolder.INS().getDevices().getCamera().cameraStartTakeVideo();
    }

    //暂停相机录像
    @Override
    public void cameraPauseTakeVideo(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "cameraPauseTakeVideo cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "cameraPauseTakeVideo");
        DeviceHolder.INS().getDevices().getCamera().pauseRecord();
    }


    //继续录像
    @Override
    public void takeVideoContinue(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "takeVideoContinue cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "adjustTakeVideoContinue");
        DeviceHolder.INS().getDevices().getCamera().resumeRecord();
    }

    //停止录像
    @Override
    public void stopVideo(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "stopVideo cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "stopVideo");
        DeviceHolder.INS().getDevices().getCamera().stopRecord();
    }

    @Override
    public int getRecordStatus(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "getRecordStatus cameraManager is null");
//            return -1;
//        }
        LogUtils.i(TAG, "getRecordStatus");
        return DeviceHolder.INS().getDevices().getCamera().getRecordStatus();
    }

    @Override
    public boolean isCameraNotStartRecord(HashMap<String, Object> map) {
        int recordStatus = getRecordStatus(map);
        LogUtils.i(TAG, "isCameraNotStartRecord recordStatus is " + recordStatus);
        return recordStatus == RecordStatus.IDLE;
    }

    @Override
    public boolean isCameraInPause(HashMap<String, Object> map) {
        int recordStatus = getRecordStatus(map);
        LogUtils.i(TAG, "isCameraInPause recordStatus is " + recordStatus);
        return recordStatus == RecordStatus.PAUSE_RECORDING;
    }

    @Override
    public boolean isCameraInRecord(HashMap<String, Object> map) {
        int recordStatus = getRecordStatus(map);
        LogUtils.i(TAG, "isCameraInRecord recordStatus is " + recordStatus);
        return recordStatus == RecordStatus.STARTED || recordStatus == RecordStatus.RESUME_RECORDING;
    }

    @Override
    public boolean isCameraOpenedAndIsCarInVisualAngle(HashMap<String, Object> map) {
        return isCameraOpened(map) && isCarInVisualAngle(map);
    }

    @Override
    public boolean isCameraOpenedAndIsCarOutVisualAngle(HashMap<String, Object> map) {
        return isCameraOpened(map) && isCarOutVisualAngle(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakePhotoMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakePhotoMode(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakeVideoMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakeVideoMode(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakeVideoInCarMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakeVideoInCarMode(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakeVideoOutCarMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakeVideoOutCarMode(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakePhotoInCarMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakePhotoInCarMode(map);
    }

    @Override
    public boolean isCameraOpenedAndIsTakePhotoOutCarMode(HashMap<String, Object> map) {
        return isCameraOpened(map) && isTakePhotoOutCarMode(map);
    }

    @Override
    public void startFloatCamera(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "startFloatCamera cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "startFloatCamera");
        DeviceHolder.INS().getDevices().getCamera().startMirrorCamera();
    }

    @Override
    public void stopFloatCamera(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "stopFloatCamera cameraManager is null");
//            return;
//        }
        LogUtils.i(TAG, "stopFloatCamera");
        DeviceHolder.INS().getDevices().getCamera().stopFloatCamera();
    }

    @Override
    public boolean isFloatCameraStart(HashMap<String, Object> map) {
//        if (null == cameraManager) {
//            LogUtils.i(TAG, "isFloatCameraStart cameraManager is null");
//            return false;
//        }
        boolean isFloatCameraStart = DeviceHolder.INS().getDevices().getCamera().isFloatCameraStart();
        LogUtils.i(TAG, "isFloatCameraStart is " + isFloatCameraStart);
        return isFloatCameraStart;
    }

    @Override
    public boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map) {
        return isBaseCeilScreen(map) && !isBaseHaveCeilScreen();
    }

    @Override
    public boolean isScreenSpecified(HashMap<String, Object> map) {
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String nluInfo = (String) getValueInContext(map, FlowChatContext.DSKey.NLU_INFO);
        LogUtils.d(TAG, "isScreenSpecified screen_name:" + screen_name);
        boolean isScreenSpecified = !StringUtils.isBlank(screen_name);
        if (!isScreenSpecified)
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        else
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, getAssignScreen(map));

        if (nluInfo.contains("dvr_camera_app#switch")) {
            map.put("app_name", "相机");
        } else if (nluInfo.contains("dvr_dashcam_app#switch")) {
            map.put("app_name", "行车记录仪");
        }
        return isScreenSpecified;
    }

    @Override
    public boolean isOnlySoundLocation(HashMap<String, Object> map) {
        boolean isOnlySoundLocation = false;
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String assignPosition = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
//        String tab_name = map.containsKey("tab_name") ? (String) getValueInContext(map, "tab_name") : "";
        if (StringUtils.isBlank(screen_name) && StringUtils.isBlank(assignPosition))
            isOnlySoundLocation = true;
        LogUtils.d(TAG, "isOnlySoundLocation:" + isOnlySoundLocation);
//        if (StringUtils.equals(tab_name, "negative_screen"))
//            map.put("app_name", "负一屏");
//        else if (StringUtils.equals(tab_name, "application_list"))
//            map.put("app_name", "应用中心");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        return isOnlySoundLocation;
    }

    @Override
    public boolean isSupportAllPosition(HashMap<String, Object> map) {
        //todo:临时这样处理
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        LogUtils.d(TAG, "isSupportAllPosition carType:" + carType);
        return carType.contains("H37");
    }

    @Override
    public boolean isBackMirrorOpen(HashMap<String, Object> map) {
        boolean isBackMirrorOpen = DeviceHolder.INS().getDevices().getCamera().isBackMirrorOpen();
        LogUtils.d(TAG, "isBackMirrorOpen:" + isBackMirrorOpen);
        return isBackMirrorOpen;
    }

    @Override
    public int getBackMirrorType(HashMap<String, Object> map) {
        //电子后视镜当前摄像头状态 1：宝宝守护(车内) 2：电子后视镜(车外)
        int backMirrorType = DeviceHolder.INS().getDevices().getCamera().getBackMirrorType();
        LogUtils.d(TAG, "backMirrorType:" + backMirrorType);
        return backMirrorType;
    }

    @Override
    public void switchbackMirrorType(HashMap<String, Object> map) {
        //切换摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
        int currentBackMirrorType = getBackMirrorType(map);
        LogUtils.d(TAG, "switchbackMirrorType currentBackMirrorType:" + currentBackMirrorType);
        if (currentBackMirrorType == Constant.CameraType.OMS)
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.CMS);
        else if (currentBackMirrorType == Constant.CameraType.CMS)
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.OMS);
    }

    @Override
    public void adjustBackMirrorType(HashMap<String, Object> map) {
        String position = map.containsKey("position") ? (String) getValueInContext(map, "position") : "";
        LogUtils.d(TAG, "isTargetMirrorType position:" + position);
        if (StringUtils.equals(position, "inside_car") || StringUtils.equals(position, "self")) {
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.CMS);
        } else if (StringUtils.equals(position, "outside_car")) {
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.OMS);
        }
    }
//
//    @Override
//    public boolean isBackMirrorInBigWindow(HashMap<String, Object> map) {
//        boolean isBigWindow = DeviceHolder.INS().getDevices().getCamera().isBackMirrorInBigWindow();
//        LogUtils.d(TAG, "isBackMirrorInBigWindow isBigWindow:" + isBigWindow);
//        return isBigWindow;
//    }
//
//    @Override
//    public void zoomBackMirror(HashMap<String, Object> map) {
////        boolean isBigWindow = isBackMirrorInBigWindow(map);
//        String switch_mode = map.containsKey("switch_mode") ? (String) getValueInContext(map, "switch_mode") : "";
//        LogUtils.d(TAG, "zoomBackMirror switch_mode:" + switch_mode);
//        if (StringUtils.equals(switch_mode, Constant.SwitchMode.FULL_SCREEN))
//            DeviceHolder.INS().getDevices().getCamera().zoomBackMirror(true);
//        else if (StringUtils.equals(switch_mode, Constant.SwitchMode.SMALL_WINDOW))
//            DeviceHolder.INS().getDevices().getCamera().zoomBackMirror(false);
//    }
//
//    @Override
//    public void switchBackMirror(HashMap<String, Object> map) {
//        String position = map.containsKey("position") ? (String) getValueInContext(map, "position") : "";
//        LogUtils.d(TAG, "switchBackMirror position:" + position);
//        DeviceHolder.INS().getDevices().getCamera().switchBackMirrorType(StringUtils.equals(position, "outside_car") ? Constant.CameraType.CMS : Constant.CameraType.OMS);
//    }


    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        if (StringUtils.isBlank(str)) {
            LogUtils.d(TAG, "replacePlaceHolder str is blank");
            return "";
        }
        LogUtils.d(TAG, "replacePlaceHolder str:" + str);
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "app_name":
                String appName = (String) getValueInContext(map, "app_name");
                LogUtils.d(TAG, "replacePlaceHolder appName:" + appName);
                if (!StringUtils.isBlank(str) && StringUtils.contains(str, "@{app_name}"))
                    str = str.replace("@{app_name}", appName);
                break;
            case "fn_name":
                String uiName = (String) getValueInContext(map, "dvr_ui_name");
                LogUtils.d(TAG, "replacePlaceHolder uiName:" + uiName);
                if (!StringUtils.isBlank(str) && StringUtils.contains(str, "@{fn_name}"))
                    str = str.replace("@{fn_name}", uiName);
                break;
            case "screen_name":
                String screenName = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
                String replaceStr = "中控屏";
                if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_PASSENGER)) {
                    replaceStr = "副驾屏";
                } else if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_CEIL)) {
                    replaceStr = "吸顶屏";
                }
                str = str.replace("@{screen_name}", replaceStr);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);


        LogUtils.e(TAG, "tts :" + str);
        return str;
    }


    /**
     * 设置二次交互的返回码
     */
    public void setSecondInteractionParams(HashMap<String, Object> map) {
        map.put(FlowChatContext.FlowChatResultKey.RESULT_CODE, 1);
    }

}
