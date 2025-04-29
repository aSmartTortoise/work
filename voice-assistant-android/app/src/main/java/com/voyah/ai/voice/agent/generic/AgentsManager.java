package com.voyah.ai.voice.agent.generic;

import android.content.Context;
import android.util.Log2;

import com.voyah.ai.common.utils.FileUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author:lcy agent统一类
 * @data:2024/1/29
 **/
public class AgentsManager {
    private static final String TAG = AgentsManager.class.getSimpleName();
//    private static final List<BaseAgentX> agentList = new ArrayList<>();
    private static final List<BaseAgentX> agentList = new ArrayList<>();

    private static AgentsManager agentsManager = new AgentsManager();

    public AgentsManager() {

    }

    public static AgentsManager getInstant() {
        return agentsManager;
    }

    public void init(Context context) {
        List<String> classList = FileUtils.getFromAssetsToList("agents.txt", context);
        for (String className : classList) {
            try {
                Class<?> clazz = Class.forName(className);
                BaseAgentX agent = (BaseAgentX) clazz.newInstance();
//                LogUtils.i(TAG, "agent.getAgentName() is " + agent.getAgentName());
                if (!StringUtils.isBlank(agent.getAgentName())) {
                    agentList.add(agent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log2.i(TAG, "class.size is " + agentList.size());

    }

    public List<BaseAgentX> getAgentList() {
        return agentList;
    }
}
