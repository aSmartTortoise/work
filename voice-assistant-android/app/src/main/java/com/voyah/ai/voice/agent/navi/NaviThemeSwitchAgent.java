package com.voyah.ai.voice.agent.navi;

import android.app.UiModeManager;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviThemeSwitchAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi_theme#switch";
    }




    @Override
    public boolean isNeedNaviInFront() {
        return true;
    }

    @Override
    public boolean isNeedNavigationStarted() {
        return false;
    }

    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String theme = getParamKey(paramsMap, Constant.SWITCH_THEME, 0);
        int themeValue = -1;
        String mode = "";
        if (NaviConstants.DAY.equals(theme)) {
            themeValue = UiModeManager.MODE_NIGHT_NO;
            mode = "白天";
        }
        if (NaviConstants.NIGHT.equals(theme)) {
            themeValue = UiModeManager.MODE_NIGHT_YES;
            mode = "黑夜";
        }
        if (NaviConstants.AUTO.equals(theme)) {
            themeValue = UiModeManager.MODE_NIGHT_AUTO;
            mode = "自动";
        }
        if (themeValue != -1) {
            int currentTheme = DeviceHolder.INS().getDevices().getNavi().getNaviMap().getThemeStyle();
            TTSBean tts;
            if (currentTheme == themeValue) {
                tts = TtsBeanUtils.getTtsBean(3009400, mode);
            } else {
                if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isDealSplitScreening()) {
                    tts = TtsBeanUtils.getTtsBean(3017000);
                } else {
                    tts = TtsBeanUtils.getTtsBean(3009401, mode);
                    DeviceHolder.INS().getDevices().getNavi().getNaviMap().setThemeStyle(themeValue);
                }
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext);
    }
}
