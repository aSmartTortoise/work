package com.voyah.voice.main.service

import android.content.Context
import android.content.res.Configuration
import android.os.Process
import android.os.RemoteCallbackList
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.voice.drawing.api.IDrawingAPI
import com.voice.drawing.api.IDrawingAPICallback
import com.voyah.voice.framework.ext.normalScope
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.main.presenter.DrawingAPIPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  author : jie wang
 *  date : 2024/8/8 11:24
 *  description :
 */
class DrawingAPIBinder private constructor(): IDrawingAPI.Stub() {

    private val drawingAPICallbacks = RemoteCallbackList<IDrawingAPICallback>()
    private var normalScope: CoroutineScope = normalScope()

    companion object {
        fun getInstance(): DrawingAPIBinder = Holder.instance
    }


    var apiPresenter: DrawingAPIPresenter = DrawingAPIPresenter(AppHelper.getApplication())

    fun init() {
        LogUtils.d("init")
    }

    override fun openApp(): Int {
        LogUtils.d("openApp userHandle:${Process.myUserHandle()}")
        val result: Int = apiPresenter.openApp()
        LogUtils.d("openApp result:$result")
        LogUtils.d("openApp app version name:${AppUtils.getAppVersionName()}")
        return result
    }

    override fun openAppAsDisplayId(displayId: Int): Int {
        LogUtils.d("openAppAsDisplayId displayId:$displayId")
        val result: Int = apiPresenter.openAppAsDisplayId(displayId)
        LogUtils.d("openAppAsDisplayId result:$result")
        LogUtils.d("openAppAsDisplayId app version name:${AppUtils.getAppVersionName()}")
        return result
    }

    override fun closeApp(): Int {
        val result: Int = apiPresenter.closeApp()
        LogUtils.d("closeApp result:$result")
        return result
    }

    override fun registerDrawingAPICallback(callback: IDrawingAPICallback?) {
        LogUtils.d("registerDrawingAPICallback callback:$callback")
        callback?.let {
            drawingAPICallbacks.register(it)
        }
    }

    override fun unRegisterDrawingAPICallback(callback: IDrawingAPICallback?) {
        LogUtils.d("unRegisterDrawingAPICallback callback:$callback")
        callback?.let {
            drawingAPICallbacks.unregister(it)
        }
    }

    override fun isAppForeground(): Int {
        val result: Int = apiPresenter.isAppForeground()
        LogUtils.d("isAppForeground result:$result")
        LogUtils.d("isAppForeground app version name:${AppUtils.getAppVersionName()}")
        return result
    }

    override fun openDrawingInfo(): Int {
        LogUtils.d("openDrawingInfo userHandle:${Process.myUserHandle()}")
        val result: Int = apiPresenter.openDrawingInfo("", 0)
        LogUtils.d("openDrawingInfo result:$result")
        return result
    }

    override fun postDrawingState(drawingStateJson: String?) {
        LogUtils.d("postDrawingState userHandle:${Process.myUserHandle()}")
        LogUtils.d("postDrawingState drawingStateJson:$drawingStateJson")
        LogUtils.d("postDrawingState app version name:${AppUtils.getAppVersionName()}")
        normalScope.launch {
            drawingStateJson?.let {
                apiPresenter.postDrawingState(it, 0)
            }
        }
    }

    override fun postDrawingStateAsDisplayId(
        drawingStateJson: String?,
        displayId: Int
    ) {
        LogUtils.d("postDrawingStateAsDisplayId userHandle:${Process.myUserHandle()}, displayId:$displayId")
        LogUtils.d("postDrawingStateAsDisplayId drawingStateJson:$drawingStateJson")
        LogUtils.d("postDrawingStateAsDisplayId app version name:${AppUtils.getAppVersionName()}")
        normalScope.launch {
            drawingStateJson?.let {
                apiPresenter.postDrawingState(it, displayId)
            }
        }
    }

    fun startDrawing() {
        normalScope.launch(Dispatchers.Default) {
            LogUtils.d("startDrawing")
            val callbacks = drawingAPICallbacks.beginBroadcast()
            for (i in 0 until callbacks) {
                drawingAPICallbacks.getBroadcastItem(i)?.apply {
                    startDrawing()
                }
            }
            drawingAPICallbacks.finishBroadcast()
        }
    }

    fun redraw(prompt: String) {
        LogUtils.d("redraw userHandle:${Process.myUserHandle()}")
        LogUtils.d("redraw prompt:$prompt")
        normalScope.launch(Dispatchers.Default) {
            val callbacks = drawingAPICallbacks.beginBroadcast()
            LogUtils.d("redraw callbacks size is:${callbacks}")
            for (i in 0 until callbacks) {
                drawingAPICallbacks.getBroadcastItem(i)?.apply {
                    redraw(prompt)
                }
            }
            drawingAPICallbacks.finishBroadcast()
        }
    }

    fun onConfigurationChanged() {
        apiPresenter.onConfigurationChanged()
    }

    fun onSplitScreenStateChange() {
        apiPresenter?.onSplitScreenStateChange()
    }

    private object Holder {
        val instance = DrawingAPIBinder()
    }

    fun close() {
        LogUtils.d("close")
    }
}