package com.voyah.ai.logic.dc;


import static com.voice.sdk.device.carservice.signal.SteeringWheelSignal.STEERING_WHEEL_CUSTOM_TYPE;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ISteeringWheel;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.SteeringWheelSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;

public class SteeringWheelControlImpl extends AbsDevices {

    private static final String TAG = SteeringWheelControlImpl.class.getSimpleName();

    public SteeringWheelControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "SteeringWheel";
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
        switch (key) {
            case "switch_mode":
            case "set_steering_mode":
                String switchMode = (String) getValueInContext(map, "switch_mode");
                if (key.equals("switch_mode")) {
                    str = str.replace("@{switch_mode}", getSwitchMode(map, switchMode));
                } else {
                    str = str.replace("@{set_steering_mode}", getSwitchMode(map, switchMode));
                }
                break;
            case "set_steering_assistance": //方向盘助力
                String steerMode = getSteerModeStringFromContext(map);
                str = str.replace("@{set_steering_assistance}", steerMode);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

    private String getSwitchMode(HashMap<String, Object> map, String str) {
        switch (str) {
            case "360_park":
                str = "360全景影像";
                break;
            case "arhud":
                str = "HUD显示";
                break;
            case "screen_shift":
                str = "屏幕移动";
                break;
            case "selfie":
                str = "自拍拍照";
                break;
            case "shout_out":
                str = "对外喊话";
                break;
            case "source_switch":
                str = "音源切换";
                break;
            case "trip_shoot":
                str = "旅拍拍照";
                break;
            case "low_speed_alert":
                str = "低速行人警示音";
                break;
            case "mute":
                str = "静音";
                break;
            default:
        }
        return str;
    }

    public boolean getCustomKeyPage(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getCustomKeyPage");
        if (mSettingHelper == null) {
            return false;
        }
        return mSettingHelper.isCurrentState(SettingConstants.CUSTOM_STEER_WHEEL_KEY);
    }

    public void setCustomKeyPage(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setCustomKeyPage");
        if (mSettingHelper == null) {
            return;
        }
        if (map.containsKey("switch_type")) {
            String status = (String) getValueInContext(map, "switch_type");
            if (status.equals("close")) {
                DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
                return;
            }
        }
        mSettingHelper.exec(SettingConstants.CUSTOM_STEER_WHEEL_KEY);
    }

    public boolean isValidMode(HashMap<String, Object> map) {
        String mode = getOneMapValue("switch_mode", map);
        String[] switchMode37AList =
                new String[]{"360_park", "arhud", "screen_shift", "selfie", "shout_out", "source_switch", "trip_shoot", "low_speed_alert"};
        String[] switchMode56CHighList =
                new String[]{"source_switch", "arhud", "mute"};
        String[] switchMode56CList =
                new String[]{"source_switch", "mute"};
        int carConfig = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        if (isH56CCar(map)) {
            return Arrays.toString(carConfig == 2 ? switchMode56CList : switchMode56CHighList).contains(mode);
        } else if (isH37ACar(map)) {
            return Arrays.toString(switchMode37AList).contains(mode);
        } else if (isH37BCar(map)) {
            return Arrays.toString(switchMode37AList).contains(mode);
        }
        return false;
    }

    public int getCustomKeyMode(HashMap<String, Object> map) {
        return operator.getIntProp(STEERING_WHEEL_CUSTOM_TYPE);
    }

    public int getOrderCustomKeyMode(HashMap<String, Object> map) {
        return getSystemConfig(map);
    }

    private int getSystemConfig(HashMap<String, Object> map) {
        String str = "";
        int mode = 0;
        if (map.containsKey("switch_mode")) {
            str = (String) getValueInContext(map, "switch_mode");
        }
        switch (str) {
            case "source_switch":
                mode = ISteeringWheel.CustomType.SOURCE_SWITCH;
                break;
            case "trip_shoot":
                mode = ISteeringWheel.CustomType.TRIP_SHOOT;
                break;
            case "selfie":
                mode = ISteeringWheel.CustomType.SELFIE;
                break;
            case "screen_shift":
                mode = ISteeringWheel.CustomType.SCREEN_SHIFT;
                break;
            case "360_park":
                mode = ISteeringWheel.CustomType.PARK_360;
                break;
            case "arhud":
                mode = ISteeringWheel.CustomType.AR_HUD;
                break;
            case "shout_out":
                mode = ISteeringWheel.CustomType.SHOUT_OUT;
                break;
            case "low_speed_alert":
                mode = ISteeringWheel.CustomType.LOW_SPEED_ALERT;
                break;
            case "mute":
                mode = ISteeringWheel.CustomType.MUTE;
                break;
            default:
                break;
        }
        LogUtils.d(TAG, "getSystemConfig: " + mode);
        return mode;
    }

    public void setCustomKeyMode(HashMap<String, Object> map) {
        operator.setIntProp(STEERING_WHEEL_CUSTOM_TYPE, getOrderCustomKeyMode(map));
    }

    /** ======================= 方向盘助力 ================== */

    /**
     * 判断当前是否自定义驾驶模式
     *
     * @param map
     * @return
     */
    public boolean isCustomDrivingMode(HashMap<String, Object> map) {
        return ICarSetting.DrivingMode.CUSTOM == operator.getIntProp(CarSettingSignal.CARSET_DRIVING_MODE);
    }

    public int getCurSteerWheelModeCode(HashMap<String, Object> map) {
        return operator.getIntProp(SteeringWheelSignal.STEERING_WHEEL_DRV_EPS_MODESET);
    }

    public void setSteerWheelMode(HashMap<String, Object> map) {
        operator.setIntProp(SteeringWheelSignal.STEERING_WHEEL_DRV_EPS_MODESET, getExpectedSteerWheelModeCode(map));
    }

    /**
     * 从参数中提取意向方向盘助力模式
     *
     * @param map
     * @return
     */
    public int getExpectedSteerWheelModeCode(HashMap<String, Object> map) {
        //兼容模糊意图
        String expectedMode = (String) getValueInContext(map, "switch_mode");
        if (StringUtils.isBlank(expectedMode) || expectedMode.contains("|")) {
            if (getCurSteerWheelModeCode(map) == ISteeringWheel.EpsMode.COMFORT) {
                map.put("switch_mode", "sport");
                return ISteeringWheel.EpsMode.SPORT;
            } else {
                map.put("switch_mode", "comfortable");
                return ISteeringWheel.EpsMode.COMFORT;
            }
        }


        int epsModeCode;
        if (expectedMode.equals("comfortable")) {
            epsModeCode = ISteeringWheel.EpsMode.COMFORT;
        } else if (expectedMode.equals("sport")) {
            epsModeCode = ISteeringWheel.EpsMode.SPORT;
        } else {
            epsModeCode = ISteeringWheel.EpsMode.INVALID;
        }
        return epsModeCode;
    }

    private String getSteerModeStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String chassisStyle = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(chassisStyle)) {
            switch (chassisStyle) {
                case "comfortable":
                    res = "舒适";
                    break;
                case "sport":
                    res = "运动";
                    break;
                default:
            }
        }
        return res;
    }

    /**
     * ======================= 方向盘加热 ==================
     */

    public boolean isOpenSteeringWheelHeat(HashMap<String, Object> map) {
        return operator.getIntProp(SteeringWheelSignal.STEERING_WHEEL_HEAT) == (isH56DCar(map) ? 2 : 1);
    }

    public void setSteeringWheelHeat(HashMap<String, Object> map) {
        int orderState = 0;
        if (map.containsKey("switch_type")) {
            String str = (String) getValueInContext(map, "switch_type");
            if (isH37BCar(map)) {
                orderState = str.equals("open") ? 1 : 0;
            } else {
                orderState = str.equals("open") ? ISysSetting.IWirelessCharge.WC_ON : ISysSetting.IWirelessCharge.WC_OFF;
            }
        }
        operator.setIntProp(SteeringWheelSignal.STEERING_WHEEL_HEAT, orderState);
    }

    public boolean isSupportHeat(HashMap<String, Object> map) {
        return operator.getBooleanProp(SteeringWheelSignal.STEERING_WHEEL_CONFIG);
    }

}
