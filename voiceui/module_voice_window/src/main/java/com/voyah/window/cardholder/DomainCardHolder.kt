package com.voyah.window.cardholder

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.Weather
import com.voyah.window.R
import com.voyah.window.adapter.DomainMultiItemAdapter
import com.voyah.window.itemdecoration.DomainItemDecoration

/**
 *  author : jie wang
 *  date : 2024/2/29 19:55
 *  description : 处理天气card
 */
class DomainCardHolder(context: Context) : BaseCardHolder<List<MultiItemEntity>>(context) {

    private var rvCard: RecyclerView? = null
    private var domainItemAdapter: DomainMultiItemAdapter? = null
    companion object {
        const val TAG = "DomainCardViewHolder"
    }

    override fun getCardView(): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.card_root, null, false)
        initView(cardView)
        return cardView
    }
    override fun initView(rootView: View) {
        rvCard = rootView.findViewById(R.id.rv_card)
    }

    override fun expandCard(data: List<MultiItemEntity>) {
        require(!TextUtils.isEmpty(domainType)) {
            LogUtils.w("need to init domain type ...")
        }
        require(data.isNotEmpty()) {
            LogUtils.w("data is empty ...")
        }

        var drawTimeLineFlag = false
        when (domainType) {
            DomainType.DOMAIN_TYPE_WEATHER -> {

                //todo 天气现象icon需要确认。
                if (data.size == 1 && data[0].itemType == ViewType.WEATHER_TYPE_1) {
                    (data[0] as Weather).weatherIcon = R.drawable.icon_weather_cloudy_l
                } else if (data[0].itemType == ViewType.WEATHER_TYPE_2) {
                    for (item in data) {
                        (item as Weather).weatherIcon = R.drawable.icon_weather_cloudy_s
                    }
                }
            }

            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                if (data.size > 1) {
                    drawTimeLineFlag = true
                }
            }
        }

        domainItemAdapter = DomainMultiItemAdapter().apply {
            onItemClickListener = { _, position ->
                LogUtils.d("onItemClick position:$position")
                getItem(position)?.let { itemEntity ->
                    val itemType = itemEntity.itemType
                    LogUtils.d("onItemClick itemType:$itemType")
                    domainItemCallback?.invoke(position, itemType)
                }
            }
        }
        rvCard?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = domainItemAdapter
            addItemDecoration(DomainItemDecoration(context).also { decoration ->
                decoration.drawTimeLineFlag = drawTimeLineFlag
            })
        }
        domainItemAdapter!!.setData(data)
    }

    override fun refreshCard(data: List<MultiItemEntity>) {
        domainItemAdapter?.setData(data)
    }

    override fun scrollCard(direction: Int): Boolean {
        LogUtils.d("scrollCard direction:$direction")
        return false
    }

    override fun onCardCollapse() {
    }


}