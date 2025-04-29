package com.voyah.ai.sdk.bean;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TTS speaker角色
 */
@StringDef({DhSpeaker.SPEAKER_CANTONESE, DhSpeaker.SPEAKER_SICHUAN, DhSpeaker.SPEAKER_OFFICIAL_1,
        DhSpeaker.SPEAKER_OFFICIAL_2, DhSpeaker.SPEAKER_OFFICIAL_3, DhSpeaker.SPEAKER_OFFICIAL_4,
        DhSpeaker.SPEAKER_OFFICIAL_5})
@Retention(RetentionPolicy.SOURCE)
public @interface DhSpeaker {
    String SPEAKER_CANTONESE = "cantonese";  //粤语
    String SPEAKER_SICHUAN = "sichuan";    //四川话
    String SPEAKER_OFFICIAL_1 = "official_1"; //官方音色1
    String SPEAKER_OFFICIAL_2 = "official_2";  //官方音色2
    String SPEAKER_OFFICIAL_3 = "official_3";  //官方音色3
    String SPEAKER_OFFICIAL_4 = "official_4";  //官方音色4
    String SPEAKER_OFFICIAL_5 = "official_5";  //官方音色5
}
