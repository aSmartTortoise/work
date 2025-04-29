package com.voyah.ai.basecar;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ScheduleInterface;
import com.voice.sdk.device.UserCenterInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.CalendatDataServiceImpl;
import com.voyah.cockpit.calendar.ICalendarDataCallback;
import com.voyah.cockpit.calendar.bean.Day;
import com.voyah.cockpit.calendar.bean.RepeatEntity;
import com.voyah.cockpit.calendar.bean.ScheduleEntity;
import com.voyah.cockpit.calendar.bean.Time;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.ScheduleInfo;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.cockpit.window.util.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * author : jie wang
 * date : 2024/4/22 13:44
 * description :
 */
public class ScheduleInterfaceImpl extends BaseAppPresenter implements ScheduleInterface {

    private static final String TAG = "CalendarImpl";

    private CalendatDataServiceImpl mCalendarService;

    private CountDownLatch mCountDownLatch;
    private boolean hasSchedule = false;
    private volatile boolean executingFlag = false;
    private CardInfo mCardInfo;

    private final ICalendarDataCallback.Stub mCalendarDataCallback = new ICalendarDataCallback.Stub() {

        @Override
        public void backData(List<ScheduleEntity> scheduleList) throws RemoteException {
            LogUtils.d(TAG, "backData scheduleList:" + scheduleList);
            ScheduleInterface scheduleInterface = DeviceHolder.INS().getDevices().getSchedule();
            if (scheduleList != null && !scheduleList.isEmpty() && executingFlag) {
                hasSchedule = true;
                ScheduleEntity entity = scheduleList.get(0);
                if (entity != null) {
                    Time time = entity.getTime();
                    if (time != null) {
                        Day day = time.getDay();
                        LocalDate date = LocalDate.of(
                                day.getYear(),
                                day.getMonthOfYear(),
                                day.getDayOfMonth());
                        // 定义日期格式
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        scheduleInterface.toScheduleList(date.format(formatter));
                    }
                }

                if (mCountDownLatch != null) {
                    LogUtils.d(TAG, "backData count down.");
                    mCountDownLatch.countDown();
                }
            }
        }
    };

    private ScheduleInterfaceImpl() {
    }

    public static ScheduleInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        initSdk();
        mCalendarService = CalendatDataServiceImpl.getInstance(mContext);
        mCalendarService.startService(() -> LogUtils.d(TAG, "onServiceConnected"));
    }

    @Override
    public boolean openApp() {
        openApp(ApplicationConstant.PKG_CALENDAR);
        return true;
    }

    @Override
    public boolean closeApp() {
        return false;
    }

    @Override
    public boolean isAppForeground() {
        return MegaForegroundUtils.isForegroundApp(mContext, ApplicationConstant.PKG_CALENDAR);
    }


    @Override
    public int insertSchedule(String dateString, String event) {
        LogUtils.d(TAG, "insertSchedule dateString:" + dateString);
        Time time = getScheduleTime(dateString);
        ScheduleEntity entity = new ScheduleEntity();

        entity.setRepeatEntity(new RepeatEntity(0, null));
        entity.setContent(event);

        entity.setCreateTime(String.valueOf(System.currentTimeMillis()));
        entity.setTime(time);
        String userId = getUserId();
        entity.setUserId(userId);
        return mCalendarService.insert(entity);
    }

    @Override
    public void querySchedule(String date) {
        if (mCalendarDataCallback == null) {
            LogUtils.e(TAG, "querySchedule mCalendarDataCallback should init firstly.");
            return;
        }
        if (TextUtils.isEmpty(date)) return;
        String[] dateArr = DateUtil.getDateArr(date);
        if (dateArr == null || dateArr.length != 3) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]);

        try {
            hasSchedule = false;
            mCountDownLatch = new CountDownLatch(1);
            int dateInt = Integer.parseInt(sb.toString());
            LogUtils.d(TAG, "querySchedule dateInt:" + dateInt);
            mCalendarService.selectByTimeStr(dateInt, mCalendarDataCallback);
        } catch (NumberFormatException e) {
            LogUtils.e(TAG, "querySchedule invalid param date e:" + e);
        }
    }

    @Override
    public void queryScheduleByTimeRange(String startTimeStr, String endTimeStr) {
        if (mCalendarDataCallback == null) {
            LogUtils.e(TAG, "queryScheduleByTimeRange mCalendarDataCallback should init firstly.");
            return;
        }
        if (TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr)) return;
        hasSchedule = false;
        mCountDownLatch = new CountDownLatch(1);
        hasSchedule = false;
        mCountDownLatch = new CountDownLatch(1);
        Time timeStart = getScheduleTime(startTimeStr);
        timeStart.setSecond(0);
        Time timeEnd = getScheduleTime(endTimeStr);
        timeEnd.setSecond(0);
        LogUtils.d(TAG, "queryScheduleByTimeRange selectTimeSlot");
        mCalendarService.selectTimeSlot(timeStart, timeEnd, mCalendarDataCallback);
    }

    @Override
    public void queryScheduleByTime(String timeStr) {
        if (mCalendarDataCallback == null) {
            LogUtils.e(TAG, "queryScheduleByTime mCalendarDataCallback should init firstly.");
            return;
        }

        if (TextUtils.isEmpty(timeStr)) return;
        hasSchedule = false;
        mCountDownLatch = new CountDownLatch(1);
        Time time = getScheduleTime(timeStr);
        LogUtils.d(TAG, "queryScheduleByTime selectTimeSlot");
        mCalendarService.selectTimePoint(time, mCalendarDataCallback);
    }

    @Override
    public void queryScheduleByCurrentTime() {
        if (mCalendarDataCallback == null) {
            LogUtils.e(TAG, "queryScheduleByCurrentTime mCalendarDataCallback should init firstly.");
            return;
        }
        hasSchedule = false;
        mCountDownLatch = new CountDownLatch(1);
        mCalendarService.selectUncompleted(mCalendarDataCallback);
    }

    @Override
    public boolean isUserLogin() {
        boolean isLogin = false;
        UserCenterInterface userCenterInterface = DeviceHolder.INS().getDevices().getUserCenter();
        if (userCenterInterface != null) {
            isLogin = userCenterInterface.isLogin();
            LogUtils.d(TAG, "isUserLogin isLogin:" + isLogin);
        }
        return isLogin;
    }

    @Override
    public String getUserId() {
        String userId = null;
//        UserCenterPresenterImpl userCenterImpl = DeviceInterfaceImpl.getInstant(mContext)
//                .getUserCenterImpl();
//        if (userCenterImpl != null) {
//            userId = userCenterImpl.getUserId();
//
//        }
//        LogUtils.d(TAG, "getUserId userId:" + userId);
        return userId;
    }

    @Override
    public void toScheduleList(String date) {
        beforeOpenApp();
        String packageSchedule = "com.voyah.cockpit.calendar";
        String targetActivityName = "com.voyah.cockpit.calendar.view.activity.CalendarActivity";
        long timeStamp = DateUtil.getMillis(date);
        int year = DateUtil.getYear(timeStamp);
        int month = DateUtil.getMonth(timeStamp) + 1;
        int day = DateUtil.getDay(timeStamp);
        LogUtils.d(TAG, "toScheduleList year:" + year + " month:" + month + " day:" + day);
        Bundle businessBundle = new Bundle();
        businessBundle.putInt("year", year);
        businessBundle.putInt("month", month);
        businessBundle.putInt("day", day);
        if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isNeedSplitScreen()) {
            DeviceHolder.INS().getDevices().getSystem().getSplitScreen().enterSplitScreen(
                    packageSchedule,
                    targetActivityName,
                    businessBundle);
        } else {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageSchedule, targetActivityName));
            intent.putExtras(businessBundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @NonNull
    private Time getScheduleTime(String dateString) {
        LogUtils.d(TAG, "getScheduleTime dateString:" + dateString);
        long timeMillis = DateUtil.getTimeStamp(dateString);
        int year = DateUtil.getYear(timeMillis);
        int month = DateUtil.getMonth(timeMillis) + 1;
        int dayInt = DateUtil.getDay(timeMillis);
        Day day = new Day(year, month, dayInt);
        int hour = DateUtil.getHour(timeMillis);
        int minute = DateUtil.getMinute(timeMillis);
        int second = DateUtil.getSecond(timeMillis);
        LogUtils.d(TAG, "getScheduleTime year:" + year + " month:" + month + " dayInt:" + dayInt
                + " hour:" + hour + " minute:" + minute + " second:" + second);

        return new Time(day, hour, minute, second);
    }

    @Override
    public boolean beforeConstructResponse() {
        boolean result = false;
        try {
            mCountDownLatch.await(1500, TimeUnit.MILLISECONDS);
            result = hasSchedule;
        } catch (InterruptedException e) {
            LogUtils.w(TAG, "beforeConstructResponse exception occur e:" + e);
        }
        LogUtils.i(TAG, "beforeConstructResponse result:" + result);
        return result;
    }

    @Override
    public void afterConstructResponse() {
        LogUtils.i(TAG, "afterConstructResponse");
        hasSchedule = false;
    }

    @Override
    public void beforeExecuteAgent() {
        LogUtils.i(TAG, "beforeExecuteAgent");
        executingFlag = true;
    }

    @Override
    public void afterExecuteAgent() {
        LogUtils.i(TAG, "afterExecuteAgent");
        executingFlag = false;
    }

    @Override
    public boolean isSystemInfoHidingOpen() {
        return super.isSystemInfoHidingOpen();
    }

    @Override
    public String getTimeType() {
        return super.getTimeType();
    }

    @Override
    public void constructCardInfo(String time, String event, String requestId) {
        LogUtils.i(TAG, "constructCardInfo");
        mCardInfo = new CardInfo();
        List<ScheduleInfo> scheduleInfos = new ArrayList<>();
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setItemType(ViewType.SCHEDULE_TYPE_1);
        schedule.setTime(time);
        schedule.setEvent(event);
        scheduleInfos.add(schedule);

        mCardInfo.setSchedules(scheduleInfos);
        mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_SCHEDULE);
        mCardInfo.setSessionId(requestId);
        mCardInfo.setRequestId(requestId);
    }

    @Override
    public boolean isCardInfoEmpty() {
        return mCardInfo == null;
    }

    @Override
    public void onShowUI(String business, int location) {
        LogUtils.i(TAG, "onShowUI business" + business + " location:" + location);
        if (mCardInfo != null) {
            UIMgr.INSTANCE.showCard(
                    UiConstant.CardType.SCHEDULE_CARD,
                    mCardInfo,
                    mCardInfo.getSessionId(),
                    business,
                    location);
            mCardInfo = null;
        }
    }

    @Override
    public String getTTSTime(String time) {
        long timeStamp = DateUtil.getTimeStamp(time);
        int year = DateUtil.getYear(timeStamp);
        int month = DateUtil.getMonth(timeStamp) + 1;
        int day = DateUtil.getDay(timeStamp);
        int hour = DateUtil.getHour(timeStamp);
        int minute = DateUtil.getMinute(timeStamp);
        String customTimeStr = DateUtil.getCustomTimeStr(timeStamp, true);
        String ttsTime = String.format(
                Locale.CHINA,
                "%d年%d月%d日 %s",
                year,
                month,
                day,
                customTimeStr
        );
        LogUtils.i(TAG, "getTTSTime ttsTime:" + ttsTime);
        return ttsTime;
    }

    @Override
    public long getInterval(String time) {
        return DateUtil.getTimeStamp(time) - System.currentTimeMillis();
    }

    private static class Holder {
        private static ScheduleInterfaceImpl INSTANCE = new ScheduleInterfaceImpl();
    }





}
