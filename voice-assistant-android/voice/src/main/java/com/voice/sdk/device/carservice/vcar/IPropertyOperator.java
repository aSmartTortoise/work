package com.voice.sdk.device.carservice.vcar;


/**
 * @Date 2024/6/25 13:48
 * @Author 8327821
 * @Email *
 * @Description 包装中间件，提供车辆属性的接口
 **/
public interface IPropertyOperator {

    //不用区分多位置的参数，尝试读取信号时area 参数可传递 AREA_NONE
    int AREA_NONE = -1;

    default int getIntProp(String key) {
        return getIntProp(key, AREA_NONE);
    }

    int getIntProp(String key, int area);

    default void setIntProp(String key, int value) {
        setIntProp(key, AREA_NONE, value);
    }

    void setIntProp(String key, int area, int value);

    default float getFloatProp(String key) {
        return getFloatProp(key, AREA_NONE);
    }
    float getFloatProp(String key, int area);

    default void setFloatProp(String key, float value) {
        setFloatProp(key, AREA_NONE, value);
    }
    void setFloatProp(String key, int area, float value);

    default String getStringProp(String key) {
        return getStringProp(key, AREA_NONE);
    }
    String getStringProp(String key, int area);

    default void setStringProp(String key, String value) {
        setStringProp(key, AREA_NONE, value);
    }
    void setStringProp(String key, int area, String value);

    default boolean getBooleanProp(String key) {
        return getBooleanProp(key, AREA_NONE);
    }
    boolean getBooleanProp(String key, int area);

    default void setBooleanProp(String key, boolean value) {
        setBooleanProp(key, AREA_NONE, value);
    }
    void setBooleanProp(String key, int area, boolean value);

    String isSupport(String key);
}
