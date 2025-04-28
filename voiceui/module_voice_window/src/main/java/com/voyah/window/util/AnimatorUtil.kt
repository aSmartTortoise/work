package com.voyah.window.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils

object AnimatorUtil {
     fun collapseLayout(layout: LinearLayout, initialHeight: Int, targetHeight: Int,
                        onChange: (progress: Boolean) -> Unit) {
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            layout.layoutParams.height = value
            layout.requestLayout()
        }
         animator.addListener(object : Animator.AnimatorListener {
             override fun onAnimationStart(animation: Animator) {
                 LogUtils.d("collapseLayout card animation start ${initialHeight}")
                 onChange.invoke(true)
             }

             override fun onAnimationEnd(animation: Animator) {
                 LogUtils.d("collapseLayout card animation end")
                 layout.visibility = View.GONE
                 onChange.invoke(false)
                 layout.layoutParams.height = 0
                 layout.requestLayout()
             }

             override fun onAnimationCancel(animation: Animator) {
                 LogUtils.d("collapseLayout card animation cancel")
                 onChange.invoke(false)
             }

             override fun onAnimationRepeat(animation: Animator) {
                 LogUtils.d("collapseLayout card animation repeat")
             }

         })
        animator.setDuration(400)
        animator.start()
    }

    fun expandLayout(layout: LinearLayout, initialHeight: Int, targetHeight: Int,
                     onChange: (progress: Boolean) -> Unit) {
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            layout.layoutParams.height = value
            layout.requestLayout()
        }
        layout.visibility = View.VISIBLE
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                LogUtils.d("expand card animation start")
                onChange.invoke(true)
            }

            override fun onAnimationEnd(animation: Animator) {
                LogUtils.d("expand card animation end ${targetHeight}")
                onChange.invoke(false)
                val layoutParams = layout.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layout.layoutParams = layoutParams
                layout.requestLayout()
            }

            override fun onAnimationCancel(animation: Animator) {
                onChange.invoke(false)
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        animator.setDuration(400)
        animator.start()
    }

    fun getReasonHeight(view: TextView, text: String, textSize: Float, width: Int): Int {
        return calculateTextHeight(text, textSize, width)
    }

    fun calculateTextHeight(text: String, textSize: Float, width: Int): Int {
        val textPaint = TextPaint().apply {
            this.textSize = textSize
        }
        val staticLayout = StaticLayout(
            text, textPaint, width,
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 5.0f, false
        )
        return staticLayout.height
    }

    fun getTextViewHeight(textView: TextView): Int {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textView.width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }

    fun adjustTextViewHeight(textView: TextView) {
        val height = getTextViewHeight(textView)
        val layoutParams = textView.layoutParams
        layoutParams.height = height
        textView.layoutParams = layoutParams
    }

    fun calculateTextViewHeight(textView: TextView, text: String, width: Int): Int {
        textView.text = text

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        textView.measure(widthMeasureSpec, heightMeasureSpec)

        return textView.measuredHeight
    }

}