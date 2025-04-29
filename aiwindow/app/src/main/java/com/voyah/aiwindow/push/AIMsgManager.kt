package com.voyah.aiwindow.push

import com.blankj.utilcode.util.LogUtils
import com.voyah.aiwindow.aidlbean.AIMessage
import com.voyah.aiwindow.aidlbean.State
import com.voyah.aiwindow.push.task.AITask
import com.voyah.aiwindow.push.task.IResultCallback
import com.voyah.aiwindow.push.task.TaskExecutor
import com.voyah.aiwindow.sdk.IMsgResultCallback
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 远程AI小窗消息管理类
 */
object AIMsgManager {
    private val mSequenceId = AtomicInteger()
    private val mTaskQueue: BlockingQueue<AITask>
    private val mTaskExecutor: TaskExecutor
    private const val MAX_SIZE = 5 //队列最大长度

    fun start() {
        stop()
        mTaskExecutor.startWork()
    }

    fun stop() {
        mTaskExecutor.quit()
    }

    fun add(msg: AIMessage, callback: IMsgResultCallback?): Int {
        val callbackWrapper = object : IResultCallback {
            override fun onResult(state: State, msg: String?) {
                try {
                    callback?.onResult(state.code, msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        val task = AITask(msg, callbackWrapper)
        if (!mTaskQueue.contains(task)) {
            if (mTaskQueue.size >= MAX_SIZE) {
                LogUtils.d("queue is overflow, delete first task")
                val first = mTaskQueue.remove()
                LogUtils.d("remove task: ${first.sequence}")
                first.callback?.onResult(State.ERROR, "queue is full, abandon execute!!!")
            }
            task.sequence = mSequenceId.getAndIncrement()
            mTaskQueue.add(task)
            // 高优先级消息，打断低优先级显示
            val headTask = mTaskQueue.peek()
            if (headTask != null && headTask.priority > mTaskExecutor.task?.priority) {
                mTaskExecutor.task?.cancel()
            }
        }
        LogUtils.d("after add() called with: mTaskQueue size :${mTaskQueue.size}")
        return mTaskQueue.size
    }


    init {
        mTaskQueue = PriorityBlockingQueue()
        mTaskExecutor = TaskExecutor(mTaskQueue)
    }
}