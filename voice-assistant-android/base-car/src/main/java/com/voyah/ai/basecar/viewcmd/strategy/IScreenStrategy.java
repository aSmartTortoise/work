package com.voyah.ai.basecar.viewcmd.strategy;


import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import java.util.List;

public interface IScreenStrategy {
    /**
     * 注册可见即可说命令
     *
     * @param pkg       包名(带uid)
     * @param displayId     屏幕id
     * @param splitScreenId 分屏id
     * @param texts         热词列表
     */
    void addViewCommands(String pkg, int displayId, @SplitScreenId int splitScreenId, List<String> texts);

    /**
     * 删除可见即可说命令
     *
     * @param pkg       包名(带uid)
     * @param displayId     屏幕id
     * @param splitScreenId 分屏id
     */
    void removeViewCommands(String pkg, int displayId, @SplitScreenId int splitScreenId);

    /**
     * 响应可见
     *
     * @param result 可见结果
     */
    void handleViewCommand(ViewCmdResult result);

    /**
     * 触发扫描
     *
     * @param displayId 屏幕id
     */
    void triggerViewCmdUpload(int displayId);

    /**
     * 设置全局可见热词
     */
    void setGlobalViewCmd(String pkg, List<String> list);

    /**
     * 设置免唤醒可见热词
     */
    void setKwsViewCmd(String pkg, List<String> list);
}
