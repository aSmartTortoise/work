package com.voice.sdk.device.ui

import com.voice.sdk.VoiceImpl
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.system.DeviceScreenType
import com.voice.sdk.device.ui.UIMgr.hasVoiceInterAction
import com.voice.sdk.util.LogUtils

/**
 * @Date 2025/2/18 13:41
 * @Author 8327821
 * @Email *
 * @Description 屏幕亮灭屏状态统一监听
 **/


interface IScreenStateChangeListener {
    fun onScreenStateChanged(screenId: Int, state: Int) {}

    /**
     * 是否持久化监听，否则回调一次后自动移除
     */
    fun isPersistent(): Boolean = true
}
object ScreenStateHelper {

    private val screen by lazy {
        DeviceHolder.INS().devices.system.screen
    }

    const val STATE_ON = 1 // 亮屏
    const val STATE_OFF = 0 // 灭屏

    const val SCREENID_MAIN: Int = 0
    const val SCREENID_PASSENGER: Int = 1
    const val SCREENID_INSTRUMENT: Int = 2
    const val SCREENID_VEHICLE_CONTROL: Int = 3
    const val SCREENID_HUD: Int = 4
    const val SCREENID_VPA: Int = 5
    const val SCREENID_CEILING: Int = 6
    const val SCREENID_LEFT_REAR: Int = 7
    const val SCREENID_RIGHT_REAR: Int = 8
    const val SCREENID_MID_REAR: Int = 9

    /*=======================监听屏幕熄屏退出语音 START===========================*/

    private val screenStateMap = mutableMapOf<Int, Int>().apply {
        put(SCREENID_MAIN, screen.getScreenOnOffState(SCREENID_MAIN))
        put(SCREENID_PASSENGER, screen.getScreenOnOffState(SCREENID_PASSENGER))
        put(SCREENID_CEILING, screen.getScreenOnOffState(SCREENID_CEILING))
    }

        private val listener: IScreenStateChangeListener = object : IScreenStateChangeListener {
        override fun onScreenStateChanged(screenId: Int, state: Int) {
            if (state == STATE_OFF) {
                LogUtils.d("ScreenStateHelper", "screenId = $screenId, state = $state")
                when (screenId) {
                    SCREENID_MAIN -> {
                        LogUtils.d("ScreenStateHelper", "onMainScreenOff")

                        //37车型, 只有一块屏，只要熄屏就退语音
                        val carType = DeviceHolder.INS().devices.carServiceProp.getCarType()
                        if (carType.contains("37")) {
                            VoiceImpl.getInstance().exDialog() //主驾有语音交互
                            return
                        }
                        //多屏车型，需要考虑当前屏幕是否有语音内容（来自该声源或者承载其他声源）
                        if (hasVoiceInterAction(0)) {
                            VoiceImpl.getInstance().exDialog() //主驾有语音交互
                            return
                        }
                        //后排声源，借助中控屏承载的场景
                        if (hasVoiceInterAction(2)) {
                            if (!screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
                                VoiceImpl.getInstance().exDialog()
                                return
                            }
                            if (!screen.isCeilScreenOpen) {
                                VoiceImpl.getInstance().exDialog()
                                return
                            }
                        }
                    }

                    SCREENID_PASSENGER -> {
                        LogUtils.d("ScreenStateHelper", "onPassengerScreenOff")

                        if (hasVoiceInterAction(1)) {
                            VoiceImpl.getInstance().exDialog()
                        }
                    }
                    SCREENID_CEILING -> {
                        LogUtils.d("ScreenStateHelper", "onCeilScreenOff")
                        if (screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
                            if (hasVoiceInterAction(2)) {
                                VoiceImpl.getInstance().exDialog()
                            }
                        }
                    }
                }
            }
        }

        override fun isPersistent(): Boolean {
            return true
        }
    }

    fun bindVoiceWithScreen() {
        //TODO 1.5及以后车型不需要该逻辑
        if (DeviceHolder.INS().devices.carServiceProp.isH56D()) {
            return
        }
        if (screen.isSupportScreen(DeviceScreenType.CENTRAL_SCREEN)) {
            addListener(SCREENID_MAIN, listener)
        }
        if (screen.isSupportScreen(DeviceScreenType.PASSENGER_SCREEN)) {
            addListener(SCREENID_PASSENGER, listener)
        }
        if (screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
            addListener(SCREENID_CEILING, listener)
        }
    }

    /*=======================监听屏幕熄屏退出语音 END===========================*/

    //MegaScreenManager的设计是当一个屏幕的状态有变化时，回回调所有屏幕的最新状态
    private val listenerMap = mutableMapOf<Int, MutableList<IScreenStateChangeListener>>()


    fun addListener(screenId: Int, listener: IScreenStateChangeListener) {
        synchronized(listenerMap) {
            val listeners = listenerMap[screenId] ?: mutableListOf<IScreenStateChangeListener>().also {
                listenerMap[screenId] = it
            }
            listeners.add(listener)
        }
    }

    fun removeListener(screenId: Int, listener: IScreenStateChangeListener) {
        synchronized(listenerMap) {
            val listeners = listenerMap[screenId] ?: return
            listeners.remove(listener)
        }
    }

    fun notifyScreenStateChanged(screenId: Int, state: Int) {
        synchronized(listenerMap) {

            if (screenStateMap.containsKey(screenId)) {
                val oldState = screenStateMap[screenId]
                if (oldState == state) {
                    //状态没有变化，不需要回调
                    return
                } else {
                    //状态有变化，更新状态 并通知
                    screenStateMap[screenId] = state

                    val listeners = listenerMap[screenId] ?: return
                    for (listener in listeners) {
                        listener.onScreenStateChanged(screenId, state)
                        listener.takeIf {
                            !it.isPersistent()
                        }?.also {
                            //非持久化监听，移除
                            removeListener(screenId, it)
                        }
                    }
                }

            } else {
                //原本没记录这个屏幕的状态，记录下
                screenStateMap[screenId] = state
            }
        }
    }

}