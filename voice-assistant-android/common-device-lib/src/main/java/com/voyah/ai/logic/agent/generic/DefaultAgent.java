package com.voyah.ai.logic.agent.generic;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/14
 **/
public class DefaultAgent extends BaseAgentX {
    private static final String TAG = DefaultAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "douDi";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        String defaultQuery = getFlowContextKey(FlowContextKey.FC_WHOLE_QUERY, flowContext);
        LogUtils.e(TAG, "------------DefaultAgent--------- defaultQuery is " + defaultQuery);
//        if (!StringUtils.isEmpty(defaultQuery))
//            uiInterface.setTypeWriterText(defaultQuery, TypeTextStyle.SECONDARY);
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean("1100027");//PhoneUtils.getTtsBean(TTSAnsConstant.UNDERSTAND[0], TTSAnsConstant.UNDERSTAND[1]);
        ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
//        clientAgentResponse.setInValid(true);
        return clientAgentResponse;
    }

}
