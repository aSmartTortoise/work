package com.voyah.ai.device.voyah.h37.dvr;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.blankj.utilcode.util.Utils;
import com.mega.dvrsdk.DvrConstants;
import com.mega.dvrsdk.DvrServiceProxy;
import com.mega.dvrsdk.IDvrCameraStatusListener;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.dvr.MyCameraInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.common.camera.CameraManager;
import com.voyah.cockpit.common.camera.callback.IConnectCallback;
import com.voyah.cockpit.common.camera.listener.OnPreviewChangeListener;
import com.voyah.cockpit.common.camera.listener.OnTakePhotoListener;
import com.voyah.cockpit.common.util.Constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public class MyCameraManager implements MyCameraInterface {
    private static final String TAG = MyCameraManager.class.getSimpleName();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final String PKG_DVR_LOC = "com.mega.dvr";

    private static final String REAL_TIME = "real_time_image";//实时画面
    private static final String VIDEO = "video";//行车记录仪录像页面(实时画面)
    private static final String DRIVING_VIDEO = "driving_video";//行车录像
    private static final String EMERGENCY_VIDEO = "emergency_video";//紧急录像
    private static final String SENTINEL_VIDEO = "sentinel_video";//哨兵录像
    private static final String COLLECT_LIST = "collection";//收藏列表
    private static final String SETTING = "setting";//设置
    private static final String VIDEO_BROWSING = "video_browsing";//录像浏览

    public OnTakePhotoListener onTakePhotoListener = null;

    public OnPreviewChangeListener onPreviewChangeListener = null;

    private static MyCameraManager manager;

    public MyCameraManager() {
    }

    public static MyCameraManager getInstance() {
        if (null == manager) {
            synchronized (MyCameraManager.class) {
                if (null == manager) manager = new MyCameraManager();
            }
        }
        return manager;
    }

    public void init() {
        connectDvr();
        connectCamera();
    }

    public void switchCamera(int cameraType) {
        CameraManager.getInstance().switchCamera(cameraType, onPreviewChangeListener);
    }

    public void takePicture() {
        CameraManager.getInstance().takePicture(onTakePhotoListener);
    }

    @Override
    public boolean isOnOffTakeVideo() {
        return false;
    }

    @Override
    public boolean isSupportPageSwitch(String uiName) {
        return true;
    }

    @Override
    public boolean isAssignedPageOpen(String uiName) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        // 1:录像浏览界面 2:录像预览界面(实时) 3:设置界面
        int dvrView = DeviceHolder.INS().getDevices().getCamera().getDvrView(0);
        //0:普通录像列表 1:紧急录像列表 2:哨兵录像列表 3:收藏录像列表
        int dvrTabulation = DeviceHolder.INS().getDevices().getCamera().getDvrTabulation(0);
        LogUtils.i(TAG, "isAssignedPageOpen uiName is " + uiName + " ,dvrView is " + dvrView + " ,dvrTabulation is " + dvrTabulation);
        boolean isAssignedPageOpen = false;
        switch (uiName) {
            case VIDEO_BROWSING: //录像浏览
                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE;
                break;
            case REAL_TIME: //实时
            case VIDEO:
                isAssignedPageOpen = dvrView == DvrConstants.VIDEO_PREVIEW;
                break;
            case SETTING: //设置
                isAssignedPageOpen = dvrView == DvrConstants.VIDEO_SETTING;
                break;
            case DRIVING_VIDEO: //行车录像
                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE && dvrTabulation == DvrConstants.SELECT_DVR_DRIVE;
                break;
            case EMERGENCY_VIDEO: //紧急录像
                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE && dvrTabulation == DvrConstants.SELECT_DVR_CRASH;
                break;
            case SENTINEL_VIDEO: //哨兵录像
                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE && dvrTabulation == DvrConstants.SELECT_DVR_GUARD;
                break;
            case COLLECT_LIST: //收藏列表
                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE && dvrTabulation == DvrConstants.SELECT_DVR_COLLECT;
                break;
        }
        LogUtils.i(TAG, "isAssignedPageOpen is " + isAssignedPageOpen);
        return isAssignedPageOpen;
    }

    @Override
    public void openAssignedPage(String uiName) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        com.voice.sdk.util.LogUtils.i(TAG, "openAssignedPage uiName is " + uiName);
        switch (uiName) {
            case REAL_TIME: //实时
            case VIDEO:
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_PREVIEW);
                break;
            case DRIVING_VIDEO: //行车录像
                DeviceHolder.INS().getDevices().getCamera().openNormalDvr(0);
                break;
            case EMERGENCY_VIDEO: //紧急录像
                DeviceHolder.INS().getDevices().getCamera().openCrashDvr(0);
                break;
            case SENTINEL_VIDEO: //哨兵录像
                DeviceHolder.INS().getDevices().getCamera().openGuardDvr(0);
                break;
            case COLLECT_LIST: //收藏列表
                DeviceHolder.INS().getDevices().getCamera().openCollectDvr(0);
                break;
            case SETTING: //设置
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_SETTING);
                break;
            case VIDEO_BROWSING: //录像浏览
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIEW_BROWSE);
                break;
        }
    }

    public int getDvrView(int displayId) {
        return DvrServiceProxy.get().getDvrView(displayId);
    }

    public int getDvrTabulation(int displayId) {
        return DvrServiceProxy.get().getDvrTabulation(displayId);
    }

    public void openDvrView(int display, int type) {
        DvrServiceProxy.get().openDvrView(display, type);
    }

    public void openNormalDvr(int displayId) {
        DvrServiceProxy.get().openNormalDvr(displayId);
    }

    public void openCrashDvr(int displayId) {
        DvrServiceProxy.get().openCrashDvr(displayId);
    }

    public void openGuardDvr(int displayId) {
        DvrServiceProxy.get().openGuardDvr(displayId);
    }

    public void openCollectDvr(int displayId) {
        DvrServiceProxy.get().openCollectDvr(displayId);
    }

    public int getCameraStatus() {
        return DvrServiceProxy.get().getCameraStatus();
    }

    @Override
    public void startDashCam() {
        DvrServiceProxy.get().startRecord();
    }

    @Override
    public void cameraStartTakeVideo() {
        CameraManager.getInstance().startRecord();
    }

    public void startRecord() {
        DvrServiceProxy.get().startRecord();
    }

    public void stopDashCam() {
        DvrServiceProxy.get().stopRecording();
    }

    public boolean isAppForeground() {
        return CameraManager.getInstance().isAppForeground();
    }

    @Override
    public boolean isDashCamOpen() {
        return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(PKG_DVR_LOC, DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL));
    }

    public boolean isInRecording() {
        return CameraManager.getInstance().isInRecording();
    }

    public void setCaptureType(int captureType) {
        CameraManager.getInstance().setCaptureType(captureType);
    }

    public void openCamera(int cameraType, int source) {
        CameraManager.getInstance().openCamera(cameraType, source);
    }

    public int getCameraType() {
        return CameraManager.getInstance().getCameraType();
    }

    public int getCaptureType() {
        return CameraManager.getInstance().getCaptureType();
    }

    public int getRecordStatus() {
        return CameraManager.getInstance().getRecordStatus();
    }

    public boolean isFloatCameraStart() {
        return CameraManager.getInstance().isFloatCameraStart();
    }

    public void pauseRecord() {
        CameraManager.getInstance().pauseRecord();
    }

    public void resumeRecord() {
        CameraManager.getInstance().resumeRecord();
    }

    public void stopRecord() {
        CameraManager.getInstance().stopRecord();
    }

    public void startFloatCamera() {
        CameraManager.getInstance().startFloatCamera();
    }

    public void stopFloatCamera() {
        CameraManager.getInstance().stopFloatCamera();
    }

    @Override
    public void startMirrorCamera() {
        //1.5电子后视镜需求
    }

    @Override
    public void closeBackMirror() {
        //1.5电子后视镜需求
    }

    @Override
    public boolean isInTakePhoneCountDown() {
        return false;
    }

    @Override
    public boolean isStorageFull() {
        return false;
    }

    @Override
    public boolean isBackMirrorOpen() {
        return false;
    }

    @Override
    public int getBackMirrorType() {
        return 1;
    }

    @Override
    public void switchBackMirrorType(int type) {

    }

    @Override
    public boolean isBackMirrorInBigWindow() {
        return false;
    }

    @Override
    public void zoomBackMirror(boolean zoomType) {

    }

    @Override
    public void switchBackMirror(int switchType) {

    }

    private void connectCamera() {
        //初始化相机服务
        com.voyah.cockpit.common.camera.CameraManager.getInstance().init(Utils.getApp(), new IConnectCallback() {
            @Override
            public void onConnectState(ComponentName componentName, boolean b) {
                //todo:绑定失败添加重试处理-子线程
                LogUtils.d(TAG, "onConnectState componentName is " + componentName + " ,b is " + b);
                if (!b) {
//                    cameraManager = null;
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            connectCamera();
                        }
                    });
                } else {
//                    cameraManager = CameraManager.getInstance();
                }
            }
        });

        if (null == onTakePhotoListener) {
            onTakePhotoListener = new OnTakePhotoListener.Stub() {
                @Override
                public void onTakePhotoStart() throws RemoteException {
                    LogUtils.d(TAG, "onTakePhotoStart");
                }

                @Override
                public void onTakePhotoSuccess() throws RemoteException {
                    LogUtils.d(TAG, "onTakePhotoSuccess");
                }

                @Override
                public void onTakePhotoError(String s) throws RemoteException {
                    LogUtils.d(TAG, "onTakePhotoError error msg is " + s);
                }
            };
        }

        if (null == onPreviewChangeListener) {
            onPreviewChangeListener = new OnPreviewChangeListener.Stub() {
                @Override
                public void onPreviewChangeStart() throws RemoteException {
                    LogUtils.d(TAG, "onPreviewChangeStart");
                }

                @Override
                public void onPreviewChangeSuccess() throws RemoteException {
                    LogUtils.d(TAG, "onPreviewChangeSuccess");
                }

                @Override
                public void onPreviewChangeError(String s) throws RemoteException {
                    LogUtils.d(TAG, "onPreviewChangeError s:" + s);
                }
            };
        }

    }

    private void connectDvr() {
        //初始化行车记录仪服务
        DvrServiceProxy.get().init(Utils.getApp(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.i(TAG, "onServiceConnected name is " + name);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtils.i(TAG, "onServiceDisconnected name is " + name);
            }
        });

        //行车记录仪录像状态监听
        DvrServiceProxy.get().registerCameraStatusListener(new IDvrCameraStatusListener.Stub() {
            @Override
            public void onDvrCameraStatusChange(int status, String msg, long recordingStartTime) throws RemoteException {
                LogUtils.i(TAG, "onDvrCameraStatusChange status is " + status + " ,msg is " + msg + " ,recordingStartTime is " + recordingStartTime);
            }

            @Override
            public void onRecordProgress(long progress) throws RemoteException {
                LogUtils.i(TAG, "onRecordProgress progress is " + progress);
            }

            @Override
            public void onRecordFinish(int status, String path, String fileName) throws RemoteException {

            }
        });
    }

}
