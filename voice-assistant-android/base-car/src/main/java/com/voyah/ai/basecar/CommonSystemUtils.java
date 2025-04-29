package com.voyah.ai.basecar;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.nexus.provider.MegaSettings;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.viewcmd.accessibility.VoiceAccessibilityService;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.bean.DhDirection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CommonSystemUtils {
    private static final String TAG = "CommonSystemUtils";
    private static final String SPLIT_SCREEN_STATUS = "split_screen_status";

    private CommonSystemUtils() {

    }

    public static void backToHome() {
        LogUtils.i(TAG, "backToHome");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextUtils.getAppContext().startActivity(intent);
    }

    public static String getTimeType() {
        String timeType = Settings.System.getString(
                Utils.getApp().getContentResolver(),
                Settings.System.TIME_12_24);
        LogUtils.i(TAG, "timeType :" + timeType);
        return timeType;
    }

    public static void setTimeType(String timeType) {
        LogUtils.i(TAG, "setTimeType :" + timeType);
        Settings.System.putString(
                Utils.getApp().getContentResolver(),
                Settings.System.TIME_12_24,
                timeType);
        Utils.getApp().getContentResolver().notifyChange(
                Settings.System.getUriFor(Settings.System.TIME_12_24),
                null);
    }

    /**
     * 设置语言名称(中文，英文)
     */
    public static Map<String, Locale> getLanguageType() {
        return MapUtils.newLinkedHashMap(
                new Pair<>("中文", Locale.CHINESE),
                new Pair<>("英文", Locale.ENGLISH)
        );
    }

    public static void setLanguage(Locale locale) {
        try {
            Object objIActMag;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            //amn = ActivityManagerNative.getDefault();
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            // objIActMag = amn.getConfiguration();
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            // set the locale to the new value
            config.locale = locale;
            //持久化  config.userSetLocale = true;
            Class clzConfig = Class
                    .forName("android.content.res.Configuration");
            Field userSetLocale = clzConfig
                    .getField("userSetLocale");
            userSetLocale.set(config, true);
            //如果有阿拉伯语，必须加上，否则阿拉伯语与其它语言切换时，布局与文字方向不会改变
            Method setLayoutDirection = clzConfig.getDeclaredMethod("setLayoutDirection", Locale.class);
            setLayoutDirection.invoke(config, locale);
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            Class[] clzParams = {Configuration.class};
            // objIActMag.updateConfiguration(config);
            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIActMag, config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSplitScreenState(int displayId) {
        if (displayId == 0) {
            int splitScreenState = Settings.System.getInt(Utils.getApp().getContentResolver(),
                    SPLIT_SCREEN_STATUS, 0);
            return splitScreenState == 1;
        } else {
            return false;
        }
    }

    public static String getAppVersion() {
        try {
            PackageManager packageManager = Utils.getApp().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(Utils.getApp().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取栈顶包名
     */
    public static String getTopPackageName(int displayId) {
        ComponentName topComponentName = getTopComponentName(displayId);
        if (topComponentName != null) {
            return topComponentName.getPackageName();
        } else {
            return null;
        }
    }

    /**
     * 获取栈顶ComponentName
     */
    public static ComponentName getTopComponentName(int displayId) {
        ActivityManager activityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                int tmpDisplayId = ReflectUtils.reflect(taskInfo).field("displayId").get();

                if (tmpDisplayId == displayId) {
                    if (!isSplitScreenState(displayId)) {
                        if (taskInfo.topActivity != null) {
                            return taskInfo.topActivity;
                        }
                    } else {
                        Configuration configuration = ReflectUtils.reflect(taskInfo).method("getConfiguration").get();
                        Object windowConfiguration = ReflectUtils.reflect(configuration).field("windowConfiguration").get();
                        LogUtils.d(TAG, "reflect windowConfiguration:" + windowConfiguration);

                        if (windowConfiguration != null) {
                            Rect bounds = ReflectUtils.reflect(windowConfiguration).field("mBounds").get();
                            if (bounds.left > 850 && taskInfo.topActivity != null) {
                                return taskInfo.topActivity;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getTopActivityName(int displayId) {
        ComponentName topComponentName = getTopComponentName(displayId);
        if (topComponentName != null) {
            return topComponentName.getClassName();
        } else {
            return null;
        }
    }


    /**
     * 根据包名强行停止一个应用，需要系统签名
     *
     * @param pkgName 包名
     */
    public static void forceStopPackage(Context context, String pkgName) {
        try {
            if (TextUtils.equals(context.getPackageName(), pkgName)) {
                Runtime.getRuntime().exec("am kill " + android.os.Process.myPid());
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } else {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
                method.invoke(am, pkgName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "forceStopPackage Exception: " + e.getMessage());
        }
    }


    public static void startAccessibilityService() {
        Context context = Utils.getApp();
        ComponentName accessibilityComponent = new ComponentName(context.getPackageName(),
                VoiceAccessibilityService.class.getName());

        MegaSettings.putStringForUserSecure(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                accessibilityComponent.flattenToString(), 0);
        MegaSettings.putIntForUserSecure(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 1, 0);
    }

    public static void sendKeyCodeBack() {
        com.blankj.utilcode.util.LogUtils.d("sendKeyCodeBack");
        ThreadUtils.getCpuPool().submit(() -> {
            try {
                @SuppressLint("BlockedPrivateApi")
                Field declaredField = KeyEvent.class.getDeclaredField("mDisplayId");
                declaredField.setAccessible(true);
                Instrumentation temp = new Instrumentation();
                KeyEvent down = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
                KeyEvent up = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
                int displayId = MegaDisplayHelper.getVoiceDisplayId();
                declaredField.setInt(down, displayId);
                declaredField.setInt(up, displayId);
                temp.sendKeySync(down);
                temp.sendKeySync(up);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static int getNearByTtsLocation(@DhDirection int direction) {
        int location;
        if (direction == DhDirection.FRONT_LEFT) {
            location = 0x1;
        } else if (direction == DhDirection.FRONT_RIGHT) {
            location = 0x2;
        } else if (direction == DhDirection.REAR_LEFT) {
            location = 0x3;
        } else if (direction == DhDirection.REAR_RIGHT) {
            location = 0x4;
        } else {
            location = 0;
        }
        return location;
    }
}
