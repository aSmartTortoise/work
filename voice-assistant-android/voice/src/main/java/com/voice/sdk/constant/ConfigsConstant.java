package com.voice.sdk.constant;

import android.net.Uri;
import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.system.WakeupDirection;

import java.util.Map;

public class ConfigsConstant {

    public static final Map<String, WakeupDirection> WAKEUP_LOCATION_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("first_row_left", WakeupDirection.FIRST_ROW_LEFT),
            new Pair<>("first_row_right", WakeupDirection.FIRST_ROW_RIGHT),
            new Pair<>("second_row_left", WakeupDirection.SECOND_ROW_LEFT),
            new Pair<>("second_row_right", WakeupDirection.SECOND_ROW_RIGHT)
    );


    /**
     * SharePref 相关
     */
    public static final String SP_TAG_NAME = "device_config";
    public static final String SP_KEY_DIALECT = "dialect";
    public static final String SP_KEY_WAKEUP = "wakeup";
    public static final String SP_KEY_ONESHOT = "oneshot"; // 自然唤醒
    public static final String SP_KEY_CONTINUOUS_DIALOGUE = "continuous_dialogue";
    public static final String SP_KEY_FREE_WAKEUP = "free_wakeup"; // 全时免唤醒
    public static final String SP_KEY_MULTI_ZONE_DIALOGUE = "multi_zone_dialogue";
    public static final String SP_KEY_NEARBY_TTS = "nearby_tts";
    public static final String SP_KEY_PICKUP_MODE = "pickup_mode";
    public static final String SP_KEY_MIC_MASK = "mic_mask";
    public static final String SP_KEY_CONTINUOUS_TIME = "continuous_time";
    public static final String SP_KEY_MUSIC_PREFERENCE = "music_preference";
    public static final String SP_KEY_VIDEO_PREFERENCE = "video_preference";
    public static final String SP_KEY_VOICE_PRINT_RECOGNIZE = "voice_print_recognize";
    public static final String SP_KEY_VOICE_PVC = "voice_pvc"; //声音复刻
    public static final String SP_KEY_AUDIO_DUMP = "audio_dump";
    public static final String SP_KEY_FADE_WAKEUP_DUMP = "fade_wakeup_dump";
    public static final String SP_KEY_ALL_NETWORK = "all_network";
    public static final String SP_KEY_TTS_LOG = "tts_log";
    public static final String SP_KEY_PRE_ENV = "pre_env";
    public static final String SP_KEY_NEWS_PUSH = "news_push";
    public static final String SP_KEY_NEWS_PUSH_CONFIG_TIME = "news_push_config_time";
    public static final String SP_KEY_AI_MODEL_PREFERENCE = "ai_model_preference";

    /**
     * 声纹识别用户数据
     */
    public static final String SP_VPR_NAME = "vpr_device_config";
    public static final String SP_KEY_VPR_USER = "vpr_user_info";

    /**
     * 语音对外提供数据的authority
     */
    public final static String AUTHORITY_EXPORT = Utils.getApp().getPackageName() + ".export";

    /**
     * 唤醒通知
     */
    public static final Uri URI_EXPORT_WAKEUP = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/wakeup");

    /**
     * 免唤醒通知
     */
    public static final Uri URI_EXPORT_FREE_WAKEUP = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/free_wakeup");

    /**
     * oneshot通知
     */
    public static final Uri URI_EXPORT_ONESHOT = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/oneshot");

    /**
     * 四音区通知
     */
    public static final Uri URI_EXPORT_MIC_MASK = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/mic_mask");

    /**
     * 连续对话通知
     */
    public static final Uri URI_EXPORT_CONTINUOUS_DIALOGUE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/continuous_dialogue");

    /**
     * 多音区自由对话通知
     */
    public static final Uri URI_EXPORT_MULTI_ZONE_DIALOGUE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/multi_zone_dialogue");

    /**
     * 就近播报通知
     */
    public static final Uri URI_EXPORT_NEARBY_TTS = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/nearby_tts");

    /**
     * 声纹识别通知
     */
    public static final Uri URI_EXPORT_VOICE_PRINT_RECOGNIZE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/voice_print_recognize");
    /**
     * 声纹识别用户数据变化通知
     */
    public static final Uri URI_EXPORT_VPR_USER_INFO = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/vpr_user_info");
    /**
     * 音乐偏好通知
     */
    public static final Uri URI_EXPORT_MUSIC_PREFERENCE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/music_preference");

    /**
     * 视频偏好通知
     */
    public static final Uri URI_EXPORT_VIDEO_PREFERENCE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/video_preference");

    /**
     * 方言通知
     */
    public static final Uri URI_EXPORT_DIALECT = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/dialect");

    /**
     * 声音复刻列表变化通知
     */
    public static final Uri URI_EXPORT_PVC = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/pvc");

    /**
     * 语音访问网络通道变化通知
     */
    public static final Uri URI_EXPORT_ALL_NETWORK = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/all_network");
    /**
     * TTS Log日志开关变化通知
     */
    public static final Uri URI_EXPORT_TTS_LOG = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/tts_log");
    /**
     * 新闻推送开关变化通知
     */
    public static final Uri URI_NEWS_PUSH = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/news_push");
    /**
     * 新闻推送配置时间变化通知
     */
    public static final Uri URI_NEWS_PUSH_CONFIG_TIME = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/news_push_config_time");
    /**
     * 大模型偏好变化通知
     */
    public static final Uri URI_EXPORT_AI_MODEL_PREFERENCE = Uri.parse("content://" + AUTHORITY_EXPORT + "/settings/ai_model_preference");
}
