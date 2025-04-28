package com.voyah.cockpit.window.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/7 14:13
 * description : 悬浮窗类型
 */
@StringDef({WindowType.WINDOW_TYPE_VPN_TYPEWRITER_CARD,
        WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD,
        WindowType.WINDOW_TYPE_VOICE_WAVE_FRONT_LEFT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_FRONT_RIGHT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_REAR_LEFT,
        WindowType.WINDOW_TYPE_VOICE_WAVE_REAR_RIGHT,
        WindowType.WINDOW_TYPE_VOICE_WAVE,
        WindowType.WINDOW_TYPE_VTC_WAVE,
        WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT,
        WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT})
@Retention(RetentionPolicy.SOURCE)
public @interface WindowType {
    String WINDOW_TYPE_VPN_TYPEWRITER_CARD = "window_type_vpa_typewriter_card";
    String WINDOW_TYPE_VPA_TYPEWRITER_CARD = "window_type_vpa_typewriter_card";
    String WINDOW_TYPE_VOICE_WAVE_FRONT_LEFT = "window_type_voice_wave_front_left";
    String WINDOW_TYPE_VOICE_WAVE_FRONT_RIGHT = "window_type_voice_wave_front_right";
    String WINDOW_TYPE_VOICE_WAVE_REAR_LEFT = "window_type_voice_wave_rear_left";
    String WINDOW_TYPE_VOICE_WAVE_REAR_RIGHT = "window_type_voice_wave_rear_right";
    String WINDOW_TYPE_VOICE_WAVE = "window_type_voice_wave";

    String WINDOW_TYPE_VTC_WAVE = "window_type_vtc_wave";

    String WINDOW_TYPE_FEEDBACK_FRONT_LEFT = "window_type_execute_feedback_front_left";
    String WINDOW_TYPE_FEEDBACK_FRONT_RIGHT = "window_type_execute_feedback_front_right";

}
