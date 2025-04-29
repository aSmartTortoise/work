package com.voyah.ai.sdk.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ServiceAbility.ALL_ABILITY, ServiceAbility.TTS_ABILITY, ServiceAbility.ASSISTANT_ABILITY})
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceAbility {
    int ALL_ABILITY = 0;

    int TTS_ABILITY = 1;

    int ASSISTANT_ABILITY = 2;
}
