package com.voyah.voice.main.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.view.View
import com.voyah.voice.framework.interpolator.CubicBezierInterpolators

/**
 *  author : jie wang
 *  date : 2024/9/23 16:56
 *  description :
 */

fun View.setStateAnimatorList() {
    val interpolator = CubicBezierInterpolators.Type.EASE_OUT_CUBIC.create()
    val alphaPressedAnim = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.6f).apply {
        duration = 200
    }
    val xPressedAnim = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f, 0.95f).apply {
        duration = 200
    }

    val yPressedAnim = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f, 0.95f).apply {
        duration = 200
    }
    val alphaNormalAnim = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f).apply {
        duration = 200
    }
    val xNormalAnim = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f).apply {
        duration = 200
    }
    val yNormalAnim = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f).apply {
        duration = 200
    }
    val pressedAnim = AnimatorSet().apply {
        setInterpolator(interpolator)
        play(alphaPressedAnim)
            .with(xPressedAnim)
            .with(yPressedAnim)

    }
    val normalAnim = AnimatorSet().apply {
        setInterpolator(interpolator)
        play(alphaNormalAnim)
            .with(xNormalAnim)
            .with(yNormalAnim)
    }
    //这个不能少，否则没有效果
    isClickable = true
    stateListAnimator = StateListAnimator().apply {
        addState(intArrayOf(android.R.attr.state_pressed), pressedAnim)
        //负数表示该状态为false
        addState(intArrayOf(-android.R.attr.state_pressed), normalAnim)
    }
}