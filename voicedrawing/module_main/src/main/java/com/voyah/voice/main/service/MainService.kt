package com.voyah.voice.main.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.voice.drawing.api.DrawingAPIManager
import com.voyah.voice.main.ui.MainActivity
import com.voyah.common.constant.MAIN_SERVICE_HOME
import com.voyah.common.service.IMainService

/**
 * @author jackie wong
 * @date   2023/3/26 18:23
 * @desc   主页服务
 * 提供对IMainService接口的具体实现
 */
@Route(path = MAIN_SERVICE_HOME)
class MainService : IMainService {
    var liveVoiceEvent = MutableLiveData<String>()
    /**
     * 跳转主页
     * @param context
     */
    override fun toMain(context: Context) {
        MainActivity.start(context)
    }

    override fun onGetVoiceEvent(eventStr: String) {
//        LogUtils.d("onGetVoiceEvent eventStr:$eventStr")
        liveVoiceEvent.value = eventStr
    }

    override fun init(context: Context?) {

    }

    override fun startDrawing() {
        DrawingAPIBinder.getInstance().startDrawing()
    }


}