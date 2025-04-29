package com.voyah.ai.device.voyah.h37.dc.utils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.voice.sdk.device.carservice.dc.AtmoInterface;
import com.voice.sdk.device.carservice.signal.AtmosphereSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import mega.car.Signal;
import mega.car.hardware.CarPropertyValue;

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
        // 37A和37B都没有该功能 56C和56D有该功能
        return false;
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
        if (curAtmosphereMode != 0 && curAtmosphereMode != 9) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
        int setThemeColor = getThemeData().get(themeColor);
        operator.setIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR, setThemeColor);
    }

    @Override
    public String setRandomAtmosphereThemeColor(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        if (curAtmosphereMode != 0 && curAtmosphereMode != 9) {
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
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
        operator.setIntProp(AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR, count);
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
        // 37A 37Bd都支持单色
        return true;
    }

    @Override
    public boolean isSupportStaticSingleColorMode() {
        // 静态单色只有37A支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportFollowMusicSingleColorMode() {
        // 音乐律动单色只有37A支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCurSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            // 0=静态单色 9=动态单色
            if (isStaticMode) {
                return curAtmosphereMode == 0;
            } else {
                return curAtmosphereMode == 9;
            }
        } else {
            // 0=常亮 10=呼吸
            return curAtmosphereMode == 0 || curAtmosphereMode == 10;
        }
    }

    @Override
    public boolean isCurStaticSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 0=静态单色
            return curAtmosphereMode == 0;
        } else {
            // 37B 不支持静态单色
            return false;
        }
    }

    @Override
    public boolean isCurFollowMusicSingleColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 9=动态单色
            return curAtmosphereMode == 9;
        } else {
            // 37B 不支持音乐律动单色
            return false;
        }
    }

    @Override
    public void setSingleColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            // 0=静态单色 9=动态单色
            if (isStaticMode) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            } else {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
            }
        } else {
            // 0=常亮
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        }
    }

    @Override
    public void setStaticSingleColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 0=静态单色
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
        } else {
            // 37B 不支持静态单色，不会走到这里
        }
    }

    @Override
    public void setFollowMusicSingleColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 9=动态单色
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
        } else {
            // 37B 不支持音乐律动单色，不会走到这里
        }
    }

    // 氛围灯多色

    @Override
    public boolean isSupportMultiColorMode() {
        // 37A 37Bd都支持多色
        return true;
    }

    @Override
    public boolean isSupportStaticMultiColorMode() {
        // 静态多色只有37A支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportFollowMusicMultiColorMode() {
        // 音乐律动单色只有37A支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCurMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            // 6=静态多色 1=动态多色
            if (isStaticMode) {
                return curAtmosphereMode == 6;
            } else {
                return curAtmosphereMode == 1;
            }
        } else {
            // 11=流动
            return curAtmosphereMode == 11;
        }
    }

    @Override
    public boolean isCurStaticMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 6=静态多色
            return curAtmosphereMode == 6;
        } else {
            // 37B 不支持静态多色
            return false;
        }
    }

    @Override
    public boolean isCurFollowMusicMultiColorModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 1=动态多色
            return curAtmosphereMode == 1;
        } else {
            // 37B 不支持音乐律动多色
            return false;
        }
    }

    @Override
    public void setMultiColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            // 6=静态多色 1=动态多色
            if (isStaticMode) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
            } else {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
            }
        } else {
            // 11=流动
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
        }
    }

    @Override
    public void setStaticMultiColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 6=静态多色
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
        } else {
            // 37B 不支持静态单色，不会走到这里
        }
    }

    @Override
    public void setFollowMusicMultiColorMode(@NonNull IPropertyOperator operator) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            // 1=动态多色
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
        } else {
            // 37B 不支持静态单色，不会走到这里
        }
    }


    // 呼吸模式

    @Override
    public boolean isSupportAtmosphereBreatheMode() {
        // 呼吸模式 37B有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAtmosphereBreatheModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 10;
    }

    @Override
    public void setAtmosphereBreatheMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 10);
    }


    // 常亮模式

    @Override
    public boolean isSupportAtmosphereConstantLighMode() {
        // 呼吸模式 37B有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAtmosphereConstantLighModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 0;
    }

    @Override
    public void setAtmosphereConstantLighMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
    }


    // 流动模式

    @Override
    public boolean isSupportAtmosphereFlowMode() {
        // 呼吸模式 37B有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAtmosphereFlowModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 11;
    }

    @Override
    public void setAtmosphereFlowMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
    }


    // 流动模式

    @Override
    public boolean isSupportAtmosphereTimeMode() {
        // 呼吸模式 37B有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAtmosphereTimeModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 12;
    }

    @Override
    public void setAtmosphereTimeMode(@NonNull IPropertyOperator operator) {
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 12);
    }


    // 动态反馈模式

    @Override
    public boolean isSupportAtmosphereDynamicFeedbackMode() {
        // 动态反馈模式 37A有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAtmosphereDynamicFeedbackModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 0 || curAtmosphereMode == 9;
    }

    @Override
    public void setAtmosphereDynamicFeedbackMode(@NonNull IPropertyOperator operator) {
        int staticModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, staticModeARS);
    }


    // 动态模式

    @Override
    public boolean isSupportAtmosphereDynamicMode() {
        // 动态反馈模式 37A有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAtmosphereDynamicModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 1 || curAtmosphereMode == 6;
    }

    @Override
    public void setAtmosphereDynamicMode(@NonNull IPropertyOperator operator) {
        int dynamicModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, dynamicModeARS);
    }


    // 跟隨駕駛模式

    @Override
    public boolean isSupportAtmosphereFollowDriverMode() {
        // 37A 37B 都沒有该模式
        return false;
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


    // 静态模式

    @Override
    public boolean isSupportAtmosphereStaticMode() {
        // 静态模式 37A有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAtmosphereStaticModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 0 || curAtmosphereMode == 9;
    }

    @Override
    public void setAtmosphereStaticMode(@NonNull IPropertyOperator operator) {
        int staticModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, staticModeARS);
    }


    // 主题模式

    @Override
    public boolean isSupportAtmosphereThemeMode() {
        // 37A 37B 都沒有该模式
        return false;
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


    // 自定义模式

    @Override
    public boolean isSupportAtmosphereCustomMode() {
        // 37A 37B 都沒有该模式
        return false;
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


    // 中置扬声器氛围灯开关

    @Override
    public boolean isSupportAtmosphereCenterSpeakerSwitch() {
        // 中置扬声器氛围灯开关 37B有
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAtmosphereCenterSpeakerSwitchOpened(@NonNull IPropertyOperator operator) {
        return operator.getBooleanProp(AtmosphereSignal.ATMO_CENTER_SPEAKER_SWITCH_STATE);
    }

    @Override
    public void setAtmosphereCenterSpeakerSwitchState(@NonNull IPropertyOperator operator, @NonNull boolean onOff) {
        operator.setBooleanProp(AtmosphereSignal.ATMO_CENTER_SPEAKER_SWITCH_STATE, onOff);
    }


    // 音乐律动开关 and 模式

    @Override
    public boolean isSupportAtmosphereFollowMusicModeOrSwitch() {
        return true;
    }

    @Override
    public boolean isAtmosphereFollowMusicModeAdjust() {
        // 37A 是模式调节 37B 是开关
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAtmosphereFollowMusicModeOpened(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        return curAtmosphereMode == 1 || curAtmosphereMode == 6;
    }

    @Override
    public void setAtmosphereFollowMusicModeState(@NonNull IPropertyOperator operator) {
        int dynamicModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, dynamicModeARS);
    }

    @Override
    public boolean isAtmosphereFollowMusicSwitchOpened(@NonNull IPropertyOperator operator) {
        return operator.getBooleanProp(AtmosphereSignal.ATMO_FOLLOW_MUSIC_SWITCH_STATE);
    }

    @Override
    public void setAtmosphereFollowMusicSwitchState(@NonNull IPropertyOperator operator, @NonNull boolean onOff) {
        operator.setBooleanProp(AtmosphereSignal.ATMO_FOLLOW_MUSIC_SWITCH_STATE, onOff);
    }


    // 氛围灯模式模糊调节

    @Override
    public void setAtmosphereModeChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        // 37A 是静态和音乐律动两个模式轮切
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStatic = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (isStatic) {
                map.put("atmosphere_type", "follow_music");
                int lastDynamicModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, lastDynamicModeARS);
            } else {
                map.put("atmosphere_type", "static");
                int staticModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, staticModeARS);
            }
        } else {
            // 37B 是常亮，呼吸，流动，时光 四个模式轮切
            int mode;
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode == 0) {
                map.put("atmosphere_type", "breathe");
                mode = 10;
            } else if (curAtmosphereMode == 10) {
                map.put("atmosphere_type", "flow");
                mode = 11;
            } else if (curAtmosphereMode == 11) {
                map.put("atmosphere_type", "time");
                mode = 12;
            } else {
                map.put("atmosphere_type", "constant_light");
                mode = 0;
            }
            operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, mode);
        }
    }


    // 氛围灯颜色模糊调节

    @Override
    public void setAtmosphereColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode == 0) {
                // 静态单色
                int curStaticSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
                String color = getAtmosphereSingleColorChangeH37A(curStaticSingleColor);
                map.put("color", color);
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, getAtmosphereColorDataH37A().get(color));
            } else if (curAtmosphereMode == 9) {
                //静态多色
                int staticMultipleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR);
                String colorScheme = getAtmosphereMultiColorChange(staticMultipleColor);
                map.put("color", colorScheme);
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
            } else if (curAtmosphereMode == 6) {
                //音乐律动单色
                int musicSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR);
                String color = getAtmosphereSingleColorChangeH37A(musicSingleColor);
                map.put("color", color);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR, getAtmosphereColorDataH37A().get(color));
            } else {
                //音乐律动多色
                int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
                String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
                map.put("color", colorScheme);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
            }
        } else {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 0 && curAtmosphereMode != 10) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
            // 常亮 和 呼吸模式下
            int atmosphereColor;
            String json = (String) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR).getValue();
            JSONObject object = JSON.parseObject(json);
            int select = object.getIntValue("select");
            if (select == 0) {
                atmosphereColor = object.getIntValue("selection0color0");
            } else if (select == 1) {
                atmosphereColor = object.getIntValue("selection1color0");
            } else if (select == 2) {
                atmosphereColor = object.getIntValue("selection2color0");
            } else {
                atmosphereColor = object.getIntValue("selection3color0");
            }
            String atmosphereSingleColor = getAtmosphereSingleColorChangeH37B(atmosphereColor);
            map.put("color", atmosphereSingleColor);
            int setColorValue = getAtmosphereColorDataH37B().get(atmosphereSingleColor);
            if (select == 0) {
                object.put("selection0color0",setColorValue);
            } else if (select == 1) {
                object.put("selection1color0",setColorValue);
            } else if (select == 2) {
                object.put("selection2color0",setColorValue);
            } else {
                object.put("selection3color0",setColorValue);
            }
            CarPropertyValue<String> value = new CarPropertyValue<>(Signal.ID_AMB_LIGHT_SINGLE_COLOR, object.toJSONString());
            CarPropUtils.getInstance().setRawProp(value);
        }
    }


    @Override
    public void setAtmosphereStaticColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 0 && curAtmosphereMode != 9) {
                curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, curAtmosphereMode);
            }
            if (curAtmosphereMode == 0) {
                int staticSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
                String color = getAtmosphereSingleColorChangeH37A(staticSingleColor);
                map.put("color", color);
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, getAtmosphereColorDataH37A().get(color));
            } else {
                int staticMultipleColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR);
                String colorScheme = getAtmosphereMultiColorChange(staticMultipleColor);
                map.put("color", colorScheme);
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
            }
        } else {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 0 && curAtmosphereMode != 10) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
            // 常亮 和 呼吸模式下
            int atmosphereColor;
            String json = (String) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR).getValue();
            JSONObject object = JSON.parseObject(json);
            int select = object.getIntValue("select");
            if (select == 0) {
                atmosphereColor = object.getIntValue("selection0color0");
            } else if (select == 1) {
                atmosphereColor = object.getIntValue("selection1color0");
            } else if (select == 2) {
                atmosphereColor = object.getIntValue("selection2color0");
            } else {
                atmosphereColor = object.getIntValue("selection3color0");
            }
            String atmosphereSingleColor = getAtmosphereSingleColorChangeH37B(atmosphereColor);
            map.put("color", atmosphereSingleColor);
            int setColorValue = getAtmosphereColorDataH37B().get(atmosphereSingleColor);
            if (select == 0) {
                object.put("selection0color0",setColorValue);
            } else if (select == 1) {
                object.put("selection1color0",setColorValue);
            } else if (select == 2) {
                object.put("selection2color0",setColorValue);
            } else {
                object.put("selection3color0",setColorValue);
            }
            CarPropertyValue<String> value = new CarPropertyValue<>(Signal.ID_AMB_LIGHT_SINGLE_COLOR, object.toJSONString());
            CarPropUtils.getInstance().setRawProp(value);
        }
    }

    @Override
    public void setAtmosphereFollowMusicColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 1 && curAtmosphereMode != 6) {
                curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, curAtmosphereMode);
            }
            if (curAtmosphereMode == 6) {
                // 设置成音乐律动单色随机颜色
                int musicSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR);
                String color = getAtmosphereSingleColorChangeH37A(musicSingleColor);
                map.put("color", color);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR, getAtmosphereColorDataH37A().get(color));
            } else {
                //音乐律动多色
                int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
                String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
                map.put("color", colorScheme);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
            }
        } else {
            // 动态多色
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, 11);
            }
            int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
            map.put("color", colorScheme);
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
        }
    }

    @Override
    public void setAtmosphereDynamicColorChange(@NonNull IPropertyOperator operator, @NonNull HashMap<String, Object> map) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 1 && curAtmosphereMode != 6) {
                curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, curAtmosphereMode);
            }
            if (curAtmosphereMode == 6) {
                // 设置成音乐律动单色随机颜色
                int musicSingleColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR);
                String color = getAtmosphereSingleColorChangeH37A(musicSingleColor);
                map.put("color", color);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR, getAtmosphereColorDataH37A().get(color));
            } else {
                //音乐律动多色
                int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
                String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
                map.put("color", colorScheme);
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
            }
        } else {
            // 动态多色
            int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, 11);
            }
            int colourSelectTypeARS = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            String colorScheme = getAtmosphereMultiColorChange(colourSelectTypeARS);
            map.put("color", colorScheme);
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, getAtmosphereColorDataH37A().get(colorScheme));
        }
    }

    @Override
    public boolean isAtmosphereColorScheme(@NonNull String color) {
        return color.equals("冷色") || color.equals("中性色") || color.equals("暖色") || color.equals("组合色");
    }

    @Override
    public boolean isSupportCurColor(@NonNull String color) {
        boolean isSupportColor = false;
        Set<String> strings = getAtmosphereColorDataH37A().keySet();
        for (String string : strings) {
            if (string.equals(color)) {
                isSupportColor = true;
            }
        }
        return isSupportColor;
    }

    @Override
    public boolean isAtmosphereCurColorOpened(@NonNull IPropertyOperator operator, String color) {
        int curColorValue;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            curColorValue = getAtmosphereColorDataH37A().get(color);
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            int atmosphereColor;
            if (isStaticMode) {
                atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
            } else {
                atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR);
            }
            return curColorValue == atmosphereColor;
        } else {
            curColorValue = getAtmosphereColorDataH37B().get(color);
            int atmosphereColor = 0;
            CarPropertyValue propertyRaw = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR);
            if (propertyRaw != null && propertyRaw.getValue() instanceof String) {
                String json = (String) propertyRaw.getValue();
                JSONObject obj = JSON.parseObject(json);
                if (obj != null) {
                    int select = obj.getIntValue("select");
                    if (select == 0) {
                        atmosphereColor = obj.getIntValue("selection0color0");
                    } else if (select == 1) {
                        atmosphereColor = obj.getIntValue("selection1color0");
                    } else if (select == 2) {
                        atmosphereColor = obj.getIntValue("selection2color0");
                    } else {
                        atmosphereColor = obj.getIntValue("selection3color0");
                    }
                }
            }
            return curColorValue == atmosphereColor;
        }
    }

    @Override
    public boolean isAtmosphereCurColorSchemeOpened(@NonNull IPropertyOperator operator, String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            int atmosphereColorScheme;
            if (isStaticMode) {
                atmosphereColorScheme = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR);
            } else {
                atmosphereColorScheme = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            }
            return curColorValue == atmosphereColorScheme;
        } else {
            int atmosphereColorScheme = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            return curColorValue == atmosphereColorScheme;
        }
    }

    @Override
    public void setAtmosphereCurColorMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (isStaticMode) {
                if (curAtmosphereMode != 0) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
                }
            } else {
                if (curAtmosphereMode != 6) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
                }
            }
        } else {
            if (curAtmosphereMode != 0 && curAtmosphereMode != 10) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
        }
    }

    @Override
    public void setAtmosphereCurColorSchemeMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (isStaticMode) {
                if (curAtmosphereMode != 9) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
                }
            } else {
                if (curAtmosphereMode != 1) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
                }
            }
        } else {
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
            }
        }
    }

    @Override
    public void setAtmosphereColor(@NonNull IPropertyOperator operator, String color) {
        int curColorValue;
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            curColorValue = getAtmosphereColorDataH37A().get(color);
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (isStaticMode) {
                if (curAtmosphereMode != 0) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
                }
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, curColorValue);
            } else {
                if (curAtmosphereMode != 6) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
                }
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR, curColorValue);
            }
        } else {
            curColorValue = getAtmosphereColorDataH37B().get(color);
            String json = (String) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR).getValue();
            JSONObject object = JSON.parseObject(json);
            int select = object.getIntValue("select");
            if (select == 0) {
                object.put("selection0color0",curColorValue);
            } else if (select == 1) {
                object.put("selection1color0",curColorValue);
            } else if (select == 2) {
                object.put("selection2color0",curColorValue);
            } else {
                object.put("selection3color0",curColorValue);
            }
            CarPropertyValue<String> value = new CarPropertyValue<>(Signal.ID_AMB_LIGHT_SINGLE_COLOR, object.toJSONString());
            CarPropUtils.getInstance().setRawProp(value);
        }
    }

    @Override
    public void setAtmosphereColorScheme(@NonNull IPropertyOperator operator, String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (isStaticMode) {
                if (curAtmosphereMode != 9) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
                }
                operator.setIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR, curColorValue);
            } else {
                if (curAtmosphereMode != 1) {
                    operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
                }
                operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
            }
        } else {
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
        }
    }

    @Override
    public boolean isAtmosphereCurStaticColorOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            curColorValue = getAtmosphereColorDataH37A().get(color);
            int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR);
            return curColorValue == atmosphereColor;
        } else {
            curColorValue = getAtmosphereColorDataH37B().get(color);
            int atmosphereColor = 0;
            CarPropertyValue propertyRaw = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR);
            if (propertyRaw != null && propertyRaw.getValue() instanceof String) {
                String json = (String) propertyRaw.getValue();
                JSONObject obj = JSON.parseObject(json);
                if (obj != null) {
                    int select = obj.getIntValue("select");
                    if (select == 0) {
                        atmosphereColor = obj.getIntValue("selection0color0");
                    } else if (select == 1) {
                        atmosphereColor = obj.getIntValue("selection1color0");
                    } else if (select == 2) {
                        atmosphereColor = obj.getIntValue("selection2color0");
                    } else {
                        atmosphereColor = obj.getIntValue("selection3color0");
                    }
                }
            }
            return curColorValue == atmosphereColor;
        }
    }

    @Override
    public boolean isAtmosphereCurStaticColorSchemeOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int  atmosphereColorScheme = operator.getIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR);
            return curColorValue == atmosphereColorScheme;
        } else {
           // 37B 不支持静态多色
            return false;
        }
    }

    @Override
    public void setAtmosphereCurStaticColorMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (curAtmosphereMode != 0) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
        } else {
            if (curAtmosphereMode != 0 && curAtmosphereMode != 10) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
        }
    }

    @Override
    public void setAtmosphereCurStaticColorSchemeMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (curAtmosphereMode != 9) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
            }
        } else {
            // 37B 不支持静态多色
        }
    }

    @Override
    public void setAtmosphereStaticColor(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue;
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            curColorValue = getAtmosphereColorDataH37A().get(color);
            if (curAtmosphereMode != 0) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 0);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR, curColorValue);
        } else {
            curColorValue = getAtmosphereColorDataH37B().get(color);
            String json = (String) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_AMB_LIGHT_SINGLE_COLOR).getValue();
            JSONObject object = JSON.parseObject(json);
            int select = object.getIntValue("select");
            if (select == 0) {
                object.put("selection0color0",curColorValue);
            } else if (select == 1) {
                object.put("selection1color0",curColorValue);
            } else if (select == 2) {
                object.put("selection2color0",curColorValue);
            } else {
                object.put("selection3color0",curColorValue);
            }
            CarPropertyValue<String> value = new CarPropertyValue<>(Signal.ID_AMB_LIGHT_SINGLE_COLOR, object.toJSONString());
            CarPropUtils.getInstance().setRawProp(value);
        }
    }

    @Override
    public void setAtmosphereStaticColorScheme(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            boolean isStaticMode = operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
            if (curAtmosphereMode != 9) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 9);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_MULTI_COLOR, curColorValue);
        } else {
            // 37B 不支持静态多色
        }
    }

    @Override
    public boolean isAtmosphereCurFollowMusicColorOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR);
            return curColorValue == atmosphereColor;
        } else {
            // 37B 不支持音乐律动单色
            return false;
        }
    }

    @Override
    public boolean isAtmosphereCurFollowMusicColorSchemeOpened(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            return curColorValue == atmosphereColor;
        } else {
            int atmosphereColor = operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR);
            return curColorValue == atmosphereColor;
        }
    }

    @Override
    public void setAtmosphereCurFollowMusicColorMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (curAtmosphereMode != 6) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
            }
        } else {
            // 37B 不支持音乐律动单色
        }
    }

    @Override
    public void setAtmosphereCurFollowMusicColorSchemeMode(@NonNull IPropertyOperator operator) {
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (curAtmosphereMode != 1) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
            }
        } else {
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
            }
        }
    }

    @Override
    public void setAtmosphereFollowMusicColor(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (curAtmosphereMode != 6) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 6);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR, curColorValue);
        } else {
            // 37B 不支持音乐律动单色
        }
    }

    @Override
    public void setAtmosphereFollowMusicColorScheme(@NonNull IPropertyOperator operator, @NonNull String color) {
        int curColorValue = getAtmosphereColorDataH37A().get(color);
        int curAtmosphereMode = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (curAtmosphereMode != 1) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 1);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
        } else {
            if (curAtmosphereMode != 11) {
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, 11);
            }
            operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR, curColorValue);
        }
    }

    @Override
    public boolean isSupportStaticColorScheme() {
        // 37A支持静态多色，37B不支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportFollowMusicColor() {
        // 37A支持动态单色，37B不支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportColorSchemeAdjust() {
        // 37A支持调节色系，37B不支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    public String getAtmosphereSingleColorChangeH37A(int curColor) {
        String colorStr = "红色";
        if (curColor == 46) {
            colorStr = "橙色";
        } else if (curColor == 59) {
            colorStr = "黄色";
        } else if (curColor == 72) {
            colorStr = "绿色";
        } else if (curColor == 87) {
            colorStr = "紫色";
        } else if (curColor == 9) {
            colorStr = "粉色";
        } else if (curColor == 22) {
            colorStr = "蓝色";
        } else if (curColor == 128) {
            colorStr = "青色";
        } else if (curColor == 102) {
            colorStr = "红色";
        }
        return colorStr;
    }

    public String getAtmosphereSingleColorChangeH37B(int curColor) {
        String colorStr = "红色";
        if (curColor == 1) {
            colorStr = "橙色";
        } else if (curColor == 69) {
            colorStr = "黄色";
        } else if (curColor == 12) {
            colorStr = "绿色";
        } else if (curColor == 21) {
            colorStr = "青色";
        } else if (curColor == 33) {
            colorStr = "蓝色";
        } else if (curColor == 43) {
            colorStr = "紫色";
        } else if (curColor == 49) {
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
            colorScheme = "组合色";
        } else if (curColor == 5) {
            colorScheme = "冷色";
        }
        return colorScheme;
    }

    public Map<String, Integer> getAtmosphereColorDataH37A() {
        return new HashMap<String, Integer>() {
            {
                put("红色", 46);
                put("橙色", 59);
                put("黄色", 72);
                put("绿色", 87);
                put("紫色", 9);
                put("粉色", 22);
                put("蓝色", 128);
                put("青色", 102);
                put("冷色", 1);
                put("暖色", 3);
                put("中性色", 2);
                put("组合色", 5);
            }
        };
    }

    public Map<String, Integer> getAtmosphereColorDataH37B() {
        return new HashMap<String, Integer>() {
            {
                put("红色", 1);
                put("橙色", 69);
                put("黄色", 12);
                put("绿色", 21);
                put("青色", 33);
                put("蓝色", 43);
                put("紫色", 49);
                put("冷色", 1);
                put("暖色", 3);
                put("中性色", 2);
                put("组合色", 5);
            }
        };
    }

    @Override
    public boolean isSupportFollowMusicBrightnessAdjust() {
        // 37A支持动态亮度调节 37B不支持
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportStaticBrightnessAdjust() {
        // 37A支持静态亮度调节说法 37B不支持该说法
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return true;
        }
        return false;
    }

    @Override
    public int curAtmosphereBrightness(@NonNull IPropertyOperator operator, String atmosphereMode) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (atmosphereMode.isEmpty()) {
                boolean isStatic =  operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
                if (isStatic) {
                    return operator.getIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS);
                } else {
                    return operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS);
                }
            } else {
                if (atmosphereMode.equals("static")) {
                    return operator.getIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS);
                } else {
                    return operator.getIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS);
                }
            }
        } else {
            // 37B 只有静态氛围灯亮度
            return operator.getIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS);
        }
    }

    @Override
    public void setAtmosphereBrightness(@NonNull IPropertyOperator operator, String atmosphereMode, int number) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (atmosphereMode.isEmpty()) {
                boolean isStatic =  operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
                if (isStatic) {
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, number);
                } else {
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, number);
                }
            } else {
                int lightsModeARS = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
                if (atmosphereMode.equals("static")) {
                    if (lightsModeARS != 0 && lightsModeARS != 9) {
                        //如果不是静态，需要先设置成静态
                        int staticModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, staticModeARS);
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, number);
                } else {
                    if (lightsModeARS != 1 && lightsModeARS != 6) {
                        //如果不是动，需要先设置成动态
                        int dynamicModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, dynamicModeARS);
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, number);
                }
            }
        } else {
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, number);
        }
    }

    @Override
    public void openAtmosphereStaticBrightnessTab(@NonNull IPropertyOperator operator) {
        int lightsModeARS = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (lightsModeARS != 0 && lightsModeARS != 9) {
                //如果不是静态，需要先设置成静态
                int staticModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, staticModeARS);
            }
        } else {
            //37B不需要定位，每个页面都有亮度进度条
        }
    }

    @Override
    public void openAtmosphereFollowMusicBrightnessTab(@NonNull IPropertyOperator operator) {
        int lightsModeARS = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (lightsModeARS != 1 && lightsModeARS != 6) {
                //如果不是静态，需要先设置成静态
                int dynamicModeARS = operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, dynamicModeARS);
            }
        } else {
            //37B不需要定位，每个页面都有亮度进度条
        }
    }


    @Override
    public void setAtmosphereBrightnessMin(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int lightsModeARS = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (atmosphereMode.isEmpty()) {
                boolean isStatic =  operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
                if (isStatic) {
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 1);
                } else {
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, 1);
                }
            } else {
                if (atmosphereMode.equals("static")) {
                    if (lightsModeARS != 0 && lightsModeARS != 9) {
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE));
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 1);
                } else {
                    if (lightsModeARS != 6 && lightsModeARS != 1) {
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE));
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, 1);
                }
            }
        } else {
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 1);
        }
    }

    @Override
    public void setAtmosphereBrightnessMax(@NonNull IPropertyOperator operator, @NonNull String atmosphereMode) {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int lightsModeARS = operator.getIntProp(AtmosphereSignal.ATMO_ACTION_MODE);
            if (atmosphereMode.isEmpty()) {
                boolean isStatic =  operator.getBooleanProp(AtmosphereSignal.ATMO_IS_STATIC);
                if (isStatic) {
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 10);
                } else {
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, 10);
                }
            } else {
                if (atmosphereMode.equals("static")) {
                    if (lightsModeARS != 0 && lightsModeARS != 9) {
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, operator.getIntProp(AtmosphereSignal.ATMO_LAST_STATIC_MODE));
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 10);
                } else {
                    if (lightsModeARS != 6 && lightsModeARS != 1) {
                        operator.setIntProp(AtmosphereSignal.ATMO_ACTION_MODE, operator.getIntProp(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE));
                    }
                    operator.setIntProp(AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS, 10);
                }
            }
        } else {
            operator.setIntProp(AtmosphereSignal.ATMO_STATIC_BRIGHTNESS, 10);
        }
    }

    @Override
    public boolean isSupportAtmosphereEffectMode() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        return true;
    }
}
