package com.voyah.aiwindow.ui.widget.plugin

import android.view.View
import com.voyah.aiwindow.aidlbean.State

/**
 * 容器view接口方法定义
 */
interface IPluginContainerView {
    /**
     * 添加技能view到容器中
     *
     * @param view
     */
    fun showPluginView(view: View, callback: (State, String) -> Unit)

    /**
     * 添加技能view到容器中
     *
     * @param view
     * @param gravity
     */
    fun showPluginView(view: View, gravity: Int, callback: (State, String) -> Unit)

    /**
     * 添加技能view到容器中
     */
    fun showPluginView(view: View, gravity: Int, width: Int, height: Int, callback: (State, String) -> Unit)

    /**
     * 添加技能view到容器中
     */
    fun showPluginView(
        view: View,
        gravity: Int,
        width: Int,
        height: Int,
        displayId: Int,
        callback: (State, String) -> Unit
    )

    /**
     * 隐藏容器
     */
    fun dismiss()
}