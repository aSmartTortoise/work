package com.voice.sdk.device.phone.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author:lcy
 * @data:2025/3/5
 **/
public class CallLogInfo implements Comparable<CallLogInfo>, Cloneable, Parcelable {
    private String name;
    private int type;
    public byte[] headPhotoId;
    private String number;
    private String date;
    private String time;
    private long longTime;
    private int count;
    private int highlightedStart = 0;
    private int highlightedEnd = 0;
    public static final Parcelable.Creator<CallLogInfo> CREATOR = new Parcelable.Creator<CallLogInfo>() {
        public CallLogInfo createFromParcel(Parcel in) {
            return new CallLogInfo(in);
        }

        public CallLogInfo[] newArray(int size) {
            return new CallLogInfo[size];
        }
    };
    private String matchPin = "";
    private String namePinYin = "";
    private List<String> namePinyinList = new ArrayList();
    public static final int MATCHED_TYPE_DEFAULT = 0;
    public static final int MATCHED_TYPE_NAME = 1;
    public static final int MATCHED_TYPE_NUMBER = 2;
    public static final int MATCHED_TYPE_ALL = 3;
    private int matchType = 0;
    private int index = -1;

    public CallLogInfo() {
    }

    protected CallLogInfo(Parcel in) {
        this.name = in.readString();
        this.type = in.readInt();
        this.headPhotoId = in.createByteArray();
        this.number = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.longTime = in.readLong();
        this.count = in.readInt();
        this.highlightedStart = in.readInt();
        this.highlightedEnd = in.readInt();
        this.matchPin = in.readString();
        this.namePinYin = in.readString();
        this.namePinyinList = in.createStringArrayList();
        this.matchType = in.readInt();
        this.index = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeByteArray(this.headPhotoId);
        dest.writeString(this.number);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeLong(this.longTime);
        dest.writeInt(this.count);
        dest.writeInt(this.highlightedStart);
        dest.writeInt(this.highlightedEnd);
        dest.writeString(this.matchPin);
        dest.writeString(this.namePinYin);
        dest.writeStringList(this.namePinyinList);
        dest.writeInt(this.matchType);
        dest.writeInt(this.index);
    }

    public int describeContents() {
        return 0;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHighlightedStart() {
        return this.highlightedStart;
    }

    public void setHighlightedStart(int highlightedStart) {
        this.highlightedStart = highlightedStart;
    }

    public int getHighlightedEnd() {
        return this.highlightedEnd;
    }

    public void setHighlightedEnd(int highlightedEnd) {
        this.highlightedEnd = highlightedEnd;
    }

    public boolean isEqual(CallLogInfo info) {
        return this.number.equals(info.number) && this.type == info.type;
    }

    public String toString() {
        return "CallLogInfo{name='" + this.name + '\'' + ", type=" + this.type + ", number='" + this.number + '\'' + ", date='" + this.time + '\'' + ", count=" + this.count + '\'' + '}';
    }

    public List<String> getNamePinyinList() {
        return this.namePinyinList;
    }

    public void setNamePinyinList(List<String> namePinyinList) {
        this.namePinyinList = namePinyinList;
    }

    public String getMatchPin() {
        return this.matchPin;
    }

    public void setMatchPin(String matchPin) {
        this.matchPin = matchPin;
    }

    public String getNamePinYin() {
        return this.namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public Object clone() {
        CallLogInfo info = null;

        try {
            info = (CallLogInfo)super.clone();
        } catch (CloneNotSupportedException var3) {
            var3.printStackTrace();
        }

        return info;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            CallLogInfo info = (CallLogInfo)obj;
            return Objects.equals(this.name, info.name) && Objects.equals(this.number, info.number);
        }
    }

    public int getMatchType() {
        return this.matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public int compareTo(CallLogInfo o) {
        return 0;
    }

    public void setHeadPhotoDB(byte[] photoId) {
        this.headPhotoId = photoId;
    }

    public byte[] getHeadPhotoDB() {
        return this.headPhotoId;
    }

    public Bitmap getCalllogHeadPhotoImage() {
        if (this.headPhotoId == null) {
            return null;
        } else {
            Bitmap headBitmap = BitmapFactory.decodeByteArray(this.headPhotoId, 0, this.headPhotoId.length);
            return headBitmap;
        }
    }

    public void setIndex(int value) {
        this.index = value;
    }

    public int getIndex() {
        return this.index;
    }

    public void setLongTime(long time) {
        this.longTime = time;
    }

    public long getLongTime() {
        return this.longTime;
    }
}
