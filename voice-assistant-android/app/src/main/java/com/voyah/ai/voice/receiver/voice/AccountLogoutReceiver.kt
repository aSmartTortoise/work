package com.voyah.ai.voice.receiver.voice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.voice.sdk.device.DeviceHolder
import com.voyah.ai.common.utils.LogUtils

/**
 * @Date 2024/11/19 9:47
 * @Author 8327821
 * @Email *
 * @Description 账号退登广播
 **/
const val TAG = "AccountLogoutReceiver"
class AccountLogoutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        if (intent.action == "com.voyah.personalcenter.login") {
            LogUtils.d(TAG, "登录广播")
            DeviceHolder.INS().devices.voiceCopy.fetchOnce()
        } else if (intent.action == "com.voyah.personalcenter.logout") {
            LogUtils.d(TAG, "退登广播")
            DeviceHolder.INS().devices.voiceCopy.clearCacheAndReset()
        }
    }

    companion object {
        @JvmStatic
        fun register(context: Context) {
            val filter = IntentFilter().apply {
                addAction("com.voyah.personalcenter.logout")
                addAction("com.voyah.personalcenter.login")
            }
            context.registerReceiver(AccountLogoutReceiver(), filter)
        }
    }
}