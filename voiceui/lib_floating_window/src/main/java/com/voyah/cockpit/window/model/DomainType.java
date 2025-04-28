package com.voyah.cockpit.window.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/7 14:33
 * description : 即时卡片业务类型
 */
@StringDef({DomainType.DOMAIN_TYPE_BT_PHONE,
        DomainType.DOMAIN_TYPE_WEATHER,
        DomainType.DOMAIN_TYPE_SCHEDULE,
        DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO
})
@Retention(RetentionPolicy.SOURCE)
public @interface DomainType {
    String DOMAIN_TYPE_BT_PHONE = "domain_type_bt_phone";
    String DOMAIN_TYPE_WEATHER = "domain_type_weather";
    String DOMAIN_TYPE_SCHEDULE = "domain_type_schedule";
    String DOMAIN_TYPE_MULTIMEDIA_VIDEO = "domain_type_multimedia_video";
}
