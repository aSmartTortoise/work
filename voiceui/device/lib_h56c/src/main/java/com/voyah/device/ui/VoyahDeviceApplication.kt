package com.voyah.device.ui

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.hm.iou.lifecycle.annotation.AppLifecycle
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks
import com.lib.common.voyah.ServiceFactory

/**
 *  author : jie wang
 *  date : 2025/3/18 11:43
 *  description :
 */
@AppLifecycle
class VoyahDeviceApplication : IApplicationLifecycleCallbacks {

    override fun getPriority() = IApplicationLifecycleCallbacks.MAX_PRIORITY


    override fun onCreate(context: Context?) {
        LogUtils.i("onCreate")
        ServiceFactory.getInstance().voiceService = VoiceService
    }

    override fun onTerminate() {
        LogUtils.i("onTerminate")
    }

    override fun onLowMemory() {
        LogUtils.i("onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        LogUtils.i("onTrimMemory")
    }

}