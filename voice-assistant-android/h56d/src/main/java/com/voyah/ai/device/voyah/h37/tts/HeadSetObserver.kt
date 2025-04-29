package com.voyah.ai.device.voyah.h37.tts

import com.voice.sdk.device.tts.HeadSetInterface
import com.voyah.ai.basecar.carservice.CarServicePropUtils
import com.voyah.ai.common.utils.LogUtils
import mega.car.CarPropertyManager
import mega.car.config.H56D
import mega.car.hardware.CarPropertyValue

/**
 * @Date 2025/3/6 11:09
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object HeadSetObserver: HeadSetInterface {

    private val signals = HashSet<Int>(
        listOf<Int>(
            H56D.IVI_infoSet1_IVI_AUDIOHEADRESTSET_HW,
        )
    )

    private var listener : CarPropertyManager.CarPropertyEventCallback? = null


    private fun obtainCallback(callback: (Boolean) -> Unit): CarPropertyManager.CarPropertyEventCallback {
        return object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(carPropertyValue: CarPropertyValue<Any>) {
                //监听头枕音响的信号变更 1读1写 出于保险考虑，都监听
                if (carPropertyValue.propertyId == H56D.IVI_infoSet1_IVI_AUDIOHEADRESTSET_HW) {
                    val value: Any? = carPropertyValue.value
                    LogUtils.i("HeadSetObserver", "onChangeEvent head rest value:$value")
                    callback(value as Int > 1)
                }
            }

            override fun onErrorEvent(i: Int, i1: Int) {
                //do nothing
            }
        }
    }
    override fun registerHeadSetObserver(callback: (Boolean) -> Unit) {
        listener = obtainCallback(callback)
        CarServicePropUtils.getInstance().registerCallback(listener, signals)
    }

    override fun unRegisterHeadSetObserver() {
        CarServicePropUtils.getInstance().unregisterCallback(listener, signals)
    }
}