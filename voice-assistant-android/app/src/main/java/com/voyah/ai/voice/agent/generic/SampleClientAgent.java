package com.voyah.ai.voice.agent.generic;

import android.os.RemoteException;

import com.google.gson.Gson;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.sdk.IAgentCallback;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.agent.AgentResponseCode;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.nlu.NluInfo;
import com.voyah.ds.common.entity.status.ScenarioState;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 外部注册agent包装类
 * @data:2024/4/19
 **/
public class SampleClientAgent extends BaseAgentX {
    private static final String TAG = SampleClientAgent.class.getSimpleName();

//    private static final String FC_NLU_RESULT = FlowContextKey.FC_NLU_RESULT;
//    private static final String FC_TTS_TEXT = FlowContextKey.FC_TTS_TEXT;
//    private static final String FC_IS_STREAM_TTS_TEXT = FlowContextKey.FC_IS_STREAM_TTS_TEXT;

//    private static final String FC_NLU_RESULT = "nluResult";
//    private static final String FC_TTS_TEXT = "ttsText";
//    private static final String FC_IS_STREAM_TTS_TEXT = "isStreamTTSText";

//    private static final String SUFFIX = "_ctxData";

    private String mAgentNameAndParams;

    private String mAgentName;

    private String[] params;
    private IAgentCallback mIAgentCallback;

    //    private Map<String, String> mParamsMap = new HashMap<>();

    public SampleClientAgent(IAgentCallback iAgentCallback) {
        getNameAndParams(iAgentCallback);
    }

    private void getNameAndParams(IAgentCallback iAgentCallback) {
        this.mIAgentCallback = iAgentCallback;
        try {
            this.mAgentNameAndParams = iAgentCallback.getAgentName();
            if (!StringUtils.isBlank(mAgentNameAndParams)) {
                this.params = mAgentNameAndParams.split("\\|");
                this.mAgentName = params[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String AgentName() {
        LogUtils.i(TAG, "mAgentName:" + mAgentName);
        return this.mAgentName;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, mAgentName + " executeAgent ");
        if (!DeviceHolder.INS().getDevices().getCarServiceProp().getCarType().contains("H37")) {
            ClientAgentResponse clientAgentResponse = new ClientAgentResponse(0);
            TTSBean ttsBean = new TTSBean();
            ttsBean.setSelectTTs("抱歉，我还不支持此功能");
            clientAgentResponse.setmTtsObject(ttsBean);
            return clientAgentResponse;
        }

        String data = "";
        String scenario = "";

        try {
            scenario = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE, flowContext);
            LogUtils.d(TAG, "preScenario:" + scenario);
//        //nlu槽位信息
            if (flowContext.containsKey(FlowContextKey.FC_NLU_RESULT)) {
                NluInfo nluInfo = (NluInfo) flowContext.get(FlowContextKey.FC_NLU_RESULT);
                data = new Gson().toJson(nluInfo);
                JSONObject jsonObject = new JSONObject(data);
                //非初始化场景
                if (!StringUtils.equals(scenario, ScenarioState.SCENARIO_STATE_INIT)) {
                    jsonObject.put("scenario", scenario);
                }
                //上下文中是否有三方上传的参数
                if (flowContext.containsKey("thirdParams")) {
                    jsonObject.put("thirdParams", flowContext.get("thirdParams"));
                }
                data = jsonObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ClientAgentResponse(0);
        }


        LogUtils.i(TAG, "executeAgent data:" + data);
        String response = null;
        try {
            if (StringUtils.isBlank(data)) {
                return new ClientAgentResponse(0);
            }
            response = mIAgentCallback.agentExecute(data);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ClientAgentResponse(0);
        }

        int code = 0;
        String tts = "";
        String ttsId = "";
        String scene = "";
        LogUtils.d(TAG, "client response:" + response);
        try {
            if (!StringUtils.isBlank(response)) {
                JSONObject responseObject = new JSONObject(response);
                code = (int) getIntValue("responseCode", responseObject, code);
                tts = (String) getStrValue("tts", responseObject, "");
                ttsId = (String) getStrValue("ttsId", responseObject, "");
                scene = (String) getStrValue("scene", responseObject, "");
            }

            ClientAgentResponse clientAgentResponse = new ClientAgentResponse(code);
            clientAgentResponse.mExtra = new HashMap<>();

            if (!StringUtils.isBlank(ttsId)) {
                TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId);
                clientAgentResponse.setmTtsObject(ttsBean);
            } else if (!StringUtils.isBlank(tts)) {
                clientAgentResponse.setmTtsObject(tts);
            }

            if (!StringUtils.isBlank(scene)) {
                clientAgentResponse.mExtra.put("dsScenario", scene);
                clientAgentResponse.mRetCode = AgentResponseCode.EXTERNAL_SCENARIO_CODE;
            }
            return clientAgentResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return new ClientAgentResponse(0);
        }
    }

    private String getStrValue(String key, JSONObject responseObject, String defaultValue) {
        try {
            if (responseObject.has(key))
                defaultValue = (String) responseObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private int getIntValue(String key, JSONObject responseObject, int defaultValue) {
        try {
            if (responseObject.has(key))
                defaultValue = (int) responseObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
