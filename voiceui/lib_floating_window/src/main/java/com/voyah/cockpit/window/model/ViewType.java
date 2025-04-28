package com.voyah.cockpit.window.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/15 11:30
 * description : 卡片ItemView类型。
 */
@IntDef({ViewType.WEATHER_TYPE_1,
        ViewType.WEATHER_TYPE_2,
        ViewType.WEATHER_TYPE_3,
        ViewType.BT_PHONE_TYPE,
        ViewType.SCHEDULE_TYPE_1,
        ViewType.SCHEDULE_TYPE_2,
        ViewType.SCHEDULE_TYPE_3,
        ViewType.SCHEDULE_TYPE_MORE,
        ViewType.MEDIA_TYPE,
        ViewType.STOCK_TYPE,
        ViewType.CHAT_TYPE_MAIN,
        ViewType.CHAT_TYPE_RECOMMEND})
@Retention(RetentionPolicy.SOURCE)
public @interface ViewType {

    /**
     * 某一天的天气item type
     */
    int WEATHER_TYPE_1 = 1;

    /**
     * 某几天的天气查询中，头部item type。
     */
    int WEATHER_TYPE_2 = 2;

    /**
     * 某几天的天气查询中，body item type。
     */
    int WEATHER_TYPE_3 = 3;
    int BT_PHONE_TYPE = 4;

    int SCHEDULE_TYPE_1 = 5;
    int SCHEDULE_TYPE_2 = 6;
    int SCHEDULE_TYPE_3 = 7;
    int SCHEDULE_TYPE_MORE = 8;

    int MEDIA_TYPE = 9;

    int STOCK_TYPE = 10;

    int CHAT_TYPE_MAIN = 11;

    int CHAT_TYPE_RECOMMEND = 12;

    int MUSIC_TYPE = 13;

}
