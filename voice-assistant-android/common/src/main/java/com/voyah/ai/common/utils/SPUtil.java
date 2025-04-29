package com.voyah.ai.common.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static final String PREFERENCE_NAME = "my_preference";

    // 获取SharedPreferences实例
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    // 保存数据的方法
    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // 获取数据的方法
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    // 获取数据的方法
    public static Boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // 删除数据的方法
    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    // 清除所有数据的方法
    public static void clearAll(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
