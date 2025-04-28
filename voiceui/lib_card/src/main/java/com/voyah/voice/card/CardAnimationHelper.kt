package com.voyah.voice.card

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.EasyFloat
import com.voyah.voice.framework.interpolator.CubicBezierInterpolators

/**
 *  author : jie wang
 *  date : 2024/7/25 17:02
 *  description :
 */
object CardAnimationHelper {

    const val DURATION = 200L
    val cupicBezierInterpolator = CubicBezierInterpolators.Type.EASE_OUT_CUBIC.create()

    fun executeExpandAnimation(startValue: Int, endValue: Int,
                                       block: (animationValue: Int) -> Unit): Animator {
        return ValueAnimator.ofInt(startValue, endValue).apply {
            addUpdateListener {
                try {
                    val value = it.animatedValue as Int
                    block.invoke(value)
                } catch (e: Exception) {
                    LogUtils.w("onAnimationUpdate expand card anim error:$e")
                    cancel()
                }
            }
        }
    }

    private fun collapseExpandAnimation(startValue: Int, endValue: Int,
                                       block: (animationValue: Int) -> Unit): Animator {
        return ValueAnimator.ofInt(startValue, endValue).apply {
            addUpdateListener {
                try {
                    val value = it.animatedValue as Int
                    block.invoke(value)
                } catch (e: Exception) {
                    LogUtils.w("onAnimationUpdate expand card anim error:$e")
                    cancel()
                }
            }
        }
    }

    fun executeCardExpandWHXAnimation(
        view: ViewGroup,
        endValueWidth: Int,
        endValueHeight: Int,
        endValueX: Int,
        windowTag: String,
        animationStart: (animation: Animator) -> Unit,
        animationEnd: (animation: Animator) -> Unit,
        animationCancel: (animation: Animator) -> Unit): Animator {
        LogUtils.d("executeCardExpandWHXAnimation")
        val startValueWidth = view.width
        val animatorWidth = executeExpandAnimation(startValueWidth, endValueWidth) { value ->
            val layoutParams = view.layoutParams
            layoutParams?.width = value
            LogUtils.d("expand width:$value")
            view.requestLayout()
        }

        val animatorHeight = executeExpandAnimation(view.height, endValueHeight) { value ->
            val layoutParams = view.layoutParams
            layoutParams?.height = value
            LogUtils.d("expand height:$value")
            view.requestLayout()
        }

        val startValueX = EasyFloat
            .getConfig(windowTag)
            ?.currentLocationX ?: -1000
        LogUtils.d("executeCardExpandWHXAnimation startValueX:$startValueX, endValueX:$endValueX")
        val animatorLocation = executeExpandAnimation(startValueX, endValueX) { value ->
            EasyFloat.updateFloat(windowTag, x = value)
            LogUtils.d("expand x:$value")
        }
        val animatorSet = AnimatorSet()
        return animatorSet.apply {
            duration = DURATION
            addListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        LogUtils.d("expand card animation start")
                        animationStart.invoke(animation)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        LogUtils.d("expand card animation end")
                        animationEnd.invoke(animation)
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        LogUtils.d("expand card animation cancel")
                        animationCancel.invoke(animation)
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        LogUtils.d("expand card animation repeat")
                    }

                }
            )
            val interpolator = cupicBezierInterpolator
            setInterpolator(interpolator)
            playTogether(animatorWidth, animatorHeight, animatorLocation)
            start()
        }
    }

    fun executeCardExpandWXAnimation(
        view: ViewGroup,
        endValueWidth: Int,
        endValueX: Int,
        windowTag: String,
        animationStart: (animation: Animator) -> Unit,
        animationEnd: (animation: Animator) -> Unit,
        animationCancel: (animation: Animator) -> Unit): Animator {
        LogUtils.d("executeCardExpandWXAnimation")
        val animatorSet = AnimatorSet()
        val startValueWidth = view.width
        val animatorWidth = executeExpandAnimation(
            startValueWidth, endValueWidth
        ) { value ->
            val layoutParams = view.layoutParams
            layoutParams?.width = value
            view.requestLayout()
        }

        val startValueX = EasyFloat
            .getConfig(windowTag)
            ?.currentLocationX ?: -1000
        LogUtils.d("executeCardExpandWXAnimation startValueX:$startValueX, endValueX:$endValueX")
        val animatorLocation = executeExpandAnimation(startValueX, endValueX) { value ->
            EasyFloat.updateFloat(windowTag, x = value) }

        return animatorSet.apply {
            duration = DURATION
            addListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        LogUtils.d("expand card animation start")
                        animationStart.invoke(animation)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        LogUtils.d("expand card animation end")
                        animationEnd.invoke(animation)
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        LogUtils.d("expand card animation cancel")
                        animationCancel.invoke(animation)
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        LogUtils.d("expand card animation repeat")
                    }

                }
            )
            val interpolator = cupicBezierInterpolator
            setInterpolator(interpolator)
            playTogether(animatorWidth, animatorLocation)
            start()
        }
    }

    fun executeCardCollapseWHXAnimation(
        view: ViewGroup,
        endValueWidth: Int,
        endValueHeight: Int,
        endValueX: Int,
        windowTag: String,
        animationEnd: (animation: Animator) -> Unit,
        animationCancel: (animation: Animator) -> Unit): AnimatorSet {
        LogUtils.d("executeCardCollapseWHXAnimation")

        val animatorSet = AnimatorSet()
        val startValueWidth = view.width
        val animatorWidth = collapseExpandAnimation(startValueWidth, endValueWidth) { value ->
            val layoutParams = view.layoutParams
            layoutParams?.width = value
            LogUtils.d("width:$value")
            view.requestLayout()
        }

        val startValueHeight = view.height
        val animatorHeight = collapseExpandAnimation(startValueHeight, endValueHeight) { value ->
            val layoutParams = view.layoutParams
            layoutParams?.height = value
            view.requestLayout()
        }

        val startValueX = EasyFloat
            .getConfig(windowTag)
            ?.currentLocationX ?: -1000
        val animatorLocation = collapseExpandAnimation(
            startValueX, endValueX
        ) { value ->
            EasyFloat.updateFloat(
                windowTag,
                x = value
            )
        }

        return animatorSet.apply {
            duration = DURATION
            addListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        LogUtils.d("collapse card animation start")
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        LogUtils.d("collapse card animation end")
                        animationEnd.invoke(animation)
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        LogUtils.d("collapse card animation cancel")
                        animationCancel.invoke(animation)
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        LogUtils.d("collapse card animation repeat")
                    }

                }
            )
            val interpolator = cupicBezierInterpolator
            setInterpolator(interpolator)
            playTogether(animatorWidth, animatorHeight, animatorLocation)
            start()
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun scaleInOutVPA(view: View, scaleInFlag: Boolean = true): Animator {
        LogUtils.d("scaleInOutVPA scaleInFlag:$scaleInFlag")
        val factor = if (scaleInFlag) 0.8f else 1.0f
        val animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", factor)
        val animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", factor)
        return AnimatorSet().apply {
            duration = DURATION
            interpolator = cupicBezierInterpolator
            playTogether(animatorScaleX, animatorScaleY)
            start()
        }
    }

    fun scaleInOutVPANew(view: View, focusX: Float, focusY: Float, scaleInFlag: Boolean = true): Animator {
        // 计算缩放中心点
        view.pivotX = focusX
        view.pivotY = focusY
        LogUtils.d("scaleInOutVPA scaleInFlag:$scaleInFlag")
        val startValue = if (scaleInFlag) 1.0f else 0.8f
        val endValue = if (scaleInFlag) 0.8f else 1.0f
        // 使用属性动画缩放
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", startValue, endValue)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", startValue, endValue)

        return AnimatorSet().apply {
            duration = DURATION
            interpolator = cupicBezierInterpolator
            playTogether(scaleX, scaleY)
            start()
        }

    }

}