package com.voice.sdk.device;


import com.voice.sdk.device.ui.UICardInterface;
import com.voyah.ds.common.entity.IData;

/**
 * author : jie wang
 * date : 2024/4/10 19:23
 * description : 天气模块意图接口
 */
public interface IWeather extends DomainInterface, UICardInterface {


    String getDefaultTip();

    String getTipAQISearch(Object... textArr);

    String getTipSunRiseDownDaySearch(Object... textArr);

    String getTipWindSearch(Object... textArr);

    String getTipWindTimeRangeSearch(Object... textArr);

    void constructCardInfo(IData data, String requestId);

    String getDateFormat(String date);

    String getTimeFormat(String time, int referenceDay);

    String getTimeFormat(String time);

    String getHourMinuteFormat(String time);

    long getTimeStamp(String time);

    int getDay(long timeStamp);
}
