package com.voice.sdk.device.tts

/**
 * @Date 2025/3/6 11:07
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface HeadSetInterface {

    fun registerHeadSetObserver(callback : ((Boolean) -> Unit))

    fun unRegisterHeadSetObserver()
}