package com.voyah.ai.basecar.viewcmd;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.viewcmd.AccessibleAbilityInterface;
import com.voice.sdk.device.viewcmd.ViewCmdInterface;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.viewcmd.accessibility.AccessibleAbility;
import com.voyah.ai.basecar.viewcmd.strategy.ViewCmdScreenContext;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ViewCmdInterfaceImpl implements ViewCmdInterface {

    public final AccessibleAbilityInterface accessibleAbility;
    private final Map<Integer, CopyOnWriteArrayList<String>> topCoverViewMap = new ConcurrentHashMap<>();
    private final List<String> singleViewPkgList = Arrays.asList(ApplicationConstant.PKG_VOICEUI, ApplicationConstant.PKG_LAUNCHER);

    public ViewCmdInterfaceImpl() {
        accessibleAbility = new AccessibleAbility();
        accessibleAbility.init();
    }

    @Override
    public void handleViewCommand(ViewCmdResult result) {
        ViewCmdScreenContext.getInstance().handleViewCommand(result);
    }


    @Override
    public void addViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId, List<String> list) {
        if (isAbnormalUpload(pkgName, displayId, list)) {
            LogUtils.d("ignore abnormal upload, pkg:" + pkgName);
            return;
        }
        ViewCmdScreenContext.getInstance().addViewCommands(pkgName, displayId, splitScreenId, list);
    }

    @Override
    public void removeViewCommands(String pkgName, int displayId, @SplitScreenId int splitScreenId) {
        ViewCmdScreenContext.getInstance().removeViewCommands(pkgName, displayId, splitScreenId);
    }

    private boolean isAbnormalUpload(String pkgName, int displayId, List<String> list) {
        // 咪咕视频行车限制的弹窗特殊处理
        if (pkgName.startsWith(ApplicationConstant.PKG_MIGU_VIDEO)) {
            if (list != null && list.size() > 0) {
                String str = String.join(",", list);
                if (str.contains("进入音频模式") && str.contains("否")) {
                    String topPackageName = CommonSystemUtils.getTopPackageName(displayId);
                    return topPackageName != null && !TextUtils.equals(ApplicationConstant.PKG_MIGU_VIDEO, topPackageName);
                }
            }
        }
        return false;
    }

    @Override
    public void triggerViewCmdUpload(int displayId) {
        ViewCmdScreenContext.getInstance().triggerViewCmdUpload(displayId);
    }


    @Override
    public void setGlobalViewCmd(String pkg, List<String> list) {
        ViewCmdScreenContext.getInstance().setGlobalViewCmd(pkg, list);
    }

    @Override
    public void setKwsViewCmd(String pkg, List<String> list) {
        ViewCmdScreenContext.getInstance().setKwsViewCmd(pkg, list);
    }

    @Override
    public boolean isAccessibilityServiceRunning() {
        return accessibleAbility.isAccessibilityServiceRunning();
    }

    @Override
    public void enableAccessibilityService(boolean enable) {
        accessibleAbility.enableAccessibilityService(enable);
    }

    @Override
    public AccessibleAbilityInterface getAccessibleAbility() {
        return accessibleAbility;
    }

    @Override
    public boolean isShowingTopCoverView(String callPkg, int displayId) {
        CopyOnWriteArrayList<String> list = topCoverViewMap.get(displayId);
        boolean ret = list != null && list.size() > 0;
        if (ret) {
            LogUtils.d("isShowingTopCoverView, callPkg:" + callPkg + ", displayId:" + displayId + ", list:" + list);
        }
        return ret;
    }

    @Override
    public void setTopCoverViewShowing(String pkgName, int displayId, boolean isShow) {
        LogUtils.d("setTopCoverViewShowing() called with: pkgName:" + pkgName + ", displayId:" + displayId + ", isShow:" + isShow);
        if (displayId != -1) {
            CopyOnWriteArrayList<String> list = topCoverViewMap.get(displayId);
            if (isShow) {
                if (list == null) {
                    list = new CopyOnWriteArrayList<>();
                }
                list.add(pkgName);
                topCoverViewMap.put(displayId, list);
            } else if (list != null) {
                if (singleViewPkgList.contains(pkgName)) {
                    list.removeIf(s -> TextUtils.equals(s, pkgName));
                } else {
                    list.remove(pkgName);
                }
                if (list.size() == 0) {
                    topCoverViewMap.remove(displayId);
                } else {
                    topCoverViewMap.put(displayId, list);
                }
            }
        } else { // 根据包名查找删除
            for (Map.Entry<Integer, CopyOnWriteArrayList<String>> entry : topCoverViewMap.entrySet()) {
                CopyOnWriteArrayList<String> list = entry.getValue();
                list.removeIf(s -> TextUtils.equals(s, pkgName));
                if (list.size() == 0) {
                    topCoverViewMap.remove(entry.getKey());
                } else {
                    topCoverViewMap.put(entry.getKey(), list);
                }
            }
        }
    }

    @Override
    public boolean isSupportNluViewCmd() {
        return false;
    }

    /**
     * 对于FLAG_NOT_FOCUSABLE的弹窗在移除时，手动触发处于前台的应用的扫描
     * <p>
     * 设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE的弹窗在消失后，无法监听到windowFocus变化
     */
    @SuppressLint("MissingPermission")
    @Override
    public void triggerViewCmdScan(String callPkg, int displayId, boolean isFocus) {
        Intent intent = new Intent(ApplicationConstant.ACTION_WIEWCMD_TRIGGER_CHANGED);
        List<String> list = topCoverViewMap.get(displayId);
        if (list != null && !list.isEmpty()) {
            LogUtils.d("triggerViewCmdScan() called, displayId:" + displayId + ", list: " + list);
            Set<String> set = new HashSet<>(list);
            // 如果all-app与其它topCoverView共存，则不触发launcher重新扫描
            if (set.size() > 1) {
                set.remove(ApplicationConstant.PKG_LAUNCHER);
            }
            for (String pkgName : set) {
                if (ApplicationConstant.PKG_SYSTEMUI_PLUGIN.equals(pkgName)) {
                    intent.setPackage(ApplicationConstant.PKG_SYSTEMUI);
                } else {
                    intent.setPackage(pkgName);
                }
                intent.putExtra("displayId", displayId);
                if (DeviceHolder.INS().getDevices().getSystem().getApp().isSupportMulti(pkgName)) {
                    Utils.getApp().sendBroadcastAsUser(intent, MegaDisplayHelper.getUserHandleByDisplayId(displayId));
                } else {
                    Utils.getApp().sendBroadcast(intent);
                }
            }
        } else if (!isFocus) {
            String pkgName = CommonSystemUtils.getTopPackageName(displayId);
            LogUtils.d("triggerViewCmdScan() called, displayId:" + displayId + ", pkgName:" + pkgName);
            if (pkgName != null) {
                intent.setPackage(pkgName);
                intent.putExtra("displayId", displayId);
                if (DeviceHolder.INS().getDevices().getSystem().getApp().isSupportMulti(pkgName)) {
                    Utils.getApp().sendBroadcastAsUser(intent, MegaDisplayHelper.getUserHandleByDisplayId(displayId));
                } else {
                    Utils.getApp().sendBroadcast(intent);
                }
            }
        }
    }
}
