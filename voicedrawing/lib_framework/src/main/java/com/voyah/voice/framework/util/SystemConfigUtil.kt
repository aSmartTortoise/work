package com.voyah.voice.framework.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 *  author : jie wang
 *  date : 2024/10/12 15:11
 *  description :
 */
object SystemConfigUtil {

    fun isNightMode(context: Context): Boolean {
        val uiModeFlag = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("isNightMode uiModeFlag:$uiModeFlag")

        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val nightMode = uiModeManager.nightMode
        LogUtils.i("isNightMode nightMode:$nightMode")

        var nightModeFlag = false
        when (uiModeFlag) {
            Configuration.UI_MODE_NIGHT_YES -> {
//                nightModeFlag = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
//                nightModeFlag = false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }
        when (nightMode) {
            UiModeManager.MODE_NIGHT_NO -> {
                nightModeFlag = false
            }

            UiModeManager.MODE_NIGHT_YES -> {
                nightModeFlag = true
            }
        }
        LogUtils.d("isNightMode nightModeFlag:$nightModeFlag")
        return nightModeFlag
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setDefaultNightMode(context: Context) {
        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        LogUtils.i("setDefaultNightMode nightModeFlags:$nightModeFlags")

        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val nightMode = uiModeManager.nightMode
        LogUtils.i("setDefaultNightMode nightMode:$nightMode")
        uiModeManager.setApplicationNightMode(nightMode)
    }

    fun getScreenWidth() {
        ScreenUtils.getScreenWidth()
    }
}