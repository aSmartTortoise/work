package com.voyah.ai.device.voyah.h37.launcher;

import static com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils.AIR_PAGE_TAG;

import android.content.Intent;
import android.os.RemoteException;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.launcher.LauncherInterface;
import com.voice.sdk.device.launcher.ResultCallBack;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.basecar.helper.MultiAppHelper;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils;
import com.voyah.ai.device.voyah.h37.utils.SystemUiUtils;
import com.voyah.cockpit.appadapter.aidlimpl.LauncherCeilingServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.LauncherCommonServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.LauncherSecondaryScreenSaverServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.LauncherSecondaryServiceImpl;
import com.voyah.cockpit.appadapter.aidlimpl.WallpaperServiceImpl;
import com.voyah.cockpit.launcher.IAllAppHandCallBack;
import com.voyah.cockpit.launcher.ceiling.ICeilingAllAppHandCallBack;
import com.voyah.cockpit.launcher.secondary.ISecondaryAllAppHandCallBack;
import com.voyah.cockpit.launcher.wallpaper.bean.Wallpaper;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public class LauncherManager implements LauncherInterface {
    private static final String TAG = LauncherManager.class.getSimpleName();

    private LauncherCommonServiceImpl launcherCommonService; //桌面 主驾
    private LauncherSecondaryServiceImpl launcherSecondaryService; //副驾
    private LauncherCeilingServiceImpl launcherCeilingService; //吸顶
    private WallpaperServiceImpl wallpaperService; //壁纸

    private LauncherSecondaryScreenSaverServiceImpl launcherSecondaryScreenSaverService; //副驾屏保

    private static LauncherManager launcherManager;

    public LauncherManager() {
        init();
    }

    public static LauncherManager getInstance() {
        if (null == launcherManager) {
            synchronized (LauncherManager.class) {
                if (null == launcherManager)
                    launcherManager = new LauncherManager();
            }
        }
        return launcherManager;
    }

    public void init() {
        wallpaperService = WallpaperServiceImpl.getInstance(Utils.getApp());
        wallpaperService.startService(() -> LogUtils.i(TAG, "wallpaperService onServiceConnected"));

        launcherCommonService = LauncherCommonServiceImpl.getInstance(Utils.getApp());
        launcherCommonService.startService(() -> LogUtils.i(TAG, "launcherCommonService onServiceConnected"));
        launcherSecondaryService = LauncherSecondaryServiceImpl.getInstance(Utils.getApp());
        launcherSecondaryService.startService(() -> LogUtils.i(TAG, "launcherSecondaryService onServiceConnected"));
        launcherCeilingService = LauncherCeilingServiceImpl.getInstance(Utils.getApp());
        launcherCeilingService.startService(() -> LogUtils.i(TAG, "launcherCeilingService onServiceConnected"));
        launcherSecondaryScreenSaverService = LauncherSecondaryScreenSaverServiceImpl.getInstance(Utils.getApp());
        SystemUiUtils.getInstance().initVoiceSdk();
    }


    private Wallpaper getCurrentWallpaper() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final com.voyah.cockpit.launcher.wallpaper.bean.Wallpaper[] wallpaper = {null};
        wallpaperService.getCurrentWallpaper(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<com.voyah.cockpit.launcher.wallpaper.bean.Wallpaper>() {
            @Override
            public void onResult(com.voyah.cockpit.launcher.wallpaper.bean.Wallpaper result) {
                wallpaper[0] = result;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
            LogUtils.i(TAG, "getCurrentWallpaper time out");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LogUtils.i(TAG, null == wallpaper[0] ? "getCurrentWallpaper wallpaper is null "
                : "getCurrentWallpaper index is" + wallpaper[0].getIndex() + ", " + wallpaper[0].getType());
        return wallpaper[0];
    }

    @Override
    public void backToHome(DeviceScreenType screenType) {
        if (screenType == null) {
            screenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        LogUtils.i(TAG, "backToHome:" + screenType);
        if (screenType == DeviceScreenType.CEIL_SCREEN) {
            Intent it = new Intent();
            it.setPackage(MultiAppHelper.LAUNCHER_PKG_CEILING);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.setClassName(MultiAppHelper.LAUNCHER_PKG_CEILING, "com.voyah.cockpit.launcher.ceiling.view.LauncherCeiling");
            Utils.getApp().startActivity(it);
        } else if (screenType == DeviceScreenType.PASSENGER_SCREEN) {
            Intent it = new Intent();
            it.setPackage(MultiAppHelper.LAUNCHER_PKG_2);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.setClassName(MultiAppHelper.LAUNCHER_PKG_2, "com.voyah.cockpit.launcher.secondary.view.LauncherSecondary");
            Utils.getApp().startActivity(it);
        } else {
            DeviceHolder.INS().getDevices().getSystem().getApp().openApp(MultiAppHelper.LAUNCHER_PKG, DeviceScreenType.CENTRAL_SCREEN);
        }
    }

    @Override
    public boolean isNeedBackWidget() {
        return false;
    }

    @Override
    public void backToWidgetHome() {

    }

    @Override
    public boolean isAirOpen(DeviceScreenType deviceScreenType) {
        return SystemUiUtils.getInstance().isAirPageOpen(deviceScreenType.name());
    }

    @Override
    public boolean isScreenPopWindowShowTop(boolean isShowLog, DeviceScreenType deviceScreenType) {
        return LauncherViewUtils.isScreenPopWindowShowTop(isShowLog, deviceScreenType.name());
    }

    @Override
    public void closeScreenCentral(DeviceScreenType deviceScreenType, Object saveTag) {
        LauncherViewUtils.closeScreenCentral(deviceScreenType.name(), saveTag);
    }

    @Override
    public void closeAssignScreenAllApps(DeviceScreenType deviceScreenType) {
        switch (deviceScreenType) {
            case CENTRAL_SCREEN:
                hideAllAppPanel();
                break;
            case PASSENGER_SCREEN:
                hideSecondAllAppPanel();
                break;
            case CEIL_SCREEN:
                hideCeilAllAppPanel();
                break;
            default:
                hideAllAppPanel();
        }
    }

    @Override
    public String getAssignScreen(HashMap<String, Object> map) {
        return LauncherViewUtils.getAssignScreen(map);
    }

    @Override
    public int getPrivacySecurity() {
        return Settings.System.getInt(Utils.getApp().getContentResolver(), "privacySecurity", 0);
    }

    @Override
    public boolean isSystemWallpaperFlag() {
        Wallpaper wallpaper = getCurrentWallpaper();
        if (null == wallpaper)
            return false;
        boolean isSystemWallpaper = wallpaper.isSystemWallpaper();
        LogUtils.i(TAG, "isSystemWallpaper is " + isSystemWallpaper);
        return isSystemWallpaper;
    }

    @Override
    public void setApplySystemWallpaper(String switchType) {
        Wallpaper wallpaper = getCurrentWallpaper();
        if (null == wallpaper)
            return;
        int index = wallpaper.getIndex();
        LogUtils.i(TAG, "setApplySystemWallpaper index is " + index);
        if (StringUtils.equals(switchType, "prev"))
            index = index == 0 ? 7 : index - 1;
        if (StringUtils.equals(switchType, "next"))
            index = index == 7 ? 0 : index + 1;

        wallpaperService.applySystemWallpaper(index, result -> LogUtils.i(TAG, "setApplySystemWallpaper result is " + result));
    }

    @Override
    public int getCurDesktopType() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int[] desktopType = new int[1];
        desktopType[0] = -1;
        launcherCommonService.getCurDesktopType(result -> {
            LogUtils.i(TAG, "getCurDesktopType result is " + result);
            desktopType[0] = result;
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LogUtils.i(TAG, "getCurDesktopType desktopType is " + desktopType[0]);
        return desktopType[0];
    }

    @Override
    public void setDesktopType(String switch_mode) {
        if (StringUtils.equals("2d", switch_mode))
            setDesktopType2D();
        if (StringUtils.equals("3d", switch_mode))
            setDesktopType3D();
    }

    @Override
    public void setDesktopType2D() {
        launcherCommonService.setDesktopType(1, result -> {
            LogUtils.i(TAG, "setDesktopType result is " + result);
        });
    }

    @Override
    public void setDesktopType3D() {
        launcherCommonService.setDesktopType(0, result -> {
            LogUtils.i(TAG, "setDesktopType result is " + result);
        });
    }

    @Override
    public boolean isAllAppPanelShowing(DeviceScreenType deviceScreenType) {
        switch (deviceScreenType) {
            case CENTRAL_SCREEN:
                return isAllAppPanelShowing();
            case PASSENGER_SCREEN:
                return isSecondAllAppPanelShowing();
            case CEIL_SCREEN:
                return isCeilAllAppPanelShowing();
            default:
                return isAllAppPanelShowing();
        }
    }

    @Override
    public boolean isAllAppPanelShowing() {
        final boolean[] isAllAppPanelShowing = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        launcherCommonService.isAllAppPanelShowing(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isAllAppPanelShowing[0] = result;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isAllAppPanelShowing[0];
    }

    @Override
    public boolean isSecondAllAppPanelShowing() {
        final boolean[] isSecondAllAppPanelShowing = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        launcherSecondaryService.isShowingAppListOfSecondary(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isSecondAllAppPanelShowing[0] = result;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isSecondAllAppPanelShowing[0];
    }

    @Override
    public boolean isCeilAllAppPanelShowing() {
        final boolean[] isCeilAllAppPanelShowing = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        launcherCeilingService.isShowingAppListOfSecondary(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isCeilAllAppPanelShowing[0] = result;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isCeilAllAppPanelShowing[0];
    }

    @Override
    public void showAllAppPanel(DeviceScreenType deviceScreenType) {
        switch (deviceScreenType) {
            case CENTRAL_SCREEN:
                showAllAppPanel();
                break;
            case PASSENGER_SCREEN:
                showSecondAllAppPanel();
                break;
            case CEIL_SCREEN:
                showCeilAllAppPanel();
                break;
            default:
                showAllAppPanel();
        }
    }

    @Override
    public void showAllAppPanel() {
        try {
            SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        launcherCommonService.showHideAllAppPanel(true, new IAllAppHandCallBack.Stub() {
            @Override
            public void callBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "state is " + state);
            }
        });
    }

    @Override
    public void showSecondAllAppPanel() {
        try {
            SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        launcherSecondaryService.showHideAllAppPanel(true, new ISecondaryAllAppHandCallBack.Stub() {
            @Override
            public void secondaryCallBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "state is " + state);
            }
        });
    }

    @Override
    public void showCeilAllAppPanel() {
        try {
            SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        launcherCeilingService.showHideAllAppPanel(true, new ICeilingAllAppHandCallBack.Stub() {
            @Override
            public void ceilingCallBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "launcherCeilingService state is " + state);
            }
        });
    }

    @Override
    public void hideAllAppPanel() {
        launcherCommonService.showHideAllAppPanel(false, new IAllAppHandCallBack.Stub() {
            @Override
            public void callBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "state is " + state);
            }
        });
    }

    @Override
    public void hideSecondAllAppPanel() {
        launcherSecondaryService.showHideAllAppPanel(false, new ISecondaryAllAppHandCallBack.Stub() {
            @Override
            public void secondaryCallBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "launcherSecondaryService state is " + state);
            }
        });
    }

    @Override
    public void hideCeilAllAppPanel() {
        launcherCeilingService.showHideAllAppPanel(false, new ICeilingAllAppHandCallBack.Stub() {
            @Override
            public void ceilingCallBackState(boolean state) throws RemoteException {
                LogUtils.i(TAG, "launcherCeilingService state is " + state);
            }
        });
    }

    @Override
    public void showHideScreenSaver(boolean isShow, ResultCallBack<Boolean> callBack) {
        launcherSecondaryScreenSaverService.showHideScreenSaver(isShow, new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                callBack.onResult(result);
            }
        });
    }

    @Override
    public void showHideScreenSaver(boolean isShow) {
        launcherSecondaryScreenSaverService.showHideScreenSaver(isShow, new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "showHideScreenSaver result:" + result);
            }
        });
    }

    @Override
    public void isShowingScreenSaverOfSecondary(ResultCallBack<Boolean> callBack) {
        launcherSecondaryScreenSaverService.isShowingScreenSaverOfSecondary(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                callBack.onResult(result);
            }
        });
    }

    @Override
    public boolean isShowingScreenSaverOfSecondary() {
        final boolean[] isShowingScreenSaverOfSecondary = {false};
        launcherSecondaryScreenSaverService.isShowingScreenSaverOfSecondary(new com.voyah.cockpit.appadapter.interfaces.ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "isShowingScreenSaverOfSecondary  result:" + result);
                isShowingScreenSaverOfSecondary[0] = result;
            }
        });
        return isShowingScreenSaverOfSecondary[0];
    }

    @Override
    public boolean isSupportScreenSaverChange() {
        return false;
    }

    @Override
    public void showHideCeilScreenSaver(boolean isShow, ResultCallBack<Boolean> callBack) {

    }

    @Override
    public void showHideCeilScreenSaver(boolean isShow) {

    }

    @Override
    public void isShowingScreenSaverOfCeil(ResultCallBack<Boolean> callBack) {

    }

    @Override
    public boolean isShowingScreenSaverOfCeil() {
        return false;
    }

    @Override
    public boolean isNegativeScreenOpen(DeviceScreenType deviceScreenType) {
        return SystemUiUtils.getInstance().isNegativeScreenOpen();
    }

    @Override
    public void openNegativeScreen(DeviceScreenType deviceScreenType) {
        SystemUiUtils.getInstance().getNegativeScreenState(true);
    }

    @Override
    public void closeNegativeScreen(DeviceScreenType deviceScreenType) {
        SystemUiUtils.getInstance().getNegativeScreenState(false);
    }

    @Override
    public boolean isParkOpen() {
        return LauncherViewUtils.isParkOpen();
    }

    @Override
    public boolean isGearsR() {
        return false;
    }

    @Override
    public boolean isGearsROrD() {
        return false;
    }

    @Override
    public boolean isSupportScreenSaver() {
        return false;
    }

    @Override
    public boolean isSupportSecondScreenSaver() {
        return false;
    }

    @Override
    public boolean isSupportCeilScreenSaver() {
        return false;
    }

    @Override
    public boolean isScreenViewMode(DeviceScreenType deviceScreenType) {
        return false;
    }

    @Override
    public boolean isSupportExecuteScreenSaver() {
        return false;
    }

    @Override
    public boolean isMessageCenterIndependent() {
        return false;
    }

    @Override
    public boolean isMessageCenterOpen() {
        return false;
    }

    @Override
    public void switchMessageCenter(boolean switchType) {

    }

    @Override
    public boolean isMessageCenterNotEmpty() {
        return false;
    }

    @Override
    public void clearMessageCenter() {

    }

    @Override
    public boolean isRcwOpen() {
        return false;
    }

    @Override
    public boolean isSupportCms() {
        return false;
    }

    @Override
    public boolean isSupportSplitFullScreen() {
        return true;
    }
}
