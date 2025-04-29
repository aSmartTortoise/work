package com.voyah.ai.voice.agent.weather;


import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/21 20:18
 * description : 查询指定地点、指定日期（日期段、时间、时间段）的温差。
 */
@ClassAgent
public class WeatherTemDiffSearchAgent extends AbstractWeatherSearchAgent<OneDayWeather,
        MultiDaysWeather, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherTemDiffAgent";

    @Override
    public String AgentName() {
        return "weather_temDiff#search";
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
        String date = getParamKey(paramsMap, Constant.SLOT_NAME_DATE, 0);
        LogUtils.d(TAG, "getDayAgentResponse nlu date:" + date);
        String dateFormat = "";
        if (!StringUtil.isEmpty(oneDayWeather.date)) {
            dateFormat = getDateFormat(oneDayWeather.date);
        }
        TTSBean ttsBean = getTTSText(location, dateFormat,
                oneDayWeather.tempLow, oneDayWeather.tempHigh);
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
        LogUtils.d(TAG, "getDayRangeAgentResponse nlu dateJson:" + dateJson);
        String dateFormat = getDayRangeFormat(multiDaysWeather);
        String dateStartFormat = getDateStartFormat(multiDaysWeather);
        String dateEndFormat = getDateEndFormat(multiDaysWeather);
        TTSBean ttsBean = getTTSText(
                "7001040",
                location,
                dateStartFormat,
                dateEndFormat,
                multiDaysWeather.tempLow,
                multiDaysWeather.tempHigh);

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
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            OneHourWeather timeWeather) {

        // todo 某时温差需要兜底回复，因为不存在某时温差的概念。
        LogUtils.d(TAG, "getTimeAgentResponse tts:");
        return null;
    }

    @Override
    protected ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiHoursWeather timeRangeWeather) {
        String timeRangeJson = getParamKey(paramsMap,
                Constant.SLOT_NAME_TIME_RANGE, 0);

        String timeStartFormat = getTimeStartFormat(timeRangeWeather);
        String timeEndFormat = getTimeEndFormat(timeRangeWeather);

        TTSBean ttsBean = getTTSText(
                "7001041",
                location, timeStartFormat, timeEndFormat,
                timeRangeWeather.tempLow, timeRangeWeather.tempHigh);
        LogUtils.d(TAG, "getTimeRangeAgentResponse tts:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );

        return response;
    }



    private TTSBean getTTSText(String location,
                              String date,
                              Integer tempLow,
                              Integer tempHigh) {
        LogUtils.d(TAG, "getTTSText date:" + date + " location:" + location);
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        String tempLowString = "";
        if (tempLow != null) {
            tempLowString = tempLow + "°C";
        }
        String tempHighString = "";
        if (tempHigh != null) {
            tempHighString = tempHigh + "°C";
        }
        String tempDiffString = "";
        if (tempLow != null && tempHigh != null) {
            tempDiffString = (tempHigh - tempLow) + "°C";
        }

        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001039",
                location,
                date,
                tempDiffString,
                tempLowString,
                tempHighString
        );
//        @{location}@{date}，温差@{wx_temp_difference}，最低温度@{wx_temp_low}，最高温度@{wx_temp_high}

        return ttsBean;
    }

    private TTSBean getTTSText(
            String ttsId,
            String location,
            String dateStart,
            String dateEnd,
            Integer tempLow,
            Integer tempHigh) {
        LogUtils.d(TAG, "getTTSText dateStart:" + dateStart + " dateEnd:" + dateEnd + " location:" + location);
        if (location == null) {
            location = "";
        }
        if (dateStart == null) {
            dateStart = "";
        }
        if (dateEnd == null) {
            dateEnd = "";
        }
        String tempLowString = "";
        if (tempLow != null) {
            tempLowString = tempLow + "°C";
        }
        String tempHighString = "";
        if (tempHigh != null) {
            tempHighString = tempHigh + "°C";
        }
        String tempDiffString = "";
        if (tempLow != null && tempHigh != null) {
            tempDiffString = (tempHigh - tempLow) + "°C";
        }

        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                ttsId,
                location,
                dateStart,
                dateEnd,
                tempDiffString,
                tempLowString,
                tempHighString
        );
//        @{location}@{date_start}至@{date_end}，温差@{wx_temp_difference}，最低温度@{wx_temp_low}，最高温度@{wx_temp_high}

        return ttsBean;
    }
}
