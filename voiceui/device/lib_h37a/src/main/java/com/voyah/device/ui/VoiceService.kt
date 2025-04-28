package com.voyah.device.ui

import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.service.IVoiceService
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.WindowType

/**
 *  author : jie wang
 *  date : 2025/4/24 17:30
 *  description :
 */
object VoiceService : IVoiceService {

    override fun getWaveWindowTag(
        location: Int,
        passengerState: Boolean,
        ceilingState: Boolean
    ): String {
        return WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN
    }

    override fun getWaveX(
        location: Int,
        windowMargin: Int,
        passengerState: Boolean,
        ceilingState: Boolean,
        screenWidth: Int,
        windowWidth: Int
    ): Int {
        var x = windowMargin

        if (location == VoiceLocation.FRONT_RIGHT) {
            x = screenWidth - windowWidth - windowMargin
        } else if (location == VoiceLocation.REAR_LEFT ||
            (location == VoiceLocation.REAR_RIGHT)
        ) {
            x = (screenWidth - windowWidth) / 2
        }
        LogUtils.i("getWaveX x:$x")
        return x
    }

    override fun hasMultipleDisplay(): Boolean {
        return false
    }
}