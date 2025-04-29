package com.voyah.ai.logic.openapp

import com.voice.sdk.constant.ApplicationConstant
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.system.AbstractAppProcessor
import com.voice.sdk.device.system.ShareInterface
import com.voice.sdk.tts.TtsBeanUtils

/**
 * @Date 2025/3/28 15:30
 * @Author 8327821
 * @Email *
 * @Description .
 **/
class FileTransferProcessor: AbstractAppProcessor() {

    private val shareInterface: ShareInterface by lazy {
        DeviceHolder.INS().devices.getSystem().getShare()
    }

    override fun process(map: HashMap<String, Any>): String {
        val contains = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType()
        //56D的车型代码不可靠，所以判断非H37A H37B H56C。那就是56D
        if (!contains.equals("H37A") && !contains.equals("H37B") && !contains.equals("H56C")) {
            return TtsBeanUtils.getTtsBean(1100006).tts
        }
        val switchType = getOneMapValue("switch_type", map)
        if (switchType == "open") {
            return processOpen(map)
        } else if (switchType == "close") {
            return processClose(map)
        }
        //兜底回复，理论只有打开关闭两个场景
        return TtsBeanUtils.getTtsBean(1100027).tts
    }

    override fun consume(map: HashMap<String, Any>): Boolean {
        val uiName: String = getOneMapValue("ui_name", map) ?: ""
        return ApplicationConstant.UI_NAME_FILE_TRANSFER == uiName
    }

    private fun processOpen(map: HashMap<String, Any>): String {
        if (shareInterface.isFileTransferOpened()) {
            return TtsBeanUtils.getTtsBean(6007030).selectTTs
        } else {
            if (shareInterface.getConnectState() == 1) {
                //互联连接中
                return TtsBeanUtils.getTtsBean(6007032).selectTTs
            } else {
                shareInterface.openFileTransfer()
                return if (shareInterface.isMirrorOpened()) {
                    TtsBeanUtils.getTtsBean(6007034).selectTTs
                } else {
                    TtsBeanUtils.getTtsBean(6007031).selectTTs
                }
            }
        }
    }

    private fun processClose(map: HashMap<String, Any>): String {
        return TtsBeanUtils.getTtsBean(1100006).tts
    }
}