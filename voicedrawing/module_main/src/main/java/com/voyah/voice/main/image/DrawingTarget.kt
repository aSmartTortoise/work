package com.voyah.voice.main.image

import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.voice.drawing.api.model.DrawingInfo
import java.io.File

/**
 *  author : jie wang
 *  date : 2025/3/16 15:08
 *  description :
 */
open class DrawingTarget(
    private val drawingInfoTarget: DrawingInfo,
    val onLoadStart: ((drawingInfo: DrawingInfo) -> Unit),
    val onLoadFailed: ((drawingInfo: DrawingInfo) -> Unit),
    val onResourceReady: ((drawingInfo: DrawingInfo, file: File) -> Unit)
) : CustomTarget<File>() {

    override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        onLoadStart.invoke(drawingInfoTarget)
    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        onResourceReady.invoke(drawingInfoTarget, resource)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onLoadFailed.invoke(drawingInfoTarget)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        LogUtils.i("onLoadCleared")
    }
}