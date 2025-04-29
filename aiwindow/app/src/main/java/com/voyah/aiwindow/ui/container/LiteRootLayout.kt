package com.voyah.aiwindow.ui.container

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils

class LiteRootLayout(context: Context) : FrameLayout(context) {

    var childView: View? = null
    private var visibleCall: ((Int) -> Unit)? = null

    fun setView(childView: View) {
        val child0 = getChildAt(0)
        if (child0 != null && childView == child0) {
            LogUtils.i("the view has already has add")
            return
        }

        if (childView.parent != null) {
            val viewGroup = childView.parent as ViewGroup
            viewGroup.removeView(childView)
        }

        addView(childView)
        this.childView = childView
    }

    fun setView(id: Int) {
        val child0 = getChildAt(0)
        val childView = LayoutInflater.from(context).inflate(id, this, false)
        if (child0 != null && childView == child0) {
            LogUtils.i("the view has already has add")
            return
        }

        if (childView.parent != null) {
            val viewGroup = childView.parent as ViewGroup
            viewGroup.removeView(childView)
        }

        addView(childView)
        this.childView = childView
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        LogUtils.i("onWindowVisibilityChanged $visibility")
        visibleCall?.invoke(visibility)
    }

    fun setVisibleChangeCall(vis: (Int) -> Unit) {
        visibleCall = vis
    }
}