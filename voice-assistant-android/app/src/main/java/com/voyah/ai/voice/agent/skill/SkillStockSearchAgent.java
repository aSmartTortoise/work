package com.voyah.ai.voice.agent.skill;

import android.text.TextUtils;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.StockInterface;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/7/1 17:33
 * description : 股票查询
 */
@ClassAgent
public class SkillStockSearchAgent extends BaseAgentX {

    private static final String TAG = "SkillStockSearchAgent";

    @Override
    public String AgentName() {
        return "skill_stock#search";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isSequenced() {
        return true;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        ClientAgentResponse response = null;

        String dataInfo = getFlowContextKey(getAgentName() + "_" + KEY_DS_CONTEXT_DATA, flowContext);
        StockInterface stockInterface = DeviceHolder.INS().getDevices().getStock();
        String requestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        if (stockInterface != null) {
            stockInterface.constructCardInfo(dataInfo, requestId);
        }

        String ttsText = "";
        Object ttsObject = flowContext.get(FlowContextKey.FC_TTS_TEXT);
        if (ttsObject instanceof String) {
            ttsText = (String) ttsObject;
            LogUtils.d(TAG, "executeAgent tts:" + ttsText);
        }
        response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ttsText);

        if (stockInterface != null && !stockInterface.isCardInfoEmpty()) {
            response.setUiType(CARD_TYPE_INFORMATION);
            response.setInformationCard(true);
        }

        return response;
    }


    @Override
    public void showUi(String uiType, int location) {
        LogUtils.d(TAG, "showUI uiType:" + uiType);
        if (TextUtils.equals(CARD_TYPE_INFORMATION, uiType)) {
            DeviceHolder.INS().getDevices().getStock().onShowUI(mAgentIdentifier, location);
        }
    }
}
