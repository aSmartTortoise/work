package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/20 10:28
 * description :
 */
public class BTPhoneInfo implements Parcelable {
    private int selectPosition;
    private List<Contact> contacts;

    public BTPhoneInfo() {
    }

    protected BTPhoneInfo(Parcel in) {
        selectPosition = in.readInt();
        contacts = in.createTypedArrayList(Contact.CREATOR);
    }

    public static final Creator<BTPhoneInfo> CREATOR = new Creator<BTPhoneInfo>() {
        @Override
        public BTPhoneInfo createFromParcel(Parcel in) {
            return new BTPhoneInfo(in);
        }

        @Override
        public BTPhoneInfo[] newArray(int size) {
            return new BTPhoneInfo[size];
        }
    };

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTPhoneInfo that = (BTPhoneInfo) o;
        return selectPosition == that.selectPosition && Objects.equals(contacts, that.contacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectPosition, contacts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(selectPosition);
        dest.writeTypedList(contacts);
    }
}
