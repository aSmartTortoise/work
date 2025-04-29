package com.voyah.ai.voice;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.AppUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.receiver.voice.AppBroadcastReceiver;
import com.voyah.ai.voice.voice.InteractiveManager;
import com.voyah.ai.voice.voice.VrInfrastructureManager;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author:lcy 语音服务
 * @data:2024/1/20
 **/
public class VRService extends Service {
    private final static String TAG = VRService.class.getSimpleName();
    private final static AtomicBoolean isInit = new AtomicBoolean(false);

    private final static Context mContext = AppContext.instant;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getPackageNameFromPid(mContext, android.os.Process.myPid());
        LogUtils.d(TAG, "processName:" + processName);
        if (!StringUtils.equals(processName, getPackageName()))
            return;
        isInit.getAndSet(true);
        setFrontService();
        LogUtils.i(TAG, "VRServices start , version is " + AppUtils.getAppVersionName());
//        CarServicePropUtils.getInstance().init(this);
        VrInfrastructureManager.getInstance().init();
        InteractiveManager.getInstance().init();

        AppBroadcastReceiver.registerReceiver(mContext);
        //声音复刻,延迟30s提交任务
        //AccountLogoutReceiver.register(mContext);
        //HandlerUtils.INSTANCE.runOnUIThreadDelayed(VoiceReproductionManager.INSTANCE::startPolling, 30 * 1000);
        //数据同步，初始化60s后执行

    }

    public String getPackageNameFromPid(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.pid == pid) {
                    // 返回找到的包名
                    return processInfo.processName;
                }
            }
        }
        // 如果没有找到对应的包名，则返回 null 或适当的错误信息
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "------------onStartCommand------------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //进行一些反注册操作
        VrInfrastructureManager.getInstance().onDestroy();
        AppBroadcastReceiver.unregisterReceiver(mContext);
    }

    /**
     * 进程保活
     */
    private void setFrontService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("VcosVoice", "VcosVoice",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification =
                    new NotificationCompat.Builder(this, "VcosVoice").setContentTitle("")
                            .setContentText("")
                            .build();
            startForeground(1, notification);
        }

    }

    public static void startService(Context context) {
        if (isInit.get())
            return;
        isInit.getAndSet(true);
        LogUtils.i(TAG, " start vrService ");
        Intent serviceIntent = new Intent(context.getApplicationContext(), VRService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

}
