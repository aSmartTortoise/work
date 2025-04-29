package com.voyah.ai.logic

import android.util.Log2
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.appstore.AppStoreInterface
import com.voice.sdk.device.func.FuncConstants
import com.voice.sdk.device.system.AbstractAppProcessor
import com.voice.sdk.device.system.DeviceScreenType
import com.voice.sdk.tts.TtsBeanUtils
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext

/**
 * @Date 2025/3/12 17:00
 * @Author 8327821
 * @Email *
 * @Description .
 **/
const val TAG = "CommonAppCtrlImpl"
class CommonAppCtrlImpl : AbstractAppProcessor() {

    var switchType: String = ""
    var pkgName: String = ""
    var appName: String = ""
    var soundScreen: String = "" //声源位置对应屏幕
    var nluScreen: String = "" //nlu对应屏幕
    var actScreen: String = "" //实际执行位置

    val appStoreInterface: AppStoreInterface = DeviceHolder.INS().devices.appStore
    /**
     * 处理APP关闭事件 并返回一个TTS文本
     */


    override fun consume(map: HashMap<String, Any>): Boolean {
        return false
    }
    override fun process(map: HashMap<String, Any>): String {
        switchType = getOneMapValue("switch_type", map)
        appName = getOneMapValue("app_name", map)
        if (switchType == "open") {
            return processOpen(map)
        } else if (switchType == "close") {
            return processClose(map)
        }
        //兜底回复，理论只有打开关闭两个场景
        return TtsBeanUtils.getTtsBean(1100027).tts
    }

    private fun processOpen(map: HashMap<String, Any>): String {
        Log2.d(TAG, "processOpen")
        pkgName = DeviceHolder.INS().devices.system.app.getPackageName(appName);

        if (isSecondRound(map)) {
            Log2.d(TAG, "second round")
            if (!isSecondConfirm(map)) {
                //多轮取消
                return TtsBeanUtils.getTtsBean(1100036).tts
            } else {
                //多轮确认
                actScreen = FuncConstants.VALUE_SCREEN_CEIL
                return open(pkgName, actScreen)
            }
        }

        //函数指定位置
        if (nluContainsPos(map)) {
            Log2.d(TAG, "nluContainsPos")
            //车辆不支持
            if (!isCarSupportScreen(nluScreen)) {
                Log2.d(TAG, "car cannot support screen $nluScreen")
                return TtsBeanUtils.getTtsBean(1100028).tts
            }
            //应用未安装
            if (!appInterface.isInstalledByPackageName(pkgName)) {
                Log2.d(TAG, "app not installed $pkgName")
                if (appStoreInterface.isInAppStore(appName)) {
                    Log2.d(TAG, "app in app store $appName")
                    appStoreInterface.searchApp(DeviceScreenType.fromValue(nluScreen), appName)
                    return TtsBeanUtils.getTtsBean(4044801).tts
                } else {
                    Log2.d(TAG, "app not in app store $appName")
                    return TtsBeanUtils.getTtsBean(4044002).tts
                }
            }
            //应用不支持
            if (!isAppSupportScreen(pkgName, nluScreen)) {
                Log2.d(TAG, "app not support screen $pkgName $nluScreen")
                return TtsBeanUtils.getTtsBean(1100034).tts
            }

            if (isFront(pkgName, nluScreen)) {
                //已经是前台
                Log2.d(TAG, "app is already front $pkgName $nluScreen")
                return TtsBeanUtils.getTtsBean(1100029).tts
            }

            actScreen = nluScreen
            return preProcess(map)
        } else {
            //函数不带位置

            //应用未安装
            if (!appInterface.isInstalledByPackageName(pkgName)) {
                Log2.d(TAG, "app not installed $pkgName")
                if (appStoreInterface.isInAppStore(appName)) {
                    Log2.d(TAG, "app in app store $appName")
                    appStoreInterface.searchApp(DeviceScreenType.fromValue(nluScreen), appName)
                    return TtsBeanUtils.getTtsBean(4044801).tts
                } else {
                    Log2.d(TAG, "app not in app store $appName")
                    return TtsBeanUtils.getTtsBean(4044002).tts
                }
            }

            soundScreen = getScreenFromSound(getSoundSourcePos(map))
            if (isAppSupportScreen(pkgName, soundScreen)) {
                if (isFront(pkgName, soundScreen)) {
                    //已经是前台
                    return TtsBeanUtils.getTtsBean(1100001).tts
                } else {
                    actScreen = soundScreen
                    return preProcess(map)
                }
            } else {
                //声源位置不支持打开
                if (existFront(pkgName)) {
                    return TtsBeanUtils.getTtsBean(1100029).tts
                } else {
                    actScreen = DeviceHolder.INS().devices.getSystem().getApp().fetchScreen(pkgName)
                    return preProcess(map)
                }
            }
        }
    }

    private fun preProcess(map: HashMap<String, Any>): String {
        if (actScreen == FuncConstants.VALUE_SCREEN_CEIL) {
            if (!isCarSupportScreen(actScreen)) {
                return TtsBeanUtils.getTtsBean(1100028).tts
            }
            if (isCeilingOpen) {
                return showToastAndOpen(pkgName, actScreen)
            } else {
                //吸顶屏未开启
                if (nluContainsPos(map)) {
                    return showToastAndOpen(pkgName, actScreen)
                } else {
                    //设置场景值
                    map[FlowChatContext.FlowChatResultKey.RESULT_CODE] = 20000
                    map[FlowChatContext.FlowChatResultKey.RESULT_SCENE] = "State.DC_OP_CONFIRM"
                    return TtsBeanUtils.getTtsBean(1100033, appName).tts
                }
            }
        } else {
            return showToastAndOpen(pkgName, actScreen)
        }
    }

    private fun processClose(map: HashMap<String, Any>): String {
        return ""
    }

    private fun open(pkgName: String, screen: String): String {
        if (screen == FuncConstants.VALUE_SCREEN_CEIL && !isCeilingOpen) {
            DeviceHolder.INS().devices.system.screen.onCeilOpen {
                appInterface.openApp(pkgName, DeviceScreenType.CEIL_SCREEN)
                val tts = if (nluScreen.isNotBlank()) {
                    //xxx屏xxx打开了
                    TtsBeanUtils.getTtsBean(1100030).tts
                } else {
                    //xxx打开了
                    TtsBeanUtils.getTtsBean(1100002).tts
                }
                DeviceHolder.INS().devices.tts.speak(tts)
            }
            //正在展开吸顶屏
            return TtsBeanUtils.getTtsBean(2098008).tts
        }
        DeviceHolder.INS().devices.system.app.openApp(pkgName, DeviceScreenType.fromValue(screen))
        return if (nluScreen.isNotBlank()) {
            //xxx屏xxx打开了
            TtsBeanUtils.getTtsBean(1100030).tts
        } else {
            //xxx打开了
            TtsBeanUtils.getTtsBean(1100002).tts
        }
    }

    private fun showToastAndOpen(pkgName: String, screen: String): String {
        showToast(pkgName, screen)
        return open(pkgName, screen)
    }

    private fun showToast(pkgName: String, screen: String) {

        if (!isPreemptiveApp(pkgName)) {
            return
        }

        var needShow = false
        var originalScreen = FuncConstants.VALUE_SCREEN_CENTRAL

        if (isFront(pkgName, FuncConstants.VALUE_SCREEN_CENTRAL) && screen != FuncConstants.VALUE_SCREEN_CENTRAL) {
            originalScreen = FuncConstants.VALUE_SCREEN_CENTRAL
            needShow = true
        } else if (isFront(pkgName, FuncConstants.VALUE_SCREEN_PASSENGER) && screen != FuncConstants.VALUE_SCREEN_PASSENGER) {
            originalScreen = FuncConstants.VALUE_SCREEN_PASSENGER
            needShow = true
        } else if (isFront(pkgName, FuncConstants.VALUE_SCREEN_CEIL) && screen!= FuncConstants.VALUE_SCREEN_CEIL) {
            originalScreen = FuncConstants.VALUE_SCREEN_CEIL
            needShow = true
        }

        if (originalScreen == FuncConstants.VALUE_SCREEN_CENTRAL || screen == FuncConstants.VALUE_SCREEN_CENTRAL) {
            if (DeviceHolder.INS().devices.system.splitScreen.isSplitScreening()) {
                //中控分屏不显示toast
                needShow = false
            }
        }

        if (!needShow) { return }

        val toast = "${appName}已在${screen}打开"
        DeviceHolder.INS().devices.system.ui.showSystemToast(
            DeviceScreenType.fromValue(
                originalScreen
            ), toast
        )
    }

    private fun nluContainsPos(map: HashMap<String, Any>): Boolean {
        val nluPos = getOneMapValue("position", map)
        nluScreen = getOneMapValue(FuncConstants.KEY_SCREEN_NAME, map)

        nluScreen.takeIf { it.isNotBlank() }?.let {
            nluScreen = it
            return true
        }

        nluPos.takeIf { it.isNotBlank() }?.let {
            nluScreen = getScreenTypeByNluPos(it)
            return true
        }
        return false
    }

    private fun existFront(pkgName: String): Boolean {
        return appInterface.isAppForeGround(pkgName, DeviceScreenType.CENTRAL_SCREEN)
                || appInterface.isAppForeGround(pkgName, DeviceScreenType.PASSENGER_SCREEN)
                || appInterface.isAppForeGround(pkgName, DeviceScreenType.CEIL_SCREEN)
    }


    fun getScreenTypeByNluPos(position: String?): String {
        return when (position) {
            "first_row_left" -> FuncConstants.VALUE_SCREEN_CENTRAL
            "first_row_right" -> FuncConstants.VALUE_SCREEN_PASSENGER
            "rear_side",
            "rear_side_left",
            "rear_side_mid",
            "rear_side_right",
            "second_side",
            "second_row_right",
            "second_row_left",
            "second_row_mid",
            "third_side",
            "third_row_left",
            "third_row_right",
            "third_row_mid" -> FuncConstants.VALUE_SCREEN_CEIL
            else -> FuncConstants.VALUE_SCREEN_CENTRAL
        }
    }
}