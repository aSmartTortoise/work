package com.voyah.voice.main.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import com.blankj.utilcode.util.LogUtils

/**
 *  author : jie wang
 *  date : 2024/8/8 11:07
 *  description :
 */
class DrawingAPIService : Service() {

    val observerSplitScreen = object : ContentObserver(Handler(Looper.getMainLooper())) {

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            LogUtils.d("split screen state change...")
            DrawingAPIBinder.getInstance().onSplitScreenStateChange()
        }
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
        watchSplitScreenState()
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogUtils.d("onBind")
        return DrawingAPIBinder.getInstance()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val nightModeFlags = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("onConfigurationChanged nightModeFlags:$nightModeFlags")
    }

    private fun watchSplitScreenState() {
        LogUtils.i("watchSplitScreenState")
        contentResolver.registerContentObserver(
            Settings.System.getUriFor("split_screen_status"),
            false,
            observerSplitScreen
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        contentResolver.unregisterContentObserver(observerSplitScreen)
    }
}