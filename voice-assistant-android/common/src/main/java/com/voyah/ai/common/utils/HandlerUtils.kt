package com.voyah.ai.common.utils

import android.os.Handler
import android.os.Looper

/**
 * @Date 2024/9/2 15:25
 * @Author 8327821
 * @Email *
 * @Description .
 **/

object HandlerUtils {
    private var mHandler: Handler? = null

    private val mainHandler: Handler
        // 获取主线程的 Handler
        get() {
            if (mHandler == null) {
                mHandler = Handler(Looper.getMainLooper())
            }
            return mHandler!!
        }

    // 在主线程中执行任务
    fun runOnUIThread(runnable: Runnable?) {
        mainHandler.post(runnable!!)
    }

    // 在主线程中延迟执行任务
    fun runOnUIThreadDelayed(runnable: Runnable?, delayMillis: Long) {
        mainHandler.postDelayed(runnable!!, delayMillis)
    }

    // 移除在主线程中的任务
    fun removeRunnableFromUIThread(runnable: Runnable?) {
        mainHandler.removeCallbacks(runnable!!)
    }
}