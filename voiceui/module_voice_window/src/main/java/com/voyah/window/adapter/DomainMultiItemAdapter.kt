package com.voyah.window.adapter

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.voyah.cockpit.window.model.ChatMessage
import com.voyah.cockpit.window.model.Contact
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.MultiMusicInfo
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.cockpit.window.model.ScheduleInfo
import com.voyah.cockpit.window.model.StockInfo
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.Weather
import com.voyah.cockpit.window.util.DateUtil
import com.voyah.markdown.parser.markwon.DetailsParsingSpan
import com.voyah.markdown.parser.markwon.MarkwonHolder
import com.voyah.voice.framework.adapter.BaseBindViewHolder
import com.voyah.voice.glide.setUrlRound
import com.voyah.window.R
import com.voyah.window.cardholder.DomainCardHolder
import com.voyah.window.cardholder.DomainCardHolder.Companion.customExpand
import com.voyah.window.cardholder.DomainCardHolder.Companion.isThinkingExpand
import com.voyah.window.databinding.ItemChatMainBinding
import com.voyah.window.databinding.ItemContactBinding
import com.voyah.window.databinding.ItemMultimediaInnerBinding
import com.voyah.window.databinding.ItemMultimusicInnerBinding
import com.voyah.window.databinding.ItemScheduleMoreBinding
import com.voyah.window.databinding.ItemScheduleType1Binding
import com.voyah.window.databinding.ItemScheduleType2Binding
import com.voyah.window.databinding.ItemScheduleType3Binding
import com.voyah.window.databinding.ItemStockBinding
import com.voyah.window.databinding.ItemWeatherTypeNew1Binding
import com.voyah.window.databinding.ItemWeatherTypeNew2Binding
import com.voyah.window.databinding.ItemWeatherTypeNew3Binding
import com.voyah.window.databinding.ScheduleRightPart1Binding
import com.voyah.window.databinding.ScheduleRightPart2Binding
import com.voyah.window.setStateAnimatorList
import com.voyah.window.util.WeatherConfigUtil
import com.voyah.window.util.AnimatorUtil
import io.noties.markwon.Markwon
import java.util.Locale


/**
 *  author : jie wang
 *  date : 2024/4/2 9:32
 *  description :
 */
class DomainMultiItemAdapter : BaseMultiItemAdapter<MultiItemEntity>() {

    var rootView: RecyclerView? = null

    private var markwon: Markwon? = null
    var itemBindingListener: ItemBindingListener? = null
    private var deepThinking: Boolean = false
    private var deepThinkingStartTime: Long = 0

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        if (item == null) return
        when (item.itemType) {
            ViewType.WEATHER_TYPE_1 -> {
                onBindItemWeather1(holder, item, position)
            }

            ViewType.WEATHER_TYPE_2 -> {
                onBindItemWeather2(holder, item, position)
            }

            ViewType.WEATHER_TYPE_3 -> {
                onBindItemWeather3(holder, item, position)
            }

            ViewType.BT_PHONE_TYPE -> {
                onBindItemBTPhone(holder, item, position)
            }

            ViewType.SCHEDULE_TYPE_1 -> {
                onBindItemSchedule1(holder, item, position)
            }

            ViewType.SCHEDULE_TYPE_2 -> {
                onBindItemSchedule2(holder, item, position)
            }

            ViewType.SCHEDULE_TYPE_3 -> {
                onBindItemSchedule3(holder, item, position)
            }

            ViewType.SCHEDULE_TYPE_MORE -> {
                (holder.binding as ItemScheduleMoreBinding).apply {
                }
            }

            ViewType.MEDIA_TYPE -> {
                onBindItemMediaVideo(holder, item, position)
            }

            ViewType.STOCK_TYPE -> {
                onBindItemStock(holder, item, position)
            }

            ViewType.CHAT_TYPE_MAIN -> {
                onBindItemChatMain(holder, item, position)
            }

            ViewType.MUSIC_TYPE -> {
                onBindItemMediaMusic(holder, item, position)
            }
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            ViewType.WEATHER_TYPE_1 -> ItemWeatherTypeNew1Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.WEATHER_TYPE_2 -> ItemWeatherTypeNew2Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.WEATHER_TYPE_3 -> ItemWeatherTypeNew3Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.BT_PHONE_TYPE -> {
                val contactBinding = ItemContactBinding.inflate(layoutInflater, parent, false)
                contactBinding.root.setStateAnimatorList()
                contactBinding
            }
            ViewType.SCHEDULE_TYPE_1 -> ItemScheduleType1Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.SCHEDULE_TYPE_2 -> ItemScheduleType2Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.SCHEDULE_TYPE_3 -> ItemScheduleType3Binding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.SCHEDULE_TYPE_MORE -> ItemScheduleMoreBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            ViewType.MEDIA_TYPE -> {
                val mediaBinding = ItemMultimediaInnerBinding.inflate(layoutInflater, parent, false)
                mediaBinding.root.setStateAnimatorList()
                mediaBinding
            }
            ViewType.MUSIC_TYPE -> {
                val mediaBinding = ItemMultimusicInnerBinding.inflate(layoutInflater, parent, false)
                mediaBinding.root.setStateAnimatorList()
                mediaBinding
            }
            ViewType.STOCK_TYPE -> ItemStockBinding.inflate(layoutInflater, parent, false)
            ViewType.CHAT_TYPE_MAIN -> {
                val chatMainBinding = ItemChatMainBinding.inflate(layoutInflater, parent, false)
                itemBindingListener?.onGetViewHolder(chatMainBinding.root)
                chatMainBinding
            }
            else -> throw IllegalStateException("not support view type.")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemWeather1(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemWeatherTypeNew1Binding).apply {
            item as Weather
            val tempLowStr = if (item.tempLow == null) "--" else "${item.tempLow}°"
            val tempHighStr = if (item.tempHigh == null) "--" else "${item.tempHigh}°"
            SpanUtils.with(tvTemp)
                .append(tempLowStr)
                .setForegroundColor(root.context.resources.getColor(R.color.weather_temp_low))
                .append(" $tempHighStr")
                .setForegroundColor(root.context.resources.getColor(R.color.weather_temp_high))
                .create()

            val weatherDate = DateUtil.getCustomStr1(item.formatDate)
            tvDate.text = if (TextUtils.isEmpty(weatherDate)) "--"
            else weatherDate

            val dayFlag = isDayTime(item.formatDate)
            val weatherDesc = if (dayFlag) item.weatherDay else item.weatherNight

            tvWeather.text = if (TextUtils.isEmpty(weatherDesc)) "--"
            else weatherDesc

            WeatherConfigUtil.setWeatherIcon(item)

            ivWeatherIcon.setImageResource(
                if (dayFlag) {
                    item.weatherDayIcon
                } else item.weatherNightIcon
            )
            tvLocation.text = if (TextUtils.isEmpty(item.location)) "--"
            else item.location
            val wind = if (dayFlag) {
                val windDirDayStr =
                    if (TextUtils.isEmpty(item.windDirDay)) "--" else item.windDirDay
                val windLevelDayStr = if (TextUtils.isEmpty(item.windLevelDay)) "--" else
                    "${item.windLevelDay}级"
                val windDay = "$windDirDayStr $windLevelDayStr"
                windDay
            } else {
                val windDirNightStr =
                    if (TextUtils.isEmpty(item.windDirNight)) "--" else item.windDirNight
                val windLevelNightStr = if (TextUtils.isEmpty(item.windLevelNight)) "--" else
                    "${item.windLevelNight}级"
                val windNight = "$windDirNightStr $windLevelNightStr"
                windNight
            }
            tvWind.text = wind
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemWeather2(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemWeatherTypeNew2Binding).apply {
            item as Weather
            tvLocation.text = if (TextUtils.isEmpty(item.location)) "--"
            else item.location
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemWeather3(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemWeatherTypeNew3Binding).apply {
            item as Weather
            val originalDate = item.formatDate
            tvDate.text = if (TextUtils.isEmpty(originalDate)) "--"
            else DateUtil.getCustomStr2(originalDate)
            tvTempLow.text = if (item.tempLow == null) "--" else "${item.tempLow}°"
            tvTempHigh.text = if (item.tempHigh == null) "--" else "${item.tempHigh}°"


            if (item.tempLow == null
                || item.tempHigh == null
                || item.tempLowDateRange == null
                || item.tempHighDateRange == null) {
                LogUtils.w("onBindItemWeather3 ds temp data error... some temp data is null")
            } else {
                if (item.tempHighDateRange < item.tempHigh
                    || item.tempLowDateRange > item.tempLow
                    || item.tempHigh < item.tempLow
                    || item.tempHighDateRange < item.tempLowDateRange) {
                    LogUtils.w("onBindItemWeather3 ds temp data error...")
                } else {
                    tempView.setTemp(
                        item.tempHighDateRange,
                        item.tempLowDateRange,
                        item.tempHigh,
                        item.tempLow
                    )
                }
            }


            WeatherConfigUtil.setWeatherIcon(item)
            val dayFlag = isDayTime(item.formatDate)
            ivWeatherIcon.setImageResource(
                if (dayFlag) {
                    item.weatherDayIcon
                } else item.weatherNightIcon
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemBTPhone(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemContactBinding).apply {
            item as Contact
            tvIndex.text = "${position + 1}"
            tvContactName.text = item.contactName
            tvNumber.text = "${item.phoneType} ${item.number}"
        }
    }

    private fun onBindItemSchedule1(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemScheduleType1Binding).apply {
            item as ScheduleInfo
            val timeStr = item.time
            val timeStamp = DateUtil.getTimeStamp(timeStr)
            val day = DateUtil.getDay(timeStamp)

            ScheduleRightPart1Binding.inflate(
                LayoutInflater.from(this.root.context),
                this.root,
                true
            ).let { rightPartBinding ->
                rightPartBinding.tvEvent.text = item.event
                val year = DateUtil.getYear(timeStamp)
                val month = DateUtil.getMonth(timeStamp) + 1
                var hour = DateUtil.getHour(timeStamp)
                val minute = DateUtil.getMinute(timeStamp)
                val timeType = Settings.System.getString(
                    root.context.contentResolver,
                    Settings.System.TIME_12_24)
                LogUtils.d("onBindItemSchedule1 timeType:$timeType")
                val customTimeStr = DateUtil.getCustomTimeStr(timeStamp, TextUtils.equals(timeType, "24"))
                rightPartBinding.tvTime.text = String.format(
                    Locale.CHINA,
                    "%d年%d月%d日 %s",
                    year,
                    month,
                    day,
                    customTimeStr
                )
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onBindItemSchedule2(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemScheduleType2Binding).apply {
            item as ScheduleInfo
            val timeStr = item.time
            LogUtils.d("schedule type 2 timeStr:$timeStr")
            val timeStamp = DateUtil.getTimeStamp(timeStr)
            val day = DateUtil.getDay(timeStamp)
            val month = DateUtil.getMonth(timeStamp) + 1
            val customTimeStr = DateUtil.getCustomTimeStr(timeStamp, true)
            tvTime.text = String.format(
                Locale.CHINA,
                "%d月%d日\n%s",
                month,
                day,
                customTimeStr
            )
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

    private fun onBindItemSchedule3(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemScheduleType3Binding).apply {
            item as ScheduleInfo
            tvEvent.text = item.event
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemMediaVideo(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemMultimediaInnerBinding).apply {
            item as MultimediaInfo
            tvName.text = item.name
            tvIndex.text = "${position + 1}"
            ivSource.setImageResource(
                if (item.sourceType == MultimediaInfo.SourceType.TENCENT)
                    R.drawable.icon_multimedia_source_tencent
                else R.drawable.icon_multimedia_source_iqiyi
            )
            when (item.type) {
                MultimediaInfo.MediaType.TV_DRAMA -> {
                    tvType.text = "电视剧 | 共${item.episodes}集"
                }

                MultimediaInfo.MediaType.MOVIE -> {
                    tvType.text = "电影"
                }

                else -> {
                    val typeOther = if (item.episodes < 1) {
                        "其他"
                    } else "其他 | 共${item.episodes}集"
                    tvType.text = typeOther
                }
            }
            iv.setUrlRound(
                item.imgUrl,
                root.context.resources.getDimensionPixelSize(R.dimen.dp_16)
            )
            when (item.tagType) {
                MultimediaInfo.TagType.VIP -> {
                    ivTag.setImageResource(R.drawable.icon_multimedia_tag_vip)
                }

                MultimediaInfo.TagType.PAYMENT -> {
                    ivTag.setImageResource(R.drawable.icon_multimedia_tag_payment)
                }

                MultimediaInfo.TagType.SOLE_BROADCAST -> {
                    if (item.sourceType == MultimediaInfo.SourceType.TENCENT) {
                        ivTag.setImageResource(R.drawable.icon_multimedia_tag_sole_broadcast_tencent)
                    } else {
                        ivTag.setImageResource(R.drawable.icon_multimedia_tag_sole_broadcast_iqiyi)
                    }
                }
            }
        }
    }

    private fun onBindItemMediaMusic(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity?,
        position: Int
    ) {
        (holder.binding as ItemMultimusicInnerBinding).apply {
            item as MultiMusicInfo
            tvMusic.text = item.name
            tvIndex.text = "${position + 1}"
            ivMusic.setUrlRound(
                item.imgUrl,
                root.context.resources.getDimensionPixelSize(R.dimen.dp_12)
            )
            ivVip.visibility = if(item.isVip) View.VISIBLE else View.GONE
            tvArtist.text = item.artist + "·" + item.album
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindItemStock(
        holder: BaseBindViewHolder<ViewBinding>,
        item: MultiItemEntity,
        position: Int
    ) {
        (holder.binding as ItemStockBinding).apply {
            item as StockInfo
            tvStockName.text = if (TextUtils.isEmpty(item.name)) "--"
            else item.name

            tvStockCode.text = if (TextUtils.isEmpty(item.code)) "--"
            else item.code

            tvDate.text = if (TextUtils.isEmpty(item.date)) "--"
            else parseStockDate(item.date)

            var priceColor = root.context.resources.getColor(R.color.stock_flat)

            var currency = when(item.currency) {
                "CNY" -> "元"
                "HKD" -> "港元"
                "USD" -> "美元"
                else -> "元"
            }
            if (item.priceAmplitude != null) {
                when {
                    item.priceAmplitude < 0 -> {
                        priceColor = root.context.resources.getColor(R.color.stock_fall)
                    }

                    item.priceAmplitude > 0 -> {
                        priceColor = root.context.resources.getColor(R.color.stock_rise)
                    }

                    else -> {
                        LogUtils.d("onBindItemStock priceAmplitude is 0 ")
                    }
                }
            } else {
                currency = ""
            }

            val stockPriceStr = if (item.price == null) "--" else "${item.price}"
            SpanUtils.with(tvStockPrice)
                .append(stockPriceStr)
                .setFontSize(80, true)
                .setForegroundColor(priceColor)
                .append(currency)
                .setFontSize(28, true)
                .setForegroundColor(priceColor)
                .create()
            val priceAmplitudeStr = if (item.priceAmplitude == null) "--" else "${item.priceAmplitude}"
            SpanUtils.with(tvPriceAmplitude)
                .append(priceAmplitudeStr)
                .setForegroundColor(priceColor)
                .create()
            val priceRateStr = if (item.amplitudeRate == null) "--" else "${item.amplitudeRate}%"
            SpanUtils.with(tvPriceRate)
                .append(priceRateStr)
                .setForegroundColor(priceColor)
                .create()
        }

    }

    private fun parseStockDate(date: String): String {
        val dateFormat = if (date.contains(' ')) {
            DateUtil.getDateCustomized(DateUtil.getTimeStamp(date))
        } else {
            date
        }
        return dateFormat
    }

    private fun onBindItemChatMain(holder: BaseBindViewHolder<ViewBinding>,
                                   item: MultiItemEntity,
                                   position: Int) {
        (holder.binding as ItemChatMainBinding).apply {
            item as ChatMessage
            item.content?.let {
                render(it, tvContentMain)
            }
            item.noTaskReasonText?.let {
                render(it, tvReasoning)
                DomainCardHolder.reasonText = it
            }
            if (item.isModelDeepFirstReason) {
                llThinkingState.visibility = View.VISIBLE
                llReasoning.visibility = View.VISIBLE
                deepThinkingStartTime = SystemClock.elapsedRealtime()
                deepThinking = true
                setThinkingAnimation(imgThinking)
                tvReasoningState.text = "深度思考中···"
                LogUtils.d("onBindItemChatMain deepThinkingStartTime:$deepThinkingStartTime")
            }
            if (!TextUtils.isEmpty(item.content) && deepThinking) {
                deepThinking = false
                imgThinking.clearAnimation()
                imgThinking.setImageResource(R.drawable.icon_reasoning)
                tvReasoningState.text = "已深度思考(用时${getDeepThinkingTime()}s)"
//                llReasoning.visibility = View.GONE
                holder?.itemView?.postDelayed({
                    if(customExpand) return@postDelayed
                    AnimatorUtil.collapseLayout(llReasoning, llReasoning.height, 0,
                        onChange = {
                            LogUtils.d("onBindItemChatMain collapseLayout onChange:$it")
                            DomainCardHolder.isThinkingChanging = it
                            if(!it) DomainCardHolder.isThinkingExpand = false
                        })
                    imgExpand.setImageResource(R.drawable.icon_expand_down)
                }, 2000)
//                AnimatorUtil.collapseLayout(llReasoning, llReasoning.height, 0,
//                    onChange = {
//                        LogUtils.d("onBindItemChatMain collapseLayout onChange:$it")
//                        DomainCardHolder.isThinkingChanging = it
//                        if(!it) DomainCardHolder.isThinkingExpand = false
//                    })
//                imgExpand.setImageResource(R.drawable.icon_expand_down)
                LogUtils.d("onBindItemChatMain first content:${item.content}")
            }
        }
    }

    /**
     *  判断指定的日期时间是否是白天
     */
    private fun isDayTime(dateStr: String): Boolean {
        return if (DateUtil.isToday(dateStr)) {
            val currentHour = DateUtil.getHour(System.currentTimeMillis())
            currentHour in 6..18
        } else true
    }

    private fun getDeepThinkingTime(): Long {
        return (SystemClock.elapsedRealtime() - deepThinkingStartTime) / 1000
    }

    private fun setThinkingAnimation(image: ImageView) {
        var animation = RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val interpolator = LinearInterpolator()
        animation.setInterpolator(interpolator)
        animation.duration = 1000
        animation.repeatCount = Animation.INFINITE
        animation.fillAfter = true
        animation.startOffset = 1
        image.startAnimation(animation)
    }

    private fun render(md: String, tv: TextView) {
        if (markwon == null) {
            markwon = MarkwonHolder().getMarkWonInstance(completionBlock = {
                LogUtils.i("on load img completion...")
                itemBindingListener?.onLoadImgCompletion()
            })
        }
        markwon?.let {
            val spanned = it.toMarkdown(md)
            val spans: Array<DetailsParsingSpan> = spanned.getSpans(
                0, spanned.length,
                DetailsParsingSpan::class.java
            )

            // if we have no details, proceed as usual (single text-view)
            if (spans.isEmpty()) {
                LogUtils.d("render spans is empty.")
                // no details
//            val textView: TextView = appendTextView()
                it.setParsedMarkdown(tv, spanned)
                return
            }
            LogUtils.d("render spans not empty.")
        }

    }

}

interface ItemBindingListener {
    fun onGetViewHolder(itemView: View)

    fun onLoadImgCompletion()
}