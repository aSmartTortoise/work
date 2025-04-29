package com.voice.sdk.device.viewcmd;

import java.util.List;

public interface AccessibleAbilityInterface {

    void init();

    /**
     * 辅助功能是否启动
     */
    boolean isAccessibilityServiceRunning();

    void enableAccessibilityService(boolean enable);

    void clear();

    boolean isAccessibilityApp(String pkgName);

    void handAccessibilityEvent(int displayId);

    boolean handleViewCommand(int displayId, int direction, String text);

    List<String> getWidgetUiText(int displayId, List<String> list);

    boolean handleWidgetViewCommand(int displayId, int direction, String text);
}
