package com.voyah.window.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.Contact
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.ScheduleInfo
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.Weather
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.cockpit.window.util.DateUtil
import com.voyah.window.R
import com.voyah.window.databinding.ItemContactBinding
import com.voyah.window.databinding.ItemScheduleMoreBinding
import com.voyah.window.databinding.ItemScheduleType1Binding
import com.voyah.window.databinding.ItemScheduleType2Binding
import com.voyah.window.databinding.ItemScheduleType3Binding
import com.voyah.window.databinding.ItemWeatherType1Binding
import com.voyah.window.databinding.ItemWeatherType2Binding
import com.voyah.window.databinding.ItemWeatherType3Binding
import com.voyah.window.databinding.ScheduleRightPart1Binding
import com.voyah.window.databinding.ScheduleRightPart2Binding
import java.lang.IllegalStateException

/**
 *  author : jie wang
 *  date : 2024/4/2 9:32
 *  description :
 */
class DomainMultiItemAdapter : BaseMultiItemAdapter<MultiItemEntity>() {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        if (item == null) return
        when (item.itemType) {
            ViewType.WEATHER_TYPE_1 -> {
                (holder.binding as ItemWeatherType1Binding).apply {
                    item as Weather
                    tvTemp.text = "${item.tempLow}°/${item.tempHigh}°"
                    tvDate.text = item.formatDate
                    tvWeatherDesc.text = item.weatherDesc
                    ivWeatherIcon.setImageResource(item.weatherIcon)
                    tvLoaction.text = item.location
                }
            }

            ViewType.WEATHER_TYPE_2 -> {
                (holder.binding as ItemWeatherType2Binding).apply {
                    item as Weather
                    tvTemp.text = "${item.tempLow}°/${item.tempHigh}°"
                    tvLocation.text = item.location
                }
            }

            ViewType.WEATHER_TYPE_3 -> {
                (holder.binding as ItemWeatherType3Binding).apply {
                    item as Weather
                    tvDate.text = item.formatDate
                    tvTemp.text = "${item.tempLow}°/${item.tempHigh}°"
                    tvWeatherDesc.text = item.weatherDesc
                    ivWeatherIcon.setImageResource(item.weatherIcon)
                }
            }

            ViewType.BT_PHONE_TYPE -> {
                (holder.binding as ItemContactBinding).apply {
                    item as Contact
                    tvIndex.text = "${position + 1}"
                    tvContactName.text = item.contactName
                    tvNumber.text = "${item.phoneType} ${item.number}"
                }
            }

            ViewType.SCHEDULE_TYPE_1 -> {
                (holder.binding as ItemScheduleType1Binding).apply {
                    item as ScheduleInfo
                    val timeStr = item.time
                    val timeStamp = DateUtil.getTimeStamp(timeStr)
                    val day = DateUtil.getDay(timeStamp)
                    tvDate.text = "$day"

                    ScheduleRightPart1Binding.inflate(
                        LayoutInflater.from(this.root.context),
                        this.root,
                        true).let { rightPartBinding ->
                        rightPartBinding.tvEvent.text = item.event
                        val month = DateUtil.getMonth(timeStamp) + 1
                        val customTimeStr = DateUtil.getCustomTimeStr(timeStamp)
                        rightPartBinding.tvTime.text = String.format("%d月%d日 %s",
                            month, day, customTimeStr)
                    }
                }
            }

            ViewType.SCHEDULE_TYPE_2 -> {
                (holder.binding as ItemScheduleType2Binding).apply {
                    item as ScheduleInfo
                    val timeStr = item.time
                    LogUtils.d("schedule type 2 timeStr:$timeStr")
                    val timeStamp = DateUtil.getTimeStamp(timeStr)
                    val day = DateUtil.getDay(timeStamp)
                    val month = DateUtil.getMonth(timeStamp) + 1
                    val customTimeStr = DateUtil.getCustomTimeStr(timeStamp)
                    tvTime.text = String.format("%d月%d日\n%s",
                        month, day, customTimeStr)
                    ScheduleRightPart2Binding.inflate(
                        LayoutInflater.from(this.root.context), cl, true
                    ).let { rightPartBinding ->
                        LogUtils.d("onBindDefViewHolder event:${item.event}")
                        rightPartBinding.tvEvent.text = item.event
                        val drawableLocation = root.context.resources.getDrawable(
                            R.drawable.icon_schedule_location, null
                        )
                        rightPartBinding.tvLocation.text = item.location
                        val dp32 =
                            root.context.resources.getDimensionPixelSize(R.dimen.dp_32)
                        drawableLocation.setBounds(0, 0, dp32, dp32)
                        rightPartBinding.tvLocation.setCompoundDrawables(
                            drawableLocation,
                            null, null, null
                        )
                        rightPartBinding.tvLocation.compoundDrawablePadding =
                            root.context.resources.getDimensionPixelSize(R.dimen.dp_8)

                    }
                }
            }

            ViewType.SCHEDULE_TYPE_3 -> {
                (holder.binding as ItemScheduleType3Binding).apply {
                    item as ScheduleInfo
                    tvEvent.text = item.event
                }
            }

            ViewType.SCHEDULE_TYPE_MORE -> {
                (holder.binding as ItemScheduleMoreBinding).apply {
                }
            }
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            ViewType.WEATHER_TYPE_1 -> ItemWeatherType1Binding.inflate(layoutInflater, parent, false)
            ViewType.WEATHER_TYPE_2 -> ItemWeatherType2Binding.inflate(layoutInflater, parent, false)
            ViewType.WEATHER_TYPE_3 -> ItemWeatherType3Binding.inflate(layoutInflater, parent, false)
            ViewType.BT_PHONE_TYPE -> ItemContactBinding.inflate(layoutInflater, parent, false)
            ViewType.SCHEDULE_TYPE_1 -> ItemScheduleType1Binding.inflate(layoutInflater, parent, false)
            ViewType.SCHEDULE_TYPE_2 -> ItemScheduleType2Binding.inflate(layoutInflater, parent, false)
            ViewType.SCHEDULE_TYPE_3 -> ItemScheduleType3Binding.inflate(layoutInflater, parent, false)
            ViewType.SCHEDULE_TYPE_MORE -> ItemScheduleMoreBinding.inflate(layoutInflater, parent, false)
            else -> throw IllegalStateException("not support view type.")
        }
    }

}