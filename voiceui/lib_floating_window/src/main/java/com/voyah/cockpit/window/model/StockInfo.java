package com.voyah.cockpit.window.model;

import android.util.Printer;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/7/1 19:28
 * description :
 */
public class StockInfo extends MultiItemEntity {

    private String name;

    private String code;

    private String date;

    private Double price;

    /**
     *  股价涨跌幅
     */
    private Double priceAmplitude;

    /**
     *  股价涨跌比率
     */
    private Double amplitudeRate;

    /**
     *  股价涨跌状态 rise fall stable
     */
    private int status;

    /**
     *  币种
     */
    private String currency;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceAmplitude() {
        return priceAmplitude;
    }

    public void setPriceAmplitude(Double priceAmplitude) {
        this.priceAmplitude = priceAmplitude;
    }

    public Double getAmplitudeRate() {
        return amplitudeRate;
    }

    public void setAmplitudeRate(Double amplitudeRate) {
        this.amplitudeRate = amplitudeRate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int getItemType() {
        return ViewType.STOCK_TYPE;
    }

    @IntDef({StockInfo.Status.RISE,
            StockInfo.Status.FALL,
            StockInfo.Status.STABLE
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {

        int RISE = 0;
        int FALL = 1;
        int STABLE = 2;

    }



}
