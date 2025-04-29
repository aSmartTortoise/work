package com.voyah.voice.main.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.main.R
import com.voyah.common.model.ItemViewType

/**
 *  author : jie wang
 *  date : 2024/4/19 15:00
 *  description :
 */
class ChatMessageItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private val dp24: Lazy<Int> = lazy {
        context.resources.getDimensionPixelSize(R.dimen.dp_24)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = parent.getChildAdapterPosition(view)
        val viewType = parent.adapter!!.getItemViewType(adapterPosition)
        val count = parent.adapter!!.itemCount

        LogUtils.d("getItemOffsets: adapterPosition:$adapterPosition, viewType:$viewType")

        outRect.let {

            when (viewType) {
                ItemViewType.CHAT_MESSAGE_USER.viewType -> {
                    it.left = dp24.value
                    it.right = 0
                }

                ItemViewType.CHAT_MESSAGE_ASSISTANT.viewType -> {
                    it.left = 0
                    it.right = dp24.value
                }
            }

        }
    }
}