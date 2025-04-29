package com.voyah.ai.voice.agent.weather;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.domains.weather.MultiDaysIndexInfo;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayIndexInfo;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/6/6 16:11
 * description :
 */
@ClassAgent
public class WeatherLifeIndexSearchAgent extends AbstractWeatherSearchAgent<OneDayIndexInfo,
        MultiDaysIndexInfo, OneHourWeather, MultiHoursWeather> {

    private static final String TAG = "WeatherLifeIndexAgent";

    @Override
    public String AgentName() {
        return "weather_lifeIndex#search";
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
            OneDayIndexInfo oneDayWeather) {
        LogUtils.d(TAG, "getDayAgentResponse");
        String indexTypes = getParamKey(paramsMap, Constant.SLOT_NAME_INDEX_TYPE, 0);

        List<OneDayIndexInfo.IndexInfo> indexInfos = oneDayWeather.infoList;
        if (indexInfos == null || indexInfos.isEmpty()) {
            return new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ""
            );
        }

        OneDayIndexInfo.IndexInfo targetIndexInfo = null;
        for (OneDayIndexInfo.IndexInfo indexInfo : indexInfos) {
            if (TextUtils.equals(indexTypes, indexInfo.indexType)) {
                targetIndexInfo = indexInfo;
                break;
            }
        }

        if (targetIndexInfo == null) {
            return new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ""
            );
        }

        String dateFormat = getDateFormat(oneDayWeather.date);

        TTSBean ttsBean = getTTSText(
                location,
                dateFormat,
                targetIndexInfo.indexType,
                targetIndexInfo.indexLevelDesc,
                targetIndexInfo.indexDesc
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
            MultiDaysIndexInfo multiDaysWeather) {
        LogUtils.d(TAG, "getDayRangeAgentResponse");
        String indexTypes = getParamKey(paramsMap, Constant.SLOT_NAME_INDEX_TYPE, 0);

        TTSBean ttsBean = getMultiDayIndexTTSContent(location, multiDaysWeather.infoList, "7001057", indexTypes);
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
        return null;
    }

    @Override
    protected ClientAgentResponse getTimeRangeAgentResponse(
            Map<String, Object> flowContext,
            Map<String, List<Object>> paramsMap,
            String location,
            MultiHoursWeather timeRangeWeather) {
        return null;
    }

    private TTSBean getTTSText(
            String location,
            String date,
            String indexType,
            String indexLevelDesc,
            String indexDesc) {
        if (location == null) {
            location = "";
        }
        if (date == null) {
            date = "";
        }
        if (indexType == null) {
            indexType = "";
        }
        if (indexLevelDesc == null) {
            indexLevelDesc = "";
        }
        if (indexDesc == null) {
            indexDesc = "";
        }
//        @{location}@{date}，@{wx_index}@{wx_index_level}，@{index_desc}。
        TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex(
                "7001056",
                location,
                date,
                indexType,
                indexLevelDesc,
                indexDesc);

        return ttsBean;
    }

    private TTSBean getMultiDayIndexTTSContent(String location,
                                               List<OneDayIndexInfo> dayWeathers,
                                               String ttsId,
                                               String indexType) {
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
                String loopContent = getLoopContentDayIndex(dayWeathers, loopFormat, indexType);
                ttsContent = String.format(Locale.getDefault(), ttsContent, loopContent);
            }
        }
        ttsBean.setSelectTTs(ttsContent);
        return ttsBean;
    }


    private String getLoopContentDayIndex(
            List<OneDayIndexInfo> dayWeathers, String loopFormat, String indexType) {
        StringBuilder windTTSSb = new StringBuilder();

        for (OneDayIndexInfo oneDay : dayWeathers) {
            List<OneDayIndexInfo.IndexInfo> indexInfos = oneDay.infoList;
            if (indexInfos == null || indexInfos.isEmpty()) {
                continue;
            }

            OneDayIndexInfo.IndexInfo targetIndexInfo = null;
            for (OneDayIndexInfo.IndexInfo indexInfo : indexInfos) {
                if (TextUtils.equals(indexType, indexInfo.indexType)) {
                    targetIndexInfo = indexInfo;
                    break;
                }
            }

            if (targetIndexInfo == null) {
                continue;
            }

            String date = oneDay.date;
            String dateFormat = getDateFormat(date);
//            @{location}，[@{date}，@{wx_index}@{wx_index_level}，@{index_desc}，]。
            String weatherDateDesc = String.format(
                    Locale.getDefault(),
                    loopFormat,
                    dateFormat,
                    targetIndexInfo.indexType,
                    targetIndexInfo.indexLevelDesc,
                    targetIndexInfo.indexDesc
            );
            LogUtils.d(TAG, "getLoopContentDay weatherDateDesc:" + weatherDateDesc);
            windTTSSb.append(weatherDateDesc);
        }

        return windTTSSb.toString();
    }
}
