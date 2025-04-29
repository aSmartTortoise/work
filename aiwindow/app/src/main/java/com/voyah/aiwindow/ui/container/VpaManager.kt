package com.voyah.aiwindow.ui.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.voyah.ai.sdk.DhSpeechSDK
import com.voyah.ai.sdk.bean.DhDirection
import com.voyah.ai.sdk.bean.LifeState
import com.voyah.ai.sdk.listener.SimpleVAResultListener
import com.voyah.ai.sdk.manager.DialogueManager
import com.voyah.aiwindow.ui.widget.plugin.IPluginContainerView
import com.voyah.aiwindow.ui.widget.plugin.PluginViewContainer
import com.voyah.aiwindow.ui.widget.vpa.SkillParams
import com.voyah.aiwindow.ui.widget.vpa.VpaAnimationView
import com.voyah.aiwindow.ui.widget.wave.WaveAnimationView

@SuppressLint("StaticFieldLeak")
object VpaManager {

    var waveAnimView: WaveAnimationView? = null
    var vpaAnimView: VpaAnimationView? = null
    private var pluginContainer: PluginViewContainer? = null

    fun init(context: Context) {
        DhSpeechSDK.initialize(context) {
            LogUtils.d("onSpeechReady() called")
            setDialogueCallback()
        }
    }

    private fun setDialogueCallback() {
        // 监听语音状态
        DialogueManager.setVAStateListener { state ->
            LogUtils.d("onState: state = $state")
            ThreadUtils.runOnUiThread {
                when (state) {
                    LifeState.AWAKE -> {
                        val direction = DialogueManager.getWakeupDirection()
                        showWaveWindow(direction)
                        showVpaWindow()
                    }

                    LifeState.LISTENING -> {
                        vpaAnimView?.startListening()
                    }

                    LifeState.SPEAKING -> {
                        vpaAnimView?.startSpeaking()
                    }

                    LifeState.ASLEEP -> {
                        dismissAll()
                    }
                }
            }
        }

        // 监听语音asr结果
        DialogueManager.setVAResultListener(object : SimpleVAResultListener() {
            override fun onAsr(text: String, isEnd: Boolean) {
                updateAsrResult(text)
            }
        })

    }


    fun dismissWaveWindow() {
        LogUtils.d("dismissWaveWindow() called")
        waveAnimView?.let {
            it.stop()
            LiteWindowManager.dismiss(WindowTag.TAG_WINDOW_WAVE)
        }
    }

    fun dismissVpaWindow() {
        LogUtils.d("dismissVpaWindow() called")
        vpaAnimView?.let {
            it.stop()
            LiteWindowManager.dismiss(WindowTag.TAG_WINDOW_VPA)
        }
    }

    fun showWaveWindow(direction: Int) {
        LogUtils.d("showWaveWindow() called with: direction = $direction")
        waveAnimView = if (waveAnimView != null) waveAnimView else WaveAnimationView(
            Utils.getApp()
        )
        if (waveAnimView?.direction == direction) {
            LogUtils.d("the same direction, ignore!!!")
            return
        }
        val gravity = when (direction) {
            DhDirection.FRONT_LEFT -> {
                Gravity.START or Gravity.TOP
            }

            DhDirection.FRONT_RIGHT -> {
                Gravity.END or Gravity.TOP
            }

            else -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }
        LiteWindowManager.with(Utils.getApp(), WindowTag.TAG_WINDOW_WAVE)
            .setDisplayId(0)
            .setGravity(gravity)
            .setLocation(0, 0)
            .setView(waveAnimView!!)
            .setWidthAndHeight(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            .setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            .show(object : FlowCallback() {
                override fun onClick() {
                    LogUtils.i("waveCallback, onClick")
                    dismissAll()
                }

                override fun onAttach(v: View?) {
                    LogUtils.v("waveCallback, onAttach")
                    waveAnimView?.startWave(direction)
                    vpaAnimView?.startAsr()
                }

                override fun onUpdate() {
                    LogUtils.v("waveCallback, onUpdate")
                    waveAnimView?.startWave(direction)
                    vpaAnimView?.startAsr()
                }

                override fun onDismiss() {
                    LogUtils.v("waveCallback, onDismiss")
                    waveAnimView = null
                }
            })
    }

    fun showVpaWindow() {
//        if (!DhSpeechSDK.isInteractionState()) {
//            return
//        }
        val curLocX = 10
        val curLoxY = 120
        LogUtils.d("showVpaWindow() called")
        vpaAnimView =
            if (vpaAnimView != null) vpaAnimView else VpaAnimationView(Utils.getApp(), null).apply {
                this.vpaClickCallback = {
                    LogUtils.d("vpa onClick() called")
                    dismissAll()
                }
                this.skillShowCallback = {
                    LogUtils.d("skillCard show=$it")
                }
            }
        LiteWindowManager.with(Utils.getApp(), WindowTag.TAG_WINDOW_VPA)
            .setDisplayId(0)
            .setView(vpaAnimView!!)
            .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
            .setWidthAndHeight(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            .setLocation(curLocX, curLoxY)
            .setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            .show(object : FlowCallback() {

                override fun onAttach(v: View?) {
                    LogUtils.v("vpaCallback, onAttach")
                    vpaAnimView?.startAsr()
                }

                override fun onUpdate() {
                    LogUtils.v("vpaCallback, onUpdate")
                    vpaAnimView?.startAsr()
                }

                override fun onDismiss() {
                    LogUtils.v("vpaCallback, onDismiss")
                    vpaAnimView = null
                }
            })
    }

    /**
     * 显示技能弹窗
     */
    fun showSkillCard(skillParam: SkillParams) {
        if (vpaAnimView == null) {
            LogUtils.d("vpaAnimView is null, can't show skillView")
            return
        }
        ThreadUtils.runOnUiThread {
            vpaAnimView!!.showSkillCard(skillParam)
        }
    }

    /**
     * 隐藏指定的技能
     * @param tag String
     */
    fun dismissSkillCard(tag: String = "") {
        if (vpaAnimView == null) {
            return
        }
        ThreadUtils.runOnUiThread {
            vpaAnimView!!.dismissSkillCard(tag)
        }
    }

    fun updateAsrResult(text: String) {
        ThreadUtils.runOnUiThread {
            val window = LiteWindowManager.getWindow(WindowTag.TAG_WINDOW_VPA)
            window?.let {
                if (!it.isVisible()) {
                    it.setVisible(true)
                }
            } ?: showVpaWindow()
            vpaAnimView?.setText(text)
        }
    }

    /**
     * 获取插件view容器
     */
    fun getPluginContainer(): IPluginContainerView {
        if (pluginContainer == null) {
            pluginContainer = PluginViewContainer()
        }
        return pluginContainer!!
    }

    private fun dismissAll() {
        DialogueManager.stopDialogue()
        dismissSkillCard()
        dismissVpaWindow()
        dismissWaveWindow()
    }
}