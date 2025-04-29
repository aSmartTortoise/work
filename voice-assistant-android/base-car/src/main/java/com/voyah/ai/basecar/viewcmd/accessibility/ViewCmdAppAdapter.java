package com.voyah.ai.basecar.viewcmd.accessibility;

import android.content.ComponentName;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.viewcmd.ViewCmdStrategy;
import com.voyah.ai.basecar.viewcmd.accessibility.app.BaseApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 无障碍可见即可说特殊处理适配类
 */
public class ViewCmdAppAdapter {
    private final Map<String, BaseApp> iAppArrayMap;
    public final ArrayMap<String, String> fullNameMap = new ArrayMap<>();

    public ViewCmdAppAdapter(Map<String, BaseApp> map) {
        this.iAppArrayMap = map;
    }

    /**
     * 针对包名和页面的特殊处理
     */
    public List<String> handle(int displayId, String pkgName, final List<String> list, final List<String> viewIds) {
        ComponentName componentName = CommonSystemUtils.getTopComponentName(displayId);
        LogUtils.d("handle() called with: displayId = [" + displayId + "], pkgName = [" + pkgName + "], componentName = [" + componentName + "]");
        fullNameMap.clear();
        BaseApp app = iAppArrayMap.get(pkgName);
        if (componentName != null && TextUtils.equals(componentName.getPackageName(), pkgName) && app != null) {
            Map<String, String> map = app.handle(componentName.getClassName(), list, viewIds);
            if (map != null && map.size() > 0) {
                fullNameMap.putAll(map);
                ArrayList<String> keys = new ArrayList<>(fullNameMap.keySet());
                Collections.sort(keys);
                return keys;
            }
        } else {
            LogUtils.d("ignore handle, pkgName:" + pkgName + ",componentName:" + componentName);
        }
        return null;
    }

    /**
     * 多个结果选择策略
     */
    public List<AccessibilityNodeInfo> selectStrategy(int displayId, String text, @NonNull List<AccessibilityNodeInfo> list) {
        if (list.size() == 0 || list.size() == 1) {
            return list;
        }
        ComponentName componentName = CommonSystemUtils.getTopComponentName(displayId);
        CharSequence pkgName = list.get(0).getPackageName();
        if (pkgName != null) {
            BaseApp app = iAppArrayMap.get(pkgName.toString());
            if (componentName != null && TextUtils.equals(componentName.getPackageName(), pkgName) && app != null) {
                return app.selectStrategy(text, list);
            } else {
                LogUtils.d("ignore selectStrategy");
            }
        }
        return list;
    }

    public ViewCmdStrategy getViewCmdStrategy(String pkgName) {
        if (pkgName == null) {
            return ViewCmdStrategy.UI_TEXT;
        }
        BaseApp app = iAppArrayMap.get(pkgName);
        if (app != null) {
            return app.getViewCmdStrategy();
        } else {
            return ViewCmdStrategy.UI_TEXT;
        }
    }
}
