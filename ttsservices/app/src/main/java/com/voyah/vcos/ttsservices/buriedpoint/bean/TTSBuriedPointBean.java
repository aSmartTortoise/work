package com.voyah.vcos.ttsservices.buriedpoint.bean;

import android.text.TextUtils;

import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.info.TtsBean;

public class TTSBuriedPointBean {
    public static class Key {
        public static final String TTS_ID = "tts_id";
        public static final String TTS_USE = "tts_use";
        public static final String TTS_STATUS = "tts_status";
        public static final String TTS_TIME_START = "tts_time_start";
        public static final String TTS_TIME_END = "tts_time_end";
        public static final String TTS_TEXT = "tts_text";
        public static final String TTS_LINE = "tts_line";
        public static final String TTS_SPEAKER = "tts_speaker";
    }

    private String tts_id = "";

    //tts调用方，包名
    private String tts_use = "";

    //tts的状态，
    //"正常：normal
    //打断：break
    //抛弃：discard"
    private String tts_status = "";

    //tts的发起时间
    private String tts_time_start = "";

    //tts的合成时间
    private String tts_time_end = "";

    //tts的播报文本
    private String tts_text = "";

    //TTS在离线统计
    //"在线：online
    //离线：offline
    //缓存：cache
    private String tts_line = "";

    //TTS音色类型
    //"官方音色：
    //——晓晓：xiaoxiao
    //——晓甄：xiaozhen
    //——云希：yunxi
    //个性音色：
    //——custom+个性音色具体名称"
    private String tts_speaker = "";

    public String getTts_id() {
        return tts_id;
    }

    public void setTts_id(String originTtsId, String tts_id) {
        if (TextUtils.isEmpty(originTtsId))
            this.tts_id = tts_id;
        else
            this.tts_id = originTtsId;
    }

    public String getTts_use() {
        return tts_use;
    }

    public void setTts_use(String tts_use) {
        this.tts_use = tts_use;
    }

    public String getTts_status() {
        return tts_status;
    }

    public void setTts_status(String tts_status) {
        this.tts_status = tts_status;
    }

    public String getTts_time_start() {
        return tts_time_start;
    }

    public void setTts_time_start(String tts_time_start) {
        this.tts_time_start = tts_time_start;
    }

    public String getTts_time_end() {
        return tts_time_end;
    }

    public void setTts_time_end(String tts_time_end) {
        this.tts_time_end = tts_time_end;
    }

    public String getTts_text() {
        return tts_text;
    }

    public void setTts_text(String tts_text) {
        this.tts_text = tts_text;
    }

    public String getTts_line() {
        return tts_line;
    }

    public void setTts_line(String tts_line) {
        this.tts_line = tts_line;
    }

    public String getTts_speaker() {
        return tts_speaker;
    }

    public void setTts_speaker(String tts_speaker) {
        this.tts_speaker = tts_speaker;
    }


    @Override
    public String toString() {
        return "TTSBuriedPointBean{" +
                "tts_id='" + tts_id + '\'' +
                ", tts_use='" + tts_use + '\'' +
                ", tts_status='" + tts_status + '\'' +
                ", tts_time_start='" + tts_time_start + '\'' +
                ", tts_time_end='" + tts_time_end + '\'' +
                ", tts_text='" + tts_text + '\'' +
                ", tts_line='" + tts_line + '\'' +
                ", tts_speaker='" + tts_speaker + '\'' +
                '}';
    }
}
