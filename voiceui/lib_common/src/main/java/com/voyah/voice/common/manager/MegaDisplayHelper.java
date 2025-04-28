package com.voyah.voice.common.manager;


import android.content.Context;
import android.hardware.display.DisplayManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.nexus.hardware.display.MegaDisplayManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MegaDisplayHelper {

    private static final Map<Integer, Integer> displayIdCache = new ConcurrentHashMap<>();
    private static DisplayManager displayManager;

    static {
        int mainDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_MAIN);
        int passengerDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_PASSENGER);
        int cellingDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_CEILING);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_MAIN, mainDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_PASSENGER, passengerDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_CEILING, cellingDisplayId);
        LogUtils.d("mainDisplayId:" + mainDisplayId + ", passengerDisplayId:" + passengerDisplayId + ", cellingDisplayId:" + cellingDisplayId);
    }

    public static boolean isMainScreen(int displayId) {
        int screenType = getScreenTypeForDisplayId(displayId);
        return screenType == MegaDisplayManager.SCREEN_TYPE_MAIN;
    }

    public static int getMainScreenDisplayId() {
        Integer displayIdInt = displayIdCache.get(MegaDisplayManager.SCREEN_TYPE_MAIN);
        if (displayIdInt != null && displayIdInt != -1) {
            return displayIdInt;
        } else {
            int displayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_MAIN);
            if (displayId != -1) {
                displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_MAIN, displayId);
                return displayId;
            } else {
                LogUtils.e("getMainScreenDisplayId error");
                return 0;
            }
        }
    }

    public static int getPassengerScreenDisplayId() {
        Integer displayIdInt = displayIdCache.get(MegaDisplayManager.SCREEN_TYPE_PASSENGER);
        if (displayIdInt != null && displayIdInt != -1) {
            return displayIdInt;
        } else {
            int displayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_PASSENGER);
            if (displayId != -1) {
                displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_PASSENGER, displayId);
                return displayId;
            } else {
                LogUtils.e("getPassengerScreenDisplayId error");
                return 0;
            }
        }
    }

    public static int getCeilingScreenDisplayId() {
        Integer displayIdInt = displayIdCache.get(MegaDisplayManager.SCREEN_TYPE_CEILING);
        if (displayIdInt != null && displayIdInt != -1) {
            return displayIdInt;
        } else {
            int displayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_CEILING);
            if (displayId != -1) {
                displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_CEILING, displayId);
                return displayId;
            } else {
                LogUtils.e("getCeilingScreenDisplayId error");
                return 0;
            }
        }
    }

    private static int getDisplayIdForScreenType(int screenType) {
        try {
            if (displayManager == null) {
                displayManager = (DisplayManager) Utils.getApp().getSystemService(Context.DISPLAY_SERVICE);
            }
            return MegaDisplayManager.getDefaultDisplayIdForScreenType(displayManager, screenType);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static int getScreenTypeForDisplayId(int displayId) {
        try {
            if (displayManager == null) {
                displayManager = (DisplayManager) Utils.getApp().getSystemService(Context.DISPLAY_SERVICE);
            }
            return MegaDisplayManager.getScreenTypeForDisplayId(displayManager, displayId);
        } catch (Throwable e) {
            e.printStackTrace();
            return MegaDisplayManager.SCREEN_TYPE_MAIN;
        }
    }


    /**
     * 获取当前语音所在的displayId (H56C动态获取displayID)
     */
    public static int getVoiceDisplayId(int location) {
        int screenType = MegaDisplayManager.SCREEN_TYPE_MAIN;

        if (location == 1) {
            screenType = MegaDisplayManager.SCREEN_TYPE_PASSENGER;
        } else if (location == 2 || location == 3) {
            screenType = MegaDisplayManager.SCREEN_TYPE_CEILING;
        }
        Integer displayIdInt = displayIdCache.get(screenType);
        if (displayIdInt != null && displayIdInt != -1) {
            return displayIdInt;
        } else {
            int displayId = getDisplayIdForScreenType(screenType);
            if (displayId != -1) {
                displayIdCache.put(screenType, displayId);
                return displayId;
            } else {
                LogUtils.e("getDisplayIdForScreenType error, screenType:" + screenType);
                return 0;
            }
        }
    }
}
