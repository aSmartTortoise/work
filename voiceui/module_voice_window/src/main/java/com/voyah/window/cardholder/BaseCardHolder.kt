package com.voyah.window.cardholder

import android.content.Context
import android.provider.Settings
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.PageInfo
import com.voyah.voice.framework.ext.normalScope
import com.voyah.window.interfaces.CardInteractionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 *  author : jie wang
 *  date : 2024/2/29 19:56
 *  description : 处理业务功能的基类
 */
abstract class BaseCardHolder<T> (val context: Context) {

    var domainItemCallback: ((position: Int, viewType: Int) -> Unit)? = null
    var cardScrollCallback: ((direction: Int, canScroll: Boolean) -> Unit)? = null

    var domainType: String? = null
    open var sessionId: String? = null
    open var requestId: String? = null
    var fromGPTFloag: Boolean = false

    private var normalScope: CoroutineScope? = null

    var interactionCallback: CardInteractionCallback? = null
    var cardInfo: CardInfo? = null
    var rootView: View? = null

    companion object {
        const val TAG = "BaseDomain"
    }

    private fun initDomain() {
        initView(getCardView())
    }

    abstract fun getCardView(): View

    abstract fun initView(rootView: View)

    abstract fun bindData(data: T)

    abstract fun refreshCard(data: T)

    abstract fun scrollCard(direction: Int): Boolean

    open fun onCardCollapse() {
        LogUtils.d("onCardCollapse")
        normalScope?.cancel()
        normalScope = null
    }

    open fun getCurrentPage(): PageInfo? = null

    open fun setCurrentPage(pageIndex: Int): Boolean = false

    abstract fun getCardHeight(data: T, parentViewWidth: Int) : Int

    open fun nextPage(pageOffset: Int): Boolean = false



    fun getCoroutineScope(): CoroutineScope {
        if (normalScope == null) {
            normalScope = normalScope()
        }
        return normalScope!!
    }

    open fun destroy() {
        domainType = null
        sessionId = null
        requestId = null
        fromGPTFloag = false
        normalScope?.cancel()
        normalScope = null
        cardInfo = null
        rootView = null
    }

    abstract fun onUIModeChange(nightModeFlag: Boolean)

    open fun setCardCollapse() {
        LogUtils.d("setCardCollapse")
    }



}