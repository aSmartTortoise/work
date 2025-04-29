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
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/23 16:53
 * description :
 */
@ClassAgent
public class WeatherWindSearchAgent extends AbstractWeatherSearchAgent<OneDayWeather,
        MultiDaysWeather, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherWindSearchAgent";
    @Override
    public String AgentName() {
        return "weather_wind#search";
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
        TTSBean ttsBean = getTTSText(
                location,
                dateFormat,
                oneDayWeather.windLevelDay,
                oneDayWeather.windDirDay,
                oneDayWeather.windLevelNight,
                oneDayWeather.windDirNight);
        LogUtils.d(TAG, "getDayAgentResponse tts:" + ttsBean.getSelectTTs());
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

        TTSBean ttsBean = getMultiDayTTSContent(location, multiDaysWeather.dayWeathersList, "7001048");
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
            OneHourWeather timeWeather) {
        String timeFormat = getTimeFormat(timeWeather.date);
        TTSBean ttsBean = getTTSText(
                location,
                timeFormat,
                timeWeather.windLevel,
                timeWeather.windDir
        );
        LogUtils.d(TAG, "getTimeAgentResponse ttsText:" + ttsBean.getSelectTTs());

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    //todo wyj 依赖ttsid
    @Override
    protected ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiHoursWeather timeRangeWeather) {
        TTSBean ttsBean = getMultiHourTTSContent(location, timeRangeWeather.hourWeathersList, "7001049");
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
                              String windLevelDay,
                              String windDirDay,
                              String windLevelNight,
                              String windDirNight) {
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (windLevelDay == null) {
            windLevelDay = "";
        }
        if (windDirDay == null) {
            windDirDay = "";
        }
        if (windLevelNight == null) {
            windLevelNight = "";
        }
        if (windDirNight == null) {
            windDirNight = "";
        }

        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001046",
                location,
                date,
                windDirDay,
                windLevelDay,
                windDirNight,
                windLevelNight
        );

//        @{location}@{date}，白天@{wx_wind_day}@{wind_level_day}级，夜间@{wx_wind_night}@{wind_level_night}级

        return ttsBean;
    }

    private TTSBean getTTSText(String location,
                              String date,
                              String windLevel,
                              String windDir) {
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (windDir == null) {
            windDir = "";
        }
        if (windLevel == null) {
            windLevel = "";
        }
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001047",
                location,
                date,
                windDir,
                windLevel
        );

//            @{location}@{date}@{time}，@{wx_wind_now}@{wind_level_now}级

        return ttsBean;
    }

    private String getTTSText(
            String date,
            String windLevelDay,
            String windDirDay,
            String windLevelNight,
            String windDirNight) {
        if (date == null) {
            date = "";
        }
        if (windLevelDay == null) {
            windLevelDay = "";
        }
        if (windDirDay == null) {
            windDirDay = "";
        }
        if (windLevelNight == null) {
            windLevelNight = "";
        }
        if (windDirNight == null) {
            windDirNight = "";
        }
        String ttsText = DeviceHolder.INS().getDevices().getWeather().getTipWindSearch(
                date,
                windDirDay,
                windLevelDay,
                windDirNight,
                windLevelNight
        );
        return ttsText;
    }

    private String getTTSText(
            String date,
            String windLevel,
            String windDir) {

        if (date == null) {
            date = "";
        }
        if (windLevel == null) {
            windLevel = "";
        }
        if (windDir == null) {
            windDir = "";
        }
        String ttsText = DeviceHolder.INS().getDevices().getWeather()
                .getTipWindTimeRangeSearch(
                        date,
                        windDir,
                        windLevel);
        return ttsText;
    }

    @Override
    protected TTSBean getMultiDayTTSContent(String location,
                                          List<MultiDaysWeather.DayWeather> dayWeathers,
                                          String ttsId) {
        return super.getMultiDayTTSContent(location, dayWeathers, ttsId);
    }

    @Override
    protected TTSBean getMultiHourTTSContent(String location,
                                           List<MultiHoursWeather.HourWeather> hourWeathers,
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

                String loopContent = getLoopContentHour(hourWeathers, loopFormat);
                ttsContent = String.format(Locale.getDefault(), ttsContent, loopContent);
            }
        }
        ttsBean.setSelectTTs(ttsContent);
        return ttsBean;
    }

    @Override
    protected String getLoopContentDay(List<MultiDaysWeather.DayWeather> dayWeathers, String loopFormat) {
        StringBuilder windTTSSb = new StringBuilder();
        for (MultiDaysWeather.DayWeather dayWeather: dayWeathers) {
            String date = dayWeather.date;
            String dateFormat = getDateFormat(date);
            String weatherDateDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    dateFormat,
                    dayWeather.windDirDay,
                    dayWeather.windLevelDay,
                    dayWeather.windDirNight,
                    dayWeather.windLevelNight
            );
            LogUtils.d(TAG, "getLoopContentDay weatherDateDesc:" + weatherDateDesc);
            windTTSSb.append(weatherDateDesc);
        }
        return windTTSSb.toString();
    }

    @Override
    protected String getLoopContentHour(List<MultiHoursWeather.HourWeather> hourWeathers, String loopFormat) {
        StringBuilder windTTSSb = new StringBuilder();
        for (MultiHoursWeather.HourWeather hourWeather: hourWeathers) {
            String date = hourWeather.date;
            String timeFormat = getTimeFormat(date);
            //                    @{location}，[@{time}，@{wind_dir_current}@{wind_level_now}级，]。
            String weatherHourDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    timeFormat,
                    hourWeather.windDir,
                    hourWeather.windLevel
            );
            LogUtils.d(TAG, "getLoopContentHour weatherHourDesc:" + weatherHourDesc);
            windTTSSb.append(weatherHourDesc);
        }
        return windTTSSb.toString();
    }

}
