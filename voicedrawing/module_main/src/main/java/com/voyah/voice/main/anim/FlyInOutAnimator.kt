package com.voyah.voice.main.anim

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator
import kotlin.math.min

/**
 *  author : jie wang
 *  date : 2025/1/8 14:03
 *  description :
 */
class FlyInOutAnimator : OnFloatAnimator {

    override fun enterAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator = getAnimator(view, params, windowManager, sidePattern, false)

    override fun exitAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator = getAnimator(view, params, windowManager, sidePattern, true)

    private fun getAnimator(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern,
        isExit: Boolean
    ): Animator {
        val triple = initValue(view, params, windowManager, sidePattern)
        // 退出动画的起始值、终点值，与入场动画相反
        val start = if (isExit) triple.second else triple.first
        val end = if (isExit) triple.first else triple.second
        return ValueAnimator.ofInt(start, end).apply {
            duration = 200L
            addUpdateListener {
                try {
                    val value = it.animatedValue as Int
                    if (triple.third) params.x = value else params.y = value
                    // 动画执行过程中页面关闭，出现异常
                    windowManager.updateViewLayout(view, params)
                } catch (e: Exception) {
                    cancel()
                }
            }
        }
    }

    /**
     * 计算边距，起始坐标等
     */
    private fun initValue(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Triple<Int, Int, Boolean> {
        val isHorizontal = false
        val endValue: Int = params.y
        val startValue: Int = -view.bottom
        return Triple(startValue, endValue, isHorizontal)
    }
}