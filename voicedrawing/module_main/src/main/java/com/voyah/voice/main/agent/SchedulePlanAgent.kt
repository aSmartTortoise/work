package com.voyah.voice.main.agent

import com.voyah.ai.sdk.IAgentCallback

/**
 *  author : jie wang
 *  date : 2024/4/25 11:17
 *  description :
 */
class SchedulePlanAgent : IAgentCallback.Stub() {

    var voiceCallback: VoiceCallback? = null

    override fun getAgentName() = "schedule#plan"

    override fun agentExecute(data: String): String? {
        voiceCallback?.onGetVoiceContent(data)
        return ""
    }

    interface VoiceCallback {
        fun onGetVoiceContent(content: String)
    }
}