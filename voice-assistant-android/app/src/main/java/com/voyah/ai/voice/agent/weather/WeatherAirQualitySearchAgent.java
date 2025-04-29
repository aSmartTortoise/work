package com.voyah.ai.voice.agent.weather;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.domains.weather.MultiDaysAqi;
import com.voyah.ds.common.entity.domains.weather.MultiHoursAqi;
import com.voyah.ds.common.entity.domains.weather.OneDayAqi;
import com.voyah.ds.common.entity.domains.weather.OneHourAqi;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/24 17:05
 * description :
 */
@ClassAgent
public class WeatherAirQualitySearchAgent extends AbstractWeatherSearchAgent<OneDayAqi,
        MultiDaysAqi, OneHourAqi, MultiHoursAqi> {

    private static final String TAG = "WeatherAirQualityAgent";

    @Override
    public String AgentName() {
        return "weather_air_quality#search";
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
            OneDayAqi oneDayWeather) {
        LogUtils.d(TAG, "getDayAgentResponse");
        String dateFormat = getDateFormat(oneDayWeather.date);

        TTSBean ttsBean = getTTSTextNew("7001050", location, dateFormat, oneDayWeather.aqi);
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
            MultiDaysAqi multiDaysWeather) {
        TTSBean ttsBean = getMultiDayAQITTSContent(location, multiDaysWeather.dayAqiList, "7001052");
        LogUtils.d(TAG, "getDayRangeAgentResponse tts:" + ttsBean.getSelectTTs());

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
            OneHourAqi timeWeather) {
        StringBuilder sbAqi = new StringBuilder();
        sbAqi.append(location);

        String time = getParamKey(paramsMap, Constant.SLOT_NAME_TIME, 0);
        String timeFormat = getTimeFormat(timeWeather.date);

        TTSBean ttsBean = getTTSTextNew("7001051", location, timeFormat, timeWeather.aqi);
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
            MultiHoursAqi timeRangeWeather) {
        List<OneHourAqi> hourAqiList = timeRangeWeather.hourAqiList;
        if (hourAqiList == null || hourAqiList.isEmpty()) {
            LogUtils.w(TAG, "getTimeRangeAgentResponse ds response error.");
            return  new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ""
            );
        }

        TTSBean ttsBean = getMultiHourAQITTSContent(location, timeRangeWeather.hourAqiList, "7001053");
        LogUtils.d(TAG, "getTimeRangeAgentResponse tts:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    private String getTTSText(
            String date,
            String aqi) {

        if (date == null) {
            date = "";
        }
        if (aqi == null) {
            aqi = "";
        }
        String ttsText = DeviceHolder.INS().getDevices().getWeather().getTipAQISearch(date, aqi);
        return ttsText;
    }

    private TTSBean getTTSTextNew(
            String ttsId,
            String location,
            String date,
            String aqi) {
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (aqi == null) {
            aqi = "";
        }

        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                ttsId,
                location,
                date,
                aqi
        );
//        @{location}@{date}，空气质量@{wx_aqi}
        return ttsBean;
    }

    private TTSBean getMultiDayAQITTSContent(String location,
                                          List<OneDayAqi> dayWeathers,
                                          String ttsId) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String originalContent = ttsBean.getSelectTTs();
        String ttsContent = originalContent;
        if (location == null) {
            location = "";
        }
        if (!StringUtil.isEmpty(originalContent)) {
            if (originalContent.contains("@{location}")) {
                ttsContent = originalContent.replace("@{location}", location);
                LogUtils.d(TAG, "getMultiDayTTSContent ttsContent:" + ttsContent);
            }

            int indexStart = ttsContent.indexOf('[');
            int indexEnd = ttsContent.indexOf(']');
            if (indexStart > 0 && indexEnd > indexStart) {
                String loopFormat = ttsContent.substring(indexStart + 1, indexEnd);
                ttsContent = ttsContent.replace(loopFormat, "%s");
                ttsContent = ttsContent.replace("[", "");
                ttsContent = ttsContent.replace("]", "");

                loopFormat = loopFormat.replaceAll("\\@\\{(.*?)\\}", "%s");
                LogUtils.d(TAG, "getMultiDayTTSContent loopFormat:" + loopFormat);
//                    @{location}，[@{date}，白天@{wx_wind_day}@{wind_level_day}级,夜间@{wx_wind_night}@{wind_level_night}级，]。
                String loopContent = getLoopContentDayAQI(dayWeathers, loopFormat);
                ttsContent = String.format(Locale.getDefault(), ttsContent, loopContent);
            }
        }
        ttsBean.setSelectTTs(ttsContent);
        return ttsBean;
    }

    private TTSBean getMultiHourAQITTSContent(String location,
                                           List<OneHourAqi> hourWeathers,
                                           String ttsId) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String originalContent = ttsBean.getSelectTTs();
        String ttsContent = originalContent;
        if (location == null) {
            location = "";
        }
        if (!StringUtil.isEmpty(originalContent)) {
            if (originalContent.contains("@{location}")) {
                ttsContent = originalContent.replace("@{location}", location);
                LogUtils.d(TAG, "getMultiHourTTSContent ttsContent:" + ttsContent);
            }

            int indexStart = ttsContent.indexOf('[');
            int indexEnd = ttsContent.indexOf(']');
            if (indexStart > 0 && indexEnd > indexStart) {
                String loopFormat = ttsContent.substring(indexStart + 1, indexEnd);
                ttsContent = ttsContent.replace(loopFormat, "%s");
                ttsContent = ttsContent.replace("[", "");
                ttsContent = ttsContent.replace("]", "");

                loopFormat = loopFormat.replaceAll("\\@\\{(.*?)\\}", "%s");
                LogUtils.d(TAG, "getMultiHourTTSContent loopFormat:" + loopFormat);

                String loopContent = getLoopContentHourAQI(hourWeathers, loopFormat);
                ttsContent = String.format(Locale.getDefault(), ttsContent, loopContent);
            }
        }
        ttsBean.setSelectTTs(ttsContent);
        return ttsBean;
    }

    private String getLoopContentDayAQI(List<OneDayAqi> dayWeathers, String loopFormat) {
        StringBuilder windTTSSb = new StringBuilder();
        for (OneDayAqi dayWeather: dayWeathers) {
            String date = dayWeather.date;
            String dateFormat = getDateFormat(date);
//            @{location}，[@{date}，空气质量@{wx_aqi}，]。
            String weatherDateDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    dateFormat,
                    dayWeather.aqi
            );
            LogUtils.d(TAG, "getLoopContentDay weatherDateDesc:" + weatherDateDesc);
            windTTSSb.append(weatherDateDesc);
        }
        return windTTSSb.toString();
    }

    private String getLoopContentHourAQI(List<OneHourAqi> hourWeathers, String loopFormat) {
        StringBuilder windTTSSb = new StringBuilder();
        for (OneHourAqi hourWeather: hourWeathers) {
            String date = hourWeather.date;
            String timeFormat = getTimeFormat(date);
            //    @{location}，[@{time}，空气质量@{wx_aqi_now}，]
            String weatherHourDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    timeFormat,
                    hourWeather.aqi
            );
            LogUtils.d(TAG, "getLoopContentHour weatherHourDesc:" + weatherHourDesc);
            windTTSSb.append(weatherHourDesc);
        }
        return windTTSSb.toString();
    }


}
