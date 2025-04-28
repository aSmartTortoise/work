package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * author : jie wang
 * date : 2024/12/24 16:53
 * description :
 */
public class UIMessage implements Parcelable {
    private String textTypewriter;

    private int textStyle;

    private String voiceState;

    private int screenType;

    public UIMessage() {
    }

    protected UIMessage(Parcel in) {
        textTypewriter = in.readString();
        textStyle = in.readInt();
        voiceState = in.readString();
        screenType = in.readInt();
    }

    public static final Creator<UIMessage> CREATOR = new Creator<UIMessage>() {
        @Override
        public UIMessage createFromParcel(Parcel in) {
            return new UIMessage(in);
        }

        @Override
        public UIMessage[] newArray(int size) {
            return new UIMessage[size];
        }
    };

    public String getTextTypewriter() {
        return textTypewriter;
    }

    public void setTextTypewriter(String textTypewriter) {
        this.textTypewriter = textTypewriter;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
    }

    public String getVoiceState() {
        return voiceState;
    }

    public void setVoiceState(String voiceState) {
        this.voiceState = voiceState;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(textTypewriter);
        dest.writeInt(textStyle);
        dest.writeString(voiceState);
        dest.writeInt(screenType);
    }

    @NonNull
    @Override
    public String toString() {
        return "UIMessage{" +
                "textTypewriter='" + textTypewriter + '\'' +
                ", textStyle=" + textStyle +
                ", voiceState='" + voiceState + '\'' +
                ", screenType=" + screenType +
                '}';
    }
}
