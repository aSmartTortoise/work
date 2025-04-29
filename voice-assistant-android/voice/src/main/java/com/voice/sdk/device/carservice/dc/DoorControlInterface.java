package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;

public interface DoorControlInterface {
    /**
     *  当前滑移门是否打开的状态
     */
    boolean scratchDoorIsOpen(HashMap<String, Object> map);

    /**
     * 打开车门
     */
    void  setOpenScratch (HashMap<String, Object> map);


    /**
     * 关闭车门
     */
    void setCloseScratch(HashMap<String, Object> map);

    /**
     *滑移门是否暂停，默认没有暂停
     */
    boolean isPauseScratch(HashMap<String, Object> map);


    /**
     *执行暂停滑移门
     * @param map
     */
    void doingPauseScratch(HashMap<String, Object> map);


    /**
     * 判断是否是p挡  以及判断当前中控锁是否解锁
     * 只有是P档且解锁的时候，才可以触发 滑移门相关的功能
     * @param map
     * @return
     */
    boolean getFrontStatus(HashMap<String, Object> map);


    /**
     *
     * @return  滑移门电动开关是否开启  默认是开启
     */
    boolean isSwitchOpen(HashMap<String, Object> map);


    /**
     * 判断用户是否说的是打开左侧滑移门 或者是关闭左侧滑移门
     */
    boolean isOpenLeftScratch(HashMap<String, Object> map);


    /**
     * 判断用户是否说的是打开右侧滑移门或者是关闭右侧滑移门
     */
    boolean isOpenRightScratch(HashMap<String, Object> map);


    /**
     * 判断声源位置是否是前排
     */
    boolean isFrontPosition(HashMap<String, Object> map);



    /**
     * 滑移门是否已经开启   默认没开启
     * @param map
     * @return
     */
    boolean isCloseScratch(HashMap<String, Object> map);


    /**
     * 滑移门是否是开启中   默认不是开启中
     * @return
     */
    boolean isOpeningScratch(HashMap<String, Object> map);


    /**
     * 滑移门是否是关闭中   默认不是关闭中
     * @return
     */
    boolean isCloseingScratch(HashMap<String, Object> map);


    /**
     * 判断哪个滑移门没有关闭就执行关闭
     */
    void setScratchToClose(HashMap<String, Object> map);

    /**
     * 判断滑移门是否已经关闭
     */
    boolean isClosed(HashMap<String, Object> map);


    /**
     * 判断两个滑移门中是否有一个没有关闭
     */
    boolean isClosedTwoDoor(HashMap<String, Object> map);

    /**
     * 判断用户说的是 电动还是手动的控制方式  false 手动  true电动
     */
    boolean isMaintenanceAdjust(HashMap<String, Object> map);

    /**
     * 判断当前是否已经是电动调节滑移门  true 已经开启电动调节  false 还未开启电动调节
     */
    boolean isElectric(HashMap<String, Object> map);


    /**
     * 设置成电动调节滑移门
     */
    void doingElectric(HashMap<String, Object> map);

    /**
     * 判断当前是否已经是手动滑移门  true 已经是手动滑移门  false 是电动滑移门
     */
    boolean isNotElectric(HashMap<String, Object> map);

    /**
     *设置成手动调节滑移门
     */
    void doingNotElectric(HashMap<String, Object> map);



}
