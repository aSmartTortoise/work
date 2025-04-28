package com.lib.common.voyah.service

/**
 *  author : jie wang
 *  date : 2025/4/24 17:00
 *  description :
 */
interface IVoiceService {
    fun getWaveWindowTag(
        location: Int,
        passengerState: Boolean,
        ceilingState: Boolean
    ): String

    fun getWaveX(
        location: Int,
        windowMargin: Int,
        passengerState: Boolean,
        ceilingState: Boolean,
        screenWidth: Int,
        windowWidth: Int
    ): Int

    fun hasMultipleDisplay(): Boolean
}