package com.voyah.voice.main.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.framework.toast.TipsToast
import com.voyah.voice.main.repository.MainRepository
import com.voyah.common.model.SchedulePlanInfo
import com.voyah.network.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  author : jie wang
 *  date : 2024/4/17 13:59
 *  description :
 */
class MainViewModel : BaseViewModel() {

    var planInfoData = MutableLiveData<SchedulePlanInfo?>()

    private val mainRepository by lazy {
        MainRepository()
    }

    fun getSchedulePlanning(newSessionFlag: Boolean, query: String) {
        launchUI(errorBlock = { code, error ->
            TipsToast.showTips(error)
            LogUtils.d("getSchedulePlanning code:$code, error:${error}")
            planInfoData.value = null
        }) {
            LogUtils.d("getSchedulePlanning")
            planInfoData.value = mainRepository.getSchedulePlanning(newSessionFlag, query)
        }

    }

    fun registerAgent(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            LogUtils.d("registerAgent")
            mainRepository.registerAgent(context) { content ->
                viewModelScope.launch(Dispatchers.Main) {
                    LogUtils.d("onGetContent content:$content")
                }
            }
        }

    }

    override fun onCleared() {
        super.onCleared()

    }


}