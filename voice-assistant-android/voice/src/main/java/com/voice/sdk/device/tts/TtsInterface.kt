package com.voice.sdk.device.tts

/**
 * @Date 2025/2/18 19:45
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface TtsInterface {

    fun stopById(originTtsId: String)

    fun shutUpOneSelf()
}