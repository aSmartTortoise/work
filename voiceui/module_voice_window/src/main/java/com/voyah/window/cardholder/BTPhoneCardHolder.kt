package com.voyah.window.cardholder

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.CardDirection
import com.voyah.cockpit.window.model.Contact
import com.voyah.window.R
import com.voyah.voice.framework.itemdecoration.LinearItemDecoration
import com.voyah.window.adapter.DomainMultiItemAdapter


/**
 *  author : jie wang
 *  date : 2024/3/8 16:56
 *  description : 处理蓝牙电话card
 */
class BTPhoneCardHolder(context: Context) : BaseCardHolder<List<Contact>>(context) {

    private var rvCard: RecyclerView? = null
    private var contactItemAdapter: DomainMultiItemAdapter? = null

    companion object {
        const val TAG = "BTPhoneCardViewHolder"
    }

    override fun getCardView(): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.card_root, null, false)
        initView(cardView)
        return cardView
    }

    override fun initView(rootView: View) {
        rvCard = rootView.findViewById(R.id.rv_card)
    }

    override fun expandCard(data: List<Contact>) {
        contactItemAdapter = DomainMultiItemAdapter().apply {
            onItemClickListener = {_, position ->
                LogUtils.d("onItemClick position:$position")
                getItem(position)?.let { itemEntity ->
                    val itemType = itemEntity.itemType
                    LogUtils.d("onItemClick itemType:$itemType")
                    domainItemCallback?.invoke(position, itemType)
                }
            }
        }
        rvCard?.apply {
            val dp40 = context.resources.getDimensionPixelSize(R.dimen.dp_40)
            val dp16 = context.resources.getDimensionPixelSize(R.dimen.dp_16)
            val dp24 = context.resources.getDimensionPixelSize(R.dimen.dp_24)

            if (data.size > 5) {
                resetCardLayoutHeight()
            } else {
                (layoutParams as LinearLayout.LayoutParams).height = LinearLayout.LayoutParams.WRAP_CONTENT
            }

            layoutManager = LinearLayoutManager(context)
            adapter = contactItemAdapter

            val itemDecoration = LinearItemDecoration.Builder()
                .setItemPaddingLeft(dp40)
                .setItemPaddingRight(dp40)
                .setItemPaddingTop(dp16)
                .setItemPaddingBottom(dp16)
                .setFirstItemPaddingTop(dp24)
                .setLastItemPaddingBottom(dp24)
                .create()
            addItemDecoration(itemDecoration)
        }
        contactItemAdapter!!.setData(data)
    }

    @SuppressLint("Range")
    private fun RecyclerView.resetCardLayoutHeight(
        dp16: Int = context.resources.getDimensionPixelSize(R.dimen.dp_16),
        dp24: Int = context.resources.getDimensionPixelSize(R.dimen.dp_24)) {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_contact, null, false)
        val parentView = (parent as LinearLayout).parent as ViewGroup
        val parentViewWidth = parentView.width
        Log.d(TAG, "resetCardLayoutHeight: parentView :$parentViewWidth")
        val w = View.MeasureSpec.makeMeasureSpec(parentViewWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        itemView.measure(w, h)
        val measuredHeight = itemView.measuredHeight
        val measuredWidth = itemView.measuredWidth
        Log.d(TAG,
            "resetCardLayoutHeight: itemView measuredWidth:$measuredWidth, measuredHeight:$measuredHeight"
        )

        val cardLayoutParams = layoutParams as LinearLayout.LayoutParams
        cardLayoutParams.height = 5 * itemView.measuredHeight + 5 * (dp16 + dp16) + dp24
    }

    override fun refreshCard(data: List<Contact>) {
        Log.d(TAG, "refreshCard: contact size:${data.size}")

        rvCard?.let {
            if (data.size <= 5) {
                (it.layoutParams as LinearLayout.LayoutParams).height = LinearLayout.LayoutParams.WRAP_CONTENT
            } else it.resetCardLayoutHeight()
            contactItemAdapter?.setData(data)
        }
    }

    override fun scrollCard(direction: Int) : Boolean {
        LogUtils.d("scrollCard direction:$direction")

        when (direction) {
            CardDirection.DIRECTION_UP -> {
                rvCard?.let {
                    val canScrollVertically = it.canScrollVertically(1)
                    LogUtils.d("scrollCard can scroll up:$canScrollVertically")
                    if (canScrollVertically) {
                        it.smoothScrollBy(0, 100)
                        return true
                    } else {
                        LogUtils.d("scrollCard，已经到底了...")
                        cardScrollCallback?.invoke(direction, false)
                        return false
                    }
                }
            }

            CardDirection.DIRECTION_DOWN -> {
                rvCard?.let {
                    val canScrollVertically = it.canScrollVertically(-1)
                    LogUtils.d("scrollCard can scroll down:$canScrollVertically")
                    if (canScrollVertically) {
                        it.smoothScrollBy(0, -100)
                        return true
                    } else {
                        LogUtils.d("scrollCard, 已经到顶了...")
                        cardScrollCallback?.invoke(direction, false)
                        return false
                    }
                }
            }

        }

        return false
    }

    override fun onCardCollapse() {
    }


}