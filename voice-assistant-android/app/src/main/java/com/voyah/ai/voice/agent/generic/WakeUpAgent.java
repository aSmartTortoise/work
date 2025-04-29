package com.voyah.ai.voice.agent.generic;

import static com.voyah.ai.logic.agent.generic.BaseAgentX.Location.Location_MAP;


import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.launcher.LauncherInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.record.VoiceStatus;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.voice.agent.phone.PhoneUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.wakeup.WakeupType;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 唤醒Agent
 * @data:2024/4/1
 **/
@ClassAgent
public class WakeUpAgent extends BaseAgentX {
    private static final String TAG = WakeUpAgent.class.getSimpleName();
    private ScreenInterface screen = DeviceHolder.INS().getDevices().getSystem().getScreen();

    @Override
    public String AgentName() {
        return "wakeUp";
    }

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }


    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "onVoAIMessage -- WU");
        ParamsGather.isExit = false;
        //todo:语音禁用时弹Toast提示
        LauncherInterface launcherInterface = DeviceHolder.INS().getDevices().getLauncher();
        int privacySecurityStatus;
        //获取隐私保护开关状态 0关闭 1开启
        privacySecurityStatus = DeviceHolder.INS().getDevices().getLauncher().getPrivacySecurity();
        //是否为有效自然唤醒
        boolean isValidNatureWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_VALID_NATURE_WAKEUP, flowContext);
        //是否为全时免唤醒
        boolean isAllTimeWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_NO_WAKEUP_TIME, flowContext);
        int wakeUpType = getIntFlowContextKey(FlowContextKey.FC_WAKE_UP_TYPE, flowContext);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        LogUtils.i(TAG, "wakeUpType:" + wakeUpType + " ,isValidNatureWakeUp:" + isValidNatureWakeUp + " ,isAllTimeWakeUp:" + isAllTimeWakeUp + " ,carType:" + carType);
        //获取唤醒的声源位置
        //awakenLocation
        String awakenLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext);
        LogUtils.i(TAG, "privacyStatus is " + privacySecurityStatus + " ,awakenLocation is " + awakenLocation);
        //三方主动上报是在上报接口发起的tts播报
        if (wakeUpType != WakeupType.WP_INQUIRY) {
            if (!isValidNatureWakeUp && !isAllTimeWakeUp) {
                DeviceHolder.INS().getDevices().getTts().stopCurTts();
            }
        }

        handleScreenOnOff(translateLocation(awakenLocation));
        UIMgr.INSTANCE.wakeUp(translateLocation(awakenLocation), !isValidNatureWakeUp && !isAllTimeWakeUp);
        DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.AWAKE, awakenLocation);
        VoiceStateRecordManager.getInstance().updateWakeStatus(VoiceStatus.status.VOICE_STATE_AWAKE, awakenLocation);
        //todo:指定屏幕AI绘图是否在前台
        boolean isDrawTop = false;
        //适配低代码声源位置key，需要在原始上下文中put下soundLocation
        flowContext.put("soundLocation", Location_MAP.get(awakenLocation));

        if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
            isDrawTop = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround("com.voyah.voice.drawing", DeviceScreenType.fromValue(DeviceHolder.INS().getDevices().getLauncher().getAssignScreen((HashMap<String, Object>) flowContext)));
        } else {
            isDrawTop = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround("com.voyah.voice.drawing", null);
        }
        boolean isPopWindowShowTop = DeviceHolder.INS().getDevices().getLauncher().isScreenPopWindowShowTop(true, DeviceScreenType.fromValue(DeviceHolder.INS().getDevices().getLauncher().getAssignScreen((HashMap<String, Object>) flowContext)));

        if (StringUtils.equals(awakenLocation, "first_row_right") && StringUtils.contains(carType, "H56")) {
            //副驾屏屏保是否已打开
            boolean isShowingScreenSaverOfSecondary = DeviceHolder.INS().getDevices().getLauncher().isShowingScreenSaverOfSecondary();
            if (isShowingScreenSaverOfSecondary)
                DeviceHolder.INS().getDevices().getLauncher().showHideScreenSaver(false);
        } else if (!StringUtils.contains(awakenLocation, "first") && StringUtils.equals(carType, "H56D")) {
            //吸顶屏屏保是否已打开
            boolean isShowingScreenSaverOfCeil = DeviceHolder.INS().getDevices().getLauncher().isShowingScreenSaverOfCeil();
            if (isShowingScreenSaverOfCeil)
                DeviceHolder.INS().getDevices().getLauncher().showHideCeilScreenSaver(false);
        }
//        String tts = "";
        TTSBean ttsBean = new TTSBean();
        // WakeupType 2:自然唤醒  3:全时   5:三方触发主动询问
        if (!isValidNatureWakeUp && !isAllTimeWakeUp && wakeUpType != WakeupType.WP_INQUIRY) {
            if (isDrawTop && !isPopWindowShowTop) {
                ttsBean.setSelectTTs("你可以告诉我你要画什么");
            } else {
                DeviceHolder.INS().getDevices().getTts().requestAudioFocusByUsage();
                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.WAKE_UP[0], TTSAnsConstant.WAKE_UP[1]);
                if (ttsBean.getSelectTTs().contains("@{position}")) {
                    switch (awakenLocation) {
                        case "first_row_left":
                            PhoneUtils.getReplaceTtsBean(ttsBean, "@{position}", "主驾");
                            break;
                        case "first_row_right":
                            PhoneUtils.getReplaceTtsBean(ttsBean, "@{position}", "副驾");
                            break;
                        case "second_row_left":
                        case "second_row_right":
                            PhoneUtils.getReplaceTtsBean(ttsBean, "@{position}", "二排");
                            break;
                        case "third_row_left":
                        case "third_row_right":
                            PhoneUtils.getReplaceTtsBean(ttsBean, "@{position}", "三排");
                            break;
                    }
                }
            }
        }
//        else {
//            VoiceImpl.getInstance().wakeUp(translateLocation(awakenLocation), WakeupCommandParameters.WAKEUP_WEAK);
//        }

        DeviceHolder.INS().getDevices().getVoiceCarSignal().showVolumeToast(awakenLocation);
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        clientAgentResponse.setWakeUp(true);
        return clientAgentResponse;
    }

    private void handleScreenOnOff(int location) {
        if (location == 0 && !screen.isScreenOn(0)) {
            screen.requestScreenOnOff(0, true); //点亮中控
        } else if (location == 1 && !screen.isScreenOn(1)) {
            if (screen.isSupportScreen(DeviceScreenType.PASSENGER_SCREEN)) {
                screen.requestScreenOnOff(1, true);//点亮副驾屏
            } else {
                //没有副驾屏，如果中控屏熄屏，则点亮
                if (!screen.isScreenOn(0)) {
                    screen.requestScreenOnOff(0, true); //点亮中控
                }
            }

        } else if (location > 1) {
            if (screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
                //中控屏和吸顶屏都灭，点亮中控
                if (!screen.isCeilScreenOpen() && !screen.isScreenOn(0)) {
                    screen.requestScreenOnOff(0, true); //点亮中控
                }
            } else {
                //没有吸顶屏，如果中控屏熄屏，则点亮
                if (!screen.isScreenOn(0)) {
                    screen.requestScreenOnOff(0, true); //点亮中控
                }
            }
        }
    }

}
