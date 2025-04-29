package com.voyah.ai.logic.dc;

import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.IDms;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.DmsSignal;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @author:lcy DMS
 * @data:2024/4/8
 **/
public class DmsControlImpl extends AbsDevices {
    private static final String TAG = DmsControlImpl.class.getSimpleName();

    public DmsControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "dms";
    }

    /**
     * 隐私保护是否开启
     *
     * @return
     */
    public boolean isPrivacyProtectionOpen(HashMap<String, Object> map) {
        int privacySecurityStatus = operator.getIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION);
        LogUtils.i(TAG, "isPrivacyProtectionOpen privacySecurityStatus is " + privacySecurityStatus);
        return privacySecurityStatus == 1;
    }


    /**
     * 疲劳驾驶监测是否已打开
     * Dms.FatigueMonitorReq.NORMAL 标准
     * Dms.FatigueMonitorReq.SENSITIVE 灵敏
     * Dms.FatigueMonitorReq.OFF 关闭
     *
     * @return
     */
    public boolean isFatigueMonitorOpen(HashMap<String, Object> map) {
        int fatigueMonitorReq = operator.getIntProp(DmsSignal.DMS_FATIGUE_MONITOR);
        LogUtils.i(TAG, "isFatigueMonitorOpen fatigueMonitorReq is " + fatigueMonitorReq);
        return fatigueMonitorReq == IDms.IFatigueMonitor.NORMAL || fatigueMonitorReq == IDms.IFatigueMonitor.SENSITIVE;
    }


    /**
     * 当前疲劳驾驶监测模式是否为指定调节的模式
     */
    public boolean isCurrentDesignated(HashMap<String, Object> map) {
        String switch_mode = "";
        int adjustTo = -1;
        if (map.containsKey("switch_mode"))
            switch_mode = (String) getValueInContext(map, "switch_mode");
        switch (switch_mode) {
            case "standard":
                adjustTo = IDms.IFatigueMonitor.NORMAL;
                break;
            case "sensitive":
                adjustTo = IDms.IFatigueMonitor.SENSITIVE;
                break;
        }
        int currentFatigueMonitor = operator.getIntProp(DmsSignal.DMS_FATIGUE_MONITOR);
        LogUtils.i(TAG, "isCurrentDesignated fatigueMonitorReq is " + currentFatigueMonitor + " , adjustTo is " + adjustTo);
        return adjustTo == currentFatigueMonitor;
    }


    /**
     * 疲劳驾驶监测模式调节为指定模式
     */
    public void adjustDesignatedType(HashMap<String, Object> map) {
        String adjustType = "";
        int adjustTo = -1;
        if (map.containsKey("switch_mode"))
            adjustType = (String) getValueInContext(map, "switch_mode");
        switch (adjustType) {
            case "standard":
                adjustTo = IDms.IFatigueMonitor.NORMAL;
                break;
            case "sensitive":
                adjustTo = IDms.IFatigueMonitor.SENSITIVE;
                break;
        }
        LogUtils.i(TAG, "adjustDesignatedType adjustType is " + adjustType + " ,adjustTo is " + adjustTo);
        operator.setIntProp(DmsSignal.DMS_FATIGUE_MONITOR, adjustTo);
    }

    /**
     * 未指定模式的调节
     */
    public void adjustType(HashMap<String, Object> map) {
        int adjustTo = -1;
        int currentFatigueMonitor = operator.getIntProp(DmsSignal.DMS_FATIGUE_MONITOR);
        if (currentFatigueMonitor == IDms.IFatigueMonitor.NORMAL) {
            adjustTo = IDms.IFatigueMonitor.SENSITIVE;
            map.put("switch_mode", "sensitive");
        } else if (currentFatigueMonitor == IDms.IFatigueMonitor.SENSITIVE) {
            adjustTo = IDms.IFatigueMonitor.NORMAL;
            map.put("switch_mode", "standard");
        } else {
            adjustTo = IDms.IFatigueMonitor.NORMAL;
            map.put("switch_mode", "standard");
        }
        LogUtils.i(TAG, "isCurrentDesignated fatigueMonitorReq is " + currentFatigueMonitor + " ,adjustTo is " + adjustTo);
        operator.setIntProp(DmsSignal.DMS_FATIGUE_MONITOR, adjustTo);
    }


    /**
     * 打开关闭疲劳监测
     */
    public void switchFatigue(HashMap<String, Object> map) {
        String switch_type = "";
        int adjustTo = -1;
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        switch (switch_type) {
            case "open":
                adjustTo = IDms.IFatigueMonitor.NORMAL;
                break;
            case "close":
                adjustTo = IDms.IFatigueMonitor.OFF;
                break;
        }
        LogUtils.i(TAG, "switchFatigue switch_type is " + switch_type + " ,adjustTo is " + adjustTo);
        operator.setIntProp(DmsSignal.DMS_FATIGUE_MONITOR, adjustTo);
    }

    /**
     * 打开\关闭关闭分心及危险行为监测
     */
    public void switchDistract(HashMap<String, Object> map) {
        String switch_type = "";
        int adjustTo = -1;
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        switch (switch_type) {
            case "open":
                adjustTo = ICommon.Switch.ON;
                break;
            case "close":
                adjustTo = ICommon.Switch.OFF;
                break;
        }
        LogUtils.i(TAG, "switchDistract switch_type is " + switch_type + " ,adjustTo is " + adjustTo);
        operator.setIntProp(DmsSignal.DMS_DISTRACT_MONITOR, adjustTo);
    }

    /**
     * 分心及危险是否已打开
     */
    public boolean isDistractOpen(HashMap<String, Object> map) {
        int currentDistractStatus = operator.getIntProp(DmsSignal.DMS_DISTRACT_MONITOR);
        LogUtils.i(TAG, "isDistractOpen currentDistractStatus is " + currentDistractStatus);
        return currentDistractStatus == ICommon.Switch.ON;
    }

    public boolean isFirstRowLeft(HashMap<String, Object> map) {
        String soundLocation = getSoundLocation(map);
        boolean isFirstRowLeft = StringUtils.equals(soundLocation, LOCATION_LIST.get(0));
        if (!isFirstRowLeft) {
            String str = getScreenSaverReplaceStr("", "", getSoundLocation(map));
            map.put("position_scene", str);
        }
        LogUtils.d(TAG, "isFrontRow soundLocation:" + soundLocation + " ,isFirstRowLeft:" + isFirstRowLeft);
        return isFirstRowLeft;
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
        String switch_mode = map.containsKey("switch_mode") ? (String) getValueInContext(map, "switch_mode") : "";
        switch (key) {
            case "fatigue_monitoring":
                if (StringUtils.equals(switch_mode, "sensitive"))
                    str = str.replace("@{fatigue_monitoring}", "灵敏");
                else if (StringUtils.equals(switch_mode, "standard"))
                    str = str.replace("@{fatigue_monitoring}", "标准");
                break;
            case "position_scene":
                String position_scene = map.containsKey("position_scene") ? (String) getValueInContext(map, "position_scene") : "";
                str = str.replace("@{position_scene}", position_scene);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);


        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

}
