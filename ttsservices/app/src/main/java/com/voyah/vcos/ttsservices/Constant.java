package com.voyah.vcos.ttsservices;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/1/31
 **/
public interface Constant {
    public static final String DEFAULT_LAN_TYPE = "zh-CN";

    public static final String VOICE_PACKAGE_NAME = "com.voyah.ai.voice";

    public static final long OUT_LIFE_DROP_TIME = 3000;

    //    public static final long TTS_SDK_LOG_SIZE = 500 * 1024 * 1024;
    public static final long TTS_SDK_LOG_SIZE = 200000;

    interface McAccount {

        String SPEECH_SUBSCRIPTION_KEY_S = "7wnZ8CB4fgaLX19K4OApy6mRhigKC7kTr4ithgyehHtGWgfj9NAfJQQJ99AKAEc8KDXfT1gyAAAYACOGBzcq";
        String SERVICE_REGION_S = "chinaeast2";

//        //微软临时账号
//        String SPEECH_SUBSCRIPTION_KEY_S = "b880c2e49df54a2fa9cf8f47b1697743";
//        String SERVICE_REGION_S = "eastasia";


        String SPEECH_SUBSCRIPTION_KEY_P = "";
        String SERVICE_REGION_P = "";

        //复刻
        String SPEECH_SUBSCRIPTION_KEY_S_COPY = "abcfe90a91fc4a6180f8be15e181e84e";
        String SERVICE_REGION_S_COPY = "southeastasia";

        String DECRYPTION_KEY = "C1NkMMIXzIsUi7cKIXlVobXEtn5ZMDANEvS3JVpIAIktvBYrWPcVvNnL7aqLbFJoutP9R61vW1mlDzFk7O3zfuK0ThukKaTPJghx2t4VzGCgFuJZQSaVyF2a1beXE@39rZ0nCn5sJ@1f2RmahR";
    }

    interface VoiceName {
        String xiaoxiao = "zh-CN-XiaoxiaoNeural";
        String yunxi = "zh-CN-YunxiNeural";

        List<String> SPEAKER_LIST = Arrays.asList("official_1", "official_2", "official_3", "official_4", "official_5");

        Map<String, String> ON_LINE_NAME_MAP = new HashMap() {
            {
                put(SPEAKER_LIST.get(0), "zh-CN-XiaoxiaoNeural");
                put(SPEAKER_LIST.get(1), "zh-CN-XiaozhenNeural");
                put(SPEAKER_LIST.get(2), "zh-CN-XiaochenNeural");
                put(SPEAKER_LIST.get(3), "zh-CN-YunxiNeural");
                put(SPEAKER_LIST.get(4), "zh-CN-YunjianNeural");

            }
        };

        Map<String, String> OFF_LINE_NAME_MAP = new HashMap() {
            {
                put(SPEAKER_LIST.get(0), "zh-CN-XiaoxiaoNeural");
                put(SPEAKER_LIST.get(1), "zh-CN-XiaozhenNeural");
                put(SPEAKER_LIST.get(2), "zh-CN-XiaochenNeural");
                put(SPEAKER_LIST.get(3), "zh-CN-YunxiNeural");
                put(SPEAKER_LIST.get(4), "zh-CN-YunyiNeural");

            }
        };
    }


    interface SpeechConfig {
        String ONLINE = "online";
        String HYBRID = "hybrid";
        String OFFLINE = "offline";
        //采样率
        int SAMPLE_CODE = 24000;
        //在线转离线切换阀值
        String INTERVAL = "150";
        String BUFFER_TIMEOUT_MS = "1050";
        String BUFFER_TIMEOUT_MS_COPY = "2100";
        String BUFFER_LENGTH_MS = "400";
        String CACHING_MAX_NUMBER = "3000";
    }

    interface AudioTrackConfig {
        int SAMPLE_RATE = 24000; //采样率
        int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO; //通道配置
        int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; //音频格式
        int CONTENT_TYPE = AudioAttributes.CONTENT_TYPE_SPEECH; //语音类型使用
    }

    interface AudioFocusType {
        int GAIN_TYPE = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
    }

    //通道
    interface Usage {
        //默认通道
        int DEFAULT_USAGE = AudioAttributes.USAGE_ASSISTANT;
        //语音
        int VOICE_USAGE = AudioAttributes.USAGE_ASSISTANT;
        //        //导航
//        int NAVI_USAGE = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE;
//        //报警
//        int ALARM_USAGE = AudioAttributes.USAGE_ALARM;
//        //其他?
//        int OTHER_USAGE = AudioAttributes.USAGE_UNKNOWN;
        //提示音
        int NOTIFICATION_USAGE = AudioAttributes.USAGE_NOTIFICATION;

        //智驾使用通道号2003(底层功放 16和2003都默认为语音-会影响到就近信号)
        int ADAS_USAGE = 2003;
    }

    //三方
    interface UseSource {
        //语音
//        int VOICE_USAGE = 0;
//        //导航
//        int NAVI_USAGE = 1;
//        //报警
//        int ALARM_USAGE = 2;
//        //其他?
//        int OTHER_USAGE = 3;
    }

    interface ByteArrLen {
        int VR = 10240;
        int OTHER = 2048;
    }


    //
    interface Path {
//        String OFFLINE_RES_PATH = "/data/data/com.voyah.vcos.ttsservices/ZhCN_Fonts";
//        String SHARED_PATH = "/data/data/com.voyah.vcos.ttsservices/";

        //-------------------手机自动化

        String ZIP_RES = "/storage/emulated/0/Font.zip";
        //        String PHONE_OFFLINE_RES_PATH = "/data/data/com.voyah.vcos.ttsservices/";
        String PHONE_OFFLINE_RES_PATH = "/storage/emulated/0/Font";

        //-------------------实车资源文件加载路径
        String CAR_OFFLINE_RES_PATH = "/system/third_party/tts/";

        String VOICE_LOCAL_AUDIO_PATH = CAR_OFFLINE_RES_PATH + "Font/VoiceLocalCache/";

        String CAR_PROMPT_TONE_PATH = CAR_OFFLINE_RES_PATH + "Font/";

        //--------------------音频文件等本地保存路径
        String BASE_PATH = "/data/data/com.voyah.vcos.ttsservices/files/";
        //        String DIR_WRITE = BASE_PATH + "micSDKLog.log";
        String DIR_CACHE_WRITE = BASE_PATH + "speech_cache";

        String SDK_LOG_PATH = "/sdcard/microsoft/log/micSDKLog.log";
    }
}
