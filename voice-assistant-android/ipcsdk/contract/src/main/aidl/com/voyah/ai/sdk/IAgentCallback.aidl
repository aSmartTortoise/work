package com.voyah.ai.sdk;

interface IAgentCallback {

    String getAgentName();

    String agentExecute(in String context);
}
