package com.voice.sdk.device.system;

import com.voyah.ai.sdk.bean.DhDirection;

public enum WakeupDirection {

    FIRST_ROW_LEFT(DhDirection.FRONT_LEFT, "主驾"),
    FIRST_ROW_RIGHT(DhDirection.FRONT_RIGHT, "副驾"),
    SECOND_ROW_LEFT(DhDirection.REAR_LEFT, "后排左"),
    SECOND_ROW_RIGHT(DhDirection.REAR_RIGHT, "后排右");

    private final int direction;

    private final String chName;

    WakeupDirection(int direction, String chName) {
        this.direction = direction;
        this.chName = chName;
    }

    public static WakeupDirection fromKey(int direction) {
        for (WakeupDirection e : WakeupDirection.values()) {
            if (direction == e.direction) {
                return e;
            }
        }
        return FIRST_ROW_LEFT;
    }

    public int getDirection() {
        return direction;
    }

    public String getChName() {
        return chName;
    }
}