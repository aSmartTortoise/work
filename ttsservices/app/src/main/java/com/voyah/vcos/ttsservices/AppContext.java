package com.voyah.vcos.ttsservices;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mega.nexus.os.MegaUserHandle;
import com.tencent.mmkv.MMKV;
import com.voyah.vcos.ttsservices.manager.SpeakerManager;
import com.voyah.vcos.ttsservices.utils.DnnUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;


/**
 * @author:lcy
 * @data:2024/3/22
 **/
public class AppContext extends Application {
    public static Context instant;

    @Override
    public void onCreate() {
        super.onCreate();
        int userId = MegaUserHandle.myUserId();
        LogUtils.d("AppContext", "onCreate:" + userId);
        if (userId != MegaUserHandle.USER_SYSTEM) {
            return;
        }
        ContextUtils.setAppContext(this.getApplicationContext());
        instant = getApplicationContext();
        LogUtils.d("AppContext", "instant:" + instant);
        MMKV.initialize(instant);
        DnnUtils.switchToAllNetwork(instant,false);
        SpeakerManager.getInstance().init(instant);
        //todo:启动服务
        TTSService.startService(instant);
    }
}
