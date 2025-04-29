package com.voyah.voice.main.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


/**
 * author : jie wang
 * date : 2025/1/13 15:38
 * description :
 */
public class DrawingTimesGoods implements Parcelable {

    private String goodsName;

    private String price;

    private String desc;

    private long id;

    public DrawingTimesGoods() {
    }

    protected DrawingTimesGoods(Parcel in) {
        goodsName = in.readString();
        price = in.readString();
        desc = in.readString();
        id = in.readLong();
    }

    public static final Creator<DrawingTimesGoods> CREATOR = new Creator<DrawingTimesGoods>() {
        @Override
        public DrawingTimesGoods createFromParcel(Parcel in) {
            return new DrawingTimesGoods(in);
        }

        @Override
        public DrawingTimesGoods[] newArray(int size) {
            return new DrawingTimesGoods[size];
        }
    };

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(goodsName);
        dest.writeString(price);
        dest.writeString(desc);
        dest.writeLong(id);
    }

    @Override
    public String toString() {
        return "DrawingTimesGoods{" +
                "goodsName='" + goodsName + '\'' +
                ", price='" + price + '\'' +
                ", desc='" + desc + '\'' +
                ", id=" + id +
                '}';
    }
}
