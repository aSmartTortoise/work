package com.voyah.window.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.Contact
import com.voyah.voice.card.view.RecyclerViewAtViewPager2
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseRecyclerViewAdapter
import com.voyah.window.R
import com.voyah.window.databinding.ItemMultimediaOuterBinding
import com.voyah.window.itemdecoration.DomainItemDecoration
import com.voyah.window.itemdecoration.GridSpaceDecoration

/**
 *  author : jie wang
 *  date : 2024/6/21 16:15
 *  description :
 */
class ContactOuterCardAdapter
    : BaseRecyclerViewAdapter<List<Contact>, ItemMultimediaOuterBinding>() {

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemMultimediaOuterBinding>,
        item: List<Contact>?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {
            bindInnerContainer(rvContainer, item)
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemMultimediaOuterBinding.inflate(layoutInflater, parent, false)

    private fun bindInnerContainer(recyclerView: RecyclerViewAtViewPager2,
                                   contacts: List<Contact>) {
        recyclerView.apply {

            layoutManager = LinearLayoutManager(context)

            if (itemDecorationCount == 0) {
                addItemDecoration(DomainItemDecoration(context))
            }

            adapter = DomainMultiItemAdapter().also { multimediaAdapter ->
                multimediaAdapter.setData(contacts)
                multimediaAdapter.onItemClickListener = {view, position ->
                    LogUtils.d("onItemClick position:$position")
                    this@ContactOuterCardAdapter.onItemClickListener?.invoke(view, position)
                }
            }

        }
    }
}