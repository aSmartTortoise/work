package com.voyah.ai.sdk.bean;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 拾音模式定义
 */
@IntDef({DhPickupMode.CAR_PICKUP_MODE_POSITION, DhPickupMode.CAR_PICKUP_MODE_ENTIRE})
@Retention(RetentionPolicy.SOURCE)
public @interface DhPickupMode {

    /**
     * 人声自适应模式:
     * <p>
     * 定向抑制非声源，如主驾唤醒，只识别主驾的声音，其他位置的声音不收听不识别
     */
    int CAR_PICKUP_MODE_POSITION = 0;

    /**
     * 全驾模式:
     * <p>
     * 如主驾唤醒后，其他位置的声音也会被收听和识别
     */
    int CAR_PICKUP_MODE_ENTIRE = 1;
}