package com.voyah.ai.basecar.system

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.blankj.utilcode.util.Utils

/**
 * @Date 2025/4/28 16:06
 * @Author 8327821
 * @Email *
 * @Description VCOS 1.5 行车娱乐限制
 **/
object DrivingRestrict {
    const val KEY_LIMIT_PACKAGES = "APPSTORE_LIMIT_PACKAGES"
    var restrictApps: String = ""
        private set

    fun init() {
        val uri = Uri.parse("${Settings.Global.CONTENT_URI}/$KEY_LIMIT_PACKAGES")
        Utils.getApp().contentResolver.registerContentObserver(uri, true, object : ContentObserver(
            Handler(Looper.getMainLooper())
        ) {
            override fun onChange(selfChange: Boolean) {
                restrictApps = Settings.Global.getString(
                    Utils.getApp().contentResolver,
                    KEY_LIMIT_PACKAGES
                ) ?: ""
            }
        })
        restrictApps = Settings.Global.getString(
            Utils.getApp().contentResolver,
            KEY_LIMIT_PACKAGES
        ) ?: ""
    }

    fun isDrivingRestrictApp(packageName: String): Boolean {
        return restrictApps.split(",").contains(packageName)
    }
}