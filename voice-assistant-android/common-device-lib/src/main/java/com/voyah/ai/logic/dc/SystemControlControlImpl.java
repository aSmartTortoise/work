package com.voyah.ai.logic.dc;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log2;

import com.voice.sdk.VoiceImpl;
import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.GalleryInterface;
import com.voice.sdk.device.ShowBoxInterface;
import com.voice.sdk.device.UserCenterInterface;
import com.voice.sdk.device.appstore.AppStorePage;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voice.sdk.device.carservice.vcar.CarServicePropInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.launcher.LauncherInterface;
import com.voice.sdk.device.launcher.ResultCallBack;
import com.voice.sdk.device.navi.bean.NaviPage;
import com.voice.sdk.device.system.AppInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.IAppProcessor;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.system.ShareInterface;
import com.voice.sdk.device.system.SplitScreenInterface;
import com.voice.sdk.device.ui.IScreenStateChangeListener;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.AppStoreSwitchImpl;
import com.voyah.ai.logic.dc.dvr.Constant;
import com.voyah.ai.logic.openapp.BlackAppProcessor;
import com.voyah.ai.logic.openapp.FileTransferProcessor;
import com.voyah.ai.logic.openapp.MediaAppProcessor;
import com.voyah.ai.logic.util.LocationHelper;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class SystemControlControlImpl extends AbsDevices {
    private static final String TAG = SystemControlControlImpl.class.getSimpleName();
    private static final String FIRST_ROW_LEFT = "first_row_left";
    private static final String FIRST_ROW_RIGHT = "first_row_right";
    private final LauncherInterface launcher;
    private final AppInterface appInterface;
    private final ScreenInterface screenInterface;
    private static final String APPLICATION_TTS_NAME = "application_tts_name";
    private static final String IS_APP_FOREGROUND = "is_app_foreground";
    private static final String APPLICATION_TTS = "application_tts";
    private static final String DOMAIN_MAP = "map";
    private static final String DOMAIN_NAVI = "navi";
    private final ShareInterface shareInterface;
    private boolean isNaviCollection = false;

    private HashMap<String, Object> tempMap;

    private final HashSet<IAppProcessor> processors = new HashSet<>();

    private LocationHelper locationHelper = new LocationHelper();

    public SystemControlControlImpl() {
        super();
        //情景模式初始化
        DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface().setSceneMode("", -1, -1, -1);
        launcher = DeviceHolder.INS().getDevices().getLauncher();
        appInterface = DeviceHolder.INS().getDevices().getSystem().getApp();
        screenInterface = DeviceHolder.INS().getDevices().getSystem().getScreen();
        shareInterface = DeviceHolder.INS().getDevices().getSystem().getShare();
        register();
    }


    public String getDomain() {
        return Domain.SYS_CTRL.getDomain();
    }

    private void register() {
        processors.add(new AppStoreSwitchImpl());
        processors.add(new BlackAppProcessor());
        processors.add(new FileTransferProcessor());
        processors.add(new MediaAppProcessor());
    }

    public boolean dispatch(HashMap<String, Object> map) {
        for (IAppProcessor processor : processors) {
            if (processor.consume(map)) {
                Log2.d(TAG, "processor: " + processor.getClass().getSimpleName());
                String tts = processor.process(map);
                map.put(APPLICATION_TTS, tts);
                return true;
            }
        }
        return false;
    }


    public boolean isAllAppsOpen(HashMap<String, Object> map) {
        map.put("app_name", "应用中心");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        return launcher.isAllAppPanelShowing();
    }

    public void openAllApps(HashMap<String, Object> map) {
        launcher.showAllAppPanel();
    }

    public void closeAllApps(HashMap<String, Object> map) {
        launcher.closeAssignScreenAllApps(DeviceScreenType.CENTRAL_SCREEN);
    }

    public boolean isAssignScreenAllAppsOpen(HashMap<String, Object> map) {
        String assignScreenName = getAssignScreen(map);
        map.put("app_name", "应用中心");
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, getAssignScreen(map));
        return launcher.isAllAppPanelShowing(DeviceScreenType.fromValue(assignScreenName));
    }

    public void openAssignScreenAllApps(HashMap<String, Object> map) {
        String assignScreenName = getAssignScreen(map);
        launcher.showAllAppPanel(DeviceScreenType.fromValue(assignScreenName));
    }

    public void closeAssignScreenAllApps(HashMap<String, Object> map) {
        String assignScreenName = getAssignScreen(map);
        launcher.closeAssignScreenAllApps(DeviceScreenType.fromValue(assignScreenName));
    }

    public boolean isGearsP(HashMap<String, Object> map) {
        return GearInfo.CARSET_GEAR_PARKING == operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }

    public boolean isCarActivated(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getUserCenter().isCarActivated();
    }

    public boolean isValidAccountLogin(HashMap<String, Object> map) {
        UserCenterInterface userCenterInterface = DeviceHolder.INS().getDevices().getUserCenter();
        boolean isLogin = userCenterInterface.isLogin();
        String carOwnerType = userCenterInterface.getCarOwnerType();
        return isLogin && (!StringUtils.equals(carOwnerType, "2"));
    }

    public boolean isMessageCenterView(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getUserCenter().isMessageCenterView();
    }

    public void openMessageCenter(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().startOrCloseMessageCenter("open");
    }

    public void openCarActivateView(HashMap<String, Object> map) {
        //todo
        /*try {
            CarActivationManager.getInstance().openActiveCar();
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
    }

    public void startToQrPasswordLogin(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().startToQrPasswordLogin();
    }

    public boolean isReadAllMessageInstruct(HashMap<String, Object> map) {
        boolean isReadAllMessageInstruct;
        String message_type = "";
        String switch_type = "";
        if (map.containsKey("message_type"))
            message_type = (String) getValueInContext(map, "message_type");
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        isReadAllMessageInstruct = StringUtils.equals(message_type, "read") && StringUtils.equals(switch_type, "change");
        LogUtils.i(TAG, "message_type is " + message_type + "switch_type is " + switch_type + " ,isReadAllMessageInstruct is " + isReadAllMessageInstruct);
        return isReadAllMessageInstruct;
    }

    public boolean isClearAllMessageInstruct(HashMap<String, Object> map) {
        boolean isClearAllMessageInstruct;
        String message_type = "";
        String switch_type = "";
        if (map.containsKey("message_type"))
            message_type = (String) getValueInContext(map, "message_type");
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        isClearAllMessageInstruct = StringUtils.equals(message_type, "clear") && StringUtils.equals(switch_type, "open");
        LogUtils.i(TAG, "message_type is " + message_type + "switch_type is " + switch_type + " ,isClearAllMessageInstruct is " + isClearAllMessageInstruct);
        return isClearAllMessageInstruct;
    }

    public void closeMessageCenter(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().startOrCloseMessageCenter("close");
    }

    public boolean isMicroSceneOpen(HashMap<String, Object> map) {
        boolean isMicroSceneOpen = appInterface.isAppForeGround(ApplicationConstant.MICRO_SCENE_PACKAGE_NAME, DeviceScreenType.CENTRAL_SCREEN);
        LogUtils.d(TAG, "isMicroSceneOpen:" + isMicroSceneOpen);
        return isMicroSceneOpen;
    }

    public void openMicroScene(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openMicroScene");
        DeviceHolder.INS().getDevices().getSystem().getApp().openApp(ApplicationConstant.MICRO_SCENE_PACKAGE_NAME, null);
    }

    public boolean isOpenMicroScene(HashMap<String, Object> map) {
        String switch_type = "";
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.d(TAG, "isOpenMicroScene switch_type:" + switch_type);
        return StringUtils.equals("open", switch_type);
    }


    //是否指定了屏幕
    public boolean isScreenSpecified(HashMap<String, Object> map) {
//        LogUtils.e(TAG, "map:" + map.toString());
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String positions = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        String nluInfo = (String) getValueInContext(map, FlowChatContext.DSKey.NLU_INFO);
        LogUtils.d(TAG, "isScreenSpecified screen_name:" + screen_name);
        boolean isScreenSpecified = !StringUtils.isBlank(screen_name) || !StringUtils.isBlank(positions);
        if (!isScreenSpecified)
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        else
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, getAssignScreen(map));
        if (nluInfo.contains("systemControl_personalCenter")) {
            map.put("app_name", "个人中心");
        }
        return isScreenSpecified;
    }

    //是否仅有声源位置
    public boolean isOnlySoundLocation(HashMap<String, Object> map) {
        boolean isOnlySoundLocation = false;
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String assignPosition = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        String tab_name = map.containsKey("tab_name") ? (String) getValueInContext(map, "tab_name") : "";
        if (StringUtils.isBlank(screen_name) && StringUtils.isBlank(assignPosition))
            isOnlySoundLocation = true;
        LogUtils.d(TAG, "isOnlySoundLocation:" + isOnlySoundLocation);
        if (StringUtils.equals(tab_name, "negative_screen"))
            map.put("app_name", "负一屏");
        else if (StringUtils.equals(tab_name, "application_list"))
            map.put("app_name", "应用中心");
        //位置占位只有个人中心在用
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        return isOnlySoundLocation;
    }

    public boolean isCeilScreenOpend(HashMap<String, Object> map) {
        boolean isCeilScreenOpend = screenInterface.isCeilScreenOpen();
        LogUtils.d(TAG, "isCeilScreenOpend:" + isCeilScreenOpend);
        return isCeilScreenOpend;
    }

    public boolean isHaveCeilScreen(HashMap<String, Object> map) {
        boolean isHaveCeilScreen = isBaseHaveCeilScreen();
        if (isHaveCeilScreen)
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        return isHaveCeilScreen;

    }

    public boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map) {
        return isCeilScreen(map) && !isHaveCeilScreen(map);
    }

    public void openNegativeScreenAndCeilWait(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openNegativeScreenAndCeilWait");
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, splitScreenIScreenStateListener);
        screenInterface.openCeilScreen(-1);
        tempMap = map;
    }

    public void openAllAppAndCeilWait(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openAllAppAndCeilWait");
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, allAppIScreenStateListener);
        screenInterface.openCeilScreen(-1);
        tempMap = map;
    }

    public boolean isOpenConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        LogUtils.i(TAG, "isOpenConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    public boolean isSplitScreenAndAllAppConfirm(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isSplitScreenAndAllAppConfirm");
        return keyContextInMap(map, "choose_type");
    }

    //是否为指定副驾屏幕
    public boolean isTargetScreen(HashMap<String, Object> map) {
        boolean isSecondaryScreen = false;
        String screen = getAssignScreen(map);
        isSecondaryScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER);
        LogUtils.d(TAG, "isTargetScreen screen:" + screen + " ,isSecondaryScreen:" + isSecondaryScreen);
        LogUtils.d(TAG, "isTargetScreen isSecondaryScreen:" + isSecondaryScreen);
        if (!isSecondaryScreen) {
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        }
        return isSecondaryScreen;
    }

    public boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map) {
        boolean isTargetFirstRowLeftScreen;
        String screen = getAssignScreen(map);
        String nluInfo = (String) getValueInContext(map, FlowChatContext.DSKey.NLU_INFO);
        isTargetFirstRowLeftScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL);
        if (!isTargetFirstRowLeftScreen) {
            String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
            String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
            String soundLocation = getSoundLocation(map);
            isTargetFirstRowLeftScreen = StringUtils.isBlank(screenName) && StringUtils.isBlank(position) && !StringUtils.isBlank(soundLocation);
        }
        LogUtils.d(TAG, "isTargetFirstRowLeftScreen screen:" + screen + " ,isTargetFirstRowLeftScreen:" + isTargetFirstRowLeftScreen);
        if (!isTargetFirstRowLeftScreen) {
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, screen);
            map.put("app_name", "个人中心");
        }
        return isTargetFirstRowLeftScreen;
    }

    public boolean isShowingScreenSaverOfSecondary(HashMap<String, Object> map) {
        final boolean[] isOpen = {false};
        launcher.isShowingScreenSaverOfSecondary(new ResultCallBack<Boolean>() {

            public void onResult(Boolean result) {
                isOpen[0] = result;
                LogUtils.d(TAG, "isShowingScreenSaverOfSecondary result:" + result);
            }
        });
        return isOpen[0];
    }

    public boolean showScreenSaverSuccess(HashMap<String, Object> map) {
        final boolean[] showScreenSaverSuccess = {true};
        String awakenLocation = map.containsKey("awakenLocation") ? (String) getValueInContext(map, "awakenLocation") : "";
        launcher.showHideScreenSaver(true, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "showScreenSaverSuccess result:" + result + " ,awakenLocation:" + awakenLocation);
                showScreenSaverSuccess[0] = result;
                if (result && StringUtils.equals(awakenLocation, "first_row_right"))
                    VoiceImpl.getInstance().exDialog();
            }
        });
        return showScreenSaverSuccess[0];

    }

    public boolean hideScreenSaverSuccess(HashMap<String, Object> map) {
        final boolean[] hideScreenSaverSuccess = {true};
        launcher.showHideScreenSaver(false, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "hideScreenSaverSuccess result:" + result);
                hideScreenSaverSuccess[0] = result;
            }
        });
        return hideScreenSaverSuccess[0];
    }


    public boolean isSupportScreenSaver(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportScreenSaver();
    }

    public boolean isSupportSecondScreenSaver(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportSecondScreenSaver();
    }

    public boolean isSupportCeilScreenSaver(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportCeilScreenSaver();
    }

    public boolean isSecondScreenViewMode(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isScreenViewMode(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_PASSENGER));
    }

    public boolean isCeilScreenViewMode(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isScreenViewMode(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CEIL));
    }

    public boolean isShowingScreenSaverOfCeil(HashMap<String, Object> map) {
        final boolean[] isOpen = {false};
        launcher.isShowingScreenSaverOfCeil(new ResultCallBack<Boolean>() {

            public void onResult(Boolean result) {
                isOpen[0] = result;
                LogUtils.d(TAG, "isShowingScreenSaverOfCeil result:" + result);
            }
        });
        return isOpen[0];
    }

    public boolean showCeilScreenSaverSuccess(HashMap<String, Object> map) {
        final boolean[] showScreenSaverSuccess = {true};
        String awakenLocation = map.containsKey("awakenLocation") ? (String) getValueInContext(map, "awakenLocation") : "";
        launcher.showHideCeilScreenSaver(true, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "showCeilScreenSaverSuccess result:" + result + " ,awakenLocation:" + awakenLocation);
                showScreenSaverSuccess[0] = result;
                if (result && !StringUtils.contains(awakenLocation, "first"))
                    VoiceImpl.getInstance().exDialog();
            }
        });
        return showScreenSaverSuccess[0];

    }

    public boolean isSupportScreenSaverChange(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportScreenSaverChange();
    }

    public boolean hideCeilScreenSaverSuccess(HashMap<String, Object> map) {
        final boolean[] hideScreenSaverSuccess = {true};
        launcher.showHideCeilScreenSaver(false, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "hideCeilScreenSaverSuccess result:" + result);
                hideScreenSaverSuccess[0] = result;
            }
        });
        return hideScreenSaverSuccess[0];
    }

    public boolean isFirstRowRight(HashMap<String, Object> map) {
        //指定位置
        String assignPosition = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        boolean isFirstRowRight = StringUtils.equals(FIRST_ROW_RIGHT, getSoundLocation(map)) || StringUtils.equals(FIRST_ROW_RIGHT, assignPosition);
        LogUtils.d(TAG, "assignPosition:" + assignPosition + " ,isFirstRowRight:" + isFirstRowRight);
        if (!isFirstRowRight) {
//            String str = getScreenSaverReplaceStr("", "", getSoundLocation(map));
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        }
        return isFirstRowRight;
    }

    public boolean isFirstRowLeft(HashMap<String, Object> map) {
        String soundLocation = getSoundLocation(map);
        boolean isFirstRowLeft = StringUtils.equals(soundLocation, LOCATION_LIST.get(0));
        if (!isFirstRowLeft) {
            map.put("position_scene", SCREEN_STR.get(assignScreenToFuncName(soundLocation)));
        }
        LogUtils.d(TAG, "isFrontRow soundLocation:" + soundLocation + " ,isFirstRowLeft:" + isFirstRowLeft);
        return isFirstRowLeft;
    }


    public boolean isTargetScreenOrPosition(HashMap<String, Object> map) {
        boolean isTargetScreenOrPosition = false;
        String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        isTargetScreenOrPosition = !StringUtils.isBlank(screenName) || !StringUtils.isBlank(position);
        LogUtils.d(TAG, "isTargetScreenOrPosition isTargetScreenOrPosition:" + isTargetScreenOrPosition);
        if (isTargetScreenOrPosition)
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        return isTargetScreenOrPosition;
    }


    public boolean isCeilScreen(HashMap<String, Object> map) {
        return isBaseCeilScreen(map);
    }

    public boolean isNegativeScreenOpen(HashMap<String, Object> map) {
        boolean isNegativeScreenOpen = launcher.isNegativeScreenOpen(DeviceScreenType.CENTRAL_SCREEN);
        LogUtils.i(TAG, "isNegativeScreenOpen isNegativeScreenOpen:" + isNegativeScreenOpen);
        return isNegativeScreenOpen;
    }

    public boolean isNegativeScreenClose(HashMap<String, Object> map) {
        boolean isNegativeScreenClose = !launcher.isNegativeScreenOpen(DeviceScreenType.CENTRAL_SCREEN);
        LogUtils.i(TAG, "isNegativeScreenClose isNegativeScreenClose:" + isNegativeScreenClose);
        return isNegativeScreenClose;
    }


    public void closeNegativeScreen(HashMap<String, Object> map) {
        //打开关闭不需要实现，判断负一屏是否打开和关闭的接口中是已执行的
        LogUtils.i(TAG, "closeNegativeScreen");
        launcher.closeNegativeScreen(DeviceScreenType.CENTRAL_SCREEN);
    }

    public void openNegativeScreen(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openNegativeScreen");
        launcher.openNegativeScreen(DeviceScreenType.CENTRAL_SCREEN);
    }

    public void openAssignNegativeScreen(HashMap<String, Object> map) {
        //打开指定屏幕负一屏
        String assignScreenName = getAssignScreen(map);
        launcher.openNegativeScreen(DeviceScreenType.fromValue(assignScreenName));
    }

    public void closeAssignNegativeScreen(HashMap<String, Object> map) {
        //关闭指定屏幕负一屏
        String assignScreenName = getAssignScreen(map);
        launcher.closeNegativeScreen(DeviceScreenType.fromValue(assignScreenName));
    }

    public boolean isAssignNegativeScreenOpen(HashMap<String, Object> map) {
        //指定屏幕负一屏是否已打开
        String assignScreenName = getAssignScreen(map);
        boolean isNegativeScreenOpen = launcher.isNegativeScreenOpen(DeviceScreenType.fromValue(assignScreenName));
        LogUtils.i(TAG, "isAssignNegativeScreenOpen isNegativeScreenOpen:" + isNegativeScreenOpen);
        map.put("app_name", "负一屏");
        map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, getAssignScreen(map));
        return isNegativeScreenOpen;
    }

    public boolean isSupportAllPosition(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        LogUtils.d(TAG, "isSupportAllPosition carType:" + carType);
        //todo:替换成海龙封装的是否为多屏方法
        return carType.contains("H37");
    }

    public boolean isOperateFirstLeftWallpaper(HashMap<String, Object> map) {
        boolean isOperateFirstLeftWallpaper = false;
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String positions = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        String soundLocation = getSoundLocation(map);
        if (!StringUtils.isBlank(screen_name)) {
            //中控、滑移屏、主驾屏
            if (StringUtils.equals(screen_name, "screen"))
                isOperateFirstLeftWallpaper = StringUtils.equals(positions, FIRST_ROW_LEFT);
            else if (FIRST_SCREEN.contains(screen_name))
                isOperateFirstLeftWallpaper = true;
        } else if (!StringUtils.isBlank(positions)) {
            isOperateFirstLeftWallpaper = StringUtils.equals(positions, FIRST_ROW_LEFT);
        } else
            isOperateFirstLeftWallpaper = StringUtils.equals(soundLocation, FIRST_ROW_LEFT);
        LogUtils.d(TAG, "isOperateFirstLeftWallpaper screen_name:" + screen_name + " ,positions:" + positions
                + " ,soundLocation:" + soundLocation + " ,isOperateFirstLeftWallpaper:" + isOperateFirstLeftWallpaper);
        if (!isOperateFirstLeftWallpaper) {
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        }
        return isOperateFirstLeftWallpaper;
    }


    public void openPersonalCenterAccountView(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().startToAccountManager();
    }

    public boolean isLogin(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getUserCenter().isLogin();
    }

    public boolean isCarOwnerAccount(HashMap<String, Object> map) {
        return StringUtils.equals(DeviceHolder.INS().getDevices().getUserCenter().getCarOwnerType(), "1");
    }

    public boolean isCarOrdinaryAccount(HashMap<String, Object> map) {
        return StringUtils.equals(DeviceHolder.INS().getDevices().getUserCenter().getCarOwnerType(), "0");
    }

    public boolean isUnVisitorAccount(HashMap<String, Object> map) {
        String carOwnerType = DeviceHolder.INS().getDevices().getUserCenter().getCarOwnerType();
        return !StringUtils.equals(carOwnerType, "2");
    }

    public void openAccountLoginView(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().loginAccount();
    }

    public void logoutAccount(HashMap<String, Object> map) {
        UserCenterInterface userCenterInterface = DeviceHolder.INS().getDevices().getUserCenter();
        String carOwnerType = userCenterInterface.getCarOwnerType();
        if (StringUtils.equals(carOwnerType, "1")) userCenterInterface.loginOut(0, 1);
        else userCenterInterface.loginOut(0, 2);
    }

    public boolean isPersonalCenterView(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getUserCenter().isPersonalCenterView();
    }

    public void openPersonalCenterView(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getUserCenter().openApplocation();
    }

    public boolean isLoginView(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getUserCenter().isLoginView();
    }

    //H56D消息中心改为通知中心，单独模块
    public boolean isMessageCenterIndependent(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isMessageCenterIndependent();
    }

    public boolean isMessageCenterOpen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isMessageCenterOpen();
    }

    public void openMessageCenterH56D(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().switchMessageCenter(true);
    }

    public void closeMessageCenterH56D(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().switchMessageCenter(false);
    }

    public boolean isMessageCenterNotEmpty(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isMessageCenterNotEmpty();
    }

    public void clearMessageCenter(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().clearMessageCenter();
    }

    public boolean isAlbumWallpaper(HashMap<String, Object> map) {
        return !launcher.isSystemWallpaperFlag();
    }

    public boolean is3DWallpaper(HashMap<String, Object> map) {
        return launcher.getCurDesktopType() == 0;
    }

    public boolean isSwitch2DWallpaper(HashMap<String, Object> map) {
        String switch_mode = "";
        if (map.containsKey("switch_mode"))
            switch_mode = (String) getValueInContext(map, "switch_mode");
        LogUtils.i(TAG, "switch_mode is " + switch_mode);
        return StringUtils.equals(switch_mode, "2d");
    }

    public void switchDesktop(HashMap<String, Object> map) {
        LogUtils.i(TAG, "switchDesktop");
        String switch_mode = "";
        if (map.containsKey("switch_mode"))
            switch_mode = (String) getValueInContext(map, "switch_mode");
        launcher.setDesktopType(switch_mode);
    }

    public void switchDesktop2D(HashMap<String, Object> map) {
        launcher.setDesktopType2D();
    }

    public void switchDesktop3D(HashMap<String, Object> map) {
        launcher.setDesktopType3D();
    }

    public boolean isLauncherView(HashMap<String, Object> map) {
        //当前是否在桌面判断修改为默认false(除已对接的霸屏弹窗之外还存在当前获取不到的界面)
//        String assignScreenName = getAssignScreen(map);
//        boolean isLauncherView = LauncherViewUtils.isLauncherView(mContext, assignScreenName);
//        return isLauncherView;
        return false;
    }

    //H56D R挡禁用主驾页面相关操作
    public boolean isGearsRForbidden(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation() && isTargetFirstRowLeftScreen(map) && isH56DCar(map);
    }

    public boolean isOnlyGearsRForbidden(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation() && isH56DCar(map);
    }

    public boolean isSupportCutWallPaper(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isGearsROrD() && isTargetFirstRowLeftScreen(map);
    }

    public void backToLauncher(HashMap<String, Object> map) {
        LogUtils.d(TAG, "backToLauncher");
//        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(getAssignScreen(map)), "");
        DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.fromValue(getAssignScreen(map)));
    }


    public void backToCentralScreenLauncher(HashMap<String, Object> map) {
        LogUtils.d(TAG, "backToCentralScreenLauncher");
//        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL));
    }


    public void passengerScreenBackToLauncher(HashMap<String, Object> map) {
        LogUtils.d(TAG, "passengerScreenBackToLauncher");
//        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_PASSENGER), "");
        DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.PASSENGER_SCREEN);
    }

    public boolean isNeedBackWidget(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isNeedBackWidget();
    }

    public void backToWidgetHome(HashMap<String, Object> map) {
        LogUtils.d(TAG, "backToWidgetHome");
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL), "");
        DeviceHolder.INS().getDevices().getLauncher().backToWidgetHome();
    }


    public boolean isCurrent2Desktop(HashMap<String, Object> map) {
        int currentDesktopType = launcher.getCurDesktopType();
        LogUtils.i(TAG, "isCurrent2Desktop currentDesktopType is " + currentDesktopType);
        return currentDesktopType != 0;
    }


    public boolean isCurrent3Desktop(HashMap<String, Object> map) {
        int currentDesktopType = launcher.getCurDesktopType();
        LogUtils.i(TAG, "isCurrent3Desktop currentDesktopType is " + currentDesktopType);
        return currentDesktopType == 0;
    }


    public void cutWallPaper(HashMap<String, Object> map) {
        LogUtils.i(TAG, "CutWallPaper");
        String control_type = "next";
        if (map.containsKey("control_type"))
            control_type = (String) getValueInContext(map, "control_type");
        launcher.setApplySystemWallpaper(control_type);
    }


    public int openWallpaperPage(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openWallpaperPage");
        int status = -1;
        //todo
        /*if (mIPageShowManagerImpl != null) {
            try {
                status = mIPageShowManagerImpl.showWallpaper();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }*/
        LogUtils.d(TAG, "openWallpaperPage status: " + status);
        return status;
    }

    public boolean isAppLogin(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        LogUtils.d(TAG, "isAppLogin appName: " + appName + " uiName: " + uiName + " domain: " + domain);
        if ("navi".equals(domain) || "map".equals(domain)) {
            return true;
        }
        // 媒体收藏历史使用
        boolean isHistory = ApplicationConstant.UI_NAME_HISTORY.equals(uiName);
        boolean isCollection = ApplicationConstant.UI_NAME_COLLECTION.equals(uiName);
        if (isHistory || isCollection) {
            if (appName != null) {
            } else {
                // TODO: 2024/11/10 domian先不判断 打开历史记录domain会返回Music
            }
        }
        return true;
    }


    public boolean isAppExist(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        //在未安装应用时，需要检测是否安装应用(用哔哩打开播放历史)
        if (DOMAIN_NAVI.equals(domain) && ApplicationConstant.UI_NAME_HISTORY.equals(uiName)) {
            map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("5010200").getTts());
            return false;
        }

        if (StringUtils.isBlank(appName) && (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName) || ApplicationConstant.UI_NAME_HISTORY.equals(uiName))) {
            return true;
        }

        //写自己应用是否存在的判断逻辑
        if (!StringUtils.isEmpty(appName) && (appName.contains("互联") || appName.contains("voyah_share"))) {
            return true;
        }

        if (!StringUtils.isEmpty(appName) && (appName.contains("泊车辅助"))) {
            return true;
        }

        if (StringUtils.equals(appName, ApplicationConstant.SLOT_APP_NAME_AI_DRAWING)) {
            return true;
        }

        if (StringUtils.equals(appName, ApplicationConstant.APP_NAME_GALLERY)) {
            return true;
        }

        if (StringUtils.equals(appName, ApplicationConstant.SLOT_APP_NAME_WEATHER)) {
            return true;
        }

        if (!StringUtils.isEmpty(uiName) && uiName.contains(ApplicationConstant.UI_NAME_FILE_TRANSFER)) {
            return true;
        }

        if (isMediaApp(map)) {
            return true;
        }

        if (!StringUtils.isEmpty(appName)) {
            return DeviceHolder.INS().getDevices().getSystem().getApp().isInstalledByAppName(appName);
        }
        return false;
    }

    /**
     * 关闭应用不带应用名称，走退出分屏或者回到桌面
     *
     * @param map
     */
    public void backOrNavi(HashMap<String, Object> map) {
        String screen = getScreenFromSound(map);
        SplitScreenInterface split = DeviceHolder.INS().getDevices().getSystem().getSplitScreen();
        if (!DeviceHolder.INS().getDevices().getCarServiceProp().isVCOS15()
                && split.isSplitScreening()) {
            split.enterFullScreen();
        } else {
            launcher.backToHome(DeviceScreenType.fromValue(screen));
        }
    }

    public boolean hasTargetApp(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        return StringUtils.isNotBlank(appName) || StringUtils.isNotBlank(uiName);
    }

    /**
     * 判读当前应用是否在岚图商城里。
     */
    public boolean isInLanTuStore(HashMap<String, Object> map) {
        return true;
    }

    /**
     * 打开岚图商城，搜索页显示搜索结果。
     */
    public void openLanTuStore(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        if (StringUtils.isBlank(appName)) {
            map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("4003100").getSelectTTs());
        } else {
            hasValidPosInContext(map);
            int code = DeviceHolder.INS().getDevices().getAppStore().searchApp(DeviceScreenType.fromValue(getTargetScreenFromContext(map)), appName);
            if (DeviceHolder.INS().getDevices().getAppStore().isSuccessCode(code)) {
                map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("4044801").getSelectTTs());
            } else {
                map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("4044002").getSelectTTs());
            }
        }
    }


    /**
     * 因为爱奇艺里有打开媒体收藏夹的功能，没办法把打开界面和打开应用区分出来。
     */
    
    /*public void openApp(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        if ("地图".equals(appName)) {
            DeviceHolder.INS().getDevices().getNavi().openNaviApp();
        }
        AppStorePage appStorePage = AppStorePage.fromName(uiName);
        LogUtils.i("AppStoreInterfaceImpl", "appName:" + appName);
        if (ApplicationConstant.APP_NAME_APPSTORE.equals(appName) || appStorePage.getValue() != AppStorePage.PageNone.getValue()) {
            hasValidPosInContext(map);
            if (StringUtils.isEmpty(uiName)) {
                DeviceHolder.INS().getDevices().getAppStore().openAppStore(DeviceScreenType.fromValue(getTargetScreenFromContext(map)));
            } else {
                if (appStorePage.getValue() != AppStorePage.PageNone.getValue()) {
                    String tts = "商城%s打开了#打开商城%s了";
                    tts = TtsUtils.getTtsByString(tts, appStorePage.getChName());
                    map.put(APPLICATION_TTS, tts);
                    DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(getTargetScreenFromContext(map)), appStorePage.getValue());
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStore(DeviceScreenType.fromValue(getTargetScreenFromContext(map)));
                }
            }
            return;
        }


        if (!StringUtils.isEmpty(appName) && (appName.contains("互联") || appName.contains("voyah_share"))) {
            ShareUtils.getInstance().openShareApp();
            return;
        }

        if (!StringUtils.isEmpty(appName) && (appName.contains("泊车辅助"))) {
            carPropHelper.setIntProp(H56C.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ, 1);
            return;
        }

        if (!StringUtils.isEmpty(uiName) && uiName.contains(ApplicationConstant.UI_NAME_FILE_TRANSFER)) {
            ShareUtils.getInstance().openFileTransfer();
            return;
        }

        if (StringUtils.equals(appName, ApplicationConstant.APP_NAME_SHOW_BOX)) {
            ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
            if (showBoxInterface != null) {
                showBoxInterface.openApp();
                return;
            }
        }

        if (StringUtils.equals(appName, ApplicationConstant.SLOT_APP_NAME_GALLERY)) {
            GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
            if (galleryInterface != null) {
                galleryInterface.openApp();
                return;
            }
        }

        LogUtils.d(TAG, "openApp appName:" + appName);

        //兜底走系统打开应用的逻辑
        if (!StringUtils.isEmpty(appName)) {
            String packageName = DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName);
            DeviceHolder.INS().getDevices().getSystem().getApp().openApp(packageName, null);

        }

    }*/

    /**
     * 仅酷玩盒子使用
     *
     * @param map
     */
    public void closeApp(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        LogUtils.d(TAG, "closeApp appName:" + appName);

        String tabName = getOneMapValue("tab_name", map);
        LogUtils.d(TAG, "closeApp tabName:" + tabName);
        if (StringUtils.equals(tabName, ApplicationConstant.TAB_NAME_FUNNY_EXOCYTOSIS)
                || StringUtils.equals(tabName, ApplicationConstant.TAB_NAME_MUSIC_LIGHT_SHOW)) {
            ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
            showBoxInterface.closeApp();
        }
    }

    public boolean isGalleryForeground(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isGalleryForeground");
        boolean galleryForeground = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            galleryForeground = galleryInterface.isAppForeground();
        }

        return galleryForeground;
    }


    public void openGallery(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openGallery");
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            galleryInterface.openApp();
            map.put("app_name", ApplicationConstant.APP_NAME_GALLERY);
        }
    }


    public void closeGallery(HashMap<String, Object> map) {
        boolean closeFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            closeFlag = galleryInterface.closeApp();
        }
        LogUtils.i(TAG, "closeGallery closeFlag:" + closeFlag);
    }


    public boolean isTabGalleryForeground(HashMap<String, Object> map) {
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface == null) {
            return false;
        }
        String tabName = getOneMapValue("tab_name", map);
        LogUtils.d(TAG, "isTabGalleryForeground tabName:" + tabName);
        boolean tabGalleryForeground = false;
        int galleryType = galleryInterface.getGalleryType(tabName);
        if (galleryType != -1) {
            LogUtils.i(TAG, "isTabGalleryForeground galleryType:" + galleryType);
            tabGalleryForeground = galleryInterface.isTabGalleryForeground(galleryType);
        }

        return tabGalleryForeground;
    }

    public boolean openTabGallery(HashMap<String, Object> map) {
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface == null) {
            return false;
        }

        String tabName = getOneMapValue("tab_name", map);
        LogUtils.i(TAG, "openTabGallery tabName:" + tabName);
        boolean tabGalleryOpened = false;
        int galleryType = galleryInterface.getGalleryType(tabName);
        if (galleryType != -1) {
            LogUtils.i(TAG, "openTabGallery galleryType:" + galleryType);
            tabGalleryOpened = galleryInterface.openTabGallery(galleryType);
        }
        return tabGalleryOpened;
    }


    public boolean isEnterSplitScreen(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "isEnterSplitScreen switch_type is " + switch_type);
        return StringUtils.equals(switch_type, "open");
    }


    public boolean isSplitScreening(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()
                || (operator.getIntProp(CommonSignal.COMMON_SPLIT_SWITCH) == 1 && operator.getBooleanProp(CommonSignal.COMMON_IS_NAVI));
    }


    public void dismissSplitScreen(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().sendDismissSplitBroadcast(false);
    }


    public void dismissLeftSplitScreen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "dismissLiftSplitScreen");
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().sendDismissSplitBroadcast(true);
    }


    public void dismissRightSplitScreen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "dismissRightSplitScreen");
        DeviceHolder.INS().getDevices().getSystem().getSplitScreen().sendDismissSplitBroadcast(false);
    }

    public boolean isSupportSplitFullOrSmallScreen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportSplitFullScreen();
    }


    public boolean assignLeftFullScreen(HashMap<String, Object> map) {
        String position = "";
        if (map.containsKey("positions")) position = (String) map.get("positions");
        LogUtils.d(TAG, "assignLeftFullScreen position:" + position);
        return StringUtils.equals("left_side", position);
    }


    public boolean isHavePosition(HashMap<String, Object> map) {
        //函数存在position中时map存的是函数的position，没有时存的是声源位置
        boolean isSoundSourcePosition = (boolean) map.get("is_sound_source_position");
        LogUtils.d(TAG, "isHavePosition isSoundSourcePosition:" + isSoundSourcePosition);
        return isSoundSourcePosition;
    }

    public boolean isScanningPicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isScanningPicture");
        boolean isScaningFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            isScaningFlag = galleryInterface.isScanningPicture();
        }
        return isScaningFlag;
    }

    public int isEnablePictureScan(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isEnablePictureScan");
        int result = -1;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            result = galleryInterface.isEnablePictureScan();
        }
        return result;
    }

    public boolean isPictureListVisible(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isPictureListVisible");
        boolean pictureListVisible = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            pictureListVisible = galleryInterface.canPictureBrowse();
        }
        return pictureListVisible;
    }

    public boolean browsePicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "browsePicture");
        boolean browseFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            browseFlag = galleryInterface.browsePicture();
        }
        return browseFlag;
    }

    public boolean isBrowsingPicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isBrowsingPicture");
        boolean isBrowsingFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            isBrowsingFlag = galleryInterface.isBrowsingPicture();
        }
        return isBrowsingFlag;
    }

    public void exitBrowsePicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "exitBrowsePicture");
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            galleryInterface.exitBrowsePicture();
        }
    }

    public boolean isFirstItem(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isFirstItem");
        boolean isFirst = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            isFirst = galleryInterface.isFirstItem();
        }
        LogUtils.d(TAG, "isFirstItem isFirst:" + isFirst);
        return isFirst;
    }

    public void setCurrentItem(HashMap<String, Object> map) {
        // control_type=&prev
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            String controlType = getOneMapValue("control_type", map);
            if (StringUtils.equals(controlType, "prev")) {//上一张图片
                galleryInterface.setCurrentItem(-1);
            } else if (StringUtils.equals(controlType, "next")) {//下一张图片
                galleryInterface.setCurrentItem(1);
            }
        }

    }

    public boolean isLastItem(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isLastItem");
        boolean isLast = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            isLast = galleryInterface.isLastItem();
        }
        LogUtils.d(TAG, "isLastItem isLast:" + isLast);
        return isLast;
    }

    public int getGalleryBrowseState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getGalleryBrowseState");
        int browseState = 0;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            browseState = galleryInterface.getGalleryBrowseState();
        }
        LogUtils.d(TAG, "getGalleryBrowseState browseState:" + browseState);
        return browseState;
    }

    public boolean isZoomInMax(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isZoomInMax");
        boolean zoomInMaxFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            zoomInMaxFlag = galleryInterface.isZoomInMax();
        }
        LogUtils.d(TAG, "isZoomInMax zoomInMaxFlag:" + zoomInMaxFlag);
        return zoomInMaxFlag;
    }

    public int zoomInPicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "zoomInPicture");
        int result = 2;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            result = galleryInterface.zoomInPicture();
        }
        LogUtils.d(TAG, "zoomInPicture result:" + result);
        return result;
    }


    public boolean isZoomOutMin(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isZoomOutMin");
        boolean flag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            flag = galleryInterface.isZoomOutMin();
        }
        LogUtils.d(TAG, "isZoomOutMin flag:" + flag);
        return flag;
    }

    public int zoomOutPicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "zoomOutPicture");
        int result = 2;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            result = galleryInterface.zoomOutPicture();
        }
        LogUtils.d(TAG, "zoomOutPicture result:" + result);
        return result;
    }

    public boolean isGalleryRotateEnable(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isGalleryRotateEnable");
        boolean flag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            int currentGalleryType = galleryInterface.getCurrentGalleryType();
            switch (currentGalleryType) {
                case Constant.GalleryType.LOCAL:
                case Constant.GalleryType.USB:
                case Constant.GalleryType.TRANSFER:
                case Constant.GalleryType.GALLERY_TYPE_DEFAULT:
                case Constant.GalleryType.GALLERY_TYPE_TRAVEL:
                case Constant.GalleryType.GALLERY_TYPE_STORE:
                case Constant.GalleryType.GALLERY_TYPE_AI_ALBUM:
                case Constant.GalleryType.CONNECT:
                    flag = true;
                    break;

            }
        }
        LogUtils.d(TAG, "isGalleryRotateEnable flag:" + flag);
        return flag;
    }

    public int rotatePicture(HashMap<String, Object> map) {
        LogUtils.d(TAG, "rotatePicture");
        int result = 2;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            // adjust@adjust_type=&set@direction=&clockwise
            String adjustType = getOneMapValue("adjust_type", map);
            String direction = getOneMapValue("direction", map);
            LogUtils.d(TAG, "rotatePicture adjustType:" + adjustType + " direction:" + direction);
            if (StringUtils.equals(adjustType, "set") && StringUtils.equals(direction, "clockwise")) {
                result = galleryInterface.rotatePicture(1);
            }

        }
        LogUtils.d(TAG, "rotatePicture result:" + result);
        return result;
    }

    public boolean isTabShowBoxForeground(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isTabShowBoxForeground");
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface == null) {
            return false;
        }
        String tabName = getOneMapValue("tab_name", map);
        LogUtils.d(TAG, "isTabShowBoxForeground tabName:" + tabName);
        return showBoxInterface.isShowBoxTabForeground(tabName);
    }

    public void openShowBoxTab(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openShowBoxTab");
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            String tabName = getOneMapValue("tab_name", map);
            LogUtils.d(TAG, "openShowBoxTab tabName:" + tabName);
            boolean result = showBoxInterface.openShowBoxTab(tabName);
        }
    }

    public boolean hasShowBoxFeature(HashMap<String, Object> map) {
        boolean showBoxEnable = true;
        CarServicePropInterface carServiceProp = DeviceHolder.INS().getDevices().getCarServiceProp();
        boolean device_37_flag = carServiceProp.isH37A() || carServiceProp.isH37B();
        boolean device_56_flag = carServiceProp.isH56C() || carServiceProp.isH56D();
        if (device_37_flag) {

        } else if (device_56_flag) {
            showBoxEnable = false;
        }
        LogUtils.d(TAG, "hasShowBoxFeature showBoxEnable:" + showBoxEnable);
        return showBoxEnable;
    }

    public boolean isSwitch(HashMap<String, Object> map) {
        String pageName = getOneMapValue("page_name", map);
        LogUtils.d(TAG, "isSwitch pageName:" + pageName);
        return StringUtils.isEmpty(pageName);
    }

    public boolean isShoutOutOpen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isShoutOutOpen");
        boolean shoutOutOpenFlag = false;
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            shoutOutOpenFlag = showBoxInterface.isShoutOutOpen();

        }
        return shoutOutOpenFlag;
    }

    public boolean isRecorderEnable(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isRecorderEnable");
        boolean recorderEnable = false;
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            recorderEnable = showBoxInterface.isRecorderEnable();
        }
        LogUtils.d(TAG, "isRecorderEnable recorderEnable:" + recorderEnable);
        return recorderEnable;
    }

    public boolean openShoutOut(HashMap<String, Object> map) {
        boolean shoutOutOpenFlag = false;
        LogUtils.d(TAG, "openShoutOut");
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            shoutOutOpenFlag = showBoxInterface.openShoutOut();
        }
        return shoutOutOpenFlag;
    }

    public boolean isParkingGearPosition(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isParkingGearPosition");
        boolean parkingGearFlag = false;
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            parkingGearFlag = showBoxInterface.isParkingGearPosition();
        }
        return parkingGearFlag;
    }

    public boolean isMusicLightShowOpen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isMusicLightShowOpen");
        boolean musicLightShowFlag = false;
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            musicLightShowFlag = showBoxInterface.isMusicLightShowOpen();
        }
        return musicLightShowFlag;
    }

    public void closeMusicLightShowSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "closeMusicLightShowSwitch");
        ShowBoxInterface showBoxInterface = DeviceHolder.INS().getDevices().getShowBox();
        if (showBoxInterface != null) {
            showBoxInterface.closeMusicLightShowSwitch();
        }
    }

    public boolean getDlnaState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        String targetScreen = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
        return shareInterface.getDlnaState(targetScreen, switchType.equals("open"));
    }

    public void setDlnaState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        String targetScreen = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
        if (switchType.equals("open")) {
            shareInterface.openDlnaState(targetScreen);
        } else if (switchType.equals("close")) {
            shareInterface.closeDlna();
        }
    }

    public boolean isDlnaVideo(HashMap<String, Object> map) {
        boolean dlnaVideo = shareInterface.isDlnaOpened();
        boolean dlnaMainActivity = shareInterface.isDlnaMainActivity();
        LogUtils.d(TAG, "isDlnaVideo dlnaVideo : " + dlnaVideo);
        return dlnaVideo && dlnaMainActivity;
    }


    public boolean isMirrorConnecting(HashMap<String, Object> map) {
        int connectState = shareInterface.getConnectState();
        return connectState == 1;
    }

    public boolean isMirrorConnected(HashMap<String, Object> map) {
        int connectState = shareInterface.getConnectState();
        return connectState == 2;
    }


    public int isDlnaVideoPlaying(HashMap<String, Object> map) {
        int dlnaVideoPlaying = shareInterface.isDlnaVideoPlaying();
        LogUtils.d(TAG, "isDlnaVideoPlaying dlnaVideoPlaying : " + dlnaVideoPlaying);
        return dlnaVideoPlaying;
    }

    public void setDlnaVideoState(HashMap<String, Object> map) {
        String controlType = (String) getValueInContext(map, "control_type");
        if (controlType.equals("start")) {
            shareInterface.startDlnaVideo();
        } else if (controlType.equals("pause")) {
            shareInterface.pauseDlnaVideo();
        }
    }

    public boolean isCentralScreen(HashMap<String, Object> map) {
        String targetScreen = getTargetScreenFromContext(map);
        return targetScreen.equalsIgnoreCase("central_screen");
    }

    public int openScreenMirroring(HashMap<String, Object> map) {
        return shareInterface.openScreenMirroring();
    }

    public int closeScreenMirroring(HashMap<String, Object> map) {
        return shareInterface.closeScreenMirroring();
    }

    public boolean isScreenMirroringFullScreen(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        return switch_mode.equalsIgnoreCase("full_screen");
    }

    public int openScreenMirroringFullScreen(HashMap<String, Object> map) {
        return shareInterface.openScreenMirroringFullScreen();
    }

    public int openScreenMirroringSmallWindow(HashMap<String, Object> map) {
        return shareInterface.openScreenMirroringSmallWindow();
    }

    public boolean isNewPlan(HashMap<String, Object> map) {
        return shareInterface.isNewPlan();
    }

    /**
     * ----------------------- 情景模式 start------------------------------------------------------
     */

    public boolean isSupportSceneMode(HashMap<String, Object> map) {
        String tabName = getOneMapValue("tab_name", map);
        if (!TextUtils.isEmpty(tabName)) {
            switch (tabName) {
                case "pets":
                case "watch_movies":
                    return isH37ACar(map) || isH37BCar(map);
                case "car_wash":
                    return true;
                case "nap":
                case "offcar_eletric":
                    return isH56CCar(map);
            }
        }
        return false;
    }

    /**
     * 判断副驾观影是否已打开
     *
     * @param map 指令的数据
     * @return 车辆挡位数据
     */
    public boolean isMovingMode(HashMap<String, Object> map) {
        int modeStatus = DeviceHolder.INS().getDevices().getCarService().getSystemSetting()
                .getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "system_movie_mode", 0);
        LogUtils.d(TAG, "modeStatus: " + modeStatus);
        return modeStatus == 1;
    }

    /**
     * 判断是否是p挡
     *
     * @param map 指令的数据
     * @return 车辆挡位数据
     */
    public boolean getDrvStatus(HashMap<String, Object> map) {
        return operator.getIntProp(CommonSignal.COMMON_GEAR_INFO) == 0;
    }

    /**
     * 获取当前电量
     *
     * @param map 指令的数据
     * @return 当前电量
     */
    public float getElcPower(HashMap<String, Object> map) {
        float curPower = operator.getFloatProp(CommonSignal.COMMON_REMAIN_POWER);
        LogUtils.d(TAG, "getElcPower: " + curPower);
        return curPower;
    }

    public boolean isValidPosition(HashMap<String, Object> map) {
        String positions = getOneMapValue("positions", map);
        if (!StringUtils.isEmpty(positions)) {
            return positions.equals("first_row_right");
        }
        return true;
    }

    public boolean isMoving(HashMap<String, Object> map) {
//        return ScreenHelper.isMoving();
        return operator.getBooleanProp(ScreenSignal.SCREEN_MOVE_STATE);
    }

    /**
     * 宠物模式确认界面	state_pet_confirm
     * 宠物模式激活状态	state_pet_in_activation
     * 洗车模式确认界面	state_car_wash_confirm
     * 洗车模式倒计时界面	state_car_wash_count_down
     * 观影模式确认界面	state_movie_confirm
     * 观影模式进入中界面	state_movie_entering
     * 观影模式激活状态	state_movie_in_activation
     * 离车不下电模式确认界面	state_keeppower_confirm
     * 离车不下电激活状态	state_keeppower_active
     * 小憩弹窗	state_nap_confirm
     * 小憩主界面	state_nap_view
     * 小憩激活状态	state_nap_active
     */
    public boolean isSceneOpen(HashMap<String, Object> map) {
        String state = getOneMapValue("scene_mode_state", map);
        LogUtils.d(TAG, "state: " + state);
//        String modeStatus = Settings.System.getString(Utils.getApp().getContentResolver(), "com.voyah.cockpit.scenemode.state");
        String modeStatus = DeviceHolder.INS().getDevices().getCarService().
                getSystemSetting().getString(ISysSetting.SystemSettingType.SETTING_SYSTEM, "com.voyah.cockpit.scenemode.state");
        LogUtils.d(TAG, "modeStatus: " + modeStatus);
        if (!StringUtils.isEmpty(state)) {
            boolean isOpen = state.equals(modeStatus);
            if (isOpen) {
                map.put("scene_loading_mode", modeStatus);
            }
            return isOpen;
        }
        return false;
    }

    /**
     * 打开宠物模式确认界面	com.voyah.cockpit.scenemode.OpenPetConfirm
     * 关闭宠物模式确认界面	com.voyah.cockpit.scenemode.ClosePetConfirm
     * 打开宠物模式	com.voyah.cockpit.scenemode.OpenPet
     * 关闭宠物模式	com.voyah.cockpit.scenemode.ClosePet
     * 打开洗车模式确认界面	com.voyah.cockpit.scenemode.OpenCarWashConfirm
     * 关闭洗车模式确认界面	com.voyah.cockpit.scenemode.CloseCarWashConfirm
     * 打开洗车模式倒计时界面	com.voyah.cockpit.scenemode.OpenCarWashLoading
     * 关闭洗车模式倒计时界面	com.voyah.cockpit.scenemode.ClsoeCarWashLoading
     * 打开观影模式确认界面	com.voyah.cockpit.scenemode.OpenMovieConfirm
     * 关闭观影模式确认界面	com.voyah.cockpit.scenemode.CloseMovieConfirm
     * 打开观影模式	com.voyah.cockpit.scenemode.OpenMovie
     * 打开小憩的弹窗	com.voyah.cockpit.scenemode.OpenNapConfirm
     * 关闭小憩的弹窗	com.voyah.cockpit.scenemode.CloseNapConfirm
     * 打开小憩模式	com.voyah.cockpit.scenemode.OpenNap
     * 关闭小憩模式	com.voyah.cockpit.scenemode.CloseNap
     * 打开离车不下电弹窗	com.voyah.cockpit.scenemode.OpenLeaveKeepPowerConfirm
     * 打开离车不下电	com.voyah.cockpit.scenemode.OpenLeaveKeepPower
     * 关闭离车不下电弹窗	com.voyah.cockpit.scenemode.CloseLeaveKeepPowerConfirm
     */
    public void setSceneMode(HashMap<String, Object> map) {
        String state = getOneMapValue("scene_mode", map);
        LogUtils.d(TAG, "setSceneMode: " + state);
        if (isH37ACar(map) || isH37BCar(map)) {
            DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                    .startSceneModeService(state);
            return;
        }
        if (state.contains("NapConfirm")) {
            int mainDisplayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getMainScreenDisplayId();
            int passengerDisplayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getPassengerScreenDisplayId();
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            int displayId = curPosition == 1 ? passengerDisplayId : mainDisplayId;
            String position = getOneMapValue("positions", map);
            int napSeat = -1;
            if (!StringUtils.isEmpty(position)) {
                napSeat = getDealPosition(map);
            }
            String timeStr = getOneMapValue("duration", map);
            int napTime = -1;
            if (!TextUtils.isEmpty(timeStr)) {
                napTime = getNapOrderTime(map);
            }
            DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                    .setSceneMode(state, displayId, napSeat, napTime);
            return;
        }
        if (isH37ACar(map) || isH37ACar(map)) {
            DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                    .startSceneModeService(state);
        } else {
            DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                    .setSceneMode(state, -1, -1, -1);
        }
    }

    public void openPetMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openPetMode: ");
        DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                .startSceneModeService("com.voyah.cockpit.scenemode.OpenPet");
    }

    public void openLeaveKeepPower(HashMap<String, Object> map) {
        String state = getOneMapValue("scene_mode", map);
        LogUtils.d(TAG, "setSceneMode: " + state);
//        SysSettingHelper.startSceneModeService("com.voyah.cockpit.scenemode.OpenLeaveKeepPower");
        DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                .setSceneMode("com.voyah.cockpit.scenemode.OpenLeaveKeepPower", -1, -1, -1);
    }

    public void openNapMode(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                .setSceneMode("com.voyah.cockpit.scenemode.OpenNap", -1, getDealPosition(map), -1);
    }

    public void openNapTimeMode(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getCarService().getSceneModeInterface()
                .setSceneMode("com.voyah.cockpit.scenemode.OpenNap", -1, -1, getNapOrderTime(map));
    }

    private int getNapOrderTime(HashMap<String, Object> map) {
        int time = 0;
        String timeStr = getOneMapValue("duration", map);
        if (!StringUtils.isEmpty(timeStr)) {
            time = Integer.parseInt(timeStr) / 60;
        }
        if (time <= 120) {
            time = (int) (Math.ceil((double) time / 5) * 5);
        } else {
            time = 125;
        }
        LogUtils.d(TAG, "getNapOrderTime: " + time);
        return time;
    }

    public boolean isOpenParkingApp(HashMap<String, Object> map) {
        int status = operator.getIntProp(CommonSignal.COMMON_360_STATE);
        LogUtils.d(TAG, "isOpenParkingApp: " + status);
        return status != 0;
    }

    public boolean isHaveNluPosition(HashMap<String, Object> map) {
        String str = getOneMapValue("positions", map);
        return !StringUtils.isEmpty(str);
    }

    public boolean isEnablePosition(HashMap<String, Object> map) {
        return getDealPosition(map) != -1;
    }

    private int getDealPosition(HashMap<String, Object> map) {
        int area = -1;
        List<Integer> positionList = getMethodPosition(map);
        LogUtils.d(TAG, "getDealPosition: " + positionList.toString());
        int fl = -1;
        int fr = -1;
        int rl = -1;
        int rr = -1;
        for (Integer position : positionList) {
            if (position == 0) {
                fl = 1;
            } else if (position == 1) {
                fr = 1;
            } else if (position == 2) {
                rl = 1;
            } else if (position == 3) {
                rr = 1;
            }
        }
        // 互斥的场景需要兜住
        if (fl == 1 && rl == 1) {
            return area;
        } else if (fr == 1 && rr == 1) {
            return area;
        }
        // 设置组合位置或单独位置
        if (fl == 1 && rr == 1) {
            area = 12;
            map.put("seat", "主驾和二排右");
        } else if (fr == 1 && rl == 1) {
            area = 21;
            map.put("seat", "副驾和二排左");
        } else if (fl == 1 && fr == 1) {
            area = 30;
            map.put("seat", "前排");
        } else if (rl == 1 && rr == 1) {
            area = 03;
            map.put("seat", "后排右");
        } else if (fl == 1) {
            area = 10;
            map.put("seat", "主驾");
        } else if (fr == 1) {
            area = 20;
            map.put("seat", "副驾");
        } else if (rl == 1) {
            area = 01;
            map.put("seat", "二排左");
        } else if (rr == 1) {
            area = 02;
            map.put("seat", "二排右");
        }
        LogUtils.d(TAG, "getDealPosition : area" + area);
        return area;
    }

    public List<Integer> getMethodPosition(HashMap<String, Object> map) {
        ArrayList<Integer> list = new ArrayList<>();
        //表达式循环多位置的处理，此时只会获得一个位置信息。
        if (map.containsKey(DCContext.FLOW_KEY_ONE_POSITION)) {
            int position = (int) map.get(DCContext.FLOW_KEY_ONE_POSITION);
            list.add(position);
            return list;
        }
        //regulatingTemp(position:total_car)
        //图中设置进来的位置信息需要处理的情况
        HashMap<String, Object> graphContext = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphContext.containsKey(DCContext.MAP_KEY_SLOT_POSITION)) {
            String position = (String) map.get(DCContext.MAP_KEY_SLOT_POSITION);
            return locationHelper.convertLocationInformation(position);
        }

        ArrayList<Integer> mapList = (ArrayList<Integer>) map.get(DCContext.MAP_KEY_METHOD_POSITION);
        if (mapList != null) {
            list = mapList;
        }
        return list;
    }

    /**
     * ----------------------- 情景模式 end------------------------------------------------------
     */


    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.d(TAG, "replacePlaceHolder map = " + map + ", str = " + str);
        int index = 0;
        String repTTs = (String) getValueInContext(map, APPLICATION_TTS);
        if (!StringUtils.isEmpty(repTTs)) {
            return repTTs;
        }

        String slotAppName = (String) getValueInContext(map, "app_name");
        LogUtils.d(TAG, "replacePlaceHolder slotAppName:" + slotAppName);


        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        String app_name;
        if (keyContextInMap(map, APPLICATION_TTS_NAME)) {
            app_name = (String) getValueInContext(map, APPLICATION_TTS_NAME);
        } else {
            app_name = map.containsKey("app_name") ? (String) getValueInContext(map, "app_name") : "";
        }
        //打开页面
//            if (map.containsKey("ui_name")) {
//                //车鱼视听不支持打开/关闭历史记录/收藏列表
//                if (!"车鱼视听".equals(app_name) && !"咪咕视频".equals(app_name)) {
//                    String uiName = (String) getValueInContext(map, "ui_name");
//                    switch (uiName) {
//                        case "history":
//                            app_name = "观看历史";
//                            break;
//                        case "collection":
//                            if (!"爱奇艺".equals(app_name)) {
//                                app_name = "我的收藏";
//                            }
//                            break;
//                    }
//                }
//            }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);
        switch (key) {
            case "app_name":
                if (StringUtils.equals(slotAppName, ApplicationConstant.SLOT_APP_NAME_GALLERY)) {
                    if (StringUtils.isEmpty(app_name)) {
                        LogUtils.d(TAG, "replacePlaceHoler app_name is empty, and reset ...");
                        app_name = slotAppName;
                    }
                }
                str = str.replace("@{app_name}", app_name);
                break;
            case "tab_name":
                String tabName = getOneMapValue("tab_name", map);
                String slotSwitchType = (String) getValueInContext(map, "switch_type");

                GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
                if (galleryInterface != null) {
                    tabName = galleryInterface.getGalleryName(tabName);
                }

                str = str.replace("@{tab_name}", tabName);
                break;
            case "mode_name":
                String modeName = getOneMapValue("scene_loading_mode", map);
                String mergeStr = "";
                if (!StringUtils.isEmpty(modeName)) {
                    if (modeName.equals("state_car_wash_count_down")) {
                        mergeStr = "洗车模式";
                    } else if (modeName.equals("state_movie_entering") || modeName.equals("state_movie_in_activation")) {
                        mergeStr = "副驾观影模式";
                    } else if (modeName.equals("state_nap_confirm") || modeName.equals("state_nap_active")) {
                        mergeStr = "小憩模式";
                    }
                }
                str = str.replace("@{mode_name}", mergeStr);
                break;
            case "screen_name":
                String screenName = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
                String replaceStr = "中控屏";
                if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_PASSENGER)) {
                    replaceStr = "副驾屏";
                } else if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_CEIL)) {
                    replaceStr = "吸顶屏";
                }
                str = str.replace("@{screen_name}", replaceStr);
                break;
            case "seat":
                String seat = getOneMapValue("seat", map);
                if (!StringUtils.isEmpty(seat)) {
                    str = str.replace("@{seat}", seat);
                }
                break;
            case "time":
                str = str.replace("@{time}", String.valueOf(getNapOrderTime(map) == 125 ? "不限时" : getNapOrderTime(map)));
                break;
            case "position_scene":
                String position_scene = map.containsKey("position_scene") ? (String) getValueInContext(map, "position_scene") : "";
                str = str.replace("@{position_scene}", position_scene);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);
        if (map.containsKey("ui_name") && "车鱼视听".equals(app_name)) {
            str = TtsReplyUtils.getTtsBean("4003100").getSelectTTs();
        } else if (map.containsKey("ui_name") && "雷石KTV".equals(app_name)) {
            str = TtsReplyUtils.getTtsBean("4003100").getSelectTTs();
        } else if (map.containsKey("ui_name") && "咪咕视频".equals(app_name)) {
            //咪咕视频自己处理
            str = "";
        } else if (map.containsKey("ui_name") && "爱奇艺".equals(app_name)) {
            //爱奇艺自己处理收藏的TTS播报
            str = "";
        } else if (map.containsKey("ui_name") && "bilibili".equals(app_name)) {
            //哔哩哔哩自己处理收藏/历史的TTS播报
            str = "";
        }


        LogUtils.d(TAG, "replacePlaceHolder str:" + str);
        return str;
    }

    public boolean isParkApp(HashMap<String, Object> map) {
        //如果打开的是泊车辅助app，不需要tts播报，泊车辅助应用自己会播报
        if (map.containsKey("app_name")) {
            String appName = getOneMapValue("app_name", map);
            if (!StringUtils.isEmpty(appName) && (appName.contains("泊车辅助"))) {
                return true;
            }
        }
        return false;
    }

    public boolean isParkAppOpened(HashMap<String, Object> map) {
        int intProp = operator.getIntProp(CommonSignal.COMMON_360_STATE);
        return intProp != 0;
    }

    public void setParkAppState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(CommonSignal.COMMON_PARK_APP_STATE, switchType.equals("open"));
    }


    /**===============================打开关闭应用（支持56多开）==================================**/

    /**
     * APP 名称转换成包名并在上下文中缓存
     *
     * @param map
     * @return
     */
    private String extractPkgName(HashMap<String, Object> map) {
        String pkgName = getOneMapValue(FuncConstants.KEY_PKG_NAME, map);
        //有缓存取缓存
        if (StringUtils.isNotBlank(pkgName)) {
            return pkgName;
        }
        String appName = getOneMapValue(FuncConstants.KEY_APP_NAME, map);
        if (appName == null) return "";
        pkgName = DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName);
        map.put(FuncConstants.KEY_PKG_NAME, pkgName);//缓存一下
        return pkgName;
    }

    public boolean isSupportMulti(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        return appInterface.isSupportMulti(pkgName);
    }

    /**
     * 上下文带位置
     *
     * @param map
     * @return
     */
    public boolean hasValidPosInContext(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        if (StringUtils.isNotBlank(position)) {
            int screenType = DeviceHolder.INS().getDevices().getSystem().getScreen().getScreenType(position);
            switch (screenType) {
                case 0:
                    map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
                    break;
                case 1:
                    map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_PASSENGER);
                    break;
                case 2:
                    map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CEIL);
                    break;
            }
            map.put(FuncConstants.KEY_SPECIFIED_SCREEN_TYPE, FuncConstants.VALUE_SPECIFIED_SCREEN_ON);
            LogUtils.i(TAG, "screenName:" + getOneMapValue(FuncConstants.KEY_SCREEN_NAME, map));
            return true;
        } else {
            String screenName = getOneMapValue(FuncConstants.KEY_SCREEN_NAME, map);
            LogUtils.i(TAG, "screenName:" + screenName);
            boolean containsScreen = StringUtils.isNotBlank(screenName)
                    && !FuncConstants.VALUE_SCREEN.equalsIgnoreCase(screenName);
            if (containsScreen) {
                map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, screenName);
                map.put(FuncConstants.KEY_SPECIFIED_SCREEN_TYPE, FuncConstants.VALUE_SPECIFIED_SCREEN_ON);
            } else {
                map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, getScreenFromSound(map));
                map.put(FuncConstants.KEY_SPECIFIED_SCREEN_TYPE, FuncConstants.VALUE_SPECIFIED_SCREEN_OFF);
            }
            return StringUtils.isNotBlank(getOneMapValue(FuncConstants.KEY_SCREEN_NAME, map));
        }
    }

    /**
     * 声源提取屏幕位置
     *
     * @return
     */
    public String getScreenFromSound(HashMap<String, Object> map) {
        int soundSource = getSoundSourcePos(map); //声源位置
        String screenName = FuncConstants.VALUE_SCREEN_CENTRAL; //默认中控
        switch (soundSource) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
            default:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
        }
        if (FuncConstants.VALUE_SCREEN_CEIL.equalsIgnoreCase(screenName)
                && !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
            //没有吸顶屏配置的时候给到中控屏
            screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        }
        LogUtils.i(TAG, "getScreenFromSound:" + screenName);
        return screenName;
    }

    private String getTargetScreenFromContext(HashMap<String, Object> map) {
        String targetScreen = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
        LogUtils.i(TAG, "getTargetScreenFromContext:" + targetScreen);
        return targetScreen;
    }

    private String getOriginalScreenFromContext(HashMap<String, Object> map) {
        String originalScreen = getOneMapValue(FuncConstants.KEY_ORIGINAL_SCREEN, map);
        LogUtils.d(TAG, "getOriginalScreenFromContext, originalScreen: " + originalScreen);
        return originalScreen;
    }

    /**
     * 通用逻辑
     *
     * @param map
     * @return
     */
    public boolean isAppOpenedWithScreen(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String targetScreen = getTargetScreenFromContext(map);
        launcher.closeScreenCentral(DeviceScreenType.fromValue(targetScreen), "");
        return isAppOpenedWithScreen(pkgName, targetScreen);
    }

    private boolean isAppOpenedWithScreen(String pkgName, String targetScreen) {
        switch (targetScreen) {
            case FuncConstants.VALUE_SCREEN_CENTRAL:
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CENTRAL_SCREEN);
            case FuncConstants.VALUE_SCREEN_PASSENGER:
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.PASSENGER_SCREEN);
            case FuncConstants.VALUE_SCREEN_CEIL:
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CEIL_SCREEN);
            default:
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CENTRAL_SCREEN);
        }
    }

    /**
     * 语控带明确指定位置使用，是否支持在特定屏幕打开
     *
     * @param map
     * @return
     */
    public boolean isSupportScreen(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String targetScreen = getTargetScreenFromContext(map);
        return appInterface.isAppSupportScreen(pkgName, DeviceScreenType.fromValue(targetScreen)) && //产品定义这个屏幕是否支持打开XX应用
                judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, targetScreen); //硬件配置是否有这个屏幕*/
    }

    public boolean isCarSupportScreen(HashMap<String, Object> map) {
        String targetScreen = getTargetScreenFromContext(map);
        return judgeSupport(CommonSignal.COMMON_SCREEN_ENUMERATION, targetScreen);
    }

    /**
     * 是否期望在吸顶屏打开？
     *
     * @param map
     * @return
     */
    public boolean isCeiling(HashMap<String, Object> map) {
        String targetScreen = getTargetScreenFromContext(map);
        return StringUtils.equalsIgnoreCase(targetScreen, FuncConstants.VALUE_SCREEN_CEIL);
    }

    public void tryOpenAppWithScreen(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String targetScreen = getTargetScreenFromContext(map);
        LogUtils.d(TAG, "tryOpenAppWithScreen, pkgName: " + pkgName + ", targetScreen: " + targetScreen);
        DeviceHolder.INS().getDevices().getSystem().getApp().openApp(pkgName, DeviceScreenType.fromValue(targetScreen));

    }

    public void tryCloseAppWithScreen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "tryCloseAppWithScreen");
        String targetScreen = getTargetScreenFromContext(map);
        String pkgName = extractPkgName(map);
        DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(pkgName, DeviceScreenType.fromValue(targetScreen));
    }

    public boolean isTargetCentralScreen(HashMap<String, Object> map) {
        return FuncConstants.VALUE_SCREEN_CEIL.equalsIgnoreCase(getTargetScreenFromContext(map));
    }

    public void tryShowToast(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String targetScreen = getTargetScreenFromContext(map);
        if (!DeviceHolder.INS().getDevices().getSystem().getApp().isPreemptiveApp(pkgName)) {
            //非抢占式，不用弹toast
            return;
        }
        boolean needShow = false;
        String originalScreen = FuncConstants.VALUE_SCREEN_CENTRAL;

        if (isAppOpenedWithScreen(pkgName, FuncConstants.VALUE_SCREEN_CENTRAL)
                && !StringUtils.equalsIgnoreCase(targetScreen, FuncConstants.VALUE_SCREEN_CENTRAL)) {
            //原来在主屏显示，现在希望显示到其他屏
            originalScreen = FuncConstants.VALUE_SCREEN_CENTRAL;
            needShow = true;
        } else if (isAppOpenedWithScreen(pkgName, FuncConstants.VALUE_SCREEN_PASSENGER)
                && !StringUtils.equalsIgnoreCase(targetScreen, FuncConstants.VALUE_SCREEN_PASSENGER)) {
            //原来在副驾显示，现在希望显示到其他屏
            originalScreen = FuncConstants.VALUE_SCREEN_PASSENGER;
            needShow = true;
        } else if (isAppOpenedWithScreen(pkgName, FuncConstants.VALUE_SCREEN_CEIL)
                && !StringUtils.equalsIgnoreCase(targetScreen, FuncConstants.VALUE_SCREEN_CEIL)) {
            //原来在吸顶显示，现在希望显示到其他屏
            originalScreen = FuncConstants.VALUE_SCREEN_CEIL;
            needShow = true;
        }

        if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(targetScreen) || FuncConstants.VALUE_SCREEN_CENTRAL.equals(originalScreen)) {
            if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
                needShow = false;
            }
        }
        if (!needShow) {
            LogUtils.d(TAG, "no need to show toast");
            return;
        }
        String toastMsg = replacePlaceHolder(map, "@{app_name}已在@{screen_name}打开");
        DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.fromValue(originalScreen), toastMsg);
    }

    public boolean isCeilOpen(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_CEIL_OPEN);
    }

    public void openCeilingScreen(HashMap<String, Object> map) {
        int lastOpenAngle = operator.getIntProp(CommonSignal.COMMON_LAST_ANGLE);
        operator.setIntProp(CommonSignal.COMMON_CEIL_ANGLE, lastOpenAngle);
    }

    public void waitCeilOpen(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getScreen().openCeilScreen(15000);
    }

    public void ttsAndOpen(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getScreen().onCeilOpen(new Runnable() {
            private final HashMap<String, Object> param = new HashMap<>(map);

            @Override
            public void run() {
                tryShowToast(param);
                if (isMediaApp(param)) {
                    openMediaApp(param);
                } else {
                    tryOpenAppWithScreen(param);
                }
            }
        });
    }

    /**
     * 存在一个屏幕上是前台
     *
     * @return
     */
    public boolean existFrontScreen(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        boolean isCentral = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CENTRAL_SCREEN);
        boolean isPassenger = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.PASSENGER_SCREEN);
        boolean isCeiling = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CEIL_SCREEN);
        if (isCentral) {
            map.put(FuncConstants.KEY_ORIGINAL_SCREEN, FuncConstants.VALUE_SCREEN_CENTRAL);
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
            launcher.closeScreenCentral(DeviceScreenType.CENTRAL_SCREEN, "");
        } else if (isPassenger) {
            map.put(FuncConstants.KEY_ORIGINAL_SCREEN, FuncConstants.VALUE_SCREEN_PASSENGER);
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_PASSENGER);
            launcher.closeScreenCentral(DeviceScreenType.PASSENGER_SCREEN, "");
        } else if (isCeiling) {
            map.put(FuncConstants.KEY_ORIGINAL_SCREEN, FuncConstants.VALUE_SCREEN_CEIL);
            map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CEIL);
            launcher.closeScreenCentral(DeviceScreenType.CEIL_SCREEN, "");
        }
        return isCentral || isPassenger || isCeiling;
    }

    public void findScreenToOpen(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String canOpenScreen = DeviceHolder.INS().getDevices().getSystem().getApp().fetchScreen(pkgName);
        //实际操作的屏幕
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, canOpenScreen);
    }

    public void findScreenToClose(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String originalScreen = getOriginalScreenFromContext(map);
        //实际操作的屏幕
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, originalScreen);
        DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(pkgName, DeviceScreenType.fromValue(originalScreen));
    }

    public boolean isSecondRound(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isSeatMovementReminder");
        return keyContextInMap(map, "choose_type");
    }

    public void showRRestrictToast(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.CENTRAL_SCREEN, "出于安全考虑，当前场景不支持此操作");
    }

    public boolean isDrivingRestrict(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        return isVCOS15() && isGearsD() && DeviceHolder.INS().getDevices().getSystem().getApp().isDrivingRestrict(pkgName);
    }

    /**
     * 二次确认 选择确认
     */
    public boolean isSecondConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        LogUtils.i(TAG, "isSecondConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }


    /**
     * 打开应用是否需要特殊处理
     *
     * @param map
     * @return
     */
    public boolean isSpecialApps(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        //TODO 媒体、互联、泊车等需要单独处理的汇总白名单
        return isMediaApp(map) || isNavi(map) || isParkApp(map);
    }

    public boolean isCloseSpecialApps(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        //TODO 媒体、等需要单独处理的汇总白名单
        return isMediaApp(map) || isNavi(map) || isParkApp(map);
    }

    /**
     * 是否导航意图，1.明确指定导航 2.“打开收藏”，没指定是导航还是音乐，导航在前台，且不是分屏就给到导航
     *
     * @param map
     * @return
     */
    public boolean isNavi(HashMap<String, Object> map) {
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        isNaviCollection = false;
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            isNaviCollection = DOMAIN_MAP.equals(domain) || DOMAIN_NAVI.equals(domain);
            if (StringUtils.isBlank(domain) && (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront() && (!DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen()))) {
                isNaviCollection = true;
            }
        }
        return isNaviCollection;
    }

    /**
     * 导航收藏是否前台
     *
     * @param map
     * @return
     */
    public boolean isNaviCollectionFront(HashMap<String, Object> map) {
        String domain = getOneMapValue("domain", map);
        String name = DOMAIN_MAP.equals(domain) ? "地图收藏夹" : "导航收藏夹";
        map.put(APPLICATION_TTS_NAME, name);
        return DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()
                && DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPage() == NaviPage.PAGE_FAV.getValue();
    }

    public void openNaviCollection(HashMap<String, Object> map) {
        if (isNaviCollection) {
            if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
                map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("3011303").getSelectTTs());
                LogUtils.i(TAG, "isHideInformation not open favorites");
            } else if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                map.put(APPLICATION_TTS, TtsReplyUtils.getTtsBean("3011302").getSelectTTs());
                LogUtils.i(TAG, "in navigation not open favorites");
            } else {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
            }
        }
    }

    public void closeNaviCollection(HashMap<String, Object> map) {
        if (isNaviCollection) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(false);
        }
    }

    public boolean isMediaApp(HashMap<String, Object> map) {
        String pkgName = extractPkgName(map);
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        //媒体单独做逻辑
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            return !isNavi(map);
        } else if (ApplicationConstant.UI_NAME_HISTORY.equals(uiName) || DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().isMedia(appName)) {
            return true; //TODO zhangyifan
        } else {
            //视频应用自己处理
            return DeviceHolder.INS().getDevices().getMediaCenter().isVideoApp(pkgName);
        }
    }

    public boolean isCloseMediaApp(HashMap<String, Object> map) {
        return isMediaApp(map);
    }

    public void openMediaApp(HashMap<String, Object> map) {
        LogUtils.i(TAG, "openMediaApp");
        hasValidPosInContext(map);
        String screenName = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
        String soundLocation = getSoundLocation(map);
        closeAssignScreenAllApps(map);
        //todo
        DeviceHolder.INS().getDevices().getMediaCenter().initVoicePosition(screenName, soundLocation);

        String pkgName = extractPkgName(map);
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String mediaType = getOneMapValue("domain", map);
        String isSpecifiedScreen = getOneMapValue(FuncConstants.KEY_SPECIFIED_SCREEN_TYPE, map);

        if (DeviceHolder.INS().getDevices().getMediaCenter().switchVideoPage(uiName, appName, false, mediaType)) {
            return;
        }

        switchMediaAppControl(appName, uiName, true);
    }

    public void closeMediaApp(HashMap<String, Object> map) {
        LogUtils.i(TAG, "closeMediaApp");
        hasValidPosInContext(map);
        String screenName = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
        String soundLocation = getSoundLocation(map);
        //todo
        DeviceHolder.INS().getDevices().getMediaCenter().initVoicePosition(screenName,soundLocation);

        String pkgName = extractPkgName(map);
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String mediaType = getOneMapValue("domain", map);
        String isSpecifiedScreen = getOneMapValue(FuncConstants.KEY_SPECIFIED_SCREEN_TYPE, map);
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
            screenName = DeviceScreenType.CENTRAL_SCREEN.name();
        }

        if (DeviceHolder.INS().getDevices().getMediaCenter().switchVideoPage(uiName, appName, false, mediaType)) {
            return;
        }

        LogUtils.d(TAG, "closeMediaApp music");
        switchMediaAppControl(appName, uiName, false);
    }

    private void switchMediaAppControl(String appName, String uiName, boolean isOpen) {
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().switchCollectUI(isOpen, appName).getSelectTTs());
        } else if (ApplicationConstant.UI_NAME_HISTORY.equals(uiName)) {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().switchHistoryUI(isOpen, appName).getSelectTTs());
        } else {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().openMediaAppByAppName(appName, isOpen).getSelectTTs());
        }
    }

    private IScreenStateChangeListener splitScreenIScreenStateListener = new IScreenStateChangeListener() {

        public void onScreenStateChanged(int screenId, int state) {
            LogUtils.d(TAG, "splitScreenIScreenStateListener screenId:" + screenId + " ,state:" + state);
            if (null != tempMap && state == ScreenStateHelper.STATE_ON)
                openAssignNegativeScreen(tempMap);
        }


        public boolean isPersistent() {
            return false;
        }
    };

    private IScreenStateChangeListener allAppIScreenStateListener = new IScreenStateChangeListener() {

        public void onScreenStateChanged(int screenId, int state) {
            LogUtils.d(TAG, "allAppIScreenStateListener screenId:" + screenId + " ,state:" + state);
            if (null != tempMap && state == ScreenStateHelper.STATE_ON)
                openAssignScreenAllApps(tempMap);
        }


        public boolean isPersistent() {
            return false;
        }
    };
}
