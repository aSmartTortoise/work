package com.voyah.aiwindow.push.task

import com.blankj.utilcode.util.LogUtils
import java.util.concurrent.BlockingQueue

class TaskExecutor(private val taskQueue: BlockingQueue<AITask>) : Thread() {
    private var isRunning = true

    var task: AITask? = null

    fun startWork() {
        isRunning = true
        start()
    }

    fun quit() {
        isRunning = false
        interrupt()
    }

    override fun run() {
        while (isRunning) {
            try {
                task = taskQueue.take()
                LogUtils.d("TaskExecutor running, task sequence: ${task?.sequence}")
            } catch (e: InterruptedException) {
                if (!isRunning) {
                    interrupt()
                    break
                }
                continue
            }
            //立即执行
            task?.execute()
        }
    }
}