package com.voyah.ai.basecar.viewcmd.accessibility.app;

import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.voyah.ai.basecar.viewcmd.ViewCmdStrategy;
import com.voyah.ai.basecar.viewcmd.accessibility.ResponseType;
import com.voyah.ai.basecar.viewcmd.accessibility.ViewCmdGesture;

import java.util.List;
import java.util.Map;

public abstract class BaseApp {

    protected final ArrayMap<String, String> fullNameMap = new ArrayMap<>();
    protected static final String REGEX_PURE_DIGITS = "^\\d+$";

    private static final String[] CMDS_GESTURE_UP = new String[]{
            "上滑",
            "往上滑",
            "向上滑",
            "再往上滑",
            "再向上滑",
            "往上划",
            "向上划"
    };

    private static final String[] CMDS_GESTURE_DOWN = new String[]{
            "下滑",
            "往下滑",
            "向下滑",
            "再往下滑",
            "再向下滑",
            "往下划",
            "向下划"
    };

    private static final String[] CMDS_GESTURE_LEFT = new String[]{
            "左滑",
            "往左滑",
            "向左滑",
            "再往左滑",
            "再向左滑",
            "往左划",
            "向左划"
    };

    private static final String[] CMDS_GESTURE_RIGHT = new String[]{
            "右滑",
            "往右滑",
            "向右滑",
            "再往右滑",
            "再向右滑",
            "往右划",
            "向右划"
    };

    public Map<String, String> handle(String topApp, List<String> list, List<String> viewIds) {
        return null;
    }

    public List<AccessibilityNodeInfo> selectStrategy(String text, @NonNull List<AccessibilityNodeInfo> list) {
        return list;
    }

    protected abstract String packageName();

    protected String createId(String viewId) {
        return String.format("%s%s/%s", this.packageName(), ResponseType.ID.type(), viewId);
    }

    protected String createText(String text) {
        return String.format("%s%s/%s", this.packageName(), ResponseType.TEXT.type(), text);
    }

    protected String createGesture(ViewCmdGesture gesture) {
        return this.createGesture(this.packageName(), gesture);
    }

    protected String createGesture(String pkg, ViewCmdGesture gesture) {
        return String.format("%s%s/%s", pkg, ResponseType.GESTURE.type(), gesture.direct());
    }

    protected static void removePureDigits(List<String> list) {
        list.removeIf(s -> s.matches(REGEX_PURE_DIGITS));
    }

    protected void addGestureUpDown() {
        for (String up : CMDS_GESTURE_UP) {
            this.fullNameMap.put(up, createGesture(ViewCmdGesture.UP));
        }
        for (String down : CMDS_GESTURE_DOWN) {
            this.fullNameMap.put(down, createGesture(ViewCmdGesture.DOWN));
        }
    }

    protected void addGestureLeftRight() {
        for (String left : CMDS_GESTURE_LEFT) {
            this.fullNameMap.put(left, createGesture(ViewCmdGesture.LEFT));
        }
        for (String right : CMDS_GESTURE_RIGHT) {
            this.fullNameMap.put(right, createGesture(ViewCmdGesture.RIGHT));
        }
    }

    public ViewCmdStrategy getViewCmdStrategy() {
        return ViewCmdStrategy.UI_TEXT;
    }
}
