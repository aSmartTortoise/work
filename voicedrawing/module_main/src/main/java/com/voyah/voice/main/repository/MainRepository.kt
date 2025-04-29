package com.voyah.voice.main.repository

import android.content.Context
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.voyah.ai.sdk.DhSpeechSDK
import com.voyah.ai.sdk.manager.DialogueManager
import com.voyah.common.model.DrawingTimesInfo
import com.voyah.voice.main.agent.SchedulePlanAgent
import com.voyah.common.model.SchedulePlanInfo
import com.voyah.common.model.User
import com.voyah.network.manager.ApiManager
import com.voyah.network.repository.BaseRepository
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

/**
 *  author : jie wang
 *  date : 2024/4/17 14:46
 *  description :
 */
class MainRepository : BaseRepository() {

    private var gson: Gson? = null

    suspend fun getSchedulePlanning(newSessionFlag: Boolean, query: String): SchedulePlanInfo? {
        return requestResponse {
            LogUtils.d("api get schedule planning.")
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("phone", "18808086666");
//            map.put("isUpdated", "1");
//            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());

            var uniqueDeviceId = DeviceUtils.getUniqueDeviceId()
            uniqueDeviceId = "1122222222111"
            val map: Map<String, Any> = mutableMapOf<String, Any>().apply {
                if (newSessionFlag) {
                    put("new_window", "True")
                } else {
                    put("new_window", "False")
                }
                put("query", query)
                put("deviceId", uniqueDeviceId)
            }
            val requestBody = RequestBody.create(
                MediaType.parse("Content-Type, application/json"),
                JSONObject(map).toString()
            )
            ApiManager.api.getSchedulePlanning(requestBody)
        }
    }

    suspend fun getRemainingDrawingTimes(vinCode: String): DrawingTimesInfo? {
        return executeRequest {
            ApiManager.api.getRemainingDrawingTimes(vinCode)
        }
    }

    suspend fun login(account: String, pwd: String): User? {
        return requestResponse {
            LogUtils.d("api login.")
            ApiManager.api.login(account, pwd)
        }
    }

   fun registerAgent(context: Context, block: (String) -> Unit) {
       DhSpeechSDK.initialize(context.applicationContext) {
           LogUtils.d("onSpeechReady")
           LogUtils.d("register agent.")

           DialogueManager.registerAgentX(SchedulePlanAgent().apply {
               voiceCallback = object : SchedulePlanAgent.VoiceCallback {
                   override fun onGetVoiceContent(content: String) {
                       LogUtils.d("content:$content")
                       if (gson == null) {
                           gson = Gson()
                       }

                       block.invoke(content)
                   }
               }
           })
       }
    }

}