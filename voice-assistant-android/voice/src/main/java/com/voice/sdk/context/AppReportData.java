package com.voice.sdk.context;

import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class AppReportData {

    public static final String PACKAGE_NAME_MAP = "com.mega.map";
    public static final String PACKAGE_NAME_MUSIC = "com.voyah.cockpit.voyahmusic";
    public static final String PACKAGE_NAME_BILI = "com.bilibili.bilithings";
    public static final String PACKAGE_NAME_IQY = "com.arcvideo.car.iqy.video";
    public static final String PACKAGE_NAME_MIGU = "cn.cmvideo.car.play";
    public static final String PACKAGE_NAME_TX = "com.tencent.qqlive.audiobox";
    public static final String PACKAGE_NAME_KTV = "com.thunder.carplay";
    public static final String PACKAGE_NAME_TIKTOK = "com.bytedance.byteautoservice3";
    public static final String PACKAGE_NAME_USB = "com.voyah.cockpit.video";
    public static final String PACKAGE_NAME_DESK_TOP = "com.voyah.cockpit.launcher";
    public static final String PACKAGE_NAME_DESK_TOP_2 = "com.crystal.h37.arcreator";

    private static final Map<String, String> APP_CATEGORY_MAP = new HashMap<>();

    static {
        APP_CATEGORY_MAP.put(PACKAGE_NAME_MAP, ReportConstant.APP_CATEGORY_MAP);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_MUSIC, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_BILI, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_IQY, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_MIGU, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_TX, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_KTV, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_TIKTOK, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_USB, ReportConstant.APP_CATEGORY_MEDIA);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_DESK_TOP, ReportConstant.APP_CATEGORY_DESKTOP);
        APP_CATEGORY_MAP.put(PACKAGE_NAME_DESK_TOP_2, ReportConstant.APP_CATEGORY_DESKTOP);
    }


    private AppReportData() {
    }

    private static final AppReportData appContext = new AppReportData();

    public static AppReportData getInstance() {
        return appContext;
    }

    public void updateFrontAppInfo(boolean isSplitScreen, String rightFrontApp) {
        LogUtils.i(DeviceContextUtils.TAG, "updateFrontAppInfo called(), isSplitScreen:" + isSplitScreen + ",rightFrontApp:" + rightFrontApp);
        String leftFrontApp = isSplitScreen ? PACKAGE_NAME_MAP : "";
        if (rightFrontApp == null) {
            rightFrontApp = "";
        }
        DeviceContextUtils.getInstance().updateDeviceInfo(
                DeviceInfo.build(ReportConstant.KEY_SPILT_SCREEN, isSplitScreen),
                DeviceInfo.build(ReportConstant.KEY_RIGHT_FRONT_APP, rightFrontApp),
                DeviceInfo.build(ReportConstant.KEY_LEFT_FRONT_APP, leftFrontApp),
                DeviceInfo.build(ReportConstant.KEY_LEFT_FRONT_APP_CATEGORY, APP_CATEGORY_MAP.getOrDefault(leftFrontApp, ReportConstant.APP_CATEGORY_UNKNOWN)),
                DeviceInfo.build(ReportConstant.KEY_RIGHT_FRONT_APP_CATEGORY, APP_CATEGORY_MAP.getOrDefault(rightFrontApp, ReportConstant.APP_CATEGORY_UNKNOWN)));
    }

}
