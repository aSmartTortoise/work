package com.voice.sdk.device.dvr;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public interface MyCameraInterface {

    void init();

    void switchCamera(int cameraType);

    void takePicture();

    boolean isOnOffTakeVideo();

    boolean isSupportPageSwitch(String uiName);

    boolean isAssignedPageOpen(String uiName);

    void openAssignedPage(String uiName);

    int getDvrView(int displayId);

    int getDvrTabulation(int displayId);

    void openDvrView(int display, int type);

    void openNormalDvr(int displayId);

    void openCrashDvr(int displayId);

    void openGuardDvr(int displayId);

    void openCollectDvr(int displayId);

    int getCameraStatus();

    void startDashCam();

    void cameraStartTakeVideo();

    void stopDashCam();

    boolean isAppForeground();

    boolean isDashCamOpen();

    boolean isInRecording();

    void setCaptureType(int captureType);

    void openCamera(int cameraType, int source);

    int getCameraType();

    int getCaptureType();

    int getRecordStatus();

    boolean isFloatCameraStart();

    void pauseRecord();

    void resumeRecord();

    void stopRecord();

    void startFloatCamera();

    void stopFloatCamera();

    void startMirrorCamera();

    void closeBackMirror();

    boolean isInTakePhoneCountDown(); //当前是否为拍照倒计时中

    boolean isStorageFull(); //内存是否已满

    boolean isBackMirrorOpen(); //电子后视镜是否已打开

    int getBackMirrorType(); //电子后视镜当前摄像头状态

    void switchBackMirrorType(int type); //电子后视镜切换到指定摄像头

    boolean isBackMirrorInBigWindow(); //电子后视镜当前是否处于全屏

    void zoomBackMirror(boolean zoomType); //设置电子后视镜为全屏或小窗

    void switchBackMirror(int switchType); //打开关闭电子后视镜
}
