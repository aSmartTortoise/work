package com.voyah.window.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import com.blankj.utilcode.util.LogUtils

/**
 *  author : jie wang
 *  date : 2024/8/21 10:37
 *  description :
 */
class CardContentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var motionEventUpBlock: (() -> Unit?)? = null

    companion object {
        const val TAG = "CardContentLayout"
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val actionMasked = event?.action?.and(MotionEvent.ACTION_MASK)
        LogUtils.d("dispatchTouchEvent: actionMasked:$actionMasked")
        if (MotionEvent.ACTION_UP == actionMasked) {
            motionEventUpBlock?.invoke()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val actionMasked = event?.action?.and(MotionEvent.ACTION_MASK)
        LogUtils.d("onInterceptTouchEvent: actionMasked:$actionMasked")
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val actionMasked = event?.action?.and(MotionEvent.ACTION_MASK)
        LogUtils.d("onTouchEvent: actionMasked:$actionMasked")
        return  super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtils.d("onDetachedFromWindow")
    }


}