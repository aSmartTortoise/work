package com.voyah.ai.sdk.bean;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 语音交互生命阶段
 * - ready --> awake -->  listening --> inputting --> recognizing -->(nlu) holding--->speaking--->asleep
 * -                          ↑                                                          ↓
 * -                           --------------------<----------------播报完成---------------
 *
 * PS:全双工状态下：播报态优先级高于输入态/识别态
 */
@StringDef({LifeState.UNKNOWN, LifeState.READY, LifeState.AWAKE,
        LifeState.SPEAKING, LifeState.LISTENING, LifeState.INPUTTING,
        LifeState.RECOGNIZING, LifeState.HOLDING, LifeState.ASLEEP})
@Retention(RetentionPolicy.SOURCE)
public @interface LifeState {
    // 未知态，比如授权失败等异常
    String UNKNOWN = "unknown";

    // sdk 初始化就绪
    String READY = "ready";

    // 唤醒态
    String AWAKE = "awake";

    // 播报态
    String SPEAKING = "speaking";

    // 聆听态
    String LISTENING = "listening";

    // 输入态(检测到vad.begin)
    String INPUTTING = "inputting";

    // 识别态(检测到vad.end)
    String RECOGNIZING = "recognizing";

    // 等待交互态(接收到nlu)
    String HOLDING = "holding";

    // 休眠态
    String ASLEEP = "asleep";
}