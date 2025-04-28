package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 语音服务模式，在线、离线。
 */
@IntDef({VoiceMode.VOICE_MODE_ONLINE,
        VoiceMode.VOICE_MODE_OFFLINE})
@Retention(RetentionPolicy.SOURCE)
public @interface VoiceMode {

    int VOICE_MODE_ONLINE = 0;
    int VOICE_MODE_OFFLINE = 1;

}
