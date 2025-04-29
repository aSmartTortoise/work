package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/7/22 14:12
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class WindowSignal {
    public static final String WINDOW_CHILD_LOCK = "window_ChildLock"; //儿童锁
    public static final String WINDOW_WINDOW = "window_Window"; //车窗（多位置），127代表需要手动学习
    public static final String WINDOW_DRIVER = "window_Driver";//主驾车窗
    public static final String WINDOW_PASSENGER= "window_Passenger";//副驾车窗
    public static final String WINDOW_LEFTREAR= "window_LeftRear";//左后车窗
    public static final String WINDOW_RIGHTREAR= "window_RightRear";//右后车窗

    public static final String WINDOW_CHILDLOCKLEFT = "window_ChildLockLeft";//左侧车窗禁用（非儿童锁，这个替代儿童锁信号）

    public static final String WINDOW_CHILDLOCKRIGHT = "window_ChildLockRight";//左侧车窗禁用（非儿童锁，这个替代儿童锁信号）


    public static final String WINDOW_SETDRIVER = "window_SetDriver";//设置主驾车窗开合度
    public static final String WINDOW_SETPASSENGER = "window_SetPassenger";//设置副驾车窗开合度
    public static final String WINDOW_SETLEFTREAR = "window_SetLeftRear";//设置左后车窗开合度
    public static final String WINDOW_SETRIGHTREAR = "window_SetRightRear";//设置右后车窗开合度
}
