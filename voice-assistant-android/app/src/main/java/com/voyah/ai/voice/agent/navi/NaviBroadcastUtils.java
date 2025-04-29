package com.voyah.ai.voice.agent.navi;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

public class NaviBroadcastUtils {
    private static final String TAG = "NaviBroadcastUtils";

    private NaviBroadcastUtils() {

    }


    public static ClientAgentResponse changeBroadcast(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String operation = AbstractNaviAgent.getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        String mode = AbstractNaviAgent.getParamKey(paramsMap, NaviConstants.BROADCAST_MODE, 0);
        if (mode.isEmpty()) {
            mode = AbstractNaviAgent.getParamKey(paramsMap, NaviConstants.SWITCH_MODE, 0);
        }
        LogUtils.i(TAG, "operation:" + operation + ",mode:" + mode);
        switch (operation) {
            case Constant.OPEN:
            case Constant.RETURN:
                if (NaviConstants.BROADCAST_MODE_PROMPT.equals(mode) || NaviConstants.BROADCAST_MODE_BRIEF.equals(mode) || NaviConstants.BROADCAST_MODE_DETAIL.equals(mode)) {
                    int modeValue = NaviConstants.SpeakModeType.SPEAK_MODE_PROMPT;
                    if (NaviConstants.BROADCAST_MODE_BRIEF.equals(mode)) {
                        modeValue = NaviConstants.SpeakModeType.SPEAK_MODE_BRIEF;
                    }
                    if (NaviConstants.BROADCAST_MODE_DETAIL.equals(mode)) {
                        modeValue = NaviConstants.SpeakModeType.SPEAK_MODE_DETAIL;
                    }
                    DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(false);
                    NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMode(modeValue);
                    TTSBean tts;
                    if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_OPEN || naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_CLOSE) {
                        tts = TtsBeanUtils.getTtsBean(3010201, NaviConstants.SpeakModeType.getSpeakModeDesc(modeValue));
                    } else if (naviResponse.isSuccess()) {
                        tts = TtsBeanUtils.getTtsBean(3010200, NaviConstants.SpeakModeType.getSpeakModeDesc(modeValue));
                    } else {
                        tts = TtsBeanUtils.getTtsBean(3013103);
                    }
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);

                } else if (NaviConstants.BROADCAST_MODE_STANDARD.equals(mode)) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010300));
                } else {
                    if (DeviceHolder.INS().getDevices().getNavi().getNaviSetting().isSpeakMute()) {
                        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(false);
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010501));
                    }

                }
            case Constant.CLOSE:
                if (TextUtils.isEmpty(mode)) {
                    if (DeviceHolder.INS().getDevices().getNavi().getNaviSetting().isSpeakMute()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010600));
                    } else {
                        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(true);
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
                    }
                } else {
                    DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(true);
                    switch (mode) {
                        case NaviConstants.BROADCAST_MODE_STANDARD:
                        case NaviConstants.BROADCAST_MODE_DETAIL:
                        case NaviConstants.BROADCAST_MODE_PROMPT:
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010700));
                        default:
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010900));
                    }
                }
            case Constant.CHANGE:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(false);
                NaviResponse<Integer> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMode(-1);
                if (naviResponse != null && naviResponse.isSuccess()) {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3010200, NaviConstants.SpeakModeType.getSpeakModeDesc(naviResponse.getData())));
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }

        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
    }

}
