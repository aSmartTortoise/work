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

    private Integer tempHigh;

    private Integer tempLow;

    private Integer tempHighDateRange;
    private Integer tempLowDateRange;

    private String weatherDay;

    private String weatherNight;

    private int weatherDayIcon;

    private int weatherNightIcon;

    private String windDirDay;

    private String windDirNight;

    private String windLevelDay;

    private String windLevelNight;



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

    public Integer getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(Integer tempHigh) {
        this.tempHigh = tempHigh;
    }

    public Integer getTempLow() {
        return tempLow;
    }

    public void setTempLow(Integer tempLow) {
        this.tempLow = tempLow;
    }

    public Integer getTempHighDateRange() {
        return tempHighDateRange;
    }

    public void setTempHighDateRange(Integer tempHighDateRange) {
        this.tempHighDateRange = tempHighDateRange;
    }

    public Integer getTempLowDateRange() {
        return tempLowDateRange;
    }

    public void setTempLowDateRange(Integer tempLowDateRange) {
        this.tempLowDateRange = tempLowDateRange;
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

    public String getWindDirDay() {
        return windDirDay;
    }

    public void setWindDirDay(String windDirDay) {
        this.windDirDay = windDirDay;
    }

    public String getWindDirNight() {
        return windDirNight;
    }

    public void setWindDirNight(String windDirNight) {
        this.windDirNight = windDirNight;
    }

    public String getWindLevelDay() {
        return windLevelDay;
    }

    public void setWindLevelDay(String windLevelDay) {
        this.windLevelDay = windLevelDay;
    }

    public String getWindLevelNight() {
        return windLevelNight;
    }

    public void setWindLevelNight(String windLevelNight) {
        this.windLevelNight = windLevelNight;
    }
}
