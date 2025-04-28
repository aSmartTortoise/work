package com.voyah.voice.card.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.voyah.voice.card.R

class VerticalDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mIndicatorColor: Int = context.resources.getColor(R.color.indicatorColor)
    private var mIndicatorInactiveColor: Int = context.resources.getColor(R.color.indicatorInactiveColor)
    private var mIndicatorRadius: Float = context.resources.getDimensionPixelSize(R.dimen.dp_6).toFloat()
    private var mIndicatorSpacing: Float = context.resources.getDimensionPixelSize(R.dimen.dp_24).toFloat()
    private var mIndicatorPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = mIndicatorColor
    }
    private var mDotsCount: Int = 0
    private var mCurrentPage: Int = 0

    fun setupWithViewPager2(viewPager2: ViewPager2) {
        mDotsCount = viewPager2.adapter?.itemCount ?: 0
        invalidate()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mCurrentPage = position
                invalidate()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = getTotalHeight()
        val width = (mIndicatorRadius * 2).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until mDotsCount) {
            val y = i * (2 * mIndicatorRadius + mIndicatorSpacing) + mIndicatorRadius
            mIndicatorPaint.color = if (i == mCurrentPage) mIndicatorColor
            else mIndicatorInactiveColor
            canvas.drawCircle(width / 2f, y, mIndicatorRadius, mIndicatorPaint)
        }
    }

    private fun getTotalHeight(): Int {
        return (mIndicatorRadius * 2 * mDotsCount + mIndicatorSpacing * (mDotsCount - 1)).toInt();
    }
}