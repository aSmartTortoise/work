package com.voice.sdk.device.base;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.IPvcCallback;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhMicMask;
import com.voyah.ai.sdk.bean.DhSwitch;

public interface SettingsInterface {

    void enableSwitch(@DhSwitch String switchName, boolean enable);

    boolean isEnableSwitch(@DhSwitch String switchName);

    void setUserVoiceMicMask(@DhMicMask int micMask);

    int getUserVoiceMicMask();


    void setMusicPreference(int preference);

    int getMusicPreference();

    void setVideoPreference(int preference);

    int getVideoPreference();

    void setDialect(DhDialect dialect);


    DhDialect getCurrentDialect();

    void getPvcList(@NonNull IPvcCallback callback);


    boolean enableDebugAudioDump(boolean dumpAudio);

    boolean enableFadeWakeupDump(boolean dumpFadeAwake);

    boolean enableAllNetwork(boolean enable);

    boolean enableTtsLogDump(boolean enable);

    boolean enablePreEnv(boolean enable);

    boolean isDebugAudioDumpEnabled();

    boolean isFadeWakeupDumpEnabled();

    boolean isAllNetworkEnabled();

    boolean isTtsLogDumpEnabled();

    boolean isPreEnvEnabled();

    void setNewsPushConfigTime(String time);

    String getNewsPushConfigTime();

    int getAiModelPreference();

    void setAiModelPreference(int preference);
}
