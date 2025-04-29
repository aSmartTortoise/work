package com.voyah.ai.device.system

import android.content.Context
import android.os.UserHandle
import com.voice.sdk.device.system.AppInterface
import com.voice.sdk.device.system.AttributeInterface
import com.voice.sdk.device.system.DeviceScreenType
import com.voice.sdk.device.system.KeyboardInterface
import com.voice.sdk.device.system.ScreenInterface
import com.voice.sdk.device.system.ShareInterface
import com.voice.sdk.device.system.SplitScreenInterface
import com.voice.sdk.device.system.SystemInterface
import com.voice.sdk.device.system.UiInterface
import com.voice.sdk.device.system.VolumeInterface

/**
 * @Date 2025/4/2 19:56
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object SystemInterfaceImpl: SystemInterface {

    val appImpl : AppInterface = object : AppInterface {
        override fun getPackageName(p0: String?): String {
            return "AppInterface.getPackageName"
        }

        override fun isInstalledByAppName(p0: String?): Boolean {
            return true
        }

        override fun isInstalledByPackageName(p0: String?): Boolean {
            return true
        }

        override fun getAppVersionName(p0: String?): String {
            return "AppInterface.getAppVersionName"
        }

        override fun getAppVersionName(): String {
            return "AppInterface.getAppVersionName"
        }

        override fun getAppVersionCode(p0: String?): Long {
            return 1
        }

        override fun getAppVersionCode(): Long {
            return 1
        }

        override fun isSystemApp(p0: String?): Boolean {
            return true
        }

        override fun refreshAppInfo() {

        }

        override fun openApp(p0: String?, p1: DeviceScreenType?): Boolean {
            return true
        }

        override fun closeApp(p0: String?, p1: DeviceScreenType?) {

        }

        override fun isAppForeGround(p0: String?, p1: DeviceScreenType?): Boolean {
            return true
        }

        override fun isAppForeGround(p0: String?, p1: Int): Boolean {
            return true
        }

        override fun isSupportMulti(p0: String?): Boolean {
            return true
        }

        override fun switchPage(
            p0: String?,
            p1: Boolean,
            p2: String?,
            p3: String?,
            p4: Any?,
            p5: Any?
        ) {

        }

        override fun startTtsService() {

        }

        override fun fetchScreen(p0: String?): String {
            return "AppInterface.fetchScreen"
        }

        override fun isPreemptiveApp(p0: String?): Boolean {
            return false
        }

        override fun isAppSupportScreen(p0: String?, p1: DeviceScreenType?): Boolean {
            return true
        }

    }

    val screenImpl: ScreenInterface = object : ScreenInterface {
        override fun isSupportMultiScreen(): Boolean {
            return false
        }

        override fun isSupportScreen(p0: DeviceScreenType?): Boolean {
            return false
        }

        override fun isCeilScreenOpen(): Boolean {
            return false
        }

        override fun openCeilScreen(p0: Int) {

        }

        override fun onCeilOpen(p0: Runnable?) {

        }

        override fun getDisplayId(p0: DeviceScreenType?): Int {
            return -1
        }

        override fun getUserHandle(p0: DeviceScreenType?): UserHandle? {
            return null
        }

        override fun getScreenContext(p0: DeviceScreenType?): Context? {
            return null
        }

        override fun getMainScreenDisplayId(): Int {
            return -1
        }

        override fun getPassengerScreenDisplayId(): Int {
            return -1
        }

        override fun getCeilingScreenDisplayId(): Int {
            return -1
        }

        override fun getCurVpaDisplayId(): Int {
            return -1
        }

        override fun getVoiceDisplayId(p0: Int): Int {
            return -1
        }

        override fun getScreenType(p0: String?): Int {
            return -1
        }

        override fun requestScreenOnOff(p0: Int, p1: Boolean) {

        }

        override fun isScreenOn(p0: Int): Boolean {
            return false
        }
    }

    val shareImpl: ShareInterface = object : ShareInterface {
        override fun getDlnaState(): Int {
            return 0
        }

        override fun openDlnaState(): Boolean {
            return true
        }

        override fun openShareApp(): Int {
            return 0
        }

        override fun closeDlna(): Int {
            return 0
        }

        override fun getConnectState(): Int {
            return 0
        }

        override fun isMirrorOpened(): Boolean {
            return true
        }

        override fun isDlnaVideoPlaying(): Int {
            return 0
        }

        override fun startDlnaVideo(): Int {
            return 0
        }

        override fun pauseDlnaVideo(): Int {
            return 0
        }

        override fun isDlnaOpened(): Boolean {
            return true
        }

        override fun isDlnaMainActivity(): Boolean {
            return true
        }

        override fun isFileTransferOpened(): Boolean {
            return true
        }

        override fun openFileTransfer(): Int {
            return 0
        }
    }

    override fun getVolume(): VolumeInterface {
        TODO("Not yet implemented")
    }

    override fun getUi(): UiInterface {
        TODO("Not yet implemented")
    }

    override fun getApp(): AppInterface {
        return appImpl
    }

    override fun getScreen(): ScreenInterface {
        return screenImpl
    }

    override fun getSplitScreen(): SplitScreenInterface {
        TODO("Not yet implemented")
    }

    override fun getAttribute(): AttributeInterface {
        TODO("Not yet implemented")
    }

    override fun getKeyboard(): KeyboardInterface {
        TODO("Not yet implemented")
    }

    override fun getShare(): ShareInterface {
        return shareImpl
    }
}