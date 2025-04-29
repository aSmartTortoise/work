package com.voyah.ai.basecar.system;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.mega.nexus.content.MegaContext;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.AppInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MultiAppHelper;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonAppInterfaceImpl implements AppInterface {
    private static final String TAG = "CommonAppInterfaceImpl";
    private static final String TTS_SERVICE_PACKAGE = "com.voyah.vcos.ttsservices";
    private static final Object locker = new Object();

    private final HashMap<String, String> appNameMap = new HashMap<>();

    private final Set<String> installedPackages = new HashSet<>();

    private static final HashMap<String, String[]> appByName = new HashMap<>();

    private static final Set<String> removeAppSet = new HashSet<>();

    static {
        appByName.put("本地视频".toLowerCase(), new String[]{"usb视频"});
        appByName.put("儿童座椅".toLowerCase(), new String[]{"宝宝座椅", "安全座椅"});
        appByName.put("商城".toLowerCase(), new String[]{"应用商店", "应用市场", "应用商城", "岚图商城"});
        appByName.put("咪咕视频".toLowerCase(), new String[]{"咪咕"});
        appByName.put("哔哩哔哩".toLowerCase(), new String[]{"b站", "bilibili"});
        appByName.put("车鱼视听".toLowerCase(), new String[]{"车鱼", "抖音短视频"});
        appByName.put("雷石KTV".toLowerCase(), new String[]{"雷石"});
        appByName.put("随乐游tv版".toLowerCase(), new String[]{"随乐游"});

        removeAppSet.add("Launcher".toLowerCase());
        removeAppSet.add("文件".toLowerCase());
        removeAppSet.add("MegaEngineeringMode".toLowerCase());
        removeAppSet.add("MegaAudioTestTools".toLowerCase());
        removeAppSet.add("ota升级".toLowerCase());
        removeAppSet.add("mcu_updater".toLowerCase());
        removeAppSet.add("设置".toLowerCase());
        DrivingRestrict.INSTANCE.init();
    }


    public CommonAppInterfaceImpl() {
        refreshAppInfo();
    }

    @Override
    public String getPackageName(String appName) {
        synchronized (locker) {
            LogUtils.i(TAG, "getPackageName:" + appName);
            if (TextUtils.isEmpty(appName)) {
                return "";
            }
            appName = appName.toLowerCase();
            if (appNameMap.containsKey(appName)) {
                return appNameMap.get(appName);
            }
            return "";
        }
    }

    @Override
    public boolean isInstalledByAppName(String appName) {
        synchronized (locker) {
            appName = appName.toLowerCase();
            boolean isInstalled = appNameMap.containsKey(appName);
            LogUtils.i(TAG, "isInstalledByAppName:" + appName + ",isInstalled:" + isInstalled);
            return isInstalled;
        }
    }

    @Override
    public boolean isInstalledByPackageName(String packageName) {
        synchronized (locker) {
            boolean isInstalled = installedPackages.contains(packageName);
            LogUtils.i(TAG, "isInstalledByPackageName:" + packageName + ",isInstalled:" + isInstalled);
            return isInstalled;
        }
    }


    @Override
    public String getAppVersionName(String packageName) {
        String versionName = "";
        try {
            PackageManager packageManager = ContextUtils.getAppContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "getAppVersionName:" + packageName + "," + versionName);
        return versionName;
    }

    @Override
    public long getAppVersionCode(String packageName) {
        long versionCode = 0;
        try {
            PackageManager packageManager = ContextUtils.getAppContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.getLongVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "getAppVersionCode:" + packageName + "," + versionCode);
        return versionCode;
    }

    @Override
    public String getAppVersionName() {
        LogUtils.i(TAG, "getAppVersionName");
        return getAppVersionName(ContextUtils.getAppContext().getPackageName());
    }

    @Override
    public long getAppVersionCode() {
        LogUtils.i(TAG, "getAppVersionCode");
        return getAppVersionCode(ContextUtils.getAppContext().getPackageName());
    }

    @Override
    public boolean isSystemApp(String packageName) {
        boolean isSystemApp = false;
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager pm = ContextUtils.getAppContext().getPackageManager();
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i(TAG, "isSystemApp:" + packageName + "," + isSystemApp);
        return isSystemApp;
    }

    @Override
    public boolean openApp(String packageName, DeviceScreenType deviceScreenType) {
        LogUtils.i(TAG, "openApp:" + packageName + ",screenType:" + deviceScreenType);
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        if (!MultiAppHelper.INSTANCE.canOpenOnTargetScreen(packageName, deviceScreenType.name())) {
            deviceScreenType = DeviceScreenType.fromValue(MultiAppHelper.INSTANCE.fetchScreen(packageName));
        }

        PackageManager packageManager = ContextUtils.getAppContext().getPackageManager();
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                LogUtils.i(TAG, "not find launch intent");
                return false;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(deviceScreenType, "");
            if (deviceScreenType == DeviceScreenType.CENTRAL_SCREEN) {
                LogUtils.d(TAG, "openApp[central]");
                if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isNeedSplitScreen()) {
                    DeviceHolder.INS().getDevices().getSystem().getSplitScreen().enterSplitScreen(packageName, intent.getComponent() != null ? intent.getComponent().getClassName() : null);
                } else {
                    Bundle bundle =
                            ActivityOptions.makeBasic().setLaunchDisplayId(
                                            DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(deviceScreenType)
                                    )
                                    .toBundle();
                    Utils.getApp().startActivity(intent, bundle);
                }
            } else {
                Bundle bundle =
                        ActivityOptions.makeBasic().setLaunchDisplayId(
                                        DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(deviceScreenType)
                                )
                                .toBundle();
                if (isSupportMulti(packageName)) {
                    LogUtils.d(TAG, "realOpenApp[supportMulti: true]");
                    MegaContext.startActivityAsUser(
                            Utils.getApp(),
                            intent,
                            bundle,

                            DeviceHolder.INS().getDevices().getSystem().getScreen().getUserHandle(
                                    deviceScreenType
                            )
                    );
                } else {
                    LogUtils.d(TAG, "realOpenApp[supportMulti: false]");
                    Utils.getApp().startActivity(intent, bundle);
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "openMultiApp error:" + e);
        }


        return true;
    }

    @Override
    public void closeApp(String packageName, DeviceScreenType deviceScreenType) {
        if (isAppForeGround(packageName, deviceScreenType)) {
            if (deviceScreenType == DeviceScreenType.CENTRAL_SCREEN) {
                if (DeviceHolder.INS().getDevices().getCarServiceProp().isH56D()) {
                    //TODO 适配56D行车桌面
                    DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
                } else {
                    if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
                        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().sendDismissSplitBroadcast(false);
                    } else {
                        DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
                    }
                }
            } else {
                DeviceHolder.INS().getDevices().getLauncher().backToHome(deviceScreenType);
            }
        }
    }


    @Override
    public void refreshAppInfo() {
        synchronized (locker) {
            appNameMap.clear();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = ContextUtils.getAppContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
            for (ResolveInfo info : resolveInfos) {
                String appName = info.activityInfo.loadLabel(ContextUtils.getAppContext().getPackageManager()).toString().toLowerCase();
                String packageName = info.activityInfo.packageName;
                installedPackages.add(packageName);
                if (!removeAppSet.contains(appName) && !appNameMap.containsKey(appName)) {
                    if (appByName.containsKey(appName)) {
                        String[] byNameArr = appByName.get(appName);
                        if (byNameArr != null) {
                            for (String byName : byNameArr) {
                                appNameMap.put(byName.toLowerCase(), packageName);
                                LogUtils.i(TAG, "应用名称:" + byName.toLowerCase() + ",包名:" + packageName);
                            }
                        }
                    }
                    LogUtils.i(TAG, "应用名称:" + appName + ",包名:" + packageName);
                    appNameMap.put(appName, packageName);
                }
            }
        }
    }

    @Override
    public boolean isAppForeGround(String pkgName, DeviceScreenType deviceScreenType) {
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        int displayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(deviceScreenType);
        return isAppForeGround(pkgName, displayId);
    }

    @Override
    public boolean isAppForeGround(String pkgName, int displayId) {
        String curPkgName = CommonSystemUtils.getTopPackageName(displayId);
        LogUtils.i(TAG, "isAppForeGround:" + displayId + ",curPkgName:" + curPkgName + ",pkgName:" + pkgName);
        return StringUtils.equalsIgnoreCase(curPkgName, pkgName);
    }

    @Override
    public boolean isSupportMulti(String pkgName) {
        boolean isSupport = MultiAppHelper.INSTANCE.supportMulti(pkgName);
        LogUtils.i(TAG, "isSupportMulti:" + pkgName + ",isSupport:" + isSupport);
        return isSupport;
    }

    @Override
    public void switchPage(String screenName, boolean switchTag, String tag, String secondTag, Object saveTag, Object map) {
        SwitchUtils.switchPage(screenName, switchTag, tag, secondTag, saveTag, map);
    }

    @Override
    public void startTtsService() {
        Intent launchIntent = Utils.getApp().getPackageManager().getLaunchIntentForPackage(TTS_SERVICE_PACKAGE);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
            LogUtils.i(TAG, "startTtsService");
        } else {
            // 应用程序未安装或无法启动
        }
    }

    @Override
    public String fetchScreen(String pkgName) {
        return MultiAppHelper.INSTANCE.fetchScreen(pkgName);
    }

    @Override
    public boolean isPreemptiveApp(String pkgName) {
        return MultiAppHelper.INSTANCE.isPreemptiveApp(pkgName);
    }

    @Override
    public boolean isAppSupportScreen(String pkgName, DeviceScreenType deviceScreenType) {
        return MultiAppHelper.INSTANCE.canOpenOnTargetScreen(pkgName, deviceScreenType.name());
    }

    @Override
    public boolean isDrivingRestrict(String pkgName) {
        return DrivingRestrict.INSTANCE.isDrivingRestrictApp(pkgName);
    }
}
