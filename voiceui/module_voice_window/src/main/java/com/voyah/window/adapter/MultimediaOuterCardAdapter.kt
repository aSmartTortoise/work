package com.voyah.window.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseRecyclerViewAdapter
import com.voyah.window.databinding.ItemMultimediaBinding

/**
 *  author : jie wang
 *  date : 2024/6/21 16:15
 *  description :
 */
class MultimediaOuterCardAdapter : BaseRecyclerViewAdapter<List<MultimediaInfo>, ItemMultimediaBinding>() {

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemMultimediaBinding>,
        item: List<MultimediaInfo>?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {

            LogUtils.d("onBindDefViewHolder ")
            tv.text = "$position"
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemMultimediaBinding.inflate(layoutInflater, parent, false)
}