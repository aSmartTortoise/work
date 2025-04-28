package com.voyah.window.presenter

import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.ChatMessage
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.voice.framework.ext.normalScope
import com.voyah.window.cardholder.DomainCardHolder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.TimerTask

/**
 *  author : jie wang
 *  date : 2024/11/14 19:57
 *  description : 处理大模型卡片流式上屏 请求中到响应开始这段时间内的 提示
 */
class CardTimerTask(private val cardInfo: CardInfo, private val cardHolder: DomainCardHolder) :
    TimerTask() {

    val message = cardInfo.chatMessages[0].copy()

    private var timeNum = 0
    var content = ""
    private val normalScope = normalScope()

    var bindDataBlock: ((messages: List<ChatMessage>) -> Unit)? = null

    override fun run() {
        timeNum++

        when (timeNum % 3) {
            0 -> {
                content = "生成中 ..."
            }

            1 -> {
                content = "生成中 ."
            }

            2 -> {
                content = "生成中 .."
            }
        }

        message.content = content

        if (timeNum == 1) {
            normalScope.launch {
                LogUtils.d("on bind chat data, waiting tip...")
                bindDataBlock?.invoke(cardInfo.chatMessages)
            }

        } else {
            normalScope.launch {
                cardHolder.notifyDataSetChange(message)
            }
        }
    }

    fun cancelTask() {
        LogUtils.d("cancelTask")
        cancel()
        normalScope.cancel()
    }

}