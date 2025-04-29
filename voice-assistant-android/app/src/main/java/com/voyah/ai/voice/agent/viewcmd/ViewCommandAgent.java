package com.voyah.ai.voice.agent.viewcmd;

import static com.voice.sdk.constant.ConfigsConstant.WAKEUP_LOCATION_MAP;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.WakeupDirection;
import com.voice.sdk.device.viewcmd.ViewCmdInterface;
import com.voyah.ai.common.utils.LogUtils;
import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;

@ClassAgent
public class ViewCommandAgent extends BaseAgentX {

    private static final String TAG = ViewCommandAgent.class.getSimpleName();

    @Override
    public String AgentName() {
        return "viewCommand";
    }

    @Override
    public int getPriority() {
        return 4;
    }


    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        ViewCmdInterface viewCmdInterface = DeviceHolder.INS().getDevices().getViewCmd();
        ViewCmdResult result = new ViewCmdResult();
        if (flowContext.containsKey(FlowContextKey.FC_WHOLE_QUERY)) {
            result.query = (String) flowContext.get(FlowContextKey.FC_WHOLE_QUERY);
        }
        if (flowContext.containsKey(FlowContextKey.FC_VIEW_COMMAND_INFOS)) {
            List<?> viewCmdArray = (List<?>) flowContext.get(FlowContextKey.FC_VIEW_COMMAND_INFOS);
            if (viewCmdArray != null && viewCmdArray.size() > 0) {
                Object viewCmd = viewCmdArray.get(0);
                if (viewCmd instanceof String) {
                    JSONObject viewCmdObj = JSONObject.parseObject((String) viewCmd);
                    result.text = viewCmdObj.getString("text");
                    result.prompt = viewCmdObj.getString("prompt");
                    String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
                    LogUtils.d(TAG, "soundLocation:" + soundLocation + ", viewCmdArray:" + viewCmdArray);
                    int direction = DeviceHolder.INS().getDevices().getDialogue().getDirection();
                    if (!TextUtils.isEmpty(soundLocation)) {
                        WakeupDirection wakeupDirection = WAKEUP_LOCATION_MAP.get(soundLocation);
                        if (wakeupDirection != null) {
                            direction = wakeupDirection.getDirection();
                        }
                    }
                    result.direction = direction;
                    viewCmdInterface.handleViewCommand(result);
                }
            }
        }
        return new ClientAgentResponse(Constant.CommonResponseCode.SUCCESS, flowContext);
    }
}
