package com.voice.sdk.device.viewcmd;

import java.util.List;


public interface ViewCmdInterface {

    /**
     * 响应可见即可说语义
     *
     * @param result 可见结果
     */
    void handleViewCommand(ViewCmdResult result);

    /**
     * 注册可见即可说命令
     *
     * @param pkgName       包名
     * @param displayId     屏幕ID
     * @param splitScreenId 分屏ID
     * @param texts         热词集合
     */
    void addViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId, List<String> texts);

    /**
     * 删除可见即可说命令
     *
     * @param pkgName       包名
     * @param displayId     屏幕ID
     * @param splitScreenId 分屏ID
     */
    void removeViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId);

    /**
     * 触发可见即可说UI文本集合的上传
     *
     * @param displayId 屏幕ID
     */
    void triggerViewCmdUpload(int displayId);

    /**
     * 触发可见即可说扫描
     */
    void triggerViewCmdScan(String callPkg, int display, boolean isFocus);

    /**
     * 设置全局可见热词
     */
    void setGlobalViewCmd(String pkg, List<String> list);

    /**
     * 设置免唤醒可见热词
     */
    void setKwsViewCmd(String pkg, List<String> list);

    /**
     * 无障碍服务是否正在运行
     */
    boolean isAccessibilityServiceRunning();

    /**
     * 无障碍服务开关
     */
    void enableAccessibilityService(boolean enable);

    /**
     * 获取无障碍服务处理ability
     */
    AccessibleAbilityInterface getAccessibleAbility();

    /**
     * 是否正在显示空调/座椅/ALL-APP等特殊view
     */
    boolean isShowingTopCoverView(String callPkg, int displayId);

    /**
     * 设置某特殊view显示和消失
     */
    void setTopCoverViewShowing(String pkgName, int displayId, boolean isShow);

    /**
     * 是否支持nlu的可见
     */
    boolean isSupportNluViewCmd();
}
