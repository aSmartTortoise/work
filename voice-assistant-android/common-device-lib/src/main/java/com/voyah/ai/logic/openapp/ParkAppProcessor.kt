package com.voyah.ai.logic.openapp

import com.voice.sdk.device.system.AbstractAppProcessor
import com.voice.sdk.tts.TtsBeanUtils
import org.apache.commons.lang3.StringUtils

/**
 * @Date 2025/3/28 15:51
 * @Author 8327821
 * @Email *
 * @Description .
 **/
class ParkAppProcessor : AbstractAppProcessor() {
    override fun process(map: HashMap<String, Any>): String {
        val switchType = getOneMapValue("switch_type", map)
        if (switchType == "open") {
            return processOpen(map)
        } else if (switchType == "close") {
            return processClose(map)
        }
        return ""
    }

    override fun consume(map: HashMap<String, Any>): Boolean {
        /*//如果打开的是泊车辅助app，不需要tts播报，泊车辅助应用自己会播报
        if (map.containsKey("app_name")) {
            val appName = getOneMapValue("app_name", map)
            if (!StringUtils.isEmpty(appName) && (appName.contains("泊车辅助"))) {
                return true
            }
        }*/
        //TODO 未完成，先都返回false
        return false
    }

    private fun processOpen(map: HashMap<String, Any>): String {
        if (!isFromFirstRowLeft(map)) {
            return TtsBeanUtils.getTtsBean(5010503).tts
        }
        return ""
    }

    private fun processClose(map: HashMap<String, Any>): String {
        return ""
    }
}