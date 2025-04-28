package com.voyah.window.manager

import android.content.Context
import android.os.RemoteCallbackList
import androidx.annotation.MainThread
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.lzf.easyfloat.EasyFloat
import com.voyah.cockpit.window.IReceiveWindowMsgCallback
import com.voyah.cockpit.window.IVoyahWindowManager
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.ExecuteFeedbackInfo
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.WindowAction
import com.voyah.cockpit.window.model.WindowMessage
import com.voyah.cockpit.window.model.WindowType
import com.voyah.voice.framework.ext.normalScope
import com.voyah.cockpit.window.util.DateUtil
import com.voyah.window.model.WeatherConfig
import com.voyah.window.util.WeatherConfigUtil
import com.voyah.window.windowholder.ExecFeedbackWindowHolder
import com.voyah.window.windowholder.VPATyperCardWindowHolder
import com.voyah.window.windowholder.VoiceWaveWindowHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *  author : jie wang
 *  date : 2024/4/12 13:48
 *  description :
 */
class VoyahWindowManager(val context: Context) : IVoyahWindowManager.Stub() {
    private val receiveCallbacks = RemoteCallbackList<IReceiveWindowMsgCallback>()

    private var vtcWindowHolder: VPATyperCardWindowHolder? = null
    private var voiceWaveWindowHolder: VoiceWaveWindowHolder? = null
    private var leftFeedbackWindowHolder: ExecFeedbackWindowHolder? = null
    private var rightFeedbackWindowHolder: ExecFeedbackWindowHolder? = null

    private var gson: Gson? = null
    private var normalScope: CoroutineScope? = null

    private var action: String? = null

    private val weatherConfigList: List<WeatherConfig> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        LogUtils.d("VoyahWindowManager, lazy init readSample")
        WeatherConfigUtil.readSamples(context)
    }

    private var countDownLatch: CountDownLatch? = null

    init {
        normalScope = normalScope()
    }

    override fun sendWindowMessage(windowMessage: String?) {
        LogUtils.w("sendWindowMessage not support~")
    }

    override fun registerReceiveCallback(callback: IReceiveWindowMsgCallback?) {
        callback?.let {
            receiveCallbacks.register(it)
        }
    }

    override fun unRegisterReceiveCallback(callback: IReceiveWindowMsgCallback?) {
        callback?.let {
            receiveCallbacks.unregister(it)
        }
    }

    override fun onVoiceAwake(voiceServiceMode: Int, voiceLocation: Int) {
        LogUtils.d("onVoiceAwake voiceServiceMode:$voiceServiceMode, voiceLocation:$voiceLocation")
        normalScope?.launch {
            if (vtcWindowHolder == null) getVTCWindowHolder()
            if (voiceWaveWindowHolder == null) getVoiceWaveWindowHolder()
            vtcWindowHolder?.apply {
                onVoiceAwake(voiceServiceMode, voiceLocation)

            }
            voiceWaveWindowHolder?.apply {
                onVoiceAwake(voiceServiceMode, voiceLocation)
            }
        }
    }

    override fun onVoiceListening() {
        LogUtils.d("onVoiceListening")
        normalScope?.launch {
            vtcWindowHolder?.apply {
                onVoiceListening()
            }
            voiceWaveWindowHolder?.apply {
                onVoiceListening()
            }
        }
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onVoiceSpeaking")
        normalScope?.launch {
            vtcWindowHolder?.onVoiceSpeaking()
            voiceWaveWindowHolder?.onVoiceSpeaking()
        }
    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        normalScope?.launch {
            dismissAllWindow()
        }
    }

    override fun inputTypewriterText(text: String, textStyle: Int) {
        LogUtils.d("inputTypewriterText: text:$text, textStyle:$textStyle")
        normalScope?.launch {
            vtcWindowHolder?.inputText(text, textStyle)
        }
    }

    override fun showCard(cardJson: String?) {
        normalScope?.launch(Dispatchers.Default) {
            LogUtils.d("showCard cardJson:$cardJson")
            var cardInfo: CardInfo? = null
            try {
                cardInfo = getGson().fromJson(cardJson, CardInfo::class.java)
            } catch (e: Exception) {
                LogUtils.e("showCard invalid cardJson.")
            }
            if (cardInfo == null) return@launch
            if (cardInfo.domainType == DomainType.DOMAIN_TYPE_WEATHER) {
                formatWeatherOriginalDate(cardInfo)
            }
            this@VoyahWindowManager.action = cardInfo.action

            withContext(Dispatchers.Main) {
                vtcWindowHolder?.expandCard(cardInfo)
            }
        }
    }

    override fun updateCardData(cardJson: String?) {
        LogUtils.d("updateCardData cardJson:$cardJson")
        val cardInfo = getGson().fromJson(cardJson, CardInfo::class.java)
        normalScope?.launch {
            vtcWindowHolder?.refreshCard(cardInfo)
        }
    }

    override fun dismissCard() {
        LogUtils.d("dismissCard")
        normalScope?.launch {
            vtcWindowHolder?.collapseCard()
        }
    }

    override fun showExecuteFeedbackWindow(feedbackJson: String?) {
        LogUtils.d("showExecuteFeedbackWindow feedbackJson:$feedbackJson")
        val feedbackInfo =
            getGson().fromJson(feedbackJson, ExecuteFeedbackInfo::class.java) ?: return
        normalScope?.launch {
            when (feedbackInfo.location) {
                VoiceLocation.FRONT_LEFT -> {
                    val show = EasyFloat.isShow(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT)
                    if (!show) {
                        getLeftFeedbackWindowHolder().apply {
                            this.feedbackInfo = feedbackInfo
                            showWindow()
                        }
                    }
                }

                VoiceLocation.FRONT_RIGHT,
                VoiceLocation.REAR_LEFT,
                VoiceLocation.REAR_RIGHT -> {
                    val show = EasyFloat.isShow(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT)
                    if (!show) {
                        getRightFeedbackWindowHolder().apply {
                            this.feedbackInfo = feedbackInfo
                            showWindow()
                        }
                    }
                }
            }
        }
    }

    override fun updateExecuteFeedbackWindowData(feedbackJson: String?) {
        LogUtils.d("updateExecuteFeedbackWindowData feedbackJson:$feedbackJson")
        val feedbackInfo = getGson().fromJson(feedbackJson, ExecuteFeedbackInfo::class.java)
    }

    override fun dismissExecuteFeedbackWindow(voiceLocation: Int) {
        LogUtils.d("dismissExecuteFeedbackWindow voiceLocation:$voiceLocation")
        normalScope?.launch {
            when (voiceLocation) {
                VoiceLocation.FRONT_LEFT -> {
                    leftFeedbackWindowHolder?.dismissWindow(voiceLocation)
                }

                VoiceLocation.FRONT_RIGHT,
                VoiceLocation.REAR_LEFT,
                VoiceLocation.REAR_RIGHT -> {
                    rightFeedbackWindowHolder?.dismissWindow(voiceLocation)
                }
            }
        }
    }

    override fun scrollCard(direction: Int) {
        LogUtils.d("scrollCard direction:$direction")
        normalScope?.launch {
            vtcWindowHolder?.scrollCard(direction)
        }
    }

    override fun scrollCardView(direction: Int): Boolean {
        LogUtils.d("scrollCardView direction:$direction")
        countDownLatch = CountDownLatch(1)
        var canScroll = false
        normalScope?.launch {
            canScroll = vtcWindowHolder?.scrollCard(direction) ?: false
            countDownLatch?.countDown()
        }

        try {
            LogUtils.d("scrollCard await ...")
            countDownLatch!!.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("scrollCard e:$e")
            canScroll = false
        } finally {
            countDownLatch = null
        }

        LogUtils.d("scrollCard canScroll:$canScroll")
        return canScroll
    }


    private fun getGson(): Gson {
        if (gson == null) gson = Gson()
        return gson!!
    }

    private fun getVTCWindowHolder() {
        vtcWindowHolder = VPATyperCardWindowHolder(context.applicationContext).apply {
            vtcWindowCallback = object : VPATyperCardWindowHolder.VTCWindowCallback {
                override fun onVTCDismiss() {
                    LogUtils.d("onVTCDismiss")
                    dismissAllWindow()
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            WindowMessage(
                                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                                WindowAction.WINDOW_ACTION_DISMISS
                            ).let { msg ->
                                onReceiveWindowMessage(msg)
                            }
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onCardCollapse() {
                    LogUtils.d("onCardCollapse")
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            WindowMessage(
                                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                                WindowAction.WINDOW_ACTION_COLLAPSE_CARD
                            ).let { msg ->
                                onReceiveWindowMessage(msg)
                            }
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onContactSelect(position: Int) {
                    LogUtils.d("onContactSelect position:$position")
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            val cardInfo = CardInfo().also { cardInfo ->
                                cardInfo.position = position
                                cardInfo.domainType = DomainType.DOMAIN_TYPE_BT_PHONE
                            }
                            onReceiveVoyahWindowMessage(getGson().toJson(cardInfo))
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onDomainItemClick(position: Int, viewType: Int) {
                    LogUtils.d("onDomainItemClick position:$position, viewType:$viewType")
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            val cardInfo = CardInfo().also { cardInfo ->
                                cardInfo.position = position
                                when (viewType) {
                                    ViewType.SCHEDULE_TYPE_MORE -> {
                                        cardInfo.domainType = DomainType.DOMAIN_TYPE_SCHEDULE
                                    }

                                    ViewType.BT_PHONE_TYPE -> {
                                        cardInfo.domainType = DomainType.DOMAIN_TYPE_BT_PHONE
                                        cardInfo.action = this@VoyahWindowManager.action
                                        this@VoyahWindowManager.action = null
                                    }
                                }
                                cardInfo.itemType = viewType

                            }
                            onReceiveVoyahWindowMessage(getGson().toJson(cardInfo))
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onCardCanScroll(cardType: String, direction: Int, canScroll: Boolean) {
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            onCardScroll(cardType, direction, canScroll)
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }
            }
        }
    }

    private fun getVoiceWaveWindowHolder() {
        voiceWaveWindowHolder = VoiceWaveWindowHolder(context.applicationContext)
    }

    private fun getLeftFeedbackWindowHolder(): ExecFeedbackWindowHolder {
        if (leftFeedbackWindowHolder == null)
            leftFeedbackWindowHolder = ExecFeedbackWindowHolder(context.applicationContext)
        return leftFeedbackWindowHolder!!
    }

    private fun getRightFeedbackWindowHolder(): ExecFeedbackWindowHolder {
        if (rightFeedbackWindowHolder == null)
            rightFeedbackWindowHolder = ExecFeedbackWindowHolder(context.applicationContext)
        return rightFeedbackWindowHolder!!
    }

    private fun formatWeatherOriginalDate(cardInfo: CardInfo) {

        if (!cardInfo.weathers.isNullOrEmpty() && cardInfo.weathers[0] != null) {
            if (cardInfo.weathers[0].itemType == ViewType.WEATHER_TYPE_1) {
                val weather = cardInfo.weathers[0]
                val date = weather.formatDate
                weather.formatDate = DateUtil.getCustomStr1(date)

//                WeatherConfigUtil.setWeatherIcon(weather)

            } else if (cardInfo.weathers[0].itemType == ViewType.WEATHER_TYPE_2) {
                for (i in 0 until cardInfo.weathers.size) {
                    val originalDate = cardInfo.weathers[i].formatDate
                    if (!originalDate.isNullOrEmpty()) {
                        cardInfo.weathers[i].formatDate = DateUtil.getCustomStr2(originalDate)
                    }
                }
            }
        }
    }

    private fun dismissAllWindow() {
        LogUtils.d("dismissAllWindow")
        vtcWindowHolder?.onVoiceExit()
        voiceWaveWindowHolder?.onVoiceExit()
        leftFeedbackWindowHolder?.onVoiceExit()
        rightFeedbackWindowHolder?.onVoiceExit()
    }

    @MainThread
    fun onDestroy() {
        LogUtils.d("onDestroy")
        dismissAllWindow()
        normalScope?.cancel()
        normalScope = null
    }

}