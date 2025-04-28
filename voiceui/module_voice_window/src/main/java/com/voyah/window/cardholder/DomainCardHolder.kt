package com.voyah.window.cardholder

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.ChatMessage
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.MultiItemEntity
import com.voyah.cockpit.window.model.StreamMode
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.Weather
import com.voyah.voice.framework.animator.NoChangeItemAnimator
import com.voyah.window.R
import com.voyah.window.adapter.DomainMultiItemAdapter
import com.voyah.window.adapter.ItemBindingListener
import com.voyah.window.extension.isChatCard
import com.voyah.window.itemdecoration.DomainChatItemDecoration
import com.voyah.window.itemdecoration.DomainItemDecoration
import com.voyah.window.util.AnimatorUtil
import com.voyah.window.util.SystemConfigUtil
import com.voyah.window.windowholder.BaseVTCWindowHolder.Companion.EXPAND_DOWN
import com.voyah.window.windowholder.BaseVTCWindowHolder.Companion.EXPAND_UP
import kotlinx.coroutines.Job


/**
 *  author : jie wang
 *  date : 2024/2/29 19:55
 *  description :
 */
class DomainCardHolder(context: Context) : BaseCardHolder<List<MultiItemEntity>>(context) {

    var rvCard: RecyclerView? = null
    private var domainItemAdapter: DomainMultiItemAdapter? = null

    var refreshEndBlock: ((verticalScrollEnabled: Boolean) -> Unit)? = null

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            LogUtils.d("onScrollStateChanged newState:$newState")
            interactionCallback?.onScrollStateChange(recyclerView, newState, domainType!!, requestId)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            LogUtils.d("onScrolled dy:$dy")
        }
    }

    val chatContentLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            rvCard?.let { rvCard ->
                setVerticalScrollEnable()
                val itemEntity = domainItemAdapter?.getItem(0)
                if (itemEntity is ChatMessage) {
                    val streamMode = itemEntity.streamMode
                    LogUtils.d("on chat item layout change, streamMode:$streamMode")
                    if (streamMode == StreamMode.NOT_STREAM || streamMode == StreamMode.COMPLETION) {
                        tvChatContent?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                        val verticalScrollEnabled = rvCard.isVerticalScrollBarEnabled
                        LogUtils.d("on chat item layout change, verticalScrollBarEnabled:$verticalScrollEnabled")
                        if (verticalScrollEnabled) {
                            refreshEndBlock?.invoke(true)
                        } else {
                            refreshEndBlock?.invoke(false)
                        }
                    } else if(streamMode == StreamMode.ON_GOING) {
                        if (itemEntity.isModelDeepFirstReason) {
                            updateThinkingView()
                        }
                    }
                }
            }
        }
    }

    var tvChatContent: TextView? = null
    var llThinkingState: LinearLayout? = null
    var tvReasoningState: TextView? = null
    var tvReasoningContent: TextView? = null
    var llReason: LinearLayout? = null
    var imgExpand: ImageView? = null
    var viewMaskUp: View? = null
    var viewMaskDown: View? = null

    val chatBindingListener = object : ItemBindingListener {
        override fun onGetViewHolder(itemView: View) {
            llThinkingState = itemView.findViewById(R.id.ll_thinking_state)
            tvReasoningState = itemView.findViewById(R.id.tv_reasoning_state)
            llReason = itemView.findViewById(R.id.ll_reasoning)
            tvReasoningContent = itemView.findViewById(R.id.tv_reasoning)
            imgExpand = itemView.findViewById(R.id.img_expand)
            tvChatContent = itemView.findViewById(R.id.tv_content_main)
            tvChatContent!!.viewTreeObserver.addOnGlobalLayoutListener(chatContentLayoutListener)
            imgExpand?.setOnClickListener { 
                customExpand = true
                expandChatContent()
            }
        }

        override fun onLoadImgCompletion() {
            LogUtils.i("onLoadImgCompletion")
            rvCard?.let { rvCard ->
                rvCard.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        rvCard.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        setVerticalScrollEnable()
                    }
                })
            }
        }
    }

    private fun setVerticalScrollEnable() {
        rvCard?.let {
            val canScrollDown = it.canScrollVertically(1)
            val canScrollUp = it.canScrollVertically(-1)
            LogUtils.d("setVerticalScrollEnable, canScrollDown:$canScrollDown, canScrollUp:$canScrollUp")
            LogUtils.d("setVerticalScrollEnable, rvCard height:${it.height}")
            if (canScrollDown) {
                val scrollBarEnabled = it.isVerticalScrollBarEnabled
                LogUtils.d("setVerticalScrollEnable scrollBarEnabled:$scrollBarEnabled")
                if (!scrollBarEnabled) {
                    it.isVerticalScrollBarEnabled = true
                    setMuskViewVisible()
                }
                it.smoothScrollBy(0, Int.MAX_VALUE)
            }
        }
    }

    companion object {
        const val TAG = "DomainCardViewHolder"
        var isThinkingChanging: Boolean = false
        var isThinkingExpand: Boolean = true
        var reasonText: String = ""
        var updateJob: Job? = null
        var customExpand: Boolean = false
    }

    override fun getCardView(): View {
        val layoutId = when {
            (domainType == DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM)
                    || (domainType == DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM) -> {
                R.layout.card_root_chat
            }
            isChatCard(domainType) -> R.layout.card_root_chat
            else -> R.layout.card_root
        }
        val cardView = LayoutInflater.from(context).inflate(layoutId, null, false)
        initView(cardView)
        rootView = cardView
        return cardView
    }

    override fun initView(rootView: View) {
        rvCard = rootView.findViewById<RecyclerView?>(R.id.rv_card).apply {
            addOnScrollListener(scrollListener)
            isVerticalScrollBarEnabled = false
        }

        viewMaskUp = rootView.findViewById(R.id.v_mask_up)
        viewMaskDown = rootView.findViewById(R.id.v_mask_down)
    }

    override fun bindData(data: List<MultiItemEntity>) {
        LogUtils.d("bindData")
        if (TextUtils.isEmpty(domainType)) {
            LogUtils.w("need to init domain type ...")
            return
        }
        if (data.isEmpty()) {
            LogUtils.w("data is empty ...")
            return
        }

        domainItemAdapter = DomainMultiItemAdapter().apply {
            rootView = rvCard

            when {
                (domainType == DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM)
                        || (domainType == DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM) -> {
                    itemBindingListener = chatBindingListener
                }

                isChatCard(domainType) -> itemBindingListener = chatBindingListener
            }

        }

        rvCard?.apply {
            var drawTimeLineFlag = false
            var decoration: RecyclerView.ItemDecoration = DomainItemDecoration(context)
            when (domainType) {
                DomainType.DOMAIN_TYPE_SCHEDULE -> {
                    if (data.size > 1) {
                        drawTimeLineFlag = true
                        decoration as DomainItemDecoration
                        decoration.drawTimeLineFlag = drawTimeLineFlag
                    }

                }

                DomainType.DOMAIN_TYPE_WEATHER -> {
                    if (data.size > 8) {
                        isVerticalScrollBarEnabled = true
                    }
                }

                DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM,
                DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM -> {
                    decoration = DomainChatItemDecoration(context)
                }

                else -> {
                    if (isChatCard(domainType)) {
                        itemAnimator = NoChangeItemAnimator()
                        decoration = DomainChatItemDecoration(context)

                    }
                }
            }

            layoutManager = LinearLayoutManager(context)
            adapter = domainItemAdapter

            if (itemDecorationCount == 1) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(decoration)
            if(isChatCard(domainType)) {
                updateThinkingView()
            }

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

    override fun getCardHeight(data: List<MultiItemEntity>, parentViewWidth: Int): Int {
        var cardHeight = LinearLayout.LayoutParams.WRAP_CONTENT
        when(domainType) {
            DomainType.DOMAIN_TYPE_WEATHER -> {
                data[0].let { entity ->
                    entity as Weather
                    cardHeight = when (entity.itemType) {
                        ViewType.WEATHER_TYPE_2 -> {
                            getWeatherDayRangeHeight(
                                itemSize = data.size,
                                parentViewWidth = parentViewWidth)
                        }
                        ViewType.WEATHER_TYPE_1 -> {
                            getWeatherDayHeight(parentViewWidth = parentViewWidth)
                        }
                        else -> LinearLayout.LayoutParams.WRAP_CONTENT
                    }

                }
            }

            DomainType.DOMAIN_TYPE_STOCK -> {
                cardHeight = getStockHeight(parentViewWidth = parentViewWidth)
            }

            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                cardHeight = getScheduleHeight(parentViewWidth = parentViewWidth)
            }
        }
        LogUtils.d("getCardHeight:$cardHeight")
        return cardHeight
    }

    @SuppressLint("Range")
    private fun getWeatherDayRangeHeight(
        vertical: Int = context.resources.getDimensionPixelSize(R.dimen.dp_24),
        top: Int = context.resources.getDimensionPixelSize(R.dimen.dp_32),
        bottom: Int = context.resources.getDimensionPixelSize(R.dimen.dp_48),
        itemSize: Int,
        parentViewWidth: Int
    ): Int {
        val headerView =
            LayoutInflater.from(context).inflate(R.layout.item_weather_type_new2, null, false)
        val w = View.MeasureSpec.makeMeasureSpec(parentViewWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        headerView.measure(w, h)
        val measuredHeight = headerView.measuredHeight
        LogUtils.d("getWeatherDayRangeHeight: headerView measuredHeight:$measuredHeight")

        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_weather_type_new3, null, false)
        itemView.measure(w, h)
        LogUtils.d("getWeatherDayRangeHeight: itemView measuredHeight:${itemView.measuredHeight}")
        val count = if (itemSize > 8) 7 else itemSize - 1
        return headerView.measuredHeight + count * itemView.measuredHeight + (count - 1) * vertical + 2 * top + bottom
    }

    @SuppressLint("Range")
    private fun getWeatherDayHeight(
        top: Int = context.resources.getDimensionPixelSize(R.dimen.dp_24),
        bottom: Int = context.resources.getDimensionPixelSize(R.dimen.dp_48),
        parentViewWidth: Int
    ): Int {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_weather_type_new1, null, false)
        val w = View.MeasureSpec.makeMeasureSpec(parentViewWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        itemView.measure(w, h)
        val measuredHeight = itemView.measuredHeight
        LogUtils.d("getWeatherDayHeight: itemView measuredHeight:$measuredHeight")

        return measuredHeight + top + bottom
    }

    @SuppressLint("Range")
    private fun getStockHeight(
        top: Int = context.resources.getDimensionPixelSize(R.dimen.dp_32),
        bottom: Int = context.resources.getDimensionPixelSize(R.dimen.dp_48),
        parentViewWidth: Int
    ): Int {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_stock, null, false)
        val w = View.MeasureSpec.makeMeasureSpec(parentViewWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        itemView.measure(w, h)
        val measuredHeight = itemView.measuredHeight
        LogUtils.d("getStockHeight: itemView measuredHeight:$measuredHeight")
        return measuredHeight + top + bottom
    }

    @SuppressLint("Range")
    private fun getScheduleHeight(
        top: Int = context.resources.getDimensionPixelSize(R.dimen.dp_32),
        bottom: Int = context.resources.getDimensionPixelSize(R.dimen.dp_48),
        parentViewWidth: Int
    ): Int {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_schedule_type1, null, false)
        val w = View.MeasureSpec.makeMeasureSpec(parentViewWidth, View.MeasureSpec.EXACTLY)
        val h = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        itemView.measure(w, h)
        val measuredHeight = itemView.measuredHeight
        LogUtils.d("getScheduleHeight: itemView measuredHeight:$measuredHeight")
        return measuredHeight + top + bottom
    }

    fun notifyDataSetChange(chatMessage: ChatMessage) {
        domainItemAdapter?.let { adapter ->
            val itemCount = adapter.itemCount
            adapter.getItem(itemCount - 1)?.let { entity ->
                entity as ChatMessage
                adapter.updateItem(itemCount - 1, chatMessage)
            }
//            scrollToBottom()
        }
    }

    private fun scrollToBottom() {
        rvCard?.let { rcv ->
            val canScroll = rcv.canScrollVertically(1)
            LogUtils.d("scrollToBottom canScrollVertically:$canScroll")
            if (canScroll) {
                rcv.smoothScrollBy(0, Int.MAX_VALUE)
            }
        }
    }

    private fun getStreamMode(): Int {
        var streamMode = StreamMode.NOT_STREAM
        domainItemAdapter?.let { adapter ->
            adapter.getItem(0)?.let { entity ->
                entity as ChatMessage
                streamMode = entity.streamMode
            }

        }
        return streamMode
    }


    override fun onUIModeChange(nightModeFlag: Boolean) {
        LogUtils.d("onUIModeChange nightModeFlag:$nightModeFlag")
        domainItemAdapter?.let {

        }
    }

    override fun setCardCollapse() {
        super.setCardCollapse()
        LogUtils.d("setCardCollapse")
        if (isChatCard(domainType)) {
            tvChatContent?.viewTreeObserver?.removeOnGlobalLayoutListener(chatContentLayoutListener)
        }
    }

    private fun expandChatContent() {
        if (isThinkingChanging) {
            return
        }

         if(llReason?.visibility == View.VISIBLE) {
             toggleCollapse(llReason!!)

         } else {
             toggleCollapse(llReason!!)

         }

    }

    fun expandChatContentByVoice(resId: Int) {
        if (resId == EXPAND_UP && llReason?.visibility != View.VISIBLE) {
            expandChatContent()
        } else if (resId == EXPAND_DOWN && llReason?.visibility == View.VISIBLE) {
            expandChatContent()
        }
    }

    private fun setMuskViewVisible() {
        viewMaskUp?.visibility = View.VISIBLE
        viewMaskDown?.visibility = View.VISIBLE
    }


    private fun updateThinkingView() {
        if (SystemConfigUtil.isDeepSeekMode()) {
            llThinkingState?.visibility = View.VISIBLE
            llReason?.visibility = View.VISIBLE
        }
    }

    fun toggleCollapse(reasonLayout: LinearLayout) {
        if (isThinkingChanging) {
            LogUtils.d("toggleCollapse isThinkingChanging:$isThinkingChanging")
            return
        }
        var initialHeight = reasonLayout.height
        if (isThinkingExpand) {
            imgExpand?.setImageResource(R.drawable.icon_expand_down)
            AnimatorUtil.collapseLayout(reasonLayout, initialHeight, 0,
                onChange = {
                    isThinkingChanging = it
                    if(!it) isThinkingExpand = false
                })
        } else {
            imgExpand?.setImageResource(R.drawable.icon_expand_up)
            var targetHeight = reasonLayout.measuredHeight
            tvReasoningContent?.let {
//                targetHeight = AnimatorUtil.getReasonHeight(it, reasonText, it.textSize, tvReasoningContent?.width!!)
                targetHeight = AnimatorUtil.calculateTextViewHeight(it, reasonText, tvReasoningContent?.width!!)
            }
            AnimatorUtil.expandLayout(reasonLayout, 0, targetHeight,
                onChange = {
                    isThinkingChanging = it
                    if(!it) {
                        isThinkingExpand = true
                    }
                })
        }
        LogUtils.d("toggleCollapse initialHeight:$initialHeight")
    }

    override fun destroy() {
        LogUtils.d("destroy")
        setCardCollapse()
        updateJob?.cancel()
        customExpand = false
        super.destroy()
        rvCard?.removeOnScrollListener(scrollListener)
    }

}
