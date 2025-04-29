package com.voyah.ai.voice.agent.flowchart.helper;

import java.util.HashSet;
import java.util.Set;

public class InterventionTreatmentHelper {
    private static final String TAG = "InterventionTreatmentHe";


    public static InterventionTreatmentHelper getInstance() {
        return InterventionTreatmentHelper.Holder.instance;
    }

    private static class Holder {
        private static final InterventionTreatmentHelper instance = new InterventionTreatmentHelper();
    }

    //function 的名字集合
    private Set<String> set = new HashSet<>();

//    //系统控制、车辆控制、其他控制的函数，需要根据tab去区分的。
//    private Set<String> set2 = new HashSet<>();
//
//    //在set2的基础上，再加上tab_name能明确出来是打开应用和界面的集合。
//    private Set<String> set3 = new HashSet<>();

    private InterventionTreatmentHelper() {
        set.add("air_ui#switch");//空调界面
        set.add("seat_child_ui#switch");//儿童座椅界面
        set.add("seat_ui#switch");//座椅界面
        set.add("fragrance_ui#switch");//香氛界面
        set.add("atmosphere_ui#switch");//氛围灯界面
        set.add("outLight_ui#switch");//车外灯界面
        set.add("dvr_camera_app#switch");//dvr应用开关
        set.add("dvr_camera_ui#switch");//dvr界面开关
        set.add("dvr_dashcam_app#switch");//行车记录仪应用开关
        set.add("dvr_dashcam_ui#switch");//行车记录仪界面开关
        set.add("systemControl_gallery_app#switch");//打开相册应用
        set.add("systemControl_gallery#switch");//打开相册界面
        set.add("systemControl_gallery_photo#switch");//浏览图片
        set.add("systemControl_personalCenter_ui#switch");//个人中心
        set.add("systemControl_messageCenter#switch");//消息中心
        set.add("systemControl_taskCenter#switch");//任务中心
        set.add("systemControl_app#switch");//打开应用
        set.add("systemControl#switch");//系统语控开关
        set.add("systemControl_wallpaper#switch");//壁纸设置开关
        set.add("systemControl_gallery#switch");

//        set2.add("systemSetting#switch");//系统设置开关
//        set2.add("carSetting#switch");//系统设置开关

    }

    /**
     * 关闭负一屏幕，应用列表，屏幕清洁
     */
    public void closeNegativeScreen(String nlu) {
//        if (nlu != null && (PlatFormUtil.nluIsEqual(nlu, "systemControl#switch@switch_type=&open@tab_name=&negative_screen"))) {
//            return;
//        }
//        FunctionCodec functionCodec = FunctionCodec.decode(nlu);
//        Map<String, String> paramMap = functionCodec.getParams();
//        String functionName = functionCodec.getFunctionName();
//        String switchType = paramMap.get("switch_type");
//        String tabName = paramMap.get("tab_name");
//
//        //通过函数就可以确认当前方法都是开关界面和应用的功能。
//        if (switchType != null && switchType.equals("open") && set.contains(functionName)) {
//                //todo:负一屏支持多位置，不能默认关闭主驾的
////            if (SystemUiUtils.getInstance().isNegativeScreenOpen()) {
////                LogUtils.d(TAG, "close NegativeScreen，switchType：" + switchType + " functionName:" + functionName);
////                GlobalProcessingHelper.getInstance().closeNegativeScreen();
////            }
//
//
////            if (!PlatFormUtil.nluIsEqual(nlu, "systemControl#switch@switch_type=&open@tab_name=&application_list")) {
////                if (CommonDeviceInterface.getInstance().isAllAppsOpen()) {
////                    CommonDeviceInterface.getInstance().closeAllApps();
////                }
////            }
//            if (!PlatFormUtil.nluIsEqual(nlu, "air_ui#switch@switch_type=&open@screen_name=&all@position=&all")) {
//                if (!PlatFormUtil.nluIsEqual(nlu, "fragrance_ui#switch@switch_type=&open@screen_name=&all@position=&all")) {
//                    if (CommonDeviceInterface.getInstance().isAirOpen(null)) {
//                        CommonDeviceInterface.getInstance().closeAirPop();
//                    }
//                }
//            }
//            if (!PlatFormUtil.nluIsEqual(nlu, "fragrance_ui#switch@switch_type=&open")) {
//                if (CommonDeviceInterface.getInstance().isFragranceOpen()) {
//                    CommonDeviceInterface.getInstance().closeFragranceApp();
//                }
//            }
//            // 打开座椅界面有三个语义
//            if (!PlatFormUtil.nluIsEqual(nlu, "seat_ui#switch@switch_type=&open")
//                    && !PlatFormUtil.nluIsEqual(nlu, "seat_ui#switch@switch_type=&open@ui_name=&comfort")
//                    && !PlatFormUtil.nluIsEqual(nlu, "seat_ui#switch@switch_type=&open@ui_name=&adjust")) {
//                if (CommonDeviceInterface.getInstance().isSeatPageOpen(FuncConstants.VALUE_SCREEN_CENTRAL)) {
//                    CommonDeviceInterface.getInstance().closeSeatPage(FuncConstants.VALUE_SCREEN_CENTRAL);
//                }
//            }
//
//            if (!PlatFormUtil.nluIsEqual(nlu, "screen#switch@screen_name=&screen@tab_name=&clean@switch_type=&open")
//                    && !PlatFormUtil.nluIsEqual(nlu, "systemControl#switch@switch_type=&open@tab_name=&application_list")) {
//                // 关闭屏幕清洁模式
//                try {
//                    if (SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode")) {
//                        LogUtils.d(TAG, "close NegativeScreen， ExitCleanMode");
//                        SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
//                    }
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        //包含page_name的都是界面打开。
//        if (paramMap.containsKey("page_name")) {
//            closeAllScreen();
//
//
//            // 关闭屏幕清洁模式
//            try {
//                if (SettingUtils.getInstance().isCurrentState("com.voyah.vehicle.action.ExitCleanMode")) {
//                    LogUtils.d(TAG, "close NegativeScreen， ExitCleanMode");
//                    SettingUtils.getInstance().exec("com.voyah.vehicle.action.ExitCleanMode");
//                }
//            } catch (IllegalArgumentException  e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 关闭所有优先级高的界面。
     */
    public void closeAllScreen() {
//        //关闭负一屏
//        if (SystemUiUtils.getInstance().isNegativeScreenOpen()) {
//            LogUtils.d(TAG, "closeAllScreen ，close NegativeScreen");
//            GlobalProcessingHelper.getInstance().closeNegativeScreen();
//        }
//        //关闭应用列表界面
//        if (CommonDeviceInterface.getInstance().isAllAppsOpen()) {
//            CommonDeviceInterface.getInstance().closeAllApps();
//        }
//
//        //关闭空调界面
//        if (CommonDeviceInterface.getInstance().isAirOpen(null)) {
//            CommonDeviceInterface.getInstance().closeAirPop();
//        }
//
//        //关闭座椅界面
//        if (CommonDeviceInterface.getInstance().isSeatPageOpen(FuncConstants.VALUE_SCREEN_CENTRAL)) {
//            CommonDeviceInterface.getInstance().closeSeatPage(FuncConstants.VALUE_SCREEN_CENTRAL);
//        }
    }


    //空调界面是否已打开
//    private boolean isAirOpen() {
//        boolean isAirOpen = HvacServiceImpl.getInstance(AppContext.instant).isCurrentState("ACTION_IS_AC_FOREGROUND");
//        LogUtils.d(TAG, "isAirOpen:" + isAirOpen);
//        return isAirOpen;
//    }
//
//    //关闭空调弹窗界面
//    private void closeAirPop() {
//        LogUtils.d(TAG, "closeAirPop");
//        HvacServiceImpl.getInstance(AppContext.instant).exec("ACTION_CLOSE_AC_PAGE");
//    }
//
//    //座椅界面是否打开
//    private boolean isSeatPageOpen() {
//        boolean isSeatPageOpen = SeatServiceImpl.getInstance(AppContext.instant).isCurrentState("ACTION_GET_SEAT_PANEL_STATE");
//        LogUtils.d(TAG, "isSeatPageOpen:" + isSeatPageOpen);
//        return isSeatPageOpen;
//    }
//
//    //关闭座椅弹窗界面
//    private void closeSeatPage() {
//        LogUtils.d(TAG, "closeSeatPage");
//        SeatServiceImpl.getInstance(AppContext.instant).exec("ACTION_CLOSE_SEAT_PANEL");
//    }
//
//
//    public boolean isAllAppsOpen() {
//        boolean isAllAppsOpen = DeviceInterfaceImpl.getInstant(AppContext.instant).getLauncherImpl().isAllAppPanelShowing();
//        LogUtils.i(TAG, "isAllAppsOpen is " + isAllAppsOpen);
//        return isAllAppsOpen;
//    }
//
//
//    public void closeAllApps() {
//        DeviceInterfaceImpl.getInstant(AppContext.instant).getLauncherImpl().hideAllAppPanel();
//    }
//
//    public boolean isFragranceOpen() {
//        return HvacServiceImpl.getInstance(AppContext.instant).isCurrentState("ACTION_IS_FRAG_DIALOG_SHOWING");
//    }
//
//    public void closeFragranceApp() {
//        HvacServiceImpl.getInstance(AppContext.instant).exec("ACTION_DISMISS_FRAG_DIALOG");
//    }

}
