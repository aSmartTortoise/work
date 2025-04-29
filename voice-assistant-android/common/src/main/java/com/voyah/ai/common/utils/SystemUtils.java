package com.voyah.ai.common.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.Utils;

import java.util.List;

public class SystemUtils {

    public static int getDisplayIdByPkg(String pkg) {
        ActivityManager activityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(99);
            for (ActivityManager.RunningTaskInfo runningTaskInfo : taskInfoList) {
                if (runningTaskInfo.topActivity != null) {
                    if (runningTaskInfo.topActivity.getPackageName().equalsIgnoreCase(pkg)) {
                        return ReflectUtils.reflect(runningTaskInfo).field("displayId").get();
                    }
                }
            }
        }
        return -1;
    }
}
