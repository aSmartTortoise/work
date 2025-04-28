package com.voyah.window.windowholder

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.voyah.cockpit.window.model.LanguageType
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.window.R
import com.voyah.window.view.vpa.VpaState

/**
 *  author : jie wang
 *  date : 2024/3/26 11:31
 *  description : window holder的基类。
 */
abstract class BaseWindowHolder(val context: Context) {

    val screenWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        ScreenUtils.getScreenWidth()
    }
    val screenHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        ScreenUtils.getScreenHeight()
    }

    val statusBarHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(
            R.dimen.dp_88
        )
    }

    //todo 联系桌面刘恒老师导航栏高度先写死。
    val navigationBarHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        140
    }

    var voiceMode: Int = VoiceMode.VOICE_MODE_ONLINE
    var languageType: Int = LanguageType.MANDARIN
    var wakeVoiceLocation: Int = VoiceLocation.FRONT_RIGHT
    var currentNightModeFlag: Boolean = false

    var screenStateProvider: IScreenStateProvider? = null

    init {
        val screenWidth = getScreenWidthAsDisplayId(getDisplayId())
        LogUtils.i("init screenWidth:$screenWidth")
    }

    open fun isWindowShow(): Boolean = false

    abstract fun getDisplayId(): Int

    abstract fun onVoiceListening()

    abstract fun onVoiceSpeaking()

    abstract fun onVoiceExit()

    abstract fun getLayoutRes(): Int

    abstract fun showWindow()

    open fun dismissWindow() {
    }

    abstract fun setVoiceServiceMode(voiceMode: Int)

    abstract fun setVoiceLanguageType(languageType: Int)

    abstract fun onUIModeChange(nightModeFlag: Boolean)

    abstract fun setVoiceFocusLocation()

    fun getScreenWidthAsDisplayId(displayId: Int): Int {
        LogUtils.d("getScreenWidthAsDisplayId displayId:$displayId")
        val point = Point()
        val displayManager = context.getSystemService(DisplayManager::class.java) as DisplayManager
        val display = displayManager.getDisplay(displayId)
        if (display != null) {
            LogUtils.d("getScreenWidth display not null.")
            display.getRealSize(point)
        } else {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getRealSize(point)
        }
        val width = point.x
        LogUtils.d("screenWidth:$width")
        return width
    }

    fun getLocationParsed(locationType: Int): String {
        return when(locationType) {
            VoiceLocation.FRONT_LEFT -> "主驾"
            VoiceLocation.FRONT_RIGHT -> "副驾"
            VoiceLocation.REAR_LEFT -> "二排左"
            VoiceLocation.REAR_RIGHT -> "二排右"
            VoiceLocation.THIRD_ROW_LEFT -> "三排排左"
            VoiceLocation.THIRD_ROW_RIGHT -> "三排排右"
            else -> "未知位置"
        }
    }

}