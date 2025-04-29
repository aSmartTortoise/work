package com.voyah.ai.logic.dc;

import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.STEP_VOLUME;
import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.STREAM_BLUETOOTH;
import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.STREAM_VOICE_CALL;
import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.VOLUME_MAX;
import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.VOLUME_MIN;
import static com.voice.sdk.device.carservice.constants.ISysSetting.IVolume.VOLUME_VOL_MIN;

import android.bluetooth.BluetoothClass;
import android.os.RemoteException;
import android.text.TextUtils;

import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.constant.SystemUiConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.SystemControlInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SystemSettingControlImpl extends AbsDevices {

    private static final String TAG = SystemSettingControlImpl.class.getSimpleName();

    private static final String THEME_MODE = "theme_mode";
    private final SystemControlInterface systemControlInterface;

    public SystemSettingControlImpl() {
        super();
        operator.setBooleanProp(SysSettingSignal.SYS_INIT_VOLUME, true);
        systemControlInterface = DeviceHolder.INS().getDevices().getCarService().getSystemControlInterface();
    }

    @Override
    public String getDomain() {
        return "SystemSetting";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "page_name":
                str = str.replace("@{page_name}", getPageName(map));
                break;
            case "tab_name":
                str = str.replace("@{tab_name}", getTabName(map));
                break;
            case "time_mode":
            case "hours_systems":
                String time_mode = getSetTimeType(map);
                if (key.equals("time_mode")) {
                    str = str.replace("@{time_mode}", time_mode);
                } else {
                    str = str.replace("@{hours_systems}", time_mode);
                }
                break;
            case "language":
                String language = (String) getValueInContext(map, "language");
                str = str.replace("@{language}", language);
                break;
            case "module_name":
                if (map.containsKey("module_name")) {
                    String module_name = (String) getValueInContext(map, "module_name");
                    str = str.replace("@{module_name}", getVolumeName().get(module_name));
                } else {
                    str = str.replace("@{module_name}", "");
                }
                break;
            case "volume":
                if (map.containsKey("module_name")) {
                    String module_name = (String) getValueInContext(map, "module_name");
                    str = str.replace("@{volume}", getVolumeName().get(module_name));
                } else {
                    str = str.replace("@{volume}", "");
                }
                break;
            case "number":
                String number = String.valueOf(getSetNumber(map));
                str = str.replace("@{number}", number);
                break;
            case "open_tab":
                str = str.replace("@{open_tab}", getTabName(map));
                break;
            case "switch_mode":
            case "driver_assistance_broadcast":
            case "sound_wave_mode":
            case "headrest_sound_mode":
            case "sound_mode":
            case "sound_tone":
            case "allert_tonet":
                String switch_mode = (String) getValueInContext(map, "switch_mode");
                if (key.equals("switch_mode")) {
                    str = str.replace("@{switch_mode}", getSwitchModeName().get(switch_mode));
                } else if (key.equals("driver_assistance_broadcast")) {
                    str = str.replace("@{driver_assistance_broadcast}", getSwitchModeName().get(switch_mode));
                } else if (key.equals("sound_wave_mode")) {
                    str = str.replace("@{sound_wave_mode}", getSwitchModeName().get(switch_mode));
                } else if (key.equals("headrest_sound_mode")) {
                    str = str.replace("@{headrest_sound_mode}", getSwitchModeName().get(switch_mode));
                } else if (key.equals("sound_mode")) {
                    str = str.replace("@{sound_mode}", getCurSoundEffectsName().get(switch_mode));
                } else if (key.equals("sound_tone")) {
                    str = str.replace("@{sound_tone}", systemControlInterface.getTtsText(switch_mode));
                } else if (key.equals("allert_tonet")) {
                    str = str.replace("@{allert_tonet}", getLowSpeedPedesWarningName().get(switch_mode));
                }
                break;
            case "position":
            case "sound_wave_position":
                String position = (String) getValueInContext(map, "positions");
                if (key.equals("sound_wave_position")) {
                    str = str.replace("@{sound_wave_position}", getPositionSideName().get(position));
                } else {
                    str = str.replace("@{position}", getPositionSideName().get(position));
                }
                break;
            case "mode":
            case "theme_color":
                String mode = getOneMapValue(THEME_MODE, map);
                if (key.equals("mode")) {
                    str = str.replace("@{mode}", getThemeModeName().get(mode));
                } else {
                    str = str.replace("@{theme_color}", getThemeModeName().get(mode));
                }
                break;
            case "app_name":
                str = str.replace("@{app_name}", "设置应用");
                break;
            case "Instrument_mode":
                String level = (String) getValueInContext(map, "level");
                if (level.equals("low")) {
                    str = str.replace("@{Instrument_mode}", "低");
                } else if (level.equals("mid")) {
                    str = str.replace("@{Instrument_mode}", "中");
                } else if (level.equals("high")) {
                    str = str.replace("@{Instrument_mode}", "高");
                }
                break;
            case "charge_seat":
                str = str.replace("@{charge_seat}", mergePositionToString(map));
                break;
            case "sound_position":
                if (map.containsKey("switch_mode")) {
                    mode = (String) getValueInContext(map, "switch_mode");
                } else {
                    mode = (String) map.get("positions");
                }
                str = str.replace("@{sound_position}", getVolumeFocusName().get(mode));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.i(TAG, "tts :" + str);
        return str;
    }

    private String mergePositionToString(HashMap<String, Object> map) {
        switch (dealPosition(map)) {
            case PositionSignal.FIRST_ROW_LEFT:
                return "左前";
            case PositionSignal.FIRST_ROW_RIGHT:
                return "右前";
            case PositionSignal.FIRST_ROW:
                return "前排";
            case PositionSignal.REAR_ROW:
                return "后排";
            case PositionSignal.SECOND_ROW_RIGHT:
                return "右后";
            case PositionSignal.SECOND_ROW_LEFT:
                return "左后";
            case PositionSignal.LEFT_COL:
                return "左侧";
            case PositionSignal.RIGHT_COL:
                return "右侧";
            case PositionSignal.ALL:
                return "全部";
        }
        return "全部";
    }

    /**
     * SwitchMode模式名称
     */
    public Map<String, String> getSwitchModeName() {
        
        return new HashMap<String, String>() {
            {
                put("tradition", "传统");
                        put("technology", "科技");
                        put("brief", "简洁");
                        put("detail", "详细");
                        put("drive_concentration", "主驾专注");
                        put("drive_private", "主驾私享");
            }
        };
    }

    public Map<String, String> getThemeModeName() {
        return new HashMap<String, String>() {
            {
                put("0", "自动");
                put("1", "浅色");
                put("2", "深色");
            }
        };
    }

    public boolean isSupportCeilScreen(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
    }

    public int getCurCeilScreenLevel(HashMap<String, Object> map) {
        int curTemp = operator.getIntProp(CommonSignal.COMMON_CEIL_ANGLE);
        LogUtils.d(TAG, "getCeilScreenLevel: " + curTemp);
        return curTemp;
    }

    private boolean isSupportDealCeilScreen(HashMap<String, Object> map) {
        return isSupportCeilScreen(map) && getCurCeilScreenLevel(map) > 0;
    }

    public boolean isDevicePageOpen(HashMap<String, Object> map) {
        String deviceName = getOneMapValue("device_name", map);
        String tabName = getOneMapValue("tab_name", map);
        String pageName = getOneMapValue("page_name", map);
        String switchType = getOneMapValue("switch_type", map);
        int disPlayId =  getOpenPageOrderScreen(map);
        if (!TextUtils.isEmpty(switchType)) {
            // 关闭弹窗，其他屏不用判断弹窗是否已打开，默认就是打开
            if (switchType.equals("close")) {
                if (disPlayId == 0) {
                    return operator.getBooleanProp(CommonSignal.COMMON_SETTING_STATE);
                } else  {
                    return true;

                }
            }
        }
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(pageName)) {
            // 打开弹窗，其他屏不用判断弹窗是否已打开，默认就是未打开
            if (disPlayId == 0) {
                switch (deviceName) {
                    case "bluetooth":
                        return mSettingHelper.isCurrentState(SettingConstants.BLUETOOTH);
                    case "hotspot":
                        return mSettingHelper.isCurrentState(SettingConstants.HOTSPOT_PAGE);
                    case "wifi":
                        return mSettingHelper.isCurrentState(SettingConstants.WLAN_PAGE);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void setDevicePage(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        String deviceName = getOneMapValue("device_name", map);
        String tabName = getOneMapValue("tab_name", map);
        int passengerDisplayId =  DeviceHolder.INS().getDevices().getSystem().getScreen().getPassengerScreenDisplayId();
        int ceilDisplayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getCeilingScreenDisplayId();
        int disPlayId =  getOpenPageOrderScreen(map);
        if (map.containsKey("switch_type")) {
            if (switchType.equals("close")) {
                if (disPlayId == 0) {
                DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(ApplicationConstant.PKG_SETTINGS, DeviceScreenType.CENTRAL_SCREEN);
                }  else if (disPlayId == 1) {
                    operator.setIntProp(CommonSignal.COMMON_DISMISS_DIALOG, passengerDisplayId);
                } else if (disPlayId == 2) {
                    switch (deviceName) {
                        case "bluetooth":
                        case "hotspot":
                        case "wifi":
                            operator.setIntProp(CommonSignal.COMMON_DEAL_BT, SystemUiConstant.CEILING.QS_PANEL_BT_DIALOG.HIDE);
                            operator.setIntProp(CommonSignal.COMMON_DISMISS_DIALOG, ceilDisplayId);
                            break;
                    }
                }
                return;
            }
        }

        if (!TextUtils.isEmpty(deviceName)) {
            if (disPlayId == 0) {
                switch (deviceName) {
                    case "bluetooth":
                        mSettingHelper.exec(SettingConstants.BLUETOOTH);
                        break;
                    case "hotspot":
                        mSettingHelper.exec(SettingConstants.HOTSPOT_PAGE);
                        break;
                    case "wifi":
                        mSettingHelper.exec(SettingConstants.WLAN_PAGE);
                        break;
                }
            } else if (disPlayId == 1) {
                DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.PASSENGER_SCREEN, "");
                switch (deviceName) {
                    case "bluetooth":
                        operator.setIntProp(CommonSignal.COMMON_OPEN_BT, passengerDisplayId);
                        break;
                    case "hotspot":
                        operator.setIntProp(CommonSignal.COMMON_OPEN_AP, passengerDisplayId);
                        break;
                    case "wifi":
                        operator.setIntProp(CommonSignal.COMMON_OPEN_WIFI, passengerDisplayId);
                        break;
                }
            } else if (disPlayId == 2) {
                DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.CEIL_SCREEN, "");
                switch (deviceName) {
                    case "bluetooth":
                        operator.setIntProp(CommonSignal.COMMON_DISMISS_DIALOG, ceilDisplayId);
                        operator.setIntProp(CommonSignal.COMMON_DEAL_BT, SystemUiConstant.CEILING.QS_PANEL_BT_DIALOG.SHOW);
                        break;
                    case "hotspot":
                        operator.setIntProp(CommonSignal.COMMON_DEAL_BT, SystemUiConstant.CEILING.QS_PANEL_BT_DIALOG.HIDE);
                        operator.setIntProp(CommonSignal.COMMON_OPEN_AP, ceilDisplayId);
                        break;
                    case "wifi":
                        operator.setIntProp(CommonSignal.COMMON_DEAL_BT, SystemUiConstant.CEILING.QS_PANEL_BT_DIALOG.HIDE);
                        operator.setIntProp(CommonSignal.COMMON_OPEN_WIFI, ceilDisplayId);
                        break;
                }
            }
        }
        if (!TextUtils.isEmpty(tabName)) {
            if (tabName.equals("hotspot_trust")) {
                mSettingHelper.exec(SettingConstants.HOTSPOT_PAGE);
            } else {
                LogUtils.e(TAG, "pageState error");
            }
        }
    }

    public boolean isOpenCeilScreenPage(HashMap<String, Object> map) {
        return getOpenPageOrderScreen(map) == 2;
    }

    public int getOpenPageOrderScreen(HashMap<String, Object> map) {
        // 此处需要区分声源和指令位置
        // 传递displayId 0,1,2,4对应主驾，副驾(副驾娱乐屏)，吸顶屏(二排娱乐屏)
        if (isH37ACar(map) || isH37BCar(map)) {
            return 0;
        }
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
                                displayId = 2;
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
                                displayId = isSupportDealCeilScreen(map) ? 2 : 0;
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
                        displayId = isSupportDealCeilScreen(map) ? 2 : 0;
                        break;
                }
            }
        }
        LogUtils.d(TAG, "getScreenSwitchState displayId: " + displayId);
        return displayId;
    }


    /** ----------------------- 蓝牙 start----------------------------------------------------*/

    /**
     * 蓝牙开关状态
     *
     * @param map 指令的数据
     * @return 蓝牙开关状态
     */
    public boolean getBluetoothSwitchState(HashMap<String, Object> map) {
        String switch_type = getOneMapValue("switch_type", map);
        int displayId = getOpenPageOrderScreen(map);
        if (!TextUtils.isEmpty(switch_type)) {
            if (displayId == 2) {
                // 吸顶屏无法获取蓝牙开关状态，只能设置
                displayId = switch_type.equals("open") ? 1003 : 2003;
            }
        }
        return operator.getBooleanProp(SysSettingSignal.SYS_BLUETOOTH_SWITCH, displayId);
    }

    /**
     * 打开/关闭蓝牙
     * switch_type:open/close
     *
     * @param map 指令的数据
     */
    public void setBluetoothSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setBluetoothSwitchState");
        String switchType = (String) getValueInContext(map, "switch_type");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setBluetoothSwitchState error");
            return;
        }
        if (switchType.equals("open")) {
            LogUtils.d(TAG, "setBluetoothSwitch open");
            operator.setBooleanProp(SysSettingSignal.SYS_BLUETOOTH_SWITCH, getOpenPageOrderScreen(map), true);
        } else if (switchType.equals("close")) {
            LogUtils.d(TAG, "setBluetoothSwitch close");
            operator.setBooleanProp(SysSettingSignal.SYS_BLUETOOTH_SWITCH, getOpenPageOrderScreen(map), false);
        }
    }

    /** ----------------------- 蓝牙 end------------------------------------------------------*/

    /** ----------------------- 热点 start----------------------------------------------------*/

    /**
     * 是否处于热点互联，苹果手机
     *
     * @param map 指令的数据
     * @return true是打开，false是关闭
     */
    public boolean isHotSpotSharing(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_HOTSPOT_SHARING);
    }


    /**
     * 热点是否打开
     *
     * @param map 指令的数据
     * @return 热点开关状态
     */
    public boolean getHotspotSwitchState(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_HOTSPOT_SWITCH);
    }

    /**
     * 打开/关闭热点
     * switch_type:open/close
     *
     * @param map 指令的数据
     */
    public void setHotspotSwitchState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        LogUtils.d(TAG, "setHotspotSwitchState");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setHotspotSwitchState error");
            return;
        }
        if (switchType.equals("open")) {
            LogUtils.d(TAG, "setHotspotSwitch open");
            operator.setBooleanProp(SysSettingSignal.SYS_HOTSPOT_SWITCH, true);
        } else if (switchType.equals("close")) {
            LogUtils.d(TAG, "setHotspotSwitch close");
            operator.setBooleanProp(SysSettingSignal.SYS_HOTSPOT_SWITCH, false);
        }
    }

    /** ----------------------- 热点 end------------------------------------------------------*/

    /** ----------------------- wifi start----------------------------------------------------*/

    /**
     * 是否处于wifi互联，安卓手机
     *
     * @param map 指令的数据
     * @return true是打开，false是关闭
     */
    public boolean isWifiSharing(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_WIFI_SHARING);
    }


    /**
     * Wifi是否打开
     *
     * @param map 指令的数据
     * @return true是打开，false是关闭
     */
    public boolean getWifiSwitchState(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_WIFI_SWITCH);
    }

    /**
     * 打开/关闭Wifi
     * switch_type:open/close
     *
     * @param map 指令的数据
     */
    public void setWifiSwitchState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        LogUtils.d(TAG, "setWifiSwitchState");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setWifiSwitchState error");
            return;
        }
        if (switchType.equals("open")) {
            LogUtils.d(TAG, "setWifiSwitch open");
            operator.setBooleanProp(SysSettingSignal.SYS_WIFI_SWITCH, true);
        } else if (switchType.equals("close")) {
            LogUtils.d(TAG, "setWifiSwitch close");
            operator.setBooleanProp(SysSettingSignal.SYS_WIFI_SWITCH, false);
        }
    }

    /** ----------------------- wifi end------------------------------------------------------*/

    /**
     * ----------------------- 无线充电 start------------------------------------------------------
     */


    public int getWirelessStateSwitch(HashMap<String, Object> map) {
        return operator.getIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE);
    }

    public boolean isWirelessEnable(HashMap<String, Object> map) {
        int area = 0;
        String nluPosition = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(nluPosition)) {
            switch (nluPosition) {
                case "first_row_left":
                    area = PositionSignal.FIRST_ROW_LEFT;
                    break;
                case "first_row_right":
                    area = PositionSignal.FIRST_ROW_RIGHT;
                    break;
                case "front_side":
                    area = PositionSignal.FIRST_ROW;
                    break;
                case "rear_side":
                    area = PositionSignal.REAR_ROW;
                    break;
                case "rear_side_left":
                case "second_row_left":
                    area = PositionSignal.SECOND_ROW_LEFT;
                    break;
                case "rear_side_right":
                case "second_row_right":
                    area = PositionSignal.SECOND_ROW_RIGHT;
                    break;
                case "left_side":
                    area = PositionSignal.LEFT_COL;
                    break;
                case "right_side":
                    area = PositionSignal.RIGHT_COL;
                    break;
                case "total_car":
                    area = PositionSignal.ALL;
                    break;
            }
        } else {
            area = getSoundSourcePos(map);
        }
        LogUtils.d(TAG, "getWirelessStateSwitch area: " + area);
        return operator.getBooleanProp(SysSettingSignal.SYS_WIRELESS_CHARGE, area);
    }

    public boolean isWirelessPositionEnable(HashMap<String, Object> map) {
        String nluPosition = getOneMapValue("positions", map);
        boolean isN2Car = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL) == 2;
        if (!TextUtils.isEmpty(nluPosition)) {
            switch (nluPosition) {
                case "rear_side":
                case "rear_side_left":
                case "second_row_left":
                case "rear_side_right":
                case "second_row_right":
                    return !isN2Car;
                case "third_row_left":
                case "third_row_right":
                    return false;
            }
        } else {
            int curPosition = getSoundSourcePos(map);
            switch (curPosition) {
                case PositionSignal.SECOND_ROW_LEFT:
                case PositionSignal.SECOND_ROW_RIGHT:
                    return !isN2Car;
                case PositionSignal.THIRD_ROW_LEFT:
                case PositionSignal.THIRD_ROW_RIGHT:
                    return false;
            }
        }
        LogUtils.d(TAG, "isPositionEnable");
        return true;
    }

    private int dealPosition(HashMap<String, Object> map) {
        int area = 0;
        String nluPosition = getOneMapValue("positions", map);
        boolean isN2Car = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL) == 2;
        if (!TextUtils.isEmpty(nluPosition)) {
            switch (nluPosition) {
                case "first_row_left":
                    area = PositionSignal.FIRST_ROW_LEFT;
                    break;
                case "first_row_right":
                    area = PositionSignal.FIRST_ROW_RIGHT;
                    break;
                case "front_side":
                    area = PositionSignal.FIRST_ROW;
                    break;
                case "rear_side":
                case "second_side":
                    area = PositionSignal.REAR_ROW;
                    break;
                case "rear_side_left":
                case "second_row_left":
                    area = PositionSignal.SECOND_ROW_LEFT;
                    break;
                case "rear_side_right":
                case "second_row_right":
                    area = PositionSignal.SECOND_ROW_RIGHT;
                    break;
                case "left_side":
                    area = isN2Car ? PositionSignal.FIRST_ROW_LEFT : PositionSignal.LEFT_COL;
                    break;
                case "right_side":
                    area = isN2Car ? PositionSignal.FIRST_ROW_RIGHT : PositionSignal.RIGHT_COL;
                    break;
                case "total_car":
                    area = PositionSignal.ALL;
                    break;
            }
        } else {
            area = getSoundSourcePos(map);
        }
        LogUtils.d(TAG, "dealPosition: " + area);
        return area;
    }

    public boolean isWirelessOrderState(HashMap<String, Object> map) {
        int orderPosition = dealPosition(map);
        int orderState = ICommon.Switch.OFF;
        if (map.containsKey("switch_type")) {
            String str = (String) getValueInContext(map, "switch_type");
            if (str.equals("open")) {
                orderState = ISysSetting.IWirelessCharge.WC_ON;
            } else if (str.equals("close")) {
                orderState = ISysSetting.IWirelessCharge.WC_OFF;
            }
        }
        int workingStatesFL = operator.getIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 0);
        int workingStatesFR = operator.getIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 1);
        int workingStatesRL = operator.getIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 2);
        int workingStatesRR = operator.getIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 3);
        LogUtils.d(TAG, "workingStatesFL:" + workingStatesFL + "workingStatesFR:" + workingStatesFR + "workingStatesRL: " + workingStatesRL + "workingStatesRR: " + workingStatesRR);
        switch (orderPosition) {
            case PositionSignal.FIRST_ROW_LEFT:
                return workingStatesFL == orderState;
            case PositionSignal.FIRST_ROW_RIGHT:
                return workingStatesFR == orderState;
            case PositionSignal.FIRST_ROW:
                return workingStatesFL == orderState && workingStatesFR == orderState;
            case PositionSignal.REAR_ROW:
                return workingStatesRL == orderState && workingStatesRR == orderState;
            case PositionSignal.SECOND_ROW_RIGHT:
                return workingStatesRR == orderState;
            case PositionSignal.SECOND_ROW_LEFT:
                return workingStatesRL == orderState;
            case PositionSignal.LEFT_COL:
                return workingStatesFL == orderState && workingStatesRL == orderState;
            case PositionSignal.RIGHT_COL:
                return workingStatesFR == orderState && workingStatesRR == orderState;
            case PositionSignal.ALL:
                return workingStatesFL == orderState && workingStatesFR == orderState && workingStatesRL == orderState && workingStatesRR == orderState;
        }
        return false;
    }

    public void setWirelessStateSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setWirelessStateSwitch: ");
        if (!isH56CCar(map)) {
            int status = ICommon.Switch.OFF;
            if (map.containsKey("switch_type")) {
                String str = (String) getValueInContext(map, "switch_type");
                if (str.equals("open")) {
                    status = ICommon.Switch.ON;
                } else if (str.equals("close")) {
                    status = ICommon.Switch.OFF;
                }
            }
            operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, status);
            return;
        }
        int status = ICommon.Switch.OFF;
        if (map.containsKey("switch_type")) {
            String str = (String) getValueInContext(map, "switch_type");
            if (str.equals("open")) {
                status = ISysSetting.IWirelessCharge.WC_ON;
            } else if (str.equals("close")) {
                status = ISysSetting.IWirelessCharge.WC_OFF;
            }
        }
        int orderPosition = dealPosition(map);
        switch (orderPosition) {
            case PositionSignal.FIRST_ROW_LEFT:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 0, status);
                break;
            case PositionSignal.FIRST_ROW_RIGHT:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 1, status);
                break;
            case PositionSignal.SECOND_ROW_LEFT:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 2, status);
                break;
            case PositionSignal.SECOND_ROW_RIGHT:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 3, status);
                break;
            case PositionSignal.FIRST_ROW:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 0, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 1, status);
                break;
            case PositionSignal.REAR_ROW:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 2, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 3, status);
                break;
            case PositionSignal.LEFT_COL:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 0, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 2, status);
                break;
            case PositionSignal.RIGHT_COL:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 1, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 3, status);
                break;
            case PositionSignal.ALL:
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 0, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 1, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 2, status);
                operator.setIntProp(SysSettingSignal.SYS_WIRELESS_CHARGE, 3, status);
                break;
        }
    }

    /** ----------------------- 无线充电 end------------------------------------------------------*/

    /** ----------------------- 打开界面 start------------------------------------------------------*/

    /**
     * 是否提示用户需要手动操作
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean getIsManualOperation(HashMap<String, Object> map) {
        String tabName = (String) getValueInContext(map, "tab_name");
        if (!map.containsKey("tab_name")) {
            LogUtils.e(TAG, "getIsManualOperation error");
            return false;
        }
        return !tabName.equals("flow");
    }

    /** ----------------------- 打开界面 end------------------------------------------------------*/

    /**
     * ----------------------- 时间类型 start------------------------------------------------------
     */

    /**
     * 是否指定时间模式
     */
    public boolean getSpecifyTimeType(HashMap<String, Object> map) {
        LogUtils.i(TAG, "getSpecifyTimeType");
        return map.containsKey("time_mode") ? true : false;
    }

    /**
     * 模糊说法时，切换到另外一个模式
     */
    public void setChangeTimeType(HashMap<String, Object> map) {
        String timeType = getTimeType(map);
        if (timeType.equals("12")) {
            operator.setStringProp(SysSettingSignal.SYS_TIME_TYPE, "24");
            map.put("time_mode", "24h");
        } else if (timeType.equals("24")) {
            operator.setStringProp(SysSettingSignal.SYS_TIME_TYPE, "12");
            map.put("time_mode", "12h");
        }
    }

    /**
     * 当前时间显示类型
     */
    public String getTimeType(HashMap<String, Object> map) {
        LogUtils.i(TAG, "getTimeType");
        String timeType = operator.getStringProp(SysSettingSignal.SYS_TIME_TYPE);
        return timeType;
    }

    /**
     * 要设置的时间显示类型
     */
    public String getSetTimeType(HashMap<String, Object> map) {
        LogUtils.i(TAG, "getSetTimeType");
        String timeType = (String) getValueInContext(map, "time_mode");
        return timeType.replace("h", "");
    }

    /**
     * 设置时间类型
     */
    public void setTimeType(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setTimeType");
        operator.setStringProp(SysSettingSignal.SYS_TIME_TYPE, getSetTimeType(map));
    }

    /**
     * 时间类型页面是否已打开
     */
    public boolean getTimeTypePageOpened(HashMap<String, Object> map) {
        if (!mSettingHelper.isCurrentState(SettingConstants.TIME_FORMAT)) {
            mSettingHelper.exec(SettingConstants.TIME_FORMAT);
        }
        return mSettingHelper.isCurrentState(SettingConstants.TIME_FORMAT);
    }


    /** ----------------------- 时间类型 end------------------------------------------------------*/

    /**
     * ----------------------- 主题模式 start------------------------------------------------------
     */

    public boolean isDealSplitScreening(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDealSplitScreening();
    }

    public int getThemeMode(HashMap<String, Object> map) {
        int themeMode = operator.getIntProp(SysSettingSignal.SYS_THEME_MODE);
        LogUtils.d(TAG, "getThemeMode: " + themeMode);
        return themeMode;
    }

    public int getOrdThemeMode(HashMap<String, Object> map) {
        int ordThemeMode = 0;
        String str = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(str)) {
            switch (str) {
                case "day":
                    ordThemeMode = 1;
                    break;
                case "auto":
                    ordThemeMode = 0;
                    break;
                case "night":
                    ordThemeMode = 2;
                    break;
                default:
                    LogUtils.e(TAG, "getOrdThemeMode error");
            }
        } else {
            int curThemeMode = getThemeMode(map);
            switch (curThemeMode) {
                case ISysSetting.ThemeModeType.MODE_NIGHT_AUTO:
                    ordThemeMode = 1;
                    break;
                case ISysSetting.ThemeModeType.MODE_NIGHT_NO:
                    ordThemeMode = 2;
                    break;
                case ISysSetting.ThemeModeType.MODE_NIGHT_YES:
                    ordThemeMode = 0;
                    break;
                default:
                    LogUtils.e(TAG, "getOrdThemeMode error");
            }
        }
        map.put(THEME_MODE, String.valueOf(ordThemeMode));
        LogUtils.d(TAG, "getOrdThemeMode: " + ordThemeMode);
        return ordThemeMode;
    }

    public void setThemeMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setThemeMode");
        operator.setIntProp(SysSettingSignal.SYS_THEME_MODE, getOrdThemeMode(map));
    }

    /** ----------------------- 主题模式 end------------------------------------------------------*/

    /**
     * ----------------------- 音量设置 start----------------------------------------------------
     */

    /**
     * 音量过高提醒
     */
    public boolean isHighVolumeReminder(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isHighVolumeReminder");
        return keyContextInMap(map, "choose_type");
    }

    /**
     * 是取消，还是确认
     */
    public boolean isCancelConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        LogUtils.i(TAG, "isCancelConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    /**
     * 获取number数值
     */
    public int getSetNumber(HashMap<String, Object> map) {
        String numberString = (String) getValueInContext(map, "number");
        if (numberString.contains("%")) {
            numberString = numberString.replace("%", "");
            numberString = String.valueOf(Double.parseDouble(numberString) * 30 / 100);
        }
        if (numberString.contains("/")) {
            String[] parts = numberString.split("/");
            double numerator = Double.parseDouble(parts[0]);
            double denominator = Double.parseDouble(parts[1]);
            numberString = ((numerator / denominator) * 30) + "";
        }
        double doubleValue = Double.parseDouble(numberString);
        double roundedUp = Math.ceil(doubleValue); // 向上取整
        int number = (int) roundedUp; // 转换为int
        LogUtils.i(TAG, "getSetNumber number :" + number);
        return number;
    }

    public double mapRange(double value) {
        return (value * 30) / 100;
    }

    /**
     * 是否指定调节的音量类型
     */
    public boolean getVolumeType(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        LogUtils.i(TAG, "getVolumeType :" + module_name);
        return module_name != null;
    }

    /**
     * 调节通道是否是通话
     */
    public boolean getVolumeIsPhone(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        LogUtils.i(TAG, "getVolumeIsPhone :" + module_name);
        return module_name.equals("phone");
    }

    /**
     * 调节通道是否是蓝牙设备
     */
    public boolean getVolumeIsBluetooth(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        LogUtils.i(TAG, "getVolumeIsBluetooth :" + module_name);
        return module_name.equals("bluetooth_headset") || module_name.equals("bluetooth");
    }

    /**
     * 调节通道是否是语音
     */
    public boolean getVolumeIsVoice(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        LogUtils.i(TAG, "getVolumeIsVoice :" + module_name);
        return module_name.equals("voice");
    }

    /**
     * 调节通道是否是系统
     */
    public boolean getVolumeIsSystem(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        LogUtils.i(TAG, "getVolumeIsSystem :" + module_name);
        return module_name.equals("system") || module_name.equals("system_sound");
    }

    public boolean isCanAdjust(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        if (module_name.equals("navi") || module_name.equals("voice") ||
                module_name.equals("phone") || module_name.equals("media") || module_name.equals("bluetooth_headset")) {
            return true;
        }
        return false;
    }

    /**
     * 获取具体会设置的值
     */
    public boolean getSpecificNumber(HashMap<String, Object> map) {
        int volume = getVolume(map);
        int setNumber = getSetNumber(map);
        String adjustType = (String) getValueInContext(map, "adjust_type");
        if (adjustType.equals("increase")) {
            if (setNumber + volume > 30) {
                return 30 - volume > 17;
            } else {
                return setNumber > 17;
            }
        } else if (adjustType.equals("set")) {
            if (setNumber < 30) {
                return setNumber - volume > 17;
            } else {
                return 30 - volume > 17;
            }
        }
        return false;
    }

    /**
     * 当module_name没有值时，需要获取当前音频焦点
     * 无音频焦点时，默认为媒体音频通道
     */
    public void setVolumeType(HashMap<String, Object> map) {
        int value = operator.getIntProp(SysSettingSignal.SYS_GET_VOLUME_TYPE);
        if (value == 1) {
            map.put("module_name", "navi");
        } else {
            map.put("module_name", "media");
        }
    }

    /**
     * 导航状态
     */
    public boolean getNavStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_IS_NAVI);
    }

    /**
     * 蓝牙耳机是否已经连接
     */
    public boolean isBtHeadsetConnect(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_HAS_BT_CONNECT);
    }

    /**
     * 是否是调节蓝牙/蓝牙耳机音量
     */
    public boolean isAdjustBtHeadset(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        return module_name.equals("bluetooth_headset") || module_name.equals("bluetooth");
    }

    /**
     * 是否是调节媒体音量
     */
    public boolean isAdjustMedia(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        return module_name.equals("media");
    }

    /**
     * 设置调节类型为蓝牙耳机
     */
    public void setVolumeType2BtHeadset(HashMap<String, Object> map) {
        map.put("module_name", "bluetooth_headset");
    }

    /**
     * 获取当前类型音频通道音量
     */
    public int getVolume(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        int volume = getVolumeByStream(getVolumeCode().get(module_name));
        LogUtils.i(TAG, "getVolume : module_name" + module_name + "-- volume :" + volume);
        return volume;
    }

    private int getVolumeByStream(int stream) {
        switch (stream) {
            case ISysSetting.IVolume.STREAM_VOICE_CALL:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_PHONE);
            case ISysSetting.IVolume.STREAM_NVI:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_NAVI);
            case ISysSetting.IVolume.STREAM_NOTIFICATION:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_SYSTEM);
            case STREAM_BLUETOOTH:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_BLUETOOTH);
            case ISysSetting.IVolume.STREAM_ASSISTANT:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_ASSISTANT);
            case ISysSetting.IVolume.STREAM_MUSIC:
                return operator.getIntProp(SysSettingSignal.SYS_VOLUME_MEDIA);
            default:
                return 1;
        }
    }

    private void setVolumeByStream(int stream, int value) {
        switch (stream) {
            case ISysSetting.IVolume.STREAM_VOICE_CALL:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_PHONE, value);
                break;
            case ISysSetting.IVolume.STREAM_NVI:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_NAVI, value);
                break;
            case ISysSetting.IVolume.STREAM_NOTIFICATION:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_SYSTEM, value);
                break;
            case STREAM_BLUETOOTH:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_BLUETOOTH, value);
                break;
            case ISysSetting.IVolume.STREAM_ASSISTANT:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_ASSISTANT, value);
                break;
            case ISysSetting.IVolume.STREAM_MUSIC:
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_MEDIA, value);
        }
    }

    /**
     * 获取当前类型音频通道音量的最小值
     */
    public int getVolumeMin(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        int volumeMin = getVolumeMin(getVolumeCode().get(module_name));
        LogUtils.i(TAG, "getVolumeMin : module_name" + module_name + "-- volumeMin :" + volumeMin);
        return volumeMin;
    }

    public int getVolumeMin(int streamType) {
        //通话最小音量 = 1
        if (streamType == STREAM_VOICE_CALL) {
            return VOLUME_VOL_MIN;
        }
        //其他最小音量 = 0
        return VOLUME_MIN;
    }

    public boolean isLessMin(HashMap<String, Object> map) {
        int curVolume = getVolume(map);
        int number = getSetNumber(map);
        return curVolume - number < getVolumeMin(map);
    }

    public boolean isGreaterMax(HashMap<String, Object> map) {
        int curVolume = getVolume(map);
        int number = getSetNumber(map);
        return curVolume + number > VOLUME_MAX;
    }

    public boolean isSearch(HashMap<String, Object> map) {
        return map.containsKey("operation_type");
    }

    public boolean isAdjustDecreaseToMin(HashMap<String, Object> map) {
        int curVolume = getVolume(map);
        return curVolume - 3 < 1;
    }

    /**
     * 调节音量
     */
    public void setVolume(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        String adjust_type = (String) getValueInContext(map, "adjust_type");
        int curVolume = getVolume(map);
        int temp = 0;
        if (adjust_type.equals("increase")) {
            if (map.containsKey("number")) {
                //调高number
                int number = getSetNumber(map);
                temp = Math.min(curVolume + number, VOLUME_MAX);
            } else {
                //调高一点
                temp = Math.min(curVolume + STEP_VOLUME, VOLUME_MAX);
            }
        } else if (adjust_type.equals("decrease")) {
            if (map.containsKey("number")) {
                //调低number
                int number = getSetNumber(map);
                temp = Math.max(curVolume - number, getVolumeMin(map));
            } else {
                //调低一点
                temp = Math.max(curVolume - STEP_VOLUME, getVolumeMin(map));
            }
        } else if (adjust_type.equals("set")) {
            if (map.containsKey("number")) {
                //调到number
                int number = getSetNumber(map);
                if (number > VOLUME_MAX) {
                    temp = VOLUME_MAX;
                    map.put("number", "30");
                } else if (number < VOLUME_MIN) {
                    temp = VOLUME_MIN;
                    map.put("number", "0");
                } else {
                    temp = number;
                }
            } else {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    //调到最大
                    temp = VOLUME_MAX;
                } else if (level.equals("min")) {
                    //调到最小
                    temp = getVolumeMin(map);
                }
            }
        }
        setVolumeByStream(getVolumeCode().get(module_name), temp);
    }

    /**
     * 调节通话音量为1
     */
    public void setPhoneVolume(HashMap<String, Object> map) {
        setVolumeByStream(STREAM_VOICE_CALL, 1);
    }

    private boolean getMuteStateByStream(int Stream) {
        switch (Stream) {
            case ISysSetting.IVolume.STREAM_VOICE_CALL:
                return operator.getBooleanProp(SysSettingSignal.SYS_MUTE_PHONE);
            case ISysSetting.IVolume.STREAM_NVI:
                return operator.getBooleanProp(SysSettingSignal.SYS_MUTE_NAVI);
            case STREAM_BLUETOOTH:
                return operator.getBooleanProp(SysSettingSignal.SYS_MUTE_BLUETOOTH);
            case ISysSetting.IVolume.STREAM_ASSISTANT:
                return operator.getBooleanProp(SysSettingSignal.SYS_MUTE_ASSISTANT);
            case ISysSetting.IVolume.STREAM_MUSIC:
                return operator.getBooleanProp(SysSettingSignal.SYS_MUTE_MEDIA);
            default:
                return false;
        }
    }

    private void setMuteStateByStream(int Stream, boolean muted) {
        switch (Stream) {
            case ISysSetting.IVolume.STREAM_VOICE_CALL:
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_PHONE, muted);
                break;
            case ISysSetting.IVolume.STREAM_NVI:
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_NAVI, muted);
                break;
            case STREAM_BLUETOOTH:
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_BLUETOOTH, muted);
                break;
            case ISysSetting.IVolume.STREAM_ASSISTANT:
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_ASSISTANT, muted);
                break;
            case ISysSetting.IVolume.STREAM_MUSIC:
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_MEDIA, muted);
        }
    }

    /**
     * 获取音量静音的状态
     */
    public boolean getMuteSound(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        boolean muteSound = getMuteStateByStream(getVolumeCode().get(module_name));
        LogUtils.i(TAG, "getMuteSound : module_name-" + module_name + "-- muteSound :" + muteSound);
        if (!muteSound) {
            int volume = getVolumeByStream(getVolumeCode().get(module_name));
            LogUtils.i(TAG, "getMuteSound : volume-" + volume);
            return volume == 0;
        }
        return muteSound;
    }

    /**
     * 设置音量静音
     */
    public void setMuteSound(HashMap<String, Object> map) {
        String module_name = (String) getValueInContext(map, "module_name");
        String mute_switch = (String) getValueInContext(map, "mute_switch");
        LogUtils.i(TAG, "setMuteSound : module_name-" + module_name + "-- mute_switch :" + mute_switch);
        if (mute_switch != null) {
            setMuteStateByStream(getVolumeCode().get(module_name), mute_switch.equals("open"));
        } else {
            setMuteStateByStream(getVolumeCode().get(module_name), false);
        }
    }


    /**
     * 获取所有音频通道静音状态
     */
    public boolean getAllMuteSound(HashMap<String, Object> map) {
        boolean allMuteSound = true;
        if (map.containsKey("mute_switch")) {
            String mute_switch = (String) getValueInContext(map, "mute_switch");
            allMuteSound = operator.getBooleanProp(SysSettingSignal.SYS_VOLUME_MUTE_STATE, mute_switch.equals("open") ? 1 : 2);
        } else if (map.containsKey("operation_type")) {
            allMuteSound = operator.getBooleanProp(SysSettingSignal.SYS_VOLUME_MUTE_STATE, 2);;
        }
        return allMuteSound;
    }

    /**
     * 设置所有音频通道静音状态
     * 全部静音时，通话不能静音，所以不用处理通话音量
     */
    public void setAllMuteSound(HashMap<String, Object> map) {
        String mute_switch = (String) getValueInContext(map, "mute_switch");
        boolean btHeadsetConnect = isBtHeadsetConnect(map);
        LogUtils.i(TAG, "setAllMuteSound - mute_switch-" + mute_switch);
        if (mute_switch != null) {
            if (mute_switch.equals("open")) {
                setAllMuteSound(true, btHeadsetConnect);
                //发送一键静音信号，需要给到仪表
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_INSTRUMENT, true);
            } else {
                setAllMuteSound(false, btHeadsetConnect);
                //发送取消一键静音信号，需要给到仪表
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_INSTRUMENT, false);
            }
        } else {
            setAllMuteSound(false, btHeadsetConnect);
            //发送取消一键静音信号，需要给到仪表
            operator.setBooleanProp(SysSettingSignal.SYS_MUTE_INSTRUMENT, false);
        }
    }

    private void setAllMuteSound(boolean mute, boolean isBtEarphonesOpened) {
        if (mute) {
            //静音处理
            operator.setBooleanProp(SysSettingSignal.SYS_MUTE_NAVI, true);
            operator.setBooleanProp(SysSettingSignal.SYS_MUTE_ASSISTANT, true);
            if (isBtEarphonesOpened) {
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_BLUETOOTH, 0);
            } else {
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_MEDIA, true);
            }
        } else {
            //取消静音处理，如果已经静音需要取消，如果音量=0，需要设置成15
            boolean navi = operator.getBooleanProp(SysSettingSignal.SYS_MUTE_NAVI);
            if (navi) {
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_NAVI, false);
            } else {
                int naviVolume = operator.getIntProp(SysSettingSignal.SYS_VOLUME_NAVI);
                if (naviVolume == 0) {
                    operator.setIntProp(SysSettingSignal.SYS_VOLUME_NAVI, 15);
                }
            }

            boolean voice = operator.getBooleanProp(SysSettingSignal.SYS_MUTE_ASSISTANT);
            if (voice) {
                operator.setBooleanProp(SysSettingSignal.SYS_MUTE_ASSISTANT, false);
            } else {
                int voiceVolume = operator.getIntProp(SysSettingSignal.SYS_VOLUME_ASSISTANT);
                if (voiceVolume == 0) {
                    operator.setIntProp(SysSettingSignal.SYS_VOLUME_ASSISTANT, 15);
                }
            }

            if (isBtEarphonesOpened) {
                int bluetoothVolume = operator.getIntProp(SysSettingSignal.SYS_VOLUME_BLUETOOTH);
                if (bluetoothVolume == 0) {
                    operator.setIntProp(SysSettingSignal.SYS_VOLUME_BLUETOOTH, 15);
                }
            } else {
                boolean media = operator.getBooleanProp(SysSettingSignal.SYS_MUTE_MEDIA);
                if (media) {
                    operator.setBooleanProp(SysSettingSignal.SYS_MUTE_MEDIA, false);
                } else {
                    int voiceVolume = operator.getIntProp(SysSettingSignal.SYS_VOLUME_MEDIA);
                    if (voiceVolume == 0) {
                        operator.setIntProp(SysSettingSignal.SYS_VOLUME_MEDIA, 15);
                    }
                }
            }
        }
    }

    /**
     * 通过音频焦点的类型code来获取module_name
     */
    public Map<Integer, String> getVolumeModuleName() {
        return new HashMap<Integer, String>() {
            {
                put(ISysSetting.IVolume.STREAM_VOICE_CALL, "phone");
                        put(ISysSetting.IVolume.STREAM_NVI, "navi");
                        put(ISysSetting.IVolume.STREAM_NOTIFICATION, "system_sound");
                        put(STREAM_BLUETOOTH, "bluetooth_headset");
                        put(ISysSetting.IVolume.STREAM_ASSISTANT, "voice");
                        put(ISysSetting.IVolume.STREAM_MUSIC, "media");
            }
        };
    }

    /**
     * 通过module_name来获取音频焦点的类型code
     */
    public Map<String, Integer> getVolumeCode() {
        return new HashMap<String, Integer>() {
            {
                put("phone", ISysSetting.IVolume.STREAM_VOICE_CALL);
                put("navi", ISysSetting.IVolume.STREAM_NVI);
                put("system_sound", ISysSetting.IVolume.STREAM_NOTIFICATION);
                put("bluetooth_headset", STREAM_BLUETOOTH);
                put("voice", ISysSetting.IVolume.STREAM_ASSISTANT);
                put("media", ISysSetting.IVolume.STREAM_MUSIC);
                put("system", ISysSetting.IVolume.STREAM_NOTIFICATION);
                put("bluetooth", STREAM_BLUETOOTH);
            }
        };
    }

    /**
     * 通过module_name来获取音频焦点的类型名称
     */
    public Map<String, String> getVolumeName() {
        return new HashMap<String, String>() {
            {
                put("phone", "通话");
                put("navi", "导航");
                put("system_sound", "系统提示音");
                put("bluetooth_headset", "蓝牙耳机");
                put("voice", "语音");
                put("media", "媒体");
                put("system", "系统提示音");
                put("bluetooth", "蓝牙");
            }
        };
    }

    /**
     * ----------------------- 音量设置 end----------------------------------------------------
     */

    /**
     * ----------------------- 按键音 start----------------------------------------------------
     */

    /**
     * 获取按键音的开关状态
     */
    public int getKeyTone(HashMap<String, Object> map) {
        return operator.getIntProp(SysSettingSignal.SYS_KEY_TONE);
    }


    /**
     * 设置按键音的开关(open = 1 , close = 0)
     */
    public void setKeyTone(HashMap<String, Object> map) {
        //打开关闭（因为沿用的之前静音的逻辑，所以打开关闭是反的）
        if (map.containsKey("mute_switch")) {
            String mute_switch = (String) getValueInContext(map, "mute_switch");
            LogUtils.i(TAG, "setKeyTone : switch_type-" + mute_switch);
            operator.setIntProp(SysSettingSignal.SYS_KEY_TONE, "open".equals(mute_switch) ? 0 : 1);
        }
        //查询
        if (map.containsKey("operation_type")) {
            operator.setIntProp(SysSettingSignal.SYS_KEY_TONE, ICommon.Switch.ON);
        }
    }

    /**
     * ----------------------- 按键音 end----------------------------------------------------
     */

    /**
     * ----------------------- 音量随速 start----------------------------------------------------
     */

    /**
     * 获取音量随速的开关状态
     */
    public boolean getVolumeWithSpeedStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_VOLUME_FOLLOW_SPEED);
    }

    /**
     * 设置音量随速的开关(open = 1 , close = 0)
     */
    public void setVolumeWithSpeedStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setVolumeWithSpeedStatus : switch_type-" + switch_type);
        operator.setBooleanProp(SysSettingSignal.SYS_VOLUME_FOLLOW_SPEED, "open".equals(switch_type));
    }

    /**
     * ----------------------- 音量随速 end----------------------------------------------------
     */

    /**
     * ----------------------- 低速行人警示 start----------------------------------------------------
     */

    public boolean getLowSpeedPedesWarningStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH);
    }

    public void setLowSpeedPedesWarningStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH, "open".equals(switch_type));
    }

    public boolean isCurLowSpeedPedesWarningMode(HashMap<String, Object> map) {
        // 如果开关是关闭的，需要先把低速行人警示开关打开
        if (!getLowSpeedPedesWarningStatus(map)) {
            operator.setBooleanProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH, true);
        }
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int lowSpeedPedesWarningStatus = operator.getIntProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE);
        return lowSpeedPedesWarningStatus == getLowSpeedPedesWarningMode().get(switch_mode);
    }

    public void setLowSpeedPedesWarningMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        operator.setIntProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE,getLowSpeedPedesWarningMode().get(switch_mode));
    }

    public void changeLowSpeedPedesWarningMode(HashMap<String, Object> map) {
        int mode;
        boolean state = operator.getBooleanProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH);
        if (!state) {
            //如果是关闭状态的，就设置模式2
            operator.setBooleanProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH, true);
            mode = 2;
        } else {
            int value = operator.getIntProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE);
            if (value == 4) {
                mode = 2;
            } else {
                mode = value + 1;
            }
        }
        map.put("switch_mode",getLowSpeedPedesWarningValue().get(mode));
        operator.setIntProp(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE,mode);
    }

    public Map<String, String> getLowSpeedPedesWarningName() {
        return new HashMap<String, String>() {
            {
                put("future_technology", "科技未来");
                put("dreamer", "梦想家");
                put("eden", "伊甸园");
            }
        };
    }

    public Map<String, Integer> getLowSpeedPedesWarningMode() {
        return new HashMap<String, Integer>() {
            {
                put("future_technology",2);
                put("dreamer",3);
                put("eden",4);
            }
        };
    }

    public Map<Integer, String> getLowSpeedPedesWarningValue() {
        return new HashMap<Integer, String>() {
            {
                put(2,"future_technology");
                put(3,"dreamer");
                put(4,"eden");
            }
        };
    }

    /**
     * ----------------------- 低速行人警示 end----------------------------------------------------
     */

    /**
     * ----------------------- 模拟声浪 start----------------------------------------------------
     */

    /**
     * 获取模拟声浪开关状态 startMode = 1 是关闭状态，startMode > 1 是开启状态
     */
    public int getVolumeImitateStatus(HashMap<String, Object> map) {
        int startMode = operator.getIntProp(SysSettingSignal.SYS_VOLUME_IMITATE);
        LogUtils.i(TAG, "getVolumeImitateStatus : startMode-" + startMode);
        return startMode;
    }

    /**
     * 设置模拟声浪开关状态
     */
    public void setVolumeImitateStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        if (switch_type.equals("open")) {
            if (map.containsKey("switch_mode")) {
                String switch_mode = (String) getValueInContext(map, "switch_mode");
                if (switch_mode.equals("tradition")) {
                    operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TRADITION);
                } else if (switch_mode.equals("technology")) {
                    operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TECHNOLOGY);
                }
            } else {
                operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TRADITION);
            }
        } else if (switch_type.equals("close")) {
            operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.OFF);
        }
    }

    /**
     * 当前声浪模式 = 要设置的模式
     */
    public boolean getCurVolumeImitateModeState(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int volumeImitateStatus = getVolumeImitateStatus(map);
        if (switch_mode.equals("tradition")) {
            return volumeImitateStatus == ISysSetting.VolumeImitate.TRADITION;
        } else {
            return volumeImitateStatus == ISysSetting.VolumeImitate.TECHNOLOGY;
        }
    }

    /**
     * 设置模拟声浪为传统
     */
    public void setCurVolumeImitateToTradition(HashMap<String, Object> map) {
        operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TRADITION);
    }

    /**
     * 切换模拟声浪模式到另外一个
     */
    public void setCurVolumeImitateToChange(HashMap<String, Object> map) {
        int volumeImitateStatus = getVolumeImitateStatus(map);
        if (volumeImitateStatus == ISysSetting.VolumeImitate.TRADITION) {
            operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TECHNOLOGY);
            map.put("switch_mode", "technology");
        } else if (volumeImitateStatus == ISysSetting.VolumeImitate.TECHNOLOGY) {
            operator.setIntProp(SysSettingSignal.SYS_VOLUME_IMITATE, ISysSetting.VolumeImitate.TRADITION);
            map.put("switch_mode", "tradition");
        }
    }

    /**
     * 是否是设置车内车外位置
     */
    public boolean isHasPosition(HashMap<String, Object> map) {
        String positions = (String) getValueInContext(map, "positions");
        return positions != null;
    }

    /**
     * 是否是车内
     */
    public boolean isInsideCar(HashMap<String, Object> map) {
        String positions = (String) getValueInContext(map, "positions");
        return "inside_car".equals(positions);
    }

    /**
     * 是否是车外
     */
    public boolean isOutsideCar(HashMap<String, Object> map) {
        String positions = (String) getValueInContext(map, "positions");
        return "outside_car".equals(positions);
    }

    /**
     * 获取模拟声浪发声位置 = 0 车内， = 1 车外
     */
    public int getVolumeImitatePosition(HashMap<String, Object> map) {
        int intProp = operator.getIntProp(SysSettingSignal.SYS_IMITATE_POS);
        LogUtils.i(TAG, "getVolumeImitatePosition : intProp-" + intProp);
        return intProp;
    }

    /**
     * 设置模拟声浪发声位置
     */
    public void setVolumeImitatePosition(HashMap<String, Object> map) {
        String positions = (String) getValueInContext(map, "positions");
        LogUtils.i(TAG, "setVolumeImitatePosition : position-" + positions);
        if (positions.equals("inside_car")) {
            operator.setIntProp(SysSettingSignal.SYS_IMITATE_POS, ISysSetting.VolumeImitatePos.IN_CAR);
        } else if (positions.equals("outside_car")) {
            operator.setIntProp(SysSettingSignal.SYS_IMITATE_POS, ISysSetting.VolumeImitatePos.OUT_CAR);
        }
    }

    /**
     * 设置默认为车内
     */
    public void setVolumeImitateInSide(HashMap<String, Object> map) {
        operator.setIntProp(SysSettingSignal.SYS_IMITATE_POS, ISysSetting.VolumeImitatePos.IN_CAR);
        map.put("positions", "inside_car");
    }

    /**
     * 切换模拟声浪位置到另外一个
     */
    public void setCurVolumeImitatePositionToChange(HashMap<String, Object> map) {
        int volumeImitatePosition = getVolumeImitatePosition(map);
        if (volumeImitatePosition == ISysSetting.VolumeImitatePos.IN_CAR) {
            operator.setIntProp(SysSettingSignal.SYS_IMITATE_POS, ISysSetting.VolumeImitatePos.OUT_CAR);
            map.put("positions", "outside_car");
        } else if (volumeImitatePosition == ISysSetting.VolumeImitatePos.OUT_CAR) {
            operator.setIntProp(SysSettingSignal.SYS_IMITATE_POS, ISysSetting.VolumeImitatePos.IN_CAR);
            map.put("positions", "inside_car");
        }
    }

    /**
     * position to name
     */
    public Map<String, String> getPositionSideName() {
        return new HashMap<String, String>() {
            {
                put("outside_car", "车外");
                put("inside_car", "车内");
            }
        };
    }

    /**
     * ----------------------- 模拟声浪 end----------------------------------------------------
     */

    public boolean isAdjust(HashMap<String, Object> map) {
        return (map.containsKey("positions") && getOneMapValue("positions", map) != null) || map.containsKey("switch_mode") || map.containsKey("adjust_type");
    }

    /**
     * 声场聚焦
     */
    public boolean getVolumeFocus(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setVolumeFocus");
        return mSettingHelper.isCurrentState(SettingConstants.SOUND_FIELD_FOCUS);
    }

    /**
     * 声场聚焦
     */
    public void setVolumeFocus(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setVolumeFocus");
        mSettingHelper.exec(SettingConstants.SOUND_FIELD_FOCUS);
    }

    /**
     * 声场音色
     */
    public boolean isVolumeFeatureOpend(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isVolumeFeatureOpend");
        boolean currentState = mSettingHelper.isCurrentState(SettingConstants.SOUND_CHARACTER);
        return currentState;
    }

    /**
     * 声场音色
     */
    public void setVolumeFeature(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setVolumeFeature");
        mSettingHelper.exec(SettingConstants.SOUND_CHARACTER);
    }

    /**
     * 音效设置（低音/中音/重音）
     */
    public void setDiapason(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setDiapason");
        mSettingHelper.exec(SettingConstants.SOUND_CHARACTER);
    }

    public boolean isDiapasonAssessVehicleConfiguration(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_DIAPASON_ASSESS_VEHICLE_CONFIGURATION);
    }

    public boolean isDeepBass(HashMap<String, Object> map) {
        if (map.containsKey("diapason_mode")) {
            String  diapasonMode = (String) getValueInContext(map, "diapason_mode");
            return diapasonMode.equalsIgnoreCase("deep_bass");
        }
        return true;
    }

    /**
     * 获取媒体外放二次确认是否需要开启
     */
    public boolean getTurnOnMediaOutside(HashMap<String, Object> map) {
        //是否进行弹窗提示 1 = 提示  2 = 不提示
        int value = operator.getIntProp(SysSettingSignal.SYS_MEDIA_OUTPLAY_CONFIRM);
        LogUtils.i(TAG, "getTurnOnMediaOutside value :" + value);
        return value == 1;
    }

    /**
     * 打开媒体外放二次确认弹窗
     */
    public void setTurnOnMediaOutside(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setTurnOnMediaOutside");
        operator.setBooleanProp(SysSettingSignal.SYS_SHOW_MEDIA_OUTSIDE_DIALOG,true);
    }

    /**
     * 获取媒体外放状态
     */
    public boolean getMediaOutsideSoundStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_MEDIA_OUTPLAY);
    }

    /**
     * 设置媒体外放
     */
    public void setMediaOutsideSoundStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setMediaOutsideSoundStatus : switch_type-" + switch_type);
        if (switch_type.equals("open")) {
            operator.setIntProp(SysSettingSignal.SYS_MEDIA_OUTPLAY, ICommon.Switch.ON);
        } else if (switch_type.equals("close")) {
            operator.setIntProp(SysSettingSignal.SYS_MEDIA_OUTPLAY, ICommon.Switch.OFF);
        }
    }

    /**
     * 获取辅助驾驶播报开关状态
     */
    public int getDrivingAssistBroadcastStatus(HashMap<String, Object> map) {
        int driveAidBroadcast = operator.getIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST);
        LogUtils.i(TAG, "getDrivingAssistBroadcastStatus : driveAidBroadcast-" + driveAidBroadcast);
        return driveAidBroadcast;
    }

    /**
     * 设置辅助驾驶播报开关状态
     */
    public void setDrivingAssistBroadcastStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setDrivingAssistBroadcastStatus : switch_type-" + switch_type);
        if (switch_type.equals("open")) {
            //打开就是默认设置成“简洁”
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.BRIEF);
        } else if (switch_type.equals("close")) {
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.OFF);
        }
    }

    /**
     * 打开辅助驾驶播报开关状态
     */
    public void setDrivingAssistBroadcastOpen(HashMap<String, Object> map) {
        operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.BRIEF);
        map.put("switch_mode", "brief");
    }

    /**
     * 获取辅助驾驶播报模式
     */
    public int getDrivingAssistBroadcastMode(HashMap<String, Object> map) {
        int driveAidBroadcastMode = operator.getIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST);
        LogUtils.i(TAG, "getDrivingAssistBroadcastMode : driveAidBroadcastMode-" + driveAidBroadcastMode);
        return driveAidBroadcastMode;
    }

    /**
     * 当前模式是否已经是要设置的模式
     */
    public boolean getCurModeState(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int driveAidBroadcastMode = getDrivingAssistBroadcastMode(map);
        if (switch_mode.equals("brief")) {
            return driveAidBroadcastMode == ISysSetting.DrvAssistBroadcast.BRIEF;
        } else {
            return driveAidBroadcastMode == ISysSetting.DrvAssistBroadcast.DETAIL;
        }
    }

    /**
     * 设置辅助驾驶播报模式（1:简洁，2:详细）
     */
    public void setDrivingAssistBroadcastMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        LogUtils.i(TAG, "setDrivingAssistBroadcastMode : switch_mode-" + switch_mode);
        if (switch_mode.equals("brief")) {
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.BRIEF);
        } else if (switch_mode.equals("detail")) {
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.DETAIL);
        }
    }

    /**
     * 切换辅助驾驶播报模式（1:简洁，2:详细）
     */
    public void setChangeDrivingAssistBroadcastMode(HashMap<String, Object> map) {
        int drivingAssistBroadcastMode = getDrivingAssistBroadcastMode(map);
        if (drivingAssistBroadcastMode == ISysSetting.DrvAssistBroadcast.BRIEF) {
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.DETAIL);
            map.put("switch_mode", "detail");
        } else if (drivingAssistBroadcastMode == ISysSetting.DrvAssistBroadcast.DETAIL) {
            operator.setIntProp(SysSettingSignal.SYS_DRV_ASSIST_CAST, ISysSetting.DrvAssistBroadcast.BRIEF);
            map.put("switch_mode", "brief");
        }
    }

    /**
     * ----------------------- 通用函数 start------------------------------------------------------
     */

    private String getPageName(HashMap<String, Object> map) {
        String str = "";
        String pageName = (String) getValueInContext(map, "page_name");
        if (!TextUtils.isEmpty(pageName)) {
            switch (pageName) {
                case "setting":
                    str = "设置";
                    break;
                case "connect_history":
                    str = "历史连接";
                    break;
            }
        }
        return str;
    }

    public boolean isSettingPage(HashMap<String, Object> map) {
        String pageName = (String) getValueInContext(map, "page_name");
        if (!TextUtils.isEmpty(pageName)) {
            switch (pageName) {
                case "setting":
                    return true;
                case "connect_history":
                    return false;
            }
        }
        return false;
    }

    private String getTabName(HashMap<String, Object> map) {
        String str = "";
        String pageName = (String) getValueInContext(map, "tab_name");
        if (!TextUtils.isEmpty(pageName)) {
            switch (pageName) {
                case "device_name":
                    str = "通用页面";
                    break;
                case "flow":
                    str = "流量查询";
                    break;
                case "common":
                    str = "通用";
                    break;
                case "vehicle":
                    str = "车辆";
                    break;
                case "hud":
                    str = "HUD";
                    break;
                case "instrument_screen":
                    str = "仪表";
                    break;
                case "central_screen":
                    str = "中控";
                    break;
            }
        }
        return str;
    }

    /**
     * 当前车辆挡位（P挡:0 R挡:1 N挡:2 D挡:3 S挡:4）
     */
    public int getDriveGearPosition(HashMap<String, Object> map) {
        int curDriveGearPosition = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
        LogUtils.i(TAG, "curDriveGearPosition :" + curDriveGearPosition);
        return curDriveGearPosition;
    }

    /** ----------------------- 通用函数 end------------------------------------------------------*/

    /**
     * 判断设置是否打开
     *
     * @return
     */
    public boolean isSettingOpen(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.CENTRAL_SCREEN, "");
        return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(ApplicationConstant.PKG_SETTINGS, DeviceScreenType.CENTRAL_SCREEN);
    }

    public boolean isFrSettingOpen(HashMap<String, Object> map) {
        return mSettingHelper.isCurrentState(SettingConstants.PASSENGER_SCREEN_PAGE);
    }

    public void openFrSetting(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.PASSENGER_SCREEN_PAGE);;
    }

    /** ----------------------- 后排乘客安全带未系提醒 start------------------------------------------------------*/

    /**
     * 获取后排乘客安全带未系提醒开关状态
     */
    public boolean getSeatBeltReminderState(HashMap<String, Object> map) {
        boolean state = operator.getBooleanProp(SysSettingSignal.SYS_REAR_BELT_REMINDER_SWITCH);
        LogUtils.i(TAG, "getSeatBeltReminderState state :" + state);
        return state;
    }

    /**
     * 获取后排乘客安全带未系提醒开关状态
     */
    public void setSeatBeltReminderState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_REAR_BELT_REMINDER_SWITCH, "open".equals(switch_type));
    }


    /** ----------------------- 后排乘客安全带未系提醒 end------------------------------------------------------*/

    /** ----------------------- 5G优先开关 start------------------------------------------------------*/

    /**
     * 获取优先5G开关状态
     */
    public boolean get5GState(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_5GP_SWITCH);
    }

    /**
     * 获取后排乘客安全带未系提醒开关状态
     */
    public void set5GStateState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_5GP_SWITCH, switch_type.equals("open"));
    }


    /** ----------------------- 5G优先开关 end------------------------------------------------------*/


    /**
     * ----------------------- 遥控钥匙播报 start------------------------------------------------------
     */

    public boolean getRemoteKeyBroadcastState(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_REMOTE_KEY_BROADCAST_SWITCH);
    }

    public void setRemoteKeyBroadcastState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_REMOTE_KEY_BROADCAST_SWITCH, switch_type.equals("open"));
    }

    /** ----------------------- 遥控钥匙播报 end------------------------------------------------------*/

    /**
     * ----------------------- 智能声场 start------------------------------------------------------
     */

    public boolean getIntelligentVolume(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_INTELLIGENT_VOLUME_SWITCH);
    }

    public void setIntelligentVolume(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_INTELLIGENT_VOLUME_SWITCH,switch_type.equals("open"));
    }


    /** ----------------------- 智能声场 end------------------------------------------------------*/

    /**
     * ----------------------- 仪表提示音 start------------------------------------------------------
     */

    public boolean isInstument(HashMap<String, Object> map) {
        if (map.containsKey("module_name")) {
            String module_name = (String) getValueInContext(map, "module_name");
            return module_name.equals("instrument_screen");
        }
        return false;
    }

    public boolean isCurLevelMode(HashMap<String, Object> map) {
        String level = (String) getValueInContext(map, "level");
        int value = operator.getIntProp(SysSettingSignal.SYS_INSTUMENT_TONE);
        if (level.equals("low")) {
            return value == 0;
        }
        if (level.equals("mid")) {
            return value == 1;
        }
        if (level.equals("high")) {
            return value == 2;
        }
        return false;
    }

    public void setInstumentTone(HashMap<String, Object> map) {
        int value = 0;
        String level = (String) getValueInContext(map, "level");
        if (level.equals("low")) {
            value = 0;
        }
        if (level.equals("mid")) {
            value = 1;
        }
        if (level.equals("high")) {
            value = 2;
        }
        operator.setIntProp(SysSettingSignal.SYS_INSTUMENT_TONE, value);
    }

    /** ----------------------- 仪表提示音 end------------------------------------------------------*/

    /**
     * 打开日志平台
     *
     * @param map
     */
    public void openLogPage(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getCarService().getSystemSetting().openUpLogPage();
    }

    /** ----------------------- 头枕音响 start------------------------------------------------------*/

    public boolean isHeadrestSoundConfig(HashMap<String, Object> map) {
        int mHeadrestSoundConfig = operator.getIntProp(CommonSignal.COMMON_SUPPORT_HEADREST);
        LogUtils.i(TAG, "isHeadrestSoundConfig mHeadrestSoundConfig :" + mHeadrestSoundConfig);
        return mHeadrestSoundConfig != 0;
    }

    public boolean isHeadrestSoundOpend(HashMap<String, Object> map) {
        int mHeadrestAudioMode = operator.getIntProp(CommonSignal.COMMON_HEADREST_STATE);
        LogUtils.i(TAG, "isHeadrestSoundOpend mHeadrestAudioMode :" + mHeadrestAudioMode);
        return mHeadrestAudioMode > 1;
    }

    public void setHeadrestSoundState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE, switch_type.equals("open") ? 2 : 1);
    }

    public boolean isHeadrestSoundModeOpend(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int mHeadrestAudioMode = operator.getIntProp(CommonSignal.COMMON_HEADREST_STATE);
//        mode2=主驾专注 mode3=主驾私享
        if (switch_mode.equals("drive_concentration")) {
            return mHeadrestAudioMode == 2;
        } else if (switch_mode.equals("drive_private")) {
            return mHeadrestAudioMode == 3;
        }
        return false;
    }

    public void setHeadrestSoundMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        //mode2=主驾专注 mode3=主驾私享
        if (switch_mode.equals("drive_concentration")) {
            operator.setIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE, 2);
        } else if (switch_mode.equals("drive_private")) {
            operator.setIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE, 3);
        }
    }

    public void setHeadrestSoundModeToNext(HashMap<String, Object> map) {
        int mHeadrestAudioMode = operator.getIntProp(CommonSignal.COMMON_HEADREST_STATE);
        if (mHeadrestAudioMode == 1 || mHeadrestAudioMode == 3) {
            //如果头枕音响模式是关闭或者主驾私享，就调节到主驾专注
            operator.setIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE, 2);
            map.put("switch_mode","drive_concentration");
        } else {
            //如果头枕音响模式是主驾专注，就调节到主驾私享
            operator.setIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE, 3);
            map.put("switch_mode","drive_private");
        }
    }

    /** ----------------------- 头枕音响 end------------------------------------------------------*/

    /** ----------------------- AI音乐曲风自动适配 start------------------------------------------------------*/

    public boolean isHasAiMusicStyle(HashMap<String, Object> map) {
        //N2 没有 N3N4有
        int mEquipmentLevelConfig = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        return mEquipmentLevelConfig == 3 || mEquipmentLevelConfig == 4;
    }

    public boolean isCurOriginalHIFIMode(HashMap<String, Object> map) {
        int modeValue = operator.getIntProp(SysSettingSignal.SYS_SOUND_EFFECTS_MODE);
        return modeValue == 1;
    }

    public boolean isAiMusicStyleOpend(HashMap<String, Object> map) {
        //1=关 2=开
        int state = DeviceHolder.INS().getDevices().getCarService().getSystemSetting()
                .getInt(ISysSetting.SystemSettingType.SETTING_GLOBAL, "MUSIC_STYLE_SWITCH", 0);
        return state == 1;
    }

    public void setAiMusicStyleState(HashMap<String, Object> map) {
        //1=关 2=开
        String switch_type = (String) getValueInContext(map, "switch_type");
        DeviceHolder.INS().getDevices().getCarService().getSystemSetting()
                .putInt(ISysSetting.SystemSettingType.SETTING_GLOBAL, "MUSIC_STYLE_SWITCH", switch_type.equals("open") ? 1 : 0);
    }

    /** ----------------------- AI音乐曲风自动适配 end------------------------------------------------------*/

    /** ----------------------- 音效模式 start------------------------------------------------------*/

    public boolean isCurSoundEffectsMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int intProp = operator.getIntProp(SysSettingSignal.SYS_SOUND_EFFECTS_MODE);
        return intProp == getCurSoundEffectsModeValue().get(switch_mode);
    }

    public void setSoundEffectsMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        operator.setIntProp(SysSettingSignal.SYS_SOUND_EFFECTS_MODE, getCurSoundEffectsModeValue().get(switch_mode));
    }

    public void changeSoundEffectsMode(HashMap<String, Object> map) {
        int nextMode = 1;
        int mode = operator.getIntProp(SysSettingSignal.SYS_SOUND_EFFECTS_MODE);
        if (mode != 5) {
            nextMode = mode + 1;
        }
        map.put("switch_mode", getCurSoundEffectsMode().get(nextMode));
        operator.setIntProp(SysSettingSignal.SYS_SOUND_EFFECTS_MODE, nextMode);
    }

    public Map<String, Integer> getCurSoundEffectsModeValue() {
        return new HashMap<String, Integer>() {{
            put("hifi_soundtrack", 1);
            put("odeum", 2);
            put("cinema", 3);
            put("livehouse", 4);
            put("3d_surround", 5);
        }};
    }

    public Map<Integer, String> getCurSoundEffectsMode() {
        return new HashMap<Integer, String>() {
            {
                put(1, "hifi_soundtrack");
                put(2, "odeum");
                put(3, "cinema");
                put(4, "livehouse");
                put(5, "3d_surround");
            }
        };
    }

    public Map<String, String> getCurSoundEffectsName() {
        return new HashMap<String, String>() {
            {
                put("hifi_soundtrack", "HIFI原声");
                put("odeum", "音乐厅");
                put("cinema", "影院");
                put("livehouse", "LiveHouse");
                put("3d_surround", "3D环绕");
            }
        };
    }

    /** ----------------------- 音效模式 end------------------------------------------------------*/

    /** ----------------------- 声场音色模式 start------------------------------------------------------*/

    public boolean isSupportMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        return systemControlInterface.isSupportMode(switch_mode);
    }

    public boolean isCurVolumeFeatureMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        return systemControlInterface.isCurVolumeFeatureMode(switch_mode);
    }

    public void setVolumeFeatureMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        systemControlInterface.setVolumeFeatureMode(switch_mode);
    }

    public void changeVolumeFeatureMode(HashMap<String, Object> map) {
        String modeName = systemControlInterface.changeVolumeFeatureMode();
        map.put("switch_mode",modeName);
    }

    public void setVolumeFeatureToEqualizerAndOpenPage(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.NEW_SOUND_EFFECTEQ_DIGLOG);
        systemControlInterface.setVolumeFeatureMode("equalizer");
    }

    /** ----------------------- 声场音色模式 end------------------------------------------------------*/

    /** ----------------------- 声场聚焦模式 start------------------------------------------------------*/

    public boolean isOpenVolumeFocusUI(HashMap<String, Object> map) {
        String positions = (String) map.get("positions");
        return !map.containsKey("switch_mode") && positions == null;
    }

    public boolean isSupportCurVolumeFocusMode(HashMap<String, Object> map) {
        String mode;
        if (map.containsKey("switch_mode")) {
            mode = (String) getValueInContext(map, "switch_mode");
        } else {
            mode = (String) map.get("positions");
        }
        return systemControlInterface.isSupportCurVolumeFocusMode(mode);
    }

    public boolean isCurVolumeFocusMode(HashMap<String, Object> map) {
        return systemControlInterface.isCurVolumeFocusMode(map);
    }

    public void setVolumeFocusMode(HashMap<String, Object> map) {
        systemControlInterface.setVolumeFocusMode(map);
    }

    public void changeVolumeFocusMode(HashMap<String, Object> map) {
        systemControlInterface.changeVolumeFocusMode(map);
    }

    public Map<String, String> getVolumeFocusName() {
        return new HashMap<String, String>() {
            {
                put("first_row_left","主驾");
                put("first_row_right","副驾");
                put("front_side","前排");
                put("rear_side","后排");
                put("total_car","全部");
                put("custom","自定义");
            }
        };
    }

    /** ----------------------- 声场聚焦模式 end------------------------------------------------------*/

    public boolean isHasPositionTab(HashMap<String, Object> map) {
        return !isH37BCar(map) && !isH37ACar(map);
    }

    public boolean isDealPosition(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            return position.equals("first_row_right");
        }
        String screen = getOneMapValue("screen_name", map);
        if (!TextUtils.isEmpty(screen)) {
            return screen.equals("entertainment_screen") || screen.equals("passenger_screen");
        }
        return false;
    }

    /** ----------------------- 夜间静音 start------------------------------------------------------*/

    public boolean isNighttimeMuteOpend(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_NIGHTTIME_MUTE_SWITCH);
    }

    public void setNighttimeMuteState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_NIGHTTIME_MUTE_SWITCH, switch_type.equals("open"));
    }

    public boolean isShowNighttimeMuteDialog(HashMap<String, Object> map) throws RemoteException {
        return mSettingHelper.isCurrentState(SettingConstants.NIGHTTIME_SILENT);
    }

    public void showNighttimeMuteDialog(HashMap<String, Object> map) throws RemoteException {
        mSettingHelper.exec(SettingConstants.NIGHTTIME_SILENT);
    }

    /** ----------------------- 夜间静音 end------------------------------------------------------*/

    public boolean isAutoParkingHintSwitchOpened(HashMap<String, Object> map) {
        return operator.getBooleanProp(SysSettingSignal.SYS_AUTO_PARKING_HINT_SWITCH);
    }

    public void setAutoParkingHintSwitch(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(SysSettingSignal.SYS_AUTO_PARKING_HINT_SWITCH, switch_type.equals("open"));
    }

}
