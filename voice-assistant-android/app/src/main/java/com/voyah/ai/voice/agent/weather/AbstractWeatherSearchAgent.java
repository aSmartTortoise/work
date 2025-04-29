package com.voyah.ai.voice.agent.weather;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.IWeather;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.weather.Location;
import com.voyah.ds.common.entity.domains.weather.MultiDaysAqi;
import com.voyah.ds.common.entity.domains.weather.MultiDaysIndexInfo;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursAqi;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayAqi;
import com.voyah.ds.common.entity.domains.weather.OneDayIndexInfo;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourAqi;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;
import com.voyah.ds.common.entity.domains.weather.WeatherType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/5/23 9:44
 * description :
 */
public abstract class AbstractWeatherSearchAgent<D, DR, T, TR> extends BaseAgentX {

    private static final String TAG = "AbstractWeatherSearchAgent";

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {
        ClientAgentResponse response = null;
        Object checkFailedObject = flowContext.get(FlowContextKey.FC_ONLINE_WEATHER_CHECK_FAILED);
        if (checkFailedObject instanceof String) {
            String errorCodeTarget = (String) checkFailedObject;
            LogUtils.d(TAG, "executeAgent errorCodeTarget:" + errorCodeTarget);
            response = getWeatherSearchErrorResponseNew(flowContext, errorCodeTarget);
            return response;
        }
        Object weatherTypeObj = flowContext.get(FlowContextKey.FC_WEATHER_TYPE);
        if (weatherTypeObj instanceof Integer) {
            int weatherType = (Integer) weatherTypeObj;
            LogUtils.d(TAG, "executeAgent weatherType:" + weatherType);

            String locationJson = getParamKey(paramsMap,
                    Constant.SLOT_NAME_LOCATION, 0);
            LogUtils.d(TAG, "executeAgent locationJson:" + locationJson);
            String location = formatLocationJson(locationJson);

            switch (weatherType) {
                case WeatherType.ONE_DAY_WEATHER:
                    Object oneDayWeatherObj = flowContext.get(FlowContextKey.FC_ONE_DAY_WEATHER);
                    location = getLocation(location, oneDayWeatherObj);
                    if ((oneDayWeatherObj instanceof OneDayWeather)
                            || (oneDayWeatherObj instanceof OneDayAqi)
                            || (oneDayWeatherObj instanceof OneDayIndexInfo)) {
                        D oneDayWeather = (D) oneDayWeatherObj;

                        response = getDayAgentResponse(flowContext,
                                paramsMap,
                                location,
                                oneDayWeather);
                    }
                    break;
                case WeatherType.MULTI_DAYS_WEATHER:
                    Object multiDaysWeatherObj = flowContext.get(FlowContextKey.FC_MULTI_DAYS_WEATHER);
                    location = getLocation(location, multiDaysWeatherObj);
                    if ((multiDaysWeatherObj instanceof MultiDaysWeather)
                            || (multiDaysWeatherObj instanceof MultiDaysAqi)
                            || (multiDaysWeatherObj instanceof MultiDaysIndexInfo)) {
                        DR multiDaysWeather = (DR) multiDaysWeatherObj;
                        response = getDayRangeAgentResponse(flowContext,
                                paramsMap,
                                location,
                                multiDaysWeather);
                    }
                    break;

                case WeatherType.TIME_WEATHER:
                    Object timeWeatherObj = flowContext.get(FlowContextKey.FC_ONE_HOUR_WEATHER);
                    location = getLocation(location, timeWeatherObj);
                    if ((timeWeatherObj instanceof OneHourWeather)
                            || (timeWeatherObj instanceof OneHourAqi)) {
                        T timeWeather = (T) timeWeatherObj;
                        response = getTimeAgentResponse(flowContext, paramsMap, location, timeWeather);
                    }
                    break;
                case WeatherType.TIME_RANGE_WEATHER:
                    Object timeRangeWeatherObj = flowContext.get(FlowContextKey.FC_MULTI_HOURS_WEATHER);
                    location = getLocation(location, timeRangeWeatherObj);
                    if ((timeRangeWeatherObj instanceof MultiHoursWeather)
                            || (timeRangeWeatherObj instanceof MultiHoursAqi)) {
                        TR timeRangeWeather = (TR) timeRangeWeatherObj;
                        response = getTimeRangeAgentResponse(flowContext,
                                paramsMap,
                                location,
                                timeRangeWeather);

                    }

                    break;
                default:
                    break;
            }
        }

        if (response == null) {
            String ttsText = DeviceHolder.INS().getDevices().getWeather().getDefaultTip();
            if (!StringUtil.isEmpty(ttsText)) {
                response = new ClientAgentResponse(
                        Constant.WeatherAgentResponseCode.WEATHER_SEARCH_ERROR,
                        flowContext,
                        ttsText);
            }
        }

        return response;
    }

    private ClientAgentResponse getWeatherSearchErrorResponseNew(
            Map<String, Object> flowContext,
            String errorCode) {
        ClientAgentResponse response = null;
        LogUtils.d(TAG, "getWeatherSearchErrorResponseNew errorCode:" + errorCode);
        if (!StringUtil.isEmpty(errorCode)) {
            String ttsId = null;
            switch (errorCode) {
                case "10000":
                    ttsId = "1100009";
                    break;
                case "10001":// 位置信息不能为空!
                    ttsId = "7001023";
                    break;
                case "10002": // 不支持该天气位置查询
                    ttsId = "7001025";
                    break;
                case "10010":
                    ttsId = "7001027";
                    break;
                case "10011":
                    ttsId = "7001026";
                    break;
                case "10012":
                    ttsId = "7001030";
                    break;
                case "10013":
                    ttsId = "7001029";
                    break;
                case "10014":
                    ttsId = "7001028";
                    break;
                case "10015":
                    ttsId = "7001058";
                    break;
                case "10016":
                    ttsId = "1100006";
                    break;
                default:
                    break;
            }
            if (ttsId != null) {
                TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
                if ("7001025".equals(ttsId)) {
                    Object locationObject = flowContext.get(FlowContextKey.FC_WEATHER_INVALID_LOCATION);
                    LogUtils.d(TAG, "getWeatherSearchErrorResponseNew locationObject:" + locationObject);
                    String location = "";
                    if (locationObject instanceof String) {
                        location = (String) locationObject;
                    }
                    ttsBean = TtsBeanUtils.getTtsBean(ttsBean, "@{location}", location);
                }
                response = new ClientAgentResponse(
                        Constant.CommonResponseCode.SUCCESS,
                        flowContext,
                        ttsBean);
            }
        }

        if (response == null) {
            String ttsText = DeviceHolder.INS().getDevices().getWeather()
                    .getDefaultTip();
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ttsText);
        }

        return response;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private String formatLocationJson(String locationJson) {
        String location;
        if (locationJson == null) {
            return null;
        }
        try {
            JSONObject locationEntity = new JSONObject(locationJson);
            StringBuilder locationSB = new StringBuilder();
            if (locationEntity.has("province")) {
                String province = locationEntity.getString("province");
                if (!StringUtil.isEmpty(province)) {
                    locationSB.append(province);
                }
            }

            if (locationEntity.has("city")) {
                String city = locationEntity.getString("city");
                if (!StringUtil.isEmpty(city)) {
                    locationSB.append(city);
                }
            }

            if (locationEntity.has("district")) {
                String district = locationEntity.getString("district");
                if (!StringUtil.isEmpty(district)) {
                    locationSB.append(district);
                }
            }

            if (locationEntity.has("poi")) {
                String poi = locationEntity.getString("poi");
                if (!StringUtil.isEmpty(poi)) {
                    locationSB.append(poi);
                }
            }

            location = locationSB.toString();
        } catch (JSONException e) {
            LogUtils.w(TAG, "formatLocationJson parse location error e:" + e);
            location = locationJson;
        }
        return location;
    }

    protected abstract ClientAgentResponse getDayAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            D oneDayWeather);

    protected abstract ClientAgentResponse getDayRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            DR multiDaysWeather);

    protected abstract ClientAgentResponse getTimeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            T timeWeather);

    protected abstract ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            TR timeRangeWeather);

    /**
     * 将date或者time格式化成xx月xx日
     * @param date
     * @return
     */
    @NonNull
    protected String getDateFormat(String date) {
        return DeviceHolder.INS().getDevices().getWeather().getDateFormat(date);
    }

    /**
     * @param dateJson llm定义的日期date_range {
     *         "start": "2024-05-11",
     *         "end": "2024-05-12"
     * }
     * @return 格式化后的日期 xx月xx日至xx月xx日
     */
    @Nullable
    protected String getDateRangeFormat(String dateJson) {
        String dateFormat = null;
        try {
            JSONObject dateJsonObject = new JSONObject(dateJson);
            StringBuilder dateSb = new StringBuilder();
            if (dateJsonObject.has("start")) {
                String startDate = dateJsonObject.getString("start");
                String startDateFormat = getDateFormat(startDate);
                if (!StringUtil.isEmpty(startDateFormat)) {
                    dateSb.append(startDateFormat);
                }
            }

            if (dateJsonObject.has("end")) {
                String endDate = dateJsonObject.getString("end");
                String endDateFormat = getDateFormat(endDate);
                if (!StringUtil.isEmpty(endDateFormat)) {
                    dateSb.append("至").append(endDateFormat);
                }
            }
            dateFormat = dateSb.toString();
        } catch (JSONException e) {
            LogUtils.w(TAG, "getDateRangeFormat format date range error:" + e);
        }
        return dateFormat;
    }

    protected String getTimeFormat(String time, int referenceDay) {
        return DeviceHolder.INS().getDevices().getWeather().getTimeFormat(time, referenceDay);
    }

    /**
     * @param time 时间，格式为  "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的time xx月xx日xx点
     */
    protected String getTimeFormat(String time) {
        return DeviceHolder.INS().getDevices().getWeather().getTimeFormat(time);
    }

    /**
     *
     * @param time 时间，格式为  "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的time xx点xx分
     */
    protected String getHourMinuteFormat(String time) {
        return DeviceHolder.INS().getDevices().getWeather().getHourMinuteFormat(time);
    }

    @Nullable
    protected String getTimeRangeFormat(String timeRangeJson) {
        String timeFormat = null;

        try {
            JSONObject timeRangeJsonObject = new JSONObject(timeRangeJson);
            StringBuilder timeSb = new StringBuilder();
            if (timeRangeJsonObject.has("start")) {
                String startTime = timeRangeJsonObject.getString("start");
                String timeFormatStart = getTimeFormat(startTime, 0);
                if (!StringUtil.isEmpty(timeFormatStart)) {
                    timeSb.append(timeFormatStart);
                }
                IWeather weather = DeviceHolder.INS().getDevices().getWeather();
                long timeStamp = weather.getTimeStamp(startTime);
                int referenceDay = weather.getDay(timeStamp);
                if (timeRangeJsonObject.has("end")) {
                    String endTime = timeRangeJsonObject.getString("end");
                    String timeFormatEnd = getTimeFormat(endTime, referenceDay);

                    if (!StringUtil.isEmpty(timeFormatEnd)) {
                        timeSb.append("至").append(timeFormatEnd);
                    }
                }
            }
            timeFormat = timeSb.toString();

        } catch (JSONException e) {
            LogUtils.d(TAG, "getTimeRangeFormat format time range error:" + e);
        }
        return timeFormat;
    }

    private String getLocation(String location, Object weatherObject) {
        LogUtils.d(TAG, "getLocation location start:" + location);
        StringBuilder sbLocation = new StringBuilder();
        com.voyah.ds.common.entity.domains.weather.Location locationDetail = null;
        if (weatherObject instanceof OneDayWeather) {
            OneDayWeather oneDayWeather = (OneDayWeather) weatherObject;
            locationDetail = oneDayWeather.locationDetail;
        } else if (weatherObject instanceof OneDayAqi) {
            OneDayAqi oneDayAqi = (OneDayAqi) weatherObject;
            locationDetail = oneDayAqi.locationDetail;
        } else if (weatherObject instanceof OneDayIndexInfo) {
            OneDayIndexInfo oneDayIndexInfo = (OneDayIndexInfo) weatherObject;
            locationDetail = oneDayIndexInfo.locationDetail;
        } else if (weatherObject instanceof MultiDaysWeather) {
            MultiDaysWeather multiDaysWeather = (MultiDaysWeather) weatherObject;
            locationDetail = multiDaysWeather.locationDetail;
        } else if (weatherObject instanceof MultiDaysAqi) {
            MultiDaysAqi multiDaysAqi = (MultiDaysAqi) weatherObject;
            locationDetail = multiDaysAqi.locationDetail;
        } else if (weatherObject instanceof MultiDaysIndexInfo) {
            MultiDaysIndexInfo multiDaysIndexInfo = (MultiDaysIndexInfo) weatherObject;
            locationDetail = multiDaysIndexInfo.locationDetail;
        } else if (weatherObject instanceof OneHourWeather) {
            OneHourWeather oneHourWeather = (OneHourWeather) weatherObject;
            locationDetail = oneHourWeather.locationDetail;
        } else if (weatherObject instanceof OneHourAqi) {
            OneHourAqi oneHourAqi = (OneHourAqi) weatherObject;
            locationDetail = oneHourAqi.locationDetail;
        } else if (weatherObject instanceof MultiHoursWeather) {
            MultiHoursWeather multiHoursWeather = (MultiHoursWeather) weatherObject;
            locationDetail = multiHoursWeather.locationDetail;
        } else if (weatherObject instanceof MultiHoursAqi) {
            MultiHoursAqi multiHoursAqi = (MultiHoursAqi) weatherObject;
            locationDetail = multiHoursAqi.locationDetail;
        }

        if (locationDetail != null) {
            if (!StringUtil.isEmpty(locationDetail.poi)) {
                sbLocation.insert(0, locationDetail.poi);
            }

            if (!StringUtil.isEmpty(locationDetail.district)) {
                sbLocation.insert(0, locationDetail.district);
            }

            if (!StringUtil.isEmpty(locationDetail.city)) {
                sbLocation.insert(0, locationDetail.city);
            }
        }

        int length = sbLocation.length();
        if (length > 0) {
            location = sbLocation.toString();
        }
        LogUtils.d(TAG, "getLocation location end:" + location);
        return location;
    }

    @Nullable
    protected String getDayRangeFormat(MultiDaysWeather multiDaysWeather) {
        List<MultiDaysWeather.DayWeather> dayWeathers = multiDaysWeather.dayWeathersList;
        String dateFormat = "";
        if (dayWeathers != null && dayWeathers.size() > 1) {
            StringBuilder dateSb = new StringBuilder();
            MultiDaysWeather.DayWeather startDayWeather = dayWeathers.get(0);
            if (startDayWeather != null) {
                String startDate = startDayWeather.date;
                if (!StringUtil.isEmpty(startDate)) {
                    String startDateFormat = getDateFormat(startDate);
                    if (!StringUtil.isEmpty(startDateFormat)) {
                        dateSb.append(startDateFormat);
                    }
                }
            }

            MultiDaysWeather.DayWeather endDayWeather = dayWeathers.get(dayWeathers.size() - 1);
            if (endDayWeather != null) {
                String endDate = endDayWeather.date;
                if (!StringUtil.isEmpty(endDate)) {
                    String endDateFormat = getDateFormat(endDate);
                    if (!StringUtil.isEmpty(endDateFormat)) {
                        dateSb.append("至").append(endDateFormat);
                    }
                }
            }
            dateFormat = dateSb.toString();
        }


        LogUtils.d(TAG, "getDayRangeFormat dateFormat:" + dateFormat);
        return dateFormat;
    }

    protected String getDateStartFormat(MultiDaysWeather multiDaysWeather) {
        List<MultiDaysWeather.DayWeather> dayWeathers = multiDaysWeather.dayWeathersList;
        String dateFormat = "";
        if (dayWeathers != null && dayWeathers.size() > 0) {
            StringBuilder dateSb = new StringBuilder();
            MultiDaysWeather.DayWeather startDayWeather = dayWeathers.get(0);
            if (startDayWeather != null) {
                String startDate = startDayWeather.date;
                if (!StringUtil.isEmpty(startDate)) {
                    dateFormat = getDateFormat(startDate);

                }
            }

        }

        LogUtils.d(TAG, "getDateStartFormat dateFormat:" + dateFormat);
        return dateFormat;
    }

    protected String getDateEndFormat(MultiDaysWeather multiDaysWeather) {
        List<MultiDaysWeather.DayWeather> dayWeathers = multiDaysWeather.dayWeathersList;
        String dateFormat = "";
        if (dayWeathers != null && dayWeathers.size() > 1) {
            StringBuilder dateSb = new StringBuilder();

            MultiDaysWeather.DayWeather endDayWeather = dayWeathers.get(dayWeathers.size() - 1);
            if (endDayWeather != null) {
                String endDate = endDayWeather.date;
                if (!StringUtil.isEmpty(endDate)) {
                    dateFormat = getDateFormat(endDate);
                }
            }
        }


        LogUtils.d(TAG, "getDateEndFormat dateFormat:" + dateFormat);
        return dateFormat;
    }

    @Nullable
    protected String getTimeRangeTimeFormat(MultiHoursWeather timeRangeWeather) {
        String timeFormat = "";
        List<MultiHoursWeather.HourWeather> timeRangeWeathers = timeRangeWeather.hourWeathersList;
        if (timeRangeWeathers != null && timeRangeWeathers.size() > 0) {
            StringBuilder timeSb = new StringBuilder();
            MultiHoursWeather.HourWeather startWeather = timeRangeWeathers.get(0);
            if (startWeather != null) {
                String startTime = startWeather.date;
                if (!StringUtil.isEmpty(startTime)) {
                    String timeFormatStart = getTimeFormat(startTime, 0);
                    if (!StringUtil.isEmpty(timeFormatStart)) {
                        timeSb.append(timeFormatStart);
                    }
                    IWeather weather = DeviceHolder.INS().getDevices().getWeather();
                    long timeStamp = weather.getTimeStamp(startTime);
                    int referenceDay = weather.getDay(timeStamp);
                    MultiHoursWeather.HourWeather endWeather = timeRangeWeathers.get(timeRangeWeathers.size() - 1);
                    if (endWeather != null) {
                        String endTime = endWeather.date;
                        if (!StringUtil.isEmpty(endTime)) {
                            String timeFormatEnd = getTimeFormat(endTime, referenceDay);

                            if (!StringUtil.isEmpty(timeFormatEnd)) {
                                timeSb.append("至").append(timeFormatEnd);
                            }
                        }
                    }
                }
            }

            timeFormat = timeSb.toString();

        }
        LogUtils.d(TAG, "getTimeRangeTimeFormat timeFormat:" + timeFormat);
        return timeFormat;
    }

    @Nullable
    protected String getTimeStartFormat(MultiHoursWeather timeRangeWeather) {
        String timeFormat = "";
        List<MultiHoursWeather.HourWeather> timeRangeWeathers = timeRangeWeather.hourWeathersList;
        if (timeRangeWeathers != null && timeRangeWeathers.size() > 0) {

            MultiHoursWeather.HourWeather startWeather = timeRangeWeathers.get(0);
            if (startWeather != null) {
                String startTime = startWeather.date;
                if (!StringUtil.isEmpty(startTime)) {
                    timeFormat = getTimeFormat(startTime, 0);
                }
            }
        }
        LogUtils.d(TAG, "getTimeStartFormat timeFormat:" + timeFormat);
        return timeFormat;
    }

    protected String getTimeEndFormat(MultiHoursWeather timeRangeWeather) {
        String timeFormat = "";
        List<MultiHoursWeather.HourWeather> timeRangeWeathers = timeRangeWeather.hourWeathersList;
        if (timeRangeWeathers != null && timeRangeWeathers.size() > 1) {
            MultiHoursWeather.HourWeather startWeather = timeRangeWeathers.get(0);
            if (startWeather != null) {
                String startTime = startWeather.date;
                if (!StringUtil.isEmpty(startTime)) {

                    IWeather weather = DeviceHolder.INS().getDevices().getWeather();
                    long timeStamp = weather.getTimeStamp(startTime);
                    int referenceDay = weather.getDay(timeStamp);
                    MultiHoursWeather.HourWeather endWeather = timeRangeWeathers.get(timeRangeWeathers.size() - 1);
                    if (endWeather != null) {
                        String endTime = endWeather.date;
                        if (!StringUtil.isEmpty(endTime)) {
                            timeFormat = getTimeFormat(endTime, referenceDay);
                        }
                    }
                }
            }
        }
        LogUtils.d(TAG, "getTimeEndFormat timeFormat:" + timeFormat);
        return timeFormat;
    }

    protected TTSBean getMultiDayTTSContent(String location,
                                          List<MultiDaysWeather.DayWeather> dayWeathers,
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
                String loopContent = getLoopContentDay(dayWeathers, loopFormat);
                ttsContent = String.format(Locale.getDefault(), ttsContent, loopContent);
            }
        }
        ttsBean.setSelectTTs(ttsContent);
        return ttsBean;
    }

    protected TTSBean getMultiHourTTSContent(String location,
                                           List<MultiHoursWeather.HourWeather> hourWeathers,
                                           String ttsId) {
        return null;
    }

    protected String getLoopContentDay(List<MultiDaysWeather.DayWeather> dayWeathers, String loopFormat) {

        return null;
    }

    protected String getLoopContentHour(List<MultiHoursWeather.HourWeather> hourWeathers, String loopFormat) {

        return null;
    }



}
