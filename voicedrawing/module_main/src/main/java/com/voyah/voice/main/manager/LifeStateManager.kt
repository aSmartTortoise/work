package com.voyah.voice.main.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.voyah.vcos.EVENT_TYPE_ACTIVE_TIME
import com.voyah.vcos.EVENT_TYPE_OPEN_APP
import com.voyah.vcos.manager.MegaDisplayHelper
import com.voyah.voice.framework.report.Report
import com.voyah.voice.framework.report.ReportHelp
import com.voyah.voice.framework.report.TrackOther
import java.lang.ref.WeakReference

/**
 *  author : jie wang
 *  date : 2024/8/23 11:04
 *  description :
 */
class LifeStateManager private constructor() {

    companion object {
        fun getInstance(): LifeStateManager = Holder.instance
    }

    lateinit var application: Application
    private var activityCount = 0
    private var mTopActivity: WeakReference<Activity>? = null
    private var activeTime = 0L

    fun getTopActivity(): Activity? = mTopActivity?.get()

    fun setLifecycleCallbacks(application: Application) {
        LogUtils.d("setLifecycleCallbacks")
        this.application = application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                LogUtils.d("onActivityCreated")
            }

            override fun onActivityStarted(activity: Activity) {
                // 计算启动的activity数目
                activityCount++
                LogUtils.d("onActivityStarted activityCount:$activityCount")
                if (activityCount == 1) {
                    activeTime = System.currentTimeMillis()
                    ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                        EVENT_TYPE_OPEN_APP,
                        "",
                        0,
                        null
                    )
                }
            }

            override fun onActivityResumed(activity: Activity) {
                LogUtils.d("onActivityResumed")
                mTopActivity?.clear()
                mTopActivity = WeakReference<Activity>(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                LogUtils.d("onActivityPaused")
            }

            override fun onActivityStopped(activity: Activity) {
                // 计算关闭的activity数目，并判断当前App是否处于后台
                activityCount--
                LogUtils.d("onActivityStopped activityCount:$activityCount")
                if (activityCount == 0) {
                    activeTime = System.currentTimeMillis() - activeTime
                    ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                        EVENT_TYPE_ACTIVE_TIME,
                        "",
                        activeTime,
                        null
                    )
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                LogUtils.d("onActivityDestroyed activity:$activity")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }

    fun isForeground() = activityCount > 0

    private object Holder {
        val instance = LifeStateManager()
    }


}