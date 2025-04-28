package com.voyah.window.cardholder

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.voice.framework.ext.normalScope
import com.voyah.window.R
import com.voyah.window.adapter.MultimediaOuterCardAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  author : jie wang
 *  date : 2024/6/21 15:29
 *  description :
 */
class MultimediaCardHolder(context: Context) : BaseCardHolder<List<MultimediaInfo>>(context) {

    private var vp: ViewPager2? = null

    private val list = arrayListOf<Int>(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN)

    private var pageAdapter: MultimediaOuterCardAdapter? = null

    private val multimediaCardHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_744)
    }

    var vpAttachStateChangeListener: View.OnAttachStateChangeListener? = null

    private var normalScope: CoroutineScope? = null

    companion object {
        const val TAG = "MultimediaCardHolder"
    }

    override fun getCardView(): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.card_root_multimedia, null, false)
        initView(cardView)
        return cardView
    }

    override fun initView(rootView: View) {
        vp = rootView.findViewById<ViewPager2>(R.id.vp_card).apply {
            addOnAttachStateChangeListener(getAttachStateChangeListener())
        }
    }

    override fun scrollCard(direction: Int): Boolean {
        return false
    }

    override fun refreshCard(data: List<MultimediaInfo>) {

    }

    override fun expandCard(data: List<MultimediaInfo>) {
        LogUtils.d("expandCard")
        getCoroutineScope().launch(Dispatchers.Default) {
            val chunkedData = data.chunked(8)
           LogUtils.d("checked data.")
            withContext(Dispatchers.Main) {
                LogUtils.d("bind viewPager2.")
                pageAdapter = MultimediaOuterCardAdapter()
                vp?.apply {
                    setCardHeight()
                    adapter = pageAdapter
                    orientation = ViewPager2.ORIENTATION_VERTICAL
                }

                pageAdapter!!.setData(chunkedData)

                vp?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        vp?.let {
                            it.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                            LogUtils.d("onGlobalLayout height:${it.height}, measuredHeight:${it.measuredHeight}")
                        }
                    }

                })

            }
        }
    }

    private fun ViewPager2.setCardHeight() {
        val cardLayoutParams = layoutParams as LinearLayout.LayoutParams
        cardLayoutParams.height = multimediaCardHeight
    }

    override fun onCardCollapse() {
        LogUtils.d("onCardCollapse")
        if (vp != null && vpAttachStateChangeListener != null) {
            vp!!.removeOnAttachStateChangeListener(vpAttachStateChangeListener)
        }
        vpAttachStateChangeListener = null
        normalScope?.cancel()
        normalScope = null
    }

    private fun getCoroutineScope(): CoroutineScope {
        if (normalScope == null) {
            normalScope = normalScope()
        }
        return normalScope!!
    }

    private fun getAttachStateChangeListener() : View.OnAttachStateChangeListener {
        if (vpAttachStateChangeListener == null) {
            vpAttachStateChangeListener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {

                }

                override fun onViewDetachedFromWindow(v: View) {

                }

            }
        }
        return vpAttachStateChangeListener!!
    }

}