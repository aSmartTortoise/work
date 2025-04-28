package com.voyah.device.ui

import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.service.IVoiceService
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.WindowType

/**
 *  author : jie wang
 *  date : 2025/4/24 17:15
 *  description :
 */
object VoiceService : IVoiceService {

    override fun getWaveWindowTag(
        location: Int,
        passengerState: Boolean,
        ceilingState: Boolean
    ): String {
        LogUtils.i("getWindowTag location:$location")
        var windowTag = WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN

        if (location == VoiceLocation.FRONT_LEFT) {
            windowTag = WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN
        } else if (location == VoiceLocation.FRONT_RIGHT) {
            windowTag = if (passengerState) {
                WindowType.WINDOW_TYPE_VOICE_WAVE_PASSENGER_SCREEN
            } else {
                WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN
            }
        } else {
            LogUtils.i("getWindowTag ceilingState:$ceilingState")
            windowTag = if (ceilingState && location >= VoiceLocation.REAR_LEFT) {
                WindowType.WINDOW_TYPE_VOICE_WAVE_CEILING_SCREEN
            } else {
                WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN
            }
        }
        LogUtils.i("getWindowTag windowTag:$windowTag")
        return windowTag
    }

    override fun getWaveX(
        location: Int,
        windowMargin: Int,
        passengerState: Boolean,
        ceilingState: Boolean,
        screenWidth: Int,
        windowWidth: Int
    ): Int {
        LogUtils.i("getWaveX location:$location")
        var x = windowMargin

        if (location == VoiceLocation.FRONT_RIGHT && !passengerState) {
            x = screenWidth - windowWidth - windowMargin
        }

        if (location >= VoiceLocation.REAR_LEFT) {
            LogUtils.d("getWaveX ceilingScreenEnable:$ceilingState")
            if (ceilingState) {
                if (location % 2 == 1) {
                    //320 吸顶屏比正常屏幕宽320px
                    x = screenWidth - windowWidth - windowMargin + 320
                }
            } else {
                x = (screenWidth - windowWidth) / 2
            }
        }

        LogUtils.i("getWaveX x:$x")
        return x
    }

    override fun hasMultipleDisplay(): Boolean {
        return true
    }
}