package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/6/4 17:10
 * description : 卡片滚动的方向 上 下 左 右 滚动。
 */

@IntDef({CardDirection.DIRECTION_UP,
        CardDirection.DIRECTION_DOWN,
        CardDirection.DIRECTION_LEFT,
        CardDirection.DIRECTION_RIGHT
})
@Retention(RetentionPolicy.SOURCE)
public @interface CardDirection {

    int DIRECTION_UP = 0;
    int DIRECTION_DOWN = 1;
    int DIRECTION_LEFT = 2;
    int DIRECTION_RIGHT = 3;


}
