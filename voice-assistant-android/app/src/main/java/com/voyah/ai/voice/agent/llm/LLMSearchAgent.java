package com.voyah.ai.voice.agent.llm;

import android.util.Log2;

import com.example.filter_annotation.ClassAgent;
import com.google.gson.Gson;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.llm.LLMTextInterface;
import com.voice.sdk.model.LLMDataInfo;
import com.voice.sdk.util.MapUtil;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.status.StreamMode;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@ClassAgent
public class LLMSearchAgent extends BaseAgentX {

    private static final String TAG = "LLMSearchAgent";


    private final StringBuilder sb = new StringBuilder();

    @Override
    public String AgentName() {
        return "llm#search";
    }


    public String replaceSpecialText(String tts) {
        tts = tts.replaceAll("⭐⭐⭐⭐⭐", "五颗星")
                .replaceAll("⭐⭐⭐⭐", "四颗星")
                .replaceAll("⭐⭐⭐", "三颗星")
                .replaceAll("⭐⭐", "两颗星")
                .replaceAll("⭐", "一颗星");
        return tts;
    }

    @Override
    public ClientAgentResponse executeAgent(
            Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");
        parseLLMData(flowContext);
        int streamMode = -1;
        Object streamModeO = flowContext.get(FlowContextKey.FC_STREAM_MODE);
        if (streamModeO instanceof Integer) {
            streamMode = (int) streamModeO;
        }
        if (streamMode == StreamMode.STREAM_MODE_START) {
            sb.setLength(0);
        }


        String ttxText = "";
        Object ttsObject = flowContext.get(FlowContextKey.FC_NO_TASK_TEXT);
        if (ttsObject instanceof String) {
            ttxText = (String) ttsObject;
        }
        ClientAgentResponse response;
        if (streamMode == StreamMode.NOT_STREAM_MODE) {
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ttxText);
        } else {
            sb.append(ttxText);
            Log2.i(TAG, "full ttsText:" + sb);
            String curText = "";
            if (streamMode == StreamMode.STREAM_MODE_END) {
                curText = sb.toString();
            } else {
                if (sb.toString().contains("。") || sb.toString().contains("，")) {
                    int index1 = sb.toString().indexOf('。');
                    int index2 = sb.toString().indexOf('，');
                    int index = Math.max(index1, index2);
                    curText = sb.substring(0, index + 1);
                    sb.delete(0, index + 1);
                }

            }
            Log2.i(TAG, "remove ttsText:" + sb);
            LogUtils.d(TAG, "executeAgent before ttsText:" + curText);
            curText = curText
                    .replaceAll("\\[.*?\\]", "")
                    .replaceAll("[*_#\\[\\]()]", "")
                    .replaceAll("https?://\\S+", "")
                    .replaceAll("![a-zA-Z0-9\\-]+\\.png", "")
                    .replaceAll("\\n\\+\\s*", "");//过滤掉无序列表中的修饰符
            LogUtils.d(TAG, "executeAgent after ttsText:" + curText);
            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    replaceSpecialText(curText));
        }
        Object streamFlagObject = flowContext.get(FlowContextKey.FC_IS_STREAM_NO_TASK_TEXT);
        if (streamFlagObject instanceof Boolean) {
            boolean streamFlag = (boolean) streamFlagObject;
            response.setStream(streamFlag);
            response.setStreamStatus(streamMode);
        }
        LLMTextInterface llmTextInterface = DeviceHolder.INS().getDevices().getLLMText();
        if ((llmTextInterface != null) && !llmTextInterface.isCardInfoEmpty()) {
            response.setInformationCard(true);
            response.setUiType(CARD_TYPE_INFORMATION);
        }
        return response;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isSequenced() {
        return true;
    }


    @Override
    public void showUi(String uiType, int location) {
        LogUtils.d(TAG, "showUI uiType:" + uiType);
        if (CARD_TYPE_INFORMATION.equals(uiType)) {
            LLMTextInterface llmTextInterface = DeviceHolder.INS().getDevices().getLLMText();
            if (llmTextInterface != null) {
                llmTextInterface.onShowUI(mAgentIdentifier, location);
            }
        }
    }

    private void parseLLMData(Map<String, Object> flowContext) {
        String dataInfo = getFlowContextKey(getAgentName() + "_" + KEY_DS_CONTEXT_DATA, flowContext);
        String topicType = getFlowContextKey(FlowContextKey.FC_QUERY_CLASS_LABEL, flowContext);
        LogUtils.d(TAG, "parseLLMData llm data:" + dataInfo);
        LogUtils.d(TAG, "parseLLMData topicType:" + topicType);
        Object streamModeObject = flowContext.get(FlowContextKey.FC_STREAM_MODE);
        int streamMode = StreamMode.NOT_STREAM_MODE;
        if (streamModeObject instanceof Integer) {
            streamMode = (int) streamModeObject;
            LogUtils.d(TAG, "parseLLMData streamMode:" + streamMode);
        }

        if (streamMode == StreamMode.NOT_STREAM_MODE) {//非流式的
            String topicTypeLower = topicType.toLowerCase(Locale.getDefault());
            if (topicTypeLower.startsWith("faq")) {//FAQ-松龙
                getCardInfoNotStream(flowContext, topicType);
            } else {//百科-图灵
                if (topicTypeLower.startsWith("joke")
                        || topicTypeLower.startsWith("wiki")
                        || topicTypeLower.startsWith("poetry")
                        || topicTypeLower.startsWith("translate")
                        || topicTypeLower.startsWith("horoscope")
                        || topicTypeLower.startsWith("stock")
                ) {
                    LLMDataInfo.ExtraData extraInfo = getLLMExtraInfo(dataInfo);
                    if (extraInfo != null) {
                        Integer length = extraInfo.getLen();
                        LogUtils.d(TAG, "parseLLMData length:" + length);
                        if (length == null) {// 松龙大模型没有给len字段
                            getCardInfoNotStream(flowContext, topicType);
                        } else {
                            if (length >= 30) {
                                getCardInfoNotStream(flowContext, topicType);
                            }
                        }
                    }
                }
            }
        } else { //流式的
            LLMDataInfo.ExtraData extraInfo = getLLMExtraInfo(dataInfo);
            int totalLen = 0;
            if (extraInfo != null && extraInfo.getFaqLen() != null) {
                totalLen = extraInfo.getFaqLen();
            }

            Map<String, Object> map = new MapUtil().builder(flowContext).put("totalLen", totalLen).build();

            LLMTextInterface llmTextInterface = DeviceHolder.INS().getDevices().getLLMText();
            if (llmTextInterface != null) {
                llmTextInterface.constructStreamCardInfo(map);
            }
        }
    }

    private LLMDataInfo.ExtraData getLLMExtraInfo(String dataInfo) {
        LLMDataInfo.ExtraData extraInfo = null;
        if (!StringUtil.isEmpty(dataInfo)) {
            Gson gson = new Gson();
            LLMDataInfo llmDataInfo = gson.fromJson(dataInfo, LLMDataInfo.class);
            if (llmDataInfo != null) {
                String extraData = llmDataInfo.getExtraData();
                if (!StringUtil.isEmpty(extraData)) {
                    extraInfo = gson.fromJson(
                            extraData, LLMDataInfo.ExtraData.class);
                }
            }
        }
        return extraInfo;
    }

    private void getCardInfoNotStream(Map<String, Object> flowContext, String topicType) {
        LLMTextInterface llmTextInterface = DeviceHolder.INS().getDevices().getLLMText();
        if (llmTextInterface != null) {
            Object responseTextObject = flowContext.get(FlowContextKey.FC_NO_TASK_TEXT);
            LogUtils.d(TAG, "getCardInfoNotStream llm data text:->" + responseTextObject + "<-");
            String content = "";
            if (responseTextObject instanceof String) {
                content = (String) responseTextObject;
                LogUtils.d(TAG, "getCardInfoNotStream content:" + content);

            }
            String requestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);
            llmTextInterface.constructCardInfo(content, topicType, requestId);
        }
    }
}
