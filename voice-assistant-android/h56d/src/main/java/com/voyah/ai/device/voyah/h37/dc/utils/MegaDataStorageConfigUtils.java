package com.voyah.ai.device.voyah.h37.dc.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.blankj.utilcode.util.LogUtils;

import mega.config.MegaDataStorageConfig;

public class MegaDataStorageConfigUtils {
    public static final String Amb_Light_Clothing_Link_Status = "amb_light_clothing_link_status";
    public static final String Amb_Light_Clothing_Main_Vice = "amb_light_clothing_main_vice";

    public static void init(Context applicationContext) {
        MegaDataStorageConfig.init(applicationContext);
    }

    public static int getInt(String key, int defValue) {
        int value = MegaDataStorageConfig.getInt(key, defValue);

        LogUtils.d("get int from MegaDataStorageConfig, key = "
                + key + ", value = " + value + ", defValue = " + defValue);

        return value;
    }

    public static void putInt(String key, int value) {
        MegaDataStorageConfig.putInt(key, value);

        LogUtils.d("put int to MegaDataStorageConfig, key = "
                + key + ", value = " + value);
        MegaDataStorageConfig.getContentResolver().notifyChange(
                Settings.System.getUriFor(key), null);
    }

    /**
     * 注册监听.
     */
    public static void registerObserver(String key) {
        registerObserver(key, mObserver);
    }

    public static void registerObserver(String key, ContentObserver observer) {
        LogUtils.d("register observer, key = " + key);
        if (MegaDataStorageConfig.getContentResolver() == null) {
            LogUtils.d("register observer, MegaDataStorageConfig.getContentResolver() == null");
            return;
        }
        MegaDataStorageConfig.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(key), false, observer);
    }

    private static final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri == null) {
                LogUtils.e("Amb Light uri is Null");
                return;
            }
        }
    };

    public static void notifyChange(String key) {
        MegaDataStorageConfig.getContentResolver().notifyChange(
                Settings.System.getUriFor(key), null);
    }
}
