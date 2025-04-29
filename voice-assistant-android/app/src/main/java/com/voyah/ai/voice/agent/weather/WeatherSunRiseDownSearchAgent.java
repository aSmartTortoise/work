package com.voyah.ai.voice.agent.weather;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/24 17:15
 * description :
 */

@ClassAgent
public class WeatherSunRiseDownSearchAgent extends AbstractWeatherSearchAgent<OneDayWeather,
        MultiDaysWeather, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherSunRiseDownAgent";

    @Override
    public String AgentName() {
        return "weather_sunRiseDown#search";
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
        String dateFormat = getDateFormat(oneDayWeather.date);
        String sunRiseTime = getHourMinuteFormat(oneDayWeather.sunRise);
        String sunDownTime = getHourMinuteFormat(oneDayWeather.sunDown);

        TTSBean ttsBean = getTTSContent(location, sunRiseTime, sunDownTime, dateFormat);
//        @{location}@{date}，@{wx_rise}日出，@{wx_set}日落

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
        TTSBean ttsBean = getMultiDayTTSContent(location, multiDaysWeather.dayWeathersList, "7001055");
        LogUtils.d(TAG, "getDayRangeAgentResponse tts:" + ttsBean.getSelectTTs());
        ClientAgentResponse response = new ClientAgentResponse(
                Constant.WeatherAgentResponseCode.WEATHER_SUN_RISE_DOWN_SEARCH_DATE_RANGE,
                flowContext,
                ttsBean
        );
        return response;
    }

    @Override
    protected ClientAgentResponse getTimeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            OneHourWeather timeWeather) {
        // 某时日出日落需要兜底回复，因为不存在某时日出日落的概念。
        return null;
    }

    @Override
    protected ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiHoursWeather timeRangeWeather) {
        // 某时段日出日落需要兜底回复，因为不存在某时段日出日落的概念。
        return null;
    }

    private String getTTSText(
                              String date,
                              String sunRise,
                              String sunDown) {

        if (date == null) {
            date = "";
        }
        if (sunRise == null) {
            sunRise = "";
        }
        if (sunDown == null) {
            sunDown = "";
        }
        String ttsText = DeviceHolder.INS().getDevices().getWeather()
                .getTipSunRiseDownDaySearch(date, sunRise, sunDown);
        return ttsText;
    }

    private TTSBean getTTSContent(
            String location,
            String sunRiseTime,
            String sunDownTime,
            String dateFormat) {
        if (location == null) {
            location = "";
        }

        if (sunRiseTime == null) {
            sunRiseTime = "";
        }
        if (sunDownTime == null) {
            sunDownTime = "";
        }
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001054",
                location,
                dateFormat,
                sunRiseTime,
                sunDownTime
        );
        return ttsBean;
    }

    @Override
    protected String getLoopContentDay(List<MultiDaysWeather.DayWeather> dayWeathers, String loopFormat) {
        StringBuilder windTTSSb = new StringBuilder();
        for (MultiDaysWeather.DayWeather dayWeather: dayWeathers) {
            String date = dayWeather.date;
            String dateFormat = getDateFormat(date);
//            @{location}，[@{date}，@{wx_rise}日出，@{wx_set}日落，]。
            String sunRiseFormat = getHourMinuteFormat(dayWeather.sunRise);
            String sunDownFormat = getHourMinuteFormat(dayWeather.sunDown);
            String weatherDateDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    dateFormat,
                    sunRiseFormat,
                    sunDownFormat
            );
            LogUtils.d(TAG, "getLoopContentDay weatherDateDesc:" + weatherDateDesc);
            windTTSSb.append(weatherDateDesc);
        }
        return windTTSSb.toString();
    }
}
