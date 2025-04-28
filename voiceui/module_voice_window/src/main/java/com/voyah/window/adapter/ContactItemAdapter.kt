package com.voyah.window.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseRecyclerViewAdapter
import com.voyah.window.databinding.ItemContactBinding
import com.voyah.cockpit.window.model.Contact

/**
 *  author : jie wang
 *  date : 2024/3/5 20:33
 *  description :
 */
class ContactItemAdapter : BaseRecyclerViewAdapter<Contact, ItemContactBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemContactBinding>,
        item: Contact?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {
            tvIndex.text = "${position + 1}"
            tvContactName.text = item.contactName
            tvNumber.text = "${item.phoneType} ${item.number}"
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemContactBinding.inflate(layoutInflater, parent, false)
}