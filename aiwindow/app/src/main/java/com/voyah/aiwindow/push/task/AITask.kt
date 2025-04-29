package com.voyah.aiwindow.push.task

import android.view.Gravity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.voyah.aiwindow.aidlbean.AIMessage
import com.voyah.aiwindow.aidlbean.State
import com.voyah.aiwindow.common.PluginHelper
import com.voyah.aiwindow.ui.container.VpaManager
import java.util.concurrent.CountDownLatch

class AITask(
    var aiMessage: AIMessage,  // ai消息
    var callback: IResultCallback? // 回调
) : Comparable<AITask> {
    // 序列标记
    var sequence = 0

    // 优先级
    var priority = aiMessage.priority;

    var latch: CountDownLatch? = null

    fun cancel() {
        latch?.countDown()
        callback?.onResult(State.DISMISS, "aiMessage is cancelled by high priority msg")
    }

    fun execute() {
        LogUtils.d("execute() called, task = $aiMessage")
        callback?.onResult(State.TAKEN, "aiMessage is handled")
        latch = CountDownLatch(1)
        ThreadUtils.runOnUiThreadDelayed({
            // 切换到UI线程弹出push窗口
            LogUtils.d("handleMsgOnUiThread, pkg: ${aiMessage.pkgName}, clazz: ${aiMessage.clazz}")
            val pluginView = PluginHelper.loadPlugin(Utils.getApp(), aiMessage)
            if (pluginView != null) {
                VpaManager.getPluginContainer().showPluginView(
                    pluginView, Gravity.CENTER,
                    aiMessage.width, aiMessage.height
                ) { state, msg ->
                    callback?.onResult(state, msg)
                    if (state == State.DISMISS) {
                        latch?.countDown()
                    }
                }
            } else {
                LogUtils.d("loadPlugin fail:" + aiMessage.clazz)
                callback?.onResult(State.ERROR, "aiMessage load plugin view fail!!!")
                latch?.countDown()
            }
        }, DELAY_TIME)

        // 等待弹窗dismiss
        try {
            latch?.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 做优先级比较
     */
    override fun compareTo(other: AITask): Int {
        val me = priority
        val it = other.priority
        return if (me == it) sequence - other.sequence else it.ordinal - me.ordinal
    }

    override fun toString(): String {
        return "AITask(aiMessage=$aiMessage, callback=$callback, sequence=$sequence)"
    }

    companion object {
        // 消息延时执行时间
        private const val DELAY_TIME = 500L
    }
}