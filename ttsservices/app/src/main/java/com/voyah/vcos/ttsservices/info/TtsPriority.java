package com.voyah.vcos.ttsservices.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * tts优先级的定义 PO>P1>P2
 */
public enum TtsPriority implements Parcelable {
    /**
     * P2 tts priority.
     */
    P3(3),
    P2(2),
    /**
     * P1 tts priority.
     */
    P1(1),
    /**
     * P0 tts priority.
     */
    P0(0);

    private TtsPriority(int id) {
        this.id = id;
    }


    public int getValue() {
        return this.id;
    }

    private final int id;

    public static final Creator<TtsPriority> CREATOR = new Creator<TtsPriority>() {
        @Override
        public TtsPriority createFromParcel(Parcel in) {
            return TtsPriority.valueOf(in.readString());
        }

        @Override
        public TtsPriority[] newArray(int size) {
            return new TtsPriority[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name());
    }
}
