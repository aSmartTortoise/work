package com.voice.sdk.device.func

/**
 * @Date 2025/1/8 14:19
 * @Author 8327821
 * @Email *
 * @Description 函数槽位常量
 **/
object FuncConstants {

    //screen_name
    const val KEY_SCREEN_NAME = "screen_name"

    const val VALUE_SCREEN = "screen" //算法会下发 screen = screen 这个是没有意义的需要排除
    const val VALUE_SCREEN_CENTRAL = "central_screen"
    const val VALUE_SCREEN_PASSENGER = "passenger_screen"
    const val VALUE_SCREEN_CEIL = "ceil_screen"
    const val VALUE_SPECIFIED_SCREEN_ON = "value_specified_screen_on"
    const val VALUE_SPECIFIED_SCREEN_OFF = "value_specified_screen_off"

    //应用包名
    const val KEY_PKG_NAME = "package_name"

    //应用名称
    const val KEY_APP_NAME =  "app_name"

    //目标屏幕类型
    const val KEY_TARGET_SCREEN_TYPE = "target_screen"

    //抢占前在哪块屏幕上
    const val KEY_ORIGINAL_SCREEN = "original_screen"

    //指定屏幕类型
    const val KEY_SPECIFIED_SCREEN_TYPE = "specified_screen"


}