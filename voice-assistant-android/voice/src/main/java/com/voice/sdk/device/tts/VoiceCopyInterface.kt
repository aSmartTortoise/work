package com.voice.sdk.device.tts

/**
 * @Date 2025/3/6 11:33
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface VoiceCopyInterface {
    fun getTTSData(voiceId: String): String

    fun clearCacheAndReset(): Unit

    fun fetchOnce(): Unit
}