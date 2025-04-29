package com.voyah.ai.logic.openapp

import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.system.AbstractAppProcessor
import com.voice.sdk.tts.TtsBeanUtils

/**
 * @Date 2025/3/21 11:23
 * @Author 8327821
 * @Email *
 * @Description .
 **/
class BlackAppProcessor : AbstractAppProcessor() {

    private val blackAppList = if (DeviceHolder.INS().devices.carServiceProp.isH56C()
        || DeviceHolder.INS().devices.carServiceProp.isH56D() ){
        arrayListOf("微场景", "车辆健康", "酷玩盒子")
    } else {
        emptyList()
    }
    override fun process(map: HashMap<String, Any>): String {
        return TtsBeanUtils.getTtsBean(1100006).tts
    }

    override fun consume(map: HashMap<String, Any>): Boolean {
        //如果在不支持清单里，就拦截回复兜底
        val appName = getOneMapValue("app_name", map)
        return blackAppList.contains(appName)
    }
}