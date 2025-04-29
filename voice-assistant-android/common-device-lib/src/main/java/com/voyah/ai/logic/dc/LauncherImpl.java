package com.voyah.ai.logic.dc;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.launcher.ResultCallBack;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2024/5/7
 **/
public class LauncherImpl extends AbsDevices {
    private static final String TAG = LauncherImpl.class.getSimpleName();

    public LauncherImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "launcher";
    }

    public void backToHome(DeviceScreenType screenType) {
        DeviceHolder.INS().getDevices().getLauncher().backToHome(screenType);
    }


    public boolean isAirOpen(DeviceScreenType deviceScreenType) {
        return DeviceHolder.INS().getDevices().getLauncher().isAirOpen(deviceScreenType);
    }


    public boolean isScreenPopWindowShowTop(boolean isShowLog, DeviceScreenType deviceScreenType) {
        return DeviceHolder.INS().getDevices().getLauncher().isScreenPopWindowShowTop(isShowLog, deviceScreenType);
    }

    public void closeScreenCentral(DeviceScreenType deviceScreenType, Object saveTag) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(deviceScreenType, saveTag);
    }

    public void closeAssignScreenAllApps(DeviceScreenType deviceScreenType) {
        DeviceHolder.INS().getDevices().getLauncher().closeAssignScreenAllApps(deviceScreenType);
    }

    public String getAssignScreen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().getAssignScreen(map);
    }


    public int getPrivacySecurity() {
        return DeviceHolder.INS().getDevices().getLauncher().getPrivacySecurity();
    }

    //当前壁纸是否为系统壁纸

    public boolean isSystemWallpaperFlag() {
        boolean isSystemWallpaper = DeviceHolder.INS().getDevices().getLauncher().isSystemWallpaperFlag();
        LogUtils.i(TAG, "isSystemWallpaper is " + isSystemWallpaper);
        return isSystemWallpaper;
    }

    /**
     * 设置系统壁纸(根据当前壁纸index，切换上一张、下一张)
     *
     * @param switchType switchType 壁纸轮切切换类型
     */
    public void setApplySystemWallpaper(String switchType) {
        //系统壁纸索引,有效范围[0-7]
        DeviceHolder.INS().getDevices().getLauncher().setApplySystemWallpaper(switchType);
    }

    //获取当前壁纸类型 0-3D车控桌面  1-壁纸桌面
    public int getCurDesktopType() {
        LogUtils.i(TAG, "getCurDesktopType");
        return DeviceHolder.INS().getDevices().getLauncher().getCurDesktopType();
    }

    //设置当前桌面类型
    public void setDesktopType(String switch_mode) {
        LogUtils.i(TAG, "getCurDesktopType switch_mode is " + switch_mode);
        DeviceHolder.INS().getDevices().getLauncher().setDesktopType(switch_mode);
    }

    public void setDesktopType2D() {
        LogUtils.i(TAG, "setDesktopType2D");
        DeviceHolder.INS().getDevices().getLauncher().setDesktopType2D();
    }

    public void setDesktopType3D() {
        LogUtils.i(TAG, "setDesktopType3D");
        DeviceHolder.INS().getDevices().getLauncher().setDesktopType2D();
    }

    public boolean isAllAppPanelShowing(DeviceScreenType deviceScreenType) {
        return DeviceHolder.INS().getDevices().getLauncher().isAllAppPanelShowing(deviceScreenType);
    }

    //应用列表是否已打开-主驾
    public boolean isAllAppPanelShowing() {
        return DeviceHolder.INS().getDevices().getLauncher().isAllAppPanelShowing();
    }

    public boolean isSecondAllAppPanelShowing() {
        return DeviceHolder.INS().getDevices().getLauncher().isSecondAllAppPanelShowing();
    }


    public boolean isCeilAllAppPanelShowing() {
        return DeviceHolder.INS().getDevices().getLauncher().isCeilAllAppPanelShowing();
    }


    public void showAllAppPanel(DeviceScreenType deviceScreenType) {
        DeviceHolder.INS().getDevices().getLauncher().showAllAppPanel();
    }

    //打开应用列表
    public void showAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().showAllAppPanel();
    }


    public void showSecondAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().showSecondAllAppPanel();
    }


    public void showCeilAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().showCeilAllAppPanel();
    }


    //关闭应用列表
    public void hideAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().hideAllAppPanel();
    }

    public void hideSecondAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().hideSecondAllAppPanel();
    }


    public void hideCeilAllAppPanel() {
        DeviceHolder.INS().getDevices().getLauncher().hideCeilAllAppPanel();
    }

    //打开副驾屏保

    public void showHideScreenSaver(boolean isShow, ResultCallBack<Boolean> callBack) {
        DeviceHolder.INS().getDevices().getLauncher().showHideScreenSaver(isShow, callBack);
    }


    public void showHideScreenSaver(boolean isShow) {
        DeviceHolder.INS().getDevices().getLauncher().showHideScreenSaver(isShow);
    }

    //获取副驾屏屏保弹出状态
    public void isShowingScreenSaverOfSecondary(ResultCallBack<Boolean> callBack) {
        DeviceHolder.INS().getDevices().getLauncher().isShowingScreenSaverOfSecondary(callBack);
    }


    public boolean isShowingScreenSaverOfSecondary() {
        return DeviceHolder.INS().getDevices().getLauncher().isShowingScreenSaverOfSecondary();
    }


    public boolean isNegativeScreenOpen(DeviceScreenType deviceScreenType) {
        return DeviceHolder.INS().getDevices().getLauncher().isNegativeScreenOpen(deviceScreenType);
    }

    public void openNegativeScreen(DeviceScreenType deviceScreenType) {
        DeviceHolder.INS().getDevices().getLauncher().openNegativeScreen(deviceScreenType);
    }

    public void closeNegativeScreen(DeviceScreenType deviceScreenType) {
        DeviceHolder.INS().getDevices().getLauncher().closeNegativeScreen(deviceScreenType);
    }

    public boolean isParkOpen() {
        return DeviceHolder.INS().getDevices().getLauncher().isParkOpen();
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        return str;
    }
}
