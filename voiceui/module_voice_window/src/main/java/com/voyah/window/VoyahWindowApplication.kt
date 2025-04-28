package com.voyah.window

import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.hm.iou.lifecycle.annotation.AppLifecycle
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks.NORM_PRIORITY
import com.voyah.cockpit.window.WindowMessageManager
import com.voyah.cockpit.window.WindowMessageManager.WindowMessageCallback
import com.voyah.cockpit.window.model.WindowMessage
import com.voyah.cockpit.window.model.WindowType

/**
 *  author : jie wang
 *  date : 2024/3/11 16:56
 *  description : module_voice_window 组件应用生命周期管理类，用来执行初始化sdk等任务
 */
@AppLifecycle
class VoyahWindowApplication() : IApplicationLifecycleCallbacks {

    companion object {
        const val TAG = "VoyahWindowApplication"
    }

    private var context: Context? = null

    override fun getPriority() = NORM_PRIORITY

    override fun onCreate(context: Context?) {
        this.context = context
        Log.d(TAG, "onCreate")
        bindService(context!!)
    }

    override fun onTerminate() {
    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }

    private fun bindService(context: Context) {
        if (BuildConfig.DEBUG) {
            Thread {
                WindowMessageManager.getInstance().setMessageCallback(object : WindowMessageCallback {
                    override fun onServiceBind() {
                        Log.d(TAG, "onServiceBind")
                    }

                    override fun onServiceDisconnected() {

                    }

                    override fun onBinderDied() {
                    }

                    override fun onReceiveWindowMessage(msg: WindowMessage) {
                        when (msg.name) {
                            WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD -> {
                                val msgAction = msg.action
                                LogUtils.d("msg:$msgAction")
                            }

                            else -> {}
                        }
                    }

                    override fun onReceiveVoyahWindowMessage(msgJson: String) {
                        Log.d(TAG, "onReceiveVoyahWindowMessage: msgJson:$msgJson")
                    }

                    override fun onCardScroll(cardType: String, direction: Int, canScroll: Boolean) {
                        Log.d(TAG, "onCardScroll cardType:$cardType, direction:$direction, canScroll:$canScroll")
                    }
                })
                WindowMessageManager.getInstance().init(context)
            }.start()

        }
    }
}