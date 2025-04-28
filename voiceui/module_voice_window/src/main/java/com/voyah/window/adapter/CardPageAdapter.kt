package com.voyah.window.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.Contact
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.MultiMusicInfo
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.cockpit.window.model.ViewType
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
class CardPageAdapter : BaseRecyclerViewAdapter<List<MultiItemEntity>, ItemMultimediaOuterBinding>() {

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemMultimediaOuterBinding>,
        item: List<MultiItemEntity>?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {

            when (item[0].itemType) {
                ViewType.BT_PHONE_TYPE -> {
                    item as List<Contact>
                    bindInnerContainerContact(rvContainer, item)
                }
                ViewType.MEDIA_TYPE -> {
                    item as List<MultimediaInfo>
                    bindInnerContainerMultimedia(rvContainer, item)
                }
                ViewType.MUSIC_TYPE -> {
                    item as List<MultiMusicInfo>
                    bindInnerContainerMultiMusic(rvContainer, item)
                }
            }

        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemMultimediaOuterBinding.inflate(layoutInflater, parent, false)

    private fun bindInnerContainerMultimedia(recyclerView: RecyclerViewAtViewPager2,
                                             multimediaInfos: List<MultimediaInfo>) {
        recyclerView.apply {
            LogUtils.d("bindInnerContainerMultimedia")
            layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(p0: Int): Int {
                        return 1
                    }

                }
            }

            if (itemDecorationCount == 0) {
                val left = context.resources.getDimensionPixelSize(R.dimen.dp_48)
                val top = context.resources.getDimensionPixelSize(R.dimen.dp_32)
                val right = context.resources.getDimensionPixelSize(R.dimen.dp_46)
                val bottom = context.resources.getDimensionPixelSize(R.dimen.dp_46)
                val windowWidth = context.resources.getDimensionPixelSize(R.dimen.dp_1067)
                val windowHeight = context.resources.getDimensionPixelSize(R.dimen.dp_744)
                val itemWidth = context.resources.getDimensionPixelSize(R.dimen.dp_198)
                val horizontal = (windowWidth - 4 * itemWidth - left - right) / 3
                val vertical = context.resources.getDimensionPixelSize(R.dimen.dp_32)

                addItemDecoration(GridSpaceDecoration(
                    horizontal, vertical, left, right, top, bottom
                ))
            }

            adapter = DomainMultiItemAdapter().also { multimediaAdapter ->
                multimediaAdapter.setData(multimediaInfos)
                multimediaAdapter.onItemClickListener = {view, position ->
                    LogUtils.d("onItemClick position:$position")
                    this@CardPageAdapter.onItemClickListener?.invoke(view, position)
                }
            }
        }
    }

    private fun bindInnerContainerContact(recyclerView: RecyclerViewAtViewPager2,
                                          item: List<Contact>) {
        recyclerView.apply {
            LogUtils.d("bindInnerContainerContact")
            layoutManager = LinearLayoutManager(context)

            if (itemDecorationCount == 0) {
                addItemDecoration(DomainItemDecoration(context))
            }

            adapter = DomainMultiItemAdapter().also { multimediaAdapter ->
                multimediaAdapter.setData(item)
                multimediaAdapter.onItemClickListener = {view, position ->
                    LogUtils.d("onItemClick position:$position")
                    this@CardPageAdapter.onItemClickListener?.invoke(view, position)
                }
            }

        }

    }

    private fun bindInnerContainerMultiMusic(recyclerView: RecyclerViewAtViewPager2,
                                             multimediaInfos: List<MultiMusicInfo>) {
        recyclerView.apply {
            LogUtils.d("bindInnerContainerMultiMusic")
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(p0: Int): Int {
                        return 1
                    }

                }
            }

            if (itemDecorationCount == 0) {
                addItemDecoration(GridSpaceDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.dp_48), context.resources.getDimensionPixelSize(R.dimen.dp_48), 0, 0
                ))
            }

            adapter = DomainMultiItemAdapter().also { multimusicAdapter ->
                multimusicAdapter.setData(multimediaInfos)
                multimusicAdapter.onItemClickListener = {view, position ->
                    LogUtils.d("onItemClick position:$position")
                    this@CardPageAdapter.onItemClickListener?.invoke(view, position)
                }
            }
        }
    }
}