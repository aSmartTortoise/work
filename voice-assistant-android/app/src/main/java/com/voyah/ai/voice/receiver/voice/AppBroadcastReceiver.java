package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log2;

import com.voice.sdk.device.DeviceHolder;

import java.util.concurrent.atomic.AtomicBoolean;


public class AppBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AppBroadcastReceiver";
    private final static AtomicBoolean isInit = new AtomicBoolean(false);

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (intent.getData() == null) {
            return;
        }
        String packageName = intent.getData().getSchemeSpecificPart();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            // 应用被安装
            Log2.d(TAG, "应用已安装： " + packageName);
            DeviceHolder.INS().getDevices().getSystem().getApp().refreshAppInfo();

        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            // 应用被卸载
            Log2.d(TAG, "应用已卸载： " + packageName);
            DeviceHolder.INS().getDevices().getSystem().getApp().refreshAppInfo();

        }
    }

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package"); // 必须添加此行以正确过滤包名
        context.registerReceiver(new AppBroadcastReceiver(), filter);
        isInit.getAndSet(true);
    }

    public static void unregisterReceiver(Context context) {
        if (isInit.get()) {
            context.unregisterReceiver(new AppBroadcastReceiver());
            isInit.getAndSet(false);
        }
    }
}
