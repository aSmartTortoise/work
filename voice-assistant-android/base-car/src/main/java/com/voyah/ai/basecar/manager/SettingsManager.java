package com.voyah.ai.basecar.manager;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.SettingsInterface;
import com.voyah.ai.basecar.voicecopy.VoiceReproductionManager;
import com.voice.sdk.constant.ConfigsConstant;
import com.voyah.ai.common.utils.DnnUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.IPvcCallback;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhMicMask;
import com.voyah.ai.sdk.bean.DhSpeaker;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.listener.IPvcResultListener;
import com.voyah.ai.sdk.manager.TTSManager;
import com.voyah.ai.voice.platform.soa.api.parameter.FeatureConfig;

import java.io.File;

public class SettingsManager implements SettingsInterface {
    private static final String TAG = "SettingsManager";

    public void enableSwitch(@DhSwitch String switchName, boolean enable) {
        switch (switchName) {
            case DhSwitch.MainWakeup:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableWakeup(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_WAKEUP, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_WAKEUP, null);
                }
                break;
            case DhSwitch.FreeWakeup:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableAllTime(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_FREE_WAKEUP, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_FREE_WAKEUP, null);
                }
                break;
            case DhSwitch.Oneshot:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableWeakWakeup(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_ONESHOT, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_ONESHOT, null);
                }
                break;
            case DhSwitch.ContinuousDialogue:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableContinueSession(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_CONTINUOUS_DIALOGUE, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_CONTINUOUS_DIALOGUE, null);
                }
                break;
            case DhSwitch.MultiZoneDialogue:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableMultiVPA(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_MULTI_ZONE_DIALOGUE, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_MULTI_ZONE_DIALOGUE, null);
                }
                break;
            case DhSwitch.NearbyTTS:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableNearbyTTS(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_NEARBY_TTS, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_NEARBY_TTS, null);
                    DeviceHolder.INS().getDevices().getVoiceCarSignal().nearbyTtsStatusChange(enable);
                }
                break;
            case DhSwitch.VoicePrintRecognize:
                if (isEnableSwitch(switchName) != enable) {
                    ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().enableVoicePrint(enable));
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_VOICE_PRINT_RECOGNIZE, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_VOICE_PRINT_RECOGNIZE, null);
                }
                break;
            case DhSwitch.NewsPush:
                if (isEnableSwitch(switchName) != enable) {
                    SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_NEWS_PUSH, enable, true);
                    Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_NEWS_PUSH, null);
                }
                break;
            default:
                break;
        }
    }

    public boolean isEnableSwitch(@DhSwitch String switchName) {
        switch (switchName) {
            case DhSwitch.MainWakeup:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_WAKEUP, true);
            case DhSwitch.FreeWakeup:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_FREE_WAKEUP, false);
            case DhSwitch.Oneshot:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_ONESHOT, false);
            case DhSwitch.ContinuousDialogue:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_CONTINUOUS_DIALOGUE, true);
            case DhSwitch.MultiZoneDialogue:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_MULTI_ZONE_DIALOGUE, true);
            case DhSwitch.NearbyTTS:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_NEARBY_TTS, true);
            case DhSwitch.VoicePrintRecognize:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_VOICE_PRINT_RECOGNIZE, true);
            case DhSwitch.NewsPush:
                return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_NEWS_PUSH, true);
            default:
                break;
        }
        return false;
    }

    public void setUserVoiceMicMask(@DhMicMask int micMask) {
        if (getUserVoiceMicMask() != micMask) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_MIC_MASK, micMask, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_MIC_MASK, null);
        }
        ThreadUtils.getSinglePool(10).execute(() -> VoiceImpl.getInstance().setRegionConfig(micMask));
    }

    public int getUserVoiceMicMask() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getInt(ConfigsConstant.SP_KEY_MIC_MASK, DhMicMask.ALL);
    }

    /**
     * 设置音乐媒体偏好
     *
     * @param preference 偏好, 0:智能选择， 1: QQ音乐， 2：网易云音乐
     */
    public void setMusicPreference(int preference) {
        if (getMusicPreference() != preference) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_MUSIC_PREFERENCE, preference, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_MUSIC_PREFERENCE, null);
        }
    }

    public int getMusicPreference() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getInt(ConfigsConstant.SP_KEY_MUSIC_PREFERENCE, 0);
    }

    /**
     * 设置视频媒体偏好
     *
     * @param preference 偏好, 0:智能选择， 1: 腾讯视频， 2：爱奇艺
     */
    public void setVideoPreference(int preference) {
        if (getVideoPreference() != preference) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_VIDEO_PREFERENCE, preference, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_VIDEO_PREFERENCE, null);
        }
    }

    public int getVideoPreference() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getInt(ConfigsConstant.SP_KEY_VIDEO_PREFERENCE, 0);
    }

    public void setDialect(DhDialect dialect) {
        String json = SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getString(ConfigsConstant.SP_KEY_DIALECT);
        DhDialect lastDialect = DhDialect.OFFICIAL_1;
        if (!TextUtils.isEmpty(json)) {
            lastDialect = JSON.parseObject(json, DhDialect.class);
        }
        notifySetting(dialect, lastDialect);
    }

    private void notifySetting(DhDialect dialect, DhDialect lastDialect) {
        SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_DIALECT, JSON.toJSONString(dialect), true);
        if (!dialect.asr.equalsIgnoreCase(lastDialect.asr)) {
            LogUtils.d(TAG, "asr setting changed");
            ThreadUtils.getSinglePool(10).execute(() -> {
                VoiceImpl.getInstance().setDialect(dialect.asr);
                DeviceHolder.INS().getDevices().getUi().setLanguageType(dialect.asr);
            });

        }
        if (!dialect.tts.equalsIgnoreCase(lastDialect.tts)) {
            LogUtils.d(TAG, "tts setting changed");
            ThreadUtils.getSinglePool(10).execute(() -> TTSManager.setTtsSpeaker(VoiceReproductionManager.INSTANCE.getTTSData(dialect.tts)));
        }
        Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_DIALECT, null);
    }

    public DhDialect getCurrentDialect() {
        String json = SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getString(ConfigsConstant.SP_KEY_DIALECT);
        if (TextUtils.isEmpty(json)) {
            return new DhDialect(DhDialect.ID_OFFICIAL_1, DhSpeaker.SPEAKER_OFFICIAL_1,
                    DhSpeaker.SPEAKER_OFFICIAL_1, DhSpeaker.SPEAKER_OFFICIAL_1);
        } else {
            return JSON.parseObject(json, DhDialect.class);
        }
    }

    public void getPvcList(@NonNull IPvcCallback callback) {
        getPvcListInner(list -> {
            try {
                callback.pvcResult(JSON.toJSONString(list));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void getPvcListInner(IPvcResultListener listener) {
        // todo 张魏 声音复刻下掉了，先注释掉
        //listener.pvcResult(VoiceReproductionManager.INSTANCE.getCache());
    }

    public boolean enableDebugAudioDump(boolean dumpAudio) {
        File externalFileDir = Utils.getApp().getExternalFilesDir(null);
        if (externalFileDir == null) {
            LogUtils.e(TAG, "enableDebugAudioDump fail, externalFileDir=null");
            return false;
        }
        String debugAudioDir = externalFileDir.getAbsolutePath() + "/audio";
        boolean ret = VoiceImpl.getInstance().enableRecordAudio(dumpAudio, isFadeWakeupDumpEnabled(), debugAudioDir);
        if (ret) {
            if (isDebugAudioDumpEnabled() != dumpAudio) {
                SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_AUDIO_DUMP, dumpAudio, true);
            }
        } else {
            LogUtils.e(TAG, "enableDebugAudioDump fail");
        }
        return ret;
    }

    public boolean enableFadeWakeupDump(boolean dumpFadeAwake) {
        File externalFileDir = Utils.getApp().getExternalFilesDir(null);
        if (externalFileDir == null) {
            LogUtils.e(TAG, "enableFadeWakeupDump fail, externalFileDir=null");
            return false;
        }
        String debugAudioDir = externalFileDir.getAbsolutePath() + "/audio";
        boolean ret = VoiceImpl.getInstance().enableRecordAudio(isDebugAudioDumpEnabled(), dumpFadeAwake, debugAudioDir);
        if (ret) {
            if (isFadeWakeupDumpEnabled() != dumpFadeAwake) {
                SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_FADE_WAKEUP_DUMP, dumpFadeAwake, true);
            }
        } else {
            LogUtils.e(TAG, "enableFadeWakeupDump fail");
        }
        return ret;
    }

    public boolean enableAllNetwork(boolean enable) {
        if (isAllNetworkEnabled() != enable) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_ALL_NETWORK, enable, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_ALL_NETWORK, null);
            DnnUtils.switchToAllNetwork(Utils.getApp(), enable);
            return true;
        }
        return false;
    }

    public boolean enableTtsLogDump(boolean enable) {
        if (isTtsLogDumpEnabled() != enable) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_TTS_LOG, enable, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_TTS_LOG, null);
            return true;
        }
        return false;
    }

    public boolean enablePreEnv(boolean enable) {
        if (isPreEnvEnabled() != enable) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_PRE_ENV, enable, true);
            return true;
        }
        return false;
    }

    public boolean isDebugAudioDumpEnabled() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_AUDIO_DUMP, false);
    }

    public boolean isFadeWakeupDumpEnabled() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_FADE_WAKEUP_DUMP, false);
    }

    public boolean isAllNetworkEnabled() {
        boolean defaultValue = false;
        String vin = DeviceHolder.INS().getDevices().getCarServiceProp().getVinCode();
        if (!TextUtils.isEmpty(vin) && vin.length() != 17) {
            LogUtils.v(TAG, "vin is dummy, use all network channel");
            defaultValue = true;
        }
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_ALL_NETWORK, defaultValue);
    }

    public boolean isTtsLogDumpEnabled() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_TTS_LOG, false);
    }

    public boolean isPreEnvEnabled() {
        boolean hasPreEnvFlag = FileUtils.isFileExists("/system/third_party/voice/res/pre_env.ini");
        if (hasPreEnvFlag) {
            LogUtils.w(TAG, "hasPreEnvFlag = true");
        }
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getBoolean(ConfigsConstant.SP_KEY_PRE_ENV, hasPreEnvFlag);
    }

    /**
     * 设置新闻消息推送配置时间
     *
     * @param time: 自定义时间， null-系统默认时间， 否则是用户自定义时间
     */
    public void setNewsPushConfigTime(String time) {
        if (!TextUtils.equals(getNewsPushConfigTime(), time)) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_NEWS_PUSH_CONFIG_TIME, time, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_NEWS_PUSH_CONFIG_TIME, null);
        }
    }

    public String getNewsPushConfigTime() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getString(ConfigsConstant.SP_KEY_NEWS_PUSH_CONFIG_TIME, null);
    }

    /**
     * 设置大模型偏好
     *
     * @param preference 偏好, 0:逍遥座舱大模型，1:DeepSeek
     */
    public void setAiModelPreference(int preference) {
        if (getAiModelPreference() != preference) {
            SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).put(ConfigsConstant.SP_KEY_AI_MODEL_PREFERENCE, preference, true);
            Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_AI_MODEL_PREFERENCE, null);
            VoiceImpl.getInstance().setLlmModel(preference == 0 ? FeatureConfig.LLM_MODE_VOYAH : FeatureConfig.LLM_MODE_DEEPSEEK);
        }
    }

    public int getAiModelPreference() {
        return SPUtils.getInstance(ConfigsConstant.SP_TAG_NAME).getInt(ConfigsConstant.SP_KEY_AI_MODEL_PREFERENCE, 0);
    }

    private static class Holder {
        private static final SettingsManager _INSTANCE = new SettingsManager();
    }

    private SettingsManager() {
        super();
        addPrivacyModeObserver();
    }

    private void addPrivacyModeObserver() {
        ContentResolver resolver = Utils.getApp().getContentResolver();
        resolver.registerContentObserver(Settings.System.getUriFor("privacySecurity"), false, new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //1 打开; 0 关闭
                int privacySecurityStatus = Settings.System.getInt(resolver, "privacySecurity", 0);
                VoiceImpl.getInstance().enablePrivacyMode(privacySecurityStatus == 1);

                if (isEnableSwitch(DhSwitch.VoicePrintRecognize)) {
                    //TODO 启用或者禁用声纹识别
                }
                if (isEnableSwitch(DhSwitch.NewsPush)) {
                    //TODO 启用或者禁用新闻推送
                }
            }
        });
    }

    public static SettingsManager get() {
        return SettingsManager.Holder._INSTANCE;
    }
}
