package com.voyah.ai.device.voyah.common.H37Car;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.device.voyah.h37.utils.CarPropUtils;

import mega.car.Signal;

public class AtmosphereHelper {

    public static boolean getEffectModeState(int mode) {
        int carserviceKey = getCarserviceKey(mode);
        return CarPropUtils.getInstance().getIntProp(carserviceKey) == 1;
    }

    public static void setEffectModeState(int mode, boolean onOff) {
        int carserviceKey = getCarserviceKey(mode);
        CarPropUtils.getInstance().setIntProp(carserviceKey, onOff ? 1 : 0);
    }

    public static int getCarserviceKey(int mode) {
        int carserviceKey;
        switch (mode) {
            case 1:
                carserviceKey = Signal.ID_AMB_LIGHT_WELCOME_SWITCH;
                break;
            case 2:
                carserviceKey = Signal.ID_AMB_LIGHT_VOICE_SWITCH;
                break;
            case 3:
                carserviceKey = Signal.ID_AMB_LIGHT_OVER_SPEED_SWITCH;
                break;
            case 4:
                carserviceKey = Signal.ID_AMB_LIGHT_DOW_WARNING_SWITCH;
                break;
            case 5:
                carserviceKey = Signal.ID_AMB_LIGHT_FATIGUE_DRIVING_SWITCH;
                break;
            case 6:
                carserviceKey = Signal.ID_AMB_LIGHT_ADAS_STATUS_SWITCH;
                break;
            default:
                carserviceKey = Signal.ID_AMB_LIGHT_WELCOME_SWITCH;
                break;
        }
        return carserviceKey;
    }

    public static boolean getFollowMusicSwitchState() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_AMB_LIGHT_MUSIC_RHYTHM_SWITCH) == 1;
    }

    public static void setFollowMusicSwitchState(boolean onOff) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_AMB_LIGHT_MUSIC_RHYTHM_SWITCH, onOff ? 1 : 0);
    }

    public static boolean getCenterSpeakerSwitchState() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_AMB_LIGHT_IP_SWITCH) == 1;
    }

    public static void setCenterSpeakerSwitchState(boolean onOff) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_AMB_LIGHT_IP_SWITCH, onOff ? 1 : 0);
    }


    public static boolean getAtmospherePageState() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return DeviceHolder.INS().getDevices().getSetting().isCurrentState(SettingConstants.AMBIENT_LIGHT_PAGE);
        } else {
            return DeviceHolder.INS().getDevices().getSetting().isCurrentState(SettingConstants.NEW_ACTION_AMBIENTLIGHT);
        }
    }

    public static void setAtmospherePageState(boolean value) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            DeviceHolder.INS().getDevices().getSetting().exec(SettingConstants.AMBIENT_LIGHT_PAGE);
        } else {
            DeviceHolder.INS().getDevices().getSetting().exec(SettingConstants.NEW_ACTION_AMBIENTLIGHT);
        }
    }
}
