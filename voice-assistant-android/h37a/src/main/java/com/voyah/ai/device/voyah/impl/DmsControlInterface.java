//package com.voyah.ai.device.voyah.impl;
//
//import java.util.HashMap;
//
///**
// * @author:lcy
// * 方便后续会使用到，暂时先注释掉保留
// * @data:2024/4/8
// **/
//public interface DmsControlInterface {
//    //隐私保护是否已开启
//    boolean isPrivacyProtectionOpen(HashMap<String, Object> map);
//
//    //疲劳监测是否已开启
//    boolean isFatigueMonitorOpen(HashMap<String, Object> map);
//
//    //当前是否为指定模式
//    boolean isCurrentDesignated(HashMap<String, Object> map);
//
//    //切换为指定模式
//    void adjustDesignatedType(HashMap<String, Object> map);
//
//    //疲劳驾驶监测调节-模糊意图
//    void adjustType(HashMap<String, Object> map);
//
//    //打开疲劳监测
//    void switchFatigue(HashMap<String, Object> map);
//
//    //打开\关闭分心及危险行为监测
//    void switchDistract(HashMap<String, Object> map);
//
//    //分心及危险行为监测是否已打开
//    boolean isDistractOpen(HashMap<String, Object> map);
//
//    //是否为主驾
//    boolean isFirstRowLeft(HashMap<String, Object> map);
//
//}
