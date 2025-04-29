package com.voice.sdk.device.ui

/**
 * @Date 2025/2/18 19:55
 * @Author 8327821
 * @Email *
 * @Description .
 **/

interface TypeTextStyle {
    companion object {
        val PRIMARY: Int = 0
        val SECONDARY: Int = 1
        val AUXILIARY: Int = 2
    }
}

interface VoiceState {
    companion object {
        const val VOICE_STATE_AWAKE: String = "voice_state_awake"
        const val VOICE_STATE_LISTENING: String = "voice_state_listening"
        const val VOICE_STATE_SPEAKING: String = "voice_state_speaking"
        const val VOICE_STATE_OUTPUTTING: String = "voice_state_outputting"
        const val VOICE_STATE_EXIT: String = "voice_state_exit"
        const val VOICE_STATE_INIT: String = "voice_state_init"
        const val VOICE_STATE_RECOGNIZE: String = "voice_state_recognize"
        const val VOICE_STATE_RECOGNIZING: String = "voice_state_recognizing"
        const val VOICE_STATE_RECOGNIZED_INVALID: String = "voice_state_recognized_invalid"
        const val VOICE_STATE_RECOGNIZED_VALID: String = "voice_state_recognized_valid"
    }
}
