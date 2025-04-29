package com.voyah.voice.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.main.databinding.ItemChatMessageAssistantBinding
import com.voyah.voice.main.databinding.ItemChatMessageUserBinding
import com.voyah.common.model.ChatMessage
import com.voyah.common.model.ItemViewType
import com.voyah.markdown.parser.markwon.DetailsParsingSpan
import com.voyah.markdown.parser.markwon.MarkwonHolder
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseMultiItemAdapter

/**
 *  author : jie wang
 *  date : 2024/4/19 13:42
 *  description : 聊天item adapter。
 */
class ChatMessageAdapter : BaseMultiItemAdapter<ChatMessage>() {

    companion object {
        const val TAG = "ChatMessageAdapter"
    }

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ViewBinding>,
        item: ChatMessage?,
        position: Int
    ) {
        if (item == null) return
        when (item.itemType) {
            ItemViewType.CHAT_MESSAGE_USER.viewType -> {
                (holder.binding as ItemChatMessageUserBinding).apply {
                    tvChat.text = item.text
                }
                holder.let {
                    var layoutParams = it.itemView.layoutParams as RecyclerView.LayoutParams

                }
            }

            ItemViewType.CHAT_MESSAGE_ASSISTANT.viewType -> {
                (holder.binding as ItemChatMessageAssistantBinding).apply {
//                    tvChat.text = item.text

                    LogUtils.d("onBindDefViewHolder render")
                    render(item.text, tvChat)
                }
            }
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            ItemViewType.CHAT_MESSAGE_USER.viewType ->
                ItemChatMessageUserBinding.inflate(layoutInflater, parent, false)

            ItemViewType.CHAT_MESSAGE_ASSISTANT.viewType ->
                ItemChatMessageAssistantBinding.inflate(layoutInflater, parent, false)

            else -> throw IllegalStateException("not support view type.")
        }
    }

    private fun render(md: String, tv: TextView) {
        val markwon = MarkwonHolder.getInstance().markwon
        val spanned = markwon.toMarkdown(md)
        val spans: Array<DetailsParsingSpan> = spanned.getSpans(
            0, spanned.length,
            DetailsParsingSpan::class.java
        )

        // if we have no details, proceed as usual (single text-view)
        if (spans.isEmpty()) {
            LogUtils.d("render spans is empty.")
            // no details
//            val textView: TextView = appendTextView()
            markwon.setParsedMarkdown(tv, spanned)
            return
        }
        LogUtils.d("render spans not empty.")
    }
}