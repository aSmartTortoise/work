package com.voyah.voice.framework.report;

public class Report {

    public static String CLICK = "CLICK";
    public static String VIEW_CMD = "VIEW_CMD";
    public static String VOICE = "VOICE";
    private String triggerMode = CLICK;

    private long time = 0;

    private String extraStr = "";

    public Report(String triggerMode, long time, String extraStr) {
        this.triggerMode = triggerMode;
        this.time = time;
        this.extraStr = extraStr;
    }

    public String getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(String triggerMode) {
        this.triggerMode = triggerMode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getExtraStr() {
        return extraStr;
    }

    public void setExtraStr(String extraStr) {
        this.extraStr = extraStr;
    }
}
