package com.voyah.aiwindow.push.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.blankj.utilcode.util.LogUtils
import com.voyah.aiwindow.aidlbean.AIMessage
import com.voyah.aiwindow.push.AIMsgManager.add
import com.voyah.aiwindow.sdk.IAIWindowService
import com.voyah.aiwindow.sdk.IMsgResultCallback

/**
 * 响应远程连接的Service
 */
class AIRemoteService : Service() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.v("onCreate() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.v("onDestroy() called")
    }

    override fun onBind(intent: Intent): IBinder? {
        return RemoteBinder()
    }

    class RemoteBinder : IAIWindowService.Stub() {
        @Throws(RemoteException::class)
        override fun sendAIMessage(msg: AIMessage, callback: IMsgResultCallback) {
            val size = add(msg, callback)
            LogUtils.d("receive AIMessage: $msg, queue size: $size")
        }
    }
}