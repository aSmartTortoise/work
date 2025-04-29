package com.voyah.ai.device.voyah.h37.dc.utils;

import com.voice.sdk.device.system.ShareInterface;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.HandlerUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.DlnaServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.DlnaServiceImplV2;
import com.voyah.cockpit.appadapter.interfaces.ServiceConnectionCallback;
import com.voyah.vshare.domain.VoyahShareBtConnectInfo;
import com.voyah.vshare.domain.VoyahShareConnectInfo;
import com.voyah.vshare.manager.IVoyahShareConnectManager;
import com.voyah.vshare.manager.impl.VoyahShareServiceConnectManager;

import java.util.HashMap;

public class ShareUtils implements ShareInterface {

    private static final String TAG = ShareUtils.class.getSimpleName();

    private static ShareUtils mShareUtils;

    protected IVoyahShareConnectManager mIVoyahShareConnectManager;
    private DlnaServiceImplV2 mDlnaServiceImpl;

    public static ShareUtils getInstance() {
        if (null == mShareUtils) {
            synchronized (ShareUtils.class) {
                if (null == mShareUtils) {
                    mShareUtils = new ShareUtils();
                }
            }
        }
        return mShareUtils;
    }

    public ShareUtils() {
        mIVoyahShareConnectManager = new VoyahShareServiceConnectManager.Builder(ContextUtils.getAppContext()).create();
        mIVoyahShareConnectManager.connect(voyahShareConnectListener);
        HandlerUtils.INSTANCE.runOnUIThread(() -> mDlnaServiceImpl = DlnaServiceImplV2.getInstance(ContextUtils.getAppContext()));
    }

    /**
     * 打开互联
     *
     * @return 打开互联结果
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_OK 打开完成}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_ALREADY_THERE 已经是打开状态}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_CONFIRM 请确认}
     *
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_NOT_CONNECT 服务未连接}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_REMOTE_EXCEPTION 进程通讯异常}
     */
    public int openShareApp() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.OPEN_TYPE_VOYAH_SHARE);
        LogUtils.i(TAG, "openShareApp code : " + code);
        return code;
    }


    /**
     * 打开文件传输
     *
     * @return 打开文件传输结果
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_OK 打开完成}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_ALREADY_THERE 已经是打开状态}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_CONFIRM 请确认}
     *
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_NOT_CONNECT 服务未连接}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_REMOTE_EXCEPTION 进程通讯异常}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_UNKNOWN 指令无法识别}
     */
    public int openFileTransfer() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.OPEN_TYPE_FILE_TRANS);
        LogUtils.i(TAG, "openFileTransfer code : " + code);
        return code;
    }

    public int openScreenMirroring() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.OPEN_TYPE_MIRROR);
        LogUtils.i(TAG, "openScreenMirroring code : " + code);
        return code;
    }

    public int closeScreenMirroring() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.CLOSE_TYPE_MIRROR);
        LogUtils.i(TAG, "closeScreenMirroring code : " + code);
        return code;
    }

    public int openScreenMirroringFullScreen() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.FULL_SCREEN_TYPE_MIRROR);
        LogUtils.i(TAG, "openScreenMirroringFullScreen code : " + code);
        return code;
    }

    public int openScreenMirroringSmallWindow() {
        int code = mIVoyahShareConnectManager.openVoyahShare(VoyahShareServiceConnectManager.SMALL_SCREEN_TYPE_MIRROR);
        LogUtils.i(TAG, "openScreenMirroringSmallWindow code : " + code);
        return code;
    }

    /**
     * 关闭互联
     *
     * @return
     *          {@link VoyahShareServiceConnectManager#HANDLE_CLOSE_OK 关闭完成}
     *          {@link VoyahShareServiceConnectManager#HANDLE_CLOSE_ALREADY_THERE 已经是关闭状态}
     *
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_NOT_CONNECT 服务未连接}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_REMOTE_EXCEPTION 进程通讯异常}
     */
    public int closeShareApp() {
        int code = mIVoyahShareConnectManager.closeVoyahShare();
        LogUtils.i(TAG, "closeShareApp code : " + code);
        return code;
    }

    /**
     * 互联应用是否在前台
     */
    public boolean isShareAppOpened() {
        int voyahShareState = getVoyahShareState(VoyahShareServiceConnectManager.STATE_TYPE_APP);
        return voyahShareState == VoyahShareServiceConnectManager.STATE_TYPE_APP_RESULT_IN;
    }

    /**
     * 文件传输是否已经打开
     */
    public boolean isFileTransferOpened() {
        int voyahShareState = getVoyahShareState(VoyahShareServiceConnectManager.STATE_TYPE_FILE_TRANS_PAGE);
        return voyahShareState == VoyahShareServiceConnectManager.STATE_TYPE_FILE_TRANS_PAGE_RESULT_IN;
    }

    /**
     * 镜像投屏是否已经打开
     */
    public boolean isMirrorOpened() {
        int voyahShareState = getVoyahShareState(VoyahShareServiceConnectManager.STATE_TYPE_MIRROR);
        return voyahShareState == VoyahShareServiceConnectManager.STATE_TYPE_MIRROR_RESULT_IN;
    }

    /**
     * 投屏是否已经连接
     */
    public int getConnectState() {
        int voyahShareState = getVoyahShareState(VoyahShareServiceConnectManager.STATE_TYPE_CONNECT);
        return voyahShareState;
    }

    /**
     * 弹窗的状态
     */
    public int getDialogState() {
        int voyahShareState = getVoyahShareState(VoyahShareServiceConnectManager.STATE_TYPE_DIALOG);
        return voyahShareState;
    }

    /**
     * 打开视频投屏
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_OK 打开完成}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_ALREADY_THERE 已经是打开状态}
     *          {@link VoyahShareServiceConnectManager#HANDLE_OPEN_CONFIRM 请确认}
     *          {@link VoyahShareServiceConnectManager#HANDLE_CLOSING DLNA正在关闭中，请稍后再试}
     *
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_NOT_CONNECT 服务未连接}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_SERVICE_REMOTE_EXCEPTION 进程通讯异常}
     *          {@link VoyahShareServiceConnectManager#ERROR_RESULT_UNKNOWN 指令无法识别}
     */
    public boolean openDlnaState(String targetScreen) {
        int dlnaType = 1;
        if (targetScreen.equalsIgnoreCase("central_screen")) {
            dlnaType = 1;
        } else if (targetScreen.equalsIgnoreCase("passenger_screen")) {
            dlnaType = 2;
        } else if (targetScreen.equalsIgnoreCase("ceil_screen")){
            dlnaType = 3;
        }
        mDlnaServiceImpl.openSelectActivity(dlnaType);
        return true;
    }

    /**
     * 获取VoyahShare状态
     * @param type 需要获取状态的类型
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_APP 应用是否在前台}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_FILE_TRANS_PAGE 文件传输是否打开}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_MIRROR 投屏状态}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_CONNECT 应用是否在前台}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG 应用弹窗状态}
     * @return 状态回执
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_APP_RESULT_IN 应用在前台}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_APP_RESULT_OUT 应用在后台}
     *
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_FILE_TRANS_PAGE_RESULT_IN 文件传输页面已打开}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_FILE_TRANS_PAGE_RESULT_OUT 文件传输页面未打开}
     *
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_MIRROR_RESULT_IN 已经投屏状态}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_MIRROR_RESULT_OUT 未投屏状态}
     *
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_CONNECT_RESULT_IN 已经连接}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_CONNECT_RESULT_ING 正在连接}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_CONNECT_RESULT_OUT 未连接}
     *
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_OUT 无弹窗}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_CONFIRM_DIS_FLOATING_MIRROR 确认断开悬浮镜像弹窗}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_CONFIRM_CHANGE_TO_FLOATING_MIRROR 确认切换到悬浮镜像弹窗}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_CONFIRM_CHANGE_TO_FILE_TRANS 确认切换到文件传输弹窗}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_CONFIRM_CHANGE_TO_DLNA 确认断开互联切换到视频投屏弹窗}
     *             {@link VoyahShareServiceConnectManager#STATE_TYPE_DIALOG_RESULT_CONFIRM_CHANGE_TO_DIS_CONNECT 确认断开互联弹窗}
     */
    public int getVoyahShareState(int type) {
        LogUtils.i(TAG, "getVoyahShareState");
        return mIVoyahShareConnectManager.getVoyahShareState(type);
    }

    /**
     * 获取视频投屏的状态
     */
    public boolean getDlnaState(String targetScreen, boolean onOff) {
        // -1:dlna 关闭 0:dlna开启，但是没有具体在哪个屏幕开启的信息 1:dlna在主屏开启 2:dlna在副屏开启 3:dlna在吸顶屏幕开启
        int dlnaState = mDlnaServiceImpl.getDlnaState();
        if (onOff) {
            if (targetScreen.equalsIgnoreCase("central_screen")) {
                return dlnaState == 1;
            } else if (targetScreen.equalsIgnoreCase("passenger_screen")) {
                return dlnaState == 2;
            } else if (targetScreen.equalsIgnoreCase("ceil_screen")) {
                return dlnaState == 3;
            } else {
                return false;
            }
        } else {
            return dlnaState > -1;
        }
    }

    /**
     * 打开视频投屏的开关和功能 -1:失败 0:成功
     */
    public int openDlna() {
        int dlnaState = mDlnaServiceImpl.openApplication();
        LogUtils.i(TAG, "openDlna dlnaState : " + dlnaState);
        return dlnaState;
    }

    /**
     * 关闭视频投屏的开关和功能 -1:失败 0:成功
     */
    public int closeDlna() {
        int dlnaState = mDlnaServiceImpl.closeApplication();
        LogUtils.i(TAG, "closeDlna dlnaState : " + dlnaState);
        return dlnaState;
    }

    /**
     * 播放投屏视频
     */
    public int startDlnaVideo() {
        int play = mDlnaServiceImpl.play();
        LogUtils.i(TAG, "startDlnaVideo play : " + play);
        return play;
    }

    /**
     * 暂停投屏视频
     */
    public int pauseDlnaVideo() {
        int pause = mDlnaServiceImpl.pause();
        LogUtils.i(TAG, "pauseDlnaVideo play : " + pause);
        return pause;
    }

    /**
     * 是否是投屏视频播放中 0：播放页面销毁，1播放， 2暂停
     */
    public int isDlnaVideoPlaying() {
        int playing = mDlnaServiceImpl.isPlaying();
        LogUtils.i(TAG, "isDlnaVideoPlaying playing : " + playing);
        return playing;
    }

    public IVoyahShareConnectManager.VoyahShareConnectListener voyahShareConnectListener = new IVoyahShareConnectManager.VoyahShareConnectListener() {
        @Override
        public void onVoyahShareServiceConnected() {
            LogUtils.i(TAG, "onVoyahShareServiceConnected");
        }

        @Override
        public void onVoyahShareConnectStateChanged(VoyahShareConnectInfo voyahShareConnectInfo) {
            LogUtils.i(TAG, "onVoyahShareConnectStateChanged");
        }

        @Override
        public void onVoyahShareBtConnectStateChanged(VoyahShareBtConnectInfo voyahShareBtConnectInfo) {
            LogUtils.i(TAG, "onVoyahShareBtConnectStateChanged");
        }
    };

    public ServiceConnectionCallback serviceConnectionCallback = new ServiceConnectionCallback() {
        @Override
        public void onServiceConnected() {
            LogUtils.i(TAG, "onServiceConnected");
        }
    };

    /**
     * 获取37A是否WIFI连接
     *
     * @return
     */
    public boolean get37AVsWifiConnect() {
        return getVsConnectState("wlan0", "wlan1", "p2p0");
    }

    /**
     * 获取56C是否WIFI连接
     *
     * @return
     */
    public boolean get56CVsWifiConnect() {
        return getVsConnectState("wlan0", "p2p0");
    }

    /**
     * 获取37A是否AP连接
     *
     * @return
     */
    public boolean get37AVsApConnect() {
        return getVsConnectState("wlan2", "ap_br_wlan2");
    }

    /**
     * 获取37A是否AP连接
     *
     * @return
     */
    public boolean get56CVsApConnect() {
        return getVsConnectState("wlan1");
    }

    /**
     * 判断互联是否通过网卡互联
     *
     * @param netItfcNames
     * @return
     */
    private boolean getVsConnectState(String... netItfcNames) {
        if (!mIVoyahShareConnectManager.isListening()) {
            LogUtils.e(TAG, "getVsConnectState not listening");
            return false;
        }
        VoyahShareConnectInfo connectState = mIVoyahShareConnectManager.getConnectState();
        if (!connectState.isConnected()) {
            return false;
        }
        String netModuleName = connectState.getNetModuleName();
        for (String netItfcName : netItfcNames)
            if (netModuleName.equals(netItfcName)) return true;
        return false;
    }

    public boolean isDlnaOpened() {
        return MegaForegroundUtils.isForegroundApp(ContextUtils.getAppContext(), "com.voyah.cockpit.dlnaserver");
    }

    public boolean isDlnaMainActivity() {
        return MegaForegroundUtils.isDlnaMainActivity(ContextUtils.getAppContext());
    }

    public boolean isNewPlan() {
        return true;
    }

}
