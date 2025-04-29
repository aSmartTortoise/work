package com.voyah.voice.main.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.voyah.voice.main.R

/**
 *  author : jie wang
 *  date : 2024/4/19 15:00
 *  description :
 */
class DrawingGoodsItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private var dividerPaint = Paint().apply {
        isAntiAlias = true
        setColor(context.resources.getColor(R.color.gray_14FFFFFF))
    }

    private val left by lazy {
        ConvertUtils.dp2px(49.5f)
    }

    private val right by lazy {
        ConvertUtils.dp2px(49.5f)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = parent.getChildAdapterPosition(view)
        val count = parent.adapter!!.itemCount

//        outRect.let {
//            it.top = 1
//            it.left = this@DrawingGoodsItemDecoration.left
//            it.right = this@DrawingGoodsItemDecoration.right
//        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)

            val index = parent.getChildAdapterPosition(view)
            //第一个ItemView不需要绘制
            if (index == 0) {
                continue
            }

            val dividerTop: Float = view.top - 1.0f
            val dividerLeft = left.toFloat()
            val dividerBottom = view.top.toFloat()
            val dividerRight = (parent.width - right).toFloat()

            c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, dividerPaint)
        }
    }
}