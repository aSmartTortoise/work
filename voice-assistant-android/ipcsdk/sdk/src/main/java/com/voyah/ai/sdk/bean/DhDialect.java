package com.voyah.ai.sdk.bean;

import java.util.Objects;

/**
 * voyah方言
 */
public class DhDialect {

    /**
     * 内置ID
     */
    public static final String ID_CANTONESE = DhSpeaker.SPEAKER_CANTONESE;   //粤语
    public static final String ID_SICHUAN = DhSpeaker.SPEAKER_SICHUAN;     //四川话
    public static final String ID_OFFICIAL_1 = DhSpeaker.SPEAKER_OFFICIAL_1;  //官方音色1
    public static final String ID_OFFICIAL_2 = DhSpeaker.SPEAKER_OFFICIAL_2;   //官方音色2
    public static final String ID_OFFICIAL_3 = DhSpeaker.SPEAKER_OFFICIAL_3;   //官方音色3
    public static final String ID_OFFICIAL_4 = DhSpeaker.SPEAKER_OFFICIAL_4;   //官方音色4
    public static final String ID_OFFICIAL_5 = DhSpeaker.SPEAKER_OFFICIAL_5;   //官方音色5

    /**
     * 默认支持的方言
     */
    public static final DhDialect CANTONESE = new DhDialect(ID_CANTONESE, DhSpeaker.SPEAKER_CANTONESE, DhSpeaker.SPEAKER_CANTONESE, DhSpeaker.SPEAKER_CANTONESE);
    public static final DhDialect SICHUAN = new DhDialect(ID_SICHUAN, DhSpeaker.SPEAKER_SICHUAN, DhSpeaker.SPEAKER_SICHUAN, DhSpeaker.SPEAKER_SICHUAN);
    public static final DhDialect OFFICIAL_1 = new DhDialect(ID_OFFICIAL_1, DhSpeaker.SPEAKER_OFFICIAL_1, DhSpeaker.SPEAKER_OFFICIAL_1, DhSpeaker.SPEAKER_OFFICIAL_1);
    public static final DhDialect OFFICIAL_2 = new DhDialect(ID_OFFICIAL_2, DhSpeaker.SPEAKER_OFFICIAL_2, DhSpeaker.SPEAKER_OFFICIAL_2, DhSpeaker.SPEAKER_OFFICIAL_2);
    public static final DhDialect OFFICIAL_3 = new DhDialect(ID_OFFICIAL_3, DhSpeaker.SPEAKER_OFFICIAL_3, DhSpeaker.SPEAKER_OFFICIAL_3, DhSpeaker.SPEAKER_OFFICIAL_3);
    public static final DhDialect OFFICIAL_4 = new DhDialect(ID_OFFICIAL_4, DhSpeaker.SPEAKER_OFFICIAL_4, DhSpeaker.SPEAKER_OFFICIAL_4, DhSpeaker.SPEAKER_OFFICIAL_4);
    public static final DhDialect OFFICIAL_5 = new DhDialect(ID_OFFICIAL_5, DhSpeaker.SPEAKER_OFFICIAL_5, DhSpeaker.SPEAKER_OFFICIAL_5, DhSpeaker.SPEAKER_OFFICIAL_5);

    public String id;
    public String asr;
    public String tts;
    public String wakeup;

    public DhDialect() {
    }

    public DhDialect(String id) {
        this.id = id;
    }

    public DhDialect(String id, String asr, String tts, String wakeup) {
        this.id = id;
        this.asr = asr;
        this.tts = tts;
        this.wakeup = wakeup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DhDialect dialect = (DhDialect) o;
        return Objects.equals(id, dialect.id) &&
                Objects.equals(asr, dialect.asr) &&
                Objects.equals(tts, dialect.tts) &&
                Objects.equals(wakeup, dialect.wakeup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asr, tts, wakeup);
    }

    @Override
    public String toString() {
        return "DhDialect{" +
                "id=" + id +
                ", asr='" + asr + '\'' +
                ", tts='" + tts + '\'' +
                ", wakeup='" + wakeup + '\'' +
                '}';
    }
}
