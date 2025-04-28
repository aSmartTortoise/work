package com.voyah.cockpit.window.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/7 14:13
 * description : 悬浮窗类型
 */
@StringDef({WindowType.WINDOW_TYPE_VPA_ASR,
        WindowType.WINDOW_TYPE_CARD,
        WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
        WindowType.WINDOW_TYPE_VOICE_WAVE_FRONT_LEFT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_FRONT_RIGHT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_REAR_LEFT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_REAR_RIGHT,
        WindowType.WINDOW_TYPE_VOICE_WAVE,
        WindowType.WINDOW_TYPE_VTC_WAVE,
        WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT,
        WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT,
        WindowType.WINDOW_TYPE_CARD_MAIN_SCREEN,
        WindowType.WINDOW_TYPE_VPA_TYPE_MAIN_SCREEN,
        WindowType.WINDOW_TYPE_CARD_PASSENGER_SCREEN,
        WindowType.WINDOW_TYPE_VPA_TYPE_PASSENGER_SCREEN,
        WindowType.WINDOW_TYPE_CARD_CEILING_SCREEN,
        WindowType.WINDOW_TYPE_VPA_TYPE_CEILING_SCREEN})
@Retention(RetentionPolicy.SOURCE)
public @interface WindowType {
    String WINDOW_TYPE_VPA_ASR = "window_type_vpa_asr";
    String WINDOW_TYPE_CARD = "window_type_card";
    String WINDOW_TYPE_VPA_TYPEWRITER_CARD = "window_type_vpa_typewriter_card";
    String WINDOW_TYPE_VOICE_WAVE_FRONT_LEFT = "window_type_voice_wave_front_left";
    String WINDOW_TYPE_VOICE_WAVE_FRONT_RIGHT = "window_type_voice_wave_front_right";
    String WINDOW_TYPE_VOICE_WAVE_REAR_LEFT = "window_type_voice_wave_rear_left";
    String WINDOW_TYPE_VOICE_WAVE_REAR_RIGHT = "window_type_voice_wave_rear_right";
    String WINDOW_TYPE_VOICE_WAVE = "window_type_voice_wave";

    String WINDOW_TYPE_VTC_WAVE = "window_type_vtc_wave";

    String WINDOW_TYPE_FEEDBACK_FRONT_LEFT = "window_type_execute_feedback_front_left";
    String WINDOW_TYPE_FEEDBACK_FRONT_RIGHT = "window_type_execute_feedback_front_right";

    String WINDOW_TYPE_CARD_MAIN_SCREEN = "window_type_card_main_screen";
    String WINDOW_TYPE_VPA_TYPE_MAIN_SCREEN = "window_type_vpa_type_main_screen";

    String WINDOW_TYPE_CARD_PASSENGER_SCREEN = "window_type_card_passenger_screen";
    String WINDOW_TYPE_VPA_TYPE_PASSENGER_SCREEN = "window_type_vpa_type_passenger_screen";

    String WINDOW_TYPE_CARD_CEILING_SCREEN = "window_type_card_ceiling_screen";
    String WINDOW_TYPE_VPA_TYPE_CEILING_SCREEN = "window_type_vpa_type_ceiling_screen";

    String WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN = "window_type_voice_wave_main_screen";
    String WINDOW_TYPE_VOICE_WAVE_PASSENGER_SCREEN = "window_type_voice_wave_passenger_screen";
    String WINDOW_TYPE_VOICE_WAVE_CEILING_SCREEN = "window_type_voice_wave_ceiling_screen";

}
