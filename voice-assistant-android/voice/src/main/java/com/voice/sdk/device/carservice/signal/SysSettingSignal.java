package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/23 17:32
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SysSettingSignal {
    public static final String SYS_BLUETOOTH_SWITCH = "sys_BlueToothSwitch"; //蓝牙开关
    public static final String SYS_HOTSPOT_SWITCH = "sys_HotSpotSwitch"; //热点开关
    public static final String SYS_WIFI_SWITCH = "sys_WifiSwitch"; //wifi开关
    public static final String SYS_WIFI_SHARING = "sys_wifi_sharing"; //wifi互联中
    public static final String SYS_HOTSPOT_SHARING = "sys_hotspot_sharing"; //热点互联中
    public static final String SYS_WIRELESS_CHARGE = "sys_WirelessCharge"; //无线充电
    public static final String SYS_VOLUME_FOLLOW_SPEED = "sys_VolumeFollowSpeed"; //音量随速
    public static final String SYS_ACOUSTIC_VEH_ALERT_SWITCH = "sys_AcousticVehAlertSwitch"; //低速行人警示音开关
    public static final String SYS_ACOUSTIC_VEH_ALERT_MODE = "sys_AcousticVehAlertMode"; //低速行人警示音模式
    public static final String SYS_ACOUSTIC_VEH_ALERT_MODE_CONFIG = "sys_AcousticVehAlertModeConfig"; //是否有低速行人警示音模式
    public static final String SYS_ACOUSTIC_VEH_ALERT_MODE_ALL_CONFIG = "sys_AcousticVehAlertModeALLConfig"; //是否是标配有低速行人警示音模式调节
    public static final String SYS_VOLUME_IMITATE = "sys_VolumeImitate"; //模拟声浪
    public static final String SYS_IMITATE_POS = "sys_ImitatePos"; //模拟声浪发声位置
    public static final String SYS_VOLUME_IMITATE_CONFIG = "sys_VolumeImitateConfig"; //模拟声浪配置字
    public static final String SYS_MEDIA_OUTPLAY = "sys_MediaOutplaySwitch"; //媒体外放开关
    public static final String SYS_MEDIA_OUTPLAY_CONFIRM = "sys_MediaOutplayConfirm"; //媒体外放是否需要二次确认
    public static final String SYS_MEDIA_OUTPLAY_CONFIG = "sys_MediaOutplayConfig"; //是否有媒体外放功能
    public static final String SYS_DRV_ASSIST_CAST = "sys_DrvAssistBroadcast"; //驾驶辅助播报

    public static final String SYS_TIME_TYPE = "sys_TimeType"; //时间格式12/24小时制
    public static final String SYS_LANGUAGE = "sys_Language"; //系统语言
    public static final String SYS_VOLUME_STREAM_TYPE = "sys_VolumeStreamType"; //音频通道类型
    public static final String SYS_VOLUME_PHONE = "sys_VolumePhone"; //通话通道音量
    public static final String SYS_VOLUME_NAVI = "sys_VolumeNavi"; //导航通道音量
    public static final String SYS_VOLUME_SYSTEM = "sys_VolumeSystem"; //系统通知通道音量
    public static final String SYS_VOLUME_BLUETOOTH = "sys_VolumeBt"; //蓝牙通道音量
    public static final String SYS_VOLUME_ASSISTANT = "sys_VolumeAssistant"; //语音助手通道音量
    public static final String SYS_VOLUME_MEDIA = "sys_VolumeMedia"; //媒体通道音量
    //静音
    public static final String SYS_MUTE_PHONE = "sys_MutePhone"; //通话通道静音状态
    public static final String SYS_MUTE_NAVI = "sys_MuteNavi"; //导航通道静音状态
    public static final String SYS_MUTE_SYSTEM = "sys_MuteSystem"; //系统通知通道静音状态
    public static final String SYS_MUTE_BLUETOOTH = "sys_MuteBt"; //蓝牙通道静音状态
    public static final String SYS_MUTE_ASSISTANT = "sys_MuteAssistant"; //语音助手通道静音状态
    public static final String SYS_MUTE_MEDIA = "sys_MuteMedia"; //媒体通道静音状态
    public static final String SYS_MUTE_INSTRUMENT = "sys_MuteInstrument"; //仪表屏静音
    public static final String SYS_KEY_TONE = "sys_KeyTone"; //按键音开关

    public static final String SYS_5GP_SWITCH = "sys_5GSwitch"; //优先5G
    public static final String SYS_5GP_CONFIG = "sys_5GConfig"; //是否有该功能
    public static final String SYS_REAR_BELT_REMINDER_SWITCH = "sys_RearBeltReminderSwitch"; //后排未系安全带提醒
    public static final String SYS_REAR_BELT_REMINDER_CONFIG = "sys_RearBeltReminderConfig"; //是否有后排未系安全带

    public static final String SYS_THEME_MODE = "sys_theme_mode"; //主题模式

    public static final String SYS_SHOW_MEDIA_OUTSIDE_DIALOG = "sys_ShowMediaOutsideDialog"; //打开媒体外放二次确认弹窗
    public static final String SYS_INSTUMENT_TONE = "sys_InstumentTone"; //仪表提示音
    public static final String SYS_HEADREST_SOUND_STATE = "sys_HeadrestSoundState"; //头枕音响状态
    public static final String SYS_SOUND_EFFECTS_MODE = "sys_SoundEffectsMode"; //音效模式
    public static final String SYS_VOLUME_FEATURE_MODE_N4 = "sys_VolumeFeatureModeN4"; //声场音色N3N4模式
    public static final String SYS_VOLUME_FEATURE_MODE_N2 = "sys_VolumeFeatureModeN2"; //声场音色N2模式
    public static final String SYS_INIT_VOLUME = "sys_InitVolume"; //初始化音量
    public static final String SYS_VOLUME_MUTE_STATE = "sys_VolumeMuteState"; //音量静音状态
    public static final String SYS_GET_VOLUME_TYPE = "sys_GetVolumeType"; //拿到音源的类型
    public static final String SYS_NIGHTTIME_MUTE_SWITCH = "sys_NighttimeMuteSwitch"; //夜间静音
    public static final String SYS_NIGHTTIME_MUTE_CONFIG = "sys_NighttimeMuteConfig"; //夜间静音
    public static final String SYS_DEEP_BASS_SUPPORT = "sys_DeepBassSupport"; //重低音是否支持操作
    public static final String SYS_INTELLIGENT_VOLUME_CONFIG = "sys_IntelligentVolumeConfig";
    public static final String SYS_REMOTE_KEY_BROADCAST_SWITCH = "sys_RemoteKeyBroadcastSwitch";
    public static final String SYS_DIAPASON_ASSESS_VEHICLE_CONFIGURATION = "sys_DiapasonAssessVehicleConfiguration";
    public static final String SYS_DIAPASON_CONFIG = "sys_DiapasonConfig"; // 是否支持低音，中音，高音的调节
    public static final String SYS_GAIN_CONFIG = "sys_GainConfig"; // 是否支持频段，增益，延时的调节
    public static final String SYS_HAS_SOUND_EFFECTS_MODE = "sys_HasSoundEffectsMode"; // 是否有音效模式功能
    public static final String SYS_SOUND_EFFECTS_MODE_ALL_CONFIG = "sys_SoundEffectsModeAllConfig"; // 音效模式是否是标配
    public static final String SYS_VOLUME_FOCUS_JUST_TTS = "sys_VolumeFocusJustTts"; // 声场聚焦是否直接tts回复手动操作
    public static final String SYS_VOLUME_FOCUS_MODE = "sys_VolumeFocusMode"; // 声场聚焦模式
    public static final String SYS_INTELLIGENT_VOLUME_SWITCH = "sys_IntelligentVolumeSwitch"; // 智能声场开关
    public static final String SYS_VOLUME_FEATURE_JUST_TTS = "sys_VolumeFeatureJustTts"; // 声场音色是否直接tts回复手动操作
    public static final String SYS_VOLUME_FEATURE_JUST_OPEN_PAGE = "sys_VolumeFeatureJustOpenPage"; // 声场音色页面是否直接打开
    public static final String SYS_HAS_AI_MUSIC_STYLE = "sys_HasAiMusicStyle"; // 是否有AI音乐曲风开关功能
    public static final String SYS_AI_MUSIC_STYLE_ALL_CONFIG = "sys_AiMusicStyleAllConfig"; // AI音乐曲风开关功能是否是标配
    public static final String SYS_HAS_AUTO_PARKING_HINT = "sys_HasAutoParkingHint"; // 是否有自动驻车激活提示音
    public static final String SYS_AUTO_PARKING_HINT_SWITCH = "sys_AutoParkingHintSwitch"; // 自动驻车激活提示音开关

}
