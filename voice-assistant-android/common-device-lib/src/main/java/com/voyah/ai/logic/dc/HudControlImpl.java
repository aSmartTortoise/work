package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.IHud;
import com.voice.sdk.device.carservice.signal.HudSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class HudControlImpl extends AbsDevices {

    private static final String TAG = HudControlImpl.class.getSimpleName();

    public HudControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "hud";
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
            case "number":
                str = str.replace("@{number}", Integer.toString((int) getHudOrdNum(map)));
                break;
            case "switch_mode":
            case "hud_mode":
                String switch_mode = getOneMapValue("switch_mode", map);
                if (key.equals("switch_mode")) {
                    str = str.replace("@{switch_mode}", mergeModeString(map, switch_mode));
                } else {
                    str = str.replace("@{hud_mode}", mergeModeString(map, switch_mode));
                }
                break;
            case "hud_height_min":
                str = str.replace("@{hud_height_min}", "0%");
                break;
            case "hud_height_max":
                str = str.replace("@{hud_height_max}", "100%");
                break;
            case "hud_lux_min":
                str = str.replace("@{hud_lux_min}", "0%");
                break;
            case "hud_lux_max":
                str = str.replace("@{hud_lux_max}", "100%");
                break;
            case "hud_mode_function":
                str = str.replace("@{hud_mode_function}", getOrderDisplayModeStr(map));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.i(TAG, "tts :" + str);
        return str;
    }

    private String mergeModeString(HashMap<String, Object> map, String str) {
        if (TextUtils.isEmpty(str)) {
            return "雪地";
        }
        switch (str) {
            case "ar":
                str = "AR";
                break;
            case "standard":
                str = "标准";
                break;
            case "navi":
                str = "导航";
                break;
            case "intelligent_driving":
                str = "智驾";
                break;
            case "simplest":
                str = "极简";
                break;
            case "snowfield":
            case "high_contrast":
                str = "雪地";
                break;
            default:
                break;
        }
        LogUtils.d(TAG, "mergeModeString: " + str);
        return str;
    }

    /** ----------------------- 显示模式 start--------------------------------------------------*/

    /**
     * 切换hud的显示模式，目前支持ar, intelligent_driving, navi, simplest, standard
     * switch_mode:ar, intelligent_driving, navi, simplest, standard
     *
     * @param map 指令的数据
     */
    public void setHudMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setHudMode");
        operator.setIntProp(HudSignal.HUD_SWITCH, ICommon.Switch.ON);
        operator.setIntProp(HudSignal.HUD_MODE, getHudMode2Int(map));
    }

    /**
     * 判断当前是否就是X模式
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getHudMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getHudMode");
        return operator.getIntProp(HudSignal.HUD_MODE);
    }

    public int getHudMode2Int(HashMap<String, Object> map) {
        String switchMode = getOneMapValue("switch_mode", map);
        LogUtils.d(TAG, "hudMode: " + switchMode);
        int hudMode = 0;
        if (!TextUtils.isEmpty(switchMode)) {
            switch (switchMode) {
                case "simplest":
                    hudMode = IHud.HudMode.SIMPLEST;
                    break;
                case "navi":
                    hudMode = IHud.HudMode.NAVI;
                    break;
                case "intelligent_driving":
                    hudMode = IHud.HudMode.INTELLIGENT_DRIVING;
                    break;
                case "standard":
                    hudMode = IHud.HudMode.STANDARD;
                    break;
                case "ar":
                    hudMode = IHud.HudMode.AR;
                    break;
                default:
                    LogUtils.e(TAG, "hudMode is error");
                    break;
            }
        } else {
            hudMode = getNextHudMode(map);
        }
        LogUtils.d(TAG, "getHudMode2Int: " + hudMode);
        return hudMode;
    }

    private int getNextHudMode(HashMap<String, Object> map) {
        int curMode = getHudMode(map);
        int nextMode = 0;
        switch (curMode) {
            case 1:
                nextMode = 5;
                map.put("switch_mode", "AR");
                break;
            case 2:
                nextMode = 3;
                map.put("switch_mode", "智驾");
                break;
            case 3:
                nextMode = 1;
                map.put("switch_mode", "极简");
                break;
            case 4:
                nextMode = 2;
                map.put("switch_mode", "导航");
                break;
            case 5:
                nextMode = 4;
                map.put("switch_mode", "标准");
                break;
            default:
                LogUtils.e(TAG, "hudMode is error");
                break;
        }
        LogUtils.d(TAG, "getNextHudMode: " + nextMode);
        return nextMode;
    }

    public boolean isHudDisplayModeOpen(HashMap<String, Object> map) {
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            String signalStr = "hud_mode_" + switchMode;
            LogUtils.d(TAG, "signalStr: " + signalStr);
            return operator.getBooleanProp(signalStr);
        }
        return false;
    }

    public void setHudDisplayMode(HashMap<String, Object> map) {
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            String signalStr = "hud_mode_" + switchMode;
            LogUtils.d(TAG, "signalStr: " + signalStr);
            String switchType = getOneMapValue("switch_type", map);
            if (!TextUtils.isEmpty(switchType)) {
                operator.setBooleanProp(signalStr, switchType.equals("open"));
            }
        }
    }

    private String getOrderDisplayModeStr (HashMap<String, Object> map) {
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            switch (switchMode) {
                case "jad":
                    return "HUD路口放大图";
                case "tlc":
                    return "HUD红绿灯倒计时";
                case "ctd":
                    return "HUD当前时间";
                case "msd":
                    return "HUD多媒体信息";
            }
        }
        return "";
    }

    public boolean isDisableHudMode(HashMap<String, Object> map) {
        if (getOrderDisplayModeStr(map).equals("HUD多媒体信息")) {
            return getHudMode(map) != IHud.HudMode.AR;
        }
        return getHudMode(map) != 3 && getHudMode(map) != 1 && getHudMode(map) != 5;
    }

    public void upLoadHudHeightAuto(HashMap<String, Object> map) {
        operator.setIntProp(HudSignal.HUD_SWITCH, ICommon.Switch.ON);
        operator.setIntProp(HudSignal.HUD_AUTO_HEIGHT, 1);
        operator.setIntProp(HudSignal.HUD_HEIGHT_UPLOAD, 1);
    }

    /** ----------------------- 显示模式 end--------------------------------------------------*/

    /**
     * ----------------------- 模式选择 start--------------------------------------------------
     */

    public boolean getIsOrderSnowMode(HashMap<String, Object> map) {
        if (map.containsKey("switch_mode")) {
            String ordSwitchMode = (String) getValueInContext(map, "switch_mode");
            return ordSwitchMode.equals("snowfield");
        }
        return false;
    }

    /**
     * 打开/关闭高对比模式
     * switch_type:open/close
     * switch_mode:high_contrast
     *
     * @param map 指令的数据
     */
    public void setSnowMode(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        LogUtils.d(TAG, "setSnowMode");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setSnowMode error");
            return;
        }
        // hud关闭时，需要打开hud,再开启高对比模式
        if (switchType.equals("open")) {
            operator.setIntProp(HudSignal.HUD_SWITCH, 1);
            operator.setIntProp(HudSignal.HUD_SNOW_MODE_SWITCH, 1);
        }
        if (switchType.equals("close")) {
            operator.setIntProp(HudSignal.HUD_SNOW_MODE_SWITCH, 0);
        }
    }

    /**
     * 判断当前是都是高对比模式
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getSnowMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getSnowMode");
        return operator.getIntProp(HudSignal.HUD_SNOW_MODE_SWITCH);
    }

    /** ----------------------- 模式选择 end--------------------------------------------------*/

    /** ----------------------- 亮度 start--------------------------------------------------*/

    /**
     * 打开/关闭亮度自适应调节
     * switch_type:open/close
     *
     * @param map 指令的数据
     */
    public void setLightAutoMode(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        LogUtils.d(TAG, "setLightAutoMode");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setLightAutoMode error");
            return;
        }
        // hud关闭时，需要打开hud,再开启亮度自适应调节
        if (switchType.equals("open")) {
            operator.setIntProp(HudSignal.HUD_SWITCH, 1);
            operator.setIntProp(HudSignal.HUD_AUTO_LIGHT, 1);
        }
        if (switchType.equals("close")) {
            operator.setIntProp(HudSignal.HUD_AUTO_LIGHT, 0);
        }
    }

    /**
     * 是否打开亮度自适应调节
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getLightAutoMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getLightAutoMode");
        return operator.getIntProp(HudSignal.HUD_AUTO_LIGHT);
    }

    /**
     * 手动设置亮度
     * adjust_type:set/increase/decrease
     * number:0-100
     * level:min/max
     *
     * @param map 指令的数据
     */
    public void setHudLightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setHudLightNum");
        operator.setIntProp(HudSignal.HUD_SWITCH, 1);
        String adjustType = "";
        int number = 0;
        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        number = getHudOrdNum(map);
        int newVal = operator.getIntProp(HudSignal.HUD_MANUAL_LIGHT);
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = 0;
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = 100;
        } else if (adjustType.equals("increase") && number != 0) {
            newVal += number;
        } else if (adjustType.equals("increase")) {
            newVal += 10;
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal -= number;
        } else if (adjustType.equals("decrease")) {
            newVal -= 10;
        } else {
            newVal = number;
        }
        operator.setIntProp(HudSignal.HUD_MANUAL_LIGHT, newVal);
    }

    /**
     * 判断调节亮度是否就是当前亮度
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean isCurLightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isCurLightNum");
        if (map.containsKey("number")) {
            int number = Integer.parseInt((String) getValueInContext(map, "number"));
            LogUtils.d(TAG, "number: " + number);
            return getHudLightNum(map) == number;
        }
        return false;
    }

    /**
     * 获取当前亮度
     *
     * @param map 指令的数据
     * @return 当前亮度
     */
    public int getHudLightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getHudLightNum");
        return operator.getIntProp(HudSignal.HUD_MANUAL_LIGHT);
    }

    /** ----------------------- 亮度 end--------------------------------------------------*/

    /** ----------------------- 高度 start--------------------------------------------------*/

    /**
     * 打开/关闭高度自适应模式
     * switch_type：open/close
     *
     * @param map 指令的数据
     */
    public void setHeightAutoMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setHeightAutoMode");
        String switchType = (String) getValueInContext(map, "switch_type");
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setHeightAutoMode error");
            return;
        }
        // hud关闭时，需要打开hud,再开启高度自适应模式
        if (switchType.equals("open")) {
            operator.setIntProp(HudSignal.HUD_SWITCH, 1);
            operator.setIntProp(HudSignal.HUD_AUTO_HEIGHT, 1);
        }
        if (switchType.equals("close")) {
            operator.setIntProp(HudSignal.HUD_AUTO_HEIGHT, 0);
        }
    }

    /**
     * 是否开启高度自适应模式
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getHeightAutoMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getHeightAutoMode");
        return operator.getIntProp(HudSignal.HUD_AUTO_HEIGHT);
    }

    /**
     * 是否开启隐私保护模式
     *
     * @param map 指令的数据
     * @return true/false
     */
    public int getOpenPrivacyMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getOpenPrivacyMode");
        return operator.getIntProp(HudSignal.HUD_PRIVACY_MODE);
    }

    /**
     * 手动设置高度
     * adjust_type:set/increase/decrease
     * number:0-100
     * level:min/max
     *
     * @param map 指令的数据
     */
    public void setHudHeightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setHudHeightNum");
        operator.setIntProp(HudSignal.HUD_SWITCH, 1);
        String adjustType = "";
        int number = 0;
        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        number = getHudOrdNum(map);
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        int newVal = operator.getIntProp(HudSignal.HUD_MANUAL_HEIGHT);
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = 0;
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = 100;
        } else if (adjustType.equals("increase") && number != 0) {
            newVal += number;
        } else if (adjustType.equals("increase")) {
            newVal += 10;
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal -= number;
        } else if (adjustType.equals("decrease")) {
            newVal -= 10;
        } else {
            newVal = number;
        }
        operator.setIntProp(HudSignal.HUD_MANUAL_HEIGHT, newVal);
    }

    /**
     * 判断调节高度是否是当前高度
     *
     * @param map 指令的数据
     * @return true/false
     */
    public boolean isCurHeightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isCurHeightNum");
        if (map.containsKey("number")) {
            int number = Integer.parseInt((String) getValueInContext(map, "number"));
            LogUtils.d(TAG, "number: " + number);
            return getHudHeightNum(map) == number;
        }
        return false;
    }

    /**
     * 获取当前高度
     *
     * @param map 指令的数据
     * @return 当前高度
     */
    public int getHudHeightNum(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getHudHeightNum");
        return operator.getIntProp(HudSignal.HUD_MANUAL_HEIGHT);
    }

    /** ----------------------- 高度 end--------------------------------------------------*/

    /** ----------------------- 通用函数 start--------------------------------------------------*/

    /**
     * 判断是否HUD硬件温度超过阈值
     *
     * @param map 指令的数据
     * @return true: 温度过高 false: 温度正常
     */
    public int getTemperatureState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getTemperatureState");
        return operator.getIntProp(HudSignal.HUD_TEMPERATURE);
    }

    /**
     * 获取当前HUD的开关状态
     *
     * @param map 指令的数据
     * @return HUD的开关状态
     */
    public int getHudSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getHudSwitch");
        return operator.getIntProp(HudSignal.HUD_SWITCH);
    }

    /**
     * 打开/关闭HUD
     * switch_type:open/close
     *
     * @param map 指令的数据
     */
    public void setHudSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setHudSwitch");
        String arStatus = (String) getValueInContext(map, "switch_type");
        int switchType = 0;
        if (!map.containsKey("switch_type")) {
            LogUtils.e(TAG, "setHudSwitch error");
            return;
        }
        if (arStatus.equals("open")) {
            switchType = 1;
        }
        operator.setIntProp(HudSignal.HUD_SWITCH, switchType);
    }

    public int getHudOrdNum(HashMap<String, Object> map) {
        float number = 0.0f;
        if (map.containsKey("number")) {
            String numberStr = (String) getValueInContext(map, "number");
            if (numberStr.contains("-")) {
                return -1;
            }
            if (numberStr.contains("%")) {
                numberStr = numberStr.replace("%", "");
                if (numberStr.contains(".")) {
                    numberStr = numberStr.replaceAll("\\..*", "");
                }
                if (Integer.parseInt(numberStr) > 100) {
                    return 101;
                }
                number = (Integer.parseInt(numberStr) / 10.0f) * 10;
            } else if (numberStr.contains("/")) {
                String[] parts = numberStr.split("/");
                if (parts.length == 2) {
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    number = ((float) numerator / denominator) * 100;
                    if (number > 100) {
                        return 101;
                    }
                    number = (((int) number) / 10.0f) * 10;
                }
            } else {
                if (numberStr.contains(".")) {
                    numberStr = numberStr.replaceAll("\\..*", "");
                }
                if (Integer.parseInt(numberStr) > 100) {
                    return 101;
                }
                number = (Integer.parseInt(numberStr) / 10.0f) * 10;
            }
            LogUtils.d(TAG, "number: " + number);
        }
        number = (int) Math.ceil((double) number / 10.0f) * 10;
        LogUtils.d(TAG, "getHudOrdNum: " + number);
        return (int) number;
    }

    /**
     * ----------------------- 通用函数 end--------------------------------------------------
     */

    public boolean isSupportSpecialHudMode(HashMap<String, Object> map) {
        return isH56DCar(map);
    }

    public boolean isSupportHudLightAuto(HashMap<String, Object> map) {
        return !isH56DCar(map);
    }

    public boolean isSupportHud(HashMap<String, Object> map) {
        int state = operator.getIntProp(HudSignal.HUD_SUPPORT);
        LogUtils.d(TAG, "isSupportHud: " + state);
        return state == 1;
    }

}
