package com.voyah.window.windowholder

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.cockpit.window.model.WindowType
import com.voyah.window.R
import com.voyah.window.cardholder.BTPhoneCardHolder
import com.voyah.window.cardholder.DomainCardHolder
import com.voyah.window.cardholder.MultimediaCardHolder
import org.libpag.PAGFile
import org.libpag.PAGImageView

/**
 *  author : jie wang
 *  date : 2024/3/8 16:23
 *  description : vpa、typewriter、card 悬浮窗 的holder。
 */
class VPATyperCardWindowHolder(context: Context): BaseWindowHolder(context) {
    private var topLayoutHeight = 0
    private var typewriterView: TextView? = null
    private var vpaView: PAGImageView? = null
    private var llContent: ViewGroup? = null
    private var hasASR = false
    private var contentParams: FrameLayout.LayoutParams? = null
    private var viewWidth = -1

    private var clickCount = 0

    private val windowWidthSmall: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_230)
    }

    private val windowWidthMedium: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_600)
    }
    private val windowWidthLarge: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_900)
    }
    private val windowWidthXLarge: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_1067)
    }

    private var currentDomainType: String? = null
    private var btPhoneCardHolder: BTPhoneCardHolder? = null
    private var domainCardHolder: DomainCardHolder? = null
    private var multimediaCardHolder: MultimediaCardHolder? = null
    var vtcWindowCallback: VTCWindowCallback? = null

    val pagListeningOnline = PAGFile.Load(context.assets, "vpa_voice_listening_online.pag")
    val pagListeningOffline = PAGFile.Load(context.assets, "vpa_voice_listening_offline.pag")
    val pagSpeakingOnline = PAGFile.Load(context.assets, "vpa_voice_speaking_online.pag")
    val pagSpeakingOffline = PAGFile.Load(context.assets, "vpa_voice_speaking_offline.pag")

    companion object {
        const val TAG = "VTCWindowHolder"
    }

    override fun onVoiceAwake(voiceServiceMode: Int, voiceLocation: Int) {
        LogUtils.d("onVoiceAwake voiceServiceMode:$voiceServiceMode, voiceLocation:$voiceLocation")
        voiceMode = voiceServiceMode
        wakeVoiceLocation = voiceLocation
        if (!isWindowShow()) {
            showWindow()
        } else {
            collapseCard()
            setWindowSmall()
        }
    }

    override fun onVoiceListening() {
        val cardExpand = EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand
        LogUtils.d("onVoiceListening cardExpand:$cardExpand")
        if (cardExpand == true) {
            if (hasASR) hasASR = false
            typewriterView?.text = context.getString(R.string.voice_state_listening)
            vpaView?.composition = getListeningAnimRes()
        } else {
            setWindowSmall()
        }
    }

    override fun getLayoutRes() = R.layout.window_vpa_typewriter_card

    override fun isWindowShow() =
        EasyFloat.isShow(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)

    override fun showWindow() {
        val x = (screenWidth - windowWidthSmall) / 2
        LogUtils.d("showWindow: screenWidth:${screenWidth}, x:$x")
        EasyFloat.with(context)
            .setTag(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, statusBarHeight)
            .setDragEnable(false)
            .setAnimator(null)
            .setWatchOutside(true)
//            .setWindowParamsType(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 21)
            .setLayout(getLayoutRes(), object : OnInvokeView {
                override fun invoke(view: View) {
                    topLayoutHeight = view.height
                    llContent = view.findViewById<LinearLayout>(R.id.ll_content).also { contentView ->
                        viewWidth = contentView.width
                        contentParams = contentView.layoutParams as FrameLayout.LayoutParams
                    }

                    LogUtils.d("invoke: viewWidth:$viewWidth")
                    vpaView = view.findViewById<PAGImageView>(R.id.pag_vpa)?.apply {
                        showVPA()
                        setOnClickListener {
                            clickCount++
                            if (clickCount >= 5) {
                                val intent = Intent()
                                intent.action = "com.voyah.ai.debug.tools"
                                intent.setPackage("com.voyah.vcos.tools")
                                intent.flags = 0
                                context.sendBroadcast(intent)
                                clickCount = 0
                            }
                        }
                    }

                    typewriterView = view.findViewById<TextView>(R.id.typewriter_view).apply {
                        ellipsize = TextUtils.TruncateAt.START
                        isSingleLine = true
                        setOnClickListener {
                            val cardExpand =
                                EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)
                                    ?.cardExpand ?: false
                            LogUtils.d("typewriter click cardExpand:$cardExpand")
                            if (!cardExpand) {
                                vtcWindowCallback?.onVTCDismiss()
                            }
                        }
                        LogUtils.d("invoke voice service mode:${voiceMode}")
                    }
                }

            })
            .registerCallback {
                createResult { b, s, _ ->
                    LogUtils.d("createResult is success:$b, reason:$s")
                }

                show {
                    LogUtils.d("show 悬浮窗。")
                }

                dismiss {
                    LogUtils.d("vpa-typewriter-card dismiss ")
                }

                outsideTouch { _, _ ->
                    LogUtils.d("outsideTouch，点击到窗体区域外。")
                    collapseCard()
                    setWindowSmall()
                }
            }
            .show()
    }


    /**
     *  语音唤醒状态，缩放动画显示vpa。
     */
    private fun PAGImageView.showVPA() {
        composition = getListeningAnimRes()
        setRepeatCount(-1)
        play()

        val animatorSet = AnimatorSet()
        pivotX = width / 2.0f
        pivotY = height / 2.0f
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(this, "scaleX", 0f, 1.0f),
            ObjectAnimator.ofFloat(this, "scaleY", 0f, 1.0f)
        )

        animatorSet.duration = 200L
        animatorSet.start()
    }

    private fun isCardExpand() : Boolean {
        return EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand ?: false
    }

    fun collapseCard() {
        val cardExpand = EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand
        LogUtils.d("collapseCard cardExpand:$cardExpand")
        if (cardExpand == true) {
            llContent?.findViewById<View>(R.id.ll_card_container)?.visibility = View.GONE
            EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand = false
            vtcWindowCallback?.onCardCollapse()

            btPhoneCardHolder?.onCardCollapse()
            domainCardHolder?.onCardCollapse()
            multimediaCardHolder?.onCardCollapse()
        }
    }

    fun inputText(text: String, textStyle: Int) {
        LogUtils.d("inputText: hasASR:$hasASR, textStyle:$textStyle")
        if (!hasASR) {
            hasASR = true
            viewWidth = windowWidthMedium
            contentParams?.width = viewWidth
            EasyFloat.updateFloat(
                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                x = (screenWidth - viewWidth) / 2,
                width = viewWidth
            )
        }
        typewriterView?.text = text
    }

    fun expandCard(cardInfo: CardInfo) {
        LogUtils.d("expandCard")
        currentDomainType = cardInfo.domainType
        if (currentDomainType.isNullOrEmpty()) {
            LogUtils.w("expandCard invalid domain type.")
            return
        }
        llContent?.let {
            when (currentDomainType) {
                DomainType.DOMAIN_TYPE_SCHEDULE -> {
                    if (it.width != windowWidthLarge) {
                        contentParams?.width = windowWidthLarge
                        viewWidth = windowWidthLarge

                        EasyFloat.updateFloat(
                            WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                            x = (screenWidth - viewWidth) / 2,
                            width = viewWidth,
                        )
                    }
                }

                DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                    if (it.width != windowWidthXLarge) {
                        contentParams?.width = windowWidthXLarge
                        viewWidth = windowWidthXLarge

                        EasyFloat.updateFloat(
                            WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                            x = (screenWidth - viewWidth) / 2,
                            width = viewWidth
                        )
                    }
                }

                else -> {
                    if (it.width != windowWidthMedium) {
                        contentParams?.width = windowWidthMedium
                        viewWidth = windowWidthMedium

                        EasyFloat.updateFloat(
                            WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                            x = (screenWidth - viewWidth) / 2,
                            width = viewWidth,
                        )
                    }
                }
            }

            LogUtils.d("expandCard: llContent width:${it.width}, viewWidth:$viewWidth")
            showCard(cardInfo)
        }
    }

    private fun showCard(cardInfo: CardInfo) {
        if (llContent == null) return
        val cardView = getCardView()
        llContent!!.findViewById<LinearLayout>(R.id.ll_card_container)?.apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }
            val childIndex1 = getChildAt(1)
            if (childIndex1 != null) {
                removeView(childIndex1)
            }

            addView(cardView)
        }

        LogUtils.d("showCard")
        when (currentDomainType) {
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                val contacts = cardInfo.btPhoneInfo?.contacts
                if (!contacts.isNullOrEmpty()) {
                    LogUtils.d("expand contact card.")
                    btPhoneCardHolder?.expandCard(contacts)
                    EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand = true
                }
            }

            DomainType.DOMAIN_TYPE_WEATHER -> {
                if (!cardInfo.weathers.isNullOrEmpty()) {
                    domainCardHolder?.apply {
                        domainType = DomainType.DOMAIN_TYPE_WEATHER
                        expandCard(cardInfo.weathers)
                        EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand = true
                    }
                }
            }

            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                if (!cardInfo.schedules.isNullOrEmpty()) {
                    domainCardHolder?.apply {
                        domainType = DomainType.DOMAIN_TYPE_SCHEDULE
                        expandCard(cardInfo.schedules)
                        EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand = true
                    }
                }
            }

            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                if (!cardInfo.multimediaInfos.isNullOrEmpty()) {
                    multimediaCardHolder?.apply {
                        domainType = DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO
                        expandCard(cardInfo.multimediaInfos)
                        EasyFloat.getConfig(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD)?.cardExpand = true
                    }
                }
            }
        }
    }

    private fun getCardView(): View {
        return when (currentDomainType) {
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                if (btPhoneCardHolder == null) {
                    btPhoneCardHolder = BTPhoneCardHolder(context).apply {
                        domainItemCallback = { position, viewType ->
                            vtcWindowCallback?.onDomainItemClick(position, viewType)
                        }

                        cardScrollCallback = {direction, canScroll ->
                            vtcWindowCallback?.onCardCanScroll(
                                DomainType.DOMAIN_TYPE_BT_PHONE,
                                direction,
                                canScroll)
                        }
                    }
                }
                btPhoneCardHolder!!.getCardView()
            }
            DomainType.DOMAIN_TYPE_WEATHER,
            DomainType.DOMAIN_TYPE_SCHEDULE -> {
                if (domainCardHolder == null) {
                    domainCardHolder = DomainCardHolder(context).apply {
                        domainItemCallback = { position, viewType ->
                            vtcWindowCallback?.onDomainItemClick(position, viewType)
                        }

                        cardScrollCallback = {direction, canScroll ->
                            vtcWindowCallback?.onCardCanScroll(domainType!!, direction, canScroll)
                        }
                    }
                }
                domainCardHolder!!.run {
                    getCardView()
                }
            }
            DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO -> {
                if (multimediaCardHolder == null) {
                    multimediaCardHolder = MultimediaCardHolder(context)
                }

                multimediaCardHolder!!.run {
                    getCardView()
                }
            }

            else -> {
                throw NullPointerException("domainType can not be null")
            }
        }
    }

    fun refreshCard(cardInfo: CardInfo) {
        LogUtils.d("refreshCard")
        when(cardInfo.domainType) {
            DomainType.DOMAIN_TYPE_BT_PHONE -> {
                val contacts = cardInfo.btPhoneInfo?.contacts
                if (contacts.isNullOrEmpty()) return

                if (TextUtils.equals(currentDomainType, cardInfo.domainType)) {
                    btPhoneCardHolder?.refreshCard(contacts)
                } else {
                    currentDomainType = cardInfo.domainType
                    showCard(cardInfo)
                }
            }
            DomainType.DOMAIN_TYPE_WEATHER -> {
                if (cardInfo.weathers.isNullOrEmpty()) return
                if (TextUtils.equals(currentDomainType, cardInfo.domainType)) {
                    domainCardHolder?.refreshCard(cardInfo.weathers)
                } else {
                    currentDomainType = cardInfo.domainType
                    showCard(cardInfo)
                }
            }
        }
    }



    /**
     *  将window更改为小尺寸，vpa、typewriter为聆听态，且card收起。
     */
    private fun setWindowSmall() {
        if (hasASR) hasASR = false
        viewWidth = windowWidthSmall
        contentParams?.width = viewWidth
        EasyFloat.updateFloat(
            WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
            x = (screenWidth - viewWidth) / 2,
            width = viewWidth
        )
        typewriterView?.text = context.getString(R.string.voice_state_listening)
        vpaView?.composition = getListeningAnimRes()
    }


    private fun getListeningAnimRes(): PAGFile {
        var pagComposition = pagListeningOnline
        if (voiceMode == VoiceMode.VOICE_MODE_OFFLINE) {
            pagComposition = pagListeningOffline
        }
        return pagComposition
    }

    private fun getInputtingAnimRes(): PAGFile {
        var pagComposition = pagSpeakingOnline
        if (voiceMode == VoiceMode.VOICE_MODE_OFFLINE) {
            pagComposition = pagSpeakingOffline
        }
        return pagComposition
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onVoiceSpeaking")
        vpaView?.composition = getInputtingAnimRes()
    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        hasASR = false
        EasyFloat.dismiss(WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD, true)
    }

    fun scrollCard(direction: Int): Boolean {
        if (!isCardExpand()) {
            LogUtils.d("scrollCard card not expand. illegal call this method.")
            return false
        } else {
            when (currentDomainType) {
                DomainType.DOMAIN_TYPE_BT_PHONE -> {
                    return btPhoneCardHolder?.scrollCard(direction) ?: false
                }

                DomainType.DOMAIN_TYPE_WEATHER -> {
                    return domainCardHolder?.scrollCard(direction) ?: false
                }

                DomainType.DOMAIN_TYPE_SCHEDULE -> {
                    LogUtils.d("scrollCard, schedule card not support scroll...")
                    return false
                }
            }
        }

        return false
    }

    interface VTCWindowCallback {
        fun onVTCDismiss()
        fun onCardCollapse()

        fun onContactSelect(position: Int)

        fun onDomainItemClick(position: Int, viewType: Int)

        fun onCardCanScroll(cardType: String, direction: Int, canScroll: Boolean)
    }
}