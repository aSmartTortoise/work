package com.voice.sdk.device.carservice.signal;

public class DoorSignal {

    public static final String DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT = "door_ElectricSwitchIsOpenLeft";//左侧滑移门电动开关是否开启
    public static final String DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT = "door_ElectricSwitchIsOpenRight";//右侧滑移门电动开关是否开启

    public static final String DOOR_SWITCH_LEFT = "door_SwitchLeft";//打开/关闭/暂停  左侧滑移门信号
    public static final String DOOR_SWITCH_RIGHT = "door_SwitchRight";//打开/关闭/暂停  右侧滑移门信号

    public static final String DOOR_IS_OPEN_LEFT = "door_IsOpenLeft";//左侧滑移门是否已开启  已关闭
    public static final String DOOR_IS_OPEN_RIGHT = "door_IsOpenRight";//右侧滑移门是否已开启  已关闭


    public static final String DOOR_OPENING_LEFT = "door_OpeningLeft";//左侧是否是开启中  关闭中
    public static final String DOOR_OPENING_RIGHT = "door_OpeningRight";//右侧是否是开启中  关闭中

    public static final String  DOOR_ELECTRIC_SWITCH = "door_ElectricSwith";//电动手动 滑移门切换开关


    public static final String DOOR_ELECTRIC_SUCTION = "door_electricSuction";//电吸门开关  H37B

    public static final String DOOR_ELECTRIC_SUCTION_GET = "door_electricSuctionGet";//H56D 获取电吸门功能开关的状态 get

    public static final String DOOR_ELECTRIC_SUCTION_SET = "door_electricSuctionSet";//H56D 设置电吸门功能开关的状态 set

}
