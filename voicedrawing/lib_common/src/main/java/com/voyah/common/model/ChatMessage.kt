package com.voyah.common.model

import com.voyah.voice.framework.model.MultiItemModel

/**
 *  author : jie wang
 *  date : 2024/4/19 11:32
 *  description :
 */
data class ChatMessage(var text: String, val chatRole: ChatRole) : MultiItemModel {
    override var itemType: Int = ItemViewType.CHAT_MESSAGE_ASSISTANT.viewType
}
