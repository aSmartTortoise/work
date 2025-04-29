package com.voyah.vcos.manager;


import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.nexus.app.MegaUserManager;
import com.mega.nexus.hardware.display.MegaDisplayManager;
import com.mega.nexus.os.MegaUserHandle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MegaDisplayHelper {

    private static final Map<Integer, Integer> displayIdCache = new ConcurrentHashMap<>();
    private static final Map<Integer, UserHandle> userHandleCache = new ConcurrentHashMap<>();
    private static DisplayManager displayManager;
    private static int mainDisplayId, passengerDisplayId, cellingDisplayId;

    static {
        initDisplayId();
        initUserHandle();
    }

    private static void initDisplayId() {
        mainDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_MAIN);
        passengerDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_PASSENGER);
        cellingDisplayId = getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_CEILING);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_MAIN, mainDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_PASSENGER, passengerDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_CEILING, cellingDisplayId);
        LogUtils.d("mainDisplayId:" + mainDisplayId + ", passengerDisplayId:" + passengerDisplayId + ", cellingDisplayId:" + cellingDisplayId);
    }

    private static void initUserHandle() {
        UserManager userManager = (UserManager) Utils.getApp().getSystemService(Context.USER_SERVICE);
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        if (userProfiles != null && userProfiles.size() >= 3) {
            HashMap<UserHandle, Integer> list =
                    MegaUserManager.getMatchingListOfDisplayTypeAndUser(userProfiles);
            list.forEach((userHandle, value) -> {
                Integer displayInt = displayIdCache.get(value);
                int displayId = displayInt != null ? displayInt : getDisplayIdForScreenType(value);
                userHandleCache.put(displayId, userHandle);
            });
        }
        userHandleCache.put(getMainScreenDisplayId(), MegaUserHandle.SYSTEM);
        for (Map.Entry<Integer, UserHandle> entry : userHandleCache.entrySet()) {
            LogUtils.d("displayId:" + entry.getKey() + ", userHandle:" + entry.getValue());
        }
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

    /**
     * 通过 displayId 查找 UserHandle
     *
     * @param displayId 屏幕id
     */
    public static UserHandle getUserHandleByDisplayId(int displayId) {
        return userHandleCache.get(displayId);
    }

    public static int getDisplayIdByMyUserHandle() {
        UserHandle myUserHandle = Process.myUserHandle();
        int myUid = Process.myUid();

        LogUtils.i("getDisplayIdByMyUserHandle myUserHandle:" + myUserHandle + " myUid:" + myUid);
        int targetDisplayId = getMainScreenDisplayId();
        for (Map.Entry<Integer, UserHandle> entry : userHandleCache.entrySet()) {
            UserHandle userHandle = entry.getValue();
            int displayId = entry.getKey();
            LogUtils.i("getDisplayIdByMyUserHandle userHandle:" + userHandle
                    + " displayId:" + displayId);

            if (myUserHandle == userHandle) {
                targetDisplayId = displayId;
                LogUtils.i("getDisplayIdByMyUserHandle match userHandle targetDisplayId:" + targetDisplayId);
                break;
            }
        }
        LogUtils.i("getDisplayIdByMyUserHandle targetDisplayId:" + targetDisplayId);
        return targetDisplayId;
    }

    public static String getScreenTypeByMyUserHandle() {
        UserHandle myUserHandle = Process.myUserHandle();
        int myUid = Process.myUid();

        LogUtils.i("getScreenTypeByMyUserHandle myUserHandle:" + myUserHandle + " myUid:" + myUid);
        int targetDisplayId = getMainScreenDisplayId();
        for (Map.Entry<Integer, UserHandle> entry : userHandleCache.entrySet()) {
            UserHandle userHandle = entry.getValue();
            int displayId = entry.getKey();
            LogUtils.i("getScreenTypeByMyUserHandle userHandle:" + userHandle
                    + " displayId:" + displayId);

            if (myUserHandle == userHandle) {
                targetDisplayId = displayId;
                LogUtils.i("getScreenTypeByMyUserHandle match userHandle targetDisplayId:" + targetDisplayId);
                break;
            }
        }
        LogUtils.i("getScreenTypeByMyUserHandle targetDisplayId:" + targetDisplayId);
        String targetScreenType = "center";
        if (targetDisplayId == passengerDisplayId) {
            targetScreenType = "assistant";
        } else if (targetDisplayId == cellingDisplayId) {
            targetScreenType = "ceiling";
        }

        LogUtils.i("getScreenTypeByMyUserHandle targetScreenType:" + targetScreenType);

        return targetScreenType;
    }
}
