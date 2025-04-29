package com.voice.sdk.device.system

import java.util.HashMap

/**
 * @Date 2025/3/11 16:14
 * @Author 8327821
 * @Email *
 * @Description .
 **/
interface IAppProcessor {

    /**
     * 处理APP打开关闭事件 并返回一个TTS文本
     */
    fun process(map: HashMap<String, Any>): String

    fun consume(map: HashMap<String, Any>): Boolean
}