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
        DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO,
        DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC,
        DomainType.DOMAIN_TYPE_STOCK,
        DomainType.DOMAIN_TYPE_ENCYCLOPEDIA,
        DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA,
        DomainType.DOMAIN_TYPE_GOSSIP,
        DomainType.DOMAIN_TYPE_FAQ,
        DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM,
        DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM
})
@Retention(RetentionPolicy.SOURCE)
public @interface DomainType {
    String DOMAIN_TYPE_BT_PHONE = "domain_type_bt_phone";
    String DOMAIN_TYPE_WEATHER = "domain_type_weather";
    String DOMAIN_TYPE_SCHEDULE = "domain_type_schedule";
    String DOMAIN_TYPE_MULTIMEDIA_VIDEO = "domain_type_multimedia_video";
    String DOMAIN_TYPE_MULTIMEDIA_MUSIC = "domain_type_multimedia_music";
    String DOMAIN_TYPE_STOCK = "domain_type_stock";
    //百科
    String DOMAIN_TYPE_ENCYCLOPEDIA = "domain_type_encyclopedia";
    //车百科
    String DOMAIN_TYPE_CAR_ENCYCLOPEDIA = "domain_type_car_encyclopedia";
    //闲聊
    String DOMAIN_TYPE_GOSSIP = "domain_type_gossip";
    String DOMAIN_TYPE_FAQ = "domain_type_faq";
    String DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM = "domain_type_encyclopedia_not_stream";
    String DOMAIN_TYPE_FAQ_NOT_STREAM = "domain_type_faq_not_stream";
}
