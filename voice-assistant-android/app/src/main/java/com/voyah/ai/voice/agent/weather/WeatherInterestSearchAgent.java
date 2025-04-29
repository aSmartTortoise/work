package com.voyah.ai.voice.agent.weather;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;
import com.voyah.ds.common.entity.domains.weather.WeatherInterest;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/23 16:44
 * description : 查询指定地点、指定日期（日期段、时间、时间段）的兴趣点。
 */
@ClassAgent
public class WeatherInterestSearchAgent extends AbstractWeatherSearchAgent<OneDayWeather,
        MultiDaysWeather, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherInterestAgent";

    @Override
    public String AgentName() {
        return "weather_interest#search";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        return super.executeAgent(flowContext, paramsMap);
    }


    @Override
    protected ClientAgentResponse getDayAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            OneDayWeather oneDayWeather) {
        LogUtils.d(TAG, "getDayAgentResponse location:" + location + " ds location:" + oneDayWeather.location);
        LogUtils.d(TAG, "getDayAgentResponse locationDetail:" + oneDayWeather.locationDetail);
        String date = getParamKey(paramsMap, Constant.SLOT_NAME_DATE, 0);
        String dateFormat = getDateFormat(oneDayWeather.date);

        StringBuilder interestTTSSb = new StringBuilder();
        List<WeatherInterest> interestList = oneDayWeather.interestList;
        if (interestList == null || interestList.isEmpty()) {
            return null;
        }

        for (WeatherInterest interest: interestList) {
            interestTTSSb.append(interest.hit ? "有" : "没有").append(interest.interestType);
        }

        TTSBean ttsBean = getTTSTextDate(
                location,
                dateFormat,
                interestTTSSb.toString(),
                oneDayWeather.weatherDay,
                oneDayWeather.weatherNight
        );
        LogUtils.d(TAG, "getDayAgentResponse ttsText:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    @Override
    protected ClientAgentResponse getDayRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiDaysWeather multiDaysWeather) {
        String dateJson = getParamKey(paramsMap,
                Constant.SLOT_NAME_DATE_RANGE, 0);

        String dateFormat = getDayRangeFormat(multiDaysWeather);

        StringBuilder interestTTSSb = new StringBuilder();
        List<WeatherInterest> interestList = multiDaysWeather.interestList;
        if (interestList == null || interestList.isEmpty()) {
            return null;
        }

        for (WeatherInterest interest: interestList) {
            LogUtils.d(TAG, "getDayRangeResponse interestType:" + interest.interestType);
            interestTTSSb.append(interest.hit ? "有" : "没有").append(interest.interestType);
        }

        LogUtils.d(TAG, "getDayRangeAgentResponse mainWeatherDesc:" + multiDaysWeather.mainWeatherDesc);

        String dateStartFormat = getDateStartFormat(multiDaysWeather);
        String dateEndFormat = getDateEndFormat(multiDaysWeather);

        TTSBean ttsBean = getTTSTextDateRange(
                "7001044",
                location,
                dateStartFormat,
                dateEndFormat,
                interestTTSSb.toString(),
                multiDaysWeather.mainWeatherDesc
        );
        LogUtils.d(TAG, "getDayRangeAgentResponse ttsText:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    @Override
    protected ClientAgentResponse getTimeAgentResponse(
            Map<String, Object> flowContext, Map<String,
            List<Object>> paramsMap,
            String location,
            OneHourWeather timeWeather) {

        String timeStr = getParamKey(paramsMap, Constant.SLOT_NAME_TIME, 0);
        String timeFormat = getTimeFormat(timeWeather.date);

        StringBuilder interestTTSSb = new StringBuilder();
        List<WeatherInterest> interestList = timeWeather.interestList;
        if (interestList == null || interestList.isEmpty()) {
            return null;
        }

        for (WeatherInterest interest: interestList) {
            interestTTSSb.append(interest.hit ? "有" : "没有").append(interest.interestType);
        }

        TTSBean ttsBean = getTTSTextTime(
                location,
                timeFormat,
                interestTTSSb.toString(),
                timeWeather.weatherDesc
        );
        LogUtils.d(TAG, "getTimeAgentResponse ttsText:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    @Override
    protected ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiHoursWeather timeRangeWeather) {
        String timeRangeJson = getParamKey(paramsMap,
                Constant.SLOT_NAME_TIME_RANGE, 0);
        String timeFormat = getTimeRangeTimeFormat(timeRangeWeather);

        StringBuilder interestTTSSb = new StringBuilder();
        List<WeatherInterest> interestList = timeRangeWeather.interestList;
        if (interestList == null || interestList.isEmpty()) {
            return null;
        }

        for (WeatherInterest interest: interestList) {
            interestTTSSb.append(interest.hit ? "有" : "没有").append(interest.interestType);
        }

        String timeStartFormat = getTimeStartFormat(timeRangeWeather);
        String timeEndFormat = getTimeEndFormat(timeRangeWeather);

        TTSBean ttsBean = getTTSTextDateRange(
                "7001045",
                location,
                timeStartFormat,
                timeEndFormat,
                interestTTSSb.toString(),
                timeRangeWeather.mainWeatherDesc
        );
        LogUtils.d(TAG, "getTimeRangeAgentResponse ttsText:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    private TTSBean getTTSTextDate(String location,
                                   String date,
                                   String interest,
                                   String weatherDay,
                                   String weatherNight) {
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (interest == null) {
            interest = "";
        }
        if (weatherDay == null) {
            weatherDay = "";
        }
        if (weatherNight == null) {
            weatherNight = "";
        }
//        @{location}@{date}，@{wx_condition}，白天天气@{wx_day}，夜间天气@{wx_night}。
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001042",
                location,
                date,
                interest,
                weatherDay,
                weatherNight
        );

        return ttsBean;
    }

    private TTSBean getTTSTextDateRange(String ttsId,
            String location,
                              String dateStart,
                              String dateEnd,
                              String interest,
                              String weather) {
        if (location == null) {
            location = "";
        }
        if (dateStart == null) {
            dateStart = "";
        }
        if (dateEnd == null) {
            dateEnd = "";
        }
        if (interest == null) {
            interest = "";
        }
        if (weather == null) {
            weather = "";
        }
//        @{location}@{date_start}至@{date_end}，@{wx_condition}，天气@{wx_main}。
//        @{location}@{time_start}至@{time_end}，@{wx_condition}，天气@{wx_main}。
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                ttsId,
                location,
                dateStart,
                dateEnd,
                interest,
                weather);

        return ttsBean;
    }

    private TTSBean getTTSTextTime(String location,
                                   String time,
                                   String interest,
                                   String weather) {
        if (location == null) {
            location = "";
        }
        if (time == null) {
            time = "";
        }
        if (interest == null) {
            interest = "";
        }
        if (weather == null) {
            weather = "";
        }

//        @{location}@{time}，@{wx_condition}，天气@{wx_now}。
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001043",
                location,
                time,
                interest,
                weather
        );
        return ttsBean;
    }
}
