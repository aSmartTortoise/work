package com.voyah.ai.basecar.viewcmd.strategy;


import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import java.util.List;

public class ViewCmdScreenContext {

    private final IScreenStrategy mStrategy;

    public static ViewCmdScreenContext getInstance() {
        return Holder._INSTANCE;
    }

    private ViewCmdScreenContext() {
        mStrategy = new ComposeMultiScreenStrategyImpl();
    }

    public void addViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId, List<String> texts) {
        mStrategy.addViewCommands(pkgName, displayId, splitScreenId, texts);
    }

    public void removeViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId) {
        mStrategy.removeViewCommands(pkgName, displayId, splitScreenId);
    }

    public void handleViewCommand(ViewCmdResult result) {
        mStrategy.handleViewCommand(result);
    }

    public void triggerViewCmdUpload(int displayId) {
        mStrategy.triggerViewCmdUpload(displayId);
    }

    public void setGlobalViewCmd(String pkg, List<String> list) {
        mStrategy.setGlobalViewCmd(pkg, list);
    }

    public void setKwsViewCmd(String pkg, List<String> list) {
        mStrategy.setKwsViewCmd(pkg, list);
    }

    public IScreenStrategy getCurStrategy() {
        return mStrategy;
    }

    private static class Holder {
        private static final ViewCmdScreenContext _INSTANCE = new ViewCmdScreenContext();
    }
}
