package com.voyah.ai.voice.agent.generic;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 退出二次交互agent
 * @data:2024/10/28
 **/

@ClassAgent
public class ExitMultiInteractionAgent extends BaseAgentX {

    public static final String FC_EXIT_MULTI_INTERACTION_REASON = "exitMultiInteractionReason";
    //语义无效,命中可见,退二次交互
    public static final int EXIT_MULTI_INTERACTION_HIT_VIEW_COMMAND = 1;
    //gen#close通用取消,退二次交互
    public static final int EXIT_MULTI_INTERACTION_GEN_CLOSE = 2;
    //域内跨意图,退二次交互
    public static final int EXIT_MULTI_INTERACTION_IN_DOMAIN_CROSS = 3;
    private static final String TAG = ExitMultiInteractionAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "exitMultiInteraction";
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.d(TAG, "exitMultiInteraction");

        int exitReason = getIntFlowContextKey(FlowContextKey.FC_EXIT_MULTI_INTERACTION_REASON, flowContext);
        if (EXIT_MULTI_INTERACTION_HIT_VIEW_COMMAND != exitReason) {
            int screenLocation = BaseAgentX.getAwakenLocation(flowContext);
            UIMgr.INSTANCE.forceExitAct("exitMultiInteraction", screenLocation);
            //强制关闭卡片
            UIMgr.INSTANCE.dismissCardOnScreen(getAwakenLocation(flowContext));
            DeviceHolder.INS().getDevices().getTts().stopCurTts();
        }
        return new ClientAgentResponse(0);
    }

}
