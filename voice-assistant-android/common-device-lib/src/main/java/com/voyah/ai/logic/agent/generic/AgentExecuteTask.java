package com.voyah.ai.logic.agent.generic;


import static com.voyah.ai.logic.agent.generic.BaseAgentX.getBooleanFlowContextKey;
import static com.voyah.ai.logic.agent.generic.BaseAgentX.getFlowContextKey;
import static com.voyah.ai.logic.agent.generic.BaseAgentX.getIntFlowContextKey;
import static com.voyah.ai.voice.sdk.api.task.AgentX.INVALID_PRIORITY;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.voice.sdk.VoiceConfigManager;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.DialogueInterface;
import com.voice.sdk.device.tts.BeanTtsInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UIState;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.record.VoiceStateRecord;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.util.LogUtils;
import com.voice.sdk.vcar.TTSCallBack;
import com.voice.sdk.vcar.VirtualDeviceManager;
import com.voyah.ai.logic.agent.flowchart.ttsEnd.TTsEndCallBack;
import com.voyah.ai.logic.buriedpoint.BuriedPointHelper;
import com.voice.sdk.BuildConfig;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.listener.ITtsPlayListener;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ai.voice.platform.agent.api.version.v1.AgentResponse;
import com.voyah.ai.voice.sdk.api.task.AgentInfoHolder;
import com.voyah.ai.voice.sdk.api.task.AgentXCallback;
import com.voyah.ds.common.entity.agent.AgentCommon;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.nlu.NluInfo;
import com.voyah.ds.common.entity.nlu.Slot;
import com.voyah.ds.common.entity.status.ScenarioState;
import com.voyah.ds.common.entity.status.StreamMode;
import com.voyah.ds.common.entity.wakeup.WakeupType;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:lcy
 * @data:2024/3/29
 **/
public class AgentExecuteTask {
    private static final String TAG = AgentExecuteTask.class.getSimpleName();
    private static Map<String, String> requestTtsIdMap = new HashMap<>();
    private String playTtsString;
    private TTSBean ttsBean;
    private String executeTag;
    private String uiType;
    private String dsScenario;
    private boolean isStream;
    private int streamStatus = -1;
    private boolean isExit;
    private boolean isInformationCard;
    private boolean isInvalid;
    private boolean isIgQueryEmpty;
    private boolean isAsrRec;
    private String asrText;
    private boolean isWakeUp;
    private boolean isCrossDomain;
    private TTsEndCallBack mTTsEndCallBack;
    private int nearTtsLocation; //就近播报特殊场景指定位置
    private UiInterface uiInterface;
    public List<Slot> paramsList = new ArrayList<>();
    public Map<String, List<Object>> paramsMap = new HashMap();
    private IAgentExecuteTask mIAgentExecuteTask;
    private VoiceTtsBean voiceTtsBean;
    private String mSessionId;
    private String mRequestId;
    private Object mQueryId = ""; //queryID 用来区分一语多意图下共用requestID的问题
    private String mAgentName;
    private String scenario;
    private String mapScenario;
    private boolean isNLuFromStreamAsr;//是否为流式nlu
    private boolean isLastNluFromStreamAsr; //是否为流式nlu最后一个任务

    private AgentXCallback mAgentXCallback;
    private AgentResponse mAgentResponse;
    private boolean isICVI = false; //信息类卡片展示且注册了可见
    private int mPriority = -1;

    private boolean isValidAgent = false;

    private WeakReference<ITtsPlayListener> iTtsPlayListenerWeakReference;

    private int contextStreamMode = StreamMode.NOT_STREAM_MODE;
    private int exeFinalDelayTime = 200; //agent执行完，延迟destroy的时间，兜底都给200，兜底多意图agent执行中间回落聆听的问题
    private int uiSoundLocation = 0; //query 声源位置

    private VoiceStateRecordManager voiceStateRecordManager;

    private VoiceStateRecord voiceStateRecord;

    private BeanTtsInterface beanTtsInterface = DeviceHolder.INS().getDevices().getTts();


    public AgentExecuteTask(IAgentExecuteTask iAgentExecuteTask) {
        this.mIAgentExecuteTask = iAgentExecuteTask;
    }

    public void executeAgent(Map<String, Object> flowContext, AgentXCallback agentXCallback, String agentName, int priority, AgentInfoHolder agentInfoHolder) throws Exception {
        uiInterface = DeviceHolder.INS().getDevices().getUiCardInterface();
        voiceStateRecordManager = VoiceStateRecordManager.getInstance();
        paramsList.clear();
        paramsMap.clear();
        boolean isOnlineNluResult = flowContext.containsKey("ctx-isOnline") ? (boolean) flowContext.get("ctx-isOnline") : false;
        if (flowContext.containsKey(FlowContextKey.FC_NLU_RESULT)) {
            NluInfo nluInfo = (NluInfo) flowContext.get(FlowContextKey.FC_NLU_RESULT);
            if (nluInfo != null) {
                LogUtils.i(TAG, "onVoAIMessage -- DS " + new Gson().toJson(nluInfo));
                LogUtils.v(TAG, "app version:" + DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionName() + "\n nluInfo--> domain:" + nluInfo.domain + "\n intent:" + nluInfo.intent
                        + "\n nluStr:" + nluInfo.nluStr + "\n slotList:" + new Gson().toJson(nluInfo.slotList) + " ,isOnlineNluResult:" + isOnlineNluResult + " ,env:" + VoiceConfigManager.getInstance().getVoiceEnv());
            }
            if (nluInfo != null && nluInfo.slotList != null)
                paramsList.addAll(nluInfo.slotList);
        }
        mSessionId = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SESSION_ID, flowContext);
        mRequestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
        mQueryId = flowContext.getOrDefault(FlowContextKey.FC_QUERY_ID, "");
        mAgentName = agentName;
        mAgentXCallback = agentXCallback;
        mAgentResponse = agentInfoHolder.createAgentResponse(0, null);

        boolean wakeUpTime = getBooleanFlowContextKey(FlowContextKey.FC_IS_NO_WAKEUP_TIME, flowContext);
        int streamMode = flowContext.containsKey(FlowContextKey.FC_STREAM_MODE) ? (int) flowContext.get(FlowContextKey.FC_STREAM_MODE) : -1;
        contextStreamMode = streamMode;
        isNLuFromStreamAsr = flowContext.containsKey(FlowContextKey.FC_IS_NLU_FROM_STREAM_ASR) ? (boolean) flowContext.get(FlowContextKey.FC_IS_NLU_FROM_STREAM_ASR) : false;
        isLastNluFromStreamAsr = flowContext.containsKey(FlowContextKey.FC_IS_LAST_NLU_FROM_STREAM_ASR) ? (boolean) flowContext.get(FlowContextKey.FC_IS_LAST_NLU_FROM_STREAM_ASR) : false;
//        LogUtils.d(TAG, "isNLuFromStreamAsr:" + isNLuFromStreamAsr + " ,isLastNluFromStreamAsr:" + isLastNluFromStreamAsr);
        //是否为有效自然唤醒
        boolean isValidNatureWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_VALID_NATURE_WAKEUP, flowContext);
        //是否为全时免唤醒
        boolean isAllTimeWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_NO_WAKEUP_TIME, flowContext);
        uiSoundLocation = BaseAgentX.getSoundSourceLocation(flowContext); //UI上屏统一只取声源位置
        mPriority = priority;
        LogUtils.d(TAG, "isNatureWakeup" + isValidNatureWakeUp + " ,isAllTimeWakeUp:" + isAllTimeWakeUp);
        if (StringUtils.equals(agentName, "flowchart")) {
            List<Object> list = new ArrayList<>();
            list.add(agentInfoHolder);
            paramsMap.put("agentInfoHolders", list);
        }
        isValidAgent = !StringUtils.equals(mAgentName, "ignore") && !StringUtils.equals(mAgentName, "douDi") && !StringUtils.equals(mAgentName, "asrTextRecStream") && !StringUtils.equals(mAgentName, "wakeUp");

        String query = getFlowContextKey(FlowContextKey.FC_WHOLE_QUERY, flowContext);
//        LogUtils.e(TAG, "query:" + query);
        if (!StringUtils.equals("wakeUp", agentName) || isValidNatureWakeUp || isAllTimeWakeUp) {
            if (!StringUtils.equals(agentName, "asrTextRecStream")
                    && streamMode != 1
                    && StringUtils.isNotBlank(mRequestId)
                    && !StringUtils.equals(agentName, "instanceTts")) //instanceTts  中tts文本为空当拒识处理
            {
                if (StringUtils.equals(agentName, "ignore")
                        || StringUtils.equals(agentName, "douDi")
                        || StringUtils.equals(agentName, "exitMultiInteraction")
                        || StringUtils.equals(agentName, "crossDomain")
                        || StringUtils.equals(agentName, "exitDialog") && isPassiveExit(flowContext)) {
                    UIMgr.INSTANCE.exitState(UIState.STATE_ASR, getAgentIdentifier());
                } else if (isNotStream() || isFirstFrameOfStream()) {
                    //1. 先喊一个流式，过程中喊媒体卡片，流式agent会触发跨域
                    //2. 自然唤醒免唤醒都会补一个唤醒的agent, 不需要执行状态
                    if (!StringUtils.equals("wakeUp", agentName)) {
                        //先进入执行态再退出ASR，否则中间会瞬时回落到聆听态
                        //流式中间帧不重复进入
                        if ("viewCommand".equalsIgnoreCase(mAgentName)) {
                            UIMgr.INSTANCE.enterState(UIState.STATE_CMD, query, mSessionId, getAgentIdentifier(), uiSoundLocation);
                        } else {
                            UIMgr.INSTANCE.enterState(UIState.STATE_ACTION, query, mSessionId, getAgentIdentifier(), uiSoundLocation);
                        }
                        UIMgr.INSTANCE.exitState(UIState.STATE_ASR, getAgentIdentifier());
                    }
                }
            }
        }

        ClientAgentResponse clientAgentResponse = mIAgentExecuteTask.executeAgent(flowContext,
                paramsList != null ? getParamsList() : null);
//        LogUtils.d(TAG, "agentName" + agentName + " ,wakeUpTime:" + wakeUpTime + " ,query:" + query + " ,isNLuFromStreamAsr:" + isNLuFromStreamAsr + " ,isLastNluFromStreamAsr:" + isLastNluFromStreamAsr);

//        LogUtils.e(TAG, "wakeUpTime:" + wakeUpTime + " ,priority:" + priority + " ,isValidNatureWakeUp:" + isValidNatureWakeUp + " ,isAllTimeWakeUp:" + isAllTimeWakeUp);

        //更新一下场景值，如果有场景值，说明在多轮下
        scenario = (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        if (StringUtils.equals(agentName, "instanceTts")) {
            if (clientAgentResponse.isClearInput() || !scenario.equalsIgnoreCase(ScenarioState.SCENARIO_STATE_INIT)) {
                //特殊的instanceTts
                UIMgr.INSTANCE.exitState(UIState.STATE_ASR, getAgentIdentifier());
            } else {
                //正常的instanceTts
                UIMgr.INSTANCE.enterState(UIState.STATE_ACTION, query, mSessionId, getAgentIdentifier(), uiSoundLocation);
                UIMgr.INSTANCE.exitState(UIState.STATE_ASR, getAgentIdentifier());
            }
        }
        //code映射场景值使用 据识、asr识别、自然唤醒(维持二次交互)、instantTtsAgent为空的话保持上下文中 dialogTimeOut维持上下文中 可见维持上下文中场景
        HashMap<String, String> stateHashMap = flowContext.containsKey(FlowContextKey.FC_SCENARIO_CODE_STATE_MAP) ? (HashMap<String, String>) flowContext.get(FlowContextKey.FC_SCENARIO_CODE_STATE_MAP) : null;
        boolean isHashMapEmpty = (null == stateHashMap || stateHashMap.isEmpty());

        if (StringUtils.equals(agentName, "wakeUp") && getIntFlowContextKey(FlowContextKey.FC_WAKE_UP_TYPE, flowContext) == 2)
            mapScenario = scenario;
        else if (StringUtils.equals(agentName, "instanceTts") && isHashMapEmpty)
            mapScenario = scenario;
        else if (StringUtils.equals(agentName, "viewCommand") || StringUtils.equals(agentName, "dialogTimeout"))
            mapScenario = scenario;
        else if (clientAgentResponse.mRetCode == Constant.PhoneAgentResponseCode.KEEP_SCENE) {
            mapScenario = scenario;
        } else {
            if (isHashMapEmpty) {
                mapScenario = ScenarioState.SCENARIO_STATE_INIT;
            } else {
                String state = stateHashMap.get(clientAgentResponse.mRetCode + "");
                if (StringUtils.isBlank(state)) {
                    mapScenario = ScenarioState.SCENARIO_STATE_INIT;
                } else if (Constant.KEEP_SCENARIO.equalsIgnoreCase(state)) {
                    mapScenario = scenario;
                } else {
                    mapScenario = stateHashMap.get(clientAgentResponse.mRetCode + "");
                }
            }
        }

        if (isMulti()) {
            //产品定义，如果某个音区有二次交互，需要打断其他音区的唤醒状态
            UIMgr.INSTANCE.onMultiInteraction(uiSoundLocation);
        }


//        LogUtils.d(TAG, "scenario:" + scenario + " ,mapScenario:" + mapScenario + " ,isHashMapEmpty:" + isHashMapEmpty);
        LogUtils.d(TAG, "agentName" + agentName + " ,wakeUpTime:" + wakeUpTime + " ,query:" + query + " ,isNLuFromStreamAsr:" +
                isNLuFromStreamAsr + " ,isLastNluFromStreamAsr:" + isLastNluFromStreamAsr + " ,scenario:" + scenario + " ,mapScenario:" + mapScenario);
        //拿到ttsBean对象
        Object object = clientAgentResponse.getmTtsObject();
        if (object instanceof String) {
            playTtsString = (String) object;
            ttsBean = new TTSBean();
            ttsBean.setSelectTTs(playTtsString);
            //todo 如果是流势就需要特殊处理
            Object isStreamMode = flowContext.get(FlowContextKey.FC_IS_STREAM_MODE);
            if (isStreamMode != null && isStreamMode instanceof Boolean) {
                boolean isRealStreamMode = (boolean) isStreamMode;
                if (isRealStreamMode) {
//                    int streamMode = (int) flowContext.get(FlowContextKey.FC_STREAM_MODE);
//            public static final int STREAM_MODE_START = 0;
//            public static final int STREAM_MODE_IN = 1;
//            public static final int STREAM_MODE_END = 2;
                    String tts_id = "";
                    switch (streamMode) {
                        case StreamMode.STREAM_MODE_START:
                            tts_id = UUID.randomUUID().toString();
                            requestTtsIdMap.put(mRequestId, tts_id);
//                            LogUtils.d(TAG, "STREAM_MODE_START:" + tts_id);
                            break;
                        case StreamMode.STREAM_MODE_IN:
                            tts_id = requestTtsIdMap.get(mRequestId);
//                            LogUtils.d(TAG, "STREAM_MODE_IN:" + tts_id);
                            break;
                        case StreamMode.STREAM_MODE_END:
                            tts_id = requestTtsIdMap.remove(mRequestId);
//                            LogUtils.d(TAG, "STREAM_MODE_END:" + tts_id);
//                            LogUtils.d(TAG, "====================");
                            break;
                    }
                    ttsBean.setId(tts_id);
                } else {
                    ttsBean.setId(UUID.randomUUID().toString());
                }
            }

            voiceTtsBean = createVoiceTtsBean(ttsBean);
        } else if (object instanceof TTSBean) {
            TTSBean curTTSBean = (TTSBean) object;
            ttsBean = new TTSBean();
            ttsBean.setId(UUID.randomUUID().toString());
            ttsBean.setEmotion(curTTSBean.getEmotion());
            ttsBean.setSelectTTs(curTTSBean.getSelectTTs());
            ttsBean.setPlaceholder(curTTSBean.getPlaceholder());
            ttsBean.setPriority(curTTSBean.getPriority());
            playTtsString = ttsBean.getSelectTTs();
            voiceTtsBean = createVoiceTtsBean(ttsBean);
        } else {
            ttsBean = new TTSBean();
            ttsBean.setSelectTTs("");
        }
        if (null != voiceTtsBean)
            voiceTtsBean.setPackageName(DeviceHolder.INS().getDevices().getBuriedPointManager().getPackageName());

        handleInterruptibleUI();

        //给测试提供的日志
        NluInfo nluInfo = (NluInfo) flowContext.get(FlowContextKey.FC_NLU_RESULT);
        if (nluInfo != null) {
            String nlu = nluInfo.nluStr;
        }
        //2.埋点数据生成的地方
        if (!agentName.equals("asrTextRecStream")) {
            try {
//                LogUtils.d(TAG, "BURIED_POINT，data upload start.....当前agentName:" + agentName);
                BuriedPointHelper.getInstance().upLoading(flowContext, agentName, ttsBean);
//                LogUtils.d(TAG, "BURIED_POINT，data upload end.....");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isICVI = StringUtils.equals(AgentCommon.VIEW_COMMAND, mAgentName) && uiInterface.isCardShowAndRegisterViewCmd(0);
//        //兜底可见agent执行，tts在三方发起播报，且非必定播报的情况下卡片关闭了，但是tts还在播报
//        if (StringUtils.equals(AgentCommon.VIEW_COMMAND, mAgentName) && !uiInterface.isCardShowAndRegisterViewCmd(0))
//        beanTtsInterface.shutUpOneSelf();
        if (flowContext.containsKey(FlowContextKey.FC_NLU_RESULT) || StringUtils.equals(AgentCommon.VIEW_COMMAND, mAgentName)) {
            dialogCallback(flowContext, nluInfo);
        }

//        //区分上下文和补充map
//        clientAgentResponse.mExtra = new HashMap<>();


//        if (streamStatus == 1 && null != voiceTtsBean)
//            voiceTtsBean.setTtsId("");

        isStream = clientAgentResponse.isStream();
        streamStatus = clientAgentResponse.getStreamStatus();
        executeTag = clientAgentResponse.mExecuteTag;
        uiType = clientAgentResponse.mUiType;
        nearTtsLocation = clientAgentResponse.getNearTtsLocation();
        isExit = clientAgentResponse.isExit();
        isInformationCard = clientAgentResponse.isInformationCard();
        isInvalid = clientAgentResponse.isInValid();
        isIgQueryEmpty = clientAgentResponse.isIgQueryEmpty();
        isAsrRec = clientAgentResponse.isAsrRec();
        asrText = clientAgentResponse.getAsrText();
        isWakeUp = clientAgentResponse.isWakeUp();
        dsScenario = clientAgentResponse.getDsScenario();
        isCrossDomain = clientAgentResponse.isCrossDomain();
        mTTsEndCallBack = clientAgentResponse.gettTsEndCallBack();

        if (isValidAgent && StringUtils.isBlank(ttsBean.getSelectTTs()) && streamStatus == -1) {
            beanTtsInterface.releaseAudioFocusByUsage();
        }

//        LogUtils.d(TAG, "mAgentName:" + mAgentName + " ,mRequestId:" + mRequestId);

        LogUtils.d(TAG, "mAgentName:" + mAgentName + " ,mRequestId:" + mRequestId + " ,agentName" + agentName + " ,wakeUpTime:" + wakeUpTime + " ,query:" + query + " ,isNLuFromStreamAsr:" +
                isNLuFromStreamAsr + " ,isLastNluFromStreamAsr:" + isLastNluFromStreamAsr + " ,isValidAgent:" + isValidAgent);

        if (!isAsrRec) {
            LogUtils.d(TAG, "playTtsString:" + playTtsString + " ,streamStatus:" + streamStatus + " ,isICVI is " + isICVI +
                    " ,isIgQueryEmpty:" + isIgQueryEmpty + " ,isInvalid:" + isInvalid + " ,scenario:" + scenario + " ,mapScenario:" + mapScenario);
//            LogUtils.i(TAG, "playTtsString is " + playTtsString + " ,executeTag is " + executeTag + " ,isExit is " + isExit +
//                    " ,isInformationCard is " + isInformationCard + " ,isAsrRec is " + isAsrRec + " ,asrText is " + asrText + " ,isWakeUp is " + isWakeUp + " ,dsScenario is " +
//                    dsScenario + " ,streamStatus is " + streamStatus + " ,uiType is " + uiType + " ,isInvalid is " + isInvalid + " ,isCrossDomain is " + isCrossDomain + " ,isStream" + isStream + " ,isICVI is " + isICVI);
        }

        if (isEmptyPlayStr(playTtsString)) {
            exeFinalDelayTime = 1000; //没有要播的文本，默认2000ms
        }

        String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        voiceStateRecord = voiceStateRecordManager.getVoiceStateRecord(soundLocation);

        mAgentResponse.retCode = clientAgentResponse.mRetCode;
        mAgentResponse.extra = clientAgentResponse.mExtra;
        agentXCallback.onAgentBeginExecute(mAgentResponse);
        if (isInvalid) {
            boolean isTtsPlay = voiceStateRecordManager.isTtsPlay();
//            //todo:needWaitReqId应该与mRequestId相同（相同移除tts播报的requestId）
            String needWaitReqId = voiceStateRecordManager.getVoiceStateRecord(soundLocation).getCurrentTtsRequestId();
            if (isTtsPlay) {
                LogUtils.d(TAG, "dontWaitCallback is false  isTtsPlay:" + isTtsPlay + " ,needWaitReqId:" + needWaitReqId);
                clientAgentResponse.mExtra.put("dontWaitCallback", false);
                clientAgentResponse.mExtra.put("needWaitReqId", needWaitReqId);
            } else {
                LogUtils.d(TAG, "set dontWaitCallback true");
                clientAgentResponse.mExtra.put("dontWaitCallback", true);
            }

            agentXCallback.onAgentExecuteFinal(mAgentResponse);
//            LogUtils.d("UIState", "isInvalid call destroy");
            checkAndDestroyAgent(false);
//            return mAgentResponse;
        } else {
//            String awakenLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext);
//            LogUtils.d(TAG, "soundLocation:" + soundLocation + " ,awakenLocation:" + awakenLocation);

            //更新语音任务记录
            //todo:信息类卡片交互指令不做任务记录，保留上一轮任务状态（根据信息类卡片是否展示作为依据）

            saveVoiceState();

            //更新上下文上报信息
            uploadFlowContext(clientAgentResponse.mExtra);

            //播报前VPA及打字机状态更新
            uploadBeforePlay();

            //tts播报处理
            if (BuildConfig.VCar_enabled) {
                //虚拟车获取tts结果
                handleVCar();
            } else {
                toPlayTts(clientAgentResponse, soundLocation);
            }
            //设置任务状态是否需要等待tts播报完成
            setAgentResponse(clientAgentResponse);
        }


//        LogUtils.d(TAG, "playTtsString：" + playTtsString + " ,mPriority:" + mPriority + " ,StringUtils.isBlank(playTtsString):" + StringUtils.isBlank(playTtsString)
//                + " ,TextUtils.isEmpty(playTtsString):" + TextUtils.isEmpty(playTtsString) + " ,TextUtils.equals(text, \"\\\"\\\"\"):" + TextUtils.equals(playTtsString, "\"\"") + " ,---:" + StringUtils.equals(playTtsString, "\"\""));
//        LogUtils.d(TAG, "isStream：" + isStream + " ,isWakeUp:" + isWakeUp + " ,isExit:" + isExit);
        if (!isStream && (isWakeUp || isExit || isEmptyPlayStr(playTtsString))) {
            if (null == mAgentXCallback) {
                LogUtils.d(TAG, "mAgentXCallback is null");
            } else if (INVALID_PRIORITY != mPriority) {
                mAgentResponse.requestId = mRequestId;
                LogUtils.d(TAG, "onAgentExecuteFinal3 mRequestId:" + mRequestId);
                mAgentXCallback.onAgentExecuteFinal(mAgentResponse);
                if (mAgentName != null && mAgentName.startsWith("media")) {
                    exeFinalDelayTime = 2000; //媒体要走搜索，给两秒高亮
                }
                LogUtils.d("UIState", "no tts call destroy:" + getAgentIdentifier());
                checkAndDestroyAgent(false);
            }
        } else if ((isStream || isInformationCard) && streamStatus != 2 && streamStatus != -1) {
            mAgentResponse.requestId = mRequestId;
            LogUtils.d(TAG, "onAgentExecuteFinal2 mRequestId:" + mRequestId);
            mAgentXCallback.onAgentExecuteFinal(mAgentResponse);
        }
    }

    private void dialogCallback(Map<String, Object> flowContext, NluInfo nluInfo) {
        NluResult nluResult = new NluResult();
        nluResult.id = flowContext.get(FlowContextKey.FC_REQ_ID) + "," + flowContext.get(FlowContextKey.FC_QUERY_ID);
        nluResult.isOnline = getBooleanFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_IS_ONLINE, flowContext);
        boolean isViewCmd = TextUtils.equals(mAgentName, AgentCommon.VIEW_COMMAND);
        DialogueInterface dialogueManager = DeviceHolder.INS().getDevices().getDialogue();
        if (!isViewCmd) {
            if (nluInfo != null) {
                nluResult.rawNlu = new Gson().toJson(nluInfo);
                dialogueManager.onNluResultCallback(nluResult);
            }
        } else {
            List<?> viewCmdArray = (List<?>) flowContext.get(FlowContextKey.FC_VIEW_COMMAND_INFOS);
            JSONObject nluObj = new JSONObject();
            try {
                nluObj.put("domain", "viewCommand");
                nluObj.put("slotList", viewCmdArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nluResult.rawNlu = nluObj.toString();
            dialogueManager.onNluResultCallback(nluResult);
        }
        if (!TextUtils.isEmpty(playTtsString)) {
            dialogueManager.onVoiceStateCallback(LifeState.HOLDING);
            dialogueManager.onTtsCallback(playTtsString);
        } else {
            if (isViewCmd) {
                //todo 张魏
//                ThreadUtils.getMainHandler().postDelayed(() ->
//                                dialogueManager.onVoiceStateCallback(LifeState.LISTENING),
//                        DELAY_TIME_FOR_VIEW_COMMAND);
            } else {
                dialogueManager.onVoiceStateCallback(LifeState.LISTENING);
            }
        }
    }

    private void setNearbyTtsPosition(String soundLocation) {
        LogUtils.d(TAG, "setNearbyTtsPosition nearTtsLocation:" + nearTtsLocation + " ,soundLocation:" + soundLocation);
        if (null == voiceTtsBean)
            return;
        if (nearTtsLocation != -2) {
            voiceTtsBean.setSoundLocation(nearTtsLocation);
        } else {
            int location = translateNearbyTtsLocation(soundLocation);
            voiceTtsBean.setSoundLocation(location != -1 ? location : 0);
        }
    }


    private void saveVoiceState() {
        voiceStateRecordManager.setIsAsr(isAsrRec && !StringUtils.isBlank(asrText));
        if (!isAsrRec && !isInvalid && streamStatus != 1 && streamStatus != 2 && !isICVI)
            voiceStateRecord.setCurrentTaskRequestId(mRequestId);
        if (isICVI) {
            LogUtils.d(TAG, "isICVI is true dont save state");
        } else if (isCrossDomain || (StringUtils.equals(scenario, mapScenario) && !StringUtils.equals(scenario, ScenarioState.SCENARIO_STATE_INIT) && !StringUtils.equals(mapScenario, ScenarioState.SCENARIO_STATE_INIT))) {
            //跨域 二次交互中
            //上下文中场景与映射获得场景相同-不做记录
            LogUtils.d(TAG, "same scenario:" + scenario);
        } else {
            if (!isAsrRec && !isInvalid && streamStatus != 1 && streamStatus != 2) {
                voiceStateRecord.updatePrevTaskMessage();

                LogUtils.e(TAG, "uiType:" + uiType + " ,isInformationCard:" + isInformationCard + " ,mRequestId:" + mRequestId);
//                VoiceStateRecord.setCurrentTaskRequestId(mRequestId);
                if (isInformationCard && !StringUtils.isBlank(uiType))
                    voiceStateRecord.setCurrentTaskType(0);
                else if (!isInformationCard && !StringUtils.isBlank(uiType))
                    voiceStateRecord.setCurrentTaskType(2);
                else if (!isInformationCard && StringUtils.isBlank(uiType))
                    voiceStateRecord.setCurrentTaskType(3);
                else
                    voiceStateRecord.setCurrentTaskType(1);

                LogUtils.d(TAG, voiceStateRecord.VoiceStateString());
            }
        }
    }

    private void uploadFlowContext(Map<String, Object> mExtra) {
        if (!isEmptyPlayStr(playTtsString))
            mExtra.put("multiInteractionTts", playTtsString); //二次交互场景三次无效语义重复提醒是使用

        if (!StringUtils.isBlank(dsScenario))
            mExtra.put("dsScenario", dsScenario); //执行结束场景上传
    }

    private void uploadBeforePlay() {
        if (isAsrRec || streamStatus == 1)
            return;

        if (isExit) {
//            UIMgr.INSTANCE.forceExitAll("AgentExecuteTask");
        } else if (isInvalid && isEmptyPlayStr(playTtsString)) {
            //据识
            setVoiceState(true, isInformationCard, uiInterface);
        } else if (!isEmptyPlayStr(playTtsString)) {
            //UIMgr.INSTANCE.enterState(UIState.STATE_ACTION, playTtsString, mAgentName);
        } else if (isEmptyPlayStr(playTtsString) && !isInformationCard) {
            setVoiceState(false, false, uiInterface);
        }

    }

    private void toPlayTts(ClientAgentResponse clientAgentResponse, String soundLocation) {
        if (isStream || isInformationCard || !isEmptyPlayStr(playTtsString)) {
            createTtsPlayCallBack();
        }

//        if (!isEmptyPlayStr(playTtsString) && streamStatus != 1) {
        LogUtils.i(TAG, "toPlayTts soundLocation:" + soundLocation);
        setNearbyTtsPosition(soundLocation);
//        }
        if (!StringUtils.isBlank(executeTag)) {
            //1.先播报后执行
            if (!isEmptyPlayStr(playTtsString)) {
//                beanTtsInterface.speak(playTtsString, isWakeUp || isExit || isInformationCard, synchronizedTtsPlayListener);
                beanTtsInterface.speakBean(voiceTtsBean, iTtsPlayListenerWeakReference.get());
            } else
                executeOrder(uiType, executeTag);
        } else {
            executeOrder(uiType, executeTag);
            uiType = "";
            //信息类优先级临时调整为最低
            if (!isEmptyPlayStr(playTtsString) && !isStream && !isInformationCard) {
//                beanTtsInterface.speak(playTtsString, isWakeUp || isExit || isInformationCard, iTtsPlayListener);
                if (isWakeUp || isExit)
                    voiceTtsBean.setTtsPriority("P1");
                else
                    voiceTtsBean.setTtsPriority("P2");
                beanTtsInterface.speakBean(voiceTtsBean, iTtsPlayListenerWeakReference.get());
            } else if (isStream) {
                if (1 == streamStatus && (StringUtils.isBlank(voiceTtsBean.getTts()))) {
                    LogUtils.d(TAG, "speakStreamBean streamStatus:" + streamStatus + " ,tts is blank so back");
                } else {
                    voiceTtsBean.setTtsPriority("P3");
                    beanTtsInterface.speakStreamBean(voiceTtsBean, iTtsPlayListenerWeakReference.get(), streamStatus);
                }
                if (streamStatus != 2) {
                    clientAgentResponse.mRetCode = Constant.LLMSearchAgentResponseCode.LLM_AYSNC_RETURN_CODE;
                } else {
                    clientAgentResponse.mRetCode = Constant.LLMSearchAgentResponseCode.LLM_LONG_TIME_SNC_RETURN_CODE;
                }
            } else if (isInformationCard) {
//                beanTtsInterface.speakInformation(playTtsString, "InformationCard", iTtsPlayListener);
                voiceTtsBean.setTtsPriority("P3");
                beanTtsInterface.speakInformationBean(voiceTtsBean, iTtsPlayListenerWeakReference.get(), "InformationCard");
            }
        }
    }

    private void createTtsPlayCallBack() {
        iTtsPlayListenerWeakReference = new WeakReference<>(new ITtsPlayListener() {
            @Override
            public void onPlayBeginning(String s) {
                saveTtsPlayStatus(true, mRequestId);
                LogUtils.d(TAG, "stream:" + isStream + " ,speak onPlayBeginning s is " + s + " ,iTtsPlayListener mRequestId:" + mRequestId + ",mAgentName:" + mAgentName);
            }

            @Override
            public void onPlayEnd(String s, int i) {
                LogUtils.e(TAG, "stream:" + isStream + " ,speak onPlayEnd s is " + s + " ,iTtsPlayListener mRequestId:" + mRequestId + ", mAgentName:" + mAgentName);
//            executeOrder(uiType, executeTag);
//            countDownLatch.countDown();
                saveTtsPlayStatus(false, "");
                executeOrder(uiType, executeTag);
                setVoiceState(isInvalid, isInformationCard, uiInterface);
                if ((null != mAgentXCallback && !isWakeUp && !isExit) && INVALID_PRIORITY != mPriority) {
                    mAgentResponse.requestId = mRequestId;
                    mAgentXCallback.onAgentExecuteFinal(mAgentResponse);
                    LogUtils.d("UIState", "onPlayEnd call destroy:" + getAgentIdentifier());
                    checkAndDestroyAgent(false);
                }
//                else
//                    LogUtils.e(TAG, "stream:" + isStream + " ,mAgentXCallback is null or isWakeUp " + isWakeUp + " ,isExit:" + isExit + " ,mPriority:" + mPriority);

                if (null != mTTsEndCallBack)
                    mTTsEndCallBack.excute();

                if (isValidAgent)
                    beanTtsInterface.releaseAudioFocusByUsage();
                iTtsPlayListenerWeakReference.clear();
            }

            @Override
            public void onPlayError(String s, int i) {
//                LogUtils.i(TAG, "stream:" + isStream + " ,speak onPlayError onPlayEnd s is " + s + " ,i is " + i);
                LogUtils.e(TAG, "stream:" + isStream + " ,speak onPlayError onPlayEnd s is " + s + " ,iTtsPlayListener mRequestId:" + mRequestId + ", mAgentName:" + mAgentName + " ,error");
//            executeOrder(uiType, executeTag);
//            countDownLatch.countDown();
                saveTtsPlayStatus(false, "");
                executeOrder(uiType, executeTag);
                setVoiceState(isInvalid, isInformationCard, uiInterface);
                if ((null != mAgentXCallback && !isWakeUp && !isExit) && INVALID_PRIORITY != mPriority) {
                    mAgentResponse.requestId = mRequestId;
                    mAgentXCallback.onAgentExecuteFinal(mAgentResponse);
                    LogUtils.d("UIState", "onPlayError call destroy" + getAgentIdentifier());
                    exeFinalDelayTime = 0;
                    checkAndDestroyAgent(true);
                }
//                else
//                    LogUtils.e(TAG, "stream:" + isStream + " ,mAgentXCallback is null or isWakeUp " + isWakeUp + " ,isExit:" + isExit + " ,mPriority:" + mPriority);

                if (null != mTTsEndCallBack)
                    mTTsEndCallBack.excute();

                if (isValidAgent)
                    beanTtsInterface.releaseAudioFocusByUsage();
                iTtsPlayListenerWeakReference.clear();
            }
        });
    }

    private void setAgentResponse(ClientAgentResponse clientAgentResponse) {
        LogUtils.i(TAG, "return response mRetCode is " + clientAgentResponse.mRetCode + " ,mExtra is " + clientAgentResponse.mExtra + " ,mRequestId:" + mRequestId + " ,mAgentName:" + mAgentName);
//        if (!isAsrRec)
//            LogUtils.e(TAG, " 1 mRequestId:" + mRequestId + ", mAgentName:" + mAgentName);
        if (isEmptyPlayStr(playTtsString)) {
//            boolean isTtsPlay = VoiceStateRecord.isTtsPlay();
//            //todo:needWaitReqId应该与mRequestId相同（相同移除tts播报的requestId）
//            String needWaitReqId = VoiceStateRecord.getCurrentTtsRequestId();
//            LogUtils.i(TAG, "isTtsPlay:" + isTtsPlay);
//            if (isTtsPlay && isInvalid) {
//                LogUtils.d(TAG, "dontWaitCallback is false  isTtsPlay:" + isTtsPlay + " ,needWaitReqId:" + needWaitReqId);
//                clientAgentResponse.mExtra.put("dontWaitCallback", false);
//                clientAgentResponse.mExtra.put("needWaitReqId", needWaitReqId);
//            } else
            if (!isStream) {
                //ds是否需要等待callback结果
                LogUtils.d(TAG, "1.dontWaitCallback is true");
                clientAgentResponse.mExtra.put("dontWaitCallback", true);
            } else {
                if (streamStatus != 2) {
                    LogUtils.d(TAG, "2.dontWaitCallback is true");
                    clientAgentResponse.mExtra.put("dontWaitCallback", true);
                }
            }
        }
    }


    private void executeOrder(String uiType, String executeTag) {
        LogUtils.i(TAG, "executeOrder uiType is " + uiType + " ,executeTag is " + executeTag);
        mIAgentExecuteTask.showUi(uiType, uiSoundLocation);
        if (!StringUtils.isBlank(executeTag))
            mIAgentExecuteTask.executeOrder(executeTag, uiSoundLocation);
        paramsGatherClear();
    }

    public Map<String, List<Object>> getParamsList() {
        if (!paramsList.isEmpty()) {
            Gson gson = new Gson();
//            LogUtils.i(TAG, " nluJsonString " + gson.toJson(paramsList));
            List<Object> list = null;
            for (Slot slot : paramsList) {
                list = new ArrayList<>();
                if (!StringUtils.isBlank(slot.slotValue) && slot.slotValue.contains("|")) {
                    String[] values = slot.slotValue.split("\\|");
                    if (values.length > 1) {
                        list.addAll(Arrays.asList(values));
                    }
                } else {
                    list.add(slot.slotValue);
                }
                paramsMap.put(slot.slotType, list);
            }
        }
//        LogUtils.i(TAG, "jsonString is " + gson.toJson(paramsMap));
        return paramsMap;
    }

    private void setVoiceState(boolean isInvalid, boolean isInformationCard, UiInterface uiInterface) {
        if (isNLuFromStreamAsr && !isLastNluFromStreamAsr) {
            LogUtils.d(TAG, "nlu stream task in execution");
            return;
        }
        String currentReqId = voiceStateRecord.getCurrentTaskRequestId();
        String preReqId = voiceStateRecord.getPrevTaskRequestId();
        int currentTaskType = voiceStateRecord.getCurrentTaskType();
        LogUtils.d(TAG, "currentReqId:" + currentReqId + " ,currentTaskType:" + currentTaskType + " ,mRequestId:" + mRequestId + " ,preReqId:" + preReqId + " ,isCrossDomain:" + isCrossDomain);
        //非信息类卡片展示（有效语义-聆听｜无效语义-500ms聆听）
        if (isInvalid) {
            exeFinalDelayTime = 500;
        } else if (isInformationCard) {
            //信息类卡片展示tts播报完后倒计时5(10)秒(天气、股票、日程)
            if (StringUtils.equals(currentReqId, mRequestId)) {
                int delayTime = 5000;
                if (DeviceHolder.INS().getDevices().getCarServiceProp().isVCOS15()) {
                    delayTime = 10 * 1000;
                }
                exeFinalDelayTime = delayTime;
            }
        }
        paramsGatherClear();
    }

    private void paramsGatherClear() {
//        ParamsGather.requestId = "";
    }

    private void saveTtsPlayStatus(boolean isPlay, String requestId) {
//        Log.i(TAG, "isPlay:" + isPlay + " ,requestId:" + requestId);
        voiceStateRecordManager.setIsPlayTts(isPlay);
        voiceStateRecord.setCurrentTtsRequestId(requestId);
    }

    public static int translateNearbyTtsLocation(String soundLocation) {
//        LogUtils.i(TAG, "translateNearbyTtsLocation soundLocation is " + soundLocation);
        int location = -2;
        if (BaseAgentX.Location.Location_LIST.contains(soundLocation)) {
            location = BaseAgentX.Location.Location_MAP.get(soundLocation);
        }
        return location + 1;
    }

    private VoiceTtsBean createVoiceTtsBean(TTSBean ttsBean) {
        if (BuildConfig.VCar_enabled) {
            return null;
        }

        try {
            VoiceTtsBean voiceTtsBean1 = new VoiceTtsBean("", "");
            voiceTtsBean1.setTtsId(ttsBean.getId());
            voiceTtsBean1.setTts(ttsBean.getSelectTTs());
            voiceTtsBean1.setEmotion(ttsBean.getEmotion());
            voiceTtsBean1.setTtsPriority(ttsBean.getPriority());
            return voiceTtsBean1;
        } catch (Throwable t) {
            //VoiceTtsBean 实现了android.os.Parcelable接口，可能会抛出异常
            LogUtils.e(TAG, "createVoiceTtsBean error:" + t.getMessage());
            return null;
        }
    }


    //todo:临时情感设置
    private boolean containsKeyword(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        // 检查是否有匹配的词
        boolean isFind = matcher.find();
        return isFind;
    }

    private String getAgentIdentifier() {
        if (mQueryId == null) {
            mQueryId = "";
        } else {
            mQueryId = mQueryId.toString();
        }
        return UIMgr.INSTANCE.obtainToken(mRequestId, (String) mQueryId);
    }

    private boolean isFreeWakeUp(Map<String, Object> context) {
        int wakeUpType = getIntFlowContextKey(FlowContextKey.FC_WAKE_UP_TYPE, context);
        return WakeupType.isAllTimeWakeup(getIntFlowContextKey(FlowContextKey.FC_WAKE_UP_TYPE, context));
    }

    //多轮
    private boolean isMulti() {
        return mapScenario != null && !StringUtils.equals(mapScenario, ScenarioState.SCENARIO_STATE_INIT);
    }

    private boolean isPassiveExit(Map<String, Object> flowContext) {
        boolean isContinueListenTimeout = flowContext.containsKey(FlowContextKey.FC_CONTINUE_LISTEN_TIMEOUT)
                ? (boolean) flowContext.get(FlowContextKey.FC_CONTINUE_LISTEN_TIMEOUT) : false;
        //延时轮三次据识
        boolean isIgnoreCountExceedLimitInDelayListening = flowContext.containsKey(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT_IN_DELAY_LISTENING)
                ? (boolean) flowContext.get(FlowContextKey.FC_IGNORE_COUNT_EXCEED_LIMIT_IN_DELAY_LISTENING) : false;
        boolean isMultiInteractionTimeout = flowContext.containsKey(FlowContextKey.FC_MULTI_INTERACTION_TIMEOUT)
                ? (boolean) flowContext.get(FlowContextKey.FC_MULTI_INTERACTION_TIMEOUT) : false;
        return isContinueListenTimeout || isIgnoreCountExceedLimitInDelayListening || isMultiInteractionTimeout;
    }

    /**
     * 是否流式首帧
     *
     * @return
     */
    private boolean isFirstFrameOfStream() {
        return StreamMode.STREAM_MODE_START == contextStreamMode;
    }

    /**
     * 流式但非尾帧，允许idle打断
     *
     * @return
     */
    private boolean isStreamButNotEnd() {
        return StreamMode.STREAM_MODE_IN == contextStreamMode || StreamMode.STREAM_MODE_START == contextStreamMode;
    }

    private boolean isLastStreamFrame() {
        return StreamMode.STREAM_MODE_END == contextStreamMode;
    }

    private boolean isNotStream() {
        return StreamMode.NOT_STREAM_MODE == contextStreamMode;
    }

    private void handleInterruptibleUI() {
        //20241210 UI状态管理，多轮的agent 依赖其他agent来销毁的case，在语音回到idle态时强制退出
        if (isMulti()) {
            UIMgr.INSTANCE.tryUpdateInterruptible(mSessionId, getAgentIdentifier(), true, uiSoundLocation);
        }
        String token = getAgentIdentifier();
        String ttsId = "";
        if (voiceTtsBean != null) {
            ttsId = voiceTtsBean.getTtsId();
        }
        Runnable run = UIMgr.INSTANCE.obtainTimeoutRun(token, mRequestId, ttsId);
        if (isStreamButNotEnd()) {
            long timeOut = 5000;
            if (isFirstFrameOfStream()) {
                if ("llm#drawing".equalsIgnoreCase(mAgentName)) {
                    timeOut = 25 * 1000; //大模型服务接口15s超时，如果客户端超时比服务接口短，会导致慢返回的尾帧播报不响应
                } else {
                    timeOut = 10 * 1000; //普通流式
                }
            }
            DeviceHolder.INS().getDevices().getThreadDelay().addDelayRunnable(run, timeOut);
        }
        if (isLastStreamFrame()) {
            DeviceHolder.INS().getDevices().getThreadDelay().removeDelayRunnable(run);
            UIMgr.INSTANCE.clearTimeoutRun(token);//清理缓存
        }
    }

    private void checkAndDestroyAgent(boolean isPlayError) {
        //识别 唤醒 退出 拒识 这些agent不涉及UI或者UI不在这个流程中处理
        if (isAsrRec
                || isWakeUp
                || isExit
                || StringUtils.equals(mAgentName, "ignore")
                || StringUtils.equals(mAgentName, "crossDomain")
                || StringUtils.equals(mAgentName, "exitMultiInteraction")) {
            return;
        }
        if (isMulti()) { //多轮不退出UI执行态
            return;
        }
        if (isStream && StreamMode.STREAM_MODE_END != streamStatus && !isPlayError) { //流式非尾帧不退出
            return;
        }
        DeviceHolder.INS().getDevices().getThreadDelay().addDelayRunnable(UIMgr.INSTANCE.obtainExitRun(getAgentIdentifier()), exeFinalDelayTime);
    }

    private void handleVCar() {
        TTSCallBack callBack = VirtualDeviceManager.getInstance().getTtsCallBack();
        LogUtils.d(TAG, "handleVCar callBack:" + callBack + " ,ttsBean:" + ttsBean.toString());
        if (callBack != null && ttsBean != null) {
            callBack.tts(ttsBean);
        }
        //虚拟车，无需在tts播完后回调
        mAgentXCallback.onAgentExecuteFinal(mAgentResponse);
    }

    private boolean isEmptyPlayStr(String tts) {
        return StringUtils.isBlank(playTtsString) || StringUtils.equals(tts, "\"\"") || StringUtils.equals(tts, "“”");
    }
}
