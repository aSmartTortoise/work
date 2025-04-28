package com.voyah.window.anim

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator
import com.voyah.voice.card.CardAnimationHelper.executeExpandAnimation
import com.voyah.voice.framework.interpolator.CubicBezierInterpolators

/**
 *  author : jie wang
 *  date : 2024/10/17 10:11
 *  description :
 */
class FadeInOutAnimator : OnFloatAnimator {

    override fun enterAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? {
        return getAnimator(view, params, windowManager, sidePattern, false)
    }

    override fun exitAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? {
        return getAnimator(view, params, windowManager, sidePattern, true)
    }

    @SuppressLint("Recycle")
    private fun getAnimator(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern,
        isExit: Boolean
    ): Animator? {
        if (view is ViewGroup && view.childCount > 0) {
            val targetView = view.getChildAt(0)
            val alpha = targetView.alpha
            val startValueAlpha = if (isExit) 1f else 0f
            val endValueAlpha = if (isExit) 0f else 1f
            val alphaAnimator = ObjectAnimator.ofFloat(targetView, "alpha", startValueAlpha, endValueAlpha).apply {

            }
            val width = targetView.width
            LogUtils.d("getAnimator width:$width")
            val startValueWidth = if (isExit) width else 0
            val endValueWidth = if (isExit) 0 else width
            val animatorWidth = executeExpandAnimation(startValueWidth, endValueWidth) { value ->
                val layoutParams = targetView.layoutParams
                layoutParams?.width = value
                LogUtils.d("view width change, now is :$value")
                targetView.requestLayout()
            }

            return AnimatorSet().apply {
                duration = 200L
                interpolator = CubicBezierInterpolators.Type.EASE_OUT_CUBIC.create()
                playTogether(alphaAnimator)
            }
        } else {
            return null
        }


    }
}