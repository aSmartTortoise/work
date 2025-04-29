package com.voyah.voice.main.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.voyah.common.model.DrawingTimesInfo
import com.voyah.network.viewmodel.BaseViewModel
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.vcos.EVENT_TYPE_DRAWING_HISTORY
import com.voyah.vcos.EVENT_TYPE_REDRAW
import com.voyah.vcos.postReport
import com.voyah.voice.main.model.AiDrawDataStore
import com.voyah.voice.main.model.RemainTimes
import com.voyah.voice.main.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  author : jie wang
 *  date : 2024/8/12 16:29
 *  description :
 */
class DrawingInfoModel : BaseViewModel() {


    private val mainRepository by lazy {
        MainRepository()
    }

    var drawingTimesInfo = MutableLiveData<DrawingTimesInfo?>()

    var drawingTimesCallback: DrawingTimesCallback? = null

    fun getRemainingDrawingTimes(vinCode: String) {
        launchUI(errorBlock = { code, error ->
            LogUtils.d("getRemainingDrawingTimes code:$code, error:${error}")
            drawingTimesCallback?.onError()

        }) {
            LogUtils.d("getRemainingDrawingTimes")
            val drawingTimes = mainRepository.getRemainingDrawingTimes(vinCode)
            drawingTimesCallback?.onGetDrawingTimes(drawingTimes)
        }
    }

    fun getRemainingDrawingTimesNew() {
        viewModelScope.launch(Dispatchers.Default) {
            LogUtils.d("getRemainingDrawingTimesNew")
            val remainTimes: RemainTimes? = AiDrawDataStore.getInstance(AppHelper.getApplication())
                .performRemainTimeHttpGetRequest(AppHelper.getApplication())
            withContext(Dispatchers.Main) {
                remainTimes?.let { remainTimes ->
                    remainTimes.result?.let { result ->
                        drawingTimesCallback?.onGetRemainTimes(result)
                    } ?: run {
                        drawingTimesCallback?.onError()
                    }

                } ?: run {
                    drawingTimesCallback?.onError()
                }
            }
        }


    }

    fun postRedrawEvent(view: View) {
        postReport(view, EVENT_TYPE_REDRAW)
    }

    fun postDrawingHistoryEvent(view: View) {
        postReport(view, EVENT_TYPE_DRAWING_HISTORY)
    }

    interface DrawingTimesCallback {
        fun onGetDrawingTimes(drawingTimes: DrawingTimesInfo?)
        fun onGetRemainTimes(remainTimes: RemainTimes.ResultDTO)
        fun onError()
    }


}