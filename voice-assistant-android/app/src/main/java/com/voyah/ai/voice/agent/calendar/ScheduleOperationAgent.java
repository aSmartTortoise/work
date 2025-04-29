package com.voyah.ai.voice.agent.calendar;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ScheduleInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.listener.UICardListener;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/4/22 11:24
 * description : 日程的增删改查操作
 */

@ClassAgent
public class ScheduleOperationAgent extends BaseAgentX {

    private static final String TAG = "ScheduleOperationAgent";




    private final UICardListener mUICardListener = new UICardListener() {
        @Override
        public void onCardItemClick(int position, int itemType, int screenType) {
            LogUtils.d(TAG, "onCardItemClick position:" + position + " viewType:" + itemType);
            ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
            boolean foregroundFlag = scheduleInterface.isAppForeground();
            LogUtils.d(TAG, "onCardItemClick foregroundFlag:" + foregroundFlag);
            if (foregroundFlag) {
            } else {
                scheduleInterface.openApp();
            }
        }

        @Override
        public void uiCardClose(String sessionId) {
            LogUtils.d(TAG, "uiCardClose");
            UIMgr.INSTANCE.removeCardStateListener(mUICardListener);
        }

    };


    @Override
    public String AgentName() {
        return "schedule#operation";
    }

    @Override
    public boolean isSequenced() {
        return true;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        DeviceHolder.INS().getDevices().getSchedule().beforeExecuteAgent();
        ClientAgentResponse response = null;
        String ttsText = "";
        String operationType = getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
        LogUtils.d(TAG, "executeAgent operationType:" + operationType);
        switch (operationType) {
            case Constant.OperationType.INSERT:
                response = getInformationSwitchOpenResponse(flowContext);
                if (response != null) break;
                response = getNotLoginResponse(flowContext);
                if (response != null) break;
                String date = getParamKey(paramsMap, Constant.SLOT_NAME_DATE, 0);
                String time = getParamKey(paramsMap, Constant.SLOT_NAME_TIME, 0);
                String event = getParamKey(paramsMap, Constant.SLOT_NAME_EVENT, 0);
                String timeRange = getParamKey(paramsMap, Constant.SLOT_NAME_TIME_RANGE, 0);
                LogUtils.d(TAG, "executeAgent date:" + date + " time:" + time + " event:" + event);

                if (TextUtils.isEmpty(date) && !TextUtils.isEmpty(timeRange)) {
                    LogUtils.d(TAG, "executeAgent timeRange:" + timeRange);
                    JSONObject timeRangeJSONObject = GsonUtils.parseToJSONObject(timeRange);
                    if (timeRangeJSONObject.has("start")) {
                        try {
                            String startTimeStr = timeRangeJSONObject.getString("start");
                            if (startTimeStr.contains(" ")) {
                                date = startTimeStr.split(" ")[0];
                                LogUtils.d(TAG, "executeAgent date form time range start is:" + date);
                            }
                        } catch (JSONException e) {
                            LogUtils.w(TAG, "executeAgent parse nlu timeRange data start error:" + e);
                        }
                    }
                }


                if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(event)) {
                    response = getPastTimeResponse(flowContext, time, event);
                    if (response != null) break;
                    ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
                    int result = scheduleInterface.insertSchedule(time, event);
                    LogUtils.d(TAG, "executeAgent insertSchedule:" + result);
                    // 好的,岚图将在【日期】【时间】提醒您
                    String ttsTime = scheduleInterface.getTTSTime(time);
                    String timeType = scheduleInterface.getTimeType();
                    LogUtils.d(TAG, "executeAgent Settings time type is:" + timeType);

                    TTSBean ttsBean = TtsBeanUtils.getTtsBeanByRegex("6006022", ttsTime);
                    LogUtils.d(TAG, "executeAgent insert schedule tts is:" + ttsBean.getSelectTTs());
                    response = new ClientAgentResponse(
                            Constant.CommonResponseCode.SUCCESS,
                            flowContext,
                            ttsBean
                    );
                    response.setInformationCard(true);
                    response.setUiType(CARD_TYPE_INFORMATION);

                    String requestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
                    scheduleInterface.constructCardInfo(time, event, requestId);
                } else if (TextUtils.isEmpty(date)
                        && TextUtils.isEmpty(time)
                        && TextUtils.isEmpty(event)) {
                    LogUtils.d(TAG, "executeAgent no date, no time, no event...");
                    response = new ClientAgentResponse(
                            Constant.CommonResponseCode.SECONDARY_INTERACTION,
                            flowContext,
                            TTSIDConvertHelper.getInstance().getTTSBean("6006010", 2)
                    );
                    response.setDsScenario(
                            Constant.SCENARIO_SCHEDULE_TIME_INQUIRY
                                    + "|"
                                    + Constant.SCENARIO_SCHEDULE_EVENT_INQUIRY
                    );
                } else if (!TextUtils.isEmpty(date)
                        && TextUtils.isEmpty(time)
                        && TextUtils.isEmpty(event)) {
                    LogUtils.d(TAG, "executeAgent only has date...");

                    String amendDate = date + " 23:59:59";
                    response = getPastTimeResponse(flowContext, amendDate, null);
                    if (response != null) break;
                    response = new ClientAgentResponse(
                            Constant.CommonResponseCode.SECONDARY_INTERACTION,
                            flowContext,
                            TTSIDConvertHelper.getInstance().getTTSBean("6006011", 2)
                    );
                    response.setDsScenario(Constant.SCENARIO_SCHEDULE_TIME_INQUIRY);
                } else if (TextUtils.isEmpty(date)
                        && !TextUtils.isEmpty(time)
                        && TextUtils.isEmpty(event)) {
                    LogUtils.d(TAG, "executeAgent only has time...");
                    response = getPastTimeResponse(flowContext, time, null);
                    if (response != null) break;
                    response = new ClientAgentResponse(
                            Constant.CommonResponseCode.SECONDARY_INTERACTION,
                            flowContext,
                            TTSIDConvertHelper.getInstance().getTTSBean("6006012", 2)
                    );
                    response.setDsScenario(Constant.SCENARIO_SCHEDULE_EVENT_INQUIRY);
                } else if (TextUtils.isEmpty(date)
                        && TextUtils.isEmpty(time)
                        && !TextUtils.isEmpty(event)) {
                    LogUtils.d(TAG, "executeAgent only has event...");
                    response = getFutureTimeResponse(flowContext, event, date);
                } else if (!TextUtils.isEmpty(date)
                        && !TextUtils.isEmpty(time)
                        && TextUtils.isEmpty(event)) {//不存在这种情况

                } else if (!TextUtils.isEmpty(date)
                        && TextUtils.isEmpty(time)
                        && !TextUtils.isEmpty(event)) {
                    String amendDate = date + " 23:59:59";
                    response = getPastTimeResponse(flowContext, amendDate, event);
                    if (response != null) break;
                    response = getFutureTimeResponse(flowContext, event, date);
                }
                break;
            case Constant.OperationType.DELETE:
                response = new ClientAgentResponse(
                        Constant.CommonResponseCode.SUCCESS,
                        flowContext,
                        TTSIDConvertHelper.getInstance().getTTSBean("6006000", 2)
                );
                break;
            case Constant.OperationType.UPDATE:
                response = new ClientAgentResponse(
                        Constant.CommonResponseCode.SUCCESS,
                        flowContext,
                        TTSIDConvertHelper.getInstance().getTTSBean("6006003", 2)
                );
                break;
            case Constant.OperationType.QUERY:
                response = getInformationSwitchOpenResponse(flowContext);
                if (response != null) break;
                response = getNotLoginResponse(flowContext);
                if (response != null) break;
                ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();


                date = getParamKey(paramsMap, Constant.SLOT_NAME_DATE, 0);
                String dateRange = getParamKey(paramsMap, Constant.SLOT_NAME_DATE_RANGE, 0);
                time = getParamKey(paramsMap, Constant.SLOT_NAME_TIME, 0);
                String timeRangeJson = getParamKey(paramsMap, Constant.SLOT_NAME_TIME_RANGE, 0);
                LogUtils.d(TAG, "executeAgent schedule query date:" + date + " dateRange:"
                        + dateRange + " time:" + time + " timeRange:" + timeRangeJson);
                if (!TextUtils.isEmpty(date)) {
                    scheduleInterface.querySchedule(date);
                    response = getScheduleQueryAgentResponse(flowContext);
                } else if (!TextUtils.isEmpty(dateRange)) {
                    JSONObject dateRangeJSONObject = GsonUtils.parseToJSONObject(dateRange);
                    if (dateRangeJSONObject.has("start")) {
                        try {
                            String startDateStr = dateRangeJSONObject.getString("start");
                            if (dateRangeJSONObject.has("end")) {
                                try {
                                    String endDateStr = dateRangeJSONObject.getString("end");
                                    startDateStr += " 00:00:00";
                                    endDateStr += " 23:59:59";
                                    scheduleInterface.queryScheduleByTimeRange(startDateStr, endDateStr);
                                    response = getScheduleQueryAgentResponse(flowContext);
                                } catch (JSONException e) {
                                    LogUtils.w(TAG, "executeAgent parse nlu dateRange data end error:" + e);
                                }
                            }
                        } catch (JSONException e) {
                            LogUtils.w(TAG, "executeAgent parse nlu dateRange data start error:" + e);
                        }
                    }
                } else if (!TextUtils.isEmpty(time)) {
                    scheduleInterface.queryScheduleByTime(time);
                    response = getScheduleQueryAgentResponse(flowContext);
                } else if (!TextUtils.isEmpty(timeRangeJson)) {
                    JSONObject timeRangeJSONObject = GsonUtils.parseToJSONObject(timeRangeJson);
                    if (timeRangeJSONObject.has("start")) {
                        try {
                            String startTimeStr = timeRangeJSONObject.getString("start");
                            if (timeRangeJSONObject.has("end")) {
                                try {
                                    String endTimeStr = timeRangeJSONObject.getString("end");
                                    scheduleInterface.queryScheduleByTimeRange(startTimeStr, endTimeStr);
                                    response = getScheduleQueryAgentResponse(flowContext);
                                } catch (JSONException e) {
                                    LogUtils.w(TAG, "executeAgent parse nlu timeRange data end error:" + e);
                                }
                            }
                        } catch (JSONException e) {
                            LogUtils.w(TAG, "executeAgent parse nlu timeRange data start error:" + e);
                        }
                    }
                } else {
                    scheduleInterface.queryScheduleByCurrentTime();
                    response = getScheduleQueryAgentResponse(flowContext);
                }

                break;
        }
        LogUtils.d(TAG, "executeAgent return.");

        ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
        scheduleInterface.afterExecuteAgent();
        return response;
    }

    @NonNull
    private ClientAgentResponse getScheduleQueryAgentResponse(Map<String, Object> flowContext) {
        ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
        boolean result = scheduleInterface.beforeConstructResponse();
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(
                !result ? "6006021" : "6006020", 2);

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsBean
        );

        scheduleInterface.afterConstructResponse();
        return response;
    }


    @Override
    public void destroyAgent() {

    }

    @Override
    public void executeOrder(String executeTag, int location) {
        super.executeOrder(executeTag, location);
    }

    @Override
    public void showUi(String uiType, int location) {
        LogUtils.d(TAG, "showUI uiType:" + uiType);
        if (CARD_TYPE_INFORMATION.equals(uiType)) {
            DeviceHolder.INS().getDevices().getSchedule().onShowUI(mAgentIdentifier, location);
        }
    }

    private static ClientAgentResponse getInformationSwitchOpenResponse(Map<String, Object> flowContext) {
        ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
        boolean infoHidingOpenFlag = scheduleInterface.isSystemInfoHidingOpen();
        LogUtils.d(TAG, "getInformationSwitchOpenResponse infoHidingOpenFlag:" + infoHidingOpenFlag);
        ClientAgentResponse response = null;
        if (infoHidingOpenFlag) {
            TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("6006002", 2);
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ttsBean
            );
        }
        return response;
    }

    @Nullable
    private static ClientAgentResponse getNotLoginResponse(Map<String, Object> flowContext) {
        ClientAgentResponse response = null;
        ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
        boolean isLogin = scheduleInterface.isUserLogin();
        LogUtils.d(TAG, "executeAgent isUserLogin:" + isLogin);

        if (!isLogin) {
            TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("6006001", 2);
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ttsBean
            );
        }
        return response;
    }

    private @Nullable ClientAgentResponse getPastTimeResponse(
            Map<String, Object> flowContext,
            String time,
            String event) {
        ClientAgentResponse response = null;
        long interval = DeviceHolder.INS().getDevices().getSchedule().getInterval(time);
        if (interval <= 60 * 1000L) {
            LogUtils.d(TAG, "executeAgent add schedule, interval less 1 min...");
            String scenario;
            if (!TextUtils.isEmpty(event)) {
                if (event.length() > 100) {
                    scenario = Constant.SCENARIO_SCHEDULE_TIME_INQUIRY
                            + "|"
                            + Constant.SCENARIO_SCHEDULE_EVENT_INQUIRY;
                } else {
                    scenario = Constant.SCENARIO_SCHEDULE_TIME_INQUIRY;
                }
            } else {
                scenario = Constant.SCENARIO_SCHEDULE_TIME_INQUIRY
                        + "|"
                        + Constant.SCENARIO_SCHEDULE_EVENT_INQUIRY;
            }
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SECONDARY_INTERACTION,
                    flowContext,
                    TTSIDConvertHelper.getInstance().getTTSBean("6006013", 2)
            );

            response.setDsScenario(scenario);
        }
        return response;
    }

    private ClientAgentResponse getFutureTimeResponse(
            Map<String, Object> flowContext,
            String event,
            String date) {
        ClientAgentResponse response;
        TTSBean ttsBean = StringUtil.isEmpty(date) ? TTSIDConvertHelper.getInstance().getTTSBean("6006010", 2)
                : TTSIDConvertHelper.getInstance().getTTSBean("6006011", 2);
        LogUtils.d(TAG, "getFutureTimeResponse tts is:" + ttsBean.getSelectTTs());
        if (event.length() > 100) {
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SECONDARY_INTERACTION,
                    flowContext,
                    ttsBean);
            response.setDsScenario(Constant.SCENARIO_SCHEDULE_TIME_INQUIRY
                    + "|"
                    + Constant.SCENARIO_SCHEDULE_EVENT_INQUIRY);
        } else {
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SECONDARY_INTERACTION,
                    flowContext,
                    ttsBean);
            response.setDsScenario(Constant.SCENARIO_SCHEDULE_TIME_INQUIRY);
        }
        return response;
    }



}
