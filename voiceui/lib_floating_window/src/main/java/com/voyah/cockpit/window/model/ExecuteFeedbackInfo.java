package com.voyah.cockpit.window.model;

import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/27 10:14
 * description :
 */
public class ExecuteFeedbackInfo {
    private int location;

    private String text;

    private boolean enable;


    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecuteFeedbackInfo that = (ExecuteFeedbackInfo) o;
        return location == that.location
                && enable == that.enable
                && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, text, enable);
    }
}
