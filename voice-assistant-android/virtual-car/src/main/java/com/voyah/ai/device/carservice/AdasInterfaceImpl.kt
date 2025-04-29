package com.voyah.ai.device.carservice

import com.voice.sdk.device.carservice.dc.AdasInterface
import com.voice.sdk.device.carservice.dc.NoaActivateInterface
import com.voice.sdk.device.carservice.dc.WardInterface

/**
 * @Date 2025/4/1 15:52
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object AdasInterfaceImpl: AdasInterface {
    override fun registerNoaActivateCallback(p0: NoaActivateInterface?) {
        println("AdasInterfaceImpl.registerNoaActivateCallback: not yet implemented")
    }

    override fun registerWardCallback(p0: String?, p1: WardInterface?) {
        println("AdasInterfaceImpl.registerWardCallback: not yet implemented")
    }

    override fun registerAVMStateCallback() {
        println("AdasInterfaceImpl.registerAVMStateCallback: not yet implemented")
    }

    override fun unRegisterAdasCallback() {
        println("AdasInterfaceImpl.unRegisterAdasCallback: not yet implemented")
    }
}