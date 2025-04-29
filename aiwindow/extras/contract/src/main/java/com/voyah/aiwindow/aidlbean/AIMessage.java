package com.voyah.aiwindow.aidlbean;

import android.os.Parcel;
import android.os.Parcelable;

public class AIMessage implements Parcelable {
    /**
     * 包名
     */
    public String pkgName;

    /**
     * 插件view类名
     */
    public String clazz;
    /**
     * 数据
     */
    public String data;
    /**
     * 宽
     */
    public int width;
    /**
     * 高
     */
    public int height;
    /**
     * 优先级
     */
    public Priority priority = Priority.DEFAULT;

    public AIMessage(String pkgName, String clazz, String data) {
        this.pkgName = pkgName;
        this.clazz = clazz;
        this.data = data;
    }

    public static final Creator<AIMessage> CREATOR = new Creator<AIMessage>() {
        @Override
        public AIMessage createFromParcel(Parcel in) {
            return new AIMessage(in);
        }

        @Override
        public AIMessage[] newArray(int size) {
            return new AIMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected AIMessage(Parcel in) {
        pkgName = in.readString();
        clazz = in.readString();
        data = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pkgName);
        dest.writeString(clazz);
        dest.writeString(data);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public String toString() {
        return "AIMessage{" +
                "pkgName='" + pkgName + '\'' +
                ", clazz='" + clazz + '\'' +
                ", data='" + data + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", priority=" + priority +
                '}';
    }
}
