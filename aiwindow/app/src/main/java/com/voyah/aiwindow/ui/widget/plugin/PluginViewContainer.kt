package com.voyah.aiwindow.ui.widget.plugin

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.voyah.aiwindow.R
import com.voyah.aiwindow.aidlbean.State
import com.voyah.aiwindow.ui.container.FlowCallback
import com.voyah.aiwindow.ui.container.LiteRootLayout
import com.voyah.aiwindow.ui.container.LiteWindowManager
import com.voyah.aiwindow.ui.container.WindowTag

/**
 * 插件view卡片容器
 */
class PluginViewContainer : IPluginContainerView {

    override fun showPluginView(view: View, callback: (State, String) -> Unit) {
        showPluginView(view, Gravity.CENTER, callback)
    }

    override fun showPluginView(view: View, gravity: Int, callback: (State, String) -> Unit) {
        showPluginView(
            view,
            gravity,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            callback
        )
    }

    override fun showPluginView(
        view: View,
        gravity: Int,
        width: Int,
        height: Int,
        callback: (State, String) -> Unit
    ) {
        showPluginView(view, gravity, width, height, 0, callback)
    }


    override fun showPluginView(
        view: View,
        gravity: Int,
        width: Int,
        height: Int,
        displayId: Int,
        callback: (State, String) -> Unit
    ) {
        val window = LiteWindowManager.getWindow(WindowTag.TAG_WINDOW_PLUGIN)

        if (window != null) {
            val rootLayout = window.voiceWindow.rootLayout
            window.voiceWindow.params?.gravity = gravity
            window.voiceWindow.params?.width = width
            window.voiceWindow.params?.height = height
            val rootContentView = rootLayout.findViewById<FrameLayout>(R.id.layout_frame)
            rootContentView.removeAllViews()
            rootContentView.addView(view)
        } else {
            LiteWindowManager.with(Utils.getApp(), WindowTag.TAG_WINDOW_PLUGIN)
                .setDisplayId(displayId)
                .setLayoutId(R.layout.plugin_container)
                .setWidthAndHeight(width, height)
                .setIntercept(true)
                .setGravity(gravity)
                .show(object : FlowCallback() {
                    override fun onAttach(v: View?) {
                        LogUtils.i("onAttach() called with: v = $v")
                        callback.invoke(State.SHOW, "aiMessage's view is onAttached by window")
                    }

                    override fun onClick() {
                        LogUtils.d("onClick() called")
                    }

                    override fun onVisible(v: View, vis: Int) {
                        LogUtils.i("onVisible() called with: view = $v, vis = $vis")
                        if (vis == View.VISIBLE) {
                            v as LiteRootLayout
                            val rootContentView =
                                v.childView?.findViewById<FrameLayout>(R.id.layout_frame)
                            if (rootContentView != null) {
                                rootContentView.removeAllViews()
                                rootContentView.addView(view)
                            } else {
                                LogUtils.d("can not find R.id.layout_frame")
                            }
                        }
                    }

                    override fun onDismiss() {
                        LogUtils.i("onDismiss() called")
                        callback.invoke(State.DISMISS, "aiMessage's view is onAttached by window")
                    }
                })
        }
    }

    override fun dismiss() {
        LiteWindowManager.dismiss(WindowTag.TAG_WINDOW_PLUGIN)
    }

}