package com.voyah.ai.basecar.viewcmd.accessibility.app;

import android.util.Pair;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BiliApp extends BaseApp {
    public static final String PACKAGE_NAME = "com.bilibili.bilithings";

    private final Map<String, Integer> TAB_MAP = MapUtils.newHashMap(
            new Pair<>("搜索", 0),
            new Pair<>("FM", 1),
            new Pair<>("视频", 2),
            new Pair<>("续播", 3),
            new Pair<>("我的", 4)
    );

    @Override
    public Map<String, String> handle(String topApp, List<String> list, List<String> viewIds) {
        fullNameMap.clear();
        addGestureUpDown();
        addGestureLeftRight();
        buildIdMap(viewIds);
        return fullNameMap;
    }

    @Override
    public List<AccessibilityNodeInfo> selectStrategy(String text, @NonNull List<AccessibilityNodeInfo> list) {
        Integer index = TAB_MAP.get(text);
        if (index != null) {
            LogUtils.d("selectStrategy() called with: text = [" + text + "], index = [" + index + "]");
            AccessibilityNodeInfo nodeInfo = list.get(index);
            return Collections.singletonList(nodeInfo);
        }
        return null;
    }

    private void buildIdMap(List<String> viewIds) {
        fullNameMap.put("搜索", createId("ll_tab_root"));
        fullNameMap.put("FM", createId("ll_tab_root"));
        fullNameMap.put("视频", createId("ll_tab_root"));
        fullNameMap.put("续播", createId("ll_tab_root"));
        fullNameMap.put("我的", createId("ll_tab_root"));

        if (viewIds.contains(createId("setting"))) {
            fullNameMap.put("设置", createId("setting"));
        }
        if (viewIds.contains(createId("switch_audio"))) {
            fullNameMap.put("彩蛋提示音效", createId("switch_audio"));
            fullNameMap.put("使用H264", createId("switch_h264"));
            fullNameMap.put("使用H二六四", createId("switch_h264"));
            fullNameMap.put("ijk解码", createId("switch_codec"));
        } else if (viewIds.contains(createId("switch_personalized"))) {
            fullNameMap.put("个性化内容推荐", createId("switch_personalized"));
        }
    }


    @Override
    protected String packageName() {
        return PACKAGE_NAME;
    }

}
