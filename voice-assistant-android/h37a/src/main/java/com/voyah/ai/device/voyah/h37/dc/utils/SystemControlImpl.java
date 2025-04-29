package com.voyah.ai.device.voyah.h37.dc.utils;

import static com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils.getValueInContext;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.carservice.dc.SystemControlInterface;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mega.car.Signal;
import mega.car.config.Infotainment;

public class SystemControlImpl implements SystemControlInterface {

    @Override
    public String getTtsText(String switch_mode) {
        return getVolumeFeatureNameN3N4().get(switch_mode);
    }

    @Override
    public boolean isSupportMode(String switch_mode) {
        return getVolumeFeatureNameN3N4().containsKey(switch_mode);
    }

    @Override
    public boolean isCurVolumeFeatureMode(String switch_mode) {
        int mode = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 1);
        return mode == getVolumeFeatureModeN3N4().get(switch_mode);
    }

    @Override
    public void setVolumeFeatureMode(String switch_mode) {
        int mode = getVolumeFeatureModeN3N4().get(switch_mode);
        CarPropUtils.getInstance().setIntProp(Signal.ID_AMP_AI_MODE, mode);
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", mode);
    }

    @Override
    public String changeVolumeFeatureMode() {
        int nextMode;
        int mode = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 1);
        nextMode = mode == 8 ? 1 : mode + 1;
        CarPropUtils.getInstance().setIntProp(Signal.ID_AMP_AI_MODE, nextMode);
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", nextMode);
        return getVolumeFeatureKeyN3N4().get(nextMode);
    }

    public Map<String, String> getVolumeFeatureNameN3N4() {
        return new HashMap<String, String>() {
            {
                put("standard","标准");
                put("fashionable","流行");
                put("ballad","民谣");
                put("electronic","电子");
                put("rock","摇滚");
                put("classical","古典");
                put("jazz","爵士");
                put("equalizer","均衡器");
            }
        };
    }

    public Map<String, Integer> getVolumeFeatureModeN3N4() {
        return new HashMap<String, Integer>() {
            {
                put("standard",1);
                put("fashionable",2);
                put("ballad",3);
                put("electronic",4);
                put("rock",5);
                put("classical",6);
                put("jazz",7);
                put("equalizer",8);
            }
        };
    }

    public Map<Integer, String> getVolumeFeatureKeyN3N4() {
        return new HashMap<Integer, String>() {
            {
                put(1,"standard");
                put(2,"fashionable");
                put(3,"ballad");
                put(4,"electronic");
                put(5,"rock");
                put(6,"classical");
                put(7,"jazz");
                put(8,"equalizer");
            }
        };
    }

    @Override
    public boolean isSupportCurVolumeFocusMode(String mode) {
        return getVolumeFocusMode().containsKey(mode);
    }

    @Override
    public boolean isCurVolumeFocusMode(HashMap<String, Object> map) {
        String mode;
        if (map.containsKey("switch_mode")) {
            mode = (String) getValueInContext(map, "switch_mode");
        } else {
            mode = (String) map.get("positions");
        }
        int modeValue = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 0);
        return modeValue == getVolumeFocusMode().get(mode);
    }

    @Override
    public void setVolumeFocusMode(HashMap<String, Object> map) {
        String mode;
        if (map.containsKey("switch_mode")) {
            mode = (String) getValueInContext(map, "switch_mode");
        } else {
            mode = (String) map.get("positions");
        }
        CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_SOUND_FIELD, getVolumeFocusMode().get(mode));
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", getVolumeFocusMode().get(mode));
    }

    @Override
    public void changeVolumeFocusMode(HashMap<String, Object> map) {
        int nextMode = 0;
        int mode = Settings.Global.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 0);
        if (mode == 0) {
            nextMode = 4;
        } else if (mode == 1) {
            nextMode = 2;
        } else if (mode == 2) {
            nextMode = 6;
        } else if (mode == 3) {
            nextMode = 0;
        } else if (mode == 4) {
            nextMode = 1;
        } else if (mode == 6) {
            nextMode = 3;
        }
        if (nextMode == 4) {
            map.put("switch_mode", getVolumeFocusModeValue().get(nextMode));
        } else {
            map.put("positions", getVolumeFocusModeValue().get(nextMode));
        }
        CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_SOUND_FIELD, nextMode);
        Settings.Global.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", nextMode);
    }

    public Map<Integer, String> getVolumeFocusModeValue() {
        return new HashMap<Integer, String>() {
            {
                put(1,"first_row_left");
                put(2,"first_row_right");
                put(6,"front_side");
                put(3,"rear_side");
                put(0,"total_car");
                put(4,"custom");
            }
        };
    }

    public Map<String, Integer> getVolumeFocusMode() {
        return new HashMap<String, Integer>() {
            {
                put("first_row_left",1);
                put("first_row_right",2);
                put("front_side",6);
                put("rear_side",3);
                put("total_car",0);
                put("custom",4);
            }
        };
    }
}
