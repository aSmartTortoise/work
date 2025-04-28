package com.voyah.window.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.ViewType
import com.voyah.window.R

/**
 *  author : jie wang
 *  date : 2024/4/2 10:54
 *  description : 大模型卡片itemView decoration
 */
class DomainChatItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val left: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_72)
    }

    private val right: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_42)
    }

    private val top: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_32)
    }

    private val bottom: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_0)
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
                ViewType.CHAT_TYPE_MAIN -> {
                    it.left = left
                    it.right = right
                    it.top = top
                    it.bottom = bottom
                }
            }

        }
    }

}