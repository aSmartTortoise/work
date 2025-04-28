package com.voyah.window.windowholder

import android.content.Context
import com.lzf.easyfloat.utils.DisplayUtils
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.window.R

/**
 *  author : jie wang
 *  date : 2024/3/26 11:31
 *  description : window holder的基类。
 */
abstract class BaseWindowHolder(val context: Context) {

    val screenWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        DisplayUtils.getScreenWidth(context)
    }
    val screenHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        DisplayUtils.getScreenHeight(context)
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
    var wakeVoiceLocation: Int = VoiceLocation.FRONT_RIGHT

    abstract fun isWindowShow(): Boolean

    abstract fun onVoiceAwake(voiceServiceMode: Int, voiceLocation: Int)

    abstract fun onVoiceListening()

    abstract fun onVoiceSpeaking()

    abstract fun onVoiceExit()

    abstract fun getLayoutRes(): Int

    abstract fun showWindow()

    open fun dismissWindow() {
    }
}