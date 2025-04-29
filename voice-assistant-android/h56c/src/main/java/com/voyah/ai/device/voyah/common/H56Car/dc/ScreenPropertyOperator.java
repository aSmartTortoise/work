package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.signal.HudSignal;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.utils.ScreenUtils;
import com.voyah.ai.device.voyah.common.H56Car.ScreenHelper;
import com.voyah.ai.basecar.utils.HudUtils;

import mega.car.config.H56C;
import mega.car.config.Qnx;


/**
 * @Date 2024/7/26 10:30
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class ScreenPropertyOperator extends Base56Operator {
    @Override
    void init() {
//        map.put(ScreenSignal.SCREEN_POS, Signal.ID_SMCPOSITION);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case ScreenSignal.SCREEN_BRIGHTNESS:
                return ScreenUtils.getInstance().getCentralBrightInitValue();
            case ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS:
                return ScreenUtils.getInstance().getMeterBrightInitValue();
            case ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS:
                return ScreenUtils.getInstance().getPassengerBrightInitValue();
            case ScreenSignal.SCREEN_CEILING_BRIGHTNESS:
                return ScreenUtils.getInstance().getCeilingBrightInitValue();
            case ScreenSignal.SCREEN_ON_OFF:
                return ScreenUtils.getInstance().getScreenOnOff(area);
            case ScreenSignal.SCREEN_TYPE:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_CSD_EQUIPMENT, 9);
            case ScreenSignal.SCREEN_LOCK_CONFIG:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_SECONDROW_DISPLAY_EQUIPMENT, -1);
            case ScreenSignal.SCREEN_LOCK_RL:
                return CarServicePropUtils.getInstance().getIntProp(H56C.ACP_RL_IVI_ACP_RL_LOCKSTA);
            case ScreenSignal.SCREEN_LOCK_RR:
                return CarServicePropUtils.getInstance().getIntProp(H56C.ACP_RR_IVI_ACP_RR_LOCKSTA);
            case ScreenSignal.SCREEN_CEILING_ANGLE:
                return CarPropUtils.getInstance().getIntProp(Qnx.ID_CEILING_ROTATION_LOCATION);
            case ScreenSignal.SCREEN_CENTER_AUTO:
                return DeviceHolder.INS().getDevices().getCarService().getSystemSetting().getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "screen_brightness_mode", 0);
            case ScreenSignal.SCREEN_PASSENGER_AUTO:
                return DeviceHolder.INS().getDevices().getCarService().getSystemSetting().getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "psg_screen_brightness_mode", 0);
            case ScreenSignal.SCREEN_INSTRUMENT_AUTO:
                return DeviceHolder.INS().getDevices().getCarService().getSystemSetting().getInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "inst_screen_brightness_mode", 0);

            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case ScreenSignal.SCREEN_POS:
                ScreenHelper.setScreenPos(value);
                break;
            case ScreenSignal.SCREEN_BRIGHTNESS:
                ScreenUtils.getInstance().setCentralBrightnessLevel(value);
                break;
            case ScreenSignal.SCREEN_INSTRUMENT_BRIGHTNESS:
                ScreenUtils.getInstance().setMeterBrightnessLevel(value);
                break;
            case ScreenSignal.SCREEN_PASSENGER_BRIGHTNESS:
                ScreenUtils.getInstance().setPassengerBrightnessLevel(value);
                break;
            case ScreenSignal.SCREEN_CEILING_BRIGHTNESS:
                ScreenUtils.getInstance().setCeilingBrightnessLevel(value);
                break;
            case ScreenSignal.SCREEN_ON_OFF:
                ScreenUtils.getInstance().controlScreenBright(value == ICommon.Switch.ON, area);
                break;
            case ScreenSignal.SCREEN_LOCK_STATE:
                CarServicePropUtils.getInstance().setIntProp(H56C.IVI_chassisSet2_IVI_ACPLRLOCK, value);
                break;
            case ScreenSignal.SCREEN_CEILING_ANGLE:
                CarPropUtils.getInstance().setIntProp(Qnx.ID_CEILING_ROTATION_ANGLE, value);
                break;
            case ScreenSignal.SCREEN_CENTER_AUTO:
                DeviceHolder.INS().getDevices().getCarService().getSystemSetting().putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "screen_brightness_mode", value);
                break;
            case ScreenSignal.SCREEN_PASSENGER_AUTO:
                DeviceHolder.INS().getDevices().getCarService().getSystemSetting().putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "psg_screen_brightness_mode", value);
                break;
            case ScreenSignal.SCREEN_INSTRUMENT_AUTO:
                DeviceHolder.INS().getDevices().getCarService().getSystemSetting().putInt(ISysSetting.SystemSettingType.SETTING_SYSTEM, "inst_screen_brightness_mode", value);
                break;
            default:
                setCommonInt(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case ScreenSignal.SCREEN_MOVE_STATE:
                return ScreenHelper.isMoving();
            case ScreenSignal.SCREEN_ABNORMAL_STATE:
                return ScreenHelper.isAbnormalState();
            case HudSignal.HUD_SWITCH:
                return HudUtils.getInstance().getArHudSwitch() == ICommon.Switch.ON;
            case ScreenSignal.SCREEN_POWER_OFF_RESET:
                return ScreenHelper.getPowerOffResetState();
            case ScreenSignal.SCREEN_CLEAN_MODE_SWITCH:
                return ScreenHelper.getCleanMode();
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        switch (key) {
            case ScreenSignal.SCREEN_MOVE_STATE: //不支持写
            case ScreenSignal.SCREEN_ABNORMAL_STATE: //不支持写
                return;
            case ScreenSignal.SCREEN_POWER_OFF_RESET:
                ScreenHelper.setPowerOffState(value);
                break;
            default:
                setCommonBoolean(key, area, value);
        }
    }
}
