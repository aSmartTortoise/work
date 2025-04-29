package com.voice.sdk.device;

import com.voice.sdk.device.ui.UICardInterface;

/**
 * author : jie wang
 * date : 2025/3/5 16:54
 * description :
 */
public interface ScheduleInterface extends DomainInterface, UICardInterface {

    boolean openApp();

    boolean closeApp();

    int insertSchedule(String dateString, String event);

    void querySchedule(String date);

    void queryScheduleByTimeRange(String startTimeStr, String endTimeStr);

    void queryScheduleByTime(String timeStr);

    void queryScheduleByCurrentTime();

    boolean isUserLogin();

    String getUserId();

    void toScheduleList(String date);

    boolean beforeConstructResponse();

    void afterConstructResponse();

    void beforeExecuteAgent();

    void afterExecuteAgent();

    boolean isSystemInfoHidingOpen();

    String getTimeType();

    void constructCardInfo(String time, String event, String requestId);

    String getTTSTime(String time);

    long getInterval(String time);




}
