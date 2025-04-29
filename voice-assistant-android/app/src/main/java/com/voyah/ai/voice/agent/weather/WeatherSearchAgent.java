package com.voyah.ai.voice.agent.weather;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.IWeather;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.IData;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/4/7 17:31
 * description : 查询指定地点、指定日期（日期段、时间、时间段）的天气。
 */
@ClassAgent
public class WeatherSearchAgent extends AbstractWeatherSearchAgent<OneDayWeather,
        MultiDaysWeather, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherSearchAgent";

    @Override
    public String AgentName() {
        return "weather#search";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        return super.executeAgent(flowContext, paramsMap);
    }


    @Override
    public void showUi(String uiType, int location) {
        LogUtils.d(TAG, "showUi uiType:" + uiType);
        if (CARD_TYPE_INFORMATION.equals(uiType)) {
            IWeather weatherInterface = DeviceHolder.INS().getDevices().getWeather();
            if (weatherInterface != null) {
                weatherInterface.onShowUI(mAgentIdentifier, location);
            }
        }
    }

    @Override
    public ClientAgentResponse getDayAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            OneDayWeather oneDayWeather) {
        constructCardInfo(flowContext, oneDayWeather);

        String weatherDay = oneDayWeather.weatherDay;
        String weatherNight = oneDayWeather.weatherNight;
        String dateFormat = "";
        if (!StringUtil.isEmpty(oneDayWeather.date)) {
            dateFormat = getDateFormat(oneDayWeather.date);
        }
        TTSBean ttsBean = getTTSText(location, dateFormat, weatherDay, weatherNight,
                oneDayWeather.tempLow, oneDayWeather.tempHigh, oneDayWeather.tips);

        LogUtils.d(TAG, "getDayAgentResponse ttsText:" + ttsBean.getSelectTTs());
        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );

        IWeather weatherInterface = DeviceHolder.INS().getDevices().getWeather();
        if (weatherInterface != null && !weatherInterface.isCardInfoEmpty()) {
            response.setInformationCard(true);
            response.setUiType(CARD_TYPE_INFORMATION);
        }

        return response;
    }

    @Override
    public ClientAgentResponse getDayRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiDaysWeather multiDaysWeather) {
        constructCardInfo(flowContext, multiDaysWeather);

        String mainWeatherDesc = multiDaysWeather.mainWeatherDesc;
        String dateJson = getParamKey(paramsMap,
                Constant.SLOT_NAME_DATE_RANGE, 0);
        LogUtils.d(TAG, "getDayRangeAgentResponse dateJson:" + dateJson);
        String dateStartFormat = getDateStartFormat(multiDaysWeather);
        String dateEndFormat = getDateEndFormat(multiDaysWeather);
        TTSBean ttsBean = getTTSText(
                "7001037",
                location, dateStartFormat, dateEndFormat, mainWeatherDesc,
                multiDaysWeather.tempLow, multiDaysWeather.tempHigh);
        LogUtils.d(TAG, "getDayRangeAgentResponse tts:" + ttsBean.getSelectTTs());
        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        response.setInformationCard(true);
        response.setUiType(CARD_TYPE_INFORMATION);
        return response;
    }

    @Override
    public ClientAgentResponse getTimeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            OneHourWeather timeWeather) {
        String timeStr = getParamKey(paramsMap, Constant.SLOT_NAME_TIME, 0);
        LogUtils.d(TAG, "getTimeAgentResponse timeStr:" + timeStr);
        LogUtils.d(TAG, "getTimeAgentResponse time:" + timeWeather.date);
        String timeFormat = getTimeFormat(timeWeather.date);
        String weatherDesc = timeWeather.weatherDesc;

        TTSBean ttsBean = getTTSText(location, timeFormat, weatherDesc, timeWeather.temp);
        LogUtils.d(TAG, "getTimeAgentResponse tts:" + ttsBean.getSelectTTs());
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
        LogUtils.d(TAG, "getTimeRangeAgentResponse timeRangeJson:" + timeRangeJson);
        String timeStartFormat = getTimeStartFormat(timeRangeWeather);
        String timeEndFormat = getTimeEndFormat(timeRangeWeather);

        TTSBean ttsBean = getTTSText(
                "7001038",
                location,
                timeStartFormat,
                timeEndFormat,
                timeRangeWeather.mainWeatherDesc,
                timeRangeWeather.tempLow,
                timeRangeWeather.tempHigh);

        LogUtils.d(TAG, "getTimeRangeAgentResponse tts:" + ttsBean.getSelectTTs());
        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );
        return response;
    }

    @Override
    public int getPriority() {
        return 2;
    }


    private TTSBean getTTSText(String location,
                              String time,
                              String weatherDesc,
                              Integer temp) {
        LogUtils.d(TAG, "getTTSText time:" + time + " location:" + location);

        if (location == null) {
            location = "";
        }
        if (time == null) {
            time = "";
        }
        if (weatherDesc == null) {
            weatherDesc = "";
        }

        String tempString = "";
        if (temp != null) {
            tempString = temp + "°C";
        }
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001036",
                location,
                time,
                weatherDesc,
                tempString
        );
//        @{location}@{time}，天气@{wx_now}，气温@{wx_temp_now}


        return ttsBean;
    }

    private TTSBean getTTSText(String ttsId, String location,
                               String dateStart,
                               String dateEnd,
                               String weatherDesc,
                               Integer tempLow,
                               Integer tempHigh) {

        if (location == null) {
            location = "";
        }
        if (dateStart == null) {
            dateStart = "";
        }
        if (dateEnd == null) {
            dateEnd = "";
        }
        if (weatherDesc == null) {
            weatherDesc = "";
        }

        String tempLowString = "";
        if (tempLow != null) {
            tempLowString = tempLow + "°C";
        }

        String tempHighString = "";
        if (tempHigh != null) {
            tempHighString = tempHigh + "°C";
        }


        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                ttsId,
                location,
                dateStart,
                dateEnd,
                weatherDesc,
                tempLowString,
                tempHighString
        );
        //        @{location}@{date_start}至@{date_end}，天气@{wx_main}，最低温度@{wx_temp_low}，最高温度@{wx_temp_high}

        return ttsBean;
    }

    private TTSBean getTTSText(String location,
                              String date,
                              String weatherDay,
                              String weatherNight,
                              Integer tempLow,
                              Integer tempHigh,
                              String weatherTip) {
        LogUtils.d(TAG, "getTTSText date:" + date + " location:" + location);
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (weatherTip == null) {
            weatherTip = "";
        }
        if (weatherDay == null) {
            weatherDay = "";
        }
        if (weatherNight == null) {
            weatherNight = "";
        }
        String tempLowString = "";
        if (tempLow != null) {
            tempLowString = tempLow + "°C";
        }
        String tempHighString = "";
        if (tempHigh != null) {
            tempHighString = tempHigh + "°C";
        }

        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001035",
                location,
                date,
                weatherDay,
                weatherNight,
                tempLowString,
                tempHighString,
                weatherTip);
//        "@{location}@{date}，白天天气@{wx_day}，夜间天气@{wx_night}，最低温度@{wx_temp_low}，最高温度@{wx_temp_high}，@{wx_tips}"

        return ttsBean;
    }

    private void constructCardInfo(Map<String, Object> flowContext, IData data) {
        String requestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        IWeather weatherInterface = DeviceHolder.INS().getDevices().getWeather();
        if (weatherInterface != null) {
            weatherInterface.constructCardInfo(data, requestId);
        }
    }
}
