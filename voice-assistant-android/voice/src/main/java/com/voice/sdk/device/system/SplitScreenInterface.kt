package com.voice.sdk.device.system

import android.os.Bundle
import com.voice.sdk.device.carservice.constants.ICommon.Switch

/**
 * @Date 2025/3/3 14:44
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface SplitScreenInterface {

    fun isNeedSplitScreen(): Boolean

    fun isDealSplitScreening(): Boolean

    fun isSplitScreening(): Boolean

    fun enterSpiltScreen()

    fun enterFullScreen()

    fun switchSplitScreen()

    fun getRightSpiltScreenPackage(): String

    fun getLeftSpiltScreenPackage(): String

    fun isSupportDriveDesktop(): Boolean

    fun isDriveDesktop(): Boolean

    fun updateSplitSwitch(switch: Boolean)

    fun enterSplitScreen(
        pkgName: String,
        className: String?,
    )

    /**
     * (支持分屏应用调用)
     * 适用没有三方sdk接口的打开xxx场景
     * 指定位置分屏打开
     *
     * @param pkgName（String类型） : 包名，必需传递
     * @param className             （String类型）：activity类名，可不传递，不传递时将拉起package对应的启动
     * @param position（整型）：必需传递0或1  0：进入分屏左侧，1：进入分屏右侧
     */
    fun enterSplitScreen(
        pkgName: String,
        className: String?,
        businessBundle: Bundle
    )

    /**
     * 退出指定位置分屏
     * dismissLeft（布尔型）：true：左侧应用退出分屏(右侧全屏)；false：右侧应用退出分屏(左侧全屏)
     */
    fun sendDismissSplitBroadcast(dismissLeft: Boolean)

    /**
     * 注册分屏和全屏变化监听
     */
    fun addSplitStatusListener(listener: SplitStatusListener)

    /**
     * 移除分屏和全屏变化监听
     */
    fun removeSplitStatusListener(listener: SplitStatusListener)

    /**
     * 分屏全屏切换变化回调
     */
    fun onSplitStatusChanged(isSplitScreen: Boolean)

    interface SplitStatusListener {
        fun onChanged(isSplitScreen: Boolean)
    }
}