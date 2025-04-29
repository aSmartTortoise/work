package com.voyah.aiwindow.common

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ViewSizeChangeAnimation : Animation {
    private var initialWidth = 0
    private var targetWidth = 0
    var view: View? = null

    constructor(view: View, targetWidth: Int) : super() {
        this.view = view
        this.targetWidth = targetWidth
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view?.layoutParams?.width =
            initialWidth + ((targetWidth - initialWidth) * interpolatedTime).toInt()
        view?.requestLayout()
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        this.initialWidth = width
        super.initialize(width, height, parentWidth, parentHeight)
    }
    override fun willChangeBounds(): Boolean {
        return true
    }
}