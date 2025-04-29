package com.voice.sdk.device.launcher;

import com.voice.sdk.device.system.DeviceScreenType;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2024/5/7
 **/
public interface LauncherInterface {

    void backToHome(DeviceScreenType screenType);

    boolean isNeedBackWidget(); //是否需要返回widget桌面

    void backToWidgetHome(); //主驾打开widget桌面

    //空调弹窗是否打开
    boolean isAirOpen(DeviceScreenType deviceScreenType);

    //获取指定屏幕是否有霸屏弹窗展示
    boolean isScreenPopWindowShowTop(boolean isShowLog, DeviceScreenType deviceScreenType);

    //关闭指定屏幕的霸屏弹窗
    void closeScreenCentral(DeviceScreenType deviceScreenType, Object saveTag);

    void closeAssignScreenAllApps(DeviceScreenType deviceScreenType);

    /**
     * @param map 对话系统上下文数据 注意低代码流程map为二次封装，其中soundLocation与原始ds返回的map字段存在差异
     *            ,可以根据实际使用数据进行修改或自定义map,例:wakeUpAgent
     * @return
     */
    String getAssignScreen(HashMap<String, Object> map);

    int getPrivacySecurity();

    boolean isSystemWallpaperFlag();

    void setApplySystemWallpaper(String switchType);

    int getCurDesktopType();

    void setDesktopType(String switch_mode);

    void setDesktopType2D();

    void setDesktopType3D();

    boolean isAllAppPanelShowing(DeviceScreenType deviceScreenType);

    boolean isAllAppPanelShowing();

    boolean isSecondAllAppPanelShowing();

    boolean isCeilAllAppPanelShowing();

    void showAllAppPanel(DeviceScreenType deviceScreenType);

    void showAllAppPanel();

    void showSecondAllAppPanel();

    void showCeilAllAppPanel();

    void hideAllAppPanel();

    void hideSecondAllAppPanel();

    void hideCeilAllAppPanel();

    //----------副驾屏保
    void showHideScreenSaver(boolean isShow, ResultCallBack<Boolean> callBack);

    void showHideScreenSaver(boolean isShow);

    void isShowingScreenSaverOfSecondary(ResultCallBack<Boolean> callBack);

    boolean isShowingScreenSaverOfSecondary();

    //----------吸顶屏屏保
    boolean isSupportScreenSaverChange();

    void showHideCeilScreenSaver(boolean isShow, ResultCallBack<Boolean> callBack);

    void showHideCeilScreenSaver(boolean isShow);

    void isShowingScreenSaverOfCeil(ResultCallBack<Boolean> callBack);

    boolean isShowingScreenSaverOfCeil();

    boolean isNegativeScreenOpen(DeviceScreenType deviceScreenType);

    void openNegativeScreen(DeviceScreenType deviceScreenType);

    void closeNegativeScreen(DeviceScreenType deviceScreenType);

    boolean isParkOpen();

    boolean isGearsR();

    boolean isGearsROrD();

    //-------------------屏保新增接口
    boolean isSupportScreenSaver(); //车型是否支持屏保

    boolean isSupportSecondScreenSaver(); //副驾屏是否支持屏保
    boolean isSupportCeilScreenSaver(); //吸顶屏是否支持屏保

    boolean isScreenViewMode(DeviceScreenType deviceScreenType);

    boolean isSupportExecuteScreenSaver();

    boolean isMessageCenterIndependent();

    boolean isMessageCenterOpen();

    void switchMessageCenter(boolean switchType);

    boolean isMessageCenterNotEmpty();

    void clearMessageCenter();

    boolean isRcwOpen();

    boolean isSupportCms();
    boolean isSupportSplitFullScreen();
}
