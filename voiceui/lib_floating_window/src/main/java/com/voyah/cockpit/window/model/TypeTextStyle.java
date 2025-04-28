package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 打字机文本样式
 */
@IntDef({TypeTextStyle.PRIMARY,
        TypeTextStyle.SECONDARY,
        TypeTextStyle.AUXILIARY})
@Retention(RetentionPolicy.SOURCE)
public @interface TypeTextStyle {


    int PRIMARY = 0;// 非拒识的asr、valid asr 文本样式
    int SECONDARY = 1; // 拒识的asr、invalid asr 文本样式
    int AUXILIARY = 2;

}
