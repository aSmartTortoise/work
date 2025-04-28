package com.voyah.window.cardholder

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.LogUtils

/**
 *  author : jie wang
 *  date : 2024/2/29 19:56
 *  description : 处理业务功能的基类
 */
abstract class BaseCardHolder<T> (val context: Context) {

    var domainItemCallback: ((position: Int, viewType: Int) -> Unit)? = null
    var cardScrollCallback: ((direction: Int, canScroll: Boolean) -> Unit)? = null

    var domainType: String? = null

    companion object {
        const val TAG = "BaseDomain"
    }

    private fun initDomain() {
        initView(getCardView())
    }

    abstract fun getCardView(): View

    abstract fun initView(rootView: View)

    abstract fun expandCard(data: T)

    abstract fun refreshCard(data: T)

    abstract fun scrollCard(direction: Int): Boolean

    abstract fun onCardCollapse();


}