package com.voice.sdk.device.ui

/**
 * @Date 2025/3/8 16:05
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface IThreadDelay {
    fun init()
    fun  addDelayRunnable(runnable: Runnable, delay: Long)

    fun  removeDelayRunnable(runnable: Runnable)
}