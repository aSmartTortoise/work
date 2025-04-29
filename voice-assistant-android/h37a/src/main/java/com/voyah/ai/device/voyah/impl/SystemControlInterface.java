//package com.voyah.ai.device.voyah.impl;
//
//import java.util.HashMap;
//
///**
// * @author:lcy
// * 方便后续会使用到，暂时先注释掉保留
// * @data:2024/5/7
// **/
//public interface SystemControlInterface {
//    //应用列表是否已打开
//    boolean isAllAppsOpen(HashMap<String, Object> map);
//
//    //打开应用列表
//    void openAllApps(HashMap<String, Object> map);
//
//    //关闭应用列表
//    void closeAllApps(HashMap<String, Object> map);
//
//    //指定屏幕应用列表是否已打开
//    boolean isAssignScreenAllAppsOpen(HashMap<String, Object> map);
//
//    //打开指定屏幕应用列表
//    void openAssignScreenAllApps(HashMap<String, Object> map);
//
//    //关闭指定屏幕应用列表
//    void closeAssignScreenAllApps(HashMap<String, Object> map);
//
//
//    //当前挡位是否为P挡
//    boolean isGearsP(HashMap<String, Object> map);
//
//    //车辆是否已激活
//    boolean isCarActivated(HashMap<String, Object> map);
//
//    //打开车辆激活界面
//    void openCarActivateView(HashMap<String, Object> map);
//
//    //拉起账号登录弹窗(无效接口)
//    void openAccountLoginView(HashMap<String, Object> map);
//
//    //是否为全部消息标记为已读指令
//    boolean isReadAllMessageInstruct(HashMap<String, Object> map);
//
//
//    //负一屏是否已打开(下拉菜单)
//    boolean isNegativeScreenOpen(HashMap<String, Object> map);
//
//    //负一屏是否已关闭(下拉菜单)
//    boolean isNegativeScreenClose(HashMap<String, Object> map);
//
//    //关闭负一屏(下拉菜单)
//    void closeNegativeScreen(HashMap<String, Object> map);
//
//    //打开负一屏(下拉菜单)
//    void openNegativeScreen(HashMap<String, Object> map);
//
//    //打开指定屏幕负一屏
//    void openAssignNegativeScreen(HashMap<String, Object> map);
//
//    //关闭指定屏幕负一屏
//    void closeAssignNegativeScreen(HashMap<String, Object> map);
//
//    //指定屏幕负一屏是否已打开
//    boolean isAssignNegativeScreenOpen(HashMap<String, Object> map);
//
//    //是否支持全位置指令执行(区分单屏与多屏)
//    boolean isSupportAllPosition(HashMap<String, Object> map);
//
//    //是否为操作主驾壁纸
//    boolean isOperateFirstLeftWallpaper(HashMap<String, Object> map);
//
//    //当前使用壁纸是否为相册壁纸
//    boolean isAlbumWallpaper(HashMap<String, Object> map);
//
//    //当前使用壁纸是否为3D壁纸
//    boolean is3DWallpaper(HashMap<String, Object> map);
//
//    //是否为切换到2D桌面
//    boolean isSwitch2DWallpaper(HashMap<String, Object> map);
//
//    //切换桌面
//    void switchDesktop(HashMap<String, Object> map);
//
//    //切换2D桌面
//    void switchDesktop2D(HashMap<String, Object> map);
//
//    //切换3D桌面
//    void switchDesktop3D(HashMap<String, Object> map);
//
//    //当前是否在桌面
//    boolean isLauncherView(HashMap<String, Object> map);
//
//    //返回桌面
//    void backToLauncher(HashMap<String, Object> map);
//
//    void passengerScreenBackToLauncher(HashMap<String, Object> map);
//
//    //当前是否为2D桌面
//    boolean isCurrent2Desktop(HashMap<String, Object> map);
//
//    //当前是否为3D桌面
//    boolean isCurrent3Desktop(HashMap<String, Object> map);
//
////    //轮切壁纸
////    void autoCutWallPaper(HashMap<String, Object> map);
//
//    //切换壁纸(上一张壁纸or下一张壁纸)
//    void cutWallPaper(HashMap<String, Object> map);
//
//    //打开壁纸设置页面
//    int openWallpaperPage(HashMap<String, Object> map);
//
//    boolean isAppExist(HashMap<String, Object> map);
//
//    boolean isAppOpened(HashMap<String, Object> map);
//
//    void openApp(HashMap<String, Object> map);
//
//    void closeApp(HashMap<String, Object> map);
//
//    boolean isGalleryForeground(HashMap<String, Object> map);
//
//    void openGallery(HashMap<String, Object> map);
//
//    void closeGallery(HashMap<String, Object> map);
//
//    boolean isTabGalleryForeground(HashMap<String, Object> map);
//
//    boolean openTabGallery(HashMap<String, Object> map);
//
//    //是否为进入分屏
//    boolean isEnterSplitScreen(HashMap<String, Object> map);
//
//    //当前是否处于分屏中
//    boolean isSplitScreening(HashMap<String, Object> map);
//
//    //退出分屏、导航全屏
//    void dismissSplitScreen(HashMap<String, Object> map);
//
//    //退出左侧分屏
//    void dismissLeftSplitScreen(HashMap<String, Object> map);
//
//    //退出右侧分屏
//    void dismissRightSplitScreen(HashMap<String, Object> map);
//
//    //是否为指定左侧全屏
//    boolean assignLeftFullScreen(HashMap<String, Object> map);
//
//    //分屏指令是否包含位置信息
//    boolean isHavePosition(HashMap<String, Object> map);
//
//
//    //是否有有效账号登录(非游客)
//    boolean isValidAccountLogin(HashMap<String, Object> map);
//
//    //打开个人中心账号(账号管理)界面
//    void openPersonalCenterAccountView(HashMap<String, Object> map);
//
//    //当前是否有账号登录
//    boolean isLogin(HashMap<String, Object> map);
//
//    //当前登录账号为车主账号
//    boolean isCarOwnerAccount(HashMap<String, Object> map);
//
//    //当前为普通账号
//    boolean isCarOrdinaryAccount(HashMap<String, Object> map);
//
//    //登录账号是否为非游客账号(车主or普通账号)
//    boolean isUnVisitorAccount(HashMap<String, Object> map);
//
//    //打开登录账号弹窗
//    void startToQrPasswordLogin(HashMap<String, Object> map);
//
//    //退出账号弹窗
//    void logoutAccount(HashMap<String, Object> map);
//
//    //是否已在个人中心页面
//    boolean isPersonalCenterView(HashMap<String, Object> map);
//
//    //打开个人中心
//    void openPersonalCenterView(HashMap<String, Object> map);
//
//    //当前是否在账号登录界面
//    boolean isLoginView(HashMap<String, Object> map);
//
//    //是否已在消息中心页面
//    boolean isMessageCenterView(HashMap<String, Object> map);
//
//    //打开消息中心
//    void openMessageCenter(HashMap<String, Object> map);
//
//    //关闭消息中心
//    void closeMessageCenter(HashMap<String, Object> map);
//
//    //微场景是否已打开
//    boolean isMicroSceneOpen(HashMap<String, Object> map);
//
//    //打开微场景
//    void openMicroScene(HashMap<String, Object> map);
//
//    //判断是否为打开微场景操作
//    boolean isOpenMicroScene(HashMap<String, Object> map);
//
//    //是否为指定副驾屏幕
//    boolean isTargetScreen(HashMap<String, Object> map);
//
//    //是否为指定主驾屏操作
//    boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map);
//
//    //副驾屏屏保是否已打开
//    boolean isShowingScreenSaverOfSecondary(HashMap<String, Object> map);
//
//    //打开副驾屏屏保
//    boolean showScreenSaverSuccess(HashMap<String, Object> map);
//
//    //关闭副驾屏屏保
//    boolean hideScreenSaverSuccess(HashMap<String, Object> map);
//
//    //是否指定了屏幕
//    boolean isScreenSpecified(HashMap<String, Object> map);
//
//    //是否指定副驾
//    boolean isFirstRowRight(HashMap<String, Object> map);
//
//    //声源位置是否主驾
//    boolean isFirstRowLeft(HashMap<String, Object> map);
//
//    //指定屏幕是否为吸顶屏
//    boolean isCeilScreen(HashMap<String, Object> map);
//
//    //是否为按照声源位置执行映射到屏幕
//    boolean isOnlySoundLocation(HashMap<String, Object> map);
//
//    //吸顶屏是否已打开
//    boolean isCeilScreenOpend(HashMap<String, Object> map);
//
//    //是否有吸顶屏
//    boolean isHaveCeilScreen(HashMap<String, Object> map);
//
//    //指定吸顶屏且不存在吸顶屏
//    boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map);
//
//    //展开吸顶屏屏幕并打开负一屏
//    void openNegativeScreenAndCeilWait(HashMap<String, Object> map);
//
//    //展开吸顶屏屏幕并打开AllApp
//    void openAllAppAndCeilWait(HashMap<String, Object> map);
//
//    //确认打开
//    boolean isOpenConfirm(HashMap<String, Object> map);
//
//    //是否为二次确认
//    boolean isSplitScreenAndAllAppConfirm(HashMap<String, Object> map);
//}
