package com.voyah.ai.basecar.system;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.SplitScreenInterface;
import com.voyah.ai.basecar.navi.NaviInterfaceImpl;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.AntiShake;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.LauncherCommonServiceImpl;
import com.voyah.cockpit.appadapter.interfaces.ResultCallBack;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class CommonSplitScreenImpl implements SplitScreenInterface {
    private static final String TAG = "SplitScreenInterfaceImpl";

    //free form 自由窗口方案
    private static final String FREE_FORM_INTENT_KEY = "free_form_action";
    private static final int FREE_FORM_ACTION_NONE = 0; //不做处理
    private static final int FREE_FORM_ACTION_ENTER = 1; //进入自由窗口
    private static final int FREE_FORM_ACTION_QUIT = 2; //退出自由窗口


    private static final String ACTION_SPLIT_SCREEN_ENTER = "com.android.systemui.split_screen_enter";
    private static final String ACTION_SPLIT_SCREEN_DISMISS = "com.android.systemui.split_screen_dismiss";

    public static final String KEY_APP_STATUS = "persist.sys.map.app_status";
    public static final String SPLIT_SCREEN_STATUS = "application.layering";

    public static final int VAL_APP_STATUS_NONAVI_BACKGROUND = 0;  // 非导航态&导航处于后台
    public static final int VAL_APP_STATUS_NONAVI_FOREGROUND = 1; // 非导航态&导航处于前台
    public static final int VAL_APP_STATUS_NAVI_FOREGROUND = 2; // 导航态&导航处于前台
    public static final int VAL_APP_STATUS_NAVI_BACKGROUND = 3; //导航态&导航处于后台

    private HandlerThread mHandlerThread;

    private boolean isNavi;

    private boolean isMapTop;

    private boolean isNeedSplitScreen;
    private final List<SplitStatusListener> splitStatusListeners = new CopyOnWriteArrayList<>();

    //车型区分是否使用自由窗口方案
    protected boolean usingFreeForm() {
        return false;
    }

    @NonNull
    @Override
    public String getRightSpiltScreenPackage() {
        LogUtils.i(TAG, "getRightSpiltScreenPackage");
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        LauncherCommonServiceImpl.getInstance(ContextUtils.getAppContext()).getSplitScreenPackageName(1, result -> {
            LogUtils.i(TAG, "onResult:" + result);
            completableFuture.complete(result);
        });
        try {
            String result = completableFuture.get(3000, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "getRightSpiltScreenPackage:" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    @Override
    public String getLeftSpiltScreenPackage() {
        LogUtils.i(TAG, "getLeftSpiltScreenPackage");
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        LauncherCommonServiceImpl.getInstance(ContextUtils.getAppContext()).getSplitScreenPackageName(0, result -> {
            LogUtils.i(TAG, "onResult:" + result);
            completableFuture.complete(result);
        });
        try {
            String result = completableFuture.get(3000, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "getLeftSpiltScreenPackage:" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean isDriveDesktop() {
        LogUtils.i(TAG, "isDriveDesktop");
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        LauncherCommonServiceImpl.getInstance(ContextUtils.getAppContext()).isDriveDesktop(new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.i(TAG, "onResult:" + result);
                completableFuture.complete(result);
            }
        });
        try {
            boolean result = completableFuture.get(3000, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "isDriveDesktop:" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public CommonSplitScreenImpl() {
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        getNaviStatus();
        getSplitScreenStatus();
        registerNaviAndSplitStatusChange();
        registerSplitScreenStatusObserver();
    }

    private void getSplitScreenStatus() {
//        int splitScreenStatus = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(), "split_screen_status", 0);
        //1:打开 2:关闭
        //0826 刷机第一次起来这个取不到值，必须切一次开关；因此同步设置的默认值（设置默认开）
        int splitScreenStatus = Settings.Global.getInt(Utils.getApp().getContentResolver(), SPLIT_SCREEN_STATUS, 1);
        LogUtils.d(TAG, "getSplitScreenStatus splitScreenStatus:" + splitScreenStatus);
        isNeedSplitScreen = splitScreenStatus == 1;
    }

    private void registerSplitScreenStatusObserver() {
        // 全屏和半屏状态切换
        ContentObserver mSpitScreenObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (!AntiShake.check(this, 50)) {
                    onSplitStatusChanged(isSplitScreening());
                }
            }
        };

        ContextUtils.getAppContext().getContentResolver().registerContentObserver(
                Settings.System.getUriFor("split_screen_status"),
                false, mSpitScreenObserver);
    }


    private void getNaviStatus() {
        int status = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(), KEY_APP_STATUS, 0);
        LogUtils.d(TAG, "map app status==>: " + status);
        if (status == VAL_APP_STATUS_NONAVI_BACKGROUND) {
            LogUtils.d(TAG, "map app status==>: 未导航&后台");
            isNavi = false;
            isMapTop = false;
        } else if (status == VAL_APP_STATUS_NONAVI_FOREGROUND) {
            LogUtils.d(TAG, "map app status==>: 未导航&前台");
            isNavi = false;
            isMapTop = true;
        } else if (status == VAL_APP_STATUS_NAVI_FOREGROUND) {
            LogUtils.d(TAG, "map app status==>: 导航&前台");
            isNavi = true;
            isMapTop = true;
        } else if (status == VAL_APP_STATUS_NAVI_BACKGROUND) {
            LogUtils.d(TAG, "map app status==>: 导航&后台");
            isNavi = true;
            isMapTop = false;
        }
    }


    private void registerNaviAndSplitStatusChange() {

        ContextUtils.getAppContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(KEY_APP_STATUS), false,
                new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        LogUtils.d(TAG, "registerNaviStatusChange");
                        getNaviStatus();
                    }
                });

        ContextUtils.getAppContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(SPLIT_SCREEN_STATUS), false,
                new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        LogUtils.d(TAG, "registerSplitStatusChange");
                        getSplitScreenStatus();
                    }

                    @Override
                    public boolean deliverSelfNotifications() {
                        return true;
                    }
                });


    }

    //是否需要分屏
    public boolean isNeedSplitScreen() {
        LogUtils.d(TAG, "isNeedSplitOpen isNavi:" + isNavi + " ,isNeedSplitScreen:" + isNeedSplitScreen);
        return isNavi && isNeedSplitScreen;
    }


    /**
     * (支持分屏应用调用)
     * 适用没有三方sdk接口的打开xxx场景
     * 指定位置分屏打开
     *
     * @param packageName（String类型） : 包名，必需传递
     * @param className             （String类型）：activity类名，可不传递，不传递时将拉起package对应的启动
     */
    public void enterSplitScreen(@NonNull String packageName, String className) {
        enterSplitScreen(packageName, className, null);
    }

    public void enterSplitScreen(
            @NonNull String packageName,
            String className,
            Bundle businessBundle) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        if (usingFreeForm()) {
            intent.setComponent(new ComponentName(packageName, className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FREE_FORM_INTENT_KEY, FREE_FORM_ACTION_ENTER); // 进入自由窗口
            if (businessBundle != null) {
                intent.putExtras(businessBundle); // 业务参数
            }
            startActivity(intent);
        } else {
            //广播分屏
            bundle.putString("packageName", packageName);
            if (!StringUtils.isBlank(className))
                bundle.putString("className", className);
            bundle.putInt("position", 1);
            if (businessBundle != null) {
                bundle.putAll(businessBundle);
            }
            intent.setAction(ACTION_SPLIT_SCREEN_ENTER);
            intent.putExtras(bundle);
            ContextUtils.getAppContext().sendBroadcast(intent);
        }

    }

    /**
     * 退出指定位置分屏
     * dissmissLeftft（布尔型）：true：左侧应用退出分屏(右侧全屏)；false：右侧应用退出分屏(左侧全屏)
     */
    public void sendDismissSplitBroadcast(boolean dissmissLeft) {
        Intent intent = new Intent();
        intent.putExtra("dissmissLeft", dissmissLeft);
        intent.setAction(ACTION_SPLIT_SCREEN_DISMISS);
        ContextUtils.getAppContext().sendBroadcast(intent);
    }


    //当前是否分屏 (导航在前台且栈顶不为导航)
    @Override
    public boolean isSplitScreening() {
        int splitScreenStatus = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(), "split_screen_status", 0);
        return splitScreenStatus == 1;
    }

    /**
     * 是否处于分屏处理中
     *
     * @return true/false
     */
    public boolean isDealSplitScreening() {
        int splitScreenStatus = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(), "split_screen_progress", 0);
        LogUtils.i(TAG, "isDealSplitScreening splitScreenStatus:" + splitScreenStatus);
        return splitScreenStatus == 1;
    }

    /**
     * 如果是语控分屏开关，会由语音修改application.layering的值，
     * 设置能接收到change的通知，但是语音自己是触发变动的进程，不会收到通知
     */
    public void updateSplitSwitch(boolean switchStatus) {
        isNeedSplitScreen = switchStatus;
    }


    @Override
    public void enterSpiltScreen() {
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            if (isSplitScreening()) {
                LogUtils.i(TAG, "already spilt screen");
            } else {
                LogUtils.i(TAG, "navi full start launcher");
                List<String> recentPkgList = MegaForegroundUtils.getRecentPackage(ContextUtils.getAppContext(), 20);
                LogUtils.i(TAG, "packageList:" + GsonUtils.toJson(recentPkgList));
                Intent appIntent = null;
                int index = recentPkgList.indexOf(ApplicationConstant.PACKAGE_NAME_MAP);
                for (int i = index + 1; i < recentPkgList.size(); i++) {
                    if (!(ApplicationConstant.PACKAGE_NAME_MAP.equals(recentPkgList.get(i)) ||
                            ApplicationConstant.PKG_LAUNCHER.equals(recentPkgList.get(i)) ||
                            ApplicationConstant.PKG_ARCREATOR.equals(recentPkgList.get(i)))) {
                        appIntent = ContextUtils.getAppContext().getPackageManager().getLaunchIntentForPackage(recentPkgList.get(i));
                        if (appIntent != null) {
                            break;
                        }
                    }

                }
                String packageName = (appIntent != null && !ApplicationConstant.PACKAGE_NAME_MAP.equals(appIntent.getPackage()))
                        ? appIntent.getPackage() : ApplicationConstant.PKG_LAUNCHER;
                Intent intent = new Intent();
                intent.setAction("com.android.systemui.split_screen_enter");
                intent.putExtra("packageName", packageName);
                intent.putExtra("position", 1);
                ContextUtils.getAppContext().sendBroadcast(intent);
            }

        } else {
            LogUtils.i(TAG, "start navi with spilt screen");
            Intent intent = new Intent();
            intent.setAction("com.android.systemui.split_screen_enter");
            intent.putExtra("packageName", ApplicationConstant.PACKAGE_NAME_MAP);
            intent.putExtra("position", 0);
            ContextUtils.getAppContext().sendBroadcast(intent);
        }
    }

    @Override
    public void enterFullScreen() {
        LogUtils.i(TAG, "enterFullScreen");
        if (isSplitScreening()) {
            Intent intent = new Intent();
            intent.setAction("com.android.systemui.split_screen_dismiss");
            intent.putExtra("dissmissLeft", false);
            ContextUtils.getAppContext().sendBroadcast(intent);
        }
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
            NaviInterfaceImpl.getInstance().openNaviApp();
        }
    }

    @Override
    public void addSplitStatusListener(@NonNull SplitStatusListener listener) {
        splitStatusListeners.add(listener);
    }

    @Override
    public void removeSplitStatusListener(@NonNull SplitStatusListener listener) {
        splitStatusListeners.remove(listener);
    }

    @Override
    public boolean isSupportDriveDesktop() {
        return false;
    }

    @Override
    public void switchSplitScreen() {
        LogUtils.i(TAG, "switchSplitScreen");
        LauncherCommonServiceImpl.getInstance(ContextUtils.getAppContext()).switchSplitScreen();
    }

    @Override
    public void onSplitStatusChanged(boolean isSplitScreen) {
        splitStatusListeners.forEach(listener -> listener.onChanged(isSplitScreen));
    }
}
