package com.voice.sdk.device.carservice.vcar

import com.voice.sdk.device.carservice.constants.GearInfo

/**
 * @Date 2025/2/14 11:09
 * @Author 8327821
 * @Email *
 * @Description 针对CarServicePropUtils 抽象的接口
 **/
interface CarServicePropInterface<T> {
    fun getVinCode(): String

    fun getCarType(): String

    fun isH37A(): Boolean

    fun isH37B(): Boolean

    fun isH56C(): Boolean

    fun isH56D(): Boolean

    fun isVCOS15(): Boolean

    fun setIntProp(propid: Int, status: Int): Unit


    fun setIntProp(propid: Int, area: Int, status: Int): Unit


    fun getIntProp(propid: Int): Int


    fun getIntProp(propId: Int, area: Int): Int


    fun setFloatProp(propid: Int, status: Float): Unit


    fun setFloatProp(propid: Int, area: Int, status: Float): Unit


    fun getFloatProp(propid: Int): Float


    fun getFloatProp(propid: Int, area: Int): Float

    fun setRawProp(propid: Int, area: Int, status: Int, sourceId: Int): Unit


    /**
     * get carserver prop the return raw value
     *
     * @param propid
     * @return
     */
    fun getPropertyRaw(propid: Int): CarPropertyValue<T>

    /**
     * get carserver prop the return raw value
     *
     * @param propid
     * @param areaId
     * @return
     */
    fun getPropertyRaw(propid: Int, areaId: Int): CarPropertyValue<T>

    /**
     * 通过sharedUserId来判断当前是车机还是模拟器
     *
     * @return true是车机，false是模拟器
     */
    fun vehicleSimulatorJudgment(): Boolean

    fun registerCallback(cb: CarPropertyEventCallback, ids: Set<Integer>): Unit

    fun unregisterCallback(changeCallback: CarPropertyEventCallback, integerHashSet: HashSet<Integer>): Unit

//    public String getVehicleType() {
//        String vehicleType = System.getProperty("vehicle_model");
//        return StringUtils.isBlank(vehicleType) ? "H37A" : vehicleType;
//    }

    /**
     * 车型配置
     * 0001	装备等级-N1
     * 0010	装备等级-N2
     * 0011	装备等级-N3
     * 0100	装备等级-N4
     * 0101	N1大客户版
     * 0110	N2大客户版
     * 0111	N3大客户版
     * 1000	N1对公版
     * 1001	N2对公版对公版
     * 1010	N3对公版对公版
     * 1011	装备等级-N5
     * 1100	装备等级-G1
     * 1101	装备等级N3+
     * 1110	装备等级N4+
     * 1111	行政版
     *
     * @return
     */
    fun getCarModeConfig(): Int

    /**
     * 获取档位
     *
     * @return 档位  0：P档, 1：R档, 2：N档, 3：D档, 9：无效
     */
    fun getDrvInfoGearPosition(): Int
}