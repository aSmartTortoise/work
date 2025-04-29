package com.voyah.voice.main

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.UserHandle
import com.blankj.utilcode.util.LogUtils
import com.hm.iou.lifecycle.annotation.AppLifecycle
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks
import com.hm.lifecycle.api.IApplicationLifecycleCallbacks.NORM_PRIORITY
import com.mega.nexus.os.MegaSystemProperties
import com.voice.drawing.api.DrawingAPIManager
import com.voice.drawing.api.DrawingAPIManager.DrawingAPIManagerCallback
import com.voice.drawing.api.model.APIConfig
import com.voyah.cockpit.common.gallery.GalleryManager
import com.voyah.cockpit.common.gallery.callback.IConnectCallback
import com.voyah.vcos.manager.MegaDisplayHelper
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.framework.util.SystemConfigUtil
import com.voyah.voice.main.manager.LifeStateManager
import com.voyah.voice.main.service.DrawingAPIBinder
import com.xiaoma.xmsdk.XmSdk

/**
 *  author : jie wang
 *  date : 2024/4/16 10:44
 *  description :
 */

@AppLifecycle
class MainApplication() : IApplicationLifecycleCallbacks {

    val nightModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                LogUtils.d("onReceive action configuration changed.")
                onMyConfigurationChange()
            }
        }
    }

    override fun getPriority() = NORM_PRIORITY

    override fun onCreate(context: Context?) {
        LogUtils.d("onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemConfigUtil.setDefaultNightMode(context!!)
        }
        watchUIModeChange(context!!)

        LifeStateManager.getInstance().setLifecycleCallbacks(context!!.applicationContext as Application)
        XmSdk.init(context!!)

        GalleryManager.getInstance().init(context, object : IConnectCallback {

            override fun onConnectState(p0: ComponentName?, p1: Boolean) {
                LogUtils.i("gallery sdk init, onConnectState componentName:$p0, p1:$p1")
            }
        })

        if (BuildConfig.DEBUG) {
            val callbackMain: DrawingAPIManagerCallback = object : DrawingAPIManagerCallback {
                override fun onServiceBind() {
                    LogUtils.d("main onServiceBind")
                }

                override fun onServiceDisconnected() {
                }

                override fun onBinderDied() {
                }

                override fun startDrawing() {
                    LogUtils.d("main startDrawing")
                }

                override fun redraw(prompt: String) {
                    LogUtils.d("main redraw prompt:$prompt")
                }

                override fun redraw(prompt: String, userHandle: UserHandle) {
                    LogUtils.d("main redraw prompt:$prompt")
                }
            }

            val userHandleMain: UserHandle = MegaDisplayHelper.getUserHandleByDisplayId(
                MegaDisplayHelper.getMainScreenDisplayId()
            )
            val apiConfigMain = APIConfig(userHandleMain, callbackMain)

            val apiConfigs: MutableList<APIConfig> = ArrayList()
            apiConfigs.add(apiConfigMain)
            DrawingAPIManager.getInstance().init(context, apiConfigs)
        }

    }

    private fun watchUIModeChange(context: Context) {
        LogUtils.i("watchUIModeChange")
        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        context.registerReceiver(nightModeReceiver, filter)
    }

    private fun onMyConfigurationChange() {
        val defaultUIMode = MegaSystemProperties.get("persist.mega.daynight.mode", "0")
        LogUtils.i("setDefaultNightMode defaultUIMode:$defaultUIMode")
        val isNightMode = SystemConfigUtil.isNightMode(AppHelper.getApplication())
        LogUtils.i("onMyConfigurationChange isNightMode:$isNightMode")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemConfigUtil.setDefaultNightMode(AppHelper.getApplication())
        }
        DrawingAPIBinder.getInstance().onConfigurationChanged()
    }

    override fun onTerminate() {
        LogUtils.d("onTerminate")
    }

    override fun onLowMemory() {
        LogUtils.d("onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        LogUtils.d("onTrimMemory level:$level")
    }


}