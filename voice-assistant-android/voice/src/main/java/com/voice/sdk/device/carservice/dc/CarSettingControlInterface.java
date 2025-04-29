package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;

public interface CarSettingControlInterface {

    /** ---------------------------双怠速--start----------------------------------------*/

    boolean isHybridModel(HashMap<String, Object> map);

    /** ---------------------------双怠速--end-------------------------------------------*/


    /**-----------------------------后排车窗禁用------start-----------------------------------*/

    //判断后排车窗禁用开关是否打开
    boolean isOpenBackWindowLock(HashMap<String, Object> map);
    //执行打开后排车窗禁用开关
    void openTheBackWindowSwitch(HashMap<String, Object> map);
    //执行关闭后排车窗禁用开关
    void closeTheBackWindowSwitch(HashMap<String, Object> map);

    /**-----------------------------后排车窗禁用------end-------------------------------------*/


    /**-----------------------------后车标灯--------start----------------------------------*/
    //判断后车标灯是否打开，  false为没有打开，true为打开
    boolean isOpenRearBadgeLights(HashMap<String, Object> map);
    //执行打开后车标灯
    void openTheRearBadgeLightsSwitch(HashMap<String, Object> map);
    //执行关闭后车标灯
    void closeTheRearBadgeLightsSwitch(HashMap<String, Object> map);

    /**-----------------------------后车标灯--------end-------------------------------------*/

    /**-----------------------------强制纯电--------start-------------------------------------*/
    //判断当前是否是纯电优先模式  true 是纯电优先  false 不是纯电优先
    boolean isModePower(HashMap<String, Object> map);
    boolean isOpenModePower(HashMap<String, Object> map);

    //跳转到打开强制纯电的页面
    void jumpToModePower(HashMap<String, Object> map);
    //判断当前强制纯电是否关闭  false 没有关闭 true代表关闭
     boolean isCloseModePower(HashMap<String, Object> map);
    //执行关闭 强制纯电
    void closeTheModePower(HashMap<String, Object> map);

    /**-----------------------------强制纯电---------end-------------------------------------*/



}
