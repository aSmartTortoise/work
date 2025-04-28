package com.voyah.cockpit.window.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * author : jie wang
 * date : 2024/6/25 13:36
 * description :
 */
public class PageInfo implements Parcelable {

    /**
     *  当前page的index
     */
    private int position;

    /**
     *  当前page中item的数目
     */
    private int itemCount;

    /**
     *  每一页中item的最大数量
     */
    private int maxItemCount;

    public PageInfo() {
    }

    protected PageInfo(Parcel in) {
        position = in.readInt();
        itemCount = in.readInt();
        maxItemCount = in.readInt();
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel in) {
            return new PageInfo(in);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(itemCount);
        dest.writeInt(maxItemCount);
    }
}
