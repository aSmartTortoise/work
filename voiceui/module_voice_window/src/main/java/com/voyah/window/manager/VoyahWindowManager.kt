package com.voyah.window.manager

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteCallbackList
import android.util.Log
import androidx.annotation.MainThread
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.voyah.cockpit.window.ICallback
import com.voyah.cockpit.window.IReceiveWindowMsgCallback
import com.voyah.cockpit.window.IVoyahWindowManager
import com.voyah.cockpit.window.model.APIResult
import com.voyah.cockpit.window.model.CardInfo
import com.voyah.cockpit.window.model.DomainType
import com.voyah.cockpit.window.model.ExecuteFeedbackInfo
import com.voyah.cockpit.window.model.LanguageType
import com.voyah.cockpit.window.model.PageInfo
import com.voyah.cockpit.window.model.ScreenType
import com.voyah.cockpit.window.model.StreamMode
import com.voyah.cockpit.window.model.ViewType
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.cockpit.window.model.VoiceState
import com.voyah.cockpit.window.model.WindowAction
import com.voyah.cockpit.window.model.WindowMessage
import com.voyah.cockpit.window.model.WindowType
import com.voyah.voice.framework.ext.normalScope
import com.voyah.window.interfaces.VTCWindowCallback
import com.voyah.cockpit.window.model.UIMessage
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.window.model.CardDataWrapper
import com.voyah.window.windowholder.BaseVTCWindowHolder
import com.voyah.window.windowholder.CeilingScreenWindowHolder
import com.voyah.window.windowholder.IScreenStateProvider
import com.voyah.window.windowholder.MainScreenWindowHolder
import com.voyah.window.windowholder.PassengerScreenWindowHolder
import com.voyah.window.windowholder.VoiceWaveWindowHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *  author : jie wang
 *  date : 2024/4/12 13:48
 *  description :
 */
class VoyahWindowManager(val context: Context) : IVoyahWindowManager.Stub(), IScreenStateProvider {
    private val receiveCallbacks = RemoteCallbackList<IReceiveWindowMsgCallback>()

    private var mainWindowHolder: MainScreenWindowHolder? = null
    private var passengerWindowHolder: PassengerScreenWindowHolder? = null
    private var ceilingWindowHolder: CeilingScreenWindowHolder? = null
    private var voiceWaveWindowHolder: VoiceWaveWindowHolder? = null

    private var gson: Gson? = null
    private var normalScope: CoroutineScope


    private var voiceMode = VoiceMode.VOICE_MODE_ONLINE
    private var languageType = LanguageType.MANDARIN
    private var windowHelper: WindowHelper? = null

    private val screenState = mutableListOf(1, 1 ,1)


    private val messageHandler = VoiceMessageHandler(WeakReference(this))

    init {
        normalScope = normalScope()
    }

    companion object {
        const val TAG = "VoyahWindowManager"
        const val KEY_SCREEN_TYPE = "screenType"
        const val KEY_SCREEN_ENABLE = "screenEnable"
        const val MSG_SHOW_VPA = 100
        const val MSG_VOICE_EXIT = 101
        const val MSG_DISMISS_VOICE_VIEW = 102
        const val MSG_SHOW_WAVE = 103
        const val MSG_DISMISS_WAVE_VIEW = 104
        const val MSG_SHOW_CARD = 105
        const val MSG_DISMISS_CARD = 106
        const val MSG_SET_VOICE_MODE = 107
        const val MSG_SET_LANGUAGE_TYPE = 108
        const val MSG_SET_CEILING_SCREEN_ENABLE = 109
        const val MSG_SET_SCREEN_ENABLE = 110
    }

    class VoiceMessageHandler(private val referenceWindowManager: WeakReference<VoyahWindowManager>) :
        Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            LogUtils.d("handleMessage what:${msg.what}, " +
                    "type:${referenceWindowManager.get()?.getMessageTypeString(msg.what)}")
            when (msg.what) {
                MSG_SHOW_VPA -> {
                    if (msg.obj is UIMessage) {
                        val msgEntity = msg.obj as UIMessage
                        referenceWindowManager.get()?.handleShowVPAMessage(msgEntity)
                    }
                }

                MSG_VOICE_EXIT -> {
                    referenceWindowManager.get()?.dismissAllWindow()
                }

                MSG_DISMISS_VOICE_VIEW -> {
                    if (msg.obj is Int) {
                        val screenType = msg.obj as Int
                        referenceWindowManager.get()?.handleDismissVoiceViewMessage(screenType)
                    }
                }

                MSG_SHOW_WAVE -> {
                    if (msg.obj is Int) {
                        val location = msg.obj as Int
                        referenceWindowManager.get()?.handleShowWaveMessage(location)
                    }
                }

                MSG_DISMISS_WAVE_VIEW -> {
                    referenceWindowManager.get()?.handleDismissWaveViewMessage()
                }

                MSG_SHOW_CARD -> {
                    if (msg.obj is CardInfo) {
                        val cardInfo = msg.obj as CardInfo
                        referenceWindowManager.get()?.handleShowCardMessage(cardInfo)
                    }
                }

                MSG_DISMISS_CARD -> {
                    if (msg.obj is Int) {
                        val screenType = msg.obj as Int
                        referenceWindowManager.get()?.handleDismissCardMessage(screenType)
                    }
                }

                MSG_SET_VOICE_MODE -> {
                    if (msg.obj is Int) {
                        val voiceMode = msg.obj as Int
                        referenceWindowManager.get()?.handleSetVoiceModeMessage(voiceMode)
                    }
                }

                MSG_SET_LANGUAGE_TYPE -> {
                    if (msg.obj is Int) {
                        val languageType = msg.obj as Int
                        referenceWindowManager.get()?.handleSetLanguageTypeMessage(languageType)
                    }
                }

                MSG_SET_SCREEN_ENABLE -> {
                    msg.data?.let {
                        val screenType = it.getInt(KEY_SCREEN_TYPE)
                        val enable = it.getBoolean(KEY_SCREEN_ENABLE)
                        referenceWindowManager.get()
                            ?.handleSetScreenEnableMessage(screenType, enable)
                    }
                }
            }
        }
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

    override fun showVoiceView(voiceServiceMode: Int, languageType: Int, voiceLocation: Int) {
        LogUtils.d("showVoiceView voiceServiceMode:$voiceServiceMode, languageType:$languageType, " +
                "voiceLocation:$voiceLocation")
        onVoiceAwake(voiceServiceMode, languageType, voiceLocation)
    }

    @Deprecated(message = " no longer maintained", replaceWith = ReplaceWith(
        "showVoiceVpa(uiMessage) or showWave(location)"))
    override fun onVoiceAwake(voiceServiceMode: Int, languageType: Int, voiceLocation: Int) {
        LogUtils.d("onVoiceAwake voiceServiceMode:$voiceServiceMode, languageType:$languageType, " +
                "voiceLocation:$voiceLocation")
    }

    override fun onVoiceListening() {
        LogUtils.d("onVoiceListening")
    }

    private fun setVoiceListening(screenType: Int) {
        LogUtils.d("setVoiceListening screenType:$screenType")
        getWindowHolderWithScreenType(screenType)?.run {
            onVoiceListening()
        }

        //先注释，声浪不处理状态
        //voiceWaveWindowHolder?.onVoiceListening()
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onVoiceSpeaking")
        normalScope?.launch {
            setVoiceSpeaking(ScreenType.MAIN)
        }
    }

    private fun setVoiceSpeaking(screenType: Int) {
        LogUtils.d("setVoiceSpeaking screenType:$screenType")
        getWindowHolderWithScreenType(screenType)?.run {
            onVoiceSpeaking()
        }
        //先注释，声浪不处理状态
        //voiceWaveWindowHolder?.onVoiceSpeaking()
    }

    override fun onVoiceExit() {
        Log.d(TAG, "onVoiceExit app version name:${AppUtils.getAppVersionName()}")
        val message = Message.obtain().apply {
            what = MSG_VOICE_EXIT
        }
        messageHandler.sendMessage(message)
    }

    override fun inputTypewriterText(text: String, textStyle: Int) {
        LogUtils.d("inputTypewriterText: text:$text, textStyle:$textStyle")
        normalScope?.launch {
            changeTypeWriterText(text, textStyle, ScreenType.MAIN)
        }
    }

    private fun changeTypeWriterText(text: String, textStyle: Int, screenType: Int) {
        LogUtils.d("changeTypeWriterText text:$text, textStyle:$textStyle, screenType:$screenType")
        getWindowHolderWithScreenType(screenType)?.inputText(text, textStyle)
    }

    override fun showCard(cardJson: String?) {
        try {
            LogUtils.d("showCard cardJson:$cardJson")
            val cardInfo = getGson().fromJson(cardJson, CardInfo::class.java)
            val message = Message.obtain().apply {
                what = MSG_SHOW_CARD
                obj = cardInfo
            }
            messageHandler.sendMessage(message)
        } catch (e: Exception) {
            LogUtils.e("showCard invalid cardJson.")
        }
    }

    private fun handleShowCardMessage(cardInfo: CardInfo) {
        val cardData = preProcessCardInfo(cardInfo)
        handleCards(cardData.targetScreen)
    }

    private fun preProcessCardInfo(cardInfo: CardInfo) : CardDataWrapper {
        LogUtils.d("preProcessCardInfo cardInfo:$cardInfo")
        //处理屏幕
        val screenType = cardInfo.screenType
        val targetScreen = if (screenState[screenType] == 1) {
            screenType
        } else {
            ScreenType.MAIN
        }

        //处理卡片信息本身 (流式)
        val domainType = cardInfo.domainType
        val streamMode = cardInfo.chatMessages?.get(0)?.streamMode ?: StreamMode.NOT_STREAM
        when (domainType) {
            DomainType.DOMAIN_TYPE_GOSSIP,
            DomainType.DOMAIN_TYPE_FAQ,
            DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA,
            DomainType.DOMAIN_TYPE_ENCYCLOPEDIA -> {
                when (streamMode) {
                    StreamMode.NOT_STREAM,
                    StreamMode.START -> {
                        cardInfo.chatMessages?.get(0)?.apply {
                            this.content = ""
                            this.noTaskReasonText = ""
                        }
                        CardDataRepo.add(CardDataWrapper(cardInfo, targetScreen))
                    }
                    StreamMode.ON_GOING,
                    StreamMode.COMPLETION-> {
                        CardDataRepo.appendFrame(cardInfo, targetScreen)
                    }
                }
            }
            else -> {
                CardDataRepo.add(CardDataWrapper(cardInfo, targetScreen))
            }
        }
        return CardDataRepo.fetchCardData(targetScreen)!!
    }

    override fun updateCardData(cardJson: String?) {
        LogUtils.d("updateCardData cardJson:$cardJson")
    }

    override fun dismissCard(screenType: Int) {
        LogUtils.d("dismissCard screenType:$screenType")
        val message = Message.obtain().apply {
            what = MSG_DISMISS_CARD
            obj = screenType
        }
        messageHandler.sendMessage(message)
    }

    private fun handleDismissCardMessage(screenType: Int) {
        LogUtils.d("handleDismissCardMessage screenType:$screenType")
        CardDataRepo.remove(screenType)
        handleCards(screenType)
    }

    private fun handleCards(@ScreenType screenType: Int) {
        LogUtils.d("handleCards on screenType:$screenType")
        val cardData = CardDataRepo.fetchCardData(screenType)
        if (cardData != null) {
            getWindowHolderWithScreenType(screenType, true)?.toShowCard(cardData.cardInfo)
        } else {
            LogUtils.d("handleCards no card data")
            getWindowHolderWithScreenType(screenType)?.collapseCard()
        }
    }

    override fun dismissVoiceView(screenType: Int) {
        LogUtils.d("dismissVoiceView screenType:$screenType")
        val message = Message.obtain().apply {
            what = MSG_DISMISS_VOICE_VIEW
            obj = screenType
        }
        messageHandler.sendMessage(message)
    }

    private fun handleDismissVoiceViewMessage(screenType: Int) {
        LogUtils.d("handleDismissVoiceViewMessage")
        dismissVPATypeWindow(screenType)
    }

    override fun showExecuteFeedbackWindow(feedbackJson: String?) {
        LogUtils.d("showExecuteFeedbackWindow feedbackJson:$feedbackJson")
    }

    override fun updateExecuteFeedbackWindowData(feedbackJson: String?) {
        LogUtils.d("updateExecuteFeedbackWindowData feedbackJson:$feedbackJson")
        val feedbackInfo = getGson().fromJson(feedbackJson, ExecuteFeedbackInfo::class.java)
    }

    override fun dismissExecuteFeedbackWindow(voiceLocation: Int) {
        LogUtils.d("dismissExecuteFeedbackWindow voiceLocation:$voiceLocation")

    }

    override fun scrollCard(direction: Int, screenType: Int) {
        LogUtils.d("scrollCard direction:$direction, screenType:$screenType")
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                scrollCard(direction)
            }
        }
    }

    override fun scrollCardView(direction: Int, screenType: Int): Boolean {
        LogUtils.d("scrollCardView direction:$direction, screenType:$screenType")
        val countDownLatch = CountDownLatch(1)
        var canScroll = false
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                canScroll = scrollCard(direction)
            }

            countDownLatch.countDown()
        }

        try {
            LogUtils.d("scrollCard await ...")
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("scrollCard e:$e")
            canScroll = false
        } finally {

        }

        LogUtils.d("scrollCard canScroll:$canScroll")
        return canScroll
    }

    override fun nextPage(pageOffset: Int, screenType: Int): Boolean {
        LogUtils.d("nextPage pageOffset:$pageOffset, screenType:$screenType")
        val countDownLatch = CountDownLatch(1)
        var canSelect = false
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                canSelect = nextPage(pageOffset)
            }

            countDownLatch.countDown()
        }

        try {
            LogUtils.d("nextPage await ...")
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("nextPage e:$e")
            canSelect = false
        } finally {
        }

        LogUtils.d("nextPage canSelect:$canSelect")
        return canSelect
    }

    override fun getCurrentPage(screenType: Int): PageInfo? {
        LogUtils.d("getCurrentPage screenType:$screenType")
        val countDownLatch = CountDownLatch(1)
        var pageInfo: PageInfo? = null

        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                pageInfo = getCurrentPage()
            }

            countDownLatch.countDown()
        }

        try {
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("getCurrentPage e:$e")
        } finally {
        }

        LogUtils.d("getCurrentPage pageInfo:$pageInfo")
        return pageInfo
    }

    override fun setCurrentItem(itemIndex: Int, screenType: Int): Boolean {
        LogUtils.w("setCurrentItem not implementation yet")
        return false
    }

    override fun setCurrentPage(pageIndex: Int, screenType: Int): Boolean {
        LogUtils.d("setCurrentPage pageIndex:$pageIndex, screenType:$screenType")
        val countDownLatch = CountDownLatch(1)
        var canSelect = false
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                canSelect = setCurrentPage(pageIndex)
            }

            countDownLatch.countDown()
        }

        try {
            LogUtils.d("setCurrentPage await ...")
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("setCurrentPage e:$e")
            canSelect = false
        } finally {
        }

        LogUtils.d("setCurrentPage canSelect:$canSelect")
        return canSelect
    }

    override fun getCurrentCardType(screenType: Int): String? {
        LogUtils.d("getCurrentCardType")
        val countDownLatch = CountDownLatch(1)
        var cardType: String? = null
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                cardType = getCurrentCardType()
            }

            countDownLatch.countDown()
        }

        try {
            LogUtils.d("getCurrentCardType await ...")
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("getCurrentCardType e:$e")
        } finally {
        }

        LogUtils.d("getCurrentCardType cardType:$cardType")
        return cardType
    }

    override fun setVoiceMode(voiceMode: Int) {
        LogUtils.d("setVoiceMode voiceMode:$voiceMode")
        val message = Message.obtain().apply {
            what = MSG_SET_VOICE_MODE
            obj = voiceMode
        }
        messageHandler.sendMessage(message)
    }

    private fun handleSetVoiceModeMessage(voiceMode: Int) {
        LogUtils.d("handleSetVoiceModeMessage voiceMode:$voiceMode")
        this.voiceMode = voiceMode
        mainWindowHolder?.setVoiceServiceMode(voiceMode)
        passengerWindowHolder?.setVoiceServiceMode(voiceMode)
        ceilingWindowHolder?.setVoiceServiceMode(voiceMode)
        voiceWaveWindowHolder?.setVoiceServiceMode(voiceMode)
    }

    override fun setLanguageType(languageType: Int) {
        LogUtils.d("setLanguageType languageType:$languageType")
        val message = Message.obtain().apply {
            what = MSG_SET_LANGUAGE_TYPE
            obj = languageType
        }
        messageHandler.sendMessage(message)
    }

    private fun handleSetLanguageTypeMessage(languageType: Int) {
        LogUtils.d("handleSetLanguageTypeMessage languageType:$languageType")
        this.languageType = languageType
        mainWindowHolder?.setVoiceLanguageType(languageType)
        passengerWindowHolder?.setVoiceLanguageType(languageType)
        ceilingWindowHolder?.setVoiceLanguageType(languageType)
    }

    override fun getCardViewRegisterViewCmdState(displayId: Int, screenType: Int): Int {
        LogUtils.d("getCardViewRegisterViewCmdState displayId:$displayId, screenType:$screenType")
        return APIResult.SUCCESS
    }

    override fun getCardState(screenType: Int): Int {
        LogUtils.d("getCardState screenType:$screenType")
        val countDownLatch = CountDownLatch(1)
        var result = APIResult.INIT
        normalScope?.launch {
            getWindowHolderWithScreenType(screenType)?.run {
                result = getCardState()
            }

            countDownLatch.countDown()
        }

        try {
            LogUtils.d("getCardState await ...")
            countDownLatch.await(400L, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            LogUtils.d("getCardState e:$e")
            result = APIResult.ERROR
        } finally {
        }
        LogUtils.d("getCardState result:$result")

        return result
    }

    override fun showWave(location: Int) {
        LogUtils.d("showWave with location:$location")
        val message = Message.obtain().apply {
            what = MSG_SHOW_WAVE
            obj = location
        }
        messageHandler.sendMessage(message)
    }

    private fun handleShowWaveMessage(location: Int) {
        LogUtils.i("handleShowWaveMessage location:$location")
        AppHelper.locationVoiceFocus = location

        voiceWaveWindowHolder?.let {
            it.setVoiceFocusLocation()
            it.showWave(location)
        } ?: run {
            LogUtils.i("handleShowWaveMessage voiceWaveWindowHolder is not initialized...")
            getVoiceWaveWindowHolder()
            voiceWaveWindowHolder?.showWave(location)
        }

        mainWindowHolder?.setVoiceFocusLocation()
        passengerWindowHolder?.setVoiceFocusLocation()
        ceilingWindowHolder?.setVoiceFocusLocation()
    }

    override fun dismissWave() {
        LogUtils.d("dismissWave")
        val message = Message.obtain().apply {
            what = MSG_DISMISS_WAVE_VIEW
        }
        messageHandler.sendMessage(message)
    }

    private fun handleDismissWaveViewMessage() {
        LogUtils.d("handleDismissWaveViewMessage")
        voiceWaveWindowHolder?.dismissVoiceWaveView()
    }

    override fun showVpa(text: String, textStyle: Int, voiceState: String) {
        LogUtils.d("showVpa text:$text, textStyle:$textStyle, voiceState:$voiceState")
        showVoiceVpa(UIMessage().apply {
            textTypewriter = text
            this.voiceState = voiceState
            this.textStyle = textStyle
            this.screenType = ScreenType.MAIN
        })
    }

    override fun showVoiceVpa(uiMessage: UIMessage?) {
        LogUtils.d("showVoiceVpa uiMessage:$uiMessage")
        uiMessage?.let {
            val message = Message.obtain().apply {
                what = MSG_SHOW_VPA
                obj = it
            }
            messageHandler.sendMessage(message)
        } ?: run {
            LogUtils.w("showVoiceVpa unsupported param...")
        }

    }

    private fun handleShowVPAMessage(uiMessage: UIMessage) {
        LogUtils.d("handleShowVPAMessage, uiMessage:$uiMessage")
        val text = uiMessage.textTypewriter
        val textStyle = uiMessage.textStyle
        val voiceState = uiMessage.voiceState

        val screenType = uiMessage.screenType
        getWindowHolderWithScreenType(screenType, true)?.let {
            if (!it.isVAWindowShow() && !it.isVAWindowShowing()) {
                it.showVPA()
            } else if (it.isVPATypeExitAnimRunning()) {
                it.showVPA()
            }
            when (voiceState) {
                VoiceState.VOICE_STATE_LISTENING -> {
                    setVoiceListening(screenType)
                }

                VoiceState.VOICE_STATE_SPEAKING -> {
                    setVoiceSpeaking(screenType)
                    changeTypeWriterText(text, textStyle, screenType)
                }

                VoiceState.VOICE_STATE_RECOGNIZING -> {
                    changeTypeWriterText(text, textStyle, screenType)
                }
            }
        } ?: run {
            LogUtils.w("handleShowVPAMessage invalid screenType:$screenType")
        }

    }


    private fun dismissVPATypeWindow(screenType: Int) {
        LogUtils.d("dismissVPATypeWindow screenType:$screenType")
        getWindowHolderWithScreenType(screenType)?.dismissVoiceView()
    }

    override fun setCeilingScreenEnable(enable: Boolean) {
        LogUtils.d("setCeilingScreenEnable enable:$enable")
        screenState[2] = if (enable) 1 else 0
        val message = Message.obtain().apply {
            what = MSG_SET_SCREEN_ENABLE
            data = Bundle().apply {
                putInt(KEY_SCREEN_TYPE, 2)
                putBoolean(KEY_SCREEN_ENABLE, enable)
            }
        }
        messageHandler.sendMessage(message)
    }

    private fun cloneVPATypeCardState() {
        mainWindowHolder?.let { mainWindowHolder ->
            passengerWindowHolder?.let { passengerWindowHolder ->
                passengerWindowHolder.voiceState = mainWindowHolder.voiceState
                passengerWindowHolder.currentTextStype = mainWindowHolder.currentTextStype
                val currentTypeText = mainWindowHolder.getCurrentTypeText()
                passengerWindowHolder.showCardWindow(true)
                passengerWindowHolder.inputText(currentTypeText, passengerWindowHolder.currentTextStype)
            }

        }
    }

    override fun setScreenEnable(screenType: Int, enable: Boolean) {
        LogUtils.d("setScreenEnable screenType:$screenType, enable:$enable")
        val message = Message.obtain().apply {
            what = MSG_SET_SCREEN_ENABLE
            data = Bundle().apply {
                putInt(KEY_SCREEN_TYPE, screenType)
                putBoolean(KEY_SCREEN_ENABLE, enable)
            }
        }
        messageHandler.sendMessage(message)
    }

    public fun bindHelper(windowHelper: WindowHelper) {
        this.windowHelper = windowHelper
    }

    override fun call(method: String, bundle: Bundle?): Bundle {
        LogUtils.d("call bundle:$bundle")
        return bundle?.let { windowHelper?.callSync(method, it) }!!
    }

    override fun callAsync(method: String, bundle: Bundle?, callback: ICallback?) {
        LogUtils.d("callAsync bundle: $bundle,  $callback")
        bundle?.let { windowHelper?.callAsync(method, bundle, callback) }
    }

    override fun register(name: String?, callback: ICallback?) {
        LogUtils.d("register name: $name,  $callback")
        name?.let {
            callback?.let { callback ->
                windowHelper?.register(name, callback)
            }
        } ?: run {
            LogUtils.w("register name is null")
        }
    }

    override fun unRegister(name: String?) {
        LogUtils.d("unRegister name: $name")
        name?.let {
            windowHelper?.unregister(name)
        } ?: run {
            LogUtils.w("unRegister name is null")
        }
    }

    private fun handleSetScreenEnableMessage(@ScreenType screenType: Int, enable: Boolean) {
        LogUtils.i("handleSetScreenEnableMessage screenType:$screenType, enable:$enable")
        try {
            screenState[screenType] = if (enable) 1 else 0
        } catch (e: Exception) {
            LogUtils.e("handleSetScreenEnableMessage e:$e")
        }
        //1. 声浪
        voiceWaveWindowHolder?.onScreenStateChange()

        //2. 处理卡片的移位
        if (enable) {
            CardDataRepo.fetchCardData(ScreenType.MAIN)?.let {
                CardDataRepo.rollback(screenType)
                handleCards(screenType)
                handleCards(ScreenType.MAIN)
            }
        } else {
            CardDataRepo.fetchCardData(screenType)?.let {
                //如果这个屏有卡片存在
                //产品要求发生挤占时，直接将卡片移到主屏幕，同时清除主屏幕的卡片（若有）,也要清除副驾屏的卡片
                CardDataRepo.remove(ScreenType.MAIN)
                it.moveTo(ScreenType.MAIN)
                handleCards(ScreenType.MAIN)
                handleCards(screenType)
            }
        }

    }

    private fun getMessageTypeString(msgWhat: Int): String {
        return when (msgWhat) {
            MSG_SHOW_VPA -> {
                "showVPA"
            }

            MSG_VOICE_EXIT -> {
                "voiceExit"
            }

            MSG_DISMISS_VOICE_VIEW -> {
                "dismissVoiceView"
            }

            MSG_SHOW_WAVE -> {
                "showWave"
            }

            MSG_DISMISS_WAVE_VIEW -> {
                "dismissWave"
            }

            MSG_SHOW_CARD -> {
                "showCard"
            }

            MSG_DISMISS_CARD -> {
               "dismissCard"
            }

            MSG_SET_VOICE_MODE -> {
                "setVoiceMode"
            }

            MSG_SET_LANGUAGE_TYPE -> {
                "setLanguageType"
            }

            MSG_SET_CEILING_SCREEN_ENABLE -> {
                "setCeilingScreenEnable"
            }

            MSG_SET_SCREEN_ENABLE -> "setScreenEnable"

            else -> {
                "unSupportedType"
            }
        }
    }


    private fun getWindowHolderWithScreenType(
        screenType: Int, needInit: Boolean = false
    ): BaseVTCWindowHolder? {
        LogUtils.d("getWindowHolderWithScreenType screenType:$screenType ")
        val param = screenType.coerceIn(0..2)
        return when (param) {
            ScreenType.MAIN -> {
                if (mainWindowHolder == null && needInit) {
                    getMainWindowHolder() //初始化
                }
                mainWindowHolder
            }

            ScreenType.PASSENGER -> {
                if (passengerWindowHolder == null && needInit) {
                    getPassengerWindowHolder() //初始化
                }
                passengerWindowHolder
            }

            ScreenType.CEILING -> {
                if (ceilingWindowHolder == null && needInit) {
                    getCeilingWindowHolder() //初始化
                }
                ceilingWindowHolder
            }

            else -> {
                LogUtils.e("illegalParams: screenType:$screenType")
                return null
            }
        }
    }

    private fun getGson(): Gson {
        if (gson == null) gson = Gson()
        return gson!!
    }

    private fun getMainWindowHolder() {
        LogUtils.d("getMainWindowHolder")
        mainWindowHolder = MainScreenWindowHolder(context.applicationContext).apply {
            voiceMode = this@VoyahWindowManager.voiceMode
            languageType = this@VoyahWindowManager.languageType
            setVoiceFocusLocation()
            vtcWindowCallback = object : VTCWindowCallback {

                override fun onVPATypeInit() {
                    LogUtils.d("onVPATypeShowing")
                }

                override fun onVTCDismiss() {
                    LogUtils.d("onVTCDismiss")
                    dismissAllWindow()
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            WindowMessage(
                                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                                WindowAction.WINDOW_ACTION_DISMISS,
                                ScreenType.MAIN
                            ).let { msg ->
                                onReceiveWindowMessage(msg)
                            }
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onCardCollapse(domainType: String?, sessionId: String?) {
                    LogUtils.d("onCardCollapse")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.action = WindowAction.WINDOW_ACTION_COLLAPSE_CARD
                                        cardInfo.screenType = ScreenType.MAIN
                                    }
                                    onReceiveVoyahWindowMessage(getGson().toJson(cardInfo))
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onDomainItemClick(position: Int, viewType: Int, action: String?) {
                    normalScope?.launch(Dispatchers.Default) {
                        LogUtils.d("onDomainItemClick position:$position, viewType:$viewType")
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.position = position
                                        cardInfo.action = WindowAction.WINDOW_ACTION_ITEM_CLICK
                                        cardInfo.itemType = viewType
                                        cardInfo.screenType = ScreenType.MAIN
                                        when (viewType) {
                                            ViewType.SCHEDULE_TYPE_MORE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_SCHEDULE
                                            }

                                            ViewType.BT_PHONE_TYPE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_BT_PHONE
                                                cardInfo.action = action
                                            }

                                            ViewType.MEDIA_TYPE -> {
                                                cardInfo.domainType =
                                                    DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO
                                            }

                                            ViewType.MUSIC_TYPE -> {
                                                cardInfo.domainType =
                                                    DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC
                                            }

                                        }
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onDomainItemClick jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onReceiveVoyahWindowMessage callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
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

                override fun interruptStreamInput(domainType: String) {
                    LogUtils.d("interruptStreamInput")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    interruptStreamInput(domainType)
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onInteractionInCard(domainType: String?, sessionId: String?) {
                    LogUtils.d("onInteractionInCard domainType:$domainType")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.action = WindowAction.WINDOW_ACTION_INTERACTION_IN_CARD
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.screenType = ScreenType.MAIN
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onInteractionInCard jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onInteractionInCard callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }
            }

            LogUtils.d("getMainWindowHolder app version name is:${AppUtils.getAppVersionName()}")
        }
    }

    private fun getPassengerWindowHolder() {
        LogUtils.d("getPassengerWindowHolder")
        passengerWindowHolder = PassengerScreenWindowHolder(context.applicationContext).apply {
            voiceMode = this@VoyahWindowManager.voiceMode
            languageType = this@VoyahWindowManager.languageType
            setVoiceFocusLocation()
            vtcWindowCallback = object : VTCWindowCallback {

                override fun onVPATypeInit() {
                    LogUtils.d("onVPATypeShowing")
                }

                override fun onVTCDismiss() {
                    LogUtils.d("onVTCDismiss")
                    dismissAllWindow()
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            WindowMessage(
                                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                                WindowAction.WINDOW_ACTION_DISMISS,
                                ScreenType.PASSENGER
                            ).let { msg ->
                                onReceiveWindowMessage(msg)
                            }
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onCardCollapse(domainType: String?, sessionId: String?) {
                    LogUtils.d("onCardCollapse")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.action = WindowAction.WINDOW_ACTION_COLLAPSE_CARD
                                        cardInfo.screenType = ScreenType.PASSENGER
                                    }
                                    onReceiveVoyahWindowMessage(getGson().toJson(cardInfo))
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onDomainItemClick(position: Int, viewType: Int, action: String?) {
                    normalScope?.launch(Dispatchers.Default) {
                        LogUtils.d("onDomainItemClick position:$position, viewType:$viewType")
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.position = position
                                        cardInfo.action = WindowAction.WINDOW_ACTION_ITEM_CLICK
                                        cardInfo.itemType = viewType
                                        cardInfo.screenType = ScreenType.PASSENGER
                                        when (viewType) {
                                            ViewType.SCHEDULE_TYPE_MORE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_SCHEDULE
                                            }

                                            ViewType.BT_PHONE_TYPE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_BT_PHONE
                                                cardInfo.action = action
                                            }

                                            ViewType.MEDIA_TYPE -> {
                                                cardInfo.domainType =
                                                    DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO
                                            }

                                        }
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onDomainItemClick jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onReceiveVoyahWindowMessage callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
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

                override fun interruptStreamInput(domainType: String) {
                    LogUtils.d("interruptStreamInput")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    interruptStreamInput(domainType)
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onInteractionInCard(domainType: String?, sessionId: String?) {
                    LogUtils.d("onInteractionInCard domainType:$domainType")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.action = WindowAction.WINDOW_ACTION_INTERACTION_IN_CARD
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.screenType = ScreenType.PASSENGER
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onInteractionInCard jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onInteractionInCard callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }
            }

            LogUtils.d("getPassengerWindowHolder app version name is:${AppUtils.getAppVersionName()}")
        }
    }

    private fun getCeilingWindowHolder() {
        LogUtils.d("getCeilingWindowHolder")
        ceilingWindowHolder = CeilingScreenWindowHolder(context.applicationContext).apply {
            voiceMode = this@VoyahWindowManager.voiceMode
            languageType = this@VoyahWindowManager.languageType
            setVoiceFocusLocation()
            vtcWindowCallback = object : VTCWindowCallback {

                override fun onVPATypeInit() {
                    LogUtils.d("onVPATypeShowing")
                }

                override fun onVTCDismiss() {
                    LogUtils.d("onVTCDismiss")
                    dismissAllWindow()
                    val callbacks = receiveCallbacks.beginBroadcast()
                    for (i in 0 until callbacks) {
                        receiveCallbacks.getBroadcastItem(i)?.apply {
                            WindowMessage(
                                WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
                                WindowAction.WINDOW_ACTION_DISMISS,
                                ScreenType.CEILING
                            ).let { msg ->
                                onReceiveWindowMessage(msg)
                            }
                        }
                    }
                    receiveCallbacks.finishBroadcast()
                }

                override fun onCardCollapse(domainType: String?, sessionId: String?) {
                    LogUtils.d("onCardCollapse")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.action = WindowAction.WINDOW_ACTION_COLLAPSE_CARD
                                        cardInfo.screenType = ScreenType.CEILING
                                    }
                                    onReceiveVoyahWindowMessage(getGson().toJson(cardInfo))
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onDomainItemClick(position: Int, viewType: Int, action: String?) {
                    normalScope?.launch(Dispatchers.Default) {
                        LogUtils.d("onDomainItemClick position:$position, viewType:$viewType")
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.position = position
                                        cardInfo.action = WindowAction.WINDOW_ACTION_ITEM_CLICK
                                        cardInfo.itemType = viewType
                                        cardInfo.screenType = ScreenType.CEILING
                                        when (viewType) {
                                            ViewType.SCHEDULE_TYPE_MORE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_SCHEDULE
                                            }

                                            ViewType.BT_PHONE_TYPE -> {
                                                cardInfo.domainType = DomainType.DOMAIN_TYPE_BT_PHONE
                                                cardInfo.action = action
                                            }

                                            ViewType.MEDIA_TYPE -> {
                                                cardInfo.domainType =
                                                    DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO
                                            }

                                        }
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onDomainItemClick jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onReceiveVoyahWindowMessage callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
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

                override fun interruptStreamInput(domainType: String) {
                    LogUtils.d("interruptStreamInput")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    interruptStreamInput(domainType)
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }

                override fun onInteractionInCard(domainType: String?, sessionId: String?) {
                    LogUtils.d("onInteractionInCard domainType:$domainType")
                    normalScope?.launch(Dispatchers.Default) {
                        synchronized(receiveCallbacks) {
                            val callbacks = receiveCallbacks.beginBroadcast()
                            for (i in 0 until callbacks) {
                                receiveCallbacks.getBroadcastItem(i)?.apply {
                                    val cardInfo = CardInfo().also { cardInfo ->
                                        cardInfo.action = WindowAction.WINDOW_ACTION_INTERACTION_IN_CARD
                                        cardInfo.domainType = domainType
                                        cardInfo.sessionId = sessionId
                                        cardInfo.screenType = ScreenType.CEILING
                                    }
                                    val jsonMsg = getGson().toJson(cardInfo)
                                    LogUtils.d("onInteractionInCard jsonMsg:$jsonMsg")
                                    try {
                                        onReceiveVoyahWindowMessage(jsonMsg)
                                    } catch (e: Exception) {
                                        LogUtils.w("onInteractionInCard callback error:$e")
                                    }
                                }
                            }
                            receiveCallbacks.finishBroadcast()
                        }
                    }
                }
            }

            LogUtils.d("getCeilingWindowHolder app version name is:${AppUtils.getAppVersionName()}")
        }
    }

    private fun getVoiceWaveWindowHolder() {
//        SystemConfigUtil.setDefaultNightMode(context)
        voiceWaveWindowHolder = VoiceWaveWindowHolder(context.applicationContext).apply {
            screenStateProvider = this@VoyahWindowManager
        }
    }

    private fun dismissAllWindow() {
        LogUtils.d("dismissAllWindow")
        mainWindowHolder?.onVoiceExit()
        mainWindowHolder = null
        passengerWindowHolder?.onVoiceExit()
        passengerWindowHolder = null
        ceilingWindowHolder?.onVoiceExit()
        ceilingWindowHolder = null
        voiceWaveWindowHolder?.onVoiceExit()
        voiceWaveWindowHolder = null
    }


    fun onUIModeChange(nightModeFlag: Boolean) {
        LogUtils.d("onUIModeChange nightModeFlag:$nightModeFlag")
        mainWindowHolder?.onUIModeChange(nightModeFlag)
        passengerWindowHolder?.onUIModeChange(nightModeFlag)
        ceilingWindowHolder?.onUIModeChange(nightModeFlag)
        voiceWaveWindowHolder?.onUIModeChange(nightModeFlag)
    }

    @MainThread
    fun onDestroy() {
        LogUtils.d("onDestroy")
        dismissAllWindow()
        normalScope.cancel()
    }

    override fun getScreenState(@ScreenType screen: Int): Int {
        return screenState[screen]
    }

    override fun getVoiceMode(): Int {
        return voiceMode
    }

    override fun getLanguageType(): Int {
        return languageType
    }
}