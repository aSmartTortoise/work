package com.voyah.ai.logic;


import android.util.Log2;

import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.appstore.AppStorePage;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.AbstractAppProcessor;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class AppStoreSwitchImpl extends AbstractAppProcessor {
    private static final String TAG = "AppStoreSwitchImpl";

    @Override
    public String process(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        String uiName = getOneMapValue("ui_name", map);
        AppStorePage targetPage = AppStorePage.fromName(uiName);
        String originScreen = getOriginScreen(map);
        String targetScreen = originScreen;
        int location = getAwakenLocation(map);
        if (isSecondRound(map)) {
            if (!isSecondConfirm(map)) {
                return TtsBeanUtils.getTtsBean(1100036).getSelectTTs();
            } else {
                targetScreen = FuncConstants.VALUE_SCREEN_CEIL;
            }
        }
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.fromValue(originScreen))) {
            return TtsBeanUtils.getTtsBean(1100028).getSelectTTs();
        }
        if (StringUtils.isBlank(originScreen)) {
            targetScreen = getScreenFromSound(location);
        }
        boolean inFront = DeviceHolder.INS().getDevices().getAppStore().isInFront(DeviceScreenType.fromValue(targetScreen));
        int currentPage = AppStorePage.PageNone.getValue();
        if (inFront) {
            currentPage = DeviceHolder.INS().getDevices().getAppStore().getCurrentPage(DeviceScreenType.fromValue(targetScreen));
        }

        Log2.i(TAG, "originScreen:" + originScreen + ",targetScreen:" + targetScreen + ",targetPage:" + targetPage.getChName() + ",currentPage:" + currentPage);
        if ("open".equals(switchType)) {
            if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(targetScreen)) {
                if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
                    return "出于安全考虑，当前场景不支持此操作。";
                }
            }
            if (FuncConstants.VALUE_SCREEN_CEIL.equals(targetScreen)) {
                if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isCeilScreenOpen()) {
                    if (!isSecondRound(map)) {
                        map.put(FlowChatContext.FlowChatResultKey.RESULT_CODE, 20000);
                        map.put(FlowChatContext.FlowChatResultKey.RESULT_SCENE, "State.DC_OP_CONFIRM");
                        return TtsBeanUtils.getTtsBean(1100033, ApplicationConstant.APP_NAME_APPSTORE).getSelectTTs();
                    } else {
                        DeviceHolder.INS().getDevices().getSystem().getScreen().openCeilScreen(15000);
                    }
                }
            }
            //未指定page
            if (targetPage == AppStorePage.PageNone) {
                if (inFront) {
                    return TtsBeanUtils.makeOpenSuccessTts(originScreen, targetScreen, location, true, targetPage.getChName()).getSelectTTs();
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStore(DeviceScreenType.fromValue(targetScreen));
                    return TtsBeanUtils.makeOpenSuccessTts(originScreen, targetScreen, location, false, targetPage.getChName()).getSelectTTs();
                }
            } else {
                //要打开的页就是当前页
                if (targetPage.getValue() == currentPage) {
                    return TtsBeanUtils.makeOpenSuccessTts(originScreen, targetScreen, location, true, targetPage.getChName()).getSelectTTs();
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), targetPage.getValue());
                    return TtsBeanUtils.makeOpenSuccessTts(originScreen, targetScreen, location, false, targetPage.getChName()).getSelectTTs();
                }
            }


        } else if ("close".equals(switchType)) {
            if (inFront) {
                if (targetPage.getValue() == currentPage || (currentPage == 4 && targetPage == AppStorePage.PageMine) || targetPage == AppStorePage.PageNone) {
                    DeviceHolder.INS().getDevices().getAppStore().closeAppStore(DeviceScreenType.fromValue(targetScreen));
                    return TtsBeanUtils.makeCloseSuccessTts(originScreen, targetScreen, location, false, targetPage.getChName()).getSelectTTs();
                } else {
                    return TtsBeanUtils.makeCloseSuccessTts(originScreen, targetScreen, location, true, targetPage.getChName()).getSelectTTs();
                }
            } else {
                if (targetPage == AppStorePage.PageNone) {
                    return TtsBeanUtils.makeCloseSuccessTts(originScreen, targetScreen, location, true, ApplicationConstant.APP_NAME_APPSTORE).getSelectTTs();
                } else {
                    return TtsBeanUtils.makeCloseSuccessTts(originScreen, targetScreen, location, true, targetPage.getChName()).getSelectTTs();
                }
            }
        }
        return TtsBeanUtils.getTtsBean(2057100).getSelectTTs();
    }

    @Override
    public boolean consume(HashMap<String, Object> map) {
        String appName = getOneMapValue("app_name", map);
        return ApplicationConstant.APP_NAME_APPSTORE.equals(appName);
    }

}
