package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/5 11:09
 * description :
 */
public class WindowMessage implements Parcelable{
    /**
     *  window name，标识要操作的悬浮窗类型。vpa_typewriter_card、四个音浪、后排两个执行反馈等。
     */
    private String name;

    /**
     *  标识操作悬浮窗的行为。比如
     *  显示悬浮窗、输入打字机内容、展开card、收起card、收起卡片并停止tts、隐藏悬浮窗、设置悬浮窗背景、设置vpa状态。
     *
     *  收起卡片并停止tts
     *      用户点击窗体边框外区域，响应收起卡片并终止tts。
     */
    private String action;

    /**
     *  屏幕的位置
     */
    private int screenType;


    public WindowMessage() {
    }

    public WindowMessage(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public WindowMessage(String name, String action, int screenType) {
        this.name = name;
        this.action = action;
        this.screenType = screenType;
    }

    protected WindowMessage(Parcel in) {
        name = in.readString();
        action = in.readString();
        screenType = in.readInt();
    }

    public static final Creator<WindowMessage> CREATOR = new Creator<WindowMessage>() {
        @Override
        public WindowMessage createFromParcel(Parcel in) {
            return new WindowMessage(in);
        }

        @Override
        public WindowMessage[] newArray(int size) {
            return new WindowMessage[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowMessage that = (WindowMessage) o;
        return screenType == that.screenType && Objects.equals(name, that.name) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, action, screenType);
    }

    @Override
    public String toString() {
        return "WindowMessage{" +
                "name='" + name + '\'' +
                ", action='" + action + '\'' +
                ", screenType=" + screenType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(action);
        dest.writeInt(screenType);
    }


}
