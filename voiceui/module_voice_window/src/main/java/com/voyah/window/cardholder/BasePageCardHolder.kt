package com.voyah.window.cardholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.PageInfo
import com.voyah.voice.card.indicator.VerticalDotsIndicator
import com.voyah.window.R
import com.voyah.window.adapter.CardPageAdapter

/**
 *  author : jie wang
 *  date : 2024/7/18 20:20
 *  description :
 */
abstract class BasePageCardHolder<D : MultiItemEntity>(context: Context)
    : BaseCardHolder<List<D>>(context) {
    var vp: ViewPager2? = null
    var vdi: VerticalDotsIndicator? = null
    protected var chunkSize = 0

    private var pageAdapter: CardPageAdapter? = null
    var action: String? = null


    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            LogUtils.d("onPageSelected position:$position")
//            interactionCallback?.onPageSelected(vp!!, position, domainType!!)
        }
    }

    override fun getCardView(): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.card_root_page, null, false)
        initView(cardView)
        rootView = cardView
        return cardView
    }

    override fun initView(rootView: View) {
        LogUtils.d("initView")
        vp = rootView.findViewById<ViewPager2>(R.id.vp_card).apply {
//            registerOnPageChangeCallback(pageChangeCallback)
        }
        vdi = rootView.findViewById<VerticalDotsIndicator>(R.id.vdi)
    }

    override fun bindData(data: List<D>) {
        LogUtils.d("bindData")
        val chunkedData = data.chunked(chunkSize)
        pageAdapter = CardPageAdapter()
        initAdapter(pageAdapter!!, data)
        vp?.let {
            it.adapter = pageAdapter
            it.orientation = ViewPager2.ORIENTATION_VERTICAL

            if (chunkedData.size > 1) {
                it.viewTreeObserver?.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            it.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                            LogUtils.d("onGlobalLayout height:${it.height}")
                            vdi?.visibility = View.VISIBLE
                        }
                    })
            }
        }

        pageAdapter!!.setData(chunkedData)
        vdi?.setupWithViewPager2(vp!!)
    }

    private fun initAdapter(adapter: CardPageAdapter, data: List<D>) {
        adapter.onItemClickListener = { _, position ->
            LogUtils.d("onItemClick position:$position")
            vp?.let {
                val currentPage = it.currentItem
                LogUtils.d("onItemClick currentPage:$currentPage")
                val itemType = data[currentPage * chunkSize + position].itemType
                domainItemCallback?.invoke(position, itemType)
            }
        }
    }

    override fun scrollCard(direction: Int): Boolean {
        return false
    }

    override fun refreshCard(data: List<D>) {

    }

    override fun getCurrentPage(): PageInfo? {
        return vp?.run {
            val pageInfo = PageInfo().also { pageInfo ->
                val position = currentItem
                pageInfo.position = position
                pageInfo.maxItemCount = chunkSize
                val pageAdapter = adapter
                if (pageAdapter is CardPageAdapter) {
                    val itemEntities = pageAdapter.getData()[position]
                    if (itemEntities.isNotEmpty()) {
                        pageInfo.itemCount = itemEntities.size
                    }
                }

            }
            pageInfo
        }
    }

    override fun setCurrentPage(pageIndex: Int): Boolean {
        vp?.let {
            val pageCount = it.adapter!!.itemCount
            when {
                pageIndex >= pageCount || pageIndex < 0 -> {
                    return false
                }
                else -> {
                    it.setCurrentItem(pageIndex, true)
                    return true
                }
            }
        }

        return false
    }

    override fun nextPage(pageOffset: Int): Boolean {
        vp?.let {
            val currentPage = it.currentItem
            val pageCount = it.adapter!!.itemCount
            if (pageOffset > 0) {
                if (currentPage + pageOffset >= pageCount) {
                    return false
                }
                it.setCurrentItem(currentPage + pageOffset, true)
                return true
            } else if (pageOffset == 0) {
                return false
            } else {
                if (currentPage + pageOffset < 0) {
                    return false
                }
                it.setCurrentItem(currentPage + pageOffset, true)
                return true
            }
        }

        return false
    }

    override fun destroy() {
        action = null
        super.destroy()
//        vp?.unregisterOnPageChangeCallback(pageChangeCallback)
    }


}