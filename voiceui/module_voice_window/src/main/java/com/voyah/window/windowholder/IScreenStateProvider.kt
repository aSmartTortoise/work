package com.voyah.window.windowholder

import com.voyah.cockpit.window.model.LanguageType
import com.voyah.cockpit.window.model.ScreenType
import com.voyah.cockpit.window.model.VoiceMode

/**
 * @Date 2025/4/24 14:16
 * @Author 8327821
 * @Email *
 * @Description 用于各windowHolder 获取个屏幕状态
 **/
interface IScreenStateProvider {

    fun getScreenState(@ScreenType screen: Int): Int

    @VoiceMode fun getVoiceMode(): Int

    @LanguageType fun getLanguageType(): Int
}