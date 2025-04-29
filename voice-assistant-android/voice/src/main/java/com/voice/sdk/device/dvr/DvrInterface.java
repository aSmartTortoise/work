package com.voice.sdk.device.dvr;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2024/4/7
 **/
public interface DvrInterface {
    void init();

    //隐私保护是否开启
    boolean isPrivacyProtectionOpen(HashMap<String, Object> map);

    //是否为指定主驾屏操作(包含声源位置)
    boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map);

    //指定中控屏或仅声源位置
    boolean isisTargetFirstRowLeftOrSoundLocation(HashMap<String, Object> map);

    //当前车型是否为H56C
    boolean isH56C(HashMap<String, Object> map);

    //是否指定了位置信息
    boolean isAdjustPosition(HashMap<String, Object> map);

    //是否指定车内操作
    boolean isAdjustToTakeCarIn(HashMap<String, Object> map);

    //是否指定车外操作
    boolean isNotAdjustToCarOut(HashMap<String, Object> map);

    //指定切换的摄像头非车内车外
    boolean isNotAdjustToCarInOrCarOut(HashMap<String, Object> map);

    //当前车型是否为H56C且操作车内摄像头
    boolean isH56CAndAdjustCarIn(HashMap<String, Object> map);

    //------------------------行车记录仪--------------------------

    //行车记录仪开始暂停录像是否为开关控制-H56D修改为控制开关
    boolean isOnOffTakeVideo(HashMap<String, Object> map);

    //是否支持指定页面的打开关闭 H56D实时画面和录像浏览不支持
    boolean isSupportPageSwitch(HashMap<String, Object> map);

    //行车记录仪是否在前台
    boolean isDashCamOpen(HashMap<String, Object> map);

    //行车记录仪指定页面是已打开
    boolean isAssignedPageOpen(HashMap<String, Object> map);

    //行车记录仪实时画面是否已打开
    boolean isRealTimePageOpen(HashMap<String, Object> map);

    //打开行车记录仪实时录制页面
    void openRealTimePage(HashMap<String, Object> map);

    //是否为暂停行车记录仪二次确认
    boolean isDashCamPauseConfirm(HashMap<String, Object> map);

    //是否为暂停行车记录仪场景  State.DC_OP_DVR_CONFIRM
    boolean isDashCamScenarioState(HashMap<String, Object> map);

    //是否确认暂停
    boolean isPauseConfirm(HashMap<String, Object> map);

    //行车记录仪是否为暂停录像状态
    boolean isDashCamPausing(HashMap<String, Object> map);

    //行车记录仪是否为录像中状态
    boolean isDashCamInRecording(HashMap<String, Object> map);

    //打开行车记录仪
    void openDashCam(HashMap<String, Object> map);

    //关闭行车记录仪(行车记录仪后台执行)
    void closeDashCam(HashMap<String, Object> map);

    //是否为行车记录仪录像页面操作
    boolean isDashCamVideoUi(HashMap<String, Object> map);

    //打开行车记录仪指定页面
    void openAssignedPage(HashMap<String, Object> map);

    //行车记录仪开始录制
    void startDashCam(HashMap<String, Object> map);

    //行车记录仪暂停录制
    void stopDashCam(HashMap<String, Object> map);

    //指定行车记录仪操作
    boolean isDashCamTask(HashMap<String, Object> map);

    //指定相机录像操作
    boolean isCameraTask(HashMap<String, Object> map);


    //------------------------相机--------------------------

    //指定屏幕是否为后屏/后排
    boolean isRearScreen(HashMap<String, Object> map);

    //摄像头异常 被占用(360环视是否在前台)
    boolean isCameraException(HashMap<String, Object> map);

    //相机应用是否已打开
    boolean isCameraOpened(HashMap<String, Object> map);

    //相机是否为正在录像中
    boolean isCameraInRecording(HashMap<String, Object> map);

    //当前是否为拍照模式
    boolean isTakePhotoMode(HashMap<String, Object> map);

    //当前是否为录像模式
    boolean isTakeVideoMode(HashMap<String, Object> map);

    //当前是否为车内视角
    boolean isCarInVisualAngle(HashMap<String, Object> map);

    //当前是否为车外视角
    boolean isCarOutVisualAngle(HashMap<String, Object> map);

    //是否为车内录像模式
    boolean isTakeVideoInCarMode(HashMap<String, Object> map);

    //是否为车外录像模式
    boolean isTakeVideoOutCarMode(HashMap<String, Object> map);

    //是否是车内拍照模式
    boolean isTakePhotoInCarMode(HashMap<String, Object> map);

    //是否是车外拍照模式
    boolean isTakePhotoOutCarMode(HashMap<String, Object> map);

    //关闭相机应用
    void closeCamera(HashMap<String, Object> map);

    //打开相机应用
    void openCamera(HashMap<String, Object> map);

    //切换到录像模式
    void adjustToVideo(HashMap<String, Object> map);

    //切换到拍照模式
    void adjustToTakePhoto(HashMap<String, Object> map);

    //切换为车内录像
    void adjustToCarInAndVideo(HashMap<String, Object> map);

    //切换为车外录像
    void adjustToCarOutAndVideo(HashMap<String, Object> map);

    //切换为车内拍照模式
    void adjustToCarInAndTakePhoto(HashMap<String, Object> map);

    //切换为车外拍照模式
    void adjustToCarOutAndTakePhoto(HashMap<String, Object> map);

    //切换为自拍
    void adjustToTakeMySelf(HashMap<String, Object> map);

    //切换摄像头
    void adjustSecurityCamera(HashMap<String, Object> map);

    //切换到车内摄像头
    void adjustCarInSecurityCamera(HashMap<String, Object> map);

    //切换到车外摄像头
    void adjustCarOutSecurityCamera(HashMap<String, Object> map);

    //执行拍照动作
    void takePhoto(HashMap<String, Object> map);

    //是否为拍照倒计时中
    boolean isInTakePhoneCountDown(HashMap<String, Object> map);

    //当前是否为泊车场景限制
    boolean isGearsRForbidden(HashMap<String, Object> map);


    //内存是否已满
    boolean isStorageFull(HashMap<String, Object> map);

    //相机开始录像
    void cameraStartTakeVideo(HashMap<String, Object> map);

    //相机暂停录像
    void cameraPauseTakeVideo(HashMap<String, Object> map);

    //继续录像
    void takeVideoContinue(HashMap<String, Object> map);

    //停止录像
    void stopVideo(HashMap<String, Object> map);

    //相机录像状态
    int getRecordStatus(HashMap<String, Object> map);

    //是否未开始录像
    boolean isCameraNotStartRecord(HashMap<String, Object> map);

    //相机录像是否处于暂停中
    boolean isCameraInPause(HashMap<String, Object> map);

    //相机是否录像中(不包括暂停)
    boolean isCameraInRecord(HashMap<String, Object> map);

    //相机已打开且车内摄像头已打开
    boolean isCameraOpenedAndIsCarInVisualAngle(HashMap<String, Object> map);

    //相机已打开且车外摄像头已打开
    boolean isCameraOpenedAndIsCarOutVisualAngle(HashMap<String, Object> map);

    //相机已打开且为拍照模式
    boolean isCameraOpenedAndIsTakePhotoMode(HashMap<String, Object> map);

    //相机已打开且为录像模式
    boolean isCameraOpenedAndIsTakeVideoMode(HashMap<String, Object> map);

    //相机已打开且为车内录像模式
    boolean isCameraOpenedAndIsTakeVideoInCarMode(HashMap<String, Object> map);

    //相机已打开且为车外录像模式
    boolean isCameraOpenedAndIsTakeVideoOutCarMode(HashMap<String, Object> map);

    //相机已打开且为车内拍照模式
    boolean isCameraOpenedAndIsTakePhotoInCarMode(HashMap<String, Object> map);

    //相机已打开且为车外拍照模式
    boolean isCameraOpenedAndIsTakePhotoOutCarMode(HashMap<String, Object> map);


    //开启车内实时监控悬浮窗
    void startFloatCamera(HashMap<String, Object> map);

    //关闭车内实时监控悬浮窗
    void stopFloatCamera(HashMap<String, Object> map);

    //车内实时监控悬浮窗是否已打开
    boolean isFloatCameraStart(HashMap<String, Object> map);

    //指定吸顶屏且不存在吸顶屏
    boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map);

    //是否指定了屏幕
    boolean isScreenSpecified(HashMap<String, Object> map);

    //是否为按照声源位置执行映射到屏幕
    boolean isOnlySoundLocation(HashMap<String, Object> map);

    boolean isSupportAllPosition(HashMap<String, Object> map);


    //------------------------电子后视镜--------------------------

    //电子后视镜是否已打开
    boolean isBackMirrorOpen(HashMap<String, Object> map);

    //电子后视镜当前摄像头状态 1：宝宝守护 2：电子后视镜
    int getBackMirrorType(HashMap<String, Object> map);
//
    //电子后视镜切换到指定摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
    void switchbackMirrorType(HashMap<String, Object> map);

    //电子后视镜切换到指定摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
    void adjustBackMirrorType(HashMap<String, Object> map);
//
//    //电子后视镜当前是否为全屏
//    boolean isBackMirrorInBigWindow(HashMap<String, Object> map);
//
//    //电子后视镜切换为全屏、小窗 全屏设置true 小窗设置为 false
//    void zoomBackMirror(HashMap<String, Object> map);
//
//    //打开关闭电子后视镜 1:后排宝宝守护 4:电子后视镜
//    void  switchBackMirror(HashMap<String, Object> map);
}
