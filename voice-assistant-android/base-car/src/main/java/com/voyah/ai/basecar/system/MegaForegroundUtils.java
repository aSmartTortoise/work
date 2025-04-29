package com.voyah.ai.basecar.system;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.SplitScreenInterface;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MegaForegroundUtils {
    private static final String TAG = MegaForegroundUtils.class.getSimpleName();

    //保存应用包名到 主界面activity名的映射，用来处理分屏
    private static final HashMap<String, String> pkgName2MainActivityName = new HashMap<>();

    static {
        pkgName2MainActivityName.put("com.voyah.cockpit.adas", "com.voyah.cockpit.adas.MainActivity");
    }

    public static boolean isForegroundApp(String packageName) {
        String pack = CommonSystemUtils.getTopPackageName(0);
        LogUtils.i(TAG, "isForegroundApp pack0 is " + pack);
        return (StringUtils.equals(pack, packageName));
    }

    public static boolean isForegroundApp(Context context, String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return false;
        }
        String topPackageName = getTopPkg(context);
        boolean isTop = packageName.equalsIgnoreCase(topPackageName);
        LogUtils.i(TAG, "isForegroundApp topPackageName is " + topPackageName + " , isTop is " + isTop);
        return isTop;
    }

    /**
     * 举例：如果allapp在前台的话，会出现栈顶还是本地视频，这里加入allApp的判断，如果栈顶还是本地视频，并且allApp不在前台，证明本地视频在前台
     *
     * @param context
     * @param packageName
     * @return
     */
//    public static boolean isFrontApp(Context context, String packageName) {
//        return isFrontApp(context, packageName, true);
//    }
//
//    public static boolean isFrontApp(Context context, String packageName, boolean isShowLog) {
//        if (StringUtils.isBlank(packageName)) {
//            return false;
//        }
//        String topPackageName = getTopPkg(context);
//        boolean isTop = packageName.equalsIgnoreCase(topPackageName);
//        boolean popWindowShowTop = LauncherViewUtils.isPopWindowShowTop(context, isShowLog);
//        if (isShowLog)
//            LogUtils.d(TAG, "isForegroundApp: " + isTop + ", popWindowShowTop = " + popWindowShowTop + " ,topPackageName is " + topPackageName);
//        return isTop && !popWindowShowTop;
//    }

    /**
     * 获取前台应用的包名
     */
    public static String getTopPkg(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获取正在运行的task列表，其中1表示最近运行的task，通过该数字限制列表中task数目，最近运行的靠前
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

            if (runningTaskInfos != null && runningTaskInfos.size() != 0) {
                return (runningTaskInfos.get(0).topActivity).getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static List<String> getRecentPackage(Context context, int size) {
        List<String> packageList = new ArrayList<>();
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskList = manager.getRunningTasks(size);
            if (runningTaskList != null && runningTaskList.size() > 0) {
                for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskList) {
                    if (runningTaskInfo.topActivity != null) {
                        packageList.add(runningTaskInfo.topActivity.getPackageName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageList;
    }

    public static boolean isDlnaMainActivity(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
            if (runningTaskInfos != null && runningTaskInfos.size() != 0) {
                String className = runningTaskInfos.get(0).baseActivity.getClassName();
                LogUtils.i(TAG, "className :" + className);
                return className.contains("MainActivity");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //打开指定应用
    public static void openApp(Context context, String pkgName) {
        LogUtils.d(TAG, "openApp pkgName:" + pkgName);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLaunchIntentForPackage(pkgName);
        } catch (Exception e) {
            LogUtils.e(TAG, "openApp error:" + e);
        }

        try {
            String activityName = pkgName2MainActivityName.getOrDefault(pkgName, "");
            SplitScreenInterface splitScreenInterface = DeviceHolder.INS().getDevices().getSystem().getSplitScreen();
            if (splitScreenInterface.isNeedSplitScreen()) {
                LogUtils.d(TAG, "命中分屏逻辑，使用分屏打开");
                splitScreenInterface.enterSplitScreen(pkgName,
                        activityName);
            } else {
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        }
    }

    //返回桌面
    public static void backToLauncher(Context context) {
        LogUtils.d(TAG, "backToLauncher");
        PackageManager packageManager = context.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLaunchIntentForPackage(ApplicationConstant.PKG_LAUNCHER);
        } catch (Exception e) {
            LogUtils.e(TAG, "backToLauncher error:" + e);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
