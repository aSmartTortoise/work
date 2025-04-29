package com.voyah.ai.logic.dc;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.IAtmosphere;
import com.voice.sdk.device.carservice.dc.AtmoInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.AtmosphereSignal;
import com.voice.sdk.util.LogUtils;
import com.voice.sdk.util.ThreadPoolUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class AtmosphereControlImpl extends AbsDevices {

    private static final String TAG = AtmosphereControlImpl.class.getSimpleName();
    private final AtmoInterface ambLightsImpl;

    public AtmosphereControlImpl() {
        super();
        ambLightsImpl = DeviceHolder.INS().getDevices().getCarService().getAtmo();
    }

    @Override
    public String getDomain() {
        return "atmosphere";
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
            case "alm_colour":
                String color = (String) getValueInContext(map, "color");
                if (color.contains("色")) {
                    color = color.replace("色", "");
                }
                str = str.replace("@{alm_colour}", color);
                break;
            case "atmosphere_type":
                String atmosphereType = (String) getValueInContext(map, "atmosphere_type");
                str = str.replace("@{atmosphere_type}", getModeName().get(atmosphereType));
                break;
            case "alm_theme":
                String alm_theme = (String) getValueInContext(map, "atmosphere_type");
                str = str.replace("@{alm_theme}", getModeName().get(alm_theme));
                break;
            case "theme_type":
                String themeType = (String) getValueInContext(map, "theme_type");
                str = str.replace("@{theme_type}", themeType);
                break;
            case "number_level":
                String number_level = curSetBrightness(map) + "";
                str = str.replace("@{number_level}", number_level);
                break;
            case "number":
                String number = curSetBrightness(map) + "";
                str = str.replace("@{number}", number);
                break;
            case "atmosphere_mode":
                String atmosphereMode = (String) getValueInContext(map, "atmosphere_mode");
                str = str.replace("@{atmosphere_mode}", getModeName().get(atmosphereMode));
                break;
            case "alm_lux_min":
                str = str.replace("@{alm_lux_min}", "10%");
                break;
            case "alm_lux_max":
                str = str.replace("@{alm_lux_max}", "100%");
                break;
            case "alm_lux_num":
                String num = curSetBrightness(map) + "";
                str = str.replace("@{alm_lux_num}", num);
                break;
            case "alm_selection":
                String mode = (String) getValueInContext(map, "atmosphere_mode");
                str = str.replace("@{alm_selection}", getModeName().get(mode));
                break;
            case "app_name":
                str = str.replace("@{app_name}", "设置应用");
                break;
            case "alm_mode":
                String alm_mode = (String) getValueInContext(map, "atmosphere_type");
                str = str.replace("@{alm_mode}", getModeName().get(alm_mode));
                break;
            case "alm_effect":
                String switch_mode = (String) getValueInContext(map, "switch_mode");
                str = str.replace("@{alm_effect}", getEffectModeName().get(switch_mode));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.i(TAG, "tts :" + str);
        return str;
    }


    public boolean isHasAtmosphere(HashMap<String, Object> map) {
        return operator.getIntProp(AtmosphereSignal.ATMO_CONFIG) > 0;
    }


    // 氛围灯开关

    public boolean getAtmosphereStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(AtmosphereSignal.ATMO_SWITCH_STATE);
    }

    public void setAtmosphereStatus(HashMap<String, Object> map) {
        String status = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AtmosphereSignal.ATMO_SWITCH_STATE, "open".equals(status));
    }

    public void openAtmosphereStatus(HashMap<String, Object> map) {
        operator.setBooleanProp(AtmosphereSignal.ATMO_SWITCH_STATE, true);
    }


    // 单色

    private boolean isCurrentStatic() {
        return operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
    }

    public boolean isSupportSingleColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportSingleColorMode();
    }

    public boolean isSupportStaticSingleColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportStaticSingleColorMode();
    }

    public boolean isSupportFollowMusicSingleColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportFollowMusicSingleColorMode();
    }

    public boolean isCurSingleColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurSingleColorModeOpened(operator);
    }

    public void setSingleColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setSingleColorMode(operator);
    }

    public boolean isCurStaticSingleColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurStaticSingleColorModeOpened(operator);
    }

    public void setStaticSingleColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setStaticSingleColorMode(operator);
    }

    public boolean isCurFollowMusicSingleColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurFollowMusicSingleColorModeOpened(operator);
    }

    public void setFollowMusicSingleColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setFollowMusicSingleColorMode(operator);
    }


    // 多色

    public boolean isSupportMultiColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportMultiColorMode();
    }

    public boolean isSupportStaticMultiColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportStaticMultiColorMode();
    }

    public boolean isSupportFollowMusicMultiColorMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportFollowMusicMultiColorMode();
    }

    public boolean isCurMultiColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurMultiColorModeOpened(operator);
    }

    public void setMultiColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setMultiColorMode(operator);
    }

    public boolean isCurStaticMultiColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurStaticMultiColorModeOpened(operator);
    }

    public void setStaticMultiColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setStaticMultiColorMode(operator);
    }

    public boolean isCurFollowMusicMultiColorModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isCurFollowMusicMultiColorModeOpened(operator);
    }

    public void setFollowMusicMultiColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setFollowMusicMultiColorMode(operator);
    }


    // 氛围灯呼吸模式

    public boolean isSupportAtmosphereBreatheMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereBreatheMode();
    }

    public boolean isAtmosphereBreatheModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereBreatheModeOpened(operator);
    }

    public void setAtmosphereBreatheMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereBreatheMode(operator);
    }


    // 氛围灯常亮模式

    public boolean isSupportAtmosphereConstantLightMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereConstantLighMode();
    }

    public boolean isAtmosphereConstantLighModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereConstantLighModeOpened(operator);
    }

    public void setAtmosphereConstantLighMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereConstantLighMode(operator);
    }


    // 氛围灯流动模式

    public boolean isSupportAtmosphereFlowMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereFlowMode();
    }

    public boolean isAtmosphereFlowModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereFlowModeOpened(operator);
    }

    public void setAtmosphereFlowMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereFlowMode(operator);
    }


    // 氛围灯时光模式

    public boolean isSupportAtmosphereTimeMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereTimeMode();
    }

    public boolean isAtmosphereTimeModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereTimeModeOpened(operator);
    }

    public void setAtmosphereTimeMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereTimeMode(operator);
    }


    // 氛围灯动态反馈模式

    public boolean isSupportAtmosphereDynamicFeedbackMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereDynamicFeedbackMode();
    }

    public boolean isAtmosphereDynamicFeedbackModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereDynamicFeedbackModeOpened(operator);
    }

    public void setAtmosphereDynamicFeedbackMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereDynamicFeedbackMode(operator);
    }


    // 氛围灯动态模式

    public boolean isSupportAtmosphereDynamicMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereDynamicMode();
    }

    public boolean isAtmosphereDynamicModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereDynamicModeOpened(operator);
    }

    public void setAtmosphereDynamicMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereDynamicMode(operator);
    }


    // 氛围灯跟随驾驶模式

    public boolean isSupportAtmosphereFollowDriverMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereFollowDriverMode();
    }

    public boolean isAtmosphereFollowDriverModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereFollowDriverModeOpened(operator);
    }

    public void setAtmosphereFollowDriverMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereFollowDriverMode(operator);
    }


    // 氛围灯静态模式

    public boolean isSupportAtmosphereStaticMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereStaticMode();
    }

    public boolean isAtmosphereStaticModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereStaticModeOpened(operator);
    }

    public void setAtmosphereStaticMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereStaticMode(operator);
    }


    // 氛围灯主题模式

    public boolean isSupportAtmosphereCustomMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereCustomMode();
    }

    public boolean isAtmosphereCustomModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereCustomModeOpened(operator);
    }

    public void setAtmosphereCustomMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCustomMode(operator);
    }

    // 氛围灯自定义模式

    public boolean isSupportAtmosphereThemeMode(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereThemeMode();
    }

    public boolean isAtmosphereThemeModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereThemeModeOpened(operator);
    }

    public void setAtmosphereThemeMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereThemeMode(operator);
    }


    // 中置扬声器氛围灯开关

    public boolean isSupportAtmosphereCenterSpeakerSwitch(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereCenterSpeakerSwitch();
    }

    public boolean isAtmosphereCenterSpeakerSwitchOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereCenterSpeakerSwitchOpened(operator);
    }

    public void setAtmosphereCenterSpeakerSwitchState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        ambLightsImpl.setAtmosphereCenterSpeakerSwitchState(operator, switch_type.equals("open"));
    }


    // 音乐律动开关 and 模式

    public boolean isSupportAtmosphereFollowMusicModeOrSwitch(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereFollowMusicModeOrSwitch();
    }

    public boolean isAtmosphereFollowMusicModeAdjust(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereFollowMusicModeAdjust();
    }

    public boolean isAtmosphereFollowMusicModeOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereFollowMusicModeOpened(operator);
    }

    public void setAtmosphereFollowMusicModeState(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereFollowMusicModeState(operator);
    }

    public boolean isAtmosphereFollowMusicSwitchOpened(HashMap<String, Object> map) {
        return ambLightsImpl.isAtmosphereFollowMusicSwitchOpened(operator);
    }

    public void setAtmosphereFollowMusicSwitchState(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        ambLightsImpl.setAtmosphereFollowMusicSwitchState(operator, switch_type.equals("open"));
    }


    // 氛围灯模式模糊调节

    public void setAtmosphereModeChange(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereModeChange(operator, map);
    }


    // 氛围灯颜色模糊调节

    public void setAtmosphereColorChange(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereColorChange(operator, map);
    }


    // 静态氛围灯颜色模糊调节

    public void setAtmosphereStaticColorChange(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereStaticColorChange(operator, map);
    }


    // 音乐律动氛围灯颜色模糊调节

    public void setAtmosphereFollowMusicColorChange(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereFollowMusicColorChange(operator, map);
    }


    // 动态氛围灯颜色模糊调节

    public void setAtmosphereDynamicColorChange(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereDynamicColorChange(operator, map);
    }

    public boolean isAtmosphereColorScheme(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereColorScheme(color);
    }

    public boolean isSupportCurColor(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isSupportCurColor(color);
    }

    public boolean isAtmosphereCurColorOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurColorOpened(operator, color);
    }

    public boolean isAtmosphereCurColorSchemeOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurColorSchemeOpened(operator, color);
    }

    public void setAtmosphereCurColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurColorMode(operator);
    }

    public void setAtmosphereCurColorSchemeMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurColorSchemeMode(operator);
    }

    public void setAtmosphereColor(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereColor(operator, color);
    }

    public void setAtmosphereColorScheme(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereColorScheme(operator, color);
    }

    public boolean isAtmosphereCurStaticColorOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurStaticColorOpened(operator, color);
    }

    public boolean isAtmosphereCurStaticColorSchemeOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurStaticColorSchemeOpened(operator, color);
    }

    public void setAtmosphereCurStaticColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurStaticColorMode(operator);
    }

    public void setAtmosphereCurStaticColorSchemeMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurStaticColorSchemeMode(operator);
    }

    public void setAtmosphereStaticColor(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereStaticColor(operator, color);
    }

    public void setAtmosphereStaticColorScheme(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereStaticColorScheme(operator, color);
    }

    public boolean isAtmosphereCurFollowMusicColorOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurFollowMusicColorOpened(operator, color);
    }

    public boolean isAtmosphereCurFollowMusicColorSchemeOpened(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        return ambLightsImpl.isAtmosphereCurFollowMusicColorSchemeOpened(operator, color);
    }

    public void setAtmosphereCurFollowMusicColorMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurFollowMusicColorMode(operator);
    }

    public void setAtmosphereCurFollowMusicColorSchemeMode(HashMap<String, Object> map) {
        ambLightsImpl.setAtmosphereCurFollowMusicColorSchemeMode(operator);
    }

    public void setAtmosphereFollowMusicColor(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereFollowMusicColor(operator, color);
    }

    public void setAtmosphereFollowMusicColorScheme(HashMap<String, Object> map) {
        String color = (String) getValueInContext(map, "color");
        ambLightsImpl.setAtmosphereFollowMusicColorScheme(operator, color);
    }

    public boolean isSupportStaticColorScheme(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportStaticColorScheme();
    }

    public boolean isSupportFollowMusicColor(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportFollowMusicColor();
    }

    public boolean isSupportColorSchemeAdjust(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportColorSchemeAdjust();
    }

    public boolean isSupportFollowMusicBrightnessAdjust(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportFollowMusicBrightnessAdjust();
    }

    public boolean isSupportStaticBrightnessAdjust(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportStaticBrightnessAdjust();
    }

    public int curAtmosphereBrightness(HashMap<String, Object> map) {
        String atmosphereMode = "";
        if (map.containsKey("atmosphere_type")) {
            atmosphereMode = (String) getValueInContext(map, "atmosphere_type");
        }
        return ambLightsImpl.curAtmosphereBrightness(operator, atmosphereMode);
    }

    public void setAtmosphereBrightness(HashMap<String, Object> map) {
        int number = 1;
        LogUtils.i(TAG, "setAtmosphereBrightness");
        String adjustType = (String) getValueInContext(map, "adjust_type");
        if (adjustType.equals("increase")) {
            if (map.containsKey("number_level") || map.containsKey("number")) {
                number = Math.min(curAtmosphereBrightness(map) + curSetBrightness(map), 10);
            } else {
                number = curAtmosphereBrightness(map) + 1;
            }
        } else if (adjustType.equals("decrease")) {
            if (map.containsKey("number_level") || map.containsKey("number")) {
                number = Math.max(curAtmosphereBrightness(map) - curSetBrightness(map), 1);
            } else {
                number = curAtmosphereBrightness(map) - 1;
            }
        } else if (adjustType.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    number = 10;
                } else if (level.equals("min")) {
                    number = 1;
                }
            } else {
                number = curSetBrightness(map);
            }
        }
        String atmosphereMode = "";
        if (map.containsKey("atmosphere_type")) {
            atmosphereMode = (String) getValueInContext(map, "atmosphere_type");
        }
        ambLightsImpl.setAtmosphereBrightness(operator, atmosphereMode, number);
    }

    public void openAtmosphereStaticBrightnessTab(HashMap<String, Object> map) {
        if (map.containsKey("atmosphere_type")) {
            ambLightsImpl.openAtmosphereStaticBrightnessTab(operator);
        }
    }

    public void openAtmosphereFollowMusicBrightnessTab(HashMap<String, Object> map) {
        if (map.containsKey("atmosphere_type")) {
            ambLightsImpl.openAtmosphereFollowMusicBrightnessTab(operator);
        }
    }

    public void setAtmosphereBrightnessMin(HashMap<String, Object> map) {
        String atmosphereMode = "";
        if (map.containsKey("atmosphere_type")) {
            atmosphereMode = (String) getValueInContext(map, "atmosphere_type");
        }
        ambLightsImpl.setAtmosphereBrightnessMin(operator, atmosphereMode);
    }

    public void setAtmosphereBrightnessMax(HashMap<String, Object> map) {
        String atmosphereMode = "";
        if (map.containsKey("atmosphere_type")) {
            atmosphereMode = (String) getValueInContext(map, "atmosphere_type");
        }
        ambLightsImpl.setAtmosphereBrightnessMax(operator, atmosphereMode);
    }

    private void setStaticBrightness(int brightness) {
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, brightness);
    }


    private void setDynamicBrightness(int brightness) {
        operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, brightness);
    }

    public Map<String, Integer> getModeData() {
        return new HashMap<String, Integer>() {
            {
                put("static", IAtmosphere.Mode.MODE_STATIC);
                put("dynamic", IAtmosphere.Mode.MODE_DYNAMIC);
                put("follow_music", IAtmosphere.Mode.MODE_DYNAMIC);
                put("follow_driver", IAtmosphere.Mode.MODE_DRIVE);
                put("dj", IAtmosphere.Mode.MODE_DJ);
                put("single_color", IAtmosphere.Mode.MODE_SINGLE_COLOR);
                put("multi_color", IAtmosphere.Mode.MODE_DYNAMIC);
                put("dynamic_feedback", IAtmosphere.Mode.MODE_STATIC);
                put("theme", IAtmosphere.Mode.MODE_THEME);
                put("constant_light", IAtmosphere.Mode.MODE_STATIC);
                put("breathe", IAtmosphere.Mode.MODE_BREATHE);
                put("flow", IAtmosphere.Mode.MODE_FLOW);
                put("time", IAtmosphere.Mode.MODE_TIME);
                put("custom_color", IAtmosphere.Mode.MODE_STATIC);
            }
        };
    }

    public Map<String, String> getModeName() {
        return new HashMap<String, String>() {
            {
                put("static", "静态");
                put("dynamic", "动态");
                put("follow_music", "音乐律动");
                put("follow_driver", "跟随驾驶");
                put("dj", "DJ律动模式");
                put("single_color", "单色");
                put("multi_color", "多色");
                put("dynamic_feedback", "动态反馈");
                put("theme", "主题");
                put("constant_light", "常亮");
                put("breathe", "呼吸");
                put("flow", "流动");
                put("time", "时光");
                put("custom_color", "自定义");
            }
        };
    }

    public boolean isSupportAtmosphereThemeColor(HashMap<String, Object> map) {
        return ambLightsImpl.isSupportAtmosphereThemeColor();
    }

    public boolean isSupportTheme(HashMap<String, Object> map) {
        String themeColor = (String) getValueInContext(map, "theme_type");
        return ambLightsImpl.isSupportCurSetThemeColor(themeColor);
    }

    public boolean isCurThemeColorOpened(HashMap<String, Object> map) {
        String themeColor = (String) getValueInContext(map, "theme_type");
        return ambLightsImpl.isCurThemeColorOpened(operator, themeColor);
    }

    public void setAtmosphereTheme(HashMap<String, Object> map) {
        String themeColor = (String) getValueInContext(map, "theme_type");
        ambLightsImpl.setAtmosphereThemeColor(operator, themeColor);
    }

    public void setRandomAtmosphereTheme(HashMap<String, Object> map) {
        String atmosphereThemeColorName = ambLightsImpl.setRandomAtmosphereThemeColor(operator);
        map.put("theme_type", atmosphereThemeColorName);
    }

    public boolean isNumberKey(HashMap<String, Object> map) {
        return map.containsKey("number");
    }

    public boolean isNumberOrNumberLevelKey(HashMap<String, Object> map) {
        return map.containsKey("number") || map.containsKey("number_level");
    }

    public boolean isBrightnessMin(HashMap<String, Object> map) {
        int curNumber = curSetBrightness(map);
        return curNumber < 1;
    }

    public boolean isBrightnessMax(HashMap<String, Object> map) {
        int curNumber = curSetBrightness(map);
        return curNumber > 10;
    }

    public int curSetBrightness(HashMap<String, Object> map) {
        if (map.containsKey("number_level")) {
            String number_level = (String) getValueInContext(map, "number_level");
            if (number_level.contains("挡")) {
                number_level = number_level.replace("挡", "");
            }
            double curNumber = Double.parseDouble(number_level);
            if (map.containsKey("adjust_type")) {
                String adjustType = (String) getValueInContext(map, "adjust_type");
                if (adjustType.equals("set")) {
                    if (curNumber < 1) {
                        LogUtils.i(TAG, "curSetBrightness curNumber:" + curNumber);
                        return (int) curNumber;
                    }
                }
            }
            double roundedUp = Math.ceil(curNumber); // 向上取整
            int curSetNumber = (int) roundedUp; // 转换为int
            LogUtils.i(TAG, "curSetBrightness curSetNumber:" + curSetNumber);
            return curSetNumber;
        } else if (map.containsKey("number")) {
            String number = (String) getValueInContext(map, "number");
            if (number.contains("/")) {
                String[] parts = number.split("/");
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);
                number = ((numerator / denominator) * 100) + "";
            }
            if (number.contains("%")) {
                number = number.replace("%", "");
            }
            Double curNumber = Double.parseDouble(number);
            double decimalPart = curNumber / 10.0;
            if (map.containsKey("adjust_type")) {
                String adjustType = (String) getValueInContext(map, "adjust_type");
                if (adjustType.equals("set")) {
                    if (decimalPart < 1) {
                        LogUtils.i(TAG, "curSetBrightness decimalPart:" + decimalPart);
                        return (int) decimalPart;
                    }
                }
            }
            double roundedUp = Math.ceil(decimalPart); // 向上取整
            int curSetNumber = (int) roundedUp; // 转换为int
            LogUtils.i(TAG, "curSetBrightness curSetNumber:" + curSetNumber);
            return curSetNumber;
        }
        return 1;
    }

    public boolean getAtmospherePageState(HashMap<String, Object> map) {
        return operator.getBooleanProp(AtmosphereSignal.ATMO_ATMOSPHERE_PAGE_STATE);
    }

    public void setAtmospherePageState(HashMap<String, Object> map) {
        operator.setBooleanProp(AtmosphereSignal.ATMO_ATMOSPHERE_PAGE_STATE, true);
    }

    public boolean isSupportAtmosphereEffectMode(HashMap<String, Object> ma) {
        return ambLightsImpl.isSupportAtmosphereEffectMode();
    }

    public boolean isEffectModeOpened(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        return operator.getBooleanProp(AtmosphereSignal.ATMO_EFFECT_MODE, getEffectModeValue().get(switch_mode));
    }

    public void setEffectMode(HashMap<String, Object> map) {
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AtmosphereSignal.ATMO_EFFECT_MODE, getEffectModeValue().get(switch_mode), switch_type.equals("open"));
    }

    public Map<String, String> getEffectModeName() {
        return new HashMap<String, String>() {
            {
                put("dws", "动态迎宾");
                put("va", "语音助手");
                put("osw", "超速报警");
                put("dow", "开门预警");
                put("dfm", "疲劳提醒");
                put("sd", "智驾状态");
            }
        };
    }

    public Map<String, Integer> getEffectModeValue() {
        return new HashMap<String, Integer>() {
            {
                put("dws", 1);
                put("va", 2);
                put("osw", 3);
                put("dow", 4);
                put("dfm", 5);
                put("sd", 6);
            }
        };
    }
}
