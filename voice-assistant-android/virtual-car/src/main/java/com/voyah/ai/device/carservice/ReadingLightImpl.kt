package com.voyah.ai.device.carservice

import com.voice.sdk.device.carservice.dc.ReadingLightInterface
import java.util.ArrayList

/**
 * @Date 2025/4/1 15:56
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object ReadingLightImpl: ReadingLightInterface {
    override fun getAllPositions(): ArrayList<Int> {
        return arrayListOf(0,1,2,3,4,5)
    }

    override fun isAllCarTTS(p0: ArrayList<Int>?): Boolean {
        return if (p0 == null) {
            false
        } else {
            p0.size > 4
        }
    }
}