package com.voyah.aiwindow

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.voyah.aiwindow.push.AIMsgManager
import com.voyah.aiwindow.ui.container.VpaManager

class DaemonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        //日志保存目录
        val logDir = Utils.getApp().externalCacheDir!!
            .absolutePath + "/logs"
        LogUtils.getConfig()
            .setConsoleFilter(LogUtils.V)
            .setBorderSwitch(false)
            .setDir(logDir).isLogHeadSwitch = false

        // 接收消息模块开始工作
        AIMsgManager.start()

        VpaManager.init(applicationContext)
    }
}