package com.voice.sdk.device.dvr;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2025/4/15
 **/
public interface CmsInterface {
    //------------------------电子后视镜--------------------------

    //是否支持电子后视镜
    boolean isSupportCms(HashMap<String, Object> map);

    //电子后视镜是否已打开
    boolean isBackMirrorOpen(HashMap<String, Object> map);

    //是否为指定摄像头
    boolean isTargetMirrorType(HashMap<String, Object> map);

    //电子后视镜当前摄像头状态 1：宝宝守护 2：电子后视镜
    int getBackMirrorType(HashMap<String, Object> map);

    //切换电子后视镜摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
    void switchbackMirrorType(HashMap<String, Object> map);

    //电子后视镜切换到指定摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
    void adjustBackMirrorType(HashMap<String, Object> map);

    //电子后视镜当前是否为全屏
    boolean isBackMirrorInBigWindow(HashMap<String, Object> map);

    //电子后视镜切换为全屏、小窗 全屏设置true 小窗设置为 false
    void zoomBackMirror(HashMap<String, Object> map);

    //切换电子后视镜摄像头 1:后排宝宝守护 4:电子后视镜
    void switchBackMirror(HashMap<String, Object> map);

    //当前是否为R挡
    boolean isOnlyGearsRForbidden(HashMap<String, Object> map);

    //CRW是否已打开
    boolean isCrwOpen(HashMap<String, Object> map);

    //开启悬浮窗
    void startFloatCamera(HashMap<String, Object> map);

    //关闭悬浮窗
    void stopFloatCamera(HashMap<String, Object> map);

    //是否指定中控屏
    boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map);

    //是否指定吸顶屏且吸顶屏不存在
    boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map);

    boolean isHaveCeilScreen(HashMap<String, Object> map);

    boolean isCeilScreen(HashMap<String, Object> map);

    //是否为指定副驾屏幕
    boolean isTargetScreen(HashMap<String, Object> map);

    //是否仅为声源位置
    boolean isOnlySoundLocation(HashMap<String, Object> map);
}
