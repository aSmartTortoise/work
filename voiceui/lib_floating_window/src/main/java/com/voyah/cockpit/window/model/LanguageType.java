package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 设置语音语言类型。
 */
@IntDef({LanguageType.MANDARIN,
        LanguageType.SICHUANESE,
        LanguageType.CANTONESE})
@Retention(RetentionPolicy.SOURCE)
public @interface LanguageType {

    int MANDARIN = 0;
    int SICHUANESE = 1;
    int CANTONESE = 2;

}
