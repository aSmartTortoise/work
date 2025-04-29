package com.voyah.ai.logic.dc.dvr;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.dvr.CmsInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.dc.AbsDevices;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @author:lcy
 * @data:2025/4/15
 **/
public class CmsControlImpl extends AbsDevices implements CmsInterface {
    private static final String TAG = CmsControlImpl.class.getSimpleName();

    @Override
    public boolean isSupportCms(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isSupportCms();
    }

    @Override
    public boolean isBackMirrorOpen(HashMap<String, Object> map) {
        boolean isBackMirrorOpen = DeviceHolder.INS().getDevices().getCamera().isBackMirrorOpen();
        LogUtils.d(TAG, "isBackMirrorOpen:" + isBackMirrorOpen);
        return isBackMirrorOpen;
    }

    @Override
    public boolean isTargetMirrorType(HashMap<String, Object> map) {
        String positions = map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) ? (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) : "";
        int backMirrorType = getBackMirrorType(map);
        LogUtils.d(TAG, "isTargetMirrorType positions:" + positions + " ,backMirrorType:" + backMirrorType);
        if (StringUtils.equals(positions, "inside_car") || StringUtils.equals(positions, "self")) {
            map.put("cms_position", "车内");
            return backMirrorType == 1;
        } else if (StringUtils.equals(positions, "outside_car")) {
            map.put("cms_position", "车外");
            return backMirrorType == 4;
        }

        return false;
    }

    @Override
    public int getBackMirrorType(HashMap<String, Object> map) {
        //电子后视镜当前摄像头状态 1：宝宝守护(车内) 4：电子后视镜(车外)
        int backMirrorType = DeviceHolder.INS().getDevices().getCamera().getBackMirrorType();
        LogUtils.d(TAG, "backMirrorType:" + backMirrorType);
        return backMirrorType;
    }

    @Override
    public void switchbackMirrorType(HashMap<String, Object> map) {
        //切换摄像头 1:OMS 宝宝守护 4:CMS 电子后视镜
        int currentBackMirrorType = getBackMirrorType(map);
        LogUtils.d(TAG, "switchbackMirrorType currentBackMirrorType:" + currentBackMirrorType);
        if (currentBackMirrorType == Constant.CameraType.OMS)
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.CMS);
        else if (currentBackMirrorType == Constant.CameraType.CMS)
            DeviceHolder.INS().getDevices().getCamera().switchBackMirror(Constant.CameraType.OMS);
    }

    @Override
    public void adjustBackMirrorType(HashMap<String, Object> map) {
        String positions = map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) ? (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) : "";
        LogUtils.d(TAG, "adjustBackMirrorType positions:" + positions);
        if (StringUtils.equals(positions, "inside_car") || StringUtils.equals(positions, "self")) {
            DeviceHolder.INS().getDevices().getCamera().switchBackMirrorType(Constant.CameraType.OMS);
        } else if (StringUtils.equals(positions, "outside_car")) {
            DeviceHolder.INS().getDevices().getCamera().switchBackMirrorType(Constant.CameraType.CMS);
        }
    }

    @Override
    public boolean isBackMirrorInBigWindow(HashMap<String, Object> map) {
        boolean isBigWindow = DeviceHolder.INS().getDevices().getCamera().isBackMirrorInBigWindow();
        LogUtils.d(TAG, "isBackMirrorInBigWindow isBigWindow:" + isBigWindow);
        return isBigWindow;
    }

    @Override
    public void zoomBackMirror(HashMap<String, Object> map) {
        String switch_mode = map.containsKey("switch_mode") ? (String) getValueInContext(map, "switch_mode") : "";
        LogUtils.d(TAG, "zoomBackMirror switch_mode:" + switch_mode);
        if (StringUtils.equals(switch_mode, Constant.SwitchMode.FULL_SCREEN))
            DeviceHolder.INS().getDevices().getCamera().zoomBackMirror(true);
        else if (StringUtils.equals(switch_mode, Constant.SwitchMode.SMALL_WINDOW))
            DeviceHolder.INS().getDevices().getCamera().zoomBackMirror(false);
    }

    @Override
    public void switchBackMirror(HashMap<String, Object> map) {
        String positions = map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) ? (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) : "";
        LogUtils.d(TAG, "switchBackMirror positions:" + positions);
        DeviceHolder.INS().getDevices().getCamera().switchBackMirrorType(StringUtils.equals(positions, "outside_car") ? Constant.CameraType.CMS : Constant.CameraType.OMS);
    }

    @Override
    public boolean isOnlyGearsRForbidden(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation() && isH56DCar(map);
    }

    @Override
    public boolean isCrwOpen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isRcwOpen();
    }

    @Override
    public void startFloatCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "startFloatCamera");
        DeviceHolder.INS().getDevices().getCamera().startMirrorCamera();
    }

    @Override
    public void stopFloatCamera(HashMap<String, Object> map) {
        LogUtils.i(TAG, "stopFloatCamera");
        DeviceHolder.INS().getDevices().getCamera().stopFloatCamera();
    }


    @Override
    public boolean isTargetFirstRowLeftScreen(HashMap<String, Object> map) {
        boolean isTargetFirstRowLeftScreen;
        String screen = getAssignScreen(map);
        isTargetFirstRowLeftScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_CENTRAL);
        LogUtils.d(TAG, "isTargetFirstRowLeftScreen screen:" + screen + " ,isTargetFirstRowLeftScreen:" + isTargetFirstRowLeftScreen);
//            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, screen);
        map.put("app_name", "电子后视镜");
        return isTargetFirstRowLeftScreen;
    }

    @Override
    public boolean isAssignCeilScreenAndHaveCeilScreen(HashMap<String, Object> map) {
        return isCeilScreen(map) && !isHaveCeilScreen(map);
    }

    @Override
    public boolean isHaveCeilScreen(HashMap<String, Object> map) {
        boolean isHaveCeilScreen = isBaseHaveCeilScreen();
        if (isHaveCeilScreen)
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
        return isHaveCeilScreen;

    }

    @Override
    public boolean isCeilScreen(HashMap<String, Object> map) {
        return isBaseCeilScreen(map);
    }

    @Override
    public boolean isTargetScreen(HashMap<String, Object> map) {
        boolean isSecondaryScreen = false;
        String screen = getAssignScreen(map);
        isSecondaryScreen = StringUtils.equals(screen, FuncConstants.VALUE_SCREEN_PASSENGER);
        LogUtils.d(TAG, "isTargetScreen screen:" + screen + " ,isSecondaryScreen:" + isSecondaryScreen);
        LogUtils.d(TAG, "isTargetScreen isSecondaryScreen:" + isSecondaryScreen);
        if (!isSecondaryScreen) {
            map.put("position_scene", SCREEN_STR.get(getAssignScreen(map)));
            map.put("app_name", "电子后视镜");
        }
        return isSecondaryScreen;
    }

    @Override
    public boolean isOnlySoundLocation(HashMap<String, Object> map) {
        boolean isOnlySoundLocation = false;
        String screen_name = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String assignPosition = map.containsKey(FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) ? (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION) : "";
        if (StringUtils.isBlank(screen_name) && StringUtils.isBlank(assignPosition))
            isOnlySoundLocation = true;
        LogUtils.d(TAG, "isOnlySoundLocation:" + isOnlySoundLocation);
        map.put(FuncConstants.KEY_TARGET_SCREEN_TYPE, FuncConstants.VALUE_SCREEN_CENTRAL);
        return isOnlySoundLocation;
    }

    @Override
    public String getDomain() {
        return "cms";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        if (StringUtils.isBlank(str)) {
            LogUtils.d(TAG, "replacePlaceHolder str is blank");
            return "";
        }
        LogUtils.d(TAG, "replacePlaceHolder str:" + str);
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "app_name":
                String appName = (String) getValueInContext(map, "app_name");
                LogUtils.d(TAG, "replacePlaceHolder appName:" + appName);
                if (!StringUtils.isBlank(str) && StringUtils.contains(str, "@{app_name}"))
                    str = str.replace("@{app_name}", appName);
                break;
            case "cms_position":
                String cms_position = (String) getValueInContext(map, "cms_position");
                LogUtils.d(TAG, "replacePlaceHolder appName:" + cms_position);
                if (!StringUtils.isBlank(str) && StringUtils.contains(str, "@{cms_position}"))
                    str = str.replace("@{cms_position}", cms_position);
                break;
            case "fn_name":
                String uiName = (String) getValueInContext(map, "dvr_ui_name");
                LogUtils.d(TAG, "replacePlaceHolder uiName:" + uiName);
                if (!StringUtils.isBlank(str) && StringUtils.contains(str, "@{fn_name}"))
                    str = str.replace("@{fn_name}", uiName);
                break;
            case "screen_name":
                String screenName = getOneMapValue(FuncConstants.KEY_TARGET_SCREEN_TYPE, map);
                String replaceStr = "中控屏";
                if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_PASSENGER)) {
                    replaceStr = "副驾屏";
                } else if (StringUtils.equalsIgnoreCase(screenName, FuncConstants.VALUE_SCREEN_CEIL)) {
                    replaceStr = "吸顶屏";
                }
                str = str.replace("@{screen_name}", replaceStr);
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
