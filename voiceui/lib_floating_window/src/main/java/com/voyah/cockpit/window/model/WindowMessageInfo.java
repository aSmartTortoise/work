package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/6 10:39
 * description :
 */
public class WindowMessageInfo implements Parcelable {
    /**
     *  打字机文本
     */
    @Deprecated
    private String typewriterText;

    /**
     *  卡片数据的业务
     */
    @Deprecated
    private String domainName;

    /**
     *  联系人列表
     *  Use contactInfo instead.
     */
    @Deprecated
    private List<Contact> contacts;

    /**
     *  语音状态
     */
    @Deprecated
    private String voiceState;


    private BTPhoneInfo btPhoneInfo;

    private VoiceInfo voiceInfo;

    private TypewriterInfo typewriterInfo;

    private ExecuteFeedbackInfo feedbackInfo;

    private CardInfo cardInfo;

    public WindowMessageInfo() {
    }

    protected WindowMessageInfo(Parcel in) {
        typewriterText = in.readString();
        domainName = in.readString();
        voiceState = in.readString();
        btPhoneInfo = in.readParcelable(BTPhoneInfo.class.getClassLoader());
    }

    public static final Creator<WindowMessageInfo> CREATOR = new Creator<WindowMessageInfo>() {
        @Override
        public WindowMessageInfo createFromParcel(Parcel in) {
            return new WindowMessageInfo(in);
        }

        @Override
        public WindowMessageInfo[] newArray(int size) {
            return new WindowMessageInfo[size];
        }
    };

    @Deprecated
    public String getTypewriterText() {
        return typewriterText;
    }

    @Deprecated
    public void setTypewriterText(String typewriterText) {
        this.typewriterText = typewriterText;
    }

    @Deprecated
    public String getDomainName() {
        return domainName;
    }

    @Deprecated
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Deprecated
    public List<Contact> getContacts() {
        return contacts;
    }

    @Deprecated
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Deprecated
    public String getVoiceState() {
        return voiceState;
    }

    @Deprecated
    public void setVoiceState(String voiceState) {
        this.voiceState = voiceState;
    }

    public BTPhoneInfo getBTPhoneInfo() {
        return btPhoneInfo;
    }

    public void setBTPhoneInfo(BTPhoneInfo btPhoneInfo) {
        this.btPhoneInfo = btPhoneInfo;
    }

    public VoiceInfo getVoiceInfo() {
        return voiceInfo;
    }

    public void setVoiceInfo(VoiceInfo voiceInfo) {
        this.voiceInfo = voiceInfo;
    }

    public TypewriterInfo getTypewriterInfo() {
        return typewriterInfo;
    }

    public void setTypewriterInfo(TypewriterInfo typewriterInfo) {
        this.typewriterInfo = typewriterInfo;
    }

    public ExecuteFeedbackInfo getFeedbackInfo() {
        return feedbackInfo;
    }

    public void setFeedbackInfo(ExecuteFeedbackInfo feedbackInfo) {
        this.feedbackInfo = feedbackInfo;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowMessageInfo that = (WindowMessageInfo) o;
        return Objects.equals(typewriterText, that.typewriterText)
                && Objects.equals(domainName, that.domainName)
                && Objects.equals(voiceState, that.voiceState)
                && Objects.equals(btPhoneInfo, that.btPhoneInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typewriterText, domainName, voiceState, btPhoneInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(typewriterText);
        dest.writeString(domainName);
        dest.writeString(voiceState);
        dest.writeParcelable(btPhoneInfo, flags);
    }
}
