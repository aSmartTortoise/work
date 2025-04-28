package com.voyah.cockpit.window.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 语音状态
 */
@StringDef({VoiceState.VOICE_STATE_INIT,
        VoiceState.VOICE_STATE_AWAKE,
        VoiceState.VOICE_STATE_LISTENING,
        VoiceState.VOICE_STATE_RECOGNIZE,
        VoiceState.VOICE_STATE_SPEAKING,
        VoiceState.VOICE_STATE_OUTPUTTING,
        VoiceState.VOICE_STATE_EXIT,
        VoiceState.VOICE_STATE_RECOGNIZING,
        VoiceState.VOICE_STATE_RECOGNIZED_INVALID,
        VoiceState.VOICE_STATE_RECOGNIZED_VALID
})
@Retention(RetentionPolicy.SOURCE)
public @interface VoiceState {

    String VOICE_STATE_AWAKE = "voice_state_awake";
    String VOICE_STATE_LISTENING = "voice_state_listening";
    String VOICE_STATE_SPEAKING = "voice_state_speaking";
    String VOICE_STATE_OUTPUTTING = "voice_state_outputting";
    String VOICE_STATE_EXIT = "voice_state_exit";
    String VOICE_STATE_INIT = "voice_state_init";
    String VOICE_STATE_RECOGNIZE = "voice_state_recognize";
    String VOICE_STATE_RECOGNIZING = "voice_state_recognizing";
    String VOICE_STATE_RECOGNIZED_INVALID = "voice_state_recognized_invalid";
    String VOICE_STATE_RECOGNIZED_VALID = "voice_state_recognized_valid";

}
