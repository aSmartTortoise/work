package com.voyah.ai.device.voyah.h37.dc.utils;

import androidx.annotation.NonNull;

import com.voice.sdk.device.carservice.dc.AtmoInterface;
import com.voice.sdk.device.carservice.signal.AtmosphereSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 氛围灯实现类
 */
public class AmbLightsImpl implements AtmoInterface {

    public static AmbLightsImpl ambLights;

    /**
     * 初始化氛围灯SDK
     */
    public AmbLightsImpl init() {
        if (ambLights == null) {
            synchronized (ShareUtils.class) {
                if (ambLights == null) {
                    ambLights = new AmbLightsImpl();
                }
            }
        }
        return ambLights;
    }

    // 氛围灯主题

    @Override
    public boolean isSupportAtmosphereThemeColor() {
        // 37A和37B都没有该功能 56D和56D有该功能
        return true;
    }

    @Override
    public boolean isSupportCurSetThemeColor(@NonNull String themeColor) {
        boolean isSupport = false;
        Set<String> strings = getThemeData().keySet();
        for (String string : strings) {
            if (string.equals(themeColor)) {
                isSupport = true;
            }
        }
        return isSupport;
    }

    @Override
    public boolean isCurThemeColorOpened(IPropertyOperator operator, @NonNull String themeColor) {
        int curThemeColor = operator.getIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR);
        int setThemeColor = getThemeData().get(themeColor);
        return curThemeColor == setThemeColor;
    }

    @Override
    public void setAtmosphereThemeColor(IPropertyOperator operator, @NonNull String themeColor) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 5) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 5);
        }
        int setThemeColor = getThemeData().get(themeColor);
        operator.setIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR, setThemeColor);
    }

    @Override
    public String setRandomAtmosphereThemeColor(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 5) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 5);
        }
        int curThemeColor = operator.getIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR);
        int count = new Random().nextInt(10);
        if (count == curThemeColor) {
            if (count == 9) {
                count = count - 1;
            } else {
                count = count + 1;
            }
        }
        operator.setIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR,count);
        return getThemeName().get(count);
    }

    public Map<String, Integer> getThemeData() {
        return new HashMap<String, Integer>() {
            {
                put("立春", 0);
                put("春分", 1);
                put("谷雨", 2);
                put("立夏", 3);
                put("夏至", 4);
                put("立秋", 5);
                put("秋分", 6);
                put("霜降", 7);
                put("立冬", 8);
                put("冬至", 9);
            }
        };
    }

    public Map<Integer, String> getThemeName() {
        return new HashMap<Integer, String>() {
            {
                put(0, "立春");
                put(1, "春分");
                put(2, "谷雨");
                put(3, "立夏");
                put(4, "夏至");
                put(5, "立秋");
                put(6, "秋分");
                put(7, "霜降");
                put(8, "立冬");
                put(9, "冬至");
            }
        };
    }

    // 氛围灯单色

    @Override
    public boolean isSupportSingleColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isSupportStaticSingleColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isSupportFollowMusicSingleColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurStaticSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurFollowMusicSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public void setSingleColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    @Override
    public void setStaticSingleColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    @Override
    public void setFollowMusicSingleColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    // 氛围灯多色

    @Override
    public boolean isSupportMultiColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isSupportStaticMultiColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isSupportFollowMusicMultiColorMode() {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurStaticMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public boolean isCurFollowMusicMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
        return false;
    }

    @Override
    public void setMultiColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    @Override
    public void setStaticMultiColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    @Override
    public void setFollowMusicMultiColorMode(@NonNull IPropertyOperator operator) {
        // 56D没有单色 & 多色
    }

    @Override
    public boolean isSupportAtmosphereBreatheMode() {
        // 56D没有呼吸模式
        return false;
    }

    @Override
    public boolean isAtmosphereBreatheModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有呼吸模式
        return false;
    }

    @Override
    public void setAtmosphereBreatheMode(@NonNull IPropertyOperator operator) {
        // 56D没有呼吸模式
    }

    @Override
    public boolean isSupportAtmosphereConstantLighMode() {
        // 56D没有常亮模式
        return false;
    }

    @Override
    public boolean isAtmosphereConstantLighModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有常亮模式
        return false;
    }

    @Override
    public void setAtmosphereConstantLighMode(@NonNull IPropertyOperator operator) {
        // 56D没有常亮模式
    }

    @Override
    public boolean isSupportAtmosphereFlowMode() {
        // 56D没有流动模式
        return false;
    }

    @Override
    public boolean isAtmosphereFlowModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有流动模式
        return false;
    }

    @Override
    public void setAtmosphereFlowMode(@NonNull IPropertyOperator operator) {
        // 56D没有流动模式
    }

    @Override
    public boolean isSupportAtmosphereTimeMode() {
        // 56D没有时光模式
        return false;
    }

    @Override
    public boolean isAtmosphereTimeModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有时光模式
        return false;
    }

    @Override
    public void setAtmosphereTimeMode(@NonNull IPropertyOperator operator) {
        // 56D没有时光模式
    }

    @Override
    public boolean isSupportAtmosphereDynamicFeedbackMode() {
        // 56D没有动态反馈模式
        return false;
    }

    @Override
    public boolean isAtmosphereDynamicFeedbackModeOpened(@NonNull IPropertyOperator operator) {
        // 56D没有动态反馈模式
        return false;
    }

    @Override
    public void setAtmosphereDynamicFeedbackMode(@NonNull IPropertyOperator operator) {
        // 56D没有动态反馈模式
    }

    @Override
    public boolean isSupportAtmosphereDynamicMode() {
        return true;
    }

    @Override
    public boolean isAtmosphereDynamicModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 1;
    }

    @Override
    public void setAtmosphereDynamicMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
    }

    @Override
    public boolean isSupportAtmosphereFollowDriverMode() {
        return true;
    }

    @Override
    public boolean isAtmosphereFollowDriverModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 2;
    }

    @Override
    public void setAtmosphereFollowDriverMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 2);
    }

    @Override
    public boolean isSupportAtmosphereStaticMode() {
        return true;
    }

    @Override
    public boolean isAtmosphereStaticModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 0;
    }

    @Override
    public void setAtmosphereStaticMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
    }

    @Override
    public boolean isSupportAtmosphereThemeMode() {
        return true;
    }

    @Override
    public boolean isAtmosphereThemeModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 5;
    }

    @Override
    public void setAtmosphereThemeMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 5);
    }

    @Override
    public boolean isSupportAtmosphereCustomMode() {
        return true;
    }

    @Override
    public boolean isAtmosphereCustomModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 0;
    }

    @Override
    public void setAtmosphereCustomMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
    }

    @Override
    public boolean isSupportAtmosphereCenterSpeakerSwitch() {
        return false;
    }

    @Override
    public boolean isAtmosphereCenterSpeakerSwitchOpened(@NonNull IPropertyOperator operator) {
        return false;
    }

    @Override
    public void setAtmosphereCenterSpeakerSwitchState(@NonNull IPropertyOperator operator, boolean onOff) {

    }

    @Override
    public boolean isSupportAtmosphereFollowMusicModeOrSwitch() {
        return true;
    }

    @Override
    public boolean isAtmosphereFollowMusicModeAdjust() {
        return true;
    }

    @Override
    public boolean isAtmosphereFollowMusicModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 1;
    }

    @Override
    public void setAtmosphereFollowMusicModeState(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
    }

    @Override
    public boolean isAtmosphereFollowMusicSwitchOpened(@NonNull IPropertyOperator operator) {
        // 56C是模式调节
        return false;
    }

    @Override
    public void setAtmosphereFollowMusicSwitchState(@NonNull IPropertyOperator operator, boolean onOff) {
        // 56C是模式调节
    }

    @Override
    public void setAtmosphereModeChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        // 56C 是自定义，主题模式，跟随驾驶，音乐律动 四个模式轮切
        int mode;
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode == 0) {
            map.put("atmosphere_type", "theme");
            mode = 5;
        } else if (curAtmosphereMode == 5) {
            map.put("atmosphere_type", "follow_driver");
            mode = 2;
        } else if (curAtmosphereMode == 2) {
            map.put("atmosphere_type", "follow_music");
            mode = 1;
        } else {
            map.put("atmosphere_type", "custom_color");
            mode = 0;
        }
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, mode);
    }

    @Override
    public void setAtmosphereColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode == 0) {
            int curStaticSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
            String atmosphereSingleColor = getAtmosphereSingleColorChange(curStaticSingleColor);
            map.put("color", atmosphereSingleColor);
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, getAtmosphereColorData().get(atmosphereSingleColor));
        } else if (curAtmosphereMode == 1) {
            int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
            map.put("color", colorScheme);
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorData().get(colorScheme));
        } else {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            int curStaticSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
            String atmosphereSingleColor = getAtmosphereSingleColorChange(curStaticSingleColor);
            map.put("color", atmosphereSingleColor);
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, getAtmosphereColorData().get(atmosphereSingleColor));
        }
    }

    @Override
    public void setAtmosphereStaticColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 0) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
        int curStaticSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
        String atmosphereSingleColor = getAtmosphereSingleColorChange(curStaticSingleColor);
        map.put("color", atmosphereSingleColor);
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, getAtmosphereColorData().get(atmosphereSingleColor));
    }

    @Override
    public void setAtmosphereFollowMusicColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
        int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
        String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
        map.put("color", colorScheme);
        operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorData().get(colorScheme));
    }

    @Override
    public void setAtmosphereDynamicColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
        int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
        String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
        map.put("color", colorScheme);
        operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorData().get(colorScheme));
    }

    @Override
    public boolean isAtmosphereColorScheme(@NonNull String color) {
        return color.equals("冷色") || color.equals("中性色") || color.equals("暖色");
    }

    @Override
    public boolean isSupportCurColor(@NonNull String color) {
        return getAtmosphereColorData().containsKey(color);
    }

    @Override
    public boolean isAtmosphereCurColorOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
        return curColorValue == atmosphereColor;
    }

    @Override
    public boolean isAtmosphereCurColorSchemeOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int atmosphereColorScheme = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
        return curColorValue == atmosphereColorScheme;
    }

    @Override
    public void setAtmosphereCurColorMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode == 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
    }

    @Override
    public void setAtmosphereCurColorSchemeMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
    }

    @Override
    public void setAtmosphereColor(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 0) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, curColorValue);
    }

    @Override
    public void setAtmosphereColorScheme(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
        operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
    }

    @Override
    public boolean isAtmosphereCurStaticColorOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
        return curColorValue == atmosphereColor;
    }

    @Override
    public boolean isAtmosphereCurStaticColorSchemeOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        // 56D 不支持静态多色
        return false;
    }

    @Override
    public void setAtmosphereCurStaticColorMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode == 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
    }

    @Override
    public void setAtmosphereCurStaticColorSchemeMode(@NonNull IPropertyOperator operator) {
        // 56D 不支持静态多色
    }

    @Override
    public void setAtmosphereStaticColor(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 0) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, curColorValue);
    }

    @Override
    public void setAtmosphereStaticColorScheme(@NonNull IPropertyOperator operator, @NonNull String color) {
        // 56D 不支持静态多色
    }

    @Override
    public boolean isAtmosphereCurFollowMusicColorOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        // 56C 不支持音乐律动单色
        return false;
    }

    @Override
    public boolean isAtmosphereCurFollowMusicColorSchemeOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        // 56C 不支持音乐律动单色
        return false;
    }

    @Override
    public void setAtmosphereCurFollowMusicColorMode(@NonNull IPropertyOperator operator) {
        // 56C 不支持音乐律动单色
    }

    @Override
    public void setAtmosphereCurFollowMusicColorSchemeMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
    }

    @Override
    public void setAtmosphereFollowMusicColor(@NonNull IPropertyOperator operator, @NonNull String color) {
        // 56d 不支持音乐律动单色
    }

    @Override
    public void setAtmosphereFollowMusicColorScheme(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorData().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        }
        operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
    }

    @Override
    public boolean isSupportStaticColorScheme() {
        // 56D不支持静态多色
        return false;
    }

    @Override
    public boolean isSupportFollowMusicColor() {
        // 56D不支持音乐律动单色
        return false;
    }

    @Override
    public boolean isSupportFollowMusicBrightnessAdjust() {
        return false;
    }

    @Override
    public boolean isSupportStaticBrightnessAdjust() {
        return true;
    }

    @Override
    public int curAtmosphereBrightness(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode) {
        return operator.getIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS);
    }

    @Override
    public void setAtmosphereBrightness(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode, int number) {
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, number);
    }

    @Override
    public void openAtmosphereStaticBrightnessTab(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode == 1) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
    }

    @Override
    public void openAtmosphereFollowMusicBrightnessTab(@NonNull IPropertyOperator operator) {
        // 56D 没有动态氛围灯亮度
    }

    @Override
    public void setAtmosphereBrightnessMin(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode) {
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 1);
    }

    @Override
    public void setAtmosphereBrightnessMax(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode) {
        operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 10);
    }

    @Override
    public boolean isSupportAtmosphereEffectMode() {
        return true;
    }

    public String getAtmosphereSingleColorChange(int curColor) {
        String colorStr = "红色";
        if (curColor == 64) {
            colorStr = "橙色";
        } else if (curColor == 58) {
            colorStr = "黄色";
        } else if (curColor == 49) {
            colorStr = "绿色";
        } else if (curColor == 19) {
            colorStr = "紫色";
        } else if (curColor == 2) {
            colorStr = "蓝色";
        } else if (curColor == 1) {
            colorStr = "青色";
        } else if (curColor == 102) {
            colorStr = "红色";
        }
        return colorStr;
    }

    public String getAtmosphereMultiColorChange(int curColor) {
        String colorScheme = "冷色";
        if (curColor == 1) {
            colorScheme = "中性色";
        } else if (curColor == 2) {
            colorScheme = "暖色";
        } else if (curColor == 3) {
            colorScheme = "冷色";
        }
        return colorScheme;
    }

    public Map<String, Integer> getAtmosphereColorData() {
        return new HashMap<String, Integer>() {
            {
                put("红色", 64);
                put("橙色", 58);
                put("黄色", 49);
                put("绿色", 19);
                put("紫色", 2);
                put("蓝色", 1);
                put("青色", 28);
                put("冷色", 1);
                put("暖色", 3);
                put("中性色", 2);
            }
        };
    }

    @Override
    public boolean isSupportColorSchemeAdjust() {
        return true;
    }
}
