package com.voyah.common.provider

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.voyah.common.constant.MAIN_SERVICE_HOME
import com.voyah.common.service.IMainService

/**
 * @author jackie wong
 * @date   2023/3/26 18:30
 * @desc   MainService提供类，对外提供相关能力
 * 任意模块就能通过MainServiceProvider使用对外暴露的能力
 */
object MainServiceProvider {

    @Autowired(name = MAIN_SERVICE_HOME)
    lateinit var mainService: IMainService

    init {
        ARouter.getInstance().inject(this)
    }

    /**
     * 跳转主页
     * @param context
     * @param index tab位置
     */
    fun toMain(context: Context) {
        mainService.toMain(context)
    }

    fun startDrawing() {
        mainService.startDrawing()
    }

    fun onGetVoiceEvent(eventStr: String) {
        LogUtils.d("mainService:$mainService")
        mainService.onGetVoiceEvent(eventStr)
    }

}