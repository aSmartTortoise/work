package com.voyah.ai.logic.agent.generic;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/29
 **/
public interface IAgentExecuteTask {
    String AgentName();

    ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap);

    default void destroyAgent() {

    }

    //先TTS在执行
    void executeOrder(String executeTag, int location);

    void showUi(String uiType, int location);
}
