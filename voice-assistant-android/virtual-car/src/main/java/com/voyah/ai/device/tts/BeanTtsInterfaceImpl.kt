package com.voyah.ai.device.tts

import com.voice.sdk.device.tts.BeanTtsInterface
import com.voyah.ai.sdk.IGeneralTtsCallback
import com.voyah.ai.sdk.VoiceTtsBean
import com.voyah.ai.sdk.listener.ITtsPlayListener

/**
 * @Date 2025/4/7 14:23
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object BeanTtsInterfaceImpl: BeanTtsInterface {
    override fun initialize() {
    }

    override fun speak(p0: String?) {
    }

    override fun speak(p0: String?, p1: ITtsPlayListener?) {
    }

    override fun speak(p0: String?, p1: ITtsPlayListener?, p2: String?) {
    }

    override fun speak(p0: String?, p1: Boolean, p2: ITtsPlayListener?) {
    }

    override fun speak(p0: String?, p1: Int, p2: Int, p3: ITtsPlayListener?) {
    }

    override fun speak(p0: String?, p1: Int, p2: ITtsPlayListener?) {
        TODO("Not yet implemented")
    }

    override fun speakSynchronized(p0: String?, p1: Boolean, p2: ITtsPlayListener?) {
    }

    override fun speakImt(p0: String?) {
    }

    override fun speakImt(p0: String?, p1: ITtsPlayListener?) {
    }

    override fun speakImt(p0: String?, p1: ITtsPlayListener?, p2: String?) {
    }

    override fun speakInformation(p0: String?, p1: String?, p2: ITtsPlayListener?) {
    }

    override fun speakStream(p0: String?, p1: ITtsPlayListener?, p2: Int) {
    }

    override fun speakBean(p0: VoiceTtsBean?, p1: ITtsPlayListener?) {
    }

    override fun speakStreamBean(p0: VoiceTtsBean?, p1: ITtsPlayListener?, p2: Int) {
    }

    override fun speakInformationBean(p0: VoiceTtsBean?, p1: ITtsPlayListener?, p2: String?) {
    }

    override fun stopCurTts() {
    }

    override fun shutUp() {
    }

    override fun shutUpOneSelf() {
    }

    override fun isSpeaking(p0: Int): Boolean {
        return false
    }

    override fun requestAudioFocusByUsage() {
    }

    override fun releaseAudioFocusByUsage() {
    }

    override fun stopById(p0: String?) {
    }

    override fun registerGeneralTtsStatus(p0: IGeneralTtsCallback?) {
    }

    override fun unregisterGeneralTtsStatus(p0: IGeneralTtsCallback?) {
    }

    override fun onDestroy() {
    }
}