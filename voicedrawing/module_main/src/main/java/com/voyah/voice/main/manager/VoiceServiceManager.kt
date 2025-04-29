package com.voyah.voice.main.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.main.service.VoiceService
import com.voyah.voice.framework.helper.CoroutineScopeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  author : jie wang
 *  date : 2024/4/24 20:37
 *  description :
 */
class VoiceServiceManager private constructor(private val context: Context) {

    companion object {
        fun getInstance(context: Context) = Holder.getInstance(context)
    }

    private object Holder {
        fun getInstance(context: Context) = VoiceServiceManager(context)
    }

    var serviceCallback: VoiceServiceCallback? = null
    var localBinder: VoiceService.LocalBinder? = null

    private val deathRecipient: DeathRecipient = object : DeathRecipient {
        override fun binderDied() {
            LogUtils.d("binderDied.")
            localBinder?.unlinkToDeath(this, 0)
            localBinder = null
            init()
        }
    }

    private var serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtils.d("onServiceConnected")
            serviceCallback?.onServiceBind()
            localBinder = (service as VoiceService.LocalBinder).apply {
                linkToDeath(deathRecipient, 0)
            }
            initVoice()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            localBinder = null
        }

    }

    fun init() {
        if (!checkWindowMessageServiceBind()) {
            bindService(context)
        }
    }

    private fun checkWindowMessageServiceBind(): Boolean {
        return localBinder?.isBinderAlive ?: false
    }

    private fun bindService(context: Context) {
        CoroutineScopeHelper.getInstance().normalScope?.launch(Dispatchers.Default) {
            val intent = Intent()
            intent.action = "com.voyah.voice.drawing.VOICE_SERVICE"
            intent.setPackage("com.voyah.voice.drawing")
            try {
                val result = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                LogUtils.d("bindService result:$result")
            } catch (e: Exception) {
                LogUtils.d("bindService: bind remote service error, e:$e")
            }
        }

    }

    private fun initVoice() {
        localBinder?.init()
    }

    interface VoiceServiceCallback {
        fun onServiceBind()
    }
}