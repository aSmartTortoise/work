package com.voyah.ai.logic.agent.flowchart;

import android.text.TextUtils;

import com.voice.sdk.PathUtil;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.Devices;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.agent.flowchart.findchart.ContextDate;
import com.voyah.ai.logic.agent.flowchart.helper.LocationHelper;
import com.voyah.ai.logic.agent.flowchart.helper.ShuntHelper;
import com.voyah.ai.logic.agent.flowchart.ttsEnd.TTsEndCallBack;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.logic.agent.generic.Constant;
import com.voyah.ai.logic.dc.manager.DeviceManager;
import com.voyah.ai.logic.dc.manager.DevicesIntentManager;
import com.voyah.ai.voice.platform.agent.api.VehicleControlToolApi;
import com.voyah.ai.voice.platform.agent.api.bean.TTSEndExeMethod;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ai.voice.platform.agent.api.flowchart.findchart.FindFlowChartHelper;
import com.voyah.ai.voice.platform.agent.api.function.FunctionCodec;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.sdk.api.task.AgentInfoHolder;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.nlu.NluInfo;
import com.voyah.ds.common.entity.nlu.Slot;
import com.voyah.ds.common.entity.status.ScenarioState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlowChartAgent extends BaseAgentX {
    private static final String TAG = FlowChartAgent.class.getSimpleName();
    private DevicesIntentManager devicesIntentManager;
    private LocationHelper locationHelper = new LocationHelper();
    private FindFlowChartHelper findFlowChartHelper = new FindFlowChartHelper();
    private Map<String, Object> secondGraphMap;

    private ShuntHelper shuntHelper = new ShuntHelper();

    //保存临时的函数。
    private String oldNlu;
    //二次交互的位置信息。
    private String position;

    public FlowChartAgent() {

//        DeviceManager.getInstance().init(AppContext.instant);
        DeviceManager.getInstance().init();
        devicesIntentManager = DeviceManager.getInstance().getDevicesIntentManager();
    }

    @Override
    public String AgentName() {
        return "flowchart";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "-----run--FlowChartAgent------");
        LogUtils.i(TAG, "request_id:" + flowContext.get(FlowContextKey.FC_REQ_ID));

        String workflowGraph = "";
        if (paramsMap != null) {
            List<Object> list = paramsMap.get("agentInfoHolders");
            if (list != null) {
                AgentInfoHolder agentInfoHolder = (AgentInfoHolder) list.get(0);
                workflowGraph = agentInfoHolder.getAgentRes();
            } else {
                LogUtils.d(TAG, "list为null");
            }

        } else {
            LogUtils.d(TAG, "paramsMap为null");
        }

//        String workflowGraph = ((AgentInfoHolder) ((paramsMap.get("agentInfoHolders")).get(0))).getAgentRes();


        //此处需要对数据解析，把数据解析到map里
        NluInfo nluInfo = (NluInfo) flowContext.get(FlowContextKey.FC_NLU_RESULT);
        HashMap<String, Object> params = new HashMap<>();

        //函数参数干预。
        FunctionCodec preFunctionCodec = FunctionCodec.decode(nluInfo.nluStr);
        String functionName = preFunctionCodec.getFunctionName();
        Map<String, String> curParamsMap = preFunctionCodec.getParams();
        if (functionName.startsWith("air_temp") && curParamsMap.containsKey("number_level")) {
            String nlu = nluInfo.nluStr;
            String repNlu = nlu.replace("number_level", "number_temp");
            nluInfo.nluStr = repNlu;
        }


        //适配本地找图，为了本地测试
        if (TextUtils.isEmpty(workflowGraph)) {
            LogUtils.d(TAG, "传下来的流程图是null的。" + workflowGraph);
            ContextDate.GRAPH_SAVE_PATH = PathUtil.getGraphPath();
            workflowGraph = findFlowChartHelper.findFlowChart(nluInfo.nluStr);
        }


        //把NLu的数据转换成低代码需要的参数intent,air_****-->air
        String intent;
        //把场景值传入到参数里
        params.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE, flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE));
        LogUtils.i(TAG, "FlowContextKey.SC_DEVICE_SCENARIO_STATE的值是：" + flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE));
        //通过场景值判断是否是二次交互
        if (!flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE).equals(ScenarioState.SCENARIO_STATE_INIT)) {
            LogUtils.i(TAG, "position:" + (position != null ? position : "is null"));
            if (position != null && !position.isEmpty()) {
                params.put("position", position);
                LogUtils.i(TAG, "二次交互场景值情况下，position设置成：" + position);
            }
        }
        //通过图的内容判断是否是二次交互
        if (workflowGraph.contains("confirm-confirm") || workflowGraph.contains("cancel-cancel")) {
            params.put(FlowChatContext.FlowChatResultKey.RESULT_MAP, secondGraphMap);
            //1.获取到上次的图,并覆盖当前的图
            //FlowChatContext.FlowChatResultKey.SECOND_INTERACTION_IN_MAP_GRAPH_KEY//大师sdk适配
            workflowGraph = (String) secondGraphMap.get("second_interaction_in_map_graph_key");
            LogUtils.i(TAG, "==================" + secondGraphMap.toString());
            String nlu = (String) secondGraphMap.get(FlowChatContext.DSKey.NLU_INFO);
            if (nlu.startsWith("flowChart#choose")) {
                intent = getIntent(oldNlu);
                LogUtils.d(TAG, "多次二次交互读取老的nlu:" + oldNlu);
            } else {
                intent = getIntent(nlu);
                LogUtils.d(TAG, "第一次，二次交互，读取nlu：" + nlu);
            }

            params.put(DCContext.INTENT, intent);

            FunctionCodec functionCodec = FunctionCodec.decode((String) secondGraphMap.get(FlowChatContext.DSKey.NLU_INFO));
            Map<String, String> paramMap = functionCodec.getParams();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
            LogUtils.d(TAG, "second position1:" + position);
            if (!paramMap.containsKey("position") && position != null && !position.isEmpty()) {
                params.put("position", position);
                LogUtils.d(TAG, "second position2:" + position);
            }
        } else {
            intent = getIntent(nluInfo.nluStr);
            params.put(DCContext.INTENT, intent);
        }
        LogUtils.i(TAG, "cur intent is ：" + intent);

        params.put(DCContext.NLU_INFO, nluInfo.nluStr);
        //把nlu的槽位信息放入map里。
        if (nluInfo.slotList != null) {
            for (int i = 0; i < nluInfo.slotList.size(); i++) {
                Slot slot = nluInfo.slotList.get(i);
                params.put(slot.slotType, slot.slotValue);
            }
        }
        //根据当前的车型，传给低代码当前座椅位置信息。
        String str = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (str.equals("H56C") || str.equals("H56D")) {
            //6座位
            params.put("position_size", 6);
        } else {
            //4座位
            params.put("position_size", 4);
        }

        String awakenLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext);
        params.put("awakenLocation", awakenLocation);
        //声源位置的处理，判断当前nlu里是否有位置信息，没有则把声源位置信息加进去。并且把声源位置信息记录下来。

        String soundLocation = (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION);
        params.put("soundLocation", soundLocation);
        //把nlu也传到上下文里。
        params.put("nlu", nluInfo.nluStr);

        //判断是否是唤醒后首条query
        if (flowContext.containsKey(FlowContextKey.FC_FIRST_QUERY_AFTER_WAKEUP)) {
            params.put(FlowContextKey.FC_FIRST_QUERY_AFTER_WAKEUP, flowContext.get(FlowContextKey.FC_FIRST_QUERY_AFTER_WAKEUP));
        }


        //把这块代码放到低代码里了

        //图结构
        String flowChart = workflowGraph;
//        if (TextUtils.isEmpty(flowChart)) {
//            ContextDate.GRAPH_SAVE_PATH = FlowChartManager.getInstance().getGraphPath();
//            flowChart = findFlowChartHelper.findFlowChart(nluInfo.nluStr);
//        }
        LogUtils.i(TAG, "flow chart context ：" + flowChart);
        //获取devices
        Devices devices = devicesIntentManager.getDevices(intent);
        LogUtils.i(TAG, "当前的devices是否位null:" + (devices == null));
        //todo 调用低代码的执行方法
        LogUtils.i(TAG, "to low code params value is：" + params.toString());
        Map<String, Object> resultMap = VehicleControlToolApi.getInstance().executeMethod(flowChart, params, devices);
        if (resultMap == null) {

            LogUtils.e(TAG, "flowchart execute exception , return result is null");
            return new ClientAgentResponse(Constant.CommonResponseCode.ERROR, flowContext);
        }

        LogUtils.d(TAG, "Intervention processing completed");


        Object tts = resultMap.get(FlowChatContext.FlowChatResultKey.TTS);
        if (tts != null) {
            LogUtils.d(TAG, "tts context is：" + tts.toString());
        } else {
            LogUtils.d(TAG, "tts context is null");
        }
        //判断是否有tts播放完，再执行的逻辑ContextDate.MAP_KEY_HIT_METHOD
        if (resultMap.containsKey("hit_tts_method")) {
            ClientAgentResponse clientAgentResponse = new ClientAgentResponse(0, flowContext, tts);
            clientAgentResponse.settTsEndCallBack(new TTsEndCallBack() {
                @Override
                public void excute() {
                    TTSEndExeMethod ttsEndExeMethod = (TTSEndExeMethod) resultMap.get("hit_tts_method");
                    Object o = ttsEndExeMethod.getMethodBean();
                    Map<String, Object> map = (Map<String, Object>) ttsEndExeMethod.getMethodMap();
                    String methodName = ttsEndExeMethod.getMethodName();
                    try {
                        Class clazz = o.getClass();
                        LogUtils.d(TAG, "当前的类名是：" + clazz.getName() + " 需要执行的方法名：" + methodName);
                        Method method = clazz.getDeclaredMethod(methodName, HashMap.class);
                        method.setAccessible(true);
                        // 调用 执行 方法
                        method.invoke(o, map);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.d(TAG, e.getMessage().toString());
                    }

                }
            });

            return clientAgentResponse;
        }

        //把单独需要传给上下文的数据,在流程图中设置的一口气全都上传上去。
        if (resultMap.containsKey(FlowChatContext.FlowChatResultKey.RESULT_TO_DS)) {
            HashMap<String, Object> resultToDsMap = (HashMap<String, Object>) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_TO_DS);
            Iterator<String> iterator = resultToDsMap.keySet().iterator();
            //这块做的操作其实就是推出语音交互。
//
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.equals("stopContinueListen")) {
                    VoiceImpl.getInstance().exDialog();
                }
            }
        }
        //对结果进行处理
        if (resultMap.containsKey(FlowChatContext.FlowChatResultKey.RESULT_CODE) &&
                (int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE) != -1 &&
                (int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE) != 0) {
            LogUtils.i(TAG, "判读当前返回值里是否包含，场景值：" + resultMap.containsKey(FlowChatContext.FlowChatResultKey.RESULT_SCENE));
            LogUtils.i(TAG, "二次交互返回值触发：" + (int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE));
            //是二次交互的处理，先把需要传给ds的上下文传递出去
            secondGraphMap = (Map<String, Object>) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
            LogUtils.i(TAG, "==================" + secondGraphMap.toString());
            String nlu = (String) secondGraphMap.get(FlowChatContext.DSKey.NLU_INFO);
            if (!nlu.startsWith("flowChart#choose")) {
                oldNlu = nlu;
                LogUtils.d(TAG, "二次交互，保存上次的函数：" + oldNlu);
            }
            //保存上次的position信息。
            position = secondGraphMap.get("position") == null ? "" : (String) secondGraphMap.get("position");
            LogUtils.d(TAG, "second position3:" + position);
            //拿到code码和tts内容返回。
            ClientAgentResponse clientAgentResponse;
            if (tts == null) {
                if (!(tts instanceof TTSBean) && TextUtils.isEmpty((CharSequence) tts)) {
                    clientAgentResponse = new ClientAgentResponse((int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE), flowContext);
//                clientAgentResponse.setDsScenario("State.DC_OP_CONFIRM");
                    clientAgentResponse.setDsScenario((String) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_SCENE));
                    return clientAgentResponse;
                }

            }
            if (tts instanceof TTSBean) {
                clientAgentResponse = new ClientAgentResponse((int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE), flowContext, (TTSBean) tts);
            } else {
                clientAgentResponse = new ClientAgentResponse((int) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_CODE), flowContext, (String) tts);
            }

            LogUtils.i(TAG, "设置的场景值是：" + (String) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_SCENE));
//            clientAgentResponse.setDsScenario("State.DC_OP_CONFIRM");
            clientAgentResponse.setDsScenario((String) resultMap.get(FlowChatContext.FlowChatResultKey.RESULT_SCENE));
            return clientAgentResponse;
        }

//        if (tts == null || tts.isEmpty()) {
//            return new ClientAgentResponse(0, flowContext);
//        }
        if (tts == null) {
            LogUtils.d(TAG, "tts为null的兜底处理");
            if (!(tts instanceof TTSBean) && TextUtils.isEmpty((CharSequence) tts)) {
                return new ClientAgentResponse(0, flowContext);
            } else {
                return new ClientAgentResponse(0, flowContext);
            }
        }

        ClientAgentResponse clientAgentResponse;
        if (tts instanceof TTSBean) {
            clientAgentResponse = new ClientAgentResponse(0, flowContext, (TTSBean) tts);

        } else {
            clientAgentResponse = new ClientAgentResponse(0, flowContext, (String) tts);
        }
        //对tts的播放位置进行处理
        if (resultMap.containsKey("tts_play_position")) {
            String ttsSoundLocation = (String) resultMap.get("tts_play_position");
            clientAgentResponse.setNearTtsLocation(Integer.parseInt(ttsSoundLocation));

        }
        return clientAgentResponse;
    }

    /**
     * 获取低代码对应的intent，就是把一级实体当作设备封装的分类。
     * air#switch@switch_type:open  --> air
     * air_temp#adjust@adjust_type:increase --> air
     *
     * @param nluStr
     * @return
     */
    private String getIntent(String nluStr) {
        String[] strArray = nluStr.split("#");
        String[] st = strArray[0].split("_");
        return st[0];
    }

}
