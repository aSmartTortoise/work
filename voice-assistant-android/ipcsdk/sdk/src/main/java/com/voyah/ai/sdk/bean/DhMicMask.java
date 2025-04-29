package com.voyah.ai.sdk.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({DhMicMask.FRONT_LEFT, DhMicMask.FRONT_RIGHT, DhMicMask.REAR_LEFT, DhMicMask.REAR_RIGHT,
        DhMicMask.FRONT, DhMicMask.REAR, DhMicMask.LEFT, DhMicMask.RIGHT, DhMicMask.FrontLeft_RearRight,
        DhMicMask.FrontRight_RearLeft, DhMicMask.FRONT_RearLeft, DhMicMask.FRONT_RearRight, DhMicMask.REAR_FrontLef,
        DhMicMask.REAR_FrontRight, DhMicMask.ALL})
@Retention(RetentionPolicy.SOURCE)
public @interface DhMicMask {

    int FRONT_LEFT = 1;
    int FRONT_RIGHT = 2;
    int REAR_LEFT = 4;
    int REAR_RIGHT = 8;
    int FRONT = (FRONT_LEFT | FRONT_RIGHT);
    int REAR = (REAR_LEFT | REAR_RIGHT);
    int LEFT = (FRONT_LEFT | REAR_LEFT);
    int RIGHT = (FRONT_RIGHT | REAR_RIGHT);
    int FrontLeft_RearRight = (FRONT_LEFT | REAR_RIGHT);
    int FrontRight_RearLeft = (FRONT_RIGHT | REAR_LEFT);
    int FRONT_RearLeft = (FRONT | REAR_LEFT);
    int FRONT_RearRight = (FRONT | REAR_RIGHT);
    int REAR_FrontLef = (REAR | FRONT_LEFT);
    int REAR_FrontRight = (REAR | FRONT_RIGHT);
    int ALL = 0x0F;
}
