package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;

public interface FragranceControlInterface {

    /**
     * 香氛开关状态
     * curFragranceState
     * getFragranceSwitchState
     * @param map
     * @return
     */
    boolean getFragranceSwitchState(HashMap<String, Object> map);

    /**
     * 香氛开关
     * fragranceSwitch
     * setFragranceSwitchState
     * @param map
     */
    void setFragranceSwitchState(HashMap<String, Object> map);

    /**
     * 获取香氛模式
     * curFragranceMode
     * getFragranceMode
     * @param map
     * @return
     */
    String getFragranceMode(HashMap<String, Object> map);

    /**
     * 设置香氛模式
     * @param map
     */
    void setFragranceMode(HashMap<String, Object> map);

    /**
     * 获取香氛浓度等级
     * curFragranceConcentrationLevel
     * getFragranceConcentrationLevel
     * @param map
     * @return
     */
    String getFragranceConcentrationLevel(HashMap<String, Object> map);

    /**
     * 设置香氛浓度等级
     * @param map
     * @return
     */
    void setFragranceConcentrationLevel(HashMap<String, Object> map);

    /**
     * 设置香氛时间
     * @param map
     */
    void setFragranceTime(HashMap<String, Object> map);


    boolean getFragranceUiSwitchState(HashMap<String, Object> map);

    void setFragranceUiSwitchState(HashMap<String, Object> map);



}
