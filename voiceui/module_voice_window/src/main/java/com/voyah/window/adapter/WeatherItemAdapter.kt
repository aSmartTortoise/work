package com.voyah.window.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.framework.adapter.BaseRecyclerViewAdapter
import com.voyah.cockpit.window.model.Weather
import com.voyah.window.databinding.ItemWeatherType1Binding

/**
 *  author : jie wang
 *  date : 2024/3/5 20:33
 *  description :
 */
class WeatherItemAdapter : BaseRecyclerViewAdapter<Weather, ItemWeatherType1Binding>() {

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemWeatherType1Binding>,
        item: Weather?,
        position: Int
    ) {
        if (item == null) return
        holder.binding.apply {
            tvTemp.text = "${item.tempLow}°/${item.tempHigh}°"
            tvDate.text = item.formatDate
            tvWeatherDesc.text = item.weatherDesc
            ivWeatherIcon.setImageResource(item.weatherIcon)
            tvLoaction.text = item.location
            LogUtils.d("onBindDefViewHolder itemType:${item.itemType}")
        }
    }


    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemWeatherType1Binding.inflate(layoutInflater, parent, false)
}