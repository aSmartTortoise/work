package com.voyah.ai.sdk.bean;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 语音设置开关项定义
 */
@StringDef({DhSwitch.MainWakeup, DhSwitch.FreeWakeup, DhSwitch.Oneshot, DhSwitch.ContinuousDialogue,
        DhSwitch.FreeViewCmd, DhSwitch.MultiZoneDialogue, DhSwitch.NearbyTTS, DhSwitch.VoicePrintRecognize,
        DhSwitch.NewsPush})
@Retention(RetentionPolicy.SOURCE)
public @interface DhSwitch {
    /**
     * 主唤醒开关
     */
    String MainWakeup = "MainWakeup";
    /**
     * 全局命令词开关
     */
    String FreeWakeup = "FreeWakeup";
    /**
     * Oneshot开关
     */
    String Oneshot = "Oneshot";
    /**
     * 连续识别模式开关
     */
    String ContinuousDialogue = "ContinuousDialogue";
    /**
     * 免唤醒可见即可说开关
     */
    String FreeViewCmd = "FreeViewCmd";
    /**
     * 多音区自由对话开关
     */
    String MultiZoneDialogue = "MultiZoneDialogue";
    /**
     * 就近播报开关
     */
    String NearbyTTS = "NearbyTTS";

    /**
     * 声纹识别开关
     */
    String VoicePrintRecognize = "VoicePrintRecognize";

    /**
     * 新闻推送开关
     */
    String NewsPush = "NewsPush";
}
