package com.voyah.vcos.ttsservices.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by lcy on 2023/12/18.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    private static Context mContext;
    private static String sharedUserId;

    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return versionName;
    }

    public static long getAppVersionCode(Context context) {
        long versionCode = 0L;

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return versionCode;
    }
}
