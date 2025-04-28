package com.voyah.window.model

import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.ScreenType

/**
 * @Date 2025/4/22 17:26
 * @Author 8327821
 * @Email *
 * @Description .
 **/
data class CardDataWrapper(
    var cardInfo: CardInfo,
    @ScreenType val originScreen: Int = cardInfo.screenType,
    @ScreenType var targetScreen: Int = originScreen,
    val sessionId : String = cardInfo.sessionId,
) {

    fun moveTo(@ScreenType targetScreen: Int) {
        this.targetScreen = targetScreen
    }
}
