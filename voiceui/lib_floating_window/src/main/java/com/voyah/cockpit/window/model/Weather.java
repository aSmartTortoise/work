package com.voyah.cockpit.window.model;

/**
 * author : jie wang
 * date : 2024/4/1 16:27
 * description : 天气实体
 */
public class Weather extends MultiItemEntity {
    private String formatDate;

    private String weatherDesc;

    private int weatherIcon;

    private String location;

    private int tempHigh;

    private int tempLow;

    private String weatherDay;

    private String weatherNight;

    private int weatherDayIcon;

    private int weatherNightIcon;



    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(int tempHigh) {
        this.tempHigh = tempHigh;
    }

    public int getTempLow() {
        return tempLow;
    }

    public void setTempLow(int tempLow) {
        this.tempLow = tempLow;
    }

    public String getWeatherDay() {
        return weatherDay;
    }

    public void setWeatherDay(String weatherDay) {
        this.weatherDay = weatherDay;
    }

    public String getWeatherNight() {
        return weatherNight;
    }

    public void setWeatherNight(String weatherNight) {
        this.weatherNight = weatherNight;
    }

    public int getWeatherDayIcon() {
        return weatherDayIcon;
    }

    public void setWeatherDayIcon(int weatherDayIcon) {
        this.weatherDayIcon = weatherDayIcon;
    }

    public int getWeatherNightIcon() {
        return weatherNightIcon;
    }

    public void setWeatherNightIcon(int weatherNightIcon) {
        this.weatherNightIcon = weatherNightIcon;
    }
}
