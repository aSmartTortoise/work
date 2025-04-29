package com.voice.sdk.device.carservice.dc

import com.voice.sdk.device.carservice.vcar.IPropertyOperator
import java.util.HashMap

/**
 * @Date 2025/3/3 17:55
 * @Author 8327821
 * @Email *
 * @Description 氛围灯控制接口
 **/
interface AtmoInterface {

    // 氛围灯主题接口
    fun isSupportAtmosphereThemeColor(): Boolean
    fun isSupportCurSetThemeColor(themeColor :String): Boolean
    fun isCurThemeColorOpened(operator : IPropertyOperator, themeColor :String): Boolean
    fun setAtmosphereThemeColor(operator : IPropertyOperator, themeColor :String)
    fun setRandomAtmosphereThemeColor(operator : IPropertyOperator): String


    // 单色
    fun isSupportSingleColorMode(): Boolean
    fun isSupportStaticSingleColorMode(): Boolean
    fun isSupportFollowMusicSingleColorMode(): Boolean
    fun isCurSingleColorModeOpened(operator : IPropertyOperator): Boolean
    fun isCurStaticSingleColorModeOpened(operator : IPropertyOperator): Boolean
    fun isCurFollowMusicSingleColorModeOpened(operator : IPropertyOperator): Boolean
    fun setSingleColorMode(operator : IPropertyOperator)
    fun setStaticSingleColorMode(operator : IPropertyOperator)
    fun setFollowMusicSingleColorMode(operator : IPropertyOperator)


    // 多色
    fun isSupportMultiColorMode(): Boolean
    fun isSupportStaticMultiColorMode(): Boolean
    fun isSupportFollowMusicMultiColorMode(): Boolean
    fun isCurMultiColorModeOpened(operator : IPropertyOperator): Boolean
    fun isCurStaticMultiColorModeOpened(operator : IPropertyOperator): Boolean
    fun isCurFollowMusicMultiColorModeOpened(operator : IPropertyOperator): Boolean
    fun setMultiColorMode(operator : IPropertyOperator)
    fun setStaticMultiColorMode(operator : IPropertyOperator)
    fun setFollowMusicMultiColorMode(operator : IPropertyOperator)


    // 呼吸模式
    fun isSupportAtmosphereBreatheMode(): Boolean
    fun isAtmosphereBreatheModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereBreatheMode(operator : IPropertyOperator)


    // 常亮模式
    fun isSupportAtmosphereConstantLighMode(): Boolean
    fun isAtmosphereConstantLighModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereConstantLighMode(operator : IPropertyOperator)


    // 流动模式
    fun isSupportAtmosphereFlowMode(): Boolean
    fun isAtmosphereFlowModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereFlowMode(operator : IPropertyOperator)


    // 时光模式
    fun isSupportAtmosphereTimeMode(): Boolean
    fun isAtmosphereTimeModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereTimeMode(operator : IPropertyOperator)


    // 动态反馈模式
    fun isSupportAtmosphereDynamicFeedbackMode(): Boolean
    fun isAtmosphereDynamicFeedbackModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereDynamicFeedbackMode(operator : IPropertyOperator)


    // 动态模式
    fun isSupportAtmosphereDynamicMode(): Boolean
    fun isAtmosphereDynamicModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereDynamicMode(operator : IPropertyOperator)


    // 跟随驾驶模式
    fun isSupportAtmosphereFollowDriverMode(): Boolean
    fun isAtmosphereFollowDriverModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereFollowDriverMode(operator : IPropertyOperator)


    // 静态模式
    fun isSupportAtmosphereStaticMode(): Boolean
    fun isAtmosphereStaticModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereStaticMode(operator : IPropertyOperator)


    // 主题模式
    fun isSupportAtmosphereThemeMode(): Boolean
    fun isAtmosphereThemeModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereThemeMode(operator : IPropertyOperator)

    // 自定义模式
    fun isSupportAtmosphereCustomMode(): Boolean
    fun isAtmosphereCustomModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereCustomMode(operator : IPropertyOperator)


    // 中置扬声器氛围灯开关
    fun isSupportAtmosphereCenterSpeakerSwitch(): Boolean
    fun isAtmosphereCenterSpeakerSwitchOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereCenterSpeakerSwitchState(operator : IPropertyOperator, onOff :Boolean)


    // 音乐律动开关 and 模式
    fun isSupportAtmosphereFollowMusicModeOrSwitch(): Boolean
    fun isAtmosphereFollowMusicModeAdjust(): Boolean
    fun isAtmosphereFollowMusicModeOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereFollowMusicModeState(operator : IPropertyOperator)
    fun isAtmosphereFollowMusicSwitchOpened(operator : IPropertyOperator): Boolean
    fun setAtmosphereFollowMusicSwitchState(operator : IPropertyOperator, onOff :Boolean)


    // 氛围灯模式模糊调节
    fun setAtmosphereModeChange(operator : IPropertyOperator, map : HashMap<String, Object>)
    // 氛围灯颜色模糊调节
    fun setAtmosphereColorChange(operator : IPropertyOperator, map : HashMap<String, Object>)
    // 静态氛围灯颜色模糊调节
    fun setAtmosphereStaticColorChange(operator : IPropertyOperator, map : HashMap<String, Object>)
    // 音乐律动氛围灯颜色模糊调节
    fun setAtmosphereFollowMusicColorChange(operator : IPropertyOperator, map : HashMap<String, Object>)
    // 动态氛围灯颜色模糊调节
    fun setAtmosphereDynamicColorChange(operator : IPropertyOperator, map : HashMap<String, Object>)
    fun isAtmosphereColorScheme(color: String): Boolean
    fun isSupportCurColor(color: String): Boolean
    fun isAtmosphereCurColorOpened(operator: IPropertyOperator, color: String): Boolean
    fun isAtmosphereCurColorSchemeOpened(operator: IPropertyOperator, color: String): Boolean
    fun setAtmosphereCurColorMode(operator: IPropertyOperator)
    fun setAtmosphereCurColorSchemeMode(operator: IPropertyOperator)
    fun setAtmosphereColor(operator: IPropertyOperator, color: String)
    fun setAtmosphereColorScheme(operator: IPropertyOperator, color: String)
    fun isAtmosphereCurStaticColorOpened(operator: IPropertyOperator, color: String): Boolean
    fun isAtmosphereCurStaticColorSchemeOpened(operator: IPropertyOperator, color: String): Boolean
    fun setAtmosphereCurStaticColorMode(operator: IPropertyOperator)
    fun setAtmosphereCurStaticColorSchemeMode(operator: IPropertyOperator)
    fun setAtmosphereStaticColor(operator: IPropertyOperator, color: String)
    fun setAtmosphereStaticColorScheme(operator: IPropertyOperator,  color: String)
    fun isAtmosphereCurFollowMusicColorOpened(operator: IPropertyOperator, color: String): Boolean
    fun isAtmosphereCurFollowMusicColorSchemeOpened(operator: IPropertyOperator, color: String): Boolean
    fun setAtmosphereCurFollowMusicColorMode(operator: IPropertyOperator)
    fun setAtmosphereCurFollowMusicColorSchemeMode(operator: IPropertyOperator)
    fun setAtmosphereFollowMusicColor(operator: IPropertyOperator, color: String)
    fun setAtmosphereFollowMusicColorScheme(operator: IPropertyOperator,  color: String)
    fun isSupportStaticColorScheme(): Boolean
    fun isSupportFollowMusicColor(): Boolean
    fun isSupportColorSchemeAdjust(): Boolean


    fun isSupportFollowMusicBrightnessAdjust(): Boolean
    fun isSupportStaticBrightnessAdjust(): Boolean
    fun curAtmosphereBrightness(operator: IPropertyOperator, atmosphereMode: String): Int
    fun setAtmosphereBrightness(operator: IPropertyOperator, atmosphereMode: String, number: Int)
    fun openAtmosphereStaticBrightnessTab(operator: IPropertyOperator)
    fun openAtmosphereFollowMusicBrightnessTab(operator: IPropertyOperator)
    fun setAtmosphereBrightnessMin(operator: IPropertyOperator, atmosphereMode: String)
    fun setAtmosphereBrightnessMax(operator: IPropertyOperator, atmosphereMode: String)

    fun isSupportAtmosphereEffectMode(): Boolean
}