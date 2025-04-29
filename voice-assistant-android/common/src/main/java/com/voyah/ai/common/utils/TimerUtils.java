package com.voyah.ai.common.utils;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils extends Timer {
    private static final String TAG = "TimerUtils";
    private static TimerUtils mTimer;
    private static final Map<String, TimerTask> mTaskMap = new HashMap();

    private TimerUtils() {
    }

    public static TimerUtils getInstance() {
        if (mTimer == null) {
            mTimer = new TimerUtils();
        }

        return mTimer;
    }

    public void startTimer(TimerTask task, String taskName, int millisec) {
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }

        mTaskMap.put(taskName, task);

        try {
            mTimer.schedule(task, (long) millisec);
        } catch (IllegalStateException var6) {
            var6.printStackTrace();
        }

    }

    public void startTimer(TimerTask task, String taskName, int millisec, int timeGap) {
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }

        mTaskMap.put(taskName, task);

        try {
            mTimer.schedule(task, (long) millisec, (long) timeGap);
        } catch (IllegalStateException var7) {
            var7.printStackTrace();
        }

    }

    public void cancelTimer(String taskName) {
        TimerTask timerTask = mTaskMap.get(taskName);
        if (timerTask != null) {
            Log.i(TAG, "cancelTimer=" + taskName);
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }

    }

    public boolean hasTimer(String taskName) {
        return mTaskMap.containsKey(taskName);
    }
}
