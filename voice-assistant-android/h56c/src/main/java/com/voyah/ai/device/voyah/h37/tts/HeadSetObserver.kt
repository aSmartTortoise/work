package com.voyah.ai.device.voyah.h37.tts

import com.voice.sdk.device.tts.HeadSetInterface
import com.voyah.ai.basecar.carservice.CarServicePropUtils
import com.voyah.ai.common.utils.LogUtils
import mega.car.CarPropertyManager
import mega.car.config.H56C
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
            H56C.IVI_infoSet1_IVI_AUDIOHEADRESTSET,
            H56C.AMP_Response1_AMP_AUDIOHEADRESTSTS
        )
    )

    private var listener : CarPropertyManager.CarPropertyEventCallback? = null


    private fun obtainCallback(callback: (Boolean) -> Unit): CarPropertyManager.CarPropertyEventCallback {
        return object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(carPropertyValue: CarPropertyValue<Any>) {
                //监听头枕音响的信号变更 1读1写 出于保险考虑，都监听
                if (carPropertyValue.propertyId == H56C.IVI_infoSet1_IVI_AUDIOHEADRESTSET || carPropertyValue.propertyId == H56C.AMP_Response1_AMP_AUDIOHEADRESTSTS) {
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