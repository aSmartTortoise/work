package com.voyah.ai.basecar.viewcmd.strategy;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.viewcmd.ViewCmdCache;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.FeedbackManager;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.R;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.viewcmd.accessibility.AccessibleAbility;
import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主驾屏可见策略
 */
public class MasterScreenStrategyImpl extends AbstractBaseScreenStrategy {

    public MasterScreenStrategyImpl() {
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().addSplitStatusListener(isSplitScreen -> {
            LogUtils.d("screen changed, isSplitScreen:" + isSplitScreen);
            if (isSplitScreen) {
                viewCmdCache.removeViewCommands(null, SplitScreenId.FULL_SCREEN);
            } else {
                viewCmdCache.removeViewCommands(null, SplitScreenId.LEFT_SCREEN);
                viewCmdCache.removeViewCommands(null, SplitScreenId.RIGHT_SCREEN);
            }
        });
    }

    @Override
    public void addViewCommands(String pkgName, int displayId, int splitScreenId, List<String> list) {
        List<String> preList = specialPreHandle(splitScreenId, list);
        List<String> newList = normalize(pkgName, splitScreenId, preList);
        // 无障碍服务只出现在分屏右侧, 或全屏
        if (splitScreenId != SplitScreenId.LEFT_SCREEN && !TextUtils.equals(pkgName, AccessibleAbility.TAG)
                && MegaDisplayHelper.getVoiceDisplayId() == displayId) {
            accessibleAbility.clear();
        }
        if (!TextUtils.equals(pkgName, AccessibleAbility.TAG)) {  // 自注册方式
            // 分屏状态下，座椅/空调/负一屏/All-APP等全屏界面实际以splitScreenId=LEFT_SCREEN上传
            if (splitScreenId == SplitScreenId.LEFT_SCREEN && !pkgName.startsWith(ApplicationConstant.PACKAGE_NAME_MAP)
                    && !pkgName.startsWith(ApplicationConstant.PKG_ADAS)) {
                splitScreenId = SplitScreenId.RIGHT_SCREEN;
                viewCmdCache.removeViewCommands(pkgName, SplitScreenId.LEFT_SCREEN);
            }
            viewCmdCache.addViewCommands(pkgName, splitScreenId, newList);
            if (MegaDisplayHelper.getVoiceDisplayId() == displayId) {
                String topPkg = CommonSystemUtils.getTopPackageName(mainDisplayId);
                LogUtils.d("topPkg:" + topPkg);
                boolean isAccessApp = accessibleAbility.isAccessibilityApp(topPkg);
                if (isAccessApp && splitScreenId == SplitScreenId.LEFT_SCREEN) { // 左侧上传指令集，右侧是无障碍情况
                    accessibleAbility.handAccessibilityEvent(mainDisplayId);
                } else {
                    int other = splitScreenId == SplitScreenId.LEFT_SCREEN ? SplitScreenId.RIGHT_SCREEN
                            : SplitScreenId.LEFT_SCREEN;
                    ViewCmdCache.Cache cache = viewCmdCache.getCache(other);
                    List<String> totalList = new ArrayList<>(newList);
                    if (cache != null) { // 全屏情况为null
                        totalList.addAll(cache.list);
                    }
                    FeedbackManager.get().uploadViewCmd(totalList, globalViewCmdCache, kwsViewCmdCache);
                }
            }
        } else if (MegaDisplayHelper.getVoiceDisplayId() == displayId) { // 无障碍注册方式
            ViewCmdCache.Cache cache = viewCmdCache.getCache(SplitScreenId.LEFT_SCREEN);
            List<String> totalList = new ArrayList<>(newList);
            if (cache != null) { // 全屏情况为null
                totalList.addAll(cache.list);
            }
            FeedbackManager.get().uploadViewCmd(totalList, globalViewCmdCache, kwsViewCmdCache);
        }
    }

    @Override
    public void removeViewCommands(String pkgName, int displayId, int splitScreenId) {
        viewCmdCache.removeViewCommands(pkgName, splitScreenId);
        if (MegaDisplayHelper.getVoiceDisplayId() == displayId) {
            List<String> totalList = new ArrayList<>();
            if (splitScreenId != SplitScreenId.FULL_SCREEN) {
                int other = splitScreenId == SplitScreenId.LEFT_SCREEN ? SplitScreenId.RIGHT_SCREEN
                        : SplitScreenId.LEFT_SCREEN;
                ViewCmdCache.Cache cache = viewCmdCache.getCache(other);
                if (cache != null) {
                    totalList.addAll(cache.list);
                }
            }
            FeedbackManager.get().uploadViewCmd(totalList, globalViewCmdCache, kwsViewCmdCache);
        }
    }

    @Override
    public void handleViewCommand(ViewCmdResult result) {
        LogUtils.d("handleViewCommand() called with: result = [" + result + "]");
        boolean consumed = handleViewCommandInner(ViewCmdCache.KWS_ID, result, ViewCmdType.TYPE_KWS);
        if (consumed) {
            LogUtils.d("handled by kws viewCmd!");
            return;
        }

        if (DeviceHolder.INS().getDevices().getSystem().getKeyboard().isKeyboardShowing(mainDisplayId)) {
            consumed = handleCommonViewCmd(mainDisplayId, result);
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
        boolean isSplitScreening = DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening();
        int splitScreenId = isSplitScreening ? SplitScreenId.RIGHT_SCREEN : SplitScreenId.FULL_SCREEN;
        String newText = makeJson(result);
        Map<String, String> textMap = this.textMap.get(splitScreenId);
        if (textMap != null && textMap.get(newText) != null) {
            newText = textMap.get(newText);
        }
        newText = getTextFromSpecialMap(splitScreenId, newText);
        if ("Widget".equalsIgnoreCase(result.prompt)) {
            consumed = accessibleAbility.handleWidgetViewCommand(mainDisplayId, result.direction, newText);
        }
        if (consumed) {
            LogUtils.d("handled by accessible ability!");
            return;
        }

        consumed = accessibleAbility.handleViewCommand(mainDisplayId, result.direction, newText);
        if (consumed) {
            LogUtils.d("handled by accessible ability!");
            return;
        }

        consumed = handleViewCommandInner(splitScreenId, result, ViewCmdType.TYPE_NORMAL);
        if (consumed) {
            LogUtils.d("handled by app!");
            return;
        }

        if (Utils.getApp().getString(R.string.viewcmd_back).equals(result.text) && DeviceHolder.INS().getDevices().getViewCmd().isShowingTopCoverView(Utils.getApp().getPackageName(), 0)) {
            consumed = handleCommonViewCmd(mainDisplayId, result);
            if (consumed) {
                LogUtils.d("handled by topCoverView!");
                return;
            }
        }

        if (isSplitScreening) {
            consumed = handleViewCommandInner(SplitScreenId.LEFT_SCREEN, result, ViewCmdType.TYPE_NORMAL);
            if (consumed) {
                LogUtils.d("handled by left split screen!");
                return;
            }
        }

        consumed = handleCommonViewCmd(mainDisplayId, result);
        if (!consumed) {
            if (isSplitScreening) {
                consumed = handleViewCommandInner(SplitScreenId.FULL_SCREEN, result, ViewCmdType.TYPE_NORMAL);
                if (consumed) {
                    LogUtils.w("handled by full screen in splitting screen scene!");
                    return;
                }
            }
            LogUtils.e("unhandled, please check the code!!!");
        }
    }

    @Override
    public void setGlobalViewCmd(String pkg, List<String> list) {
        globalViewCmdCache.clear();
        if (list.size() > 0) {
            List<String> newList = normalize(pkg, ViewCmdCache.GLOBAL_ID, list);
            globalViewCmdCache.addViewCommands(pkg, ViewCmdCache.GLOBAL_ID, newList);
        }
    }

    @Override
    public void triggerViewCmdUpload(int displayId) {
        if (DialogueManager.get().isInteractionState()) {
            if (!DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
                ViewCmdCache.Cache cache = viewCmdCache.getCache(SplitScreenId.FULL_SCREEN);
                if (cache != null) {
                    FeedbackManager.get().uploadViewCmd(cache.list, globalViewCmdCache, kwsViewCmdCache);
                } else {
                    accessibleAbility.handAccessibilityEvent(displayId);
                }
            } else {
                String topPkg = CommonSystemUtils.getTopPackageName(displayId);
                LogUtils.d("topPkg:" + topPkg);
                boolean isAccessApp = accessibleAbility.isAccessibilityApp(topPkg);
                if (isAccessApp) {
                    accessibleAbility.handAccessibilityEvent(displayId);
                } else {
                    ViewCmdCache.Cache cacheLeft = viewCmdCache.getCache(SplitScreenId.LEFT_SCREEN);
                    ViewCmdCache.Cache cacheRight = viewCmdCache.getCache(SplitScreenId.RIGHT_SCREEN);
                    List<String> list = new ArrayList<>();
                    if (cacheLeft != null) {
                        list.addAll(cacheLeft.list);
                    }
                    if (cacheRight != null) {
                        list.addAll(cacheRight.list);
                    }
                    FeedbackManager.get().uploadViewCmd(list, globalViewCmdCache, kwsViewCmdCache);
                }
            }
        } else {
            LogUtils.d("Current is not interactive state, ignore triggerViewCmdUpload!!!");
        }
    }
}

