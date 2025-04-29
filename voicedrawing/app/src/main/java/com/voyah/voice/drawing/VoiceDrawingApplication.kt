package com.voyah.voice.drawing

import android.app.Application
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.hm.lifecycle.api.ApplicationLifecycleManager
import com.voyah.viewcmd.aspect.VoiceViewCmdInit
import com.voyah.voice.framework.report.ReportHelp

/**
 *  author : jie wang
 *  date : 2024/2/26 19:20
 *  description :
 */

@VoiceViewCmdInit
class VoiceDrawingApplication: Application() {
    companion object {
        const val TAG = "VoiceDrawingApplication"
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.getConfig()
            .setBorderSwitch(false)
            .isLogHeadSwitch = false
        LogUtils.d("onCreate")
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this)

        // 应用生命周期分发到各个module。
        ApplicationLifecycleManager.init()
        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.voice.main.MainApplication"
        )
        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.voice.framework.FrameworkApplication"
        )
        ApplicationLifecycleManager.registerApplicationLifecycleCallbacks(
            "com.voyah.device.drawing.VoyahDeviceApplication"
        )

        ApplicationLifecycleManager.onCreate(this)
        ReportHelp.getInstance().init(this)


    }

    override fun onTerminate() {
        Log.i(TAG,"onTerminate")
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