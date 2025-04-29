package com.voice.sdk.util

import com.voyah.ai.common.utils.LogUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * @Date 2024/9/20 14:24
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object ThreadPoolUtils {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    private val MAX_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE_SECONDS = 30L
    private val QUEUE = LinkedBlockingQueue<Runnable>(128)
    private val THREAD_FACTORY = createThreadFactory("common", false)
    private val REJECT_STRATEGY = ThreadPoolExecutor.AbortPolicy()



    private val defaultPool by lazy {
        ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, QUEUE, THREAD_FACTORY, REJECT_STRATEGY)
    }
    fun getDefaultPool(): ExecutorService {
        return defaultPool
    }

    fun getSinglePool(threadName: String? = "default"): ExecutorService {
        return Executors.newSingleThreadExecutor(createThreadFactory(threadName))
    }

    fun getScheduledPool(threadName: String? = "scheduled"): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(1, createThreadFactory(threadName))
    }

    fun threadExecute(r: Runnable) {
        defaultPool.execute(r)
    }

    fun isShutdown(): Boolean {
        return defaultPool.isShutdown
    }

    fun shutdownNow(): List<Runnable> {
        return defaultPool.shutdownNow()
    }

    fun createThreadFactory(threadNamePrefix: String?, isDaemon: Boolean = false) : ThreadFactory{
        return if (threadNamePrefix != null) {
            ThreadFactoryImpl(threadNamePrefix, isDaemon)
        } else {
            Executors.defaultThreadFactory();
        }
    }
}

class ThreadFactoryImpl(private val threadNamePrefix: String, private val isDaemon: Boolean = false) : ThreadFactory {

    private val  threadIndex =  AtomicLong(0);

    override fun newThread(r: Runnable?): Thread {
        val thread = Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet()).apply {
            this.isDaemon = isDaemon
            this.uncaughtExceptionHandler = DefaultExceptionHandler()
        }
        return thread
    }
}

class DefaultExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtils.d("ThreadPoolUtils", "线程[" + t.name + "]异常: " + e.message)
    }
}

