package com.voyah.ai.voice.agent.weather;

import com.example.filter_annotation.ClassAgent;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/4/10 10:08
 * description : 打开-关闭天气应用
 */

@ClassAgent
public class WeatherAppSwitchAgent extends BaseAgentX {

    private static final String TAG = "WeatherAppSwitchAgent";

    @Override
    public String AgentName() {
        return "weather_app#switch";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext,
                                            Map<String, List<Object>> paramsMap) {

        ClientAgentResponse response = new ClientAgentResponse(
                Constant.CommonResponseCode.SUCCESS,
                flowContext,
                ""
        );
        return response;
    }


}
