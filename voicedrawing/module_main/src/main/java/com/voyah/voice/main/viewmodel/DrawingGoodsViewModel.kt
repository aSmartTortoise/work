package com.voyah.voice.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.voyah.network.viewmodel.BaseViewModel
import com.voyah.voice.main.model.DrawingTimesGoods
import com.xiaoma.xmsdk.XmSdk
import com.xiaoma.xmsdk.constant.RequestType
import com.xiaoma.xmsdk.model.GoodsBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  author : jie wang
 *  date : 2024/9/3 15:26
 *  description :
 */
class DrawingGoodsViewModel: BaseViewModel() {

    var drawingGoodsList = MutableLiveData<List<GoodsBean>?>()

    var drawingGoodsCallback: DrawingGoodsCallback? = null


    fun getDrawingGoods() {
        LogUtils.d("getDrawingGoods")
        viewModelScope.launch(Dispatchers.Default) {
            XmSdk.getAppStoreHandler()?.requestGoodsList(
                RequestType.GODDS_AI_DRAWING
            ) { result, goods ->
                LogUtils.d("getDrawingGoods result:$result, goods size:${goods?.size ?: 0}")
                LogUtils.d("getDrawingGoods goods:$goods")

                goods?.let {
                    val drawingTimesGoodsList = mutableListOf<DrawingTimesGoods>()
                    for ((index, good) in goods.withIndex()) {
                        val drawingTimesGoods = DrawingTimesGoods().apply {
                            goodsName = good.cnName
                            price = good.price
                            desc = good.cnIntroduce
                            id = good.id
                        }
                        drawingTimesGoodsList.add(index, drawingTimesGoods)
                    }
                    viewModelScope.launch {
                        drawingGoodsCallback?.onGetDrawingGoods(drawingTimesGoodsList)
                    }
                } ?: run {
                    viewModelScope.launch {
                        drawingGoodsCallback?.onGetDrawingGoods(null)
                    }
                }

            } ?: run {
                viewModelScope.launch {
                    drawingGoodsCallback?.onGetDrawingGoods(null)
                }
            }
        }
    }

    interface DrawingGoodsCallback {
        fun onGetDrawingGoods(goods: List<DrawingTimesGoods>?)
    }
}