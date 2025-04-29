package com.voyah.ai.device.voyah.h37.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.voyah.h37.launcher.LauncherManager;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mega.car.config.H56D;

/**
 * @author:lcy
 * @data:2024/10/29
 **/
public class LauncherViewUtils {
    public static final String TAG = "LauncherViewUtils";

    public static final List<String> LOCATION_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
    public static final List<String> SCREEN_LIST = Arrays.asList("central_screen", "passenger_screen", "entertainment_screen", "scratch_screen", "ceil_screen");

    public static final Map<String, String> SCREEN_STR = new HashMap<String, String>() {
        {
            put("ceil_screen", "吸顶屏");
            put("central_screen", "中控屏");
            put("passenger_screen", "副驾屏");
            put("entertainment_screen", "娱乐屏");
            put("instrument_screen", "仪表屏");
            put("scratch_screen", "滑移屏");
        }
    };
    public static final String SCREEN = "screen"; //屏幕(没有指定单一的屏幕)
    public static final List<String> FIRST_SCREEN = Arrays.asList("central_screen", "scratch_screen");
    public static final List<String> THIRD_SCREEN = Arrays.asList("entertainment_screen", "ceil_screen");
    public static final String ENTERTAINMENT_SCREEN = "entertainment_screen"; //娱乐屏(需要根据指定位置和声源位置来判断是副驾屏还是吸顶屏)
    public static final String launcherPackageName1 = "com.crystal.h37.arcreator";
    public static final String ACTION_DOCK_TOUCH = "com.voyah.cockpit.navigationbar.dock_touch";//DCKER栏点击事件广播
    public static final String NEGATIVE_SCREEN_TAG = "NegativeScreen"; //负一屏
    public static final String ALL_APPS_TAG = "NegativeScreen"; //应用中心
    public static final String AIR_PAGE_TAG = "AirPage"; //空调弹窗
    public static final String SEAT_PAGE = "SeatPage"; //座椅弹窗
    public static final String PARK_PAGE = "PARK_PAGE"; //泊车辅助弹窗
    public static final String SCREEN_CLEAN_PAGE = "SCREEN_CLEAN_PAGE"; //屏幕清洁弹窗
    public static final String SCREEN_PROTECTOR = "screen_protector";//参数key
    public final static String EXTRA_DOCK_TOUCH_TYPE = "extra_dock_touch_type";//参数key
    public final static int FLAG_RECEIVER_INCLUDE_BACKGROUND = 0x01000000;//后台可监听
    public static final int TYPE_NONE = 0;//表示点击空白处

    public static final String launcherPackageName = "com.voyah.cockpit.launcher"; //主屏
    public static final String launcherPackageNameSecondary = "com.voyah.cockpit.launcher.secondary"; //副屏
    public static final String launcherPackageNameCeiling = "com.voyah.cockpit.launcher.ceiling"; //娱乐屏


    public static final Map<String, String> launcherPackageNames = new HashMap() {
        {
            put(FuncConstants.VALUE_SCREEN_CENTRAL, launcherPackageName);
            put(FuncConstants.VALUE_SCREEN_PASSENGER, launcherPackageNameSecondary);
            put(FuncConstants.VALUE_SCREEN_CEIL, launcherPackageNameCeiling);
        }
    };

    private static List<String> tags = new ArrayList<>();

    //负一屏弹窗
    public static boolean isNegativeScreenOpen() {
        boolean isNegativeScreenOpen = SystemUiUtils.getInstance().isNegativeScreenOpen();
//        LogUtils.d(TAG, "isNegativeScreenOpen:" + isNegativeScreenOpen);
        return isNegativeScreenOpen;
    }

    //应用中心弹窗
    public static boolean isAllAppsOpen(Context context) {
        boolean isAllAppsOpen = DeviceHolder.INS().getDevices().getLauncher().isAllAppPanelShowing();
//        LogUtils.d(TAG, "isAllAppsOpen:" + isAllAppsOpen);
        return isAllAppsOpen;
    }

    public static boolean isSecondAllAppPanelShowing(Context context) {
        boolean isSecondAllAppPanelShowing = DeviceHolder.INS().getDevices().getLauncher().isSecondAllAppPanelShowing();
        return isSecondAllAppPanelShowing;
    }

    public static boolean isCeilAllAppPanelShowing(Context context) {
        boolean isCeilAllAppPanelShowing = DeviceHolder.INS().getDevices().getLauncher().isCeilAllAppPanelShowing();
        return isCeilAllAppPanelShowing;
    }

    //空调弹窗
    public static boolean isAirOpen(String screenType) {
        closeScreenCentral(screenType, AIR_PAGE_TAG);
        boolean isAirOpen = SystemUiUtils.getInstance().isAirPageOpen(screenType);
//        boolean isAirOpen = CommonDeviceInterface.getInstance().isAirOpen(context);
//        LogUtils.d(TAG, "isAirOpen:" + isAirOpen);
        return isAirOpen;
    }

    //座椅弹窗
    public static boolean isSeatPageOpen(Context context, String screenType) {
        boolean isSeatPageOpen = SystemUiUtils.getInstance().isSeatPageOpen(screenType);
//        LogUtils.d(TAG, "isSeatPageOpen:" + isSeatPageOpen);
        return isSeatPageOpen;
    }

    //泊车辅助弹窗(360)
    public static boolean isParkOpen() {
        int intProp = CarPropUtils.getInstance().getIntProp(H56D.APA_avmState_APA_AVMSTS);
        // 0=关闭 1=360打开 2=自动泊车打开
        boolean isParkOpen = intProp == 1;
//        LogUtils.d(TAG, "isParkOpen intProp:" + intProp + " ,isParkOpen:" + isParkOpen);
        return isParkOpen;
    }

    //桌面是否在前台
    public static boolean isLauncherView(Context context, String assignScreen) {
        if (StringUtils.isBlank(assignScreen))
            assignScreen = FuncConstants.VALUE_SCREEN_CENTRAL;
        boolean isScreenPopWindowShowTop = isScreenPopWindowShowTop(true, assignScreen);
        boolean isLauncherView = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(launcherPackageNames.get(assignScreen), DeviceScreenType.fromValue(assignScreen));
        LogUtils.d(TAG, "isLauncherView isScreenPopWindowShowTop:" + isScreenPopWindowShowTop + " ,isLauncherView:" + isLauncherView);
        return !isScreenPopWindowShowTop && isLauncherView;
    }


    /**
     * 获取指定屏幕是否有霸屏弹窗展示
     *
     * @param assignScreen 指定屏幕FuncConstants类
     * @return
     */
    public static boolean isScreenPopWindowShowTop(boolean isShowLog, String assignScreen) {
        //负一屏
        boolean isNegativeScreenShow = SystemUiUtils.getInstance().isNegativeScreenOpen(assignScreen);
        //应用中心
        boolean isAllAppsShow = SystemUiUtils.getInstance().isAssignScreenAllAppsOpen(Utils.getApp(), assignScreen);
        //空调
        boolean isAirPageOpen = SystemUiUtils.getInstance().isAirPageOpen(assignScreen);
        //座椅
        boolean isSeatPageOpen = SystemUiUtils.getInstance().isSeatPageOpen(assignScreen);
        //泊车辅助
        boolean isParkingOpen = SystemUiUtils.getInstance().isParkingOpen(Utils.getApp(), assignScreen);
        //屏幕清洁模式
        boolean isScreenCleanMode = SystemUiUtils.getInstance().isScreenCleanMode();
        if (isShowLog)
            LogUtils.d(TAG, "isScreenPopWindowShowTop isNegativeScreenShow:" + isNegativeScreenShow +
                    " ,isAllAppsShow:" + isAllAppsShow + ",isAirPageOpen:" + isAirPageOpen + ",isSeatPageOpen:" + isSeatPageOpen + ",isScreenCleanMode:" + isScreenCleanMode);
        return isNegativeScreenShow || isAllAppsShow || isAirPageOpen || isSeatPageOpen || isParkingOpen || isScreenCleanMode;
    }


    /**
     * 关闭指定屏幕的霸屏弹窗
     *
     * @param assignScreen 指定屏幕FuncConstants类
     * @param saveTag      排除指定功能不做处理,为空全部处理
     */
    public static void closeScreenCentral(String assignScreen, Object saveTag) {
        if (null == saveTag)
            return;
        tags.clear();
        if (saveTag instanceof String) {
            tags.add((String) saveTag);
        } else if (saveTag instanceof List) {
            tags.addAll((List<String>) saveTag);
        }
        //关闭哨兵模式弹窗
        String screen = StringUtils.isBlank(assignScreen) ? FuncConstants.VALUE_SCREEN_CENTRAL : assignScreen.toLowerCase();
        if (StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL))
            Settings.Global.putInt(Utils.getApp().getContentResolver(), "SENTRY_DIALOG_STATUS", 2);
        //负一屏
        if (!tags.contains(NEGATIVE_SCREEN_TAG) && SystemUiUtils.getInstance().isNegativeScreenOpen(assignScreen))
            SystemUiUtils.getInstance().getNegativeScreenState(false, assignScreen);
        //应用中心
        if (!tags.contains(ALL_APPS_TAG) && SystemUiUtils.getInstance().isAssignScreenAllAppsOpen(Utils.getApp(), assignScreen))
            SystemUiUtils.getInstance().closeAssignScreenAllApps(Utils.getApp(), assignScreen);
        //空调
        if (!tags.contains(AIR_PAGE_TAG) && SystemUiUtils.getInstance().isAirPageOpen(assignScreen))
            SystemUiUtils.getInstance().closeAirPage(assignScreen);
        //座椅
        if (!tags.contains(SEAT_PAGE) && SystemUiUtils.getInstance().isSeatPageOpen(assignScreen))
            SystemUiUtils.getInstance().closeSeatPage(assignScreen);
        //泊车辅助
        if (!tags.contains(PARK_PAGE) && SystemUiUtils.getInstance().isParkingOpen(Utils.getApp(), assignScreen))
            SystemUiUtils.getInstance().closeParking(Utils.getApp(), assignScreen);
        //屏幕清洁模式
        if (!tags.contains(SCREEN_CLEAN_PAGE) && SystemUiUtils.getInstance().isScreenCleanMode())
            SystemUiUtils.getInstance().closeScreenCleanMode();
        if (assignScreen.equalsIgnoreCase(DeviceScreenType.PASSENGER_SCREEN.name()) && !tags.contains(SCREEN_PROTECTOR) && LauncherManager.getInstance().isShowingScreenSaverOfSecondary()) {
            LauncherManager.getInstance().showHideScreenSaver(false);
        }
    }


    //关闭负一平屏
    public static boolean closeNegativeScreen() {
        try {
            int negativeScreenState = SystemUiUtils.getInstance().getNegativeScreenState(false);
            LogUtils.i(TAG, "isNegativeScreenClose negativeScreenState is " + negativeScreenState);
            return negativeScreenState == 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //关闭主驾应用中心
    public static void closeAllApps(Context context) {
        LogUtils.d(TAG, "closeAllApps");
        DeviceHolder.INS().getDevices().getLauncher().hideAllAppPanel();
    }

    //关闭副驾应用中心
    public static void closeSecondAllApps(Context context) {
        LogUtils.d(TAG, "closeSecondAllApps");
        DeviceHolder.INS().getDevices().getLauncher().hideSecondAllAppPanel();
    }

    //关闭吸顶应用中心
    public static void closeCeilAllApps(Context context) {
        LogUtils.d(TAG, "closeCeilAllApps");
        DeviceHolder.INS().getDevices().getLauncher().hideCeilAllAppPanel();
    }

    //关闭主驾应用中心
    public static void openAllApps(Context context) {
        LogUtils.d(TAG, "openAllApps");
        DeviceHolder.INS().getDevices().getLauncher().showAllAppPanel();
    }

    //关闭副驾应用中心
    public static void openSecondAllApps(Context context) {
        LogUtils.d(TAG, "openSecondAllApps");
        DeviceHolder.INS().getDevices().getLauncher().showSecondAllAppPanel();
    }

    //关闭吸顶应用中心
    public static void openCeilAllApps(Context context) {
        LogUtils.d(TAG, "openCeilAllApps");
        DeviceHolder.INS().getDevices().getLauncher().showCeilAllAppPanel();
    }


//    //关闭空调
//    public static void closeAirPop() {
//        LogUtils.d(TAG, "closeAirPop");
//        CommonDeviceInterface.getInstance().closeAirPop();
//    }

    //打开泊车辅助应用
    public static void openParking() {
        LogUtils.d(TAG, "closeParkingPage");
        CarPropUtils.getInstance().setIntProp(H56D.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ, 1);
    }

    //关闭泊车辅助应用
    public static void closeParking() {
        LogUtils.d(TAG, "closeParkingPage");
        int intProp = CarPropUtils.getInstance().getIntProp(H56D.APA_avmState_APA_AVMSTS);
        // 1 需要关闭avm  2 需要关闭apa
        if (intProp == 1) {
            CarPropUtils.getInstance().setIntProp(H56D.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ, 2);
        } else {
            CarPropUtils.getInstance().setIntProp(H56D.IVI_AVMSet_IVI_VOICE_APAONOFFREQ, 2);
        }
    }

    //泊车辅助应用是否打开
    public static boolean isParkingOpen() {
        int intProp = CarPropUtils.getInstance().getIntProp(H56D.APA_avmState_APA_AVMSTS);
        // 0=关闭 1=360打开 2=自动泊车打开
        boolean isParkOpen = intProp != 0;
        return isParkOpen;
    }

    //关闭座椅弹窗
    public static void closeSeatPage(String screenType) {
//        LogUtils.d(TAG, "closeSeatPage");
        SystemUiUtils.getInstance().closeSeatPage(screenType);
    }

    //关闭蓝牙连接弹窗
    public static void closeBluetoothPop(Context context) {
        LogUtils.d(TAG, "closeBluetoothPop");
        sendDockTouchBroadcast(context);
    }

    @SuppressLint("WrongConstant")
    private static void sendDockTouchBroadcast(Context context) {
        Intent intent = new Intent(ACTION_DOCK_TOUCH);
        intent.putExtra(EXTRA_DOCK_TOUCH_TYPE, TYPE_NONE);//模拟点击空白处
        intent.addFlags(FLAG_RECEIVER_INCLUDE_BACKGROUND);
        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            //
        }
    }

    /**
     * @param map 对话系统上下文数据 注意低代码流程map为二次封装，其中soundLocation与原始ds返回的map字段存在差异
     *            ,可以根据实际使用数据进行修改或自定义map,例:wakeUpAgent
     * @return
     */
    public static String getAssignScreen(HashMap<String, Object> map) {
        String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        String soundLocation = getSoundLocation(map);
        String screen = SCREEN_LIST.get(0);
        if (StringUtils.isBlank(screenName) && StringUtils.isBlank(position)) {
            screen = soundLocation;
        } else if (StringUtils.isBlank(screenName)) {
            //指定屏幕为空
            if (StringUtils.isBlank(position)) {
                //没有指定屏幕和位置-根据声源位置
                screen = soundLocation;
            } else if (!StringUtils.isBlank(position)) {
                //没有指定屏幕有指定位置-根据指定位置
                screen = position;
            }
        } else {
            if (StringUtils.equals(screenName, SCREEN)) {
                //指定了屏幕，但是是非单一屏幕
                screen = position;
            } else if (StringUtils.equals(screenName, ENTERTAINMENT_SCREEN)) {
                //指定屏幕且为娱乐屏
                if (!StringUtils.isBlank(position))
                    screen = position;
                else
                    screen = soundLocation;
            } else {
                screen = screenName;
            }
        }

        String assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;//默认主屏
        if (LOCATION_LIST.contains(screen)) {
            if (StringUtils.equals(screen, LOCATION_LIST.get(0)))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (StringUtils.equals(screen, LOCATION_LIST.get(1)))
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
        } else {
            if (FIRST_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (THIRD_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;

        }

        LogUtils.d(TAG, "getAssignScreen screenName:" + screenName + " ,position:" + position + " ,soundLocation:" + soundLocation + " ,screen:" + screen + " ,assignScreenName:" + assignScreenName);
        return assignScreenName;
    }

    private static String getSoundLocation(HashMap<String, Object> map) {
        return LOCATION_LIST.get((int) getValueInContext(map, "soundLocation"));
    }

    /**
     * 获取图中方法的key对应的值，或者ds里数据对应的值。
     * 图的方法参数放到了，上下文中图的上下文里。
     * ds数据放在了上下文里。
     * 先从图的上下文里找，再从全局上下文找
     *
     * @param map
     * @param key
     * @return
     */
    public static Object getValueInContext(Map<String, Object> map, String key) {
        HashMap<String, Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (null != graphMap && graphMap.containsKey(key)) {
            return graphMap.get(key);
        }
        //二次交互里的上下文中获取数据
        HashMap<String, Object> graphMap2 = (HashMap<String, Object>) map.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
        if (graphMap2 != null && graphMap2.containsKey(key)) {
            return graphMap2.get(key);
        }
        return map.get(key);
    }

}
