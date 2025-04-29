package com.voyah.vcos.ttsservices.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author:lcy
 * @data:2024/1/20
 **/
public class LogUtils {
    public static final int LOG_LEVEL_NONE = 0;     //不输出任和log
    public static final int LOG_LEVEL_DEBUG = 1;    //调试 蓝色
    public static final int LOG_LEVEL_INFO = 2;     //提现 绿色
    public static final int LOG_LEVEL_WARN = 3;     //警告 橙色
    public static final int LOG_LEVEL_ERROR = 4;    //错误 红色
    public static final int LOG_LEVEL_ALL = 5;      //输出所有等级

    private static boolean TO_CONSOLE = true;
    private static int LEN_SPLIT = 900;//1024 * 2;

    private static boolean isShowThreadName = false;
    /**
     * 允许输出的log日志等级
     * 当出正式版时,把mLogLevel的值改为 LOG_LEVEL_NONE,
     * 就不会输出任何的Log日志了.
     */
    public static int mLogLevel = LOG_LEVEL_ALL;

    private static final String BASE_TAG = "TTSService ";

    /**
     * 获取Log等级
     *
     * @return
     */
    public static int getLogLevel() {
        return mLogLevel;
    }

    /**
     * 给输出的Log等级赋值
     *
     * @param level
     */
    public static void setLogLevel(int level) {
        mLogLevel = level;

        if (TO_CONSOLE) {
            return;
        }
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
            if (TO_CONSOLE) {
                String[] arr;
                if (isShowThreadName)
                    arr = splitMessage(msg + " , threadName:" + Thread.currentThread().getName());
                else
                    arr = splitMessage(msg);
                for (String info : arr) {
                    Log.d(BASE_TAG + tag, info);
                }
                return;
            }
        }
    }

    public static void i(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO && !TextUtils.isEmpty(msg)) {
            if (TO_CONSOLE) {
                String[] arr;
                if (isShowThreadName)
                    arr = splitMessage(msg + " , threadName:" + Thread.currentThread().getName());
                else
                    arr = splitMessage(msg);
                for (String info : arr) {
                    Log.i(BASE_TAG + tag, info);
                }
                return;
            }
        }
    }

    public static void w(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN && !TextUtils.isEmpty(msg)) {
            if (TO_CONSOLE) {
                String[] arr;
                if (isShowThreadName)
                    arr = splitMessage(msg + " , threadName:" + Thread.currentThread().getName());
                else
                    arr = splitMessage(msg);
                for (String info : arr) {
                    Log.w(BASE_TAG + tag, info);
                }
                return;
            }
        }
    }

    public static void e(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR && !TextUtils.isEmpty(msg)) {
            if (TO_CONSOLE) {
                String[] arr;
                if (isShowThreadName)
                    arr = splitMessage(msg + " , threadName:" + Thread.currentThread().getName());
                else
                    arr = splitMessage(msg);
                for (String info : arr) {
                    Log.e(BASE_TAG + tag, info);
                }
                return;
            }
        }
    }

    public static void v(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL && !TextUtils.isEmpty(msg)) {
            if (TO_CONSOLE) {
                String[] arr;
                if (isShowThreadName)
                    arr = splitMessage(msg + " , threadName:" + Thread.currentThread().getName());
                else
                    arr = splitMessage(msg);
                for (String info : arr) {
                    Log.v(BASE_TAG + tag, info);
                }
                return;
            }
        }
    }

    public static void e(String tag, String errTag, Throwable e) {
        if (getLogLevel() >= LOG_LEVEL_ERROR && e != null) {
            if (isShowThreadName)
                Log.e(BASE_TAG + tag, errTag + " , threadName:" + Thread.currentThread().getName(), e);
            else
                Log.e(BASE_TAG + tag, errTag, e);
        }
    }
}
