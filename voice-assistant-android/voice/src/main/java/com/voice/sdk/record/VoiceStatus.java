package com.voice.sdk.record;

/**
 * @author:lcy
 * @data:2024/7/25
 **/
public interface VoiceStatus {

    interface status {
        int VOICE_STATE_AWAKE = 0;
        int VOICE_STATE_LISTENING = 1;
        int VOICE_STATE_SPEAKING = 2;
        int VOICE_STATE_EXIT = 3;
    }

    interface wakeUpLocation {
        String FIRST_ROW_LEFT = "first_row_left";
        String FIRST_ROW_RIGHT = "first_row_right";
        String SECOND_ROW_LEFT = "second_row_left";
        String SECOND_ROW_RIGHT = "second_row_right";
        String THIRD_ROW_LEFT = "third_row_left";
        String THIRD_ROW_RIGHT = "third_row_right";
    }
}
