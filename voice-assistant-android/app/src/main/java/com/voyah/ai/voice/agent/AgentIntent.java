package com.voyah.ai.voice.agent;

import com.voyah.ai.voice.sdk.api.task.AgentX;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:lcy
 * @data:2024/4/1
 **/
public abstract class AgentIntent {
    //domain , IntentManager
    protected List<AgentX> list = new ArrayList<>();

    public abstract void initAgent();

    public List<AgentX> getAgentList(){
        return list;
    }
}
