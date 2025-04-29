package com.voyah.aiwindow.push.task

import com.voyah.aiwindow.aidlbean.State

/**
 * 语音内部执行推送消息回调
 */
interface IResultCallback {
    /**
     * 执行结果
     */
    fun onResult(state: State, msg: String?)
}