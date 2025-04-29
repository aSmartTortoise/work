package com.voice.sdk.device.ui

import android.util.Log
import com.voice.sdk.VoiceImpl
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.ui.UIMgr.applyNewStateOnScreen
import com.voice.sdk.device.ui.UIMgr.dismissCard
import com.voice.sdk.device.ui.UIScreen.Companion.blankBusiness
import com.voice.sdk.device.ui.UIScreen.Companion.blankCard
import com.voice.sdk.device.ui.listener.IUiStateListener
import com.voice.sdk.device.ui.listener.UICardListener
import com.voice.sdk.util.ThreadPoolUtils
import com.voyah.ai.common.utils.LogUtils

/**
 * @Date 2024/11/14 10:18
 * @Author 8327821
 * @Email *
 * @Description VPA状态管理
 **/

/**
 * UI能力提供者,提供UI能力 (VOICE UI工程)
 */
interface UiAbilityInterface {

    fun onChangeCardOwner(cardType: Int, cardData: Any, business: String, location: Int, callback: Runnable)

    fun showVoiceVpa(text: String, textStyle: Int, voiceState: String, location: Int)

    fun dismissVoiceView(location: Int)

    fun showWave(location: Int)

    fun dismissWave()

    fun showCard(cardType: Int, cardData: Any, paramLocation: Int)

    fun dismissCard(location: Int)

    fun onVoiceExit()

    fun setLanguageType(type: String)

    fun setUiStateListener(listener: IUiStateListener?)

    fun addCardStateListener(listener: UICardListener?) //卡片、vpa点击事件，控制语音交互生命周期

    fun removeCardStateListener(listener: UICardListener?) //卡片内项目点击事件，响应卡片交互事件

}



/**
 * UI状态定义，步长<<2，防止后续新增状态
 */
enum class UIState(val value: Int) {
    STATE_ASR(32),
    STATE_CMD(16),
    STATE_ACTION(8),
    STATE_LISTENING(2)
}

/**
 * UI 元素定义，包括业务方、文本、位置、是否可打断
 */
data class UIBusiness(
    var session: String, //sessionId
    var business: String, //reqid
    var vpaText: String, //文本
    var location: Int, //需要上屏的位置
    var interruptible: Boolean = false)

data class CardBusiness(
    var session: String, //sessionId
    var business: String, //reqid
    var location: Int, //需要上屏的位置
    var interruptible: Boolean = false) //是否可打断的业务

/**
 * UI 依赖的屏幕抽象
 */
class UIScreen(val location: Int = 0) {

    companion object {
        val blankBusiness = UIBusiness("", "", "", 0)
        val blankCard = CardBusiness("", "", 0)
    }
    var ASR_BUSINESS: UIBusiness = blankBusiness //ASR状态的业务方和要展示文本的映射
    var CMD_BUSINESS: UIBusiness = blankBusiness //可见的执行态，需要UI展示，但是不期望打断语音的agent
    var ACT_BUSINESS: UIBusiness = blankBusiness
    var LISTEN_BUSINESS: UIBusiness = blankBusiness
    var enabled = true
    var CARD_OWNER : CardBusiness = blankCard //卡片的业务方和要展示文本的映射
        set(value) {
            Log.d(UIMgr.TAG, "[screen : ${this.location}] LAST_CARD_OWNER updated from $field to $value")
            field = value
        }

    fun isEmpty() : Boolean {
        return ASR_BUSINESS == blankBusiness && CMD_BUSINESS == blankBusiness && ACT_BUSINESS == blankBusiness && LISTEN_BUSINESS == blankBusiness
    }

    fun onAwake(newLoc: Int) {
        LogUtils.d(UIMgr.TAG, "onAwake on screen [$location], newLoc = $newLoc")
        if (newLoc != location) {
            exitVpa()
        } else {
            LogUtils.d(UIMgr.TAG, "re awake on screen [$location]")
            if (!isEmpty()) {
                ASR_BUSINESS = blankBusiness
                CMD_BUSINESS = blankBusiness
                ACT_BUSINESS = blankBusiness
                applyNewStateOnScreen(this.location)
            }
        }
        if (CARD_OWNER.interruptible) { //多轮的卡片，收起
            dismissCard(CARD_OWNER.business)
        }
    }

    fun exitVpa() {
        //处理VPA
        if (!isEmpty()) {
            ASR_BUSINESS = blankBusiness
            CMD_BUSINESS = blankBusiness
            ACT_BUSINESS = blankBusiness
            LISTEN_BUSINESS = blankBusiness
            applyNewStateOnScreen(this.location)
        }
    }

    fun exitCard() {
        //处理卡片
        CARD_OWNER.business.takeIf { it.isNotEmpty() }?.let {
            Log.d(UIMgr.TAG, "[screen : ${this.location}] dismissCard $it")
            dismissCard(it)
        }
    }

    fun forceExit() {
        Log.d(UIMgr.TAG, "[screen : ${this.location}] forceExit")
        exitVpa()
        exitCard()
    }
}

class ExitRun(val token: String) : Runnable {
    override fun run() {
        UIMgr.removeExitRun(this) //执行完从列表中移除

        Log.d(UIMgr.TAG, "exitAgentAct : $token")
        UIMgr.exitState(UIState.STATE_ASR, token)
        UIMgr.exitState(UIState.STATE_CMD, token)
        UIMgr.exitState(UIState.STATE_ACTION, token)
        dismissCard(token)
    }
}

object UIMgr {
    const val TAG = "UIState"
    private val IUiAbilityProvider = DeviceHolder.INS().devices.ui
    private val carProp = DeviceHolder.INS().devices.carServiceProp

    private val mainScreen = UIScreen() //中控
    private val secondaryScreen = UIScreen(1) //副屏
    private val thirdScreen = UIScreen(2) //吸顶屏
    private val  screenMap= mutableMapOf(0 to mainScreen, 1 to secondaryScreen, 2 to thirdScreen)

    private val timeoutRunSet = mutableSetOf<TimeoutRun>() //超时任务列表
    private val exitRunSet = mutableSetOf<ExitRun>() //退出的任务列表
    val executor = ThreadPoolUtils.getSinglePool("UIMgr")

    init {

        if (!DeviceHolder.INS().devices.carServiceProp.getCarType().contains("56")) {
            secondaryScreen.enabled = false
        }

        IUiAbilityProvider.setUiStateListener(object : IUiStateListener {
            override fun uiCardClose(sessionId: String?) {
                Log.i(TAG, "uiCardClose")
                DeviceHolder.INS().getDevices().getTts().shutUpOneSelf()
                VoiceImpl.getInstance().exitSessionDialog(sessionId)
            }

            override fun uiModelCardClose(requestId: String?) {
                DeviceHolder.INS().devices.getTts().shutUpOneSelf()
                VoiceImpl.getInstance().exitRequest(requestId)
            }

            override fun uiVpaClose() {
                Log.i(TAG, "uiVpaClose")
                DeviceHolder.INS().devices.getTts().shutUpOneSelf()
                VoiceImpl.getInstance().exDialog()
            }
        })
    }


    fun onScreenStateChange(screen: Int, enabled: Boolean) {
        Log.d(TAG, "onScreenStateChange screen = $screen; enabled = $enabled")
        if (screen == 1) {
            secondaryScreen.enabled = enabled
            if (!secondaryScreen.isEmpty() && !enabled) {
                IUiAbilityProvider.dismissVoiceView(1)
            }
        } else if (screen == 2) {
            thirdScreen.enabled = enabled
            if (!thirdScreen.isEmpty() && !enabled) {
                IUiAbilityProvider.dismissVoiceView(2)
                //IUiAbilityProvider.dismissCard(2)
            }
            /*if (!thirdScreen.isEmpty() && enabled) {
                IUiAbilityProvider.dismissVoiceView(0)
                IUiAbilityProvider.dismissCard(0)
            }*/
        }
        applyNewState() //屏幕展开，刷新各屏幕状态
    }

    private fun getScreenByLocation(paramLocation: Int) : UIScreen {
        val location = paramLocation.coerceIn(0..2)
        return screenMap[location] ?: mainScreen
    }

    /**
     * @param paramLocation Int 唤醒位置，声浪显示位置
     * @param interrupt Boolean 是否打断当前状态，true 打断，false 不打断
     */
    fun wakeUp(paramLocation: Int, interrupt: Boolean) {
        LogUtils.d(TAG, "wakeUp paramLocation = $paramLocation, interrupt = $interrupt")
        val location = paramLocation.coerceIn(0..5)
        if (interrupt) {
            if (carProp.isVCOS15()) {
                screenMap.values.forEach{
                    it.onAwake(location)
                }
            } else {
                //唤醒抢占优先级最高，当有流程触发唤醒时，强制打断其他状态，进入唤醒态
                screenMap.values.forEach{
                    it.forceExit()
                }
            }
        }
        executor.execute {
            showWave(location, "wake up")
        }
        //自然唤醒场景，不抢占清空其他状态
        enterState(UIState.STATE_LISTENING, "", "", "wakeUp", location)
    }


    /**
     * 一个业务只能有一个VPA状态，为了兜底业务方未调用对应exit，当一个业务有新状态进来的时候，自动清楚之前的状态
     * @param business String 业务方标识
     */
    fun enterState(state: UIState, vpaText: String, sessionId: String, business: String, paramLocation: Int) {
        val location = paramLocation.coerceIn(0..2)
        Log.d(TAG, "enterState state=${state.name}, vpaText = $vpaText, business = $business, location = $location")

        val targetScreen = getScreenByLocation(location) //找一个屏幕承载业务，两者location不一定一致
        when (state) {
            //抢占式，只能有一个屏有ASR
            UIState.STATE_ASR -> {
                screenMap.forEach { (index, screen) ->
                    if (targetScreen == screen) {
                        screen.ASR_BUSINESS = UIBusiness(sessionId, business, vpaText, location)
                    } else {
                        screen.ASR_BUSINESS = blankBusiness
                    }
                }
            }
            //共存式，多个屏可以有多个CMD
            UIState.STATE_CMD -> {
                targetScreen.CMD_BUSINESS = UIBusiness(sessionId, business, vpaText, location)
            }
            //共存式，多个屏可以有多个ACT
            UIState.STATE_ACTION -> {
                targetScreen.ACT_BUSINESS = UIBusiness(sessionId, business, vpaText, location)
            }
            //抢占式，只能有一个屏有聆听
            UIState.STATE_LISTENING -> {
                screenMap.forEach { (index, screen) ->
                    if (targetScreen == screen) {
                        screen.LISTEN_BUSINESS = UIBusiness(sessionId, business, vpaText, location)
                    } else {
                        screen.LISTEN_BUSINESS = blankBusiness
                    }
                }
            }
        }
        applyNewStateOnScreen(targetScreen.location)
    }

    fun exitState(state: UIState, business: String) {
        Log.d(TAG, "exitState state=${state.name}, business = $business")
        when (state) {
            UIState.STATE_ASR -> {
                screenMap.values.forEach {
                    if (it.ASR_BUSINESS.business.isBlank()) {
                        return@forEach
                    }
                    if (business.contains(it.ASR_BUSINESS.business, true)) {
                        it.ASR_BUSINESS = blankBusiness
                        applyNewStateOnScreen(it.location)
                    }
                }
            }
            UIState.STATE_CMD -> {
                screenMap.values.forEach {
                    if (it.CMD_BUSINESS.business.isBlank()) {
                        return@forEach
                    }
                    if (it.CMD_BUSINESS.business == business) {
                        it.CMD_BUSINESS = blankBusiness
                        applyNewStateOnScreen(it.location)
                    }
                }
            }
            UIState.STATE_ACTION -> {
                screenMap.values.forEach {
                    if (it.ACT_BUSINESS.business.isBlank()) {
                        return@forEach
                    }
                    if (it.ACT_BUSINESS.business == business) {
                        it.ACT_BUSINESS = blankBusiness
                        applyNewStateOnScreen(it.location)
                    }
                }
            }
            UIState.STATE_LISTENING -> {
                screenMap.values.forEach {
                    if (it.LISTEN_BUSINESS.business.isBlank()) {
                        return@forEach
                    }

                    it.LISTEN_BUSINESS = blankBusiness
                    applyNewStateOnScreen(it.location)
                }
            }
        }
    }

    //UI状态决策
    private fun applyNewState() {
        screenMap.values.forEach {
            applyNewStateOnScreen(it.location)
        }
    }

    fun applyNewStateOnScreen(paramLocation: Int = 0) {
        val location = paramLocation.coerceIn(0..2)
        val screen = getScreenByLocation(location)
        LogUtils.d(TAG, "applyNewStateOnScreen location = $location, enabled = ${screen.enabled}")
        if (screen.ASR_BUSINESS.business.isNotBlank()) {
            //仍然有ASR上屏的业务，
            Log.d(TAG, "ASR: [business = ${screen.ASR_BUSINESS.business}, vpaText = ${screen.ASR_BUSINESS.vpaText}, location = ${screen.ASR_BUSINESS.location}]")
            val asr = screen.ASR_BUSINESS.copy() //防止线程池任务执行前内容被修改
            executor.execute {
                IUiAbilityProvider.showVoiceVpa(
                    asr.vpaText,
                    TypeTextStyle.SECONDARY,
                    VoiceState.VOICE_STATE_RECOGNIZING,
                    if (screen.enabled) screen.location else 0
                )
            }
        } else if (screen.CMD_BUSINESS.business.isNotBlank()) {
            //仍然有执行态的业务，取队首元素FIFO
            Log.d(TAG, "CMD: [business = ${screen.CMD_BUSINESS.business}, vpaText = ${screen.CMD_BUSINESS.vpaText}, location = ${screen.CMD_BUSINESS.location}]")
            val cmd = screen.CMD_BUSINESS.copy() //防止线程池任务执行前内容被修改
            executor.execute {
                IUiAbilityProvider.showVoiceVpa(
                    cmd.vpaText,
                    TypeTextStyle.PRIMARY,
                    VoiceState.VOICE_STATE_SPEAKING,
                    if (screen.enabled) screen.location else 0
                )
            }
        } else if (screen.ACT_BUSINESS.business.isNotBlank()){
            //仍然有执行态的业务，取队首元素FIFO
            Log.d(TAG, "ACT: [business = ${screen.ACT_BUSINESS.business}, vpaText = ${screen.ACT_BUSINESS.vpaText}, location = ${screen.ACT_BUSINESS.location}]")
            val act = screen.ACT_BUSINESS.copy() //防止线程池任务执行前内容被修改
            executor.execute {
                IUiAbilityProvider.showVoiceVpa(
                    act.vpaText,
                    TypeTextStyle.PRIMARY,
                    VoiceState.VOICE_STATE_SPEAKING,
                    if (screen.enabled) screen.location else 0
                )
            }
        } else if (screen.LISTEN_BUSINESS.business.isNotBlank()){
            //降级到聆听态
            Log.d(TAG, "LISTEN: [business = ${screen.LISTEN_BUSINESS.business}, vpaText = ${screen.LISTEN_BUSINESS.vpaText}, location = ${screen.LISTEN_BUSINESS.location}]")
            //如果聆听态也有文本需要显示的话
            val listen = screen.LISTEN_BUSINESS.copy() //防止线程池任务执行前内容被修改
            executor.execute {
                IUiAbilityProvider.showVoiceVpa(
                    "",
                    TypeTextStyle.AUXILIARY,
                    VoiceState.VOICE_STATE_LISTENING,
                    if (screen.enabled) screen.location else 0
                )
            }
        } else {
            //没有业务要处理，退出
            Log.d(TAG, "no more business on voice area ${screen.location}, may call dismissVoiceView")
            executor.execute {
                if (screen == mainScreen) {
                    if ((!secondaryScreen.isEmpty() && !secondaryScreen.enabled)) {
                        //刷新
                        applyNewStateOnScreen(1)
                    } else if ((!thirdScreen.isEmpty() && !thirdScreen.enabled)) {
                        applyNewStateOnScreen(2)
                    } else {
                        IUiAbilityProvider.dismissVoiceView(0)
                    }
                }else if (screen == secondaryScreen) {
                    if (screen.enabled) {
                        IUiAbilityProvider.dismissVoiceView(screen.location)
                    } else {
                        if (mainScreen.isEmpty()) {
                            if (thirdScreen.enabled || thirdScreen.isEmpty()) {
                                IUiAbilityProvider.dismissVoiceView(0)
                            }
                        } else {
                            //0423 37车型暴露的问题，主驾唤醒，副驾“打开车窗”，执行完退出时，
                            // 由于主驾有聆听态，所以没掉接口，导致“打开车窗”不会清屏
                            // 这里强行调一下刷成聆听态，吸顶屏同理
                            applyNewStateOnScreen(0)
                        }
                    }
                } else if (screen == thirdScreen) {
                    if (screen.enabled) {
                        //如果吸顶屏启用，清除吸顶屏状态
                        IUiAbilityProvider.dismissVoiceView(screen.location)
                    } else {
                        //如果吸顶屏未启用，且中控屏没有其他业务，才清除中控屏状态
                        if (mainScreen.isEmpty()) {
                            if (secondaryScreen.enabled || secondaryScreen.isEmpty()) {
                                IUiAbilityProvider.dismissVoiceView(0)
                            }
                        } else {
                            applyNewStateOnScreen(0)
                        }
                    }
                }
            }
        }
    }


    fun showWave(paramLocation: Int, business: String) {
        val location = paramLocation.coerceIn(0..5)
        IUiAbilityProvider.showWave(location)
    }

    fun closeWave(business: String) {
        IUiAbilityProvider.dismissWave()
    }

    fun addCardStateListener(listener: UICardListener) {
        IUiAbilityProvider.addCardStateListener(listener)
    }

    fun removeCardStateListener(listener: UICardListener) {
        IUiAbilityProvider.removeCardStateListener(listener)
    }

    fun showCard(cardType: Int, cardData: Any, sessionId: String, business: String, paramLocation: Int) {
        val location = paramLocation.coerceIn(0..2)

        val screenLocation = if (screenMap[location]!!.enabled) {
            location
        } else {
            0
        }
        LogUtils.d(TAG, "showCard [$business] with [soundLoc: $paramLocation] [screenLoc: $screenLocation]")
        tryChangeCardOwner(cardType, cardData, sessionId, business, location)
        executor.execute {
            IUiAbilityProvider.showCard(cardType, cardData, screenLocation)
        }
    }

    fun dismissCard(business: String) {
        Log.d(TAG, "dismissCard: with business $business" )

        if (business.isBlank()) {
            return
        }

        screenMap.values.forEach {
            if (it.CARD_OWNER.business.isBlank()) {
                return@forEach //避免无效接口调用
            }
            Log.d(TAG, "dismissCard on screen [${it.location}], card owner = [${it.CARD_OWNER}], business = [$business]")
            if (it.CARD_OWNER.business.contains(business, true)) { //防止先展示卡片的业务，关闭后续业务的卡片
                it.CARD_OWNER = blankCard
                executor.execute {
                    IUiAbilityProvider.dismissCard(if (it.enabled) it.location else 0)
                }
            }
        }
    }

    fun dismissCardBySession(sessionId: String) {
        Log.d(TAG, "dismissCardBySession: with session $sessionId" )
        screenMap.values.forEach {
            if (it.CARD_OWNER.session.isBlank()) {
                return@forEach //避免无效接口调用
            }
            if (it.CARD_OWNER.session.equals(sessionId, true)) {
                it.CARD_OWNER = blankCard
                dismissCard(it.CARD_OWNER.business)
            }
        }
    }

    /**
     * 卡片内发生交互，需要调整10s后隐藏卡片 大模型20s
     */
    fun onInteractionInCard(reqId : String, isLLM: Boolean = false) {
        Log.d(TAG, "onInteractionInCard reqId = $reqId")
        val exitRun = findExitRun(reqId) ?: return //没有找到对应的任务，说明这张卡片已经被关闭了
        DeviceHolder.INS().devices.threadDelay.removeDelayRunnable(exitRun)
        val delayTime : Long = if (isLLM && carProp.isVCOS15()) 20 * 1000 else 10000 //10s后隐藏卡片 大模型20s
        DeviceHolder.INS().devices.threadDelay.addDelayRunnable(exitRun, delayTime)
    }

    fun onMultiInteraction(location: Int) {
        Log.d(TAG, "onMultiInteraction on screen [$location]")
        val screen = getScreenByLocation(location)
        screenMap.values.forEach {
            if (it != screen) {
                it.forceExit()
            }
        }
    }

    //对应屏幕是否有UI元素展示
    fun hasVoiceInterAction(location: Int): Boolean {
        LogUtils.d(TAG, "hasVoiceInterAction on screen [$location]")
        val screen = getScreenByLocation(location)
        return !screen.isEmpty()
    }


    /**
     * FIXME
     * 强制关掉指定屏幕上的卡片，谨慎使用
     */
    fun dismissCardOnScreen(location: Int) {
        Log.d(TAG, "dismissCardOnScreen on screen paramLocation: [$location]")
        val screen = getScreenByLocation(location)

        val actLocation = if (screen.enabled) {
            screen.location
        } else {
            0
        }
        screen.CARD_OWNER = blankCard
        executor.execute {
            IUiAbilityProvider.dismissCard(actLocation)
        }
    }

    /**
     * FIXME 先保留这个强制性接口，后续排查调用方是否合理
     * @param business String
     */
    fun forceExitAll(business: String) {
        Log.d(TAG, "call forceExitAll for reason $business")
        screenMap.values.forEach {
            it.forceExit()
        }
        executor.execute {
            IUiAbilityProvider.onVoiceExit()
        }
    }

    /**
     * 强制退出执行态，多轮拒识使用
     */
    fun forceExitAct(reason: String, paramLocation: Int = 0) {
        val location = paramLocation.coerceIn(0..2)
        Log.d(TAG, "call forceExitAct on screen : [$location], for reason $reason")
        screenMap[location]?.apply {
            this.ASR_BUSINESS = blankBusiness
            this.CMD_BUSINESS = blankBusiness
            this.ACT_BUSINESS = blankBusiness
            this.CARD_OWNER = blankCard
            applyNewStateOnScreen(this.location)
        }

        executor.execute {
            IUiAbilityProvider.dismissCard(location)
        }
    }

    /**
     * 退出指定session的执行态
     */
    fun forceExitSessionAct(sessionId: String) {
        screenMap.values.find {
            it.ACT_BUSINESS.session == sessionId
        }?.let {
            it.ACT_BUSINESS = blankBusiness
            applyNewStateOnScreen(it.location)
            if (it.CARD_OWNER.session == sessionId) {
                dismissCard(it.CARD_OWNER.business)
            }
        }
    }

    /**
     * 语音idle状态，退出聆听，退出ASR，退出可打断业务
     */
    fun exitWeakBusiness() {
        Log.d(TAG, "exitWeakBusiness")
        closeWave("idle")
        screenMap.values.forEach {
            if (it.isEmpty()) {
                return@forEach //避免无效接口调用
            }

            it.ASR_BUSINESS = blankBusiness
            it.LISTEN_BUSINESS = blankBusiness
            if (it.ACT_BUSINESS.interruptible) {
                //保存token
                val session = it.ACT_BUSINESS.session
                val cardSession = it.CARD_OWNER
                it.ACT_BUSINESS = blankBusiness

                applyNewStateOnScreen(it.location) //先dismissVoiceView 再 dismissCard

                //如果可打断业务拥有卡片
                Log.d(TAG, "exitWeakBusiness dismissCard for $session and card session is $cardSession")
                if (session.equals(cardSession.session, true)) {
                    dismissCard(cardSession.business)
                }
            } else {
                applyNewStateOnScreen(it.location)
            }
        }
    }


    /**
     * agent执行完后，根据场景值判断如果是多轮或者流式等依赖后续agent来打断的场景
     * 如果在agent打断之前，语音退到idle状态，会导致状态退不掉
     * @param business String
     * @param interruptible Boolean
     */
    fun tryUpdateInterruptible(session: String = "", business: String, interruptible: Boolean = true, location: Int = 0) {
        getScreenByLocation(location).apply {
            LogUtils.d(TAG, "tryUpdateInterruptible on screen [$location], session = $session, business = $business, interruptible = $interruptible")
            if (ACT_BUSINESS.business.equals(business, true)) {
                ACT_BUSINESS = ACT_BUSINESS.copy(interruptible = interruptible)
            }
            if (CARD_OWNER.session.equals(session, true)) {
                CARD_OWNER = CARD_OWNER.copy(interruptible = interruptible)
            }
        }
    }

    /**
     * 目前只有llm#search有流式 + 卡片的情况，为了避免DS兜不住llm打断后继续下发的情况，这个场景下只针对首帧修改卡片owner
     * @param cardType Int
     * @param cardData Any
     * @param business String
     * @return 大模型卡片的首帧和其他卡片允许更新
     */
    private fun tryChangeCardOwner(cardType: Int, cardData: Any, sessionId: String, business: String, location: Int) {
        val targetScreen = getScreenByLocation(location)

        IUiAbilityProvider.onChangeCardOwner(cardType, cardData, business, location) {
            targetScreen.CARD_OWNER = CardBusiness(sessionId, business, location, false)
        }
    }

    fun obtainToken(reqId: String, queryId: String = ""): String {
        return if (queryId.isBlank()) reqId else "$reqId-$queryId"
    }

    fun obtainTimeoutRun(token: String, reqId: String, ttsId: String?): Runnable {
        return timeoutRunSet.find { run ->
            run.token == token
        } ?: TimeoutRun().also {
            it.token = token
            it.reqId = reqId //给轩明停DS
            it.ttsId = ttsId //给成远停TTS
            timeoutRunSet.add(it)
        }
    }

    fun clearTimeoutRun(token: String) {
        timeoutRunSet.removeIf { run ->
            run.token == token
        }
    }

    /**
     * 从缓存找延时任务，用于卡片发生交互，需要重置显示时间
     */
    fun findExitRun(reqId: String): ExitRun? {
        return exitRunSet.find { run ->
            run.token.startsWith(reqId)
        }
    }

    /**
     * 优先从缓存取，没有就创建一个，用于agent执行完退出时，清理UI状态
     */
    fun obtainExitRun(token: String): ExitRun {
        return exitRunSet.find { run ->
            run.token.startsWith(token) //业务展示卡片用的是reqID-queryId, UI 那边只维护了reqId, 所以不能判断相等
        }?: ExitRun(token).also {
            exitRunSet.add(it)
        }
    }

    fun removeExitRun(run: Runnable) {
        exitRunSet.remove(run)
    }

    class TimeoutRun: Runnable {
        var token = ""
        var reqId = ""
        var ttsId : String? = null
        override fun run() {
            Log.d(TAG, "timeout trigger for business $token and runnable is $this and ttsId is $ttsId")
            clearTimeoutRun(token)
            exitState(UIState.STATE_ACTION, token)
            dismissCard(token)
            //通知DS退出
            reqId.takeIf {
                it.isNotBlank()
            }.let {
                VoiceImpl.getInstance().exitRequest(it)
            }
            //通知TTS退出
            ttsId?.let {
                DeviceHolder.INS().devices.tts.stopById(it)
            } ?: run {
                DeviceHolder.INS().devices.tts.shutUpOneSelf()
            }
        }
    }
}

