package com.voyah.ai.voice.agent.navi;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

@ClassAgent
public class NaviPageAgent extends AbstractNaviAgent {
    @Override
    public String AgentName() {
        return "navi#page";
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
    public boolean isAsync() {
        return true;
    }


    @Override
    public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String indexType = getParamKey(paramsMap, Constant.INDEX_TYPE, 0);
        String indexStr = getParamKey(paramsMap, Constant.SELECT_INDEX, 0);
        if (Constant.ABSOLUTE.equals(indexType)) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        } else {
            int index = Integer.parseInt(indexStr);
            NaviResponse<String> naviResponse;
            if (index > 0) {
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().nextPage();
            } else {
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().prevPage();
            }
            if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_TOP) {
                return new ClientAgentResponse(NaviResponseCode.PAGE_SELECT.getValue(), flowContext, TtsBeanUtils.getTtsBean(3012901));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.ALREADY_BOTTOM) {
                return new ClientAgentResponse(NaviResponseCode.PAGE_SELECT.getValue(), flowContext, TtsBeanUtils.getTtsBean(3012801));
            } else if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.PAGE_SELECT.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
            } else {
                return new ClientAgentResponse(NaviResponseCode.PAGE_SELECT.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103)
                );
            }
        }
    }


}
