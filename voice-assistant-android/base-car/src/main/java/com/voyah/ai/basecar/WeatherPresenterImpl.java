package com.voyah.ai.basecar;


import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ResourceUtils;
import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.IWeather;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.model.CPEntity;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.window.util.DateUtil;
import com.voyah.ds.common.entity.IData;

import org.json.JSONArray;

import java.util.Locale;

/**
 * author : jie wang
 * date : 2024/4/10 19:23
 * description : 天气模块意图接口的实现类
 */
public class WeatherPresenterImpl extends BaseAppPresenter implements IWeather {

    private static final String TAG = "WeatherPresenterImpl";

    public static WeatherPresenterImpl getInstance() {
        return Holder.INSTANCE;
    }

    private JSONArray mWeatherErrorArray;

    private CPEntity mCPEntity;

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        initSdk();
    }

    @Override
    public boolean isAppForeground() {
        return false;
    }

    public JSONArray getWeatherSearchErrorConfig() {
        if (mWeatherErrorArray == null) {
            try {
                String json = ResourceUtils.readAssets2String("tips/weather_search_error.json");
                mWeatherErrorArray = GsonUtils.parseToJSONOArray(json);
            } catch (Exception e) {
                LogUtils.d(TAG, "getWeatherSearchErrorConfig e:" + e);
            }
        }

        return mWeatherErrorArray;
    }

    @Override
    public String getDefaultTip() {
        return mContext.getString(R.string.tts_weather_query_no_data);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public String getTipAQISearch(Object... textArr) {
        return mContext.getString(R.string.tts_weather_aqi_search, textArr);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public String getTipSunRiseDownDaySearch(Object... textArr) {
        return mContext.getString(R.string.tts_weather_sun_rise_down_day_search, textArr);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public String getTipWindSearch(Object... textArr) {
        return mContext.getString(R.string.tts_weather_wind_search, textArr);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public String getTipWindTimeRangeSearch(Object... textArr) {
        return mContext.getString(R.string.tts_weather_wind_time_range_search, textArr);
    }

    @Override
    public void constructCardInfo(IData data, String requestId) {
        mCPEntity = new CPEntity();
        mCPEntity.setData(data);
        mCPEntity.setRequestId(requestId);
    }

    @Override
    public boolean isCardInfoEmpty() {
        return mCPEntity == null;
    }

    @Override
    public void onShowUI(String business, int location) {
        if (!isCardInfoEmpty()) {
            UIMgr.INSTANCE.showCard(
                    UiConstant.CardType.WEATHER_CARD, mCPEntity, mCPEntity.getRequestId(), business, location);
            mCPEntity = null;
        }
    }

    @Override
    public String getDateFormat(String date) {
        long timeStamp = DateUtil.getMillis(date);
        int month = DateUtil.getMonth(timeStamp) + 1;
        int day = DateUtil.getDay(timeStamp);
        String dateFormat = String.format(Locale.CHINA, "%1$d月%2$d日", month, day);
        LogUtils.d(TAG, "getDateFormat month:" + month + " day:" + day + " dateFormat:" + dateFormat);
        return dateFormat;
    }

    @Override
    public String getTimeFormat(String time, int referenceDay) {
        long timeStamp = DateUtil.getTimeStamp(time);
        int month = DateUtil.getMonth(timeStamp) + 1;
        int day = DateUtil.getDay(timeStamp);
        int hour = DateUtil.getHour(timeStamp);
        int minute = DateUtil.getMinute(timeStamp);
        LogUtils.d(TAG, "getTimeFormatStr month:" + month + " day:" + day + " hour:" + hour
                + " minute:" + minute);
        String dateFormat;
        if (day == referenceDay) {
            dateFormat = hour + "点";
        } else {
            dateFormat = String.format(Locale.CHINA, "%1$d月%2$d日%3$d点", month, day, hour);
        }

        return dateFormat;
    }

    @Override
    public String getTimeFormat(String time) {
        long timeStamp = DateUtil.getTimeStamp(time);
        int month = DateUtil.getMonth(timeStamp) + 1;
        int day = DateUtil.getDay(timeStamp);
        int hour = DateUtil.getHour(timeStamp);
        int minute = DateUtil.getMinute(timeStamp);
        LogUtils.i(TAG, "getTimeFormat month:" + month + " day:" + day + " hour:" + hour
                + " minute:" + minute);
        String dateFormat = String.format(Locale.CHINA, "%1$d月%2$d日%3$d点", month, day, hour);
        LogUtils.i(TAG, "getTimeFormat dateFormat:" + dateFormat);
        return dateFormat;
    }

    @Override
    public String getHourMinuteFormat(String time) {
        long timeStamp = DateUtil.getTimeStamp(time);
        int hour = DateUtil.getHour(timeStamp);
        int minute = DateUtil.getMinute(timeStamp);
        String dateFormat = String.format(Locale.CHINA, "%1$d点%2$d分", hour, minute);
        LogUtils.i(TAG, "getHourMinuteFormat dateFormat:" + dateFormat);
        return dateFormat;
    }

    @Override
    public long getTimeStamp(String time) {
        return DateUtil.getTimeStamp(time);
    }

    @Override
    public int getDay(long timeStamp) {
        return DateUtil.getDay(timeStamp);
    }

    private static class Holder {
        private static WeatherPresenterImpl INSTANCE = new WeatherPresenterImpl();
    }


}
