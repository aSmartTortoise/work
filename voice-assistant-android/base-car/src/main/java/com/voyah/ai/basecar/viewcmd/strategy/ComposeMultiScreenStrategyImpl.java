package com.voyah.ai.basecar.viewcmd.strategy;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voice.sdk.device.viewcmd.ViewCmdResult;

import java.util.List;

/**
 * 多屏策略
 */
public class ComposeMultiScreenStrategyImpl implements IScreenStrategy {

    private final MasterScreenStrategyImpl masterStrategy;
    private final PassengersScreenStrategyImpl passengersStrategy;
    private final int mainDisplayId;

    public ComposeMultiScreenStrategyImpl() {
        masterStrategy = new MasterScreenStrategyImpl();
        passengersStrategy = new PassengersScreenStrategyImpl();
        mainDisplayId = MegaDisplayHelper.getMainScreenDisplayId();
    }

    @Override
    public void addViewCommands(String pkgName, int displayId, int splitScreenId, List<String> texts) {
        if (displayId == mainDisplayId) {
            masterStrategy.addViewCommands(pkgName, displayId, splitScreenId, texts);
        } else {
            passengersStrategy.addViewCommands(pkgName, displayId, splitScreenId, texts);
        }
    }

    @Override
    public void removeViewCommands(String pkgName, int displayId, int splitScreenId) {
        if (displayId == mainDisplayId) {
            masterStrategy.removeViewCommands(pkgName, displayId, splitScreenId);
        } else {
            passengersStrategy.removeViewCommands(pkgName, displayId, splitScreenId);
        }
    }

    @Override
    public void handleViewCommand(ViewCmdResult result) {
        int curDisplayId = MegaDisplayHelper.getVoiceDisplayId();
        if (curDisplayId == mainDisplayId) {
            masterStrategy.handleViewCommand(result);
        } else {
            passengersStrategy.handleViewCommand(result);
        }
    }

    @Override
    public void triggerViewCmdUpload(int displayId) {
        if (displayId == mainDisplayId) {
            masterStrategy.triggerViewCmdUpload(displayId);
        } else {
            passengersStrategy.triggerViewCmdUpload(displayId);
        }
    }

    @Override
    public void setGlobalViewCmd(String pkg, List<String> list) {
        masterStrategy.setGlobalViewCmd(pkg, list);
        int curDisplayId = MegaDisplayHelper.getVoiceDisplayId();
        if (curDisplayId != mainDisplayId) {
            passengersStrategy.setGlobalViewCmd(pkg, list);
        }
    }

    @Override
    public void setKwsViewCmd(String pkg, List<String> list) {
        masterStrategy.setKwsViewCmd(pkg, list);
        passengersStrategy.setKwsViewCmd(pkg, list);
    }

}
