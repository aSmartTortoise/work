package com.voyah.window.manager

import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.ScreenType
import com.voyah.window.model.CardDataWrapper

/**
 * @Date 2025/4/22 17:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object CardDataRepo {

    private val cardData = mutableSetOf<CardDataWrapper>()

    fun fetchCardData(@ScreenType screen: Int): CardDataWrapper? {
        //先不考虑多个结果，原则上不存在
        return cardData.find {
            it.targetScreen == screen
        }
    }

    fun move(@ScreenType fromScreen: Int, @ScreenType targetScreen: Int) {
        if (fromScreen == targetScreen) {
            return
        }
        cardData.filter {
            it.originScreen == fromScreen
        }.forEach {
            it.moveTo(targetScreen)
        }
    }

    //当某个位置可用之后，遍历所有的卡片数据，找到原本在这个位置的卡片，恢复其位置
    fun rollback(@ScreenType screen: Int) {
        cardData.forEach {
            if (it.originScreen == screen) {
                it.moveTo(it.originScreen)
            }
        }
    }

    fun add(dataWrapper: CardDataWrapper) {
        cardData.removeIf {
            it.targetScreen == dataWrapper.targetScreen
        }
        cardData.add(dataWrapper)
    }

    fun remove(@ScreenType screen: Int) {
        cardData.removeIf {
            it.targetScreen == screen
        }
    }

    //根据sessionId移除
    fun remove(sessionId: String) {
        cardData.removeIf {
            it.sessionId == sessionId
        }
    }

    fun appendFrame(
        cardInfo: CardInfo,
        @ScreenType targetScreen: Int
    ) {
        cardData.find {
            it.sessionId == cardInfo.sessionId
        }?.apply {
            val oldMessage = this.cardInfo.chatMessages?.get(0)
            cardInfo.chatMessages[0].let {
                it.content = oldMessage?.content + it.content
                it.noTaskReasonText = oldMessage?.noTaskReasonText + (it.noTaskReasonText ?: "")
                this.cardInfo.chatMessages[0] = it
            }
            this.targetScreen = targetScreen
        } ?: run {
            LogUtils.e("appendFrame", "sessionId not found")
        }
    }

}