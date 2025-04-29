package com.voyah.ai.basecar.helper;


import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.Display;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.nexus.app.MegaUserManager;
import com.mega.nexus.hardware.display.MegaDisplayManager;
import com.mega.nexus.os.MegaUserHandle;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhScreenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MegaDisplayHelper {

    private static final Map<Integer, Integer> displayIdCache = new ConcurrentHashMap<>();
    private static final Map<Integer, UserHandle> userHandleCache = new ConcurrentHashMap<>();

    private static DisplayManager displayManager;

    static {
        buildDisplayIdMap();
        buildUserHandleMap();
    }

    private static void buildDisplayIdMap() {
        boolean isMultiScreen = DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen();
        int mainDisplayId = isMultiScreen ? getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_MAIN) : 0;
        int passengerDisplayId = isMultiScreen ? getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_PASSENGER) : 0;
        int cellingDisplayId = isMultiScreen ? getDisplayIdForScreenType(MegaDisplayManager.SCREEN_TYPE_CEILING) : 0;
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_MAIN, mainDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_PASSENGER, passengerDisplayId);
        displayIdCache.put(MegaDisplayManager.SCREEN_TYPE_CEILING, cellingDisplayId);
        LogUtils.d("mainDisplayId:" + mainDisplayId + ", passengerDisplayId:" + passengerDisplayId + ", cellingDisplayId:" + cellingDisplayId);
    }

    private static void buildUserHandleMap() {
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
            userHandleCache.put(0, MegaUserHandle.SYSTEM);
            return;
        }
        try {
            userHandleCache.clear();
            UserManager userManager = (UserManager) Utils.getApp().getSystemService(Context.USER_SERVICE);
            List<UserHandle> userProfiles = userManager.getUserProfiles();
            if (userProfiles != null && !userProfiles.isEmpty()) {
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
        } catch (Exception e) {
            e.printStackTrace();
            userHandleCache.clear();
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

    private static int getDisplayIdForScreenType(int megaScreenType) {
        try {
            if (displayManager == null) {
                displayManager = (DisplayManager) Utils.getApp().getSystemService(Context.DISPLAY_SERVICE);
            }
            return MegaDisplayManager.getDefaultDisplayIdForScreenType(displayManager, megaScreenType);
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
    public static int getVoiceDisplayId() {
        if (!DialogueManager.get().isInteractionState()) {
            return -1;
        } else {
            int curDirection = DialogueManager.get().getDirection();
            return getVoiceDisplayId(curDirection);
        }
    }

    /**
     * 根据语音方位获取displayId
     */
    public static int getVoiceDisplayId(int direction) {
        int screenType = MegaDisplayManager.SCREEN_TYPE_MAIN;
        if (direction == DhDirection.FRONT_RIGHT) {
            screenType = MegaDisplayManager.SCREEN_TYPE_PASSENGER;
        } else if (direction == DhDirection.REAR_LEFT || direction == DhDirection.REAR_RIGHT) {
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
                LogUtils.e("getVoiceDisplayId error, screenType:" + screenType);
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
        if (userHandleCache.isEmpty()) {
            buildUserHandleMap();
        }
        return userHandleCache.getOrDefault(displayId, MegaUserHandle.SYSTEM);
    }

    public static int getDisplayId(DeviceScreenType deviceScreenType) {
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        int megaScreenType = getMegaScreenTypeFromCustom(deviceScreenType);
        Integer v = displayIdCache.get(megaScreenType);
        return v != null ? v : 0;
    }

    public static int getDisplayId(@DhScreenType int screenType) {
        if (screenType == DhScreenType.SCREEN_MAIN) {
            return getMainScreenDisplayId();
        } else if (screenType == DhScreenType.SCREEN_PASSENGER) {
            return getPassengerScreenDisplayId();
        } else if (screenType == DhScreenType.SCREEN_CEILING) {
            return getCeilingScreenDisplayId();
        }
        return 0;
    }

    public static UserHandle getUserHandle(DeviceScreenType deviceScreenType) {
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        int displayId = getDisplayId(deviceScreenType);
        return userHandleCache.getOrDefault(displayId, MegaUserHandle.SYSTEM);
    }

    public static Context getScreenContext(DeviceScreenType deviceScreenType) {
        int displayId = getDisplayId(deviceScreenType);
        if (displayManager == null) {
            displayManager = (DisplayManager) Utils.getApp().getSystemService(Context.DISPLAY_SERVICE);
        }
        Display display = displayManager.getDisplay(displayId);
        LogUtils.i("getScreenContext:" + display);
        if (display != null) {
            return Utils.getApp().createDisplayContext(display);
        }
        return Utils.getApp();
    }

    /**
     * 将自定义的DeviceScreenType转换成镁佳定义的type
     */
    private static int getMegaScreenTypeFromCustom(DeviceScreenType deviceScreenType) {
        switch (deviceScreenType) {
            case PASSENGER_SCREEN:
                return MegaDisplayManager.SCREEN_TYPE_PASSENGER;
            case CEIL_SCREEN:
                return MegaDisplayManager.SCREEN_TYPE_CEILING;
            default:
                return MegaDisplayManager.SCREEN_TYPE_MAIN;
        }
    }
}
