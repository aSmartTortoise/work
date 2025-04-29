package com.voyah.vcos.asraudiorecord.util;

import android.text.TextUtils;
import android.util.Log;

@SuppressWarnings("unused")
public class LogUtils {

    private LogUtils() {

    }

    private static final String TAG = "tag = VRRecord_ ";
    public static final int LOG_LEVEL_NONE = 0;     //不输出任和log
    public static final int LOG_LEVEL_DEBUG = 1;    //调试 蓝色
    public static final int LOG_LEVEL_INFO = 2;     //提现 绿色
    public static final int LOG_LEVEL_WARN = 3;     //警告 橙色
    public static final int LOG_LEVEL_ERROR = 4;    //错误 红色
    public static final int LOG_LEVEL_ALL = 5;      //输出所有等级

    private final static int LEN_SPLIT = 900;

    public static int mLogLevel = LOG_LEVEL_ALL;

    private static final String TAG_INIT = "VRInit";

    public static int getLogLevel() {
        return mLogLevel;
    }

    public static void setLogLevel(int level) {
        mLogLevel = level;
    }

    private static String[] splitMessage(String msg) {
        int len = TextUtils.isEmpty(msg) ? 0 : msg.length();
        if (len == 0) {
            return new String[0];
        }
        String[] arr = new String[(len - 1) / LEN_SPLIT + 1];
        int index = 0;
        while (len > LEN_SPLIT) {
            arr[index++] = msg.substring(0, LEN_SPLIT);
            msg = msg.substring(LEN_SPLIT);
            len -= LEN_SPLIT;
        }
        if (len > 0) {
            arr[index] = msg;
        }
        return arr;
    }

    public static void d(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG && !TextUtils.isEmpty(msg)) {
            String[] arr = splitMessage(msg);
            for (String info : arr) {
                Log.d(TAG + tag, info);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO && !TextUtils.isEmpty(msg)) {
            String[] arr = splitMessage(msg);
            for (String info : arr) {
                Log.i(TAG + tag, info);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN && !TextUtils.isEmpty(msg)) {
            String[] arr = splitMessage(msg);
            for (String info : arr) {
                Log.w(TAG + tag, info);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR && !TextUtils.isEmpty(msg)) {
            String[] arr = splitMessage(msg);
            for (String info : arr) {
                Log.e(TAG + tag, info);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL && !TextUtils.isEmpty(msg)) {
            String[] arr = splitMessage(msg);
            for (String info : arr) {
                Log.v(TAG + tag, info);
            }
        }
    }

    public static void e(String tag, String errTag, Throwable e) {
        if (getLogLevel() >= LOG_LEVEL_ERROR && e != null) {
            Log.e(tag, errTag, e);
        }
    }

}
