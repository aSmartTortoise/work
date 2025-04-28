package com.voyah.voice.ui

import android.app.Application
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.hm.lifecycle.api.ApplicationLifecycleManager
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.viewcmd.aspect.VoiceViewCmdInit

/**
 *  author : jie wang
 *  date : 2024/2/26 19:20
 *  description :
 */
@VoiceViewCmdInit(isCompatibleMode = false)
class VoiceUIApplication: Application() {

    companion object {
        const val TAG = "VoiceUIApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:")
        LogUtils.getConfig()
            .setLogSwitch(true)
            .setBorderSwitch(false)
            .isLogHeadSwitch = false
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this)
        VoiceViewCmdUtils.setLogLevel(if (BuildConfig.DEBUG) Log.VERBOSE else Log.DEBUG)
        // 应用生命周期分发到各个module。
        ApplicationLifecycleManager.init()

        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.window.VoyahWindowApplication")
        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.voice.framework.FrameworkApplication")
        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.device.ui.VoyahDeviceApplication")

        ApplicationLifecycleManager.onCreate(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ApplicationLifecycleManager.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ApplicationLifecycleManager.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        ApplicationLifecycleManager.onTrimMemory(level)
    }
}