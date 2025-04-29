package com.voyah.ai.device.voyah.common.H56Car;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.Lighting;
import mega.car.config.Qnx;

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
                carserviceKey = Qnx.ID_AMB_LIGHT_WELCOME_SWITCH;
                break;
            case 2:
                carserviceKey = Qnx.ID_AMB_LIGHT_VOICE_SWITCH;
                break;
            case 3:
                carserviceKey = Qnx.ID_AMB_LIGHT_OVER_SPEED_SWITCH;
                break;
            case 4:
                carserviceKey = Qnx.ID_AMB_LIGHT_DOW_WARNING_SWITCH;
                break;
            case 5:
                carserviceKey = Qnx.ID_AMB_LIGHT_FATIGUE_DRIVING_SWITCH;
                break;
            case 6:
                carserviceKey = Qnx.ID_AMB_LIGHT_ADAS_STATUS_SWITCH;
                break;
            default:
                carserviceKey = Qnx.ID_AMB_LIGHT_WELCOME_SWITCH;
                break;
        }
        return carserviceKey;
    }

    public static boolean getFollowMusicSwitchState() {
        return CarPropUtils.getInstance().getFloatProp(Qnx.ID_AMB_LIGHT_MUSIC_RHYTHM_SWITCH) == 1;
    }

    public static void setFollowMusicSwitchState(boolean onOff) {
        CarPropUtils.getInstance().setIntProp(Qnx.ID_AMB_LIGHT_MUSIC_RHYTHM_SWITCH, onOff ? 1 : 0);
    }

    public static boolean getAtmospherePageState() {
        return DeviceHolder.INS().getDevices().getSetting().isCurrentState(SettingConstants.NEW_ACTION_AMBIENTLIGHT);
    }

    public static void setAtmospherePageState(boolean value) {
        DeviceHolder.INS().getDevices().getSetting().exec(SettingConstants.NEW_ACTION_AMBIENTLIGHT);
    }
}
