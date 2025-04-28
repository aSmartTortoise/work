package com.voyah.window.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.utils.DisplayUtils
import com.voyah.window.manager.VoyahWindowManager


class FloatingWindowService : Service() {

    private var windowBinder: VoyahWindowManager? = null
    companion object {
        const val TAG = "FloatingWindowService"
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        getScreenInfo()
    }

    override fun onBind(intent: Intent): IBinder? {
        LogUtils.d("onBind")
        if (windowBinder == null) {
            windowBinder = VoyahWindowManager(this)
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

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        windowBinder?.onDestroy()
        windowBinder = null
    }

}