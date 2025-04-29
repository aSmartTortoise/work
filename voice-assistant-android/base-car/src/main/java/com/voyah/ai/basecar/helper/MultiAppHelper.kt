package com.voyah.ai.basecar.helper

import android.annotation.SuppressLint
import com.voice.sdk.device.func.FuncConstants
import com.voice.sdk.device.system.DeviceScreenType
import com.voyah.ai.common.utils.LogUtils

val TAG: String = MultiAppHelper::class.java.simpleName

@SuppressLint("ServiceCast")
object MultiAppHelper {

    const val LAUNCHER_PKG_2 = "com.voyah.cockpit.launcher.secondary" //副驾屏
    const val LAUNCHER_PKG = "com.voyah.cockpit.launcher" //中控屏
    const val LAUNCHER_PKG_CEILING = "com.voyah.cockpit.launcher.ceiling" //吸顶屏

    private val multiApps = listOf(
        AppSupportInfo.MultiApp("爱奇艺", "com.arcvideo.car.iqy.video"),
        AppSupportInfo.MultiApp("本地视频", "com.voyah.cockpit.video"),
        AppSupportInfo.MultiApp("QQ音乐", "com.tencent.qqlive.audiobox"),
        AppSupportInfo.MultiApp("岚图音乐", "com.voyah.cockpit.voyahmusic"),
        AppSupportInfo.MultiApp("咪咕视频", "cn.cmvideo.car.play"),
        AppSupportInfo.MultiApp("腾讯视频", "com.tencent.qqlive.audiobox"),
        AppSupportInfo.MultiApp("哔哩哔哩", "com.bilibili.bilithings"),
        AppSupportInfo.MultiApp("车鱼视听", "com.bytedance.byteautoservice3"),
        AppSupportInfo.MultiApp("网易云音乐", ""),
        AppSupportInfo.MultiApp("喜马拉雅", ""),
        AppSupportInfo.MultiApp("云听", ""),
        AppSupportInfo.MultiApp("AI绘画", "com.voyah.voice.drawing"),
        AppSupportInfo.MultiApp("浏览器", ""),
    )

    private val preemptiveApps = listOf(
        AppSupportInfo.PreemptiveApp("岚图商城", "com.xiaoma.appstore"),
        AppSupportInfo.PreemptiveApp("雷石KTV", "com.thunder.carplay"),
        AppSupportInfo.PreemptiveApp("演示助手", "com.voyah.demoassistant"),
        AppSupportInfo.PreemptiveApp("有线投屏", "com.voyah.cockpit.casting"),
        AppSupportInfo.PreemptiveApp("儿童座椅", "com.voyah.cockpit.child.seat"),
    )

    private val sceneModeApps = listOf(
        AppSupportInfo.FrontApp("场景模式", "com.voyah.cockpit.scene.mode"),
    )

    private val ceilingApps = listOf(
        AppSupportInfo.CeilingApp("远程控制", "com.voyah.bt_remote_control"),
    )

    private val allSupportApp = multiApps + preemptiveApps + sceneModeApps + ceilingApps

    fun canOpenOnTargetScreen(pkgName: String, screenName: String): Boolean {
        LogUtils.d(TAG, "canOpenOnTargetScreen[pkgName: $pkgName, screenName: $screenName]")
        val targetDeviceScreenType = DeviceScreenType.fromValue(screenName)
        val appInfo: AppSupportInfo = allSupportApp.find {
            it.pkgName == pkgName
        } ?: AppSupportInfo.OrdinaryApp("", pkgName) //不在支持列表的应用，默认只支持主屏
        return appInfo.isSupportScreen(deviceScreenType = targetDeviceScreenType)
    }

    fun supportMulti(pkgName: String): Boolean {
        LogUtils.d(TAG, "supportMulti[pkgName: $pkgName]")
        val appInfo: AppSupportInfo = allSupportApp.find {
            it.pkgName == pkgName
        } ?: AppSupportInfo.OrdinaryApp("", pkgName) //不在支持列表的应用，默认只支持主屏
        return appInfo.isSupportMulti()
    }


    /**
     * 是否在抢占应用列表中
     */
    fun isPreemptiveApp(pkgName: String): Boolean {
        return preemptiveApps.any {
            it.pkgName == pkgName
        }
    }

    fun fetchScreen(pkgName: String): String {
        val appInfo = allSupportApp.find {
            it.pkgName == pkgName
        } ?: AppSupportInfo.OrdinaryApp("", pkgName)
        LogUtils.d(TAG, "fetchScreen[appInfo: $appInfo]")
        return if (appInfo.supportMain) {
            FuncConstants.VALUE_SCREEN_CENTRAL
        } else if (appInfo.supportPassenger) {
            FuncConstants.VALUE_SCREEN_PASSENGER
        } else if (appInfo.supportCeiling) {
            FuncConstants.VALUE_SCREEN_CEIL
        } else {
            FuncConstants.VALUE_SCREEN_CENTRAL//兜底主屏
        }
    }
}


//应用多开
sealed class AppSupportInfo(
    val appName: String,
    val pkgName: String,
    val supportMain: Boolean,
    val supportPassenger: Boolean,
    val supportCeiling: Boolean,
    private val supportMulti: Boolean
) {
    fun isSupportScreen(deviceScreenType: DeviceScreenType): Boolean {
        return when (deviceScreenType) {
            DeviceScreenType.CENTRAL_SCREEN -> supportMain
            DeviceScreenType.PASSENGER_SCREEN -> supportPassenger
            DeviceScreenType.CEIL_SCREEN -> supportCeiling
        }
    }

    fun isSupportMulti(): Boolean {
        return supportMulti
    }

    /**
     * 多媒体应用，支持中控屏，支持副驾屏，支持吸顶屏，支持三屏同看
     * 支持多个屏幕，可以同时显示
     */
    class MultiApp(
        appName: String,
        pkgName: String
    ) : AppSupportInfo(appName, pkgName, true, true, true, true)

    /**
     * 抢占式应用，支持多个屏幕，但只能在一个屏幕上显示
     */
    class PreemptiveApp(
        appName: String,
        pkgName: String
    ) : AppSupportInfo(appName, pkgName, true, true, true, false)


    /**
     * 普通应用，只支持主屏
     */
    class OrdinaryApp(
        appName: String,
        pkgName: String
    ) : AppSupportInfo(appName, pkgName, true, false, false, false)

    /**
     * 支持主副驾(前排)
     */
    class FrontApp(
        appName: String = "情景模式",
        pkgName: String
    ) : AppSupportInfo(appName, pkgName, true, true, false, false)

    /**
     * 只支持吸顶屏
     */
    class CeilingApp(
        appName: String = "遥控器",
        pkgName: String
    ) : AppSupportInfo(appName, pkgName, false, false, true, false)
}