package com.voyah.vcos.ttsservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.voyah.vcos.ttsservices.buriedpoint.BuriedPointManager;
import com.voyah.vcos.ttsservices.manager.TTSManager;
import com.voyah.vcos.ttsservices.manager.TtsResolverManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.utils.AppUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author:lcy
 * @data:2024/2/8
 **/
public class TTSService extends Service {
    private final static String TAG = "TTSService";
    private final static AtomicBoolean isInit = new AtomicBoolean(false);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isInit.getAndSet(true);
        setFrontService();
        if (!Util.vehicleSimulatorJudgment()) {
            Util.unzipFile(Constant.Path.ZIP_RES, Constant.Path.PHONE_OFFLINE_RES_PATH);
        }
        LogUtils.i(TAG, "TTSService onCreate......" + AppUtils.getAppVersionName(AppContext.instant));
        ProximityInteractionManager.getInstance().init();
        BuriedPointManager.getInstance().init(this);
        //todo:初始化TTS实例
        TtsResolverManager.getInstance().init(this);
//        //添加提示音通道及播放器等
//        TTSManager.getInstance().initTts(this, Constant.Usage.NOTIFICATION_USAGE);
    }

    /**
     * 进程保活
     */
    private void setFrontService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ttsService", "ttsService",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification =
                    new NotificationCompat.Builder(this, "ttsService").setContentTitle("")
                            .setContentText("")
                            .build();
            startForeground(1, notification);
        }
    }

    public static void startService(Context context) {
        LogUtils.i(TAG, "startService...");
        if (isInit.get())
            return;
        isInit.getAndSet(true);
        LogUtils.i(TAG, " start vrService ");
        Intent serviceIntent = new Intent(context.getApplicationContext(), TTSService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
