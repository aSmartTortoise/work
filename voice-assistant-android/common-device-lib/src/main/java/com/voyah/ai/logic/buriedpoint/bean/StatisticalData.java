package com.voyah.ai.logic.buriedpoint.bean;

import java.util.HashMap;

/**
 * 统计数据类。
 */
public class StatisticalData {
    //美佳数据
    private String app_id;
    //vin码
    private String vin;
    //用户标识
    private String user_id;
    //车型
    private String vehicle_type;
    //系统版本
    private String system_version;
    //语音版本
    private String app_version;
    //当前位置
    private String location;
    //当前时间
    private String time;

    //网络状况
    //"在线：online
    //离线：offline"
    private String internet;
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
    private String voice_zone;
    //声纹信息
    //"开关：on/off
    //男女：man/woman
    //年龄：children/adult/old"
//    private HashMap<String, String> voiceprint;

    private String switch_type;
    private String gender;
    private String age;
    //kws唤醒类型
    //"主唤醒：major
    //方控唤醒：voice_key
    //自然唤醒：natual
    //全时免唤醒：alltime
    //可见即可说免唤醒：visible
    //其他唤醒：other"
    private String wakeup_type;
    //kws文本
    private String wakeup_word;
    //识别类型
    //"识别：major
    //可见即可说：visible"
    private String asr_type;
    //多轮ID
    private String session_id;
    //queryID
    private String query_id;
    //交互轮数
    private String interaction_count;
    //时间戳：人声开始
    private String timestamp_vad_start;
    //时间戳：人声结束
    private String timestamp_vad_end;
    //ASR首字上屏时间
    private String timestamp_asr;
    //时间戳：执行语义的时间
    private String timestamp_nlu;
    //ASR文本
    private String asr_word;
    //离线语义
    //"自研的CGDM结构
    //playtts——区分：笑话、FAQ、闲聊、兜底
    //拒识——增加字段"
//    private String offline_nlu;
//    //在线语义
//    private String online_nlu;

    //语义离在线状态
    private String nlu_online_state;
    //语义的参数
    private String nlu_params;
    //语义的函数
    private String nlu_content;
    //语义来源
    //"传统：nlu
    //大模型：gpt
    //KWS：kws"
    private String nlu_source;
    //语义执行状态
    //"正常结束：normal_end
    //被唤醒打断：waken_end"
    private String nlu_status;
    //NLG上屏文案
    private String nlg_screen;
    //NLG播报文案
    private String nlg_speak;
    //NLG生成类型
    //"默认：system
    //大模型生成：gpt
    //第三方CPSP：cpsp
    //其他：other"
    private String nlg_type;
    //TTS ID
    private String tts_id;

    public String getApp_id() {
        return app_id;
    }

    public String getSwitch_type() {
        return switch_type;
    }

    public void setSwitch_type(String switch_type) {
        if (switch_type == null) {
            this.switch_type = "";
        } else {
            this.switch_type = switch_type;
        }
    }

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

    public void setApp_id(String app_id) {
        if (app_id == null) {
            this.app_id = "";
        } else {
            this.app_id = app_id;
        }
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        if (vin == null) {
            this.vin = "";
        } else {
            this.vin = vin;
        }
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
        if (vehicle_type == null) {
            this.vehicle_type = "";
        } else {
            this.vehicle_type = vehicle_type;
        }
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        if (system_version == null) {
            this.system_version = "";
        } else {
            this.system_version = system_version;
        }
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        if (app_version == null) {
            this.app_version = "";
        } else {
            this.app_version = app_version;
        }
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
        if (time == null) {
            this.time = "";
        } else {
            this.time = time;
        }
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
        if (interaction_count == null) {
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

    public void setTts_id(String tts_id) {
        if (tts_id == null) {
            this.tts_id = "";
        } else {
            this.tts_id = tts_id;
        }
    }

    @Override
    public String toString() {
        return "StatisticalData{" +
                "app_id='" + app_id + '\'' +
                ", vin='" + vin + '\'' +
                ", user_id='" + user_id + '\'' +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", system_version='" + system_version + '\'' +
                ", app_version='" + app_version + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", internet='" + internet + '\'' +
                ", sets=" + sets +
                ", voice_zone='" + voice_zone + '\'' +
                ", switch_type='" + switch_type + '\'' +
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
                '}';
    }
}
