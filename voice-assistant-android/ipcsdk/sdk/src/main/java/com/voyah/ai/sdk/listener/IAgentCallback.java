package com.voyah.ai.sdk.listener;

/**
 * @author:lcy
 * @data:2024/4/19
 **/
public interface IAgentCallback {
    String getAgentName();//向客户端获取agentName、及客户端需要的数据key 格式:agentName|key1|key2|key3

    int agentExecute(String context);//通知客户端执行-带有返回结果，是否需要添加返回信息待定
}
