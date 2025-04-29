package com.voyah.voice.main.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.voyah.ai.sdk.DhSpeechSDK
import com.voyah.ai.sdk.IAsrListener
import com.voyah.ai.sdk.bean.ServiceAbility
import com.voyah.ai.sdk.manager.DialogueManager
import com.voyah.voice.main.agent.SchedulePlanAgent
import com.voyah.voice.main.model.AsrInfo
import com.voyah.common.model.SchedulePlanInfoX
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 *  author : jie wang
 *  date : 2024/4/24 16:37
 *  description :
 */
class VoiceService : Service() {

    private var gson: Gson? = null

    private var schedulePlanAgent: SchedulePlanAgent? = null

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {

        val service: VoiceService
            get() = this@VoiceService

        fun init() {
            initVoiceSDK()
        }

        private fun initVoiceSDK() {
            DhSpeechSDK.initialize(applicationContext, ServiceAbility.ALL_ABILITY) {
                LogUtils.d("onSpeechReady")
                LogUtils.d("register agent.")

                schedulePlanAgent = SchedulePlanAgent().apply {
                    voiceCallback = object : SchedulePlanAgent.VoiceCallback {
                        override fun onGetVoiceContent(content: String) {
                            handleVoiceContent(content)
                        }

                    }
                }
                DialogueManager.registerAgentX(schedulePlanAgent)

                DialogueManager.registerAsrListener(object : IAsrListener.Stub() {

                    override fun asrStatus(status: Int) {
                        LogUtils.d("asrStatus status:$status")
                        val asrInfo = AsrInfo(status, "")
                        EventBus.getDefault().post(asrInfo)
                    }

                    override fun asrText(asrText: String?) {
                        LogUtils.d("asrText asr:$asrText")
                        if (!asrText.isNullOrEmpty()) {
                            val asrInfo = AsrInfo(-1, asrText)
                            EventBus.getDefault().post(asrInfo)
                        }
                    }

                } )
            }
        }
    }

    private fun handleVoiceContent(content: String) {
//        LogUtils.d("handleVoiceContent content:$content")
        try {
            val jsonObject = JSONObject(content)
            val streamMode = jsonObject.getInt("streamMode")
            LogUtils.d("handleVoiceContent streamMode:$streamMode")
            if (streamMode == 0 || (streamMode == -1)) {// 流开始或者非流式响应
                if (jsonObject.has("reqId")) {
                    val reqId = jsonObject.getString("reqId")
                    LogUtils.d("handleVoiceContent reqId is $reqId")
                }
            }
            if (jsonObject.has("extraData")) {
                val extraDataStr = jsonObject.getString("extraData")
                if (!extraDataStr.isNullOrEmpty()) {
                    val extraJsonObject = JSONObject(extraDataStr)
                    val infoJson = extraJsonObject.getString("scheduleInfo")
                    val plaInfo = getGson().fromJson(infoJson, SchedulePlanInfoX::class.java)
                    plaInfo.streamMode = streamMode
                    EventBus.getDefault().post(plaInfo)
                }
            } else {
                if (streamMode == 2) {//流结束
                    val plaInfo = SchedulePlanInfoX(-1, null, null, null, streamMode)
                    EventBus.getDefault().post(plaInfo)
                }
            }
        } catch (e: Exception) {
            LogUtils.e("handleVoiceContent e:$e")
        }
    }

    private fun getGson(): Gson {
        if (gson == null) gson = Gson()
        return gson!!
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
    }

}