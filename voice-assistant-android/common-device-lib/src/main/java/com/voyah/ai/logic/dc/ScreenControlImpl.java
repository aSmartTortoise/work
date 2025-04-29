package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.IScreen;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.SystemSettingInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.HudSignal;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voice.sdk.device.launcher.LauncherInterface;
import com.voice.sdk.device.launcher.ResultCallBack;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;


public class ScreenControlImpl extends AbsDevices {

    private static final String TAG = ScreenControlImpl.class.getSimpleName();
    private final LauncherInterface launcher;

    private final SystemSettingInterface mSystemSettingImpl;

    public ScreenControlImpl() {
        super();
        launcher = DeviceHolder.INS().getDevices().getLauncher();
        mSystemSettingImpl = DeviceHolder.INS().getDevices().getCarService().getSystemSetting();
    }

    @Override
    public String getDomain() {
        return "screen";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "tts :" + str);
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        LogUtils.i(TAG, "key :" + key + " ,mergeScreenName(map):" + mergeScreenName(map));
        switch (key) {
            case "position":
                String pos = "";
                String screenLockPos = getOneMapValue("screen_lock", map);
                if (!TextUtils.isEmpty(screenLockPos)) {
                    pos = screenLockPos;
                } else {
                    int targetPosition = getTargetPosition(map);
                    if (targetPosition == 1) {
                        pos = "主驾";
                    } else if (targetPosition == 2) {
                        pos = "中间";
                    } else if (targetPosition == 3) {
                        pos = "副驾";
                    }
                }
                str = str.replace("@{position}", pos);
                break;
            case "number":
                str = str.replace("@{number}", ((int) getScreenOrdNum(map) + "%"));
                break;
            case "screen_name":
                str = str.replace("@{screen_name}", mergeScreenName(map));
                break;
            case "shut_off_time":
                str = str.replace("@{shut_off_time}", getShutOffTimeStr(map));
                break;
            case "screen_angle":
                String temp = getOneMapValue("screen_angle_temp", map);
                if (!TextUtils.isEmpty(temp)) {
                    str = str.replace("@{screen_angle}", (temp + "度"));
                } else {
                    str = str.replace("@{screen_angle}", (""));
                }
                break;
            case "position_scene":
                str = str.replace("@{position_scene}", mergeScreenName(map));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);
        return str;
    }

    private String mergeScreenName(HashMap<String, Object> map) {
        String str = "屏幕";
        // 因为所有屏幕操作都用同一个占位符号，需要先特殊处理下
        String ordScreen = getOneMapValue("order_screen", map);
        if (!TextUtils.isEmpty(ordScreen)) {
            switch (ordScreen) {
                case "0":
                    str = "中控屏";
                    break;
                case "1":
                    str = "副驾屏";
                    break;
                case "2":
                    str = "吸顶屏";
                    break;
                case "3":
                    str = "仪表屏";
                    break;
                case "4":
                    str = "所有屏幕";
                    break;
                default:
                    return str;
            }
            return str;
        }
        String screenName = getOneMapValue("screen_name", map);
        if (!TextUtils.isEmpty(screenName)) {
            switch (screenName) {
                case "screen":
                    str = "屏幕";
                    break;
                case "central_screen":
                    str = "中控屏";
                    break;
                case "instrument_screen":
                    str = "仪表屏";
                    break;
                case "entertainment_screen":
                    str = "副驾屏";
                    break;
                case "ceil_screen":
                    str = "吸顶屏";
                    break;
                default:
                    return str;
            }
        }
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            switch (position) {
                case "first_row_left":
                    str = "中控屏";
                    break;
                case "first_row_right":
                    str = "副驾屏";
                    break;
                case "total_car":
                    str = "所有屏幕";
                    break;
                default:
                    return str;
            }
        }
        LogUtils.d(TAG, "mergeScreenName :" + str);
        return str;
    }

    private String getShutOffTimeStr(HashMap<String, Object> map) {
        String str = "";
        int time = getOrderShutOffTime(map);
        switch (time) {
            case 0:
                str = "永不";
                break;
            case 50 * 1000:
                str = "50秒";
                break;
            case 180 * 1000:
                str = "3分钟";
                break;
            case 300 * 1000:
                str = "5分钟";
                break;
        }
        LogUtils.d(TAG, "getShutOffTimeStr :" + str);
        return str;
    }

    public boolean isSupportScreenMove(HashMap<String, Object> map) {
        return isH37ACar(map) || isH37BCar(map);
    }

    /**
     * 获取屏幕位置
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getScreenPosition(HashMap<String, Object> map) {
        int currentScreenPosition = operator.getIntProp(ScreenSignal.SCREEN_POS);
        LogUtils.d(TAG, "getScreenPosition :" + currentScreenPosition);
        return currentScreenPosition;
    }

    /**
     * 设置要调节的位置
     *
     * @param map 指令的数据
     */
    public void setScreenPosition(HashMap<String, Object> map) {
        int targetPosition = getTargetPosition(map);
        LogUtils.d(TAG, "setScreenPosition :" + targetPosition);
        operator.setIntProp(ScreenSignal.SCREEN_POS, targetPosition);
    }

    /**
     * 根据声源位置，指令位置。去获取用户希望的目标位置是哪个
     *
     * @param map 指令的数据
     * @return 目标位置
     */
    public int getTargetPosition(HashMap<String, Object> map) {
        ArrayList<Integer> currentSoundPositions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
        int currentSoundPosition = currentSoundPositions.get(0);
        int targetPosition = -1;
        String moveType = "";
        LogUtils.d(TAG, "getTargetPosition :");
        if (map.containsKey("move_type")) {
            moveType = getValueInContext(map, "move_type") + "";
            targetPosition = getTargetPositionByMoveType(currentSoundPosition, moveType);
            return targetPosition;
        }
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            targetPosition = getTargetPositionByPosition(map, position);
            return targetPosition;
        } else {
            targetPosition = getTargetPositionByNone(map);
        }
        return targetPosition;
    }

    private int getTargetPositionByMoveType(int currentSoundPosition, String moveType) {
        LogUtils.d(TAG, "getTargetPositionByMoveType :" + moveType);
        int targetPosition = -1;
        switch (moveType) {
            case "here":
                targetPosition = currentSoundPosition == 0 ? IScreen.IScreenPos.DRIVER : IScreen.IScreenPos.PASSENGER;
                break;
            case "there":
                targetPosition = currentSoundPosition == 0 ? IScreen.IScreenPos.PASSENGER : IScreen.IScreenPos.DRIVER;
                break;
            case "mid_side":
                targetPosition = IScreen.IScreenPos.MIDDLE;
                break;
            case "leftward":
                targetPosition = IScreen.IScreenPos.DRIVER;
                break;
            case "rightward":
                targetPosition = IScreen.IScreenPos.PASSENGER;
                break;
        }
        return targetPosition;
    }

    private int getTargetPositionByNone(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getTargetPositionByNone");
        int targetPosition = -1;
        int curPosition = getScreenPosition(map);
        if (getDrvStatus(map)) {
            switch (curPosition) {
                case IScreen.IScreenPos.DRIVER:
                    targetPosition = IScreen.IScreenPos.MIDDLE;
                    break;
                case IScreen.IScreenPos.MIDDLE:
                    targetPosition = IScreen.IScreenPos.PASSENGER;
                    break;
                case IScreen.IScreenPos.PASSENGER:
                    targetPosition = IScreen.IScreenPos.DRIVER;
                    break;
            }
        } else {
            switch (curPosition) {
                case IScreen.IScreenPos.DRIVER:
                    targetPosition = IScreen.IScreenPos.PASSENGER;
                    break;
                case IScreen.IScreenPos.PASSENGER:
                    targetPosition = IScreen.IScreenPos.DRIVER;
                    break;
            }
        }
        LogUtils.d(TAG, "targetPosition: " + targetPosition);
        return targetPosition;
    }


    private int getTargetPositionByPosition(HashMap<String, Object> map, String position) {
        LogUtils.d(TAG, "getTargetPositionByPosition :" + position);
        int targetPosition = -1;
        switch (position) {
            case "first_row_left":
            case "left_side":
                targetPosition = IScreen.IScreenPos.DRIVER;
                break;
            case "first_row_right":
            case "right_side":
                targetPosition = IScreen.IScreenPos.PASSENGER;
                break;
            default:
                getTargetPositionByNone(map);
        }
        return targetPosition;
    }

    /**
     * 判断屏幕是否处于移动中
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean getMoveState(HashMap<String, Object> map) {
        boolean isMoving = operator.getBooleanProp(ScreenSignal.SCREEN_MOVE_STATE);
        LogUtils.d(TAG, "getMoveState :" + isMoving);
        return isMoving;
    }

    /**
     * 判断中控屏发出阻停卡滞信号
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean getScreenAbnormalState(HashMap<String, Object> map) {
        return operator.getBooleanProp(ScreenSignal.SCREEN_ABNORMAL_STATE);
    }

    public void showScreenAbnormalDialog(HashMap<String, Object> map) {
        LogUtils.d(TAG, "showScreenAbnormalDialog");
        mSystemSettingImpl.putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "SHOW_AR_HUD_MOVE_SCREEN_HINT", 2);
    }

    /**
     * 判断是否是p挡
     *
     * @param map 指令的数据
     * @return 车辆挡位数据
     */
    public boolean getDrvStatus(HashMap<String, Object> map) {
        int drvStatus = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
        LogUtils.d(TAG, "getDrvStatus :" + drvStatus);
        return drvStatus == GearInfo.CARSET_GEAR_PARKING;
    }

    /**
     * 判断屏幕是否已在主驾，且hud关闭，且为p挡
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean getDriverPositionAndOpenHud(HashMap<String, Object> map) {
        boolean driverPos = operator.getIntProp(ScreenSignal.SCREEN_POS) == IScreen.IScreenPos.DRIVER;
        boolean hudOff = !operator.getBooleanProp(HudSignal.HUD_SWITCH);
        return driverPos && hudOff && !getDrvStatus(map);
    }


    public boolean isTargetScreen(HashMap<String, Object> map) {
        boolean isSecondaryScreen = StringUtils.equals("passenger_screen", getAssignScreen(map));
        LogUtils.d(TAG, "isTargetScreen isSecondaryScreen:" + isSecondaryScreen);
        if (!isSecondaryScreen) {
            getOrderScreen(map);
        }
        return isSecondaryScreen;
    }

    public boolean isAssignOpen(HashMap<String, Object> map) {
        String switch_type = map.containsKey("switch_type") ? (String) getValueInContext(map, "switch_type") : "";
        return StringUtils.equals(switch_type, "open");
    }


    public boolean isShowingScreenSaverOfSecondary(HashMap<String, Object> map) {
        final boolean[] isOpen = {false};
        launcher.isShowingScreenSaverOfSecondary(new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isOpen[0] = result;
                LogUtils.d(TAG, "isShowingScreenSaverOfSecondary result:" + result);
            }
        });
        return isOpen[0];
    }


    public void showScreenSaver(HashMap<String, Object> map) {
        String awakenLocation = map.containsKey("awakenLocation") ? (String) getValueInContext(map, "awakenLocation") : "";
        launcher.showHideScreenSaver(true, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "showScreenSaverSuccess result:" + result + " ,awakenLocation:" + awakenLocation);
                if (result && StringUtils.equals(awakenLocation, "first_row_right"))
                    VoiceImpl.getInstance().exDialog();
            }
        });
    }


    public void hideScreenSaver(HashMap<String, Object> map) {
        launcher.showHideScreenSaver(false, new ResultCallBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                LogUtils.d(TAG, "showScreenSaver result:" + result);
            }
        });
    }

    public boolean isAssignPassengerScreen(HashMap<String, Object> map) {
        boolean isAssignPassengerScreen = StringUtils.equals(getSoundLocation(map), LOCATION_LIST.get(1));
        return isAssignPassengerScreen;
    }

    public boolean isAssignCeilScreen(HashMap<String, Object> map) {
        String soundLocation = getSoundLocation(map);
        boolean isAssignCeilScreen = !StringUtils.equals(soundLocation, LOCATION_LIST.get(0))
                && !StringUtils.equals(soundLocation, LOCATION_LIST.get(1));
        return isAssignCeilScreen;
    }

    public boolean isSupportExecuteScreenSaver(HashMap<String, Object> map){
        return DeviceHolder.INS().getDevices().getLauncher().isSupportExecuteScreenSaver();
    }

    public boolean isSupportScreenSaver(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportScreenSaver();
    }

    public boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map) {
        return isBaseCeilScreen(map) && !isBaseHaveCeilScreen();
    }

    public void showHudDialog(HashMap<String, Object> map) {
        LogUtils.d(TAG, "showHudDialog");
        mSystemSettingImpl.putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "show_ar_hud_move_screen_hint", 1);
    }

    /** ----------------------- 显示 start------------------------------------------------------*/

    /**
     * 是否是LCD屏幕
     * 针对H37A项目,软件读配置字MegaProperties.CONFIG_CSSD_EQUIPMENT
     * 如果这个值为7[0111],则代表为OLED屏幕;
     * 如果这个值为9[1001],则代表为LCD屏幕
     *
     * @param map
     * @return
     */
    public boolean isLcdScreen(HashMap<String, Object> map) {
        int screenType = operator.getIntProp(ScreenSignal.SCREEN_TYPE, 9);
        LogUtils.d(TAG, "isLcdScreen: " + screenType);
        return screenType == 9 || !isH37ACar(map);
    }

    /**
     * 判断是否操作不可用的屏幕，37没有副驾屏
     *
     * @param map
     * @return
     */
    public boolean isDealCeilScreen(HashMap<String, Object> map) {
        int orderScreen = 0;
        String tabName = getOneMapValue("tab_name", map);
        if (!TextUtils.isEmpty(tabName) && tabName.equals("brightness_auto")) {
            orderScreen = getOrderAutoScreen(map);
        } else {
            orderScreen = getOrderScreen(map);
        }
        LogUtils.d(TAG, "isDealCeilScreen: " + orderScreen);
        if (isH37ACar(map) || isH37BCar(map)) {
            return orderScreen == 1 || orderScreen == 2;
        }
        return orderScreen == 2;
    }

    public boolean isOpenParkingApp(HashMap<String, Object> map) {
        int status = operator.getIntProp(CommonSignal.COMMON_360_STATE);
        LogUtils.d(TAG, "isOpenParkingApp: " + status);
        return status != 0;
    }

    /**
     * 屏幕亮度自动调节开关，当前产品搁置，暂无该需求
     *
     * @param map 上下文数据
     * @return true/false
     */
    public boolean isOrdAutoBrightnessState(HashMap<String, Object> map) {
        int screenType = getOrderAutoScreen(map);
        int centerScreen = operator.getIntProp(ScreenSignal.SCREEN_CENTER_AUTO);
        int rightScreen = operator.getIntProp(ScreenSignal.SCREEN_PASSENGER_AUTO);
        int instrumentScreen = operator.getIntProp(ScreenSignal.SCREEN_INSTRUMENT_AUTO);
        int ordType = 0;
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            ordType = switchType.equals("open") ? 1 : 0;
        }
        switch (screenType) {
            case 0:
                return centerScreen == ordType;
            case 1:
                return rightScreen == ordType;
            case 3:
                return instrumentScreen == ordType;
            case 4:
                if (isH37ACar(map) || isH37BCar(map)) {
                    return centerScreen == ordType && instrumentScreen == ordType;
                }
                return centerScreen == ordType && rightScreen == ordType && instrumentScreen == ordType;
            default:
                break;
        }
        return true;
    }

    /**
     * 屏幕亮度自动调节开关，当前产品搁置，暂无该需求
     *
     * @param map 上下文数据
     */
    public void setBrightnessSwitchState(HashMap<String, Object> map) {
        int screenType = getOrderAutoScreen(map);
        int ordType = 0;
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            ordType = switchType.equals("open") ? 1 : 0;
        }
        switch (screenType) {
            case 0:
                operator.setIntProp(ScreenSignal.SCREEN_CENTER_AUTO, ordType);
                break;
            case 1:
                operator.setIntProp(ScreenSignal.SCREEN_PASSENGER_AUTO, ordType);
                break;
            case 3:
                operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_AUTO, ordType);
                break;
            case 4:
                if (isH37ACar(map) || isH37BCar(map)) {
                    operator.setIntProp(ScreenSignal.SCREEN_CENTER_AUTO, ordType);
                    operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_AUTO, ordType);
                } else {
                    operator.setIntProp(ScreenSignal.SCREEN_CENTER_AUTO, ordType);
                    operator.setIntProp(ScreenSignal.SCREEN_PASSENGER_AUTO, ordType);
                    operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_AUTO, ordType);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 屏幕自动亮度，只支持仪表屏，主副驾
     *
     * @param map
     * @return
     */
    private int getOrderAutoScreen(HashMap<String, Object> map) {
        // 传递displayId 0,1,2,3,4对应中控，副驾(副驾娱乐屏)，吸顶屏(二排娱乐屏)，仪表，全部屏幕
        int screenType = 0;
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            switch (position) {
                case "first_row_left":
                    screenType = 0;
                    break;
                case "first_row_right":
                    screenType = 1;
                    break;
                case "total_car":
                    screenType = 4;
                    break;
                default:
                    screenType = 1;
                    break;
            }
        } else {
            String screenName = getOneMapValue("screen_name", map);
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if (!TextUtils.isEmpty(screenName)) {
                switch (screenName) {
                    case "central_screen":
                        screenType = 0;
                        break;
                    case "passenger_screen":
                    case "entertainment_screen":
                        screenType = 1;
                        break;
                    case "screen":
                        switch (curPosition) {
                            case 0:
                                screenType = 0;
                                break;
                            case 1:
                                screenType = 1;
                                break;
                            default:
                                screenType = 0;
                                break;
                        }
                        break;
                    case "instrument_screen":
                        screenType = 3;
                        break;
                    default:
                        screenType = 0;
                        break;
                }
            } else {
                switch (curPosition) {
                    case 0:
                        break;
                    case 1:
                        screenType = 1;
                        break;
                    default:
                        screenType = 0;
                        break;
                }
            }
        }
        if (isH37ACar(map) || isH37BCar(map)) {
            // 自动亮度，37没有副驾，给到中控
            screenType = screenType == 1 ? 0 : screenType;
        }
        map.put("order_screen", screenType + "");
        LogUtils.d(TAG, "getOrderAutoScreen: " + screenType);
        return screenType;
    }

    /**
     * 已和产品对齐，需求未下发，暂不判断中控屏屏温度是否超出
     *
     * @param map 上下文数据
     * @return true/false
     */
    public boolean getCentralScreenTemp(HashMap<String, Object> map) {
        // 已和产品对齐，需求未下发，暂不判断中控屏屏温度是否超出
        return false;
    }

    /**
     * 获取操作屏幕亮度(不带具体屏幕信息，或者副驾娱乐屏，指向这里)
     *
     * @param map 上下文数据
     * @return 中控屏/副驾屏/吸顶屏亮度
     */
    public float getCentralScreenBrightness(HashMap<String, Object> map) {
        int state = 0;
        int centralScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_BRIGHTNESS);
        int passengerScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS);
        int ceilingScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_CEILING_BRIGHTNESS);
        int instrumentScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS);
        if (getOrderScreen(map) == 0) {
            state = centralScreenBrightness;
        } else if (getOrderScreen(map) == 1) {
            state = passengerScreenBrightness;
        } else if (getOrderScreen(map) == 2) {
            state = ceilingScreenBrightness;
        } else if (getOrderScreen(map) == 4) {
            // 所有屏幕，这里需要带上仪表屏
            if (isH37ACar(map) || isH37BCar(map)) {
                if (centralScreenBrightness == 100 && instrumentScreenBrightness == 100) {
                    state = 100;
                }
                if (centralScreenBrightness == 1 && instrumentScreenBrightness == 1) {
                    state = 1;
                }
                String adjustType = "";
                String level = "";
                float number = getScreenOrdNum(map);
                if (map.containsKey("adjust_type")) {
                    adjustType = (String) getValueInContext(map, "adjust_type");
                    LogUtils.d(TAG, "adjustType: " + adjustType);
                }
                if (map.containsKey("level")) {
                    level = (String) getValueInContext(map, "level");
                    LogUtils.d(TAG, "level: " + level);
                }
                if (adjustType.equals("increase") && number != 0) {
                    state = Math.min(centralScreenBrightness, instrumentScreenBrightness);
                }
                if (adjustType.equals("decrease") && number != 0) {
                    state = Math.max(centralScreenBrightness, instrumentScreenBrightness);
                }
                LogUtils.d(TAG, "getCentralScreenBrightness: " + state);
                return state;
            }
            if (centralScreenBrightness == 100 && passengerScreenBrightness == 100
                    && ceilingScreenBrightness == 100 && instrumentScreenBrightness == 100) {
                state = 100;
            }
            if (centralScreenBrightness == 1 && passengerScreenBrightness == 1
                    && ceilingScreenBrightness == 1 && instrumentScreenBrightness == 1) {
                state = 1;
            }
            String adjustType = "";
            String level = "";
            float number = getScreenOrdNum(map);
            if (map.containsKey("adjust_type")) {
                adjustType = (String) getValueInContext(map, "adjust_type");
                LogUtils.d(TAG, "adjustType: " + adjustType);
            }
            if (map.containsKey("level")) {
                level = (String) getValueInContext(map, "level");
                LogUtils.d(TAG, "level: " + level);
            }
            if (adjustType.equals("increase") && number != 0) {
                state = Math.min(centralScreenBrightness, Math.min(passengerScreenBrightness, Math.min(ceilingScreenBrightness, instrumentScreenBrightness)));
            }
            if (adjustType.equals("decrease") && number != 0) {
                state = Math.max(centralScreenBrightness, Math.max(passengerScreenBrightness, Math.max(ceilingScreenBrightness, instrumentScreenBrightness)));
            }
        }
        LogUtils.d(TAG, "getCentralScreenBrightness: " + state);
        return state;
    }

    /**
     * 设置中控屏亮度
     *
     * @param map 上下文数据
     */
    public void setCentralScreenBrightness(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setCentralScreenBrightness");
        String adjustType = "";
        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        float number = getScreenOrdNum(map);
        int newVal = 0;
        int newVal0 = 0;
        int newVal1 = 0;
        int newVal2 = 0;
        int newVal3 = 0;
        int curVal = (int) getCentralScreenBrightness(map);
        int centralScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_BRIGHTNESS);
        int passengerScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS);
        int ceilingScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_CEILING_BRIGHTNESS);
        int instrumentScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS);
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = 1;
            newVal0 = 1;
            newVal1 = 1;
            newVal2 = 1;
            newVal3 = 1;
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = 100;
            newVal0 = 100;
            newVal1 = 100;
            newVal2 = 100;
            newVal3 = 100;
        } else if (adjustType.equals("increase") && number != 0) {
            newVal = Math.min((int) number + curVal, 100);
            newVal0 = Math.min((int) number + centralScreenBrightness, 100);
            newVal1 = Math.min((int) number + passengerScreenBrightness, 100);
            newVal2 = Math.min((int) number + ceilingScreenBrightness, 100);
            newVal3 = Math.min((int) number + instrumentScreenBrightness, 100);
        } else if (adjustType.equals("increase")) {
            newVal = Math.min(curVal + 10, 100);
            newVal0 = Math.min(centralScreenBrightness + 10, 100);
            newVal1 = Math.min(passengerScreenBrightness + 10, 100);
            newVal2 = Math.min(ceilingScreenBrightness + 10, 100);
            newVal3 = Math.min(instrumentScreenBrightness + 10, 100);
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal = Math.max(curVal - (int) number, 1);
            newVal0 = Math.max(centralScreenBrightness - (int) number, 1);
            newVal1 = Math.max(passengerScreenBrightness - (int) number, 1);
            newVal2 = Math.max(ceilingScreenBrightness - (int) number, 1);
            newVal3 = Math.max(instrumentScreenBrightness - (int) number, 1);
        } else if (adjustType.equals("decrease")) {
            newVal = Math.max(curVal - 10, 1);
            newVal0 = Math.max(centralScreenBrightness - 10, 1);
            newVal1 = Math.max(passengerScreenBrightness - 10, 1);
            newVal2 = Math.max(ceilingScreenBrightness - 10, 1);
            newVal3 = Math.max(instrumentScreenBrightness - 10, 1);
        } else {
            newVal = Math.min(Math.max((int) number, 1), 100);
            newVal0 = Math.min(Math.max((int) number, 1), 100);
            newVal1 = Math.min(Math.max((int) number, 1), 100);
            newVal2 = Math.min(Math.max((int) number, 1), 100);
            newVal3 = Math.min(Math.max((int) number, 1), 100);
        }
        LogUtils.d(TAG, "setCentralScreenBrightness: newVal0: " + newVal0 + "newVal1" + newVal1 + "newVal2 " + newVal2 + "newVal3 " + newVal3);
        if (getOrderScreen(map) == 0) {
            operator.setIntProp(ScreenSignal.SCREEN_BRIGHTNESS, newVal);
        } else if (getOrderScreen(map) == 1) {
            operator.setIntProp(ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS, newVal);
        } else if (getOrderScreen(map) == 2) {
            operator.setIntProp(ScreenSignal.SCREEN_CEILING_BRIGHTNESS, newVal);
        } else if (getOrderScreen(map) == 4) {
            if (isH37ACar(map) || isH37BCar(map)) {
                operator.setIntProp(ScreenSignal.SCREEN_BRIGHTNESS, newVal0);
                operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS, newVal3);
            } else {
                operator.setIntProp(ScreenSignal.SCREEN_BRIGHTNESS, newVal0);
                operator.setIntProp(ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS, newVal1);
                operator.setIntProp(ScreenSignal.SCREEN_CEILING_BRIGHTNESS, newVal2);
                operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS, newVal3);
            }
        }
    }

    public boolean getInstrumentScreenTemp(HashMap<String, Object> map) {
        // 已和产品对齐，需求未下发，暂不判断仪表屏温度是否超出
        return false;
    }

    /**
     * 获取当前仪表屏亮度
     *
     * @param map 上下文数据
     * @return 仪表屏亮度
     */
    public float getInstrumentScreenBrightness(HashMap<String, Object> map) {
        int instrumentScreenBrightness = operator.getIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS);
        LogUtils.d(TAG, "getInstrumentScreenBrightness: " + instrumentScreenBrightness);
        return instrumentScreenBrightness;
    }

    /**
     * 获取当前仪表屏亮度
     *
     * @param map 上下文数据
     */
    public void setInstrumentScreenBrightness(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setInstrumentScreenBrightness");
        String adjustType = "";

        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        float number = getScreenOrdNum(map);
        int newVal = 0;
        int curVal = (int) getInstrumentScreenBrightness(map);
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = 1;
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = 100;
        } else if (adjustType.equals("increase") && number != 0) {
            newVal = Math.min((int) number + curVal, 100);
        } else if (adjustType.equals("increase")) {
            newVal = Math.min(curVal + 10, 100);
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal = Math.max(curVal - (int) number, 1);
        } else if (adjustType.equals("decrease")) {
            newVal = Math.max(curVal - 10, 1);
        } else {
            newVal = Math.min(Math.max((int) number, 1), 100);
        }
        operator.setIntProp(ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS, newVal);
    }

    /**
     * 获取语音指令想要调节的亮度数值
     *
     * @param map 上下文数据
     * @return 语音指令想要调节的亮度数值
     */
    public float getScreenOrdNum(HashMap<String, Object> map) {
        float number = 0.0f;
        if (map.containsKey("number")) {
            String numberStr = (String) getValueInContext(map, "number");
            if (numberStr.contains("%")) {
                numberStr = numberStr.replace("%", "");
                BigDecimal bd = new BigDecimal(numberStr);
                number = bd.setScale(0, RoundingMode.CEILING).intValue();
            } else if (numberStr.contains("/")) {
                String[] parts = numberStr.split("/");
                if (parts.length == 2) {
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    number = ((float) numerator / denominator) * 100;
                }
            } else {
                BigDecimal bd = new BigDecimal(numberStr);
                number = bd.setScale(0, RoundingMode.CEILING).intValue();
            }
            LogUtils.d(TAG, "number: " + number);
        }
        return number;
    }

    /** ----------------------- 显示 end------------------------------------------------------*/


    /**
     * ----------------------- 下电复位 start------------------------------------------------------
     */

    //产品改成兜底了
    public boolean getPowerOffResetStateSwitch(HashMap<String, Object> map) {
        return operator.getBooleanProp(ScreenSignal.SCREEN_POWER_OFF_RESET);
    }

    public void setPowerOffResetStateSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setPowerOffResetStateSwitch");
        boolean status = false;
        if (map.containsKey("switch_type")) {
            String switchType = (String) getValueInContext(map, "switch_type");
            if (switchType.equals("open")) {
                status = true;
            }
        }
        operator.setBooleanProp(ScreenSignal.SCREEN_POWER_OFF_RESET, status);
    }

    /** ----------------------- 下电复位 end------------------------------------------------------*/

    /**
     * ----------------------- 屏幕亮屏/熄屏 start------------------------------------------------------
     */

    public int getScreenSwitchState(HashMap<String, Object> map) {
        int status = operator.getIntProp(ScreenSignal.SCREEN_ON_OFF, getOrderScreen(map));
        int screen0 = operator.getIntProp(ScreenSignal.SCREEN_ON_OFF, 0);
        int screen1 = operator.getIntProp(ScreenSignal.SCREEN_ON_OFF, 1);
        if ((isH56CCar(map) || isH56DCar(map)) && getOrderScreen(map) == 4) {
            if (screen0 == 1 && screen1 == 1) {
                status = 1;
            } else {
                status = 0;
            }
        }
        LogUtils.d(TAG, "getScreenSwitchState : " + status);
        return status;
    }

    public void setScreenSwitchState(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            LogUtils.d(TAG, "setScreenSwitchState: " + switchType);
            if (switchType.equals("open")) {
                if (getOrderScreen(map) == 4) {
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, 0, ICommon.Switch.ON);
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, 1, ICommon.Switch.ON);
                } else {
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, getOrderScreen(map), ICommon.Switch.ON);
                }
            } else {
                if (getOrderScreen(map) == 4) {
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, 0, ICommon.Switch.OFF);
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, 1, ICommon.Switch.OFF);
                } else {
                    operator.setIntProp(ScreenSignal.SCREEN_ON_OFF, getOrderScreen(map), ICommon.Switch.OFF);
                }
            }
        }
    }

    public int getOrderScreen(HashMap<String, Object> map) {
        // 传递displayId 0,1,2,4对应主驾，副驾(副驾娱乐屏)，吸顶屏(二排娱乐屏),全车
        int displayId = 0;
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            switch (position) {
                case "first_row_left":
                    displayId = 0;
                    break;
                case "first_row_right":
                    displayId = 1;
                    break;
                case "rear_side":
                    displayId = 2;
                    break;
                case "total_car":
                    displayId = 4;
                    break;
            }
            if (position.contains("second")) {
                displayId = 2;
            }
        } else {
            String screenName = getOneMapValue("screen_name", map);
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if (!TextUtils.isEmpty(screenName)) {
                switch (screenName) {
                    case "central_screen":
                        displayId = 0;
                        break;
                    case "passenger_screen":
                        displayId = 1;
                        break;
                    case "ceil_screen":
                        displayId = 2;
                        break;
                    case "entertainment_screen":
                        switch (curPosition) {
                            case 0:
                            case 1:
                                displayId = 1;
                                break;
                            default:
                                displayId = operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG) ? 2 : 1;
                                break;
                        }
                        break;
                    case "screen":
                        switch (curPosition) {
                            case 0:
                                displayId = 0;
                                break;
                            case 1:
                                displayId = 1;
                                break;
                            default:
                                displayId = operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG) ? 2 : 0;
                                break;
                        }
                        break;
                }
            } else {
                switch (curPosition) {
                    case 0:
                        displayId = 0;
                        break;
                    case 1:
                        displayId = 1;
                        break;
                    default:
                        displayId = operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG) ? 2 : 0;
                        break;
                }
            }
        }
        if (isH37ACar(map) || isH37BCar(map)) {
            // 37没有副驾和吸顶屏，给到中控
            displayId = displayId == 1 ? 0 : displayId;
        }
        map.put("order_screen", displayId + "");
        LogUtils.d(TAG, "getScreenSwitchState displayId: " + displayId);
        return displayId;
    }

    /** ----------------------- 屏幕亮屏/熄屏 end------------------------------------------------------*/

    /** ----------------------- 屏幕清洁模式 start------------------------------------------------------*/

    /**
     * 判断屏幕清洁模式是否打开，0 退出，1 开启
     *
     * @param map
     * @return
     */
    public boolean isScreenCleanMode(HashMap<String, Object> map) {
        boolean status = operator.getBooleanProp(ScreenSignal.SCREEN_CLEAN_MODE_SWITCH);
        LogUtils.d(TAG, "isScreenCleanMode: " + status);
        return status;
    }

    public void setScreenCleanMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setScreenCleanMode");
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            mSettingHelper.exec(str.equals("open") ? SettingConstants.CLEAN_MODE : SettingConstants.EXIT_CLEAN_MODE);
        }

    }

    /** ----------------------- 屏幕清洁模式 end------------------------------------------------------*/

    /**
     * ----------------------- 吸顶屏调节 start------------------------------------------------------
     */

    public boolean isSupportCeilScreen(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
    }

    public void setCeilScreenState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            if ("open".equals(str)) {
                int curTemp = -1;
                curTemp = mSystemSettingImpl.getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "ceiling_screen_angle", 0);
                ;
                LogUtils.d(TAG, "ceiling_screen_angle  curTemp:" + curTemp);
                if (curTemp < 90) {
                    curTemp = 105;
                }
                operator.setIntProp(ScreenSignal.SCREEN_CEILING_ANGLE, curTemp);
            } else {
                operator.setIntProp(ScreenSignal.SCREEN_CEILING_ANGLE, 0);
            }
        }
    }

    public int getCurCeilScreenLevel(HashMap<String, Object> map) {
        int curTemp = operator.getIntProp(ScreenSignal.SCREEN_CEILING_ANGLE);
        LogUtils.d(TAG, "getCeilScreenLevel: " + curTemp);
        return curTemp;
    }

    public void setCurCeilScreenLevel(HashMap<String, Object> map) {
        String adjustType = "";
        int number = 0;
        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        number = getCeilScreenOrdNum(map);
        int newVal = getCurCeilScreenLevel(map);
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = 90;
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = 115;
        } else if (adjustType.equals("increase") && number != 0) {
            newVal += number;
        } else if (adjustType.equals("increase")) {
            newVal += 5;
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal -= number;
        } else if (adjustType.equals("decrease")) {
            newVal -= 5;
        } else {
            newVal = number;
        }
        LogUtils.d(TAG, "newVal: " + newVal);
        operator.setIntProp(ScreenSignal.SCREEN_CEILING_ANGLE, newVal);
    }

    public int getCeilScreenOrdNum(HashMap<String, Object> map) {
        String temp = getOneMapValue("number_temp", map);
        int ordTemp = 0;
        if (!TextUtils.isEmpty(temp)) {
            ordTemp = Integer.parseInt(temp);
        }
        String number = getOneMapValue("number", map);
        if (!TextUtils.isEmpty(number)) {
            ordTemp = Integer.parseInt(number);
        }
        map.put("screen_angle_temp", String.valueOf(ordTemp));
        LogUtils.d(TAG, "getCeilScreenOrdNum: " + ordTemp);
        return ordTemp;
    }

    public boolean isValidNum(HashMap<String, Object> map) {
        return getCeilScreenOrdNum(map) < 116 && getCeilScreenOrdNum(map) > 89;
    }

    public boolean isSupportScreenLock(HashMap<String, Object> map) {
        int type = operator.getIntProp(ScreenSignal.SCREEN_LOCK_CONFIG, -1);
        LogUtils.d(TAG, "isSupportScreenLock: " + type);
        boolean isValidPosition = true;
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            if (!position.equals("left_side") && !position.equals("right_side")) {
                isValidPosition = false;
            }
        }
        return type == 3 && isValidPosition;
    }

    public boolean isHasScreenLockPosition(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        return !TextUtils.isEmpty(position);
    }

    public boolean isScreenLock(HashMap<String, Object> map) {
        // 左右同时上锁，开关才会打开
        int rlState = operator.getIntProp(ScreenSignal.SCREEN_LOCK_RL);
        int rrState = operator.getIntProp(ScreenSignal.SCREEN_LOCK_RR);
        LogUtils.d(TAG, "rlState: " + rlState);
        LogUtils.d(TAG, "rrState: " + rrState);
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            if (position.equals("left_side")) {
                map.put("screen_lock", "左侧");
                return rlState == 1;
            } else if (position.equals("right_side")){
                map.put("screen_lock", "右侧");
                return rlState == 1;
            }
        }
        return rlState == 1 && rrState == 1;
    }

    public void setScreenLock(HashMap<String, Object> map) {
        if (map.containsKey("switch_type")) {
            String switchType = (String) getValueInContext(map, "switch_type");
            String position = getOneMapValue("positions", map);
            if (!TextUtils.isEmpty(position)) {
                if (position.equals("left_side")) {
                    operator.setIntProp(ScreenSignal.SCREEN_LOCK_RL, switchType.equals("open") ? 1 : 2);
                    return;
                } else if (position.equals("right_side")){
                    operator.setIntProp(ScreenSignal.SCREEN_LOCK_RR, switchType.equals("open") ? 1 : 2);
                    return;
                }
            }
            operator.setIntProp(ScreenSignal.SCREEN_LOCK_STATE, switchType.equals("open") ? 1 : 2);
        }
    }

    public boolean isSupportTime(HashMap<String, Object> map) {
        int time = 0;
        String ordStr = getOneMapValue("duration", map);
        if (!TextUtils.isEmpty(ordStr)) {
            time = Integer.parseInt(ordStr);
        }
        LogUtils.d(TAG, "isSupportTime: " + time);
        return time == 50 || time == 180 || time == 300 || time == 60000;
    }

    public int getCurShutOffTime(HashMap<String, Object> map) {
        int time = mSystemSettingImpl.getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "SCREEN_OFF_TIMEOUT", 0);
        LogUtils.d(TAG, "getCurShutOffTime: " + time);
        return time;
    }

    public int getOrderShutOffTime(HashMap<String, Object> map) {
        int time = -1;
        String ordStr = getOneMapValue("duration", map);
        if (!TextUtils.isEmpty(ordStr)) {
            time = Integer.parseInt(ordStr);
        }
        if (time == 60000) {
            time = 0;
        }
        LogUtils.d(TAG, "getOrderShutOffTime: " + time);
        return time * 1000;
    }

    public void setShutOffTime(HashMap<String, Object> map) {
        mSystemSettingImpl.putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "SCREEN_OFF_TIMEOUT", getOrderShutOffTime(map));
    }

    public boolean isSupportCeilScreenLock(HashMap<String, Object> map) {
        return isH56DCar(map);
    }

    public boolean isCeilScreenLock(HashMap<String, Object> map) {
        return operator.getIntProp(ScreenSignal.SCREEN_CEIL_LOCK) == 1;
    }

    public void setCeilScreenLock(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            operator.setIntProp(ScreenSignal.SCREEN_CEIL_LOCK, switchType.equals("open") ? 1 : 0);
        }
    }
}
