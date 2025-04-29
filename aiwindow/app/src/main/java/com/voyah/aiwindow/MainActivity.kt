package com.voyah.aiwindow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.voyah.ai.sdk.bean.DhDirection
import com.voyah.aiwindow.aidlbean.AIMessage
import com.voyah.aiwindow.aidlbean.State
import com.voyah.aiwindow.common.PluginHelper
import com.voyah.aiwindow.databinding.ActivityMainBinding
import com.voyah.aiwindow.ui.container.VpaManager
import com.voyah.aiwindow.ui.container.VpaManager.getPluginContainer
import com.voyah.aiwindow.ui.widget.vpa.SkillParams

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        requestDrawOverlaysPermission(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                LogUtils.d("onGranted() called")
            }

            override fun onDenied() {
                LogUtils.d("onDenied() called")
            }
        })
        val message = AIMessage(PKG_NAME, CLAZZ_VIEW, "hello ,I come from plugin")
        message.width = 1000
        message.height = 600
        binding.btnShowPluginView.setOnClickListener {
            val pluginView = PluginHelper.loadPlugin(applicationContext, message)
            if (pluginView != null) {
                getPluginContainer().showPluginView(
                    pluginView, Gravity.CENTER,
                    message.width, message.height
                ) { state: State, msg: String ->
                    LogUtils.d("view state:$state, msg:$msg")
                }
            } else {
                LogUtils.d("loadPlugin fail:" + message.clazz)
            }
        }
        binding.btnDismiss.setOnClickListener { getPluginContainer().dismiss() }

        // 测试声浪动画
        binding.btnWakeup0.setOnClickListener {
            sendWaveAnimMsg(DhDirection.FRONT_LEFT)
        }
        binding.btnWakeup1.setOnClickListener {
            sendWaveAnimMsg(DhDirection.FRONT_RIGHT)
        }
        binding.btnWakeup2.setOnClickListener {
            sendWaveAnimMsg(DhDirection.REAR_LEFT)
        }
        binding.btnWakeup3.setOnClickListener {
            sendWaveAnimMsg(DhDirection.REAR_RIGHT)
        }
        binding.btnShowAsr.setOnClickListener {
            sendShowAsrMsg()
        }

        binding.btnUpdateAsr.setOnClickListener {
            sendUpdateAsrMsg()
        }

        binding.btnUpdateState.setOnClickListener {
            sendUpdateState()
        }

        binding.btnShowSkill.setOnClickListener {
            val skillParams = SkillParams().apply {
                tag = "contact"
                w = 900
                h = 300
                view = CustomView(Utils.getApp())
            }
            VpaManager.showSkillCard(skillParams)
        }

        binding.btnAsleep.setOnClickListener {
            handler.sendEmptyMessage(MSG_START_ASLEEP)
        }
    }

    private fun sendWaveAnimMsg(direction: Int) {
        handler.removeCallbacksAndMessages(null)
        // 显示声浪
        var msg = Message.obtain()
        msg.arg1 = direction
        msg.what = MSG_START_WAKEUP
        handler.sendMessage(msg)
    }

    private fun sendShowAsrMsg() {
        handler.removeCallbacksAndMessages(null)
        // 显示asr
        var msg = Message.obtain()
        msg.what = MSG_START_ASR
        handler.sendMessage(msg)
    }

    private fun sendUpdateAsrMsg() {
        handler.removeCallbacksAndMessages(null)
        // 显示asr
        var msg = Message.obtain()
        msg.what = MSG_UPDATE_ASR
        handler.sendMessage(msg)
    }

    private fun sendUpdateState() {
        handler.removeCallbacksAndMessages(null)
        // 显示asr
        var msg = Message.obtain()
        msg.what = MSG_START_SPEAKING
        handler.sendMessage(msg)
    }


    private val handler = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
        LogUtils.d("handleMessage, receive msg: ${msg.what}, direction: ${msg.arg1}")
        val direction = msg.arg1
        when (msg.what) {
            MSG_START_WAKEUP -> {
                VpaManager.showWaveWindow(direction)
            }

            MSG_START_LISTENING -> {
                VpaManager.vpaAnimView?.startListening()
            }

            MSG_START_SPEAKING -> {
                VpaManager.vpaAnimView?.startSpeaking()
            }

            MSG_START_ASR -> {
                VpaManager.showVpaWindow()
            }

            MSG_UPDATE_ASR -> {
                VpaManager.updateAsrResult("我想")
            }

            else -> {
                VpaManager.dismissWaveWindow()
                VpaManager.dismissVpaWindow()
            }
        }
        true
    })

    /**
     * 申请浮窗权限
     */
    private fun requestDrawOverlaysPermission(callback: PermissionUtils.SimpleCallback) {
        if (!PermissionUtils.isGrantedDrawOverlays()) {
            PermissionUtils.requestDrawOverlays(callback)
        } else {
            callback.onGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        binding.unbind()
    }

    companion object {
        private const val PKG_NAME = "com.voyah.plugin"
        private const val CLAZZ_VIEW = "com.voyah.plugin.CustomView"

        private const val MSG_START_WAKEUP = 0
        private const val MSG_START_LISTENING = 1
        private const val MSG_START_SPEAKING = 2
        private const val MSG_START_ASR = 3
        private const val MSG_UPDATE_ASR = 4
        private const val MSG_START_ASLEEP = 5
    }
}