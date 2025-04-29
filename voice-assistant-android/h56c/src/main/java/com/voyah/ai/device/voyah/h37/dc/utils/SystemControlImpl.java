package com.voyah.ai.device.voyah.h37.dc.utils;

import static com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils.getValueInContext;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.dc.SystemControlInterface;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mega.car.config.H56C;
import mega.car.config.Infotainment;

public class SystemControlImpl implements SystemControlInterface {

    @Override
    public String getTtsText(String switch_mode) {
        String str;
        if (isHighLevelCar()) {
            str = getVolumeFeatureNameN3N4().get(switch_mode);
        } else {
            str = getVolumeFeatureNameN2().get(switch_mode);
        }
        return str;
    }

    @Override
    public boolean isSupportMode(String switch_mode) {
        if (isHighLevelCar()) {
            //N3N4配置
            return getVolumeFeatureNameN3N4().containsKey(switch_mode);
        } else {
            //N2
            return getVolumeFeatureNameN2().containsKey(switch_mode);
        }
    }

    @Override
    public boolean isCurVolumeFeatureMode(String switch_mode) {
        if (isHighLevelCar()) {
            //N3N4模式调节从1开始，所以默认值=1
            int mode = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 1);
            return mode == getVolumeFeatureModeN3N4().get(switch_mode);
        } else {
            //N2模式调节从0开始，所以默认值=0
            int mode = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 0);
            int musicStyleSwitch = Settings.Global.getInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 0);
            if (switch_mode.equals("ai")) {
                return musicStyleSwitch == 1;
            } else {
                if (musicStyleSwitch == 1) {
                    return false;
                }
                return mode == getVolumeFeatureModeN2().get(switch_mode);
            }
        }
    }

    @Override
    public void setVolumeFeatureMode(String switch_mode) {
        if (isHighLevelCar()) {
            //N3N4配置
            int mode = getVolumeFeatureModeN3N4().get(switch_mode);
            CarPropUtils.getInstance().setIntProp(H56C.IVI_infoSet1_IVI_AI_MODESET, mode);
            Settings.System.putInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", mode);
        } else {
            int mode = getVolumeFeatureModeN2().get(switch_mode);
            if (mode == 5) {
                Settings.Global.putInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 1);
            } else {
                int musicStyleSwitch = Settings.Global.getInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 0);
                if (musicStyleSwitch == 1) {
                    Settings.Global.putInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 0);
                }
                Settings.System.putInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", mode);
                Utils.getApp().getContentResolver().notifyChange(Settings.System.getUriFor("key_characteristic_sound"), null);
                CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_DYNAMICAUDIO_MODE, mode);
            }
        }
    }

    @Override
    public String changeVolumeFeatureMode() {
        int nextMode;
        boolean isHighLevelCar = isHighLevelCar();
        int mode;
        if (isHighLevelCar) {
            mode = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 1);
            nextMode = mode == 8 ? 1 : mode + 1;
        } else {
            mode = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", 0);
            int musicStyleSwitch = Settings.Global.getInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 0);
            if (musicStyleSwitch == 1) {
                //AI之后是均衡器
                nextMode = 4;
                Settings.Global.putInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 0);
            } else {
                //如果当前是轻柔，下一个直接设置成AI，并直接return
                if (mode == 3) {
                    Settings.Global.putInt(Utils.getApp().getContentResolver(), "MUSIC_STYLE_SWITCH", 1);
                    return "ai";
                }
                nextMode = mode == 4 ? 0 : mode + 1;
            }
        }
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_characteristic_sound", nextMode);
        if (isHighLevelCar) {
            //N3N4配置
            CarPropUtils.getInstance().setIntProp(H56C.IVI_infoSet1_IVI_AI_MODESET, nextMode);
            return getVolumeFeatureKeyN3N4().get(nextMode);
        } else {
            //N2
            CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_DYNAMICAUDIO_MODE, nextMode);
            Utils.getApp().getContentResolver().notifyChange(Settings.System.getUriFor("key_characteristic_sound"), null);
            return getVolumeFeatureKeyN2().get(nextMode);
        }
    }

    public Map<String, String> getVolumeFeatureNameN2() {
        return new HashMap<String, String>() {
            {
                put("original","原声");
                put("rock","摇滚");
                put("classical","古典");
                put("soft","轻柔");
                put("ai","AI");
                put("equalizer","均衡器");
            }
        };
    }

    public Map<String, Integer> getVolumeFeatureModeN2() {
        return new HashMap<String, Integer>() {
            {
                put("original",0);
                put("rock",1);
                put("classical",2);
                put("soft",3);
                put("ai",5);
                put("equalizer",4);
            }
        };
    }

    public Map<Integer, String> getVolumeFeatureKeyN2() {
        return new HashMap<Integer, String>() {
            {
                put(0,"original");
                put(1,"rock");
                put(2,"classical");
                put(3,"soft");
                put(5,"ai");
                put(4,"equalizer");
            }
        };
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

    public boolean isHighLevelCar() {
        //是否是高配车
        int mEquipmentLevelConfig = MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
        return mEquipmentLevelConfig == 3 || mEquipmentLevelConfig == 4;
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
        int modeValue = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 1);
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
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", getVolumeFocusMode().get(mode));
    }

    @Override
    public void changeVolumeFocusMode(HashMap<String, Object> map) {
        int nextMode = 1;
        int mode = Settings.System.getInt(Utils.getApp().getContentResolver(), "key_focus_sound", 1);
        if (mode == 1) {
            nextMode = 5;
        } else if (mode == 2) {
            nextMode = 3;
        } else if (mode == 3) {
            nextMode = 4;
        } else if (mode == 4) {
            nextMode = 1;
        } else if (mode == 5) {
            nextMode = 2;
        }
        if (nextMode == 5) {
            map.put("switch_mode", getVolumeFocusModeValue().get(nextMode));
        } else {
            map.put("positions", getVolumeFocusModeValue().get(nextMode));
        }
        CarPropUtils.getInstance().setIntProp(Infotainment.ID_AMP_SOUND_FIELD, nextMode);
        Settings.System.putInt(Utils.getApp().getContentResolver(), "key_focus_sound", nextMode);
    }

    public Map<Integer, String> getVolumeFocusModeValue() {
        return new HashMap<Integer, String>() {
            {
                put(2,"first_row_left");
                put(3,"first_row_right");
                put(4,"rear_side");
                put(1,"total_car");
                put(5,"custom");
            }
        };
    }

    public Map<String, Integer> getVolumeFocusMode() {
        return new HashMap<String, Integer>() {
            {
                put("first_row_left",2);
                put("first_row_right",3);
                put("rear_side",4);
                put("total_car",1);
                put("custom",5);
            }
        };
    }
}
