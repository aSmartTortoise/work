package com.voyah.ai.logic.agent.flowchart.findchart;

import java.util.HashMap;
import java.util.Map;

public class ContextDate {
    public static final Boolean DEBUG = false;
    //图保存的路径
    public static String GRAPH_SAVE_PATH;
    //存放上下文数据
    public static final Map<String, Object> map = new HashMap<>();
    public static final String TAG_TIME_STATISTICS = "time_statistics";
    //当前执行的图，指令
    public static final String CURRENT_GRAPH_NAME = "current_graph_name";
    public static final String CONTEXT = "context";

    public static final String INST_RESULT = "result";//结果列表

    public static final String NODE_TAG = "node_tag";
    public static final String NODE_METHOD_NAME = "node_method_name";
    public static final String NODE_KEY_PARAMS = "node_key_params";
    public static final String MAP_KEY_BRANCH = "branch";//分支信息。
    public static final String MAP_KAY_POSITION_VALUE_MAP = "map_position_value";//菱形处理后的结果，位置对应的结果
    public static final String MAP_KEY_POSITION = "positions";
    public static final String MAP_KEY_POSITION_FLOW = "positions_flow";

    public static final String MAP_KEY_METHOD_POSITION = "method_position";//分支信息。
    public static final String MAP_KEY_HIT_TTS = "hit_tts";//分支信息。

    public static final String MAP_KEY_INTENT = "device_intent";//用来区分到底是哪个类里的方法

    //===========================需要移到客户端的数据=============
    //槽里算法的信息对应的key
    public static final String MAP_KEY_SLOT_IS_sound_source_position = "xuman_slot_is_sound_source_position";//判断当前槽里的位置信息是否来自声源位置。

    //方法params的key
    public static final String MAP_KEY_METHOD_PARAM_SWITCH = "switch";//

    //图里的key
    public static final String GRAPH_PARAMS_KEY_SPECIFIED_POSITION = "specified_position";//图中只包含那些位置信息的key.
    public static final String FLOW_KEY_ONE_POSITION = "one_position";
    public static final String GRAPH_PARAMS_KEY_METHOD_NAME_CUR = "method_name_cur";
    public static final String GRAPH_PARAMS_KEY_METHOD_NAME_EQ = "method_name_eq";
}
