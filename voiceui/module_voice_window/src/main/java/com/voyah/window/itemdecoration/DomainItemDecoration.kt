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
 *  description : 卡片itemView decoration
 */
class DomainItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val dp24: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_24)
    }

    private val dp40: Lazy<Int> = lazy {
        context.resources.getDimensionPixelSize(R.dimen.dp_40)
    }

    private val radius: Lazy<Int> = lazy {
        context.resources.getDimensionPixelSize(R.dimen.dp_9)
    }

    private val dp44: Lazy<Int> = lazy {
        context.resources.getDimensionPixelSize(R.dimen.dp_44)
    }

    private val lineWidth: Lazy<Int> = lazy {
        context.resources.getDimensionPixelSize(R.dimen.dp_2)
    }

    private val left: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_48)
    }

    private val right: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_30)
    }

    private val top: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_32)
    }

    private val bottom: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_48)
    }

    private var dotPaint: Paint? = null
    private var linePaint: Paint? = null

    var drawTimeLineFlag: Boolean = false

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

//        LogUtils.d("getItemOffsets: adapterPosition:$adapterPosition, viewType:$viewType")
        outRect.let {

            when (viewType) {
                ViewType.WEATHER_TYPE_1 -> {
                    it.left = left
                    it.right = right
                    it.top = dp24
                    it.bottom = bottom
                }

                ViewType.WEATHER_TYPE_2 -> {
                    it.left = left
                    it.right = right
                    it.top = top
                    it.bottom = top
                }

                ViewType.WEATHER_TYPE_3 -> {
                    it.left = left
                    it.right = right
                    when (adapterPosition) {
                        1 -> {
                            it.top = 0
                            it.bottom = dp24 / 2
                        }

                        count - 1 -> {
                            it.top = dp24 / 2
                            it.bottom = bottom
                        }

                        else -> {
                            it.top = dp24 / 2
                            it.bottom = dp24 / 2
                        }
                    }
                }

                ViewType.SCHEDULE_TYPE_1 -> {
                    it.left = left
                    it.right = right
                    it.top = top
                    it.bottom = bottom
                }

                ViewType.SCHEDULE_TYPE_2,
                ViewType.SCHEDULE_TYPE_3 -> {
                    it.left = dp40.value
                    it.right = dp40.value

                    when (adapterPosition) {
                        0 -> {
                            it.top = dp24 + dp40.value
                            it.bottom = dp24
                        }
                        count - 1 -> {
                            it.top = dp24
                            it.bottom = dp24 + dp40.value
                        }
                        else -> {
                            it.top = dp24
                            it.bottom = dp24
                        }
                    }
                }

                ViewType.SCHEDULE_TYPE_MORE -> {
                    it.left = dp40.value
                    it.right = dp40.value
                    it.top = dp24
                    it.bottom = dp40.value
                }

                ViewType.STOCK_TYPE -> {
                    it.left = left
                    it.right = right
                    it.top = top
                    it.bottom = bottom
                }

                ViewType.BT_PHONE_TYPE -> {
                    it.left = left
                    it.right = right

                    when (adapterPosition) {
                        0 -> {
                            it.top = top
                            it.bottom = 0
                        }
                        count - 1 -> {
                            it.top = 0
                            it.bottom = bottom
                        }
                        else -> {
                            it.top = 0
                            it.bottom = 0
                        }
                    }
                }
            }

        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        if (!drawTimeLineFlag) return
        val adapter = parent.adapter
        val itemCount = parent.childCount
        if (itemCount <= 0) return
        if (adapter == null) return

        for (index in 0 until itemCount) {
            val child = parent.getChildAt(index)
            child?.let { child ->
                val params = child.layoutParams as RecyclerView.LayoutParams
                val pos = params.viewAdapterPosition
                LogUtils.d("onDraw pos:$pos, childTop:${child.top}, childLeft:${child.left}," +
                        " childRight:${child.right}, childBottom:${child.bottom}")

                val viewType = adapter.getItemViewType(pos)

                if (child is ViewGroup
                    && (viewType == ViewType.SCHEDULE_TYPE_2 || viewType == ViewType.SCHEDULE_TYPE_3)) {
                    val tvTime = child.findViewById<TextView>(R.id.tv_time)
                    val clLayout = child.findViewById<ViewGroup>(R.id.cl)
                    LogUtils.d("onDraw tvTime right:${tvTime.right}")
                    LogUtils.d("onDraw clLayout left:${clLayout.left}")
                    val centerX: Int = parent.paddingLeft + dp40.value + (clLayout.left + tvTime.right) / 2
                    val centerY = child.top + dp44.value + radius.value

                    if (dotPaint == null) {
                        dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#ccFFFFFF")
                        }
                    }

                    // 绘点
                    canvas.drawCircle(
                        centerX.toFloat(),
                        centerY.toFloat(),
                        radius.value.toFloat(),
                        dotPaint!!)

                    if (linePaint == null) {
                        linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#1fFFFFFF")
                            strokeWidth = lineWidth.value.toFloat()
                        }
                    }

                    /**
                     * 绘制上半轴线
                     */
                    val upLineStartX = centerX
                    val upLineStartY = child.getTop() - dp24
                    val upLineStopX = centerX
                    val upLineStopY: Int = centerY - radius.value

                    if (pos > 0) {
                        canvas.drawLine(
                            upLineStartX.toFloat(),
                            upLineStartY.toFloat(),
                            upLineStopX.toFloat(),
                            upLineStopY.toFloat(),
                            linePaint!!
                        )
                    }

                    /**
                     * 绘制下半轴线
                     */
                    val bottomLineStartX = centerX
                    val bottomLineStartY: Int = centerY + radius.value
                    val bottomLineStopX = centerX
                    val bottomLineStopY = child.getBottom() + dp24

                    if (pos < adapter.itemCount - 1) {
                        val nextViewType = adapter.getItemViewType(pos + 1)
                        if (nextViewType == ViewType.SCHEDULE_TYPE_2
                            || nextViewType == ViewType.SCHEDULE_TYPE_3) {
                            canvas.drawLine(
                                bottomLineStartX.toFloat(),
                                bottomLineStartY.toFloat(),
                                bottomLineStopX.toFloat(),
                                bottomLineStopY.toFloat(),
                                linePaint!!
                            )
                        }
                    }
                }
            }
        }
    }
}