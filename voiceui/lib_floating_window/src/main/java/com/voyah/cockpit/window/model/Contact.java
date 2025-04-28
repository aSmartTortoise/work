package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/6 10:40
 * description : 联系人实体
 */
public class Contact extends MultiItemEntity implements Parcelable {
    private String contactName;
    private String number;

    /**
     *  号码标识。电话、手机等
     */
    private String phoneType;

    public Contact() {
        setItemType(ViewType.BT_PHONE_TYPE);
    }

    public Contact(String contactName, String number, String phoneType) {
        this.contactName = contactName;
        this.number = number;
        this.phoneType = phoneType;
    }

    protected Contact(Parcel in) {
        contactName = in.readString();
        number = in.readString();
        phoneType = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(contactName, contact.contactName)
                && Objects.equals(number, contact.number)
                && Objects.equals(phoneType, contact.phoneType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactName, number, phoneType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(contactName);
        dest.writeString(number);
        dest.writeString(phoneType);
    }
}
