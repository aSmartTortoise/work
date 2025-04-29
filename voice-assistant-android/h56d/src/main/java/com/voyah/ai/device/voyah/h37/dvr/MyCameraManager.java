package com.voyah.ai.device.voyah.h37.dvr;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
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
import com.voyah.cockpit.common.camera.FloatCameraManager;
import com.voyah.cockpit.common.camera.callback.IConnectCallback;
import com.voyah.cockpit.common.camera.listener.OnFloatCameraStateChangeListener;
import com.voyah.cockpit.common.camera.listener.OnPreviewChangeListener;
import com.voyah.cockpit.common.camera.listener.OnTakePhotoListener;

import java.util.HashMap;
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

    public OnFloatCameraStateChangeListener onFloatCameraStateChangeListener = null;

    private boolean isFloatOpened; //悬浮窗是否已打开
    private boolean isFloatFull; //悬浮窗谁否为全屏

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
        return true;
    }

    @Override
    public boolean isSupportPageSwitch(String uiName) {
        return !StringUtils.equals(uiName, VIDEO_BROWSING) && !StringUtils.equals(uiName, VIDEO) && !StringUtils.equals(REAL_TIME, uiName);
    }

    @Override
    public boolean isAssignedPageOpen(String uiName) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
//        // 1:录像浏览界面 2:录像预览界面(实时)
//        int dvrView = DeviceHolder.INS().getDevices().getCamera().getDvrView(0);
        //0:普通录像列表 1:紧急录像列表 2:哨兵录像列表 3:收藏录像列表
        int dvrTabulation = DeviceHolder.INS().getDevices().getCamera().getDvrTabulation(0);
        LogUtils.i(TAG, "isAssignedPageOpen uiName is " + uiName + " ,dvrTabulation is " + dvrTabulation);
        boolean isAssignedPageOpen = false;
        switch (uiName) {
//            case VIDEO_BROWSING: //录像浏览
//                isAssignedPageOpen = dvrView == DvrConstants.VIEW_BROWSE;
//                break;
//            case REAL_TIME: //实时
//            case VIDEO:
//                isAssignedPageOpen = dvrView == DvrConstants.VIDEO_PREVIEW;
//                break;
            case DRIVING_VIDEO: //行车录像
                isAssignedPageOpen = dvrTabulation == 0;
                break;
            case EMERGENCY_VIDEO: //紧急录像
                isAssignedPageOpen = dvrTabulation == 1;
                break;
            case SENTINEL_VIDEO: //哨兵录像
                isAssignedPageOpen = dvrTabulation == 2;
                break;
            case COLLECT_LIST: //收藏列表
                isAssignedPageOpen = dvrTabulation == 3;
                break;
            case SETTING: //设置
                isAssignedPageOpen = dvrTabulation == 4;
                break;
        }
        LogUtils.i(TAG, "isAssignedPageOpen is " + isAssignedPageOpen);
        return isAssignedPageOpen;
    }

    @Override
    public void openAssignedPage(String uiName) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        LogUtils.i(TAG, "openAssignedPage uiName is " + uiName);
        switch (uiName) {
//            case REAL_TIME: //实时 H56不存在该页面
//            case VIDEO:
//                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIDEO_PREVIEW);
//                break;
            case DRIVING_VIDEO: //行车录像
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, 0);
                break;
            case EMERGENCY_VIDEO: //紧急录像
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, 1);
                break;
            case SENTINEL_VIDEO: //哨兵录像
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, 2);
                break;
            case COLLECT_LIST: //收藏列表
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, 3);
                break;
            case SETTING: //设置
                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, 4);
                break;
//            case VIDEO_BROWSING: //录像浏览 H56不存在该页面
//                DeviceHolder.INS().getDevices().getCamera().openDvrView(0, DvrConstants.VIEW_BROWSE);
//                break;
        }
    }

    public int getDvrView(int displayId) {
        return DvrServiceProxy.get().getDvrView(displayId);
    }

    public int getDvrTabulation(int displayId) {
        return DvrServiceProxy.get().getDvrTabulation(displayId);
    }

    public void openDvrView(int display, int type) {
        LogUtils.d(TAG, "openDvrView type:" + type);
        DvrServiceProxy.get().openDvrView(display, type);
    }

    public void openNormalDvr(int displayId) {
//        DvrServiceProxy.get().openNormalDvr(displayId);
    }

    public void openCrashDvr(int displayId) {
//        DvrServiceProxy.get().openCrashDvr(displayId);
    }

    public void openGuardDvr(int displayId) {
//        DvrServiceProxy.get().openGuardDvr(displayId);
    }

    public void openCollectDvr(int displayId) {
//        DvrServiceProxy.get().openCollectDvr(displayId);
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
        return isBackMirrorOpen();
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

    //打开后排宝宝守护(车内)
    public void startFloatCamera() {
//        startMirrorCamera();
        FloatCameraManager.getInstance().startFloatCamera(1, onPreviewChangeListener);
    }

    //打开电子后视镜(车外)
    public void startMirrorCamera() {
//        CameraManager.getInstance().startMirrorCamera(4);
        FloatCameraManager.getInstance().startFloatCamera(4, onPreviewChangeListener);
    }


    //关闭电子后视镜
    public void closeBackMirror() {
//        CameraManager.getInstance().closeBackMirror();
        FloatCameraManager.getInstance().stopFloatCamera();
    }

    public void stopFloatCamera() {
//        CameraManager.getInstance().stopFloatCamera();
        FloatCameraManager.getInstance().stopFloatCamera();
    }

    @Override
    public boolean isInTakePhoneCountDown() {
        return CameraManager.getInstance().isInCountDown();
    }

    @Override
    public boolean isStorageFull() {
        return CameraManager.getInstance().isStorageFull();
    }

    @Override
    public boolean isBackMirrorOpen() {
        //todo:FloatCameraManager中缺少 浮窗是否打开接口
        return isFloatOpened;
//        return CameraManager.getInstance().isBackMirrorOpen();
    }

    @Override
    public int getBackMirrorType() {
//        return CameraManager.getInstance().getBackMirrorType();
        return FloatCameraManager.getInstance().getFloatCameraType();
    }

    @Override
    public void switchBackMirrorType(int type) {
        //todo:摄像头切换过程中再次切换状态会不对
        //OMS(宝宝守护) 1 、CMS(电子后视镜) 4
//        CameraManager.getInstance().switchBackMirrorType(type);
        FloatCameraManager.getInstance().startFloatCamera(type, onPreviewChangeListener);
    }

    @Override
    public boolean isBackMirrorInBigWindow() {
        //todo:FloatCameraManager中缺少 浮窗是否全屏接口
//        return CameraManager.getInstance().isInBigWindow();
        return isFloatFull;
    }

    @Override
    public void zoomBackMirror(boolean zoomType) {
        //todo:切换大小窗目前不支持子线程操作，相机预计0515解决
        ThreadUtils.runOnUiThread(() -> FloatCameraManager.getInstance().setFullScreen(zoomType));
//        CameraManager.getInstance().zoomBackMirror(zoomType);
    }

    @Override
    public void switchBackMirror(int switchType) {
//        CameraManager.getInstance().startMirrorCamera(switchType);
        FloatCameraManager.getInstance().startFloatCamera(switchType, onPreviewChangeListener);
    }

    private void connectCamera() {
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

        if (null == onFloatCameraStateChangeListener) {
            onFloatCameraStateChangeListener = new OnFloatCameraStateChangeListener.Stub() {
                @Override
                public void onFloatCameraStateChange(int cameraType, int state) throws RemoteException {
                    //cameraType：1 宝宝守护 2 电子后视镜
                    //state：0 关闭  1 打开  2 全屏 3 小屏
                    LogUtils.d(TAG, "OnFloatCameraStateChangeListener cameraType:" + cameraType + " ,state:" + state);
                    isFloatOpened = state != 0;
                    isFloatFull = state == 2;
                }
            };
        }

        initCamera();
        initFloatCamera();

    }

    private void initFloatCamera() {
        try {
            FloatCameraManager.getInstance().init(Utils.getApp(), new IConnectCallback() {
                @Override
                public void onConnectState(ComponentName componentName, boolean b) {
                    {
                        //todo:绑定失败添加重试处理-子线程
                        LogUtils.d(TAG, "initFloatCamera onConnectState componentName is " + componentName + " ,b is " + b);
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
                                }
                            });
                        } else {
//                    cameraManager = CameraManager.getInstance();
                        }
                    }
                }
            });
            //注册悬浮窗监听
            FloatCameraManager.getInstance().addFloatCameraStateChangeListener("com.voyah.ai.voice", onFloatCameraStateChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
            initFloatCamera();
        }
    }

    private void initCamera() {
        try {
            //初始化相机服务
            com.voyah.cockpit.common.camera.CameraManager.getInstance().init(Utils.getApp(), new IConnectCallback() {
                @Override
                public void onConnectState(ComponentName componentName, boolean b) {
                    //todo:绑定失败添加重试处理-子线程
                    LogUtils.d(TAG, "initCamera onConnectState componentName is " + componentName + " ,b is " + b);
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
                            }
                        });
                    } else {
//                    cameraManager = CameraManager.getInstance();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            initCamera();
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
