package com.voyah.window.cardholder

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.window.R

/**
 *  author : jie wang
 *  date : 2024/6/21 15:29
 *  description :
 */
class MultimediaCardHolder(context: Context) : BasePageCardHolder<MultimediaInfo>(context) {

    private val multimediaCardLargeHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_744)
    }

    private val multimediaCardHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_396)
    }

    private val columnNum = 4

    var vpAttachStateChangeListener: View.OnAttachStateChangeListener? = null


    companion object {
        const val TAG = "MultimediaCardHolder"
    }


    override fun initView(rootView: View) {
        super.initView(rootView)
        vp?.apply {
            addOnAttachStateChangeListener(getAttachStateChangeListener())
        }
    }


    override fun bindData(data: List<MultimediaInfo>) {
        LogUtils.d("bindData")
        chunkSize = 8
        super.bindData(data)
    }

    override fun onCardCollapse() {
       super.onCardCollapse()
        if (vp != null && vpAttachStateChangeListener != null) {
            vp!!.removeOnAttachStateChangeListener(vpAttachStateChangeListener)
        }
        vpAttachStateChangeListener = null
    }

    private fun getAttachStateChangeListener() : View.OnAttachStateChangeListener {
        if (vpAttachStateChangeListener == null) {
            vpAttachStateChangeListener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    LogUtils.d("onViewAttachedToWindow")

                }

                override fun onViewDetachedFromWindow(v: View) {
                    LogUtils.d("onViewDetachedFromWindow")
                }

            }
        }
        return vpAttachStateChangeListener!!
    }

    override fun getCardHeight(data: List<MultimediaInfo>, parentViewWidth: Int): Int {
        val rowNum = (data.size + columnNum - 1) / columnNum
        LogUtils.d("getCardHeight rowNum:$rowNum")
        val cardHeight = if (rowNum > 1) multimediaCardLargeHeight else multimediaCardHeight
        LogUtils.d("getCardHeight:$cardHeight")
        rootView?.let {
            val layoutParams = it.layoutParams
            layoutParams.height = cardHeight
            layoutParams.width = parentViewWidth
            it.layoutParams = layoutParams
        }
        return cardHeight
    }

    override fun onUIModeChange(nightModeFlag: Boolean) {
    }

}