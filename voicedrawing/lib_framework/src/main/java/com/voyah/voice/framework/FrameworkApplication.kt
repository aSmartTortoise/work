package com.voyah.voice.framework

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.hm.iou.lifecycle.annotation.AppLifecycle
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.framework.toast.TipsToast

/**
 *  author : jie wang
 *  date : 2024/4/17 15:54
 *  description :
 */
@AppLifecycle
class FrameworkApplication() : IApplicationLifecycleCallbacks {

    override fun getPriority() = IApplicationLifecycleCallbacks.MAX_PRIORITY

    override fun onCreate(context: Context?) {
        LogUtils.d("onCreate")
        TipsToast.init(context!! as Application)
        AppHelper.init(context!! as Application, BuildConfig.DEBUG)
    }

    override fun onTerminate() {

    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }
}