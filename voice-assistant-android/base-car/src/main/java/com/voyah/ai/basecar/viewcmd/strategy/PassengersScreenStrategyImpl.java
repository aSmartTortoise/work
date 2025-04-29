package com.voyah.ai.basecar.viewcmd.strategy;


import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.nexus.os.MegaScreenManager;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.IScreenStateChangeListener;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voice.sdk.device.viewcmd.ViewCmdCache;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.FeedbackManager;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.viewcmd.accessibility.AccessibleAbility;
import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 非主驾屏可见策略
 */
public class PassengersScreenStrategyImpl extends AbstractBaseScreenStrategy {

    public PassengersScreenStrategyImpl() {
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, new IScreenStateChangeListener() {

            @Override
            public void onScreenStateChanged(int screenId, int state) {
                LogUtils.d("onScreenStateChanged() called with: screenId = [" + screenId + "], state = [" + state + "]");
                if (MegaScreenManager.SCREENID_CEILING == screenId && MegaScreenManager.STATE_ON == state) {
                    DeviceHolder.INS().getDevices().getViewCmd().triggerViewCmdScan(Utils.getApp().getPackageName(),
                            MegaDisplayHelper.getCeilingScreenDisplayId(), false);
                }
            }

            @Override
            public boolean isPersistent() {
                return true;
            }
        });
    }

    @Override
    public void addViewCommands(String pkgName, int displayId, int splitScreenId, List<String> list) {
        IScreenStrategy strategy = ViewCmdScreenContext.getInstance().getCurStrategy();
        if (strategy instanceof PassengersScreenStrategyImpl && splitScreenId == SplitScreenId.LEFT_SCREEN) {
            LogUtils.d("No support split screen at yet, ignore!!!");
            return;
        }
        if (displayId == ceilingDisplayId && !isCeilOpen()) {
            LogUtils.d("addViewCommands called(), celling screen has closed!");
            list = new ArrayList<>();
        }
        List<String> preList = specialPreHandle(displayId, list);
        List<String> newList = normalize(pkgName, displayId, preList);
        if (!TextUtils.equals(pkgName, AccessibleAbility.TAG)) {  // 自注册方式
            viewCmdCache.addViewCommands(pkgName, displayId, newList);
            if (MegaDisplayHelper.getVoiceDisplayId() == displayId) {
                accessibleAbility.clear();
            }
        }
        if (MegaDisplayHelper.getVoiceDisplayId() == displayId) {
            FeedbackManager.get().uploadViewCmd(newList, globalViewCmdCache, kwsViewCmdCache);
        }
    }

    @Override
    public void removeViewCommands(String pkgName, int displayId, int splitScreenId) {
        viewCmdCache.removeViewCommands(pkgName, displayId);
        if (MegaDisplayHelper.getVoiceDisplayId() == displayId) {
            FeedbackManager.get().uploadViewCmd(new ArrayList<>(), globalViewCmdCache, kwsViewCmdCache);
        }
    }

    @Override
    public void handleViewCommand(ViewCmdResult result) {
        LogUtils.d("handleViewCommand() called with: nluStr = [" + result + "]");
        boolean consumed = handleViewCommandInner(ViewCmdCache.KWS_ID, result, ViewCmdType.TYPE_KWS);
        if (consumed) {
            LogUtils.d("handled by kws viewCmd!");
            return;
        }
        int displayId = MegaDisplayHelper.getVoiceDisplayId();

        if (DeviceHolder.INS().getDevices().getSystem().getKeyboard().isKeyboardShowing(displayId)) {
            consumed = handleCommonViewCmd(displayId, result);
            if (consumed) {
                LogUtils.d("handled by inputMethod!");
                return;
            }
        }

        consumed = handleViewCommandInner(ViewCmdCache.GLOBAL_ID, result, ViewCmdType.TYPE_GLOBAL);
        if (consumed) {
            LogUtils.d("handled by global viewCmd!");
            return;
        }

        String newText = makeJson(result);
        Map<String, String> textMap = this.textMap.get(displayId);
        if (textMap != null && textMap.get(newText) != null) {
            newText = textMap.get(newText);
        }
        newText = getTextFromSpecialMap(displayId, newText);
        consumed = accessibleAbility.handleViewCommand(displayId, result.direction, newText);
        if (consumed) {
            LogUtils.d("handled by accessible ability!");
            return;
        }
        consumed = handleViewCommandInner(displayId, result, ViewCmdType.TYPE_NORMAL);
        if (consumed) {
            LogUtils.d("handled by app!");
            return;
        }

        consumed = handleCommonViewCmd(displayId, result);
        if (!consumed) {
            LogUtils.e("unhandled, please check the code!!!");
        }
    }

    @Override
    public void setGlobalViewCmd(String pkg, List<String> list) {
        triggerViewCmdUpload(MegaDisplayHelper.getVoiceDisplayId());
    }

    @Override
    public void triggerViewCmdUpload(int displayId) {
        if (DialogueManager.get().isInteractionState()) {
            ViewCmdCache.Cache cache = viewCmdCache.getCache(displayId);
            if (cache != null) {  // 如果对应displayId有缓存，则使用缓存上传
                FeedbackManager.get().uploadViewCmd(cache.list, globalViewCmdCache, kwsViewCmdCache);
            } else {
                accessibleAbility.handAccessibilityEvent(displayId);
            }
        } else {
            LogUtils.d("Current is not interactive state, ignore triggerViewCmdUpload!!!");
        }
    }
}
