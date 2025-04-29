package com.voyah.ai.voice.agent.llm;


import com.example.filter_annotation.ClassAgent;
import com.google.gson.Gson;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.llm.LLMDrawingInterface;
import com.voice.sdk.model.LLMDataInfo;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.toolkit.util.StringUtil;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.status.StreamMode;

import java.util.List;
import java.util.Map;

/**
 * author : jie wang
 * date : 2024/8/12 10:19
 * description :
 */
@ClassAgent
public class LLMDrawingAgent extends BaseAgentX {

    private static final String TAG = "LLMDrawingAgent";

    @Override
    public String AgentName() {
        return "llm#drawing";
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
    public ClientAgentResponse executeAgent(
            Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------" + TAG + "----------");

        ClientAgentResponse response = null;
        Object ttsObject = flowContext.get(FlowContextKey.FC_TTS_TEXT);
        String ttxText = "";
        if (ttsObject instanceof String) {
            ttxText = (String) ttsObject;
            LogUtils.d(TAG, "executeAgent ttxText:" + ttxText);
        }

        Object queryObject = flowContext.get(FlowContextKey.FC_WHOLE_QUERY);
        LogUtils.d(TAG, "parse llm drawing data drawing prompt:" + queryObject);
        String prompt = null;
        if (queryObject instanceof String) {
            prompt = (String) queryObject;
        }

        Object queryExtractObject = flowContext.get("queryExtract");
        LogUtils.d(TAG, "parse llm drawing data queryExtractObject:" + queryExtractObject);
        String keyWords = null;
        if (queryExtractObject instanceof String) {
            String drawingKeyWords = (String) queryExtractObject;
            if (!StringUtil.isEmpty(drawingKeyWords)) {
                keyWords = drawingKeyWords;
            }
        }

        String requestId = getFlowContextKey(FlowContextKey.FC_REQ_ID, flowContext);

        Object codeObject = flowContext.get(FlowContextKey.FC_LLM_RESULT_IMG_CODE);
        LogUtils.d(TAG, "parse llm drawing data code:" + codeObject);
        int code = -1;
        if (codeObject instanceof Integer) {
            code = (Integer) codeObject;
            response = getErrorResponse(code, flowContext, ttxText);
        }

        Object soundLocationObj = flowContext.get(
                FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION);
        LogUtils.d(TAG, "parse llm drawing data soundLocation:" + soundLocationObj);
        String targetScreenType = FuncConstants.VALUE_SCREEN_CENTRAL;
        if (soundLocationObj instanceof String) {
            String soundLocation = (String) soundLocationObj;
            switch (soundLocation) {
                case "first_row_left":
                    targetScreenType = FuncConstants.VALUE_SCREEN_CENTRAL;
                    break;
                case "first_row_right":
                    targetScreenType = FuncConstants.VALUE_SCREEN_PASSENGER;
                    break;
                case "second_row_left":
                case "second_row_right":
                case "third_row_left":
                case "third_row_right":
                    targetScreenType = FuncConstants.VALUE_SCREEN_CEIL;
                    break;
            }
        }

        LogUtils.d(TAG, "parse llm drawing data targetScreenType:" + targetScreenType);

        Object streamModeObject = flowContext.get(FlowContextKey.FC_STREAM_MODE);
        int streamMode = StreamMode.NOT_STREAM_MODE;
        if (streamModeObject instanceof Integer) {
            streamMode = (int) streamModeObject;
            LogUtils.d(TAG, "parse llm drawing data streamMode:" + streamMode);
        }

        if (response != null) {
            LLMDrawingInterface llmDrawingInterface = DeviceHolder.INS().getDevices().getLLMDrawing();
            if (llmDrawingInterface != null) {
                llmDrawingInterface.postDrawingState(prompt,
                        keyWords,
                        ttxText,
                        streamMode,
                        code,
                        0,
                        3,
                        null,
                        requestId,
                        targetScreenType);
            }

            return response;
        }
        
        Object streamFlagObject = flowContext.get(FlowContextKey.FC_IS_STREAM_NO_TASK_TEXT);
        LogUtils.d(TAG, "parse llm drawing data stream flag:" + streamFlagObject);
        boolean drawingSuccessFlag = false;
        if (streamFlagObject instanceof Boolean) {
            boolean streamFlag = (boolean) streamFlagObject;
            LogUtils.d(TAG, "parse llm drawing data streamFlag:" + streamFlag);
            boolean postDrawingStatusFlag = false;
            int drawingState = -1;
            List<String> imgList = null;
            if (streamMode == StreamMode.STREAM_MODE_START) {
                LogUtils.d(TAG, "stream start.");
                postDrawingStatusFlag = true;
                drawingState = 0;
            } else {
                Object isImgsObject = flowContext.get(FlowContextKey.FC_IS_LLM_RESULT_IMG_URLS);
                if (isImgsObject instanceof Boolean) {
                    boolean isImgs = (boolean) isImgsObject;
                    if (isImgs) {
                        Object imgsObject = flowContext.get(FlowContextKey.FC_LLM_RESULT_IMG_URLS);
                        if (imgsObject instanceof List) {
                            postDrawingStatusFlag = true;
                            drawingState = 2;
                            imgList = (List<String>) imgsObject;
                            drawingSuccessFlag = true;
                        }
                    }
                }
            }

            response = new ClientAgentResponse(
                    Constant.CommonResponseCode.SUCCESS,
                    flowContext,
                    ttxText
            );

            if (drawingSuccessFlag) {
                response.setmTtsObject("");
            }

            LLMDrawingInterface llmDrawingInterface = DeviceHolder.INS().getDevices().getLLMDrawing();
            if ((llmDrawingInterface != null) && postDrawingStatusFlag) {
                String dataInfo = getFlowContextKey(getAgentName() + "_" + KEY_DS_CONTEXT_DATA, flowContext);
                LogUtils.d(TAG, "executeAgent dataInfo:" + dataInfo);
                int imageSize = getImageSize(dataInfo);
                llmDrawingInterface.postDrawingState(
                        prompt,
                        keyWords,
                        ttxText,
                        streamMode,
                        code,
                        imageSize,
                        drawingState,
                        imgList,
                        requestId,
                        targetScreenType);
            }
        }

        return response;
    }


    private ClientAgentResponse getErrorResponse(
            int code,
            Map<String, Object> flowContext,
            String ttxText) {
        ClientAgentResponse response = null;
        LogUtils.d(TAG, "getErrorResponse code:" + code);
        switch (code) {
            case -1001:
            case -1002:
            case -4022:
            case -5000:
            case -5001:
            case -5002:
            case -5003:
            case -5004:
            case -6666:
            case -8888:
            case -9999:
                response = new ClientAgentResponse(
                        Constant.CommonResponseCode.SUCCESS,
                        flowContext,
                        ttxText
                );
                break;
            case 0:
            case 200:
                break;
            default:
                if (ttxText.isEmpty()) {
                    ttxText = "未知异常。";
                }
                response = new ClientAgentResponse(
                        Constant.CommonResponseCode.SUCCESS,
                        flowContext,
                        ttxText
                );
                break;
        }
        return response;
    }

    private int getImageSize(String dataInfo) {
        int imageNum = 4;
        if (!StringUtil.isEmpty(dataInfo)) {
            Gson gson = new Gson();
            LLMDataInfo llmDataInfo = gson.fromJson(dataInfo, LLMDataInfo.class);
            if (llmDataInfo != null) {
                String extraData = llmDataInfo.getExtraData();
                if (!StringUtil.isEmpty(extraData)) {
                    LLMDataInfo.ExtraData extraInfo = gson.fromJson(
                            extraData, LLMDataInfo.ExtraData.class);
                    if (extraInfo != null) {
                        imageNum = extraInfo.getImageNum();
                        LogUtils.d(TAG, "getImageSize imageNum:" + imageNum);

                    }
                }
            }
        }
        return imageNum;
    }



}
