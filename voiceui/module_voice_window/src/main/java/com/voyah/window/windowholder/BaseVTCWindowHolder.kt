package com.voyah.window.windowholder

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.hjq.shape.layout.ShapeFrameLayout
import com.hjq.shape.layout.ShapeLinearLayout
import com.lzf.easyfloat.EasyFloat
import com.voyah.cockpit.window.model.APIResult
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.ChatMessage
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.LanguageType
import com.voyah.cockpit.window.model.PageInfo
import com.voyah.cockpit.window.model.StreamMode
import com.voyah.cockpit.window.model.TypeTextStyle
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.cockpit.window.model.VoiceState
import com.voyah.cockpit.window.model.WindowType
import com.voyah.viewcmd.GestureUtils
import com.voyah.viewcmd.VoiceViewCmdManager
import com.voyah.viewcmd.interceptor.IDirectRegisterInterceptor
import com.voyah.viewcmd.interceptor.SimpleInterceptor
import com.voyah.voice.card.CardAnimationHelper
import com.voyah.voice.framework.ext.normalScope
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.window.R
import com.voyah.window.cardholder.BTPhoneCardHolder
import com.voyah.window.cardholder.BaseCardHolder
import com.voyah.window.cardholder.DomainCardHolder
import com.voyah.window.cardholder.MultiMusicCardHolder
import com.voyah.window.cardholder.MultimediaCardHolder
import com.voyah.window.extension.isChatCard
import com.voyah.window.extension.supportDeepSeek
import com.voyah.window.interfaces.CardInteractionCallback
import com.voyah.window.interfaces.VTCWindowCallback
import com.voyah.window.manager.CardDataRepo
import com.voyah.window.presenter.CardTimerTask
import com.voyah.window.util.RequestType
import com.voyah.window.util.SystemConfigUtil
import com.voyah.window.view.CardContentLayout
import com.voyah.window.view.vpa.VpaState
import com.voyah.window.view.vpa.VpaSurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.regex.Pattern

/**
 *  author : jie wang
 *  date : 2025/1/6 19:32
 *  description :
 */
abstract class BaseVTCWindowHolder(context: Context) : BaseWindowHolder(context) {

    var originalLayoutHeight = 0
    private var originalLayoutWidth = 0
    private var tvAsr: TextView? = null
    private var tvAsrPlaceHolder: TextView? = null
    private var vVpaPlaceHolder: View? = null
    private var sfvVPA: VpaSurfaceView? = null
    var sllCard: ShapeLinearLayout? = null
    private var llVARoot: ViewGroup? = null
    var sflVPAType: ShapeFrameLayout? = null
    private var llasr: ViewGroup? = null
    private var tvGPTTip: TextView? = null
    var llCardContainer: ViewGroup? = null
    var llPlaceHolder: ViewGroup? = null

    private val windowVAX: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_905)
    }

    private val xFullScreenVPAType: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_859)
    }

    private val xSplitScreenVPAType: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_1069)
    }

    private val windowCardX2: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_698)
    }

    private val windowWidthSmall: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_292)
    }

    private val windowWidthMedium: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_600)
    }

    private val windowWidthLarge: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_740)
    }

    private val windowWidthXLarge: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_900)
    }
    private val windowWidthXXLarge: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_1067)
    }

    private val shadowSize: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_10)
    }

    val windowTop: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_12) - shadowSize
    }

    private val placeholderViewHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_112) + 2 * shadowSize
    }

    private val minWidthAsr: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_122)
    }

    private val maxWidthAsr: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_384)
    }


    private val focusX by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_20)
    }

    var cardHolder: BaseCardHolder<*>? = null
    var vtcWindowCallback: VTCWindowCallback? = null

    var voiceState: String = VoiceState.VOICE_STATE_INIT


    private var collapseCardAnimator: Animator? = null
    var expandCardAnimator: Animator? = null
    private var enlargeCardAnimator: Animator? = null
    private var cardMotionEventUpInput = false
    private var cardScroll = false
    private var vpaSmallFlag = false
    private var normalScope: CoroutineScope = normalScope()
    private var vpaScaleInOutJob: Job? = null
    private var cardTimerTask: CardTimerTask? = null
    var vpaTypeViewInitFlag = false
    private var textASRToDisplay: String? = null
    private var textStyleToDisplay:Int = TypeTextStyle.SECONDARY
    var currentTextStype: Int = TypeTextStyle.SECONDARY
    private var chatMessageToDisplay: ChatMessage? = null
    var showCardFlag = true
    var cardInfoToDisplay: CardInfo? = null

    private var mInteractionCallback = object : CardInteractionCallback {
        override fun onScrollStateChange(view: View, newState: Int, domainType: String, requestId: String?) {
            LogUtils.d("onScrollStateChange newState:$newState, domainType:$domainType, " +
                    "requestId:$requestId")
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                LogUtils.d("start dragging .")
                cardScroll = true
            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                LogUtils.d("animating to a final position.")
                cardScroll = true
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LogUtils.d("scroll end, cardMotionEventUpInput:$cardMotionEventUpInput")
                if (cardMotionEventUpInput) {
                    vtcWindowCallback?.onInteractionInCard(domainType, requestId)
                    cardMotionEventUpInput = false
                } else {
                    vtcWindowCallback?.onInteractionInCard(domainType, requestId)
                }
                cardScroll = false
            }
        }

        override fun onPageSelected(view: View, position: Int, domainType: String) {
            LogUtils.d("onPageSelected position:$position, domainType:$domainType")
            cardScroll = true
        }

    }

    private var motionEventUpCallback = {
        LogUtils.d("retrieve card holder view up event...")
        if (isInformationCard(cardHolder?.domainType)) {
            LogUtils.d("card layout retrieve motion event up... carScroll:$cardScroll")
            cardMotionEventUpInput = true
            if (cardScroll) {
                LogUtils.d("card scroll ing.")
            } else {
                vtcWindowCallback?.onInteractionInCard(cardHolder?.domainType, cardHolder?.requestId)
                cardMotionEventUpInput = false
            }
        }
    }

    private val directInterceptor = object : IDirectRegisterInterceptor {
        override fun bind(): Map<String, Int> {
            LogUtils.d("direct interceptor bind")
            return makeupViewCmd()
        }

        override fun onTriggered(text: String, resId: Int): Boolean {
            LogUtils.d("onTriggered() direct interceptor called with: text = [$text], resId = [$resId]")
            if (resId == ID_BACK_VOICE_CMD) {
                normalScope.launch {
                    recoverWindowSmall(true)
                }
                return true
            } else if (resId == EXPAND_UP || resId == EXPAND_DOWN) {
                normalScope.launch {
                    if(cardHolder is DomainCardHolder) {
                        val domainCardHolder = cardHolder as DomainCardHolder
                        domainCardHolder.expandChatContentByVoice(resId)
                    }
                }
                return true
            } else if (resId > GestureUtils.RES_ID_GESTURE_MIN && resId < GestureUtils.RES_ID_GESTURE_MAX) {
                normalScope.launch {
                    cardHolder?.let {
                        if (it is DomainCardHolder) {
                            it.rvCard?.let {
                                GestureUtils.performGesture(it, resId);
                            }
                        }
                    }
                }

                return false
            }
            return false
        }
    }

    private fun makeupViewCmd(): ArrayMap<String, Int> {
        val arrayMap: ArrayMap<String, Int> = ArrayMap()
        arrayMap["返回"] = ID_BACK_VOICE_CMD
        arrayMap["展开"] = EXPAND_UP
        arrayMap["收起"] = EXPAND_DOWN
        arrayMap["上滑|往上滑|向上滑|再往上滑|再向上滑|往上划|向上划"] = GestureUtils.RES_ID_GESTURE_UP
        arrayMap["下滑|往下滑|向下滑|再往下滑|再向下滑|往下划|向下划"] =  GestureUtils.RES_ID_GESTURE_DOWN
        arrayMap["滑到最上|滑到最上面|滑到顶部|滑到最前"] =  GestureUtils.RES_ID_GESTURE_UP_BEGIN
        arrayMap["滑到最下|滑到最下面|滑到底部|滑到最后"] =  GestureUtils.RES_ID_GESTURE_DOWN_END
        return arrayMap
    }

    private val simpleInterceptor = object : SimpleInterceptor() {
        override fun bind(): MutableMap<String, Int> {
            val arrayMap: ArrayMap<String, Int> = ArrayMap()
            arrayMap["返回"] = ID_BACK_VOICE_CMD
            return arrayMap
        }

        override fun onTriggered(text: String?, resId: Int): Boolean {
            if (resId == ID_BACK_VOICE_CMD) {
                normalScope.launch {
                    recoverWindowSmall(true)
                }
                return true
            }
            return super.onTriggered(text, resId)
        }
    }


    companion object {
        const val TAG = "VTCWindowHolder"
        const val LISTENING_DELAY_TIME = 5000L
        const val maxLengthSmallCard = 42
        const val ID_BACK_VOICE_CMD = 10
        const val EXPAND_UP: Int = 50
        const val EXPAND_DOWN: Int = 51
    }

    override fun getLayoutRes() = R.layout.window_vpa_typewriter_card


    fun isCardWindowShow() = EasyFloat.isShow(getCardWindowTag()).apply {
        LogUtils.d("isCardWindowShow result:$this")
    }

    private fun isCardWindowShowing() = EasyFloat.isShowing(getCardWindowTag()).apply {
        LogUtils.d("isCardWindowShowing result:$this")
    }


    fun isVAWindowShow() = EasyFloat.isShow(getVPATypeWindowTag()).apply {
        LogUtils.d("isVAWindowShow result:$this")
    }

    fun isVAWindowShowing() = EasyFloat.isShowing(getVPATypeWindowTag()).apply {
        LogUtils.d("isVAWindowShowing result:$this")
    }

    fun isCardExpand() : Boolean {
        return (EasyFloat.getConfig(getCardWindowTag())?.cardExpand ?: false).apply {
            LogUtils.d("isCardExpand:$this")
        }
    }

    private fun isExitAnimRunning() = EasyFloat.isExitAnimRunning(getVPATypeWindowTag()) ?: false

    private fun isVPATypeEnterAnimRunning() = EasyFloat.isEnterAnimRunning(getVPATypeWindowTag()) ?: false

    abstract fun getCardWindowTag(): String


    abstract fun getVPATypeWindowTag(): String

    open fun showCardWindow(showVA: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemConfigUtil.setDefaultNightMode(context)
        }
        currentNightModeFlag = SystemConfigUtil.isNightMode(context)
        LogUtils.i("showCardWindow currentNightModeFlag:$currentNightModeFlag")
    }

    override fun showWindow() {

    }

    abstract fun showVAWindow()

    override fun setVoiceFocusLocation() {
        LogUtils.i("setVoiceFocusLocation location of voice focus:${AppHelper.locationVoiceFocus}")
    }

    fun onVoiceFocusChange(own: Boolean) {
        LogUtils.i("onVoiceFocusChange own:$own")
        isVAWindowShowing()
        val visibility = if (own) View.VISIBLE else View.GONE
        if (isVAWindowShow()) {
            sfvVPA?.visibility = visibility
        }
        isCardWindowShowing()
        if (isCardWindowShow()) {
            vVpaPlaceHolder?.visibility = visibility
        }
    }

    fun showVPA() {
        LogUtils.d("showVPA")
        voiceState = VoiceState.VOICE_STATE_AWAKE
        if (!isVAWindowShow()) {
            if (!isCardWindowShowing()
                && !isCardWindowShow()) {
                showCardWindow(true)
            } else {
                showVAWindow()
            }
        } else {
            val exitAnimRunningFlag = isVPATypeExitAnimRunning()
            LogUtils.d("showVPA exitAnimRunningFlag:$exitAnimRunningFlag")
            if (exitAnimRunningFlag) {
                EasyFloat.cancelExitAnimRunning(getVPATypeWindowTag())
                LogUtils.d("showVPA after cancel exit animation ...")
                if (!isCardWindowShowing() && !isCardWindowShow()) {
                    showCardWindow(true)
                } else {
                    showVAWindow()
                }
            } else {
                recoverWindowSmall(false)
            }
        }
    }

    /**
     *  vpa typewriter window是否在执行退出的动画
     */
    fun isVPATypeExitAnimRunning(): Boolean {
        val result = EasyFloat.isExitAnimRunning(getVPATypeWindowTag()) ?: false
        LogUtils.i("isVPATypeExitAnimRunning result:$result")
        return result
    }

    fun onVpaTypeWindowDismiss() {
        vpaTypeViewInitFlag = false
        textASRToDisplay = null
        textStyleToDisplay = TypeTextStyle.SECONDARY
        if (isCardWindowShow()) {
            val cardExpandFlag = isCardExpand()
            val expandCardRunningFlag = expandCardAnimator?.isRunning ?: false
            LogUtils.d(
                "onVpaTypeWindowDismiss, expandCardRunningFlag:$expandCardRunningFlag, " +
                        "cardExpandFlag:$cardExpandFlag"
            )
            if (!expandCardRunningFlag && !cardExpandFlag) {
                EasyFloat.dismiss(getCardWindowTag(), true)
            } else if (cardExpandFlag) {//卡片展开状态
                if (isCollapseCardRunning()) {// 正在执行收起卡片的动画
                    //                                cancelCollapseCardAnimator()
                } else {
                    val height = sllCard!!.height
                    llPlaceHolder?.let { placeholderView ->
                        placeholderView.visibility = View.GONE
                        LogUtils.d("on va window dismiss, originalLayoutHeight:$originalLayoutHeight")
                        setContentViewHeight(sllCard!!, height - originalLayoutHeight)
                    }
                }
            }
        }
    }

    override fun onVoiceListening() {
        val currentVoiceState = voiceState
        LogUtils.d("onVoiceListening currentVoiceState:$currentVoiceState")
        if (currentVoiceState != VoiceState.VOICE_STATE_LISTENING) {
            voiceState = VoiceState.VOICE_STATE_LISTENING
            setVoiceListening()

//            if (!isCardExpand() && !vpaSmallFlag) {
//                launchVPAScaleInJob(true)
//            }
        }
    }

    private fun launchVPAScaleInJob(scaleInFlag: Boolean = true) {
        LogUtils.d("launchVPAScaleInJob")
        cancelVPAScaleInJob()
        vpaScaleInOutJob =
            normalScope.launch(Dispatchers.Default) {
                delay(LISTENING_DELAY_TIME)
                withContext(Dispatchers.Main) {
                    scaleInOutVPA(scaleInFlag)
                }
            }
    }

    private fun scaleInOutVPA(scaleInFlag: Boolean = true) {
        LogUtils.d("scaleInOutVPA scaleInFlag:$scaleInFlag")
        sflVPAType?.let {
            vpaSmallFlag = scaleInFlag
            val scaleInOutAnimator = CardAnimationHelper.scaleInOutVPANew(
                it,
                focusX.toFloat(),
                0f,
                scaleInFlag)
        } ?: run {
            LogUtils.w("scaleInOutVPA viewVPATypeRoot is null, unexpected error occur...")
        }

    }

    fun recoverWindowSmall(fromUserTouch: Boolean = true) {
        LogUtils.d("recoverWindowSmall, fromUserTouch:$fromUserTouch")
        val cardExpand = isCardExpand()
        if (cardExpand) {//卡片展开了
            executeCardCollapse(fromUserTouch, resetASRViewListening = true)
        } else {
            LogUtils.d("recoverWindowSmall card not expand.")
            setVoiceListening()
        }
    }

    fun initCardView(view: View) {
        sllCard = view.findViewById(R.id.sfl_card)
        LogUtils.d("initCardView llContent width:${sllCard?.width}")
        llCardContainer = view.findViewById<ViewGroup>(R.id.ll_card_container).let {
            (it as CardContentLayout).apply {
                motionEventUpBlock = motionEventUpCallback
            }
        }

        llPlaceHolder = view.findViewById<LinearLayout>(R.id.ll_placeholder)
        LogUtils.d("initCardView llPlaceHolder width:${llPlaceHolder?.width}")
        tvAsrPlaceHolder = view.findViewById<TextView>(R.id.tv_asr_place_holder)
        vVpaPlaceHolder = view.findViewById<TextView>(R.id.v_vpa_place_holder)
    }


    @SuppressLint("Range")
    private fun getPlaceholderViewSize(placeholderView: View) {
        val w = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.AT_MOST)
        val h = View.MeasureSpec.makeMeasureSpec(placeholderViewHeight, View.MeasureSpec.EXACTLY)
        placeholderView.measure(w, h)
        originalLayoutHeight = placeholderView.measuredHeight
        originalLayoutWidth = placeholderView.measuredWidth
        LogUtils.d("getPlaceholderViewSize measuredHeight:$originalLayoutHeight, measuredWidth:$originalLayoutWidth")
    }

    fun initVAView(view: View) {
        sflVPAType = view.findViewById(R.id.sfl_vpa_type)
        llVARoot = view.findViewById<ViewGroup>(R.id.ll_va_root).apply {
            LogUtils.d("initVAView vpa typewriter layout width:$width")
        }

        sfvVPA = view.findViewById<VpaSurfaceView?>(R.id.sfv_vpa).apply {
            LogUtils.d("initVAView vpa width:$width, height:$height")
        }

        tvAsr = view.findViewById<TextView?>(R.id.typewriter_view).apply {
            val typewriterOriginalSize = textSize
            LogUtils.d("initVAView typewriterOriginalSize:$typewriterOriginalSize, minWidth:$minWidth," +
                    "width:$width")
        }

        llasr = view.findViewById<ViewGroup>(R.id.ll_asr)

        view.setOnClickListener {
            vtcWindowCallback?.onVTCDismiss()
        }

        tvGPTTip = view.findViewById(R.id.tv_gpt_tip)

        bindVAView(voiceMode, languageType)

        //有卡片的场景，展示VPA窗口不显示阴影
        if (isCardExpand()) {
            onWindowSizeChange(true)
        }

        if (isCardExpand() && !isCollapseCardRunning()) {//如果卡片展开状态下，没有执行收起动画
            llPlaceHolder?.let { placeholderView ->
                if (originalLayoutHeight == 0) {
                    getPlaceholderViewSize(placeholderView)
                }

                if (placeholderView.visibility != View.VISIBLE) {
                    placeholderView.visibility = View.VISIBLE
                    sllCard?.let { sllCard->
                        val endValueHeight = sllCard.height + originalLayoutHeight
                        setContentViewHeight(sllCard, endValueHeight)
                    }
                }
            }
        }
        vtcWindowCallback?.onVPATypeInit()
        vpaTypeViewInitFlag = true
        LogUtils.d("initVAView textASRToDisplay:$textASRToDisplay, textStyleToDisplay:$textStyleToDisplay")
        if (textASRToDisplay != null) {
            inputText(textASRToDisplay!!, textStyleToDisplay)
            textASRToDisplay = null
            textStyleToDisplay = TypeTextStyle.SECONDARY
        }
    }

    /**
     *  绑定vap、asr view
     */
    private fun bindVAView(voiceMode: Int, languageType: Int) {
        LogUtils.d("bindVAView voiceMode:${getVoiceModeParsed(voiceMode)}, " +
                "languageType:${getLanguageTypeParsed(languageType)}")
        LogUtils.d("bindVAView nightModeFlag:$currentNightModeFlag")
        var asrText = ""
        var vpaState = VpaState.LISTENING_NIGHT_NORMAL
        if (voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
            if (voiceState == VoiceState.VOICE_STATE_LISTENING) {
                asrText = getListeningTextWithLanguage(languageType)
            }

            vpaState = if (currentNightModeFlag) VpaState.LISTENING_NIGHT_NORMAL
            else VpaState.LISTENING_LIGHT_NORMAL
        } else {
            asrText = context.getString(R.string.asr_offline_mode)
            vpaState = if (currentNightModeFlag) VpaState.LISTENING_NIGHT_PRIVATE
            else VpaState.LISTENING_LIGHT_PRIVATE
        }
        tvAsr?.apply {
            text = asrText
            val textColor = context.resources.getColor(R.color.asr_text_valid)
            currentTextStype = TypeTextStyle.PRIMARY
            setTextColor(textColor)
        }

        tvAsrPlaceHolder?.text = asrText

        sfvVPA?.startDraw(vpaState)
    }


    fun collapseCard() {
        LogUtils.d("collapseCard")
        showCardFlag = false
        if (isCardExpand()) {
            executeCardCollapse(fromUserTouch = false, resetASRViewListening = false)
        } else {
            if (cardInfoToDisplay != null) {
                LogUtils.d("collapseCard cardInfoToDisplay not null.")
                cardInfoToDisplay = null
            }
            val expandCardRunningFlag = expandCardAnimator?.isRunning ?: false
            LogUtils.d("collapseCard expandCardRunningFlag:$expandCardRunningFlag")
            if (expandCardRunningFlag) {
                expandCardAnimator?.cancel()
                expandCardAnimator = null
                recoverCardWindowSmall()
            }
        }
    }

    private fun recoverCardWindowSmall() {
        LogUtils.d("recoverCardWindowSmall")
        val vpaTypeWindowShowFlag = isVAWindowShow()

        if (vpaTypeWindowShowFlag) {
            EasyFloat.updateFloat(getCardWindowTag(), x = getVPATypeX())
            sllCard?.let { sllCard ->
                sllCard.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        sllCard.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        setContentViewWidth(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
                        setContentViewHeight(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
                    }

                })
                setContentViewWidth(sllCard, sflVPAType!!.width)
                setContentViewHeight(sllCard, sflVPAType!!.height)
            }

            onWindowSizeChange(false)

            val chatCardFlag = isChatCard(cardHolder?.domainType)
            val fromGPTFlag = cardHolder?.fromGPTFloag ?: false
            LogUtils.d("recoverCardWindowSmall chatCardFlag:$chatCardFlag, fromGPTFlag:$fromGPTFlag")
            if (chatCardFlag && fromGPTFlag) {
                setGPTTipVisible(View.GONE)
            }
        } else {
            EasyFloat.dismiss(getCardWindowTag(), true)
        }

        removeCardView()

        onCardHolderDestroy()
    }

    private fun executeCardCollapse(fromUserTouch: Boolean = true, resetASRViewListening: Boolean = false) {
        if (isCollapseCardRunning()) {
            LogUtils.w("executeCardCollapse collapse card animation ing...")
            return
        }
        LogUtils.d("executeCardCollapse fromUserTouch:$fromUserTouch")

        val enlargeCardAnimRunningFlag = enlargeCardAnimator?.isRunning ?: false
        LogUtils.d("executeCardCollapse enlargeCardAnimRunningFlag:$enlargeCardAnimRunningFlag")
        if (enlargeCardAnimRunningFlag) {
            enlargeCardAnimator?.cancel()
            enlargeCardAnimator = null
        }
        val exitAnimRunningFlag = isExitAnimRunning()
        LogUtils.d("executeCardCollapse exitAnimRunningFlag:$exitAnimRunningFlag")
        val endValueWidth: Int
        val endValueHeight: Int
        if (isVAWindowShow() && !exitAnimRunningFlag) {
            endValueWidth = sflVPAType!!.width
            endValueHeight = sflVPAType!!.height
        } else {
            endValueWidth = 0
            endValueHeight = 0
        }

        cardHolder?.setCardCollapse()
        collapseCardAnimator = CardAnimationHelper.executeCardCollapseWHXAnimation(
            sllCard!!,
            endValueWidth,
            endValueHeight,
            getVPATypeX(),
            getCardWindowTag(),
            animationEnd = { _ ->
                LogUtils.d("collapseCard animation end.")

                onCollapseCardWithAnimation(fromUserTouch, resetASRViewListening)
                if (cardInfoToDisplay != null) {
                    showCard(cardInfoToDisplay!!)
                    cardInfoToDisplay = null
                }
            },
            animationCancel = { _ ->
                LogUtils.d("collapseCard animation cancel.")
            })
    }


    private fun onCollapseCardWithAnimation(fromUserTouch: Boolean, resetASRViewListening: Boolean = false) {
        LogUtils.d("onCollapseCardWithAnimation fromUserTouch:$fromUserTouch, " +
                "resetASRViewListening:$resetASRViewListening")
        collapseCardAnimator = null
        if (isVAWindowShow()) {
            sllCard?.let { sllCard ->
                setContentViewWidth(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
                setContentViewHeight(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
            }

            onWindowSizeChange(false)

            if (resetASRViewListening) {
                tvAsr?.apply {
                    text = context.getString(R.string.voice_state_listening)
                    val textColor = context.resources.getColor(R.color.asr_text_valid)
                    currentTextStype = TypeTextStyle.PRIMARY
                    setTextColor(textColor)
                }
                tvAsrPlaceHolder?.text = context.getString(R.string.voice_state_listening)

//                vpaView?.composition = getListeningAnimRes()
            }

            val chatCardFlag = isChatCard(cardHolder?.domainType)
            val fromGPTFlag = cardHolder?.fromGPTFloag ?: false
            LogUtils.d("onCollapseCardWithAnimation chatCardFlag:$chatCardFlag, fromGPTFlag:$fromGPTFlag")
            if (chatCardFlag && fromGPTFlag) {
                setGPTTipVisible(View.GONE)
            }

//            if (voiceState != VoiceState.VOICE_STATE_RECOGNIZED_VALID && !vpaSmallFlag) {
//                launchVPAScaleInJob(true)
//            }

        } else if (isVAWindowShowing()) {
            sllCard?.let { sllCard ->
                setContentViewWidth(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
                setContentViewHeight(sllCard, FrameLayout.LayoutParams.WRAP_CONTENT)
            }

            onWindowSizeChange(false)

        } else {
            EasyFloat.dismiss(getCardWindowTag(), true)
        }


        removeCardView()

        cardInfoToDisplay?.let {
            LogUtils.d("onCollapseCardWithAnimation cardInfoToDisplay not null...")
            val sameRequestFlag = isSameRequest(it.requestId ?: "")
            LogUtils.d("onCollapseCardWithAnimation sameRequestFlag:$sameRequestFlag")
            if (sameRequestFlag) {
                cardInfoToDisplay = null
            }
        }

        if (fromUserTouch) {
            val sessionId = cardHolder?.sessionId
            val domainType = cardHolder?.domainType
            vtcWindowCallback?.onCardCollapse(domainType, sessionId)
        }

        onCardHolderDestroy()
    }

    fun dismissVoiceView() {
        LogUtils.d("dismissVoiceView")
        removeVPATypeView()
    }

    private fun setVoiceListening() {
        LogUtils.d("setVoiceListening")
        bindVAView(voiceMode, languageType)
    }

    fun inputText(text: String, textStyle: Int) {
        LogUtils.d("inputText: textStyle:$textStyle")
        if (!vpaTypeViewInitFlag) {
            textASRToDisplay = text
            textStyleToDisplay = textStyle
            return
        }

        var textColor = context.resources.getColor(R.color.asr_text_invalid)
        when(textStyle) {
            TypeTextStyle.PRIMARY -> {
                voiceState = VoiceState.VOICE_STATE_RECOGNIZED_VALID
                textColor = context.resources.getColor(R.color.asr_text_valid)
            }

            else -> {
                voiceState = VoiceState.VOICE_STATE_RECOGNIZE
            }
        }

        currentTextStype = textStyle

        tvAsr?.apply {
            setText(text)
            setTextColor(textColor)
        }

        tvAsrPlaceHolder?.text = text
    }

    fun toShowCard(cardInfo: CardInfo) {
        showCardFlag = true
        val domainType = cardInfo.domainType
        LogUtils.d("toShowCard domainType:$domainType")
        when (domainType) {
            DomainType.DOMAIN_TYPE_GOSSIP,
            DomainType.DOMAIN_TYPE_FAQ,
            DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA,
            DomainType.DOMAIN_TYPE_ENCYCLOPEDIA -> {
                if (!cardInfo.chatMessages.isNullOrEmpty()) {
                    val chatMessage = cardInfo.chatMessages[0]
                    chatMessage?.let { message ->
                        val streamMode = message.streamMode
                        LogUtils.d("toShowCard streamMode:$streamMode")
                        val content = message.content
                        when (streamMode) {
                            StreamMode.NOT_STREAM -> { //非流模式
                                tryShowCard(cardInfo)
                            }

                            StreamMode.START -> { //流开始了
                                tryShowCard(cardInfo)
                            }

                            StreamMode.ON_GOING -> { //流进行中
                                message.content = filterMarkdownTableImg(content)
                                tryShowSameChatCard(message, requestId = cardInfo.requestId)
                            }

                            StreamMode.COMPLETION -> { //流结束了
                                //處理无序列表 item中图片加载的异常
                                message.content = findLineBreakImg(message.content)
                                tryShowSameChatCard(
                                    message = message,
                                    requestId = cardInfo.requestId
                                )
                            }

                            else -> {
                                LogUtils.d("toShowCard, showCard invalid data...")
                            }
                        }
                    }
                }
            }

            else -> {
                tryShowCard(cardInfo)
            }
        }
    }

    /**
     *  正则表达式：
     * [-+*]\\s+.*: 匹配无序列表项（-、+ 或 *）及其后面的文本内容。
     * \\n\\s+: 匹配换行符后紧接若干个空格。
     * \\r\\s+: 匹配换行符后紧接若干个空格。
     * !\\[[^\\]]+\\]\\([^\\)]+\\): 匹配 Markdown 图片语法，包括图片描述和链接。
     *
     * 逻辑过滤：
     * 使用 split("\r").size == 2 检查子串是否仅包含一个换行符。
     *
     */
    private fun findLineBreakImg(content: String): String {
        val timeStart = System.currentTimeMillis()
        val regex = "([-+*]\\s+.*\\r\\s+)(!\\[[^\\]]+\\]\\([^\\)]+\\))"

        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(content)

        var result: String = content
        while (matcher.find()) {
            val matchedSubstring = matcher.group()
            if (matchedSubstring.split("\r").size == 2) {
                LogUtils.d("findLineBreakImg matchedSubstring:$matchedSubstring")
                if (matchedSubstring.contains("\r")) {
                    val target = matchedSubstring.replace("\r", "  \r")
                    result = result.replace(matchedSubstring, target)
                }
            }

        }
        LogUtils.d("findLineBreakImg time cost:${System.currentTimeMillis() - timeStart}")
        return result
    }


    /**
     * 1. 过滤Markdown表格，去除修饰符; 替换掉Markdown表格中的竖线和连字符
     * 2. 过滤Markdown图片。
     */
    private fun filterMarkdownTableImg(markdownText: String): String {
        return markdownText
            .replace("!\\[[^\\]]*\\]\\([^\\)]+\\)".toRegex(), "<img>")
            .replace("\\|".toRegex(), "") // 去除竖线
            .replace("^-+".toRegex(), "") // 去除行头的连字符（表头分隔符）
            .replace("-+$".toRegex(), "") // 去除行尾的连字符（表头分隔符）
    }

    private fun tryShowSameChatCard(message: ChatMessage, requestId: String) {
        LogUtils.d("tryShowSameChatCard")
        val sameRequestFlag = isSameRequest(requestId)
        LogUtils.d("tryShowSameChatCard sameRequestFlag:$sameRequestFlag")
        val streamMode = message.streamMode
        if (sameRequestFlag) {
            val chatCardExpand = isChatCardExpand()
            if (chatCardExpand) {
                LogUtils.d("tryShowSameChatCard notify data set change")
                tryNotifyDataSetChange(message)
            } else {
                LogUtils.d("tryShowSameChatCard, same session, and streamMode:$streamMode, need wait...")
                if (streamMode == StreamMode.COMPLETION) {
                    chatMessageToDisplay = message
                }
            }
        } else {
            val collapsingCardFlag = isCollapseCardRunning()
            LogUtils.d(
                "tryShowSameChatCard, not same session, and collapsingCardFlag:$collapsingCardFlag," +
                        " and streamMode:$streamMode"
            )
            if (collapsingCardFlag && streamMode == StreamMode.COMPLETION) {
                chatMessageToDisplay = message
            }
        }
    }

    private fun tryShowCard(cardInfo: CardInfo) {
        LogUtils.d("tryShowCard")
        val cardShowingFlag = isCardWindowShowing()
        if (!cardShowingFlag) {
            val cardShowedFlag = isCardWindowShow()
            if (cardShowedFlag) {
                LogUtils.d("tryShowCard , card window showed.")
                if (isVAWindowShowing()) {
                    cardInfoToDisplay = cardInfo
                    LogUtils.d("tryShowCard va window is showing, need wait...")
                } else if (isVAWindowShow()) {
                    LogUtils.d("tryShowCard va window showed, expand card now...")
                    tryExpandCard(cardInfo)
                } else {
                    LogUtils.d("tryShowCard va window not showing and not showed, expand card now...")
                    tryExpandCard(cardInfo)
                }
            } else {
                cardInfoToDisplay = cardInfo
                LogUtils.d("tryShowCard , card window not showed, need wait...")
                showCardWindow(false)
            }
        } else {
            cardInfoToDisplay = cardInfo
            LogUtils.d("tryShowCard , card window is showing, need wait...")
        }
    }

    fun tryExpandCard(cardInfo: CardInfo) {
        LogUtils.d("tryExpandCard")
        if (cardInfo.domainType.isNullOrEmpty()) {
            LogUtils.w("tryExpandCard invalid domain type.")
            return
        }

        val collapseCardRunningFlag = isCollapseCardRunning()
        LogUtils.d("tryExpandCard collapseCardRunningFlag:$collapseCardRunningFlag")
        if (collapseCardRunningFlag) {
            cardInfoToDisplay = cardInfo
        } else {
            showCard(cardInfo)
        }
    }

    private fun showCard(cardInfo: CardInfo) {
        LogUtils.d("showCard")
//        cancelVPAScaleInJob()
//        if (vpaSmallFlag) {
//            scaleInOutVPA(false)
//        }
        llCardContainer?.apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }

            val childIndex0 = getChildAt(0)
            if (childIndex0 != null) {
                LogUtils.d("showCard remove previous card holder view ...")
                removeView(childIndex0)
                EasyFloat.getConfig(getCardWindowTag())?.cardExpand = false
                EasyFloat.setWatchOutside(getCardWindowTag(), false)
                onCardHolderDestroy()
            }
            val cardView = getCardView(cardInfo)
            if (cardInfo.domainType == DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC) {
                registerViewCommandForAop()
            } else {
                registerViewCommandDef()
            }
            addView(cardView)
        }
        if (!isChatCard(cardInfo.domainType)) {
            setGPTTipVisible(View.GONE)
        }
//        if (supportDeepSeek(cardInfo.domainType)) {
//            tvGPTTip?.text = context.getString(R.string.reason_from_deepseek)
//        } else {
//            tvGPTTip?.text = context.getString(R.string.reason_from_voyah)
//        }

        updateWindowLocationAndSize()
    }

    private fun getCardView(cardInfo: CardInfo): View {
        val currentDomainType = cardInfo.domainType
        val actionInCardInfo = cardInfo.action
        LogUtils.d("getCardView currentDomainType:$currentDomainType")
        when (currentDomainType) {
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                cardHolder = BTPhoneCardHolder(context).apply {
                    action = actionInCardInfo
                    domainItemCallback = { position, viewType ->
                        vtcWindowCallback?.onDomainItemClick(position, viewType, action)
//                            executeCardCollapse(false)
                    }

                }
            }

            DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM,
            DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM -> {
                cardHolder = DomainCardHolder(context).apply {
                    refreshEndBlock = {
                        LogUtils.d("not stream card on refresh end...")
                    }
                }
            }
            DomainType.DOMAIN_TYPE_WEATHER,
            DomainType.DOMAIN_TYPE_SCHEDULE,
            DomainType.DOMAIN_TYPE_STOCK -> {
                cardHolder = DomainCardHolder(context)
            }


            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                cardHolder = MultimediaCardHolder(context).apply {
                    domainItemCallback = { position, viewType ->
                        vtcWindowCallback?.onDomainItemClick(position, viewType, action)
//                        executeCardCollapse(false)
                    }
                }
            }

            DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                cardHolder = MultiMusicCardHolder(context).apply {
                    domainItemCallback = { position, viewType ->
                        vtcWindowCallback?.onDomainItemClick(position, viewType, action)
//                        executeCardCollapse(false)
                    }
                }
            }

            else -> {
                if (isChatCard(currentDomainType)) {
                    cardHolder = DomainCardHolder(context).apply {
                        fromGPTFloag = cardInfo.isFromGPTFlag
                        refreshEndBlock = {
                            LogUtils.d("llm card on refresh end...")
                        }
                    }
                } else {
                    LogUtils.w("getCardView, invalid domain type...")
                }
            }
        }

        return cardHolder?.run {
            domainType = currentDomainType
            this.sessionId = cardInfo.sessionId
            this.requestId = cardInfo.requestId
            this.cardInfo = cardInfo
            if (isInformationCard(domainType)) {
                interactionCallback = mInteractionCallback
            }
            getCardView()

        } ?: run {
            View(context)
        }
    }

    private fun registerViewCommandDef() {
        val cardInfo = cardHolder?.cardInfo
        cardInfo?.let {
            val domainType = cardInfo.domainType
            LogUtils.d("registerViewCommandDef domainType:$domainType")
            cardHolder?.let {
                VoiceViewCmdManager.getInstance()
                    .directRegister(it.rootView, true, null, directInterceptor)
            }
        }
    }

    private fun registerViewCommandForAop() {
        val cardInfo = cardHolder?.cardInfo
        cardInfo?.let {
            val domainType = cardInfo.domainType
            LogUtils.d("registerViewCommandForAop domainType:$domainType")
            cardHolder?.let {
                VoiceViewCmdManager.getInstance().register(it.rootView, false, true, simpleInterceptor)
            }
        }
    }

    private fun updateWindowLocationAndSize() {
        val cardInfo = cardHolder?.cardInfo ?: run {
            LogUtils.w("updateWindowLocationAndSize cardInfo is null...")
            return
        }
        val domainType = cardInfo.domainType
        LogUtils.d("updateWindowLocationAndSize domainType:$domainType")
        sllCard?.let {
            var placeholderHeight = 0
            if (isVAWindowShowing() || isVAWindowShow()) {
                llPlaceHolder?.let { placeholderView ->
                    if (originalLayoutHeight == 0) {
                        getPlaceholderViewSize(placeholderView)
                    }

                    if (placeholderView.visibility != View.VISIBLE) {
                        placeholderView.visibility = View.VISIBLE
                    }
                }
                placeholderHeight = originalLayoutHeight
            }
            var endWindowWidth = windowWidthLarge
            when (domainType) {
                DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                    val multimediaInfos = cardInfo.multimediaInfos
                    if (!multimediaInfos.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as MultimediaCardHolder
                            endWindowWidth = windowWidthXXLarge
                            val endValueHeight = cardHolder.getCardHeight(
                                multimediaInfos, endWindowWidth) + placeholderHeight
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget)
                        }
                    }
                }

                DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                    val multiMusicInfos = cardInfo.multiMusicInfos
                    if (!multiMusicInfos.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as MultiMusicCardHolder
                            val endValueHeight = cardHolder. multimediaCardLargeHeight + placeholderHeight
                            endWindowWidth = cardHolder.multimediaCardLargeWidth
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget)
                        }
                    }
                }

                DomainType.DOMAIN_TYPE_STOCK -> {
                    val stockInfos = cardInfo.stockInfos
                    if (!stockInfos.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as DomainCardHolder
                            val endValueHeight = cardHolder.getCardHeight(stockInfos,
                                endWindowWidth) + placeholderHeight
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget)
                        }
                    }

                }
                DomainType.DOMAIN_TYPE_BT_PHONE -> {
                    val contacts = cardInfo.btPhoneInfo?.contacts
                    if (!contacts.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as BTPhoneCardHolder
                            val endValueHeight = cardHolder.getCardHeight(
                                contacts, endWindowWidth) + placeholderHeight
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget)
                        }
                    }
                }
                DomainType.DOMAIN_TYPE_SCHEDULE -> {
                    val schedules = cardInfo.schedules
                    if (!schedules.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as DomainCardHolder
                            val endValueHeight = cardHolder.getCardHeight(
                                schedules, endWindowWidth) + placeholderHeight
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget)
                        }
                    }

                }

                DomainType.DOMAIN_TYPE_WEATHER -> {
                    val weathers = cardInfo.weathers
                    if (!weathers.isNullOrEmpty()) {
                        cardHolder?.let { cardHolder ->
                            cardHolder as DomainCardHolder
                            val endValueHeight = cardHolder.getCardHeight(
                                weathers, endWindowWidth) + placeholderHeight
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                            expandCardWHXAnimation(
                                it,
                                endWindowWidth,
                                endValueHeight,
                                windowXCardTarget
                            )
                        }
                    }
                }

                DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM,
                DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM -> {
                    val chatMessages = cardInfo.chatMessages
                    if (!chatMessages.isNullOrEmpty()) {
                        val message = chatMessages[0]
                        if (message != null && message.content != null) {
                            setContentViewHeight(it, FrameLayout.LayoutParams.WRAP_CONTENT)
                            val length = message.content.length
                            LogUtils.d("length:$length")
                            endWindowWidth = if (length <= maxLengthSmallCard ) windowWidthLarge else windowWidthXXLarge
                            endWindowWidth += 2 * shadowSize
                            val windowXCardTarget = (screenWidth - endWindowWidth) / 2

                            expandCardWXAnimation(it, endWindowWidth, windowXCardTarget, cardInfo)
                        }
                    }
                }

                else -> {
                    if (isChatCard(domainType)) {
                        setContentViewHeight(it, FrameLayout.LayoutParams.WRAP_CONTENT)
                        LogUtils.d("updateWindowLocationAndSize card width:${it.width}")
                        endWindowWidth += 2 * shadowSize
                        val windowXCardTarget = (screenWidth - endWindowWidth) / 2
                        expandCardWXAnimation(it, endWindowWidth, windowXCardTarget, cardInfo)
                    } else {
                        LogUtils.d("updateWindowLocationAndSize not chat card")
                    }
                }
            }

        }
    }

    private fun expandCardWHXAnimation(
        view: ViewGroup,
        targetWindowWidth: Int,
        endValueHeight: Int,
        windowX: Int
    ) {

        LogUtils.d("expandCardWHXAnimation card width is:${view.width}")
        var animationCancelFlag = false
        expandCardAnimator = CardAnimationHelper.executeCardExpandWHXAnimation(
            view,
            targetWindowWidth,
            endValueHeight,
            windowX,
            getCardWindowTag(),
            animationStart = { _ ->
                LogUtils.d("domainType: expand card WHX animation start")
                onWindowSizeChange(true)
            },
            animationEnd = { _ ->
                LogUtils.d(
                    "domainType: expand card WHX animation end, " +
                            "animationCancelFlag:$animationCancelFlag"
                )
                if (!animationCancelFlag) {
                    bindCardData()
                    onCardExpanded()
                }
            },
            animationCancel = { _ ->
                animationCancelFlag = true
                LogUtils.d("domainType: expand card WHX animation cancel,")
            }
        )
    }

    private fun bindCardData() {
        val cardInfo = cardHolder?.cardInfo ?: run {
            LogUtils.w("bindCardData cardInfo is null...")
            return
        }
        val domainType = cardInfo.domainType
        LogUtils.d("bindCardData domainType:$domainType")
        when (domainType) {
            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                cardHolder?.apply {
                    this as MultimediaCardHolder
                    bindData(cardInfo.multimediaInfos)
                }
            }

            DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                cardHolder?.apply {
                    this as MultiMusicCardHolder
                    bindData(cardInfo.multiMusicInfos)
                }
            }

            DomainType.DOMAIN_TYPE_STOCK -> {
                cardHolder?.apply {
                    this as DomainCardHolder
                    bindData(cardInfo.stockInfos)
                }
            }

            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                cardHolder?.apply {
                    this as BTPhoneCardHolder
                    bindData(cardInfo.btPhoneInfo.contacts)
                }
            }

            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                cardHolder?.apply {
                    this as DomainCardHolder
                    bindData(cardInfo.schedules)
                }
            }

            DomainType.DOMAIN_TYPE_WEATHER -> {
                cardHolder?.apply {
                    this as DomainCardHolder
                    bindData(cardInfo.weathers)
                }
            }

            DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM,
            DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM -> {
                cardHolder?.apply {
                    this as DomainCardHolder
                    bindData(cardInfo.chatMessages)
                }
            }
        }
    }

    private fun expandCardWXAnimation(
        view: ViewGroup,
        endWindowWidth: Int,
        windowXCardTarget: Int,
        cardInfo: CardInfo
    ) {
        var animationCancelFlag = false
        val domainType = cardInfo.domainType
        expandCardAnimator = CardAnimationHelper.executeCardExpandWXAnimation(
            view,
            endWindowWidth,
            windowXCardTarget,
            getCardWindowTag(),
            animationStart = { _ ->
                LogUtils.d("domainType:$domainType, expand card WX animation start")
                onWindowSizeChange(true)
            },
            animationEnd = { _ ->
                LogUtils.d(
                    "domainType:$domainType, expand card WX animation end, " +
                            "animationCancelFlag:$animationCancelFlag"
                )
                if (!animationCancelFlag) {
                    if ((domainType == DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM)
                        || (domainType == DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM)
                    ) {
                        bindCardData()
                        onCardExpanded()
                    } else {
                        bindChatCard(cardInfo)
                    }
                }
            },
            animationCancel = { _ ->
                animationCancelFlag = true
                LogUtils.d("domainType:$domainType, expand card WX animation cancel")
            }
        )
    }

    private fun bindChatCard(cardInfo: CardInfo) {
        LogUtils.d("bindChatCard")
        if (!cardInfo.chatMessages.isNullOrEmpty()) {
            cardHolder?.apply {
                if (fromGPTFloag) {
                    setGPTTipVisible(View.VISIBLE)
                }
                this as DomainCardHolder
                val message = cardInfo.chatMessages[0]
                if (message.streamMode == StreamMode.NOT_STREAM) {
                    LogUtils.d("bindChatCard streamMode is not stream, and bindData...")
                    bindData(cardInfo.chatMessages)
                    onCardExpanded()
                } else if (message.streamMode == StreamMode.START){
                    executeTimeTaskTip(this, cardInfo)
                } else if(message.streamMode == StreamMode.ON_GOING || message.streamMode == StreamMode.COMPLETION) {
                    LogUtils.i("bindChatCard streamMode is not on going")
                    bindData(cardInfo.chatMessages)
                    onCardExpanded()
                }
            }
        }
    }

    private fun executeTimeTaskTip(cardHolder: DomainCardHolder, cardInfo: CardInfo) {
        LogUtils.d("executeTimeTaskTip")
        cardTimerTask?.cancelTask()
        cardTimerTask = null
        cardTimerTask = CardTimerTask(cardInfo, cardHolder).apply {
            bindDataBlock = { messages: List<ChatMessage> ->
                cardHolder.bindData(messages)
                onCardExpanded()
            }
        }

        Timer().schedule(cardTimerTask, 0, 300)
    }

    private fun onCardExpanded() {
        LogUtils.d("onCardExpanded")
        EasyFloat.getConfig(getCardWindowTag())?.cardExpand = true
        EasyFloat.setWatchOutside(getCardWindowTag(), true)
        if (isVAWindowShowing() || isVAWindowShow()) {
            llVARoot?.background = null
            llPlaceHolder?.let { placeholderView ->
                LogUtils.d("onCardExpanded originalLayoutHeight:$originalLayoutHeight")
                if (originalLayoutHeight == 0) {
                    getPlaceholderViewSize(placeholderView)
                }

                if (placeholderView.visibility != View.VISIBLE) {
                    placeholderView.visibility = View.VISIBLE
                }
            }
        } else if (!isVAWindowShow()) {
            sllCard?.post {
                llPlaceHolder?.let { placeholderView ->
                    if (placeholderView.visibility == View.VISIBLE) {
                        placeholderView.visibility = View.GONE
                        val height = sllCard!!.measuredHeight
                        LogUtils.d("onCardExpanded llContent height:$height")
                        LogUtils.d("onCardExpanded originalLayoutHeight:$originalLayoutHeight")
                        setContentViewHeight(sllCard!!, height - originalLayoutHeight)
                    }
                }
            }
        }

        LogUtils.d("onCardExpanded chatMessageToDisplay:$chatMessageToDisplay")
        if (chatMessageToDisplay != null) {
            tryNotifyDataSetChange(chatMessageToDisplay!!)
        }


    }

    fun setContentViewWidth(view: ViewGroup, width: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = width
        view.requestLayout()
    }

    fun setContentViewHeight(view: ViewGroup, height: Int) {
        val layoutParams = view.layoutParams
        layoutParams.height = height
        view.requestLayout()
    }

    private fun setContentViewWidthHeight(view: ViewGroup, width: Int, height: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        view.requestLayout()
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onVoiceSpeaking current voice state is:$voiceState")

        val currentVoiceState = voiceState

        if (!TextUtils.equals(currentVoiceState, VoiceState.VOICE_STATE_SPEAKING)) {
            LogUtils.d("onVoiceSpeaking nightModeFlag:$currentNightModeFlag")
            val vpaState = if (voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
                if (currentNightModeFlag) VpaState.SPEAKING_NIGHT_NORMAL
                else VpaState.SPEAKING_LIGHT_NORMAL
            } else {
                if (currentNightModeFlag) VpaState.SPEAKING_NIGHT_PRIVATE
                else VpaState.SPEAKING_LIGHT_PRIVATE
            }

            sfvVPA?.startDraw(vpaState)
        }
        voiceState = VoiceState.VOICE_STATE_SPEAKING
    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        cancelCollapseCardAnimator()
        expandCardAnimator?.cancel()
        expandCardAnimator = null
        enlargeCardAnimator?.cancel()
        enlargeCardAnimator = null

        removeCardView()
        onCardHolderDestroy()

        EasyFloat.dismiss(getCardWindowTag(), true)
        removeVPATypeView()
        normalScope.cancel()
        cardTimerTask?.cancelTask()
        cardTimerTask = null
    }

    fun cancelCollapseCardAnimator() {
        LogUtils.i("cancelCollapseCardAnimator")
        collapseCardAnimator?.cancel()
        collapseCardAnimator = null
    }

    private fun removeVPATypeView() {
        LogUtils.d("removeVPATypeView")
        voiceState = VoiceState.VOICE_STATE_EXIT
        val vaWindowShow = isVAWindowShow()
        val vaWindowShowing = isVAWindowShowing()
        val vpaTypeEnterAnimRunningFlag = isVPATypeEnterAnimRunning()
        val vpaTypeExitAnimRunningFlag = isVPATypeExitAnimRunning()
        LogUtils.d("removeVPATypeView vaWindowShow:$vaWindowShow, vaWindowShowing:$vaWindowShowing" +
                " \n vpaTypeEnterAnimRunningFlag:$vpaTypeEnterAnimRunningFlag" +
                " \n vpaTypeExitAnimRunningFlag:$vpaTypeExitAnimRunningFlag")
        if (vpaTypeEnterAnimRunningFlag) {
            EasyFloat.cancelEnterAnimRunning(getVPATypeWindowTag())
        }

        if (vaWindowShow || vaWindowShowing) {
            if (!vpaTypeExitAnimRunningFlag) {
                EasyFloat.dismiss(getVPATypeWindowTag())
            }
        }

        vpaTypeViewInitFlag = false
        textASRToDisplay = null
        textStyleToDisplay = TypeTextStyle.SECONDARY
//        cancelVPAScaleInJob()
    }

    private fun removeCardView() {
        EasyFloat.getConfig(getCardWindowTag())?.cardExpand = false
        EasyFloat.setWatchOutside(getCardWindowTag(), false)

        llCardContainer?.apply {
            val childIndex0 = getChildAt(0)
            if (childIndex0 != null) {
                LogUtils.d("removeCardView remove card holder view.")
                removeView(childIndex0)
            }
        }
    }


    fun scrollCard(direction: Int): Boolean {
        if (!isCardExpand()) {
            LogUtils.d("scrollCard card not expand. illegal call this method.")
            return false
        } else {
            when (cardHolder?.domainType) {
                DomainType.DOMAIN_TYPE_BT_PHONE -> {
                    return cardHolder?.scrollCard(direction) ?: false
                }

                DomainType.DOMAIN_TYPE_WEATHER -> {
                    return cardHolder?.scrollCard(direction) ?: false
                }

                DomainType.DOMAIN_TYPE_SCHEDULE -> {
                    LogUtils.d("scrollCard, schedule card not support scroll...")
                    return false
                }
            }
        }

        return false
    }

    fun nextPage(pageOffset: Int): Boolean {
        if (!isCardExpand()) {
            LogUtils.d("nextPage card not expand. illegal call this method.")
            return false
        }
        when (cardHolder?.domainType) {
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                return cardHolder?.nextPage(pageOffset) ?: false
            }
            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                return cardHolder?.nextPage(pageOffset) ?: false
            }
            DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                return cardHolder?.nextPage(pageOffset) ?: false
            }
        }

        return false
    }

    fun getCurrentPage(): PageInfo? {
        if (!isCardExpand()) {
            LogUtils.d("getCurrentPage card not expand. illegal call this method.")
            return null
        }
        when(cardHolder?.domainType) {
            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                return cardHolder?.getCurrentPage()
            }
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                return cardHolder?.getCurrentPage()
            }
            DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                return cardHolder?.getCurrentPage()
            }
        }
        return null
    }

    fun setCurrentPage(pageIndex: Int): Boolean {
        if (!isCardExpand()) {
            LogUtils.d("setCurrentPage card not expand. illegal call this method.")
            return false
        }
        when (cardHolder?.domainType) {
            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                return cardHolder?.setCurrentPage(pageIndex) ?: false
            }
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                return cardHolder?.setCurrentPage(pageIndex) ?: false
            }
            DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC -> {
                return cardHolder?.setCurrentPage(pageIndex) ?: false
            }
        }

        return false
    }

    fun getCurrentCardType() = cardHolder?.domainType

    private fun tryNotifyDataSetChange(chatMessage: ChatMessage) {
        sllCard?.let { sllCard ->
            LogUtils.d("tryNotifyDataSetChange total len is:${chatMessage.totalLen}")
            if (chatMessage.totalLen > 0 || chatMessage.isModelDeepFirstReason) {//faq 响应的内容流式上屏
                if (chatMessage.totalLen > maxLengthSmallCard || chatMessage.isModelDeepFirstReason) {//大卡片
                    val width = sllCard.width
                    LogUtils.d("tryNotifyDataSetChange card root view width is:$width")
                    if (width < windowWidthXXLarge) {//大模型卡片没有变成大卡片
                        val enlargeCardAnimRunningFlag = enlargeCardAnimator?.isRunning ?: false
                        LogUtils.d("tryNotifyDataSetChange enlargeCardAnimRunningFlag:$enlargeCardAnimRunningFlag")
                        chatMessageToDisplay = chatMessage
                        if (!enlargeCardAnimRunningFlag) {
                            LogUtils.d("tryNotifyDataSetChange enlargeLLMCardWithWidth")
                            enlargeLLMCardWithWidth(sllCard)
                            if(chatMessage.isModelDeepFirstReason) {
                                notifyDataSetChange(chatMessage)
                            }
                        } else {
                            LogUtils.d("tryNotifyDataSetChange enlarge Card Anim is running, just wait...")
                        }
                    } else {
                        notifyDataSetChange(chatMessage)
                    }
                } else { //小卡片
                    notifyDataSetChange(chatMessage)
                }
            } else {// 非faq 响应的内容流式上屏。
                val width = sllCard.width
                LogUtils.d("tryNotifyDataSetChange card root view width is:$width")
                if (width < windowWidthXXLarge) {//大模型卡片没有变成大卡片
                    val enlargeCardAnimRunningFlag = enlargeCardAnimator?.isRunning ?: false
                    LogUtils.d("tryNotifyDataSetChange enlargeCardAnimRunningFlag:$enlargeCardAnimRunningFlag")
                    chatMessageToDisplay = chatMessage
                    if (!enlargeCardAnimRunningFlag) {
                        LogUtils.d("tryNotifyDataSetChange enlargeLLMCardWithWidth")
                        enlargeLLMCardWithWidth(sllCard)
                    } else {
                        LogUtils.d("tryNotifyDataSetChange enlarge Card Anim is running, just wait...")
                    }
                } else {
                    notifyDataSetChange(chatMessage)
                }
            }
        }
    }

    private fun enlargeLLMCardWithWidth(cardRootView: ViewGroup) {
        val windowXCardTarget = (screenWidth - windowWidthXXLarge) / 2
        var animationCancelFlag = false
        enlargeCardAnimator = CardAnimationHelper.executeCardExpandWXAnimation(
            cardRootView,
            windowWidthXXLarge,
            windowXCardTarget,
            getCardWindowTag(),
            animationStart = { _ ->
                LogUtils.d("enlarge llm card WX animation start")
            },
            animationEnd = { _ ->
                LogUtils.d("enlarge llm card WX animation end, animationCancelFlag:$animationCancelFlag")
                if (!animationCancelFlag) {
                    if (chatMessageToDisplay != null) {
                        notifyDataSetChange(chatMessageToDisplay!!)
                    }
                }
            },
            animationCancel = { _ ->
                animationCancelFlag = true
                LogUtils.d("enlarge llm card WX animation cancel")
            }
        )
    }

    private fun notifyDataSetChange(chatMessage: ChatMessage) {
        LogUtils.d("notifyDataSetChange content:${chatMessage.content}")
        cardTimerTask?.cancelTask()
        cardTimerTask = null
        cardHolder?.apply {
            this as DomainCardHolder
            this.notifyDataSetChange(chatMessage)
            cardInfo?.chatMessages?.let { chatMessages ->
                chatMessages[0] = chatMessage
            }
        }
        LogUtils.d("notifyDataSetChange chatMessageToDisplay:$chatMessageToDisplay")
        if (chatMessageToDisplay != null) {
            chatMessageToDisplay = null
        }
    }

    suspend fun refreshGPT(id: String) {
        withContext(Dispatchers.Main) {
            when(RequestType.get(id)) {
                RequestType.REQUEST_TYPE_NORMAL -> {
                    tvGPTTip?.visibility = View.GONE
                    tvGPTTip?.text = context.getString(R.string.reason_from_voyah)
                }
                RequestType.REQUEST_TYPE_LANTU -> {
                    tvGPTTip?.visibility = View.VISIBLE
                    tvGPTTip?.text = context.getString(R.string.reason_from_voyah)
                }
                RequestType.REQUEST_TYPE_DEEPSEEK -> {
                    tvGPTTip?.visibility = View.VISIBLE
                    tvGPTTip?.text = context.getString(R.string.reason_from_deepseek)
                }
                else -> {
                    tvGPTTip?.visibility = View.GONE
                }
            }
        }

    }

    private fun setGPTTipVisible(visibility: Int = View.VISIBLE) {
        tvGPTTip?.visibility = visibility
        if (SystemConfigUtil.isDeepSeekMode()) {
            tvGPTTip?.text = context.getString(R.string.reason_from_deepseek)
        } else {
            tvGPTTip?.text = context.getString(R.string.reason_from_voyah)
        }
    }

    open fun onCardHolderDestroy() {
        LogUtils.d("onCardHolderDestroy")
        cardHolder?.destroy()
        cardHolder = null
    }

    fun isCollapseCardRunning(): Boolean {
        val result = collapseCardAnimator?.isRunning ?: false
        LogUtils.i("isCollapseCardRunning result:$result")
        return result
    }

    fun isSameSession(sessionId: String): Boolean {
        val targetSessionId = cardHolder?.sessionId ?: "sessionId-default"
        LogUtils.d("isSameSession targetSessionId:$targetSessionId")
        return TextUtils.equals(sessionId, targetSessionId)
    }

    private fun isSameRequest(requestId: String): Boolean {
        val targetRequestId = cardHolder?.requestId ?: "requestId-default"
        LogUtils.d("isSameRequest targetRequestId:$targetRequestId")
        return TextUtils.equals(requestId, targetRequestId)
    }

    fun isChatCardExpand(): Boolean {
        return isChatCard(cardHolder?.domainType) && isCardExpand()
    }

    /**
     *  判断卡片是否是信息展示类
     *  Returns: true-信息展示类；false-快捷交互类
     */
    private fun isInformationCard(domainType: String?): Boolean {
        LogUtils.d("isInformationShowCard, domainType: $domainType")
        val informationCardFlag = when (domainType) {
            DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM,
            DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM,
            DomainType.DOMAIN_TYPE_WEATHER,
            DomainType.DOMAIN_TYPE_STOCK,
            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                true
            }

            else -> {
                isChatCard(domainType)
            }
        }
        LogUtils.d("isInformationShowCard, informationCardFlag: $informationCardFlag")
        return informationCardFlag
    }

    override fun setVoiceServiceMode(voiceMode: Int) {
        LogUtils.d("setVoiceMode voiceMode:${getVoiceModeParsed(voiceMode)}, voiceState:$voiceState" +
                " current voice mode is:${getVoiceModeParsed(this.voiceMode)}")
        if (this.voiceMode != voiceMode) {
            this.voiceMode = voiceMode
            var asrText: String? = null
            var vpaState = VpaState.LISTENING_NIGHT_NORMAL
            if (this.voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
                vpaState = getVPAStatePath(VoiceMode.VOICE_MODE_ONLINE)
                if (voiceState == VoiceState.VOICE_STATE_LISTENING) {
                    asrText = getListeningTextWithLanguage(this.languageType)
                }
            } else {
                vpaState = getVPAStatePath(VoiceMode.VOICE_MODE_OFFLINE)

                if (voiceState == VoiceState.VOICE_STATE_LISTENING) {
                    asrText = context.getString(R.string.asr_offline_mode)
                }
            }

            sfvVPA?.startDraw(vpaState)

            if (!asrText.isNullOrEmpty()) {
                tvAsr?.text = asrText
                tvAsrPlaceHolder?.text = asrText
            }
        }
    }

    private fun getVPAStatePath(voiceMode: Int): VpaState {
        LogUtils.d("getVPAState nightModeFlag:$currentNightModeFlag," +
                " voiceMode:${getVoiceModeParsed(voiceMode)}")
        val vpaState = if (voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
            if ((voiceState != VoiceState.VOICE_STATE_SPEAKING)
                && (voiceState != VoiceState.VOICE_STATE_EXIT)
                && (voiceState != VoiceState.VOICE_STATE_INIT)
            ) {
                if (currentNightModeFlag) VpaState.LISTENING_NIGHT_NORMAL
                else VpaState.LISTENING_LIGHT_NORMAL
            } else if (voiceState == VoiceState.VOICE_STATE_SPEAKING) {
                if (currentNightModeFlag) VpaState.SPEAKING_NIGHT_NORMAL
                else VpaState.SPEAKING_LIGHT_NORMAL
            } else VpaState.LISTENING_LIGHT_NORMAL
        } else {
            if ((voiceState != VoiceState.VOICE_STATE_SPEAKING)
                && (voiceState != VoiceState.VOICE_STATE_EXIT)
                && (voiceState != VoiceState.VOICE_STATE_INIT)
            ) {
                if (currentNightModeFlag) VpaState.LISTENING_NIGHT_PRIVATE
                else VpaState.LISTENING_LIGHT_PRIVATE
            } else if (voiceState == VoiceState.VOICE_STATE_SPEAKING) {
                if (currentNightModeFlag) VpaState.SPEAKING_NIGHT_PRIVATE
                else VpaState.SPEAKING_LIGHT_PRIVATE
            } else VpaState.LISTENING_LIGHT_PRIVATE
        }
        return vpaState
    }

    override fun setVoiceLanguageType(languageType: Int) {
        LogUtils.d("setVoiceLanguageType current voiceMode:${getVoiceModeParsed(voiceMode)}" +
                ", languageType:${getLanguageTypeParsed(languageType)}" +
                ", current language type is:${getLanguageTypeParsed(this.languageType)}")
        if (this.languageType != languageType) {
            this.languageType = languageType
            var asrText: String? = null
            if (voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
                if (voiceState == VoiceState.VOICE_STATE_LISTENING) {
                    asrText = getListeningTextWithLanguage(this.languageType)
                }
            } else {
                if (voiceState == VoiceState.VOICE_STATE_LISTENING) {
                    asrText = context.getString(R.string.asr_offline_mode)
                }
            }

            if (!asrText.isNullOrEmpty()) {
                tvAsr?.text = asrText
                tvAsrPlaceHolder?.text = asrText
            }
        }
    }

    private fun getVoiceModeParsed(voiceMode: Int): String {
        return if (voiceMode == VoiceMode.VOICE_MODE_ONLINE) {
            "voice_mode_online"
        } else "voice_mode_offline"
    }

    private fun getLanguageTypeParsed(languageType: Int): String {
        return when(languageType) {
            LanguageType.SICHUANESE -> "四川话"
            LanguageType.CANTONESE -> "粤语"
            else -> "普通话"
        }
    }

    private fun getListeningTextWithLanguage(languageType: Int): String {
        val asrText = when (languageType) {
            LanguageType.SICHUANESE -> {
                context.getString(R.string.voice_state_listening_language_type_sichuan)
            }

            LanguageType.CANTONESE -> {
                context.getString(R.string.voice_state_listening_language_type_yueyu)
            }

            else -> {
                context.getString(R.string.voice_state_listening)
            }
        }
        return asrText
    }

    fun getCardState(): Int {
        LogUtils.d("getCardState")
        val cardExpand = isCardExpand()
        var result: Int = APIResult.INIT
        if (cardExpand) {
            if (cardHolder != null) {
                val informationCardExpandFlag = isInformationCard(cardHolder!!.domainType)
                result = if (informationCardExpandFlag) {
                    APIResult.INFORMATION_CARD_EXPAND
                } else {
                    APIResult.OTHER_CARD_EXPAND
                }
            } else {
                result = APIResult.NO_RESOURCE
            }
        } else {
            result = APIResult.CARD_COLLAPSED
        }
        LogUtils.d("getCardState result:$result")
        return result
    }

    fun getVPAState(): Int {
        LogUtils.d("getVPAState")
        var result: Int = APIResult.INIT

        if (isVAWindowShowing()) {
            return APIResult.VA_SHOWING
        } else if (isVAWindowShow()) {
            result = APIResult.VA_SHOWN
        }

        LogUtils.d("getVPAState result:$result")
        return result
    }

    override fun onUIModeChange(nightModeFlag: Boolean) {
        LogUtils.d("onUIModeChange nightModeFlag:$nightModeFlag")
        if (currentNightModeFlag == nightModeFlag) {
            LogUtils.d("onUIModeChange not change...")
            return
        }
        currentNightModeFlag = nightModeFlag
        LogUtils.d("onUIModeChange change...")
        val colorSolid = if (currentNightModeFlag) {
            context.resources.getColor(R.color.bg_vpa_typewriter_night)
        } else {
            context.resources.getColor(R.color.bg_vpa_typewriter_light)
        }

        sllCard?.let { sllCard ->
            if (isCardExpand()) {
                cardHolder?.onUIModeChange(nightModeFlag)
                executeCardCollapse(fromUserTouch = false, resetASRViewListening = false)
            } else {
            }
            sllCard.shapeDrawableBuilder
                .setSolidColor(colorSolid)
                .intoBackground()
        }

        sflVPAType?.apply {
            shapeDrawableBuilder
                .setSolidColor(colorSolid)
                .intoBackground()
        }

        val colorASRTextValid = if (currentNightModeFlag) {
            context.resources.getColor(R.color.asr_text_valid_night)
        } else {
            context.resources.getColor(R.color.asr_text_valid_light)
        }
        currentTextStype = TypeTextStyle.PRIMARY
        tvAsr?.setTextColor(colorASRTextValid)

        val colorGPTTip = if (currentNightModeFlag) {
            context.resources.getColor(R.color.gpt_tip_night)
        } else {
            context.resources.getColor(R.color.gpt_tip_light)
        }

        tvGPTTip?.setTextColor(colorGPTTip)


        val vpaStatePath = getVPAStatePath(voiceMode)
        sfvVPA?.startDraw(vpaStatePath)
    }

    @SuppressLint("Range")
    private fun getVPAWidth(parentViewHeight: Int): Int {
        val view = LayoutInflater.from(context).inflate(R.layout.window_vpa_asr, null, false)
        val h = View.MeasureSpec.makeMeasureSpec(parentViewHeight, View.MeasureSpec.EXACTLY)
        val w = View.MeasureSpec.makeMeasureSpec(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            View.MeasureSpec.AT_MOST
        )
        view.measure(w, h)
        val measuredWidth = view.measuredWidth
        LogUtils.d("getVPAWidth: view measuredWidth:$measuredWidth")
        return measuredWidth
    }

    private fun cancelVPAScaleInJob() {
        LogUtils.d("cancelVPAScaleInJob")
        vpaScaleInOutJob?.cancel() ?: run {
            LogUtils.d("cancelVPAScaleInJob vpaScaleInJob = null")
        }
        vpaScaleInOutJob = null
    }

    private fun onWindowSizeChange(cardExpandFlag: Boolean) {
        LogUtils.d("onWindowSizeChange cardExpandFlag:$cardExpandFlag")
        if (cardExpandFlag) {
            sflVPAType?.apply {
                shapeDrawableBuilder
                    .setShadowColor(0x00000000)
                    .intoBackground()
            }
            sllCard?.visibility = View.VISIBLE
        } else {
            val colorShadow = context.resources.getColor(R.color.shadow)
            sflVPAType?.apply {
                shapeDrawableBuilder
                    .setShadowColor(colorShadow)
                    .intoBackground()
            }
            sllCard?.visibility = View.INVISIBLE
        }
    }

    fun getVPATypeX(): Int {
        return xFullScreenVPAType - shadowSize
    }

    fun getCurrentTypeText(): String = tvAsr?.text.toString() ?: context.getString(R.string.voice_state_listening)

    fun onScreenStateChange() {
        LogUtils.d("onScreenStateChange")
    }








}