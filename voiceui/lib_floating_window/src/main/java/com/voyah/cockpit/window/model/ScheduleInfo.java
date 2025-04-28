package com.voyah.cockpit.window.model;

/**
 * author : jie wang
 * date : 2024/4/26 13:45
 * description :
 */
public class ScheduleInfo extends MultiItemEntity {

    private String time;
    private String event;

    private String location;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
