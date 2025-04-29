package com.voyah.ai.sdk.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * voyah方位
 */
@IntDef({DhDirection.FRONT_LEFT, DhDirection.FRONT_RIGHT, DhDirection.REAR_LEFT, DhDirection.REAR_RIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface DhDirection {
    int FRONT_LEFT = 0;   //主驾
    int FRONT_RIGHT = 1;  //副驾
    int REAR_LEFT = 2;    //后左
    int REAR_RIGHT = 3;    //后右
}
