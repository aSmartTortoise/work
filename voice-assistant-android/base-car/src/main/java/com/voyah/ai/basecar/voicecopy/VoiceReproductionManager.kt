package com.voyah.ai.basecar.voicecopy

import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.vcos.vehicle.env.EnvApi
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.tts.VoiceCopyInterface
import com.voice.sdk.constant.ConfigsConstant
import com.voyah.ai.common.utils.LogUtils
import com.voyah.ai.sdk.bean.DhDialect
import com.voyah.ai.sdk.bean.PvcResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Date 2024/8/28 15:54
 * @Author 8327821
 * @Email *
 * @Description 声音复刻
 */

object VoiceReproductionManager : VoiceCopyInterface {

    private val TAG: String = VoiceReproductionManager::class.java.simpleName

    private val HOST: String = EnvApi.getBaseHttpUrl(Utils.getApp())

    //37API接口path
    private const val API = "/h56api"

    //声音复刻的接口
    private const val API_VOICE_REPRODUCTION = "/service-manager/vcos/manager/voice/list"

    //获取token的接口
    private const val API_GET_TOKEN = "/buz-user-service/app/getToken"

    //开发环境
    private lateinit var APP_ID: String
    private lateinit var APP_SECRET: String

    private val FINAL_URL_TOKEN = HOST + API + API_GET_TOKEN
    private val FINAL_URL_FETCH = HOST + API + API_VOICE_REPRODUCTION
    private const val POLLING_INTERVAL = 5 * 60 * 1000 //5min

    init {
        //1生产 2预生产 3测试 4开发
        LogUtils.d(TAG, "当前环境：${EnvApi.getEnv(Utils.getApp())}")
        when (EnvApi.getEnv(Utils.getApp())) {
            1 -> {
                APP_ID = "8d7eac1cfc9540b5a709"
                APP_SECRET = "8f4dc023398e4ca995c6a0ffbb95d97a"
            }

            2 -> {
                APP_ID = "129d61982a5a48ca9eb6"
                APP_SECRET = "79d13bf51f374287abe11edc6b28e38c"
            }

            3 -> {
                //测试环境已经不再使用了
                APP_ID = "690224c86ca64d43a5e4"
                APP_SECRET = "268565581acb41059825f4e01d8f93c2"
            }

            4 -> {
                APP_ID = "690224c86ca64d43a5e4"
                APP_SECRET = "268565581acb41059825f4e01d8f93c2"
            }

            else -> {
                //默认使用开发环境
                APP_ID = "690224c86ca64d43a5e4"
                APP_SECRET = "268565581acb41059825f4e01d8f93c2"
            }
        }
    }


    private var tokenInfo: Pair<String, Long>? = null
        private set(value) {
            field = value
            //通知SDK 刷新token
        }

    private var vin: String = DeviceHolder.INS().devices.carServiceProp.getVinCode()

    private var cache: MutableSet<VoiceReproductionBean> = mutableSetOf()

    private val handler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        try {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getString(
                ConfigsConstant.SP_KEY_VOICE_PVC
            ).takeIf {
                it.isNotBlank()
            }?.also {
                cache.addAll(Gson().fromJson(it, Array<VoiceReproductionBean>::class.java).asList())
            }
        } catch (e: Exception) {
            LogUtils.d(TAG, e.message)
        }
    }

    private val fetchListTask: Runnable = Runnable {
        /*LogUtils.d(TAG, "拉取列表任务开始, ${System.currentTimeMillis().toDateTime()}")
        LogUtils.d(TAG, "当前环境：${EnvApi.getEnv(Utils.getApp())}")
        //TODO 加上登录限制
        if (!DeviceInterfaceImpl.getInstant(Utils.getApp()).userCenterImpl.isLogin) {
            //未登录
            LogUtils.d(TAG, "未登录，等下一个周期")
            return@Runnable
        }
        try {
            getToken({LogUtils.d(TAG, "token 网络接口失败")}) {
                fetch({LogUtils.d(TAG, "fetch 网络接口失败")}) {
                    onFetchSuccess(it)
                }
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, e.message)
        }
        LogUtils.d(TAG, "拉取列表任务结束, ${System.currentTimeMillis().toDateTime()}")*/
    }

    private fun getToken(onFailure: (() -> Unit), onSuccess: Runnable) {
        //20240914 过期前五分钟，重新触发一次网络接口获取新token
        if (tokenInfo != null && tokenInfo!!.second - POLLING_INTERVAL > System.currentTimeMillis()) {
            //token 仍然有效
            onSuccess.run()
        } else {
            //走网络接口拉取token
            val okHttpClient = OkHttpClient()
            var body: String
            HashMap<String, String>().apply {
                this["appId"] = APP_ID
                this["appSecret"] = APP_SECRET
            }.also {
                body = Gson().toJson(it)
            }

            val request = Request.Builder()
                .url(FINAL_URL_TOKEN)
                .post(body.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                // 解析JSON响应
                val jsonResponse = JSONObject(response.body!!.string())
                // 从JSON对象中提取所需的数据
                val token: TokenBean =
                    Gson().fromJson(jsonResponse.getString("model"), TokenBean::class.java)
                //缓存token信息
                tokenInfo = Pair(token.token, System.currentTimeMillis() + token.expire * 1000)
                onSuccess.run()
            } else {
                onFailure.invoke()
            }
        }
    }

    private fun fetch(onFailure: Runnable, onSuccess: (ResponseBody) -> Unit) {
        val token = tokenInfo?.first ?: return
        val userId = ""// todo DeviceInterfaceImpl.getInstant(Utils.getApp()).userCenterImpl.userId
        val jsonBody: String
        HashMap<String, String>().apply {
            this["vin"] = vin
            this["oneid"] = userId
            //this["vin"] = "LDP95H964RE108193"
            //this["oneid"] = "144115205301793582"
        }.let {
            jsonBody = Gson().toJson(it)
        }
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(FINAL_URL_FETCH)
            .header("token", token)
            .post(jsonBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val response = okHttpClient.newCall(request).execute()

        if (response.isSuccessful) {
            onSuccess.invoke(response.body!!)
        } else {
            onFailure.run()
        }
    }

    fun startPolling() {
        handler.post(object : Runnable {
            override fun run() {
                executor.execute(fetchListTask)
                //轮询
                handler.postDelayed(this, POLLING_INTERVAL.toLong())
            }
        })
    }

    /**
     * 拉取一次
     */
    override fun fetchOnce() {
        executor.execute(fetchListTask)
    }

    //提供给设置的列表
    fun getCache(): List<PvcResult> {
        return cache.map {
            PvcResult().apply {
                this.voice_id = it.voiceId
                this.voice_name = it.voiceName
            }
        }
    }

    //提供给TTS的json
    override fun getTTSData(voiceId: String): String {
        var json: String = ""
        val map = mutableMapOf(
            "voiceName" to voiceId,
            "profileId" to "",
            "voiceSex" to -1
        )
        cache.firstOrNull {
            it.voiceId.equals(voiceId)
        }?.let {
            map["voiceName"] = it.voiceName
            map["profileId"] = it.profileId
            map["voiceSex"] = it.voiceSex
        }
        json = Gson().toJson(map)
        return json.ifBlank { voiceId }
    }

    //账号退登广播
    override fun clearCacheAndReset() {
        SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(
            ConfigsConstant.SP_KEY_VOICE_PVC, ""
        ) //清楚文件缓存
        cache.clear() //清楚内存缓存
        LogUtils.d(TAG, "clearCacheAndReset")
        Utils.getApp().contentResolver.notifyChange(
            ConfigsConstant.URI_EXPORT_PVC,
            null
        ) //通知设置刷新音色列表

        //切换到官方音色
        val currentDialect = com.voyah.ai.basecar.manager.SettingsManager.get().currentDialect
        val currentTts = currentDialect.tts
        //如果之前选中了自定义的音色（非官方音色），退登后回到默认，否则维持原状
        if (!currentTts.startsWith("official")) {
            //说明之前选中的被删除了
            com.voyah.ai.basecar.manager.SettingsManager.get().setDialect(
                currentDialect.apply {
                    //仅修改TTS，防止识别的设置被修改
                    this.tts = DhDialect.ID_OFFICIAL_1
                })
        }
    }


    @Throws(IOException::class)
    private fun onFetchSuccess(responseBody: ResponseBody?) {
        LogUtils.d(TAG, "请求成功，开始解析返回结果")
        if (responseBody == null) {
            return
        }

        //处理结果
        val jsonObject = JSONObject(responseBody.string())
        val listJson = jsonObject.getString("model")
        val list = Gson().fromJson(listJson, Array<VoiceReproductionBean>::class.java).asList()

        //判断云端和本地差异
        var hasDistinct = false
        if (list.size == cache.size) {
            for (bean in list) {
                if (!cache.contains(bean)) {
                    hasDistinct = true
                }
            }
        } else {
            hasDistinct = true
        }
        //云端删除，本地需要切换到默认音色
        val currentDialect = com.voyah.ai.basecar.manager.SettingsManager.get().currentDialect
        val currentTts = currentDialect.tts
        //选中了自定义的音色（非官方音色），最新拉取的列表又没有这个选项了
        if (!currentTts.startsWith("official") && !list.map {
                it.voiceId
            }.contains(currentTts)) {
            //说明之前选中的被删除了
            com.voyah.ai.basecar.manager.SettingsManager.get().setDialect(
                currentDialect.apply {
                    //仅修改TTS，防止识别的设置被修改
                    this.tts = DhDialect.ID_OFFICIAL_1
                })
        }


        //更新缓存
        cache.apply {
            this.clear()
            this.addAll(list)
            this.forEachIndexed { index, bean ->
                LogUtils.d(TAG, "缓存： index:$index, data:$bean")
            }
            //如果当前选中的被手机端删除了，
            com.voyah.ai.basecar.manager.SettingsManager.get().currentDialect.tts
        }

        if (hasDistinct) {
            //通知监听方
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(
                ConfigsConstant.SP_KEY_VOICE_PVC, listJson
            )
            Utils.getApp().contentResolver.notifyChange(ConfigsConstant.URI_EXPORT_PVC, null)
        }
    }

    private fun onFetchFailed(netCode: Int) {
        LogUtils.d(TAG, "网络请求失败，失败码：$netCode")
        //todo 如果请求失败了，是直接等轮询还是立即重试？
    }
}

data class TokenBean(val token: String, val expire: Int)
