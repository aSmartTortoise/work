package com.voyah.ai.logic.openapp;

import android.text.TextUtils;

import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.AbstractAppProcessor;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voice.sdk.util.LogUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MediaAppProcessor extends AbstractAppProcessor {
    private static final String TAG = MediaAppProcessor.class.getSimpleName();

    @Override
    public @NotNull String process(@NotNull HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map); // 指定位置
        String screenName = getOneMapValue("screen_name", map);//指定屏幕
        String soundLocation = getScreenName(getUiSoundLocation(map));//声源位置
        String queryScreen = getScreenName(!TextUtils.isEmpty(position) ? position : screenName);
        String targetScreen = !TextUtils.isEmpty(queryScreen) ? queryScreen : soundLocation;
        if(!TextUtils.isEmpty(queryScreen)){
            if(FuncConstants.VALUE_SCREEN_CEIL.equals(queryScreen)){
                //指定位置时 车辆无后排屏配置，则播报不支持
                //若吸顶屏折叠 则直接打开吸顶屏
                 if(!isCarSupportScreen(FuncConstants.VALUE_SCREEN_CEIL)){
                     return TtsReplyUtils.getTtsBean("1100028").getSelectTTs();
                 } else {
                     if(!isCeilingOpen()){
                         openCeilScreenAndApp(map);
                         return TtsReplyUtils.getTtsBean("2098008").getSelectTTs();
                     }
                 }
            }
        } else {
            if(FuncConstants.VALUE_SCREEN_CEIL.equals(soundLocation)){
                //声源位置时 车辆无后排屏配置，则控制中控屏
                //若吸顶屏折叠 则二次确认
                if(!isCarSupportScreen(FuncConstants.VALUE_SCREEN_CEIL)){
                    targetScreen = FuncConstants.VALUE_SCREEN_CENTRAL;
                } else {
                    if(!isCeilingOpen()){
                        if (isSecondRound(map)) {
                            if (!isSecondConfirm(map)) {
                                return TtsReplyUtils.getTtsBean("1100036").getSelectTTs();
                            } else {
                                openCeilScreenAndApp(map);
                                return TtsReplyUtils.getTtsBean("2098008").getSelectTTs();
                            }
                        }
                    }
                }
            }
        }
        openMediaApp(map,targetScreen);
        return "";
    }

    @Override
    public boolean consume(@NotNull HashMap<String, Object> map) {
        return isMediaApp(map);
    }

    public boolean isMediaApp(HashMap<String, Object> map) {
        //TODO 媒体、互联、泊车等需要单独处理的汇总白名单
        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        //媒体单独做逻辑
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            return !isNavi(map);
        } else if (ApplicationConstant.UI_NAME_HISTORY.equals(uiName)) {
            return true; //TODO zhangyifan
        } else {
            return DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().isMedia(appName)
                    || DeviceHolder.INS().getDevices().getMediaCenter().isVideoApp(DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName));
        }
    }

    /**
     * 是否导航意图，1.明确指定导航 2.“打开收藏”，没指定是导航还是音乐，导航在前台，且不是分屏就给到导航
     *
     * @param map
     * @return
     */
    public boolean isNavi(HashMap<String, Object> map) {
        String uiName = getOneMapValue("ui_name", map);
        String domain = getOneMapValue("domain", map);
        boolean isNaviCollection = false;
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            isNaviCollection = "map".equals(domain) || "navi".equals(domain);
            if (StringUtils.isBlank(domain) && (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront() && (!DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen()))) {
                isNaviCollection = true;
            }
        }
        return isNaviCollection;
    }

    public void openMediaApp(HashMap<String, Object> map,String targetScreen) {
        LogUtils.i(TAG, "openMediaApp");

        String appName = getOneMapValue("app_name", map);
        String uiName = getOneMapValue("ui_name", map);
        String switchType = getOneMapValue("switch_type", map);
        String soundLocation = getScreenName(getUiSoundLocation(map));//声源位置
        String mediaType = getOneMapValue("domain", map);
        boolean isOpen = "open".equals(switchType);
        DeviceHolder.INS().getDevices().getMediaCenter().initVoicePosition(targetScreen, soundLocation);
        if (DeviceHolder.INS().getDevices().getMediaCenter().switchVideoPage(uiName, appName, true, mediaType)) {
            return;
        }
        switchMediaAppControl(appName, uiName, isOpen);
    }

    private void switchMediaAppControl(String appName, String uiName, boolean isOpen) {
        if (ApplicationConstant.UI_NAME_COLLECTION.equals(uiName)) {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().switchCollectUI(isOpen, appName).getSelectTTs());
        } else if (ApplicationConstant.UI_NAME_HISTORY.equals(uiName)) {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().switchHistoryUI(isOpen, appName).getSelectTTs());
        } else {
            DeviceHolder.INS().getDevices().getMediaCenter().speakTts(DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().openMediaAppByAppName(appName, isOpen).getSelectTTs());
        }
    }

    public String getScreenName(String location) {
        if(TextUtils.isEmpty(location)){
            return "";
        }
        String screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        switch (location) {
            case "first_row_left":
            case "central_screen":
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case "first_row_right":
            case "passenger_screen":
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            case "second_row_left":
            case "second_row_right":
            case "third_row_left":
            case "third_row_right":
            case "second_row_mid":
            case "second_side":
            case "rear_side":
            case "ceil_screen":
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
        }
        return screenName;
    }

    public void openCeilScreenAndApp(HashMap<String, Object> map){
        DeviceHolder.INS().getDevices().getSystem().getScreen().onCeilOpen(new Runnable() {
            private final HashMap<String, Object> param = new HashMap<>(map);

            @Override
            public void run() {
                openMediaApp(param, FuncConstants.VALUE_SCREEN_CEIL);
            }
        });
    }
}
