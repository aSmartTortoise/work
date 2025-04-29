package com.voice.sdk.device.carservice.dc.carsetting

/**
 * @Date 2025/3/3 10:22
 * @Author 8327821
 * @Email *
 * @Description 设置应用提供的打开页面和判断前台的接口能力
 **/
interface SettingInterface {

    fun exec(action: String)

    fun isCurrentState(action: String): Boolean

    fun getCurrentState(action: String): String
}