package com.voyah.ai.voice.agent.calendar;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/4/23 10:12
 * description : 日历应用打开-关闭  为什么目录为calendar，而agent那么为Schedule打头，因为
 *  这些agent控制、依赖的应用是Calendar。但是日程的crud是Schedule。Schedule agent命名和语音助手的
 *  Server代码逻辑有关（可咨询刘轩明）
 */
@ClassAgent
public class ScheduleAppSwitchAgent extends BaseAgentX {

    private static final String TAG = "ScheduleAppSwitchAgent";

    @Override
    public String AgentName() {
        return "schedule_app#switch";
    }

    @Override
    public boolean isSequenced() {
        return true;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        String switchType = getParamKey(paramsMap, Constant.SWITCH_TYPE, 0);
        return new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                "");
    }

    @Override
    public void destroyAgent() {

    }

    @Override
    public void executeOrder(String executeTag, int location) {
        super.executeOrder(executeTag, location);
    }

    @Override
    public void showUi(String uiType, int location) {
        super.showUi(uiType, location);
    }
}
