package com.voyah.ai.basecar.viewcmd.accessibility.app;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.voyah.ai.basecar.viewcmd.ViewCmdStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VoHiCoiApp extends BaseApp {
    public static final String PACKAGE_NAME = "com.bytedance.byteautoservice3";
    private static final String ClearScreenActivity = "com.bytedance.awemeopen.biz.carplay.feed.activity.clearscreen.ClearScreenActivity";

    @Override
    public Map<String, String> handle(String topApp, List<String> list, List<String> viewIds) {
        fullNameMap.clear();
        if (list.contains("推荐") && list.contains("长视频") && list.contains("随听")) {
            addGestureUpDown();
            fullNameMap.put("搜索", createId("btn_home_search"));
        } else if (list.contains("个性化推荐") && viewIds.contains(createId("iv_radio"))) {
            fullNameMap.put("个性化推荐", createId("iv_radio"));
        } else if (list.contains("个人信息收集设置") && list.contains("无痕搜索") && list.contains("无痕阅读")) {
            fullNameMap.put("无痕搜索", createId("iv_radio"));
            fullNameMap.put("无痕阅读", createId("iv_radio"));
        } else if (viewIds.contains(createId("tv_privacy_text")) && viewIds.contains(createId("cb_privacy"))) {
            fullNameMap.put("已阅读并同意", createId("cb_privacy"));
            fullNameMap.put("已阅读并同意《个人信息保护指引及免责声明》", createId("cb_privacy"));
        }
        if (ClearScreenActivity.equals(topApp) && viewIds.contains(createId("aoc_compat_back"))) {
            fullNameMap.put("关闭", createId("aoc_compat_back"));
        }
        return fullNameMap;
    }

    @Override
    public List<AccessibilityNodeInfo> selectStrategy(String text, @NonNull List<AccessibilityNodeInfo> list) {
        if (list.size() > 1) {
            if ("无痕搜索".equals(text)) {
                return Collections.singletonList(list.get(0));
            } else if ("无痕阅读".equals(text)) {
                return Collections.singletonList(list.get(1));
            }
        }
        return super.selectStrategy(text, list);
    }

    @Override
    public ViewCmdStrategy getViewCmdStrategy() {
        return ViewCmdStrategy.CONTENT_DESCRIPTION;
    }

    @Override
    protected String packageName() {
        return PACKAGE_NAME;
    }

}
