package com.voyah.ai.voice;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log2;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceConfigManager;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.DnnUtils;

import java.lang.reflect.Method;

/**
 * @author:lcy
 * @data:2024/1/20
 **/
public class AppContext extends Application {
    public static Context instant;
    private static final String TAG = "VOICE_APP";

    @Override
    public void onCreate() {
        super.onCreate();
        Log2.i(TAG, "onCreate app version:" + BuildConfig.VERSION_NAME);
        MultiDex.install(this);
        ContextUtils.setAppContext(this.getApplicationContext());
        Utils.init(this);
        instant = getApplicationContext();
        realCarInit(getApplicationContext());
        VoiceConfigManager.getInstance().setPreEnvEnabled(DeviceHolder.INS().getDevices().getVoiceSettings().isPreEnvEnabled());
        DnnUtils.switchToAllNetwork(this, DeviceHolder.INS().getDevices().getVoiceSettings().isAllNetworkEnabled());
        DeviceHolder.INS().getDevices().getThreadDelay().init();
        VRService.startService(instant);
        LogUtils.getConfig().setConsoleFilter(LogUtils.V)
                .setBorderSwitch(false)
                .setLogHeadSwitch(false);
    }


    public void realCarInit(Context context) {
        try {
            Class<?> realCarClass = Class.forName("com.voyah.ai.device.RealCar");
            Method initMethod = realCarClass.getMethod("init", Context.class);
            initMethod.invoke(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

