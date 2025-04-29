package com.voyah.ai.basecar.system

import com.mega.nexus.os.IScreenStateChangeListener
import com.mega.nexus.os.MegaScreenManager
import com.voice.sdk.device.system.ScreenInterface
import com.voice.sdk.device.ui.ScreenStateHelper
import com.voyah.ai.basecar.utils.ScreenUtils
import com.voyah.ai.common.utils.LogUtils

/**
 * @Date 2025/3/24 9:46
 * @Author 8327821
 * @Email *
 * @Description .
 **/
abstract class BaseScreenInterfaceImpl() : ScreenInterface {

    init {
        MegaScreenManager.getInstance()
            .registerScreenStateChangeListener(object : IScreenStateChangeListener.Stub() {
                override fun onChanged(displayId: Int, newState: Int) {
                    LogUtils.d("BaseScreenInterfaceImpl", "onChanged displayId=$displayId, newState=$newState")
                    ScreenStateHelper.notifyScreenStateChanged(displayId, newState)
                }
            })
    }
    override fun requestScreenOnOff(displayId: Int, isOn: Boolean) {
        ScreenUtils.getInstance().controlScreenBright(isOn, displayId)
    }

    override fun isScreenOn(displayId: Int): Boolean {
        return ScreenUtils.getInstance().getScreenOnOff(displayId) == MegaScreenManager.STATE_ON
    }

    override fun getScreenOnOffState(displayId: Int): Int {
        return ScreenUtils.getInstance().getScreenOnOff(displayId)
    }
}