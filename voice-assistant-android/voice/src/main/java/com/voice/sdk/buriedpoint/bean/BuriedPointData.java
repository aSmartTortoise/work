package com.voice.sdk.buriedpoint.bean;

import java.util.HashMap;

public class BuriedPointData {

    public static class Key {
        public static final String VIN = "vin";
        public static final String USER_ID = "user_id";
        public static final String VEHICLE_TYPE = "vehicle_type";
        public static final String SYSTEM_VERSION = "system_version";
        public static final String APP_VERSION = "app_version";
        public static final String LOCATION = "location";
        public static final String TIME = "time";
        public static final String INTERNET = "internet";
        public static final String SETS = "sets";//todo
        public static final String VOICE_ZONE = "voice_zone";
        public static final String SWITCH_TYPE = "switch_type";
        public static final String GENDER = "gender";
        public static final String age = "age";
        public static final String WAKEUP_TYPE = "wakeup_type";
        public static final String WAKEUP_WORD = "wakeup_word";
        public static final String ASR_TYPE = "asr_type";
        public static final String SESSION_ID = "session_id";
        public static final String MULT_ID = "mult_id";
        public static final String QUERY_ID = "query_id";
        public static final String INTERACTION_COUNT = "interaction_count";
        public static final String TIMESTAMP_VAD_START = "timestamp_vad_start";
        public static final String TIMESTAMP_VAD_END = "timestamp_vad_end";
        public static final String TIMESTAMP_ASR = "timestamp_asr";
        public static final String TIMESTAMP_NLU = "timestamp_nlu";
        public static final String ASR_WORD = "asr_word";
        //        public static final String OFFLINE_NLU = "offline_nlu";
        public static final String NLU_ONLINE_STATE = "nlu_online_state";
        public static final String NLU_PARAMS = "nlu_params";
        public static final String NLU_CONTENT = "nlu_content";
        public static final String NLU_SOURCE = "nlu_source";
        public static final String NLU_STATUS = "nlu_status";
        public static final String NLG_SCREEN = "nlg_screen";
        public static final String NLG_SPEAK = "nlg_speak";
        public static final String NLG_TYPE = "nlg_type";
        public static final String TTS_ID = "tts_id";
        public static final String NLU_VERSION = "nlu_version";
        public static final String NULL_DATA = "null_data";
        public static final String SPEECH_ENV = "speech_env";
        public static final String TRACK_ID = "track_id";
        public static final String NULL_QUERY = "null_query";


    }

    //vin码
    private String vin = "";
    //用户标识
    private String user_id = "";

    private String mult_id = "";
    //车型
    private String vehicle_type = "";
    //系统版本
    private String system_version = "";
    //语音版本
    private String app_version = "";
    //当前位置
    private String location = "";
    //当前时间
    private String time = "";

    //网络状况
    //"在线：online
    //离线：offline"
    private String internet = "";
    //语音设置项
    //"1、连续对话开关：
    //——连续对话开：sessionon
    //——连续对话关：sessionoff
    //2、方言识别设置：
    //——普通话：mandarin
    //——四川话：sicuan
    //——粤语：Cantonese"
    private HashMap<String, String> sets;
    //方位
    //"主驾：driver
    //副驾：copilot
    //二排左：second_left
    //二排右：second_right
    //三排左：third_left
    //三排右：third_right"
    private String voice_zone = "";
    //声纹信息
    //"开关：on/off
    //男女：man/woman
    //年龄：children/adult/old"
//    private HashMap<String, String> voiceprint;
    //声纹开关
    private String voiceprint_switch;
    //男女
    private String voiceprint_gender;
    //性别
    private String voiceprint_age;

    private String gender = "";
    private String age = "";

    //kws唤醒类型
    //"主唤醒：major
    //方控唤醒：voice_key
    //自然唤醒：natual
    //全时免唤醒：alltime
    //可见即可说免唤醒：visible
    //其他唤醒：other"
    private String wakeup_type = "";
    //kws文本
    private String wakeup_word = "";
    //识别类型
    //"识别：major
    //可见即可说：visible"
    private String asr_type = "";
    //多轮ID
    private String session_id = "";
    //queryID
    private String query_id = "";
    //交互轮数
    private String interaction_count = "";
    //时间戳：人声开始
    private String timestamp_vad_start = "";
    //时间戳：人声结束
    private String timestamp_vad_end = "";
    //ASR首字上屏时间
    private String timestamp_asr = "";
    //时间戳：执行语义的时间
    private String timestamp_nlu = "";
    //ASR文本
    private String asr_word = "";
    //离线语义
    //"自研的CGDM结构
    //playtts——区分：笑话、FAQ、闲聊、兜底
    //拒识——增加字段"
//    private String offline_nlu;
//    //在线语义
//    private String online_nlu;
    private String nlu_online_state = "";
    private String nlu_params = "";
    private String nlu_content = "";
    //语义来源
    //"传统：nlu
    //大模型：gpt
    //KWS：kws"
    private String nlu_source = "";
    //语义执行状态
    //"正常结束：normal_end
    //被唤醒打断：waken_end"
    private String nlu_status = "";
    //NLG上屏文案
    private String nlg_screen = "";
    //NLG播报文案
    private String nlg_speak = "";
    //NLG生成类型
    //"默认：system
    //大模型生成：gpt
    //第三方CPSP：cpsp
    //其他：other"
    private String nlg_type = "";
    //TTS ID
    private String tts_id = "";

    private String nlu_version = "";

    private String nlu_classify_level1 = "";

    private String nlu_classify_level2 = "";

    private String speech_env = "";

    private String track_id = "";

    private String null_query = "";


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender == null) {
            this.gender = "";
        } else {
            this.gender = gender;
        }
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        if (age == null) {
            this.age = "";
        } else {
            this.age = age;
        }
    }


    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {

        this.vin = vin;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        if (user_id == null) {
            this.user_id = "";
        } else {
            this.user_id = user_id;
        }

    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        this.system_version = system_version;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {

        this.time = time;

    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        if (internet == null) {
            this.internet = "";
        } else {
            this.internet = internet;
        }
    }

    public HashMap<String, String> getSets() {
        return sets;
    }

    public void setSets(HashMap<String, String> sets) {
        if (sets == null) {
            this.sets = new HashMap<>();
        } else {
            this.sets = sets;
        }
    }

    public String getVoice_zone() {
        return voice_zone;
    }

    public void setVoice_zone(String voice_zone) {
        if (voice_zone == null) {
            this.voice_zone = "";
        } else {
            this.voice_zone = voice_zone;
        }
    }


    public String getWakeup_type() {
        return wakeup_type;
    }

    public void setWakeup_type(String wakeup_type) {
        if (wakeup_type == null) {
            this.wakeup_type = "";
        } else {
            this.wakeup_type = wakeup_type;
        }
    }

    public String getWakeup_word() {
        return wakeup_word;
    }

    public void setWakeup_word(String wakeup_word) {
        if (wakeup_word == null) {
            this.wakeup_word = "";
        } else {
            this.wakeup_word = wakeup_word;
        }
    }

    public String getAsr_type() {
        return asr_type;
    }

    public void setAsr_type(String asr_type) {
        if (asr_type == null) {
            this.asr_type = "";
        } else {
            this.asr_type = asr_type;
        }
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        if (session_id == null) {
            this.session_id = "";
        } else {
            this.session_id = session_id;
        }
    }

    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        if (query_id == null) {
            this.query_id = "";
        } else {
            this.query_id = query_id;
        }
    }

    public String getInteraction_count() {
        return interaction_count;
    }

    public void setInteraction_count(String interaction_count) {
        if (interaction_count == null || interaction_count.equals("null")) {
            this.interaction_count = "";
        } else {
            this.interaction_count = interaction_count;
        }
    }

    public String getTimestamp_vad_start() {
        return timestamp_vad_start;
    }

    public void setTimestamp_vad_start(String timestamp_vad_start) {
        if (timestamp_vad_start == null) {
            this.timestamp_vad_start = "";
        } else {
            this.timestamp_vad_start = timestamp_vad_start;
        }
    }

    public String getTimestamp_vad_end() {
        return timestamp_vad_end;
    }

    public void setTimestamp_vad_end(String timestamp_vad_end) {
        if (timestamp_vad_end == null) {
            this.timestamp_vad_end = "";
        } else {
            this.timestamp_vad_end = timestamp_vad_end;
        }
    }

    public String getTimestamp_asr() {
        return timestamp_asr;
    }

    public void setTimestamp_asr(String timestamp_asr) {
        if (timestamp_asr == null) {
            this.timestamp_asr = "";
        } else {
            this.timestamp_asr = timestamp_asr;
        }
    }

    public String getTimestamp_nlu() {
        return timestamp_nlu;
    }

    public void setTimestamp_nlu(String timestamp_nlu) {
        if (timestamp_nlu == null) {
            this.timestamp_nlu = "";
        } else {
            this.timestamp_nlu = timestamp_nlu;
        }
    }

    public String getAsr_word() {
        return asr_word;
    }

    public void setAsr_word(String asr_word) {
        if (asr_word == null) {
            this.asr_word = "";
        } else {
            this.asr_word = asr_word;
        }
    }

    public String getNlu_online_state() {
        return nlu_online_state;
    }

    public void setNlu_online_state(String nlu_online_state) {
        if (nlu_online_state == null) {
            this.nlu_online_state = "";
        } else {
            this.nlu_online_state = nlu_online_state;
        }
    }

    public String getNlu_params() {
        return nlu_params;
    }

    public void setNlu_params(String nlu_params) {
        if (nlu_params == null) {
            this.nlu_params = "";
        } else {
            this.nlu_params = nlu_params;
        }
    }

    public String getNlu_content() {
        return nlu_content;
    }

    public void setNlu_content(String nlu_content) {
        if (nlu_content == null) {
            this.nlu_content = "";
        } else {
            this.nlu_content = nlu_content;
        }
    }

    public String getNlu_source() {
        return nlu_source;
    }

    public void setNlu_source(String nlu_source) {
        if (nlu_source == null) {
            this.nlu_source = "";
        } else {
            this.nlu_source = nlu_source;
        }
    }

    public String getNlu_status() {
        return nlu_status;
    }

    public void setNlu_status(String nlu_status) {
        if (nlu_status == null) {
            this.nlu_status = "";
        } else {
            this.nlu_status = nlu_status;
        }
    }

    public String getNlg_screen() {
        return nlg_screen;
    }

    public void setNlg_screen(String nlg_screen) {
        if (nlg_screen == null) {
            this.nlg_screen = "";
        } else {
            this.nlg_screen = nlg_screen;
        }
    }

    public String getNlg_speak() {
        return nlg_speak;
    }

    public void setNlg_speak(String nlg_speak) {
        if (nlg_speak == null) {
            this.nlg_speak = "";
        } else {
            this.nlg_speak = nlg_speak;
        }
    }

    public String getNlg_type() {
        return nlg_type;
    }

    public void setNlg_type(String nlg_type) {
        if (nlg_type == null) {
            this.nlg_type = "";
        } else {
            this.nlg_type = nlg_type;
        }
    }

    public String getTts_id() {
        return tts_id;
    }

    public String getMult_id() {
        return mult_id;
    }

    public void setMult_id(String mult_id) {
        if (mult_id == null) {
            this.mult_id = "";
        } else {
            this.mult_id = mult_id;
        }
    }

    public void setTts_id(String tts_id) {
        if (tts_id == null) {
            this.tts_id = "";
        } else {
            this.tts_id = tts_id;
        }
    }

    public String getVoiceprint_switch() {
        return voiceprint_switch;
    }

    public void setVoiceprint_switch(String voiceprint_switch) {
        if (voiceprint_switch == null) {
            this.voiceprint_switch = "";
        } else {
            this.voiceprint_switch = voiceprint_switch;
        }
    }

    public String getVoiceprint_gender() {
        return voiceprint_gender;
    }

    public void setVoiceprint_gender(String voiceprint_gender) {
        if (voiceprint_gender == null) {
            this.voiceprint_gender = "";
        } else {
            this.voiceprint_gender = voiceprint_gender;
        }
    }

    public String getVoiceprint_age() {
        return voiceprint_age;
    }

    public void setVoiceprint_age(String voiceprint_age) {
        if (voiceprint_age == null) {
            this.voiceprint_age = "";
        } else {
            this.voiceprint_age = voiceprint_age;
        }
    }

    public String getNlu_version() {
        return nlu_version;
    }

    public void setNlu_version(String nlu_version) {
        this.nlu_version = nlu_version;
    }

    public String getNlu_classify_level1() {
        return nlu_classify_level1;
    }

    public void setNlu_classify_level1(String nlu_classify_level1) {
        this.nlu_classify_level1 = nlu_classify_level1;
    }

    public String getNlu_classify_level2() {
        return nlu_classify_level2;
    }

    public void setNlu_classify_level2(String nlu_classify_level2) {
        this.nlu_classify_level2 = nlu_classify_level2;
    }

    public String getSpeech_env() {
        return speech_env;
    }

    public void setSpeech_env(String speech_env) {
        if (speech_env == null) {
            this.speech_env = "";
        } else {
            this.speech_env = speech_env;
        }
    }


    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public String getNull_query() {
        return null_query;
    }

    public void setNull_query(String null_query) {
        this.null_query = null_query;
    }

    @Override
    public String toString() {
        return "BuriedPointData{" +
                "vin='" + vin + '\'' +
                ", user_id='" + user_id + '\'' +
                ", mult_id='" + mult_id + '\'' +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", system_version='" + system_version + '\'' +
                ", app_version='" + app_version + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", internet='" + internet + '\'' +
                ", sets=" + sets +
                ", voice_zone='" + voice_zone + '\'' +
                ", voiceprint_switch='" + voiceprint_switch + '\'' +
                ", voiceprint_gender='" + voiceprint_gender + '\'' +
                ", voiceprint_age='" + voiceprint_age + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", wakeup_type='" + wakeup_type + '\'' +
                ", wakeup_word='" + wakeup_word + '\'' +
                ", asr_type='" + asr_type + '\'' +
                ", session_id='" + session_id + '\'' +
                ", query_id='" + query_id + '\'' +
                ", interaction_count='" + interaction_count + '\'' +
                ", timestamp_vad_start='" + timestamp_vad_start + '\'' +
                ", timestamp_vad_end='" + timestamp_vad_end + '\'' +
                ", timestamp_asr='" + timestamp_asr + '\'' +
                ", timestamp_nlu='" + timestamp_nlu + '\'' +
                ", asr_word='" + asr_word + '\'' +
                ", nlu_online_state='" + nlu_online_state + '\'' +
                ", nlu_params='" + nlu_params + '\'' +
                ", nlu_content='" + nlu_content + '\'' +
                ", nlu_source='" + nlu_source + '\'' +
                ", nlu_status='" + nlu_status + '\'' +
                ", nlg_screen='" + nlg_screen + '\'' +
                ", nlg_speak='" + nlg_speak + '\'' +
                ", nlg_type='" + nlg_type + '\'' +
                ", tts_id='" + tts_id + '\'' +
                ", nlu_version='" + nlu_version + '\'' +
                ", nlu_classify_level1='" + nlu_classify_level1 + '\'' +
                ", nlu_classify_level2='" + nlu_classify_level2 + '\'' +
                ", speech_env='" + speech_env + '\'' +
                ", track_id='" + track_id + '\'' +
                ", null_query='" + null_query + '\'' +
                '}';
    }
}
