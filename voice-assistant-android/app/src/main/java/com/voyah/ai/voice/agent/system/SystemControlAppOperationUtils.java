package com.voyah.ai.voice.agent.system;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.appstore.AppStorePage;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.MemoryCacheUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SystemControlAppOperationUtils {

    private static final String TAG = "SystemControlApp";

    public static final String APP_NAME = TAG + "_APP_NAME";

    public static final String OPERATION = TAG + "_OPERATION";

    public static boolean isSecondConfirm(Map<String, List<Object>> paramsMap) {
        String chooseType = BaseAgentX.getParamKey(paramsMap, "choose_type", 0);
        LogUtils.i(TAG, "isSecondConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    public static boolean isSecondRound(Map<String, List<Object>> paramsMap) {
        String chooseType = BaseAgentX.getParamKey(paramsMap, "choose_type", 0);
        LogUtils.i(TAG, "isSecondRound chooseType is " + chooseType);
        return !StringUtils.isEmpty(chooseType);
    }


    public static ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "flowContext:" + GsonUtils.toJson(flowContext));
        LogUtils.i(TAG, "paramsMap:" + GsonUtils.toJson(paramsMap));
        String operation;
        String appName;
        if (!isSecondRound(paramsMap)) {
            operation = BaseAgentX.getParamKey(paramsMap, Constant.OPERATION_TYPE, 0);
            appName = BaseAgentX.getParamKey(paramsMap, Constant.APP_NAME, 0);
            MemoryCacheUtils.saveCache(OPERATION, operation);
            MemoryCacheUtils.saveCache(APP_NAME, appName);
        } else {
            operation = (String) MemoryCacheUtils.getCache(OPERATION);
            appName = (String) MemoryCacheUtils.getCache(APP_NAME);
        }
        String packageName = DeviceHolder.INS().getDevices().getSystem().getApp().getPackageName(appName);
        boolean isSystemApp = DeviceHolder.INS().getDevices().getSystem().getApp().isSystemApp(packageName);
        boolean isInAppStore = DeviceHolder.INS().getDevices().getAppStore().isInAppStore(appName);
        boolean isInstalled = !TextUtils.isEmpty(packageName);
        String originScreen = BaseAgentX.getParamKey(paramsMap, FuncConstants.KEY_SCREEN_NAME, 0);
        if (FuncConstants.VALUE_SCREEN.equals(originScreen)) {
            originScreen = "";
        }
        if (TextUtils.isEmpty(originScreen)) {
            String position = BaseAgentX.getParamKey(paramsMap, DCContext.MAP_KEY_SLOT_POSITION, 0);
            if (!TextUtils.isEmpty(position)) {
                int location = BaseAgentX.translateLocation(position);
                originScreen = getScreenFromSound(location);
            }
        }
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.fromValue(originScreen))) {
            return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, TtsBeanUtils.getTtsBean(1100028));
        }
        String targetScreen = originScreen;
        if (TextUtils.isEmpty(originScreen)) {
            targetScreen = getScreenFromSound(BaseAgentX.getAwakenLocation(flowContext));
        }
        if (isSecondRound(paramsMap)) {
            if (!isSecondConfirm(paramsMap)) {
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, TtsBeanUtils.getTtsBean(1100036));
            } else {
                targetScreen = FuncConstants.VALUE_SCREEN_CEIL;
            }
        }

        if (FuncConstants.VALUE_SCREEN_CENTRAL.equals(targetScreen)) {
            if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, "出于安全考虑，当前场景不支持此操作。");
            }
        }

        if (FuncConstants.VALUE_SCREEN_CEIL.equals(targetScreen)) {
            if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isCeilScreenOpen()) {
                if (!isSecondRound(paramsMap)) {
                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(20000, flowContext, TtsBeanUtils.getTtsBean(1100033, "岚图商城"));
                    clientAgentResponse.setDsScenario("State.DC_CEILING_SCREEN_CONFIRM2");
                    return clientAgentResponse;
                } else {
                    DeviceHolder.INS().getDevices().getSystem().getScreen().openCeilScreen(15000);
                }
            }
        }
        LogUtils.i(TAG, "operation:" + operation + ",appName:" + appName + ",packageName:" + packageName + ",isSystemApp:" + isSystemApp + ",isInAppStore:" + isInAppStore);
        int code;
        switch (operation) {
            case Constant.OPERATION_DOWNLOAD: {
                TTSBean tts;
                if (!TextUtils.isEmpty(appName)) {
                    if (isSystemApp) {
                        tts = TtsBeanUtils.getTtsBean(4044203);
                    } else {
                        code = DeviceHolder.INS().getDevices().getAppStore().downLoadApp(DeviceScreenType.fromValue(targetScreen), appName);
                        if (DeviceHolder.INS().getDevices().getAppStore().isSuccessCode(code)) {
                            if (isInstalled) {
                                tts = TtsBeanUtils.getTtsBean(4044200);
                            } else {
                                tts = TtsBeanUtils.getTtsBean(5021300);
                            }
                        } else {
                            tts = TtsBeanUtils.getTtsBean(4044002);
                        }
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), AppStorePage.PageAppStore.getValue());
                    tts = TtsBeanUtils.getTtsBean(5021300);
                }
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
            }
            case Constant.OPERATION_SEARCH: {
                TTSBean tts = null;
                if (!TextUtils.isEmpty(appName)) {
                    if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                        if (DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().isMedia(appName)) {
                            DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusic().openMediaAppByAppName(appName, true);
                            tts = TtsBeanUtils.getTtsBean(1100002, appName);
                        } else if (isInstalled) {
                            DeviceHolder.INS().getDevices().getSystem().getApp().openApp(packageName, DeviceScreenType.fromValue(targetScreen));
                            tts = TtsBeanUtils.getTtsBean(1100002, appName);
                        }
                    }
                    if (tts == null) {
                        code = DeviceHolder.INS().getDevices().getAppStore().searchApp(DeviceScreenType.fromValue(targetScreen), appName);
                        if (DeviceHolder.INS().getDevices().getAppStore().isSuccessCode(code)) {
                            tts = TtsBeanUtils.getTtsBean(4044001);
                        } else {
                            tts = TtsBeanUtils.getTtsBean(4044002);
                        }
                    }
                } else {
                    if (DeviceHolder.INS().getDevices().getAppStore().getCurrentPage(DeviceScreenType.fromValue(targetScreen)) == AppStorePage.PageAppStore.getValue()) {
                        tts = TtsBeanUtils.getTtsBean(4044101);
                    } else {
                        DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), AppStorePage.PageAppStore.getValue());
                        tts = TtsBeanUtils.getTtsBean(4044100);
                    }
                }
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
            }
            case Constant.OPERATION_UPDATE: {
                TTSBean tts;
                if (!TextUtils.isEmpty(appName)) {
                    if (isSystemApp) {
                        tts = TtsBeanUtils.getTtsBean(4044604);
                    } else {
                        code = DeviceHolder.INS().getDevices().getAppStore().searchApp(DeviceScreenType.fromValue(targetScreen), appName);
                        if (DeviceHolder.INS().getDevices().getAppStore().isSuccessCode(code)) {
                            tts = TtsBeanUtils.getTtsBean(5021300);
                        } else {
                            tts = TtsBeanUtils.getTtsBean(4044002);
                        }
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), AppStorePage.PageMine.getValue());
                    tts = TtsBeanUtils.getTtsBean(5021300);
                }
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
            }
            case Constant.OPERATION_UNINSTALL: {
                TTSBean tts;
                if (!TextUtils.isEmpty(appName)) {
                    if (isSystemApp) {
                        tts = TtsBeanUtils.getTtsBean(4044403);
                    } else {
                        if (!isInstalled) {
                            if (isInAppStore) {
                                tts = TtsBeanUtils.getTtsBean(4044401);
                            } else {
                                tts = TtsBeanUtils.getTtsBean(4044002);
                            }
                        } else {
                            tts = TtsBeanUtils.getTtsBean(5021300);
                        }
                        DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), AppStorePage.PageMine.getValue());
                    }
                } else {
                    DeviceHolder.INS().getDevices().getAppStore().openAppStorePage(DeviceScreenType.fromValue(targetScreen), AppStorePage.PageMine.getValue());
                    tts = TtsBeanUtils.getTtsBean(5021300);
                }
                return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, tts);
            }
        }

        return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext, "");
    }


    public static String getScreenFromSound(int location) {
        String screenName;
        switch (location) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.PASSENGER_SCREEN)) {
                    screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                } else {
                    screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                }
                break;
            default:
                if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
                    screenName = FuncConstants.VALUE_SCREEN_CEIL;
                } else {
                    screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                }
                break;
        }
        return screenName;
    }

}
