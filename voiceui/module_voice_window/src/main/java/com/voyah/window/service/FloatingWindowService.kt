package com.voyah.window.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.utils.DisplayUtils
import com.mega.nexus.os.MegaSystemProperties
import com.voyah.voice.framework.ext.normalScope
import com.voyah.window.manager.VoyahWindowManager
import com.voyah.window.manager.WindowHelper
import com.voyah.window.util.SystemConfigUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FloatingWindowService : Service() {

    private var windowBinder: VoyahWindowManager? = null
    private var windowHelper: WindowHelper= WindowHelper()
    private var normalScope: CoroutineScope? = null
    val nightModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                LogUtils.d("onReceive ")
                onMyConfigurationChange()
            }
        }
    }
    val uiModeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            LogUtils.i("on setting uiMode change")
//            onMyConfigurationChange()
            val uiMode = MegaSystemProperties.get("persist.mega.daynight.mode", "0")
            LogUtils.i("on setting uiMode change, uiMode:$uiMode")
            normalScope?.launch(Dispatchers.Default) {
                delay(200L)
                withContext(Dispatchers.Main) {
                    val isNightMode = SystemConfigUtil.isNightMode(this@FloatingWindowService)
                    LogUtils.i("on setting uiMode change, isNightMode:$isNightMode")
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                            SystemConfigUtil.setDefaultNightMode(this@FloatingWindowService)
//                        }
//                        windowBinder?.onUIModeChange(isNightMode)
                }
            }
        }
    }
    companion object {
        const val TAG = "FloatingWindowService"
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        getScreenInfo()
        normalScope = normalScope()
        watchUIModeChange()
    }

    override fun onBind(intent: Intent): IBinder? {
        LogUtils.d("onBind")
        if (windowBinder == null) {
            windowBinder = VoyahWindowManager(this)
            windowHelper.setBinder(windowBinder!!)
        }
        return windowBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d("onStartCommand: intent action:${intent?.action}")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getScreenInfo() {
        val screenSize = DisplayUtils.getScreenSize(this)
        LogUtils.d("getScreenInfo: screen width:${screenSize.x}, height:${screenSize.y}")
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val densityDpi = displayMetrics.densityDpi
        val scaledDensity = displayMetrics.scaledDensity
        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels
        val statusBarHeight = DisplayUtils.getStatusBarHeight(this)
        val navigationBarHeight = DisplayUtils.getNavigationBarHeight(this)
        LogUtils.d("getScreenInfo: density:$density, densityDpi:$densityDpi, scaledDensity:$scaledDensity")
        LogUtils.d("getScreenInfo: widthPixels:$widthPixels, heightPixels:$heightPixels")
        LogUtils.d("getScreenInfo: statusBarHeight:$statusBarHeight, navigationBarHeight:$navigationBarHeight")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val defaultUIMode = MegaSystemProperties.get("persist.mega.daynight.mode", "0")
        LogUtils.i("onConfigurationChanged defaultUIMode:$defaultUIMode")

        val nightModeFlags = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("onConfigurationChanged nightModeFlags:$nightModeFlags")

        var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                mode = AppCompatDelegate.MODE_NIGHT_YES
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                mode = AppCompatDelegate.MODE_NIGHT_NO
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }

//        AppCompatDelegate.setDefaultNightMode(mode)
//
//        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//            windowBinder?.onUIModeChange(true)
//        } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
//            windowBinder?.onUIModeChange(false)
//        }
    }

    private fun watchUIModeChange() {
        LogUtils.i("watchUIModeChange")
        contentResolver.registerContentObserver(
            Settings.System.getUriFor("persist.mega.daynight.mode"),
            false,
            uiModeObserver
        )

        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        registerReceiver(nightModeReceiver, filter)
    }

    private fun onMyConfigurationChange() {
        val uiMode = MegaSystemProperties.get("persist.mega.daynight.mode", "0")
        LogUtils.i("onMyConfigurationChange uiMode:$uiMode")
        normalScope?.launch(Dispatchers.Default) {
            delay(200L)
            withContext(Dispatchers.Main) {
                val isNightMode = SystemConfigUtil.isNightMode(this@FloatingWindowService)
                LogUtils.i("onMyConfigurationChange isNightMode:$isNightMode")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SystemConfigUtil.setDefaultNightMode(this@FloatingWindowService)
                }
                windowBinder?.onUIModeChange(isNightMode)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        windowBinder?.onDestroy()
        windowBinder = null
        windowHelper.destroy()
        unregisterReceiver(nightModeReceiver)
        contentResolver.unregisterContentObserver(uiModeObserver)
        normalScope?.cancel()
        normalScope = null
    }

}