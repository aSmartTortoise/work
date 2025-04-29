package com.voyah.ai.device.manager

import com.voice.sdk.device.base.DialogueInterface
import com.voyah.ai.sdk.SceneIntent
import com.voyah.ai.sdk.bean.NluResult
import com.voyah.ai.sdk.listener.IRmsDbListener
import com.voyah.ai.sdk.listener.IVAReadyListener
import com.voyah.ai.sdk.listener.IVAResultListener
import com.voyah.ai.sdk.listener.IVAStateListener

/**
 * @Date 2025/4/3 10:20
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object DialogueManager:  DialogueInterface{
    override fun registerResultListener(p0: IVAResultListener?) {
    }

    override fun unregisterResultListener(p0: IVAResultListener?) {
    }

    override fun registerStateCallback(p0: IVAStateListener?) {
    }

    override fun unregisterStateListener(p0: IVAStateListener?) {
    }

    override fun registerReadyListener(p0: IVAReadyListener?) {
    }

    override fun unregisterReadyListener(p0: IVAReadyListener?) {
    }

    override fun onViewCommandCallback(p0: String?, p1: NluResult?) {
    }

    override fun onAsrResultCallback(p0: Boolean, p1: String?, p2: Boolean) {
    }

    override fun onTtsCallback(p0: String?) {
    }

    override fun onNluResultCallback(p0: NluResult?) {
    }

    override fun onShortCommandCallback(p0: String?, p1: NluResult?) {
    }

    override fun onVoiceStateCallback(p0: String?, vararg p1: String?) {
    }

    override fun isInteractionState(): Boolean {
        return true
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun getSpeechState(): String {
        return ""
    }

    override fun getDirection(): Int {
        return 0
    }

    override fun getDirectionNature(): String {
        return ""
    }

    override fun stopDialogue() {
    }

    override fun startDialogue(p0: Int) {
    }

    override fun registerRmsDbListener(p0: IRmsDbListener?) {
    }

    override fun unregisterRmsDbListener(p0: IRmsDbListener?) {
    }

    override fun onRmsChangeCallback(p0: Float) {
    }

    override fun triggerSceneIntent(p0: SceneIntent?): Int {
        return 0
    }
}