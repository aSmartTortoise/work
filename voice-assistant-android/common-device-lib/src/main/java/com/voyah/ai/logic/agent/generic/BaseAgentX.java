package com.voyah.ai.logic.agent.generic;


import com.voice.sdk.context.ParamsGather;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UIState;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ai.voice.platform.agent.api.version.v1.AgentResponse;
import com.voyah.ai.voice.sdk.api.task.AgentInfoHolder;
import com.voyah.ai.voice.sdk.api.task.AgentX;
import com.voyah.ai.voice.sdk.api.task.AgentXCallback;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/2/19
 **/
public abstract class BaseAgentX extends AgentX implements IAgentExecuteTask {
    private static final String TAG = BaseAgentX.class.getSimpleName();
    public static final List<String> LOCATION_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
    //中控屏    副驾屏、滑移屏  娱乐屏、吸顶屏
    public static final List<String> SCREEN_LIST = Arrays.asList("central_screen", "passenger_screen", "scratch_screen", "entertainment_screen", "ceil_screen");
    public static final String SCREEN = "screen"; //屏幕(没有指定单一的屏幕)
    public static final List<String> FIRST_SCREEN = Arrays.asList("central_screen");
    public static final List<String> THIRD_SCREEN = Arrays.asList("entertainment_screen", "ceil_screen");
    public static final String ENTERTAINMENT_SCREEN = "entertainment_screen"; //娱乐屏(需要根据指定位置和声源位置来判断是副驾屏还是吸顶屏)
    protected final String CARD_TYPE_INFORMATION = "information";
    protected final String KEY_DS_CONTEXT_DATA = "ctxData";

    protected String mAgentIdentifier = getAgentName(); //使用agentName来标识agent会出现ABA，前一个A的close destroy了最新A的界面


    @Override
    public void execute(AgentInfoHolder agentInfoHolder, AgentXCallback agentXCallback) {
//        LogUtils.d(TAG, "execute ---------------" + agentInfoHolder.getRequestId());
        AgentExecuteTask agentExecuteTask = new AgentExecuteTask(this);
        Map<String, Object> map = agentInfoHolder.getFlowContext();
        if (!StringUtils.equals(getAgentName(), "asrTextRecStream")) {
            String query = null;
            extractId(map);
            if (map.containsKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION)) {
                ParamsGather.location = (String) map.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION);
            }
            if (map.containsKey(FlowContextKey.FC_WHOLE_QUERY)) {
                query = (String) map.get(FlowContextKey.FC_WHOLE_QUERY);
            }

//            LogUtils.i(TAG, "executeMessage requestId is " + ParamsGather.requestId + " ,location is " + ParamsGather.location + " , query is " + query
//                    + " ,agentName is " + getAgentName());
        }

        try {
            agentExecuteTask.executeAgent(map, agentXCallback, getAgentName(), getPriority(), agentInfoHolder);
        } catch (Exception e) {
            e.printStackTrace();
            AgentResponse agentResponse = agentInfoHolder.createAgentResponse(0);
            agentXCallback.onAgentBeginExecute(agentResponse);
            agentXCallback.onAgentExecuteFinal(agentResponse);
            UIMgr.INSTANCE.exitState(UIState.STATE_ACTION, mAgentIdentifier);
        }
    }

    @Override
    public String getAgentName() {
        return AgentName();
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public boolean isSequenced() {
        return false;
    }

    @Override
    public void destroy() {
        //
    }

    @Override
    public void executeOrder(String executeTag, int location) {

    }

    @Override
    public void showUi(String uiType, int location) {

    }

    protected void extractId(Map<String, Object> map) {
        if (map.containsKey(FlowContextKey.FC_REQ_ID)) {
            String reqId = (String) map.get(FlowContextKey.FC_REQ_ID);
            Object queryId = map.getOrDefault(FlowContextKey.FC_QUERY_ID, "");
            ParamsGather.requestId = reqId;
            reqId = reqId == null ? "" : reqId;

            if (queryId == null) {
                queryId = "";
            } else {
                queryId = queryId.toString();
            }
            mAgentIdentifier = UIMgr.INSTANCE.obtainToken(reqId, (String) queryId);
        }
    }

    public static String getParamKey(Map<String, List<Object>> paramsMap, String key, int index) {
//        LogUtils.i(TAG, "paramsMap:" + GsonUtils.toJson(paramsMap));
        String value = "";
        if (paramsMap == null || paramsMap.isEmpty()) {
            LogUtils.e(TAG, "paramsMap empty");
            return value;
        }
        if (!paramsMap.containsKey(key)) {
            LogUtils.e(TAG, "not containsKey");
            return value;
        }
        List<Object> objectList = paramsMap.get(key);
        if (objectList == null || objectList.size() <= index) {
            LogUtils.e(TAG, "objectList null or size min:" + (objectList != null ? objectList.size() : null));
            return value;
        }
        Object result = objectList.get(index);
        if (result instanceof String) {
            value = (String) result;
        }
//        LogUtils.i(TAG, "getParamKey key:" + key + ",value:" + value);
        return value;
    }

    public static int getIntegerParams(String strValue) {
        try {
            double value = Double.parseDouble(strValue);
            return (int) Math.round(value);
        } catch (NumberFormatException e) {
            LogUtils.d(TAG, "The string \"" + strValue + "\" is not a valid number.");
            return 0; // 或者你可以选择抛出一个异常
        }
    }

    public static String getFlowContextKey(String key, Map<String, Object> flowContext) {
        return flowContext.containsKey(key) ? (String) flowContext.get(key) : "";
    }

    public static int getIntFlowContextKey(String key, Map<String, Object> flowContext) {
        return flowContext.containsKey(key) ? (int) flowContext.get(key) : 0;
    }

    public static boolean getBooleanFlowContextKey(String key, Map<String, Object> flowContext) {
        return flowContext.containsKey(key) ? (boolean) flowContext.get(key) : false;
    }

    public static int getAwakenLocation(Map<String, Object> flowContext) {
        String awakenLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext);
        return translateLocation(awakenLocation);
    }

    public static int getSoundSourceLocation(Map<String, Object> flowContext) {
        String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        return translateLocation(soundLocation);
    }

    public static String getUiSoundLocation(Map<String, Object> flowContext) {
        //是否为全时免唤醒
        boolean isAllTimeWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_NO_WAKEUP_TIME, flowContext);
        if (isAllTimeWakeUp) {
            return getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext); //免唤醒取指令位置
        } else {
            return getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext); //正常指令取前置唤醒位置
        }
    }

    public static int translateLocation(String awakenLocation) {
        LogUtils.i(TAG, "translateLocation is " + awakenLocation);
        int location = 0; //默认主驾
        if (Location.Location_LIST.contains(awakenLocation)) {
            location = Location.Location_MAP.get(awakenLocation);
        }
        return location;
    }

    public int getIndex(String indexType, String indexStr, int currentIndex, int size) {
        if (indexStr == null || indexStr.length() == 0) {
            return -1;
        }
        if (size == 0) {
            return -1;
        }
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
        if (Constant.RELATIVE.equals(indexType)) {
            return -1;
        } else if (Constant.ABSOLUTE.equals(indexType)) {
            if (index > 0) {
                if (index <= size) {
                    return index - 1;
                } else {
                    return Integer.MAX_VALUE;
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    public int getIndex(String indexType, String indexStr, int[] range) {
        if (indexStr == null || indexStr.length() == 0) {
            return -1;
        }
        if (range[0] > range[1] || range[0] < 1) {
            return -1;
        }
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
        if (Constant.ABSOLUTE.equals(indexType)) {
            if (index >= range[0] && index <= range[1]) {
                return index - 1;
            }
            if (index < 0) {
                index = Math.abs(index);
                int curIndex = range[1] - index + 1;
                if (curIndex >= range[0] && curIndex <= range[1]) {
                    return curIndex - 1;
                }
            }

        }
        return -1;
    }

    /**
     * 声源提取屏幕位置
     *
     * @return
     */
    public String getScreenFromSound(HashMap<String, Object> map) {
        int soundSource = getSoundSourcePos(map); //声源位置
        String screenName = FuncConstants.VALUE_SCREEN_CENTRAL; //默认中控
        switch (soundSource) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
            default:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
        }
        return screenName;
    }

    /**
     * 获取声源位置
     */
    public int getSoundSourcePos(HashMap<String, Object> map) {
        Object object = getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
        if (object instanceof ArrayList) {
            return ((ArrayList<Integer>) object).get(0);
        }
        return PositionSignal.FIRST_ROW_LEFT;//兜底主驾
    }

    /**
     * 获取图中方法的key对应的值，或者ds里数据对应的值。
     * 图的方法参数放到了，上下文中图的上下文里。
     * ds数据放在了上下文里。
     * 先从图的上下文里找，再从全局上下文找
     *
     * @param map
     * @param key
     * @return
     */
    public Object getValueInContext(Map<String, Object> map, String key) {
        HashMap<String, Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphMap.containsKey(key)) {
            return graphMap.get(key);
        }
        //二次交互里的上下文中获取数据
        HashMap<String, Object> graphMap2 = (HashMap<String, Object>) map.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
        if (graphMap2 != null && graphMap2.containsKey(key)) {
            return graphMap2.get(key);
        }
        return map.get(key);
    }

    public String getScreen(String location) {
        String screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        switch (location) {
            case "first_row_left":
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case "first_row_right":
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            case "second_row_left":
            case "second_row_right":
            case "third_row_left":
            case "third_row_right":
            case "second_row_mid":
            case "second_side":
            case "rear_side":
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
        }
        return screenName;
    }

    public interface Location {
        List<String> Location_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
        Map<String, Integer> Location_MAP = new HashMap() {
            {
                put(Location_LIST.get(0), 0);
                put(Location_LIST.get(1), 1);
                put(Location_LIST.get(2), 2);
                put(Location_LIST.get(3), 3);
                put(Location_LIST.get(4), 4);
                put(Location_LIST.get(5), 5);
            }
        };
    }
}
