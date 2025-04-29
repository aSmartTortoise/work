package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/6/26 18:43
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class VCarSeatSignal {
    public static final String SEAT_HEAT_CUR_LEVEL = "seatHeatLevel"; //座椅加热挡位

    public static final String SEAT_VENT_CUR_LEVEL = "seatVentLevel"; //座椅通风挡位

    //按摩
    public static final String MASSAGE_CUR_SWITCH = "seatMassageSwitch"; //座椅按摩挡位
    public static final String MASSAGE_CUR_LEVEL = "seatMassageLevel"; //座椅按摩挡位
    public static final String MASSAGE_MODE = "seatMassageMode"; //座椅按摩模式

    //座椅调节
    public static final String SEAT_ADJUST_POSITION = "seatAdjustPosition"; //座椅前后调节
    public static final String SEAT_ADJUST_BACK = "seatAdjustBack"; //座椅靠背调节
    public static final String SEAT_SUPPORT_MANUAL_ADJUST_BACK = "seatSupportManualadJustBack"; //座椅靠背是否支持手动调节（对tts回复有影响）
    public static final String SEAT_ADJUST_CUSHION_HEIGHT = "seatAdjustCushionHeight"; //座椅高度调节
    public static final String SEAT_ADJUST_CUSHION_ANGLE = "seatAdjustCushionAngle"; //坐垫角度调节
    public static final String SEAT_OCCUPIED_STATUS = "seatOccupiedStatus"; //座位上是否有人
    public static final String SEAT_ADJUST_STOP_MOVE = "seatAdjustStopMove"; //座椅调节的停止移动
    public static final String SEAT_COMFORTABLY_STOP_MOVE = "seatComfortablyStopMove"; //座椅一键舒躺/一键零重力的停止移动
    public static final String SEAT_FOLD_STOP_ADJUST = "seatFoldStopAdjust"; //座椅折叠/展开的停止移动
    public static final String SEAT_POSITION_STOP_MOVE = "seatPositionStopMove"; //座椅位置激活的停止移动
    public static final String SEAT_MIRROR_SAVE = "seatMirrorSave"; //将当前后视镜角度位置，保存到制定槽位
    public static final String SEAT_MIRROR_RECOVERY = "seatMirrorRecovery";
    public static final String SEAT_MEMORY_SAVE = "seatMemorySave"; //将当前座椅位置，保存到制定槽位
    public static final String SEAT_MEMORY_READ = "seatMemoryRead"; //读取指定槽位的座椅数据
    public static final String SEAT_MEMORY_RECOVERY = "seatMemoryRecovery"; //将座椅位置设置成指定槽位的数据
    public static final String SEAT_ONEKEY_RESET = "seatOnekeyReset"; //副驾座椅一键恢复
    public static final String SEAT_WELCOME_MODE_SWITCH = "seatWelcomeModeSwitch"; //座椅迎宾模式开关
    public static final String SECOND_SEAT_WELCOME_MODE_SWITCH = "secondSeatWelcomeModeSwitch"; //后排座椅迎宾模式开关
    public static final String BABY_SEAT_STATE = "BABY_SEAT_STATE"; //儿童座椅是否可用
    public static final String BABY_SEAT_COUNT = "babySeatCount"; //儿童座椅连接数
    public static final String BABY_SEAT_HEAT_SWITCH = "babySeatHeatSwitch"; //儿童座椅加热开关
    public static final String BABY_SEAT_VENT_SWITCH = "babySeatVentSwitch"; //儿童座椅通风开关
    public static final String CAR_STATE_DRIVING = "carStateDriving"; //车辆状态 是否驾驶中
    public static final String SEAT_PAGE_SATE = "seat_page_sate"; //座椅界面
    public static final String SEAT_ZERO_GRAVITY_CONFIG = "seatZeroGravityConfig"; //是否支持座椅零重力
    public static final String SEAT_ZERO_GRAVITY = "seatZeroGravity"; //座椅零重力
    public static final String SEAT_FOLD = "seatFold"; //座椅折叠
    public static final String SEAT_SUPPORT_ADJUST_THIRD_SIDE = "seatSupportAdjustThirdSide"; //是否支持的是3排折叠展开
    public static final String SEAT_FOLD_CONFIG = "seatFoldConfig"; //座椅折叠配置
    public static final String SEAT_USER_STATE = "seatUserState"; //座椅使用情况
    public static final String SEAT_CUR_FOLD_POSITION = "seaCurPosition"; //座椅折叠位置
    public static final String SEAT_SAVE_PAGE_SATE = "seat_SavePageSate"; //座椅记忆保存弹窗
    public static final String SEAT_SUPPORT_ADJUST_CONFIG = "seatSupportAdjustConfig";
    public static final String SEAT_STOP_MOVE = "seatStopMove"; //座椅停止移动
    public static final String SEAT_MESSAGE_SUPPORT_TEXT = "seatMessageSupportText";
    public static final String SEAT_ONEKEY_RESET_OLAY_FIRST_ROW_RIGHT = "seatOnekeyResetOlayFirstRowRight";
    public static final String SEAT_PAGE_VENTANDHEAT = "SEAT_PAGE_VENTANDHEAT";
    public static final String SEAT_PAGE_MASSAGE = "SEAT_PAGE_MASSAGE";
    public static final String SEAT_PAGE_COMFORT_ENJOY = "SEAT_PAGE_COMFORT_ENJOY";
    public static final String SEAT_PAGE_FRONT_MEM_ADJUST = "SEAT_PAGE_FRONT_MEM_ADJUST";
    public static final String SEAT_PAGE_2ROW_MEM_ADJUST = "SEAT_PAGE_2ROW_MEM_ADJUST";
    public static final String SEAT_PAGE_3ROW_MEM_ADJUST = "SEAT_PAGE_3ROW_MEM_ADJUST";

}
