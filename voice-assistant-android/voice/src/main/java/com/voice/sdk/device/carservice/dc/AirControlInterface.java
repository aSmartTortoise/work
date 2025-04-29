package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;

/**
 * 空调相关设备能力接口
 */
public interface AirControlInterface {
    /**
     * 空调当前打开状态
     * @return
     */
    boolean getAirSwitchState(HashMap<String, Object> map);

    /**
     * 空调开关
     *
     * @param map
     */
    void setAirSwitchState(HashMap<String, Object> map);

    /**
     * 空调温区同步开关状态
     *
     * @param map
     * @return
     */
    boolean getAirTempSynState(HashMap<String, Object> map);


    /**
     * 空调温区同步开关
     *
     * @param map
     */
    void setAirTempSynState(HashMap<String, Object> map);


    /**
     * 当前空调温度
     *
     * @param map
     * @return
     */
    float getAirTemp(HashMap<String, Object> map);

    /**
     * 设置空调温度
     *
     * @param map
     */
    void setAirTemp(HashMap<String, Object> map);

    /**
     * 获取空调最小温度
     * @param map
     * @return
     */
    float getMinAirTemp(HashMap<String, Object> map);


    /**
     * 获取空调最大温度
     * @param map
     * @return
     */
    float getMaxAirTemp(HashMap<String, Object> map);



    /**
     * 获取当前风量
     * @param map
     * @return
     */
    int getAirWind(HashMap<String, Object> map);


    /**
     * 获取空调最大风量
     *
     * @param map
     * @return
     */
    int getMaxAirWind(HashMap<String, Object> map);

    /**
     * 获取空调最小风量
     *
     * @param map
     * @return
     */
    int getMinAirWind(HashMap<String, Object> map);

    /**
     * 设置空调风量
     *
     * @param map
     */
    void setAirWind(HashMap<String, Object> map);

    /**
     * 空调制冷开关状态
     *
     * @param map
     * @return
     */
    boolean getACSwitchStatus(HashMap<String, Object> map);

    /**
     * 空调制冷开关
     *
     * @param map
     */
    void setACSwitchStatus(HashMap<String, Object> map);

    /**
     * 当前空调吹风模式
     *
     * @param map
     * @return
     */
    String getWindMode(HashMap<String, Object> map);

    /**
     * 设置吹风模式
     *
     * @param map
     */
    void setWindMode(HashMap<String, Object> map);

    /**
     * 获取当前空调扫风模式
     *
     * @return
     */
    String getScavengingWindMode(HashMap<String, Object> map);


    /**
     * 设置当前空调扫风模式
     * @param map
     */
    void setScavengingWindMode(HashMap<String, Object> map);

    /**
     * 前除霜开关状态
     *
     * @param map
     * @return
     */
    boolean getFrontDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 后除霜开关状态
     *
     * @param map
     * @return
     */
    boolean getRearDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 前除霜开关
     *
     * @param map
     */
    void setFrontDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 后除霜开关
     *
     * @param map
     */
    void setRearDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 当前空调循环模式
     *
     * @param map
     * @return
     */
    String getAirCycleMode(HashMap<String, Object> map);

    /**
     * 设置空调循环模式
     *
     * @param map
     * @return
     */
    void setAirCycleMode(HashMap<String, Object> map);

    /**
     * 当前空调自动循环开关状态
     *
     * @param map
     * @return
     */
    boolean getAirCycleAutoState(HashMap<String, Object> map);


    /**
     * 设置空调自动循环状态。
     * @param map
     */
    void setAirCycleAutoState(HashMap<String, Object> map);


    /**
     * 当前空调pm2.5的值
     *
     * @param map
     * @return
     */
    int getAirPM(HashMap<String, Object> map);

    /**
     * 当前空调干燥模式
     *
     * @param map
     * @return
     */
    String getDryMode(HashMap<String, Object> map);

    /**
     * 设置当前空调干燥模式
     * @param map
     */
    void setDryMode(HashMap<String, Object> map);

    /**
     * 当前干燥模式开关状态
     *
     * @return
     */
    boolean getDryModeSwitchState(HashMap<String, Object> map);

    /**
     * 当前干燥模式开关
     *
     * @param map
     */
    void setDryModeSwitchState(HashMap<String, Object> map);

    /**
     * 当前通风降温开关状态
     *
     * @param map
     * @return
     */
    boolean getVentilationSwitchState(HashMap<String, Object> map);

    /**
     * 通风降温开关设置
     *
     * @param map
     */
    void setVentilationSwitchState(HashMap<String, Object> map);

    /**
     * 当前自动除霜开关状态。
     *
     * @param map
     * @return
     */
    boolean getAutoDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 自动除霜开关设置。
     *
     * @param map
     */
    void setAutoDefrostSwitchState(HashMap<String, Object> map);

    /**
     * 当前自动除霜等级获取
     *
     * @param map
     * @return
     */
    String getAutoDefrostLevel(HashMap<String, Object> map);


    /**
     * 获取自动除霜最大等级
     * @param map
     * @return
     */
    String getMaxAutoDefrostLevel(HashMap<String, Object> map);

    /**
     * 获取自动除霜最小等级
     * @param map
     * @return
     */
    String getMinAutoDefrostLevel(HashMap<String, Object> map);

    /**
     * 设置自动除霜等级
     * @param map
     */
    void setAutoDefrostLevel(HashMap<String, Object> map);

    /**
     * 当前Auto开关状态
     *
     * @param map
     * @return
     */
    boolean getAutoSwitchState(HashMap<String, Object> map);

    /**
     * 设置Auto开关
     *
     * @param map
     */
    void setAutoSwitchState(HashMap<String, Object> map);


    /**
     * 判断当前的位置信息是否包含副驾，不同车型判断不一样。
     *
     * @param map
     * @return
     */
    boolean curPositionIsContainRightPosition(HashMap<String, Object> map);


    /**
     * 判断当前的位置信息是否只包含副驾，不同车型判断不一样。
     *
     * @param map
     * @return
     */
    boolean curPositionIsOnlyContainRightPosition(HashMap<String, Object> map);

    /**
     * 修改空调执行逻辑中的位置信息。
     * @param map
     */
    void changePositionInAir(HashMap<String, Object> map);

    /**
     * 判断当前位置信息是否支持对应的出风模式调节。
     * 当空调数量是1的时候，所有模式都支持。
     * 当空调数量大于1的时候，后排空调不支持带有吹窗的模式
     * @param map
     * @return
     */
    boolean judgeCurrentPositionWindModIsSupportMode(HashMap<String, Object> map);


    /**
     * 获取负离子开关状态
     * @param map
     * @return
     */
    boolean getAnionSwitchState(HashMap<String, Object> map);

    /**
     * 设置负离子开关状态
     * @param map
     */
    void setAnionSwitchState(HashMap<String, Object> map);

    /**
     * 获取自动负离子开关状态
     * @param map
     * @return
     */
    boolean getAnionAutoSwitchState(HashMap<String, Object> map);

    /**
     * 设置自动负离子开关状态
     * @param map
     */
    void setAnionAutoSwitchState(HashMap<String, Object> map);

    /**
     * 获取智能识别开关状态
     * @param map
     * @return
     */
    boolean getIntelligentIdentificationSwitchStatus(HashMap<String, Object> map);

    /**
     * 设置智能识别开关状态
     * @param map
     */
    void setIntelligentIdentificationSwitchStatus(HashMap<String, Object> map);

    /**
     * 获取快速制冷开关状态。
     * @param map
     * @return
     */
    boolean getFastCoolingSwitchState(HashMap<String, Object> map);

    /**
     * 设置快速制冷开关状态。
     * @param map
     */
    void setFastCoolingSwitchState(HashMap<String, Object> map);


    /**
     * 获取快速制热开关状态。
     * @param map
     * @return
     */
    boolean getFastHeaterSwitchState(HashMap<String, Object> map);

    /**
     * 设置快速制热开关状态。
     * @param map
     */
    void setFastHeaterSwitchState(HashMap<String, Object> map);

    /**
     * 获取出风口开关状态。
     * @param map
     * @return
     */
    boolean getOutletSwitchState(HashMap<String, Object> map);

    /**
     * 设置出风口开关状态。
     * @param map
     */
    void setOutletSwitchState(HashMap<String, Object> map);


    /**
     * 获取空调界面开关状态
     * @param map
     * @return
     */
    boolean getAirUiSwitchState(HashMap<String, Object> map);


    /**
     * 设置空调界面开关
     * @param map
     */
    void setAirUiSwitchState(HashMap<String, Object> map);


    /**
     * 判断当前屏幕信息是否是兜底屏幕信息
     * @param map
     * @return
     */
    boolean isDouDiScreen(HashMap<String, Object> map);

    /**
     * 判断当前车是否有多个屏幕
     * @param map
     * @return
     */
    boolean isCarHasMoreScreen(HashMap<String, Object> map);

    /**
     * 当前除霜打开后,判断传入的数据是否不包含副驾
     * @param map
     * @return
     */
    boolean getBeforeDefrostingOpenPositionIsSupportTempChange(HashMap<String, Object> map);



}
