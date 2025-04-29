package com.voice.sdk.asrInput

import com.voice.sdk.VoiceConfigManager
import com.voice.sdk.device.DeviceHolder
import com.voyah.ai.common.utils.LogUtils
import com.voyah.ds.protocol.entity.AsrRecognizeResult
import com.voyah.ds.protocol.impl.request.entity.ContextAsrRequestHead
import com.voyah.ds.protocol.impl.response.entity.ErrorResponse
import com.voyah.ds.sdk.engine.online.OnlineConfig
import com.voyah.ds.sdk.engine.online.OnlineEngine
import com.voyah.ds.sdk.env.DefaultEnv
import com.voyah.ds.sdk.env.IEnv
import com.voyah.ds.sdk.proxy.IAsrRequestOpCallback
import com.voyah.ds.sdk.proxy.impl.DsProxyConfig
import java.util.UUID

/**
 * @Date 2024/11/29 10:53
 * @Author 8327821
 * @Email *
 * @Description .
 **/

class IftyAsr(val callback: IOnAsrCallback) {

    //环境变量设为常量
    private var isGoing = true
    private val head = ContextAsrRequestHead().apply {
        reqId = UUID.randomUUID().toString()
        isFullDuplex = true
        deviceId = vin
        asrMode = 1
    }
    val instance = OnlineEngine.getInstance(env)

    companion object {
        const val TAG = "IftyAsr"
        private val vin = DeviceHolder.INS().devices?.carServiceProp?.getVinCode() ?: "123456";
        private val env: IEnv = object : DefaultEnv() {
            override fun hasNetwork(): Boolean {
                return true
            }

            override fun provideDsOnlineConfig(): OnlineConfig {
                val onlineConfig = super.provideDsOnlineConfig().apply {
                    deviceId = vin
                }
                val clientEnv = VoiceConfigManager.getInstance().voiceEnv
                onlineConfig.dsUrl = when (clientEnv) {
                    "test" -> {
                        "ws://14.103.48.30:8281/hu"
                    }

                    "dev" -> {
                        "ws://14.103.48.30:8271/hu"
                    }

                    "pre" -> {
                        "wss://ai-ds-pre.voyah.cn:1444/hu"
                    }

                    "prod" -> {
                        "wss://ai-ds-prd.voyah.cn:1444/hu"
                    }

                    else -> {
                        "ws://14.103.48.30:8271/hu"
                    }
                }
                return onlineConfig
            }

            override fun provideDsProxyConfig(): DsProxyConfig {
                val dsProxyConfig = super.provideDsProxyConfig()
                dsProxyConfig.maxWaitOnlineTimeout = 5L
                return dsProxyConfig
            }
        }



    }

    init {
        val status = instance.startAsrInputMethodAudioRequest(head, object : IAsrRequestOpCallback {
            override fun onRecognizeResult(result: AsrRecognizeResult?, p1: Boolean) {
                if (result?.end == 1) {
                    //识别结束
                    callback.onRecognizeResult(result.text, true)
                    callback.onRecognizeFinish()
                    stopAsr()
                } else {
                    callback.onRecognizeResult(result?.text, false)
                }
            }

            override fun onRecognizeError(error: ErrorResponse?) {
                callback.onRecognizeError(false)
            }

        })
        if (!status) {
            callback.onRecognizeError(true)
        }
    }


    fun receiveAudio(byteArray: ByteArray, phrase: RecognizePhase) {
        if (phrase == RecognizePhase.RECOGNIZE_END) {
            isGoing = false
        }
        val result = instance.sendAsrInputMethodAudioRequest(head, listOf(byteArray))
        if (!result) {
            stopAsr()
            callback.onRecognizeError(false)
        }
    }

    /**
     * 客户端手动停止录音
     */
    fun stopAsr() {
        LogUtils.d(TAG, "stopAsr")
        isGoing = false
        instance.stopAsrInputMethodAudioRequest(head);
    }

    /**
     * 是否识别中
     * @return Boolean
     */
    fun isOpen(): Boolean {
        return isGoing
    }


}