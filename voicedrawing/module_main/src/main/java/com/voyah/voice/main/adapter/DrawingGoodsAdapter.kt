package com.voyah.voice.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseRecyclerViewAdapter
import com.voyah.voice.main.databinding.ItemDrawingGoodsBinding
import com.voyah.voice.main.ext.setStateAnimatorList
import com.voyah.voice.main.model.DrawingTimesGoods

/**
 *  author : jie wang
 *  date : 2024/8/30 20:10
 *  description :
 */
class DrawingGoodsAdapter : BaseRecyclerViewAdapter<DrawingTimesGoods, ItemDrawingGoodsBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemDrawingGoodsBinding>,
        item: DrawingTimesGoods?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {
            tvGoodsName.text = item.goodsName
            tvPrice.text = "￥${item.price}"
            tvDesc.text = item.desc
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDrawingGoodsBinding {
        val goodsBinding = ItemDrawingGoodsBinding.inflate(layoutInflater, parent, false)
        goodsBinding.root.setStateAnimatorList()
        return goodsBinding
    }
}