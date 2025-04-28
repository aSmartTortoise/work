package com.voyah.window.cardholder

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.Contact
import com.voyah.window.R


/**
 *  author : jie wang
 *  date : 2024/3/8 16:56
 *  description : 处理蓝牙电话card
 */
class BTPhoneCardHolder(context: Context) : BasePageCardHolder<Contact>(context) {

    private val maxCardHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_560)
    }

    private val top: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_32)
    }

    private val bottom: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_48)
    }

    private val itemHeight: Int  by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_120)
    }

    companion object {
        const val TAG = "BTPhoneCardViewHolder"
    }

    override fun bindData(data: List<Contact>) {
        LogUtils.d("bindData")
        chunkSize = 4
        super.bindData(data)
    }

    override fun getCardHeight(data: List<Contact>, parentViewWidth: Int): Int {
        val height = if (data.size >= 4) {
            maxCardHeight
        } else {
            data.size * itemHeight + top + bottom
        }
        LogUtils.d("getCardHeight height:$height")
        return height
    }

    override fun onUIModeChange(nightModeFlag: Boolean) {
    }


}